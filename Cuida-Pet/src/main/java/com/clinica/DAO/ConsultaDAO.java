package com.clinica.DAO;

import com.clinica.model.Consulta; // << PRECISA implementar Identifiable
import com.clinica.model.Animal;   // Precisa ter getId(), setId()
import com.clinica.model.Cliente;  // Precisa ter getId(), setId()
import com.clinica.model.Veterinario; // Precisa ter getId(), setId()
import com.clinica.persistence.JsonPersistenceHelper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConsultaDAO {

    private final JsonPersistenceHelper<Consulta> persistenceHelper;
    private List<Consulta> consultas; // Cache em memória

    // --- Dependências para carregar objetos relacionados ---
    private final ClienteDAO clienteDAO;
    private final AnimalDAO animalDAO;
    private final VeterinarioDAO veterinarioDAO;
    // ------------------------------------------------------

    // Construtor agora recebe as dependências
    public ConsultaDAO(ClienteDAO clienteDAO, AnimalDAO animalDAO, VeterinarioDAO veterinarioDAO) {
        this.persistenceHelper = new JsonPersistenceHelper<>("consultas.json", new TypeReference<List<Consulta>>() {});
        this.consultas = persistenceHelper.readAll(); // Carrega dados do JSON
        // Armazena as instâncias dos DAOs injetados
        this.clienteDAO = clienteDAO;
        this.animalDAO = animalDAO;
        this.veterinarioDAO = veterinarioDAO;
    }

    private void saveData() {
        persistenceHelper.writeAll(consultas);
    }

    // Método auxiliar para carregar os objetos completos (Cliente, Animal, Veterinario)
    // baseado nos IDs armazenados no objeto Consulta vindo do JSON.
    private void carregarRelacionamentos(Consulta consulta) {
        if (consulta == null) return;

        // Carrega Cliente
        if (consulta.getCliente() != null && consulta.getCliente().getId() > 0) {
            Cliente clienteCompleto = clienteDAO.exibir(consulta.getCliente().getId());
            consulta.setCliente(clienteCompleto); // Substitui o objeto só com ID pelo completo
        } else {
             // Se o ID não for válido ou o objeto for nulo no JSON, define como null
             consulta.setCliente(null);
        }

        // Carrega Animal
        if (consulta.getAnimal() != null && consulta.getAnimal().getId() > 0) {
            Animal animalCompleto = animalDAO.exibir(consulta.getAnimal().getId());
            consulta.setAnimal(animalCompleto);
        } else {
            consulta.setAnimal(null);
        }

        // Carrega Veterinário
        if (consulta.getVeterinario() != null && consulta.getVeterinario().getId() > 0) {
            Veterinario vetCompleto = veterinarioDAO.exibir(consulta.getVeterinario().getId());
            consulta.setVeterinario(vetCompleto);
        } else {
             consulta.setVeterinario(null);
        }
    }

     // Método auxiliar para preparar a Consulta para salvar no JSON
     // Garante que apenas os IDs das entidades relacionadas sejam salvos
    private Consulta prepararParaSalvar(Consulta consultaOriginal) {
        Consulta consultaParaSalvar = new Consulta(); // Cria cópia rasa ou objeto novo
        // Copia dados básicos
        consultaParaSalvar.setId(consultaOriginal.getId());
        consultaParaSalvar.setDataHora(consultaOriginal.getDataHora());
        consultaParaSalvar.setStatus(consultaOriginal.getStatus());

        // Cria objetos de referência contendo APENAS o ID
        if (consultaOriginal.getCliente() != null && consultaOriginal.getCliente().getId() > 0) {
            Cliente clienteRef = new Cliente();
            clienteRef.setId(consultaOriginal.getCliente().getId());
            consultaParaSalvar.setCliente(clienteRef);
        }
        if (consultaOriginal.getAnimal() != null && consultaOriginal.getAnimal().getId() > 0) {
            Animal animalRef = new Animal();
            animalRef.setId(consultaOriginal.getAnimal().getId());
            consultaParaSalvar.setAnimal(animalRef);
        }
         if (consultaOriginal.getVeterinario() != null && consultaOriginal.getVeterinario().getId() > 0) {
            Veterinario vetRef = new Veterinario();
            vetRef.setId(consultaOriginal.getVeterinario().getId());
            consultaParaSalvar.setVeterinario(vetRef);
        }
        return consultaParaSalvar;
    }

    public void inserir(Consulta consulta) {
        // Validações opcionais (se os IDs de cliente, animal, vet existem)
        if (consulta.getCliente() == null || clienteDAO.exibir(consulta.getCliente().getId()) == null) {
             System.err.println("Erro ao inserir consulta: Cliente inválido ou não encontrado.");
             return; // Ou lançar exceção
        }
         if (consulta.getAnimal() != null && animalDAO.exibir(consulta.getAnimal().getId()) == null) {
             System.err.println("Erro ao inserir consulta: Animal inválido ou não encontrado.");
             return; // Ou lançar exceção
        }
         if (consulta.getVeterinario() == null || veterinarioDAO.exibir(consulta.getVeterinario().getId()) == null) {
             System.err.println("Erro ao inserir consulta: Veterinário inválido ou não encontrado.");
             return; // Ou lançar exceção
        }


        int nextId = persistenceHelper.getNextId(consultas);
        consulta.setId(nextId); // Define o ID no objeto original também

        Consulta consultaParaSalvar = prepararParaSalvar(consulta);

        consultas.add(consultaParaSalvar); // Adiciona a versão com IDs ao cache
        saveData(); // Salva no arquivo JSON
    }

    public void alterar(Consulta consultaAtualizada) {
         // Validações opcionais como em inserir()

        Optional<Consulta> consultaExistenteOpt = consultas.stream()
                .filter(c -> c.getId() == consultaAtualizada.getId())
                .findFirst();

        if (consultaExistenteOpt.isPresent()) {
            // Prepara a versão atualizada para salvar (com IDs)
             Consulta consultaParaSalvar = prepararParaSalvar(consultaAtualizada);
             // Remove o antigo e adiciona o novo (ou atualiza o existente na lista)
             consultas.removeIf(c -> c.getId() == consultaAtualizada.getId());
             consultas.add(consultaParaSalvar);
             saveData();
        } else {
              System.err.println("Tentativa de alterar consulta inexistente com ID: " + consultaAtualizada.getId());
        }
    }

    public void remover(int id) {
        boolean removed = consultas.removeIf(c -> c.getId() == id);
        if (removed) {
            saveData();
        }
         if (!removed) {
             System.err.println("Tentativa de remover consulta inexistente com ID: " + id);
        }
    }

    public List<Consulta> listarTodos() {
        // Cria uma nova lista para não modificar o cache original
        List<Consulta> consultasCompletas = new ArrayList<>();
        for (Consulta c : consultas) {
            // Cria uma cópia para não alterar o cache diretamente ao carregar relacionamentos
            Consulta copia = criarCopiaConsulta(c);
            carregarRelacionamentos(copia); // Carrega Cliente, Animal, Veterinario completos
            consultasCompletas.add(copia);
        }
        return consultasCompletas;
    }


    public Consulta exibir(int id) {
        Optional<Consulta> consultaOpt = consultas.stream()
                .filter(c -> c.getId() == id)
                .findFirst();

        if (consultaOpt.isPresent()) {
            Consulta copia = criarCopiaConsulta(consultaOpt.get()); // Cria cópia
            carregarRelacionamentos(copia); // Carrega relacionamentos na cópia
            return copia;
        } else {
            return null; // Não encontrado
        }
    }

     // Método auxiliar para criar uma cópia superficial de uma consulta
    private Consulta criarCopiaConsulta(Consulta original) {
        Consulta copia = new Consulta();
        copia.setId(original.getId());
        copia.setDataHora(original.getDataHora());
        copia.setStatus(original.getStatus());
        // Copia as referências (que serão substituídas ou mantidas em carregarRelacionamentos)
        copia.setCliente(original.getCliente());
        copia.setAnimal(original.getAnimal());
        copia.setVeterinario(original.getVeterinario());
        return copia;
    }

}
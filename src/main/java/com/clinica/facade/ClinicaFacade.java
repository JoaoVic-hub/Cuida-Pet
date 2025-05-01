package com.clinica.facade;

import com.clinica.DAO.*;
import com.clinica.Util.ValidadorUtil; // Mantido para ProntuarioController e validações locais
import com.clinica.controller.*; // ProntuarioController é usado
import com.clinica.model.*;
import com.clinica.observer.DataObserver;
import com.clinica.observer.DataType;
import java.util.List;
import java.util.Objects; // Para Objects.requireNonNull
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.SwingUtilities;

/**
 * Facade Singleton atuando como Subject (Observado) no padrão Observer.
 * Centraliza o acesso às operações e notifica observers sobre mudanças nos dados.
 * Usa DAOs diretamente para operações de escrita/modificação.
 * Modificada para suportar o padrão Command/Memento retornando estados anteriores.
 */
public class ClinicaFacade {

    // --- Singleton ---
    private static volatile ClinicaFacade instance;

    // --- Observer ---
    private final List<DataObserver> observers = new CopyOnWriteArrayList<>();

    // --- DAOs (Criados via Factory Methods) ---
    private final ClienteDAO clienteDAO;
    private final AnimalDAO animalDAO;
    private final VeterinarioDAO veterinarioDAO;
    private final ConsultaDAO consultaDAO;
    private final ProntuarioDAO prontuarioDAO;
    private final AgendaDAO agendaDAO;
    private final EmpresaDAO empresaDAO;

    // --- Controllers (Apenas os que realmente têm lógica extra) ---
    private final ProntuarioController prontuarioController; // Mantido se tiver lógica complexa

    // Construtor privado
    private ClinicaFacade() {
        this.clienteDAO = createClienteDAO();
        this.animalDAO = createAnimalDAO();
        this.veterinarioDAO = createVeterinarioDAO();
        this.empresaDAO = createEmpresaDAO();
        // Garante que DAOs necessários para ConsultaDAO são criados primeiro
        this.consultaDAO = createConsultaDAO(this.clienteDAO, this.animalDAO, this.veterinarioDAO);
        // Garante que ConsultaDAO existe para ProntuarioDAO
        this.prontuarioDAO = createProntuarioDAO(this.consultaDAO);
        // Garante que ConsultaDAO existe para AgendaDAO
        this.agendaDAO = createAgendaDAO(this.consultaDAO);
        // Cria controller se necessário
        this.prontuarioController = createProntuarioController(this.prontuarioDAO, this.consultaDAO);
        System.out.println("Instância de ClinicaFacade (Subject) criada.");
    }

    // Método getInstance()
    public static ClinicaFacade getInstance() {
        if (instance == null) {
            synchronized (ClinicaFacade.class) {
                if (instance == null) {
                    instance = new ClinicaFacade();
                }
            }
        }
        return instance;
    }

    // --- Factory Methods ---
    private ClienteDAO createClienteDAO() { return new ClienteDAO(); }
    private AnimalDAO createAnimalDAO() { return new AnimalDAO(); }
    private VeterinarioDAO createVeterinarioDAO() { return new VeterinarioDAO(); }
    private EmpresaDAO createEmpresaDAO() { return new EmpresaDAO(); }
    // Injeção de dependência explícita para DAOs que dependem de outros
    private ConsultaDAO createConsultaDAO(ClienteDAO c, AnimalDAO a, VeterinarioDAO v) {
        Objects.requireNonNull(c, "ClienteDAO não pode ser nulo para ConsultaDAO");
        Objects.requireNonNull(a, "AnimalDAO não pode ser nulo para ConsultaDAO");
        Objects.requireNonNull(v, "VeterinarioDAO não pode ser nulo para ConsultaDAO");
        return new ConsultaDAO(c, a, v);
    }
    private ProntuarioDAO createProntuarioDAO(ConsultaDAO c) {
        Objects.requireNonNull(c, "ConsultaDAO não pode ser nulo para ProntuarioDAO");
        return new ProntuarioDAO(c);
    }
    private AgendaDAO createAgendaDAO(ConsultaDAO c) {
         Objects.requireNonNull(c, "ConsultaDAO não pode ser nulo para AgendaDAO");
        return new AgendaDAO(c);
    }
    private ProntuarioController createProntuarioController(ProntuarioDAO pDAO, ConsultaDAO cDAO) {
         Objects.requireNonNull(pDAO, "ProntuarioDAO não pode ser nulo para ProntuarioController");
         Objects.requireNonNull(cDAO, "ConsultaDAO não pode ser nulo para ProntuarioController");
        return new ProntuarioController(pDAO, cDAO);
    }


    // --- Métodos Observer ---
    public void addObserver(DataObserver observer) {
        if (observer != null && !observers.contains(observer)) { // Evita duplicados
            observers.add(observer);
            System.out.println("Observer ADICIONADO: " + observer.getClass().getSimpleName() + " (Total: " + observers.size() + ")");
        }
    }
    public void removeObserver(DataObserver observer) {
         if (observer != null) {
            boolean removed = observers.remove(observer);
            if (removed) {
                 System.out.println("Observer REMOVIDO: " + observer.getClass().getSimpleName() + " (Restantes: " + observers.size() + ")");
            }
        }
     }
    // Método de notificação permanece o mesmo
    private void notifyObservers(DataType typeChanged) {
        System.out.println("Notificando " + observers.size() + " observers sobre mudança em: " + typeChanged);
        // Usar SwingUtilities.invokeLater para garantir que updates na UI ocorram na EDT
        for (DataObserver observer : observers) {
            SwingUtilities.invokeLater(() -> {
                try {
                     System.out.println(" -> Notificando observer: " + observer.getClass().getSimpleName() + " na thread: " + Thread.currentThread().getName());
                    observer.update(typeChanged);
                 } catch (Exception e) {
                    System.err.println("Erro no update do observer " + observer.getClass().getSimpleName() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            });
        }
     }

    // --- Métodos da Fachada (CRUD com retorno para Memento/Command) ---

    // == Cliente Operations ==

    /**
     * Adiciona um novo cliente. Não retorna estado anterior pois é uma adição.
     * Lança IllegalArgumentException para dados inválidos.
     */
    public void adicionarCliente(String nome, String endereco, String email, String telefone, String cpf, String senha) {
        // Validações básicas
        if (nome == null || nome.trim().isEmpty()) throw new IllegalArgumentException("Nome do cliente não pode ser vazio.");
        if (endereco == null || endereco.trim().isEmpty()) throw new IllegalArgumentException("Endereço não pode ser vazio.");
        if (!ValidadorUtil.isEmailValido(email)) throw new IllegalArgumentException("Email inválido: " + email);
        // Removido: Validação de formato CPF aqui, ValidadorUtil.isCpfValido já faz isso
        if (!ValidadorUtil.isCpfValido(cpf)) throw new IllegalArgumentException("CPF inválido: " + cpf);
        if (senha == null || senha.trim().isEmpty()) throw new IllegalArgumentException("Senha não pode ser vazia.");

        Cliente cliente = new Cliente(nome.trim(), endereco.trim(), email.trim().toLowerCase(), telefone, cpf, senha);
        this.clienteDAO.inserir(cliente); // DAO define o ID
        System.out.println("Cliente inserido via Facade/DAO (adicionarCliente): ID " + cliente.getId());
        notifyObservers(DataType.CLIENTE);
    }

    /**
     * Adiciona cliente ignorando busca por CEP (apenas usa endereço fornecido).
     * Chama o método adicionarCliente principal.
     */
     public void adicionarClienteComCep(String nome, String cep, String enderecoDigitado, String email, String telefone, String cpf, String senha) {
         System.out.println("Facade: Chamando adicionarClienteComCep. Usando endereço digitado: '" + enderecoDigitado + "'.");
         // Validações podem ser repetidas ou confiar no adicionarCliente chamado
         adicionarCliente(nome, enderecoDigitado, email, telefone, cpf, senha);
    }

    /**
     * Atualiza um cliente existente.
     * Retorna uma cópia do estado do cliente ANTES da atualização.
     * Lança RuntimeException se o cliente não for encontrado.
     * Lança IllegalArgumentException para dados inválidos.
     */
    public Cliente atualizarCliente(int id, String nome, String endereco, String email, String telefone, String cpf, String senha) {
        Cliente estadoAntigo = clienteDAO.exibir(id);
        if (estadoAntigo == null) {
            throw new RuntimeException("Cliente com ID " + id + " não encontrado para atualização.");
        }
        // Cria cópia do estado antigo ANTES de qualquer validação ou alteração
        Cliente estadoAntigoCopia = new Cliente(estadoAntigo.getNome(), estadoAntigo.getEndereco(), estadoAntigo.getEmail(), estadoAntigo.getTelefone(), estadoAntigo.getCpf(), estadoAntigo.getSenha());
        estadoAntigoCopia.setId(id); // Copia o ID também

        // Validações dos NOVOS dados
        if (nome == null || nome.trim().isEmpty()) throw new IllegalArgumentException("Nome do cliente não pode ser vazio.");
        if (endereco == null || endereco.trim().isEmpty()) throw new IllegalArgumentException("Endereço não pode ser vazio.");
        if (!ValidadorUtil.isEmailValido(email)) throw new IllegalArgumentException("Email inválido: " + email);
        if (!ValidadorUtil.isCpfValido(cpf)) throw new IllegalArgumentException("CPF inválido: " + cpf);
        // Não validar senha vazia na atualização, pode ser que o usuário não queira mudar
        // if (senha == null || senha.trim().isEmpty()) throw new IllegalArgumentException("Senha não pode ser vazia.");

        // Cria o objeto com os dados atualizados
        Cliente clienteAtualizado = new Cliente(nome.trim(), endereco.trim(), email.trim().toLowerCase(), telefone, cpf, senha);
        clienteAtualizado.setId(id); // Define o ID para o DAO saber quem alterar

        clienteDAO.alterar(clienteAtualizado); // Realiza a alteração

        // Notifica observers sobre a mudança
        notifyObservers(DataType.CLIENTE);
        // Notificar outros tipos se a mudança de cliente afetar outras áreas (Consulta, Agenda, etc.)
        notifyObservers(DataType.CONSULTA);
        notifyObservers(DataType.AGENDA);
        notifyObservers(DataType.ANIMAL); // Nome do cliente pode aparecer em listas de animais

        return estadoAntigoCopia; // Retorna a cópia do estado ANTES da alteração
    }

    /**
     * Remove um cliente pelo ID.
     * Verifica se o cliente possui animais associados antes de remover.
     * Retorna o objeto Cliente que foi removido.
     * Lança RuntimeException se o cliente não for encontrado ou se possuir animais.
     */
    public Cliente removerCliente(int id) {
        Cliente clienteParaRemover = clienteDAO.exibir(id);
        if (clienteParaRemover == null) {
             throw new RuntimeException("Cliente com ID " + id + " não encontrado para remoção.");
        }

        // Verifica dependências (animais)
        if (!animalDAO.listarPorCliente(id).isEmpty()) {
            throw new RuntimeException("Não é possível remover. Cliente ID " + id + " possui animais associados.");
        }
        // TODO: Adicionar verificação de consultas futuras se necessário

        clienteDAO.remover(id); // Remove do DAO

        // Notifica observers
        notifyObservers(DataType.CLIENTE);
        // Notificar outras áreas afetadas pela remoção do cliente
        notifyObservers(DataType.ANIMAL); // Lista de animais pode mudar (embora já devesse estar vazia)
        notifyObservers(DataType.CONSULTA); // Consultas passadas do cliente podem ser afetadas em relatórios
        notifyObservers(DataType.AGENDA); // Itens da agenda associados a este cliente

        return clienteParaRemover; // Retorna o objeto que foi efetivamente removido
    }


    // == Animal Operations ==

    /**
     * Adiciona um novo animal.
     * Retorna o objeto Animal com o ID definido após a inserção.
     * Lança RuntimeException se o cliente dono não for encontrado.
     * Lança NullPointerException se o animal for nulo.
     */
    public Animal adicionarAnimalObj(Animal animal) {
        Objects.requireNonNull(animal, "Objeto Animal não pode ser nulo.");
        if (clienteDAO.exibir(animal.getClienteId()) == null) {
            throw new RuntimeException("Não é possível adicionar animal. Cliente dono (ID: " + animal.getClienteId() + ") não existe.");
        }
        // DAO.inserir definirá o ID no objeto 'animal' passado por referência
        animalDAO.inserir(animal);
        System.out.println("Animal inserido via Facade/DAO (adicionarAnimalObj): ID " + animal.getId() + " para Cliente ID " + animal.getClienteId());
        notifyObservers(DataType.ANIMAL);
        notifyObservers(DataType.CLIENTE); // Notifica cliente pois a lista de animais dele mudou
        return animal; // Retorna o objeto com o ID
    }

    /**
     * Atualiza um animal existente.
     * Retorna uma cópia do estado do animal ANTES da atualização.
     * Lança RuntimeException se o animal ou o cliente dono não for encontrado.
     * Lança NullPointerException se o animal for nulo.
     */
    public Animal atualizarAnimalObj(Animal animalAtualizado) {
        Objects.requireNonNull(animalAtualizado, "Objeto Animal atualizado não pode ser nulo.");
        int animalId = animalAtualizado.getId();
        if (animalId <= 0) throw new IllegalArgumentException("ID inválido para atualização do animal.");

        Animal estadoAntigo = animalDAO.exibir(animalId);
        if (estadoAntigo == null) {
            throw new RuntimeException("Animal com ID " + animalId + " não encontrado para atualização.");
        }
        // Verifica se o cliente dono ainda existe (importante se o clienteId pudesse ser mudado)
        if (clienteDAO.exibir(animalAtualizado.getClienteId()) == null) {
             throw new RuntimeException("Não é possível atualizar animal. Cliente dono (ID: " + animalAtualizado.getClienteId() + ") não existe.");
        }

        // Cria cópia do estado antigo
        Animal estadoAntigoCopia = new Animal(estadoAntigo.getNome(), estadoAntigo.getEspecie(), estadoAntigo.getRaca(), estadoAntigo.getDataNascimento(), estadoAntigo.getClienteId());
        estadoAntigoCopia.setId(animalId);

        // Realiza a alteração no DAO
        animalDAO.alterar(animalAtualizado);

        // Notifica observers
        notifyObservers(DataType.ANIMAL);
        // Notificar outras áreas afetadas (Consulta, Agenda)
        notifyObservers(DataType.CONSULTA);
        notifyObservers(DataType.AGENDA);
        // Notificar cliente também? Pode ser útil se a UI do cliente mostra detalhes do animal.
        notifyObservers(DataType.CLIENTE);

        return estadoAntigoCopia; // Retorna a cópia do estado anterior
    }

    /**
     * Remove um animal pelo ID.
     * Retorna o objeto Animal que foi removido.
     * Lança RuntimeException se o animal não for encontrado.
     */
    public Animal removerAnimal(int id) {
        Animal animalParaRemover = animalDAO.exibir(id);
        if (animalParaRemover == null) {
            throw new RuntimeException("Animal com ID " + id + " não encontrado para remoção.");
        }
        // TODO: Adicionar verificação de dependências (consultas futuras para este animal?)

        animalDAO.remover(id); // Remove do DAO

        // Notifica observers
        notifyObservers(DataType.ANIMAL);
        // Notificar outras áreas afetadas
        notifyObservers(DataType.CLIENTE); // Lista de animais do cliente mudou
        notifyObservers(DataType.CONSULTA); // Consultas associadas
        notifyObservers(DataType.AGENDA); // Itens da agenda associados

        return animalParaRemover; // Retorna o objeto removido
    }


    // == Veterinario Operations ==

    /**
     * Adiciona um novo veterinário.
     * Retorna o objeto Veterinario com o ID definido após a inserção.
     * Lança IllegalArgumentException para dados inválidos.
     */
    public Veterinario adicionarVeterinario(String n, String e, String t, String c, String s, String crmv, String esp) {
        // Validações
        if (n == null || n.trim().isEmpty()) throw new IllegalArgumentException("Nome do veterinário não pode ser vazio.");
        if (!ValidadorUtil.isEmailValido(e)) throw new IllegalArgumentException("Email inválido: " + e);
        if (!ValidadorUtil.isCpfValido(c)) throw new IllegalArgumentException("CPF inválido: " + c);
        if (s == null || s.trim().isEmpty()) throw new IllegalArgumentException("Senha não pode ser vazia.");
        if (!ValidadorUtil.isCrmvValido(crmv)) throw new IllegalArgumentException("CRMV inválido: " + crmv);

        Veterinario vet = new Veterinario(n, e, t, c, s, crmv, esp);
        veterinarioDAO.inserir(vet); // DAO define o ID
        System.out.println("Veterinário inserido via Facade/DAO: ID " + vet.getId());
        notifyObservers(DataType.VETERINARIO);
        return vet; // Retorna o objeto com ID
    }

    /**
     * Atualiza um veterinário existente.
     * Retorna uma cópia do estado do veterinário ANTES da atualização.
     * Lança RuntimeException se o veterinário não for encontrado.
     * Lança IllegalArgumentException para dados inválidos.
     */
    public Veterinario atualizarVeterinario(int id, String n, String e, String t, String c, String s, String crmv, String esp) {
        Veterinario estadoAntigo = veterinarioDAO.exibir(id);
         if (estadoAntigo == null) {
            throw new RuntimeException("Veterinário com ID " + id + " não encontrado para atualização.");
         }
         // Cria cópia do estado antigo
         Veterinario estadoAntigoCopia = new Veterinario(estadoAntigo.getNome(), estadoAntigo.getEmail(), estadoAntigo.getTelefone(), estadoAntigo.getCpf(), estadoAntigo.getSenha(), estadoAntigo.getCrmv(), estadoAntigo.getEspecialidade());
         estadoAntigoCopia.setId(id);

        // Validações dos novos dados
        if (n == null || n.trim().isEmpty()) throw new IllegalArgumentException("Nome do veterinário não pode ser vazio.");
        if (!ValidadorUtil.isEmailValido(e)) throw new IllegalArgumentException("Email inválido: " + e);
        if (!ValidadorUtil.isCpfValido(c)) throw new IllegalArgumentException("CPF inválido: " + c);
        // Não validar senha vazia na atualização
        if (!ValidadorUtil.isCrmvValido(crmv)) throw new IllegalArgumentException("CRMV inválido: " + crmv);

        // Cria objeto atualizado
        Veterinario vetAtualizado = new Veterinario(n, e, t, c, s, crmv, esp);
        vetAtualizado.setId(id); // Define ID para alteração

        veterinarioDAO.alterar(vetAtualizado); // Altera no DAO

        // Notifica observers
        notifyObservers(DataType.VETERINARIO);
        notifyObservers(DataType.CONSULTA); // Nome/dados do vet podem aparecer em consultas/agenda
        notifyObservers(DataType.AGENDA);

        return estadoAntigoCopia; // Retorna estado anterior
    }

    /**
     * Remove um veterinário pelo ID.
     * Retorna o objeto Veterinario que foi removido.
     * Lança RuntimeException se o veterinário não for encontrado ou tiver dependências.
     */
    public Veterinario removerVeterinario(int id) {
        Veterinario vetParaRemover = veterinarioDAO.exibir(id);
        if (vetParaRemover == null) {
            throw new RuntimeException("Veterinário com ID " + id + " não encontrado para remoção.");
        }
        // TODO: Adicionar verificação de dependências (consultas futuras?)
        // Exemplo:
        // boolean temConsultas = consultaDAO.listarTodos().stream()
        //     .anyMatch(con -> con.getVeterinario() != null && con.getVeterinario().getId() == id &&
        //                     (con.getStatus().equals("Agendada") || con.getStatus().equals("Em Andamento"))); // Verifica futuras ou em andamento
        // if (temConsultas) {
        //     throw new RuntimeException("Não é possível remover. Veterinário ID " + id + " possui consultas futuras ou em andamento.");
        // }

        veterinarioDAO.remover(id); // Remove do DAO

        // Notifica observers
        notifyObservers(DataType.VETERINARIO);
        notifyObservers(DataType.CONSULTA); // Consultas/Agenda podem ser afetadas
        notifyObservers(DataType.AGENDA);

        return vetParaRemover; // Retorna o objeto removido
    }


    // == Consulta Operations ==

    /**
     * Adiciona uma nova consulta.
     * Retorna o objeto Consulta com o ID definido após a inserção.
     * Lança RuntimeException se cliente, animal(se ID > 0) ou veterinário não forem encontrados.
     * Lança NullPointerException se a consulta for nula.
     */
    public Consulta adicionarConsulta(Consulta consulta) {
        Objects.requireNonNull(consulta, "Objeto Consulta não pode ser nulo.");
        // Valida se as referências existem antes de inserir
        if (consulta.getCliente() == null || clienteDAO.exibir(consulta.getCliente().getId()) == null) {
             throw new RuntimeException("Não é possível adicionar consulta. Cliente inválido ou não encontrado.");
        }
        if (consulta.getAnimal() != null && consulta.getAnimal().getId() > 0 && animalDAO.exibir(consulta.getAnimal().getId()) == null) {
             throw new RuntimeException("Não é possível adicionar consulta. Animal inválido ou não encontrado.");
        }
        if (consulta.getVeterinario() == null || veterinarioDAO.exibir(consulta.getVeterinario().getId()) == null) {
             throw new RuntimeException("Não é possível adicionar consulta. Veterinário inválido ou não encontrado.");
        }

        // DAO definirá o ID na consulta passada por referência
        consultaDAO.inserir(consulta);
        System.out.println("Consulta inserida via Facade/DAO: ID " + consulta.getId());
        notifyObservers(DataType.CONSULTA);
        notifyObservers(DataType.AGENDA); // Agenda é diretamente derivada de consulta
        return consulta; // Retorna a consulta com ID
    }

    /**
     * Atualiza uma consulta existente.
     * Retorna uma cópia do estado da consulta ANTES da atualização.
     * Lança RuntimeException se a consulta, cliente, animal(se ID > 0) ou veterinário não forem encontrados.
     * Lança NullPointerException se a consulta for nula.
     */
    public Consulta atualizarConsulta(int id, Consulta consultaAtualizada) {
        // Validações iniciais
        Objects.requireNonNull(consultaAtualizada, "Objeto Consulta atualizado não pode ser nulo.");
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido para atualização da consulta: " + id);
        }
        consultaAtualizada.setId(id); // Garante que o ID esteja correto no objeto que será salvo

        // Busca o estado atual (antigo) no banco de dados
        Consulta estadoAntigo = consultaDAO.exibir(id);
        if (estadoAntigo == null) {
            throw new RuntimeException("Consulta com ID " + id + " não encontrada para atualização.");
        }

        // --- CORREÇÃO AQUI: Usa o Builder para criar a cópia do estado antigo ---
        Consulta.Builder builderCopia = new Consulta.Builder()
                .dataHora(estadoAntigo.getDataHora())
                .status(estadoAntigo.getStatus())
                // Assume que os getters retornam objetos com pelo menos o ID
                // Se os objetos retornados por getCliente/getVeterinario/getAnimal
                // forem apenas referências com ID, está correto.
                // Se eles já forem os objetos completos, também funciona.
                .cliente(estadoAntigo.getCliente())
                .veterinario(estadoAntigo.getVeterinario());

        // Adiciona o animal à cópia apenas se ele existia no estado antigo
        if (estadoAntigo.getAnimal() != null) {
            builderCopia.animal(estadoAntigo.getAnimal());
        }

        Consulta estadoAntigoCopia = builderCopia.build(); // Constrói a cópia
        estadoAntigoCopia.setId(id); // Define o ID na cópia também
        // --- FIM DA CORREÇÃO ---

        // Valida as referências no objeto ATUALIZADO (consultaAtualizada)
        // antes de passá-lo para o DAO.alterar
        Cliente clienteAtualizadoRef = consultaAtualizada.getCliente();
        if (clienteAtualizadoRef == null || clienteDAO.exibir(clienteAtualizadoRef.getId()) == null) {
             throw new RuntimeException("Não é possível atualizar consulta. Cliente (ID: " + (clienteAtualizadoRef != null ? clienteAtualizadoRef.getId() : "null") + ") inválido ou não encontrado.");
        }
        Animal animalAtualizadoRef = consultaAtualizada.getAnimal();
        if (animalAtualizadoRef != null && animalAtualizadoRef.getId() > 0 && animalDAO.exibir(animalAtualizadoRef.getId()) == null) {
             throw new RuntimeException("Não é possível atualizar consulta. Animal (ID: " + animalAtualizadoRef.getId() + ") inválido ou não encontrado.");
        }
        Veterinario vetAtualizadoRef = consultaAtualizada.getVeterinario();
        if (vetAtualizadoRef == null || veterinarioDAO.exibir(vetAtualizadoRef.getId()) == null) {
             throw new RuntimeException("Não é possível atualizar consulta. Veterinário (ID: " + (vetAtualizadoRef != null ? vetAtualizadoRef.getId() : "null") + ") inválido ou não encontrado.");
        }

        // Realiza a alteração no DAO usando o objeto atualizado
        consultaDAO.alterar(consultaAtualizada);

        // Notifica os observers sobre a mudança
        notifyObservers(DataType.CONSULTA);
        notifyObservers(DataType.AGENDA); // Agenda depende de consulta
        notifyObservers(DataType.PRONTUARIO); // Prontuário depende de consulta

        return estadoAntigoCopia; // Retorna a cópia do estado ANTES da alteração
    }

    /**
     * Remove uma consulta pelo ID.
     * Retorna o objeto Consulta que foi removido.
     * Lança RuntimeException se a consulta não for encontrada.
     */
    public Consulta removerConsulta(int id) {
        Consulta consultaParaRemover = consultaDAO.exibir(id);
        if (consultaParaRemover == null) {
            throw new RuntimeException("Consulta com ID " + id + " não encontrada para remoção.");
        }
        // TODO: Adicionar verificação de dependências (Prontuários associados?)
        // Embora o ProntuarioDAO provavelmente falhe ao tentar carregar uma consulta inexistente.
        // Poderia verificar se existem prontuários com esta consultaId antes de remover.
        // List<Prontuario> prontuarios = prontuarioDAO.listarPorConsulta(id);
        // if (!prontuarios.isEmpty()) {
        //     throw new RuntimeException("Não é possível remover. Consulta ID " + id + " possui prontuários associados.");
        // }

        consultaDAO.remover(id); // Remove do DAO

        // Notifica observers
        notifyObservers(DataType.CONSULTA);
        notifyObservers(DataType.AGENDA);
        notifyObservers(DataType.PRONTUARIO); // Notifica prontuário pois a consulta pai sumiu

        return consultaParaRemover; // Retorna o objeto removido
    }


    // == Prontuario Operations ==
    // Adicionar, atualizar, remover prontuários (podem seguir o mesmo padrão se necessário undo)

    /**
     * Adiciona um prontuário usando o ProntuarioController.
     * O Controller já lida com a lógica e notificação (se implementado lá).
     * Este método da facade apenas delega e re-notifica (ou confia na notificação do controller).
     * Retorna true se sucesso, false caso contrário. (Não retorna estado para Memento aqui)
     */
    public boolean adicionarProntuario(Prontuario prontuario) {
        Objects.requireNonNull(prontuario, "Objeto Prontuario não pode ser nulo.");
        // Validação extra na facade, se necessário, antes de chamar controller
        if (prontuario.getConsulta() == null || buscarConsultaPorId(prontuario.getConsulta().getId()) == null) {
             throw new IllegalArgumentException("Consulta associada ao prontuário é inválida ou não existe.");
        }

        boolean sucesso = prontuarioController.adicionarProntuario(prontuario);
        if (sucesso) {
            notifyObservers(DataType.PRONTUARIO); // Garante notificação pela facade
            // Notificar consulta também? Depende se a UI de consulta mostra algo sobre prontuários.
            // notifyObservers(DataType.CONSULTA);
        }
        return sucesso;
    }

    // TODO: Implementar atualizarProntuario e removerProntuario na Facade,
    // chamando os métodos correspondentes no ProntuarioController ou ProntuarioDAO,
    // e retornando estados anteriores se o undo for necessário para prontuários.
    // Exemplo:
    /*
    public Prontuario removerProntuario(int id) {
        Prontuario prontuarioRemovido = prontuarioDAO.exibir(id); // Ou use controller.buscar...
        if (prontuarioRemovido == null) {
             throw new RuntimeException("Prontuário ID " + id + " não encontrado.");
        }
        boolean sucesso = prontuarioController.removerProntuario(id); // Delega para controller/DAO
        if (sucesso) {
            notifyObservers(DataType.PRONTUARIO);
            return prontuarioRemovido;
        } else {
             throw new RuntimeException("Falha ao remover prontuário ID " + id + ".");
        }
    }
    */


    // --- Métodos de Leitura (não precisam de alteração para Memento) ---

    public List<Cliente> listarTodosClientes() { return clienteDAO.listarTodos(); }
    public Cliente buscarClientePorId(int id) { return clienteDAO.exibir(id); }
    public List<Cliente> buscarClientesPorNome(String n) { return clienteDAO.pesquisarPorNome(n); }
    public List<Animal> listarAnimaisPorCliente(int id) { return animalDAO.listarPorCliente(id); }
    public Animal buscarAnimalPorId(int id) { return animalDAO.exibir(id); }
    public List<Veterinario> listarTodosVeterinarios() { return veterinarioDAO.listarTodos(); }
    public Veterinario buscarVeterinarioPorId(int id) { return veterinarioDAO.exibir(id); }
    public List<Consulta> listarTodasConsultas() { return consultaDAO.listarTodos(); }
    public Consulta buscarConsultaPorId(int id) { return consultaDAO.exibir(id); }
    public List<Agenda> listarAgendaPorVeterinario(int id) { return agendaDAO.listarPorVeterinario(id); }
    public List<Agenda> listarAgendaCompleta() { return agendaDAO.listarTodos(); }
    public List<Prontuario> listarProntuariosPorConsulta(int id) { return prontuarioController.listarProntuariosPorConsulta(id); } // Delega
    public Prontuario buscarProntuarioPorId(int id) { return prontuarioController.buscarProntuarioPorId(id); } // Delega

    // Relatório - Apenas leitura
    public List<Consulta> buscarConsultasParaRelatorio(int m, int a) {
         return listarTodasConsultas().stream()
                    .filter(c->c!=null && c.getDataHora()!=null && c.getDataHora().getMonthValue()==m && c.getDataHora().getYear()==a)
                    .toList();
     }

    // Autenticação - Apenas leitura
    public Cliente autenticarCliente(String e, String s) { return clienteDAO.autenticar(e, s); }
    public Veterinario autenticarVeterinario(String e, String s) { return veterinarioDAO.autenticar(e, s); }
    public Empresa autenticarEmpresa(String e, String s) { return empresaDAO.autenticar(e, s); }

} // Fim da classe ClinicaFacade
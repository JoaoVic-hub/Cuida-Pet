package com.clinica.facade;

import com.clinica.DAO.*;
import com.clinica.Util.ValidadorUtil; 
import com.clinica.controller.*; 
import com.clinica.model.*;
import com.clinica.observer.DataObserver;
import com.clinica.observer.DataType;
import java.util.List;
import java.util.Objects; 
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.SwingUtilities;

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

    // --- Controllers  ---
    private final ProntuarioController prontuarioController; 

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

    /**
     * Adiciona um novo cliente. Não retorna estado anterior pois é uma adição.
     * Lança IllegalArgumentException para dados inválidos.
     */
    public void adicionarCliente(String nome, String endereco, String email, String telefone, String cpf, String senha) {
        // Validações básicas
        if (nome == null || nome.trim().isEmpty()) throw new IllegalArgumentException("Nome do cliente não pode ser vazio.");
        if (endereco == null || endereco.trim().isEmpty()) throw new IllegalArgumentException("Endereço não pode ser vazio.");
        if (!ValidadorUtil.isEmailValido(email)) throw new IllegalArgumentException("Email inválido: " + email);
        if (!ValidadorUtil.isCpfValido(cpf)) throw new IllegalArgumentException("CPF inválido: " + cpf);
        if (senha == null || senha.trim().isEmpty()) throw new IllegalArgumentException("Senha não pode ser vazia.");

        Cliente cliente = new Cliente(nome.trim(), endereco.trim(), email.trim().toLowerCase(), telefone, cpf, senha);
        this.clienteDAO.inserir(cliente); // DAO define o ID
        System.out.println("Cliente inserido via Facade/DAO (adicionarCliente): ID " + cliente.getId());
        notifyObservers(DataType.CLIENTE);
    }

     public void adicionarClienteComCep(String nome, String cep, String enderecoDigitado, String email, String telefone, String cpf, String senha) {
         System.out.println("Facade: Chamando adicionarClienteComCep. Usando endereço digitado: '" + enderecoDigitado + "'.");
         adicionarCliente(nome, enderecoDigitado, email, telefone, cpf, senha);
    }

    public Cliente atualizarCliente(int id, String nome, String endereco, String email, String telefone, String cpf, String senha) {
        Cliente estadoAntigo = clienteDAO.exibir(id);
        if (estadoAntigo == null) {
            throw new RuntimeException("Cliente com ID " + id + " não encontrado para atualização.");
        }
        Cliente estadoAntigoCopia = new Cliente(estadoAntigo.getNome(), estadoAntigo.getEndereco(), estadoAntigo.getEmail(), estadoAntigo.getTelefone(), estadoAntigo.getCpf(), estadoAntigo.getSenha());
        estadoAntigoCopia.setId(id);

        if (nome == null || nome.trim().isEmpty()) throw new IllegalArgumentException("Nome do cliente não pode ser vazio.");
        if (endereco == null || endereco.trim().isEmpty()) throw new IllegalArgumentException("Endereço não pode ser vazio.");
        if (!ValidadorUtil.isEmailValido(email)) throw new IllegalArgumentException("Email inválido: " + email);
        if (!ValidadorUtil.isCpfValido(cpf)) throw new IllegalArgumentException("CPF inválido: " + cpf);

        Cliente clienteAtualizado = new Cliente(nome.trim(), endereco.trim(), email.trim().toLowerCase(), telefone, cpf, senha);
        clienteAtualizado.setId(id);

        clienteDAO.alterar(clienteAtualizado); 

        // Notifica observers sobre a mudança
        notifyObservers(DataType.CLIENTE);
        notifyObservers(DataType.CONSULTA);
        notifyObservers(DataType.AGENDA);
        notifyObservers(DataType.ANIMAL);

        return estadoAntigoCopia;
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

        if (!animalDAO.listarPorCliente(id).isEmpty()) {
            throw new RuntimeException("Não é possível remover. Cliente ID " + id + " possui animais associados.");
        }

        clienteDAO.remover(id);

        // Notifica observers
        notifyObservers(DataType.CLIENTE);
        // Notificar outras áreas afetadas pela remoção do cliente
        notifyObservers(DataType.ANIMAL); // Lista de animais pode mudar (embora já devesse estar vazia)
        notifyObservers(DataType.CONSULTA); // Consultas passadas do cliente podem ser afetadas em relatórios
        notifyObservers(DataType.AGENDA); // Itens da agenda associados a este cliente

        return clienteParaRemover; // Retorna o objeto que foi efetivamente removido
    }

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
        animalDAO.inserir(animal);
        System.out.println("Animal inserido via Facade/DAO (adicionarAnimalObj): ID " + animal.getId() + " para Cliente ID " + animal.getClienteId());
        notifyObservers(DataType.ANIMAL);
        notifyObservers(DataType.CLIENTE);
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
        if (clienteDAO.exibir(animalAtualizado.getClienteId()) == null) {
             throw new RuntimeException("Não é possível atualizar animal. Cliente dono (ID: " + animalAtualizado.getClienteId() + ") não existe.");
        }

        Animal estadoAntigoCopia = new Animal(estadoAntigo.getNome(), estadoAntigo.getEspecie(), estadoAntigo.getRaca(), estadoAntigo.getDataNascimento(), estadoAntigo.getClienteId());
        estadoAntigoCopia.setId(animalId);

        // Realiza a alteração no DAO
        animalDAO.alterar(animalAtualizado);

        // Notifica observers
        notifyObservers(DataType.ANIMAL);
        // Notificar outras áreas afetadas (Consulta, Agenda)
        notifyObservers(DataType.CONSULTA);
        notifyObservers(DataType.AGENDA);
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

        animalDAO.remover(id); // Remove do DAO

        // Notifica observers
        notifyObservers(DataType.ANIMAL);
        // Notificar outras áreas afetadas
        notifyObservers(DataType.CLIENTE);
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

        if (n == null || n.trim().isEmpty()) throw new IllegalArgumentException("Nome do veterinário não pode ser vazio.");
        if (!ValidadorUtil.isEmailValido(e)) throw new IllegalArgumentException("Email inválido: " + e);
        if (!ValidadorUtil.isCpfValido(c)) throw new IllegalArgumentException("CPF inválido: " + c);
        if (!ValidadorUtil.isCrmvValido(crmv)) throw new IllegalArgumentException("CRMV inválido: " + crmv);

        // Cria objeto atualizado
        Veterinario vetAtualizado = new Veterinario(n, e, t, c, s, crmv, esp);
        vetAtualizado.setId(id); // Define ID para alteração

        veterinarioDAO.alterar(vetAtualizado); // Altera no DAO

        // Notifica observers
        notifyObservers(DataType.VETERINARIO);
        notifyObservers(DataType.CONSULTA);
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

        veterinarioDAO.remover(id); // Remove do DAO

        // Notifica observers
        notifyObservers(DataType.VETERINARIO);
        notifyObservers(DataType.CONSULTA);
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

        Consulta estadoAntigo = consultaDAO.exibir(id);
        if (estadoAntigo == null) {
            throw new RuntimeException("Consulta com ID " + id + " não encontrada para atualização.");
        }

        Consulta.Builder builderCopia = new Consulta.Builder()
                .dataHora(estadoAntigo.getDataHora())
                .status(estadoAntigo.getStatus())
                .cliente(estadoAntigo.getCliente())
                .veterinario(estadoAntigo.getVeterinario());

        if (estadoAntigo.getAnimal() != null) {
            builderCopia.animal(estadoAntigo.getAnimal());
        }

        Consulta estadoAntigoCopia = builderCopia.build(); // Constrói a cópia
        estadoAntigoCopia.setId(id); // Define o ID na cópia também

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
        notifyObservers(DataType.AGENDA);
        notifyObservers(DataType.PRONTUARIO); 

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

        consultaDAO.remover(id);

        // Notifica observers
        notifyObservers(DataType.CONSULTA);
        notifyObservers(DataType.AGENDA);
        notifyObservers(DataType.PRONTUARIO);

        return consultaParaRemover;
    }

    /**
     * Adiciona um prontuário usando o ProntuarioController.
     * O Controller já lida com a lógica e notificação (se implementado lá).
     * Este método da facade apenas delega e re-notifica (ou confia na notificação do controller).
     * Retorna true se sucesso, false caso contrário.
     */
    public boolean adicionarProntuario(Prontuario prontuario) {
        Objects.requireNonNull(prontuario, "Objeto Prontuario não pode ser nulo.");
        if (prontuario.getConsulta() == null || buscarConsultaPorId(prontuario.getConsulta().getId()) == null) {
             throw new IllegalArgumentException("Consulta associada ao prontuário é inválida ou não existe.");
        }

        boolean sucesso = prontuarioController.adicionarProntuario(prontuario);
        if (sucesso) {
            notifyObservers(DataType.PRONTUARIO); // Garante notificação pela facade
        }
        return sucesso;
    }

    // --- Métodos de Leitura ---

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
    public List<Prontuario> listarProntuariosPorConsulta(int id) { return prontuarioController.listarProntuariosPorConsulta(id); }
    public Prontuario buscarProntuarioPorId(int id) { return prontuarioController.buscarProntuarioPorId(id); }

    // Relatório
    public List<Consulta> buscarConsultasParaRelatorio(int m, int a) {
         return listarTodasConsultas().stream()
                    .filter(c->c!=null && c.getDataHora()!=null && c.getDataHora().getMonthValue()==m && c.getDataHora().getYear()==a)
                    .toList();
     }

    // Autenticação
    public Cliente autenticarCliente(String e, String s) { return clienteDAO.autenticar(e, s); }
    public Veterinario autenticarVeterinario(String e, String s) { return veterinarioDAO.autenticar(e, s); }
    public Empresa autenticarEmpresa(String e, String s) { return empresaDAO.autenticar(e, s); }
}
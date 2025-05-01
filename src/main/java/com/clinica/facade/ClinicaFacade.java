package com.clinica.facade; // Ou o pacote que preferir

import com.clinica.DAO.*; // Importar DAOs
import com.clinica.controller.*; // Importar Controllers
import com.clinica.model.*; // Importar Models
import java.util.List;

/**
 * Facade Singleton para centralizar o acesso às operações da clínica.
 * Utiliza Factory Methods internos para a criação dos DAOs.
 * Simplifica a comunicação entre a camada de Visão e os Controllers/DAOs.
 */
public class ClinicaFacade {

    // --- Singleton Implementation ---
    private static volatile ClinicaFacade instance;

    // Instâncias dos DAOs (criados uma vez via Factory Methods)
    private final ClienteDAO clienteDAO;
    private final AnimalDAO animalDAO;
    private final VeterinarioDAO veterinarioDAO;
    private final ConsultaDAO consultaDAO;
    private final ProntuarioDAO prontuarioDAO;
    private final AgendaDAO agendaDAO;
    private final EmpresaDAO empresaDAO;

    // Instâncias dos Controllers (mantendo a instanciação original por enquanto)
    // Idealmente, poderiam também ser criados via Factory Methods se a criação fosse complexa
    private final ClienteController clienteController;
    private final AnimalController animalController;
    private final VeterinarioController veterinarioController;
    private final ConsultaController consultaController;
    private final ProntuarioController prontuarioController;

    // Construtor privado para Singleton
    private ClinicaFacade() {
        // --- Criação dos DAOs usando Factory Methods ---
        // Cria DAOs independentes primeiro
        this.clienteDAO = createClienteDAO();
        this.animalDAO = createAnimalDAO();
        this.veterinarioDAO = createVeterinarioDAO();
        this.empresaDAO = createEmpresaDAO();

        // Cria DAOs que dependem de outros, passando as instâncias criadas
        this.consultaDAO = createConsultaDAO(this.clienteDAO, this.animalDAO, this.veterinarioDAO);
        this.prontuarioDAO = createProntuarioDAO(this.consultaDAO);
        this.agendaDAO = createAgendaDAO(this.consultaDAO);
        // -------------------------------------------------

        // Inicializa os Controllers (mantendo a forma atual)
        // Se a criação dos controllers se tornar complexa,
        // poderiam ser usados Factory Methods para eles também.
        this.clienteController = new ClienteController();
        this.animalController = new AnimalController();
        this.veterinarioController = new VeterinarioController();
        this.consultaController = new ConsultaController();
        this.prontuarioController = new ProntuarioController();

        System.out.println("Instância de ClinicaFacade criada (com Factory Methods para DAOs).");
    }

    // Método público para obter a instância única (Double-Checked Locking)
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

    // --- Factory Methods Privados para Criação dos DAOs ---

    private ClienteDAO createClienteDAO() {
        // Encapsula a criação do ClienteDAO
        // Poderia ter lógica adicional aqui se necessário (configurações, etc.)
        return new ClienteDAO();
    }

    private AnimalDAO createAnimalDAO() {
        // Encapsula a criação do AnimalDAO
        return new AnimalDAO();
    }

    private VeterinarioDAO createVeterinarioDAO() {
        // Encapsula a criação do VeterinarioDAO
        return new VeterinarioDAO();
    }

    private EmpresaDAO createEmpresaDAO() {
        // Encapsula a criação do EmpresaDAO
        return new EmpresaDAO();
    }

    private ConsultaDAO createConsultaDAO(ClienteDAO clienteDep, AnimalDAO animalDep, VeterinarioDAO vetDep) {
        // Encapsula a criação do ConsultaDAO, recebendo suas dependências
        return new ConsultaDAO(clienteDep, animalDep, vetDep);
    }

    private ProntuarioDAO createProntuarioDAO(ConsultaDAO consultaDep) {
        // Encapsula a criação do ProntuarioDAO, recebendo sua dependência
        return new ProntuarioDAO(consultaDep);
    }

     private AgendaDAO createAgendaDAO(ConsultaDAO consultaDep) {
        // Encapsula a criação do AgendaDAO, recebendo sua dependência
        return new AgendaDAO(consultaDep);
    }

    // --- Métodos da Fachada (Delegando para os Controllers/DAOs) ---
    // (O corpo destes métodos permanece o mesmo da versão anterior da Facade)

    // == Cliente Operations ==
    public List<Cliente> listarTodosClientes() {
        // A implementação atual do ClienteController instancia seu próprio DAO.
        // Se o controller fosse refatorado para receber o DAO, usaríamos this.clienteDAO aqui.
        return clienteController.listarTodosClientes();
        // return this.clienteDAO.listarTodos(); // Alternativa se controller usasse o DAO injetado
    }

    public Cliente buscarClientePorId(int id) {
        return clienteController.buscarClientePorId(id);
        // return this.clienteDAO.exibir(id);
    }

    public List<Cliente> buscarClientesPorNome(String nome) {
        return clienteController.buscarClientesPorNome(nome);
        // return this.clienteDAO.pesquisarPorNome(nome);
    }

    public void adicionarCliente(String nome, String endereco, String email, String telefone, String cpf, String senha) {
        clienteController.adicionarCliente(nome, endereco, email, telefone, cpf, senha);
        // Ou, se o controller não existisse mais:
        // Cliente cliente = new Cliente(nome, endereco, email, telefone, cpf, senha);
        // this.clienteDAO.inserir(cliente);
    }

     public void adicionarClienteComCep(String nome, String cep, String email, String telefone, String cpf, String senha) {
        clienteController.adicionarClienteComCep(nome, cep, email, telefone, cpf, senha);
    }

    public void atualizarCliente(int id, String nome, String endereco, String email, String telefone, String cpf, String senha) {
        clienteController.atualizarCliente(id, nome, endereco, email, telefone, cpf, senha);
        // Ou:
        // Cliente cliente = new Cliente(nome, endereco, email, telefone, cpf, senha);
        // cliente.setId(id);
        // this.clienteDAO.alterar(cliente);
    }

    public void removerCliente(int id) {
        clienteController.removerCliente(id);
        // this.clienteDAO.remover(id);
    }

    // == Animal Operations ==
    public List<Animal> listarAnimaisPorCliente(int clienteId) {
        return animalController.listarAnimaisPorCliente(clienteId);
        // return this.animalDAO.listarPorCliente(clienteId);
    }

    public Animal buscarAnimalPorId(int id) {
        return animalController.buscarAnimalPorId(id);
        // return this.animalDAO.exibir(id);
    }

    public boolean adicionarAnimalObj(Animal animal) {
        // O método no controller já usa o DAO interno dele.
        // Se quiséssemos usar o DAO da facade: this.animalDAO.inserir(animal); return true; (ajustar retorno)
        return animalController.adicionarAnimalObj(animal);
    }

     public boolean atualizarAnimalObj(Animal animal) {
        // Idem acima: this.animalDAO.alterar(animal); return true;
        return animalController.atualizarAnimalObj(animal);
    }

    public void removerAnimal(int id) {
        animalController.removerAnimal(id);
        // this.animalDAO.remover(id);
    }

    // == Veterinario Operations ==
    public List<Veterinario> listarTodosVeterinarios() {
        return veterinarioController.listarTodosVeterinarios();
        // return this.veterinarioDAO.listarTodos();
    }

    public Veterinario buscarVeterinarioPorId(int id) {
        return veterinarioController.buscarVeterinarioPorId(id);
        // return this.veterinarioDAO.exibir(id);
    }

    public void adicionarVeterinario(String nome, String email, String telefone, String cpf, String senha, String crmv, String especialidade) {
        veterinarioController.adicionarVeterinario(nome, email, telefone, cpf, senha, crmv, especialidade);
        // Veterinario vet = new Veterinario(nome, email, telefone, cpf, senha, crmv, especialidade);
        // this.veterinarioDAO.inserir(vet);
    }

    public void atualizarVeterinario(int id, String nome, String email, String telefone, String cpf, String senha, String crmv, String especialidade) {
        veterinarioController.atualizarVeterinario(id, nome, email, telefone, cpf, senha, crmv, especialidade);
        // Veterinario vet = new Veterinario(nome, email, telefone, cpf, senha, crmv, especialidade);
        // vet.setId(id);
        // this.veterinarioDAO.alterar(vet);
    }

    public void removerVeterinario(int id) {
        veterinarioController.removerVeterinario(id);
        // this.veterinarioDAO.remover(id);
    }

    // == Consulta Operations ==
    // Aqui usamos diretamente os DAOs criados pela facade, pois eles têm a lógica correta de persistência e relacionamento
    public List<Consulta> listarTodasConsultas() {
        return consultaDAO.listarTodos(); // Usa o DAO da facade que relê o arquivo
    }

    public Consulta buscarConsultaPorId(int id) {
        return consultaDAO.exibir(id); // Usa o DAO da facade que carrega relacionamentos
    }

    public void adicionarConsulta(Consulta consulta) {
        consultaDAO.inserir(consulta); // Delega para o DAO da facade
    }

    public void atualizarConsulta(int id, Consulta consultaAtualizada) {
         // Garante que o ID está no objeto antes de passar para o DAO
         consultaAtualizada.setId(id);
         consultaDAO.alterar(consultaAtualizada); // Delega para o DAO da facade
    }

    public void removerConsulta(int id) {
        consultaDAO.remover(id); // Delega para o DAO da facade
    }

    // == Agenda Operations ==
    public List<Agenda> listarAgendaPorVeterinario(int vetId) {
        return agendaDAO.listarPorVeterinario(vetId); // Usa o DAO da facade
    }

     public List<Agenda> listarAgendaCompleta() {
        return agendaDAO.listarTodos(); // Usa o DAO da facade
    }

    // == Prontuario Operations ==
    public boolean adicionarProntuario(Prontuario prontuario) {
        // O ProntuarioController instancia seu próprio DAO.
        // Se ele usasse o DAO injetado: this.prontuarioDAO.inserir(prontuario); return true;
        return prontuarioController.adicionarProntuario(prontuario);
    }

     public List<Prontuario> listarProntuariosPorConsulta(int consultaId) {
         return prontuarioController.listarProntuariosPorConsulta(consultaId);
         // return this.prontuarioDAO.listarPorConsulta(consultaId);
     }

    // == Relatórios ==
    public List<Consulta> buscarConsultasParaRelatorio(int mes, int ano) {
        List<Consulta> todas = listarTodasConsultas(); // Usa o método da facade
        return todas.stream()
                .filter(c -> c != null && c.getDataHora() != null)
                .filter(c -> c.getDataHora().getMonthValue() == mes && c.getDataHora().getYear() == ano)
                .toList(); // Use toList() para Java 16+ ou .collect(Collectors.toList())
    }

    // == Autenticação ==
    public Cliente autenticarCliente(String email, String senha) {
        return clienteDAO.autenticar(email, senha); // Delega ao DAO da facade
    }

    public Veterinario autenticarVeterinario(String email, String senha) {
        return veterinarioDAO.autenticar(email, senha); // Delega ao DAO da facade
    }

     public Empresa autenticarEmpresa(String email, String senha) {
        return empresaDAO.autenticar(email, senha); // Delega ao DAO da facade
    }
}

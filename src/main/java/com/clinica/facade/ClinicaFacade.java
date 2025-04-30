package com.clinica.facade; // Ou o pacote que preferir

import com.clinica.DAO.*; // Importar DAOs
import com.clinica.controller.*; // Importar Controllers
import com.clinica.model.*; // Importar Models
import java.util.List; // Importar itens específicos da view se necessário (melhor evitar)

/**
 * Facade Singleton para centralizar o acesso às operações da clínica.
 * Simplifica a comunicação entre a camada de Visão e os Controllers/DAOs.
 */
public class ClinicaFacade {

    // --- Singleton Implementation ---
    private static volatile ClinicaFacade instance; // volatile para garantir visibilidade entre threads

    // Instâncias dos DAOs (criados uma vez)
    private final ClienteDAO clienteDAO;
    private final AnimalDAO animalDAO;
    private final VeterinarioDAO veterinarioDAO;
    private final ConsultaDAO consultaDAO;
    private final ProntuarioDAO prontuarioDAO;
    private final AgendaDAO agendaDAO;
    // Adicionar outros DAOs se existirem (ex: EmpresaDAO)
    private final EmpresaDAO empresaDAO;

    // Instâncias dos Controllers (usando os DAOs criados)
    private final ClienteController clienteController;
    private final AnimalController animalController;
    private final VeterinarioController veterinarioController;
    private final ConsultaController consultaController;
    private final ProntuarioController prontuarioController;
    // Não há AgendaController, usamos AgendaDAO diretamente ou criamos um
    // controller

    // Construtor privado para Singleton
    private ClinicaFacade() {
        // Inicializa os DAOs na ordem correta de dependência
        this.clienteDAO = new ClienteDAO();
        this.animalDAO = new AnimalDAO(); // AnimalDAO não parece ter dependências no construtor
        this.veterinarioDAO = new VeterinarioDAO();
        this.empresaDAO = new EmpresaDAO(); // Se for usar

        // DAOs que dependem de outros DAOs
        this.consultaDAO = new ConsultaDAO(clienteDAO, animalDAO, veterinarioDAO);
        this.prontuarioDAO = new ProntuarioDAO(consultaDAO); // ProntuarioDAO precisa do ConsultaDAO
        this.agendaDAO = new AgendaDAO(consultaDAO); // AgendaDAO precisa do ConsultaDAO

        // Inicializa os Controllers (eles instanciam seus próprios DAOs atualmente,
        // o ideal seria injetar os DAOs criados aqui, mas vamos manter como está por
        // enquanto
        // para minimizar as mudanças nos controllers existentes)
        // Se os controllers fossem modificados para aceitar DAOs no construtor, seria:
        // this.clienteController = new ClienteController(this.clienteDAO);
        // this.animalController = new AnimalController(this.animalDAO);
        // ...etc
        // Como não foram modificados, instanciamos normalmente:
        this.clienteController = new ClienteController();
        this.animalController = new AnimalController();
        this.veterinarioController = new VeterinarioController();
        this.consultaController = new ConsultaController(); // Este já instancia os DAOs internamente de forma correta
        this.prontuarioController = new ProntuarioController(); // Este já instancia os DAOs internamente de forma
                                                                // correta

        System.out.println("Instância de ClinicaFacadeSingleton criada."); // Log para depuração
    }

    // Método público para obter a instância única (Double-Checked Locking para
    // thread-safety)
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

    // --- Métodos da Fachada (Delegando para os Controllers/DAOs) ---

    // == Cliente Operations ==
    public List<Cliente> listarTodosClientes() {
        return clienteController.listarTodosClientes();
    }

    public Cliente buscarClientePorId(int id) {
        return clienteController.buscarClientePorId(id);
    }

    public List<Cliente> buscarClientesPorNome(String nome) {
        return clienteController.buscarClientesPorNome(nome);
    }

    public void adicionarCliente(String nome, String endereco, String email, String telefone, String cpf,
            String senha) {
        clienteController.adicionarCliente(nome, endereco, email, telefone, cpf, senha);
    }

    public void adicionarClienteComCep(String nome, String cep, String email, String telefone, String cpf,
            String senha) {
        clienteController.adicionarClienteComCep(nome, cep, email, telefone, cpf, senha);
    }

    public void atualizarCliente(int id, String nome, String endereco, String email, String telefone, String cpf,
            String senha) {
        clienteController.atualizarCliente(id, nome, endereco, email, telefone, cpf, senha);
    }

    public void removerCliente(int id) {
        clienteController.removerCliente(id);
    }

    // == Animal Operations ==
    public List<Animal> listarAnimaisPorCliente(int clienteId) {
        return animalController.listarAnimaisPorCliente(clienteId);
    }

    public Animal buscarAnimalPorId(int id) {
        return animalController.buscarAnimalPorId(id);
    }

    public boolean adicionarAnimalObj(Animal animal) {
        return animalController.adicionarAnimalObj(animal);
    }

    public boolean atualizarAnimalObj(Animal animal) {
        return animalController.atualizarAnimalObj(animal);
    }

    public void removerAnimal(int id) {
        animalController.removerAnimal(id);
    }

    // == Veterinario Operations ==
    public List<Veterinario> listarTodosVeterinarios() {
        return veterinarioController.listarTodosVeterinarios();
    }

    public Veterinario buscarVeterinarioPorId(int id) {
        return veterinarioController.buscarVeterinarioPorId(id);
    }

    public void adicionarVeterinario(String nome, String email, String telefone, String cpf, String senha, String crmv,
            String especialidade) {
        veterinarioController.adicionarVeterinario(nome, email, telefone, cpf, senha, crmv, especialidade);
    }

    public void atualizarVeterinario(int id, String nome, String email, String telefone, String cpf, String senha,
            String crmv, String especialidade) {
        veterinarioController.atualizarVeterinario(id, nome, email, telefone, cpf, senha, crmv, especialidade);
    }

    public void removerVeterinario(int id) {
        veterinarioController.removerVeterinario(id);
    }

    // == Consulta Operations ==
    public List<Consulta> listarTodasConsultas() {
        // Usa o método do DAO que já relê o arquivo (conforme correção anterior)
        return consultaDAO.listarTodos();
        // Ou, se o controller tivesse um método que garantisse a releitura:
        // return consultaController.listarTodasConsultas();
    }

    public Consulta buscarConsultaPorId(int id) {
        // Usa o método do DAO que já carrega relacionamentos
        return consultaDAO.exibir(id);
        // Ou, se o controller fizesse isso:
        // return consultaController.buscarConsultaPorId(id);
    }

    public void adicionarConsulta(Consulta consulta) {
        // Delega para o DAO diretamente ou para o controller
        consultaDAO.inserir(consulta);
        // consultaController.adicionarConsulta(consulta);
    }

    public void atualizarConsulta(int id, Consulta consultaAtualizada) {
        // Delega para o DAO diretamente ou para o controller
        consultaDAO.alterar(consultaAtualizada); // O ID já deve estar na consultaAtualizada
        // consultaController.atualizarConsulta(id, consultaAtualizada);
    }

    public void removerConsulta(int id) {
        // Delega para o DAO diretamente ou para o controller
        consultaDAO.remover(id);
        // consultaController.removerConsulta(id);
    }

    // == Agenda Operations ==
    // Acessando o AgendaDAO diretamente através da fachada
    public List<Agenda> listarAgendaPorVeterinario(int vetId) {
        // AgendaDAO foi inicializado no construtor da Facade
        return agendaDAO.listarPorVeterinario(vetId);
    }

    public List<Agenda> listarAgendaCompleta() {
        // AgendaDAO foi inicializado no construtor da Facade
        return agendaDAO.listarTodos();
    }

    // == Prontuario Operations == (Exemplo)
    public boolean adicionarProntuario(Prontuario prontuario) {
        return prontuarioController.adicionarProntuario(prontuario);
    }

    public List<Prontuario> listarProntuariosPorConsulta(int consultaId) {
        return prontuarioController.listarProntuariosPorConsulta(consultaId);
    }

    // Adicionar outros métodos necessários (atualizar, remover, buscar por ID,
    // etc.)

    // == Relatórios (exemplo de como centralizar) ==
    // Você pode mover a lógica de geração de relatórios para cá ou
    // manter um ReportController/Service e chamá-lo aqui.
    // Por simplicidade, vamos assumir que a lógica está aqui por enquanto.

    // Este método pode precisar ser ajustado dependendo de como você quer gerar
    // relatórios
    // Exemplo: buscar dados para relatório de consultas por período/vet
    public List<Consulta> buscarConsultasParaRelatorio(int mes, int ano) {
        List<Consulta> todas = listarTodasConsultas(); // Usa o método da facade que já lê do arquivo
        return todas.stream()
                .filter(c -> c != null && c.getDataHora() != null)
                .filter(c -> c.getDataHora().getMonthValue() == mes && c.getDataHora().getYear() == ano)
                .toList(); // Use toList() para Java 16+ ou .collect(Collectors.toList())
    }

    // == Autenticação (Exemplo) ==
    public Cliente autenticarCliente(String email, String senha) {
        return clienteDAO.autenticar(email, senha); // Delega ao DAO
    }

    public Veterinario autenticarVeterinario(String email, String senha) {
        return veterinarioDAO.autenticar(email, senha); // Delega ao DAO
    }

    public Empresa autenticarEmpresa(String email, String senha) {
        return empresaDAO.autenticar(email, senha); // Delega ao DAO
    }
}
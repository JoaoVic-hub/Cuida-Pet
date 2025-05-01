package com.clinica.facade;

import com.clinica.DAO.*;
import com.clinica.Util.ValidadorUtil; // Mantido para ProntuarioController
import com.clinica.controller.*;
import com.clinica.model.*;
import com.clinica.observer.DataObserver;
import com.clinica.observer.DataType; // Importar Validador
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.SwingUtilities;

/**
 * Facade Singleton atuando como Subject (Observado) no padrão Observer.
 * Centraliza o acesso às operações e notifica observers sobre mudanças nos dados.
 * Usa DAOs diretamente para operações de escrita/modificação.
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
    private final ProntuarioController prontuarioController;

    // Construtor privado
    private ClinicaFacade() {
        this.clienteDAO = createClienteDAO();
        this.animalDAO = createAnimalDAO();
        this.veterinarioDAO = createVeterinarioDAO();
        this.empresaDAO = createEmpresaDAO();
        this.consultaDAO = createConsultaDAO(this.clienteDAO, this.animalDAO, this.veterinarioDAO);
        this.prontuarioDAO = createProntuarioDAO(this.consultaDAO);
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
    private ConsultaDAO createConsultaDAO(ClienteDAO c, AnimalDAO a, VeterinarioDAO v) { return new ConsultaDAO(c, a, v); }
    private ProntuarioDAO createProntuarioDAO(ConsultaDAO c) { return new ProntuarioDAO(c); }
    private AgendaDAO createAgendaDAO(ConsultaDAO c) { return new AgendaDAO(c); }
    private ProntuarioController createProntuarioController(ProntuarioDAO pDAO, ConsultaDAO cDAO) { return new ProntuarioController(pDAO, cDAO); }


    // --- Métodos Observer ---
    public void addObserver(DataObserver observer) {
        if (observer != null) {
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
        for (DataObserver observer : observers) {
            SwingUtilities.invokeLater(() -> {
                try { observer.update(typeChanged); } catch (Exception e) {
                    System.err.println("Erro no update do observer " + observer.getClass().getSimpleName() + ": " + e.getMessage()); e.printStackTrace();
                }
            });
        }
     }

    // --- Métodos da Fachada ---

    // == Cliente Operations ==
    public void adicionarCliente(String nome, String endereco, String email, String telefone, String cpf, String senha) {
        // Validações básicas
        if (nome == null || nome.trim().isEmpty()) throw new IllegalArgumentException("Nome do cliente não pode ser vazio.");
        if (endereco == null || endereco.trim().isEmpty()) throw new IllegalArgumentException("Endereço não pode ser vazio.");
        if (!ValidadorUtil.isEmailValido(email)) throw new IllegalArgumentException("Email inválido: " + email);
        if (!ValidadorUtil.isCpfValido(cpf)) throw new IllegalArgumentException("CPF inválido: " + cpf);
        if (senha == null || senha.trim().isEmpty()) throw new IllegalArgumentException("Senha não pode ser vazia.");

        Cliente cliente = new Cliente(nome.trim(), endereco.trim(), email.trim().toLowerCase(), telefone, cpf, senha);
        this.clienteDAO.inserir(cliente); // Usa o DAO diretamente
        System.out.println("Cliente inserido via Facade/DAO (adicionarCliente): ID " + cliente.getId());
        notifyObservers(DataType.CLIENTE);
    }

    /**
     * Adiciona um cliente usando o endereço e CEP fornecidos diretamente, sem busca automática.
     * Este método agora simplesmente chama adicionarCliente, passando o endereço recebido.
     * @param nome Nome do cliente.
     * @param cep CEP digitado pelo usuário (informativo, não usado para busca).
     * @param enderecoDigitado Endereço completo digitado pelo usuário. <<< PARÂMETRO CORRIGIDO
     * @param email Email do cliente.
     * @param telefone Telefone do cliente.
     * @param cpf CPF do cliente.
     * @param senha Senha do cliente.
     */
     // ***** ASSINATURA CORRIGIDA PARA 7 PARÂMETROS *****
     public void adicionarClienteComCep(String nome, String cep, String enderecoDigitado, String email, String telefone, String cpf, String senha) {
         System.out.println("Facade: Chamando adicionarClienteComCep. Usando endereço digitado: '" + enderecoDigitado + "'. CEP '" + cep + "' será ignorado para busca.");
         // Chama o método principal de adicionar cliente, passando o enderecoDigitado recebido
         adicionarCliente(nome, enderecoDigitado, email, telefone, cpf, senha);
    }


    public void atualizarCliente(int id, String nome, String endereco, String email, String telefone, String cpf, String senha) {
        // Validações...
        Cliente cliente = new Cliente(nome.trim(), endereco.trim(), email.trim().toLowerCase(), telefone, cpf, senha);
        cliente.setId(id);
        this.clienteDAO.alterar(cliente);
        notifyObservers(DataType.CLIENTE);
        notifyObservers(DataType.CONSULTA);
        notifyObservers(DataType.AGENDA);
        notifyObservers(DataType.ANIMAL);
    }

    // ... (Restante dos métodos da Facade: removerCliente, Animal, Vet, Consulta, etc.) ...
    public void removerCliente(int id) {
        if (!this.animalDAO.listarPorCliente(id).isEmpty()) { throw new RuntimeException("Cliente possui animais."); }
        this.clienteDAO.remover(id);
        notifyObservers(DataType.CLIENTE);
        notifyObservers(DataType.CONSULTA); notifyObservers(DataType.AGENDA); notifyObservers(DataType.ANIMAL);
    }
    public boolean adicionarAnimalObj(Animal animal) {
        if (animal == null) return false;
        if (this.clienteDAO.exibir(animal.getClienteId()) == null) { System.err.println("Facade: Cliente inexistente ID: " + animal.getClienteId()); return false; }
        this.animalDAO.inserir(animal);
        notifyObservers(DataType.ANIMAL); notifyObservers(DataType.CLIENTE);
        return true;
    }
     public boolean atualizarAnimalObj(Animal animal) {
        if (animal == null) return false;
        this.animalDAO.alterar(animal);
        notifyObservers(DataType.ANIMAL); notifyObservers(DataType.CONSULTA); notifyObservers(DataType.AGENDA);
        return true;
    }
    public void removerAnimal(int id) { this.animalDAO.remover(id); notifyObservers(DataType.ANIMAL); notifyObservers(DataType.CLIENTE); notifyObservers(DataType.CONSULTA); notifyObservers(DataType.AGENDA); }
    public void adicionarVeterinario(String n, String e, String t, String c, String s, String crmv, String esp) { if (!ValidadorUtil.isCrmvValido(crmv)) throw new IllegalArgumentException("CRMV inválido."); Veterinario vet = new Veterinario(n, e, t, c, s, crmv, esp); this.veterinarioDAO.inserir(vet); notifyObservers(DataType.VETERINARIO); }
    public void atualizarVeterinario(int id, String n, String e, String t, String c, String s, String crmv, String esp) { if (!ValidadorUtil.isCrmvValido(crmv)) throw new IllegalArgumentException("CRMV inválido."); Veterinario vet = new Veterinario(n, e, t, c, s, crmv, esp); vet.setId(id); this.veterinarioDAO.alterar(vet); notifyObservers(DataType.VETERINARIO); notifyObservers(DataType.CONSULTA); notifyObservers(DataType.AGENDA); }
    public void removerVeterinario(int id) { this.veterinarioDAO.remover(id); notifyObservers(DataType.VETERINARIO); notifyObservers(DataType.CONSULTA); notifyObservers(DataType.AGENDA); }
    public void adicionarConsulta(Consulta con) { consultaDAO.inserir(con); notifyObservers(DataType.CONSULTA); notifyObservers(DataType.AGENDA); }
    public void atualizarConsulta(int id, Consulta con) { con.setId(id); consultaDAO.alterar(con); notifyObservers(DataType.CONSULTA); notifyObservers(DataType.AGENDA); }
    public void removerConsulta(int id) { consultaDAO.remover(id); notifyObservers(DataType.CONSULTA); notifyObservers(DataType.AGENDA); notifyObservers(DataType.PRONTUARIO); }
    public boolean adicionarProntuario(Prontuario p) { boolean suc = prontuarioController.adicionarProntuario(p); if(suc) { notifyObservers(DataType.PRONTUARIO); } return suc; }
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
    public List<Consulta> buscarConsultasParaRelatorio(int m, int a) { return listarTodasConsultas().stream().filter(c->c!=null&&c.getDataHora()!=null&&c.getDataHora().getMonthValue()==m&&c.getDataHora().getYear()==a).toList(); }
    public Cliente autenticarCliente(String e, String s) { return clienteDAO.autenticar(e, s); }
    public Veterinario autenticarVeterinario(String e, String s) { return veterinarioDAO.autenticar(e, s); }
    public Empresa autenticarEmpresa(String e, String s) { return empresaDAO.autenticar(e, s); }

}

package com.clinica.facade;

import com.clinica.DAO.*;
import com.clinica.model.*;
import java.util.List;
import java.sql.SQLException;

/**
 * Implementa o padrão Facade para fornecer uma interface simplificada
 * para as operações comuns do sistema da clínica veterinária.
 * Oculta a complexidade da interação entre diferentes DAOs e Controllers.
 */
public class ClinicaFacade {

    // DAOs obtidos através da Factory
    private ClienteDAO clienteDAO;
    private AnimalDAO animalDAO;
    private ConsultaDAO consultaDAO;
    private VeterinarioDAO veterinarioDAO;
    // Adicione outros DAOs conforme necessário

    /**
     * Construtor da Facade. Inicializa os DAOs necessários usando a DAOFactory.
     */
    public ClinicaFacade() {
        // Nota: A DAOFactory já utiliza a conexão Singleton
        this.clienteDAO = DAOFactory.createClienteDAO();
        this.animalDAO = DAOFactory.createAnimalDAO();
        this.consultaDAO = DAOFactory.createConsultaDAO();
        this.veterinarioDAO = DAOFactory.createVeterinarioDAO();
        // Inicialize outros DAOs aqui
    }

    // --- Operações Simplificadas --- 

    /**
     * Cadastra um novo cliente no sistema.
     *
     * @param cliente O objeto Cliente a ser cadastrado.
     * @return true se o cadastro foi bem-sucedido, false caso contrário.
     */
    public boolean cadastrarCliente(Cliente cliente) {
        try {
            clienteDAO.inserir(cliente);
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar cliente via Facade: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cadastra um novo animal associado a um cliente existente.
     *
     * @param animal O objeto Animal a ser cadastrado.
     * @return true se o cadastro foi bem-sucedido, false caso contrário.
     */
    public boolean cadastrarAnimal(Animal animal) {
        try {
            animalDAO.inserir(animal);
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar animal via Facade: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Cadastra um novo veterinário no sistema.
     *
     * @param veterinario O objeto Veterinario a ser cadastrado.
     * @return true se o cadastro foi bem-sucedido, false caso contrário.
     */
    public boolean cadastrarVeterinario(Veterinario veterinario) {
        try {
            veterinarioDAO.inserir(veterinario);
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar veterinário via Facade: " + e.getMessage());
            return false;
        }
    }

    /**
     * Agenda uma nova consulta.
     *
     * @param consulta O objeto Consulta a ser agendado.
     * @return true se o agendamento foi bem-sucedido, false caso contrário.
     */
    public boolean agendarConsulta(Consulta consulta) {
        try {
            // Poderia incluir validações adicionais aqui (ex: verificar disponibilidade do veterinário)
            consultaDAO.inserir(consulta);
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao agendar consulta via Facade: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca um cliente pelo seu ID.
     *
     * @param idCliente O ID do cliente.
     * @return O objeto Cliente encontrado, ou null se não encontrado.
     */
    public Cliente buscarClientePorId(int idCliente) {
        try {
            return clienteDAO.buscarPorId(idCliente);
        } catch (SQLException e) {
            System.err.println("Erro ao buscar cliente por ID via Facade: " + e.getMessage());
            return null;
        }
    }

    /**
     * Busca todos os animais de um determinado cliente.
     *
     * @param idCliente O ID do cliente.
     * @return Uma lista de Animais do cliente, ou uma lista vazia se não houver ou em caso de erro.
     */
    public List<Animal> buscarAnimaisPorCliente(int idCliente) {
        try {
            return animalDAO.listarPorCliente(idCliente);
        } catch (SQLException e) {
            System.err.println("Erro ao buscar animais por cliente via Facade: " + e.getMessage());
            return List.of(); // Retorna lista vazia em caso de erro
        }
    }
    
    /**
     * Lista todos os veterinários cadastrados.
     *
     * @return Uma lista de Veterinarios, ou uma lista vazia em caso de erro.
     */
    public List<Veterinario> listarTodosVeterinarios() {
        try {
            return veterinarioDAO.listarTodos();
        } catch (SQLException e) {
            System.err.println("Erro ao listar veterinários via Facade: " + e.getMessage());
            return List.of(); // Retorna lista vazia em caso de erro
        }
    }

    /**
     * Lista todas as consultas agendadas.
     *
     * @return Uma lista de Consultas, ou uma lista vazia em caso de erro.
     */
    public List<Consulta> listarTodasConsultas() {
        try {
            return consultaDAO.listarTodos();
        } catch (SQLException e) {
            System.err.println("Erro ao listar consultas via Facade: " + e.getMessage());
            return List.of(); // Retorna lista vazia em caso de erro
        }
    }

    // --- Adicionar mais métodos conforme a necessidade da aplicação ---
    // Exemplo: um método que cadastra cliente e seu primeiro animal de uma vez
    /*
    public boolean cadastrarClienteComAnimal(Cliente cliente, Animal animal) {
        try {
            // Idealmente, isso deveria ser uma transação
            clienteDAO.inserir(cliente);
            // Assume que o ID do cliente é gerado e setado no objeto cliente após inserção
            animal.setCliente(cliente); 
            animalDAO.inserir(animal);
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar cliente com animal via Facade: " + e.getMessage());
            // Rollback seria necessário em uma implementação transacional
            return false;
        }
    }
    */

    /**
     * Método para fechar a conexão Singleton ao final da aplicação.
     * Delega a chamada para a instância de ConexaoMySQL.
     */
    public void fecharConexaoBanco() {
        ConexaoMySQL.getInstancia().fecharConexao();
    }
}


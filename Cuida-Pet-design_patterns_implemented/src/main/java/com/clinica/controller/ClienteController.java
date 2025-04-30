package com.clinica.controller;

import com.clinica.DAO.ClienteDAO;
import com.clinica.DAO.DAOFactory; // Importar a Factory
import com.clinica.DTO.EnderecoViaCepDTO;
import com.clinica.adapter.EnderecoAdapter;
import com.clinica.model.Cliente;
// import com.google.gson.Gson; // Dependência externa - Adicionar ao projeto (Maven/Gradle)
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException; // Importar SQLException
import java.util.ArrayList; // Para retornar lista vazia
import java.util.List;

/**
 * Controller para gerenciar operações relacionadas a Clientes.
 * Utiliza ClienteDAO (obtido via DAOFactory) para interagir com o banco de dados.
 * Adicionado tratamento de exceções SQLException.
 * Inclui funcionalidade para buscar endereço via CEP (requer Gson).
 */
public class ClienteController {
    private ClienteDAO clienteDAO;
    // private Gson gson = new Gson(); // Instanciar Gson aqui requer a dependência configurada

    public ClienteController() {
        // Obtém a instância do DAO através da Factory
        this.clienteDAO = DAOFactory.createClienteDAO();
    }

    /**
     * Adiciona um novo cliente ao sistema.
     * 
     * @param nome Nome do cliente.
     * @param endereco Endereço completo.
     * @param email Email do cliente.
     * @param telefone Telefone do cliente.
     * @param cpf CPF do cliente.
     * @param senha Senha do cliente.
     * @param cep CEP do cliente.
     * @param numero Número do endereço.
     * @param complemento Complemento do endereço.
     * @return true se o cliente foi adicionado com sucesso, false caso contrário.
     */
    public boolean adicionarCliente(String nome, 
                                 String endereco, 
                                 String email,
                                 String telefone, 
                                 String cpf, 
                                 String senha,
                                 String cep,      // Adicionado
                                 String numero,   // Adicionado
                                 String complemento // Adicionado
                                 ) 
    {
        // Usa o construtor atualizado de Cliente
        Cliente cliente = new Cliente(nome, endereco, email, telefone, cpf, senha, cep, numero, complemento);
        try {
            clienteDAO.inserir(cliente);
            System.out.println("Cliente cadastrado com sucesso! ID: " + cliente.getId());
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar cliente: " + e.getMessage());
            return false;
        }
    }

    /**
     * Adiciona um novo cliente buscando o endereço pelo CEP.
     * Requer a biblioteca Gson configurada no projeto.
     *
     * @param nome Nome do cliente.
     * @param cep CEP para busca do endereço.
     * @param numero Número do endereço (complementa o CEP).
     * @param complemento Complemento do endereço (complementa o CEP).
     * @param email Email do cliente.
     * @param telefone Telefone do cliente.
     * @param cpf CPF do cliente.
     * @param senha Senha do cliente.
     * @return true se o cliente foi adicionado com sucesso, false caso contrário.
     */
    public boolean adicionarClienteComCep(String nome, 
                                       String cep, 
                                       String numero, // Adicionado como parâmetro
                                       String complemento, // Adicionado como parâmetro
                                       String email,
                                       String telefone, 
                                       String cpf, 
                                       String senha) 
    {
        String enderecoCompleto = "Endereço não encontrado via CEP"; // Valor padrão
        String cepRetornado = cep; // Usa o CEP fornecido como padrão
        
        if (cep != null && !cep.trim().isEmpty()) {
             try {
                // Nota: A instanciação do Gson deve ocorrer apenas se a dependência estiver presente.
                // Idealmente, injetar Gson ou verificar sua disponibilidade.
                // com.google.gson.Gson gson = new com.google.gson.Gson(); 
                // EnderecoViaCepDTO dto = buscarEnderecoPorCep(cep, gson);
                EnderecoViaCepDTO dto = buscarEnderecoPorCep(cep); // Chama o método atualizado
                if (dto != null && dto.getLogradouro() != null) {
                    // Usa o adapter para formatar o endereço base
                    enderecoCompleto = EnderecoAdapter.fromViaCepDTO(dto);
                    cepRetornado = dto.getCep() != null ? dto.getCep() : cep; // Usa o CEP retornado se disponível
                } else {
                    System.out.println("Não foi possível obter o endereço completo para o CEP: " + cep);
                }
            } catch (Exception e) {
                System.err.println("Erro ao buscar endereço por CEP (" + cep + "): " + e.getMessage());
                // Continua com o endereço padrão
            }
        }
        
        // Cria o cliente com os dados obtidos/fornecidos
        Cliente cliente = new Cliente(nome, enderecoCompleto, email, telefone, cpf, senha, cepRetornado, numero, complemento);
        
        try {
            clienteDAO.inserir(cliente);
            System.out.println("Cliente cadastrado com sucesso (via CEP)! ID: " + cliente.getId());
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao inserir cliente (via CEP): " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza os dados de um cliente existente.
     *
     * @param id ID do cliente a ser atualizado.
     * @param nome Novo nome.
     * @param endereco Novo endereço.
     * @param email Novo email.
     * @param telefone Novo telefone.
     * @param cpf Novo CPF.
     * @param senha Nova senha (CUIDADO: Texto plano).
     * @param cep Novo CEP.
     * @param numero Novo número.
     * @param complemento Novo complemento.
     * @return true se o cliente foi atualizado com sucesso, false caso contrário.
     */
    public boolean atualizarCliente(int id, 
                                 String nome, 
                                 String endereco, 
                                 String email,
                                 String telefone, 
                                 String cpf, 
                                 String senha,
                                 String cep,      // Adicionado
                                 String numero,   // Adicionado
                                 String complemento // Adicionado
                                 ) 
    {
        Cliente cliente = new Cliente(nome, endereco, email, telefone, cpf, senha, cep, numero, complemento);
        cliente.setId(id); // Define o ID para identificar qual cliente atualizar
        try {
            clienteDAO.alterar(cliente);
            System.out.println("Cliente atualizado com sucesso! ID: " + id);
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar cliente (ID: " + id + "): " + e.getMessage());
            return false;
        }
    }

    /**
     * Remove um cliente do sistema.
     *
     * @param id ID do cliente a ser removido.
     * @return true se o cliente foi removido com sucesso, false caso contrário.
     */
    public boolean removerCliente(int id) {
        try {
            clienteDAO.remover(id);
            System.out.println("Cliente removido com sucesso! ID: " + id);
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao remover cliente (ID: " + id + "): " + e.getMessage());
            // Considerar verificar o código de erro SQL para chave estrangeira
            return false;
        }
    }

    /**
     * Lista todos os clientes cadastrados.
     *
     * @return Uma lista de todos os clientes, ou uma lista vazia em caso de erro.
     */
    public List<Cliente> listarTodosClientes() {
        try {
            return clienteDAO.listarTodos();
        } catch (SQLException e) {
            System.err.println("Erro ao listar todos os clientes: " + e.getMessage());
            return new ArrayList<>(); // Retorna lista vazia
        }
    }

    /**
     * Busca o primeiro cliente encontrado com o nome especificado (busca parcial).
     *
     * @param nome Nome ou parte do nome a pesquisar.
     * @return O primeiro Cliente encontrado, ou null se nenhum for encontrado ou em caso de erro.
     */
    public Cliente buscarClientePorNome(String nome) {
        try {
            List<Cliente> clientes = clienteDAO.pesquisarPorNome(nome);
            if (clientes != null && !clientes.isEmpty()) {
                return clientes.get(0); // Retorna o primeiro encontrado
            }
            return null;
        } catch (SQLException e) {
             System.err.println("Erro ao pesquisar cliente por nome (" + nome + "): " + e.getMessage());
            return null;
        }
    }

    /**
     * Busca todos os clientes que correspondem ao nome especificado (busca parcial).
     *
     * @param nome Nome ou parte do nome a pesquisar.
     * @return Uma lista de Clientes encontrados, ou uma lista vazia em caso de erro.
     */
    public List<Cliente> buscarClientesPorNome(String nome) {
         try {
            return clienteDAO.pesquisarPorNome(nome);
        } catch (SQLException e) {
             System.err.println("Erro ao pesquisar clientes por nome (" + nome + "): " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Busca um cliente pelo seu ID.
     *
     * @param id O ID do cliente.
     * @return O objeto Cliente encontrado, ou null se não for encontrado ou em caso de erro.
     */
    public Cliente buscarClientePorId(int id) {
        try {
            // Corrigido: usar buscarPorId em vez de exibir
            return clienteDAO.buscarPorId(id); 
        } catch (SQLException e) {
            System.err.println("Erro ao buscar cliente por ID (ID: " + id + "): " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Autentica um cliente pelo email e senha.
     * 
     * @param email Email do cliente.
     * @param senha Senha do cliente.
     * @return O Cliente autenticado, ou null se a autenticação falhar ou ocorrer erro.
     */
     public Cliente autenticarCliente(String email, String senha) {
        try {
            return clienteDAO.autenticar(email, senha);
        } catch (SQLException e) {
            System.err.println("Erro ao autenticar cliente (email: " + email + "): " + e.getMessage());
            return null;
        }
    }

    /**
     * Busca informações de endereço usando a API ViaCEP.
     * Requer a biblioteca Gson.
     *
     * @param cep O CEP a ser consultado (apenas números).
     * @return Um EnderecoViaCepDTO com os dados encontrados, ou null em caso de erro ou CEP não encontrado.
     * @throws Exception Se ocorrer um erro de conexão ou parsing.
     */
    // private EnderecoViaCepDTO buscarEnderecoPorCep(String cep, com.google.gson.Gson gson) throws Exception { // Passando Gson como parâmetro
    private EnderecoViaCepDTO buscarEnderecoPorCep(String cep) throws Exception { // Sem passar Gson
        // Remove caracteres não numéricos do CEP
        String cepLimpo = cep.replaceAll("[^0-9]", "");
        if (cepLimpo.length() != 8) {
            throw new IllegalArgumentException("CEP inválido: " + cep);
        }

        // Atenção: A instanciação do Gson deve ocorrer aqui ou ser injetada
        // Isso só funcionará se a dependência Gson estiver no classpath.
        com.google.gson.Gson gson = new com.google.gson.Gson(); 

        // URL da API ViaCEP
        // AVISO: O construtor new URL(String) está depreciado desde Java 20.
        // Alternativa: URI.create(urlString).toURL();
        String urlString = "https://viacep.com.br/ws/" + cepLimpo + "/json/";
        URL url = new URL(urlString); 
        
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(5000); // Timeout de conexão 5 seg
            conn.setReadTimeout(5000);    // Timeout de leitura 5 seg

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // Sucesso
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                EnderecoViaCepDTO dto = gson.fromJson(br, EnderecoViaCepDTO.class);
                // Verifica se o ViaCEP retornou erro no JSON (ex: CEP não encontrado)
                if (dto.isErro()) { 
                    System.out.println("CEP não encontrado na base do ViaCEP: " + cep);
                    return null;
                }
                return dto;
            } else {
                System.err.println("Erro ao acessar o ViaCEP: HTTP " + responseCode);
                // Ler a resposta de erro, se houver
                // try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) { ... }
                return null;
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}


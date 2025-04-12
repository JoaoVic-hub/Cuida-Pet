package com.clinica.controller;

import com.clinica.DAO.ClienteDAO;
import com.clinica.DTO.EnderecoViaCepDTO;
import com.clinica.adapter.EnderecoAdapter;
import com.clinica.model.Cliente;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ClienteController {
    private ClienteDAO clienteDAO;
    private Gson gson = new Gson();

    public ClienteController() {
        this.clienteDAO = new ClienteDAO();
    }

    /**
     * Adiciona um cliente (SEM usar CEP) recebendo também a senha.
     * Agora, os parâmetros seguem a ordem: (nome, endereco, email, telefone, cpf, senha)
     * E chamamos o construtor do Cliente nessa mesma ordem.
     */
    public void adicionarCliente(String nome, 
                                 String endereco, 
                                 String email,
                                 String telefone, 
                                 String cpf, 
                                 String senha) 
    {
        // Construtor no Cliente: (nome, endereco, email, telefone, cpf, senha)
        Cliente cliente = new Cliente(
            nome,      // 1
            endereco,  // 2
            email,     // 3
            telefone,  // 4
            cpf,       // 5
            senha      // 6
        );
        clienteDAO.inserir(cliente);
        System.out.println("Cliente cadastrado com sucesso!");
    }

    /**
     * Adiciona um cliente com CEP integrado ao ViaCEP, e também define a senha.
     * Caso o CEP seja válido, o endereço virá do ViaCEP. Caso contrário, use 'endereco' nulo ou vazio.
     * Parâmetros: (nome, cep, email, telefone, cpf, senha).
     */
    public void adicionarClienteComCep(String nome, 
                                       String cep, 
                                       String email,
                                       String telefone, 
                                       String cpf, 
                                       String senha) 
    {
        try {
            String endereco = null;
            if (cep != null && !cep.trim().isEmpty()) {
                EnderecoViaCepDTO dto = buscarEnderecoPorCep(cep);
                if (dto != null) {
                    // Ex.: "Rua X, Bairro Y, Cidade-Z"
                    endereco = EnderecoAdapter.fromViaCepDTO(dto);
                }
            }
            // Construtor: (nome, endereco, email, telefone, cpf, senha)
            Cliente cliente = new Cliente(
                nome,           // 1
                endereco,       // 2 (pode ser nulo se CEP falhar)
                email,          // 3
                telefone,       // 4
                cpf,            // 5
                senha           // 6
            );
            clienteDAO.inserir(cliente);
            System.out.println("Cliente cadastrado com sucesso (via CEP)!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao cadastrar cliente com CEP.");
        }
    }

    /**
     * Atualiza um cliente (por ID), inclusive a senha.
     * Parâmetros: (id, nome, endereco, email, telefone, cpf, senha).
     * Também chama o construtor na mesma ordem.
     */
    public void atualizarCliente(int id, 
                                 String nome, 
                                 String endereco, 
                                 String email,
                                 String telefone, 
                                 String cpf, 
                                 String senha) 
    {
        // Construtor: (nome, endereco, email, telefone, cpf, senha)
        Cliente cliente = new Cliente(
            nome,       // 1
            endereco,   // 2
            email,      // 3
            telefone,   // 4
            cpf,        // 5
            senha       // 6
        );
        // Define o ID (já existente)
        cliente.setId(id);
        clienteDAO.alterar(cliente);
        System.out.println("Cliente atualizado com sucesso!");
    }

    /**
     * Remove (exclui) um cliente do BD pelo ID.
     */
    public void removerCliente(int id) {
        clienteDAO.remover(id);
        System.out.println("Cliente removido com sucesso!");
    }

    /**
     * Retorna todos os clientes cadastrados.
     */
    public List<Cliente> listarTodosClientes() {
        return clienteDAO.listarTodos();
    }

    /**
     * Busca clientes por nome (usa LIKE no DAO).
     */
    public List<Cliente> buscarClientesPorNome(String nome) {
        return clienteDAO.pesquisarPorNome(nome);
    }

    /**
     * Busca um cliente específico pelo ID.
     */
    public Cliente buscarClientePorId(int id) {
        return clienteDAO.exibir(id);
    }

    // =========================== Métodos Auxiliares =========================== //

    /**
     * Método privado que consome a API do ViaCEP e retorna um DTO com os dados do endereço.
     */
    private EnderecoViaCepDTO buscarEnderecoPorCep(String cep) {
        try {
            String urlString = "https://viacep.com.br/ws/" + cep + "/json/";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                System.out.println("Erro ao acessar o ViaCEP: HTTP " + conn.getResponseCode());
                return null;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            EnderecoViaCepDTO dto = gson.fromJson(br, EnderecoViaCepDTO.class);
            conn.disconnect();
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

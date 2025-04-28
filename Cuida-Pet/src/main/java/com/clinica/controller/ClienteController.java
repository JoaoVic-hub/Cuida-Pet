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

   
    public void adicionarCliente(String nome, 
                                 String endereco, 
                                 String email,
                                 String telefone, 
                                 String cpf, 
                                 String senha) 
    {
       
        Cliente cliente = new Cliente(nome, endereco, email, telefone, cpf, senha);
        clienteDAO.inserir(cliente);
        System.out.println("Cliente cadastrado com sucesso!");
    }

   
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
                   
                    endereco = EnderecoAdapter.fromViaCepDTO(dto);
                }
            }
          
            Cliente cliente = new Cliente(nome, endereco, email, telefone, cpf, senha);
            clienteDAO.inserir(cliente);
            System.out.println("Cliente cadastrado com sucesso (via CEP)!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao cadastrar cliente com CEP.");
        }
    }

   
    public void atualizarCliente(int id, 
                                 String nome, 
                                 String endereco, 
                                 String email,
                                 String telefone, 
                                 String cpf, 
                                 String senha) 
    {
        
        Cliente cliente = new Cliente(nome, endereco, email, telefone, cpf, senha);
        cliente.setId(id);
        clienteDAO.alterar(cliente);
        System.out.println("Cliente atualizado com sucesso!");
    }

 
    public void removerCliente(int id) {
        clienteDAO.remover(id);
        System.out.println("Cliente removido com sucesso!");
    }

 
    public List<Cliente> listarTodosClientes() {
        return clienteDAO.listarTodos();
    }

    public Cliente buscarClientePorNome(String nome) {
        List<Cliente> clientes = clienteDAO.pesquisarPorNome(nome);
        if (clientes != null && !clientes.isEmpty()) {
            return clientes.get(0);
        }
        return null;
    }

    public List<Cliente> buscarClientesPorNome(String nome) {
        return clienteDAO.pesquisarPorNome(nome);
    }
    

    
    public Cliente buscarClientePorId(int id) {
        return clienteDAO.exibir(id);
    }

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

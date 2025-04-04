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

    // Método já existente para adicionar cliente (sem integração com ViaCEP)
    public void adicionarCliente(String nome, String endereco, String email, String telefone, String cpf) {
        Cliente cliente = new Cliente(nome, endereco, email, telefone, cpf);
        clienteDAO.inserir(cliente);
        System.out.println("Cliente cadastrado com sucesso!");
    }
    
    // Novo método: adiciona cliente utilizando o CEP para preencher o endereço via ViaCEP
    public void adicionarClienteComCep(String nome, String cep, String email, String telefone, String cpf) {
        try {
            String endereco = null;
            if (cep != null && !cep.trim().isEmpty()) {
                EnderecoViaCepDTO dto = buscarEnderecoPorCep(cep);
                if (dto != null) {
                    // Usa o adapter para converter o DTO em uma String formatada
                    endereco = EnderecoAdapter.fromViaCepDTO(dto);
                }
            }
            Cliente cliente = new Cliente(nome, endereco, email, telefone, cpf);
            clienteDAO.inserir(cliente);
            System.out.println("Cliente cadastrado com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao cadastrar cliente com CEP.");
        }
    }

    public void atualizarCliente(int id, String nome, String endereco, String email, String telefone, String cpf) {
        Cliente cliente = new Cliente(nome, endereco, email, telefone, cpf);
        cliente.setId(id);
        clienteDAO.alterar(cliente);
        System.out.println("Cliente atualizado com sucesso!");
    }

    public List<Cliente> buscarClientesPorNome(String nome) {
        return clienteDAO.pesquisarPorNome(nome);
    }

    public void removerCliente(int id) {
        clienteDAO.remover(id);
        System.out.println("Cliente removido com sucesso!");
    }

    public List<Cliente> listarTodosClientes() {
        return clienteDAO.listarTodos();
    }

    public Cliente buscarClientePorId(int id) {
        return clienteDAO.exibir(id);
    }

    // Método privado que consome a API do ViaCEP e retorna um DTO com os dados do endereço
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

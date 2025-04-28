package com.clinica.controller;

import com.clinica.DAO.AnimalDAO;
import com.clinica.DAO.ClienteDAO;
import com.clinica.DAO.ConsultaDAO;
import com.clinica.DAO.VeterinarioDAO;
import com.clinica.model.Consulta;
import java.util.List;

public class ConsultaController {
    private ConsultaDAO consultaDAO;

    public ConsultaController() {
         ClienteDAO clienteDAO = new ClienteDAO();
        AnimalDAO animalDAO = new AnimalDAO();
        VeterinarioDAO veterinarioDAO = new VeterinarioDAO();
        this.consultaDAO = new ConsultaDAO(clienteDAO,animalDAO,veterinarioDAO);
    }


    public void adicionarConsulta(Consulta consulta) {
        consultaDAO.inserir(consulta);
        System.out.println("Consulta cadastrada com sucesso!");
    }

 
    public void atualizarConsulta(int id, Consulta consultaAtualizada) {
        consultaAtualizada.setId(id);
        consultaDAO.alterar(consultaAtualizada);
        System.out.println("Consulta atualizada com sucesso!");
    }

    
    public void removerConsulta(int id) {
        consultaDAO.remover(id);
        System.out.println("Consulta removida com sucesso!");
    }

    public List<Consulta> listarTodasConsultas() {
        return consultaDAO.listarTodos();
    }

    public Consulta buscarConsultaPorId(int id) {
        return consultaDAO.exibir(id);
    }
}

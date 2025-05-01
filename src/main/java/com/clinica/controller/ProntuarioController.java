package com.clinica.controller; 

import com.clinica.DAO.ProntuarioDAO;
import com.clinica.DAO.ConsultaDAO; 
import com.clinica.DAO.ClienteDAO;
import com.clinica.DAO.AnimalDAO;  
import com.clinica.DAO.VeterinarioDAO;
import com.clinica.model.Prontuario;
import com.clinica.model.Consulta;

import java.util.List;
import java.util.Objects;

public class ProntuarioController {

    private final ProntuarioDAO prontuarioDAO;
    private final ConsultaDAO consultaDAO;

    public ProntuarioController() {
        // Instanciação em cadeia das dependências
        ClienteDAO clienteDAO = new ClienteDAO();
        AnimalDAO animalDAO = new AnimalDAO();
        VeterinarioDAO veterinarioDAO = new VeterinarioDAO();
        this.consultaDAO = new ConsultaDAO(clienteDAO, animalDAO, veterinarioDAO);
        this.prontuarioDAO = new ProntuarioDAO(this.consultaDAO);
    }

    public ProntuarioController(ProntuarioDAO prontuarioDAO, ConsultaDAO consultaDAO) {
         Objects.requireNonNull(prontuarioDAO, "ProntuarioDAO não pode ser nulo");
         Objects.requireNonNull(consultaDAO, "ConsultaDAO não pode ser nulo");
        this.prontuarioDAO = prontuarioDAO;
        this.consultaDAO = consultaDAO;
    }


    public boolean adicionarProntuario(Prontuario prontuario) {
        if (prontuario == null || prontuario.getConsulta() == null || prontuario.getConsulta().getId() <= 0) {
            System.err.println("Controller: Tentativa de adicionar prontuário com consulta inválida.");
            return false;
        }
        Consulta consultaExistente = consultaDAO.exibir(prontuario.getConsulta().getId());
        if (consultaExistente == null) {
             System.err.println("Controller: Consulta ID " + prontuario.getConsulta().getId() + " não encontrada para o prontuário.");
             return false;
        }

        try {
            prontuarioDAO.inserir(prontuario);
            System.out.println("Controller: Prontuário adicionado para consulta ID " + prontuario.getConsulta().getId());
            return true;
        } catch (Exception e) {
             System.err.println("Controller: Erro ao adicionar prontuário: " + e.getMessage());
             e.printStackTrace();
             return false;
        }
    }

    public boolean atualizarProntuario(Prontuario prontuario) {
        if (prontuario == null || prontuario.getId() <= 0 || prontuario.getConsulta() == null || prontuario.getConsulta().getId() <= 0) {
             System.err.println("Controller: Tentativa de atualizar prontuário com dados inválidos.");
            return false;
        }
        Consulta consultaExistente = consultaDAO.exibir(prontuario.getConsulta().getId());
        if (consultaExistente == null) {
             System.err.println("Controller: Consulta ID " + prontuario.getConsulta().getId() + " não encontrada para atualizar prontuário.");
             return false;
        }

        try {
             prontuarioDAO.alterar(prontuario);
             System.out.println("Controller: Prontuário ID " + prontuario.getId() + " atualizado.");
             return true;
        } catch (Exception e) {
             System.err.println("Controller: Erro ao atualizar prontuário ID " + prontuario.getId() + ": " + e.getMessage());
             e.printStackTrace();
             return false;
        }
    }

    public boolean removerProntuario(int id) {
         if (id <= 0) return false;
         try {
              prontuarioDAO.remover(id);
              System.out.println("Controller: Prontuário ID " + id + " removido.");
              return true;
         } catch (Exception e) {
              System.err.println("Controller: Erro ao remover prontuário ID " + id + ": " + e.getMessage());
              e.printStackTrace();
              return false;
         }
    }

    public List<Prontuario> listarTodosProntuarios() {
        return prontuarioDAO.listarTodos();
    }

    public List<Prontuario> listarProntuariosPorConsulta(int consultaId) {
        if (consultaId <= 0) {
            return new ArrayList<>(); // Retorna lista vazia se ID inválido
        }
        return prontuarioDAO.listarPorConsulta(consultaId);
    }

    public Prontuario buscarProntuarioPorId(int id) {
         if (id <= 0) return null;
        return prontuarioDAO.exibir(id);
    }
}
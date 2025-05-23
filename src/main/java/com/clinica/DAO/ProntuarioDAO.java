package com.clinica.DAO;

import com.clinica.model.Prontuario; 
import com.clinica.model.Consulta;   
import com.clinica.persistence.JsonPersistenceHelper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProntuarioDAO {

    private final JsonPersistenceHelper<Prontuario> persistenceHelper;
    private List<Prontuario> prontuarios;

    private final ConsultaDAO consultaDAO;

    public ProntuarioDAO(ConsultaDAO consultaDAO) {
        this.persistenceHelper = new JsonPersistenceHelper<>("prontuarios.json", new TypeReference<List<Prontuario>>() {});
        this.prontuarios = persistenceHelper.readAll();
        this.consultaDAO = consultaDAO;
    }

    private void saveData() {
        persistenceHelper.writeAll(prontuarios);
    }

    // Carrega o objeto Consulta completo
    private void carregarRelacionamentos(Prontuario prontuario) {
         if (prontuario == null) return;

         if (prontuario.getConsulta() != null && prontuario.getConsulta().getId() > 0) {
              Consulta consultaCompleta = consultaDAO.exibir(prontuario.getConsulta().getId());
              prontuario.setConsulta(consultaCompleta);
         } else {
             prontuario.setConsulta(null); // Define como null se ID inválido ou objeto nulo
         }
    }

    // Prepara para salvar no JSON (apenas com ID da consulta)
     private Prontuario prepararParaSalvar(Prontuario prontuarioOriginal) {
        Prontuario prontuarioParaSalvar = new Prontuario();
        prontuarioParaSalvar.setId(prontuarioOriginal.getId());
        prontuarioParaSalvar.setObservacoes(prontuarioOriginal.getObservacoes());
        prontuarioParaSalvar.setDiagnostico(prontuarioOriginal.getDiagnostico());
        prontuarioParaSalvar.setDataRegistro(prontuarioOriginal.getDataRegistro());

        if (prontuarioOriginal.getConsulta() != null && prontuarioOriginal.getConsulta().getId() > 0) {
            Consulta consultaRef = new Consulta();
            consultaRef.setId(prontuarioOriginal.getConsulta().getId());
            prontuarioParaSalvar.setConsulta(consultaRef); // Salva só o ID
        }
        return prontuarioParaSalvar;
     }


    public void inserir(Prontuario prontuario) {
        // Validação: Verifica se a consulta associada existe
        if (prontuario.getConsulta() == null || consultaDAO.exibir(prontuario.getConsulta().getId()) == null) {
            System.err.println("Erro ao inserir prontuário: Consulta inválida ou não encontrada.");
            return;
        }

        int nextId = persistenceHelper.getNextId(prontuarios);
        prontuario.setId(nextId); // Define ID no objeto original

        Prontuario prontuarioParaSalvar = prepararParaSalvar(prontuario);

        prontuarios.add(prontuarioParaSalvar);
        saveData();
    }

    public void alterar(Prontuario prontuarioAtualizado) {
          if (prontuarioAtualizado.getConsulta() == null || consultaDAO.exibir(prontuarioAtualizado.getConsulta().getId()) == null) {
            System.err.println("Erro ao alterar prontuário: Consulta inválida ou não encontrada.");
            return;
        }

        Optional<Prontuario> prontuarioExistenteOpt = prontuarios.stream()
                .filter(p -> p.getId() == prontuarioAtualizado.getId())
                .findFirst();

         if (prontuarioExistenteOpt.isPresent()) {
             Prontuario prontuarioParaSalvar = prepararParaSalvar(prontuarioAtualizado);
             prontuarios.removeIf(p -> p.getId() == prontuarioAtualizado.getId());
             prontuarios.add(prontuarioParaSalvar);
             saveData();
         } else {
             System.err.println("Tentativa de alterar prontuário inexistente com ID: " + prontuarioAtualizado.getId());
         }
    }

    public void remover(int id) {
        boolean removed = prontuarios.removeIf(p -> p.getId() == id);
        if (removed) {
            saveData();
        }
         if (!removed) {
             System.err.println("Tentativa de remover prontuário inexistente com ID: " + id);
        }
    }

    public List<Prontuario> listarTodos() {
        List<Prontuario> prontuariosCompletos = new ArrayList<>();
        for (Prontuario p : prontuarios) {
            Prontuario copia = criarCopiaProntuario(p); 
            carregarRelacionamentos(copia);
            prontuariosCompletos.add(copia);
        }
        return prontuariosCompletos;
    }

    public List<Prontuario> listarPorConsulta(int consultaId) {
        List<Prontuario> prontuariosFiltrados = new ArrayList<>();
         for (Prontuario p : prontuarios) {
              // Filtra pelo ID da consulta armazenado no JSON
             if (p.getConsulta() != null && p.getConsulta().getId() == consultaId) {
                 Prontuario copia = criarCopiaProntuario(p);
                 carregarRelacionamentos(copia); // Carrega a consulta completa
                 prontuariosFiltrados.add(copia);
             }
         }
         return prontuariosFiltrados;
    }


    public Prontuario exibir(int id) {
        Optional<Prontuario> prontuarioOpt = prontuarios.stream()
                .filter(p -> p.getId() == id)
                .findFirst();

         if (prontuarioOpt.isPresent()) {
            Prontuario copia = criarCopiaProntuario(prontuarioOpt.get());
            carregarRelacionamentos(copia);
            return copia;
        } else {
            return null;
        }
    }

    private Prontuario criarCopiaProntuario(Prontuario original) {
        Prontuario copia = new Prontuario();
        copia.setId(original.getId());
        copia.setObservacoes(original.getObservacoes());
        copia.setDiagnostico(original.getDiagnostico());
        copia.setDataRegistro(original.getDataRegistro());
        copia.setConsulta(original.getConsulta());
        return copia;
    }
}
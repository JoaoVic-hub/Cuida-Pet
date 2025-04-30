package com.clinica.DAO;

import com.clinica.model.Agenda; // Modelo específico para a visão da agenda
import com.clinica.model.Animal;
import com.clinica.model.Cliente;
import com.clinica.model.Consulta;
import com.clinica.model.Veterinario;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AgendaDAO {

    // --- Dependência para buscar as consultas ---
    private final ConsultaDAO consultaDAO;
    // -------------------------------------------

    // Recebe ConsultaDAO no construtor
    public AgendaDAO(ConsultaDAO consultaDAO) {
        // Não precisa mais de conexão ou helper próprio, pois depende dos dados de ConsultaDAO
        this.consultaDAO = consultaDAO;
    }

    // Mapeia um objeto Consulta (com relacionamentos já carregados) para um objeto Agenda
    // Dentro de AgendaDAO.java
private Agenda mapConsultaToAgenda(Consulta consulta) {
    // Verifica dados essenciais primeiro (Cliente e Vet)
    if (consulta == null || consulta.getCliente() == null || consulta.getVeterinario() == null) {
        System.err.println("Não foi possível mapear consulta para agenda. Dados essenciais (Cliente/Veterinário) incompletos na consulta ID: " + (consulta != null ? consulta.getId() : "null"));
        return null; // Retorna null se dados essenciais faltarem
    }

    // Verifica se o animal existe (informativo se faltar, mas não impede o mapeamento se cliente/vet OK)
    if (consulta.getAnimal() == null) {
         System.out.println("Aviso: Consulta ID " + consulta.getId() + " não possui animal associado ou animal não pôde ser carregado.");
    }


    Agenda agenda = new Agenda();
    agenda.setConsultaId(consulta.getId());
    agenda.setDataHora(consulta.getDataHora());
    agenda.setStatus(consulta.getStatus());

    Veterinario vet = consulta.getVeterinario();
    agenda.setVeterinarioId(vet.getId());
    agenda.setNomeVeterinario(vet.getNome());

    Cliente cliente = consulta.getCliente();
    agenda.setClienteId(cliente.getId());
    agenda.setNomeCliente(cliente.getNome());
    agenda.setEnderecoCliente(cliente.getEndereco());

    // Trata o caso do Animal ser nulo
    Animal animal = consulta.getAnimal();
    if (animal != null) {
        agenda.setAnimalId(animal.getId());
        agenda.setNomeAnimal(animal.getNome());
    } else {
        // Define valores padrão ou indicativos se o animal for nulo
        agenda.setAnimalId(0); // Ou -1, ou deixe nulo se o tipo permitir e a view souber tratar
        agenda.setNomeAnimal("- Sem Animal -"); // Ou string vazia ""
    }

    return agenda;
}

    // Lista a agenda completa (baseado em todas as consultas)
    public List<Agenda> listarTodos() {
        // Busca todas as consultas. O ConsultaDAO.listarTodos() já deve
        // retornar as consultas com Cliente, Animal e Veterinario carregados.
        List<Consulta> todasConsultas = consultaDAO.listarTodos();

        // Mapeia cada consulta para um item da agenda usando Stream API
        return todasConsultas.stream()
                             .map(this::mapConsultaToAgenda) // Mapeia Consulta -> Agenda
                             .filter(Objects::nonNull)       // Remove quaisquer resultados nulos do mapeamento
                             .collect(Collectors.toList());  // Coleta em uma nova lista
    }

    // Lista a agenda de um veterinário específico
    public List<Agenda> listarPorVeterinario(int veterinarioId) {
        List<Consulta> todasConsultas = consultaDAO.listarTodos();

        // Filtra as consultas pelo ID do veterinário e depois mapeia para Agenda
        return todasConsultas.stream()
                             .filter(c -> c.getVeterinario() != null && c.getVeterinario().getId() == veterinarioId) // Filtra pelo Vet ID
                             .map(this::mapConsultaToAgenda)    // Mapeia as consultas filtradas
                             .filter(Objects::nonNull)          // Remove nulos
                             .collect(Collectors.toList());     // Coleta na lista final
    }
}
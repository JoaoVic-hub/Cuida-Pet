package com.clinica.DAO;

import com.clinica.model.Agenda; 
import com.clinica.model.Animal;
import com.clinica.model.Cliente;
import com.clinica.model.Consulta;
import com.clinica.model.Veterinario;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AgendaDAO {

    private final ConsultaDAO consultaDAO;

    public AgendaDAO(ConsultaDAO consultaDAO) {
        this.consultaDAO = consultaDAO;
    }

private Agenda mapConsultaToAgenda(Consulta consulta) {
    if (consulta == null || consulta.getCliente() == null || consulta.getVeterinario() == null) {
        System.err.println("Não foi possível mapear consulta para agenda. Dados essenciais (Cliente/Veterinário) incompletos na consulta ID: " + (consulta != null ? consulta.getId() : "null"));
        return null; 
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
        agenda.setAnimalId(0);
        agenda.setNomeAnimal("- Sem Animal -");
    }

    return agenda;
}
    // Lista a agenda completa (baseado em todas as consultas)
    public List<Agenda> listarTodos() {
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
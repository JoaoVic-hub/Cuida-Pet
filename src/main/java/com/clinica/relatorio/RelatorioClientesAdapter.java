package com.clinica.relatorio;

import com.clinica.controller.ClienteController;
import com.clinica.model.Cliente;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RelatorioClientesAdapter implements IRelatorio {

    private ClienteController controller;

    public RelatorioClientesAdapter(ClienteController controller) {
        this.controller = controller;
    }

    @Override
    public String gerarRelatorio() {
        List<Cliente> clientes = controller.listarTodosClientes();
        int totalClientes = clientes.size();

        long clientesComEmail = clientes.stream()
                .filter(c -> c.getEmail() != null && !c.getEmail().isEmpty())
                .count();

        long clientesComTelefone = clientes.stream()
                .filter(c -> c.getTelefone() != null && !c.getTelefone().isEmpty())
                .count();

        String dataRelatorio = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());

        StringBuilder relatorio = new StringBuilder();
        relatorio.append("📋 RELATÓRIO DE CLIENTES - CLÍNICA VETERINÁRIA\n")
                .append("Data: ").append(dataRelatorio).append("\n\n")
                .append("📊 RESUMO ESTATÍSTICO:\n")
                .append("----------------------------------------\n")
                .append("• Total de clientes cadastrados: ").append(totalClientes).append("\n")
                .append("• Clientes com e-mail cadastrado: ").append(clientesComEmail)
                .append(" (").append(totalClientes > 0 ? (clientesComEmail * 100 / totalClientes) : 0).append("%)\n")
                .append("• Clientes com telefone cadastrado: ").append(clientesComTelefone)
                .append(" (").append(totalClientes > 0 ? (clientesComTelefone * 100 / totalClientes) : 0).append("%)\n\n")
                .append("📍 DISTRIBUIÇÃO POR REGIÃO (exemplo):\n")
                .append("----------------------------------------\n")
                .append("• Zona Norte: ").append(contarClientesPorRegiao(clientes, "norte")).append("\n")
                .append("• Zona Sul: ").append(contarClientesPorRegiao(clientes, "sul")).append("\n")
                .append("• Zona Leste: ").append(contarClientesPorRegiao(clientes, "leste")).append("\n")
                .append("• Zona Oeste: ").append(contarClientesPorRegiao(clientes, "oeste")).append("\n")
                .append("• Centro: ").append(contarClientesPorRegiao(clientes, "centro")).append("\n\n")
                .append("🔄 ÚLTIMA ATUALIZAÇÃO:\n")
                .append("----------------------------------------\n")
                .append("Relatório gerado automaticamente pelo sistema.\n");

        return relatorio.toString();
    }

    private int contarClientesPorRegiao(List<Cliente> clientes, String regiao) {
        return (int) clientes.stream()
                .filter(c -> c.getEndereco() != null && c.getEndereco().toLowerCase().contains(regiao))
                .count();
    }
}
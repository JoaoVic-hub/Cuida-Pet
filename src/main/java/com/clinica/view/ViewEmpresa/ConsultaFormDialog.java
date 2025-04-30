package com.clinica.view.ViewEmpresa; // Pacote correto

import com.clinica.facade.ClinicaFacade; // Importar a Facade
import com.clinica.model.Animal;
import com.clinica.model.Cliente;
import com.clinica.model.Consulta;
import com.clinica.model.Veterinario;
import com.clinica.view.AnimalComboItem;
import com.clinica.view.ViewEmpresa.VeterinarioComboItem;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ConsultaFormDialog extends JDialog {

    private JFormattedTextField campoDataHora;
    private JComboBox<String> comboStatus;
    private JTextField campoClienteNome;
    private JButton btnBuscarCliente;
    private JLabel lblClienteInfo;
    private Cliente clienteSelecionado = null;
    private JComboBox<AnimalComboItem> comboAnimais;
    private JComboBox<VeterinarioComboItem> comboVeterinario;
    private boolean salvo = false;
    private Consulta consulta; // Para edição

    // --- USAR A FACADE ---
    private ClinicaFacade facade = ClinicaFacade.getInstance();
    // ---------------------

    // REMOVER Controllers individuais
    // private ClienteController clienteController = new ClienteController();
    // private AnimalController animalController = new AnimalController();
    // private VeterinarioController veterinarioController = new VeterinarioController();
    // private ConsultaController consultaController = new ConsultaController();

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ConsultaFormDialog(Frame parent, Consulta consultaExistente) {
         super(parent, "Formulário de Consulta", true); // Título mais genérico
         this.consulta = consultaExistente;
         // setTitle(consultaExistente == null ? "Adicionar Consulta" : "Editar Consulta"); // Definido no super
         setDefaultCloseOperation(DISPOSE_ON_CLOSE);
         setLocationRelativeTo(parent);

        // ... (Configuração da UI: panelPrincipal, panelForm, gbc - sem mudanças) ...
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));
        getContentPane().add(panelPrincipal);

        // Painel do Formulário com GridBagLayout para mais controle
        JPanel panelForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Espaçamento
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // --- Linha 0: Data/Hora ---
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; // Label
        panelForm.add(new JLabel("Data/Hora (dd/MM/yyyy HH:mm):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; // Campo
        try {
            javax.swing.text.MaskFormatter mask = new javax.swing.text.MaskFormatter("##/##/#### ##:##");
            campoDataHora = new JFormattedTextField(mask);
        } catch (java.text.ParseException ex) {
            campoDataHora = new JFormattedTextField(dateTimeFormatter.toFormat()); // Fallback
        }
        campoDataHora.setToolTipText("Formato: dd/MM/yyyy HH:mm");
        panelForm.add(campoDataHora, gbc);

         // --- Linha 1: Status ---
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panelForm.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        comboStatus = new JComboBox<>(new String[]{"Agendada", "Em Andamento", "Concluída", "Cancelada"}); // Ordem talvez mais lógica
        panelForm.add(comboStatus, gbc);

        // --- Linha 2: Cliente ---
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panelForm.add(new JLabel("Cliente (Nome/ID):"), gbc);
        JPanel panelClienteBusca = new JPanel(new BorderLayout(5, 0));
        campoClienteNome = new JTextField(20);
        btnBuscarCliente = new JButton("Buscar");
        panelClienteBusca.add(campoClienteNome, BorderLayout.CENTER);
        panelClienteBusca.add(btnBuscarCliente, BorderLayout.EAST);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panelForm.add(panelClienteBusca, gbc);

         // --- Linha 3: Info Cliente Encontrado ---
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth=2; // Ocupa as duas colunas
        lblClienteInfo = new JLabel("Digite o nome ou ID do cliente e clique em Buscar.");
        lblClienteInfo.setFont(lblClienteInfo.getFont().deriveFont(Font.ITALIC));
        lblClienteInfo.setForeground(Color.GRAY);
        panelForm.add(lblClienteInfo, gbc);
        gbc.gridwidth=1; // Reseta gridwidth

         // --- Linha 4: Animal ---
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        panelForm.add(new JLabel("Animal:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        comboAnimais = new JComboBox<>();
        comboAnimais.setEnabled(false); // Desabilitado até selecionar cliente
        panelForm.add(comboAnimais, gbc);

         // --- Linha 5: Veterinário ---
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        panelForm.add(new JLabel("Veterinário:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        comboVeterinario = new JComboBox<>();
        panelForm.add(comboVeterinario, gbc);

        panelPrincipal.add(panelForm, BorderLayout.CENTER);

        // --- Botões Salvar/Cancelar ---
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");
        panelBotoes.add(btnCancelar); // Ordem comum: Cancelar, Salvar
        panelBotoes.add(btnSalvar);
        panelPrincipal.add(panelBotoes, BorderLayout.SOUTH);


        // --- Ações e Lógica (AJUSTAR CHAMADAS) ---

        // Carrega Veterinários usando a Facade
        carregarVeterinariosCombo();

        // Ação do Botão Buscar Cliente (usa a Facade)
        btnBuscarCliente.addActionListener(e -> buscarEConfirmarCliente());

        // Preenche campos se for edição (usa a Facade para buscar dados se necessário)
        if (consultaExistente != null) {
            preencherFormulario(consultaExistente);
        } else {
            // Define data/hora atual como padrão ao adicionar
            campoDataHora.setValue(dateTimeFormatter.format(LocalDateTime.now()));
        }

        btnSalvar.addActionListener(e -> salvar()); // Método salvar usará a facade
        btnCancelar.addActionListener(e -> dispose());

        pack();
    }

    // AJUSTADO: Busca cliente usando a Facade
    private void buscarEConfirmarCliente() {
        String busca = campoClienteNome.getText().trim();
        if (busca.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite o nome ou ID do cliente.", "Busca Inválida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Cliente clienteEncontrado = null;
        try {
            int id = Integer.parseInt(busca);
            // USA A FACADE
            clienteEncontrado = facade.buscarClientePorId(id);
        } catch (NumberFormatException nfe) {
            // USA A FACADE
            List<Cliente> clientes = facade.buscarClientesPorNome(busca);
             if (clientes != null && !clientes.isEmpty()) {
                 if (clientes.size() == 1) {
                     clienteEncontrado = clientes.get(0);
                 } else {
                      // Poderia implementar um JList para seleção aqui
                     JOptionPane.showMessageDialog(this, clientes.size() + " clientes encontrados com nome similar.\nUse o ID único para selecionar.", "Múltiplos Resultados", JOptionPane.INFORMATION_MESSAGE);
                     return;
                 }
             }
        } catch (Exception ex) {
             JOptionPane.showMessageDialog(this, "Erro ao buscar cliente: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
             ex.printStackTrace();
             return;
        }


        if (clienteEncontrado != null) {
            this.clienteSelecionado = clienteEncontrado;
            lblClienteInfo.setText("☑ Cliente: " + clienteEncontrado.getId() + " - " + clienteEncontrado.getNome());
            lblClienteInfo.setForeground(new Color(0, 128, 0)); // Verde escuro
            carregarAnimaisCombo(clienteEncontrado.getId()); // Usa Facade internamente
            comboAnimais.setEnabled(true);
        } else {
            this.clienteSelecionado = null;
            lblClienteInfo.setText("☒ Cliente não encontrado.");
            lblClienteInfo.setForeground(Color.RED);
            comboAnimais.removeAllItems();
            comboAnimais.setEnabled(false);
        }
    }

    // AJUSTADO: Preenche formulário, usando Facade se precisar completar dados
    private void preencherFormulario(Consulta c) {
        campoDataHora.setValue(c.getDataHora() != null ? dateTimeFormatter.format(c.getDataHora()) : "");
        comboStatus.setSelectedItem(c.getStatus() != null ? c.getStatus() : "Agendada");

        // Preenche Cliente e Animal
        if (c.getCliente() != null && c.getCliente().getId() > 0) {
            // Tenta pegar o cliente completo da consulta, se não vier, busca via Facade
            Cliente cli = c.getCliente();
            if (cli.getNome() == null) { // Verifica se o nome veio (indicador de objeto completo)
                 cli = facade.buscarClientePorId(cli.getId());
            }

            if (cli != null) {
                this.clienteSelecionado = cli;
                campoClienteNome.setText(String.valueOf(cli.getId())); // Preenche ID para fácil busca/confirmação
                lblClienteInfo.setText("☑ Cliente: " + cli.getId() + " - " + cli.getNome());
                lblClienteInfo.setForeground(new Color(0, 128, 0));
                carregarAnimaisCombo(cli.getId()); // Carrega animais
                comboAnimais.setEnabled(true);

                // Seleciona o Animal (se existir na consulta)
                 if (c.getAnimal() != null && c.getAnimal().getId() > 0) {
                    selecionarAnimalNoCombo(c.getAnimal().getId());
                } else {
                    comboAnimais.setSelectedIndex(0); // Seleciona "- Nenhum/Não Aplicável -"
                }

            } else {
                 // Cliente ID existe na consulta mas não foi encontrado no DAO?
                 lblClienteInfo.setText("☒ Cliente ID " + c.getCliente().getId() + " não encontrado.");
                 lblClienteInfo.setForeground(Color.RED);
                 comboAnimais.setEnabled(false);
            }
        } else {
             lblClienteInfo.setText("Nenhum cliente associado.");
             lblClienteInfo.setForeground(Color.GRAY);
             comboAnimais.setEnabled(false);
        }

        // Seleciona o Veterinário
        if (c.getVeterinario() != null && c.getVeterinario().getId() > 0) {
            selecionarVeterinarioNoCombo(c.getVeterinario().getId());
        }
    }

    // AJUSTADO: Carrega veterinários usando a Facade
    private void carregarVeterinariosCombo() {
        comboVeterinario.removeAllItems();
        try {
            // USA A FACADE
            List<Veterinario> vets = facade.listarTodosVeterinarios();
            if (vets != null) {
                 for (Veterinario v : vets) {
                    comboVeterinario.addItem(new VeterinarioComboItem(v.getId(), v.getNome()));
                }
            }
        } catch (Exception e) {
             System.err.println("Erro ao carregar veterinários: " + e.getMessage());
             e.printStackTrace();
             JOptionPane.showMessageDialog(this, "Erro ao carregar lista de veterinários.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método selecionarVeterinarioNoCombo não muda

     // AJUSTADO: Carrega animais usando a Facade
    private void carregarAnimaisCombo(int clienteId) {
        comboAnimais.removeAllItems();
        // Adiciona opção padrão primeiro
        comboAnimais.addItem(new AnimalComboItem(0, "- Nenhum/Não Aplicável -", ""));
        try {
            // USA A FACADE
            List<Animal> animais = facade.listarAnimaisPorCliente(clienteId);
            if (animais != null) {
                for (Animal a : animais) {
                    // Usa formato ID - Nome (Espécie)
                    comboAnimais.addItem(new AnimalComboItem(a.getId(), a.getNome(), a.getEspecie()));
                }
            }
        } catch (Exception e) {
             System.err.println("Erro ao carregar animais do cliente: " + e.getMessage());
             e.printStackTrace();
             JOptionPane.showMessageDialog(this, "Erro ao carregar lista de animais do cliente.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
         // Garante que o "- Nenhum -" seja selecionado se não houver outros animais
         if (comboAnimais.getItemCount() <= 1) {
             comboAnimais.setSelectedIndex(0);
         }
    }

    // Método selecionarAnimalNoCombo não muda

    // AJUSTADO: Monta objeto Consulta (semelhante, mas validações podem ser centralizadas na Facade/Controller no futuro)
    private Consulta obterConsultaDoForm() throws Exception {
        LocalDateTime dataHora;
        try {
            String dtStr = (String) campoDataHora.getValue();
            if (dtStr == null || dtStr.trim().replace("/", "").replace(":", "").replace(" ", "").isEmpty()) {
                 throw new Exception("Data/Hora não pode estar vazia.");
            }
             dataHora = LocalDateTime.parse(dtStr, dateTimeFormatter);
        } catch (DateTimeParseException | ClassCastException | NullPointerException e) {
             throw new Exception("Formato inválido de Data/Hora. Use dd/MM/yyyy HH:mm.");
        }

        String status = (String) comboStatus.getSelectedItem();
        if (status == null || status.isEmpty()) {
            throw new Exception("Selecione um status para a consulta.");
        }

        if (clienteSelecionado == null) {
             throw new Exception("Nenhum cliente válido foi selecionado. Use o botão Buscar.");
        }
        // Cria referência ao cliente (só ID é necessário para salvar no DAO)
        Cliente clienteRef = new Cliente();
        clienteRef.setId(clienteSelecionado.getId());

        VeterinarioComboItem vetItem = (VeterinarioComboItem) comboVeterinario.getSelectedItem();
        if (vetItem == null) {
            throw new Exception("Nenhum veterinário selecionado.");
        }
        // Cria referência ao veterinário
        Veterinario vetRef = new Veterinario();
        vetRef.setId(vetItem.getId());

        // Pega Animal selecionado (pode ser nulo se a opção default foi escolhida)
        Animal animalRef = null;
        AnimalComboItem animalItem = (AnimalComboItem) comboAnimais.getSelectedItem();
        if (animalItem != null && animalItem.getId() > 0) { // ID > 0 significa que não é a opção default
            animalRef = new Animal();
            animalRef.setId(animalItem.getId());
        }

        // Cria o objeto Consulta
        Consulta novaConsulta = new Consulta();
        novaConsulta.setDataHora(dataHora);
        novaConsulta.setStatus(status);
        novaConsulta.setCliente(clienteRef);
        novaConsulta.setVeterinario(vetRef);
        novaConsulta.setAnimal(animalRef); // Define o animal (pode ser null)

        // Se for edição, mantém o ID original
        if (this.consulta != null) {
            novaConsulta.setId(this.consulta.getId());
        }

        return novaConsulta;
    }


    // AJUSTADO: Salva usando a Facade
    private void salvar() {
        try {
            Consulta consultaParaSalvar = obterConsultaDoForm();

            if (this.consulta == null) { // Adicionando
                // USA A FACADE
                facade.adicionarConsulta(consultaParaSalvar);
                JOptionPane.showMessageDialog(this, "Consulta adicionada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else { // Editando
                // USA A FACADE (passando o ID e o objeto atualizado)
                facade.atualizarConsulta(consultaParaSalvar.getId(), consultaParaSalvar);
                JOptionPane.showMessageDialog(this, "Consulta atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            salvo = true;
            dispose();

        } catch (Exception ex) {
             JOptionPane.showMessageDialog(this, "Erro ao salvar consulta:\n" + ex.getMessage(), "Erro de Validação", JOptionPane.ERROR_MESSAGE);
             ex.printStackTrace();
        }
    }

    // Método foiSalvo não muda
     public boolean foiSalvo() {
        return salvo;
    }

     // Métodos auxiliares selecionarVeterinarioNoCombo e selecionarAnimalNoCombo
     private void selecionarVeterinarioNoCombo(int vetId) {
        for (int i = 0; i < comboVeterinario.getItemCount(); i++) {
            VeterinarioComboItem item = comboVeterinario.getItemAt(i);
            if (item != null && item.getId() == vetId) {
                comboVeterinario.setSelectedIndex(i);
                return; // Encontrou, pode sair
            }
        }
         System.err.println("Aviso: Veterinário ID " + vetId + " não encontrado no ComboBox.");
    }

     private void selecionarAnimalNoCombo(int animalId) {
         if (!comboAnimais.isEnabled() || comboAnimais.getItemCount() == 0) {
              System.err.println("Aviso: ComboBox de animais desabilitado ou vazio ao tentar selecionar ID " + animalId);
              return; // Não adianta procurar se está desabilitado ou vazio
         }
        for (int i = 0; i < comboAnimais.getItemCount(); i++) {
            AnimalComboItem item = comboAnimais.getItemAt(i);
            // Verifica se item não é nulo e se o ID bate
            if (item != null && item.getId() == animalId) {
                comboAnimais.setSelectedIndex(i);
                return; // Encontrou
            }
        }
         // Se chegou aqui, não encontrou o animal específico
         System.err.println("Aviso: Animal ID " + animalId + " não encontrado no ComboBox.");
         // Seleciona a opção default "- Nenhum -" para evitar estado inconsistente
         comboAnimais.setSelectedIndex(0);
    }


} // Fim da classe ConsultaFormDialog
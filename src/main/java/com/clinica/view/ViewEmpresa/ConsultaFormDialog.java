package com.clinica.view.ViewEmpresa;

import com.clinica.facade.ClinicaFacade;
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
    private Cliente clienteSelecionado = null; // Guarda o objeto Cliente completo após busca
    private JComboBox<AnimalComboItem> comboAnimais;
    private JComboBox<VeterinarioComboItem> comboVeterinario;
    private boolean salvo = false;
    private Consulta consulta; // Mantém a referência para caso de edição

    // Usa a Facade para interações com a lógica de negócios
    private ClinicaFacade facade = ClinicaFacade.getInstance();

    // Formatter para Data e Hora
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Construtor do diálogo de Consulta.
     * @param parent O Frame pai do diálogo.
     * @param consultaExistente A consulta a ser editada (null se for para adicionar).
     */
    public ConsultaFormDialog(Frame parent, Consulta consultaExistente) {
         super(parent, "Formulário de Consulta", true); // Modal
         this.consulta = consultaExistente;
         setDefaultCloseOperation(DISPOSE_ON_CLOSE);
         setLocationRelativeTo(parent); // Centraliza na tela

         // --- Configuração da Interface Gráfica (UI) ---
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));
        getContentPane().add(panelPrincipal);

        JPanel panelForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Linha 0: Data/Hora
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; // Label
        panelForm.add(new JLabel("Data/Hora (dd/MM/yyyy HH:mm):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; // Campo
        try {
            // Máscara para facilitar a digitação
            javax.swing.text.MaskFormatter mask = new javax.swing.text.MaskFormatter("##/##/#### ##:##");
            mask.setPlaceholderCharacter('_');
            campoDataHora = new JFormattedTextField(mask);
        } catch (java.text.ParseException ex) {
            // Fallback se a máscara falhar
            campoDataHora = new JFormattedTextField(dateTimeFormatter.toFormat());
        }
        campoDataHora.setToolTipText("Formato: dd/MM/yyyy HH:mm");
        panelForm.add(campoDataHora, gbc);

        // Linha 1: Status
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panelForm.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        comboStatus = new JComboBox<>(new String[]{"Agendada", "Em Andamento", "Concluída", "Cancelada"});
        panelForm.add(comboStatus, gbc);

        // Linha 2: Busca Cliente
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panelForm.add(new JLabel("Cliente (Nome/ID):"), gbc);
        JPanel panelClienteBusca = new JPanel(new BorderLayout(5, 0));
        campoClienteNome = new JTextField(20);
        btnBuscarCliente = new JButton("Buscar");
        panelClienteBusca.add(campoClienteNome, BorderLayout.CENTER);
        panelClienteBusca.add(btnBuscarCliente, BorderLayout.EAST);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panelForm.add(panelClienteBusca, gbc);

        // Linha 3: Informação Cliente Encontrado
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; // Ocupa duas colunas
        lblClienteInfo = new JLabel("Digite o nome ou ID do cliente e clique em Buscar.");
        lblClienteInfo.setFont(lblClienteInfo.getFont().deriveFont(Font.ITALIC));
        lblClienteInfo.setForeground(Color.GRAY);
        panelForm.add(lblClienteInfo, gbc);
        gbc.gridwidth = 1; // Reseta para uma coluna

        // Linha 4: Animal
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        panelForm.add(new JLabel("Animal:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        comboAnimais = new JComboBox<>();
        comboAnimais.setEnabled(false); // Inicia desabilitado
        panelForm.add(comboAnimais, gbc);

        // Linha 5: Veterinário
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
        panelBotoes.add(btnCancelar);
        panelBotoes.add(btnSalvar);
        panelPrincipal.add(panelBotoes, BorderLayout.SOUTH);

        // --- Ações e Lógica Inicial ---
        carregarVeterinariosCombo(); // Carrega a lista de veterinários no combo

        btnBuscarCliente.addActionListener(e -> buscarEConfirmarCliente()); // Define ação do botão buscar

        // Preenche o formulário se estiver editando uma consulta existente
        if (consultaExistente != null) {
            preencherFormulario(consultaExistente);
        } else {
            // Define a data/hora atual como padrão ao adicionar uma nova consulta
            campoDataHora.setValue(dateTimeFormatter.format(LocalDateTime.now()));
        }

        // Define ações dos botões Salvar e Cancelar
        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose()); // Fecha o diálogo

        pack(); // Ajusta o tamanho do diálogo
    }

    /**
     * Busca o cliente pelo nome ou ID digitado e atualiza a UI.
     */
    private void buscarEConfirmarCliente() {
        String busca = campoClienteNome.getText().trim();
        if (busca.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite o nome ou ID do cliente.", "Busca Inválida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Cliente clienteEncontrado = null;
        try {
            // Tenta buscar por ID primeiro
            int id = Integer.parseInt(busca);
            clienteEncontrado = facade.buscarClientePorId(id);
        } catch (NumberFormatException nfe) {
            // Se não for número, busca por nome
            List<Cliente> clientes = facade.buscarClientesPorNome(busca);
             if (clientes != null && !clientes.isEmpty()) {
                 if (clientes.size() == 1) {
                     // Encontrou exatamente um cliente pelo nome
                     clienteEncontrado = clientes.get(0);
                 } else {
                     // Encontrou múltiplos clientes, pede ID
                     JOptionPane.showMessageDialog(this, clientes.size() + " clientes encontrados com nome similar.\nUse o ID único para selecionar.", "Múltiplos Resultados", JOptionPane.INFORMATION_MESSAGE);
                     limparSelecaoCliente(); // Limpa seleção anterior
                     return; // Interrompe
                 }
             }
        } catch (Exception ex) {
             // Erro durante a busca na facade
             JOptionPane.showMessageDialog(this, "Erro ao buscar cliente: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
             ex.printStackTrace();
             limparSelecaoCliente(); // Limpa seleção anterior
             return; // Interrompe
        }

        // Atualiza a UI com base no resultado da busca
        if (clienteEncontrado != null) {
            selecionarCliente(clienteEncontrado);
        } else {
            limparSelecaoCliente();
            lblClienteInfo.setText("☒ Cliente não encontrado.");
            lblClienteInfo.setForeground(Color.RED);
        }
    }

    /**
     * Atualiza a UI quando um cliente válido é selecionado.
     * @param cliente O cliente que foi encontrado e selecionado.
     */
    private void selecionarCliente(Cliente cliente) {
        this.clienteSelecionado = cliente;
        lblClienteInfo.setText("☑ Cliente: " + cliente.getId() + " - " + cliente.getNome());
        lblClienteInfo.setForeground(new Color(0, 128, 0)); // Verde escuro
        carregarAnimaisCombo(cliente.getId()); // Carrega animais deste cliente
        comboAnimais.setEnabled(true); // Habilita o combo de animais
    }

    /**
     * Limpa a seleção de cliente e reseta a UI relacionada.
     */
    private void limparSelecaoCliente() {
        this.clienteSelecionado = null;
        lblClienteInfo.setText("Digite o nome ou ID do cliente e clique em Buscar.");
        lblClienteInfo.setForeground(Color.GRAY);
        comboAnimais.removeAllItems(); // Limpa combo de animais
        comboAnimais.setEnabled(false); // Desabilita combo de animais
    }


    /**
     * Preenche os campos do formulário com os dados de uma consulta existente (para edição).
     * @param c A consulta existente.
     */
    private void preencherFormulario(Consulta c) {
        campoDataHora.setValue(c.getDataHora() != null ? dateTimeFormatter.format(c.getDataHora()) : "");
        comboStatus.setSelectedItem(c.getStatus() != null ? c.getStatus() : "Agendada"); // Usa status da consulta ou padrão

        // Preenche Cliente
        if (c.getCliente() != null && c.getCliente().getId() > 0) {
            Cliente cli = facade.buscarClientePorId(c.getCliente().getId()); // Busca cliente completo
            if (cli != null) {
                selecionarCliente(cli); // Atualiza UI do cliente
                campoClienteNome.setText(String.valueOf(cli.getId())); // Mostra ID no campo de busca

                // Seleciona o Animal correto no combo
                 if (c.getAnimal() != null && c.getAnimal().getId() > 0) {
                    selecionarAnimalNoCombo(c.getAnimal().getId());
                } else if (comboAnimais.getItemCount() > 0) {
                     // Se não tinha animal, mas o combo tem itens, seleciona o default "- Nenhum..."
                    comboAnimais.setSelectedIndex(0);
                }
            } else {
                 // Caso o cliente da consulta não exista mais
                 limparSelecaoCliente();
                 lblClienteInfo.setText("☒ Cliente ID " + c.getCliente().getId() + " (da consulta original) não encontrado.");
                 lblClienteInfo.setForeground(Color.RED);
            }
        } else {
            // Consulta não tinha cliente associado
            limparSelecaoCliente();
            lblClienteInfo.setText("Consulta original sem cliente associado.");
        }

        // Seleciona o Veterinário correto no combo
        if (c.getVeterinario() != null && c.getVeterinario().getId() > 0) {
            selecionarVeterinarioNoCombo(c.getVeterinario().getId());
        }
    }

    /**
     * Carrega a lista de veterinários no ComboBox.
     */
    private void carregarVeterinariosCombo() {
        comboVeterinario.removeAllItems(); // Limpa itens antigos
        try {
            List<Veterinario> vets = facade.listarTodosVeterinarios();
            if (vets != null && !vets.isEmpty()) {
                 for (Veterinario v : vets) {
                    comboVeterinario.addItem(new VeterinarioComboItem(v.getId(), v.getNome()));
                }
            } else {
                 // comboVeterinario.addItem(new VeterinarioComboItem(-1, "Nenhum veterinário cadastrado"));
                 // comboVeterinario.setEnabled(false);
            }
        } catch (Exception e) {
             System.err.println("Erro ao carregar veterinários no ComboBox: " + e.getMessage());
             e.printStackTrace();
             // comboVeterinario.addItem(new VeterinarioComboItem(-1, "Erro ao carregar"));
             JOptionPane.showMessageDialog(this, "Erro ao carregar lista de veterinários.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Carrega a lista de animais do cliente selecionado no ComboBox.
     * @param clienteId O ID do cliente cujos animais devem ser carregados.
     */
    private void carregarAnimaisCombo(int clienteId) {
        comboAnimais.removeAllItems(); // Limpa itens antigos
        // Adiciona a opção padrão "- Nenhum/Não Aplicável -" primeiro
        comboAnimais.addItem(new AnimalComboItem(0, "- Nenhum/Não Aplicável -", ""));
        try {
            List<Animal> animais = facade.listarAnimaisPorCliente(clienteId);
            if (animais != null) {
                for (Animal a : animais) {
                    // Adiciona cada animal ao combo
                    comboAnimais.addItem(new AnimalComboItem(a.getId(), a.getNome(), a.getEspecie()));
                }
            }
            // Seleciona a opção padrão se for o único item
            if (comboAnimais.getItemCount() <= 1) {
                 comboAnimais.setSelectedIndex(0);
            }
        } catch (Exception e) {
             System.err.println("Erro ao carregar animais do cliente no ComboBox: " + e.getMessage());
             e.printStackTrace();
              // Poderia adicionar item de erro
             // comboAnimais.addItem(new AnimalComboItem(-1, "Erro ao carregar", ""));
             JOptionPane.showMessageDialog(this, "Erro ao carregar lista de animais do cliente.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Constrói um objeto Consulta a partir dos dados do formulário usando o Builder.
     * @return O objeto Consulta preenchido.
     * @throws Exception Se algum dado obrigatório estiver faltando ou inválido.
     */
    private Consulta obterConsultaDoForm() throws Exception {
        // 1. Obter e Validar Data/Hora
        LocalDateTime dataHora;
        try {
            String dtStr = (String) campoDataHora.getValue(); // Obtém valor do campo formatado
            // Verifica se está vazio ou contém apenas a máscara
            if (dtStr == null || dtStr.trim().isEmpty() || dtStr.contains("_")) {
                 throw new Exception("Data/Hora não pode estar vazia ou incompleta.");
            }
             dataHora = LocalDateTime.parse(dtStr, dateTimeFormatter);
        } catch (DateTimeParseException | ClassCastException | NullPointerException e) {
             // Captura erro de parsing ou se o valor não for String
             throw new Exception("Formato inválido de Data/Hora. Use dd/MM/yyyy HH:mm.");
        }

        // 2. Obter Status
        String status = (String) comboStatus.getSelectedItem();
        if (status == null || status.isEmpty()) {
            throw new Exception("Selecione um status para a consulta.");
        }

        // 3. Obter Cliente (já validado e armazenado em clienteSelecionado)
        if (clienteSelecionado == null) {
             throw new Exception("Nenhum cliente válido foi selecionado. Use o botão Buscar.");
        }
        // Cria uma referência apenas com o ID para o Builder (o DAO/Facade usará isso)
        Cliente clienteRef = new Cliente();
        clienteRef.setId(clienteSelecionado.getId());

        // 4. Obter Veterinário
        VeterinarioComboItem vetItem = (VeterinarioComboItem) comboVeterinario.getSelectedItem();
        if (vetItem == null || vetItem.getId() <= 0) { // Verifica se é um item válido
            throw new Exception("Nenhum veterinário válido selecionado.");
        }
        // Cria referência apenas com ID
        Veterinario vetRef = new Veterinario();
        vetRef.setId(vetItem.getId());

        // 5. Obter Animal (Opcional)
        Animal animalRef = null;
        AnimalComboItem animalItem = (AnimalComboItem) comboAnimais.getSelectedItem();
        // Verifica se um animal real foi selecionado (ID > 0)
        if (animalItem != null && animalItem.getId() > 0) {
            animalRef = new Animal();
            animalRef.setId(animalItem.getId());
        }

        // 6. Usa o Builder para criar a Consulta
        Consulta.Builder builder = new Consulta.Builder()
                .dataHora(dataHora)
                .status(status)
                .cliente(clienteRef) // Passa a referência do cliente
                .veterinario(vetRef); // Passa a referência do veterinário

        if (animalRef != null) { // Adiciona o animal ao builder apenas se um foi selecionado
            builder.animal(animalRef);
        }

        Consulta novaConsulta = builder.build(); // Chama o build()

        // 7. Mantém o ID original se for uma edição
        if (this.consulta != null) {
            novaConsulta.setId(this.consulta.getId());
        }

        return novaConsulta; // Retorna o objeto construído
    }


    /**
     * Tenta salvar a consulta (adicionar ou atualizar) chamando a Facade.
     */
    private void salvar() {
        try {
            // Obtém a consulta preenchida usando o método com Builder
            Consulta consultaParaSalvar = obterConsultaDoForm();

            // Verifica se é uma nova consulta ou edição
            if (this.consulta == null) { // Adicionando nova consulta
                facade.adicionarConsulta(consultaParaSalvar);
                JOptionPane.showMessageDialog(this, "Consulta adicionada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else { // Editando consulta existente
                // O ID já foi setado em obterConsultaDoForm se this.consulta não for null
                facade.atualizarConsulta(consultaParaSalvar.getId(), consultaParaSalvar);
                JOptionPane.showMessageDialog(this, "Consulta atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            salvo = true; // Marca que salvou com sucesso
            dispose(); // Fecha o diálogo

        } catch (IllegalStateException ex) { // Captura erro do builder.build()
             JOptionPane.showMessageDialog(this, "Erro ao construir consulta:\n" + ex.getMessage(), "Erro Interno", JOptionPane.ERROR_MESSAGE);
             ex.printStackTrace();
        } catch (Exception ex) { // Captura outros erros (validação, facade, etc.)
             JOptionPane.showMessageDialog(this, "Erro ao salvar consulta:\n" + ex.getMessage(), "Erro de Validação ou Operação", JOptionPane.ERROR_MESSAGE);
             ex.printStackTrace();
        }
    }

    /**
     * Verifica se o formulário foi salvo com sucesso.
     * @return true se foi salvo, false caso contrário.
     */
     public boolean foiSalvo() {
        return salvo;
    }

     /**
      * Seleciona um veterinário no ComboBox pelo seu ID.
      * @param vetId O ID do veterinário a ser selecionado.
      */
     private void selecionarVeterinarioNoCombo(int vetId) {
        for (int i = 0; i < comboVeterinario.getItemCount(); i++) {
            VeterinarioComboItem item = comboVeterinario.getItemAt(i);
            if (item != null && item.getId() == vetId) {
                comboVeterinario.setSelectedIndex(i);
                return; // Sai após encontrar e selecionar
            }
        }
         // Se não encontrou, loga um aviso (não mostra popup para não interromper fluxo)
         System.err.println("Aviso (ConsultaFormDialog): Veterinário ID " + vetId + " não encontrado no ComboBox ao preencher formulário.");
    }

     /**
      * Seleciona um animal no ComboBox pelo seu ID.
      * @param animalId O ID do animal a ser selecionado.
      */
     private void selecionarAnimalNoCombo(int animalId) {
         // Só tenta selecionar se o combo estiver habilitado e tiver itens
         if (!comboAnimais.isEnabled() || comboAnimais.getItemCount() == 0) {
              System.err.println("Aviso (ConsultaFormDialog): ComboBox de animais desabilitado ou vazio ao tentar selecionar ID " + animalId);
              return;
         }
        for (int i = 0; i < comboAnimais.getItemCount(); i++) {
            AnimalComboItem item = comboAnimais.getItemAt(i);
            if (item != null && item.getId() == animalId) {
                comboAnimais.setSelectedIndex(i);
                return; // Sai após encontrar e selecionar
            }
        }
         // Se não encontrou o animal específico, seleciona o item default ("- Nenhum...")
         System.err.println("Aviso (ConsultaFormDialog): Animal ID " + animalId + " não encontrado no ComboBox. Selecionando default.");
         comboAnimais.setSelectedIndex(0); // Garante que o item default seja selecionado
    }

}
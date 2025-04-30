package com.clinica.view.ViewEmpresa;

import com.clinica.controller.AnimalController; // Importar
import com.clinica.controller.ClienteController;
import com.clinica.controller.ConsultaController;
import com.clinica.controller.VeterinarioController;
import com.clinica.model.Animal;
import com.clinica.model.Cliente; // Importar
import com.clinica.model.Consulta;
import com.clinica.model.Veterinario;
import com.clinica.view.AnimalComboItem; // Assume que existe
import java.awt.*; // Importar
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException; // Para busca de cliente
import java.util.Date;  // Para busca de cliente
import java.util.List;
import javax.swing.*; // Para conversão Date <-> LocalDateTime
import javax.swing.border.EmptyBorder;

public class ConsultaFormDialog extends JDialog {

    private JFormattedTextField campoDataHora;
    private JComboBox<String> comboStatus;
    // Campo Cliente: Alterado para permitir busca ou seleção
    private JTextField campoClienteNome; // Campo para digitar nome do cliente
    private JButton btnBuscarCliente;    // Botão para buscar/confirmar cliente
    private JLabel lblClienteInfo;       // Label para mostrar o cliente encontrado
    private Cliente clienteSelecionado = null; // Guarda o cliente encontrado/selecionado

    // --- NOVO ---
    private JComboBox<AnimalComboItem> comboAnimais;
    // -----------

    private JComboBox<VeterinarioComboItem> comboVeterinario;
    private boolean salvo = false;
    private Consulta consulta; // Para edição

    // --- Controllers ---
    private ClienteController clienteController = new ClienteController();
    private AnimalController animalController = new AnimalController();
    private VeterinarioController veterinarioController = new VeterinarioController();
    private ConsultaController consultaController = new ConsultaController();

    // -------------------

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ConsultaFormDialog(Frame parent, Consulta consultaExistente) { // Aceita Frame ou Dialog como pai
        super(parent, true);
        this.consulta = consultaExistente;
        setTitle(consultaExistente == null ? "Adicionar Consulta" : "Editar Consulta");
        // setSize(500, 400); // Ajuste o tamanho conforme necessário
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent); // Centraliza em relação ao pai

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
        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(new JLabel("Data/Hora (dd/MM/yyyy HH:mm):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; // Faz o campo ocupar espaço horizontal
        try {
            // Usar máscara melhora a experiência do usuário
            javax.swing.text.MaskFormatter mask = new javax.swing.text.MaskFormatter("##/##/#### ##:##");
            campoDataHora = new JFormattedTextField(mask);
        } catch (java.text.ParseException ex) {
            campoDataHora = new JFormattedTextField(dateTimeFormatter.toFormat()); // Fallback sem máscara
        }
        campoDataHora.setToolTipText("Formato: dd/MM/yyyy HH:mm");
        panelForm.add(campoDataHora, gbc);
        gbc.weightx = 0; // Reseta o peso

        // --- Linha 1: Status ---
        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        comboStatus = new JComboBox<>(new String[]{"Agendada", "Concluída", "Cancelada", "Em Andamento"}); // Adicione outros status se necessário
        panelForm.add(comboStatus, gbc);
        gbc.weightx = 0;

        // --- Linha 2: Cliente ---
        gbc.gridx = 0; gbc.gridy = 2;
        panelForm.add(new JLabel("Cliente (Nome/ID):"), gbc);
        // Painel para campo de nome e botão buscar
        JPanel panelClienteBusca = new JPanel(new BorderLayout(5, 0));
        campoClienteNome = new JTextField(20);
        btnBuscarCliente = new JButton("Buscar");
        panelClienteBusca.add(campoClienteNome, BorderLayout.CENTER);
        panelClienteBusca.add(btnBuscarCliente, BorderLayout.EAST);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panelForm.add(panelClienteBusca, gbc);
        gbc.weightx = 0;

        // --- Linha 3: Info Cliente Encontrado ---
        gbc.gridx = 1; gbc.gridy = 3;
        lblClienteInfo = new JLabel("Nenhum cliente selecionado.");
        lblClienteInfo.setFont(lblClienteInfo.getFont().deriveFont(Font.ITALIC));
        panelForm.add(lblClienteInfo, gbc);


        // --- Linha 4: Animal --- (NOVO)
        gbc.gridx = 0; gbc.gridy = 4;
        panelForm.add(new JLabel("Animal:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        comboAnimais = new JComboBox<>(); // Inicializa vazio
        comboAnimais.setEnabled(false); // Começa desabilitado até selecionar cliente
        panelForm.add(comboAnimais, gbc);
        gbc.weightx = 0;

        // --- Linha 5: Veterinário ---
        gbc.gridx = 0; gbc.gridy = 5;
        panelForm.add(new JLabel("Veterinário:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        comboVeterinario = new JComboBox<>();
        panelForm.add(comboVeterinario, gbc);
        gbc.weightx = 0;

        panelPrincipal.add(panelForm, BorderLayout.CENTER);

        // --- Botões Salvar/Cancelar ---
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");
        panelBotoes.add(btnSalvar);
        panelBotoes.add(btnCancelar);
        panelPrincipal.add(panelBotoes, BorderLayout.SOUTH);

        // --- Ações e Lógica ---

        // Carrega Veterinários
        carregarVeterinariosCombo();

        // Ação do Botão Buscar Cliente
        btnBuscarCliente.addActionListener(e -> buscarEConfirmarCliente());
        // Alternativa: buscar ao perder foco (pode ser menos intuitivo)
        // campoClienteNome.addFocusListener(new FocusAdapter() {
        //    @Override
        //    public void focusLost(FocusEvent e) {
        //        buscarEConfirmarCliente();
        //    }
        // });


        // Preenche campos se for edição
        if (consultaExistente != null) {
            preencherFormulario(consultaExistente);
        } else {
            // Define data/hora atual como padrão ao adicionar
            campoDataHora.setValue(dateTimeFormatter.format(LocalDateTime.now()));
        }

        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());

        pack(); // Ajusta o tamanho do diálogo aos componentes

    } // Fim do construtor

    // Busca cliente pelo nome/ID e atualiza a interface
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
            clienteEncontrado = clienteController.buscarClientePorId(id);
        } catch (NumberFormatException nfe) {
            // Se não for ID, busca por nome (pega o primeiro encontrado)
            List<Cliente> clientes = clienteController.buscarClientesPorNome(busca);
             if (clientes != null && !clientes.isEmpty()) {
                 if (clientes.size() == 1) {
                     clienteEncontrado = clientes.get(0);
                 } else {
                     // Se mais de um, pede para refinar busca ou selecionar (implementação futura)
                     JOptionPane.showMessageDialog(this, "Múltiplos clientes encontrados com este nome. Refine a busca ou use o ID.", "Múltiplos Resultados", JOptionPane.INFORMATION_MESSAGE);
                     return; // Impede de prosseguir por enquanto
                 }
             }
        }

        if (clienteEncontrado != null) {
            this.clienteSelecionado = clienteEncontrado;
            lblClienteInfo.setText("ID: " + clienteEncontrado.getId() + " - " + clienteEncontrado.getNome());
            lblClienteInfo.setForeground(Color.BLUE.darker());
            carregarAnimaisCombo(clienteEncontrado.getId()); // Carrega animais do cliente
            comboAnimais.setEnabled(true); // Habilita combo de animais
        } else {
            this.clienteSelecionado = null;
            lblClienteInfo.setText("Cliente não encontrado.");
            lblClienteInfo.setForeground(Color.RED);
            comboAnimais.removeAllItems();
            comboAnimais.setEnabled(false);
        }
    }


    // Preenche o formulário com dados de uma consulta existente
    private void preencherFormulario(Consulta c) {
        campoDataHora.setValue(c.getDataHora() != null ? dateTimeFormatter.format(c.getDataHora()) : "");
        comboStatus.setSelectedItem(c.getStatus() != null ? c.getStatus() : "Agendada");

        // Preenche Cliente
        if (c.getCliente() != null) {
            this.clienteSelecionado = c.getCliente(); // Assume que o objeto Consulta já tem o Cliente carregado
             if(this.clienteSelecionado.getNome() == null) { // Se DAO não carregou nome, busca
                 Cliente cliCompleto = clienteController.buscarClientePorId(clienteSelecionado.getId());
                 if (cliCompleto != null) this.clienteSelecionado = cliCompleto;
             }

            campoClienteNome.setText(String.valueOf(clienteSelecionado.getId())); // Preenche ID para fácil busca
            lblClienteInfo.setText("ID: " + clienteSelecionado.getId() + " - " + clienteSelecionado.getNome());
            lblClienteInfo.setForeground(Color.BLUE.darker());
            carregarAnimaisCombo(clienteSelecionado.getId()); // Carrega animais
            comboAnimais.setEnabled(true);

            // Seleciona o Animal no combo (se existir)
             if (c.getAnimal() != null && c.getAnimal().getId() > 0) {
                selecionarAnimalNoCombo(c.getAnimal().getId());
            }

        } else {
             lblClienteInfo.setText("Cliente não associado.");
             lblClienteInfo.setForeground(Color.RED);
             comboAnimais.setEnabled(false);
        }


        // Seleciona o Veterinário
        if (c.getVeterinario() != null && c.getVeterinario().getId() > 0) {
            selecionarVeterinarioNoCombo(c.getVeterinario().getId());
        }
    }

    // Carrega os veterinários no ComboBox
    private void carregarVeterinariosCombo() {
        comboVeterinario.removeAllItems();
        try {
            List<Veterinario> vets = veterinarioController.listarTodosVeterinarios();
            if (vets != null) {
                 for (Veterinario v : vets) {
                    // Usa VeterinarioComboItem para guardar ID e Nome
                    comboVeterinario.addItem(new VeterinarioComboItem(v.getId(), v.getNome()));
                }
            }
        } catch (Exception e) {
             System.err.println("Erro ao carregar veterinários: " + e.getMessage());
             e.printStackTrace();
             JOptionPane.showMessageDialog(this, "Erro ao carregar lista de veterinários.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Seleciona um veterinário específico no ComboBox pelo ID
    private void selecionarVeterinarioNoCombo(int vetId) {
        for (int i = 0; i < comboVeterinario.getItemCount(); i++) {
            VeterinarioComboItem item = comboVeterinario.getItemAt(i);
            if (item != null && item.getId() == vetId) {
                comboVeterinario.setSelectedIndex(i);
                break;
            }
        }
    }


    // --- NOVO: Carrega os animais do cliente no ComboBox ---
    private void carregarAnimaisCombo(int clienteId) {
        comboAnimais.removeAllItems();
        comboAnimais.addItem(new AnimalComboItem(0, "- Nenhum/Não Aplicável -", "")); // Opção default
        try {
            List<Animal> animais = animalController.listarAnimaisPorCliente(clienteId);
            if (animais != null) {
                for (Animal a : animais) {
                    comboAnimais.addItem(new AnimalComboItem(a.getId(), a.getNome(), a.getEspecie()));
                }
            }
        } catch (Exception e) {
             System.err.println("Erro ao carregar animais do cliente: " + e.getMessage());
             e.printStackTrace();
             JOptionPane.showMessageDialog(this, "Erro ao carregar lista de animais do cliente.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- NOVO: Seleciona um animal específico no ComboBox pelo ID ---
    private void selecionarAnimalNoCombo(int animalId) {
        for (int i = 0; i < comboAnimais.getItemCount(); i++) {
            AnimalComboItem item = comboAnimais.getItemAt(i);
            if (item != null && item.getId() == animalId) {
                comboAnimais.setSelectedIndex(i);
                break;
            }
        }
    }

    // Monta o objeto Consulta a partir dos dados do formulário
    private Consulta obterConsultaDoForm() throws Exception { // Lança exceção para erros de validação
        LocalDateTime dataHora;
        try {
            // Tenta fazer o parse da data/hora
            dataHora = LocalDateTime.parse((String) campoDataHora.getValue(), dateTimeFormatter);
        } catch (DateTimeParseException | ClassCastException e) {
             // Tenta fazer o parse se for Date (fallback do JFormattedTextField)
             try {
                  Object value = campoDataHora.getValue();
                  if (value instanceof Date) {
                       dataHora = ((Date) value).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                  } else {
                      throw new Exception("Formato inválido de Data/Hora. Use dd/MM/yyyy HH:mm.");
                  }
             } catch (Exception e2) {
                  throw new Exception("Formato inválido de Data/Hora. Use dd/MM/yyyy HH:mm.");
             }

        }

        String status = (String) comboStatus.getSelectedItem();
        if (status == null || status.isEmpty()) {
            throw new Exception("Selecione um status para a consulta.");
        }

        // Valida se um cliente foi selecionado
        if (clienteSelecionado == null) {
             throw new Exception("Nenhum cliente válido foi selecionado/encontrado.");
        }
        // Cria referência ao cliente selecionado
        Cliente clienteRef = new Cliente();
        clienteRef.setId(clienteSelecionado.getId());
        clienteRef.setNome(clienteSelecionado.getNome()); // Pode adicionar nome para uso imediato


        // Pega Veterinário selecionado
        VeterinarioComboItem vetItem = (VeterinarioComboItem) comboVeterinario.getSelectedItem();
        if (vetItem == null) {
            throw new Exception("Nenhum veterinário selecionado.");
        }
        Veterinario vetRef = new Veterinario();
        vetRef.setId(vetItem.getId());
        // Opcional: Buscar nome do vet aqui ou confiar que será carregado depois
        // Veterinario vetCompleto = veterinarioController.buscarVeterinarioPorId(vetItem.getId());
        // vetRef.setNome(vetCompleto != null ? vetCompleto.getNome() : "?");


        // --- NOVO: Pega Animal selecionado ---
        Animal animalRef = null; // Começa como nulo
        AnimalComboItem animalItem = (AnimalComboItem) comboAnimais.getSelectedItem();
        if (animalItem != null && animalItem.getId() > 0) { // ID > 0 significa que não é a opção default
            animalRef = new Animal();
            animalRef.setId(animalItem.getId());
             // Opcional: Buscar nome do animal aqui
             // Animal animalCompleto = animalController.buscarAnimalPorId(animalItem.getId());
             // animalRef.setNome(animalCompleto != null ? animalCompleto.getNome() : "?");
        }
        // -----------------------------------

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


    // Ação do botão Salvar
    private void salvar() {
        try {
            Consulta consultaParaSalvar = obterConsultaDoForm(); // Pega os dados validados
            // Não precisa mais instanciar o controller aqui, já é membro da classe
            if (this.consulta == null) { // Adicionando nova consulta
                consultaController.adicionarConsulta(consultaParaSalvar);
                JOptionPane.showMessageDialog(this, "Consulta adicionada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else { // Editando consulta existente
                consultaController.atualizarConsulta(consultaParaSalvar.getId(), consultaParaSalvar);
                JOptionPane.showMessageDialog(this, "Consulta atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            salvo = true; // Indica que salvou com sucesso
            dispose(); // Fecha o diálogo

        } catch (Exception ex) {
            // Exibe mensagens de erro de validação ou outras exceções
             JOptionPane.showMessageDialog(this, "Erro ao salvar consulta:\n" + ex.getMessage(), "Erro de Validação", JOptionPane.ERROR_MESSAGE);
             ex.printStackTrace(); // Ajuda na depuração
        }
    }


    // Retorna true se o formulário foi salvo com sucesso
    public boolean foiSalvo() {
        return salvo;
    }

    // Opcional: método para retornar a consulta salva (pode não ser necessário)
    // public Consulta getConsultaSalva() { ... }

} // Fim da classe ConsultaFormDialog
package com.clinica.view.ViewEmpresa; // Pacote da classe

// Imports necessários para a classe separada
import com.clinica.facade.ClinicaFacade;
import com.clinica.model.Animal;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.*;

/**
 * Janela de diálogo (JDialog) para adicionar ou editar informações de um Animal.
 * Interage com a ClinicaFacade para persistir os dados.
 */
public class AnimalDialog extends JDialog { // Removido 'private static'

    private JTextField txtNome, txtEspecie, txtRaca, txtNascimento;
    private boolean salvo = false;
    private Animal animalEditando; // Guarda o animal que está sendo editado (null se for adição)
    private int clienteId;         // ID do cliente dono do animal
    private ClinicaFacade facade;  // Referência à Facade para salvar/atualizar
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Construtor do diálogo de animais.
     * @param parent Janela pai (geralmente o frame principal ou outro diálogo).
     * @param animal O animal a ser editado (null se for para adicionar um novo).
     * @param clienteId O ID do cliente ao qual este animal pertence.
     * @param facade A instância da ClinicaFacade para operações de dados.
     */
    public AnimalDialog(Frame parent, Animal animal, int clienteId, ClinicaFacade facade) {
        // Chama o construtor da superclasse JDialog, definindo título e modalidade
        super(parent, (animal == null ? "Adicionar Animal" : "Editar Animal"), true); // true = modal
        this.animalEditando = animal;
        this.clienteId = clienteId;
        this.facade = facade; // Recebe a facade como parâmetro

        // --- Configuração do Layout e Componentes ---
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Margens internas
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Espaçamento entre componentes
        gbc.fill = GridBagConstraints.HORIZONTAL; // Faz componentes preencherem horizontalmente
        gbc.anchor = GridBagConstraints.WEST; // Alinha à esquerda

        // Linha 0: Nome
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; // Label (não estica)
        panel.add(new JLabel("Nome*:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; // Campo (estica)
        txtNome = new JTextField(animal != null ? animal.getNome() : ""); // Preenche se editando
        panel.add(txtNome, gbc);

        // Linha 1: Espécie
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("Espécie:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtEspecie = new JTextField(animal != null ? animal.getEspecie() : "");
        panel.add(txtEspecie, gbc);

        // Linha 2: Raça
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(new JLabel("Raça:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtRaca = new JTextField(animal != null ? animal.getRaca() : "");
        panel.add(txtRaca, gbc);

        // Linha 3: Nascimento
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panel.add(new JLabel("Nascimento (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtNascimento = new JTextField(animal != null && animal.getDataNascimento() != null ?
            animal.getDataNascimento().format(dateFormatter) : ""); // Formata data se existir
        panel.add(txtNascimento, gbc);

        // --- Botões ---
        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> salvarAnimal()); // Chama o método de salvar

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose()); // Simplesmente fecha o diálogo

        // Painel para alinhar botões à direita
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnSalvar);

        // Adiciona os painéis ao diálogo
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        pack(); // Ajusta o tamanho do diálogo aos componentes
        setLocationRelativeTo(parent); // Centraliza em relação à janela pai
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Garante que só o diálogo feche
    }

    /**
     * Valida os dados do formulário, cria/atualiza o objeto Animal
     * e chama a Facade para persistir os dados.
     */
    private void salvarAnimal() {
    String nome = txtNome.getText().trim();
    // Validação básica: Nome é obrigatório
    if (nome.isEmpty()) {
        JOptionPane.showMessageDialog(this, "O nome do animal é obrigatório.", "Campo Obrigatório", JOptionPane.WARNING_MESSAGE);
        txtNome.requestFocus(); // Coloca o foco no campo nome
        return; // Interrompe o salvamento
    }

    try {
        LocalDate nascimento = null;
        String nascStr = txtNascimento.getText().trim();
        // Tenta converter a data apenas se o campo não estiver vazio
         if (!nascStr.isEmpty()) {
            nascimento = LocalDate.parse(nascStr, dateFormatter);
         }

        // Cria um novo objeto Animal com os dados do formulário
        Animal animalParaSalvar = new Animal(
            nome, txtEspecie.getText().trim(), txtRaca.getText().trim(),
            nascimento, this.clienteId // Associa ao cliente correto
        );

        // REMOVE a variável 'boolean sucesso;'

        // Verifica se está editando (animalEditando não é null) ou adicionando
        if (animalEditando != null) {
            animalParaSalvar.setId(animalEditando.getId()); // Mantém o ID original
            // Chama a facade para atualizar. Se falhar, lançará exceção.
            facade.atualizarAnimalObj(animalParaSalvar);
            System.out.println("AnimalDialog: Chamada facade.atualizarAnimalObj realizada."); // Log
        } else {
            // Chama a facade para adicionar. Se falhar, lançará exceção.
            facade.adicionarAnimalObj(animalParaSalvar);
             System.out.println("AnimalDialog: Chamada facade.adicionarAnimalObj realizada."); // Log
        }

        // Se chegou aqui, a chamada à facade foi bem-sucedida (não lançou exceção)
        this.salvo = true; // Marca que foi salvo
        dispose(); // Fecha o diálogo

    } catch (DateTimeParseException ex) {
        // Erro específico se a data digitada não estiver no formato correto
        JOptionPane.showMessageDialog(this, "Formato de data inválido! Use dd/MM/yyyy.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        txtNascimento.requestFocus(); // Coloca o foco no campo de data

    // ***** ALTERAÇÃO AQUI *****
    // Remova "| IllegalArgumentException". Capturar RuntimeException é suficiente.
    } catch (RuntimeException ex) {
         // Captura exceções de tempo de execução lançadas pela Facade
         // (incluindo IllegalArgumentException, NullPointerException, etc.)
         JOptionPane.showMessageDialog(this, "Erro ao salvar o animal:\n" + ex.getMessage(), "Erro de Salvamento", JOptionPane.ERROR_MESSAGE);
         ex.printStackTrace(); // Imprime o erro no console para depuração

    } catch (Exception ex) {
         // Captura outros erros inesperados (checked exceptions que não sejam RuntimeException)
         JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado ao salvar:\n" + ex.getMessage(), "Erro Inesperado", JOptionPane.ERROR_MESSAGE);
         ex.printStackTrace(); // Imprime o erro no console para depuração
    }
} // Fim do método salvarAnimal

    /**
     * Método público para verificar se o diálogo foi fechado após salvar.
     * @return true se o botão Salvar foi clicado e a operação foi bem-sucedida, false caso contrário.
     */
    public boolean foiSalvo() {
        return salvo;
    }
}

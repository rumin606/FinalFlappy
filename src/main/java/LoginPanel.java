import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class LoginPanel extends JPanel implements KeyListener {

    private final JTextField nameField;
    private final JTextField emailField;
    private final JTextField phoneField;
    private final JLabel errorLabel;
    private final JLabel hintLabel;

    private String playerName = "";
    private String playerEmail = "";
    private String playerPhone = "";

    private final List<JTextField> fields = new ArrayList<>();
    private int focusedFieldIndex = 0;

    private final ActionListener onStart;

    private static final Color BG_COLOR = new Color(45, 52, 54);
    private static final Color ACCENT_COLOR = new Color(116, 185, 255);
    private static final Color FIELD_BG = new Color(99, 110, 114);
    private static final Color TEXT_COLOR = new Color(255, 255, 255);
    private static final Color ERROR_COLOR = new Color(255, 71, 87);
    private static final Color FOCUS_BORDER = new Color(253, 203, 110);

    public LoginPanel(int width, int height, ActionListener onStart) {
        this.onStart = onStart;
        setPreferredSize(new Dimension(width, height));
        setLayout(null);
        setBackground(BG_COLOR);
        setFocusable(true);
        addKeyListener(this);

        int centerX = width / 2;
        int startY = 100;

        JLabel titleLabel = new JLabel("FLAPPY BIRD", SwingConstants.CENTER);
        titleLabel.setBounds(0, 30, width, 50);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(ACCENT_COLOR);
        add(titleLabel);

        JLabel subtitleLabel = new JLabel("Player Registration", SwingConstants.CENTER);
        subtitleLabel.setBounds(0, 75, width, 30);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(178, 190, 195));
        add(subtitleLabel);

        int fieldWidth = 320;
        int fieldHeight = 38;
        int labelHeight = 20;
        int gap = 55;

        JLabel nameLabel = createLabel("Player Name", centerX - fieldWidth / 2, startY);
        add(nameLabel);
        nameField = createTextField(centerX - fieldWidth / 2, startY + labelHeight, fieldWidth, fieldHeight, 0);
        add(nameField);

        JLabel emailLabel = createLabel("Email Address", centerX - fieldWidth / 2, startY + gap);
        add(emailLabel);
        emailField = createTextField(centerX - fieldWidth / 2, startY + gap + labelHeight, fieldWidth, fieldHeight, 1);
        add(emailField);

        JLabel phoneLabel = createLabel("Phone Number", centerX - fieldWidth / 2, startY + gap * 2);
        add(phoneLabel);
        phoneField = createTextField(centerX - fieldWidth / 2, startY + gap * 2 + labelHeight, fieldWidth, fieldHeight, 2);
        add(phoneField);

        fields.add(nameField);
        fields.add(emailField);
        fields.add(phoneField);

        errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setBounds(0, startY + gap * 3 + labelHeight + 10, width, 25);
        errorLabel.setFont(new Font("Arial", Font.BOLD, 14));
        errorLabel.setForeground(ERROR_COLOR);
        add(errorLabel);

        hintLabel = new JLabel("Tab: next field  |  Shift+Tab: prev field  |  Enter: start game", SwingConstants.CENTER);
        hintLabel.setBounds(0, height - 60, width, 25);
        hintLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        hintLabel.setForeground(new Color(178, 190, 195));
        add(hintLabel);

        updateFieldBorders();
    }

    private JLabel createLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 320, 20);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(178, 190, 195));
        return label;
    }

    private JTextField createTextField(int x, int y, int w, int h, int index) {
        JTextField field = new JTextField();
        field.setBounds(x, y, w, h);
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setBackground(FIELD_BG);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                focusedFieldIndex = index;
                updateFieldBorders();
            }
        });
        return field;
    }

    private void updateFieldBorders() {
        for (int i = 0; i < fields.size(); i++) {
            if (i == focusedFieldIndex) {
                fields.get(i).setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(FOCUS_BORDER, 2),
                        BorderFactory.createEmptyBorder(4, 9, 4, 9)
                ));
            } else {
                fields.get(i).setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_TAB) {
            if (e.isShiftDown()) {
                focusedFieldIndex--;
                if (focusedFieldIndex < 0) focusedFieldIndex = fields.size() - 1;
            } else {
                focusedFieldIndex++;
                if (focusedFieldIndex >= fields.size()) focusedFieldIndex = 0;
            }
            fields.get(focusedFieldIndex).requestFocusInWindow();
            updateFieldBorders();
            e.consume();
        }

        if (key == KeyEvent.VK_ENTER) {
            submit();
            e.consume();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    private void submit() {
        if (validateAndSave()) {
            onStart.actionPerformed(null);
        } else {
            fields.get(focusedFieldIndex).requestFocusInWindow();
        }
    }

    private boolean validateAndSave() {
        playerName = nameField.getText().trim();
        playerEmail = emailField.getText().trim();
        playerPhone = phoneField.getText().trim();

        if (playerName.isEmpty()) {
            errorLabel.setText("Please enter your name");
            focusedFieldIndex = 0;
            fields.get(0).requestFocusInWindow();
            updateFieldBorders();
            return false;
        }
        if (playerEmail.isEmpty()) {
            errorLabel.setText("Please enter your email");
            focusedFieldIndex = 1;
            fields.get(1).requestFocusInWindow();
            updateFieldBorders();
            return false;
        }
        if (!playerEmail.contains("@") || !playerEmail.contains(".")) {
            errorLabel.setText("Please enter a valid email");
            focusedFieldIndex = 1;
            fields.get(1).requestFocusInWindow();
            updateFieldBorders();
            return false;
        }
        if (playerPhone.isEmpty()) {
            errorLabel.setText("Please enter your phone number");
            focusedFieldIndex = 2;
            fields.get(2).requestFocusInWindow();
            updateFieldBorders();
            return false;
        }
        if (!playerPhone.matches("[0-9+\\-\\s]{7,15}")) {
            errorLabel.setText("Please enter a valid phone number");
            focusedFieldIndex = 2;
            fields.get(2).requestFocusInWindow();
            updateFieldBorders();
            return false;
        }

        errorLabel.setText("");
        return true;
    }

    public String getPlayerName() { return playerName; }
    public String getPlayerEmail() { return playerEmail; }
    public String getPlayerPhone() { return playerPhone; }

    public void clearFields() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        errorLabel.setText("");
    }

    public void focusFirstField() {
        focusedFieldIndex = 0;
        fields.get(0).requestFocusInWindow();
        updateFieldBorders();
    }
}

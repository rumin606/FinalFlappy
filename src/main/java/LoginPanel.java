import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class LoginPanel extends JPanel implements KeyListener {

    private boolean isSignupMode = false;

    private final JTextField nameField;
    private final JTextField emailField;
    private final JTextField phoneField;
    private final JPasswordField passwordField;
    private final JLabel errorLabel;
    private final JLabel hintLabel;
    private final JLabel subtitleLabel;
    private final JButton loginNavButton;
    private final JButton signupNavButton;
    private final JLabel nameLabel;
    private final JLabel emailLabel;
    private final JLabel phoneLabel;
    private final JLabel passwordLabel;

    private String playerName = "";
    private String playerEmail = "";
    private String playerPhone = "";
    private String playerPassword = "";

    private final List<JTextField> loginFields = new ArrayList<>();
    private final List<JTextField> signupFields = new ArrayList<>();
    private List<JTextField> activeFields;
    private int focusedFieldIndex = 0;

    private final ActionListener onLogin;
    private final ActionListener onSignup;

    private Image backgroundImage;
    private JButton loginActionButton;
    private JButton signupActionButton;

    private String sessionPlayerName = "";
    private int sessionPlayerLevel = 1;

    private static final Color BG_COLOR = new Color(45, 52, 54);
    private static final Color ACCENT_COLOR = new Color(116, 185, 255);
    private static final Color FIELD_BG = new Color(45, 58, 74, 200);
    private static final Color TEXT_COLOR = new Color(255, 255, 255);
    private static final Color ERROR_COLOR = new Color(255, 99, 99);
    private static final Color FOCUS_BORDER = new Color(253, 203, 110);
    private static final Color NAV_ACTIVE = new Color(116, 185, 255);
    private static final Color NAV_INACTIVE = new Color(30, 40, 50, 200);
    private static final Color MUTED_TEXT = new Color(200, 214, 224);
    private static final Color LOGIN_BUTTON_COLOR = new Color(46, 134, 222);
    private static final Color SIGNUP_BUTTON_COLOR = new Color(22, 160, 133);

    public LoginPanel(int width, int height, ActionListener onLogin, ActionListener onSignup) {
        this.onLogin = onLogin;
        this.onSignup = onSignup;
        setPreferredSize(new Dimension(width, height));
        setLayout(null);
        setBackground(BG_COLOR);
        setFocusable(true);
        addKeyListener(this);

        try {
            backgroundImage = new ImageIcon(getClass().getResource("/images/bg4.jpg")).getImage();
        } catch (Exception ex) {
            backgroundImage = null;
        }

        int centerX = width / 2;
        int fieldWidth = 320;
        int fieldHeight = 38;
        int labelHeight = 20;

        JLabel titleLabel = new JLabel("FLAPPY BIRD", SwingConstants.CENTER);
        titleLabel.setBounds(0, 22, width, 48);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(ACCENT_COLOR);
        add(titleLabel);

        loginNavButton = createNavButton("LOGIN", centerX - 100, 90, 90, 35);
        signupNavButton = createNavButton("SIGNUP", centerX + 10, 90, 90, 35);
        add(loginNavButton);
        add(signupNavButton);

        subtitleLabel = new JLabel("Sign in to your account", SwingConstants.CENTER);
        subtitleLabel.setBounds(0, 135, width, 25);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        subtitleLabel.setForeground(MUTED_TEXT);
        add(subtitleLabel);

        int startY = 175;

        nameLabel = createLabel("Player Name", centerX - fieldWidth / 2, startY);
        add(nameLabel);
        nameField = createTextField(centerX - fieldWidth / 2, startY + labelHeight, fieldWidth, fieldHeight, 0);
        add(nameField);

        emailLabel = createLabel("Email Address", centerX - fieldWidth / 2, startY + 55);
        add(emailLabel);
        emailField = createTextField(centerX - fieldWidth / 2, startY + 55 + labelHeight, fieldWidth, fieldHeight, 1);
        add(emailField);

        phoneLabel = createLabel("Phone Number", centerX - fieldWidth / 2, startY + 110);
        add(phoneLabel);
        phoneField = createTextField(centerX - fieldWidth / 2, startY + 110 + labelHeight, fieldWidth, fieldHeight, 2);
        add(phoneField);

        passwordLabel = createLabel("Password", centerX - fieldWidth / 2, startY + 165);
        add(passwordLabel);
        passwordField = new JPasswordField();
        passwordField.setBounds(centerX - fieldWidth / 2, startY + 165 + labelHeight, fieldWidth, fieldHeight);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setBackground(FIELD_BG);
        passwordField.setForeground(TEXT_COLOR);
        passwordField.setCaretColor(TEXT_COLOR);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                focusedFieldIndex = activeFields.indexOf(passwordField);
                updateFieldBorders();
            }
        });
        add(passwordField);

        loginFields.add(emailField);
        loginFields.add(passwordField);

        signupFields.add(nameField);
        signupFields.add(emailField);
        signupFields.add(phoneField);
        signupFields.add(passwordField);

        errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setBounds(0, startY + 225, width, 25);
        errorLabel.setFont(new Font("Arial", Font.BOLD, 14));
        errorLabel.setForeground(ERROR_COLOR);
        add(errorLabel);

        int buttonY = startY + 252;
        loginActionButton = createActionButton("LOGIN", LOGIN_BUTTON_COLOR, centerX - 165, buttonY, 150, 44);
        signupActionButton = createActionButton("SIGNUP", SIGNUP_BUTTON_COLOR, centerX + 15, buttonY, 150, 44);
        add(loginActionButton);
        add(signupActionButton);

        hintLabel = new JLabel("Tab: next field  |  Enter/Space: submit  |  or click Login / Signup", SwingConstants.CENTER);
        hintLabel.setBounds(0, height - 60, width, 25);
        hintLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        hintLabel.setForeground(MUTED_TEXT);
        add(hintLabel);

        loginNavButton.addActionListener(e -> switchToLogin());
        signupNavButton.addActionListener(e -> switchToSignup());

        loginActionButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            if ("admin".equals(email) && "admin123".equals(password)) {
                playerEmail = email;
                playerPassword = password;
                errorLabel.setText("");
                onLogin.actionPerformed(null);
            } else if (validateLogin()) {
                onLogin.actionPerformed(null);
            } else {
                activeFields.get(0).requestFocusInWindow();
            }
        });
        signupActionButton.addActionListener(e -> {
            if (validateSignup()) {
                onSignup.actionPerformed(null);
            } else {
                activeFields.get(0).requestFocusInWindow();
            }
        });

        setMode(false);
    }

    private JButton createActionButton(String text, Color base, int x, int y, int w, int h) {
        JButton button = new JButton(text) {
            private boolean hover = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hover = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hover = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color top = hover ? base.brighter() : base;
                Color bottom = hover ? base.darker() : base.darker().darker();
                g2.setPaint(new GradientPaint(0, 0, top, 0, getHeight(), bottom));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
                g2.setColor(new Color(255, 255, 255, 70));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setBounds(x, y, w, h);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setForeground(TEXT_COLOR);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
            g2.setColor(new Color(10, 15, 25, 185));
            g2.fillRect(0, 0, getWidth(), getHeight());
        } else {
            g2.setColor(BG_COLOR);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        int centerX = getWidth() / 2;
        int cardX = centerX - 195;
        int cardY = 82;
        int cardW = 390;
        int cardH = 406;
        g2.setColor(new Color(12, 20, 32, 185));
        g2.fillRoundRect(cardX, cardY, cardW, cardH, 30, 30);
        g2.setColor(new Color(255, 255, 255, 45));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(cardX, cardY, cardW, cardH, 30, 30);

        g2.dispose();
    }

    private JButton createNavButton(String text, int x, int y, int w, int h) {
        JButton button = new JButton(text);
        button.setBounds(x, y, w, h);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void updateNavButtonStyle(JButton button, boolean active) {
        if (active) {
            button.setBackground(NAV_ACTIVE);
            button.setForeground(TEXT_COLOR);
        } else {
            button.setBackground(NAV_INACTIVE);
            button.setForeground(MUTED_TEXT);
        }
    }

    private JLabel createLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 320, 20);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(MUTED_TEXT);
        return label;
    }

    private JTextField createTextField(int x, int y, int w, int h, int index) {
        JTextField field = new JTextField();
        field.setBounds(x, y, w, h);
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setBackground(FIELD_BG);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                focusedFieldIndex = activeFields.indexOf(field);
                updateFieldBorders();
            }
        });
        return field;
    }

    private void setMode(boolean signup) {
        this.isSignupMode = signup;

        nameLabel.setVisible(signup);
        nameField.setVisible(signup);
        phoneLabel.setVisible(signup);
        phoneField.setVisible(signup);

        emailLabel.setVisible(true);
        emailField.setVisible(true);
        passwordLabel.setVisible(true);
        passwordField.setVisible(true);

        updateNavButtonStyle(loginNavButton, !signup);
        updateNavButtonStyle(signupNavButton, signup);

        subtitleLabel.setText(signup ? "Create a new account" : "Sign in to your account");

        activeFields = signup ? signupFields : loginFields;
        focusedFieldIndex = 0;
        activeFields.get(0).requestFocusInWindow();
        updateFieldBorders();
        errorLabel.setText("");
        repaint();
    }

    private void updateFieldBorders() {
        for (int i = 0; i < activeFields.size(); i++) {
            JTextField f = activeFields.get(i);
            if (i == focusedFieldIndex) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(FOCUS_BORDER, 2),
                        BorderFactory.createEmptyBorder(4, 9, 4, 9)
                ));
            } else {
                f.setBorder(BorderFactory.createCompoundBorder(
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
                if (focusedFieldIndex < 0) focusedFieldIndex = activeFields.size() - 1;
            } else {
                focusedFieldIndex++;
                if (focusedFieldIndex >= activeFields.size()) focusedFieldIndex = 0;
            }
            activeFields.get(focusedFieldIndex).requestFocusInWindow();
            updateFieldBorders();
            e.consume();
        }

        if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_SPACE) {
            submit();
            e.consume();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    private void submit() {
        boolean valid;
        if (isSignupMode) {
            valid = validateSignup();
        } else {
            valid = validateLogin();
        }
        if (valid) {
            if (isSignupMode) {
                onSignup.actionPerformed(null);
            } else {
                onLogin.actionPerformed(null);
            }
        } else {
            activeFields.get(focusedFieldIndex).requestFocusInWindow();
        }
    }

    private boolean validateLogin() {
        playerEmail = emailField.getText().trim();
        playerPassword = new String(passwordField.getPassword()).trim();

        if ("admin".equals(playerEmail) && "admin123".equals(playerPassword)) {
            errorLabel.setText("");
            return true;
        }

        if (playerEmail.isEmpty()) {
            errorLabel.setText("Please enter your email");
            focusedFieldIndex = 0;
            activeFields.get(0).requestFocusInWindow();
            updateFieldBorders();
            return false;
        }
        if (!playerEmail.contains("@") || !playerEmail.contains(".")) {
            errorLabel.setText("Please enter a valid email");
            focusedFieldIndex = 0;
            activeFields.get(0).requestFocusInWindow();
            updateFieldBorders();
            return false;
        }
        if (playerPassword.isEmpty()) {
            errorLabel.setText("Please enter your password");
            focusedFieldIndex = 1;
            activeFields.get(1).requestFocusInWindow();
            updateFieldBorders();
            return false;
        }

        errorLabel.setText("");
        return true;
    }

    private boolean validateSignup() {
        playerName = nameField.getText().trim();
        playerEmail = emailField.getText().trim();
        playerPhone = phoneField.getText().trim();
        playerPassword = new String(passwordField.getPassword()).trim();

        if (playerName.isEmpty()) {
            errorLabel.setText("Please enter your name");
            focusedFieldIndex = 0;
            activeFields.get(0).requestFocusInWindow();
            updateFieldBorders();
            return false;
        }
        if (playerEmail.isEmpty()) {
            errorLabel.setText("Please enter your email");
            focusedFieldIndex = 1;
            activeFields.get(1).requestFocusInWindow();
            updateFieldBorders();
            return false;
        }
        if (!playerEmail.contains("@") || !playerEmail.contains(".")) {
            errorLabel.setText("Please enter a valid email");
            focusedFieldIndex = 1;
            activeFields.get(1).requestFocusInWindow();
            updateFieldBorders();
            return false;
        }
        if (playerPhone.isEmpty()) {
            errorLabel.setText("Please enter your phone number");
            focusedFieldIndex = 2;
            activeFields.get(2).requestFocusInWindow();
            updateFieldBorders();
            return false;
        }
        if (!playerPhone.matches("[0-9+\\-\\s]{7,15}")) {
            errorLabel.setText("Please enter a valid phone number");
            focusedFieldIndex = 2;
            activeFields.get(2).requestFocusInWindow();
            updateFieldBorders();
            return false;
        }
        if (playerPassword.isEmpty()) {
            errorLabel.setText("Please enter a password");
            focusedFieldIndex = 3;
            activeFields.get(3).requestFocusInWindow();
            updateFieldBorders();
            return false;
        }
        if (playerPassword.length() < 4) {
            errorLabel.setText("Password must be at least 4 characters");
            focusedFieldIndex = 3;
            activeFields.get(3).requestFocusInWindow();
            updateFieldBorders();
            return false;
        }

        errorLabel.setText("");
        return true;
    }

    public String getPlayerName() {
        return sessionPlayerName.isEmpty() ? playerName : sessionPlayerName;
    }
    public String getPlayerEmail() { return playerEmail; }
    public String getPlayerPhone() { return playerPhone; }
    public String getPlayerPassword() { return playerPassword; }
    public boolean isSignupMode() { return isSignupMode; }
    public int getPlayerLevel() { return sessionPlayerLevel; }

    public void setSessionPlayer(String name, int level) {
        this.sessionPlayerName = name == null ? "" : name;
        this.sessionPlayerLevel = Math.max(1, level);
    }

    public void clearFields() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        passwordField.setText("");
        errorLabel.setText("");
        sessionPlayerName = "";
        sessionPlayerLevel = 1;
    }

    public void focusFirstField() {
        focusedFieldIndex = 0;
        activeFields.get(0).requestFocusInWindow();
        updateFieldBorders();
    }

    public void setError(String message) {
        errorLabel.setText(message);
    }

    public void switchToLogin() {
        setMode(false);
    }

    public void switchToSignup() {
        setMode(true);
    }
}

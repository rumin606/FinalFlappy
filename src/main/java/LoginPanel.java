import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LoginPanel extends JPanel implements KeyListener {

    private boolean isSignupMode = false;

    private static final int TITLE_H = 42;
    private static final int NAV_H = 34;
    private static final int SUB_H = 22;
    private static final int LABEL_H = 18;
    private static final int FIELD_H = 36;
    private static final int LABEL_GAP = 4;
    private static final int ERROR_H = 22;
    private static final int BTN_H = 44;
    private static final int HINT_H = 30;
    private static final int FIELD_W = 320;
    private static final int CARD_W = 390;
    private static final int CARD_PAD_Y = 24;
    private static final int NAV_W = 90;
    private static final int NAV_GAP_X = 10;
    private static final int BTN_W = 200;
    private static final int MAX_V_GAP = 14;
    private static final int MIN_V_GAP = 6;
    private static final int OUTER_MARGIN = 24;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*"
                    + "@(?:[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?\\.)+[A-Za-z]{2,}$");
    private static final Pattern PHONE_CHARS_PATTERN = Pattern.compile("^\\+?[0-9 ()\\-]+$");
    private static final Pattern NEPAL_MOBILE_PATTERN = Pattern.compile("^98[0-9]{8}$");

    private static final String ERR_EMAIL_EMPTY = "Please enter your email";
    private static final String ERR_EMAIL_INVALID = "Please enter a valid email (e.g. name@gmail.com)";
    private static final String ERR_PHONE_EMPTY = "Please enter your phone number";
    private static final String ERR_PHONE_INVALID = "Enter a 10-digit Nepali mobile number (e.g. +977 9812345678)";

    private final JTextField nameField;
    private final JTextField emailField;
    private final JTextField phoneField;
    private final JPasswordField passwordField;
    private final JLabel errorLabel;
    private final JLabel hintLabel;
    private final JLabel subtitleLabel;
    private final JLabel titleLabel;
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
    private JLabel[] activeLabels;
    private int focusedFieldIndex = 0;

    private final ActionListener onLogin;
    private final ActionListener onSignup;

    private Image backgroundImage;
    private JButton loginActionButton;
    private JButton signupActionButton;

    private final int baseW;
    private final int baseH;
    private int cardX;
    private int cardY;
    private int cardW = CARD_W;
    private int cardH = 400;

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
        this.baseW = width;
        this.baseH = height;
        setPreferredSize(new Dimension(width, height));
        setLayout(null);
        setBackground(BG_COLOR);
        setFocusable(true);
        addKeyListener(this);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                doLayout();
            }
        });

        try {
            backgroundImage = new ImageIcon(getClass().getResource("/images/bg4.jpg")).getImage();
        } catch (Exception ex) {
            backgroundImage = null;
        }

        titleLabel = new JLabel("FLAPPY BIRD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(ACCENT_COLOR);
        add(titleLabel);

        loginNavButton = createNavButton("LOGIN");
        signupNavButton = createNavButton("SIGNUP");
        add(loginNavButton);
        add(signupNavButton);

        subtitleLabel = new JLabel("Sign in to your account", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        subtitleLabel.setForeground(MUTED_TEXT);
        add(subtitleLabel);

        nameLabel = createLabel("Player Name");
        emailLabel = createLabel("Email Address");
        phoneLabel = createLabel("Phone Number");
        passwordLabel = createLabel("Password");
        add(nameLabel);
        add(emailLabel);
        add(phoneLabel);
        add(passwordLabel);

        nameField = createTextField();
        emailField = createTextField();
        phoneField = createTextField();
        phoneField.setToolTipText("Nepali mobile: 10 digits starting with 98, e.g. +977 9812345678");
        add(nameField);
        add(emailField);
        add(phoneField);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 15));
        passwordField.setBackground(FIELD_BG);
        passwordField.setForeground(TEXT_COLOR);
        passwordField.setCaretColor(TEXT_COLOR);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
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
        errorLabel.setFont(new Font("Arial", Font.BOLD, 14));
        errorLabel.setForeground(ERROR_COLOR);
        add(errorLabel);

        loginActionButton = createActionButton("LOGIN", LOGIN_BUTTON_COLOR, BTN_W, BTN_H);
        signupActionButton = createActionButton("SIGNUP", SIGNUP_BUTTON_COLOR, BTN_W, BTN_H);
        add(loginActionButton);
        add(signupActionButton);

        hintLabel = new JLabel("Tab: next field  |  Enter/Space: submit", SwingConstants.CENTER);
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

    @Override
    public void doLayout() {
        int w = getWidth() > 0 ? getWidth() : baseW;
        int h = getHeight() > 0 ? getHeight() : baseH;
        int cx = w / 2;
        int n = activeLabels.length;

        int fieldBlock = LABEL_H + LABEL_GAP + FIELD_H;
        int fixedH = TITLE_H + NAV_H + SUB_H + n * fieldBlock + ERROR_H + BTN_H;
        int gapSlots = 5 + n;

        int gap = (h - HINT_H - OUTER_MARGIN - fixedH) / gapSlots;
        gap = Math.max(MIN_V_GAP, Math.min(MAX_V_GAP, gap));

        int newCardH = NAV_H + SUB_H + n * fieldBlock + ERROR_H + BTN_H
                + (gapSlots - 1) * gap + 2 * CARD_PAD_Y;
        int blockH = TITLE_H + gap + newCardH;

        int y = Math.max(OUTER_MARGIN / 2, (h - HINT_H - blockH) / 2);

        titleLabel.setBounds(0, y, w, TITLE_H);
        y += TITLE_H + gap;

        cardX = cx - CARD_W / 2;
        cardY = y;
        cardW = CARD_W;
        cardH = newCardH;
        y += CARD_PAD_Y;

        int navTotal = NAV_W * 2 + NAV_GAP_X;
        loginNavButton.setBounds(cx - navTotal / 2, y, NAV_W, NAV_H);
        signupNavButton.setBounds(cx - navTotal / 2 + NAV_W + NAV_GAP_X, y, NAV_W, NAV_H);
        y += NAV_H + gap;

        subtitleLabel.setBounds(0, y, w, SUB_H);
        y += SUB_H + gap;

        int fieldX = cx - FIELD_W / 2;
        for (int i = 0; i < n; i++) {
            activeLabels[i].setBounds(fieldX, y, FIELD_W, LABEL_H);
            y += LABEL_H + LABEL_GAP;
            activeFields.get(i).setBounds(fieldX, y, FIELD_W, FIELD_H);
            y += FIELD_H + gap;
        }
        y -= gap;

        errorLabel.setBounds(0, y, w, ERROR_H);
        y += ERROR_H + gap;

        loginActionButton.setBounds(cx - BTN_W / 2, y, BTN_W, BTN_H);
        signupActionButton.setBounds(cx - BTN_W / 2, y, BTN_W, BTN_H);

        hintLabel.setBounds(0, h - HINT_H, w, HINT_H);
    }

    private JButton createActionButton(String text, Color base, int w, int h) {
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
        button.setSize(w, h);
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

        g2.setColor(new Color(12, 20, 32, 190));
        g2.fillRoundRect(cardX, cardY, cardW, cardH, 30, 30);
        g2.setColor(new Color(255, 255, 255, 45));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(cardX, cardY, cardW, cardH, 30, 30);

        g2.dispose();
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
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

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(MUTED_TEXT);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 15));
        field.setBackground(FIELD_BG);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
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

        loginActionButton.setVisible(!signup);
        signupActionButton.setVisible(signup);

        subtitleLabel.setText(signup ? "Create a new account" : "Sign in to your account");

        activeFields = signup ? signupFields : loginFields;
        activeLabels = signup
                ? new JLabel[]{nameLabel, emailLabel, phoneLabel, passwordLabel}
                : new JLabel[]{emailLabel, passwordLabel};
        focusedFieldIndex = 0;
        doLayout();
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
                        BorderFactory.createEmptyBorder(3, 9, 3, 9)
                ));
            } else {
                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                        BorderFactory.createEmptyBorder(4, 10, 4, 10)
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
            return fail(0, ERR_EMAIL_EMPTY);
        }
        if (!isValidEmail(playerEmail)) {
            return fail(0, ERR_EMAIL_INVALID);
        }
        if (playerPassword.isEmpty()) {
            return fail(1, "Please enter your password");
        }

        errorLabel.setText("");
        return true;
    }

    private boolean validateSignup() {
        playerName = nameField.getText().trim();
        playerEmail = emailField.getText().trim();
        playerPassword = new String(passwordField.getPassword()).trim();

        if (playerName.isEmpty()) {
            return fail(0, "Please enter your name");
        }
        if (playerEmail.isEmpty()) {
            return fail(1, ERR_EMAIL_EMPTY);
        }
        if (!isValidEmail(playerEmail)) {
            return fail(1, ERR_EMAIL_INVALID);
        }

        String phone = phoneField.getText().trim();
        if (phone.isEmpty()) {
            return fail(2, ERR_PHONE_EMPTY);
        }
        String normalizedPhone = normalizeNepalPhone(phone);
        if (normalizedPhone == null) {
            return fail(2, ERR_PHONE_INVALID);
        }
        playerPhone = normalizedPhone;

        if (playerPassword.isEmpty()) {
            return fail(3, "Please enter a password");
        }
        if (playerPassword.length() < 4) {
            return fail(3, "Password must be at least 4 characters");
        }

        errorLabel.setText("");
        return true;
    }

    private static boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private static String normalizeNepalPhone(String phone) {
        if (!PHONE_CHARS_PATTERN.matcher(phone).matches()) {
            return null;
        }
        StringBuilder digits = new StringBuilder();
        for (int i = 0; i < phone.length(); i++) {
            char c = phone.charAt(i);
            if (c >= '0' && c <= '9') {
                digits.append(c);
            }
        }
        String national = digits.toString();
        if (national.length() == 13 && national.startsWith("977")) {
            national = national.substring(3);
        }
        return NEPAL_MOBILE_PATTERN.matcher(national).matches() ? national : null;
    }

    private boolean fail(int fieldIndex, String message) {
        errorLabel.setText(message);
        focusedFieldIndex = fieldIndex;
        activeFields.get(fieldIndex).requestFocusInWindow();
        updateFieldBorders();
        return false;
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

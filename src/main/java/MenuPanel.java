import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class MenuPanel extends JPanel implements KeyListener {

    private int selectedBird = 0;
    private int selectedBackground = 0;
    private int focusedSection = 0;

    private final Image[] birdImages;
    private final Image[] backgroundImages;
    private final String[] birdNames = {"Yellow", "Blue", "Green", "Pink", "Orange", "Blue"};
    private final String[] bgNames = {"Sky", "Night", "Sunset", "City", "Forest", "Desert"};

    private static final Color BG_COLOR = new Color(45, 52, 54);
    private static final Color ACCENT_COLOR = new Color(116, 185, 255);
    private static final Color TEXT_COLOR = new Color(255, 255, 255);
    private static final Color PANEL_BG = new Color(57, 66, 69);
    private static final Color SELECTED_BORDER = new Color(253, 203, 110);
    private static final Color FOCUS_COLOR = new Color(46, 213, 115);
    private static final Color MUTED_TEXT = new Color(178, 190, 195);
    private static final Color NAV_BTN_COLOR = new Color(99, 110, 114);

    private final JLabel birdNameLabel;
    private final JLabel bgNameLabel;
    private final JLabel playerNameLabel;
    private final JLabel hintLabel;

    private final ActionListener onPlay;
    private final ActionListener onLogout;

    private String playerName = "";
    private int playerLevel = 1;

    public MenuPanel(int width, int height, ActionListener onPlay, ActionListener onLogout,
                     ActionListener onGoToLogin, ActionListener onGoToSignup) {
        this.onPlay = onPlay;
        this.onLogout = onLogout;
        setPreferredSize(new Dimension(width, height));
        setLayout(null);
        setBackground(BG_COLOR);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        addKeyListener(this);

        birdImages = new Image[]{
                new ImageIcon(getClass().getResource("/images/bird1.png")).getImage(),
                new ImageIcon(getClass().getResource("/images/bird2.png")).getImage(),
                new ImageIcon(getClass().getResource("/images/bird3.png")).getImage(),
                new ImageIcon(getClass().getResource("/images/bird4.png")).getImage(),
                new ImageIcon(getClass().getResource("/images/bird5.png")).getImage(),
                new ImageIcon(getClass().getResource("/images/bird6.png")).getImage()
        };

        backgroundImages = new Image[]{
                new ImageIcon(getClass().getResource("/images/bg1.jpg")).getImage(),
                new ImageIcon(getClass().getResource("/images/bg2.jpg")).getImage(),
                new ImageIcon(getClass().getResource("/images/bg3.jpg")).getImage(),
                new ImageIcon(getClass().getResource("/images/bg4.jpg")).getImage(),
                new ImageIcon(getClass().getResource("/images/bg7.png")).getImage(),
                new ImageIcon(getClass().getResource("/images/bg8.png")).getImage()
        };

        int centerX = width / 2;

        JButton loginNavBtn = new JButton("Login");
        loginNavBtn.setBounds(centerX - 110, 8, 90, 28);
        loginNavBtn.setFont(new Font("Arial", Font.BOLD, 12));
        loginNavBtn.setBackground(NAV_BTN_COLOR);
        loginNavBtn.setForeground(MUTED_TEXT);
        loginNavBtn.setFocusPainted(false);
        loginNavBtn.setBorder(BorderFactory.createEmptyBorder());
        loginNavBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginNavBtn.addActionListener(e -> onGoToLogin.actionPerformed(null));
        add(loginNavBtn);

        JButton signupNavBtn = new JButton("Signup");
        signupNavBtn.setBounds(centerX + 20, 8, 90, 28);
        signupNavBtn.setFont(new Font("Arial", Font.BOLD, 12));
        signupNavBtn.setBackground(NAV_BTN_COLOR);
        signupNavBtn.setForeground(MUTED_TEXT);
        signupNavBtn.setFocusPainted(false);
        signupNavBtn.setBorder(BorderFactory.createEmptyBorder());
        signupNavBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupNavBtn.addActionListener(e -> onGoToSignup.actionPerformed(null));
        add(signupNavBtn);

        JLabel titleLabel = new JLabel("CUSTOMIZE YOUR GAME", SwingConstants.CENTER);
        titleLabel.setBounds(0, 45, width, 45);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(ACCENT_COLOR);
        add(titleLabel);

        playerNameLabel = new JLabel("", SwingConstants.CENTER);
        playerNameLabel.setBounds(0, 85, width, 25);
        playerNameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        playerNameLabel.setForeground(MUTED_TEXT);
        add(playerNameLabel);

        JLabel birdTitle = new JLabel("Select Bird", SwingConstants.CENTER);
        birdTitle.setBounds(0, 120, width, 30);
        birdTitle.setFont(new Font("Arial", Font.BOLD, 22));
        birdTitle.setForeground(TEXT_COLOR);
        add(birdTitle);

        birdNameLabel = new JLabel(birdNames[selectedBird], SwingConstants.CENTER);
        birdNameLabel.setBounds(0, 280, width, 25);
        birdNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        birdNameLabel.setForeground(TEXT_COLOR);
        add(birdNameLabel);

        JLabel bgTitle = new JLabel("Select Background", SwingConstants.CENTER);
        bgTitle.setBounds(0, 325, width, 30);
        bgTitle.setFont(new Font("Arial", Font.BOLD, 22));
        bgTitle.setForeground(TEXT_COLOR);
        add(bgTitle);

        bgNameLabel = new JLabel(bgNames[selectedBackground], SwingConstants.CENTER);
        bgNameLabel.setBounds(0, 490, width, 30);
        bgNameLabel.setFont(new Font("Arial", Font.BOLD, 22));
        bgNameLabel.setForeground(TEXT_COLOR);
        add(bgNameLabel);

        hintLabel = new JLabel("Left/Right: change bird  |  Tab: switch section  |  Enter/Space: play", SwingConstants.CENTER);
        hintLabel.setBounds(0, height - 65, width, 25);
        hintLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        hintLabel.setForeground(MUTED_TEXT);
        add(hintLabel);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(width / 2 - 60, height - 38, 120, 30);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.setBackground(new Color(255, 71, 87));
        logoutButton.setForeground(TEXT_COLOR);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        logoutButton.addActionListener(e -> onLogout.actionPerformed(null));
        add(logoutButton);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_TAB) {
            focusedSection = (focusedSection + 1) % 2;
            repaint();
            e.consume();
            return;
        }

        if (focusedSection == 0) {
            if (key == KeyEvent.VK_LEFT) {
                selectedBird--;
                if (selectedBird < 0) selectedBird = birdImages.length - 1;
                birdNameLabel.setText(birdNames[selectedBird]);
                repaint();
            }
            if (key == KeyEvent.VK_RIGHT) {
                selectedBird++;
                if (selectedBird >= birdImages.length) selectedBird = 0;
                birdNameLabel.setText(birdNames[selectedBird]);
                repaint();
            }
        } else {
            if (key == KeyEvent.VK_LEFT) {
                selectedBackground--;
                if (selectedBackground < 0) selectedBackground = backgroundImages.length - 1;
                bgNameLabel.setText(bgNames[selectedBackground]);
                repaint();
            }
            if (key == KeyEvent.VK_RIGHT) {
                selectedBackground++;
                if (selectedBackground >= backgroundImages.length) selectedBackground = 0;
                bgNameLabel.setText(bgNames[selectedBackground]);
                repaint();
            }
        }

        if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_SPACE) {
            onPlay.actionPerformed(null);
            e.consume();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        int centerX = getWidth() / 2;

        int birdBoxX = centerX - 90;
        int birdBoxY = 160;
        int birdBoxSize = 180;

        Color birdBorder = (focusedSection == 0) ? FOCUS_COLOR : SELECTED_BORDER;
        int birdStroke = (focusedSection == 0) ? 4 : 3;

        g2.setColor(PANEL_BG);
        g2.fillRoundRect(birdBoxX - 5, birdBoxY - 5, birdBoxSize + 10, birdBoxSize + 10, 15, 15);
        g2.setColor(birdBorder);
        g2.setStroke(new BasicStroke(birdStroke));
        g2.drawRoundRect(birdBoxX - 5, birdBoxY - 5, birdBoxSize + 10, birdBoxSize + 10, 15, 15);

        g2.drawImage(birdImages[selectedBird], birdBoxX + 40, birdBoxY + 25, 100, 100, null);

        int bgBoxX = centerX - 90;
        int bgBoxY = 365;
        int bgBoxW = 180;
        int bgBoxH = 140;

        Color bgBorder = (focusedSection == 1) ? FOCUS_COLOR : SELECTED_BORDER;
        int bgStroke = (focusedSection == 1) ? 4 : 3;

        g2.setColor(PANEL_BG);
        g2.fillRoundRect(bgBoxX - 7, bgBoxY - 7, bgBoxW + 14, bgBoxH + 14, 15, 15);
        g2.setColor(bgBorder);
        g2.setStroke(new BasicStroke(bgStroke));
        g2.drawRoundRect(bgBoxX - 7, bgBoxY - 7, bgBoxW + 14, bgBoxH + 14, 15, 15);

        g2.drawImage(backgroundImages[selectedBackground], bgBoxX, bgBoxY, bgBoxW, bgBoxH, null);
    }

    public int getSelectedBird() { return selectedBird; }
    public int getSelectedBackground() { return selectedBackground; }
    public Image getBirdImage(int index) { return birdImages[index]; }
    public Image getBackgroundImage(int index) { return backgroundImages[index]; }

    public void setPlayerName(String name) {
        this.playerName = name == null ? "" : name;
        updatePlayerLabel();
    }

    public void setPlayerLevel(int level) {
        this.playerLevel = Math.max(1, level);
        updatePlayerLabel();
    }

    public int getPlayerLevel() { return playerLevel; }

    private void updatePlayerLabel() {
        playerNameLabel.setText("Playing as: " + playerName + "   |   Level: " + playerLevel);
    }

    public void resetSelections() {
        selectedBird = 0;
        selectedBackground = 0;
        focusedSection = 0;
        birdNameLabel.setText(birdNames[0]);
        bgNameLabel.setText(bgNames[0]);
        repaint();
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import org.bson.Document;

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

    private final JLabel statsGamesLabel;
    private final JLabel statsBestLabel;
    private final JLabel statsTotalLabel;
    private final JLabel statsLevelLabel;
    private final JLabel statsTimeLabel;

    private final ActionListener onPlay;
    private final ActionListener onLogout;

    private String playerName = "";
    private int playerLevel = 1;

    public MenuPanel(int width, int height, ActionListener onPlay, ActionListener onLogout,
                     ActionListener onAdmin) {
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
        int cardX = centerX + 25;
        int cardW = 300;

        JLabel titleLabel = new JLabel("", SwingConstants.CENTER);
        titleLabel.setBounds(0, 30, width, 40);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(ACCENT_COLOR);
        add(titleLabel);

        playerNameLabel = new JLabel("", SwingConstants.CENTER);
        playerNameLabel.setBounds(0, 10, width, 25);
        playerNameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        playerNameLabel.setForeground(MUTED_TEXT);
        add(playerNameLabel);

        JLabel birdTitle = new JLabel("Select Bird", SwingConstants.CENTER);
        birdTitle.setBounds(-145, 108, width, 25);
        birdTitle.setFont(new Font("Arial", Font.BOLD, 20));
        birdTitle.setForeground(TEXT_COLOR);
        add(birdTitle);

        birdNameLabel = new JLabel(birdNames[selectedBird], SwingConstants.CENTER);
        birdNameLabel.setBounds(-145, 325, width, 22);
        birdNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        birdNameLabel.setForeground(TEXT_COLOR);
        add(birdNameLabel);

        JLabel bgTitle = new JLabel("Select Background", SwingConstants.CENTER);
        bgTitle.setBounds(-145, 348, width, 25);
        bgTitle.setFont(new Font("Arial", Font.BOLD, 20));
        bgTitle.setForeground(TEXT_COLOR);
        add(bgTitle);

        bgNameLabel = new JLabel(bgNames[selectedBackground], SwingConstants.CENTER);
        bgNameLabel.setBounds(-145, 520, width, 25);
        bgNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        bgNameLabel.setForeground(TEXT_COLOR);
        add(bgNameLabel);

        JLabel scoreboardTitle = new JLabel("SCOREBOARD", SwingConstants.CENTER);
        scoreboardTitle.setBounds(cardX, 118, cardW, 28);
        scoreboardTitle.setFont(new Font("Arial", Font.BOLD, 20));
        scoreboardTitle.setForeground(ACCENT_COLOR);
        add(scoreboardTitle);

        statsGamesLabel = new JLabel("0");
        statsBestLabel = new JLabel("0");
        statsTotalLabel = new JLabel("0");
        statsLevelLabel = new JLabel("1");
        statsTimeLabel = new JLabel("0m 0s");

        createStatRow("Games Played", statsGamesLabel, 170, cardX, cardW);
        createStatRow("Best Score", statsBestLabel, 212, cardX, cardW);
        createStatRow("Total Score", statsTotalLabel, 254, cardX, cardW);
        createStatRow("Current Level", statsLevelLabel, 296, cardX, cardW);
        createStatRow("Time Played", statsTimeLabel, 338, cardX, cardW);

        hintLabel = new JLabel("Left/Right: change bird  |  Tab: switch section  |  Enter/Space: play", SwingConstants.CENTER);
        hintLabel.setBounds(0, height - 72, width, 25);
        hintLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        hintLabel.setForeground(MUTED_TEXT);
        add(hintLabel);

        JButton adminButton = new JButton("Admin Panel");
        adminButton.setBounds(centerX - 210, height - 40, 120, 32);
        adminButton.setFont(new Font("Arial", Font.BOLD, 13));
        adminButton.setBackground(new Color(162, 89, 255));
        adminButton.setForeground(TEXT_COLOR);
        adminButton.setFocusPainted(false);
        adminButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        adminButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        adminButton.addActionListener(e -> onAdmin.actionPerformed(null));
        add(adminButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(centerX + 90, height - 40, 120, 32);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 13));
        logoutButton.setBackground(new Color(255, 71, 87));
        logoutButton.setForeground(TEXT_COLOR);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> onLogout.actionPerformed(null));
        add(logoutButton);
    }

    private void createStatRow(String name, JLabel valueLabel, int y, int cardX, int cardW) {
        JLabel nameLabel = new JLabel(name, SwingConstants.LEFT);
        nameLabel.setBounds(cardX + 25, y, cardW - 130, 24);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 15));
        nameLabel.setForeground(MUTED_TEXT);
        add(nameLabel);

        valueLabel.setBounds(cardX + cardW - 105, y, 80, 24);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        valueLabel.setForeground(TEXT_COLOR);
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(valueLabel);
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

        int birdBoxX = centerX - 230;
        int birdBoxY = 150;
        int birdBoxSize = 170;

        Color birdBorder = (focusedSection == 0) ? FOCUS_COLOR : SELECTED_BORDER;
        int birdStroke = (focusedSection == 0) ? 4 : 3;

        g2.setColor(PANEL_BG);
        g2.fillRoundRect(birdBoxX - 5, birdBoxY - 5, birdBoxSize + 10, birdBoxSize + 10, 15, 15);
        g2.setColor(birdBorder);
        g2.setStroke(new BasicStroke(birdStroke));
        g2.drawRoundRect(birdBoxX - 5, birdBoxY - 5, birdBoxSize + 10, birdBoxSize + 10, 15, 15);

        g2.drawImage(birdImages[selectedBird], birdBoxX + 35, birdBoxY + 35, 100, 100, null);

        int bgBoxX = centerX - 230;
        int bgBoxY = 390;
        int bgBoxW = 170;
        int bgBoxH = 170;

        Color bgBorder = (focusedSection == 1) ? FOCUS_COLOR : SELECTED_BORDER;
        int bgStroke = (focusedSection == 1) ? 4 : 3;

        g2.setColor(PANEL_BG);
        g2.fillRoundRect(bgBoxX - 7, bgBoxY - 7, bgBoxW + 14, bgBoxH + 14, 15, 15);
        g2.setColor(bgBorder);
        g2.setStroke(new BasicStroke(bgStroke));
        g2.drawRoundRect(bgBoxX - 7, bgBoxY - 7, bgBoxW + 14, bgBoxH + 14, 15, 15);

        g2.drawImage(backgroundImages[selectedBackground], bgBoxX, bgBoxY, bgBoxW, bgBoxH, null);

        int cardX = centerX + 25;
        int cardY = 108;
        int cardW = 300;
        int cardH = 405;
        g2.setColor(new Color(12, 20, 32, 200));
        g2.fillRoundRect(cardX, cardY, cardW, cardH, 20, 20);
        g2.setColor(new Color(255, 255, 255, 50));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(cardX, cardY, cardW, cardH, 20, 20);
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

    public void setPlayerStats(Document player) {
        if (player == null) {
            statsGamesLabel.setText("0");
            statsBestLabel.setText("0");
            statsTotalLabel.setText("0");
            statsLevelLabel.setText("1");
            statsTimeLabel.setText("0m 0s");
            return;
        }
        statsGamesLabel.setText(String.valueOf(player.getInteger("gamesPlayed", 0)));
        statsBestLabel.setText(String.valueOf(player.getInteger("bestScore", 0)));
        statsTotalLabel.setText(String.valueOf(player.getInteger("totalScore", 0)));
        statsLevelLabel.setText(String.valueOf(player.getInteger("currentLevel", 1)));
        Long totalDuration = player.getLong("totalDuration");
        statsTimeLabel.setText(formatDuration(totalDuration == null ? 0 : totalDuration));
    }

    private String formatDuration(long ms) {
        long totalSeconds = ms / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return minutes + "m " + seconds + "s";
    }

    private void updatePlayerLabel() {
        playerNameLabel.setText("Playing as: " + playerName /*+ "   |   Level: " + playerLevel*/);
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

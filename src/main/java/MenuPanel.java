import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
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
    private static final Color SELECTED_BORDER = new Color(253, 203, 110);
    private static final Color FOCUS_COLOR = new Color(46, 213, 115);
    private static final Color MUTED_TEXT = new Color(178, 190, 195);
    private static final Color NAV_BTN_COLOR = new Color(99, 110, 114);

    private static final Color SKY_TOP = new Color(12, 22, 43);
    private static final Color SKY_UPPER = new Color(23, 47, 82);
    private static final Color SKY_MID = new Color(44, 95, 138);
    private static final Color SKY_HORIZON = new Color(109, 168, 196);
    private static final Color SUN_CORE = new Color(255, 238, 196);
    private static final Color HILL_FAR = new Color(32, 66, 100);
    private static final Color HILL_NEAR = new Color(22, 50, 80);
    private static final Color GRASS_TOP = new Color(48, 98, 66);
    private static final Color GRASS_BOTTOM = new Color(22, 58, 40);
    private static final Color GRASS_EDGE = new Color(126, 200, 130, 130);
    private static final Color BOX_BG = new Color(10, 18, 30, 190);

    private static final int BOX = 170;
    private static final int COL_GAP = 60;
    private static final int SCORE_W = 300;
    private static final int SCORE_H = 405;
    private static final int SECTION_TITLE_H = 26;
    private static final int NAME_LABEL_H = 24;
    private static final int STAT_ROW_H = 24;
    private static final int STAT_ROW_GAP = 8;
    private static final int HEADER_H = 92;
    private static final int FOOTER_H = 84;
    private static final int CONTENT_W = BOX + COL_GAP + SCORE_W;

    private final JLabel birdNameLabel;
    private final JLabel bgNameLabel;
    private final JLabel playerNameLabel;
    private final JLabel hintLabel;
    private final JLabel titleLabel;
    private final JLabel birdTitle;
    private final JLabel bgTitle;
    private final JLabel scoreboardTitle;
    private final JButton logoutButton;
    private final List<JLabel> statNameLabels = new ArrayList<>();
    private final List<JLabel> statValueLabels = new ArrayList<>();

    private final JLabel statsGamesLabel;
    private final JLabel statsBirdLabel;
    private final JLabel statsBgLabel;
    private final JLabel statsBestLabel;
    private final JLabel statsTotalLabel;
    private final JLabel statsLevelLabel;
    private final JLabel statsTimeLabel;

    private final ActionListener onPlay;
    private final ActionListener onLogout;

    private String playerName = "";
    private int playerLevel = 1;
    private Document playerDoc = null;

    private final int baseW;
    private final int baseH;
    private int birdBoxX, birdBoxY;
    private int bgBoxX, bgBoxY;
    private int scoreCardX, scoreCardY;
    private int leftCx, rightCx;

    public MenuPanel(int width, int height, ActionListener onPlay, ActionListener onLogout) {
        this.onPlay = onPlay;
        this.onLogout = onLogout;
        this.baseW = width;
        this.baseH = height;
        setPreferredSize(new Dimension(width, height));
        setLayout(null);
        setBackground(BG_COLOR);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        addKeyListener(this);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                doLayout();
            }
        });

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

        titleLabel = new JLabel("", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(ACCENT_COLOR);
        add(titleLabel);

        playerNameLabel = new JLabel("", SwingConstants.CENTER);
        playerNameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        playerNameLabel.setForeground(MUTED_TEXT);
        add(playerNameLabel);

        birdTitle = new JLabel("Select Bird", SwingConstants.CENTER);
        birdTitle.setFont(new Font("Arial", Font.BOLD, 20));
        birdTitle.setForeground(TEXT_COLOR);
        add(birdTitle);

        birdNameLabel = new JLabel(birdNames[selectedBird], SwingConstants.CENTER);
        birdNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        birdNameLabel.setForeground(TEXT_COLOR);
        add(birdNameLabel);

        bgTitle = new JLabel("Select Background", SwingConstants.CENTER);
        bgTitle.setFont(new Font("Arial", Font.BOLD, 20));
        bgTitle.setForeground(TEXT_COLOR);
        add(bgTitle);

        bgNameLabel = new JLabel(bgNames[selectedBackground], SwingConstants.CENTER);
        bgNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        bgNameLabel.setForeground(TEXT_COLOR);
        add(bgNameLabel);

        scoreboardTitle = new JLabel("SCOREBOARD", SwingConstants.CENTER);
        scoreboardTitle.setFont(new Font("Arial", Font.BOLD, 20));
        scoreboardTitle.setForeground(ACCENT_COLOR);
        add(scoreboardTitle);

        statsGamesLabel = new JLabel("0");
        statsBirdLabel = new JLabel(birdNames[selectedBird]);
        statsBgLabel = new JLabel(bgNames[selectedBackground]);
        statsBestLabel = new JLabel("0");
        statsTotalLabel = new JLabel("0");
        statsLevelLabel = new JLabel("1");
        statsTimeLabel = new JLabel("0m 0s");

        createStatRow("Games Played", statsGamesLabel);
        createStatRow("Bird", statsBirdLabel);
        createStatRow("Background", statsBgLabel);
        createStatRow("Best Score", statsBestLabel);
        createStatRow("Total Score", statsTotalLabel);
        createStatRow("Current Level", statsLevelLabel);
        createStatRow("Time Played", statsTimeLabel);

        hintLabel = new JLabel("Left/Right: change bird  |  Tab: switch section  |  Enter/Space: play", SwingConstants.CENTER);
        hintLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        hintLabel.setForeground(MUTED_TEXT);
        add(hintLabel);

        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 13));
        logoutButton.setBackground(new Color(255, 71, 87));
        logoutButton.setForeground(TEXT_COLOR);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> onLogout.actionPerformed(null));
        add(logoutButton);

        doLayout();
    }

    @Override
    public void doLayout() {
        int w = getWidth() > 0 ? getWidth() : baseW;
        int h = getHeight() > 0 ? getHeight() : baseH;

        titleLabel.setBounds(0, 26, w, 40);
        playerNameLabel.setBounds(0, 8, w, 24);

        leftCx = Math.max(BOX / 2 + 16, (w - CONTENT_W) / 2 + BOX / 2);
        rightCx = leftCx + BOX / 2 + COL_GAP + SCORE_W / 2;

        int sectionH = SECTION_TITLE_H + 4 + BOX + 6 + NAME_LABEL_H;
        int columnH = sectionH * 2 + 18;
        int regionTop = HEADER_H;
        int regionBottom = h - FOOTER_H;
        int top = regionTop + Math.max(0, (regionBottom - regionTop - columnH) / 2);

        int y = top;
        birdTitle.setBounds(leftCx - 140, y, 280, SECTION_TITLE_H);
        y += SECTION_TITLE_H + 4;
        birdBoxX = leftCx - BOX / 2;
        birdBoxY = y;
        y += BOX + 6;
        birdNameLabel.setBounds(leftCx - 140, y, 280, NAME_LABEL_H);
        y += NAME_LABEL_H + 18;

        bgTitle.setBounds(leftCx - 140, y, 280, SECTION_TITLE_H);
        y += SECTION_TITLE_H + 4;
        bgBoxX = leftCx - BOX / 2;
        bgBoxY = y;
        y += BOX + 6;
        bgNameLabel.setBounds(leftCx - 140, y, 280, NAME_LABEL_H);

        int statH = statNameLabels.size() * (STAT_ROW_H + STAT_ROW_GAP) - STAT_ROW_GAP;
        int cardH = Math.max(SCORE_H, 58 + statH + 24);
        scoreCardX = rightCx - SCORE_W / 2;
        scoreCardY = regionTop + Math.max(0, (regionBottom - regionTop - cardH) / 2);

        scoreboardTitle.setBounds(scoreCardX, scoreCardY + 14, SCORE_W, 28);
        int statY = scoreCardY + 58;
        for (int i = 0; i < statNameLabels.size(); i++) {
            statNameLabels.get(i).setBounds(scoreCardX + 22, statY, SCORE_W - 120, STAT_ROW_H);
            statValueLabels.get(i).setBounds(scoreCardX + SCORE_W - 98, statY, 76, STAT_ROW_H);
            statY += STAT_ROW_H + STAT_ROW_GAP;
        }

        hintLabel.setBounds(0, h - FOOTER_H + 14, w, 24);
        logoutButton.setBounds(w / 2 - 60, h - 44, 120, 32);
    }

    private void createStatRow(String name, JLabel valueLabel) {
        JLabel nameLabel = new JLabel(name, SwingConstants.LEFT);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 15));
        nameLabel.setForeground(MUTED_TEXT);
        add(nameLabel);
        statNameLabels.add(nameLabel);

        valueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        valueLabel.setForeground(TEXT_COLOR);
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(valueLabel);
        statValueLabels.add(valueLabel);
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
                statsBirdLabel.setText(birdNames[selectedBird]);
                updateComboStats();
                repaint();
            }
            if (key == KeyEvent.VK_RIGHT) {
                selectedBird++;
                if (selectedBird >= birdImages.length) selectedBird = 0;
                birdNameLabel.setText(birdNames[selectedBird]);
                statsBirdLabel.setText(birdNames[selectedBird]);
                updateComboStats();
                repaint();
            }
        } else {
            if (key == KeyEvent.VK_LEFT) {
                selectedBackground--;
                if (selectedBackground < 0) selectedBackground = backgroundImages.length - 1;
                bgNameLabel.setText(bgNames[selectedBackground]);
                statsBgLabel.setText(bgNames[selectedBackground]);
                updateComboStats();
                repaint();
            }
            if (key == KeyEvent.VK_RIGHT) {
                selectedBackground++;
                if (selectedBackground >= backgroundImages.length) selectedBackground = 0;
                bgNameLabel.setText(bgNames[selectedBackground]);
                statsBgLabel.setText(bgNames[selectedBackground]);
                updateComboStats();
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
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        paintBackdrop(g2);

        Color birdBorder = (focusedSection == 0) ? FOCUS_COLOR : SELECTED_BORDER;
        int birdStroke = (focusedSection == 0) ? 4 : 3;

        g2.setColor(BOX_BG);
        g2.fillRoundRect(birdBoxX - 5, birdBoxY - 5, BOX + 10, BOX + 10, 15, 15);
        g2.setColor(birdBorder);
        g2.setStroke(new BasicStroke(birdStroke));
        g2.drawRoundRect(birdBoxX - 5, birdBoxY - 5, BOX + 10, BOX + 10, 15, 15);

        g2.drawImage(birdImages[selectedBird], birdBoxX + BOX / 2 - 50, birdBoxY + BOX / 2 - 50, 100, 100, null);

        Color bgBorder = (focusedSection == 1) ? FOCUS_COLOR : SELECTED_BORDER;
        int bgStroke = (focusedSection == 1) ? 4 : 3;

        g2.setColor(BOX_BG);
        g2.fillRoundRect(bgBoxX - 7, bgBoxY - 7, BOX + 14, BOX + 14, 15, 15);
        g2.setColor(bgBorder);
        g2.setStroke(new BasicStroke(bgStroke));
        g2.drawRoundRect(bgBoxX - 7, bgBoxY - 7, BOX + 14, BOX + 14, 15, 15);

        g2.drawImage(backgroundImages[selectedBackground], bgBoxX, bgBoxY, BOX, BOX, null);

        int statH = statNameLabels.size() * (STAT_ROW_H + STAT_ROW_GAP) - STAT_ROW_GAP;
        int cardH = Math.max(SCORE_H, 58 + statH + 24);
        g2.setColor(new Color(12, 20, 32, 200));
        g2.fillRoundRect(scoreCardX, scoreCardY, SCORE_W, cardH, 20, 20);
        g2.setColor(new Color(255, 255, 255, 50));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(scoreCardX, scoreCardY, SCORE_W, cardH, 20, 20);

        g2.dispose();
    }

    private void paintBackdrop(Graphics2D g2) {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        g2.setPaint(new LinearGradientPaint(0, 0, 0, h,
                new float[]{0f, 0.34f, 0.62f, 1f},
                new Color[]{SKY_TOP, SKY_UPPER, SKY_MID, SKY_HORIZON}));
        g2.fillRect(0, 0, w, h);

        paintSunGlow(g2, w, h);
        paintSkyBirds(g2, w, h);
        paintClouds(g2, w, h);

        int hillBase = (int) (h * 0.80);
        paintHills(g2, hillBase, 46, HILL_FAR, 0);
        paintHills(g2, hillBase + 18, 32, HILL_NEAR, 110);

        int groundY = (int) (h * 0.90);
        g2.setPaint(new LinearGradientPaint(0, groundY, 0, h,
                new float[]{0f, 1f}, new Color[]{GRASS_TOP, GRASS_BOTTOM}));
        g2.fillRect(0, groundY, w, h - groundY);
        paintGrassTufts(g2, w, groundY);
        g2.setColor(GRASS_EDGE);
        g2.fillRect(0, groundY, w, 3);

        paintVignette(g2, w, h);
    }

    private void paintSunGlow(Graphics2D g2, int w, int h) {
        float sx = w * 0.56f;
        float sy = h * 0.115f;
        float radius = Math.max(120f, h * 0.34f);

        g2.setPaint(new RadialGradientPaint(new Point2D.Float(sx, sy), radius,
                new float[]{0f, 0.28f, 1f},
                new Color[]{new Color(255, 233, 168, 170), new Color(255, 205, 130, 55), new Color(255, 200, 120, 0)}));
        g2.fillRect(0, 0, w, h);

        int disc = Math.max(44, h / 13);
        g2.setColor(new Color(SUN_CORE.getRed(), SUN_CORE.getGreen(), SUN_CORE.getBlue(), 205));
        g2.fill(new Ellipse2D.Float(sx - disc / 2f, sy - disc / 2f, disc, disc));
    }

    private void paintSkyBirds(Graphics2D g2, int w, int h) {
        g2.setColor(new Color(255, 255, 255, 95));
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        drawSkyBird(g2, w * 0.11f, h * 0.13f, 9f);
        drawSkyBird(g2, w * 0.18f, h * 0.20f, 6f);
        drawSkyBird(g2, w * 0.26f, h * 0.09f, 7f);
    }

    private void drawSkyBird(Graphics2D g2, float cx, float cy, float s) {
        GeneralPath wing = new GeneralPath();
        wing.moveTo(cx - s, cy);
        wing.quadTo(cx - s * 0.5f, cy - s * 0.85f, cx, cy);
        wing.quadTo(cx + s * 0.5f, cy - s * 0.85f, cx + s, cy);
        g2.draw(wing);
    }

    private void paintClouds(Graphics2D g2, int w, int h) {
        drawCloud(g2, w * 0.16f, h * 0.15f, 1.05f, 48);
        drawCloud(g2, w * 0.52f, h * 0.09f, 0.70f, 38);
        drawCloud(g2, w * 0.85f, h * 0.31f, 1.15f, 34);
        drawCloud(g2, w * 0.34f, h * 0.33f, 0.60f, 28);
        drawCloud(g2, w * 0.68f, h * 0.49f, 0.50f, 22);
        drawCloud(g2, w * 0.08f, h * 0.55f, 0.80f, 20);
    }

    private void drawCloud(Graphics2D g2, float cx, float cy, float s, int alpha) {
        g2.setColor(new Color(255, 255, 255, alpha));
        g2.fill(new Ellipse2D.Float(cx - 58 * s, cy - 12 * s, 116 * s, 32 * s));
        g2.fill(new Ellipse2D.Float(cx - 40 * s, cy - 34 * s, 70 * s, 44 * s));
        g2.fill(new Ellipse2D.Float(cx - 6 * s, cy - 44 * s, 58 * s, 46 * s));
        g2.fill(new Ellipse2D.Float(cx + 22 * s, cy - 26 * s, 50 * s, 36 * s));
        g2.fill(new Ellipse2D.Float(cx - 12 * s, cy - 20 * s, 62 * s, 38 * s));
    }

    private void paintHills(Graphics2D g2, int baseY, int amplitude, Color color, int phase) {
        int w = getWidth();
        GeneralPath path = new GeneralPath();
        path.moveTo(0, getHeight());
        path.lineTo(0, baseY);
        for (int x = 0; x <= w; x += 16) {
            double t = (x + phase) / (double) w;
            double wave = 0.6 * Math.sin(t * Math.PI * 2.0)
                    + 0.4 * Math.sin(t * Math.PI * 4.3 + 1.1);
            path.lineTo(x, baseY - (int) (amplitude * wave));
        }
        path.lineTo(w, getHeight());
        path.closePath();
        g2.setColor(color);
        g2.fill(path);
    }

    private void paintGrassTufts(Graphics2D g2, int w, int groundY) {
        g2.setColor(new Color(96, 168, 104, 120));
        for (int x = 12; x < w; x += 34) {
            g2.drawLine(x, groundY + 4, x, groundY - 7);
            g2.drawLine(x + 9, groundY + 4, x + 9, groundY - 5);
        }
    }

    private void paintVignette(Graphics2D g2, int w, int h) {
        float radius = (float) Math.hypot(w, h) * 0.62f;
        g2.setPaint(new RadialGradientPaint(
                new Point2D.Float(w / 2f, h / 2f), radius,
                new float[]{0.60f, 1f},
                new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, 120)}));
        g2.fillRect(0, 0, w, h);
    }

    public int getSelectedBird() { return selectedBird; }
    public int getSelectedBackground() { return selectedBackground; }
    public Image getBirdImage(int index) { return birdImages[index]; }
    public Image getBackgroundImage(int index) { return backgroundImages[index]; }
    public String getBirdName(int index) { return birdNames[index]; }
    public String getBgName(int index) { return bgNames[index]; }

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
        this.playerDoc = player;
        updateComboStats();
    }

    private void updateComboStats() {
        if (playerDoc == null) {
            statsGamesLabel.setText("0");
            statsBestLabel.setText("0");
            statsTotalLabel.setText("0");
            statsLevelLabel.setText("1");
            statsTimeLabel.setText("0m 0s");
            return;
        }
        Document comboStats = playerDoc.get("comboStats", new Document());
        String comboKey = birdNames[selectedBird] + "_" + bgNames[selectedBackground];
        Document combo = comboStats.get(comboKey, new Document());
        statsGamesLabel.setText(String.valueOf(combo.getInteger("gamesPlayed", 0)));
        statsBestLabel.setText(String.valueOf(combo.getInteger("bestScore", 0)));
        statsTotalLabel.setText(String.valueOf(combo.getInteger("totalScore", 0)));
        statsLevelLabel.setText(String.valueOf(combo.getInteger("currentLevel", 1)));
        Long totalDuration = combo.getLong("totalDuration");
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
        statsBirdLabel.setText(birdNames[0]);
        statsBgLabel.setText(bgNames[0]);
        updateComboStats();
        repaint();
    }
}

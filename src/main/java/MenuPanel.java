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
    private final String[] birdNames = {"Bird 1", "Bird 2", "Bird 3", "Bird 4", "Bird 5", "Bird 6"};
    private final String[] bgNames = {"Sky", "Sunset", "Night", "City", "Forest", "Ocean"};

    private static final Color BG_COLOR = new Color(45, 52, 54);
    private static final Color ACCENT_COLOR = new Color(116, 185, 255);
    private static final Color TEXT_COLOR = new Color(255, 255, 255);
    private static final Color PANEL_BG = new Color(57, 66, 69);
    private static final Color SELECTED_BORDER = new Color(253, 203, 110);
    private static final Color FOCUS_COLOR = new Color(46, 213, 115);

    private final JLabel birdNameLabel;
    private final JLabel bgNameLabel;
    private final JLabel playerNameLabel;
    private final JLabel hintLabel;

    private final ActionListener onPlay;

    public MenuPanel(int width, int height, ActionListener onPlay) {
        this.onPlay = onPlay;
        setPreferredSize(new Dimension(width, height));
        setLayout(null);
        setBackground(BG_COLOR);
        setFocusable(true);
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

        JLabel titleLabel = new JLabel("CUSTOMIZE YOUR GAME", SwingConstants.CENTER);
        titleLabel.setBounds(0, 20, width, 45);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(ACCENT_COLOR);
        add(titleLabel);

        playerNameLabel = new JLabel("", SwingConstants.CENTER);
        playerNameLabel.setBounds(0, 60, width, 25);
        playerNameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        playerNameLabel.setForeground(new Color(178, 190, 195));
        add(playerNameLabel);

        JLabel birdTitle = new JLabel("Select Bird", SwingConstants.CENTER);
        birdTitle.setBounds(0, 100, width, 30);
        birdTitle.setFont(new Font("Arial", Font.BOLD, 22));
        birdTitle.setForeground(TEXT_COLOR);
        add(birdTitle);

        birdNameLabel = new JLabel(birdNames[selectedBird], SwingConstants.CENTER);
        birdNameLabel.setBounds(0, 265, width, 25);
        birdNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        birdNameLabel.setForeground(TEXT_COLOR);
        add(birdNameLabel);

        JLabel bgTitle = new JLabel("Select Background", SwingConstants.CENTER);
        bgTitle.setBounds(0, 310, width, 30);
        bgTitle.setFont(new Font("Arial", Font.BOLD, 22));
        bgTitle.setForeground(TEXT_COLOR);
        add(bgTitle);

        bgNameLabel = new JLabel(bgNames[selectedBackground], SwingConstants.CENTER);
        bgNameLabel.setBounds(0, 475, width, 25);
        bgNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        bgNameLabel.setForeground(TEXT_COLOR);
        add(bgNameLabel);

        hintLabel = new JLabel("Left/Right: change bird  |  Up/Down: change bg  |  Tab: switch section  |  Enter: play", SwingConstants.CENTER);
        hintLabel.setBounds(0, height - 40, width, 25);
        hintLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        hintLabel.setForeground(new Color(178, 190, 195));
        add(hintLabel);
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

        if (key == KeyEvent.VK_ENTER) {
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
        int selectorWidth = 180;

        // Bird preview box
        int birdBoxX = centerX - 90;
        int birdBoxY = 140;
        int birdBoxSize = 180;

        Color birdBorder = (focusedSection == 0) ? FOCUS_COLOR : SELECTED_BORDER;
        int birdStroke = (focusedSection == 0) ? 4 : 3;

        g2.setColor(PANEL_BG);
        g2.fillRoundRect(birdBoxX - 5, birdBoxY - 5, birdBoxSize + 10, birdBoxSize + 10, 15, 15);
        g2.setColor(birdBorder);
        g2.setStroke(new BasicStroke(birdStroke));
        g2.drawRoundRect(birdBoxX - 5, birdBoxY - 5, birdBoxSize + 10, birdBoxSize + 10, 15, 15);

        g2.drawImage(birdImages[selectedBird], birdBoxX + 40, birdBoxY + 25, 100, 100, null);

        if (focusedSection == 0) {
            g2.setColor(FOCUS_COLOR);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString("< " + birdNames[selectedBird] + " >", centerX - 45, birdBoxY + birdBoxSize + 25);
        }

        // Background preview box
        int bgBoxX = centerX - 90;
        int bgBoxY = 350;
        int bgBoxW = 180;
        int bgBoxH = 100;

        Color bgBorder = (focusedSection == 1) ? FOCUS_COLOR : SELECTED_BORDER;
        int bgStroke = (focusedSection == 1) ? 4 : 3;

        g2.setColor(PANEL_BG);
        g2.fillRoundRect(bgBoxX - 7, bgBoxY - 7, bgBoxW + 14, bgBoxH + 14, 15, 15);
        g2.setColor(bgBorder);
        g2.setStroke(new BasicStroke(bgStroke));
        g2.drawRoundRect(bgBoxX - 7, bgBoxY - 7, bgBoxW + 14, bgBoxH + 14, 15, 15);

        g2.drawImage(backgroundImages[selectedBackground], bgBoxX, bgBoxY, bgBoxW, bgBoxH, null);

        if (focusedSection == 1) {
            g2.setColor(FOCUS_COLOR);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString("< " + bgNames[selectedBackground] + " >", centerX - 55, bgBoxY + bgBoxH + 22);
        }
    }

    public int getSelectedBird() { return selectedBird; }
    public int getSelectedBackground() { return selectedBackground; }
    public Image getBirdImage(int index) { return birdImages[index]; }
    public Image getBackgroundImage(int index) { return backgroundImages[index]; }

    public void setPlayerName(String name) {
        playerNameLabel.setText("Playing as: " + name);
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

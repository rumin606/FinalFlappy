import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class App {

    private static final String LOGIN_CARD = "LOGIN";
    private static final String MENU_CARD = "MENU";
    private static final String GAME_CARD = "GAME";
    private static final String ADMIN_CARD = "ADMIN";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

            JFrame frame = new JFrame("Flappy Bird");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(true);
            frame.setMinimumSize(new Dimension(900, 640));

            Insets insets = Toolkit.getDefaultToolkit()
                    .getScreenInsets(frame.getGraphicsConfiguration());
            frame.setSize(screen.width - insets.left - insets.right,
                    screen.height - insets.top - insets.bottom);

            CardLayout cardLayout = new CardLayout();
            JPanel cardPanel = new JPanel(cardLayout);

            LoginPanel[] loginHolder = new LoginPanel[1];
            MenuPanel[] menuHolder = new MenuPanel[1];
            FlappyBird[] gameHolder = new FlappyBird[1];
            AdminPanel[] adminHolder = new AdminPanel[1];

            ActionListener onLogin = e -> {
                LoginPanel login = loginHolder[0];
                if ("admin".equals(login.getPlayerEmail()) && "admin123".equals(login.getPlayerPassword())) {
                    adminHolder[0].refresh();
                    cardLayout.show(cardPanel, ADMIN_CARD);
                    SwingUtilities.invokeLater(() -> adminHolder[0].requestFocusInWindow());
                    return;
                }
                try {
                    ScoreDatabase db = new ScoreDatabase();
                    org.bson.Document player = db.authenticatePlayer(
                            login.getPlayerEmail(), login.getPlayerPassword());
                    if (player == null) {
                        login.setError("Invalid email or password");
                        return;
                    }
                    int level = player.getInteger("currentLevel", 1);
                    login.setSessionPlayer(player.getString("name"), level);
                    menuHolder[0].setPlayerName(player.getString("name"));
                    menuHolder[0].setPlayerLevel(level);
                    menuHolder[0].setPlayerStats(player);
                } catch (Exception ex) {
                    System.err.println("MongoDB not available: " + ex.getMessage());
                    login.setError("Database unavailable. Cannot verify login. Try again.");
                    return;
                }
                cardLayout.show(cardPanel, MENU_CARD);
                SwingUtilities.invokeLater(() -> menuHolder[0].requestFocusInWindow());
            };

            ActionListener onSignup = e -> {
                LoginPanel login = loginHolder[0];
                try {
                    ScoreDatabase db = new ScoreDatabase();
                    if (db.emailExists(login.getPlayerEmail())) {
                        login.setError("Email already registered. Please login.");
                        return;
                    }
                    db.savePlayer(login.getPlayerName(), login.getPlayerEmail(),
                            login.getPlayerPhone(), login.getPlayerPassword());
                } catch (Exception ex) {
                    System.err.println("MongoDB not available: " + ex.getMessage());
                    login.setError("Database unavailable. Account not saved. Try again.");
                    return;
                }
                login.setSessionPlayer(login.getPlayerName(), 1);
                menuHolder[0].setPlayerName(login.getPlayerName());
                menuHolder[0].setPlayerLevel(1);
                menuHolder[0].setPlayerStats(null);
                cardLayout.show(cardPanel, MENU_CARD);
                SwingUtilities.invokeLater(() -> menuHolder[0].requestFocusInWindow());
            };

            ActionListener onPlay = e -> {
                LoginPanel login = loginHolder[0];
                MenuPanel menu = menuHolder[0];
                FlappyBird game = gameHolder[0];
                game.setupGame(
                        menu.getBirdImage(menu.getSelectedBird()),
                        menu.getBackgroundImage(menu.getSelectedBackground()),
                        login.getPlayerName(),
                        login.getPlayerEmail(),
                        login.getPlayerLevel(),
                        menu.getBirdName(menu.getSelectedBird()),
                        menu.getBgName(menu.getSelectedBackground())
                );
                cardLayout.show(cardPanel, GAME_CARD);
                SwingUtilities.invokeLater(() -> game.requestFocusInWindow());
            };

            ActionListener onLogout = e -> {
                loginHolder[0].clearFields();
                menuHolder[0].resetSelections();
                cardLayout.show(cardPanel, LOGIN_CARD);
                SwingUtilities.invokeLater(() -> loginHolder[0].focusFirstField());
            };

            LoginPanel loginPanel = new LoginPanel(screen.width, screen.height, onLogin, onSignup);
            loginHolder[0] = loginPanel;

            MenuPanel menuPanel = new MenuPanel(screen.width, screen.height, onPlay, onLogout);
            menuHolder[0] = menuPanel;

            FlappyBird gamePanel = new FlappyBird();
            gameHolder[0] = gamePanel;

            AdminPanel adminPanel = new AdminPanel(screen.width, screen.height, e -> {
                cardLayout.show(cardPanel, MENU_CARD);
                SwingUtilities.invokeLater(() -> menuHolder[0].requestFocusInWindow());
            });
            adminHolder[0] = adminPanel;

            gamePanel.setOnGameOverCallback(() -> {
                SwingUtilities.invokeLater(() -> {
                    FlappyBird game = gameHolder[0];
                    String[] options = {"Return to Menu", "Quit"};
                    int choice = JOptionPane.showOptionDialog(
                            frame,
                            "Game Over!\nPlayer: " + game.playerName +
                                    "\nLevel: " + game.currentLevel +
                                    "\nScore: " + (int) game.score +
                                    "\nDuration: " + (game.gameDurationMs / 1000) + "s",
                            "Game Over",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            null,
                            options,
                            options[0]
                    );
                    if (choice == 0) {
                        menuHolder[0].resetSelections();
                        try {
                            ScoreDatabase db = new ScoreDatabase();
                            menuHolder[0].setPlayerStats(db.getPlayer(game.playerEmail));
                        } catch (Exception ex) {
                            System.err.println("Failed to refresh stats: " + ex.getMessage());
                        }
                        cardLayout.show(cardPanel, MENU_CARD);
                        SwingUtilities.invokeLater(() -> menuHolder[0].requestFocusInWindow());
                    } else {
                        System.exit(0);
                    }
                });
            });

            cardPanel.add(loginPanel, LOGIN_CARD);
            cardPanel.add(menuPanel, MENU_CARD);
            cardPanel.add(gamePanel, GAME_CARD);
            cardPanel.add(adminPanel, ADMIN_CARD);

            frame.add(cardPanel);
            frame.setLocation(0, 0);
            frame.setVisible(true);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

            cardLayout.show(cardPanel, LOGIN_CARD);
            SwingUtilities.invokeLater(() -> loginPanel.focusFirstField());
        });
    }
}

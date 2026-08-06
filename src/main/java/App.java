import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class App {

    private static final int WIDTH = 700;
    private static final int HEIGHT = 640;

    private static final String LOGIN_CARD = "LOGIN";
    private static final String MENU_CARD = "MENU";
    private static final String GAME_CARD = "GAME";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Flappy Bird");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            CardLayout cardLayout = new CardLayout();
            JPanel cardPanel = new JPanel(cardLayout);

            LoginPanel[] loginHolder = new LoginPanel[1];
            MenuPanel[] menuHolder = new MenuPanel[1];
            FlappyBird[] gameHolder = new FlappyBird[1];

            ActionListener onLogin = e -> {
                LoginPanel login = loginHolder[0];
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
                } catch (Exception ex) {
                    System.err.println("MongoDB not available: " + ex.getMessage());
                    login.setSessionPlayer(login.getPlayerEmail(), 1);
                    menuHolder[0].setPlayerName(login.getPlayerEmail());
                    menuHolder[0].setPlayerLevel(1);
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
                }
                login.setSessionPlayer(login.getPlayerName(), 1);
                menuHolder[0].setPlayerName(login.getPlayerName());
                menuHolder[0].setPlayerLevel(1);
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
                        login.getPlayerLevel()
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

            ActionListener onGoToLogin = e -> {
                loginHolder[0].clearFields();
                loginHolder[0].switchToLogin();
                menuHolder[0].resetSelections();
                cardLayout.show(cardPanel, LOGIN_CARD);
                SwingUtilities.invokeLater(() -> loginHolder[0].focusFirstField());
            };

            ActionListener onGoToSignup = e -> {
                loginHolder[0].clearFields();
                loginHolder[0].switchToSignup();
                menuHolder[0].resetSelections();
                cardLayout.show(cardPanel, LOGIN_CARD);
                SwingUtilities.invokeLater(() -> loginHolder[0].focusFirstField());
            };

            LoginPanel loginPanel = new LoginPanel(WIDTH, HEIGHT, onLogin, onSignup);
            loginHolder[0] = loginPanel;

            MenuPanel menuPanel = new MenuPanel(WIDTH, HEIGHT, onPlay, onLogout, onGoToLogin, onGoToSignup);
            menuHolder[0] = menuPanel;

            FlappyBird gamePanel = new FlappyBird();
            gameHolder[0] = gamePanel;

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

            frame.add(cardPanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            cardLayout.show(cardPanel, LOGIN_CARD);
            SwingUtilities.invokeLater(() -> loginPanel.focusFirstField());
        });
    }
}

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import org.bson.Document;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    int bwidth = 700;
    int bheight = 700;

    Image backimg;
    Image birdimg;
    Image topimg;
    Image bottomimg;

    int birdx = bwidth / 8;
    int birdy = bheight / 2;
    int birdwidth = 50;
    int birdheight = 50;

    class Bird {
        int x = birdx;
        int y = birdy;
        int width = birdwidth;
        int height = birdheight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    int pipeX = bwidth;
    int pipeY = 0;
    int pipewidth = 100;
    int pipeheight = 500;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipewidth;
        int height = pipeheight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    Bird bird;

    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;

    public static final int SCORE_PER_LEVEL = 5;
    private static final int COUNTDOWN_SECONDS = 3;
    int currentLevel = 1;
    int levelUpFlash = 0;

    ArrayList<Pipe> pipes;

    Timer gameloop;
    Timer placePipeTimer;

    boolean gameStarted = false;
    boolean gameover = false;

    boolean countdownActive = false;
    int countdown = 3;

    double score = 0;

    String playerName = "";
    String playerEmail = "";

    ScoreDatabase scoreDb;
    boolean scoreSaved = false;

    long gameStartTime;
    long gameDurationMs;

    private Runnable onGameOverCallback;

    FlappyBird() {
        setPreferredSize(new Dimension(bwidth, bheight));
        setLayout(null);
        setFocusable(true);
        addKeyListener(this);

        topimg = new ImageIcon(getClass().getResource("/images/top.png")).getImage();
        bottomimg = new ImageIcon(getClass().getResource("/images/botom.png")).getImage();

        bird = new Bird(new ImageIcon(getClass().getResource("/images/bird1.png")).getImage());
        pipes = new ArrayList<>();

        placePipeTimer = new Timer(1500, e -> placePipes());

        try {
            scoreDb = new ScoreDatabase();
        } catch (Exception e) {
            System.err.println("MongoDB not available: " + e.getMessage());
            scoreDb = null;
        }
    }

    public void setOnGameOverCallback(Runnable callback) {
        this.onGameOverCallback = callback;
    }

    public void setupGame(Image birdImg, Image bgImg, String name, String email, int level) {
        this.birdimg = birdImg;
        this.backimg = bgImg;
        this.playerName = name;
        this.playerEmail = email;

        this.currentLevel = Math.max(1, level);
        velocityX = pipeSpeedForLevel(currentLevel);

        bird.x = birdx;
        bird.y = birdy;
        bird.img = birdimg;
        velocityY = 0;
        pipes.clear();
        score = 0;
        levelUpFlash = 0;
        gameover = false;
        gameStarted = true;
        scoreSaved = false;

        countdownActive = true;
        countdown = COUNTDOWN_SECONDS;

        gameloop = new Timer(1000 / 60, this);
        gameloop.start();
        gameStartTime = System.currentTimeMillis();
        requestFocusInWindow();
    }

    private int pipeSpeedForLevel(int level) {
        return -Math.min(3 + level, 10);
    }

    private int openingSpaceForLevel(int level) {
        return Math.max(bheight / 6, bheight / 4 - (level - 1) * 8);
    }

    private int levelForScore(double s) {
        return (int) (s / SCORE_PER_LEVEL) + 1;
    }

    public void placePipes() {
        int randomPipeY = (int) (pipeY - pipeheight / 4 - Math.random() * (pipeheight / 2));
        int openingSpace = openingSpaceForLevel(currentLevel);

        Pipe topPipe = new Pipe(topimg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomimg);
        bottomPipe.y = topPipe.y + pipeheight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void draw(Graphics g) {
        g.drawImage(backimg, 0, 0, bwidth, bheight, null);

        if (!gameStarted) return;

        // Bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        // Pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
            if (pipe.x + pipe.width < 0) {
                pipes.remove(i);
                i--;
            }
        }

        // Score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 32));

        if (countdownActive) {
            g.setColor(new Color(0, 0, 0, 120));
            g.fillRect(0, 0, bwidth, bheight);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 34));
            g.drawString("Get Ready!", 235, bheight / 2 - 40);

            g.setFont(new Font("Arial", Font.BOLD, 60));
            g.drawString("" + Math.max(countdown, 0), bwidth / 2 - 20, bheight / 2 + 40);
        }

        if (gameover) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, bwidth, bheight);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("Game Over", 230, 150);

            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString(playerName + " | Level " + currentLevel + " | Score: " + (int) score, 140, 200);

            long seconds = gameDurationMs / 1000;
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Duration: " + seconds + "s", 250, 240);

            g.drawString("Press Space to Restart", 210, 290);

            if (scoreDb != null) {
                try {
                    List<Document> topScores = scoreDb.getTopScores(3);
                    if (!topScores.isEmpty()) {
                        g.setFont(new Font("Arial", Font.BOLD, 22));
                        g.drawString("Leaderboard:", 240, 340);
                        g.setFont(new Font("Arial", Font.PLAIN, 18));
                        int y = 370;
                        for (int i = 0; i < topScores.size(); i++) {
                            Document entry = topScores.get(i);
                            String name = entry.getString("playerName");
                            int s = entry.getInteger("score", 0);
                            g.drawString((i + 1) + ". " + name + " - " + s, 260, y);
                            y += 28;
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Failed to load scores: " + ex.getMessage());
                }
            }
        } else {
            g.drawString("" + (int) score, 20, 50);

            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.setColor(new Color(116, 185, 255));
            g.drawString("LEVEL " + currentLevel, 20, 78);

            if (levelUpFlash > 0) {
                g.setFont(new Font("Arial", Font.BOLD, 40));
                g.setColor(new Color(253, 203, 110));
                g.drawString("LEVEL UP!", 235, bheight / 2 - 40);
                g.setFont(new Font("Arial", Font.BOLD, 22));
                g.drawString("Level " + currentLevel, 295, bheight / 2);
                levelUpFlash--;
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void move() {
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5;

                int newLevel = levelForScore(score);
                if (newLevel > currentLevel) {
                    currentLevel = newLevel;
                    velocityX = pipeSpeedForLevel(currentLevel);
                    levelUpFlash = 45;
                    if (scoreDb != null) {
                        try {
                            scoreDb.saveLevel(playerEmail, currentLevel);
                        } catch (Exception ex) {
                            System.err.println("Failed to save level: " + ex.getMessage());
                        }
                    }
                }
            }

            if (collision(bird, pipe)) {
                gameover = true;
            }
        }

        if (bird.y > bheight) {
            gameover = true;
        }
    }

    public boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (countdownActive) {
            long elapsed = (System.currentTimeMillis() - gameStartTime) / 1000;
            countdown = (int) (COUNTDOWN_SECONDS - elapsed);
            if (countdown <= 0) {
                countdownActive = false;
                placePipeTimer.start();
            }
        } else {
            move();
        }
        repaint();

        if (gameover) {
            gameloop.stop();
            placePipeTimer.stop();
            gameDurationMs = System.currentTimeMillis() - gameStartTime;

            if (!scoreSaved && scoreDb != null) {
                scoreSaved = true;
                try {
                    scoreDb.saveScore(playerName, playerEmail, score, gameDurationMs);
                    scoreDb.saveLevel(playerEmail, currentLevel);
                } catch (Exception ex) {
                    System.err.println("Failed to save score: " + ex.getMessage());
                }
            }

            if (onGameOverCallback != null) {
                onGameOverCallback.run();
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameStarted) return;
        if (countdownActive) return;

        if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_ENTER) {
            velocityY = -11;

            if (gameover) {
                bird.y = birdy;
                velocityY = 0;
                pipes.clear();
                score = 0;
                levelUpFlash = 0;
                velocityX = pipeSpeedForLevel(currentLevel);
                gameover = false;
                scoreSaved = false;

                countdownActive = true;
                countdown = COUNTDOWN_SECONDS;

                gameloop.start();
                gameStartTime = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}

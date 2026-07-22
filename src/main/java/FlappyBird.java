import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import org.bson.Document;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    int bwidth = 700;
    int bheight = 640;

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

    ArrayList<Pipe> pipes;

    Timer gameloop;
    Timer placePipeTimer;

    boolean gameStarted = false;
    boolean gameover = false;

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

    public void setupGame(Image birdImg, Image bgImg, String name, String email) {
        this.birdimg = birdImg;
        this.backimg = bgImg;
        this.playerName = name;
        this.playerEmail = email;

        bird.x = birdx;
        bird.y = birdy;
        bird.img = birdimg;
        velocityY = 0;
        pipes.clear();
        score = 0;
        gameover = false;
        gameStarted = true;
        scoreSaved = false;

        gameloop = new Timer(1000 / 60, this);
        gameloop.start();
        placePipeTimer.start();
        gameStartTime = System.currentTimeMillis();
        requestFocusInWindow();
    }

    public void placePipes() {
        int randomPipeY = (int) (pipeY - pipeheight / 4 - Math.random() * (pipeheight / 2));
        int openingSpace = bheight / 4;

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

        if (gameover) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, bwidth, bheight);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("Game Over", 230, 150);

            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString(playerName + " - Score: " + (int) score, 200, 200);

            long seconds = gameDurationMs / 1000;
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Duration: " + seconds + "s", 250, 240);

            g.drawString("Press Space to Restart", 210, 290);

            if (scoreDb != null) {
                try {
                    List<Document> topScores = scoreDb.getTopScores(5);
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
        move();
        repaint();

        if (gameover) {
            gameloop.stop();
            placePipeTimer.stop();
            gameDurationMs = System.currentTimeMillis() - gameStartTime;

            if (!scoreSaved && scoreDb != null) {
                scoreSaved = true;
                try {
                    scoreDb.saveScore(playerName, playerEmail, score, gameDurationMs);
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

        if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_UP) {
            velocityY = -9;

            if (gameover) {
                bird.y = birdy;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameover = false;
                scoreSaved = false;
                gameloop.start();
                placePipeTimer.start();
                gameStartTime = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}

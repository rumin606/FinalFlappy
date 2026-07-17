import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import org.bson.Document;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    int bwidth = 700;
    int bheight = 640;

    // Images
    Image backimg;
    Image birdimg;
    Image topimg;
    Image bottomimg;

    Image[] birdImages;
    Image[] backgroundImages;

    int selectedBird = 0;
    int selectedBackground = 0;

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
    boolean menuScreen = true;

    double score = 0;

    JTextField nameField;
    String playerName = "";

    ScoreDatabase scoreDb;
    boolean scoreSaved = false;

    FlappyBird() {

        setPreferredSize(new Dimension(bwidth, bheight));
        setLayout(null);
        setFocusable(true);
        addKeyListener(this);

        nameField = new JTextField("Enter your name");
        nameField.setBounds(200, 380, 300, 40);
        nameField.setFont(new Font("Arial", Font.PLAIN, 18));
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setToolTipText("Enter your player name");
        add(nameField);

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

        topimg = new ImageIcon(getClass().getResource("/images/top.png")).getImage();
        bottomimg = new ImageIcon(getClass().getResource("/images/botom.png")).getImage();

        birdimg = birdImages[selectedBird];
        backimg = backgroundImages[selectedBackground];

        bird = new Bird(birdimg);

        pipes = new ArrayList<>();

        placePipeTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });

        try {
            scoreDb = new ScoreDatabase();
        } catch (Exception e) {
            System.err.println("MongoDB not available: " + e.getMessage());
            scoreDb = null;
        }
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

        if (menuScreen) {
            nameField.setVisible(true);

            g.setColor(Color.black);

            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("FLAPPY BIRD", 250, 100);

            g.setFont(new Font("Arial", Font.BOLD, 20));

            g.drawString("LEFT / RIGHT : Change Bird", 225, 200);
            g.drawString("UP / DOWN : Change Background", 200, 250);
            g.drawString("Space : Start Game", 270, 320);

            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.drawString("Player Name:", 285, 370);

            g.drawImage(birdImages[selectedBird], 130, 420, 80, 80, null);

            return;
        }

        nameField.setVisible(false);

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
            g.drawString("Game Over : " + playerName + " - " + (int) score, 50, 80);
            g.drawString("Press Space to Restart", 50, 120);

            if (scoreDb != null) {
                try {
                    List<Document> topScores = scoreDb.getTopScores(5);
                    if (!topScores.isEmpty()) {
                        g.setFont(new Font("Arial", Font.BOLD, 24));
                        g.drawString("Leaderboard:", 50, 170);
                        g.setFont(new Font("Arial", Font.PLAIN, 20));
                        int y = 200;
                        for (int i = 0; i < topScores.size(); i++) {
                            Document entry = topScores.get(i);
                            String name = entry.getString("playerName");
                            int s = entry.getInteger("score", 0);
                            g.drawString((i + 1) + ". " + name + " - " + s, 60, y);
                            y += 25;
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

    // Paint
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    // Move
    public void move() {

        velocityY += gravity;
        bird.y += velocityY;

        bird.y = Math.max(bird.y, 0);

        for (int i = 0; i < pipes.size(); i++) {

            Pipe pipe = pipes.get(i);

            pipe.x += velocityX;

            // Score
            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5;
            }

            // Collision
            if (collision(bird, pipe)) {
                gameover = true;
            }
        }

        // Ground Collision
        if (bird.y > bheight) {
            gameover = true;
        }
    }

    // Collision Detection
    public boolean collision(Bird a, Pipe b) {

        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    // Timer Action
    @Override
    public void actionPerformed(ActionEvent e) {

        move();

        repaint();

        if (gameover) {
            gameloop.stop();
            placePipeTimer.stop();

            if (!scoreSaved && scoreDb != null) {
                scoreSaved = true;
                try {
                    scoreDb.saveScore(playerName, score);
                } catch (Exception ex) {
                    System.err.println("Failed to save score: " + ex.getMessage());
                }
            }
        }
    }

    // Keyboard Controls
    @Override
    public void keyPressed(KeyEvent e) {

        // MENU CONTROLS
        if (menuScreen) {

            // Change Bird
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {

                selectedBird++;

                if (selectedBird >= birdImages.length) {
                    selectedBird = 0;
                }
            }

            if (e.getKeyCode() == KeyEvent.VK_LEFT) {

                selectedBird--;

                if (selectedBird < 0) {
                    selectedBird = birdImages.length - 1;
                }
            }

            // Change Background
            if (e.getKeyCode() == KeyEvent.VK_UP) {

                selectedBackground++;

                if (selectedBackground >= backgroundImages.length) {
                    selectedBackground = 0;
                }
            }

            if (e.getKeyCode() == KeyEvent.VK_DOWN) {

                selectedBackground--;

                if (selectedBackground < 0) {
                    selectedBackground = backgroundImages.length - 1;
                }
            }

            // Update Images
            birdimg = birdImages[selectedBird];
            backimg = backgroundImages[selectedBackground];

            // Start Game
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {

                playerName = nameField.getText().trim();
                if (playerName.isEmpty()) {
                    playerName = "Player";
                }

                menuScreen = false;
                gameStarted = true;

                bird.img = birdimg;

                gameloop = new Timer(1000 / 60, this);
                gameloop.start();

                placePipeTimer.start();
                requestFocusInWindow();
            }

            repaint();
            return;
        }

        // GAME CONTROLS
        if (e.getKeyCode() == KeyEvent.VK_SPACE ||
                e.getKeyCode() == KeyEvent.VK_UP) {

            velocityY = -9;

            // Restart Game
            if (gameover) {

                bird.y = birdy;
                velocityY = 0;

                pipes.clear();

                score = 0;
                gameover = false;
                scoreSaved = false;

                gameloop.start();
                placePipeTimer.start();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
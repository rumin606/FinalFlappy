import javax.swing.*;

public class App {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Flappy Bird");
        FlappyBird flappybird = new FlappyBird();
        frame.add(flappybird);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        flappybird.requestFocus();
    }
}
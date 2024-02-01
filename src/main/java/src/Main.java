package src;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Shooter Game");
        SampleShooterGame gamePanel = new SampleShooterGame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(Main.class.getResource("/Picture1.png")));
        Image image = imageIcon.getImage();
        frame.setIconImage(image);
        frame.setVisible(true);
    }
}

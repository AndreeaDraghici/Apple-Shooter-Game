package org.example;

/**
 * Created by Andreea Draghici on 1/27/2024
 * Name of project: Default (Template) Project
 */
public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Shooter Game");
        SampleShooterGame gamePanel = new SampleShooterGame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

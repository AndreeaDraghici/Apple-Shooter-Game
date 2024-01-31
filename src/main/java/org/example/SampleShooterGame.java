package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import javax.imageio.ImageIO;

import static org.example.Utils.*;

public class SampleShooterGame extends JPanel implements ActionListener, KeyListener, MouseListener  {

    private Timer timer;
    private int playerImageWidth = PLAYER_SIZE;
    private int playerImageHeight = PLAYER_SIZE + 20;
    private int playerX = WINDOW_WIDTH / 2 - PLAYER_SIZE / 2;
    private boolean moveLeft = false;
    private boolean moveRight = false;
    private boolean shooting = false;
    private boolean isNewRecord = false;
    private Rectangle startOverButton;
    private int score = 0; // Variabila pentru a ține evidența scorului
    private int currentGameScore = 0; // Cel mai bun scor realizat
    private int highestScore = 0;
    private ArrayList<Rectangle> projectiles = new ArrayList<>();
    private ArrayList<Rectangle> enemies = new ArrayList<>();
    private int frames = 0;

    private boolean gameOver = false; // Flag pentru a verifica dacă jocul s-a terminat
    private boolean gameStarted = false;
    private JButton startButton;
    private Image playerImage;
    private Image enemyImage;
    private Image shooterImage;

    public SampleShooterGame() {
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        setLayout(null);
        // Setăm dimensiunile butonului
        int buttonWidth = 150;
        int buttonHeight = 40;
        startButton = new JButton("Start Game");
        // Calculăm poziția centrală pentru buton
        int buttonX = WINDOW_WIDTH / 2 - buttonWidth / 2;
        int buttonY = WINDOW_HEIGHT / 2 - buttonHeight / 2;

        startButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
        add(startButton);

        startOverButton = new Rectangle(WINDOW_WIDTH / 4, WINDOW_HEIGHT / 2 + 50, 200, 40);
        addMouseListener(this);

        try {
            playerImage = ImageIO.read(getClass().getResource("/otter.png"));
            shooterImage = ImageIO.read(getClass().getResource("/appleShooter.png"));
            enemyImage = ImageIO.read(getClass().getResource("/ice.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        timer = new Timer(1000 / 60, this);
        timer.start();
        startButton.addActionListener(e -> {
            remove(startButton); // Eliminăm butonul după ce este apăsat
            gameStarted = true;
            timer.start(); // Pornim timer-ul abia după ce butonul este apăsat
            requestFocusInWindow(); // Solicităm focusul pentru a primi evenimentele de la tastatură
        });

    }
    private void setAllFonts(Component component, Font font) {
        component.setFont(font);
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                setAllFonts(child, font);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!gameStarted) {
            // The start button is managed by Swing, so no need to draw it here.
            // Just return to avoid drawing game components.
            return;
        }
        if (!gameOver) {
            //super.paintComponent(g);
            g.drawImage(playerImage, playerX, WINDOW_HEIGHT - 10 - playerImageHeight, playerImageWidth, playerImageHeight, this);

            g.setColor(Color.YELLOW);
            for (Rectangle projectile : projectiles) {
                g.drawImage(shooterImage, projectile.x, projectile.y, PROJECTILE_SIZE, PROJECTILE_SIZE, this);
            }

            g.setColor(Color.WHITE);
            for (Rectangle enemy : enemies) {
                g.drawImage(enemyImage, enemy.x, enemy.y, ENEMY_SIZE, ENEMY_SIZE, this);
            }

            // Desenăm scorul
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Scor: " + score, 10, 20);
        } else {
            drawGameOverScreen(g);
        }
    }

    private void drawGameOverScreen(Graphics g) {
        // Dimensiunile și poziția cadrului negru
        int x = 50; // distanța de la marginea ferestrei
        int y = WINDOW_HEIGHT / 4; // începe la un sfert din înălțimea ferestrei
        int width = WINDOW_WIDTH - 100; // lățimea este lățimea ferestrei minus 100
        int height = (WINDOW_HEIGHT / 2)-100; // înălțimea este jumătate din înălțimea ferestrei
        int arcWidth = 20; // lățimea arcului pentru margini rotunjite
        int arcHeight = 20; // înălțimea arcului pentru margini rotunjite

        // Convertim codul de culoare hexazecimal într-un obiect Color
        Color backgroundColor = new Color(0x17100B);

        // Setăm culoarea de fundal
        g.setColor(backgroundColor);
        g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);

        // Setăm culoarea și fontul pentru text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));

        // Apelăm metodele existente pentru a desena textele în cadran
        setMessageGameOver(g);
        setStartGameOver(g);
        setMessageGameScore(g);
        getCurrentScorOfGame(g);
        getHighestScoreOfGame(g);
    }

    private void getHighestScoreOfGame(Graphics g) {
        highestScore = getHighestScoreFromFile();
        Font highScoreFont = new Font("Arial", Font.BOLD,30);
        g.setFont(highScoreFont);
        String highestScoreText = "The highest score: " + highestScore;
        int highestScoreWidth = g.getFontMetrics(highScoreFont).stringWidth(highestScoreText);
        int highestScoreX = (WINDOW_WIDTH - highestScoreWidth) / 2;
        g.setColor(Color.WHITE);
        g.drawString(highestScoreText, highestScoreX, WINDOW_HEIGHT / 2 - 120);
    }

    private void getCurrentScorOfGame(Graphics g) {
        Font currentGameScoreFont = new Font("Arial", Font.BOLD, 20);
        g.setFont(currentGameScoreFont);
        String currentGameScoreText = "The Current Game Score score: " + currentGameScore;
        int currentGameScoreWidth = g.getFontMetrics(currentGameScoreFont).stringWidth(currentGameScoreText);
        int highScoreX = (WINDOW_WIDTH - currentGameScoreWidth) / 2;
        g.setColor(Color.WHITE);
        g.drawString(currentGameScoreText, highScoreX, WINDOW_HEIGHT / 2 - 80);
    }

    private static void setStartGameOver(Graphics g) {
        Font startOverFont = new Font("Arial", Font.BOLD, 50);
        g.setFont(startOverFont);
        String startOverText = "Start Game";
        int startOverWidth = g.getFontMetrics(startOverFont).stringWidth(startOverText);
        int startOverX = (WINDOW_WIDTH - startOverWidth) / 2;
        g.setColor(Color.GREEN);
        g.drawString(startOverText, startOverX, WINDOW_HEIGHT / 2 + 70);
    }

    private static void setMessageGameOver(Graphics g) {
        Font gameOverFont = new Font("Arial", Font.BOLD, 72);
        g.setFont(gameOverFont);
        String gameOverText = "GAME OVER";
        int gameOverWidth = g.getFontMetrics(gameOverFont).stringWidth(gameOverText);
        int gameOverX = (WINDOW_WIDTH - gameOverWidth) / 2;
        g.setColor(Color.RED);
        g.drawString(gameOverText, gameOverX, WINDOW_HEIGHT / 2);
    }

    private void setMessageGameScore(Graphics g) {
        if (highestScore == currentGameScore) {
            String newRecordText = "Congratulations! You set a new record!";
            Font newRecordFont = new Font("Arial", Font.BOLD, 20);
            g.setFont(newRecordFont);
            int newRecordWidth = g.getFontMetrics(newRecordFont).stringWidth(newRecordText);
            int newRecordX = (WINDOW_WIDTH - newRecordWidth) / 2;
            g.setColor(Color.ORANGE);
            g.drawString(newRecordText, newRecordX, WINDOW_HEIGHT / 2 - 170);
        }
        else {
            String m = "Keep trying!";
            Font newRecordFont = new Font("Arial", Font.BOLD, 20);
            g.setFont(newRecordFont);
            int newRecordWidth = g.getFontMetrics(newRecordFont).stringWidth(m);
            int newRecordX = (WINDOW_WIDTH - newRecordWidth) / 2;
            g.setColor(Color.ORANGE);
            g.drawString(m, newRecordX, WINDOW_HEIGHT / 2 - 170);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {

            if (moveLeft) playerX -= 5;
            if (moveRight) playerX += 5;
            playerX = Math.max(0, Math.min(WINDOW_WIDTH - PLAYER_SIZE, playerX));

            if (shooting && frames % 15 == 0) { // Trage la fiecare 15 cadre
                projectiles.add(new Rectangle(playerX + PLAYER_SIZE / 2 - PROJECTILE_SIZE / 2, WINDOW_HEIGHT - 60, PROJECTILE_SIZE, PROJECTILE_SIZE));
            }

            // Mișcarea proiectilelor
            for (Rectangle projectile : projectiles) {
                projectile.y -= 10;
            }

            // Elimină proiectilele care ies din ecran
            projectiles.removeIf(projectile -> projectile.y + PROJECTILE_SIZE < 0);

            // Generarea inamicilor
            if (frames % ENEMY_SPAWN_RATE == 0) {
                int enemyX = (int) (Math.random() * (WINDOW_WIDTH - ENEMY_SIZE));
                enemies.add(new Rectangle(enemyX, 0, ENEMY_SIZE, ENEMY_SIZE));
            }

            // Mișcarea inamicilor
            for (Rectangle enemy : enemies) {
                enemy.y += 5;
            }

            // Verifică coliziunile
            checkCollisions();

            // Actualizează și redesenează
            frames++;
            repaint();
        }
    }

    private void checkCollisions() {
        Iterator<Rectangle> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Rectangle enemy = enemyIterator.next();
            Iterator<Rectangle> projectileIterator = projectiles.iterator();
            while (projectileIterator.hasNext()) {
                Rectangle projectile = projectileIterator.next();
                Rectangle expandedEnemy = new Rectangle(
                        enemy.x - Utils.HIT_TOLERANCE,
                        enemy.y - Utils.HIT_TOLERANCE,
                        enemy.width + 2 * Utils.HIT_TOLERANCE,
                        enemy.height + 2 * Utils.HIT_TOLERANCE
                );
                if (expandedEnemy.intersects(projectile)) {
                    projectileIterator.remove();
                    enemyIterator.remove();
                    score += 10; // Incrementăm scorul pentru fiecare inamic lovit
                    break;
                }
            }
        }

        // Verificăm coliziunea dintre inamici și jucător
        Rectangle playerRect = new Rectangle(playerX, WINDOW_HEIGHT - playerImageHeight + 25, playerImageWidth, playerImageHeight);
        for (Rectangle enemy : enemies) {
            if (playerRect.intersects(enemy)) {
                gameOver = true; // Setăm flag-ul gameOver pe true
                currentGameScore = score;
                saveScoreToFile(score);
                timer.stop(); // Oprim timer-ul pentru a opri actualizarea jocului

                highestScore = getHighestScoreFromFile();
                if (score > highestScore) {
                    //isNewRecord = false;
                    isNewRecord = true;
                } else {
                   // isNewRecord = true;
                    isNewRecord = false;
                }
                System.out.println(isNewRecord);
                break;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> moveLeft = true;
            case KeyEvent.VK_RIGHT -> moveRight = true;
            case KeyEvent.VK_SPACE -> shooting = true;
            case KeyEvent.VK_R -> {
                if (gameOver) {
                    restartGame(); // Restart the game if it's over
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> moveLeft = false;
            case KeyEvent.VK_RIGHT -> moveRight = false;
            case KeyEvent.VK_SPACE -> shooting = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Această metodă nu este utilizată, dar trebuie să fie prezentă din cauza implementării KeyListener
    }

    public void setPlayerImageSize(int width, int height) {
        playerImageWidth = width;
        playerImageHeight = height;
    }
    private void saveScoreToFile(int score) {
        try (FileWriter writer = new FileWriter("game_scores.txt", true)) {
            writer.write("Score: " + score + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getHighestScoreFromFile() {
        int highestScore = 0;
        try (Scanner scanner = new Scanner(new File("game_scores.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                try {
                    int scoreFromFile = Integer.parseInt(line.replace("Score: ", ""));
                    if (scoreFromFile > highestScore) {
                        highestScore = scoreFromFile;
                    }
                } catch (NumberFormatException e) {
                    // Ignoră liniile care nu pot fi parsate în numere
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return highestScore;
    }

    private void restartGame() {
        // Resetați toate variabilele la starea inițială
        currentGameScore = Math.max(currentGameScore, score);
        playerX = WINDOW_WIDTH / 2 - PLAYER_SIZE / 2;
        score = 0;
        projectiles.clear();
        enemies.clear();
        frames = 0;
        isNewRecord = false;
        gameOver = false;
        timer.start();
    }
    // Metodele MouseListener
    @Override
    public void mouseClicked(MouseEvent e) {
        if (gameOver) {
            int x = e.getX();
            int y = e.getY();
            if (startOverButton.contains(x, y)) {
                restartGame();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}

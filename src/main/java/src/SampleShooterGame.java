package src;

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

import static src.Constants.*;

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
    private String gameRulesText;
    private boolean gameOver = false; // Flag pentru a verifica dacă jocul s-a terminat
    private boolean gameStarted = false;
    private JButton startHardButton;
    private boolean hardMode = false;
    private JButton startButton;
    private Image playerImage;
    private Image enemyImage;
    private Image shooterImage;
    private Image backgroundImage;
    private static final String NORMAL_MODE_SCORES_FILE = "game_scores.txt";
    private static final String HARD_MODE_SCORES_FILE = "game_scores_hard.txt";

    public SampleShooterGame() {
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        setLayout(null);

        // Setăm dimensiunile butonului
        int buttonWidth = 180;
        int buttonHeight = 50;
        startButton = new RoundedButton("Start Game", new Color(85, 176, 222,220), 25);
        // Calculăm poziția centrală pentru buton
        int buttonX = WINDOW_WIDTH / 2 - buttonWidth / 2;
        int buttonY = WINDOW_HEIGHT / 2 - buttonHeight + 390;

        startButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
        add(startButton);

        int buttonWidth2 = 250;
        startHardButton = new RoundedButton("Start Game Hard", new Color(85, 176, 222, 220), 25);
        int hardButtonX = buttonX-30; // Poziția X rămâne la fel
        int hardButtonY = buttonY + buttonHeight - 110; // Y este puțin sub butonul de start

        startHardButton.setBounds(hardButtonX, hardButtonY, buttonWidth2, buttonHeight);
        add(startHardButton);

        JLabel rulesLabel = getjLabelGame();

        startOverButton = new Rectangle(WINDOW_WIDTH / 4, WINDOW_HEIGHT / 2 + 50, 200, 40);
        addMouseListener(this);

        try {
            playerImage = ImageIO.read(getClass().getResource("/otter.png"));
            shooterImage = ImageIO.read(getClass().getResource("/appleShooter.png"));
            enemyImage = ImageIO.read(getClass().getResource("/ice.png"));
            backgroundImage = ImageIO.read(getClass().getResource("/game2.png")); // Încarcă imaginea de fundal
        } catch (IOException e) {
            e.printStackTrace();
        }

        timer = new Timer(1000 / 60, this);
        timer.start();
        startButton.addActionListener(e -> {
            remove(startButton); // Eliminăm butonul după ce este apăsat
            remove(rulesLabel);
            remove(startHardButton);
            gameStarted = true;
            timer.start(); // Pornim timer-ul abia după ce butonul este apăsat
            requestFocusInWindow(); // Solicităm focusul pentru a primi evenimentele de la tastatură
        });
        startHardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(startButton); // Eliminăm butonul de start
                remove(startHardButton); // Eliminăm butonul de start hard
                remove(rulesLabel); // Eliminăm eticheta cu reguli
                hardMode = true; // Setăm modul dificil pe true
                gameStarted = true;
                timer.start(); // Pornim timer-ul
                requestFocusInWindow(); // Solicităm focusul
            }
        });
    }

    private JLabel getjLabelGame() {
        int imageWidth = 60; // Lățimea dorită pentru imagine
        int imageHeight = 30; // Înălțimea dorită pentru imagine
        int imageWidthR = 40; // Lățimea dorită pentru imagine
        int imageHeightR = 30; // Înălțimea dorită pentru imagine
        gameRulesText = "<html>Game rules:<br>" +
                "<br>" +
                "Use the <img src='" + getClass().getResource("/keys.png") + "' width='" + imageWidth + "' height='" + imageHeight + "'> arrow keys to move the character.<br>" +
                "<br>" +
                "Press <img src='" + getClass().getResource("/space.png") + "' width='" + imageWidth + "' height='" + imageHeight + "'> space button to shoot apples.<br>" +
                "<br>" +
                "You can restart the game by pressing the <img src='" + getClass().getResource("/r-key.png") + "' width='" + imageWidthR + "' height='" + imageHeightR + "'> key.</html>";

        JLabel rulesLabel = new JLabel(gameRulesText);
        rulesLabel.setForeground(Color.WHITE);
        rulesLabel.setFont(new Font("Arial", Font.BOLD, 16));

        int labelWidth = 400;
        int labelHeight = 250;
        int labelX = WINDOW_WIDTH / 2 - labelWidth / 2 - 180;
        int labelY = WINDOW_HEIGHT / 2 - labelHeight / 2 - 300;  // puțin deasupra butonului

        rulesLabel.setBounds(labelX, labelY, labelWidth, labelHeight);
        add(rulesLabel);
        return rulesLabel;
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
            g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this); // Desenarea imaginii de fundal

            // Desenarea chenarului
            int labelWidth = 420;
            int labelHeight = 200;
            int labelX = WINDOW_WIDTH / 2 - labelWidth / 2 -180;
            int labelY = WINDOW_HEIGHT / 2 - labelHeight / 2 - 295; // poziționat puțin deasupra butonului

            Color backgroundColor = new Color(0, 0, 0, 128); // Culoarea negru cu transparență (128)
            g.setColor(backgroundColor);
            g.fillRoundRect(labelX, labelY, labelWidth, labelHeight, 30, 30); // margini rotunjite
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
        highestScore = getHighestScoreFromFile(hardMode ? HARD_MODE_SCORES_FILE : NORMAL_MODE_SCORES_FILE);
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
            if (hardMode) {
                if (frames % (ENEMY_SPAWN_RATE / 2) == 0) { // Inamicii apar de două ori mai des
                    int enemyX = (int) (Math.random() * (WINDOW_WIDTH - ENEMY_SIZE));
                    enemies.add(new Rectangle(enemyX, 0, ENEMY_SIZE, ENEMY_SIZE));
                }

                for (Rectangle enemy : enemies) {
                    enemy.y += 10; // Inamicii cad cu o viteză mai mare
                }
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
                        enemy.x - Constants.HIT_TOLERANCE,
                        enemy.y - Constants.HIT_TOLERANCE,
                        enemy.width + 2 * Constants.HIT_TOLERANCE,
                        enemy.height + 2 * Constants.HIT_TOLERANCE
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
                timer.stop(); // Oprim timer-ul pentru a opri actualizarea jocului

                // Determinăm numele fișierului pe baza modului de joc
                String scoreFile = hardMode ? HARD_MODE_SCORES_FILE : NORMAL_MODE_SCORES_FILE;
                saveScoreToFile(score, scoreFile);
                highestScore = getHighestScoreFromFile(scoreFile);

                if (score > highestScore) {
                    isNewRecord = true;
                } else {
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

    private void saveScoreToFile(int score, String fileName) {
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write("Score: " + score + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Modifică metoda getHighestScoreFromFile pentru a accepta numele fișierului ca parametru
    private int getHighestScoreFromFile(String fileName) {
        int highestScore = 0;
        try (Scanner scanner = new Scanner(new File(fileName))) {
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

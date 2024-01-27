package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;

import static org.example.Utils.*;

public class SampleShooterGame extends JPanel implements ActionListener, KeyListener {

    private Timer timer;
    private int playerX = WINDOW_WIDTH / 2 - PLAYER_SIZE / 2;
    private boolean moveLeft = false;
    private boolean moveRight = false;
    private boolean shooting = false;
    private int score = 0; // Variabila pentru a ține evidența scorului
    private ArrayList<Rectangle> projectiles = new ArrayList<>();
    private ArrayList<Rectangle> enemies = new ArrayList<>();
    private int frames = 0;

    public SampleShooterGame() {
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        timer = new Timer(1000 / 60, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);
        g.fillRect(playerX, WINDOW_HEIGHT - 50, PLAYER_SIZE, PLAYER_SIZE);

        g.setColor(Color.YELLOW);
        for (Rectangle projectile : projectiles) {
            g.fillRect(projectile.x, projectile.y, PROJECTILE_SIZE, PROJECTILE_SIZE);
        }

        g.setColor(Color.WHITE);
        for (Rectangle enemy : enemies) {
            g.fillRect(enemy.x, enemy.y, ENEMY_SIZE, ENEMY_SIZE);
        }

        // Desenăm scorul
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Scor: " + score, 10, 20);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
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

    private void checkCollisions() {
        Iterator<Rectangle> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Rectangle enemy = enemyIterator.next();
            Iterator<Rectangle> projectileIterator = projectiles.iterator();
            while (projectileIterator.hasNext()) {
                Rectangle projectile = projectileIterator.next();
                if (enemy.intersects(projectile)) {
                    projectileIterator.remove();
                    enemyIterator.remove();

                    score += 10; // Incrementăm scorul pentru fiecare inamic lovit

                    break;
                }
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> moveLeft = true;
            case KeyEvent.VK_RIGHT -> moveRight = true;
            case KeyEvent.VK_SPACE -> shooting = true;
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
}

package src;

import javax.swing.*;
import java.awt.*;

class RoundedButton extends JButton {
    private Color backgroundColor;
    private int cornerRadius;

    public RoundedButton(String label, Color bgColor, int radius) {
        super(label);
        backgroundColor = bgColor;
        cornerRadius = radius;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setFont(new Font("Arial", Font.BOLD, 22));
        setForeground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {

        if (getModel().isArmed()) {
            // Butonul este apăsat
            g.setColor(backgroundColor.darker());
        } else if (getModel().isRollover()) {
            // Mouse-ul este deasupra butonului
            g.setColor(backgroundColor.brighter());
        } else {
            // Starea normală a butonului
            g.setColor(backgroundColor);
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        g2.setColor(getForeground());

       // FontMetrics fm = g2.getFontMetrics();
       // Rectangle stringBounds = fm.getStringBounds(this.getText(), g2).getBounds();
       // int textX = (getWidth() - stringBounds.width) / 2;
       // int textY = (getHeight() - stringBounds.height) / 2 + fm.getAscent();
       // g2.drawString(getText(), textX, textY);
        g2.dispose();
        super.paintComponent(g);

    }
}

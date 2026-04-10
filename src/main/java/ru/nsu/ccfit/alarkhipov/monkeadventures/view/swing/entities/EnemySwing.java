package ru.nsu.ccfit.alarkhipov.monkeadventures.view.swing.entities;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class EnemySwing extends JPanel {

    private final Image enemyImage;
    private final int size;
    List<String> enemyTypes;
    private boolean isDying = false;
    private long deathStartTime = 0;

    public EnemySwing(int size) {
        this.size = size;
        enemyTypes = Arrays.asList(
                "/enemies/monkeEn1.png",
                "/enemies/monkeEn2.png",
                "/enemies/monkeEn3.png",
                "/enemies/monkeEn4.png",
                "/enemies/monkeEn5.png",
                "/enemies/monkeEn6.png",
                "/enemies/monkeEn7.png",
                "/enemies/monkeEn8.png",
                "/enemies/monkeEn9.png",
                "/enemies/monkeEn10.png"
        );

        String path = enemyTypes.get(ThreadLocalRandom.current().nextInt(enemyTypes.size()));

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path)));
        Image original = icon.getImage();
        this.enemyImage = original.getScaledInstance(size, size, Image.SCALE_SMOOTH);

        setPreferredSize(new Dimension(size, size));
        setOpaque(false);
        setDoubleBuffered(true);
    }

    public void startDeathAnimation() {
        isDying = true;
        deathStartTime = System.currentTimeMillis();
        repaint();
    }

    public boolean isDying() {
        return isDying;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        if (enemyImage == null) {
            return;
        }

        Graphics2D g2d = (Graphics2D) graphics;
        int drawX = (getWidth() - size) / 2;
        int drawY = (getHeight() - size) / 2;

        g2d.drawImage(enemyImage, drawX, drawY, size, size, this);
    }
}
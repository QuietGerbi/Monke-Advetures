package ru.nsu.ccfit.alarkhipov.monkeadventures.view.swing.entities;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class BossSwing extends EnemySwing {

    private Image bossImage;
    private final int size;

    public BossSwing(int size) {
        super(size);
        this.size = size;
        loadBossImage();
    }
    public void loadBossImage(){
        String path = "/enemies/monkeBoss.png";
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path)));
        Image original = icon.getImage();
        this.bossImage = original.getScaledInstance(size, size, Image.SCALE_SMOOTH);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        if (bossImage == null) return;

        Graphics2D g2d = (Graphics2D) graphics;
        int drawX = (getWidth() - size) / 2;
        int drawY = (getHeight() - size) / 2;

        g2d.drawImage(bossImage, drawX, drawY, size, size, this);
    }
}
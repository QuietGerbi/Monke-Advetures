package ru.nsu.ccfit.alarkhipov.monkeadventures.view.swing.weapons;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class MagicStaffSwing extends JPanel {

    private final Image staffImage;
    private final int sizeX;
    private final int sizeY;

    private boolean isAttacking = false;
    private long attackStartTime = 0;
    private final long attackDuration = 220;

    public MagicStaffSwing(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(
                getClass().getResource("/weapons/magicStaff.png")));
        this.staffImage = icon.getImage();

        setPreferredSize(new Dimension(sizeX, sizeY));
        setOpaque(false);
    }

    public void triggerAttack() {
        isAttacking = true;
        attackStartTime = System.currentTimeMillis();
    }

    public void drawAttackEffect(Graphics2D g2d, int playerCenterX, int playerCenterY) {
        if (!isAttacking) return;

        long timePassed = System.currentTimeMillis() - attackStartTime;

        if (timePassed < attackDuration) {
            float progress = (float) timePassed / attackDuration;
            float alpha = 0.6f * (1 - progress);

            g2d.setColor(new Color(1.0f, 0.92f, 0.55f, alpha));
            int radius = 240;
            g2d.fillOval(playerCenterX - radius, playerCenterY - radius, radius * 2, radius * 2);

        } else {
            isAttacking = false;
        }
    }

    public boolean isAttacking() {
        return isAttacking;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (staffImage == null) return;

        Graphics2D g2d = (Graphics2D) graphics;

        int drawX = (getWidth() - sizeX) / 2;
        int drawY = (getHeight() - sizeY) / 2;

        g2d.drawImage(staffImage, drawX, drawY, sizeX, sizeY, this);
    }
}


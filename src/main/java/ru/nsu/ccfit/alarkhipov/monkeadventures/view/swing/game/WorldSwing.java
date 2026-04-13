package ru.nsu.ccfit.alarkhipov.monkeadventures.view.swing.game;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nsu.ccfit.alarkhipov.monkeadventures.view.swing.entities.EnemySwing;
import ru.nsu.ccfit.alarkhipov.monkeadventures.view.swing.entities.PlayerSwing;
import ru.nsu.ccfit.alarkhipov.monkeadventures.view.swing.weapons.MagicStaffSwing;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WorldSwing extends JPanel {
    private PlayerSwing playerSwing;
    private final List<EnemySwing> enemySwings = new ArrayList<>();
    private MagicStaffSwing staffSwing;
    private final Image backgroundImage;

    private float cameraX = 0f;
    private float cameraY = 0f;

    private int currHPInfo;
    private int maxHPInfo;
    private List<Integer> enemyCurrHPs = new ArrayList<>();
    private List<Integer> enemyMaxHPs = new ArrayList<>();

    private int currentExpInfo;
    private int expToNextLevelInfo;
    private int currentDamageInfo;

    private final long gameStartTime = System.currentTimeMillis();
    private long elapsedSeconds;

    public WorldSwing() {
        setLayout(null);
        setOpaque(true);
        setDoubleBuffered(true);
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(
                getClass().getResource("/backgrounds/grassTile.png")));
        backgroundImage = icon.getImage();
    }

    public void setPlayer(PlayerSwing player) {
        this.playerSwing = player;
        add(playerSwing);
        playerSwing.setSize(playerSwing.getPreferredSize());
    }

    public void addEnemy(EnemySwing enemySwing) {
        this.enemySwings.add(enemySwing);
        add(enemySwing);
        enemySwing.setSize(enemySwing.getPreferredSize());
    }

    public void setStaff(MagicStaffSwing staff) {
        this.staffSwing = staff;
        add(staffSwing);
        staffSwing.setSize(staffSwing.getPreferredSize());
    }

    public void update(float playerWorldX, float playerWorldY) {
        this.cameraX = playerWorldX;
        this.cameraY = playerWorldY;

        if (staffSwing != null && playerSwing != null) {
            int playerCenterX = (getWidth() - playerSwing.getWidth()) / 2;
            int playerCenterY = (getHeight() - playerSwing.getHeight()) / 2;
            int staffX = playerCenterX + playerSwing.getWidth() / 2 + 30;
            int staffY = playerCenterY + playerSwing.getHeight() / 2 - 120;

            playerSwing.setLocation(playerCenterX, playerCenterY);
            playerSwing.repaint();
            staffSwing.setLocation(staffX, staffY);
        }
        repaint();
    }

    public void updateEnemyPositions(List<Point> enemyScreenPositions) {
        for (int i = 0; i < enemySwings.size() && i < enemyScreenPositions.size(); i++) {
            Point centerPoint = enemyScreenPositions.get(i);
            EnemySwing swing = enemySwings.get(i);

            int worldX = centerPoint.x - (swing.getWidth() / 2);
            int worldY = centerPoint.y - (swing.getHeight() / 2);

            swing.setLocation(worldX, worldY);
        }
    }

    public void removeEnemySwing(EnemySwing enemySwing) {
        if (enemySwing != null && enemySwings.contains(enemySwing)) {
            remove(enemySwing);
            enemySwings.remove(enemySwing);
            repaint();
        }
    }

    public void updateGameTime(long elapsedSeconds) {
        this.elapsedSeconds = elapsedSeconds;
        repaint();
    }

    public void updateHPInfo(int currentHP, int maxHP) {
        this.currHPInfo = currentHP;
        this.maxHPInfo = maxHP;
        repaint();
    }

    public void updateEnemyHPInfo(List<Integer> enemyHPs, List<Integer> enemyMaxHPs) {
        this.enemyCurrHPs = enemyHPs;
        this.enemyMaxHPs = enemyMaxHPs;
        repaint();
    }

    public void updateExpInfo(int currentExp, int expToNextLevel) {
        this.currentExpInfo = currentExp;
        this.expToNextLevelInfo = expToNextLevel;
        repaint();
    }

    public void updateDamageInfo(int damage) {
        this.currentDamageInfo = damage;
        repaint();
    }

    public void paintTime(Graphics2D g2d){
        long currentTime = System.currentTimeMillis();
        long totalSeconds = (currentTime - gameStartTime) / 1000;

        int minutes = (int) (totalSeconds / 60);
        int seconds = (int) (totalSeconds % 60);

        String timeText = String.format("%02d:%02d", minutes, seconds);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.drawString(timeText, getWidth() - 180, 50);
    }

    public void paintHPInfo(Graphics2D g2d){
        int barX = 30;
        int barY = 30;
        int barWidth = 300;
        int barHeight = 25;

        g2d.setColor(new Color(50, 50, 50));
        g2d.fillRect(barX, barY, barWidth, barHeight);

        float hpPercent = maxHPInfo > 0 ? (float) currHPInfo / maxHPInfo : 0;
        int currentBarWidth = (int) (barWidth * hpPercent);

        if (hpPercent > 0.6f) {
            g2d.setColor(new Color(0, 220, 0));
        } else if (hpPercent > 0.3f) {
            g2d.setColor(new Color(255, 200, 0));
        } else {
            g2d.setColor(new Color(220, 0, 0));
        }

        g2d.fillRect(barX, barY, currentBarWidth, barHeight);

        g2d.setColor(Color.WHITE);
        g2d.drawRect(barX, barY, barWidth, barHeight);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("HP: " + currHPInfo + " / " + maxHPInfo, barX + 10, barY + 42);

    }

    public void paintEnemiesHPInfo(Graphics2D g2d) {
        if (enemySwings == null || enemyCurrHPs == null || enemyMaxHPs == null) {
            return;
        }

        for (int i = 0; i < enemySwings.size() && i < enemyCurrHPs.size() && i < enemyMaxHPs.size(); i++) {
            EnemySwing enemyView = enemySwings.get(i);
            int curHP = enemyCurrHPs.get(i);
            int maxHP = enemyMaxHPs.get(i);

            if (maxHP <= 0) continue;

            int ex = enemyView.getX();
            int ey = enemyView.getY();
            int eWidth = enemyView.getWidth();

            int barWidth = 150;
            int barHeight = 11;
            int barX = ex + (eWidth - barWidth) / 2;
            int barY = ey - 20;

            g2d.setColor(new Color(30, 30, 30, 220));
            g2d.fillRect(barX, barY, barWidth, barHeight);

            float hpPercent = Math.max(0, Math.min(1.0f, (float) curHP / maxHP));
            int currentWidth = (int) (barWidth * hpPercent);

            Color hpColor = hpPercent > 0.55f ? new Color(0, 210, 0) :
                    hpPercent > 0.25f ? new Color(255, 180, 0) :
                            new Color(200, 0, 0);

            g2d.setColor(hpColor);
            g2d.fillRect(barX, barY, currentWidth, barHeight);

            g2d.setColor(new Color(220, 220, 220));
            g2d.drawRect(barX, barY, barWidth, barHeight);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            String text = curHP + " / " + maxHP;
            g2d.drawString(text, barX + 4, barY - 8);
        }
    }

    public void paintEXPInfo(Graphics2D g2d){
        int expBarX = 30;
        int expBarY = 80;
        int expBarWidth = 300;
        int expBarHeight = 18;

        g2d.setColor(new Color(40, 40, 70));
        g2d.fillRect(expBarX, expBarY, expBarWidth, expBarHeight);

        float expPercent = expToNextLevelInfo > 0 ? (float) currentExpInfo / expToNextLevelInfo : 0;
        int expCurrentWidth = (int) (expBarWidth * expPercent);

        g2d.setColor(new Color(0, 140, 255));
        g2d.fillRect(expBarX, expBarY, expCurrentWidth, expBarHeight);

        g2d.setColor(Color.WHITE);
        g2d.drawRect(expBarX, expBarY, expBarWidth, expBarHeight);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 15));
        g2d.drawString("EXP: " + currentExpInfo + " / " + expToNextLevelInfo,
                expBarX + 10, expBarY + 32);
    }

    public void paintPlayerDamage(Graphics2D g2d){
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Урон " + currentDamageInfo, 35, 140);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (backgroundImage != null) {
            int tileW = backgroundImage.getWidth(null);
            int tileH = backgroundImage.getHeight(null);

            int startX = (int) (-cameraX % tileW);
            int startY = (int) (-cameraY % tileH);

            for (int x = startX - tileW; x < getWidth() + tileW; x += tileW) {
                for (int y = startY - tileH; y < getHeight() + tileH; y += tileH) {
                    g2d.drawImage(backgroundImage, x, y, this);
                }
            }
        }
        if (staffSwing != null && staffSwing.isAttacking()) {
            int centerX = (getWidth() - playerSwing.getWidth()) / 2 + playerSwing.getWidth() / 2;
            int centerY = (getHeight() - playerSwing.getHeight()) / 2 + playerSwing.getHeight() / 2;

            staffSwing.drawAttackEffect(g2d, centerX, centerY);
        }

        paintEnemiesHPInfo(g2d);
        paintTime(g2d);
        paintPlayerDamage(g2d);
        paintHPInfo(g2d);
        paintEXPInfo(g2d);
    }

}

package ru.nsu.ccfit.alarkhipov.monkeadventures.controller.swing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nsu.ccfit.alarkhipov.monkeadventures.ScoreManager;
import ru.nsu.ccfit.alarkhipov.monkeadventures.model.entities.Enemy;
import ru.nsu.ccfit.alarkhipov.monkeadventures.model.entities.Player;
import ru.nsu.ccfit.alarkhipov.monkeadventures.music.SoundPad;
import ru.nsu.ccfit.alarkhipov.monkeadventures.view.swing.entities.BossSwing;
import ru.nsu.ccfit.alarkhipov.monkeadventures.view.swing.entities.EnemySwing;
import ru.nsu.ccfit.alarkhipov.monkeadventures.view.swing.game.GameView;
import ru.nsu.ccfit.alarkhipov.monkeadventures.view.swing.game.WorldSwing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class GameController implements KeyListener {
    private static final Logger log = LogManager.getLogger(GameController.class);
    private final Player player;
    private final GameView view;
    private final WorldSwing world;

    private final List<Enemy> enemies = new ArrayList<>();
    private final List<EnemySwing> enemySwings = new ArrayList<>();
    private final List<Point> enemyScreenPositions = new ArrayList<>();

    private boolean bossSpawned = false;
    private Enemy boss = null;

    private final int HPPerWave = 100;
    private final int damagePerWave = 7;
    private int currentTotalHPBonus = 0;
    private int currentTotalDamageBonus = 0;

    private boolean movingUp, movingDown, movingLeft, movingRight;
    private boolean isRunning = true;

    private int spawnInterval = 3000;
    private final long gameStartTime = System.currentTimeMillis();
    final SoundPad soundPad = new SoundPad();

    public GameController(JFrame context) {
        this.player = new Player();
        this.view = new GameView(this, context);
        this.player.addObserver(view);
        this.world = view.getWorldSwing();

        view.getFrame().addKeyListener(this);
        view.getFrame().setFocusable(true);
        view.getFrame().requestFocusInWindow();

        List<String> levelMusic = List.of("/music/enemy/Kevin MacLeod - Sneaky Snitch.mp3",
                "/music/enemy/SplinterWolf.mp3", "/music/enemy/Time Lapse by TheFatRat.mp3",
                "/music/enemy/Xue Hua Piao Piao.mp3", "/music/enemy/Master.mp3",
                "/music/enemy/Lesson in the Dark Room.mp3");
        List<String> bossMusic = List.of("/music/boss/Ashes on the Fire.mp3",
                "/music/boss/Footsteps of Doom.mp3", "/music/boss/AOT.mp3");

        startGameLoop();
        soundPad.setPlaylist1(levelMusic, true);
        soundPad.setPlaylist2(bossMusic, true);
        soundPad.start();
        startEnemySpawning();
    }

    private void startGameLoop() {
        Thread gameThread = new Thread(() -> {
            while (isRunning) {
                long startTime = System.nanoTime();
                updatePlayerPosition();
                updateAllEnemies();
                checkCollisions();
                player.getWeapon().attack(player, enemies);
                removeDeadEnemies();
                if (player.isDead()){
                    isRunning=false;
                    handlePlayerDeath();
                    break;
                }

                if (System.currentTimeMillis() - player.getWeapon().getLastAttackTime() < 50) {
                    SwingUtilities.invokeLater(() -> {view.getStaffSwing().triggerAttack();});
                }
                SwingUtilities.invokeLater(this::sendUpdateToView);
                Toolkit.getDefaultToolkit().sync();

                long timeTaken = System.nanoTime() - startTime;
                long sleepTime = 16_000_000 - timeTaken;

                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime / 1_000_000);
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                        log.info("Thread was interrupted");
                    }
                }
            }
        });
        gameThread.start();
    }

    private void updatePlayerPosition() {
        float speed = player.getCurSpeed();
        float newX = player.getX();
        float newY = player.getY();

        boolean moved = false;

        if (movingUp)    { newY -= speed; moved = true; }
        if (movingDown)  { newY += speed; moved = true; }
        if (movingLeft)  { newX -= speed; moved = true; }
        if (movingRight) { newX += speed; moved = true; }

        if (moved) {
            player.setPosition(newX, newY);
        }
    }

    private void updateAllEnemies() {
        for (Enemy enemy : enemies) {
            enemy.update(player);
        }
    }

    private void sendUpdateToView() {
        float playerX = player.getX();
        float playerY = player.getY();

        List<Integer> enemyHPs = new ArrayList<>();
        List<Integer> enemyMaxHPs = new ArrayList<>();

        world.update(playerX, playerY);

        long elapsedSeconds = (System.currentTimeMillis() - gameStartTime) / 1_000;
        world.updateGameTime(elapsedSeconds);

        enemyScreenPositions.clear();

        int centerX = world.getWidth() / 2;
        int centerY = world.getHeight() / 2;

        for (Enemy enemy : enemies) {
            int screenX = centerX + (int)(enemy.getX() - playerX);
            int screenY = centerY + (int)(enemy.getY() - playerY);
            enemyScreenPositions.add(new Point(screenX, screenY));

            enemyHPs.add(enemy.getCurHP());
            enemyMaxHPs.add(enemy.getMaxHP());
        }

        world.updateExpInfo(player.getCurrentExp(), player.getExpToNextLevel());
        world.updateHPInfo(player.getCurHP(), player.getMaxHP());
        world.updateDamageInfo(player.getWeapon().getDamage());
        world.updateEnemyPositions(enemyScreenPositions);
        world.updateEnemyHPInfo(enemyHPs, enemyMaxHPs);
    }

    private void startEnemySpawning() {
        Timer spawnTimer = new Timer(spawnInterval, e -> {
            if (!bossSpawned) {
                spawnEnemy();
            }
        });
        spawnTimer.start();

        Timer difficultyTimer = new Timer(210000, e -> {
            currentTotalHPBonus += HPPerWave;
            currentTotalDamageBonus += damagePerWave;

            if (spawnInterval > 400) {
                spawnInterval -= 400;
                spawnTimer.setDelay(spawnInterval);
            }
        });

        Timer bossTimer = new Timer(2100000, e -> {
            if (!bossSpawned) {
                spawnBoss();
            }
        });

        difficultyTimer.start();
        bossTimer.setRepeats(false);
        bossTimer.start();
    }

    private void spawnEnemy() {
        int screenWidth = world.getWidth();
        int screenHeight = world.getHeight();

        float spawnDistance = Math.max(screenWidth, screenHeight) / 2f + 150;
        double angle = Math.random() * Math.PI * 2;
        float spawnX = player.getX() + (float) (Math.cos(angle) * spawnDistance);
        float spawnY = player.getY() + (float) (Math.sin(angle) * spawnDistance);

        Enemy enemy = new Enemy(spawnX, spawnY);
        enemy.enemyIncreaseLevel(currentTotalHPBonus, currentTotalDamageBonus);
        double rand = Math.random();

        if (rand < 0.5) {
            enemy.setType(Enemy.WalkType.NORMAL);
        } else if (rand < 0.85) {
            enemy.setType(Enemy.WalkType.ZIGZAG);
        } else {
            enemy.setType(Enemy.WalkType.CHARGE);
        }

        EnemySwing enemyView = new EnemySwing(150);

        enemies.add(enemy);
        enemySwings.add(enemyView);
        world.addEnemy(enemyView);
    }

    private void spawnBoss() {
        if (bossSpawned) {
            return;
        }

        for (int i = enemies.size() - 1; i >= 0; i--) {
            world.removeEnemySwing(enemySwings.get(i));
            enemies.remove(i);
            enemySwings.remove(i);
        }

        soundPad.switchToBossMusic();

        double angle = Math.random() * Math.PI * 2;
        float distance = 600f;

        float spawnX = player.getX() + (float) Math.cos(angle) * distance;
        float spawnY = player.getY() + (float) Math.sin(angle) * distance;

        boss = new Enemy(spawnX, spawnY, 50_000, 50_000, 1.0f,
                30, 300, 100_000,
                Enemy.WalkType.NORMAL, 0f, 0f, false);

        player.getWeapon().setRadius(550);

        BossSwing bossView = new BossSwing(600);

        enemies.add(boss);
        enemySwings.add(bossView);
        world.addEnemy(bossView);

        bossSpawned = true;
    }

    private void removeDeadEnemies() {
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            if (enemy.isDead()) {
                player.addExperience(enemy.getExperienceValue());
                player.addKills();

                if (enemy == boss) {
                    isRunning = false;
                    player.addKills();
                    handleBossDeath();
                }

                if (Math.random() < 0.1) {
                    player.heal(15);
                }

                world.removeEnemySwing(enemySwings.get(i));
                enemies.remove(i);
                enemySwings.remove(i);
            }
        }
    }

    private void checkCollisions() {
        for (Enemy enemy : enemies) {
            if (isColliding(player, enemy)) {
                player.takeDamage(enemy.getDamage());
                pushAway(player, enemy);
            }
        }
    }

    private void pushAway(Player p, Enemy e) {
        float dx = p.getX() - e.getX();
        float dy = p.getY() - e.getY();
        float dist = (float) Math.hypot(dx, dy);

        if (dist < 1) return;

        float pushForce = 10f;

        p.setPosition(
                p.getX() + (dx / dist) * pushForce,
                p.getY() + (dy / dist) * pushForce
        );
    }

    public static boolean isColliding(Player p, Enemy e) {
        float dx = p.getX() - e.getX();
        float dy = p.getY() - e.getY();
        float distance = (float) Math.hypot(dx, dy);

        return distance < (p.getHitboxRadius() + e.getHitboxRadius());
    }

    private void handlePlayerDeath() {
        soundPad.stopAll();
        SwingUtilities.invokeLater(() -> {
            long elapsedSeconds = (System.currentTimeMillis() - gameStartTime) / 1000;
            ScoreManager sm = new ScoreManager();
            sm.saveIfHigher(elapsedSeconds);

            String message = "Вы погибли!\n\n" +
                    "Ваш уровень: " + player.getLevel() + "\n" +
                    "Время выживания: " + getGameTimeString() + "\n" +
                    "Вы убили: " + player.getKills()  + " бибизян\n";

            JOptionPane.showMessageDialog(view.getFrame(),
                    message,
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE);

            int choice = JOptionPane.showConfirmDialog(view.getFrame(),
                    "Хотите попробовать снова?",
                    "Game Over",
                    JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                restartGame();
            } else {
                System.exit(0);
            }
        });
    }

    private void handleBossDeath() {
        SwingUtilities.invokeLater(() -> {
            String message = "Победа!\n\n" +
                    "Вы победили босса!\n" +
                    "Ваш финальный уровень: " + player.getLevel() + "\n" +
                    "Время игры: " + getGameTimeString() + "\n" +
                    "Вы убили: " + player.getKills()  + " бибизян\n";

            JOptionPane.showMessageDialog(view.getFrame(),
                    message,
                    "Победа!",
                    JOptionPane.INFORMATION_MESSAGE);

            System.exit(0);
        });
    }

    private void restartGame() {
        this.isRunning = false;
        SwingUtilities.invokeLater(() -> {
            view.getFrame().dispose();
        });
        new GameController(view.getFrame());
    }

    private void returnToMainMenu() {
        this.isRunning = false;
        soundPad.stopAll();

        SwingUtilities.invokeLater(() -> {
            view.getFrame().dispose();
            new MainMenuController();
        });
    }

    private String getGameTimeString() {
        long totalSeconds = (System.currentTimeMillis() - gameStartTime) / 1000;
        int minutes = (int) (totalSeconds / 60);
        int seconds = (int) (totalSeconds % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }


    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP    -> movingUp = true;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN  -> movingDown = true;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT  -> movingLeft = true;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> movingRight = true;
            case KeyEvent.VK_B -> {
                if (!bossSpawned) {
                    spawnBoss();
                }
            }
            case KeyEvent.VK_M -> {
                soundPad.toggleMute();
            }

            case KeyEvent.VK_PERIOD -> {
                soundPad.nextTrack();
            }
            case KeyEvent.VK_COMMA -> {
                soundPad.previousTrack();
            }

            case KeyEvent.VK_ESCAPE -> {
                returnToMainMenu();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP    -> movingUp = false;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN  -> movingDown = false;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT  -> movingLeft = false;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> movingRight = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
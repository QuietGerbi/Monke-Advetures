package ru.nsu.ccfit.alarkhipov.monkeadventures.controller.text;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nsu.ccfit.alarkhipov.monkeadventures.ScoreManager;
import ru.nsu.ccfit.alarkhipov.monkeadventures.model.entities.Enemy;
import ru.nsu.ccfit.alarkhipov.monkeadventures.model.entities.Player;
import ru.nsu.ccfit.alarkhipov.monkeadventures.view.text.GameView;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static ru.nsu.ccfit.alarkhipov.monkeadventures.controller.swing.GameController.isColliding;

public class GameController {
    private static final Logger log = LogManager.getLogger(GameController.class);
    private final GameView view;
    private final Player player;
    private final List<Enemy> enemies = new ArrayList<>();

    private boolean bossSpawned = false;
    private Enemy boss = null;

    private final int HPPerWave = 100;
    private final int damagePerWave = 7;
    private int currentTotalHPBonus = 0;
    private int currentTotalDamageBonus = 0;
    private int spawnInterval = 3000;

    private long lastDifficultyTime;
    private final long gameStartTime = System.currentTimeMillis();
    private long lastSpawnTime;

    private boolean isRunning = true;
    final float scale = 0.37f;

    public GameController() {
        this.player = new Player();
        this.view = new GameView();
        this.player.addObserver(view);
        this.player.getWeapon().setRadius(60);
        startGameLoop();
    }

    private void startGameLoop() {
        view.printTitle();
        while (isRunning) {
            try {
                startEnemySpawning();
                updateAllEnemies();
                player.getWeapon().attack(player, enemies);
                checkCollisions();
                removeDeadEnemies();
                if (player.isDead()){
                    isRunning=false;
                    handlePlayerDeath();
                    break;
                }
                sendUpdateToView();
                view.printMap(bossSpawned);
                processInput();
            } catch (Exception e) {
                log.error("Game loop error");
            }
        }
    }

    private void updateAllEnemies() {
        for (Enemy enemy : enemies) {
            enemy.update(player);
        }
    }

    private void sendUpdateToView() {
        List<Point> enemyScreenPositions = new ArrayList<>();

        int centerX = view.getViewWidth() / 2;
        int centerY = view.getViewHeight() / 2;

        for (Enemy enemy : enemies) {
            int screenX = centerX + (int)((enemy.getX() - player.getX()) * scale);
            int screenY = centerY + (int)((enemy.getY() - player.getY()) * scale);
            enemyScreenPositions.add(new Point(screenX, screenY));
        }

        view.updateEnemyPositions(enemyScreenPositions);
        view.updateHPInfo(player.getCurHP(), player.getMaxHP());
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
        float spawnDistance = Math.max(view.getViewWidth(), view.getViewHeight()) / 2f + 150;
        double angle = Math.random() * Math.PI * 2;
        float spawnX = player.getX() + (float)(Math.cos(angle) * spawnDistance);
        float spawnY = player.getY() + (float)(Math.sin(angle) * spawnDistance);

        Enemy enemy = new Enemy(spawnX, spawnY);
        enemy.setHitboxRadius(1.5f);
        enemy.enemyIncreaseLevel(currentTotalHPBonus, currentTotalDamageBonus);

        double rand = Math.random();
        if (rand < 0.5) {
            enemy.setType(Enemy.WalkType.NORMAL);
        } else if (rand < 0.85) {
            enemy.setType(Enemy.WalkType.ZIGZAG);
        } else {
            enemy.setType(Enemy.WalkType.CHARGE);
        }

        enemies.add(enemy);
    }

    private void spawnBoss() {
        if (bossSpawned) {
            return;
        }

        enemies.clear();

        double angle = Math.random() * Math.PI * 2;
        float spawnDistance = 120f;
        float spawnX = player.getX() + (float)(Math.cos(angle) * spawnDistance);
        float spawnY = player.getY() + (float)(Math.sin(angle) * spawnDistance);

        boss = new Enemy(spawnX, spawnY, 50_000, 50_000, 1.0f,
                30, 2f, 100_000,
                Enemy.WalkType.NORMAL, 0f, 0f, false);

        enemies.add(boss);
        bossSpawned = true;
    }

    private void removeDeadEnemies() {
        enemies.removeIf(enemy -> {
            if (enemy.isDead()) {
                player.addExperience(enemy.getExperienceValue());
                player.addKills();

                if (enemy == boss) {
                    isRunning = false;
                    player.addKills();
                    handleBossDeath();
                }
                return true;
            }
            return false;
        });
    }

    private void checkCollisions() {
        for (Enemy enemy : enemies) {
            if (isColliding(player, enemy)) {
                player.takeDamage(enemy.getDamage());
            }
        }
    }


    private void handlePlayerDeath() {
        long elapsedSeconds = (System.currentTimeMillis() - gameStartTime) / 1000;
        ScoreManager sm = new ScoreManager();
        sm.saveIfHigher(elapsedSeconds);

        String message = "Вы погибли!\n\n" +
                    "Ваш уровень: " + player.getLevel() + "\n" +
                    "Время выживания: " + getGameTimeString() + "\n" +
                    "Вы убили: " + player.getKills()  + " бибизян\n";

        System.out.println(message);

        System.out.println("Хотите попробовать еще раз? y/n");

        try {
            char input = (char) System.in.read();
            if (input == 'y') {
                restartGame();
            } else {
                System.exit(0);
            }
        }
        catch (Exception e){
            log.info("Retry error");
        }
    }

    private void handleBossDeath() {
        String message = "Победа!\n\n" +
                "Вы победили босса!\n" +
                "Ваш финальный уровень: " + player.getLevel() + "\n" +
                "Время игры: " + getGameTimeString() + "\n" +
                "Вы убили: " + player.getKills()  + " бибизян\n";
        System.exit(0);
    }

    private String getGameTimeString() {
        long totalSeconds = (System.currentTimeMillis() - gameStartTime) / 1000;
        int minutes = (int) (totalSeconds / 60);
        int seconds = (int) (totalSeconds % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void restartGame() {
        this.isRunning = false;
        new GameController();
    }

    private void processInput() {
        float speed = player.getCurSpeed();
        float newX = player.getX();
        float newY = player.getY();

        boolean moved = false;

        try {
            Scanner in = new Scanner(System.in);
            String input = in.nextLine();
            switch (input.toLowerCase()) {
                case "about" -> {
                    String message = """
                            Monke Adventures\n
                            Помогите обезьянке выжить!\n
                            Бедная бибизянка зашла в чужой район и ей нужно победить остальных \n
                            бибизян чтобы выжить и вернуться к своей семье. \n
                            Благо она нашла посох монаха которым она сможет защититься\n
                            Управление:\n
                            • WASD / Стрелки — Движение\n
                            • B — Вызвать босса (если смелый)\n
                            """;
                    System.out.println(message);
                }
                case "scores" -> {
                    ScoreManager sm = new ScoreManager();
                    String best = sm.getBestTimeFormatted();
                    System.out.println("Ваш рекорд выживания: " + best);
                }
                case "w" -> { newY -= speed; moved = true; }
                case "s" -> { newY += speed; moved = true; }
                case "a" -> { newX -= speed; moved = true; }
                case "d" -> { newX += speed; moved = true; }
                case "b" -> spawnBoss();
                case "q" -> {
                    System.out.println("Выход из игры.");
                    System.exit(0);
                }
            }
            if (moved) {
                player.setPosition(newX, newY);
            }

        } catch (Exception _) {
            log.info("Input error");
        }
    }
}


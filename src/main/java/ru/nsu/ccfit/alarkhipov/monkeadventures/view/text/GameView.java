package ru.nsu.ccfit.alarkhipov.monkeadventures.view.text;

import ru.nsu.ccfit.alarkhipov.monkeadventures.observe.Observer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameView implements Observer<ArrayList<Float>> {
    private int currHPInfo;
    private int maxHPInfo;

    private float playerWorldX = 0f;
    private float playerWorldY = 0f;

    private List<Point> EnemyScreenPositions = new ArrayList<>();

    private final int viewWidth = 80;
    private final int viewHeight = 20;
    char[][] screen = new char[viewHeight][viewWidth];

    public void printMap(boolean isBossPhase) {
        for (char[] row : screen) {
            Arrays.fill(row, '.');
        }
        for (Point p : EnemyScreenPositions) {
            if (p.x >= 0 && p.x < viewWidth && p.y >= 0 && p.y < viewHeight) {
                if (isBossPhase) {
                    screen[p.y][p.x] = 'B';
                } else {
                    screen[p.y][p.x] = 'M';
                }
            }
        }

        screen[viewHeight / 2][viewWidth / 2] = 'P';

        for (char[] row : screen) {
            System.out.println(new String(row));
        }

        System.out.println("HP: " + currHPInfo + "/" + maxHPInfo);
        System.out.printf("Позиция: (%.0f, %.0f)%n", playerWorldX, playerWorldY);
        System.out.println("WASD — движение | Q — выход");
    }

    @Override
    public void update(ArrayList<Float> context) {
        this.playerWorldX = context.get(0);
        this.playerWorldY = context.get(1);
    }

    public void printTitle() {
        System.out.println("\n");
        System.out.println(" ╔═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println(" ║                                                                                                                                         ║");
        System.out.println(" ║  ███╗   ███╗ ██████╗ ███╗   ██╗██╗  ██╗███████╗    █████╗ ██████╗ ██╗   ██╗███████╗███╗   ██╗████████╗██╗   ██╗██████╗ ███████╗███████╗ ║");
        System.out.println(" ║  ████╗ ████║██╔═══██╗████╗  ██║██║ ██╔╝██╔════╝   ██╔══██╗██╔══██╗██║   ██║██╔════╝████╗  ██║╚══██╔══╝██║   ██║██╔══██╗██╔════╝██╔════╝ ║");
        System.out.println(" ║  ██╔████╔██║██║   ██║██╔██╗ ██║█████╔╝ █████╗     ███████║██║  ██║██║   ██║█████╗  ██╔██╗ ██║   ██║   ██║   ██║██████╔╝█████╗  ███████╗ ║");
        System.out.println(" ║  ██║╚██╔╝██║██║   ██║██║╚██╗██║██╔═██╗ ██╔══╝     ██╔══██║██║  ██║╚██╗ ██╔╝██╔══╝  ██║╚██╗██║   ██║   ██║   ██║██╔══██╗██╔══╝  ╚════██║ ║");
        System.out.println(" ║  ██║ ╚═╝ ██║╚██████╔╝██║ ╚████║██║  ██╗███████╗   ██║  ██║██████╔╝ ╚████╔╝ ███████╗██║ ╚████║   ██║   ╚██████╔╝██║  ██║███████╗███████║ ║");
        System.out.println(" ║  ╚═╝     ╚═╝ ╚═════╝ ╚═╝  ╚═══╝╚═╝  ╚═╝╚══════╝   ╚═╝  ╚═╝╚═════╝   ╚═══╝  ╚══════╝╚═╝  ╚═══╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝╚══════╝╚══════╝ ║");
        System.out.println(" ║                                                                                                                                         ║");
        System.out.println(" ╚═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
        System.out.println("\n");

        System.out.println("                                                          MONKE ADVENTURES");
        System.out.println("                                    Помоги бедной бибизянке выжить и вернуться домой к семье!\n");

        System.out.println("   " + "═".repeat(134));
        System.out.println("                                                       УПРАВЛЕНИЕ И КОМАНДЫ:");
        System.out.println("   " + "═".repeat(134));
        System.out.println("      [ W, A, S, D ]      — Движение обезьянки");
        System.out.println("      [ B ]               — Вызвать Monke Boss (если ты достаточно смелый)");
        System.out.println("      [ Q ]               — Выход из игры");
        System.out.println();
        System.out.println("      Введите в консоль:");
        System.out.println("      about               — Об игре");
        System.out.println("      scores              — Рекорд времени выживания");
        System.out.println("   " + "═".repeat(134));
        System.out.println();
    }

    public void updateEnemyPositions(List<Point> enemyScreenPositions) {
        this.EnemyScreenPositions = new ArrayList<>(enemyScreenPositions);
    }

    public void updateHPInfo(int current, int max) {
        this.currHPInfo = current;
        this.maxHPInfo = max;
    }

    public int getViewWidth() { return viewWidth; }
    public int getViewHeight() { return viewHeight; }
}


package ru.nsu.ccfit.alarkhipov.monkeadventures.model.weapons;

import ru.nsu.ccfit.alarkhipov.monkeadventures.model.entities.Enemy;
import ru.nsu.ccfit.alarkhipov.monkeadventures.model.entities.Player;

import java.util.List;

public class MagicStaff extends Weapon {

    public MagicStaff() {
        super("Magic Staff", 25, 315f, 0.8);
    }

    @Override
    public void attack(Player player, List<Enemy> enemies) {
        if (!isReady()) {
            return;
        }
        resetCooldown();

        for (Enemy enemy : enemies) {
            float dx = enemy.getX() - player.getX();
            float dy = enemy.getY() - player.getY();
            float distance = (float) Math.hypot(dx, dy);

            if (distance <= radius) {
                enemy.takeDamage(damage);
            }
        }
    }
}
package ru.nsu.ccfit.alarkhipov.monkeadventures.model.weapons;

import ru.nsu.ccfit.alarkhipov.monkeadventures.model.entities.Enemy;
import ru.nsu.ccfit.alarkhipov.monkeadventures.model.entities.Player;

import java.util.List;

public abstract class Weapon {

    protected String name;
    protected int damage;
    protected float radius;
    protected double cooldown;
    protected long lastAttackTime = 0;

    public Weapon(String name, int damage, float radius, double cooldown) {
        this.name = name;
        this.damage = damage;
        this.radius = radius;
        this.cooldown = cooldown;
    }

    public abstract void attack(Player player, List<Enemy> enemies);

    protected void resetCooldown() {
        lastAttackTime = System.currentTimeMillis();
    }

    protected boolean isReady() {
        return System.currentTimeMillis() - lastAttackTime >= cooldown * 1000;
    }

    public String getName() { return name; }
    public int getDamage() { return damage; }
    public float getRadius() { return radius; }
    public double getCooldown() { return cooldown; }
    public void setRadius(float radius) {
        this.radius = radius;
    }
    public long getLastAttackTime() { return lastAttackTime; }
    public void setDamage(int damage) {
        this.damage = damage;
    }
}
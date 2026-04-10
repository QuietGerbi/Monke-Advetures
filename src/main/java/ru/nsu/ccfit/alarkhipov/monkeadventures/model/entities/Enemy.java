package ru.nsu.ccfit.alarkhipov.monkeadventures.model.entities;

import ru.nsu.ccfit.alarkhipov.monkeadventures.observe.Observable;

import java.util.ArrayList;


public class Enemy extends Observable<ArrayList<Float>> implements Entity{
    private int curHP=100;
    private int maxHP=100;
    private float speed=2f;
    private int damage = 2;
    private float x, y = 0f;
    private float hitboxRadius = 40f;
    private int experienceValue = 50;
    private WalkType type = WalkType.NORMAL;
    private float zigzagAngle = 0f;
    private float chargeTimer = 0f;
    private boolean isCharging = false;
    private final long DEATH_DURATION = 450;

    private final float worldWidth = 30000f;
    private final float worldHeight = 300000f;

    public enum WalkType {
        NORMAL,
        ZIGZAG,
        CHARGE
    }

    public Enemy(float spawnX, float spawnY) {
        this.x = spawnX;
        this.y = spawnY;
    }

    @Override
    public void update(Player player){
        float dx = player.getX() - this.x;
        float dy = player.getY() - this.y;
        float distance = (float) Math.hypot(dx, dy);

        switch (type) {
            case NORMAL:
                moveTowardsPlayer(dx, dy, distance);
                break;

            case ZIGZAG:
                moveZigzag(dx, dy, distance);
                break;

            case CHARGE:
                moveCharge(dx, dy, distance);
                break;
        }

        this.x = Math.max(-worldWidth/2, Math.min(worldWidth/2, this.x));
        this.y = Math.max(-worldHeight/2, Math.min(worldHeight/2, this.y));

        ArrayList<Float> coords = new ArrayList<>();
        coords.add(this.x);
        coords.add(this.y);
        notify(coords);
    }

    private void moveTowardsPlayer(float dx, float dy, float distance) {
        if (distance > 5) {
            x += (dx / distance) * speed;
            y += (dy / distance) * speed;
        }
    }

    private void moveZigzag(float dx, float dy, float distance) {
        if (distance > 5) {
            zigzagAngle += 0.08f;  // скорость покачивания
            float zigzagOffset = (float) Math.sin(zigzagAngle) * 1.8f;

            float nx = dx / distance;
            float ny = dy / distance;

            x += (nx * speed) + (ny * zigzagOffset);
            y += (ny * speed) - (nx * zigzagOffset);
        }
    }

    private void moveCharge(float dx, float dy, float distance) {
        chargeTimer += 0.016f;

        if (!isCharging && chargeTimer > 1.2f) {
            isCharging = true;
            chargeTimer = 0f;
        }

        if (isCharging) {
            if (distance > 5) {
                x += (dx / distance) * (speed * 2.4f);
                y += (dy / distance) * (speed * 2.4f);
            }
            if (distance < 40) isCharging = false;
        } else {
            if (distance > 80) {
                x += (dx / distance) * (speed * 0.6f);
                y += (dy / distance) * (speed * 0.6f);
            }
        }
    }

    @Override
    public void setPosition(float newX, float newY){}

    public void setExperienceValue(int experienceValue) {
        this.experienceValue = experienceValue;
    }

    public float getHitboxRadius() {
        return hitboxRadius;
    }

    public void setHitboxRadius(float hitboxRadius) {
        this.hitboxRadius = hitboxRadius;
    }

    public int getExperienceValue() {
        return experienceValue;
    }

    public float getY() {
        return y;
    }

    public float getX() {
        return x;
    }

    public void setType(WalkType type) {
        this.type = type;
    }


    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void takeDamage(int damage) {
        this.curHP -= damage;
    }

    public boolean isDead() {
        return curHP <= 0;
    }

    public int getCurHP() {
        return curHP;
    }

    public void setCurHP(int curHP) {
        this.curHP = curHP;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public void setMaxHP(int maxHP) {
        this.maxHP = maxHP;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

}

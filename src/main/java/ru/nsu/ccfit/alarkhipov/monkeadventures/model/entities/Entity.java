package ru.nsu.ccfit.alarkhipov.monkeadventures.model.entities;

public interface Entity {
    void setPosition(float newX, float newY);
    void update(Player player);
}

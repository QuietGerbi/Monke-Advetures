package ru.nsu.ccfit.alarkhipov.monkeadventures.view.swing.game;

import java.awt.Image;

public class MapDecorationsSwing {

    private float x, y;
    private final Image image;
    private final int size;

    public MapDecorationsSwing(float x, float y, Image image, int size) {
        this.x = x;
        this.y = y;
        this.image = image;
        this.size = size;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public Image getImage() { return image; }
    public int getSize() { return size; }
}

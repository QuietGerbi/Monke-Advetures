package ru.nsu.ccfit.alarkhipov.monkeadventures.view.swing.game;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class BackgroundPanel extends JPanel {
    private final Image background;

    public BackgroundPanel() {
        background = new ImageIcon(Objects.requireNonNull(getClass().getResource("/backgrounds/menuBackground.png"))).getImage();
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
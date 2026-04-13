package ru.nsu.ccfit.alarkhipov.monkeadventures.view.swing.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nsu.ccfit.alarkhipov.monkeadventures.buttonSignals.ButtonSignal;
import ru.nsu.ccfit.alarkhipov.monkeadventures.controller.swing.MainMenuController;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MainMenuView {

    private static final Logger log = LogManager.getLogger(MainMenuView.class);
    private final JFrame frame = new JFrame();
    private JButton startButton;
    private JButton exitButton;
    private JButton aboutButton;
    private JButton scoresButton;
    private Font font;

    public MainMenuView(MainMenuController controller) {
        SwingUtilities.invokeLater(() -> {
            Toolkit toolKit = Toolkit.getDefaultToolkit();
            Dimension dimension = toolKit.getScreenSize();
            frame.setTitle("Monke Adventures");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setResizable(true);
            frame.setBounds(dimension.width/2-400, dimension.height/2-320, 1920, 1080);
            loadIcon();
            loadFont();
            frame.setLocationRelativeTo(null);

            BackgroundPanel mainPanel = new BackgroundPanel();
            mainPanel.setLayout(new BorderLayout());
            frame.setContentPane(mainPanel);

            JLabel title = new JLabel("MONKE ADVENTURES", SwingConstants.CENTER);
            title.setFont(font);
            title.setForeground(new Color(244, 228, 43));
            title.setBorder(BorderFactory.createEmptyBorder(80, 0, 40, 0));
            mainPanel.add(title, BorderLayout.NORTH);

            JPanel buttonsPanel = new JPanel();
            buttonsPanel.setLayout(new GridLayout(4, 1, 0, 30));
            buttonsPanel.setOpaque(false);
            buttonsPanel.setBorder(BorderFactory.createEmptyBorder(30, 400, 100, 400));

            startButton = createStyledButton("НАЧАТЬ ПРИКЛЮЧЕНИЕ");
            exitButton = createStyledButton("ВЫЙТИ");
            aboutButton = createStyledButton("О ИГРЕ");
            scoresButton = createStyledButton("РЕКОРД");

            buttonsPanel.add(startButton);
            buttonsPanel.add(aboutButton);
            buttonsPanel.add(scoresButton);
            buttonsPanel.add(exitButton);

            mainPanel.add(buttonsPanel, BorderLayout.CENTER);

            startButton.addActionListener(e -> controller.update(ButtonSignal.START, frame));
            exitButton.addActionListener(e -> controller.update(ButtonSignal.EXIT, frame));
            aboutButton.addActionListener(e -> controller.update(ButtonSignal.ABOUT, frame));
            scoresButton.addActionListener(e -> controller.update(ButtonSignal.SCORES, frame));

            frame.setVisible(true);
        });
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        Font buttonFont = this.font.deriveFont(42f);
        btn.setFont(buttonFont);
        btn.setForeground(new Color(244, 228, 43));
        btn.setBackground(new Color(161, 31, 31));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(172, 44, 44));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(161, 31, 31));
            }
        });
        return btn;
    }

    public void loadIcon(){
        try {
            Image icon = Toolkit.getDefaultToolkit().getImage(
                    getClass().getResource("/appIcon.png")
            );
            frame.setIconImage(icon);
            frame.setIconImages(java.util.List.of(icon));
        } catch (Exception e) {
            log.error("Sorry, app icon hasn't been loaded");
        }
    }

    public void loadFont() {
        try {
            var fontStream = getClass().getResourceAsStream("/fonts/RubikDirt-Regular.ttf");
            if (fontStream == null) {
                log.error("Font file not found in resources!");
                return;
            }
            this.font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
            this.font = font.deriveFont(Font.BOLD, 94f);

        } catch (Exception e) {
            log.error("Error loading font");
        }
    }
}

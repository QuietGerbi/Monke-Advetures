package ru.nsu.ccfit.alarkhipov.monkeadventures.controller.swing;

import ru.nsu.ccfit.alarkhipov.monkeadventures.ScoreManager;
import ru.nsu.ccfit.alarkhipov.monkeadventures.buttonSignals.ButtonSignal;
import ru.nsu.ccfit.alarkhipov.monkeadventures.view.swing.game.MainMenuView;

import javax.swing.*;

public class MainMenuController {

    public MainMenuController()
    {
        MainMenuView view = new MainMenuView(this);
    }

    public void update(ButtonSignal buttonSignal, JFrame context){
        switch (buttonSignal){
            case EXIT:
                System.exit(0);
            case START:
                context.dispose();
                new GameController(context);
                break;
            case ABOUT:
            String message = "<html><body style='width: 400px; padding: 10px;'>" +
                            "<h2 style='color: #2e7d32;'>Monke Adventures</h2>" +
                            "<p>Помогите обезьянке выжить!</p>" +
                            "<p>Бедная бибизянка зашла в чужой район и ей нужно победить остальных " +
                            "бибизян чтобы выжить и вернуться к своей семье. " +
                            "Благо она нашла посох монаха которым она сможет защититься</p>" +
                            "<hr>" +
                            "<b>Управление:</b><br>" +
                            "• WASD / Стрелки — Движение<br>" +
                            "• M — Включить/Выключить музыку<br>" +
                            "• , / . — Следующий/Предыдущий трек<br>" +
                            "• ESC — Выйти в главное меню<br>" +
                            "• B — Вызвать босса (если смелый)<br><br>" +
                            "</body></html>";
            JOptionPane.showMessageDialog(context, message, "О проекте", JOptionPane.INFORMATION_MESSAGE);
            break;
            case SCORES:
                ScoreManager sm = new ScoreManager();
                String best = sm.getBestTimeFormatted();

                JOptionPane.showMessageDialog(context,
                        "Ваш рекорд выживания: " + best,
                        "Личный рекорд",
                        JOptionPane.PLAIN_MESSAGE);
                break;
        }
    }


}
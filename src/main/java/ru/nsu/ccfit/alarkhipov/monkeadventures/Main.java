package ru.nsu.ccfit.alarkhipov.monkeadventures;

import ru.nsu.ccfit.alarkhipov.monkeadventures.controller.swing.MainMenuController;
import ru.nsu.ccfit.alarkhipov.monkeadventures.controller.text.GameController;

import javax.swing.*;
import java.util.Scanner;

public class Main{
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.println("Choose the way you want to play (or type q to quit):");
            System.out.println("gui | text");

            String type = in.nextLine();

            if (type.equalsIgnoreCase("gui")) {
                new MainMenuController();
                break;
            }
            else if (type.equalsIgnoreCase("text")) {
                new GameController();
                break;
            }
            else if (type.equalsIgnoreCase("q")) {
                System.out.println("Goodbye!");
                return;
            }
            else {
                System.out.println("Maybe you're wrong, type again!");
            }
        }
    }
}
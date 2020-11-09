package com.mario.gfx.gui;

import java.awt.Color;
import java.awt.Graphics;

import com.mario.Game;

public class Launcher {

    public Button[] buttons;

    public Launcher() {
        buttons = new Button[2];

        buttons[0] = new Button(Game.getFrameWidth() / 2 - 150, Game.getFrameHeight() / 2 - 100, 300, 100, "Start Game");
        buttons[1] = new Button(Game.getFrameWidth() / 2 - 150, Game.getFrameHeight() / 2 + 100, 300, 100, "Exit Game");
    }

    public void render(Graphics g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(0, 0, Game.getFrameWidth(), Game.getFrameHeight());

        for (int i = 0; i < buttons.length; i++) {
            buttons[i].render(g);
        }
    }

}

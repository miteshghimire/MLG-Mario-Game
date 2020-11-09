package com.mario.tile;

import java.awt.Color;
import java.awt.Graphics;

import com.mario.Game;
import com.mario.Handler;
import com.mario.Id;

public class Block extends Tile {

    public Block(int x, int y, int width, int height, boolean solid, Id id, Handler handler) {
        super(x, y, width, height, solid, id, handler);
    }

    public void render(Graphics g) {
        g.drawImage(Game.block.getBufferedImage(), x, y, width, height, null);
    }

    public void tick() {
    }
}

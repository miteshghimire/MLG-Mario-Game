package com.mario.entity;

import java.awt.Graphics;

import com.mario.Game;
import com.mario.Handler;
import com.mario.Id;
import com.mario.tile.Tile;

public class Coin extends Entity {

	public Coin(int x, int y, int width, int height, Id id, Handler handler) {
		super(x, y, width, height, id, handler);
	}

	public void render(Graphics g) {
		g.drawImage(Game.coin.getBufferedImage(),x,y,width,height,null);
	}

	public void tick() {
		
	}

}

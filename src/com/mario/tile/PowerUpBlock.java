package com.mario.tile;

import java.awt.Graphics;

import com.mario.Game;
import com.mario.Handler;
import com.mario.Id;
import com.mario.entity.powerup.Flower;
import com.mario.entity.powerup.Mushroom;
import com.mario.gfx.Sprite;

public class PowerUpBlock extends Tile {
	
	private Sprite powerUp;
	
	private boolean poppedUp = false;
	
	private int spriteY = getY();
	private int type;

	public PowerUpBlock(int x, int y, int width, int height, boolean solid, Id id, Handler handler, Sprite powerUp, int type) {
		super(x, y, width, height, solid, id, handler);
		this.type = type;
		this.powerUp = powerUp;
	}

	public void render(Graphics g) {
		if(!poppedUp) g.drawImage(powerUp.getBufferedImage(),x,spriteY,width,height,null);
		if(!activated) g.drawImage(Game.powerUp.getBufferedImage(),x,y,width,height,null);
		else g.drawImage(Game.usedPowerUp.getBufferedImage(),x,y,width,height,null);
	}

	public void tick() {
		if(activated&&!poppedUp) {
			spriteY--;
			if(spriteY<=y-height) {
				if(powerUp==Game.mushroom||powerUp==Game.lifeMushroom) handler.addEntity(new Mushroom(x,spriteY,width,height,Id.mushroom,handler,type));
				else if(powerUp==Game.flower) handler.addEntity(new Flower(x,spriteY,width,height,Id.flower,handler));
				poppedUp = true;
			}
		}
	}

}

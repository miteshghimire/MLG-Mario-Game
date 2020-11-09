package com.mario.entity.mob;

import java.awt.Color;
import java.awt.Graphics;

import com.mario.Handler;
import com.mario.Id;
import com.mario.entity.Entity;

public class Plant extends Entity {
	
	private int wait;
	private int pixelsTravelled;
	
	private boolean moving;
	private boolean insidePipe;

	public Plant(int x, int y, int width, int height, Id id, Handler handler) {
		super(x, y, width, height, id, handler);
		
		moving = false;
		insidePipe = true;
	}

	public void render(Graphics g) {
		g.setColor(Color.RED);
		g.fillRect(getX(), getY(), width, height);
	}

	public void tick() {
		y+=velY;
		
		if(!moving) wait++;
		
		if(wait>=120) {
			if(insidePipe) insidePipe = false;
			else insidePipe = true;
			
			moving = true;
			wait = 0;
		}
		
		if(moving) {
			if(insidePipe) setVelY(-2);
			else setVelY(2);
			
			pixelsTravelled+=velY;
			
			if(pixelsTravelled>=getHeight()||pixelsTravelled<=-getHeight()) {
				pixelsTravelled = 0;
				moving = false;
				
				setVelY(0);
			}
		}
	}

}

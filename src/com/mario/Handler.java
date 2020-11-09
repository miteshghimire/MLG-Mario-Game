package com.mario;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import com.mario.entity.Coin;
import com.mario.entity.Entity;
import com.mario.entity.mob.Koopa;
import com.mario.entity.mob.Player;
import com.mario.tile.Flag;
import com.mario.tile.Pipe;
import com.mario.tile.PowerUpBlock;
import com.mario.tile.Tile;
import com.mario.tile.Wall;

public class Handler {
	
	public LinkedList<Entity> entity = new LinkedList<Entity>();
	public LinkedList<Tile> tile = new LinkedList<Tile>();
	
	public void render(Graphics g) {
		for(int i=0;i<entity.size();i++) {
			Entity e = entity.get(i);
			if(Game.getVisibleArea()!=null&&e.getBounds().intersects(Game.getVisibleArea())&&e.getId()!=Id.particle) e.render(g);
		}
		
		for(int i=0;i<tile.size();i++) {
			Tile t = tile.get(i);
			if(Game.getVisibleArea()!=null&&t.getBounds().intersects(Game.getVisibleArea())) t.render(g);
		}
		
		for(int i=0;i<entity.size();i++) {
			Entity e = entity.get(i);
			if(Game.getVisibleArea()!=null&&e.getBounds().intersects(Game.getVisibleArea())&&e.getId()==Id.particle) e.render(g);
		}
		
		g.drawImage(Game.coin.getBufferedImage(), Game.getVisibleArea().x+20, Game.getVisibleArea().y+20, 75, 75, null);
		g.drawImage(Game.lifeMushroom.getBufferedImage(), Game.getVisibleArea().x+20, Game.getVisibleArea().y+100, 75, 75, null);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Courier", Font.BOLD, 20));
		g.drawString("x " + Game.coins, Game.getVisibleArea().x+100, Game.getVisibleArea().y+95);
		g.drawString("x" + Game.lives, Game.getVisibleArea().x+100, Game.getVisibleArea().y+175);
	}
	
	public void tick() {
		for(int i=0;i<entity.size();i++) {
			Entity e = entity.get(i);
			e.tick();
		}
		
		for(int i=0;i<tile.size();i++) {
			Tile t = tile.get(i);
			if(Game.getVisibleArea()!=null&&t.getBounds().intersects(Game.getVisibleArea())) t.tick();
		}
	}
	
	public void addEntity(Entity e) {
		entity.add(e);
	}
	
	public void removeEntity(Entity e) {
		entity.remove(e);
	}
	
	public void addTile(Tile t) {
		tile.add(t);
	}
	
	public void removeTile(Tile t) {
		tile.remove(t);
	}
	
	public void createLevel(BufferedImage level) {
		int width = level.getWidth();
		int height = level.getHeight();
		
		for(int y=0;y<height;y++) {
			for(int x = 0;x<width;x++) {
				int pixel = (int) level.getRGB(x, y);
				
				int red = (pixel >> 16) & 0xff;
				int green = (pixel >> 8) & 0xff;
				int blue = (pixel) & 0xff;
				
				if(red==0&&green==0&&blue==0) addTile(new Wall(x*64,y*64,64,64,true,Id.wall,this));
				if(red==0&&green==0&&blue==255) addEntity(new Player(x*64,y*64,48,48,Id.player,this));
				if(red==255&green==0&blue==0) addEntity(new Koopa(x*64,y*64,64,64,Id.koopa,this));
				if(red==0&&(green>=125&&green<=128)&&blue==0) addTile(new Pipe(x*64,y*64,64,64*15,true,Id.pipe,this,128-green,true));
				if(red==255&&green==255&&blue==0) addEntity(new Coin(x*64,y*64,64,64,Id.coin,this));
				if(red==255&green==128&&blue==0) addTile(new PowerUpBlock(x*64,y*64,64,64,true,Id.powerUp,this,Game.flower,0));
				if(red==0&green==255&&blue==0) addTile(new Flag(x*64,y*64,64,64*5,true,Id.flag,this));
			}
		}
		
		//Game.deathY = Game.getDeathY();
	}
	
	public void clearLevel() {
		entity.clear();
		tile.clear();
	}

}

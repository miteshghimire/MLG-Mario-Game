package com.mario;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.mario.entity.Entity;
import com.mario.gfx.Sprite;
import com.mario.gfx.SpriteSheet;
import com.mario.gfx.gui.Launcher;
import com.mario.input.KeyInput;
import com.mario.input.MouseInput;
import com.mario.tile.Tile;

public class Game extends Canvas implements Runnable {

	public static final int WIDTH = 320;
	public static final int HEIGHT = 180;
	public static final int SCALE = 4;
	private static final String TITLE = "Mario Java Tutorial";
	
	private Thread thread;
	private boolean running = false;
	
	private static BufferedImage[] levels;
	
	private static BufferedImage background;
	
	private static int playerX, playerY;
	private static int level = 0;
	
	public static int coins = 0;
	public static int lives = 5;
	public static int deathScreenTime = 0;
	
	public static boolean showDeathScreen = true;
	public static boolean gameOver = false;
	public static boolean playing = false;
	
	public static Handler handler;
	public static SpriteSheet sheet;
	public static Camera cam;
	public static Launcher launcher;
	
	public static KeyInput key;
	public static MouseInput mouse;
	
    public static Sprite grass;
    public static Sprite block;
    public static Sprite powerUp;
    public static Sprite usedPowerUp;
    
    public static Sprite mushroom;
    public static Sprite lifeMushroom;
    public static Sprite coin;
    public static Sprite star;
    public static Sprite fireball;
    public static Sprite flower;
    
    public static Sprite[] player;
    public static Sprite[] goomba;
    public static Sprite[] flag;
    public static Sprite[] particle;
    public static Sprite[] firePlayer;
    
    public static Sound jump;
    public static Sound damage;
    public static Sound death;
    public static Sound powerup;
    public static Sound win;
    
    public Game() {
        Dimension size = new Dimension(WIDTH*SCALE,HEIGHT*SCALE);
        setPreferredSize(size);
        setMaximumSize(size);
        setMinimumSize(size);
    }
    
    private void init() {
        handler = new Handler();
        sheet = new SpriteSheet("/spritesheet.png");
        cam = new Camera();
        launcher = new Launcher();
        
        key = new KeyInput();
        mouse = new MouseInput();
        
        addKeyListener(key);
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
        
        grass = new Sprite(1,1,sheet);
        powerUp = new Sprite(3,1,sheet);
        usedPowerUp = new Sprite(4,1,sheet);
        
        mushroom = new Sprite(2,1,sheet);
        lifeMushroom = new Sprite(6,1,sheet);
        coin = new Sprite(5,1,sheet);
        star = new Sprite(7, 1, sheet);
        fireball = new Sprite(9,1,sheet);
        flower = new Sprite(8,1,sheet);
        
        player = new Sprite[8];
        goomba = new Sprite[8];
        flag = new Sprite[3];
        particle = new Sprite[6];
        firePlayer = new Sprite[8];
        
        levels = new BufferedImage[2];
        
        for(int i=0;i<player.length;i++) {
            player[i] = new Sprite(i+1,16,sheet);
        }
        
        for(int i=0;i<goomba.length;i++) {
            goomba[i] = new Sprite(i+1,15,sheet);
        }
        
        for(int i=0;i<flag.length;i++) {
        	flag[i] = new Sprite(i+1,2,sheet);
        }
        
        for(int i=0;i<particle.length;i++) {
        	particle[i] = new Sprite(i+1,14,sheet);
        }
 
        for(int i=0;i<firePlayer.length;i++) {
        	firePlayer[i] = new Sprite(i+9,16,sheet);
        }
        
        try {
        	levels[0] = ImageIO.read(getClass().getResource("/level.png"));
        	levels[1] = ImageIO.read(getClass().getResource("/level2.png"));
        	background = ImageIO.read(getClass().getResource("/background.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        jump = new Sound("/audio/jump.wav");
        damage = new Sound("/audio/damage.wav");
        death = new Sound("/audio/death.wav");
        powerup = new Sound("/audio/power.wav");
        win = new Sound("/audio/win.wav");
        
    }
	private synchronized void start() {
		if(running) return;
		running = true;
		thread = new Thread(this,"Thread");
		thread.start();
	}
	
	private synchronized void stop() {
		if(!running) return;
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		init();
		requestFocus();
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		double ns = 1000000000.0/60.0;
		double delta = 0;
		int frames = 0;
		int ticks = 0;
		while(running) {
			long now = System.nanoTime();
			delta+=(now-lastTime)/ns;
			lastTime = now;
			while(delta>=1) {
				tick();
				ticks++;
				delta--;
			}
			render();
			frames++;
			if(System.currentTimeMillis()-timer>1000) {
				timer+=1000;
				System.out.println(frames + " Frames Per Second " + ticks + " Ticks Per Second");
				frames = 0;
				ticks = 0;
			}
		}
		stop();
	}
	
	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if(bs==null) {
			createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();

		g.drawImage(Game.coin.getBufferedImage(), 20, 20, 75, 75, null);
		g.drawImage(Game.lifeMushroom.getBufferedImage(), 20, 100, 75, 75, null);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Courier", Font.BOLD, 20));
		g.drawString("x " + Game.coins, 100, 95);
		g.drawString("x" + Game.lives, 100, 175);
		
		if(!showDeathScreen) {
			g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
		} else {
			g.setColor(Color.BLACK);
			g.fillRect(0,0,getWidth(),getHeight());
			
			g.setColor(Color.WHITE);
			g.setFont(new Font("Courier", Font.BOLD, 50));
			if(!gameOver) {
				g.drawImage(player[0].getBufferedImage(), 500, 300, 100, 100, null);
				g.drawString("x " + lives, 610, 400);
			} else g.drawString("Game over. :(", 500, 400);
		}
		
		if(playing) g.translate(cam.getX(), cam.getY());
		if(!showDeathScreen&&playing) handler.render(g);
		else if(!playing) launcher.render(g);
		g.dispose();
		bs.show();
	}
	
	public void tick() {
		if(playing) handler.tick();
		
		for(int i=0;i<handler.entity.size();i++) {
			Entity e = handler.entity.get(i);
			if(e.getId()==Id.player) {
				if(!e.goingDownPipe) cam.tick(e);
			}
		}
		
		if(showDeathScreen&&!gameOver&&playing) deathScreenTime++;
		if(deathScreenTime>=180) {
			deathScreenTime = 0;
			handler.clearLevel();
			handler.createLevel(levels[level]);
			showDeathScreen = false;
		}
	}
	
	public static int getFrameWidth() {
		return WIDTH*SCALE;
	}
	
	public static int getFrameHeight() {
		return HEIGHT*SCALE;
	}
	
	public static void switchLevel() {
		Game.level++;
		
		handler.clearLevel();
		handler.createLevel(levels[1]);
	}
	
	public static Rectangle getVisibleArea() {
		for(int i=0;i<handler.entity.size();i++) {
			Entity e = handler.entity.get(i);
			if(e.getId()==Id.player) {
				if(!e.goingDownPipe) {
					playerX = e.getX();
					playerY = e.getY();
					
					return new Rectangle(playerX-(getFrameWidth()/2-5),playerY-(getFrameHeight()/2-5),getFrameWidth()+10,getFrameHeight()+10);
				}
			}
		}
		
		return new Rectangle(playerX-(getFrameWidth()/2-5),playerY-(getFrameHeight()/2-5),getFrameWidth()+10,getFrameHeight()+10);
	}
	
	public static int getDeathY() {
		LinkedList<Tile> tempList = handler.tile;
		
		Comparator<Tile> tileSorter = new Comparator<Tile>() {

			public int compare(Tile t1, Tile t2) {
				if(t1.getY()>t2.getY()) return -1;
				if(t1.getY()<t2.getY()) return 1;
				return 0;
			}
			
		};
		
		Collections.sort(tempList,tileSorter);
		return tempList.getFirst().getY() + tempList.getFirst().getHeight();
	}
	
	public static void main(String[] args) {
		Game game = new Game();
		JFrame frame = new JFrame(TITLE);
		frame.add(game);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		game.start();
	}

}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package com.mario.entity.mob;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

import com.mario.Handler;
import com.mario.Id;
import com.mario.entity.Entity;
import com.mario.states.BossState;
import com.mario.tile.Tile;

public class TowerBoss extends Entity {
	
	public int jumpTime = 0;
	
	public boolean addJumpTime = false;
	
	private Random random;

	public TowerBoss(int x, int y, int width, int height, Id id, Handler handler, int hp) {
		super(x, y, width, height, id, handler);
		this.hp = hp;
		
		bossState = BossState.IDLE;
		
		random = new Random();
	}

	public void render(Graphics g) {
		if(bossState==BossState.IDLE||bossState==BossState.SPINNING) g.setColor(Color.GRAY);
		else if(bossState==BossState.RECOVERING) g.setColor(Color.RED);
		else g.setColor(Color.ORANGE);
		
		g.fillRect(x,y,width,height);
	}

	public void tick() {
		x+=velX;
		y+=velY;
		
		if(hp<=0) die();
		
		phaseTime++;
		
		if((phaseTime>=180&&bossState==BossState.IDLE)||(phaseTime>=600&&bossState!=BossState.SPINNING)) chooseState();
		
		if(bossState==BossState.RECOVERING&&phaseTime>=180) {
			bossState = bossState.SPINNING;
			phaseTime = 0;
		}
		
		if(phaseTime>=360&&bossState==BossState.SPINNING) {
			phaseTime = 0;
			bossState = BossState.IDLE;
		}
		
		if(bossState==BossState.IDLE||bossState==BossState.RECOVERING) {
			setVelX(0);
			setVelY(0);
		}
		
		if(bossState==BossState.JUMPING||bossState==BossState.RUNNING) attackable = true;
		else attackable = false;
		
		if(bossState!=BossState.JUMPING) {
			addJumpTime = false;
			jumpTime = 0;
		}
		
		if(addJumpTime) {
			jumpTime++;
			if(jumpTime>=30) {
				addJumpTime = false;
				jumpTime = 0;
			}
			
			if(!jumping&&!falling) {
				jumping = true;
				gravity = 8.0;
			}
		}
		
		for(int i=0;i<handler.tile.size();i++) {
			Tile t = handler.tile.get(i);
			if(t.isSolid()&&!goingDownPipe) {
				if(getBoundsTop().intersects(t.getBounds())) {
					setVelY(0);
					if(jumping) {
						jumping = false;
						gravity = 0.8;
						falling = true;
					}
				}
				if(getBoundsBottom().intersects(t.getBounds())) {
					setVelY(0);
					if(falling) {
						falling = false;
						addJumpTime = true;
					}
				}
				if(getBoundsLeft().intersects(t.getBounds())) {
					setVelX(0);
					if(bossState==BossState.RUNNING) setVelX(4);
					x = t.getX()+t.width;
				}
				if(getBoundsRight().intersects(t.getBounds())) {
					setVelX(0);
					if(bossState==BossState.RUNNING) setVelX(-4);
					x = t.getX()-t.width;
				}
			}
		}
		
		for(int i=0;i<handler.entity.size();i++) {
			Entity e = handler.entity.get(i);
			if(e.getId()==Id.player) {
				if(bossState==BossState.JUMPING) {
					if(jumping||falling) {
						if(getX()>=e.getX()-4&&getX()<=e.getX()+4) setVelX(0);
						else if(e.getX()<getX()) setVelX(-3);
						else if(e.getX()>getX()) setVelX(3);
					} else setVelX(0);
				} else if(bossState==BossState.SPINNING) {
					if(e.getX()<getX()) setVelX(-3);
					else if(e.getX()>getX()) setVelX(3);
				}
			}
		}
		
		if(jumping&&!goingDownPipe) {
			gravity-=0.17;
			setVelY((int)-gravity);
			if(gravity<=0.5) {
				jumping = false;
				falling = true;
			}
		}	
		if(falling&&!goingDownPipe) {
			gravity+=0.17;
			setVelY((int)gravity);
		}
	}
	
	public void chooseState() {
		int nextPhase = random.nextInt(2);
		if(nextPhase==0) {
			bossState = BossState.RUNNING;
			int dir = random.nextInt(2);
			if(dir==0) setVelX(-4);
			else setVelX(4);
		} else if(nextPhase==1) {
			bossState = BossState.JUMPING;
			
			jumping = true;
			gravity = 8.0;
		}
		
		phaseTime = 0;
	}

}

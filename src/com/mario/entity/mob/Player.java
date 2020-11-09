package com.mario.entity.mob;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;

import com.mario.Game;
import com.mario.Handler;
import com.mario.Id;
import com.mario.entity.Entity;
import com.mario.entity.Particle;
import com.mario.states.BossState;
import com.mario.states.KoopaState;
import com.mario.states.PlayerState;
import com.mario.tile.Tile;
import com.mario.tile.Trail;

public class Player extends Entity {

    private int pixelsTravelled = 0;
    private int invincibilityTime = 0;
    private int particleDelay = 0;
    private int restoreTime;

    private Random random;

    private boolean restoring;

    public Player(int x, int y, int width, int height, Id id, Handler handler) {
        super(x, y, width, height, id, handler);

        state = PlayerState.SMALL;

        random = new Random();
    }

    public void render(Graphics g) {
        if (state == PlayerState.FIRE) {
            if (facing == 0) {
                g.drawImage(Game.firePlayer[4 + frame].getBufferedImage(), x, y, width, height, null);
            } else if (facing == 1) {
                g.drawImage(Game.firePlayer[frame].getBufferedImage(), x, y, width, height, null);
            }
        } else if (facing == 0) {
            g.drawImage(Game.player[4 + frame].getBufferedImage(), x, y, width, height, null);
        } else if (facing == 1) {
            g.drawImage(Game.player[frame].getBufferedImage(), x, y, width, height, null);
        }

        /*Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.RED);
		g2.draw(getBoundsRight());
		g2.draw(getBoundsTop());*/
    }

    public void tick() {
        x += velX;
        y += velY;

        //if(getY()>Game.deathY) die();
        if (invincible) {
            if (facing == 0) {
                handler.addTile(new Trail(getX(), getY(), getWidth(), getHeight(), false, Id.trail, handler, Game.player[4 + frame].getBufferedImage()));
            } else if (facing == 1) {
                handler.addTile(new Trail(getX(), getY(), getWidth(), getHeight(), false, Id.trail, handler, Game.player[frame].getBufferedImage()));
            }

            particleDelay++;

            if (particleDelay >= 3) {
                particleDelay = 0;

                handler.addEntity(new Particle(getX() + random.nextInt(getWidth()), getY() + random.nextInt(getHeight()), 8, 8, Id.particle, handler));
            }

            if (velX == -5) {
                setVelX(-8);
            }
            if (velX == 5) {
                setVelX(8);
            }

            invincibilityTime++;
            if (invincibilityTime >= 600) {
                invincible = false;
                invincibilityTime = 0;
            }
        } else {
            if (velX == -8) {
                setVelX(-5);
            }
            if (velX == 8) {
                setVelX(5);
            }
        }

        if (restoring) {
            restoreTime++;

            if (restoreTime >= 90) {
                restoring = false;
                restoreTime = 0;
            }
        }

        for (int i = 0; i < handler.tile.size(); i++) {
            Tile t = handler.tile.get(i);
            if (t.isSolid() && !goingDownPipe) {

                if (getBounds().intersects(t.getBounds())) {
                    if (t.getId() == Id.flag) {
                        Game.switchLevel();
                        Game.win.play();
                    }
                }

                if (getBoundsTop().intersects(t.getBounds())) {
                    setVelY(0);
                    if (jumping && !goingDownPipe) {
                        jumping = false;
                        gravity = 0.8;
                        falling = true;
                    }
                    if (t.getId() == Id.powerUp) {
                        if (getBoundsTop().intersects(t.getBounds())) {
                            t.activated = true;
                        }
                    }
                }
                if (getBoundsBottom().intersects(t.getBounds())) {
                    setVelY(0);
                    if (falling) {
                        falling = false;
                    }
                } else if (!falling && !jumping) {

                    System.out.println("ah shit, nooh :(");
                    falling = true;
                    gravity = 0.8;
                }
                if (getBoundsLeft().intersects(t.getBounds())) {
                    setVelX(0);
                    x = t.getX() + t.getWidth();
                }
                if (getBoundsRight().intersects(t.getBounds())) {
                    setVelX(0);
                    x = t.getX() - width;
                }
            }
        }

        for (int i = 0; i < handler.entity.size(); i++) {
            Entity e = handler.entity.get(i);

            if (e.getId() == Id.mushroom) {
                switch (e.getType()) {
                    case 0:
                        if (getBounds().intersects(e.getBounds())) {
                            int tpX = getX();
                            int tpY = getY();
                            width += (width / 3);
                            height += (height / 3);
                            setX(tpX - width);
                            setY(tpY - height);
                            if (state == PlayerState.SMALL) {
                                state = PlayerState.BIG;
                            }
                            Game.powerup.play();
                            e.die();
                        }
                        break;
                    case 1:
                        if (getBounds().intersects(e.getBounds())) {
                            Game.lives++;
                            e.die();
                            Game.powerup.play();
                        }
                }

            } else if (e.getId() == Id.goomba || e.getId() == Id.towerBoss || e.getId() == Id.plant) {
                if (invincible && getBounds().intersects(e.getBounds())) {
                    e.die();
                    Game.damage.play();
                } else if (getBoundsBottom().intersects(e.getBoundsTop())) {
                    if (e.getId() == Id.goomba) {
                        e.die();
                        Game.damage.play();
                    } else if (e.attackable) {
                        Game.damage.play();
                        e.hp--;
                        e.falling = true;
                        e.gravity = 3.0;
                        e.bossState = BossState.RECOVERING;
                        e.attackable = false;
                        e.phaseTime = 0;

                        jumping = true;
                        falling = false;
                        gravity = 3.5;
                    }
                } else if (getBounds().intersects(e.getBounds())) {
                    if (state == PlayerState.BIG) {
                        takeDamage();
                    }
                }
            } else if (e.getId() == Id.coin) {
                if (getBounds().intersects(e.getBounds()) && e.getId() == Id.coin) {
                    Game.coins++;
                    e.die();
                }
            } else if (e.getId() == Id.koopa) {
                if (invincible && getBounds().intersects(e.getBounds())) {
                    e.die();
                    Game.damage.play();
                    Game.powerup.play();
                } else if (e.koopaState == KoopaState.WALKING) {
                    if (getBoundsBottom().intersects(e.getBoundsTop())) {
                        e.koopaState = KoopaState.SHELL;
                        Game.damage.play();

                        jumping = true;
                        falling = false;
                        gravity = 3.5;
                    } else if (getBounds().intersects(e.getBounds())) {
                        takeDamage();
                    }
                } else if (e.koopaState == KoopaState.SHELL) {
                    if (getBoundsBottom().intersects(e.getBoundsTop())) {
                        e.koopaState = KoopaState.SPINNING;

                        int dir = random.nextInt(2);

                        switch (dir) {
                            case 0:
                                e.setVelX(-10);
                                break;
                            case 1:
                                e.setVelX(10);
                                break;
                        }

                        jumping = true;
                        falling = false;
                        gravity = 3.5;
                    }

                    if (getBoundsLeft().intersects(e.getBoundsRight())) {
                        e.setVelX(-10);
                        e.koopaState = KoopaState.SPINNING;
                        Game.damage.play();
                    }

                    if (getBoundsRight().intersects(e.getBoundsLeft())) {
                        e.setVelX(10);
                        e.koopaState = KoopaState.SPINNING;
                        Game.damage.play();
                    }

                } else if (e.koopaState == KoopaState.SPINNING) {
                    if (getBoundsBottom().intersects(e.getBoundsTop())) {
                        e.koopaState = KoopaState.SHELL;

                        jumping = true;
                        falling = false;
                        gravity = 3.5;
                    } else if (getBounds().intersects(e.getBounds())) {
                        takeDamage();
                    }
                }
            } else if (e.getId() == Id.star) {
                if (getBounds().intersects(e.getBounds())) {
                    invincible = true;
                    Game.powerup.play();

                    e.die();
                }
            } else if (e.getId() == Id.flower) {
                if (getBounds().intersects(e.getBounds())) {
                    if (state == PlayerState.SMALL) {
                        int tpX = getX();
                        int tpY = getY();
                        width += (width / 3);
                        height += (height / 3);
                        setX(tpX - width);
                        setY(tpY - height);
                    }

                    state = PlayerState.FIRE;

                    e.die();
                }
            }
        }

        if (jumping && !goingDownPipe) {
            gravity -= 0.17;
            setVelY((int) -gravity);
            if (gravity <= 0.5) {
                jumping = false;
                falling = true;
            }
        }
        if (falling && !goingDownPipe) {
            gravity += 0.17;
            setVelY((int) gravity);
        }

        if (velX != 0) {
            frameDelay++;
            if (frameDelay >= 10) {
                frame++;
                if (frame > 3) {
                    frame = 0;
                }
                frameDelay = 0;
            }
        }

        if (goingDownPipe) {
            for (int i = 0; i < Game.handler.tile.size(); i++) {
                Tile t = Game.handler.tile.get(i);
                if (t.getId() == Id.pipe) {
                    if (getBounds().intersects(t.getBounds())) {
                        switch (t.facing) {
                            case 0:
                                setVelY(-5);
                                setVelX(0);
                                pixelsTravelled += -velY;
                                break;
                            case 2:
                                setVelY(5);
                                setVelX(0);
                                pixelsTravelled += velY;
                                break;
                        }
                        if (pixelsTravelled > t.height) {
                            goingDownPipe = false;
                            pixelsTravelled = 0;
                        }
                    }
                }
            }
        }
    }

    public void takeDamage() {
        if (restoring) {
            return;
        }

        if (state == PlayerState.SMALL) {
            die();
            return;
        } else if (state == PlayerState.BIG) {
            width -= (width / 4);
            height -= (height / 4);
            x += width / 4;
            y += height / 4;

            state = PlayerState.SMALL;
            Game.damage.play();

            restoring = true;
            restoreTime = 0;
            return;
        } else if (state == PlayerState.FIRE) {
            state = PlayerState.BIG;
            Game.damage.play();

            restoring = true;
            restoreTime = 0;
            return;
        }
    }

}


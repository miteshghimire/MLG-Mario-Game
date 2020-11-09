/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mario;

/**
 *
 * @author Siddharda
 */
import com.mario.entity.Entity;
public class Camera {
    private int x;
    private int y;
    
    
    public void tick(Entity player){
        setX( -player.getX() + Game.getFrameWidth ()/2);
        setY( -player.getY() + Game.getFrameHeight()/2);
    }
    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(int y) {
        this.y = y;
    }

   
}





























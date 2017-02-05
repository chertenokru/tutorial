/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.chertenok.tutorial;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author 13th
 */
public class Player {

    private int x;
    private int y;
    private int r;

    private int dx;
    private int dy;
    private int speed;

    private boolean left;
    private boolean rigth;
    private boolean up;
    private boolean down;

    private boolean firing;
    private long firingTimer;
    private long firingDelay;

    private boolean recovering;
    private long recoveryTimer;

    private int lives;
    private Color color1;
    private Color color2;

    private int scope;
    
    public Player() {
        x = GamePanel.WIDTH / 2;
        y = GamePanel.HEIGHT / 2;
        r = 5;

        dx = 0;
        dy = 0;
        speed = 5;

        lives = 3;
        color1 = Color.WHITE;
        color2 = Color.RED;
        
        
        
        firing = false;
        firingTimer = System.nanoTime();
        firingDelay = 200;

        recovering = false;
        recoveryTimer = 0;

    }

    public void update() {
        if (left) {
            dx = -speed;
        }
        if (rigth) {
            dx = speed;
        }
        if (up) {
            dy = -speed;
        }
        if (down) {
            dy = speed;
        }

        x += dx;
        y += dy;

        if (x < r) {
            x = r;
        }
        if (y < r) {
            y = r;
        }
        if (x > GamePanel.WIDTH - r) {
            x = GamePanel.WIDTH - r;
        }
        if (y > GamePanel.HEIGHT - r) {
            y = GamePanel.HEIGHT - r;
        }

        dx = 0;
        dy = 0;

        if (firing) {
            long elapsed = (System.nanoTime() - firingTimer) / 1000000;
            if (elapsed > firingDelay) {
                GamePanel.bullets.add(new Bullet(270, x, y));
                firingTimer = System.nanoTime();
            }
        }

        long elapsed = (System.nanoTime() - recoveryTimer) / 10000000;
        if (elapsed > 200) {
            recovering = false;
            recoveryTimer = 0;
        }
    }

    public void draw(Graphics2D g) {
        if (recovering) {
            g.setColor(color2);
            g.fillOval(x - r, y - r, 2 * r, 2 * r);

            g.setStroke(new BasicStroke(3));
            g.setColor(color2.darker());
            g.drawOval(x - r, y - r, 2 * r, 2 * r);
            g.setStroke(new BasicStroke(1));
        } else {
            g.setColor(color1);
            g.fillOval(x - r, y - r, 2 * r, 2 * r);

            g.setStroke(new BasicStroke(3));
            g.setColor(color1.darker());
            g.drawOval(x - r, y - r, 2 * r, 2 * r);
            g.setStroke(new BasicStroke(1));
        }

    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public void setRigth(boolean rigth) {
        this.rigth = rigth;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public void setFiring(boolean firing) {
        this.firing = firing;
    }

    public int getLives() {
        return lives;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getR() {
        return r;
    }

    public boolean isRecovering() {
        return recovering;
    }

    public void loseLife() {
        lives--;
        recovering = true;
        recoveryTimer = System.nanoTime();
    }

    public int getScope() {
        return scope;
    }

    public void addScope(int i)
    {
        scope +=i;
    }
}

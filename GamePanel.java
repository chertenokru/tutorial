/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.chertenok.tutorial;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author 13th
 */
class GamePanel extends JPanel implements Runnable, KeyListener {

    public static int HEIGHT = 600;
    public static int WIDTH = 800;
    private boolean running;
    private Thread thread;

    private BufferedImage image;
    private Graphics2D g;

    private int FPS = 30;
    private double averageFPS;

    public static Player player;
    public static ArrayList<Bullet> bullets;
    public static ArrayList<Enemy> enemies;
    
    private long waveStartTimer;
    private long waveStartTimerDiff;
    private int waveNumber;
    private boolean waveStart;
    private int waveDelay = 2000;

    public GamePanel() {
        super();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
        addKeyListener(this);
    }

    @Override
    public void run() {
        running = true;
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        

        player = new Player();
        bullets = new ArrayList<Bullet>();
        enemies = new ArrayList<Enemy>();
        
        waveStartTimer = 0;
        waveStartTimerDiff = 0;
        waveStart = true;
        waveNumber = 0;
        
        
        long startTime;
        long URDTTimeMillis;
        long waitTime;
        long totalTime = 0;

        int frameCount = 0;
        int maxFrameCount = 30;

        long targetTime = 1000 / FPS;

        while (running) {
            startTime = System.nanoTime();

            gameUpdate();
            gameRender();
            gameDraw();

            URDTTimeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - URDTTimeMillis;
            try {
                Thread.sleep(waitTime);
            } catch (Exception e) {
            }

            totalTime += System.nanoTime() - startTime;
            frameCount++;
            if (frameCount == maxFrameCount) {
                averageFPS = 1000.0 / ((totalTime / frameCount) / 1000000);
                frameCount = 0;
                totalTime = 0;
            }
        }
    }

    private void gameUpdate() {
        
        //new wave
        if (waveStartTimer == 0 && enemies.size() == 0)
        {
            waveNumber++;
            waveStart = false;
            waveStartTimer = System.nanoTime();
        }
        else
        {
            waveStartTimerDiff = (System.nanoTime()-waveStartTimer) / 1000000;
            if (waveStartTimerDiff > waveDelay)
            {
               waveStart = true;
               waveStartTimer = 0;
               waveStartTimerDiff = 0;
            }
            
        }
        
        //create enemies
        if (waveStart && enemies.size() == 0)
        {
            createNewEnemies();
        }
        
        // player update
        player.update();
        
        // bullets update
        for (int i =0; i < bullets.size(); i++)
        {
            boolean remove = bullets.get(i).update();
            if (remove) 
            {
                bullets.remove(i);
                i--;
            }
        }
        
        // enemies update
        for(Enemy e:enemies)
        {
            e.update();
                    
        }
        
        // bullet-enemy collision
        for(int i = 0; i < bullets.size(); i++)
        {
          Bullet b = bullets.get(i);
          double bx = b.getX();
          double by = b.getY();
          double br = b.getR();
          
          for (int j = 0; j < enemies.size(); j++)
          {
              Enemy e = enemies.get(j);
              double ex = e.getX();
              double ey = e.getY();
              double er = e.getR();
              
              double dx = bx - ex;
              double dy = by - ey;
              double dist = Math.sqrt(dx * dx + dy * dy);
              
              if (dist < br + er)
              {
                e.hit();
                bullets.remove(i);
                i--;
                break;
              }
              
          }
        }
        
        // check dead enemies
        for(int i = 0; i <enemies.size(); i++)
        {
            if (enemies.get(i).isDead())
            {
                Enemy e =enemies.get(i);
                player.addScope(e.getRank()+e.getType());
                enemies.remove(i);
                i--;
            }
        }
        
        // player-enemy collision
        if (!player.isRecovering())
        {
            int px = player.getX();
            int py = player.getY();
            int pr = player.getR();
            for(int i = 0; i <enemies.size();i++)
            {
                Enemy e = enemies.get(i);
                double ex = e.getX();
                double ey = e.getY();
                double er = e.getR();
                
                double dx = px - ex;
                double dy = py - ey;
                double dist = Math.sqrt(dx*dx + dy*dy);
                
                if (dist < pr + er)
                {
                    player.loseLife();
                }
                      
                {
                    
                }
            }
        }
        
    }

    private void gameRender() {
        g.setColor(new Color(0,100,255));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        player.draw(g);
        
        for(Bullet b:bullets) b.draw(g);
        for(Enemy e:enemies)  e.draw(g);
        
       // draw wave number 
       if (waveStartTimer != 0)
       {
           g.setFont(new Font("Century Gothic", Font.PLAIN, 18));
           String s = "-  W A V E   "+waveNumber+"    -";
           int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
           int alpha = (int) (255 * Math.sin(3.14*waveStartTimerDiff/ waveDelay));
           g.setColor(new Color(255, 255, 255, alpha));
           g.drawString(s, WIDTH / 2 - length / 2, HEIGHT / 2);
       }
        
       //draw players lives
       for( int i = 0; i < player.getLives(); i++)
       {
           g.setColor(Color.white);
           g.fillOval(20 + (20*i), 20, player.getR()*2, player.getR()*2);
           g.setColor(Color.white.darker());
           g.setStroke(new BasicStroke(3));
           g.drawOval(20 + (20*i), 20, player.getR()*2, player.getR()*2);
           g.setStroke(new BasicStroke(1));
           
           
       }
       
       // draw player scope
       g.setColor(Color.white);
       g.setFont(new Font("Century Gothic", Font.PLAIN, 14));
       g.drawString("Scope: "+player.getScope(), WIDTH-100, 30);
    }

    private void gameDraw() {
        Graphics g2 = this.getGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_UP) {
            player.setUp(true);
        }
        if (keyCode == KeyEvent.VK_DOWN) {
            player.setDown(true);
        }
        if (keyCode == KeyEvent.VK_LEFT) {
            player.setLeft(true);
        }
        if (keyCode == KeyEvent.VK_RIGHT) {
            player.setRigth(true);
        }
        if (keyCode == KeyEvent.VK_SPACE)
        {
            player.setFiring(true);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_UP) {
            player.setUp(false);
        }
        if (keyCode == KeyEvent.VK_DOWN) {
            player.setDown(false);
        }
        if (keyCode == KeyEvent.VK_LEFT) {
            player.setLeft(false);
        }
        if (keyCode == KeyEvent.VK_RIGHT) {
            player.setRigth(false);
        }
   if (keyCode == KeyEvent.VK_SPACE)
        {
            player.setFiring(false);
        }
    }

    private void createNewEnemies() 
    {
        enemies.clear();
        Enemy e;
       if (waveNumber == 1) 
       {
           for(int i = 0; i < 4; i++)
           {
               enemies.add(new Enemy(1, 1));
           }          
       }
       if (waveNumber == 2) 
       {
           for(int i = 0; i < 8; i++)
           {
               enemies.add(new Enemy(1, 1));
           }
       }
       
    }
}

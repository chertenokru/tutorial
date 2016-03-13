/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.chertenok.tutorial;

import javax.swing.JFrame;

/**
 *
 * @author 13th
 */
public class Game {
    
    public static void main(String[] args) {
        JFrame window =new JFrame("First Game");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        window.setContentPane(new GamePanel());
        
        window.pack();
        window.setVisible(true);
                
    }
    
}

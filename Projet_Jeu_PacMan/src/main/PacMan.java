package main;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class PacMan implements ActionListener{
    GamePanel gp;
    KeyHandler keyH;
    char direction = 'R';
    int x, y; 
    int width = 32, height = 32;
    int spriteCounter = 0;
    int spriteNum = 1;
    int timeToEat = 0;

    TileManager tile;
    Timer gameLoop;
    Boolean gameOver;
    
    public PacMan(GamePanel gp, KeyHandler keyH, TileManager tile) {

        this.gp = gp;
        this.keyH = keyH;
        this.tile = tile;
        this.tile.getPacmanImage();
        this.tile.loadMap(gp);




        gameLoop = new Timer(50, this); //20FPS
        this.gameLoop.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        tile.move();
        gp.repaint();
        if (tile.gameOver) {
            gameLoop.stop();
        }
    }


    public void update() {

        if ((keyH.upPressed == true || keyH.downPressed == true || keyH.leftPressed == true || keyH.rightPressed == true ) && tile.ghostsActive){
            if (keyH.upPressed == true) {
                direction = 'U';
                
            }else if (keyH.downPressed == true) {
                direction = 'D';
                
            }else if (keyH.leftPressed == true) {
                direction = 'L';
                
            }else if (keyH.rightPressed == true) {
                direction = 'R';
                
            }
            tile.pacman.updateDirection(direction);

            spriteCounter++;
            if (spriteCounter > 2) {
                if (spriteNum == 1) {
                    spriteNum = 2;
                }
                else if (spriteNum == 2) {
                    spriteNum = 1;
                }
                spriteCounter = 0;
            }
            updateImage();
        }
        else {
            tile.pacman.velocityX = 0;
            tile.pacman.velocityY = 0;
        }
        if (tile.attackMode) {
            timeToEat++;
            if (timeToEat == 50) {
                tile.attackMode = false;
                timeToEat = 0;
            }
        }

    }
    public void updateImage() {
    switch(direction) {
        case 'U':
            if (spriteNum == 1) {
                tile.pacman.image = tile.pacmanUpImage;
            }else{tile.pacman.image = tile.pacmanNormalImage;
            }
            break;

        case 'D':
            if (spriteNum == 1) {
                tile.pacman.image = tile.pacmanDownImage;
            }else{tile.pacman.image = tile.pacmanNormalImage;
            }
            break;

        case 'L':
            if (spriteNum == 1) {
                tile.pacman.image = tile.pacmanLeftImage;
            }else{tile.pacman.image = tile.pacmanNormalImage;
            }
            break;

        case 'R':
            if (spriteNum == 1) {
                tile.pacman.image = tile.pacmanRightImage;
            }else{tile.pacman.image = tile.pacmanNormalImage;
            }
            break;
        }
    }
}
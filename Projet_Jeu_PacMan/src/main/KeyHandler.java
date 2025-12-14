package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Timer;

public class KeyHandler implements KeyListener{
    TileManager tile;
    GamePanel gp;
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    Timer gameLoop;
    PacMan pacman;

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_Z || code == KeyEvent.VK_UP) {
            upPressed = true;
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            downPressed = true;
        }
        if (code == KeyEvent.VK_Q || code == KeyEvent.VK_LEFT) {
            leftPressed = true;
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightPressed = true;
        }
        if (code == KeyEvent.VK_SPACE) {
            tile.ghostsActive = true;
            if (tile != null && tile.gameOver) {
                tile.loadMap(gp);
                tile.resetPositions();
                tile.lives = 3;
                tile.score = 0;
                tile.gameOver = false;
                if (pacman != null) {
                    pacman.gameLoop.start();
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {    
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_Z || code == KeyEvent.VK_UP) {
            upPressed = false;
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            downPressed = false;
        }
        if (code == KeyEvent.VK_Q || code == KeyEvent.VK_LEFT) {
            leftPressed = false;
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        }
    }
}

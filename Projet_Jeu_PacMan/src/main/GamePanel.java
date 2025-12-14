package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.*;

public class GamePanel extends JPanel {
    TileManager tile = new TileManager();
    final int rowCount = 22;
    final int columnCount = 19;
    final int tileSize = 32;
    final int boardWidth = columnCount * tileSize;
    final int boardHeight = rowCount * tileSize;

    KeyHandler KeyH = new KeyHandler();
    PacMan pacman = new PacMan(this, KeyH, tile);

    public GamePanel () {
        this.setPreferredSize(new Dimension(boardWidth, boardHeight));
        this.setBackground(Color.BLACK);

        this.addKeyListener(KeyH);
        // Permet de recevoir les événements clavier
        this.setFocusable(true);
        tile.getPacmanImage();
        tile.loadMap(this);

        KeyH.tile = tile;
        KeyH.gp = this;
        KeyH.pacman = pacman;
        
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        tile.draw(g);
    }


}

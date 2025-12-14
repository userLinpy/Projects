package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import javax.swing.ImageIcon;

public class TileManager {

    public Image wallImage;
    public Image blueGhostImage;
    public Image orangeGhostImage;
    public Image pinkGhostImage;
    public Image redGhostImage;

    public Image pacmanBlackImage;
    public Image pacmanUpImage;
    public Image pacmanDownImage;
    public Image pacmanLeftImage;
    public Image pacmanRightImage;
    public Image pacmanNormalImage;
    public Image scaredGhostImage;
    public Image cherryImage;

    public void getPacmanImage() {
        wallImage = new ImageIcon(getClass().getResource("/Image/mur_32.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("/Image/blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("/Image/orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("/Image/pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("/Image/redGhost.png")).getImage();
        scaredGhostImage = new ImageIcon(getClass().getResource("/Image/scaredGhost.png")).getImage();

        pacmanBlackImage = new ImageIcon(getClass().getResource("/Image/pacmanBlack.png")).getImage();
        pacmanUpImage = new ImageIcon(getClass().getResource("/Image/pacman_up.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("/Image/pacman_down.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("/Image/pacman_left.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("/Image/pacman_right.png")).getImage();
        pacmanNormalImage = new ImageIcon(getClass().getResource("/Image/pacman_normal.png")).getImage();

        cherryImage = new ImageIcon(getClass().getResource("/Image/cherry.png")).getImage();
    }

    public class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;
        char direction = 'U'; // Up Down Left Right
        int velocityX = 0;
        int velocityY = 0;
        public int jailTimer = 0;

        public Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        public void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }

        public void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        public void updateVelocity() {
            if (this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -gp.tileSize/4;
            }
            else if (this.direction == 'D'){
                this.velocityX = 0;
                this.velocityY = gp.tileSize/4;
            }
            else if (this.direction == 'L') {
                this.velocityX = -gp.tileSize/4;
                this.velocityY = 0;
            }
            else if (this.direction == 'R') {
                this.velocityX = gp.tileSize/4;
                this.velocityY = 0;
            }
        }
    }
    private String[] tileMap = {
        "OOOOOOOOOOOOOOOOOOO",
        "XXXXXXXXXXXXXXXXXXX",
        "X                CX",
        "X XXXXX XXX XXXXX X",
        "X  XX    X    XX  X",
        "XX XX X     X XX XX",
        "X     XXXXXXX     X",
        "X XXX         XXX X",
        "X XX    XXX    XX X",
        "X     XrbpCoX     X",
        "X X X XXXXXXX X X X",
        "X   X    P    X   X",
        "X X X XXXXXXX X X X",
        "X X      X      X X",
        "X X XXXX X XXXX X X",
        "X X             X X",
        "X     XX X XX     X",
        "X XX XXX X XXX XX X",
        "X XX  XX   XX  XX X",
        "X   X    X    X   X",
        "XCX    X   X    X X",
        "XXXXXXXXXXXXXXXXXXX",
 
    };

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    HashSet<Block> cherrys;
    Block pacman;

    ArrayList<Block> jails = new ArrayList<>();

    GamePanel gp;
    char[] directions = {'U', 'D', 'L', 'R'}; //up down left right
    Random random = new Random();
    int score = 0;
    int bestScore = 0;
    int lives = 3;
    boolean gameOver = false;
    boolean ghostsActive = false;
    boolean attackMode = false;

    public void loadMap(GamePanel gp) {
        this.gp = gp;
        
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();
        cherrys = new HashSet<Block>();


        for (int r = 0; r < gp.rowCount; r++) {
            for (int i = 0; i < gp.columnCount; i++) {
                String row = tileMap[r];
                char tileMapChar = row.charAt(i);

                int x = i*gp.tileSize;
                int y = r*gp.tileSize;

                if (tileMapChar == 'X') { //block wall
                    Block wall = new Block(wallImage, x, y, gp.tileSize, gp.tileSize);
                    walls.add(wall);
                }
                else if (tileMapChar == 'b') { //blue ghost
                    Block ghost = new Block(blueGhostImage, x, y, gp.tileSize, gp.tileSize);
                    ghost.updateDirection(directions[random.nextInt(4)]);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'o') { //orange ghost
                    Block ghost = new Block(orangeGhostImage, x, y, gp.tileSize, gp.tileSize);
                    ghost.updateDirection(directions[random.nextInt(4)]);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'p') { //pink ghost
                    Block ghost = new Block(pinkGhostImage, x, y, gp.tileSize, gp.tileSize);
                    ghost.updateDirection(directions[random.nextInt(4)]);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'r') { //red ghost
                    Block ghost = new Block(redGhostImage, x, y, gp.tileSize, gp.tileSize);
                    ghost.updateDirection(directions[random.nextInt(4)]);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'P') { //pacman
                    pacman = new Block(pacmanRightImage, x, y, gp.tileSize, gp.tileSize);
                }
                else if (tileMapChar == ' ') { //food
                    Block food = new Block(null, x + 14, y + 14, 4, 4);
                    foods.add(food);
                }
                else if (tileMapChar == 'C') { //cherry
                    Block cherry = new Block(cherryImage, x, y, gp.tileSize, gp.tileSize);
                    cherrys.add(cherry);
                }
            } 
        }   
    }

    public void draw(Graphics g) {

    // Pacman
    if (pacman != null){
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);
    }
    // Ghosts
    for (Block ghost : ghosts) {
        if (attackMode) {
            g.drawImage(scaredGhostImage, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }
        else {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }
    }
    // Cherry
    for (Block cherry : cherrys) {
        g.drawImage(cherry.image, cherry.x, cherry.y, cherry.width, cherry.height, null);
    }

    // Walls
    for (Block wall : walls) {
        g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
    }
    g.setColor(Color.YELLOW);
    for (Block food : foods) {
        g.fillRect(food.x, food.y, food.width, food.height);
    }
    //score
    g.setFont(new Font("Arial", Font.PLAIN, 18));
        
    if (gameOver) {
        g.drawString("Game Over ", gp.tileSize/2, (gp.tileSize/2) + 5);
    }
    else {
        if (lives == 3) {
            g.drawImage(pacmanRightImage, gp.tileSize/2, gp.tileSize/4, gp.tileSize/2, gp.tileSize/2, null );
            g.drawImage(pacmanRightImage, 2*gp.tileSize/2, gp.tileSize/4, gp.tileSize/2, gp.tileSize/2, null );
            g.drawImage(pacmanRightImage, 3*gp.tileSize/2, gp.tileSize/4, gp.tileSize/2, gp.tileSize/2, null );
        }
        if (lives == 2) {
            g.drawImage(pacmanRightImage, gp.tileSize/2, gp.tileSize/4, gp.tileSize/2, gp.tileSize/2, null );
            g.drawImage(pacmanRightImage, 2*gp.tileSize/2, gp.tileSize/4, gp.tileSize/2, gp.tileSize/2, null );
        }
        if (lives == 1){
            g.drawImage(pacmanRightImage, gp.tileSize/2, gp.tileSize/4, gp.tileSize/2, gp.tileSize/2, null );
        }
    }
        int centerX = ((gp.columnCount * gp.tileSize) / 2) - 40;
        g.drawString("Score: " + score, centerX, (gp.tileSize / 2) + 5);

        if (score > bestScore) {
            bestScore = score;
        }
        g.drawString("BestScore: " + bestScore, gp.tileSize*14, (gp.tileSize / 2) + 5);

    }

    public boolean collision(Block a, Block b) {
        return  a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        //check wall collisions
        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        //check ghost collisions
        Block ghostEaten = null;
        if (ghostsActive) {
            for (Block ghost : ghosts) {

                if (ghost.y == gp.tileSize*9 && !(ghost.direction == 'U') && !(ghost.direction == 'D')) {
                    ghost.updateDirection('U');
                }
                ghost.x += ghost.velocityX;
                ghost.y += ghost.velocityY;
                for (Block wall : walls) {
                    if (collision(ghost, wall)) {
                        ghost.x -= ghost.velocityX;
                        ghost.y -= ghost.velocityY;
                        char newDirection = directions[random.nextInt(4)];
                        ghost.updateDirection(newDirection);
                    }
                }
                if (collision(ghost, pacman)) {
                    if (!attackMode) {
                        lives -= 1;
                        if (lives == 0) {
                            gameOver = true;
                            return;
                        }
                        resetPositions();
                    }
                    else {
                        ghostEaten = ghost;
                        jails.add(ghostEaten);
                        score += 200;
                        ghost.jailTimer = 0;
                    }
                }
            }
            ghosts.remove(ghostEaten);
        }
        if (!jails.isEmpty()) {
            ArrayList<Block> reviveList = new ArrayList<>();

            for (Block ghost : jails) {
                ghost.jailTimer ++;
                if (ghost.jailTimer >= 100) {
                    ghost.reset();
                    ghost.updateDirection(directions[random.nextInt(4)]);
                    ghosts.add(ghost);
                    reviveList.add(ghost);
                }
            }
            jails.removeAll(reviveList);
        }

        //check food collision
        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food;
                score += 10;
            }
        }
        foods.remove(foodEaten);

        if (foods.isEmpty()) {
            loadMap(gp);
            resetPositions();
        }

        //check cherry collision
        Block cherryEaten = null;
        for (Block cherry : cherrys) {
            if (collision(pacman, cherry)) {
                cherryEaten = cherry;
                score += 50;
                attackMode = true;
            }
        }
        cherrys.remove(cherryEaten);
    }
    public void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        for (Block ghost : ghosts) {
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }
}



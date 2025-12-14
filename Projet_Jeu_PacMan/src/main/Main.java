package main;


import javax.swing.JFrame; //javax.swing est une bibliothèque Java pour créer des interfaces graphiques (GUI). ex : fenêtre, zones de textes...

public class Main {
    public  static void main(String[] args) throws Exception{
        int rowCount = 22;
        int columnCount = 19;
        int tileSize = 32;
        int boardWidth = columnCount * tileSize;
        int boardHeight = rowCount * tileSize;

        JFrame frame = new JFrame("Pac Man");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GamePanel game = new GamePanel();
        frame.add(game);        
        frame.pack();
        frame.setVisible(true);
}
}
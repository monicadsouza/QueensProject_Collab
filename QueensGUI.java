import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

/**
 * GUI to show the n-queens problem, listening for input from the user, and interact with the interactive
 * configurator. The user clicks on the place where (s)he wants to place a queen, after which the board
 * is updated using the interactive configurator (logic) parsed to the constructor as a parameter.  

 * @author Mai Ajspur
 * @version 16.02.2018
 * 
 * */
public class QueensGUI extends JComponent implements MouseListener
{
	final static long 	serialVersionUID = 1234567890;
	final int 			imgSize = 100; // Size of images to draw board
	
	private IQueensLogic logic; // The logic that keeps track of what is legal or not	
	
	// Images for drawing the board
	private Image 		part, queen, invalid, backgroundW, backgroundB;
	private Image 		border_left,border_right,border_top,border_bottom;
	private Image 		corner_left_top, corner_left_bottom,corner_right_top,corner_right_bottom;
	
	public QueensGUI(IQueensLogic logic)  throws IOException {
		part = ImageIO.read(new File("imgs/maze.png"));
		queen = ImageIO.read(new File("imgs/queen.png"));
		invalid = ImageIO.read(new File("imgs/invalid.png"));
		backgroundW = ImageIO.read(new File("imgs/backgroundWhite.png"));
        backgroundB = ImageIO.read(new File("imgs/backgroundBlack.png"));
		
		border_left = ImageIO.read(new File("imgs/board_left.png"));
		border_right = ImageIO.read(new File("imgs/board_right.png"));
		border_top = ImageIO.read(new File("imgs/board_top.png"));
		border_bottom = ImageIO.read(new File("imgs/board_bottom.png"));
		corner_left_top = ImageIO.read(new File("imgs/corner_top_left.png"));
		corner_left_bottom = ImageIO.read(new File("imgs/corner_bottom_left.png"));
		corner_right_top = ImageIO.read(new File("imgs/corner_top_right.png"));
		corner_right_bottom = ImageIO.read(new File("imgs/corner_bottom_right.png"));
		
		this.logic = logic;
		this.addMouseListener(this);
	}

	/*
	 * Draws the current game board.
	 */
	public void paint(Graphics g){
		this.setDoubleBuffered(true);
		Insets in = getInsets();               
		g.translate(in.left, in.top);            

		int[][] gameboard = logic.getBoard();
		int cols = gameboard.length;
		int rows = cols;
		
        // draw borders
        for (int i = 0; i < cols; i++) {
            g.drawImage(border_left, 0, imgSize+imgSize*i, this);
            g.drawImage(border_right, imgSize + gameboard.length*imgSize, imgSize + imgSize*i, this); 
            g.drawImage(border_top, imgSize+imgSize*i, 0, this);
            g.drawImage(border_bottom, imgSize+imgSize*i, imgSize + gameboard.length*imgSize, this);
        }
		// draw corners
		g.drawImage(corner_left_top, 0, 0, this);
		g.drawImage(corner_left_bottom, 0, imgSize + rows*imgSize, this);
		g.drawImage(corner_right_top, imgSize + imgSize*cols, 0, this);
		g.drawImage(corner_right_bottom, imgSize + imgSize*cols, imgSize + rows*imgSize, this);

        // draw board
		for (int c = 0; c < cols; c++){
			for (int r = 0; r < rows; r++){
				int player = gameboard[c][r];
                
                if ( (c+r)%2 == 0 ) // white squares
                	g.drawImage(backgroundW, imgSize+imgSize*c, imgSize+imgSize*r, this);
                else // black squares
                	g.drawImage(backgroundB, imgSize+imgSize*c, imgSize+imgSize*r, this);
                
                if ( player == 1 ) // queen stands on square
					g.drawImage(queen, imgSize+imgSize*c, imgSize+imgSize*r, this);
				if (player == -1) // no queen allowed
					g.drawImage(invalid, imgSize+imgSize*c, imgSize+imgSize*r, this);
				
                g.drawImage(part, imgSize+imgSize*c, imgSize+imgSize*r, this);
			}
		}    
 	}

	/*
	 * When the user clicks on one of the board squares, the corresponding
	 * column and row is parsed to the logic. 
	 */
	public void mouseClicked(MouseEvent e){
        int col = e.getX()/100 - 1;
        int row = e.getY()/100 - 1;

        int size = logic.getBoard().length;
		if ((col >= 0) && (col < size) && (row >= 0) && (row < size)) 
			logic.insertQueen(col, row);
		repaint();
	}

	// Not used methods from the interface of MouseListener 
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mousePressed(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
}


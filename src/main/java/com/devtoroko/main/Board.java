package com.devtoroko.main;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements KeyListener {
	private static final long serialVersionUID = -7756185003808316399L;

	private BufferedImage blocks;
	
	private final int blockSize = 30;	// 이 블록 사이즈는 우리가 갖고 있는 tiles.png 이미지에 있는 블록 한개의 사이즈를 의미한다.
	
	private final int boardWidth = 10, boardHeight = 20;	
	
	@SuppressWarnings("unused")
	private int[][] board = new int[boardHeight][boardWidth];
	
	private Shape[] shapes = new Shape[7];
	
	private Shape currentShape;
	
	private Timer timer;
	
	private final int FPS = 60;
	
	private final int delay = 1000/FPS;
	
	private boolean gameOver = false;
	
	public Board() {
		
		try {
			blocks = ImageIO.read(getClass().getResource("/img/tiles.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//타이머에 대한 정의
		timer = new Timer(delay, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				update(); // Shape가 갖고 있는 값이 60프레임으로 변화가 생긴다
				repaint();// 위에 update에 의해 변한 값을 실제 Panel에 반영한다.
			}
		});
		
		//타이머를 시작한다.
		timer.start();
		
		shapes[0] = new Shape(blocks.getSubimage(0, 0, blockSize, blockSize),new int[][] {
			{1,1,1,1}//I Shape
		},this, 1);
	
		shapes[1] = new Shape(blocks.getSubimage(blockSize, 0, blockSize, blockSize),new int[][] {
			{1,1,0},
			{0,1,1}	//Z Shape
		},this, 2);
		
		shapes[2] = new Shape(blocks.getSubimage(blockSize*2, 0, blockSize, blockSize),new int[][] {
			{0,1,1},
			{1,1,0}	//S Shape
		},this, 3);
		
		shapes[3] = new Shape(blocks.getSubimage(blockSize*3, 0, blockSize, blockSize),new int[][] {
			{1,1,1},
			{0,0,1}	//J Shape
		},this, 4);
		
		shapes[4] = new Shape(blocks.getSubimage(blockSize*4, 0, blockSize, blockSize),new int[][] {
			{1,1,1},
			{1,0,0}	//L Shape
		},this,5);
		
		shapes[5] = new Shape(blocks.getSubimage(blockSize*5, 0, blockSize, blockSize),new int[][] {
			{1,1,1},
			{0,1,0}	//T Shape
		},this,6);
		
		shapes[6] = new Shape(blocks.getSubimage(blockSize*6, 0, blockSize, blockSize),new int[][] {
			{1,1},
			{1,1}	//O Shape
		},this,7);
		
//		currentShape = shapes[4];
		setNextShape();
	}
	
	public void update() {
		currentShape.update();
		if(gameOver) {
			timer.stop();
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
//		g.drawImage(blocks, 0, 0,null);
	
		currentShape.render(g);
		
		for(int row = 0 ; row < board.length ; row++) {
			for(int col = 0 ; col < board[row].length; col++) {
				if(board[row][col] != 0) {
					g.drawImage(blocks.getSubimage((board[row][col]-1)*blockSize, 0, blockSize, blockSize), 
								col*blockSize, row*blockSize, null);
				}
			}
		}
		
		// 가로선을 그린다
		for(int i = 0 ; i < boardHeight ; i++) {
			g.drawLine(0, i*blockSize, boardWidth * blockSize, i*blockSize);
		}
		
		// 세로선을 그린다
		for(int j = 0 ; j < boardWidth ; j++) {
			g.drawLine(j*blockSize, 0, j*blockSize, boardHeight*blockSize);
		}
	}
	
	
	public void setNextShape() {
		
		int index = (int)(Math.random()*shapes.length);
		
		Shape newShape = new Shape(shapes[index].getBlock(), shapes[index].getCoords(), this, shapes[index].getColor());
	
		currentShape = newShape;
		
		
		for(int row = 0 ; row < currentShape.getCoords().length ; row++) {
			for(int col = 0 ; col < currentShape.getCoords()[row].length; col++) {
				if(currentShape.getCoords()[row][col] != 0) {
					if(board[row][col + 4] != 0) {// 시작 지점이 꽉찼으면...
						gameOver = true;
					}
				}
			}
		}
	}
	
	public int getBlockSize() {
		return blockSize;
	}

	public int[][] getBoard(){
		return board;
	}

	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			currentShape.setDelatX(-1);
		}
		if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			currentShape.setDelatX(1);
		}
		if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			currentShape.speedDown();
		}
		if(e.getKeyCode() == KeyEvent.VK_UP) {
			currentShape.rotate();
		}
	}

	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			currentShape.normalSpeed();
		}
	}
	
	public void keyTyped(KeyEvent e) {}
}











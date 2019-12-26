package com.devtoroko.main;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Shape {

	private BufferedImage block;	// tile.png 에서 작은 하나의 블럭의 이미지 값을 가진다.
	private int[][] coords;			// Board에서 저의한 Shape의 실제 모양에 대한 정보를 기진다. 1이면 block 이미지로 채우고 0이면 아무것도 안 채운다.
	private Board board;			// 실제 Shape을 그릴 board에 대한 참조
	
	private int x,y;				// 도형의 좌표

	// 6강
	private int color;
	
	
	// x 축에 대한 변화는 deltaX만 사용한다. 그리고 이것은 60프레임으로 변화한다.
	private int deltaX = 0;			// 도형의 좌우 움직에 의해서 변하는 값이다. 
	
	// y축에 대한 변화는 아래 time,lastTime, normalSpeed, speedDown 변수를 사용한다.
	// y축의 변화량은 FPS를 쓰면 너무 빠르다. 그렇기 때문에 FPS와는 별개로 시간을 재고, 일정 시간이 지나면
	// y축에 변화가 생기게 해야한다.
	private long time,lastTime;		// 시간이 얼마나 지났는지를 체크하기 위한 변수
	private int normalSpeed = 600, speedDown = 60, currentSpeed;

	
	// 5강
	private boolean collision = false, moveX = false;
	
	public Shape(BufferedImage block, int[][] coords, Board board, int color) {
		this.block = block;
		this.coords = coords;
		this.board = board;
		this.color = color;
		// y축으로 떨어지는 속도에 대한 것을 정의한다.
		currentSpeed = normalSpeed;
		time = 0;
		lastTime = System.currentTimeMillis();
		
		//도형이 맨위의 가운데에서 시작한다.
		x = 4;
		y = 0;		
	}
	
	//update는 60프레임으로 계속 호출된다는 것을 명심하자.
	public void update() {
		
		time += System.currentTimeMillis() - lastTime;
		lastTime = System.currentTimeMillis();
		
		if(collision) {	//바닥에 닿으면? 색깔이 빨간색으로 바뀐다.
			
			// 바닥에 닿은 블럭은 빨간색으로 변하고 고정된다.
			for(int row = 0 ; row < coords.length ; row++) {
				for(int col = 0 ; col < coords[row].length; col++) {
					if(coords[row][col] != 0) {
						board.getBoard()[y+row][x+col] = color;//board 에다가 고정된 그림을 그려주는 것이다.
					}
				}
			}
			
			checkLine();//한줄이 꽉차면 지워주는 역할을 한다.
			board.setNextShape();
		}
		
		
		/*   x축      */
		//  !([현재 좌표 + x 축의 변화량 + 도형의 x축 길이] > 10칸)  //이건 오른쪽 edge에 대한것
		//  !( 현재 좌표 + x 축의 변화량 ) < 0  				   // 이건 왼쪽에 대한 것
		if(!(x + deltaX + coords[0].length > 10) && !(x + deltaX < 0)) {
			
			for(int row = 0 ; row < coords.length ; row++) {
				for(int col = 0 ; col < coords[row].length; col++) {
					if(coords[row][col] != 0) {
						if(board.getBoard()[y + row][x + deltaX + col] != 0) {
							moveX = false;
						}
					}
				}
			}
			
			if(moveX) {
				x += deltaX;
			}
			
		}

		
		/*   y축      */
		if(!(y + 1 + coords.length > 20)) {
			
			// 쌓이게 되는 for문이다. y축에 대허서는 쌓이는 건 좋지만 x축으로 닿으면 그것도 고정이 되어버린다. 이걸 고치자.
			for(int row = 0 ; row < coords.length ; row++) {
				for(int col = 0 ; col < coords[row].length; col++) {
					if(coords[row][col] != 0) {
						if(board.getBoard()[y+row+1][col + x] != 0) {
							collision = true;
						}
					}
				}
			}
			
			//만약 아래와 같은 조건문이 없고 y++ 를 하면 60프레임으로 떨어지기 때문에 너무 빠르다.
			//time이 일정량 쌓이면, 그때 움직인다. 그리고 여태 쌓아온 time은 0을 다시 대입한다.
			if(time > currentSpeed) {
				y++;
				time = 0;
			}
		} else {	// y축 맨 아래에 닿으면 collison = true
			collision = true;
		}
		
		deltaX = 0;
		
		moveX = true;
	}
	
	public void render(Graphics g) {
		
		for(int row = 0 ; row < coords.length; row++) {
			for(int col = 0 ; col < coords[row].length ; col++) {
				if(coords[row][col] != 0) {
					g.drawImage(block,col*board.getBlockSize() + x*board.getBlockSize(),
								row*board.getBlockSize() + y*board.getBlockSize(),null);
				}
			}
		}
	}
	
	private void checkLine() {
		
		int height = board.getBoard().length -1;
		
		for(int i = height; i > 0 ; i--) {
			
			int count = 0;
			for(int j = 0 ; j < board.getBoard()[0].length ; j++) {
				
				if(board.getBoard()[i][j] != 0) {
					count++;
				}
				
				board.getBoard()[height][j] = board.getBoard()[i][j];
			}
			
			if(count < board.getBoard()[0].length) {
				height--;
			}
		}
		
	}
	
	public void rotate() {
		
		if(collision) {//닿는 순간 rotate는 사용이 불가한 상태가 된다.
			return;
		}
		
		int[][] rotatedMatrix = null;
		
		rotatedMatrix = getTranspose(coords);
		
		rotatedMatrix = getReverseMatrix(rotatedMatrix);
		
		if(x + rotatedMatrix[0].length > 10 || y + rotatedMatrix.length > 20) return; 
		
		
		// 회전하는 도형이 종종 다른 블록에 다가올때 회전하면 겹쳐버리는 경우가 생긴다. 이것을 방지한다.
		for(int row = 0 ; row < rotatedMatrix.length; row++) {
			for(int col = 0 ; col < rotatedMatrix[0].length; col++) {
				if(board.getBoard()[y+row][x+col] != 0) {
					return;
				}
			}
		}
		
		coords = rotatedMatrix;
		
	}
	
	private int[][] getTranspose(int[][] matrix) {
		int [][] newMatrix = new int[matrix[0].length][matrix.length];
		
		for(int i = 0 ; i < matrix.length; i++) {
			for(int j = 0 ; j < matrix[0].length ; j++) {
				newMatrix[j][i] = matrix[i][j]; 
			}
		}
		
		return newMatrix;
	}
	
	private int[][] getReverseMatrix(int[][] matrix) {
		
		int middle = matrix.length / 2;
		
		for(int i = 0 ; i < middle ; i++) {
			int[] m = matrix[i];
			matrix[i] = matrix[matrix.length -i -1];
			matrix[matrix.length -i -1] = m;
		}
		return matrix;
	}
	
	
	public void setDelatX(int deltaX) {
		this.deltaX = deltaX;
	}
	
	public void normalSpeed() {
		currentSpeed = normalSpeed;
	}
	
	public void speedDown() {
		currentSpeed = speedDown;
	}

	public BufferedImage getBlock() {
		return block;
	}

	public int[][] getCoords() {
		return coords;
	}

	public int getColor() {
		return color;
	}
	
	
}

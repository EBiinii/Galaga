
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.ImageObserver;
import java.net.URL;

import javax.swing.ImageIcon;

public class Keyplay2 extends ImageIcon {
	public int x;				// 모양의 위치 좌표
	public int y;				// 모양의 위치 좌표
	private int initX, initY; 	// 초기시작 x, y좌표
	protected int xDirection;
	protected int yDirection;
	protected int xBoundary;
	protected int yBoundary;
	protected int steps;
	protected int margin;		// 이 모양의 영역이 포함되는 영역을 나타내기 위함
	public Keyplay2(URL imgURL, int x, int y, int margin, int steps, int xBoundary, int yBoundary) {
		// imgPath : 그림 파일의 경로명
		// x, y : 이미지의 시작 위치 좌표
		// margin : 이 이미지의 영역을 나타내는 범위 (이 영역안에 있으면 충돌 한 것으로 판단 하기 위함)
		// steps : 이미지가 움직일때 이동하는 좌표 단위
		// xBoundary, yBoundary : 그림이 이동할 수 있는 좌표의 최대값
		super (imgURL);
		this.x = x;
		this.initX = x;
		this.y = y;
		this.initY = y;
		this.margin = margin;
		this.xDirection = 1;
		this.yDirection = 1;
		this.steps = steps;
		this.xBoundary = xBoundary;
		this.yBoundary = yBoundary;
	}
	
	public Keyplay2(URL imgURL, int margin, int steps, int xBoundary, int yBoundary) {
		this (imgURL, 0, 0, margin, steps, xBoundary, yBoundary);
		x= (int) (Math.random() * 530)+50;//끝에 나타나지 않도록 설정
		y= (int) (Math.random() * 400);//캐릭터와 만나지 않도록 설정
	}
	
	public Keyplay2(URL imgURL, int margin, int xBoundary, int yBoundary) {
		this (imgURL, xBoundary/2, yBoundary, margin, margin, xBoundary, yBoundary);
	}
	
	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}
	
	public void setMargin(int margin) {
		this.margin = margin;
	}
	
	public int getMargin() {
		return margin;
	}
	
	
	
	public void reset() {
		x = initX; y= initY;
	}
	
	// 해당 모양을 g에 출력해주는 메소드
	public void draw(Graphics g, ImageObserver io) {
		((Graphics2D)g).drawImage(this.getImage(), x, y, margin, margin, io);
	}
	
	public boolean collide (Point p2) {
		Point p = new Point(this.x, this.y);
		if (p.distance(p2) <= 50) return true;//50은 충돌마진
		return false;
	}

	// 이 부분을 상속한 다양한 객체의 모션이 일어날 수 있도록 조정하기
	public void move() {
		if (xDirection > 0 && x >= xBoundary) {
			xDirection = -1;
			
		}
		if (xDirection < 0 && x <= 0) {
			xDirection = 1;			
		}	

	
		if (yDirection < 0 && y <= 0) {
			yDirection = 1;
		}
		y += (yDirection * steps);

	}	
}
	











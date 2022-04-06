
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.ImageObserver;
import java.net.URL;

import javax.swing.ImageIcon;

public class Keyplay2 extends ImageIcon {
	public int x;				// ����� ��ġ ��ǥ
	public int y;				// ����� ��ġ ��ǥ
	private int initX, initY; 	// �ʱ���� x, y��ǥ
	protected int xDirection;
	protected int yDirection;
	protected int xBoundary;
	protected int yBoundary;
	protected int steps;
	protected int margin;		// �� ����� ������ ���ԵǴ� ������ ��Ÿ���� ����
	public Keyplay2(URL imgURL, int x, int y, int margin, int steps, int xBoundary, int yBoundary) {
		// imgPath : �׸� ������ ��θ�
		// x, y : �̹����� ���� ��ġ ��ǥ
		// margin : �� �̹����� ������ ��Ÿ���� ���� (�� �����ȿ� ������ �浹 �� ������ �Ǵ� �ϱ� ����)
		// steps : �̹����� �����϶� �̵��ϴ� ��ǥ ����
		// xBoundary, yBoundary : �׸��� �̵��� �� �ִ� ��ǥ�� �ִ밪
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
		x= (int) (Math.random() * 530)+50;//���� ��Ÿ���� �ʵ��� ����
		y= (int) (Math.random() * 400);//ĳ���Ϳ� ������ �ʵ��� ����
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
	
	// �ش� ����� g�� ������ִ� �޼ҵ�
	public void draw(Graphics g, ImageObserver io) {
		((Graphics2D)g).drawImage(this.getImage(), x, y, margin, margin, io);
	}
	
	public boolean collide (Point p2) {
		Point p = new Point(this.x, this.y);
		if (p.distance(p2) <= 50) return true;//50�� �浹����
		return false;
	}

	// �� �κ��� ����� �پ��� ��ü�� ����� �Ͼ �� �ֵ��� �����ϱ�
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
	











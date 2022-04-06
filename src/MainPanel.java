import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class MainPanel extends JPanel{
	
	
	public void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		ImageIcon back = new ImageIcon("/res/back.png");
		g2d.drawImage(back.getImage(), 0, 0, 705,930,this);
		
		//ImageIcon end = new ImageIcon("/res/엔딩화면끝.png");
		//g2d.drawImage(end.getImage(),0,47,705,655,null);
		
	}
	
}//close class

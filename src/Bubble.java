

import java.net.URL;

public class Bubble extends Keyplay{
   public Bubble(URL imgURL, int margin, int xBoundary, int yBoundary, int x, int y) {
      super(imgURL, x, y, margin, margin, xBoundary, yBoundary);
      yDirection=-3;
   }
   //move �������̵�->������ ������
   public void move(){
      y += yDirection;
   }

}
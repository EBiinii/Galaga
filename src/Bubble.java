

import java.net.URL;

public class Bubble extends Keyplay{
   public Bubble(URL imgURL, int margin, int xBoundary, int yBoundary, int x, int y) {
      super(imgURL, x, y, margin, margin, xBoundary, yBoundary);
      yDirection=-3;
   }
   //move 오버라이드->버블의 움직임
   public void move(){
      y += yDirection;
   }

}
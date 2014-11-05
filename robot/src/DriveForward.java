package robot2;

import lejos.robotics.subsumption.*;

public class DriveForward  implements Behavior {
   private boolean suppressed = false;
   
   public boolean takeControl() {
      return true;
   }

   public void suppress() {
      suppressed = true;
   }

   public void action() {
     suppressed = false;
     Main.left.forward();
     Main.right.forward();
     while( !suppressed )
        Thread.yield(); //A hint to the scheduler that the current thread is willing to yield its current use of a processor.
     Main.left.stop(true); // clean up with fix for problem in detectBlackLine
     Main.right.stop(true);
   }
}
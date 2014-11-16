package team011;

import battlecode.common.*;
import java.util.Random;

public class RobotPlayer {


    public static void run(RobotController myRC) {
		boolean startedLoop = false;
		MapLocation startLocation = null;
		Random randGen = new Random();
		
        while (true) {
            try
			{
				while (myRC.isMovementActive())
				{
					myRC.yield();
				}
			
				if(!startedLoop)
				{
					if(myRC.canMove(myRC.getDirection()))
					{
						myRC.moveForward();
						continue;
					}
					else
					{
						startedLoop = true;
						startLocation = myRC.getLocation();
						myRC.setDirection(myRC.getDirection().rotateRight());
						continue;
					}
				}
				else if(startLocation == myRC.getLocation() && !startedLoop)
				{
					startedLoop = false;
					
					Direction moveableDirections[] = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
					//int randomInt = randGen.nextInt(8);
					System.out.println("Random Int " + 1);
					myRC.setDirection(moveableDirections[1]);
					
				}
				else
				{
					if(myRC.canMove(myRC.getDirection().rotateLeft()))
					{
						myRC.setDirection(myRC.getDirection().rotateLeft());
						continue;
					}
					else if(myRC.canMove(myRC.getDirection()))
					{
						myRC.moveForward();
						continue;
					}
					else
					{
						myRC.setDirection(myRC.getDirection().rotateRight());
						continue;
					}
				}
			}
            catch (Exception e) {
                System.out.println("caught exception:");
                e.printStackTrace();
            }
        }
    }
}

				
				/*
					myRC.setDirection(myRC.getDirection().rotateRight());
					myRC.currDirection = myRC.getDirection();
				}
				
				nearbyRobots = myRC.senseNearbyGameObjects(RobotType.SOLIDIER);
				
				if(nearbyRobots.length > 5)
				{
					actionMessage = new Messge();
					actionmessage.ints = new int[1]{currDirection};
					myRC.broadcast(
				
				
				*/
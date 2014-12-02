package team011;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TerrainTile;

public class RobotPlayer {

	public enum AreaIdentifier {
		THIS_ROBOT,

		ALLIED_ARCHON, ALLIED_DISRUPTER, ALLIED_SCORCHER, ALLIED_SCOUT, ALLIED_SOLDIER, ALLIED_TOWER,

		ENEMY_ARCHON, ENEMY_DISRUPTER, ENEMY_SCORCHER, ENEMY_SCOUT, ENEMY_SOLDIER, ENEMY_TOWER,

		WALL, EMPTY
	}

	public static void run(RobotController myRC) {

		switch (myRC.getType()) {
		case ARCHON:
			archonCommandQueue(myRC);
			break;

		case DISRUPTER:
			disrupterCommandQueue(myRC);
			break;

		case SCORCHER:
			scorcherCommandQueue(myRC);
			break;
		case SCOUT:
			scoutCommandQueue(myRC);
			break;

		case SOLDIER:
			soldierCommandQueue(myRC);
			break;
		case TOWER:
			towerCommandQueue(myRC);
			break;
		}
	}

	private static void archonCommandQueue(RobotController myRC) {
		MapLocation targetNode = null;
		Navigator navigator = new Navigator(myRC, myRC.getLocation().x,
				myRC.getLocation().y);
		navigator.setUpInitialMap();
		while (true) {
			Message sendAfterTurn = null;

			try {
				if (null == targetNode) {
					targetNode = initializeTargetNode(myRC);
					System.out.println("Staring at: " + myRC.getLocation().x
							+ ", " + myRC.getLocation().y);
					System.out.println("Target node Found at: x:"
							+ targetNode.x + ", y:" + targetNode.y);

					if (null == sendAfterTurn)
						sendAfterTurn = new Message();
					sendAfterTurn.locations = new MapLocation[] { targetNode };
				}

				if (null != sendAfterTurn)
					myRC.broadcast(sendAfterTurn);

				Direction[] path = navigator.moveTowards(targetNode,
						myRC.getLocation());
				for (Direction d : path) {
					while (myRC.isMovementActive())
						;
					myRC.setDirection(d);
					myRC.yield();
					if (myRC.canMove(myRC.getDirection())) {
						myRC.moveForward();
					} else if (TerrainTile.VOID == myRC.senseTerrainTile(myRC
							.getLocation().add(myRC.getDirection())))
						break;
					else
						myRC.yield();
				}
				while (myRC.getLocation().equals(targetNode))
					myRC.yield();
			} catch (Exception e) {
				System.out.println("caught exception:");
				e.printStackTrace();
			}
		}
	}

	private static void disrupterCommandQueue(RobotController myRC) {
		while (true) {
			try {

			} catch (Exception e) {
				System.out.println("caught exception:");
				e.printStackTrace();
			}
		}
	}

	private static void scorcherCommandQueue(RobotController myRC) {
		while (true) {
			try {

			} catch (Exception e) {
				System.out.println("caught exception:");
				e.printStackTrace();
			}
		}
	}

	private static void scoutCommandQueue(RobotController myRC) {
		while (true) {
			try {

			} catch (Exception e) {
				System.out.println("caught exception:");
				e.printStackTrace();
			}
		}
	}

	private static void soldierCommandQueue(RobotController myRC) {
		while (true) {
			try {

			} catch (Exception e) {
				System.out.println("caught exception:");
				e.printStackTrace();
			}
		}
	}

	private static void towerCommandQueue(RobotController myRC) {
		while (true) {
			try {

			} catch (Exception e) {
				System.out.println("caught exception:");
				e.printStackTrace();
			}
		}
	}

	private static MapLocation initializeTargetNode(RobotController myRC) {
		MapLocation firstVisNodes[] = myRC.senseCapturablePowerNodes();
		Message initMessages[] = myRC.getAllMessages();
		System.out.println("Num Vis Nodes: " + firstVisNodes.length);
		System.out.println("Num Message Received: " + initMessages.length);
		return firstVisNodes[initMessages.length % firstVisNodes.length];
	}

	private static AreaIdentifier[][] areaAround(RobotController myRC) {
		AreaIdentifier areaAround[][] = new AreaIdentifier[13][13];
		MapLocation centralLocation = myRC.getLocation();
		areaAround[7][7] = AreaIdentifier.THIS_ROBOT;

		Robot nearbyRobots[] = myRC.senseNearbyGameObjects(Robot.class);
		for (Robot sensedRobot : nearbyRobots) {
			try {
				RobotInfo robotInformation = myRC.senseRobotInfo(sensedRobot);
				MapLocation sensedRobotLoc = robotInformation.location;

				areaAround[7 - (centralLocation.x - sensedRobotLoc.x)][7 - (centralLocation.y - sensedRobotLoc.y)] = RobotTypeToAreaIdentifier(
						robotInformation.type, robotInformation.team);
			} catch (GameActionException e) {
				System.out.println("Caught Exception While Sensing Robot:");
				e.printStackTrace();
			}
		}
		return areaAround;
	}

	private static void NavigateTo(MapLocation towards, RobotController myRC) {
		// check straight line
		Direction straightLineDirect = myRC.getLocation().directionTo(towards);
		boolean straightLineClear = true;
		System.out.println("Navigating:");
		if (Direction.OMNI == straightLineDirect)
			return; // Already at Square

		MapLocation testLoc = myRC.getLocation().add(straightLineDirect);

		while (myRC.canSenseSquare(testLoc)) {
			testLoc = testLoc.add(straightLineDirect);
			if (myRC.senseTerrainTile(testLoc) != TerrainTile.LAND) {
				straightLineClear = true;
				break;
			}
		}

		if (straightLineClear && !myRC.isMovementActive()) {
			try {
				myRC.setDirection(straightLineDirect);
			} catch (GameActionException e) {
				e.printStackTrace();
			}
			while (myRC.isMovementActive())
				;
			// wait
			if (myRC.canMove(myRC.getDirection())
					&& myRC.getDirection().equals(straightLineDirect)) {
				try {
					myRC.moveForward();
				} catch (GameActionException e) {
					e.printStackTrace();
				}
			}
		} else if (!myRC.isMovementActive()) {
			try {
				myRC.setDirection(myRC.getDirection().rotateRight());
				System.out.println("Pointed Towards: "
						+ myRC.getDirection().name());
			} catch (GameActionException e) {
				e.printStackTrace();
			}
			while (myRC.isMovementActive())
				;
			// wait
			if (myRC.canMove(myRC.getDirection())) {
				try {
					myRC.moveForward();
				} catch (GameActionException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static boolean locationClaimed(MapLocation[] dibbsedNodes,
			MapLocation node) {
		for (int i = 0; i < dibbsedNodes.length; i++) {
			if (dibbsedNodes[i].equals(node))
				return true;
		}
		return false;
	}

	private static AreaIdentifier RobotTypeToAreaIdentifier(RobotType type,
			Team team) {
		if (team == Team.A) {
			switch (type) {
			case ARCHON:
				return AreaIdentifier.ALLIED_ARCHON;
			case DISRUPTER:
				return AreaIdentifier.ALLIED_DISRUPTER;
			case SCORCHER:
				return AreaIdentifier.ALLIED_SCORCHER;
			case SCOUT:
				return AreaIdentifier.ALLIED_SCOUT;
			case SOLDIER:
				return AreaIdentifier.ALLIED_SOLDIER;
			case TOWER:
				return AreaIdentifier.ALLIED_TOWER;
			}
		} else {
			switch (type) {
			case ARCHON:
				return AreaIdentifier.ENEMY_ARCHON;
			case DISRUPTER:
				return AreaIdentifier.ENEMY_DISRUPTER;
			case SCORCHER:
				return AreaIdentifier.ENEMY_SCORCHER;
			case SCOUT:
				return AreaIdentifier.ENEMY_SCOUT;
			case SOLDIER:
				return AreaIdentifier.ENEMY_SOLDIER;
			case TOWER:
				return AreaIdentifier.ENEMY_TOWER;
			}
		}
		return AreaIdentifier.EMPTY;
	}
}

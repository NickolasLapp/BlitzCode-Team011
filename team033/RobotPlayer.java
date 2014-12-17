package team033;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.RobotController;
import battlecode.common.RobotLevel;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;

public class RobotPlayer {

	private static RobotController myRC;

	private static Direction cameFrom;

	public static void run(RobotController myRC) {
		RobotPlayer.myRC = myRC;

		switch (myRC.getType()) {
		case ARCHON:
			archonCommandQueue();
			break;

		case DISRUPTER:
			disrupterCommandQueue();
			break;

		case SCORCHER:
			scorcherCommandQueue();
			break;
		case SCOUT:
			scoutCommandQueue();
			break;

		case SOLDIER:
			soldierCommandQueue();
			break;
		case TOWER:
			towerCommandQueue();
			break;
		}
	}

	private static void archonCommandQueue() {
		while (true) {
			MapLocation visibleNodes[] = myRC.senseCapturablePowerNodes();
			MapLocation targetNode = visibleNodes[myRC.getRobot().getID()
					% visibleNodes.length];
			boolean spawnSoldiers = false;

			Direction towards = navigateTo(targetNode);

			Message messages[] = myRC.getAllMessages();

			for (Message m : messages) {
				if (m.strings[0].contains("^c")) {
					visibleNodes = myRC.senseCapturablePowerNodes();
					for (MapLocation n : visibleNodes) {
						if (!targetNode.equals(n))
							targetNode = n;
					}
					spawnSoldiers = true;
				}
			}

			while (true) {
				if (spawnSoldiers
						&& myRC.getFlux() >= RobotType.SOLDIER.spawnCost) {
					try {
						if (myRC.senseObjectAtLocation(
								myRC.getLocation().add(myRC.getDirection()),
								RobotLevel.ON_GROUND) == null
								&& TerrainTile.LAND == myRC
										.senseTerrainTile(myRC.getLocation()
												.add(myRC.getDirection())))
							myRC.spawn(RobotType.SOLDIER);
						else {
							if (!myRC.isMovementActive())
								myRC.setDirection(myRC.getDirection()
										.rotateRight());
							else
								myRC.yield();
						}
					} catch (GameActionException e) {
						e.printStackTrace();
					}

				} else {
					if (!myRC.isMovementActive()) {
						try {
							towards = navigateTo(targetNode);

							if (Direction.NONE == towards) {
								break;
							}

							else if (towards != Direction.OMNI) {
								if (myRC.getDirection() == towards)
									myRC.moveForward();
								else
									myRC.setDirection(towards);
							}
						} catch (GameActionException e) {
							e.printStackTrace();
						}
						myRC.yield();
					}
				}
			}

			if (!spawnSoldiers) {
				while (myRC.isMovementActive())
					myRC.yield();
				try {
					myRC.setDirection(myRC.getLocation()
							.directionTo(targetNode));
				} catch (GameActionException e) {
					e.printStackTrace();
				}

				Message msg = new Message();
				msg.strings = new String[1];
				msg.strings[0] = new String("^c");
				try {
					myRC.broadcast(msg);
				} catch (GameActionException e) {
					e.printStackTrace();
				}

				while (myRC.getFlux() <= RobotType.TOWER.spawnCost) {
					myRC.yield();
				}
				try {
					myRC.spawn(RobotType.TOWER);
				} catch (GameActionException e) {
					e.printStackTrace();
				}
			}

		}
	}

	private static Direction navigateTo(MapLocation targetNode) {
		Direction straightPath = myRC.getLocation().directionTo(targetNode);
		if (Direction.NONE == straightPath || Direction.OMNI == straightPath)
			return Direction.OMNI;

		if (myRC.getLocation().add(straightPath).equals(targetNode))
			return Direction.NONE;

		if (myRC.canMove(straightPath) && cameFrom != straightPath) {
			cameFrom = myRC.getDirection().opposite();
			return straightPath;
		} else if (myRC.canMove(straightPath.rotateRight())
				&& cameFrom != straightPath.rotateRight()) {
			cameFrom = straightPath.rotateRight().opposite();
			return straightPath.rotateRight();
		} else if (myRC.canMove(straightPath.rotateLeft())
				&& cameFrom != straightPath.rotateLeft()) {
			cameFrom = straightPath.rotateLeft().opposite();
			return straightPath.rotateLeft();
		} else if (myRC.canMove(straightPath.rotateLeft().rotateLeft())
				&& cameFrom != straightPath.rotateLeft().rotateLeft()) {
			cameFrom = straightPath.rotateLeft().rotateLeft().opposite();
			return straightPath.rotateLeft().rotateLeft();
		} else if (myRC.canMove(straightPath.rotateRight().rotateRight())
				&& cameFrom != straightPath.rotateRight().rotateRight()) {
			cameFrom = straightPath.rotateRight().rotateRight().opposite();
			return straightPath.rotateRight().rotateRight();
		}

		return Direction.OMNI;
	}

	private static void towerCommandQueue() {
		// TODO Auto-generated method stub

	}

	private static void soldierCommandQueue() {
		while (true)
			myRC.yield();

	}

	private static void scoutCommandQueue() {
		// TODO Auto-generated method stub

	}

	private static void scorcherCommandQueue() {
		// TODO Auto-generated method stub

	}

	private static void disrupterCommandQueue() {
		// TODO Auto-generated method stub

	}

}

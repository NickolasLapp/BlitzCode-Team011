package team022;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Stack;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.TerrainTile;

public class RobotPlayer {

	public static void run(RobotController myRC) {
		while (true) {
			bestFirstSearch(
					myRC.senseCapturablePowerNodes()[(int) (Math.random() * 2)],
					myRC);
		}
	}

	public static MapLocation[] bestFirstSearch(MapLocation towards,
			RobotController myRC) {
		Comparator<MapLocationComparable> comp = new LocationComparator(towards);

		PriorityQueue<MapLocationComparable> openSet = new PriorityQueue<MapLocationComparable>(
				10, comp);

		openSet.add(new MapLocationComparable(myRC.getLocation(), null));

		while (!openSet.isEmpty()) {
			MapLocationComparable examine = openSet.poll();
			if (examine.equals(towards)) {
				try {
					traverseParents(myRC, examine);
				} catch (GameActionException e) {
					e.printStackTrace();
				}
			}

			for (Direction d : Direction.values()) {
				MapLocation neighbor = examine.location.add(d);
				TerrainTile terrainAtN = myRC.senseTerrainTile(neighbor);

				if (terrainAtN == null)
					try {
						traverseParents(myRC, examine);
					} catch (GameActionException e) {
						e.printStackTrace();
					}
				else if (terrainAtN != TerrainTile.VOID
						&& terrainAtN != TerrainTile.OFF_MAP)
					openSet.add(new MapLocationComparable(neighbor, examine));
				else
					continue;
			}
		}
		return null;
	}

	private static void traverseParents(RobotController myRC,
			MapLocationComparable examine) throws GameActionException {
		System.out.println("Traversing Parents?");
		examine = examine.parent;

		Stack<Direction> directions = new Stack<Direction>();
		do {
			directions.add(examine.parent.location
					.directionTo(examine.location));
			examine = examine.parent;
		} while (examine.parent != null);

		while (!directions.isEmpty()) {
			while (myRC.isMovementActive())
				;
			myRC.setDirection(directions.pop());
			myRC.yield();
			while (!myRC.canMove(myRC.getDirection())) {
				myRC.yield();
			}
			myRC.moveForward();
		}
	}

	public static class LocationComparator implements
			Comparator<MapLocationComparable> {
		public MapLocation targetLocation;

		public LocationComparator(MapLocation targetLocation) {
			super();
			this.targetLocation = targetLocation;
		}

		public int compare(MapLocationComparable first,
				MapLocationComparable second) {
			return first.location.distanceSquaredTo(targetLocation) >= second.location
					.distanceSquaredTo(targetLocation) ? 1 : -1;
		}
	}

	// public MapLocation[] bestFirstSearch(MapLocation towards, RobotController
	// myRC) {
	// PriorityQueue<MapLocationComparable> openSet = new
	// PriorityQueue<MapLocationComparable>();
	// ArrayList<MapLocationComparable> closedSet = new
	// ArrayList<MapLocationComparable>();
	//
	// openSet.add(new MapLocationComparable(myRC.getLocation(), null, myRC
	// .getLocation().distanceSquaredTo(towards)));
	//
	// while (!openSet.isEmpty()) {
	// MapLocationComparable examine = openSet.poll();
	// if(towards.equals(examine.location))
	// {
	// return generatePath(examine);
	// }
	//
	// closedSet.add(examine);
	//
	// for(Direction d : Direction.values())
	// {
	// MapLocation next = examine.location.add(d);
	//
	// if(!closedSet.contains(next) && !openSet.contains(next))
	// openSet.add(new MapLocationComparable(next, examine,
	// towards.distanceSquaredTo(next)));
	// else if(closedSet
	//
	// }
	//
	// }
	//
	//
	// return null;
	// }
	//
	// private MapLocation[] generatePath(MapLocationComparable examine) {
	// return null;
	// }
	//
	private static class MapLocationComparable {
		public MapLocation location;
		public MapLocationComparable parent;

		public MapLocationComparable(MapLocation thisLocation,
				MapLocationComparable parent) {
			this.location = thisLocation;
			this.parent = parent;
		}
	}
}
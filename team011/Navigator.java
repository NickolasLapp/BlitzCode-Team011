package team011;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.TerrainTile;

public class Navigator {

	private enum TileIdentifier {
		LAND, WALL, OFF_MAP,

		ALLY_POWERCORE, ENEMY_POWERCORE,

		EMPTY_NODE, ALLY_NODE, ENEMY_NODE,

		THIS_ROBOT,

		ALLIED_ARCHON, ALLIED_DISRUPTER, ALLIED_SCORCHER, ALLIED_SCOUT, ALLIED_SOLDIER, ALLIED_TOWER,

		ENEMY_ARCHON, ENEMY_DISRUPTER, ENEMY_SCORCHER, ENEMY_SCOUT, ENEMY_SOLDIER, ENEMY_TOWER,
	}

	private TileIdentifier map[][];
	private int xOrigin, yOrigin;
	private int xArrayCenter, yArrayCenter;

	private RobotController myRC;
	private int sensorRange;
	public Direction[] currentPath;

	public Navigator(RobotController myRC, int xOrigin, int yOrigin) {
		map = new TileIdentifier[GameConstants.MAP_MAX_WIDTH * 2 + 1][GameConstants.MAP_MAX_HEIGHT * 2 + 1];
		this.myRC = myRC;
		sensorRange = myRC.getType().sensorRadiusSquared;
		this.xOrigin = xOrigin;
		this.yOrigin = yOrigin;

		xArrayCenter = GameConstants.MAP_MAX_WIDTH + 1;
		yArrayCenter = GameConstants.MAP_MAX_HEIGHT + 1;

	}

	// implementation of A* search algorithm
	// See: http://en.wikipedia.org/wiki/A*_search_algorithm
	public Direction[] moveTowards(MapLocation startLocation, MapLocation goal) {
		PriorityQueue<MapLocationTuple> openSet = new PriorityQueue<MapLocationTuple>();
		openSet.add(new MapLocationTuple(startLocation, 0, null));

		ArrayList<MapLocation> closedSet = new ArrayList<MapLocation>();

		HashMap<Integer, Integer> g_score = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> f_score = new HashMap<Integer, Integer>();

		g_score.put(startLocation.hashCode(), 0);
		f_score.put(
				startLocation.hashCode(),
				g_score.get(startLocation.hashCode())
						+ heuristicEstimate(startLocation, goal));

		while (openSet.size() != 0) {
			MapLocationTuple current = openSet.poll();
			if (current.location.equals(goal)) {
				System.out.println("Goal Found!");
				Direction pathToTravel[] = reconstructPath(current);
				return pathToTravel;
			}

			closedSet.add(current.location);
			System.out.println("Examining Neighbors of: " + current.location.x
					+ ", " + current.location.y);
			for (Direction d : Direction.values()) {
				MapLocation neighbor = current.location.add(d);
				if (closedSet.contains(neighbor))
					continue;
				TileIdentifier neighborTerrain = map[xLocToIndex(neighbor.x)][yLocToIndex(neighbor.y)];

				if (neighborTerrain == TileIdentifier.WALL) {
					System.out.println("Wall!");
				}
				if (null != neighborTerrain
						&& TileIdentifier.LAND != neighborTerrain) {
					closedSet.add(neighbor);
					if (neighborTerrain == TileIdentifier.WALL)
						System.out.println(neighborTerrain.toString());
					continue;
				}

				int tentativeGScore = g_score.get(current.location.hashCode()) + 1;

				if (!openSet.contains(neighbor.hashCode())
						|| tentativeGScore < g_score.get(neighbor.hashCode())) {
					g_score.put(neighbor.hashCode(), tentativeGScore);
					f_score.put(neighbor.hashCode(), tentativeGScore
							+ heuristicEstimate(neighbor, goal));
					if (!openSet.contains(neighbor.hashCode()))
						openSet.add(new MapLocationTuple(neighbor, f_score
								.get(neighbor.hashCode()), current));
				}
			}

		}
		System.out.println("Returning null...");
		return null;
	}

	private int xLocToIndex(int xCoord) {
		return xArrayCenter + (xCoord - xOrigin);
	}

	private int yLocToIndex(int yCoord) {
		return yArrayCenter + (yCoord - yOrigin);
	}

	private int heuristicEstimate(MapLocation startLocation, MapLocation goal) {
		return startLocation.distanceSquaredTo(goal);
	}

	public Direction[] reconstructPath(MapLocationTuple current) {
		ArrayList<Direction> totalPath = new ArrayList<Direction>();

		while (current.cameFrom != null) {
			totalPath.add(current.location
					.directionTo(current.cameFrom.location));
			current = current.cameFrom;
		}

		Direction directions[] = totalPath.toArray(new Direction[totalPath
				.size()]);

		System.out.println("Path Calculated for Robot at: "
				+ myRC.getLocation().x + ", " + myRC.getLocation());
		MapLocation movedTo = myRC.getLocation();

		for (Direction d : directions) {
			movedTo = movedTo.add(d);
			System.out.println("Travel: " + d.name() + " to " + movedTo.x
					+ ", " + movedTo.y + "\t\t\t" + "Terrain Mapped Here: "
					+ map[xLocToIndex(movedTo.x)][yLocToIndex(movedTo.y)]);

		}

		return directions;
	}

	public void setUpInitialMap() {
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				MapLocation examine = new MapLocation(myRC.getLocation().x + i,
						myRC.getLocation().y + j);
				map[xLocToIndex(examine.x)][yLocToIndex(examine.y)] = terrainToTileIdentifier(myRC
						.senseTerrainTile(examine));

				examine = new MapLocation(myRC.getLocation().x + i,
						myRC.getLocation().y - j);
				map[xLocToIndex(examine.x)][yLocToIndex(examine.y)] = terrainToTileIdentifier(myRC
						.senseTerrainTile(examine));

				examine = new MapLocation(myRC.getLocation().x - i,
						myRC.getLocation().y + j);
				map[xLocToIndex(examine.x)][yLocToIndex(examine.y)] = terrainToTileIdentifier(myRC
						.senseTerrainTile(examine));

				examine = new MapLocation(myRC.getLocation().x - i,
						myRC.getLocation().y - j);
				map[xLocToIndex(examine.x)][yLocToIndex(examine.y)] = terrainToTileIdentifier(myRC
						.senseTerrainTile(examine));

			}
		}

		MapLocation powerCoreLocation = myRC.sensePowerCore().getLocation();
		map[xLocToIndex(powerCoreLocation.x)][yLocToIndex(powerCoreLocation.y)] = TileIdentifier.ALLY_POWERCORE;
	}

	private TileIdentifier terrainToTileIdentifier(TerrainTile terrain) {
		if (null == terrain)
			return null;
		switch (terrain) {
		case LAND:
			return TileIdentifier.LAND;
		case VOID:
			return TileIdentifier.WALL;
		case OFF_MAP:
			return TileIdentifier.OFF_MAP;
		default:
			return null;
		}
	}

	private class MapLocationTuple implements Comparable<MapLocationTuple> {
		protected final MapLocation location;
		protected Integer combinedCost;
		protected MapLocationTuple cameFrom;

		private MapLocationTuple(MapLocation location, int combinedCost,
				MapLocationTuple cameFrom) {
			this.location = location;
			this.combinedCost = combinedCost;
			this.cameFrom = cameFrom;
		}

		@Override
		public int compareTo(MapLocationTuple other) {
			return this.combinedCost.compareTo(other.combinedCost);
		}
	}
}

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Solution {

	private static final int buffer = 100;

	public static void main(String[] args) throws IOException {

		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(Solution.class.getResourceAsStream("input.txt")))) {
			List<Coord> coords = new ArrayList<>(100);
			char[] ids = "abcdefghijklmnopqrstvuwxyzABCDEFGHIJKLMNOPQRSTVUWXYZ".toCharArray();
			int index = 0;
			for (String line; (line = reader.readLine()) != null;) {
				Coord coord = Coord.parse(line);
				coord.setId(ids[index++]);
				coords.add(coord);
			}

			int minX = coords.stream().mapToInt(Coord::getX).min().orElseThrow(IllegalArgumentException::new);
			int minY = coords.stream().mapToInt(Coord::getY).min().orElseThrow(IllegalArgumentException::new);
			int maxX = coords.stream().mapToInt(Coord::getX).max().orElseThrow(IllegalArgumentException::new);
			int maxY = coords.stream().mapToInt(Coord::getY).max().orElseThrow(IllegalArgumentException::new);

			int bufferedMinX = minX - buffer;
			int bufferedMinY = minY - buffer;
			int bufferedMaxX = maxX + buffer;
			int bufferedMaxY = maxY + buffer;

			int dX = maxX - minX + 1;
			int dY = maxY - minY + 1;

			List<Coord> board = new ArrayList<>(dX * dY);

			for (int y = bufferedMinY; y <= bufferedMaxY; y++) {
				for (int x = bufferedMinX; x <= bufferedMaxX; x++) {
					final Coord c = new Coord(x, y);
					board.add(c);

					Entry<Integer, List<Coord>> closestCoordsEntry = coords.stream()
							.collect(groupingBy(coord -> coord.manhattanDistance(c), TreeMap::new, toList()))
							.firstEntry();
					List<Coord> closestCoords = closestCoordsEntry.getValue();
					if (closestCoords.size() == 1) {
						Coord closestCoord = closestCoords.get(0);
						c.setId(closestCoord.getId());
					}

				}
			}

			// Part 1
			{

				final Set<Character> borderIds = board.stream().filter(Coord::hasId).filter(coord -> (
				//
				coord.getX() == bufferedMinX ||
				//
						coord.getY() == bufferedMinY ||
						//
						coord.getX() == bufferedMaxX ||
						//
						coord.getY() == bufferedMaxY)).map(Coord::getId).collect(Collectors.toSet());
				borderIds.add((char) 0);

				Entry<Character, List<Coord>> largestAreaEntry = board.stream()
						//
						.filter(coord -> !borderIds.contains(coord.getId()))
						//
						.collect(groupingBy(Coord::getId))
						//
						.entrySet().stream()
						//
						.max(Entry.comparingByValue(Comparator.comparingInt(Collection::size)))
						.orElseThrow(IllegalStateException::new);

				System.out.println("Largest area around: [" + largestAreaEntry.getKey() + "] has size ["
						+ largestAreaEntry.getValue().size() + "].");
			}

			// Part 2
			{

				System.out.println("Size of the area of coordinates with total distances less than 10000  ["
						+ board.stream().map(c -> {
							final int totalDistance = coords.stream().mapToInt(c::manhattanDistance).sum();
							return new CoordDistance(c.getId(), c.getX(), c.getY(), totalDistance);
						}).filter(c -> c.getD() < 10000).count() + "].");

			}

		}
	}
}
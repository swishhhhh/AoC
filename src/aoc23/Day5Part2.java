package aoc23;

import utils.Helper;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * <a href="https://adventofcode.com/2023/day/5">Advent of Code 2023 Day 5</a>
 */
public class Day5Part2 {

	static class RangeMapping {
		private final long destinationStart;
		private final long sourceStart;
		private final long length;

		public RangeMapping(long destinationStart, long sourceStart, long length) {
			this.destinationStart = destinationStart;
			this.sourceStart = sourceStart;
			this.length = length;
		}

		public long getDestinationStart() {
			return destinationStart;
		}
		public long getSourceStart() {
			return sourceStart;
		}
		public long getLength() {
			return length;
		}

		@Override
		public String toString() {
			return "RangeMapping{" +
					"destinationStart=" + destinationStart +
					", sourceStart=" + sourceStart +
					", length=" + length +
					'}';
		}
	}

	static class Node {
		String type;
		long start;
		long length;
		List<NodeSegment> childSegments;
		Edge parentEdge;

		public Node(String type, long start, long length, Edge parentEdge) {
			this.type = type;
			this.start = start;
			this.length = length;
			this.parentEdge = parentEdge;
		}

		public void addChildSegment(NodeSegment segment) {
			if (childSegments == null) {
				childSegments = new ArrayList<>();
			}
			childSegments.add(segment);
		}

		@Override
		public String toString() {
			return "Node{" +
					"type='" + type + '\'' +
					", start=" + start +
					", length=" + length +
					'}';
		}
	}

	static class NodeSegment {
		long start;
		long length;
		Node parentNode;
		Edge childEdge;

		public NodeSegment(long start, long length, Node parentNode) {
			this.start = start;
			this.length = length;
			this.parentNode = parentNode;
		}

		public void setEdge(Edge edge) {
			this.childEdge = edge;
		}

		@Override
		public String toString() {
			return "NodeSegment{" +
					"start=" + start +
					", length=" + length +
					", parentNode=" + parentNode +
					'}';
		}
	}

	static class Edge {
		long increment;
		NodeSegment parentSegment;

		public Edge(long increment, NodeSegment parentSegment) {
			this.increment = increment;
			this.parentSegment = parentSegment;
		}

		@Override
		public String toString() {
			return "Edge{" +
					"increment=" + increment +
					'}';
		}
	}

	private static final ArrayList<RangeMapping> seedToSoilMappings = new ArrayList<>();
	private static final ArrayList<RangeMapping> soilToFertilizerMappings = new ArrayList<>();
	private static final ArrayList<RangeMapping> fertilizerToWaterMappings = new ArrayList<>();
	private static final ArrayList<RangeMapping> waterToLightMappings = new ArrayList<>();
	private static final ArrayList<RangeMapping> lightToTempMappings = new ArrayList<>();
	private static final ArrayList<RangeMapping> tempToHumidityMappings = new ArrayList<>();
	private static final ArrayList<RangeMapping> humidityToLocationMappings = new ArrayList<>();

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day5_input.txt");

		//initialize maps
		List<Long> seeds = null;
		ArrayList<RangeMapping> rangeMappings = null;
		for (String line: lines) {
			if (line.trim().length() == 0 || line.startsWith("#")) {
				continue;
			}

			if (line.startsWith("seeds:")) {
				seeds = Helper.extractLongsFromText(line);
			} else if (line.startsWith("seed-to-soil")) {
				rangeMappings = seedToSoilMappings;
			} else if (line.startsWith("soil-to-fertilizer")) {
				rangeMappings = soilToFertilizerMappings;
			} else if (line.startsWith("fertilizer-to-water")) {
				rangeMappings = fertilizerToWaterMappings;
			} else if (line.startsWith("water-to-light")) {
				rangeMappings = waterToLightMappings;
			} else if (line.startsWith("light-to-temperature")) {
				rangeMappings = lightToTempMappings;
			} else if (line.startsWith("temperature-to-humidity")) {
				rangeMappings = tempToHumidityMappings;
			} else if (line.startsWith("humidity-to-location")) {
				rangeMappings = humidityToLocationMappings;
			} else {
				List<Long> nums = Helper.extractLongsFromText(line);
				RangeMapping range = new RangeMapping(nums.get(0), nums.get(1), nums.get(2));
				rangeMappings.add(range);
			}
		}

		//build graph --------------------
		List<Node> seedNodes = createSeedNodes(seeds);
		List<Node> soilNodes = addGraphLayer(seedNodes, "soil", seedToSoilMappings);
		List<Node> fertilizerNodes = addGraphLayer(soilNodes, "fertilizer", soilToFertilizerMappings);
		List<Node> waterNodes = addGraphLayer(fertilizerNodes, "water", fertilizerToWaterMappings);
		List<Node> lightNodes = addGraphLayer(waterNodes, "light", waterToLightMappings);
		List<Node> temperatureNodes = addGraphLayer(lightNodes, "temperature", lightToTempMappings);
		List<Node> humidityNodes = addGraphLayer(temperatureNodes, "humidity", tempToHumidityMappings);
		List<Node> locationNodes = addGraphLayer(humidityNodes, "location", humidityToLocationMappings);

		//get lowest location
		long lowestLocation = Long.MAX_VALUE;
		for (Node n : locationNodes) {
			lowestLocation = Math.min(n.start, lowestLocation);
		}

		System.out.printf("Lowest Location = %s%n", lowestLocation);
	}

	private static List<Node> createSeedNodes(List<Long> seeds) {
		List<Node> seedNodes = new ArrayList<>();
		for (int i = 0; i < seeds.size(); i+= 2) {
			long seed = seeds.get(i);
			long length = seeds.get(i + 1);
			seedNodes.add(new Node("Seed", seed, length, null));
		}
		return seedNodes;
	}

	private static List<Node> addGraphLayer(List<Node> parentNodes, String childNodesType,
											ArrayList<RangeMapping> rangeMappings) {
		//sort range-mappings list on sourceStart field
		rangeMappings.sort(Comparator.comparingLong(RangeMapping::getSourceStart));

		List<Node> childNodes = new ArrayList<>();
		for (Node parentNode : parentNodes) {
			long segmentStart = parentNode.start;
			long parentEnd = parentNode.start + parentNode.length;
			long segmentLen, increment;

			for (RangeMapping rm : rangeMappings) {
				long rangeStart = rm.getSourceStart();
				long rangeLen = rm.getLength();
				long rangeEnd = rangeStart + rangeLen;

				if (rangeEnd < segmentStart) {
					continue;
				}

				if (rangeStart <= parentEnd) {
					//fill in gap (if any) between current and previous ranges (as well as gap before first range)
					if (segmentStart < rangeStart) {
						segmentLen = rangeStart - segmentStart;
						increment = 0;
						addSegment(childNodesType, childNodes, parentNode, segmentStart, increment, segmentLen);

						//update segmentStart to match rangeStart
						segmentStart = rangeStart;
					}

					//add segment corresponding to this range
					segmentLen = rangeLen - (segmentStart - rangeStart);
					segmentLen = Math.min(segmentLen, parentEnd - rangeStart);
					segmentLen = Math.min(segmentLen, parentNode.length); //segment len can never be longer than parent
					increment = rm.getDestinationStart() - rm.getSourceStart();
					addSegment(childNodesType, childNodes, parentNode, segmentStart, increment, segmentLen);

					//update segmentStart for next segment...
					segmentStart = segmentStart + segmentLen;
				}

				if (rangeStart > parentEnd) {
					break;
				}
			}

			//fill in gap for tail segment (if necessary)
			if (segmentStart < parentEnd) {
				segmentLen = parentEnd - segmentStart;
				increment = 0;
				addSegment(childNodesType, childNodes, parentNode, segmentStart, increment, segmentLen);
			}
		}

		return childNodes;
	}

	private static void addSegment(String childNodesType, List<Node> childNodes, Node parentNode, long segmentStart,
								   long increment, long segmentLen) {
		//create segment
		NodeSegment ns = new NodeSegment(segmentStart, segmentLen, parentNode);
		parentNode.addChildSegment(ns);

		//create edge
		Edge edge = new Edge(increment, ns);
		ns.setEdge(edge);

		//create childNode
		long childNodeStart = segmentStart + increment;
		Node childNode = new Node(childNodesType, childNodeStart, segmentLen, edge);
		childNodes.add(childNode);
	}
}

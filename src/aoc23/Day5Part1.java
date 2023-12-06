package aoc23;

import utils.Helper;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://adventofcode.com/2023/day/5">Advent of Code 2023 Day 5</a>
 */
public class Day5Part1 {

	static class RangeMapping {
		long destinationStart;
		long sourceStart;
		long length;

		public RangeMapping(long destinationStart, long sourceStart, long length) {
			this.destinationStart = destinationStart;
			this.sourceStart = sourceStart;
			this.length = length;
		}

		public long getLength() {
			return length;
		}

		boolean isInRange(long n) {
			return n >= sourceStart && n < (sourceStart + length);
		}

		long applyMapping(long n) {
			return n + (destinationStart - sourceStart);
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

		//------
		long lowestLocation = Long.MAX_VALUE;
		for (long seed: seeds) {
			//lookup soil
			long soil = lookupMapping(seed, seedToSoilMappings);
			long fertilizer = lookupMapping(soil, soilToFertilizerMappings);
			long water = lookupMapping(fertilizer, fertilizerToWaterMappings);
			long light = lookupMapping(water, waterToLightMappings);
			long temp = lookupMapping(light, lightToTempMappings);
			long humidity = lookupMapping(temp, tempToHumidityMappings);
			long location = lookupMapping(humidity, humidityToLocationMappings);
//			System.out.printf("Seed %s -> soil %s -> fert %s -> water %s -> light %s -> temp %s -> humidity %s -> location %s%n",
//					seed, soil, fertilizer, water, light, temp, humidity, location);
			lowestLocation = Math.min(location, lowestLocation);
		}

		System.out.printf("Lowest Location = %s%n", lowestLocation);
	}

	private static long lookupMapping(long source, ArrayList<RangeMapping> sourceToTargetMappings) {
		for (RangeMapping rm : sourceToTargetMappings) {
			if (rm.isInRange(source)) {
				return rm.applyMapping(source);
			}
		}

		return source; //default is for source to map to same target
	}
}

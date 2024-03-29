package aoc22;

import utils.Helper;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <a href="https://adventofcode.com/2022/day/7">Advent of Code 2022 Day 7</a>
 */
public class Day07Part1 {

	static class Directory {
		Directory parent = null;
		String path;
		private long totalSize;
		List<Directory> subDirs = new ArrayList<>();
		Map<String, Directory> dirMap = new HashMap<>();
		List<File> files = new ArrayList<>();

		public void calculateTotalSize() {
			this.totalSize = 0L;
			for (File f: files) {
				this.totalSize+= f.size;
			}

			for (Directory d: subDirs) {
				d.calculateTotalSize();
				this.totalSize+= d.getSize();
			}
		}

		public long getSize() {
			return this.totalSize;
		}
	}

	static class File {
		String name;
		long size;
	}

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc22/Day7_input.txt");

		Directory currentDir = new Directory();
		currentDir.path = "/";
		Directory root = currentDir;
		List<Directory> allDirs = new ArrayList<>();
		allDirs.add(root);

		for (String line: lines) {
			if (line.startsWith("$ cd /")) {
				continue;
			}

			if (line.startsWith("dir ")) {
				Directory dir = new Directory();
				dir.parent = currentDir;
				String dirName = line.split(" ")[1];
				dir.path = dir.parent.path + dirName + "/";
				currentDir.subDirs.add(dir);
				currentDir.dirMap.put(dirName, dir);
				allDirs.add(dir);
				continue;
			}

			List<Integer> ints = Helper.extractIntsFromText(line);
			if (ints.size() == 1) { //file
				File file = new File();
				file.name = line.split(" ")[1];
				file.size = ints.get(0);
				currentDir.files.add(file);
				continue;
			}

			if (line.startsWith("$ cd ..")) {
				if (currentDir.parent != null) {
					currentDir = currentDir.parent;
					continue;
				}
			}

			if (line.startsWith("$ cd")) {
				String dirName = line.split(" ")[2];
				currentDir = currentDir.dirMap.get(dirName);
			}

		}

		long totalSelectedSize = 0L;
		root.calculateTotalSize();
		for (Directory dir: allDirs) {
			long size = dir.getSize();
			if (size <= 100000L) {
				totalSelectedSize+= size;
			}
		}

		long answer = totalSelectedSize;
		System.out.printf("Total selected sizes = %s%n", totalSelectedSize);

		long expected = 1325919;
		if (answer != expected) {
			throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
		}
	}
}

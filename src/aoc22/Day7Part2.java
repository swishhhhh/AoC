package aoc22;

import utils.Helper;
import utils.ResourceLoader;

import java.util.*;

/**
 * <a href="https://adventofcode.com/2022/day/7">Advent of Code 2022 Day 7</a>
 */
public class Day7Part2 {

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

		//---------
		long threshold = 40_000_000L;
		root.calculateTotalSize();
		long rootSize = root.getSize();
		long neededSpace = rootSize - threshold;
		TreeMap<Long, Directory> dirsMap = new TreeMap<>(); //sorted map

		//get all sizes and store in order of their sizes
		for (Directory dir: allDirs) {
			long size = dir.getSize();
			dirsMap.put(size, dir);
		}

		for (Long size : dirsMap.keySet()) {
			if (size >= neededSpace) {
				System.out.println(size);
				break;
			}
		}
	}
}

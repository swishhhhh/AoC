import utils.ResourceLoader;
import java.util.List;

public class Day1Part1 {

	public static void main(String[] args) throws Exception {
		List<Integer> numbers = ResourceLoader.readInts("Day1Part1_input.txt");

		int numberOfIncreases = 0;
		boolean first = true;
		int prevNumber = -1; // = numbers.get(0);
		for (Integer number: numbers) {
			if (first) {
				prevNumber = number;
				first = false;
				continue;
			}

			if (number > prevNumber) {
				numberOfIncreases++;
			}
			prevNumber = number;
		}

		System.out.println("Number of increases = " + numberOfIncreases);
	}
}

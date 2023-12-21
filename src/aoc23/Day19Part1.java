package aoc23;

import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <a href="https://adventofcode.com/2023/day/19">Advent of Code 2023 Day 19</a>
 */
public class Day19Part1 {

	static class Workflow {
		String name;
		List<WorkflowRule> rules;
		String defaultDestination;

		public Workflow(String name, List<WorkflowRule> rules, String defaultDestination) {
			this.name = name;
			this.rules = rules;
			this.defaultDestination = defaultDestination;
		}

		@Override
		public String toString() {
			return "Workflow{" +
					"name='" + name + '\'' +
					", rules=" + rules +
					", defaultDestination='" + defaultDestination + '\'' +
					'}';
		}
	}

	static class WorkflowRule {
		String attributeName;
		String testOperation;
		int testValue;
		String destinationIfTrue;
		String label;

		public WorkflowRule(String attributeName, String testOperation, int testValue, String destinationIfTrue, String label) {
			this.attributeName = attributeName;
			this.testOperation = testOperation;
			this.testValue = testValue;
			this.destinationIfTrue = destinationIfTrue;
			this.label = label;
		}

		@Override
		public String toString() {
			return "Rule{'" + label + "'}";
		}
	}

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day19_input.txt");

		Map<String, Workflow> workflows = parseWorkflows(lines);
		List<Map<String, Integer>> parts = parseParts(lines);

		long sum = process(workflows, parts);

		System.out.printf("Sum = %s%n", sum);

		long expected = 480738;
		if (sum != expected) {
			throw new RuntimeException(String.format("Output %s doesn't match expected %s", sum, expected));
		}
	}

	private static long process(Map<String, Workflow> workflows, List<Map<String, Integer>> parts) {
		List<Map<String, Integer>> acceptedParts = new ArrayList<>();
		for (Map<String, Integer> part : parts) {
			if (isPartAccepted(part, workflows)) {
				acceptedParts.add(part);
			}
		}

		long sum = 0;
		for (Map<String, Integer> part : acceptedParts) {
			for (Integer value : part.values()) {
				sum+= value;
			}
		}

		return sum;
	}

	private static boolean isPartAccepted(Map<String, Integer> part, Map<String, Workflow> workflows) {
		Workflow wf = workflows.get("in");
		while (true) {
			String wfDest = null;
			for (WorkflowRule rule : wf.rules) {
				int partValue = part.get(rule.attributeName);
				boolean rulePassed = rule.testOperation.equals(">")
						? partValue > rule.testValue
						: partValue < rule.testValue;

				if (rulePassed) {
					wfDest = rule.destinationIfTrue;
					break;
				}
			}
			if (wfDest == null) {
				wfDest = wf.defaultDestination;
			}

			if (wfDest.equals("A")) {
				return true;
			} else if (wfDest.equals("R")) {
				return false;
			} else {
				wf = workflows.get(wfDest);
				if (wf == null) { //should not happen with valid input
					throw new RuntimeException("Invalid workflow name " + wfDest);
				}
			}
		}
	}

	private static Map<String, Workflow> parseWorkflows(List<String> lines) {
		Map<String, Workflow> workflows = new HashMap<>();

		for (String line: lines) {
			if (line.isBlank()) {
				break;
			}

			String[] ary = line.split("\\{"); 	//e.g.  px{a<2006:qkq,m>2090:A,rfg}
			String wfName = ary[0];			  	//e.g.  px
			ary[1] = ary[1].replace("}", "");	//e.g.  a<2006:qkq,m>2090:A,rfg
			ary = ary[1].split(",");			//e.g.  {a<2006:qkq, m>2090:A, rfg}

			String defaultDest = ary[ary.length - 1]; //e.g. rfg

			List<WorkflowRule> rules = new ArrayList<>();
			for (int i = 0; i < ary.length - 1; i++) {
				String attributeName;
				String testOperation;
				int testValue;
				String destinationIfTrue;
				String label = ary[i];

				String[] ary2 = ary[i].split(":"); 			//e.g.  {a<2006, qkq}
				destinationIfTrue = ary2[1]; 				//e.g.  qkq
				attributeName = ary2[0].substring(0, 1);  	//e.g.  a
				testOperation = ary2[0].substring(1, 2);	//e.g.  <
				testValue = Integer.parseInt(ary2[0].substring(2));  //e.g. 2006
				WorkflowRule wfRule = new WorkflowRule(attributeName, testOperation, testValue, destinationIfTrue, label);
				rules.add(wfRule);
			}

			workflows.put(wfName, new Workflow(wfName, rules, defaultDest));
		}

		return workflows;
	}

	private static List<Map<String, Integer>> parseParts(List<String> lines) {
		List<Map<String, Integer>> parts = new ArrayList<>();
		boolean blankLineEncountered = false;

		for (String line: lines) {
			if (line.isBlank()) {
				blankLineEncountered = true;
				continue;
			}

			if (!blankLineEncountered) {
				continue;
			}

			Map<String, Integer> part = new HashMap<>();
			line = line.replace("{", "").replace("}", "");
			String[] ary = line.split(",");
			for (String token : ary) {
				String[] tokenAry = token.split("=");
				part.put(tokenAry[0], Integer.parseInt(tokenAry[1]));
			}

			parts.add(part);
		}

		return parts;
	}
}

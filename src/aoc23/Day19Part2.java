package aoc23;

import utils.ResourceLoader;

import java.util.*;

/**
 * <a href="https://adventofcode.com/2023/day/19">Advent of Code 2023 Day 19</a>
 */
public class Day19Part2 {

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

	static class WorkflowJob {
		String wfName;
		Map<String, int[]> ranges;

		public WorkflowJob(String wfName, Map<String, int[]> ranges) {
			this.wfName = wfName;
			this.ranges = ranges;
		}

		public long getPartsCount() {
			long count = 1;
			for (int[] range : ranges.values()) {
				count*= (range[1] - range[0]);
			}
			return count;
		}

		@Override
		public String toString() {
			return "WorkflowJob{" +
					"wfName='" + wfName + '\'' +
					", ranges=" + ranges +
					'}';
		}
	}

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceLoader.readStrings("aoc23/Day19_input.txt");

		Map<String, Workflow> workflows = parseWorkflows(lines);

		long sum = process(workflows);
		System.out.printf("Sum = %s%n", sum);

		long expected = 131550418841958L;
		if (sum != expected) {
			throw new RuntimeException(String.format("Output %s doesn't match expected %s", sum, expected));
		}
	}

	private static long process(Map<String, Workflow> workflows) {
		Map<String, int[]> partRanges = new HashMap<>();
		for (String attrName : List.of("x", "m", "a", "s")) {
			partRanges.put(attrName, new int[]{1, 4001});
		}
		WorkflowJob job = new WorkflowJob("in", partRanges);
		Stack<WorkflowJob> stack = new Stack<>();
		stack.push(job);

		long acceptedPartsCnt = 0, rejectedPartsCnt = 0, totalExpectedParts = job.getPartsCount();

		while (!stack.isEmpty()) {
			job = stack.pop();
			Workflow wf = workflows.get(job.wfName);
			String wfDest;

			for (WorkflowRule rule : wf.rules) {
				/*
				 * Each rule splits the job into 2 segments (based on 1 of the 4 ranges). We'll refer to them as the
				 * 'split-off' segment (the segment that satisfies the rule) and the 'remaining-segment'.
				 * The split-off segment will take 1 of 4 branches:
				 *     1) if the range it splits on results in a length of zero (e.g. start and end range value are
				 * 		  the same) then do nothing with it.
				 *   otherwise:
				 *     2) accepted (increment acceptedParts)
				 *     3) rejected (increment rejectedParts)
				 *     4) routed to another workflow (back onto the queue)
				 *
				 *   The remaining-segment continues on to the next rule in the workflow. If there are no more rules
				 *   in the workflow, the segment is sent to the "destinationIfTrue" part of the rule, where it faces
				 *   a similar choice of the same 4 branches above.
				*/

				WorkflowJob[] segments = splitJob(job, rule);

				//evaluate split-off segment
				job = segments[0];
				if (job.getPartsCount() == 0) { //no point in continuing the workflow
					break;
				}

				int[] rangeWasSplitOn = job.ranges.get(rule.attributeName);
				int partValue = rangeWasSplitOn[0]; //any number in the range {>= to range[0] and < range[1]} will do
				boolean rulePassed = rule.testOperation.equals(">")
						? partValue > rule.testValue
						: partValue < rule.testValue;

				if (rulePassed) {
					wfDest = rule.destinationIfTrue;
					if (wfDest.equals("A")) {
						acceptedPartsCnt+= job.getPartsCount();
					} else if (wfDest.equals("R")) {
						rejectedPartsCnt+= job.getPartsCount();
					} else {
						job.wfName = wfDest;
						stack.push(job);
					}
				}

				//continue on to the rest of the workflow rules with the remaining segment
				job = segments[1];
			}

			//if any ranges are zero-length (job.getPartsCount() will evaluate to zero) continue to top of loop.
			if (job.getPartsCount() == 0) {
				continue;
			}

			wfDest = wf.defaultDestination;
			if (wfDest.equals("A")) {
				acceptedPartsCnt+= job.getPartsCount();
			} else if (wfDest.equals("R")) {
				rejectedPartsCnt+= job.getPartsCount();
			} else {
				wf = workflows.get(wfDest);
				if (wf == null) { //should not happen with valid input
					throw new RuntimeException("Invalid workflow name " + wfDest);
				}
				job.wfName = wfDest;
				stack.push(job);
			}
		}

		if (acceptedPartsCnt + rejectedPartsCnt != totalExpectedParts) {
			throw new RuntimeException(
					String.format("Something went wrong, accepted (%s) + rejected (%s) != expected (%s)%n",
							acceptedPartsCnt, rejectedPartsCnt, totalExpectedParts));
		}

		return acceptedPartsCnt;
	}

	private static WorkflowJob[] splitJob(WorkflowJob job, WorkflowRule rule) {
		//initialize the 2 segments as clones of their parent
		WorkflowJob splitOffSegment = new WorkflowJob(job.wfName, new HashMap<>(job.ranges));
		WorkflowJob remainingSegment = new WorkflowJob(job.wfName, new HashMap<>(job.ranges));

		int[] rangeToSplitOn = job.ranges.get(rule.attributeName);
		if (rule.testOperation.equals("<")) {
			int[] splitOffRange = new int[]{rangeToSplitOn[0], rule.testValue};
			splitOffSegment.ranges.put(rule.attributeName, splitOffRange);

			int[] remainingRange = new int[]{rule.testValue, rangeToSplitOn[1]};
			remainingSegment.ranges.put(rule.attributeName, remainingRange);

		} else {
			int[] splitOffRange = new int[]{rule.testValue + 1, rangeToSplitOn[1]};
			splitOffSegment.ranges.put(rule.attributeName, splitOffRange);

			int[] remainingRange = new int[]{rangeToSplitOn[0], rule.testValue + 1};
			remainingSegment.ranges.put(rule.attributeName, remainingRange);
		}

		return new WorkflowJob[]{splitOffSegment, remainingSegment};
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
}

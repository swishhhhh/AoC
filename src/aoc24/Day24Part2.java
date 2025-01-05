package aoc24;

import aoc24.day24.BooleanGate;
import org.apache.commons.math3.util.Pair;
import utils.ResourceLoader;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static utils.Helper.isNumeric;
import static aoc24.day24.BooleanGate.Operator.*;

/**
 * <a href="https://adventofcode.com/2024/day/24">Advent of Code 2024 Day 24</a>
 * <P>
 * Solution inspired by:
 *      <a href="https://www.reddit.com/r/adventofcode/comments/1hnb969/2024_day_24_part_2_aliasing_wires_to_spot_the/">this post</a>
 * Pre-req reading/understanding:
 *      <a href="https://www.sciencedirect.com/topics/computer-science/ripple-carry-adder">Ripple Carry Adder</a>
 * <P>
 * The input represents a 45 bit ripple carry adder represented by an initial group of 2 gates (a half-adder) and a
 *  subsequent 44 groups of 5 gates each (full adders). The trick here is to identify the gates, sort them in correct
 *  order, and then spot the defects (crossed output wires) and swap them.
 * <P>
 * Each adder has 2 inputs (x & y), a main/sum output (z), and a carry output. Full adders also have an additional (3rd)
 *  carry-in input. Each adder's/group's carry-out is the input to the next one. The first group's half adder is
 *  represented by 2 gates:
 *  <PRE>
 *      1. X(0) XOR Y(0) -> _XOR(0)
 *      2. X(0) AND Y(0) -> _CARRY(0)
 *  </PRE>
 *  The subsequent adders/groups are represented by 5 gates each to accommodate the additional intermediate operations
 *  needed to take into account the carry-in from the previous group:
 *  <PRE>
 *      1. X(N) XOR Y(N) -> _XOR(N)
 *      2. X(N) AND Y(N) -> _AND(N)
 *      3. _XOR(N) XOR _CARRY(N-1) -> Z(N)
 *      4. _XOR(N) AND _CARRY(N-1) -> _CARRY_INTERMEDIATE(N)
 *      5. _AND(N) OR  _CARRY_INTERMEDIATE(N-1) -> _CARRY(N)
 *  </PRE>
 *  Once you understand this pattern, you just have to iteratively (starting from group 1..44) inspect gates 3,4,5 of
 *  each group (gates 1 & 2 are always correct) and spot the bad output for any of the gates starting from the first.
 *  When you hit your first bad output, you swap it with the correct output (which we can always predict based on the
 *  pattern above (N, N+1, N+2), etc) and restart the process from the top. Keep doing this until you get to the end
 *  (last gate). Since gates have random meaningless names, before we can validate them we first rename them to match
 *  the patterns above. This (renaming of the gate variables) is done iteratively over a few phases (see code for details).
 */
public class Day24Part2 {
    private static final boolean DEBUG = false;
    private static final Pattern GATE_PATTERN = Pattern.compile("(\\w+) (\\S+) (\\w+) -> (\\w+)");
    private static final Pattern VAR_PATTERN = Pattern.compile("([xy])(\\d+)");
    private static final String FORMAT_CARRY = "_CARRY%02d";
    private static final String FORMAT_XOR = "_XOR%02d";
    private static final String FORMAT_AND = "_AND%02d";
    private static final String FORMAT_CARRY_INTERMEDIATE = "_CARRY_INTERMEDIATE%02d";
    private static final String FORMAT_Z = "z%02d";

    private record GateGroup(BooleanGate gate3, BooleanGate gate4, BooleanGate gate5) {}

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day24_input.txt");

        String answer = new Day24Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        String expected = "djg,dsd,hjm,mcq,sbg,z12,z19,z37";
        if (!answer.equals(expected)) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private String execute(List<String> lines) {
        List<BooleanGate> gates = new ArrayList<>();
        loadInput(lines, gates);

        Map<String, String> originalVarNames = new HashMap<>();
        List<String> swappedVars = new ArrayList<>();
        while (true) {
            List<BooleanGate> copyOfGates = deepCopy(gates); //preserve the original gates and only work off the copy
            aliasVariablesPhase1(copyOfGates, originalVarNames);
            aliasVariablesPhase2(copyOfGates, originalVarNames);
            sortGatesPhase3(copyOfGates);
            Pair<String, String> swap = validateAndSwapPhase4(copyOfGates, originalVarNames);
            if (swap == null) {
                break;
            }

            swappedVars.add(swap.getFirst());
            swappedVars.add(swap.getSecond());
            if (DEBUG) {
                System.out.printf("Swapping %s with %s%n", swap.getFirst(), swap.getSecond());
            }

            //unlike the rest of the operations (renames, swaps) done on the copy of the gates, this final step is done on the original ones
            swapOutputVarNamesAcrossAllGatesPhase4(gates, swap.getFirst(), swap.getSecond());
        }

        if (DEBUG) {
            printGates(gates);
        }

        //return a comma delimited string representing swappedVars sorted
        return swappedVars.stream().sorted().collect(Collectors.joining(","));
    }

    private void loadInput(List<String> lines, List<BooleanGate> gates) {
        //read in solved variables (first part of input) and boolean-gates (2nd part of input)
        lines.stream()
            .filter(line -> !line.startsWith("#"))
            .filter(line -> line.contains("->"))
            .forEach(line -> {
                Matcher matcher = GATE_PATTERN.matcher(line);
                if (matcher.matches()) {
                    String var1 = matcher.group(1);
                    String operatorName = matcher.group(2);
                    String var2 = matcher.group(3);
                    String resultVar = matcher.group(4);
                    gates.add(new BooleanGate(resultVar, var1, var2,
                            BooleanGate.fromSymbol(operatorName)));
                }
            });
    }

    private List<BooleanGate> deepCopy(List<BooleanGate> gates) {
        return gates.stream()
                .map(BooleanGate::copy)
                .collect(Collectors.toList());
    }

    private void aliasVariablesPhase1(List<BooleanGate> gates, Map<String, String> originalVarNames) {
    /*
       rename the z variables for all ANDs and XORs of x and y pairs with matching numbers, e.g.:
           x39 AND y39 -> rjk:  rjk is renamed to _AND39
           x21 XOR y21 -> hgv:  hgv is renamed to _XOR21
    */
        Collection<Pair<String, String>> renames = new ArrayList<>(gates.size());

        gates.stream()
                .filter(this::isMatchingXYPair)
                .forEach(gate -> processRenamePhase1(gate, renames, originalVarNames));

        renameVariables(gates, renames);
    }

    private boolean isMatchingXYPair(BooleanGate gate) {
        Matcher matcherA = VAR_PATTERN.matcher(gate.getOperandVariable1());
        Matcher matcherB = VAR_PATTERN.matcher(gate.getOperandVariable2());

        if (!matcherA.matches() || !matcherB.matches()) {
            return false;
        }

        String letterA = matcherA.group(1);
        String letterB = matcherB.group(1);
        int numA = Integer.parseInt(matcherA.group(2));
        int numB = Integer.parseInt(matcherB.group(2));

        return numA == numB && isXYPair(letterA, letterB);
    }

    private boolean isXYPair(String letterA, String letterB) {
        return (letterA.equals("x") && letterB.equals("y")) ||
               (letterA.equals("y") && letterB.equals("x"));
    }

    private void processRenamePhase1(BooleanGate gate, Collection<Pair<String, String>> renames,
                               Map<String, String> originalVarNames) {
        Matcher matcher = VAR_PATTERN.matcher(gate.getOperandVariable1());
        String num = matcher.matches() ? matcher.group(2) : "";

        String from = gate.getResultVarName();
        String to = String.format("_%s%02d", gate.getOperator(), Integer.parseInt(num));

        renames.add(new Pair<>(from, to));
        originalVarNames.put(to, from);

        if (DEBUG) {
            System.out.printf("Renaming %s to %s%n", from, to);
        }
    }

    private void aliasVariablesPhase2(List<BooleanGate> gates, Map<String, String> originalVarNames) {
        List<Pair<String, String>> renames = new ArrayList<>();

        // Initial renaming of AND00 to CARRY00
        processRenamePhase2(renames, "_AND00", "_CARRY00", gates, originalVarNames);

        // Process subsequent gates
        for (int i = 1; i < gates.size(); i++) {
            if (!processCarryIntermediateGate(i, gates, renames, originalVarNames) ||
                    !processCarryGate(i, gates, renames, originalVarNames)) {
                break;
            }
        }
    }

    private boolean processCarryIntermediateGate(int gateIdx, List<BooleanGate> gates,
                             List<Pair<String, String>> renames,Map<String, String> originalVarNames) {
        String carryVarName = String.format(FORMAT_CARRY, gateIdx - 1);
        String xorVarName = String.format(FORMAT_XOR, gateIdx);
        String carryIntermediateVarName = String.format(FORMAT_CARRY_INTERMEDIATE, gateIdx);

        BooleanGate gate = findGate(gates, carryVarName, xorVarName, AND);
        if (gate == null) {
            return false;
        }

        return processRenamePhase2(renames, gate.getResultVarName(), carryIntermediateVarName, gates, originalVarNames);
    }

    private boolean processCarryGate(int gateIdx, List<BooleanGate> gates, List<Pair<String, String>> renames,
                                     Map<String, String> originalVarNames) {
        String carryIntermediateVarName = String.format(FORMAT_CARRY_INTERMEDIATE, gateIdx);
        String andVarName = String.format(FORMAT_AND, gateIdx);
        String newCarryVarName = String.format(FORMAT_CARRY, gateIdx);

        BooleanGate gate = findGate(gates, carryIntermediateVarName, andVarName, OR);
        if (gate == null) {
            return false;
        }

        return processRenamePhase2(renames, gate.getResultVarName(), newCarryVarName, gates, originalVarNames);
    }

    private boolean processRenamePhase2(List<Pair<String, String>> renames, String from, String to, List<BooleanGate> gates,
                                        Map<String, String> originalVarNames) {
        renames.clear();
        renames.add(new Pair<>(from, to));
        originalVarNames.put(to, from);

        boolean result = renameVariables(gates, renames);

        if (DEBUG && !renames.isEmpty()) {
            System.out.printf("Renamed %s to %s%n", renames.get(0).getFirst(), renames.get(0).getSecond());
        }

        return result;
    }

    private Pair<String, String> validateAndSwapPhase4(List<BooleanGate> gates, Map<String, String> originalVarNames) {
        /*
           The gates should come in groups of 5 (excluding the first 00 gate group which is a group of only 2).
           So starting from gates.get(2), each group of 5 gates should have the following pattern (albeit not
            necessarily in that order):
                1. y01 XOR x01 -> _XOR01
                2. y01 AND x01 -> _AND01
                3. _XOR01 XOR _CARRY00 -> z01
                4. _XOR01 AND _CARRY00 -> _CARRY_INTERMEDIATE01
                5. _AND01 OR _CARRY_INTERMEDIATE01 -> _CARRY01

           Do the following for each of the last 3 gates in each group:
                3. _XOR01 XOR _CARRY00 -> z01: validate the gate output variable name (z01 above) matches the proper
                    pattern of "z(NN)" (where (NN) is the group number we're iterating through). If it doesn't match,
                    swap the output variable name with the proper one (i.e. z01 above) in all gates and return.
                4. _XOR01 AND _CARRY00 -> _CARRY_INTERMEDIATE01: validate the first variable name (_XOR01 above)
                    matches the proper pattern of "_XOR(NN)" (where "(NN)" is the group number we're currently iterating
                    over). If it doesn't match, take it (the first variable name) and look it up in originalVarNames
                    (originalVarNames.get("<incorrect-variable-name>")), let's call that "incorrectOriginalName".
                    Then take the correct/proper variable name ("_XOR(NN)") and look that up as well in the
                    originalVarNames map, let's call that "correctOriginalName". Then swap those 2 original names
                    for the output variables of all gates and return.
                5. _AND01 OR _CARRY_INTERMEDIATE01 -> _CARRY01: same as previous step (4.) above except this time
                    the proper pattern for variable 1 is "_AND(NN)" (e.g. "_AND01" in the example above). Also,
                    the "_CARRY_INTERMEDIATE01" variable can appear either the 1st or 2nd variable. If it doesn't match,
                    the treatment is the same as above (lookup the 2 original var names and swap their output variables
                    across all gates).
         */

        if (gates.size() % 5 != 2) {
            throw new IllegalStateException("Gates size is not a multiple of 5 (excluding the first gate group of 2)");
        }

        for (int group = 1; group <= gates.size() / 5; group++) {
            GateGroup gateGroup = findGatesInGroup(gates, group);
            Pair<String, String> swapPair;

            swapPair = checkGate3Pattern(gateGroup.gate3, group);
            if (swapPair != null) return swapPair;

            swapPair = checkGate4Pattern(gateGroup.gate4, group, originalVarNames);
            if (swapPair != null) return swapPair;

            swapPair = checkGate5Pattern(gateGroup.gate5, group, originalVarNames);
            if (swapPair != null) return swapPair;
        }

        return null;
    }

    private GateGroup findGatesInGroup(List<BooleanGate> gates, int group) {
        BooleanGate gate3 = null, gate4 = null, gate5 = null;
        String carryPrev = FORMAT_CARRY.formatted(group - 1);

        for (int i = ((group - 1) * 5) + 2; i < (group * 5) + 2; i++) {
            BooleanGate gate = gates.get(i);

            if (isGate3(gate, carryPrev)) {
                gate3 = gate;
            } else if (isGate4(gate, carryPrev)) {
                gate4 = gate;
            } else if (isGate5(gate)) {
                gate5 = gate;
            }
        }

        if (gate3 == null || gate4 == null || gate5 == null) {
            throw new IllegalStateException("Couldn't find gate 3, 4, or 5 for group " + group);
        }

        return new GateGroup(gate3, gate4, gate5);
    }

    private boolean isGate3(BooleanGate gate, String carryPrev) {
        //identify gate #3 by looking for _CARRY(N-1) in the 2nd variable name and "XOR" for the operation
        return gate.getOperandVariable2().equals(carryPrev) && gate.getOperator().equals(XOR);
    }

    private boolean isGate4(BooleanGate gate, String carryPrev) {
        //identify gate #4 by looking for _CARRY(N-1) in the 2nd variable name and "AND" for the operation
        return gate.getOperandVariable2().equals(carryPrev) && gate.getOperator().equals(AND);
    }

    private boolean isGate5(BooleanGate gate) {
        //identify gate #5 by looking for "OR" for the operation
        return gate.getOperator().equals(OR);
    }

    private Pair<String, String> checkGate3Pattern(BooleanGate gate, int group) {
        //3. _XOR01 XOR _CARRY00 -> z01: validate the output variable name matches pattern of "z(NN)" etc.
        String actualVarName = gate.getResultVarName();
        String expectedName = FORMAT_Z.formatted(group);
        return !actualVarName.equals(expectedName) ? new Pair<>(actualVarName, expectedName) : null;
    }

    private Pair<String, String> checkGate4Pattern(BooleanGate gate, int group, Map<String, String> originalVarNames) {
        //4. _XOR01 AND _CARRY00 -> _CARRY_INTERMEDIATE01: validate the first variable name matches "_XOR(NN)" etc.
        String actualVarName = gate.getOperandVariable1();
        String expectedName = FORMAT_XOR.formatted(group);
        return checkPatternAndCreateSwapPair(actualVarName, expectedName, originalVarNames);
    }

    private Pair<String, String> checkGate5Pattern(BooleanGate gate, int group, Map<String, String> originalVarNames) {
        //5. _AND01 OR _CARRY_INTERMEDIATE01 -> _CARRY01: same as previous step but with patter "_AND(NN)"...
        //    also, "_CARRY_INTERMEDIATE01" can appear either first or 2nd variable etc.
        String actualVarName = gate.getOperandVariable1().startsWith("_CARRY_INTERMEDIATE")
                ? gate.getOperandVariable2()
                : gate.getOperandVariable1();
        String expectedName = FORMAT_AND.formatted(group);
        return checkPatternAndCreateSwapPair(actualVarName, expectedName, originalVarNames);
    }

    private Pair<String, String> checkPatternAndCreateSwapPair(String actualVarName, String expectedName,
                                                               Map<String, String> originalVarNames) {
        if (!actualVarName.equals(expectedName)) {
            String incorrectOriginalName = originalVarNames.get(actualVarName);
            String correctOriginalName = originalVarNames.get(expectedName);
            return new Pair<>(incorrectOriginalName, correctOriginalName);
        }
        return null;
    }

    private void swapOutputVarNamesAcrossAllGatesPhase4(List<BooleanGate> gates, String var1, String var2) {
        gates.forEach(gate -> {
            String resultVar = gate.getResultVarName();
            if (resultVar.equals(var1)) {
                gate.setResultVarName(var2);
            } else if (resultVar.equals(var2)) {
                gate.setResultVarName(var1);
            }
        });
    }

    private BooleanGate findGate(List<BooleanGate> gates, String varA, String varB, BooleanGate.Operator operator) {
        for (BooleanGate gate : gates) {
            if (!gate.getOperator().equals(operator)) {
                continue;
            }

            String operand1 = gate.getOperandVariable1();
            String operand2 = gate.getOperandVariable2();

            if ((operand1.equals(varA) && operand2.equals(varB)) ||
                    (operand1.equals(varB) && operand2.equals(varA))) {
                return gate;
            }
        }

        return null;
    }

    private boolean renameVariables(List<BooleanGate> gates, Collection<Pair<String, String>> renames) {
        boolean foundAny = false;

        for (BooleanGate gate: gates) {
            for (Pair<String, String> rename: renames) {
                String from = rename.getFirst(), to = rename.getSecond();
                if (gate.getOperandVariable1().equals(from)) {
                    gate.setOperandVariable1(to);
                    foundAny = true;
                } else if (gate.getOperandVariable2().equals(from)) {
                    gate.setOperandVariable2(to);
                    foundAny = true;
                } else if (gate.getResultVarName().equals(from)) {
                    gate.setResultVarName(to);
                    foundAny = true;
                }
            }
        }

        return foundAny;
    }

    private void printGates(List<BooleanGate> gates) {
        System.out.println("==== Gates ====");
        List<BooleanGate> list = new ArrayList<>(gates);

        sortGatesPhase3(list);

        for (BooleanGate gate: list) {
            System.out.println(gate);
        }
    }

    private static void sortGatesPhase3(List<BooleanGate> list) {
        for (BooleanGate gate : list) {
            String operand1 = gate.getOperandVariable1();
            String operand2 = gate.getOperandVariable2();
            String numA = operand1.substring(Math.max(0, operand1.length() - 2));
            String numB = operand2.substring(Math.max(0, operand2.length() - 2));

            boolean swapRequired =
                    isNumeric(numB) &&
                    (!isNumeric(numA) || (Integer.parseInt(numA) < Integer.parseInt(numB)));

            if (swapRequired) {
                boolean value1 = gate.isOperandValue1();
                boolean value2 = gate.isOperandValue2();
                gate.setOperandVariable1(operand2);
                gate.setOperandVariable2(operand1);
                gate.resolveOperandValue1(value2);
                gate.resolveOperandValue2(value1);
            }
        }

        Comparator<BooleanGate> comparator = Comparator.comparing(
            gate -> gate.getOperandVariable1().substring(
                    Math.max(0, gate.getOperandVariable1().length() - 2)
            )
        );
        list.sort(comparator);
    }
}
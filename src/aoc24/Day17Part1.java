package aoc24;

import utils.Helper;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://adventofcode.com/2024/day/17">Advent of Code 2024 Day 17</a>
 */
public class Day17Part1 {
    private static class Program {
        long registerA;
        long registerB;
        long registerC;
        int instructionPtr = 0;
        int[] instructions;
        List<Long> output = new ArrayList<>();
    }

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day17_input.txt");

        String answer = new Day17Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        String expected = "5,1,4,0,5,1,0,2,6";
        if (!answer.equals(expected)) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private String execute(List<String> lines) {
        Program pgm = initializeProgram(lines);

        while (pgm.instructionPtr < pgm.instructions.length) {
            int opcode = pgm.instructions[pgm.instructionPtr];
            int operand = pgm.instructions[pgm.instructionPtr + 1];

            switch (opcode) {
                case 0:
                    //The adv instruction (opcode 0) performs division. The numerator is the value in the A register.
                    pgm.registerA = division(pgm.registerA, operand, pgm);
                    break;
                case 1:
                    //The bxl instruction (opcode 1) calculates the bitwise XOR of register B and the instruction's
                    // literal operand, then stores the result in register B.
                    pgm.registerB ^= operand;
                    break;
                case 2:
                    //The bst instruction (opcode 2) calculates the value of its combo operand modulo 8 (thereby
                    // keeping only its lowest 3 bits), then writes that value to the B register.
                    pgm.registerB = getComboOperandValue(operand, pgm) % 8;
                    break;
                case 3:
                    //The jnz instruction (opcode 3) does nothing if the A register is 0. However, if the A register is
                    // not zero, it jumps by setting the instruction pointer to the value of its literal operand.
                    if (pgm.registerA == 0) {
                        pgm.instructionPtr += 2;
                    } else {
                        pgm.instructionPtr = operand;
                    }
                    break;
                case 4:
                    //The bxc instruction (opcode 4) calculates the bitwise XOR of register B and register C, then
                    // stores the result in register B.
                    pgm.registerB ^= pgm.registerC;
                    break;
                case 5:
                    //The out instruction (opcode 5) calculates the value of its combo operand modulo 8, then outputs that value.
                    pgm.output.add(getComboOperandValue(operand, pgm) % 8);
                    break;
                case 6:
                    //The bdv instruction (opcode 6) works exactly like the adv instruction except that the result is
                    // stored in the B register. (The numerator is still read from the A register.)
                    pgm.registerB = division(pgm.registerA, operand, pgm);
                    break;
                case 7:
                    //The cdv instruction (opcode 7) works exactly like the adv instruction except that the result is
                    // stored in the C register. (The numerator is still read from the A register.)
                    pgm.registerC = division(pgm.registerA, operand, pgm);
                    break;
                default:
                    throw new RuntimeException("Unknown opcode " + opcode);
            }

            if (opcode != 3) {
                pgm.instructionPtr += 2;
            }
        }

        //return the output numbers as a comma delimited string
        return pgm.output.stream().map(Object::toString).reduce((a, b) -> a + "," + b).orElse("");
    }

    private long division(long numerator, int operand, Program pgm) {
        /*
          The denominator is found by raising 2 to the power of the instruction's operand.
          (So, an operand of 2 would divide A by 4 (2^2); an operand of 5 would divide A by 2^B.)
          The result of the division operation is truncated to an integer and returned
         */
        long operandValue = getComboOperandValue(operand, pgm);
        long denominator = (long) Math.pow(2, operandValue);
        return numerator / denominator;
    }

    private long getComboOperandValue(int operand, Program pgm) {
        return switch (operand) {
            case 0, 1, 2, 3 -> operand;
            case 4 -> pgm.registerA;
            case 5 -> pgm.registerB;
            case 6 -> pgm.registerC;
            default -> throw new RuntimeException("Unknown operand " + operand);
        };
    }

    private Program initializeProgram(List<String> lines) {
        Program pgm = new Program();
        for (String line : lines) {
            List<Integer> nums = Helper.extractIntsFromText(line);

            if (line.startsWith("Register A")) {
                pgm.registerA = nums.get(0);
            } else if (line.startsWith("Register B")) {
                pgm.registerB = nums.get(0);
            } else if (line.startsWith("Register C")) {
                pgm.registerC = nums.get(0);
            } else if (line.startsWith("Program")) {
                pgm.instructions = nums.stream().mapToInt(Integer::intValue).toArray();
            }
        }

        return pgm;
    }
}
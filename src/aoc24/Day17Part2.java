package aoc24;

import utils.Helper;
import utils.ResourceLoader;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2024/day/17">Advent of Code 2024 Day 17</a>
 */
public class Day17Part2 {
    private long result = 0;

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc24/Day17_input.txt");

        long answer = new Day17Part2().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 202322936867370L;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        String lastLine = lines.get(lines.size() - 1);
        int[] instructionsAndExpectedOutput = Helper.extractIntsFromText(lastLine).stream().mapToInt(Integer::intValue).toArray();
        solve(instructionsAndExpectedOutput.length - 1, 0, instructionsAndExpectedOutput);
        return result;
    }

    private boolean solve(int position, long currentValue, int[] instructionsAndExpectedOutput) {
        if (position < 0) {
            result = currentValue;
            return true;
        }

        //for each 3 bit segment, try every combination of 8 values (0-7) until a match is found for that segment's expected digit
        for (int digit = 0; digit < 8; digit++) {
            long candidate = (currentValue << 3) | digit;
            long registerA = candidate;  //shifts value by 3 bits (i.e. multiplies by 8) and then adds digit [values 0-7]
            long registerB = 0, registerC = 0;
            int instructionPtr = 0;
            int output = 0;

            while (instructionPtr < instructionsAndExpectedOutput.length) {
                int opcode = instructionsAndExpectedOutput[instructionPtr];
                int operandLiteralValue = instructionsAndExpectedOutput[instructionPtr + 1];
                long operandComboValue = getComboOperandValue(operandLiteralValue, registerA, registerB, registerC);
                output = 0;

                switch (opcode) {
                    case 0:
                        //The adv instruction (opcode 0) performs division. The numerator is the value in the A register.
                        // The denominator is found by raising 2 to the power of the instruction's operand.
                        // (So, an operand of 2 would divide A by 4 (2^2); an operand of 5 would divide A by 2^B.)
                        // The result of the division operation is truncated to an integer and returned
                        registerA >>= operandComboValue;
                        break;
                    case 1:
                        //The bxl instruction (opcode 1) calculates the bitwise XOR of register B and the instruction's
                        // literal operand, then stores the result in register B.
                        registerB ^= operandLiteralValue;
                        break;
                    case 2:
                        //The bst instruction (opcode 2) calculates the value of its combo operand modulo 8 (thereby
                        // keeping only its lowest 3 bits), then writes that value to the B register.
                        registerB = operandComboValue % 8;
                        break;
                    case 3:
                        //The jnz instruction (opcode 3) does nothing if the A register is 0. However, if the A register is
                        // not zero, it jumps by setting the instruction pointer to the value of its literal operand.
                        instructionPtr = (registerA != 0) ? operandLiteralValue : instructionPtr + 2;
                        break;
                    case 4:
                        //The bxc instruction (opcode 4) calculates the bitwise XOR of register B and register C, then
                        // stores the result in register B.
                        registerB ^= registerC;
                        break;
                    case 5:
                        //The out instruction (opcode 5) calculates the value of its combo operand modulo 8, then outputs that value.
                        output = (int)(operandComboValue % 8);
                        instructionPtr += instructionsAndExpectedOutput.length; //forces out of the outer while loop
                        break;
                    case 6:
                        //The bdv instruction (opcode 6) works exactly like the adv instruction (opcode 0) except that the result is
                        // stored in the B register. (The numerator is still read from the A register.)
                        registerB = registerA >> operandComboValue;
                        break;
                    case 7:
                        //The cdv instruction (opcode 7) works exactly like the adv instruction (opcode 0) except that the result is
                        // stored in the C register. (The numerator is still read from the A register.)
                        registerC = registerA >> operandComboValue;
                        break;
                    default:
                        throw new RuntimeException("Unknown opcode " + opcode);
                }

                if (opcode != 3) {
                    instructionPtr += 2;
                }
            }

            if (output == instructionsAndExpectedOutput[position] &&
                    solve(position - 1, candidate, instructionsAndExpectedOutput)) { // <- recursively moves on to next
                    //segment (which was shifted left 3 bits (multiplying by 8) + the current segment's matched digit)
                return true;
            }
        }

        return false; //invalid input (no solution possible)
    }

    private static long getComboOperandValue(int operand, long regA, long regB, long regC) {
        return switch (operand) {
            case 0, 1, 2, 3 -> operand;
            case 4 -> regA;
            case 5 -> regB;
            case 6 -> regC;
            default -> throw new RuntimeException("Unknown operand " + operand);
        };
    }
}
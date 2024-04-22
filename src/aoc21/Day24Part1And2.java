package aoc21;

import utils.Helper;
import utils.ResourceLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * <a href="https://adventofcode.com/2021/day/24">Advent of Code 2021 Day 24</a>
 */
public class Day24Part1And2 {

    private long largestModelNumber;
    private long smallestModelNumber;

    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc21/Day24_input.txt");

        Day24Part1And2 exe = new Day24Part1And2();
        exe.execute(lines);

        System.out.printf("Largest model number = %s, smallest model number=%s%n", exe.largestModelNumber, exe.smallestModelNumber);

        long expectedLargest = 95299897999897L;
        long expectedSmallest = 31111121382151L;
        if (exe.largestModelNumber != expectedLargest || exe.smallestModelNumber != expectedSmallest) {
            throw new RuntimeException(
                    String.format("Largest (%s) and/or smallest (%s) answers do not match expected (%s, %s) answers.",
                    exe.largestModelNumber, exe.smallestModelNumber, expectedLargest, expectedSmallest));
        }
    }

    private void execute(List<String> lines) {
        /*
           See write-up of what we're doing here at https://github.com/mrphlip/aoc/blob/master/2021/24.md.

           1. Of the 14 frames, 7 will pop a base-26 number off z (the 7 frames with instruction #5 = "div z 26"), and
              the other 7 will not (those with instruction #5 = "div z 1").
           2. Every frame will conditionally push a base-26 number onto z with a combination of instruction #13 =
              "mul z y" (this creates the new slot on the stack) AND instructions # 16-18 (adds a number between 0-25).
              The condition on whether the frame pushes or not depends on whether y = 1 or 26. The number pushed onto
              the z-stack is composed of the input number (1-9) provided for the frame + a variable number contained in
              instructions #16 of each frame which we'll refer to as the "yIncrement" (i.e. "add y <yIncrement>").
           3. For z to be 0 at the end of the program, we need to ensure that exactly 7 of the 14 frames will push in
              step 2 above. That condition is controlled by instruction #6 "add x <variable number>" in each frame. We'll
              refer to this variable number as the "xIncrement". The condition will always be true if xIncrement is > 9
              (for reasons explained in the link above). It just so happens that all 7 of the frames that don't pop on
              instruction #5 (i.e. "div z 1") have an x value > 9 which means they will always satisfy the condition and
              push. Which in turn means that we have to ensure that the other 7 (with instruction #5 = "div z 26") never
              satisfy the condition.
           4. The key to the condition not being satisfied is in instruction #16 "add y <variable number>". We'll refer
              to this variable number as the "yIncrement". The condition is unsatisfied when this frame's input number
              is equal to the input value of the frame popped off the z-stack + the xIncrement (from instructions #6) of
              this frame + yIncrement (from instructions #6) of the popped off frame.
              I.e. equation "inputThisFrame == inputFromPoppedFrame + xIncrement + yIncrementPopped" must be true for
              the 7 frames with instruction #5 = "div z 26". There can be multiple values that satisfy this equation,
              and we can maximize and minimize the values as long as the equation is true and input number range of 1-9
              in adhered to.
              E.g. if xIncrement = -4 and yIncrementPopped = 8, we can:
                - maximize with "9 (inputThisFrame) == 5 (inputFromPoppedFrame) - 4 (xIncrement) + 8 (yIncrementPopped)"
                - minimize with "5 (inputThisFrame) == 1 (inputFromPoppedFrame) - 4 (xIncrement) + 8 (yIncrementPopped)"
         */

        List<List<String>> frames = getInputFrames(lines); //converts input into 14 frames of 18 similar instructions

        int[] minInputs = new int[14];
        int[] maxInputs = new int[14];

        Stack<String> stack = new Stack<>();

        int frameCnt = 0;
        for (List<String> frame : frames) {
            frameCnt++;
            int yIncrement = Helper.extractIntsFromText(frame.get(15), true).get(0);

            if (frame.get(4).equals("div z 1")) {
                stack.push("frame=" + frameCnt + ",yIncrement=" + yIncrement);
                continue;
            }

            assert frame.get(4).equals("div z 26");

            int xIncrement = Helper.extractIntsFromText(frame.get(5), true).get(0);
            String popped = stack.pop();
            int poppedFrameNum = Helper.extractIntsFromText(popped).get(0);
            int yIncrementPopped = Helper.extractIntsFromText(popped, true).get(1);
            int combinedIncrements = xIncrement + yIncrementPopped;

            if (combinedIncrements >= 0) {
                //this frame gets larger one
                minInputs[frameCnt - 1] = 1 + combinedIncrements;
                minInputs[poppedFrameNum - 1] = 1;
                maxInputs[frameCnt - 1] = 9;
                maxInputs[poppedFrameNum - 1] = 9 - combinedIncrements;
            } else {
                //combinedIncrements is negative, this frame gets smaller one
                minInputs[frameCnt - 1] = 1;
                minInputs[poppedFrameNum - 1] = 1 - combinedIncrements; //note the double negative (since combinedIncrements is negative) here == addition
                maxInputs[frameCnt - 1] = 9 + combinedIncrements; //double negative...
                maxInputs[poppedFrameNum - 1] = 9;
            }
        }

        this.smallestModelNumber = convertToLong(minInputs);
        this.largestModelNumber = convertToLong(maxInputs);
    }

    private long convertToLong(int[] inputs) {
        StringBuilder sb = new StringBuilder();
        for (int i : inputs) {
            sb.append(i);
        }
        return Long.parseLong(sb.toString());
    }

    private List<List<String>> getInputFrames(List<String> lines) {
        List<List<String>> blocks = new ArrayList<>();

        List<String> block = null;
        for (String line : lines) {
            if (line.startsWith("inp")) {
                if (block != null) {
                    blocks.add(block);
                }
                block = new ArrayList<>();
            }
            assert block != null;
            block.add(line);
        }
        blocks.add(block); //last one

        return blocks;
    }
}

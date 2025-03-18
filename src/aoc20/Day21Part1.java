package aoc20;

import org.apache.commons.math3.util.Pair;
import utils.ResourceLoader;

import java.util.*;

/**
 * <a href="https://adventofcode.com/2020/day/21">Advent of Code 2020 Day 21</a>
 */
public class Day21Part1 {
    public static void main(String... args) throws Exception {
        List<String> lines = ResourceLoader.readStrings("aoc20/Day21_input.txt");

        long answer = new Day21Part1().execute(lines);
        System.out.printf("Answer = %s%n", answer);

        long expected = 2779;
        if (answer != expected) {
            throw new RuntimeException(String.format("Answer %s doesn't match expected %s", answer, expected));
        }
    }

    private long execute(List<String> lines) {
        List<Pair<List<String>, List<String>>> input = parseInput(lines);

        Map<String, String> ingredientToAllergenMap = new HashMap<>();
        Map<String, Set<String>> allergenToPossibleIngredientsMap = new HashMap<>();

        for (Pair<List<String>, List<String>> pair : input) {
            Set<String> ingredients = new HashSet<>(pair.getFirst());
            for (String allergen : pair.getSecond()) {
                evaluateAllergenInput(allergen, ingredients, ingredientToAllergenMap, allergenToPossibleIngredientsMap);
            }
        }

        Set<String> badIngredients = new HashSet<>(ingredientToAllergenMap.keySet());

        return countCleanIngredientsOccurrences(input, badIngredients);
    }

    private List<Pair<List<String>, List<String>>> parseInput(List<String> lines) {
        return lines.stream()
                .map(line -> {
                    String[] parts = line.split(" \\(contains ");
                    String[] ingredients = parts[0].split(" ");
                    String[] allergens = parts[1].replace(")", "").split(", ");
                    return new Pair<>(List.of(ingredients), List.of(allergens));
                })
                .toList();
    }

    private void evaluateAllergenInput(String allergen, Set<String> possibleIngredients,
                                       Map<String, String> ingredientToAllergenMap,
                                       Map<String, Set<String>> allergenToPossibleIngredientsMap) {
        //remove ingredients already matched to allergens (are keys in ingredientToAllergenMap)
        possibleIngredients.removeAll(ingredientToAllergenMap.keySet());

        //if allergen already seen, retain only a cross-section of its initial possible-ingredients and the new possible-ingredients
        if (allergenToPossibleIngredientsMap.containsKey(allergen)) {
            allergenToPossibleIngredientsMap.get(allergen).retainAll(possibleIngredients);
        } else { //first time allergen is seen
            allergenToPossibleIngredientsMap.put(allergen,  new HashSet<>(possibleIngredients));
        }

        while (true) {
            /*
              for each allergen -> list-of-possible-ingredients
                 if list-of-possible-ingredients has only 1 ingredient
                     add to ingredientToAllergenMap
                     remove ingredient from all other lists-of-possible-ingredients (values in allergenToPossibleIngredientsMap)
            */
            String singleIngredient = allergenToPossibleIngredientsMap.entrySet().stream()
                    .filter(entry -> entry.getValue().size() == 1)
                    .findFirst()
                    .map(entry -> {
                        String foundIngredient = entry.getValue().iterator().next();
                        ingredientToAllergenMap.put(foundIngredient, entry.getKey());
                        return foundIngredient;
                    })
                    .orElse(null);

            if (singleIngredient == null) {
                break;
            }

            // Remove the found ingredient from all possible ingredients sets
            allergenToPossibleIngredientsMap.values()
                    .forEach(possible -> possible.remove(singleIngredient));
        }
    }

    private long countCleanIngredientsOccurrences(List<Pair<List<String>, List<String>>> input, Set<String> badIngredients) {
        return input.stream()
                .flatMap(pair -> pair.getFirst().stream())
                .filter(ingredient -> !badIngredients.contains(ingredient))
                .count();

    }
}

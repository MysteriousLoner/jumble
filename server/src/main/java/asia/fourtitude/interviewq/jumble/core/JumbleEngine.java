package asia.fourtitude.interviewq.jumble.core;

import asia.fourtitude.interviewq.jumble.core.words.Word;
import asia.fourtitude.interviewq.jumble.core.words.WordsRepository;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Component
public class JumbleEngine {

    private final WordsRepository wordsRepository;

    public JumbleEngine(WordsRepository wordsRepository) {
        this.wordsRepository = wordsRepository;
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    /**
     * Trims and lower-cases the input string for case-insensitive comparisons.
     *
     * @param input the raw input string
     * @return normalised string
     */
    private String normalise(String input) {
        return input.trim().toLowerCase();
    }

    /**
     * From the input `word`, produces/generates a copy which has the same
     * letters, but in different ordering.
     *
     * Example: from "elephant" to "lehnaetp".
     *
     * Evaluation/Grading:
     * a) pass unit test: JumbleEngineTest#scramble()
     * b) scrambled letters/output must not be the same as input
     *
     * @param word  The input word to scramble the letters.
     * @return  The scrambled output/letters.
     */
    public String scramble(String word) {
        if (word == null || word.length() <= 1) {
            return word;
        }
        String normalised = normalise(word);
        boolean found = wordsRepository.getWordsAsList().stream()
                .anyMatch(w -> w.getValue().equals(normalised));
        if (!found) {
            throw new IllegalArgumentException("Word not found in dictionary: " + word);
        }
        List<Character> chars = new ArrayList<>();
        for (char c : normalised.toCharArray()) {
            chars.add(c);
        }
        String shuffled;
        do {
            Collections.shuffle(chars);
            StringBuilder sb = new StringBuilder(chars.size());
            for (char c : chars) {
                sb.append(c);
            }
            shuffled = sb.toString();
        } while (shuffled.equals(normalised));
        return shuffled;
    }

    /**
     * Retrieves the palindrome words from the internal
     * word list/dictionary ("src/main/resources/words.txt").
     *
     * Word of single letter is not considered as valid palindrome word.
     *
     * Examples: "eye", "deed", "level".
     *
     * Evaluation/Grading:
     * a) able to access/use resource from classpath
     * b) using inbuilt Collections
     * c) using "try-with-resources" functionality/statement
     * d) pass unit test: JumbleEngineTest#palindrome()
     *
     * @return  The list of palindrome words found in system/engine.
     * @see "https://www.google.com/search?q=palindrome+meaning"
     */
    public Collection<String> retrievePalindromeWords() {
        return wordsRepository.getWordsAsList().stream()
                .filter(Word::isPalindrome)
                .map(Word::getValue)
                .collect(Collectors.toList());
    }

    /**
     * Picks one word randomly from internal word list.
     *
     * Evaluation/Grading:
     * a) pass unit test: JumbleEngineTest#randomWord()
     * b) provide a good enough implementation, if not able to provide a fast lookup
     * c) bonus points, if able to implement a fast lookup/scheme
     *
     * @param length  The word picked, must of length.
     *                When length is null, then return random word of any length.
     * @return  One of the word (randomly) from word list.
     *          Or null if none matching.
     */
    public String pickOneRandomWord(Integer length) {
        List<Word> candidates;
        if (length == null) {
            candidates = wordsRepository.getWordsAsList();
        } else {
            candidates = wordsRepository.getWordsAsList().stream()
                    .filter(w -> w.getLength() == length)
                    .collect(Collectors.toList());
        }
        if (candidates.isEmpty()) {
            return null;
        }
        return candidates.get(ThreadLocalRandom.current().nextInt(candidates.size())).getValue();
    }

    /**
     * Checks if the `word` exists in internal word list.
     * Matching is case insensitive.
     *
     * Evaluation/Grading:
     * a) pass related unit tests in "JumbleEngineTest"
     * b) provide a good enough implementation, if not able to provide a fast lookup
     * c) bonus points, if able to implement a fast lookup/scheme
     *
     * @param word  The input word to check.
     * @return  true if `word` exists in internal word list.
     */
    public boolean exists(String word) {
        if (word == null || word.trim().isEmpty()) {
            return false;
        }
        String normalised = normalise(word);
        for (char c : normalised.toCharArray()) {
            if (!Character.isLetter(c)) {
                return false;
            }
        }
        return wordsRepository.getWordsAsList().stream()
                .anyMatch(w -> w.getValue().equals(normalised));
    }

    /**
     * Finds all the words from internal word list which begins with the
     * input `prefix`.
     * Matching is case insensitive.
     *
     * Invalid `prefix` (null, empty string, blank string, non letter) will
     * return empty list.
     *
     * Evaluation/Grading:
     * a) pass related unit tests in "JumbleEngineTest"
     * b) provide a good enough implementation, if not able to provide a fast lookup
     * c) bonus points, if able to implement a fast lookup/scheme
     *
     * @param prefix  The prefix to match.
     * @return  The list of words matching the prefix.
     */
    public Collection<String> wordsMatchingPrefix(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String normalised = normalise(prefix);
        for (char c : normalised.toCharArray()) {
            if (!Character.isLetter(c)) {
                return Collections.emptyList();
            }
        }
        return wordsRepository.getWordsAsList().stream()
                .filter(w -> w.getValue().startsWith(normalised))
                .map(Word::getValue)
                .collect(Collectors.toList());
    }

    /**
     * Finds all the words from internal word list that is matching
     * the searching criteria.
     *
     * `startChar` and `endChar` must be 'a' to 'z' only. And case insensitive.
     * `length`, if have value, must be positive integer (>= 1).
     *
     * Words are filtered using `startChar` and `endChar` first.
     * Then apply `length` on the result, to produce the final output.
     *
     * Must have at least one valid value out of 3 inputs
     * (`startChar`, `endChar`, `length`) to proceed with searching.
     * Otherwise, return empty list.
     *
     * Evaluation/Grading:
     * a) pass related unit tests in "JumbleEngineTest"
     * b) provide a good enough implementation, if not able to provide a fast lookup
     * c) bonus points, if able to implement a fast lookup/scheme
     *
     * @param startChar  The first character of the word to search for.
     * @param endChar    The last character of the word to match with.
     * @param length     The length of the word to match.
     * @return  The list of words matching the searching criteria.
     */
    public Collection<String> searchWords(Character startChar, Character endChar, Integer length) {
        boolean validStart  = startChar != null && Character.isLetter(startChar);
        boolean validEnd    = endChar   != null && Character.isLetter(endChar);
        boolean validLength = length    != null && length >= 1;

        // At least one input must be valid
        if (!validStart && !validEnd && !validLength) {
            return Collections.emptyList();
        }

        Character normStart = validStart ? Character.toLowerCase(startChar) : null;
        Character normEnd   = validEnd   ? Character.toLowerCase(endChar)   : null;

        return wordsRepository.getWordsAsList().stream()
                .filter(w -> !validStart  || w.getPrefix().equals(normStart))
                .filter(w -> !validEnd    || w.getPostfix().equals(normEnd))
                .filter(w -> !validLength || w.getLength() == length)
                .map(Word::getValue)
                .collect(Collectors.toList());
    }

    /**
     * Generates all possible combinations of smaller/sub words using the
     * letters from input word.
     *
     * The `minLength` set the minimum length of sub word that is considered
     * as acceptable word.
     *
     * If length of input `word` is less than `minLength`, then return empty list.
     *
     * The sub words must exist in internal word list.
     *
     * Example: From "yellow" and `minLength` = 3, the output sub words:
     *     low, lowly, lye, ole, owe, owl, well, welly, woe, yell, yeow, yew, yowl
     *
     * Evaluation/Grading:
     * a) pass related unit tests in "JumbleEngineTest"
     * b) provide a good enough implementation, if not able to provide a fast lookup
     * c) bonus points, if able to implement a fast lookup/scheme
     *
     * @param word       The input word to use as base/seed.
     * @param minLength  The minimum length (inclusive) of sub words.
     *                   When zero, return empty list.
     *                   Default is 3.
     * @return  The list of sub words constructed from input `word`.
     */
    public Collection<String> generateSubWords(String word, Integer minLength) {
        // Guard: invalid input
        if (word == null || word.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String normalised = normalise(word);
        for (char c : normalised.toCharArray()) {
            if (!Character.isLetter(c)) {
                return Collections.emptyList();
            }
        }

        // Resolve effective minLength
        int effectiveMin = (minLength == null) ? 3 : minLength;

        // minLength=0 or minLength >= seed length → nothing can qualify
        if (effectiveMin <= 0 || effectiveMin >= normalised.length()) {
            return Collections.emptyList();
        }

        // Build letter-frequency map for the seed word
        Map<Character, Integer> seedFreq = letterFrequency(normalised);

        // A candidate sub word qualifies when:
        //   1. Its length is between effectiveMin and seed.length - 1 (strictly shorter)
        //   2. Every letter it needs is available in the seed's frequency map
        return wordsRepository.getWordsAsList().stream()
                .filter(w -> w.getLength() >= effectiveMin && w.getLength() < normalised.length())
                .map(Word::getValue)
                .filter(value -> isSubset(letterFrequency(value), seedFreq))
                .collect(Collectors.toList());
    }

    /**
     * Builds a character frequency map for the given string.
     *
     * @param s the input string (already normalised to lower-case)
     * @return map of character → occurrence count
     */
    private Map<Character, Integer> letterFrequency(String s) {
        Map<Character, Integer> freq = new HashMap<>();
        for (char c : s.toCharArray()) {
            freq.merge(c, 1, Integer::sum);
        }
        return freq;
    }

    /**
     * Returns {@code true} when every entry in {@code candidate} is satisfied
     * by the available counts in {@code available}.
     *
     * @param candidate frequency map of the word being tested
     * @param available frequency map of the seed word's letters
     * @return {@code true} if the candidate can be formed from the available letters
     */
    private boolean isSubset(Map<Character, Integer> candidate, Map<Character, Integer> available) {
        for (Map.Entry<Character, Integer> entry : candidate.entrySet()) {
            if (available.getOrDefault(entry.getKey(), 0) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a game state with word to guess, scrambled letters, and
     * possible combinations of words.
     *
     * Word is of length 6 characters.
     * The minimum length of sub words is of length 3 characters.
     *
     * @param length     The length of selected word.
     *                   Expects >= 3.
     * @param minLength  The minimum length (inclusive) of sub words.
     *                   Expects positive integer.
     *                   Default is 3.
     * @return  The game state.
     */
    public GameState createGameState(Integer length, Integer minLength) {
        Objects.requireNonNull(length, "length must not be null");
        if (minLength == null) {
            minLength = 3;
        } else if (minLength <= 0) {
            throw new IllegalArgumentException("Invalid minLength=[" + minLength + "], expect positive integer");
        }
        if (length < 3) {
            throw new IllegalArgumentException("Invalid length=[" + length + "], expect greater than or equals 3");
        }
        if (minLength > length) {
            throw new IllegalArgumentException("Expect minLength=[" + minLength + "] greater than length=[" + length + "]");
        }
        String original = this.pickOneRandomWord(length);
        if (original == null) {
            throw new IllegalArgumentException("Cannot find valid word to create game state");
        }
        String scramble = this.scramble(original);
        Map<String, Boolean> subWords = new TreeMap<>();
        for (String subWord : this.generateSubWords(original, minLength)) {
            subWords.put(subWord, Boolean.FALSE);
        }
        return new GameState(original, scramble, subWords);
    }

}

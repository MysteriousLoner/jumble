package asia.fourtitude.interviewq.jumble.core;

import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * In-memory hash-table backed repository for the word list loaded from
 * {@code words.txt} on the classpath.
 *
 * <ul>
 *   <li>{@link #contains(String)} – O(1) average case via {@link HashSet}.</li>
 *   <li>{@link #wordsByLength(int)} – O(1) average case via {@link HashMap}
 *       keyed by word length.</li>
 * </ul>
 *
 * The repository is initialised during application startup (before any bean
 */
@Repository
public class WordsRepository {

    /** Master set – used for O(1) existence checks. */
    private final Set<String> wordSet = new HashSet<>();

    /** Words grouped by their length – used for O(1) random-pick-by-length. */
    private final Map<Integer, List<String>> wordsByLength = new HashMap<>();

    // -----------------------------------------------------------------------
    // Initialisation
    // -----------------------------------------------------------------------

    /**
     * Loads every word from {@code words.txt} (classpath) into the in-memory
     * hash tables on construction.
     */
    public WordsRepository() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("words.txt");
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(is, "words.txt not found on classpath"),
                             StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String word = line.trim().toLowerCase();
                if (!word.isEmpty()) {
                    insert(word);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load words.txt", e);
        }
    }

    // -----------------------------------------------------------------------
    // Mutation
    // -----------------------------------------------------------------------

    /**
     * Inserts {@code word} into the repository.
     * The word is stored in lower-case.
     *
     * @param word the word to insert (must not be null or blank)
     */
    public void insert(String word) {
        if (word == null || word.isEmpty()) {
            return;
        }
        String normalised = word.trim().toLowerCase();
        if (wordSet.add(normalised)) {
            wordsByLength
                    .computeIfAbsent(normalised.length(), k -> new ArrayList<>())
                    .add(normalised);
        }
    }

    // -----------------------------------------------------------------------
    // Lookup
    // -----------------------------------------------------------------------

    /**
     * Returns {@code true} if {@code word} exists in the repository.
     * Lookup is case-insensitive and O(1) average case.
     *
     * @param word the word to test
     * @return {@code true} when the word is present
     */
    public boolean contains(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }
        return wordSet.contains(word.trim().toLowerCase());
    }

    /**
     * Returns an unmodifiable list of all words that have exactly
     * {@code length} characters. Returns an empty list when none exist.
     *
     * @param length the desired word length
     * @return unmodifiable list of matching words
     */
    public List<String> wordsByLength(int length) {
        return Collections.unmodifiableList(
                wordsByLength.getOrDefault(length, Collections.emptyList()));
    }
}

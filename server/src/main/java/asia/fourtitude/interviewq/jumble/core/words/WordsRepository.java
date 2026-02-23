package asia.fourtitude.interviewq.jumble.core.words;

import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Classpath-loader responsible for constructing the full collection of
 * {@link Word} objects from {@code words.txt}.
 *
 * <p>Each {@link Word} is constructed directly via its all-args constructor,
 * with all derived fields ({@code length}, {@code prefix}, {@code postfix},
 * {@code palindrome}) computed here before construction.
 *
 * <p>Business-logic (filtering, random selection, etc.) lives in
 * {@link asia.fourtitude.interviewq.jumble.core.JumbleEngine}.
 */
@Repository
public class WordsRepository {

    private final List<Word> words;

    /**
     * Loads every line from {@code words.txt} (classpath) into a {@link Word} list.
     * Uses try-with-resources to ensure the stream is always closed.
     */
    public WordsRepository() {
        List<Word> loaded = new ArrayList<>();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("words.txt");
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(
                             Objects.requireNonNull(is, "words.txt not found on classpath"),
                             StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    String v = trimmed.toLowerCase();
                    int length      = v.length();
                    Character prefix  = v.charAt(0);
                    Character postfix = v.charAt(length - 1);
                    boolean palindrome = length >= 2
                            && new StringBuilder(v).reverse().toString().equals(v);
                    loaded.add(new Word(v, length, prefix, postfix, palindrome));
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load words.txt", e);
        }
        this.words = Collections.unmodifiableList(loaded);
    }

    /**
     * Returns every word loaded from the dictionary.
     *
     * @return unmodifiable list of {@link Word} objects
     */
    public List<Word> getWordsAsList() {
        return words;
    }
}

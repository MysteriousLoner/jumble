package asia.fourtitude.interviewq.jumble.core.words;

import lombok.Getter;
import lombok.ToString;

/**
 * Immutable value object representing a single dictionary entry.
 * All fields are computed and set by {@link WordsRepository} at load time.
 */
@Getter
@ToString
public class Word {

    private final String value;
    private final int length;
    private final Character prefix;
    private final Character postfix;
    private final boolean palindrome;

    public Word(String value, int length, Character prefix, Character postfix, boolean palindrome) {
        this.value = value;
        this.length = length;
        this.prefix = prefix;
        this.postfix = postfix;
        this.palindrome = palindrome;
    }
}

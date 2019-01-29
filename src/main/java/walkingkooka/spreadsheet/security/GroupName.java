package walkingkooka.spreadsheet.security;

import walkingkooka.Cast;
import walkingkooka.naming.Name;
import walkingkooka.predicate.Predicates;
import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * The name of a group.
 */
final public class GroupName implements Name, Comparable<GroupName> {

    private final static CharPredicate LETTER = CharPredicates.range('A', 'Z').or(CharPredicates.range('a', 'z'));

    final static CharPredicate INITIAL = LETTER;

    private final static CharPredicate DIGIT = CharPredicates.range('0', '9');

    final static CharPredicate PART = INITIAL.or(DIGIT.or(CharPredicates.is('-')));

    final static Predicate<CharSequence> PREDICATE = Predicates.initialAndPart(INITIAL, PART);

    final static int MAX_LENGTH = 255;

    /**
     * Factory that creates a {@link GroupName}
     */
    public static GroupName with(final String name) {
        Objects.requireNonNull(name, "name");

        if (!isAcceptableLength(name)) {
            throw new IllegalArgumentException("Name length " + name.length() + " is greater than allowed " + MAX_LENGTH);
        }

        if (!PREDICATE.test(name)) {
            throw new IllegalArgumentException("Name contains invalid character(s)=" + CharSequences.quote(name));
        }

        return new GroupName(name);
    }

    static boolean isAcceptableLength(final String name) {
        return name.length() < MAX_LENGTH;
    }

    /**
     * Private constructor
     */
    private GroupName(final String name) {
        super();
        this.name = name;
    }

    @Override
    public String value() {
        return this.name;
    }

    final String name;

    // Object..................................................................................................

    public final int hashCode() {
        return CASE_SENSITITY.hash(this.name);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof GroupName &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final GroupName other) {
        return this.compareTo(other) == 0;
    }

    @Override
    public String toString() {
        return this.name;
    }

    // Comparable ...................................................................................................

    @Override
    public int compareTo(final GroupName other) {
        return CASE_SENSITITY.comparator().compare(this.name, other.name);
    }

    private final CaseSensitivity CASE_SENSITITY = CaseSensitivity.SENSITIVE;
}

package walkingkooka.spreadsheet;

import walkingkooka.Cast;
import walkingkooka.Value;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.text.CharSequences;
import walkingkooka.text.Whitespace;

/**
 * Text that accompanies some artefact, that is intended to be read by users.
 */
public final class SpreadsheetDescription implements HashCodeEqualsDefined, Value<String> {

    public static SpreadsheetDescription with(final String message) {
        Whitespace.failIfNullOrEmptyOrWhitespace(message, "message");

        return new SpreadsheetDescription(message);
    }

    private SpreadsheetDescription(final String message) {
        this.message = message;
    }

    @Override
    public String value() {
        return this.message;
    }

    private final String message;

    @Override
    public int hashCode() {
        return this.message.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
               other instanceof SpreadsheetDescription &&
               this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetDescription error) {
        return this.message.equals(error.message);
    }

    @Override
    public String toString() {
        return CharSequences.quote(this.message).toString();
    }
}

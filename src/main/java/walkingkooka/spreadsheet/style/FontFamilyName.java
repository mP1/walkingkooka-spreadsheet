package walkingkooka.spreadsheet.style;

import walkingkooka.Cast;
import walkingkooka.naming.Name;
import walkingkooka.test.HashCodeEqualsDefined;

import java.util.Objects;

/**
 * A font family name.
 */
public final class FontFamilyName implements Name, Comparable<FontFamilyName>, HashCodeEqualsDefined {

    public static FontFamilyName with(final String name) {
        Objects.requireNonNull(name, "name");
        return new FontFamilyName(name);
    }

    private FontFamilyName(final String name) {
        this.name = name;
    }

    @Override
    public String value() {
        return this.name;
    }

    private final String name;

    // Object..................................................................................................

    public final int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
               other instanceof FontFamilyName &&
               this.equals0(Cast.to(other));
    }

    private boolean equals0(final FontFamilyName other) {
        return this.name.equals(other.name);
    }

    @Override
    public String toString() {
        return this.name;
    }

    // Comparable ...................................................................................................

    @Override
    public int compareTo(final FontFamilyName other) {
        return this.name.compareTo(other.name);
    }
}

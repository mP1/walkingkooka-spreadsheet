/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.spreadsheet.format;

import walkingkooka.Cast;
import walkingkooka.naming.Name;
import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.CaseSensitivity;

/**
 * The {@link Name} of a color with a format expression.
 */
final public class SpreadsheetColorName implements Name, Comparable<SpreadsheetColorName> {

    final static CharPredicate LETTER = CharPredicates.range('A', 'Z').or(CharPredicates.range('a', 'z'));

    /**
     * The maximum valid length of a function name.
     */
    public final static int MAX_LENGTH = 255;

    /**
     * Factory that creates a {@link SpreadsheetColorName}
     */
    public static SpreadsheetColorName with(final String name) {
        CharPredicates.failIfNullOrEmptyOrFalse(name,
                SpreadsheetColorName.class.getSimpleName(),
                LETTER);

        final int length = name.length();
        if (length > MAX_LENGTH) {
            throw new IllegalArgumentException("Color name length " + length + " greater than allowed of " + MAX_LENGTH);
        }

        return new SpreadsheetColorName(name);
    }

    /**
     * Private constructor
     */
    private SpreadsheetColorName(final String name) {
        super();
        this.name = name;
    }

    @Override
    public String value() {
        return this.name;
    }

    final String name;

    // Object..................................................................................................

    @Override
    public int hashCode() {
        return CASE_SENSITIVITY.hash(this.name);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetColorName &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetColorName other) {
        return this.compareTo(other) == 0;
    }

    @Override
    public String toString() {
        return this.name;
    }

    // Comparable ...................................................................................................

    @Override
    public int compareTo(final SpreadsheetColorName other) {
        return CASE_SENSITIVITY.comparator().compare(this.name, other.name);
    }

    // HasCaseSensitivity................................................................................................

    @Override
    public CaseSensitivity caseSensitivity() {
        return CASE_SENSITIVITY;
    }

    private final static CaseSensitivity CASE_SENSITIVITY = CaseSensitivity.SENSITIVE;
}

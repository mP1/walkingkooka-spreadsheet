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
import walkingkooka.collect.set.Sets;
import walkingkooka.naming.Name;
import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.spreadsheet.SpreadsheetStrings;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.text.CaseSensitivity;

import java.util.Set;

/**
 * The {@link Name} of a color within a spreadsheet format pattern expression.
 */
final public class SpreadsheetColorName implements Name, Comparable<SpreadsheetColorName> {

    /**
     * Early declaration prevents NPE within DEFAULTS Sets#of.
     */
    private final static CaseSensitivity CASE_SENSITIVITY = SpreadsheetStrings.CASE_SENSITIVITY;

    /**
     * Used to validate the characters within a name.
     */
    private final static CharPredicate LETTER = CharPredicates.range('A', 'Z').or(CharPredicates.range('a', 'z'));

    /**
     * The maximum valid length of an expression name.
     */
    private final static int MAX_LENGTH = 255;

    // https://www.excelsupersite.com/what-are-the-56-colorindex-colors-in-excel/
    //
    // Black, White, Red, Green, Blue, Yellow, Magenta, and Cyan

    public final static SpreadsheetColorName BLACK = new SpreadsheetColorName("Black");

    public final static SpreadsheetColorName WHITE = new SpreadsheetColorName("White");

    public final static SpreadsheetColorName RED = new SpreadsheetColorName("Red");

    public final static SpreadsheetColorName GREEN = new SpreadsheetColorName("Green");

    public final static SpreadsheetColorName BLUE = new SpreadsheetColorName("Blue");

    public final static SpreadsheetColorName YELLOW = new SpreadsheetColorName("Yellow");

    public final static SpreadsheetColorName MAGENTA = new SpreadsheetColorName("Magenta");

    public final static SpreadsheetColorName CYAN = new SpreadsheetColorName("Cyan");

    /**
     * A {@link Set} holding the default {@link SpreadsheetColorName color names}.
     */
    public final static Set<SpreadsheetColorName> DEFAULTS = Sets.of(
        BLACK,
        WHITE,
        RED,
        GREEN,
        BLUE,
        YELLOW,
        MAGENTA,
        CYAN
    );

    /**
     * Factory that creates a {@link SpreadsheetColorName}
     */
    public static SpreadsheetColorName with(final String name) {
        LETTER.failIfNullOrEmptyOrFalse(
            SpreadsheetColorName.class.getSimpleName(),
            name
        );

        final SpreadsheetColorName spreadsheetColorName;

        switch (name) {
            case "Black":
                spreadsheetColorName = BLACK;
                break;
            case "White":
                spreadsheetColorName = WHITE;
                break;
            case "Red":
                spreadsheetColorName = RED;
                break;
            case "Green":
                spreadsheetColorName = GREEN;
                break;
            case "Blue":
                spreadsheetColorName = BLUE;
                break;
            case "Yellow":
                spreadsheetColorName = YELLOW;
                break;
            case "Magenta":
                spreadsheetColorName = MAGENTA;
                break;
            case "Cyan":
                spreadsheetColorName = CYAN;
                break;
            default:
                final int length = name.length();
                if (length > MAX_LENGTH) {
                    throw new IllegalArgumentException("Color name length " + length + " greater than allowed of " + MAX_LENGTH);
                }

                spreadsheetColorName = new SpreadsheetColorName(name);
                break;
        }

        return spreadsheetColorName;
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

    private final String name;

    public SpreadsheetMetadataPropertyName<Integer> spreadsheetMetadataPropertyName() {
        return SpreadsheetMetadataPropertyName.namedColor(this);
    }

    // Object...........................................................................................................

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
}

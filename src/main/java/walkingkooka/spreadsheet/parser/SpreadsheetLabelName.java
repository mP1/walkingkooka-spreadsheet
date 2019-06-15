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

package walkingkooka.spreadsheet.parser;

import walkingkooka.Cast;
import walkingkooka.naming.Name;
import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeException;

import java.util.Objects;

/**
 * A label or {@link Name} is a name to a cell reference, range and so on.
 * <pre>
 * A Defined Name must begin with a letter or an underscore ( _ ) and consist of only letters, numbers, or underscores.
 * Spaces are not permitted in a Defined Name. Moreover, a Defined Name may not be the same as a valid cell reference.
 * For example, the name AB11 is invalid because AB11 is a valid cell reference. Names are not case sensitive.
 * </pre>
 */
final public class SpreadsheetLabelName extends SpreadsheetExpressionReference implements Name,
        Comparable<SpreadsheetLabelName> {

    private final static CharPredicate LETTER = CharPredicates.range('A', 'Z').or(CharPredicates.range('a', 'z'));

    final static CharPredicate INITIAL = LETTER;

    private final static CharPredicate DIGIT = CharPredicates.range('0', '9');

    final static CharPredicate PART = INITIAL.or(DIGIT.or(CharPredicates.is('_')));

    final static int MAX_LENGTH = 255;

    /**
     * Accepts a json string and returns a {@link SpreadsheetLabelName} or fails.
     */
    public static SpreadsheetLabelName fromJsonNode(final JsonNode node) {
        Objects.requireNonNull(node, "node");

        try {
            return with(node.stringValueOrFail());
        } catch (final JsonNodeException cause) {
            throw new IllegalArgumentException(cause.getMessage(), cause);
        }
    }

    static {
        HasJsonNode.register("spreadsheet-label-name", SpreadsheetLabelName::fromJsonNode, SpreadsheetLabelName.class);
    }

    /**
     * Factory that creates a {@link SpreadsheetLabelName}
     */
    public static SpreadsheetLabelName with(final String name) {
        CharPredicates.failIfNullOrEmptyOrInitialAndPartFalse(name, "name", INITIAL, PART);

        if (!isAcceptableLength(name)) {
            throw new IllegalArgumentException("Label length " + name.length() + " is greater than allowed " + MAX_LENGTH);
        }

        if (isCellReference(name)) {
            throw new IllegalArgumentException("Label is a valid cell reference=" + CharSequences.quote(name));
        }

        return new SpreadsheetLabelName(name);
    }

    static boolean isAcceptableLength(final String name) {
        return name.length() < MAX_LENGTH;
    }

    // modes used by isCellReference
    private final static int MODE_COLUMN = 0;
    private final static int MODE_ROW = MODE_COLUMN + 1;
    private final static int MODE_FAIL = MODE_ROW + 1;

    /**
     * Tests if the {@link String name} is a valid cell reference.
     */
    static boolean isCellReference(final String name) {
        int mode = MODE_COLUMN; // -1 too long or contains invalid char
        int column = 0;
        int row = 0;

        // AB11 max row, max column
        final int length = name.length();
        for (int i = 0; i < length; i++) {
            final char c = name.charAt(i);

            // try and parse into column + row
            if (MODE_COLUMN == mode) {
                final int digit = SpreadsheetColumnReferenceParser.valueFromDigit0(c);
                if (-1 != digit) {
                    column = column * SpreadsheetColumnReference.RADIX + digit;
                    if (column >= SpreadsheetColumnReference.MAX) {
                        mode = MODE_FAIL;
                        break; // column is too big cant be a cell reference.
                    }
                    continue;
                }
                mode = MODE_ROW;
            }
            if (MODE_ROW == mode) {
                final int digit = Character.digit(c, SpreadsheetRowReference.RADIX);
                if (-1 != digit) {
                    row = SpreadsheetRowReference.RADIX * row + digit;
                    if (row >= SpreadsheetRowReference.MAX) {
                        mode = MODE_FAIL;
                        break; // row is too big cant be a cell reference.
                    }
                    continue;
                }
                mode = MODE_FAIL;
                break;
            }
        }

        // ran out of characters still checking row must be a valid cell reference.
        return MODE_ROW == mode;
    }

    /**
     * Private constructor
     */
    private SpreadsheetLabelName(final String name) {
        super();
        this.name = name;
    }

    @Override
    public String value() {
        return this.name;
    }

    private final String name;

    @Override
    public int compareTo(final SpreadsheetLabelName other) {
        return CASE_SENSITIVITY.comparator().compare(this.name, other.name);
    }

    // Object

    @Override
    public int hashCode() {
        return CASE_SENSITIVITY.hash(this.name);
    }

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetLabelName;
    }

    @Override
    boolean equals0(final Object other) {
        return this.equals1(Cast.to(other));
    }

    private boolean equals1(final SpreadsheetLabelName other) {
        return this.compare(other) == 0;
    }

    @Override
    public String toString() {
        return this.name;
    }

    // SpreadsheetExpressionReferenceComparator........................................................................

    @Override
    final int compare(final SpreadsheetExpressionReference other) {
        return other.compare0(this);
    }

    @Override
    final int compare0(final SpreadsheetCellReference other) {
        return -LABEL_COMPARED_WITH_CELL_RESULT;
    }

    @Override
    final int compare0(final SpreadsheetLabelName other) {
        return other.compareTo(this);
    }

    // HasCaseSensitivity................................................................................................

    @Override
    public CaseSensitivity caseSensitivity() {
        return CASE_SENSITIVITY;
    }

    private final static CaseSensitivity CASE_SENSITIVITY = CaseSensitivity.INSENSITIVE;
}

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

package walkingkooka.spreadsheet.reference;

import walkingkooka.Cast;
import walkingkooka.InvalidTextLengthException;
import walkingkooka.naming.Name;
import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;

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

    private final static CharPredicate INITIAL = LETTER;

    private final static CharPredicate DIGIT = CharPredicates.range('0', '9');

    private final static CharPredicate PART = INITIAL.or(DIGIT.or(CharPredicates.is('_')));

    /**
     * The maximum valid length for a label name.
     */
    public final static int MAX_LENGTH = 255;

    /**
     * Factory that creates a {@link SpreadsheetLabelName}
     */
    static SpreadsheetLabelName with(final String name) {
        CharPredicates.failIfNullOrEmptyOrInitialAndPartFalse(name, "name", INITIAL, PART);

        if (name.length() >= MAX_LENGTH) {
            throw new InvalidTextLengthException("Label", name, 0, MAX_LENGTH);
        }

        if (isTextCellReference(name)) {
            throw new IllegalArgumentException("Label is a valid cell reference=" + CharSequences.quote(name));
        }

        return new SpreadsheetLabelName(name);
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

    /**
     * Creates a {@link SpreadsheetLabelMapping} using this label and the given {@link SpreadsheetExpressionReference}.
     */
    public SpreadsheetLabelMapping mapping(final SpreadsheetExpressionReference reference) {
        return SpreadsheetLabelMapping.with(this, reference);
    }

    public String hateosLinkId() {
        return this.name;
    }

    // SpreadsheetExpressionReferenceVisitor............................................................................

    @Override
    void accept(final SpreadsheetExpressionReferenceVisitor visitor) {
        visitor.visit(this);
    }

    // Comparable........................................................................................................

    @Override
    public int compareTo(final SpreadsheetLabelName other) {
        return CASE_SENSITIVITY.comparator().compare(this.name, other.name);
    }

    // Object...........................................................................................................

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

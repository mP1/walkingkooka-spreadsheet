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

package walkingkooka.spreadsheet.formula;

import walkingkooka.spreadsheet.SpreadsheetValueType;
import walkingkooka.spreadsheet.formula.parser.ColumnSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.reference.IllegalColumnArgumentException;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.validation.ValueTypeName;

import java.util.Optional;

/**
 * A {@link Parser} that consumes a {@link ColumnSpreadsheetFormulaParserToken}
 */
final class SpreadsheetColumnReferenceSpreadsheetParser extends SpreadsheetColumnOrRowReferenceSpreadsheetParser {

    /**
     * Singleton
     */
    final static SpreadsheetColumnReferenceSpreadsheetParser INSTANCE = new SpreadsheetColumnReferenceSpreadsheetParser(
        true // REQUIRED
    );

    /**
     * Private ctor use singleton
     */
    private SpreadsheetColumnReferenceSpreadsheetParser(final boolean required) {
        super(required);
    }

    @Override
    int valueFromDigit(final char c) {
        return SpreadsheetFormulaParsers.columnLetterValue(c);
    }

    @Override
    int radix() {
        return RADIX;
    }

    final static int RADIX = 26;

    @Override
    ParserToken token1(final SpreadsheetReferenceKind absoluteOrRelative,
                       final int value,
                       final String text) {
        try {
            return ColumnSpreadsheetFormulaParserToken.column(
                absoluteOrRelative.column(value),
                text
            );
        } catch (final IllegalColumnArgumentException cause) {
            // Invalid column ABCDE not between \"A\" and \"$MAX\"
            throw new IllegalColumnArgumentException(
                "Invalid column " + CharSequences.quoteAndEscape(text) + " not between \"A\" and \"" + SpreadsheetColumnReference.MAX_VALUE_STRING + "\""
            );
        }
    }

    @Override
    public Optional<ValueTypeName> valueType() {
        return COLUMN;
    }

    private final static Optional<ValueTypeName> COLUMN = Optional.of(
        SpreadsheetValueType.COLUMN
    );

    @Override
    public SpreadsheetColumnReferenceSpreadsheetParser optional() {
        return this.setRequired(false);
    }

    @Override
    public SpreadsheetColumnReferenceSpreadsheetParser required() {
        return this.setRequired(true);
    }

    private SpreadsheetColumnReferenceSpreadsheetParser setRequired(final boolean required) {
        return required == this.isRequired() ?
            this :
            new SpreadsheetColumnReferenceSpreadsheetParser(required);
    }

    @Override
    public String toString() {
        return SpreadsheetColumnReference.class.getSimpleName();
    }
}

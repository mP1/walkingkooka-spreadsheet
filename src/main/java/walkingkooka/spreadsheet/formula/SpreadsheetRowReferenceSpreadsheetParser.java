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
import walkingkooka.spreadsheet.formula.parser.RowSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.validation.ValueTypeName;

import java.util.Optional;

/**
 * A {@link Parser} that consumes a {@link RowSpreadsheetFormulaParserToken}
 */
final class SpreadsheetRowReferenceSpreadsheetParser extends SpreadsheetColumnOrRowReferenceSpreadsheetParser {

    /**
     * Singleton
     */
    final static SpreadsheetRowReferenceSpreadsheetParser INSTANCE = new SpreadsheetRowReferenceSpreadsheetParser(
        true // REQUIRED
    );

    /**
     * Private ctor use singleton
     */
    private SpreadsheetRowReferenceSpreadsheetParser(final boolean required) {
        super(required);
    }

    @Override
    int valueFromDigit(final char c) {
        return Character.digit(c, 10);
    }

    @Override
    int radix() {
        return 10;
    }

    @Override
    ParserToken token1(final SpreadsheetReferenceKind absoluteOrRelative, final int row, final String text) {
        return SpreadsheetFormulaParserToken.row(absoluteOrRelative.row(row), text);
    }

    @Override
    public Optional<ValueTypeName> valueType() {
        return ROW;
    }

    private final static Optional<ValueTypeName> ROW = Optional.of(
        SpreadsheetValueType.ROW
    );

    @Override
    public SpreadsheetRowReferenceSpreadsheetParser optional() {
        return this.setRequired(false);
    }

    @Override
    public SpreadsheetRowReferenceSpreadsheetParser required() {
        return this.setRequired(true);
    }

    private SpreadsheetRowReferenceSpreadsheetParser setRequired(final boolean required) {
        return required == this.isRequired() ?
            this :
            new SpreadsheetRowReferenceSpreadsheetParser(required);
    }

    @Override
    public String toString() {
        return SpreadsheetRowReference.class.getSimpleName();
    }
}

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

import walkingkooka.spreadsheet.reference.IllegalColumnArgumentException;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;

/**
 * A {@link Parser} that consumes a {@link SpreadsheetColumnReferenceParserToken}
 */
final class SpreadsheetColumnReferenceSpreadsheetParser extends SpreadsheetColumnOrRowReferenceSpreadsheetParser {

    /**
     * Singleton
     */
    final static SpreadsheetColumnReferenceSpreadsheetParser INSTANCE = new SpreadsheetColumnReferenceSpreadsheetParser();

    /**
     * Private ctor use singleton
     */
    private SpreadsheetColumnReferenceSpreadsheetParser() {
        super();
    }

    @Override
    int valueFromDigit(final char c) {
        return SpreadsheetParsers.columnLetterValue(c);
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
            return SpreadsheetColumnReferenceParserToken.columnReference(
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
    public String toString() {
        return SpreadsheetColumnReference.class.getSimpleName();
    }
}

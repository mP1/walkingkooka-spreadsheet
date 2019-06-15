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

import walkingkooka.spreadsheet.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.SpreadsheetRowReference;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;

/**
 * A {@link Parser} that consumes a {@link SpreadsheetRowReferenceParserToken}
 */
final class SpreadsheetRowReferenceParser extends SpreadsheetColumnOrRowReferenceParser<SpreadsheetRowReferenceParserToken> {

    /**
     * Singleton
     */
    final static SpreadsheetRowReferenceParser INSTANCE = new SpreadsheetRowReferenceParser();

    /**
     * Private ctor use singleton
     */
    private SpreadsheetRowReferenceParser() {
        super();
    }

    @Override
    int valueFromDigit(final char c) {
        return Character.digit(c, SpreadsheetRowReference.RADIX);
    }

    @Override
    int radix() {
        return SpreadsheetRowReference.RADIX;
    }

    @Override
    ParserToken token1(final SpreadsheetReferenceKind absoluteOrRelative, final int row, final String text) {
        return SpreadsheetParserToken.rowReference(absoluteOrRelative.row(row), text);
    }

    @Override
    public String toString() {
        return SpreadsheetRowReference.class.getSimpleName();
    }
}

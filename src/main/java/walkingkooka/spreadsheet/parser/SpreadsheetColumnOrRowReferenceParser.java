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

import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.Optional;

/**
 * Base class for either a column or row reference {@link Parser}.
 */
abstract class SpreadsheetColumnOrRowReferenceParser<T extends SpreadsheetLeafParserToken> implements Parser<SpreadsheetParserContext> {

    /**
     * Package private ctor use singleton
     */
    SpreadsheetColumnOrRowReferenceParser() {
        super();
    }

    // optional dollar sign
    // required digits
    // SpreadsheetRowReference/SpreadsheetColumnReference
    @Override
    public final Optional<ParserToken> parse(final TextCursor cursor, final SpreadsheetParserContext context) {
        Optional<ParserToken> result = Optional.empty();

        if (!cursor.isEmpty()) {
            final TextCursorSavePoint save = cursor.save();
            SpreadsheetReferenceKind absoluteOrRelativer = SpreadsheetReferenceKind.RELATIVE;

            final char c = cursor.at();
            if ('$' == c) {
                absoluteOrRelativer = SpreadsheetReferenceKind.ABSOLUTE;
                cursor.next();
            }
            result = this.parseReference(cursor, absoluteOrRelativer, save);
            if (!result.isPresent()) {
                save.restore();
            }
        }

        return result;
    }

    private Optional<ParserToken> parseReference(final TextCursor cursor,
                                                 final SpreadsheetReferenceKind absoluteOrRelative,
                                                 final TextCursorSavePoint save) {
        Optional<ParserToken> result;

        int value = 0;
        int digitCounter = 0;
        final int radix = this.radix();

        for (; ; ) {
            if (cursor.isEmpty()) {
                result = token(digitCounter, absoluteOrRelative, value - 1, save);
                break;
            }

            final int digit = valueFromDigit(cursor.at());
            if (-1 == digit) {
                result = token(digitCounter, absoluteOrRelative, value - 1, save);
                break;
            }

            digitCounter++;
            value = value * radix + digit;
            cursor.next();
        }

        return result;
    }

    abstract int valueFromDigit(final char c);

    abstract int radix();

    private Optional<ParserToken> token(final int digitCounter,
                                        final SpreadsheetReferenceKind absoluteOrRelative,
                                        final int value,
                                        final TextCursorSavePoint save) {
        return digitCounter > 0 ?
                token0(absoluteOrRelative, value, save) :
                Optional.empty();
    }

    private Optional<ParserToken> token0(final SpreadsheetReferenceKind absoluteOrRelative,
                                         final int value,
                                         final TextCursorSavePoint save) {
        try {
            return Optional.of(this.token1(absoluteOrRelative, value, save.textBetween().toString()));
        } catch (final RuntimeException cause) {
            throw new SpreadsheetParserException(cause.getMessage());
        }
    }

    abstract ParserToken token1(final SpreadsheetReferenceKind absoluteOrRelative,
                                final int row,
                                final String text);
}

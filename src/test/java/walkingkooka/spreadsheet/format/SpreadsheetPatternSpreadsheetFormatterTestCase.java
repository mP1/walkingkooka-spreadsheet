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

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetPatternSpreadsheetFormatterTestCase<F extends SpreadsheetPatternSpreadsheetFormatter,
    T extends SpreadsheetFormatParserToken>
    implements SpreadsheetFormatterTesting2<F>,
    HashCodeEqualsDefinedTesting2<F> {

    SpreadsheetPatternSpreadsheetFormatterTestCase() {
        super();
    }

    @Test
    public final void testWithNullParserTokenFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createFormatter0(null)
        );
    }

    @Override final public F createFormatter() {
        return this.createFormatter(this.pattern());
    }

    abstract String pattern();

    final F createFormatter(final String pattern) {
        return this.createFormatter0(
            this.parsePatternOrFail(
                pattern
            )
        );
    }

    final T parsePatternOrFail(final String pattern) {
        return (T) this.parsePatternOrFail(
            this.parser(),
            pattern
        );
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    final ParserToken parsePatternOrFail(final Parser<SpreadsheetFormatParserContext> parser,
                                         final String pattern) {
        return parser.orFailIfCursorNotEmpty(ParserReporters.basic())
            .parse(
                TextCursors.charSequence(pattern),
                SpreadsheetFormatParserContexts.basic(InvalidCharacterExceptionFactory.POSITION)
            ).get();
    }

    abstract Parser<SpreadsheetFormatParserContext> parser();

    abstract F createFormatter0(final T token);

    @Override
    public final String typeNamePrefix() {
        return SpreadsheetPatternSpreadsheetFormatter.class.getSimpleName();
    }

    @Override
    public final String typeNameSuffix() {
        return "";
    }

    // equals...........................................................................................................

    @Override
    public final F createObject() {
        return this.createFormatter();
    }
}

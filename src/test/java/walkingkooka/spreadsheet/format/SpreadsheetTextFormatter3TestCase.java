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
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContexts;
import walkingkooka.text.cursor.parser.ParserReporters;

import java.math.MathContext;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetTextFormatter3TestCase<F extends SpreadsheetTextFormatter3<V, T>,
        V,
        T extends SpreadsheetFormatParserToken>
        extends SpreadsheetTextFormatter2TestCase<F, V> {

    SpreadsheetTextFormatter3TestCase() {
        super();
    }

    @Test
    public final void testWithNullParserTokenFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createFormatter0(null);
        });
    }

    @Override
    final public F createFormatter() {
        return this.createFormatter(this.pattern());
    }

    abstract String pattern();

    final F createFormatter(final String pattern) {
        return this.createFormatter0(this.parsePatternOrFail(this.parser(), pattern).cast());
    }

    final T parsePatternOrFail(final String pattern) {
        return this.parsePatternOrFail(this.parser(), pattern).cast();
    }

    final SpreadsheetFormatParserToken parsePatternOrFail(final Parser<SpreadsheetFormatParserContext> parser, final String pattern) {
        return parser.orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(pattern),
                        SpreadsheetFormatParserContexts.basic(
                                ParserContexts.basic(
                                        DecimalNumberContexts.american(MathContext.DECIMAL32))))
                .get()
                .cast();
    }

    abstract Parser<SpreadsheetFormatParserContext> parser();

    abstract F createFormatter0(T token);
}

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
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.ConverterTesting2;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContexts;
import walkingkooka.spreadsheet.convert.SpreadsheetConverters;
import walkingkooka.spreadsheet.format.parser.NumberSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.LineEnding;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.SequenceParserToken;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContexts;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public final class SpreadsheetFormatterConverterTest implements ConverterTesting2<SpreadsheetFormatterConverter, SpreadsheetConverterContext>,
    HashCodeEqualsDefinedTesting2<SpreadsheetFormatterConverter> {

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;

    @Test
    public void testFormatBigDecimal() {
        this.formatDecimalAndCheck(BigDecimal.valueOf(1.25));
    }

    @Test
    public void testFormatBigInteger() {
        this.formatIntegerAndCheck(BigInteger.valueOf(125));
    }

    @Test
    public void testFormatByte() {
        this.formatIntegerAndCheck((byte) 125);
    }

    @Test
    public void testFormatShort() {
        this.formatIntegerAndCheck((short) 125);
    }

    @Test
    public void testFormatInteger() {
        this.formatIntegerAndCheck(125);
    }

    @Test
    public void testFormatLong() {
        this.formatIntegerAndCheck(125L);
    }

    @Test
    public void testFormatDouble() {
        this.formatDecimalAndCheck(1.25);
    }

    @Test
    public void testFormatFloat() {
        this.formatDecimalAndCheck(1.25f);
    }

    private void formatDecimalAndCheck(final Number number) {
        this.convertAndCheck(number, String.class, "prefix1 1.2500 suffix2");
    }

    private void formatIntegerAndCheck(final Number number) {
        this.convertAndCheck(number, String.class, "prefix1 125.0000 suffix2");
    }

    @Test
    public void testToString() {
        final SpreadsheetFormatter formatter = this.formatter("\"prefix1\" #.0000 \"suffix2\"");
        this.toStringAndCheck(formatter.converter(), formatter.toString());
    }

    @Override
    public SpreadsheetFormatterConverter createConverter() {
        return this.createConverter("\"prefix1\" #.0000 \"suffix2\"");
    }

    private SpreadsheetFormatterConverter createConverter(final String pattern) {
        return SpreadsheetFormatterConverter.with(this.formatter(pattern));
    }

    private SpreadsheetFormatter formatter(final String pattern) {
        return SpreadsheetFormatParsers.numberFormat()
            .orFailIfCursorNotEmpty(ParserReporters.basic())
            .parse(
                TextCursors.charSequence(pattern),
                SpreadsheetFormatParserContexts.basic(InvalidCharacterExceptionFactory.POSITION)
            ).map((t) -> t.cast(SequenceParserToken.class).value().get(0).cast(NumberSpreadsheetFormatParserToken.class))
            .map((t) -> SpreadsheetFormatters.number(
                t,
                false // suppressMinusSignsWithinParens
                )
            ).orElseThrow(UnsupportedOperationException::new);
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return SpreadsheetConverterContexts.basic(
            SpreadsheetConverterContexts.NO_METADATA,
            SpreadsheetConverterContexts.NO_VALIDATION_REFERENCE,
            SpreadsheetConverters.system(),
            (s) -> {
                throw new UnsupportedOperationException();
            },
            JsonNodeConverterContexts.basic(
                ExpressionNumberConverterContexts.basic(
                    Converters.fake(),
                    ConverterContexts.basic(
                        false, // canNumbersHaveGroupSeparator
                        Converters.JAVA_EPOCH_OFFSET, // dateOffset
                        LineEnding.NL,
                        ',', // valueSeparator
                        Converters.fake(),
                        DateTimeContexts.fake(),
                        DecimalNumberContexts.american(MathContext.UNLIMITED)
                    ),
                    EXPRESSION_NUMBER_KIND
                ),
                JsonNodeMarshallUnmarshallContexts.fake()
            ),
            LocaleContexts.fake()
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentSpreadsheetFormatter() {
        this.checkNotEquals(
            SpreadsheetFormatterConverter.with(
                SpreadsheetFormatters.fake()
            )
        );
    }

    @Override
    public SpreadsheetFormatterConverter createObject() {
        return SpreadsheetFormatterConverter.with(
            SpreadsheetFormatters.defaultText()
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetFormatterConverter> type() {
        return SpreadsheetFormatterConverter.class;
    }
}

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

package walkingkooka.spreadsheet.convert;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.currency.CurrencyLocaleContexts;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.storage.HasUserDirectorieses;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContexts;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetConverterFormatPatternToStringTest extends SpreadsheetConverterTestCase<SpreadsheetConverterFormatPatternToString>
    implements HashCodeEqualsDefinedTesting2<SpreadsheetConverterFormatPatternToString> {

    private final static ExpressionNumberKind KIND = ExpressionNumberKind.BIG_DECIMAL;

    @Test
    public void testWithNullPatternFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetConverterFormatPatternToString.with(null)
        );
    }

    @Test
    public void testWithEmptyPatternFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetConverterFormatPatternToString.with("")
        );
    }

    // boolean..........................................................................................................

    @Test
    public void testConvertBooleanFalseToString() {
        this.convertToStringAndCheck(
            "$000.000",
            false,
            "$000.000"
        );
    }

    @Test
    public void testConvertBooleanTrueToString() {
        this.convertToStringAndCheck(
            "$000.000",
            true,
            "$001.000"
        );
    }

    // numbers.........................................................................................................

    private final static int NUMBER = 123;

    @Test
    public void testConvertBigDecimalToString() {
        this.convertNumberToStringAndCheck(
            BigDecimal.valueOf(NUMBER)
        );
    }

    @Test
    public void testConvertBigIntegerToString() {
        this.convertNumberToStringAndCheck(
            BigInteger.valueOf(NUMBER)
        );
    }

    @Test
    public void testConvertFloatToString() {
        this.convertNumberToStringAndCheck(
            Float.valueOf(NUMBER)
        );
    }

    @Test
    public void testConvertDoubleToString() {
        this.convertNumberToStringAndCheck(
            Double.valueOf(NUMBER)
        );
    }

    @Test
    public void testConvertExpressionNumberToString() {
        this.convertNumberToStringAndCheck(
            KIND.create(NUMBER)
        );
    }

    @Test
    public void testConvertIntegerToString() {
        this.convertNumberToStringAndCheck(
            NUMBER
        );
    }

    @Test
    public void testConvertLongToString() {
        this.convertNumberToStringAndCheck(
            (long) NUMBER
        );
    }

    @Test
    public void testConvertShortToString() {
        this.convertNumberToStringAndCheck(
            (short) NUMBER
        );
    }

    private void convertNumberToStringAndCheck(final Number number) {
        this.convertToStringAndCheck(
            "$000.000",
            number,
            "$123.000"
        );
    }

    // date time ......................................................................................................

    @Test
    public void testConvertLocalDateToString() {
        this.convertToStringAndCheck(
            "yyyy mm dd",
            LocalDate.of(1999, 12, 31),
            "1999 12 31"
        );
    }

    @Test
    public void testConvertLocalDateTimeToString() {
        this.convertToStringAndCheck(
            "yyyy mm dd hh mm ss",
            LocalDateTime.of(1999, 12, 31, 12, 58, 59),
            "1999 12 31 12 58 59"
        );
    }

    @Test
    public void testConvertLocalTimeToString() {
        this.convertToStringAndCheck(
            "ss mm hh",
            LocalTime.of(12, 58, 59),
            "59 58 12"
        );
    }

    // Text...........................................................................................................

    @Test
    public void testConvertCharacterToString() {
        this.convertToStringAndCheck("Hello");
    }

    @Test
    public void testConvertStringToString() {
        this.convertToStringAndCheck("Hello");
    }

    @Test
    public void testConvertCellToString() {
        this.convertToStringAndCheck(
            SpreadsheetSelection.parseCell("$A1")
        );
    }

    @Test
    public void testConvertCellRangeToString() {
        this.convertToStringAndCheck(
            SpreadsheetSelection.parseCellRange("$A1:$B2")
        );
    }

    @Test
    public void testConvertColumnToString() {
        this.convertToStringAndCheck(
            SpreadsheetSelection.parseColumn("$A")
        );
    }

    @Test
    public void testConvertColumnRangeToString() {
        this.convertToStringAndCheck(
            SpreadsheetSelection.parseColumnRange("$A:$B")
        );
    }

    @Test
    public void testConvertRowToString() {
        this.convertToStringAndCheck(
            SpreadsheetSelection.parseRow("$12")
        );
    }

    @Test
    public void testConvertRowRangeToString() {
        this.convertToStringAndCheck(
            SpreadsheetSelection.parseRowRange("$12:$34")
        );
    }

    @Test
    public void testConvertLabelToString() {
        this.convertToStringAndCheck(
            SpreadsheetSelection.labelName("Label123")
        );
    }

    private void convertToStringAndCheck(final Object value) {
        this.convertToStringAndCheck(
            this.createConverter(),
            value,
            value.toString()
        );
    }

    private void convertToStringAndCheck(final String pattern,
                                         final Object value,
                                         final String expected) {
        this.convertToStringAndCheck(
            SpreadsheetConverterFormatPatternToString.with(pattern),
            value,
            expected
        );
    }

    private void convertToStringAndCheck(final SpreadsheetConverterFormatPatternToString converter,
                                         final Object value,
                                         final String expected) {
        this.convertAndCheck(
            converter,
            value,
            String.class,
            this.createContext(),
            expected
        );
    }

    @Override
    public SpreadsheetConverterFormatPatternToString createConverter() {
        return SpreadsheetConverterFormatPatternToString.with("!");
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        final Converter<SpreadsheetConverterContext> converter = Converters.collection(
            Lists.of(
                Converters.simple(),
                Converters.booleanToNumber(),
                SpreadsheetConverters.textToText(),
                SpreadsheetConverters.numberToNumber(),
                Converters.localDateToLocalDateTime(),
                Converters.localTimeToLocalDateTime()
            )
        );

        final Locale locale = Locale.FRANCE;

        return SpreadsheetConverterContexts.basic(
            HasUserDirectorieses.fake(),
            SpreadsheetConverterContexts.NO_METADATA,
            SpreadsheetConverterContexts.NO_VALIDATION_REFERENCE,
            converter,
            SpreadsheetLabelNameResolvers.fake(),
            JsonNodeConverterContexts.basic(
                ExpressionNumberConverterContexts.basic(
                    Converters.fake(),
                    ConverterContexts.basic(
                        false, // canNumbersHaveGroupSeparator
                        Converters.JAVA_EPOCH_OFFSET, // dateOffset
                        INDENTATION,
                        LineEnding.NL,
                        ',', // valueSeparator
                        converter.cast(ConverterContext.class),
                        CurrencyLocaleContexts.fake(),
                        DateTimeContexts.basic(
                            DateTimeSymbols.fromDateFormatSymbols(
                                new DateFormatSymbols(locale)
                            ),
                            locale,
                            1900,
                            50,
                            LocalDateTime::now
                        ),
                        DecimalNumberContexts.american(MathContext.DECIMAL128)
                    ),
                    KIND
                ),
                JsonNodeMarshallUnmarshallContexts.fake()
            ),
            LocaleContexts.jre(locale)
        );
    }

    // Object...........................................................................................................

    @Test
    public void testEqualsDifferentPattern() {
        this.checkNotEquals(
            SpreadsheetConverterFormatPatternToString.with("#.##")
        );
    }

    @Override
    public SpreadsheetConverterFormatPatternToString createObject() {
        return SpreadsheetConverterFormatPatternToString.with("$0.00");
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterFormatPatternToString> type() {
        return SpreadsheetConverterFormatPatternToString.class;
    }
}

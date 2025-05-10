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
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberConverters;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetConverterStringToFormatPatternTest extends SpreadsheetConverterTestCase<SpreadsheetConverterStringToFormatPattern> {

    private final static ExpressionNumberKind KIND = ExpressionNumberKind.BIG_DECIMAL;

    @Test
    public void testWithNullPatternFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetConverterStringToFormatPattern.with(null)
        );
    }

    @Test
    public void testWithEmptyPatternFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetConverterStringToFormatPattern.with("")
        );
    }

    // boolean.........................................................................................................

    @Test
    public void testConvertStringToBooleanFalse() {
        this.convertAndCheck(
                SpreadsheetConverterStringToFormatPattern.with("$000.000"),
                false,
                String.class,
                this.createContext(),
                "$000.000"
        );
    }

    @Test
    public void testConvertStringToBooleanTrue() {
        this.convertAndCheck(
                SpreadsheetConverterStringToFormatPattern.with("$000.000"),
                true,
                String.class,
                this.createContext(),
                "$001.000"
        );
    }

    // numbers.........................................................................................................

    private final static int NUMBER = 123;

    @Test
    public void testConvertStringToBigDecimal() {
        this.convertStringToNumberAndCheck(
                BigDecimal.valueOf(NUMBER)
        );
    }

    @Test
    public void testConvertStringToBigInteger() {
        this.convertStringToNumberAndCheck(
                BigInteger.valueOf(NUMBER)
        );
    }

    @Test
    public void testConvertStringToFloat() {
        this.convertStringToNumberAndCheck(
                Float.valueOf(NUMBER)
        );
    }

    @Test
    public void testConvertStringToDouble() {
        this.convertStringToNumberAndCheck(
                Double.valueOf(NUMBER)
        );
    }

    @Test
    public void testConvertStringToExpressionNumber() {
        this.convertStringToNumberAndCheck(
                KIND.create(NUMBER)
        );
    }

    @Test
    public void testConvertStringToInteger() {
        this.convertStringToNumberAndCheck(
                Integer.valueOf(NUMBER)
        );
    }

    @Test
    public void testConvertStringToLong() {
        this.convertStringToNumberAndCheck(
                Integer.valueOf(NUMBER)
        );
    }

    @Test
    public void testConvertStringToShort() {
        this.convertStringToNumberAndCheck(
                Short.valueOf((short) NUMBER)
        );
    }

    private void convertStringToNumberAndCheck(final Number number) {
        this.convertAndCheck(
                SpreadsheetConverterStringToFormatPattern.with("$000.000"),
                number,
                String.class,
                this.createContext(),
                "$123.000"
        );
    }

    // date time ......................................................................................................

    @Test
    public void testConvertStringToLocalDate() {
        this.convertAndCheck(
                SpreadsheetConverterStringToFormatPattern.with("yyyy mm dd"),
                LocalDate.of(1999, 12, 31),
                String.class,
                this.createContext(),
                "1999 12 31"
        );
    }

    @Test
    public void testConvertStringToLocalDateTime() {
        this.convertAndCheck(
                SpreadsheetConverterStringToFormatPattern.with("yyyy mm dd hh mm ss"),
                LocalDateTime.of(1999, 12, 31, 12, 58, 59),
                String.class,
                this.createContext(),
                "1999 12 31 12 58 59"
        );
    }

    @Test
    public void testConvertStringToLocalTime() {
        this.convertAndCheck(
                SpreadsheetConverterStringToFormatPattern.with("ss mm hh"),
                LocalTime.of(12, 58, 59),
                String.class,
                this.createContext(),
                "59 58 12"
        );
    }

    // String.... ......................................................................................................

    @Test
    public void testConvertStringToCharacter() {
        this.convertStringAndCheck("Hello");
    }

    @Test
    public void testConvertStringToString() {
        this.convertStringAndCheck("Hello");
    }

    @Test
    public void testConvertStringToCell() {
        this.convertStringAndCheck(
                SpreadsheetSelection.parseCell("$A1")
        );
    }

    @Test
    public void testConvertStringToCellRange() {
        this.convertStringAndCheck(
                SpreadsheetSelection.parseCellRange("$A1:$B2")
        );
    }

    @Test
    public void testConvertStringToColumn() {
        this.convertStringAndCheck(
                SpreadsheetSelection.parseColumn("$A")
        );
    }

    @Test
    public void testConvertStringToColumnRange() {
        this.convertStringAndCheck(
                SpreadsheetSelection.parseColumnRange("$A:$B")
        );
    }

    @Test
    public void testConvertStringToRow() {
        this.convertStringAndCheck(
                SpreadsheetSelection.parseRow("$12")
        );
    }

    @Test
    public void testConvertStringToRowRange() {
        this.convertStringAndCheck(
                SpreadsheetSelection.parseRowRange("$12:$34")
        );
    }

    @Test
    public void testConvertStringToLabel() {
        this.convertStringAndCheck(
                SpreadsheetSelection.labelName("Label123")
        );
    }

    private void convertStringAndCheck(final Object value) {
        this.convertAndCheck(
                this.createConverter(),
                value,
                String.class,
                this.createContext(),
                value.toString()
        );
    }

    @Override
    public SpreadsheetConverterStringToFormatPattern createConverter() {
        return SpreadsheetConverterStringToFormatPattern.with("!");
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        final Converter<SpreadsheetConverterContext> converter = Converters.collection(
                Lists.of(
                        ExpressionNumberConverters.toNumberOrExpressionNumber(
                                Converters.numberToNumber()
                        ),
                        Converters.localDateToLocalDateTime(),
                        Converters.localTimeToLocalDateTime(),
                        Converters.simple()
                )
        );

        final Locale locale = Locale.FRANCE;

        return SpreadsheetConverterContexts.basic(
                SpreadsheetConverterContexts.NO_VALIDATION_REFERENCE,
                converter,
                SpreadsheetLabelNameResolvers.fake(),
                ExpressionNumberConverterContexts.basic(
                        Converters.fake(),
                        ConverterContexts.basic(
                                Converters.JAVA_EPOCH_OFFSET, // dateOffset
                                converter.cast(ConverterContext.class),
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
                )
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterStringToFormatPattern> type() {
        return SpreadsheetConverterStringToFormatPattern.class;
    }
}

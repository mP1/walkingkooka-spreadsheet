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
import walkingkooka.convert.ConverterTesting2;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberConverters;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StringToFormatPatternConverterTest implements ConverterTesting2<StringToFormatPatternConverter, SpreadsheetConverterContext> {

    private final static ExpressionNumberKind KIND = ExpressionNumberKind.BIG_DECIMAL;

    @Test
    public void testWithNullPatternFails() {
        assertThrows(
                NullPointerException.class,
                () -> StringToFormatPatternConverter.with(null)
        );
    }

    @Test
    public void testWithEmptyPatternFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> StringToFormatPatternConverter.with("")
        );
    }

    // boolean.........................................................................................................

    @Test
    public void testBooleanFalse() {
        this.convertAndCheck(
                StringToFormatPatternConverter.with("$000.000"),
                false,
                String.class,
                this.createContext(),
                "$000.000"
        );
    }

    @Test
    public void testBooleanTrue() {
        this.convertAndCheck(
                StringToFormatPatternConverter.with("$000.000"),
                true,
                String.class,
                this.createContext(),
                "$001.000"
        );
    }

    // numbers.........................................................................................................

    private final static int NUMBER = 123;

    @Test
    public void testBigDecimal() {
        this.convertNumberAndCheck(
                BigDecimal.valueOf(NUMBER)
        );
    }

    @Test
    public void testBigInteger() {
        this.convertNumberAndCheck(
                BigInteger.valueOf(NUMBER)
        );
    }

    @Test
    public void testFloat() {
        this.convertNumberAndCheck(
                Float.valueOf(NUMBER)
        );
    }

    @Test
    public void testDouble() {
        this.convertNumberAndCheck(
                Double.valueOf(NUMBER)
        );
    }

    @Test
    public void testExpressionNumber() {
        this.convertNumberAndCheck(
                KIND.create(NUMBER)
        );
    }

    @Test
    public void testInteger() {
        this.convertNumberAndCheck(
                Integer.valueOf(NUMBER)
        );
    }

    @Test
    public void testLong() {
        this.convertNumberAndCheck(
                Integer.valueOf(NUMBER)
        );
    }

    @Test
    public void testShort() {
        this.convertNumberAndCheck(
                Short.valueOf((short) NUMBER)
        );
    }

    private void convertNumberAndCheck(final Number number) {
        this.convertAndCheck(
                StringToFormatPatternConverter.with("$000.000"),
                number,
                String.class,
                this.createContext(),
                "$123.000"
        );
    }

    // date time ......................................................................................................

    @Test
    public void testLocalDate() {
        this.convertAndCheck(
                StringToFormatPatternConverter.with("yyyy mm dd"),
                LocalDate.of(1999, 12, 31),
                String.class,
                this.createContext(),
                "1999 12 31"
        );
    }

    @Test
    public void testLocalDateTime() {
        this.convertAndCheck(
                StringToFormatPatternConverter.with("yyyy mm dd hh mm ss"),
                LocalDateTime.of(1999, 12, 31, 12, 58, 59),
                String.class,
                this.createContext(),
                "1999 12 31 12 58 59"
        );
    }

    @Test
    public void testLocalTime() {
        this.convertAndCheck(
                StringToFormatPatternConverter.with("ss mm hh"),
                LocalTime.of(12, 58, 59),
                String.class,
                this.createContext(),
                "59 58 12"
        );
    }

    // String.... ......................................................................................................

    @Test
    public void testCharacter() {
        this.convertStringAndCheck("Hello");
    }

    @Test
    public void testString() {
        this.convertStringAndCheck("Hello");
    }

    @Test
    public void testCell() {
        this.convertStringAndCheck(
                SpreadsheetSelection.parseCell("$A1")
        );
    }

    @Test
    public void testCellRange() {
        this.convertStringAndCheck(
                SpreadsheetSelection.parseCellRange("$A1:$B2")
        );
    }

    @Test
    public void testColumn() {
        this.convertStringAndCheck(
                SpreadsheetSelection.parseColumn("$A")
        );
    }

    @Test
    public void testColumnRange() {
        this.convertStringAndCheck(
                SpreadsheetSelection.parseColumnRange("$A:$B")
        );
    }

    @Test
    public void testRow() {
        this.convertStringAndCheck(
                SpreadsheetSelection.parseRow("$12")
        );
    }

    @Test
    public void testRowRange() {
        this.convertStringAndCheck(
                SpreadsheetSelection.parseRowRange("$12:$34")
        );
    }

    @Test
    public void testLabel() {
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
    public StringToFormatPatternConverter createConverter() {
        return StringToFormatPatternConverter.with("!");
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

        return SpreadsheetConverterContexts.basic(
                converter,
                SpreadsheetLabelNameResolvers.fake(),
                ExpressionNumberConverterContexts.basic(
                        Converters.fake(),
                        ConverterContexts.basic(
                                Converters.JAVA_EPOCH_OFFSET, // dateOffset
                                converter.cast(ConverterContext.class),
                                DateTimeContexts.locale(
                                        Locale.FRANCE,
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

    @Override
    public Class<StringToFormatPatternConverter> type() {
        return StringToFormatPatternConverter.class;
    }
}

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
import walkingkooka.Either;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetValueVisitorTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class SpreadsheetConverterUnformattedNumberSpreadsheetValueVisitorTest implements SpreadsheetValueVisitorTesting<SpreadsheetConverterUnformattedNumberSpreadsheetValueVisitor> {

    @Test
    public void testNull() {
        this.convertToStringAndCheck(
            null
        );
    }

    @Test
    public void testBigDecimal() {
        this.convertToStringAndCheck(
            BigDecimal.valueOf(12.5),
            "12.5"
        );
    }

    @Test
    public void testBigInteger() {
        this.convertToStringAndCheck(
            BigInteger.valueOf(125),
            "125"
        );
    }

    @Test
    public void testByte() {
        this.convertToStringAndCheck(
            Byte.MAX_VALUE,
            "127"
        );
    }

    @Test
    public void testCell() {
        this.convertToStringAndCheck(
            SpreadsheetSelection.parseCell("$A$1")
        );
    }

    @Test
    public void testCellRange() {
        this.convertToStringAndCheck(
            SpreadsheetSelection.parseCellRange("$A$1:B2")
        );
    }

    @Test
    public void testCharacter() {
        this.convertToStringAndCheck(
            'A'
        );
    }

    @Test
    public void testColumn() {
        this.convertToStringAndCheck(
            SpreadsheetSelection.parseColumn("$A")
        );
    }

    @Test
    public void testColumnRange() {
        this.convertToStringAndCheck(
            SpreadsheetSelection.parseColumnRange("$B:C")
        );
    }

    @Test
    public void testDouble() {
        this.convertToStringAndCheck(
            12.5,
            "12.5"
        );
    }

    @Test
    public void testExpressionNumber() {
        this.convertToStringAndCheck(
            ExpressionNumberKind.BIG_DECIMAL.create(34.5),
            "34.5"
        );
    }

    @Test
    public void testFloat() {
        this.convertToStringAndCheck(
            12.5f,
            "12.5"
        );
    }

    @Test
    public void testInt() {
        this.convertToStringAndCheck(
            12,
            "12"
        );
    }

    @Test
    public void testLocalDate() {
        final Object value = LocalDate.now();
        final String expected = "1999/12/31";

        this.convertToStringAndCheck(
            value,
            new FakeExpressionEvaluationContext() {
                @Override
                public <T> Either<T, String> convert(final Object v,
                                                     final Class<T> target) {
                    checkEquals(value, v);
                    return this.successfulConversion(
                        expected,
                        target
                    );
                }
            },
            expected
        );
    }


    @Test
    public void testLocalDateTime() {
        final Object value = LocalDateTime.now();
        final String expected = "1999/12/31 12/58/59";

        this.convertToStringAndCheck(
            value,
            new FakeExpressionEvaluationContext() {
                @Override
                public <T> Either<T, String> convert(final Object v,
                                                     final Class<T> target) {
                    checkEquals(value, v);
                    return this.successfulConversion(
                        expected,
                        target
                    );
                }
            },
            expected
        );
    }

    @Test
    public void testLocalTime() {
        final Object value = LocalTime.now();
        final String expected = "12/58/59";

        this.convertToStringAndCheck(
            value,
            new FakeExpressionEvaluationContext() {
                @Override
                public <T> Either<T, String> convert(final Object v,
                                                     final Class<T> target) {
                    checkEquals(value, v);
                    return this.successfulConversion(
                        expected,
                        target
                    );
                }
            },
            expected
        );
    }

    @Test
    public void testLong() {
        this.convertToStringAndCheck(
            12L,
            "12"
        );
    }

    @Test
    public void testRow() {
        this.convertToStringAndCheck(
            SpreadsheetSelection.parseRow("1")
        );
    }

    @Test
    public void testRowRange() {
        this.convertToStringAndCheck(
            SpreadsheetSelection.parseRowRange("$2:3")
        );
    }

    @Test
    public void testShort() {
        this.convertToStringAndCheck(
            Short.MAX_VALUE,
            "32767"
        );
    }

    @Test
    public void testString() {
        this.convertToStringAndCheck(
            "123abc"
        );
    }

    private void convertToStringAndCheck(final Object value) {
        this.convertToStringAndCheck(
            value,
            null != value ? value.toString() : (String) value
        );
    }


    private void convertToStringAndCheck(final Object value,
                                         final String expected) {
        this.convertToStringAndCheck(
            value,
            ExpressionNumberConverterContexts.fake(),
            expected
        );
    }

    private void convertToStringAndCheck(final Object value,
                                         final ExpressionNumberConverterContext context,
                                         final String expected) {
        this.checkEquals(
            expected,
            SpreadsheetConverterUnformattedNumberSpreadsheetValueVisitor.convertToString(
                value,
                context
            ),
            () -> "convertToString " + CharSequences.quoteIfChars(value)
        );
    }

    @Override
    public SpreadsheetConverterUnformattedNumberSpreadsheetValueVisitor createVisitor() {
        return new SpreadsheetConverterUnformattedNumberSpreadsheetValueVisitor(null);
    }

    @Override
    public Class<SpreadsheetConverterUnformattedNumberSpreadsheetValueVisitor> type() {
        return SpreadsheetConverterUnformattedNumberSpreadsheetValueVisitor.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public String typeNamePrefix() {
        return SpreadsheetConverterUnformattedNumber.class.getSimpleName();
    }
}

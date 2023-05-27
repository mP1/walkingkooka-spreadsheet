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
import walkingkooka.Either;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public final class GeneralNumberSpreadsheetFormatterTest extends SpreadsheetFormatterTestCase<GeneralNumberSpreadsheetFormatter> {

    private final static ExpressionNumberKind KIND = ExpressionNumberKind.BIG_DECIMAL;

    @Test
    public void testFormatZeroBigDecimal() {
        this.formatAndCheck2(
                ExpressionNumberKind.BIG_DECIMAL.zero(),
                "0"
        );
    }

    @Test
    public void testFormatZeroDouble() {
        this.formatAndCheck2(
                ExpressionNumberKind.DOUBLE.zero(),
                "0"
        );
    }

    @Test
    public void testFormatSmallNumberBigDecimal() {
        this.formatAndCheck2(
                ExpressionNumberKind.BIG_DECIMAL.create(1),
                "1"
        );
    }

    @Test
    public void testFormatSmallNumberDouble() {
        this.formatAndCheck2(
                ExpressionNumberKind.DOUBLE.create(1),
                "1"
        );
    }

    @Test
    public void testFormatSmallNumberBigDecimal2() {
        this.formatAndCheck2(
                ExpressionNumberKind.BIG_DECIMAL.create(1.5),
                "1!5"
        );
    }

    @Test
    public void testFormatSmallNumberDouble2() {
        this.formatAndCheck2(
                ExpressionNumberKind.DOUBLE.create(1.5),
                "1!5"
        );
    }

    @Test
    public void testFormatSmallNumberBigDecimal3() {
        this.formatAndCheck2(
                ExpressionNumberKind.BIG_DECIMAL.create(-12.5),
                "N12!5"
        );
    }

    @Test
    public void testFormatSmallNumberDouble3() {
        this.formatAndCheck2(
                ExpressionNumberKind.DOUBLE.create(-12.5),
                "N12!5"
        );
    }

    @Test
    public void testFormatSmallNumberBigDecimalAlmostScientific() {
        this.formatAndCheck2(
                ExpressionNumberKind.BIG_DECIMAL.create(12345678901.5),
                "12345678901!5"
        );
    }

    @Test
    public void testFormatSmallNumberDoubleAlmostScientific() {
        this.formatAndCheck2(
                ExpressionNumberKind.DOUBLE.create(12345678901.5),
                "12345678901!5"
        );
    }

    // scientific.......................................................................................................

    @Test
    public void testFormatScientificNumberBigDecimal() {
        this.formatAndCheck2(
                ExpressionNumberKind.BIG_DECIMAL.create(1E12),
                "1!X12"
        );
    }

    @Test
    public void testFormatScientificNumberDouble() {
        this.formatAndCheck2(
                ExpressionNumberKind.DOUBLE.create(1E12),
                "1!X12"
        );
    }

    @Test
    public void testFormatScientificNumberBigDecimal2() {
        this.formatAndCheck2(
                ExpressionNumberKind.BIG_DECIMAL.create(2.3E12),
                "2!3X12"
        );
    }

    @Test
    public void testFormatScientificNumberDouble2() {
        this.formatAndCheck2(
                ExpressionNumberKind.DOUBLE.create(2.3E12),
                "2!3X12"
        );
    }

    @Test
    public void testFormatScientificNumberBigDecimal3() {
        this.formatAndCheck2(
                ExpressionNumberKind.BIG_DECIMAL.create(-1.2E12),
                "N1!2X12"
        );
    }

    @Test
    public void testFormatScientificNumberDouble3() {
        this.formatAndCheck2(
                ExpressionNumberKind.DOUBLE.create(-1.2E12),
                "N1!2X12"
        );
    }

    @Test
    public void testFormatScientificNumberBigDecimal4() {
        this.formatAndCheck2(
                ExpressionNumberKind.BIG_DECIMAL.create(-1.2345678901E12),
                "N1!2345678901X12"
        );
    }

    @Test
    public void testFormatScientificNumberDouble4() {
        this.formatAndCheck2(
                ExpressionNumberKind.DOUBLE.create(-1.2345678901E12),
                "N1!2345678901X12"
        );
    }

    private void formatAndCheck2(final ExpressionNumber number,
                                 final String text) {
        this.formatAndCheck(
                this.createFormatter(),
                number,
                this.createContext(number.kind()),
                text
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                GeneralNumberSpreadsheetFormatter.INSTANCE,
                "General"
        );
    }

    // SpreadsheetFormatterTestCase.....................................................................................

    @Override
    public GeneralNumberSpreadsheetFormatter createFormatter() {
        return GeneralNumberSpreadsheetFormatter.INSTANCE;
    }

    @Override
    public Object value() {
        return KIND.zero();
    }

    @Override
    public SpreadsheetFormatterContext createContext() {
        return this.createContext(KIND);
    }

    private SpreadsheetFormatterContext createContext(final ExpressionNumberKind kind) {
        return new FakeSpreadsheetFormatterContext() {

            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> target) {
                try {
                    this.convert(value, target);
                    return true;
                } catch (final Exception failed) {
                    return false;
                }
            }

            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                final ExpressionNumber expressionNumber = ExpressionNumber.class.cast(value);
                return this.successfulConversion(
                        ExpressionNumber.class == target ?
                                expressionNumber :
                                target.cast(
                                        target == BigDecimal.class ?
                                                expressionNumber.bigDecimal() :
                                                expressionNumber.doubleValue()
                                ),
                        target
                );
            }

            @Override
            public String currencySymbol() {
                return "C";
            }

            @Override
            public char decimalSeparator() {
                return '!';
            }

            @Override
            public String exponentSymbol() {
                return "X";
            }

            @Override
            public char groupingSeparator() {
                return 'G';
            }

            @Override
            public MathContext mathContext() {
                return new MathContext(32, RoundingMode.HALF_UP);
            }

            @Override
            public char negativeSign() {
                return 'N';
            }

            @Override
            public char percentageSymbol() {
                return 'R';
            }

            @Override
            public char positiveSign() {
                return 'P';
            }
        };
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<GeneralNumberSpreadsheetFormatter> type() {
        return GeneralNumberSpreadsheetFormatter.class;
    }
}

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
import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.BigDecimal;

public final class SpreadsheetConverterNullToNumberTest extends SpreadsheetConverterTestCase<SpreadsheetConverterNullToNumber> {

    private final static ExpressionNumberKind KIND = ExpressionNumberKind.BIG_DECIMAL;

    private final static Number VALUE = 123;

    private final static ExpressionNumber MISSING = KIND.create(VALUE);

    @Test
    public void testConvertNullToNumber() {
        this.convertAndCheck(
            null,
            Number.class,
            MISSING
        );
    }

    @Test
    public void testConvertNullToExpressionNumber() {
        this.convertAndCheck(
            null,
            ExpressionNumber.class,
            MISSING
        );
    }

    @Test
    public void testConvertNullToExpressionNumberBigDecimal() {
        this.convertAndCheck(
            null,
            ExpressionNumberKind.BIG_DECIMAL.create(VALUE)
        );
    }

    // Number -> ExpressionNumberDouble gives ExpressionNumberBigDecimal when Context.expressionNumberKind is ExpressionNumberBigDecimal
    @Test
    public void testConvertNullToExpressionNumberDouble() {
        final ExpressionNumber number = ExpressionNumberKind.DOUBLE.create(VALUE);

        this.convertAndCheck(
            null, // value
            number.getClass(),
            this.createContext(ExpressionNumberKind.DOUBLE),
            Cast.to(number)
        );
    }

    @Test
    public void testConvertNullToBigDecimal() {
        this.convertAndCheck(
            null,
            BigDecimal.class,
            MISSING.bigDecimal()
        );
    }

    @Test
    public void testConvertNullToDouble() {
        this.convertAndCheck(
            null,
            Double.class,
            MISSING.doubleValue()
        );
    }

    @Test
    public void testConvertNullToInteger() {
        this.convertAndCheck(
            null,
            Integer.class,
            MISSING.intValueExact()
        );
    }

    @Test
    public void testConvertNumberToNumber() {
        this.convertFails(
            123,
            Number.class
        );
    }

    @Test
    public void testConvertExpressionNumberBigDecimalToExpressionNumber() {
        this.convertFails(
            ExpressionNumberKind.BIG_DECIMAL.zero(),
            ExpressionNumber.class
        );
    }

    @Test
    public void testConvertExpressionNumberDoubleToExpressionNumber() {
        this.convertFails(
            ExpressionNumberKind.DOUBLE.zero(),
            ExpressionNumber.class
        );
    }

    @Override
    public SpreadsheetConverterNullToNumber createConverter() {
        return SpreadsheetConverterNullToNumber.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return this.createContext(MISSING.kind());
    }

    private SpreadsheetConverterContext createContext(final ExpressionNumberKind kind) {
        return new FakeSpreadsheetConverterContext() {
            @Override
            public ExpressionNumber missingCellNumberValue() {
                return kind.create(MISSING);
            }

            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                return SpreadsheetConverters.numberToNumber()
                    .convert(
                        value,
                        target,
                        this
                    );
            }

            @Override
            public ExpressionNumberKind expressionNumberKind() {
                return kind;
            }
        };
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetConverterNullToNumber.INSTANCE,
            "null to Number"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterNullToNumber> type() {
        return SpreadsheetConverterNullToNumber.class;
    }
}

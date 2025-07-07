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
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;

public final class SpreadsheetConverterNullToNumberTest extends SpreadsheetConverterTestCase<SpreadsheetConverterNullToNumber> {

    private final static ExpressionNumber MISSING = ExpressionNumber.with(123);

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
            (Class<ExpressionNumber>) ExpressionNumberKind.BIG_DECIMAL.numberType(),
            MISSING
        );
    }

    @Test
    public void testConvertNullToExpressionNumberDouble() {
        this.convertAndCheck(
            null,
            (Class<ExpressionNumber>) ExpressionNumberKind.DOUBLE.numberType(),
            MISSING
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
        return new FakeSpreadsheetConverterContext() {
            @Override
            public ExpressionNumber missingCellNumberValue() {
                return MISSING;
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

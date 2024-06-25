
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
import walkingkooka.convert.ConverterTesting2;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.BigDecimal;

public final class SpreadsheetErrorToNumberConverterTest implements ConverterTesting2<SpreadsheetErrorToNumberConverter, SpreadsheetConverterContext> {

    @Test
    public void testNonErrorFails() {
        this.convertFails(15, String.class);
    }

    @Test
    public void testNonErrorStringFails() {
        this.convertFails("Hello", String.class);
    }

    @Test
    public void testErrorToExpressionNumber() {
        this.convertFails(
                SpreadsheetErrorKind.ERROR.setMessage("Message will be ignored"),
                ExpressionNumber.class
        );
    }

    @Test
    public void testErrorErrorToString() {
        this.convertFails(
                SpreadsheetErrorKind.ERROR.setMessage("Message will be ignored"),
                String.class
        );
    }

    @Test
    public void testNameLabelToExpressionNumber() {
        this.convertFails(
                SpreadsheetSelection.labelName("Label123"),
                ExpressionNumber.class
        );
    }

    @Test
    public void testErrorNotFoundToBigDecimal() {
        this.convertAndCheck(
                SpreadsheetError.selectionNotFound(SpreadsheetSelection.A1),
                BigDecimal.class,
                BigDecimal.ZERO
        );
    }

    @Test
    public void testErrorNotFoundToExpressionNumber() {
        this.convertAndCheck(
                SpreadsheetError.selectionNotFound(SpreadsheetSelection.A1),
                ExpressionNumber.class,
                EXPRESSION_NUMBER_KIND.zero()
        );
    }

    @Test
    public void testErrorNameToString() {
        final SpreadsheetErrorKind kind = SpreadsheetErrorKind.DIV0;

        this.convertFails(
                kind.setMessage("Message will be ignored2"),
                String.class
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetErrorToNumberConverter.INSTANCE,
                "SpreadsheetError to Number"
        );
    }

    @Override
    public SpreadsheetErrorToNumberConverter createConverter() {
        return SpreadsheetErrorToNumberConverter.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return new FakeSpreadsheetConverterContext() {
            @Override
            public ExpressionNumberKind expressionNumberKind() {
                return EXPRESSION_NUMBER_KIND;
            }
        };
    }

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.BIG_DECIMAL;

    @Override
    public Class<SpreadsheetErrorToNumberConverter> type() {
        return SpreadsheetErrorToNumberConverter.class;
    }
}

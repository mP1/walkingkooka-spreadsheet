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
import walkingkooka.spreadsheet.SpreadsheetErrorConversionException;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetErrorConverterTest implements ConverterTesting2<SpreadsheetErrorConverter, SpreadsheetConverterContext> {

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
        assertThrows(
                SpreadsheetErrorConversionException.class,
                () -> SpreadsheetErrorConverter.INSTANCE.convert(
                        SpreadsheetErrorKind.ERROR.setMessage("Ignored"),
                        ExpressionNumber.class,
                        this.createContext()
                )
        );
    }

    @Test
    public void testErrorErrorToString() {
        final SpreadsheetErrorKind kind = SpreadsheetErrorKind.ERROR;

        this.convertAndCheck(
                kind.setMessage("Message will be ignored"),
                String.class,
                kind.text()
        );
    }

    @Test
    public void testErrorDiv0ToString() {
        final SpreadsheetErrorKind kind = SpreadsheetErrorKind.DIV0;

        this.convertAndCheck(
                kind.setMessage("Message will be ignored2"),
                String.class,
                kind.text()
        );
    }

    @Test
    public void testNameLabelToExpressionNumberFails() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");

        assertThrows(
                SpreadsheetErrorConversionException.class,
                () -> this.convert(
                        SpreadsheetError.notFound(label),
                        ExpressionNumber.class
                )
        );
    }

    @Test
    public void testErrorNotFoundToBigDecimal() {
        this.convertAndCheck(
                SpreadsheetError.notFound(SpreadsheetSelection.parseCell("A1")),
                BigDecimal.class,
                BigDecimal.ZERO
        );
    }

    @Test
    public void testErrorNotFoundToExpressionNumber() {
        this.convertAndCheck(
                SpreadsheetError.notFound(SpreadsheetSelection.parseCell("A1")),
                ExpressionNumber.class,
                EXPRESSION_NUMBER_KIND.zero()
        );
    }

    @Test
    public void testErrorNameToString() {
        final SpreadsheetErrorKind kind = SpreadsheetErrorKind.DIV0;

        this.convertAndCheck(
                kind.setMessage("Message will be ignored2"),
                String.class,
                kind.text()
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetErrorConverter.INSTANCE,
                "SpreadsheetError"
        );
    }

    @Override
    public SpreadsheetErrorConverter createConverter() {
        return SpreadsheetErrorConverter.INSTANCE;
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
    public Class<SpreadsheetErrorConverter> type() {
        return SpreadsheetErrorConverter.class;
    }
}

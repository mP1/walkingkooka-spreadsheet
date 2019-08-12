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

package walkingkooka.spreadsheet.datavalidation;

import org.junit.jupiter.api.Test;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;

import java.math.MathContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetDataValidatorContextTest implements SpreadsheetDataValidatorContextTesting<BasicSpreadsheetDataValidatorContext> {

    @Test
    public void testWithNullCellReferenceFails() {
        assertThrows(NullPointerException.class, () -> {
            BasicSpreadsheetDataValidatorContext.with(null, this.value(), this.expressionEvaluationContext());
        });
    }

    @Test
    public void testWithNullValueFails() {
        assertThrows(NullPointerException.class, () -> {
            BasicSpreadsheetDataValidatorContext.with(this.cellReference(), null, this.expressionEvaluationContext());
        });
    }

    @Test
    public void testWithNullExpressionEvaluationContextFails() {
        assertThrows(NullPointerException.class, () -> {
            BasicSpreadsheetDataValidatorContext.with(this.cellReference(), this.value(), null);
        });
    }

    @Test
    public void testCellReference() {
        assertEquals(this.cellReference(), this.createContext().cellReference());
    }

    @Test
    public void testToString() {
        final ExpressionEvaluationContext context = this.expressionEvaluationContext();
        this.toStringAndCheck(this.createContext(context), context.toString());
    }

    @Override
    public BasicSpreadsheetDataValidatorContext createContext() {
        return this.createContext(expressionEvaluationContext());
    }

    protected BasicSpreadsheetDataValidatorContext createContext(final ExpressionEvaluationContext context) {
        return BasicSpreadsheetDataValidatorContext.with(cellReference(), value(), context);
    }

    private ExpressionReference cellReference() {
        return SpreadsheetReferenceKind.RELATIVE.column(1).setRow(SpreadsheetReferenceKind.RELATIVE.row(2));
    }

    private Object value() {
        return "abc123";
    }

    private ExpressionEvaluationContext expressionEvaluationContext() {
        final DecimalNumberContext decimalNumberContext = this.decimalNumberContext();

        return new FakeExpressionEvaluationContext() {
            @Override
            public MathContext mathContext() {
                return decimalNumberContext.mathContext();
            }

            @Override
            public String currencySymbol() {
                return decimalNumberContext.currencySymbol();
            }

            @Override
            public char decimalSeparator() {
                return decimalNumberContext.decimalSeparator();
            }

            @Override
            public char exponentSymbol() {
                return decimalNumberContext.exponentSymbol();
            }

            @Override
            public char groupingSeparator() {
                return decimalNumberContext.groupingSeparator();
            }

            @Override
            public char negativeSign() {
                return decimalNumberContext.negativeSign();
            }

            @Override
            public char percentageSymbol() {
                return decimalNumberContext.percentageSymbol();
            }

            @Override
            public char positiveSign() {
                return decimalNumberContext.positiveSign();
            }
        };
    }

    private DecimalNumberContext decimalNumberContext() {
        return DecimalNumberContexts.american(this.mathContext());
    }

    @Override
    public String currencySymbol() {
        return this.decimalNumberContext().currencySymbol();
    }

    @Override
    public char decimalSeparator() {
        return this.decimalNumberContext().decimalSeparator();
    }

    @Override
    public char exponentSymbol() {
        return this.decimalNumberContext().exponentSymbol();
    }

    @Override
    public char groupingSeparator() {
        return this.decimalNumberContext().groupingSeparator();
    }

    @Override
    public MathContext mathContext() {
        return MathContext.DECIMAL32;
    }

    @Override
    public char negativeSign() {
        return this.decimalNumberContext().negativeSign();
    }

    @Override
    public char percentageSymbol() {
        return this.decimalNumberContext().percentageSymbol();
    }

    @Override
    public char positiveSign() {
        return this.decimalNumberContext().positiveSign();
    }

    @Override
    public Class<BasicSpreadsheetDataValidatorContext> type() {
        return BasicSpreadsheetDataValidatorContext.class;
    }
}

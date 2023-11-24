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

package walkingkooka.spreadsheet.engine;

import org.junit.jupiter.api.Test;
import walkingkooka.predicate.PredicateTesting2;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.FunctionExpressionName;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class BasicSpreadsheetEngineFilterPredicateTest implements PredicateTesting2<BasicSpreadsheetEngineFilterPredicate, SpreadsheetCell> {

    private final static String CONTEXT_TO_STRING = "FakeSpreadsheetEngineContext123";

    @Test
    public void testTrue() {
        this.testTrue(
                SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
        );
    }

    @Test
    public void testFalse() {
        this.testFalse(
                SpreadsheetSelection.parseCell("B2")
                        .setFormula(SpreadsheetFormula.EMPTY)
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createPredicate(),
                "Test123() " + CONTEXT_TO_STRING
        );
    }

    @Override
    public BasicSpreadsheetEngineFilterPredicate createPredicate() {
        final Expression expression = Expression.call(
                Expression.namedFunction(FunctionExpressionName.with("Test123")),
                Expression.NO_CHILDREN
        );

        return BasicSpreadsheetEngineFilterPredicate.with(
                expression,
                new FakeSpreadsheetEngineContext() {
                    @Override
                    public Object evaluate(final Expression e,
                                           final Optional<SpreadsheetCell> cell) {
                        assertSame(expression, e);

                        final SpreadsheetCellReference cellReference = cell.get()
                                .reference();
                        switch (cellReference.text()) {
                            case "A1":
                                return true;
                            case "B2":
                                return false;
                            default:
                                throw new IllegalArgumentException("Unexpected cell " + cell);
                        }
                    }

                    @Override
                    public String toString() {
                        return CONTEXT_TO_STRING;
                    }
                }
        );
    }

    @Override
    public Class<BasicSpreadsheetEngineFilterPredicate> type() {
        return BasicSpreadsheetEngineFilterPredicate.class;
    }
}
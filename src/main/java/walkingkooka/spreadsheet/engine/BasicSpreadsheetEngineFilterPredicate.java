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

import walkingkooka.predicate.Predicates;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetValueType;
import walkingkooka.tree.expression.Expression;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * This {@link Predicate} is used by {@link BasicSpreadsheetEngine#filterCells(Set, String, Expression, SpreadsheetEngineContext)} to filter each and every {@link SpreadsheetCell}.
 * It is assumed the {@link Expression} returns a {@link Boolean} result otherwise an {@link IllegalStateException} will be thrown.
 * Note cells without formula text and no value are always skipped
 */
final class BasicSpreadsheetEngineFilterPredicate implements Predicate<SpreadsheetCell> {

    static BasicSpreadsheetEngineFilterPredicate with(final String valueType,
                                                      final Expression expression,
                                                      final SpreadsheetEngineContext context) {
        return new BasicSpreadsheetEngineFilterPredicate(
                valueType,
                expression,
                context
        );
    }

    private BasicSpreadsheetEngineFilterPredicate(final String valueType,
                                                  final Expression expression,
                                                  final SpreadsheetEngineContext context) {
        this.valueType = Predicates.customToString(
                SpreadsheetValueType.ANY.equals(valueType) ?
                        v -> Boolean.TRUE :
                        v -> null != v &&
                                valueType.equals(
                                        SpreadsheetValueType.typeName(v.getClass())
                                ),
                valueType
        );
        this.expression = expression;
        this.context = context;
    }

    @Override
    public boolean test(final SpreadsheetCell cell) {
        final SpreadsheetFormula formula = cell.formula();
        return false == formula.text().isEmpty() &&
                this.valueType.test(
                        formula.value()
                                .orElse(null)
                ) &&
                this.testExpression(cell);
    }

    private boolean testExpression(final SpreadsheetCell cell) {
        return this.context.evaluateAsBoolean(
                this.expression,
                Optional.of(
                        cell
                )
        );
    }

    private final Predicate<Object> valueType;

    private final Expression expression;
    private final SpreadsheetEngineContext context;

    @Override
    public String toString() {
        return this.valueType + " " + this.expression + " " + this.context;
    }
}

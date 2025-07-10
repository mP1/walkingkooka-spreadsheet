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
import walkingkooka.spreadsheet.SpreadsheetValueType;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.tree.expression.Expression;
import walkingkooka.validation.ValidationValueTypeName;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * This {@link Predicate} is used by {@link BasicSpreadsheetEngine#filterCells(Set, ValidationValueTypeName, Expression, SpreadsheetEngineContext)} to filter each and every {@link SpreadsheetCell}.
 * It is assumed the {@link Expression} returns a {@link Boolean} result otherwise an {@link IllegalStateException} will be thrown.
 * Note cells without formula text and no value are always skipped
 */
final class BasicSpreadsheetEngineFilterCellsPredicate implements Predicate<SpreadsheetCell> {

    static BasicSpreadsheetEngineFilterCellsPredicate with(final ValidationValueTypeName valueType,
                                                           final Expression expression,
                                                           final SpreadsheetEngineContext context,
                                                           final SpreadsheetExpressionReferenceLoader loader) {
        return new BasicSpreadsheetEngineFilterCellsPredicate(
            valueType,
            expression,
            context,
            loader
        );
    }

    private BasicSpreadsheetEngineFilterCellsPredicate(final ValidationValueTypeName valueType,
                                                       final Expression expression,
                                                       final SpreadsheetEngineContext context,
                                                       final SpreadsheetExpressionReferenceLoader loader) {
        this.valueType = Predicates.customToString(
            valueType.isAny() ?
                v -> Boolean.TRUE :
                v -> null != v &&
                    valueType.equals(
                        SpreadsheetValueType.toValueType(v.getClass())
                            .orElse(null)
                    ),
            valueType.value()
        );
        this.expression = expression;
        this.context = context;
        this.loader = loader;
    }

    @Override
    public boolean test(final SpreadsheetCell cell) {
        return null != cell &&
            this.testNonNull(cell);
    }

    private boolean testNonNull(final SpreadsheetCell cell) {
        final SpreadsheetFormula formula = cell.formula();
        return false == formula.text().isEmpty() &&
            this.valueType.test(
                formula.errorOrValue()
                    .orElse(null)
            ) &&
            this.evaluateExpressionAsBoolean(cell);
    }

    private boolean evaluateExpressionAsBoolean(final SpreadsheetCell cell) {
        boolean test;

        try {
            test = this.expression.toBoolean(
                this.context.spreadsheetExpressionEvaluationContext(
                    Optional.of(cell),
                    this.loader
                )
            );
        } catch (final RuntimeException ignore) {
            test = false;
        }
        return test;
    }

    private final Predicate<Object> valueType;
    private final Expression expression;
    private final SpreadsheetEngineContext context;
    private final SpreadsheetExpressionReferenceLoader loader;

    @Override
    public String toString() {
        return this.valueType + " " + this.expression + " " + this.context + " " + this.loader;
    }
}

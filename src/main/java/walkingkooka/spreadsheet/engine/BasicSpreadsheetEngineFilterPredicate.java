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

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.expression.Expression;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * This {@link Predicate} is used by {@link BasicSpreadsheetEngine#filterCells(Set, Expression, SpreadsheetEngineContext)} to filter each and every {@link SpreadsheetCell}.
 * It is assumed the {@link Expression} returns a {@link Boolean} result otherwise an {@link IllegalStateException} will be thrown.
 */
final class BasicSpreadsheetEngineFilterPredicate implements Predicate<SpreadsheetCell> {

    static BasicSpreadsheetEngineFilterPredicate with(final Expression expression,
                                                      final SpreadsheetEngineContext context) {
        return new BasicSpreadsheetEngineFilterPredicate(
                expression,
                context
        );
    }

    private BasicSpreadsheetEngineFilterPredicate(final Expression expression,
                                                  final SpreadsheetEngineContext context) {
        this.expression = expression;
        this.context = context;
    }

    @Override
    public boolean test(final SpreadsheetCell cell) {
        final SpreadsheetEngineContext context = this.context;

        final Object filter = context.evaluate(
                this.expression,
                Optional.of(
                        cell
                )
        );

        if (false == filter instanceof Boolean) {
            throw new IllegalStateException("Expected boolean result but got " + CharSequences.quoteIfChars(filter));
        }

        return (Boolean) filter;
    }

    private final Expression expression;
    private final SpreadsheetEngineContext context;

    @Override
    public String toString() {
        return this.expression + " " + this.context;
    }
}

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

import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.text.TextNode;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetFormatter} that unconditionally always returns {@link Optional#empty()}.
 */
final class ExpressionSpreadsheetFormatter implements SpreadsheetFormatter {

    static ExpressionSpreadsheetFormatter with(final Expression expression) {
        return new ExpressionSpreadsheetFormatter(
                Objects.requireNonNull(expression, "expression")
        );
    }

    private ExpressionSpreadsheetFormatter(final Expression expression) {
        super();
        this.expression = expression;
    }

    @Override
    public Optional<TextNode> format(final Optional<Object> value,
                                     final SpreadsheetFormatterContext context) {
        return Optional.of(
                context.convertOrFail(
                        context.spreadsheetExpressionEvaluationContext(
                                value // formatValue
                        ).evaluateExpression(this.expression),
                        TextNode.class
                )
        );
    }

    private final Expression expression;

    @Override
    public List<SpreadsheetFormatterSelectorToken> tokens(final SpreadsheetFormatterContext context) {
        Objects.requireNonNull(context, "context");

        return NO_TOKENS;
    }

    @Override
    public String toString() {
        return "expression";
    }
}

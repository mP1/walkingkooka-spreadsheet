/*
 * Copyright 2022 Miroslav Pokorny (github.com/mP1)
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

package walkingkooka.spreadsheet.expression;

import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.expression.Expression;

/**
 * A pair that contains a mapping from a scoped name within a LET statement to a value.
 * If the value is pure the value is cached.
 */
final class LocalLabelsSpreadsheetExpressionEvaluationContextNameAndValue {

    static LocalLabelsSpreadsheetExpressionEvaluationContextNameAndValue with(final SpreadsheetLabelName name,
                                                                              final Object value) {
        return new LocalLabelsSpreadsheetExpressionEvaluationContextNameAndValue(
                name,
                value
        );
    }

    private LocalLabelsSpreadsheetExpressionEvaluationContextNameAndValue(final SpreadsheetLabelName name,
                                                                          final Object value) {
        super();
        this.name = name;
        this.value = value;
    }

    final SpreadsheetLabelName name;

    /**
     * Returns the computed value for this parameter, caching the value if the value is pure.
     */
    Object value(final SpreadsheetExpressionEvaluationContext context) {
        Object value = this.value;

        if (!this.cached || !this.isPure(context)) {
            value = this.value;

            // value is NOT pure or cached, value must be evaluated.
            if (value instanceof SpreadsheetExpressionReference) {
                value = context.referenceOrFail((SpreadsheetExpressionReference) value);
            }
            if (value instanceof Expression) {
                value = context.evaluate((Expression) value);
            }

            if (this.isPure(context)) {
                this.cached = true;
            }
        }

        return value;
    }

    /**
     * Initially false, which means the value must be tested if evaluation or reference resolving is necessary.
     */
    private boolean cached; // false/true.

    /**
     * The initial value or the final value after evalluation, depends on the {@link #cached} flag.
     */
    Object value;

    /**
     * Tests if this value is pure, handling cases if its a value, reference or expression.
     * The result is cached for future calls.
     */
    boolean isPure(final SpreadsheetExpressionEvaluationContext context) {
        boolean purity = false;

        if (null == this.pure) {
            Object value = this.value;

            // resolve and ask resolved value/expression
            if (value instanceof SpreadsheetExpressionReference) {
                value = context.referenceOrFail((SpreadsheetExpressionReference) value);
            }

            if (value instanceof Expression) {
                final Expression expression = (Expression) value;
                purity = expression.isPure(context);
            }
        }

        this.pure = purity;
        return purity;
    }

    // tristate flag,
    // null = unknown
    // Boolean.FALSE = not pure
    // Boolean.TRUE = pure.
    private Boolean pure;

    @Override
    public String toString() {
        return this.name + "=" + CharSequences.quoteIfChars(this.value);
    }
}

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

import walkingkooka.tree.expression.ExpressionNode;

import java.util.Objects;

/**
 * A {@link SpreadsheetDataValidator} that validates the cell value against a formula. During validation,
 * it creates a {@link walkingkooka.tree.expression.ExpressionEvaluationContext} that returns the given {@link Object value}
 * for this cell.
 */
final class CustomFormulaSpreadsheetDataValidator extends SpreadsheetDataValidatorTemplate<Object> {

    /**
     * Creates a new {@link CustomFormulaSpreadsheetDataValidator}.
     */
    static CustomFormulaSpreadsheetDataValidator with(final ExpressionNode customFormula) {
        Objects.requireNonNull(customFormula, "customFormula");

        return new CustomFormulaSpreadsheetDataValidator(customFormula);
    }

    /**
     * Private ctor use factory
     */
    private CustomFormulaSpreadsheetDataValidator(final ExpressionNode customFormula) {
        super();
        this.customFormula = customFormula;
    }

    @Override
    public Class<Object> valueType() {
        return Object.class;
    }

    @Override
    boolean validate0(final Object value, final SpreadsheetDataValidatorContext context) {
        return this.validate1(this.context(value, context));
    }

    private SpreadsheetDataValidatorContext context(final Object value, final SpreadsheetDataValidatorContext context) {
        return SpreadsheetDataValidatorContexts.basic(context.cellReference(), value, context);
    }

    private boolean validate1(final SpreadsheetDataValidatorContext context) {
        return this.customFormula.toBoolean(context);
    }

    @Override
    public String toString() {
        return this.customFormula.toString();
    }

    private final ExpressionNode customFormula;
}

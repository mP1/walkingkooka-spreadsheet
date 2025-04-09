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

package walkingkooka.spreadsheet.validation;

import walkingkooka.Cast;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.validation.ValidatorContext;
import walkingkooka.validation.ValidatorContextDelegator;

import java.util.Objects;

final class BasicSpreadsheetValidatorContext implements SpreadsheetValidatorContext,
        ValidatorContextDelegator<SpreadsheetCellReference> {

    static SpreadsheetValidatorContext with(final ValidatorContext<SpreadsheetCellReference> context) {
        Objects.requireNonNull(context, "context");

        return context instanceof SpreadsheetValidatorContext ?
                (SpreadsheetValidatorContext) context :
                new BasicSpreadsheetValidatorContext(context);
    }

    private BasicSpreadsheetValidatorContext(final ValidatorContext<SpreadsheetCellReference> context) {
        super();

        this.context = context;
    }

    @Override
    public ValidatorContext<SpreadsheetCellReference> validatorContext() {
        return this.context;
    }

    private final ValidatorContext<SpreadsheetCellReference> context;

    @Override
    public SpreadsheetValidatorContext setValidationReference(final SpreadsheetCellReference cell) {
        return this.validatorContext()
                .validationReference()
                .equals(cell) ?
                this :
                new BasicSpreadsheetValidatorContext(
                        this.validatorContext()
                                .setValidationReference(
                                        Objects.requireNonNull(cell, "cell")
                                )
                );
    }

    @Override
    public SpreadsheetExpressionEvaluationContext expressionEvaluationContext() {
        return Cast.to(
                this.context.expressionEvaluationContext()
        );
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.context.toString();
    }
}

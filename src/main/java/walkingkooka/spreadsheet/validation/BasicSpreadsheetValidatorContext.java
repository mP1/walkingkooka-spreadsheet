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
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.validation.ValidatorContext;
import walkingkooka.validation.ValidatorContextDelegator;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

final class BasicSpreadsheetValidatorContext implements SpreadsheetValidatorContext,
    ValidatorContextDelegator<SpreadsheetExpressionReference> {

    static SpreadsheetValidatorContext with(final ValidatorContext<SpreadsheetExpressionReference> context) {
        Objects.requireNonNull(context, "context");

        return context instanceof SpreadsheetValidatorContext ?
            (SpreadsheetValidatorContext) context :
            new BasicSpreadsheetValidatorContext(context);
    }

    private BasicSpreadsheetValidatorContext(final ValidatorContext<SpreadsheetExpressionReference> context) {
        super();

        this.context = context;
    }

    @Override
    public SpreadsheetValidatorContext cloneEnvironment() {
        final ValidatorContext<SpreadsheetExpressionReference> context = this.context;
        final ValidatorContext<SpreadsheetExpressionReference> clone = context.cloneEnvironment();

        // Recreate only if different cloned EnvironmentContext, cloned environment should be equals
        return context == clone ?
            this :
            with(clone);
    }

    @Override
    public SpreadsheetValidatorContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        final ValidatorContext<SpreadsheetExpressionReference> before = this.context;
        final ValidatorContext<SpreadsheetExpressionReference> after = before.setEnvironmentContext(environmentContext);

        return before.equals(after) ?
            this :
            with(after);
    }

    @Override
    public SpreadsheetValidatorContext setLocale(final Locale locale) {
        this.context.setLocale(locale);
        return this;
    }

    @Override
    public SpreadsheetValidatorContext setUser(final Optional<EmailAddress> user) {
        this.context.setUser(user);
        return this;
    }

    @Override
    public <T> SpreadsheetValidatorContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                               final T value) {
        this.context.setEnvironmentValue(
            name,
            value
        );
        return this;
    }

    @Override
    public SpreadsheetValidatorContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
        this.context.removeEnvironmentValue(name);
        return this;
    }

    @Override
    public ValidatorContext<SpreadsheetExpressionReference> validatorContext() {
        return this.context;
    }

    private final ValidatorContext<SpreadsheetExpressionReference> context;

    @Override
    public SpreadsheetValidatorContext setValidationReference(final SpreadsheetExpressionReference cellOrLabel) {
        return this.validatorContext()
            .validationReference()
            .equalsIgnoreReferenceKind(cellOrLabel) ?
            this :
            new BasicSpreadsheetValidatorContext(
                this.validatorContext()
                    .setValidationReference(
                        Objects.requireNonNull(cellOrLabel, "cellOrLabel")
                    )
            );
    }

    @Override
    public SpreadsheetExpressionEvaluationContext expressionEvaluationContext(final Object value) {
        return Cast.to(
            this.context.expressionEvaluationContext(value)
        );
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.context.toString();
    }
}

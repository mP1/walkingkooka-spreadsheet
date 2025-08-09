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

import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.ConverterContextDelegator;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContextDelegator;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContexts;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.validation.Validator;
import walkingkooka.validation.ValidatorContext;
import walkingkooka.validation.provider.ValidatorSelector;

import java.time.LocalDateTime;
import java.util.Objects;

public final class SpreadsheetValidatorContextTestingTest implements SpreadsheetValidatorContextTesting<SpreadsheetValidatorContextTestingTest.TestSpreadsheetValidatorContext>,
    SpreadsheetMetadataTesting {

    @Override
    public TestSpreadsheetValidatorContext createContext() {
        return new TestSpreadsheetValidatorContext();
    }

    @Override
    public void testSetValidationReferenceSame() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEnvironmentValueWithNullFails() {
        throw new UnsupportedOperationException();
    }

    static class TestSpreadsheetValidatorContext implements SpreadsheetValidatorContext,
        ConverterContextDelegator,
        EnvironmentContextDelegator {

        TestSpreadsheetValidatorContext() {
            this(SpreadsheetSelection.A1);
        }

        TestSpreadsheetValidatorContext(final SpreadsheetExpressionReference reference) {
            this.reference = reference;
        }

        @Override
        public ConverterContext converterContext() {
            return SpreadsheetMetadataTesting.SPREADSHEET_FORMATTER_CONTEXT;
        }

        @Override
        public <T> TestSpreadsheetValidatorContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                                       final T value) {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(value, "value");
            throw new UnsupportedOperationException();
        }

        @Override
        public EnvironmentContext environmentContext() {
            return SpreadsheetMetadataTesting.ENVIRONMENT_CONTEXT;
        }

        @Override
        public LocalDateTime now() {
            return NOW.now();
        }

        @Override
        public SpreadsheetValidatorContext setValidationReference(final SpreadsheetExpressionReference cellOrLabel) {
            Objects.requireNonNull(cellOrLabel, "cellOrLabel");

            return new TestSpreadsheetValidatorContext() {

                @Override
                public SpreadsheetExpressionReference validationReference() {
                    return cellOrLabel;
                }
            };
        }

        @Override
        public SpreadsheetExpressionReference validationReference() {
            return this.reference;
        }

        private final SpreadsheetExpressionReference reference;

        @Override
        public Validator<SpreadsheetExpressionReference, ? super ValidatorContext<SpreadsheetExpressionReference>> validator(final ValidatorSelector validatorSelector) {
            Objects.requireNonNull(validatorSelector, "validatorSelector");

            throw new UnsupportedOperationException();
        }

        @Override
        public SpreadsheetExpressionEvaluationContext expressionEvaluationContext(final Object value) {
            return SpreadsheetExpressionEvaluationContexts.fake();
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }

    // class............................................................................................................

    @Override
    public Class<TestSpreadsheetValidatorContext> type() {
        return TestSpreadsheetValidatorContext.class;
    }
}

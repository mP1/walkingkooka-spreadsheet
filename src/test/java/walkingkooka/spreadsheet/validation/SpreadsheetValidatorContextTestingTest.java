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
import walkingkooka.environment.FakeEnvironmentContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.validation.ValidatorContext;

import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

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

        @Override
        public ConverterContext converterContext() {
            return SpreadsheetMetadataTesting.SPREADSHEET_FORMATTER_CONTEXT;
        }

        @Override
        public EnvironmentContext environmentContext() {
            return new FakeEnvironmentContext() {
                @Override
                public LocalDateTime now() {
                    return NOW.now();
                }

                @Override
                public Optional<EmailAddress> user() {
                    return Optional.of(USER);
                }
            };
        }

        @Override
        public LocalDateTime now() {
            return NOW.now();
        }

        @Override
        public ValidatorContext<SpreadsheetCellReference> setValidationReference(final SpreadsheetCellReference spreadsheetCellReference) {
            Objects.requireNonNull(spreadsheetCellReference, "spreadsheetCellReference");

            throw new UnsupportedOperationException();
        }

        @Override
        public SpreadsheetCellReference validationReference() {
            return SpreadsheetSelection.A1;
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }

    // DecimalNumberContextTesting......................................................................................

    private final static DecimalNumberContext DECIMAL_NUMBER_CONTEXT = SPREADSHEET_FORMATTER_CONTEXT;

    @Override
    public String currencySymbol() {
        return DECIMAL_NUMBER_CONTEXT.currencySymbol();
    }

    @Override
    public char decimalSeparator() {
        return DECIMAL_NUMBER_CONTEXT.decimalSeparator();
    }

    @Override
    public String exponentSymbol() {
        return DECIMAL_NUMBER_CONTEXT.exponentSymbol();
    }

    @Override
    public char groupSeparator() {
        return DECIMAL_NUMBER_CONTEXT.groupSeparator();
    }

    @Override
    public MathContext mathContext() {
        return DECIMAL_NUMBER_CONTEXT.mathContext();
    }

    @Override
    public char negativeSign() {
        return DECIMAL_NUMBER_CONTEXT.negativeSign();
    }

    @Override
    public char percentageSymbol() {
        return DECIMAL_NUMBER_CONTEXT.percentageSymbol();
    }

    @Override
    public char positiveSign() {
        return DECIMAL_NUMBER_CONTEXT.positiveSign();
    }

    // class............................................................................................................

    @Override
    public Class<TestSpreadsheetValidatorContext> type() {
        return TestSpreadsheetValidatorContext.class;
    }
}

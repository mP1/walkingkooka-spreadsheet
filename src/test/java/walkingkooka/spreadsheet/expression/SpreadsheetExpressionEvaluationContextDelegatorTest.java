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

package walkingkooka.spreadsheet.expression;

import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.net.Url;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoaders;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.validation.SpreadsheetValidatorContext;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;
import walkingkooka.validation.form.FakeFormHandlerContext;
import walkingkooka.validation.form.FormField;

import java.math.MathContext;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public final class SpreadsheetExpressionEvaluationContextDelegatorTest implements SpreadsheetExpressionEvaluationContextTesting<SpreadsheetExpressionEvaluationContextDelegatorTest.TestSpreadsheetExpressionEvaluationContextDelegator>,
    SpreadsheetMetadataTesting,
    DecimalNumberContextDelegator {

    @Override
    public void testEvaluateExpressionUnknownFunctionNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testLoadCellWithNullCellFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testLoadCellRangeWithNullRangeFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testLoadLabelWithNullLabelFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testReferenceWithNullReferenceFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetCellWithNullCellFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetCellWithSame() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetLocaleWithDifferent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSpreadsheetFormatterContextWithNullFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetSpreadsheetMetadataWithDifferentIdFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TestSpreadsheetExpressionEvaluationContextDelegator createContext() {
        return new TestSpreadsheetExpressionEvaluationContextDelegator();
    }

    // DecimalNumberContext.............................................................................................

    @Override
    public MathContext mathContext() {
        return new TestSpreadsheetExpressionEvaluationContextDelegator()
            .mathContext();
    }

    // DecimalNumberContextDelegator....................................................................................

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return new TestSpreadsheetExpressionEvaluationContextDelegator();
    }

    // class............................................................................................................

    @Override
    public Class<TestSpreadsheetExpressionEvaluationContextDelegator> type() {
        return TestSpreadsheetExpressionEvaluationContextDelegator.class;
    }

    final static class TestSpreadsheetExpressionEvaluationContextDelegator implements SpreadsheetExpressionEvaluationContextDelegator {

        @Override
        public SpreadsheetExpressionEvaluationContextDelegator setCell(final Optional<walkingkooka.spreadsheet.SpreadsheetCell> cell) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<SpreadsheetCell> cell() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<SpreadsheetCell> loadCellRange(final SpreadsheetCellRangeReference range) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Optional<Object>> reference(final ExpressionReference reference) {
            return this.expressionEvaluationContext().reference(reference);
        }

        @Override
        public SpreadsheetExpressionEvaluationContext enterScope(final Function<ExpressionReference, Optional<Optional<Object>>> scoped) {
            Objects.requireNonNull(scoped, "scoped");

            return new TestSpreadsheetExpressionEvaluationContextDelegator();
        }

        @Override
        public SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext() {
            return SpreadsheetExpressionEvaluationContexts.basic(
                Optional.empty(), // cell
                SpreadsheetExpressionReferenceLoaders.fake(),
                Url.parseAbsolute("https://example.com"),
                METADATA_EN_AU,
                SpreadsheetStoreRepositories.fake(),
                SPREADSHEET_FORMULA_CONVERTER_CONTEXT,
                ENVIRONMENT_CONTEXT,
                ((Optional<SpreadsheetCell> c) -> {
                    throw new UnsupportedOperationException();
                }),
                new FakeFormHandlerContext<>() {
                    @Override
                    public Optional<Object> loadFormFieldValue(final SpreadsheetExpressionReference reference) {
                        Objects.requireNonNull(reference, "reference");
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public SpreadsheetDelta saveFormFieldValues(final List<FormField<SpreadsheetExpressionReference>> fields) {
                        Objects.requireNonNull(fields, "fields");
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
                        Objects.requireNonNull(name, "name");
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public Optional<EmailAddress> user() {
                        return Optional.empty();
                    }

                    @Override
                    public SpreadsheetValidatorContext validatorContext(final SpreadsheetExpressionReference reference) {
                        Objects.requireNonNull(reference, "reference");

                        throw new UnsupportedOperationException();
                    }
                },
                TERMINAL_CONTEXT,
                EXPRESSION_FUNCTION_PROVIDER,
                PROVIDER_CONTEXT
            );
        }

        @Override
        public SpreadsheetFormatterContext spreadsheetFormatterContext(final Optional<SpreadsheetCell> cell) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName labelName) {
            Objects.requireNonNull(labelName, "labelName");
            throw new UnsupportedOperationException();
        }

        @Override
        public SpreadsheetExpressionEvaluationContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor processor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SpreadsheetExpressionEvaluationContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public TestSpreadsheetExpressionEvaluationContextDelegator cloneEnvironment() {
            throw new UnsupportedOperationException();
        }

        @Override
        public TestSpreadsheetExpressionEvaluationContextDelegator setEnvironmentContext(final EnvironmentContext environmentContext) {
            Objects.requireNonNull(environmentContext, "environmentContext");

            return new TestSpreadsheetExpressionEvaluationContextDelegator();
        }

        @Override
        public <T> TestSpreadsheetExpressionEvaluationContextDelegator setEnvironmentValue(final EnvironmentValueName<T> name,
                                                                                           final T value) {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(value, "value");
            throw new UnsupportedOperationException();
        }

        @Override
        public TestSpreadsheetExpressionEvaluationContextDelegator removeEnvironmentValue(final EnvironmentValueName<?> name) {
            Objects.requireNonNull(name, "name");
            throw new UnsupportedOperationException();
        }

        @Override
        public LineEnding lineEnding() {
            return LineEnding.NL;
        }

        @Override
        public SpreadsheetExpressionEvaluationContext setLineEnding(final LineEnding lineEnding) {
            Objects.requireNonNull(lineEnding, "lineEnding");
            throw new UnsupportedOperationException();
        }
        
        @Override
        public SpreadsheetExpressionEvaluationContext setLocale(final Locale locale) {
            Objects.requireNonNull(locale, "locale");
            throw new UnsupportedOperationException();
        }

        @Override
        public SpreadsheetExpressionEvaluationContext setUser(final Optional<EmailAddress> user) {
            Objects.requireNonNull(user, "user");
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
}

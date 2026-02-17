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

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringBuilder;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.datetime.HasNow;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.environment.EnvironmentValueWatcher;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.storage.SpreadsheetStorageContext;
import walkingkooka.spreadsheet.validation.SpreadsheetValidatorContext;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StorageContext;
import walkingkooka.storage.StorageContexts;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageValueInfo;
import walkingkooka.storage.Storages;
import walkingkooka.template.TemplateValueName;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterKind;
import walkingkooka.tree.expression.function.FakeExpressionFunction;
import walkingkooka.validation.form.FormField;

import java.math.MathContext;
import java.text.DateFormatSymbols;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetExpressionEvaluationContextLocalReferencesTest implements SpreadsheetExpressionEvaluationContextTesting<SpreadsheetExpressionEvaluationContextLocalReferences>,
    HashCodeEqualsDefinedTesting2<SpreadsheetExpressionEvaluationContextLocalReferences>,
    ToStringTesting<SpreadsheetExpressionEvaluationContextLocalReferences> {

    private final static String NAME = "Name1234";

    private final static SpreadsheetLabelName LABEL = SpreadsheetSelection.labelName(NAME);
    private final static String LABEL_LOCAL_VALUE = "LabelLocalValue";

    private final static TemplateValueName TEMPLATE_VALUE_NAME = TemplateValueName.with("TemplateValue");
    private final static String TEMPLATE_LOCAL_VALUE = "TemplateLocalValue123";

    private final static MathContext MATH_CONTEXT = MathContext.DECIMAL128;

    private final static String CURRENCY_SYMBOL = "AUD";
    private final static char DECIMAL_SEPARATOR = '/';
    private final static String EXPONENT_SYMBOL = "HELLO";
    private final static char GROUP_SEPARATOR = '/';
    private final static String INFINITY_SYMBOL = "Infinity!";
    private final static char MONETARY_DECIMAL_SEPARATOR = '/';
    private final static String NAN_SYMBOL = "Nan!";
    private final static char NEGATIVE_SYMBOL = 'N';
    private final static char PERCENT_SYMBOL = 'R';
    private final static char PERMILL_SYMBOL = '^';
    private final static char POSITIVE_SYMBOL = 'P';
    private final static char ZERO = '0';

    private final Function<ExpressionReference, Optional<Optional<Object>>> REFERENCE_TO_VALUES = new Function<>() {
        @Override
        public Optional<Optional<Object>> apply(final ExpressionReference reference) {
            return this.map.containsKey(reference) ?
                Optional.of(
                    Optional.ofNullable(this.map.get(reference))
                ) :
                Optional.empty();
        }

        @Override
        public String toString() {
            return ToStringBuilder.empty()
                .value(this.map)
                .build();
        }

        private final Map<ExpressionReference, Object> map = Maps.of(
            LABEL,
            LABEL_LOCAL_VALUE,
            TEMPLATE_VALUE_NAME,
            TEMPLATE_LOCAL_VALUE
        );
    };

    private final static Locale LOCALE = Locale.ENGLISH;

    private final static DateTimeContext DATE_TIME_CONTEXT = DateTimeContexts.basic(
        DateTimeSymbols.fromDateFormatSymbols(
            new DateFormatSymbols(LOCALE)
        ),
        LOCALE,
        1900,
        20,
        LocalDateTime::now
    );

    private final static LocaleContext LOCALE_CONTEXT = LocaleContexts.jre(LOCALE);

    private final static HasNow HAS_NOW = () -> LocalDateTime.MIN;

    private final static Storage<SpreadsheetStorageContext> STORAGE = Storages.fake();

    @Test
    public void testWithNullReferenceToValuesFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExpressionEvaluationContextLocalReferences.with(
                null,
                SpreadsheetExpressionEvaluationContexts.fake()
            )
        );
    }

    @Test
    public void testWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExpressionEvaluationContextLocalReferences.with(
                REFERENCE_TO_VALUES,
                null
            )
        );
    }

    @Override
    public void testEvaluateExpressionUnknownFunctionNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testIsPureNullNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testParseExpressionNullFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testParseValueOrExpressionNullFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testEvaluateFunctionContextReferenceWithLocalLabel() {
        this.checkEquals(
            LABEL_LOCAL_VALUE,
            this.createContext()
                .evaluateFunction(
                    new FakeExpressionFunction<>() {

                        @Override
                        public Optional<ExpressionFunctionName> name() {
                            return ExpressionFunction.ANONYMOUS_NAME;
                        }

                        @Override
                        public List<ExpressionFunctionParameter<?>> parameters(final int count) {
                            return Lists.empty();
                        }

                        @Override
                        public Object apply(final List<Object> values,
                                            final ExpressionEvaluationContext context) {
                            return context.referenceOrFail(LABEL);
                        }
                    },
                    ExpressionFunction.NO_PARAMETER_VALUES
                )
        );
    }

    @Test
    public void testEvaluateFunctionContextReferenceWithLocalTemplateValue() {
        this.checkEquals(
            TEMPLATE_LOCAL_VALUE,
            this.createContext()
                .evaluateFunction(
                    new FakeExpressionFunction<>() {

                        @Override
                        public Optional<ExpressionFunctionName> name() {
                            return ExpressionFunction.ANONYMOUS_NAME;
                        }

                        @Override
                        public List<ExpressionFunctionParameter<?>> parameters(final int count) {
                            return Lists.empty();
                        }

                        @Override
                        public Object apply(final List<Object> values,
                                            final ExpressionEvaluationContext context) {
                            return context.referenceOrFail(TEMPLATE_VALUE_NAME);
                        }
                    },
                    ExpressionFunction.NO_PARAMETER_VALUES
                )
        );
    }

    @Test
    public void testEvaluateFunctionParameterWithLocalLabel() {
        this.checkEquals(
            LABEL_LOCAL_VALUE,
            this.createContext()
                .evaluateFunction(
                    new FakeExpressionFunction<>() {

                        @Override
                        public Optional<ExpressionFunctionName> name() {
                            return ExpressionFunction.ANONYMOUS_NAME;
                        }

                        @Override
                        public List<ExpressionFunctionParameter<?>> parameters(final int count) {
                            return Lists.of(PARAMETER);
                        }

                        @Override
                        public Object apply(final List<Object> values,
                                            final ExpressionEvaluationContext context) {
                            return this.parameters(1)
                                .get(0)
                                .getOrFail(values, 0);
                        }

                        private final ExpressionFunctionParameter<String> PARAMETER = ExpressionFunctionParameter.STRING.setKinds(
                            ExpressionFunctionParameterKind.EVALUATE_RESOLVE_REFERENCES
                        );
                    },
                    Lists.of(
                        Expression.reference(LABEL)
                    )
                )
        );
    }

    @Test
    public void testFunctionWithNamedValueFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .expressionFunction(
                    SpreadsheetExpressionFunctions.name(NAME)
                )
        );
        this.checkEquals(
            "Function name Name1234 is a parameter and not an actual function",
            thrown.getMessage()
        );
    }

    @Test
    public void testIsPureWithNamedValueFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext().isPure(
                SpreadsheetExpressionFunctions.name(NAME)
            )
        );
        this.checkEquals(
            "Function name Name1234 is a parameter and not an actual function",
            thrown.getMessage()
        );
    }

    // reference........................................................................................................

    @Test
    public void testReferenceWithLocalLabelNonNullValue() {
        this.referenceAndCheck3(
            REFERENCE_TO_VALUES,
            LABEL,
            Optional.of(LABEL_LOCAL_VALUE)
        );
    }

    @Test
    public void testReferenceWithLocalLabelNullValue() {
        this.referenceAndCheck3(
            (r) -> {
                this.checkEquals(LABEL, r);
                return Optional.of(Optional.empty());
            },
            LABEL,
            Optional.empty()
        );
    }

    @Test
    public void testReferenceWithLocalLabelAbsent() {
        final Optional<Optional<Object>> value = Optional.of(
            Optional.of(LABEL_LOCAL_VALUE)
        );

        this.referenceAndCheck2(
            SpreadsheetExpressionEvaluationContextLocalReferences.with(
                (r) -> Optional.empty(),
                new FakeSpreadsheetExpressionEvaluationContext() {
                    @Override
                    public Optional<Optional<Object>> reference(final ExpressionReference reference) {
                        checkEquals(LABEL, reference, "reference");
                        return value;
                    }
                }
            ),
            LABEL,
            value
        );
    }

    private void referenceAndCheck3(final Function<ExpressionReference, Optional<Optional<Object>>> referenceToValues,
                                    final ExpressionReference reference,
                                    final Optional<Object> expected) {
        this.referenceAndCheck(
            SpreadsheetExpressionEvaluationContextLocalReferences.with(
                referenceToValues,
                SpreadsheetExpressionEvaluationContexts.fake()
            ),
            reference,
            expected
        );
    }

    // resolveIfLabelOrFail.............................................................................................

    @Test
    public void testResolveIfLabelLocalLabelFails() {
        final SpreadsheetExpressionEvaluationContextLocalReferences context = this.createContext();

        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> context.resolveIfLabelOrFail(SpreadsheetSelection.labelName(NAME))
        );

        this.checkEquals(
            "Label Name1234 has a value",
            thrown.getMessage()
        );
    }

    @Test
    public void testResolveLocalLabelFails() {
        final SpreadsheetExpressionEvaluationContextLocalReferences context = this.createContext();

        this.checkEquals(
            Optional.of(
                Optional.of(LABEL_LOCAL_VALUE)
            ),
            context.reference(SpreadsheetSelection.labelName(NAME))
        );
    }

    // evaluate.........................................................................................................

    @Override
    public void testEvaluateWithEmptyStringReturnsError() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEvaluateWithWhitespaceStringReturnsError() {
        throw new UnsupportedOperationException();
    }

    // testParseExpression..............................................................................................

    @Override
    public void testParseExpressionWithEmptyStringFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testParseExpressionWithOnlyWhitespaceStringFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testParseExpressionWithOnlyWhitespaceStringFails2() {
        throw new UnsupportedOperationException();
    }

    // testParseValueOrExpression.......................................................................................

    @Override
    public void testParseValueOrExpressionWithEmptyStringFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testParseValueOrExpressionWithOnlyWhitespaceStringFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testParseValueOrExpressionWithOnlyWhitespaceStringFails2() {
        throw new UnsupportedOperationException();
    }

    // testSetEnvironmentContext........................................................................................
    
    @Override
    public SpreadsheetExpressionEvaluationContextLocalReferences createContext() {
        final EnvironmentContext environmentContext = EnvironmentContexts.map(
            EnvironmentContexts.empty(
                Currency.getInstance("AUD"),
                INDENTATION,
                LineEnding.NL,
                LOCALE_CONTEXT.locale(),
                HAS_NOW,
                Optional.of(
                    EmailAddress.parse("user@example.com")
                )
            )
        );
        environmentContext.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SERVER_URL,
            Url.parseAbsolute("https://example.com")
        );

        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.basic(
            STORAGE,
            environmentContext
        );
        spreadsheetEnvironmentContext.setSpreadsheetId(
            Optional.of(
                SpreadsheetId.with(1)
            )
        );

        return this.createContext(spreadsheetEnvironmentContext);
    }

    private SpreadsheetExpressionEvaluationContextLocalReferences createContext(final SpreadsheetEnvironmentContext environmentContext) {
        return SpreadsheetExpressionEvaluationContextLocalReferences.with(
            REFERENCE_TO_VALUES,
            new TestSpreadsheetExpressionEvaluationContext(environmentContext)
        );
    }

    final static class TestSpreadsheetExpressionEvaluationContext extends FakeSpreadsheetExpressionEvaluationContext {

        TestSpreadsheetExpressionEvaluationContext(final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext) {
            this.spreadsheetEnvironmentContext = spreadsheetEnvironmentContext;
        }

        @Override
        public Optional<SpreadsheetCell> cell() {
            return Optional.empty();
        }

        @Override
        public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell) {
            Objects.requireNonNull(cell, "cell");

            throw new UnsupportedOperationException();
        }

        @Override
        public Set<SpreadsheetCell> loadCellRange(final SpreadsheetCellRangeReference range) {
            Objects.requireNonNull(range, "range");

            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName labelName) {
            Objects.requireNonNull(labelName, "labelName");

            throw new UnsupportedOperationException();
        }


        @Override
        public List<String> ampms() {
            return DATE_TIME_CONTEXT.ampms();
        }

        @Override
        public String ampm(int hourOfDay) {
            return DATE_TIME_CONTEXT.ampm(hourOfDay);
        }

        @Override
        public List<String> monthNames() {
            return DATE_TIME_CONTEXT.monthNames();
        }

        @Override
        public String monthName(int month) {
            return DATE_TIME_CONTEXT.monthName(month);
        }

        @Override
        public List<String> monthNameAbbreviations() {
            return DATE_TIME_CONTEXT.monthNameAbbreviations();
        }

        @Override
        public String monthNameAbbreviation(int month) {
            return DATE_TIME_CONTEXT.monthNameAbbreviation(month);
        }

        @Override
        public List<String> weekDayNames() {
            return DATE_TIME_CONTEXT.weekDayNames();
        }

        @Override
        public String weekDayName(int day) {
            return DATE_TIME_CONTEXT.weekDayName(day);
        }

        @Override
        public List<String> weekDayNameAbbreviations() {
            return DATE_TIME_CONTEXT.weekDayNameAbbreviations();
        }

        @Override
        public String weekDayNameAbbreviation(int day) {
            return DATE_TIME_CONTEXT.weekDayNameAbbreviation(day);
        }

        @Override
        public int defaultYear() {
            return DATE_TIME_CONTEXT.defaultYear();
        }

        @Override
        public int twoDigitYear() {
            return DATE_TIME_CONTEXT.twoDigitYear();
        }

        @Override
        public int twoToFourDigitYear(int year) {
            return DATE_TIME_CONTEXT.twoToFourDigitYear(year);
        }

        @Override
        public DateTimeSymbols dateTimeSymbols() {
            return DATE_TIME_CONTEXT
                .dateTimeSymbols();
        }

        @Override
        public int decimalNumberDigitCount() {
            return DEFAULT_NUMBER_DIGIT_COUNT;
        }

        @Override
        public String currencySymbol() {
            return CURRENCY_SYMBOL;
        }

        @Override
        public char decimalSeparator() {
            return DECIMAL_SEPARATOR;
        }

        @Override
        public String exponentSymbol() {
            return EXPONENT_SYMBOL;
        }

        @Override
        public char groupSeparator() {
            return GROUP_SEPARATOR;
        }

        @Override
        public String infinitySymbol() {
            return INFINITY_SYMBOL;
        }

        @Override
        public MathContext mathContext() {
            return MATH_CONTEXT;
        }

        @Override
        public char monetaryDecimalSeparator() {
            return MONETARY_DECIMAL_SEPARATOR;
        }

        @Override
        public String nanSymbol() {
            return NAN_SYMBOL;
        }

        @Override
        public char negativeSign() {
            return NEGATIVE_SYMBOL;
        }

        @Override
        public char percentSymbol() {
            return PERCENT_SYMBOL;
        }

        @Override
        public char permillSymbol() {
            return PERMILL_SYMBOL;
        }

        @Override
        public char positiveSign() {
            return POSITIVE_SYMBOL;
        }

        @Override
        public char zeroDigit() {
            return ZERO;
        }

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
        public StoragePath parseStoragePath(final String text) {
            return StoragePath.parseSpecial(
                text,
                this // HasUserDirectories
            );
        }

        @Override
        public SpreadsheetExpressionEvaluationContext setEnvironmentContext(final EnvironmentContext environmentContext) {
            Objects.requireNonNull(environmentContext, "environmentContext");

            return this.spreadsheetEnvironmentContext.equals(environmentContext) ?
                this :
                new TestSpreadsheetExpressionEvaluationContext(
                    this.spreadsheetEnvironmentContext.setEnvironmentContext(environmentContext)
                );
        }

        private final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext;

        @Override
        public Optional<StoragePath> currentWorkingDirectory() {
            return this.spreadsheetEnvironmentContext.currentWorkingDirectory();
        }

        @Override
        public void setCurrentWorkingDirectory(final Optional<StoragePath> currentWorkingDirectory) {
            this.spreadsheetEnvironmentContext.setCurrentWorkingDirectory(currentWorkingDirectory);
        }
        
        @Override
        public Indentation indentation() {
            return this.spreadsheetEnvironmentContext.indentation();
        }

        @Override
        public void setIndentation(final Indentation indentation) {
            this.spreadsheetEnvironmentContext.setIndentation(indentation);
        }
        
        @Override
        public LineEnding lineEnding() {
            return this.spreadsheetEnvironmentContext.lineEnding();
        }

        @Override
        public void setLineEnding(final LineEnding lineEnding) {
            this.spreadsheetEnvironmentContext.setLineEnding(lineEnding);
        }

        @Override
        public Locale locale() {
            return this.spreadsheetEnvironmentContext.locale();
        }

        @Override
        public void setLocale(final Locale locale) {
            this.spreadsheetEnvironmentContext.setLocale(locale);
        }

        @Override
        public LocalDateTime now() {
            return this.spreadsheetEnvironmentContext.now();
        }

        @Override
        public <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
            return this.spreadsheetEnvironmentContext.environmentValue(name);
        }

        @Override
        public AbsoluteUrl serverUrl() {
            return this.spreadsheetEnvironmentContext.serverUrl();
        }

        @Override
        public Optional<SpreadsheetId> spreadsheetId() {
            return this.spreadsheetEnvironmentContext.spreadsheetId();
        }

        @Override
        public void setSpreadsheetId(final Optional<SpreadsheetId> spreadsheetId) {
            this.spreadsheetEnvironmentContext.setSpreadsheetId(spreadsheetId);
        }

        @Override
        public Optional<EmailAddress> user() {
            return this.spreadsheetEnvironmentContext.user();
        }

        @Override
        public void setUser(final Optional<EmailAddress> user) {
            this.spreadsheetEnvironmentContext.setUser(user);
        }

        @Override
        public SpreadsheetValidatorContext validatorContext(final SpreadsheetExpressionReference reference) {
            Objects.requireNonNull(reference, "reference");

            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Currency> currencyForLocale(final Locale locale) {
            Objects.requireNonNull(locale, "locale");
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<DateTimeSymbols> dateTimeSymbolsForLocale(final Locale locale) {
            return LOCALE_CONTEXT.dateTimeSymbolsForLocale(locale);
        }

        @Override
        public Optional<DecimalNumberSymbols> decimalNumberSymbolsForLocale(final Locale locale) {
            return LOCALE_CONTEXT.decimalNumberSymbolsForLocale(locale);
        }

        @Override
        public SpreadsheetFormatterContext spreadsheetFormatterContext(final Optional<SpreadsheetCell> cell) {
            Objects.requireNonNull(cell, "cell");
            return super.spreadsheetFormatterContext(cell);
        }

        @Override
        public <T> void setEnvironmentValue(final EnvironmentValueName<T> name,
                                            final T value) {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(value, "value");
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeEnvironmentValue(final EnvironmentValueName<?> name) {
            Objects.requireNonNull(name, "name");
            throw new UnsupportedOperationException();
        }

        @Override
        public Runnable addEventValueWatcher(final EnvironmentValueWatcher watcher) {
            Objects.requireNonNull(watcher, "watcher");
            throw new UnsupportedOperationException();
        }

        @Override
        public Runnable addEventValueWatcherOnce(final EnvironmentValueWatcher watcher) {
            Objects.requireNonNull(watcher, "watcher");
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<StorageValue> loadStorage(final StoragePath path) {
            return this.storage.load(
                path,
                StorageContexts.fake()
            );
        }

        @Override
        public StorageValue saveStorage(final StorageValue value) {
            return this.storage.save(
                value,
                StorageContexts.fake()
            );
        }

        @Override
        public void deleteStorage(final StoragePath path) {
            this.storage.delete(
                path,
                StorageContexts.fake()
            );
        }

        @Override
        public List<StorageValueInfo> listStorage(final StoragePath parent,
                                                  final int offset,
                                                  final int count) {
            return this.storage.list(
                parent,
                offset,
                count,
                StorageContexts.fake()
            );
        }

        private final Storage<StorageContext> storage = Storages.tree();
    }

    @Override
    public String currencySymbol() {
        return CURRENCY_SYMBOL;
    }

    @Override
    public int decimalNumberDigitCount() {
        return DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT;
    }

    @Override
    public char decimalSeparator() {
        return DECIMAL_SEPARATOR;
    }

    @Override
    public String exponentSymbol() {
        return EXPONENT_SYMBOL;
    }

    @Override
    public char groupSeparator() {
        return GROUP_SEPARATOR;
    }

    @Override
    public String infinitySymbol() {
        return INFINITY_SYMBOL;
    }

    @Override
    public MathContext mathContext() {
        return MATH_CONTEXT;
    }

    @Override
    public char monetaryDecimalSeparator() {
        return MONETARY_DECIMAL_SEPARATOR;
    }

    @Override
    public String nanSymbol() {
        return NAN_SYMBOL;
    }

    @Override
    public char negativeSign() {
        return NEGATIVE_SYMBOL;
    }

    @Override
    public char percentSymbol() {
        return PERCENT_SYMBOL;
    }

    @Override
    public char permillSymbol() {
        return PERMILL_SYMBOL;
    }

    @Override
    public char positiveSign() {
        return POSITIVE_SYMBOL;
    }

    @Override
    public char zeroDigit() {
        return ZERO;
    }

    @Override
    public void testFindByLocaleTextWithNullTextFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testFindByLocaleTextWithNegativeOffsetFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testFindByLocaleTextWithInvalidCountFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetCurrencyWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetIndentationWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetLineEndingWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testLocaleTextWithNullFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetLocaleWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetTimeOffsetWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetUserWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetSpreadsheetMetadataWithNullFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetSpreadsheetMetadataWithDifferentIdFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testResolveLabelWithNullFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testStringEqualsCaseSensitivity() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testRemoveEnvironmentValueWithNowFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetEnvironmentValueWithNowFails() {
        throw new UnsupportedOperationException();
    }

    // hashEquals/Object................................................................................................

    @Test
    @Override
    public void testEquals() {
        final SpreadsheetExpressionEvaluationContext context = SpreadsheetExpressionEvaluationContexts.fake();

        this.checkEquals(
            SpreadsheetExpressionEvaluationContextLocalReferences.with(
                REFERENCE_TO_VALUES,
                context
            ),
            SpreadsheetExpressionEvaluationContextLocalReferences.with(
                REFERENCE_TO_VALUES,
                context
            )
        );
    }

    @Test
    public void testEqualsDifferentReferenceToValues() {
        final SpreadsheetExpressionEvaluationContext context = SpreadsheetExpressionEvaluationContexts.fake();

        this.checkNotEquals(
            SpreadsheetExpressionEvaluationContextLocalReferences.with(
                REFERENCE_TO_VALUES,
                context
            ),
            SpreadsheetExpressionEvaluationContextLocalReferences.with(
                (l) -> {
                    throw new UnsupportedOperationException();
                },
                context
            )
        );
    }

    @Test
    public void testEqualsDifferentSpreadsheetExpressionEvaluationContext() {
        this.checkNotEquals(
            SpreadsheetExpressionEvaluationContextLocalReferences.with(
                REFERENCE_TO_VALUES,
                SpreadsheetExpressionEvaluationContexts.fake()
            ),
            SpreadsheetExpressionEvaluationContextLocalReferences.with(
                REFERENCE_TO_VALUES,
                SpreadsheetExpressionEvaluationContexts.fake()
            )
        );
    }

    @Override
    public SpreadsheetExpressionEvaluationContextLocalReferences createObject() {
        return SpreadsheetExpressionEvaluationContextLocalReferences.with(
            REFERENCE_TO_VALUES,
            SpreadsheetExpressionEvaluationContexts.fake()
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final SpreadsheetExpressionEvaluationContext context = SpreadsheetExpressionEvaluationContexts.fake();

        this.toStringAndCheck(
            SpreadsheetExpressionEvaluationContextLocalReferences.with(
                REFERENCE_TO_VALUES,
                context
            ),
            "Name1234=\"LabelLocalValue\", TemplateValue=\"TemplateLocalValue123\" " + context
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetExpressionEvaluationContextLocalReferences> type() {
        return SpreadsheetExpressionEvaluationContextLocalReferences.class;
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
}

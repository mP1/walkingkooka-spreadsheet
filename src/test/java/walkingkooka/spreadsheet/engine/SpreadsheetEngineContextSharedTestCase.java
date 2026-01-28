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

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.FakeDateTimeContext;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.net.Url;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContexts;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.provider.SpreadsheetProviders;
import walkingkooka.spreadsheet.reference.FakeSpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoaders;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.storage.StoragePath;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ValueExpression;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterKind;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterName;
import walkingkooka.tree.expression.function.FakeExpressionFunction;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionInfoSet;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionSelector;

import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

public abstract class SpreadsheetEngineContextSharedTestCase<C extends SpreadsheetEngineContextShared> implements SpreadsheetEngineContextTesting<C>,
    SpreadsheetMetadataTesting,
    HashCodeEqualsDefinedTesting2<C> {

    final static String CURRENCY = "CURR";
    final static char DECIMAL = '.';
    final static String EXPONENT = "e";
    final static char GROUP_SEPARATOR = ',';
    final static String INFINITY = "Infinity!";
    final static char MINUS = '!';
    final static char MONETARY_DECIMAL_SEPARATOR = ':';
    final static String NAN = "Nan!";
    final static char PERCENT = '#';
    final static char PERMILL_SYMBOL = '^';
    final static char PLUS = '@';
    final static char ZERO_DIGIT = '0';

    final static char VALUE_SEPARATOR = ',';
    final static int WIDTH = 1;

    final static String TEST_CONTEXT_LOADCELL = "test-context-loadCell";

    final static String TEST_CONTEXT_SERVER_URL = "test-context-serverUrl";

    final static String TEST_CONTEXT_SPREADSHEET_METADATA = "test-context-spreadsheet-metadata";

    final static SpreadsheetId SPREADSHEET_ID = SpreadsheetId.with(123);

    final static StoragePath CURRENT_WORKING_DIRECTORY = StoragePath.parse("/current1/working2/directory3");

    final static SpreadsheetMetadata METADATA = SpreadsheetMetadata.NON_LOCALE_DEFAULTS
        .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
        .loadFromLocale(
            LocaleContexts.jre(LOCALE)
        ).set(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SPREADSHEET_ID)
        .set(SpreadsheetMetadataPropertyName.DATE_TIME_PARSER, SpreadsheetPattern.parseDateTimeParsePattern("dd/mm/yyyy hh:mm").spreadsheetParserSelector())
        .set(SpreadsheetMetadataPropertyName.TEXT_FORMATTER, SpreadsheetPattern.parseTextFormatPattern("@").spreadsheetFormatterSelector())
        .set(
            SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_SYMBOLS,
            DecimalNumberSymbols.with(
                MINUS,
                PLUS,
                ZERO_DIGIT,
                CURRENCY,
                DECIMAL,
                EXPONENT,
                GROUP_SEPARATOR,
                INFINITY,
                MONETARY_DECIMAL_SEPARATOR,
                NAN,
                PERCENT,
                PERMILL_SYMBOL
            )
        ).set(SpreadsheetMetadataPropertyName.CELL_CHARACTER_WIDTH, WIDTH)
        .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, EXPRESSION_NUMBER_KIND)
        .set(SpreadsheetMetadataPropertyName.VALUE_SEPARATOR, VALUE_SEPARATOR)
        .set(SpreadsheetMetadataPropertyName.FORMULA_FUNCTIONS, SpreadsheetExpressionFunctions.parseAliasSet("xyz, " + TEST_CONTEXT_LOADCELL + ", " + TEST_CONTEXT_SERVER_URL + ", " + TEST_CONTEXT_SPREADSHEET_METADATA));

    final static SpreadsheetExpressionReferenceLoader LOADER = new FakeSpreadsheetExpressionReferenceLoader() {
        @Override
        public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell,
                                                  final SpreadsheetExpressionEvaluationContext context) {
            if (cell.equalsIgnoreReferenceKind(LOAD_CELL_REFERENCE)) {
                return Optional.of(
                    LOAD_CELL_REFERENCE.setFormula(
                        SpreadsheetFormula.EMPTY.setValue(
                            Optional.of(LOAD_CELL_VALUE)
                        )
                    )
                );
            }
            return Optional.empty();
        }
    };
    
    final static SpreadsheetProvider SPREADSHEET_PROVIDER = SpreadsheetProviders.basic(
        CONVERTER_PROVIDER,
        new ExpressionFunctionProvider<>() {

            @Override
            public ExpressionFunction<?, SpreadsheetExpressionEvaluationContext> expressionFunction(final ExpressionFunctionSelector selector,
                                                                                                    final ProviderContext context) {
                Objects.requireNonNull(selector, "selector");
                Objects.requireNonNull(context, "context");

                return selector.evaluateValueText(
                    this,
                    context
                );
            }

            @Override
            public ExpressionFunction<?, SpreadsheetExpressionEvaluationContext> expressionFunction(final ExpressionFunctionName name,
                                                                                                    final List<?> values,
                                                                                                    final ProviderContext context) {
                Objects.requireNonNull(name, "name");
                Objects.requireNonNull(values, "values");
                Objects.requireNonNull(context, "context");

                switch (name.value()) {
                    case "xyz":
                        return new FakeExpressionFunction<>() {

                            @Override
                            public Optional<ExpressionFunctionName> name() {
                                return Optional.of(
                                    ExpressionFunctionName.with("xyz")
                                );
                            }

                            @Override
                            public Object apply(final List<Object> parameters,
                                                final SpreadsheetExpressionEvaluationContext context) {
                                return parameters.stream()
                                    .mapToLong(p -> context.convertOrFail(p, Long.class))
                                    .sum();
                            }

                            @Override
                            public List<ExpressionFunctionParameter<?>> parameters(final int count) {
                                return Lists.of(
                                    ExpressionFunctionParameterName.with("parameters")
                                        .variable(Object.class)
                                        .setKinds(ExpressionFunctionParameterKind.CONVERT_EVALUATE_RESOLVE_REFERENCES)
                                );
                            }

                            @Override
                            public String toString() {
                                return "xyz";
                            }
                        };
                    case TEST_CONTEXT_LOADCELL:
                        return new FakeExpressionFunction<>() {

                            @Override
                            public Optional<ExpressionFunctionName> name() {
                                return Optional.of(
                                    ExpressionFunctionName.with(TEST_CONTEXT_LOADCELL)
                                );
                            }

                            @Override
                            public Object apply(final List<Object> parameters,
                                                final SpreadsheetExpressionEvaluationContext context) {
                                return context.loadCell(
                                        (SpreadsheetCellReference) parameters.get(0)
                                    ).get()
                                    .formula()
                                    .errorOrValue()
                                    .get();
                            }

                            @Override
                            public List<ExpressionFunctionParameter<?>> parameters(final int count) {
                                return Lists.of(
                                    ExpressionFunctionParameterName.with("parameters")
                                        .variable(Object.class)
                                        .setKinds(
                                            Sets.of(ExpressionFunctionParameterKind.EVALUATE)
                                        )
                                );
                            }

                            @Override
                            public String toString() {
                                return TEST_CONTEXT_LOADCELL;
                            }
                        };
                    case TEST_CONTEXT_SERVER_URL:
                        return new FakeExpressionFunction<>() {

                            @Override
                            public Optional<ExpressionFunctionName> name() {
                                return Optional.of(
                                    ExpressionFunctionName.with(TEST_CONTEXT_SERVER_URL)
                                );
                            }

                            @Override
                            public Object apply(final List<Object> parameters,
                                                final SpreadsheetExpressionEvaluationContext context) {
                                return context.serverUrl();
                            }

                            @Override
                            public List<ExpressionFunctionParameter<?>> parameters(final int count) {
                                return Lists.of(
                                    ExpressionFunctionParameterName.with("parameters")
                                        .variable(Object.class)
                                );
                            }

                            @Override
                            public String toString() {
                                return TEST_CONTEXT_SERVER_URL;
                            }
                        };
                    case TEST_CONTEXT_SPREADSHEET_METADATA:
                        return new FakeExpressionFunction<>() {

                            @Override
                            public Optional<ExpressionFunctionName> name() {
                                return Optional.of(
                                    ExpressionFunctionName.with(TEST_CONTEXT_SPREADSHEET_METADATA)
                                );
                            }

                            @Override
                            public Object apply(final List<Object> parameters,
                                                final SpreadsheetExpressionEvaluationContext context) {
                                return context.spreadsheetMetadata();
                            }

                            @Override
                            public List<ExpressionFunctionParameter<?>> parameters(final int count) {
                                return Lists.of(
                                    ExpressionFunctionParameterName.with("parameters")
                                        .variable(Object.class)
                                );
                            }

                            @Override
                            public String toString() {
                                return TEST_CONTEXT_SPREADSHEET_METADATA;
                            }
                        };
                    default:
                        throw new UnsupportedOperationException("Unknown function: " + name);
                }
            }

            @Override
            public ExpressionFunctionInfoSet expressionFunctionInfos() {
                return SpreadsheetExpressionFunctions.infoSet(
                    Sets.of(
                        SpreadsheetExpressionFunctions.info(
                            Url.parseAbsolute("https://example.com/test/xyz"),
                            SpreadsheetExpressionFunctions.name("xyz")
                        ),
                        SpreadsheetExpressionFunctions.info(
                            Url.parseAbsolute("https://example.com/test/" + TEST_CONTEXT_LOADCELL),
                            SpreadsheetExpressionFunctions.name(TEST_CONTEXT_LOADCELL)
                        ),
                        SpreadsheetExpressionFunctions.info(
                            Url.parseAbsolute("https://example.com/test/" + TEST_CONTEXT_SERVER_URL),
                            SpreadsheetExpressionFunctions.name(TEST_CONTEXT_SERVER_URL)
                        ),
                        SpreadsheetExpressionFunctions.info(
                            Url.parseAbsolute("https://example.com/test/" + TEST_CONTEXT_SPREADSHEET_METADATA),
                            SpreadsheetExpressionFunctions.name(TEST_CONTEXT_SPREADSHEET_METADATA)
                        )
                    )
                );
            }

            @Override
            public CaseSensitivity expressionFunctionNameCaseSensitivity() {
                return SpreadsheetExpressionFunctions.NAME_CASE_SENSITIVITY;
            }
        },
        SPREADSHEET_COMPARATOR_PROVIDER,
        SPREADSHEET_EXPORTER_PROVIDER,
        SPREADSHEET_FORMATTER_PROVIDER,
        FORM_HANDLER_PROVIDER,
        SPREADSHEET_IMPORTER_PROVIDER,
        SPREADSHEET_PARSER_PROVIDER,
        VALIDATOR_PROVIDER
    );

    SpreadsheetEngineContextSharedTestCase() {
        super();
    }

    // serverUrl........................................................................................................

    @Test
    public final void testServerUrl() {
        this.serverUrlAndCheck(
            this.createContext(),
            SERVER_URL
        );
    }

    // toExpression.....................................................................................................

    @Test
    public final void testToExpression() {
        final C context = this.createContext();

        this.toExpressionAndCheck(
            context,
            SpreadsheetFormulaParserToken.expression(
                Lists.of(
                    SpreadsheetFormulaParserToken.equalsSymbol("=", "="),
                    SpreadsheetFormulaParserToken.addition(
                        Lists.of(
                            SpreadsheetFormulaParserToken.number(
                                Lists.of(
                                    SpreadsheetFormulaParserToken.digits("1", "1")
                                ),
                                "1"
                            ),
                            SpreadsheetFormulaParserToken.number(
                                Lists.of(
                                    SpreadsheetFormulaParserToken.digits("2", "2")
                                ),
                                "2"
                            )
                        ),
                        "1+2"
                    )
                ),
                "=1+2"
            ),
            Expression.add(
                Expression.value(
                    EXPRESSION_NUMBER_KIND.one()
                ),
                Expression.value(
                    EXPRESSION_NUMBER_KIND.create(2)
                )
            )
        );
    }

    // evaluate.........................................................................................................

    @Test
    public final void testEvaluate() {
        this.evaluateAndCheck(
            Expression.add(
                this.expression(1),
                this.expression(2)
            ),
            this.number(1 + 2)
        );
    }

    @Test
    public final void testEvaluateWithFunction() {
        this.evaluateAndCheck(
            Expression.call(
                Expression.namedFunction(
                    SpreadsheetExpressionFunctions.name("xyz")
                ),
                Lists.of(
                    this.expression(1),
                    this.expression(2),
                    this.expression(3)
                )
            ),
            1L + 2 + 3
        );
    }

    final static SpreadsheetCellReference LOAD_CELL_REFERENCE = SpreadsheetSelection.parseCell("Z99");
    final static Object LOAD_CELL_VALUE = "LoadCellTextValue";

    @Test
    public final void testEvaluateWithFunctionContextServerUrl() {
        this.evaluateAndCheck(
            Expression.call(
                Expression.namedFunction(
                    SpreadsheetExpressionFunctions.name(TEST_CONTEXT_SERVER_URL)
                ),
                Lists.empty()
            ),
            SERVER_URL
        );
    }

    final ExpressionNumber number(final Number value) {
        return EXPRESSION_NUMBER_KIND.create(value);
    }

    final ValueExpression<?> expression(final Number value) {
        return Expression.value(
            this.number(value)
        );
    }

    // currentWorkingDirectory......................................................................................................

    @Test
    public final void testCurrentWorkingDirectory() {
        this.currentWorkingDirectoryAndCheck(
            this.createContext(),
            CURRENT_WORKING_DIRECTORY
        );
    }

    @Test
    public final void testSetCurrentWorkingDirectory() {
        final StoragePath different = StoragePath.parse("/different");

        this.setCurrentWorkingDirectoryAndCheck(
            this.createContext(),
            different
        );
    }
    
    // indentation......................................................................................................

    @Test
    public final void testIndentation() {
        final EnvironmentContext environmentContext = EnvironmentContexts.map(
            EnvironmentContexts.empty(
                INDENTATION,
                LINE_ENDING,
                LOCALE,
                HAS_NOW,
                EnvironmentContext.ANONYMOUS
            )
        );

        this.indentationAndCheck(
            this.createContext(environmentContext),
            environmentContext.indentation()
        );
    }

    @Test
    public final void testSetIndentation() {
        final EnvironmentContext environmentContext = EnvironmentContexts.map(
            EnvironmentContexts.empty(
                INDENTATION,
                LINE_ENDING,
                LOCALE,
                HAS_NOW,
                EnvironmentContext.ANONYMOUS
            )
        );

        final C context = this.createContext(environmentContext);

        final Indentation indentation = Indentation.SPACES4;

        this.checkNotEquals(
            INDENTATION,
            indentation
        );

        context.setIndentation(indentation);

        this.indentationAndCheck(
            context,
            indentation
        );
    }
    
    // lineEnding.......................................................................................................

    @Test
    public final void testLineEnding() {
        final EnvironmentContext environmentContext = EnvironmentContexts.map(
            EnvironmentContexts.empty(
                INDENTATION,
                LINE_ENDING,
                LOCALE,
                HAS_NOW,
                EnvironmentContext.ANONYMOUS
            )
        );

        this.lineEndingAndCheck(
            this.createContext(environmentContext),
            environmentContext.lineEnding()
        );
    }

    @Test
    public final void testSetLineEnding() {
        final EnvironmentContext environmentContext = EnvironmentContexts.map(
            EnvironmentContexts.empty(
                INDENTATION,
                LINE_ENDING,
                LOCALE,
                HAS_NOW,
                EnvironmentContext.ANONYMOUS
            )
        );

        final C context = this.createContext(environmentContext);

        final LineEnding lineEnding = LineEnding.CRNL;

        this.checkNotEquals(
            LINE_ENDING,
            lineEnding
        );

        this.setLineEndingAndCheck(
            context,
            lineEnding
        );

        this.environmentValueAndCheck(
            context,
            EnvironmentValueName.LINE_ENDING,
            lineEnding
        );
    }

    // locale(EnvironmentContext).......................................................................................

    @Test
    public final void testLocale() {
        final EnvironmentContext environmentContext = EnvironmentContexts.map(
            EnvironmentContexts.empty(
                INDENTATION,
                LINE_ENDING,
                Locale.FRANCE,
                HAS_NOW,
                EnvironmentContext.ANONYMOUS
            )
        );

        this.localeAndCheck(
            this.createContext(environmentContext),
            environmentContext.locale()
        );
    }

    @Test
    public final void testSetLocale() {
        final EnvironmentContext environmentContext = EnvironmentContexts.map(
            EnvironmentContexts.empty(
                INDENTATION,
                LINE_ENDING,
                Locale.FRANCE,
                HAS_NOW,
                EnvironmentContext.ANONYMOUS
            )
        );

        final C context = this.createContext(environmentContext);

        final Locale locale = Locale.GERMAN;
        context.setLocale(locale);

        this.localeAndCheck(
            context,
            locale
        );

        this.environmentValueAndCheck(
            context,
            EnvironmentValueName.LOCALE,
            locale
        );
    }

    // environmentContext...............................................................................................

    @Test
    public final void testCloneEnvironment() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

        final EnvironmentValueName<String> name = EnvironmentValueName.with(
            "Hello",
            String.class
        );
        final String value = "Hello World123";

        spreadsheetEnvironmentContext.setEnvironmentValue(
            name,
            value
        );

        final C context = this.createContext(spreadsheetEnvironmentContext);

        this.environmentValueAndCheck(
            spreadsheetEnvironmentContext,
            name,
            value
        );

        final SpreadsheetEngineContext clone = context.cloneEnvironment();
        this.environmentValueAndCheck(
            clone,
            name,
            value
        );

        // remove name and verify gone from $context
        context.removeEnvironmentValue(name);
        this.environmentValueAndCheck(
            context,
            name
        );

        // $name should remain set in $clone
        this.environmentValueAndCheck(
            clone,
            name,
            value
        );
    }

    // setEnvironmentContext............................................................................................

    @Test
    public final void testSetEnvironmentContextWithSame() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

        final C context = this.createContext(spreadsheetEnvironmentContext);
        assertSame(
            context,
            context.setEnvironmentContext(spreadsheetEnvironmentContext)
        );
    }

    @Test
    public final void testSetEnvironmentContextWithDifferent() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

        final EnvironmentContext differentEnvironmentContext = spreadsheetEnvironmentContext.cloneEnvironment();
        differentEnvironmentContext.setLineEnding(LineEnding.CRNL);

        this.checkNotEquals(
            spreadsheetEnvironmentContext,
            differentEnvironmentContext
        );

        final C before = this.createContext(spreadsheetEnvironmentContext);
        final SpreadsheetEngineContext after = before.setEnvironmentContext(differentEnvironmentContext);

        assertNotSame(
            before,
            after
        );
    }

    // environmentValue.................................................................................................

    @Test
    public final void testEnvironmentValue() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

        final EnvironmentValueName<String> name = EnvironmentValueName.with(
            "Hello",
            String.class
        );
        final String value = "Hello World123";

        spreadsheetEnvironmentContext.setEnvironmentValue(
            name,
            value
        );

        this.environmentValueAndCheck(
            this.createContext(spreadsheetEnvironmentContext),
            name,
            value
        );
    }

    @Test
    public final void testSetEnvironmentValue() {
        final EnvironmentValueName<String> name = EnvironmentValueName.with(
            "Hello",
            String.class
        );
        final String value = "Hello World123";

        final C context = this.createContext(
            SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment()
        );
        context.setEnvironmentValue(
            name,
            value
        );

        this.environmentValueAndCheck(
            context,
            name,
            value
        );
    }

    @Test
    public final void testSetEnvironmentValueWithLocale() {
        final EnvironmentValueName<Locale> name = EnvironmentValueName.LOCALE;
        final Locale value = Locale.FRANCE;

        final C context = this.createContext(
            SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment()
        );
        context.setEnvironmentValue(
            name,
            value
        );

        this.environmentValueAndCheck(
            context,
            name,
            value
        );

        this.localeAndCheck(
            context,
            value
        );
    }

    @Test
    public final void testRemoveEnvironmentValue() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

        final EnvironmentValueName<String> name = EnvironmentValueName.with(
            "Hello",
            String.class
        );
        final String value = "Hello World123";

        spreadsheetEnvironmentContext.setEnvironmentValue(
            name,
            value
        );

        final C context = this.createContext(spreadsheetEnvironmentContext);
        context.removeEnvironmentValue(name);

        this.environmentValueAndCheck(
            context,
            name
        );
    }

    @Test
    public final void testExpressionEvaluationContextAndEnvironmentValue() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

        final EnvironmentValueName<String> name = EnvironmentValueName.with(
            "Hello",
            String.class
        );
        final String value = "Hello World123";

        spreadsheetEnvironmentContext.setEnvironmentValue(
            name,
            value
        );

        this.environmentValueAndCheck(
            this.createContext(spreadsheetEnvironmentContext)
                .spreadsheetExpressionEvaluationContext(
                    SpreadsheetEngineContext.NO_CELL,
                    SpreadsheetExpressionReferenceLoaders.fake()
                ),
            name,
            value
        );
    }

    @Test
    public final void testExpressionEvaluationContextAndNow() {
        final C context = this.createContext();
        final LocalDateTime now = context.now();

        this.checkEquals(
            context.spreadsheetExpressionEvaluationContext(
                SpreadsheetEngineContext.NO_CELL,
                SpreadsheetExpressionReferenceLoaders.fake()
            ).now(),
            now
        );
    }

    @Test
    public final void testExpressionEvaluationContextAndUser() {
        final C context = this.createContext();
        final Optional<EmailAddress> user = context.user();
        this.checkNotEquals(
            Optional.empty(),
            user
        );

        this.userAndCheck(
            context.spreadsheetExpressionEvaluationContext(
                SpreadsheetEngineContext.NO_CELL,
                SpreadsheetExpressionReferenceLoaders.fake()
            ),
            user
        );
    }

    // storage..........................................................................................................

    @Test
    public final void testStorage() {
        this.storageAndCheck(
            this.createContext(),
            STORAGE
        );
    }

    // createContext....................................................................................................

    final C createContext(final EnvironmentContext environmentContext) {
        return this.createContext(
            SpreadsheetEnvironmentContexts.basic(
                STORAGE,
                environmentContext
            )
        );
    }

    abstract C createContext(final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext);

    @Override
    public final DateTimeContext dateTimeContext() {
        return new FakeDateTimeContext() {

            @Override
            public String toString() {
                return "DateTimeContext123";
            }
        };
    }

    @Override
    public final DecimalNumberContext decimalNumberContext() {
        return DecimalNumberContexts.basic(
            DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT,
            DecimalNumberSymbols.with(
                MINUS,
                PLUS,
                ZERO_DIGIT,
                CURRENCY,
                DECIMAL,
                EXPONENT,
                GROUP_SEPARATOR,
                INFINITY,
                MONETARY_DECIMAL_SEPARATOR,
                NAN,
                PERCENT,
                PERMILL_SYMBOL
            ),
            LOCALE,
            new MathContext(
                MathContext.DECIMAL32.getPrecision(),
                RoundingMode.HALF_UP
            )
        );
    }

    // class............................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public final String typeNamePrefix() {
        return SpreadsheetEngineContext.class.getSimpleName();
    }
}

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
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.spreadsheet.FakeSpreadsheetContext;
import walkingkooka.spreadsheet.FakeSpreadsheetContextSupplier;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.SpreadsheetContextSupplier;
import walkingkooka.spreadsheet.SpreadsheetContextSuppliers;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextFactory;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContexts;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterProviders;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterProviders;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.provider.SpreadsheetProviders;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.store.repo.FakeSpreadsheetStoreRepository;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.spreadsheet.value.SpreadsheetErrorKind;
import walkingkooka.terminal.TerminalContext;
import walkingkooka.terminal.TerminalContexts;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterKind;
import walkingkooka.tree.expression.function.FakeExpressionFunction;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionInfoSet;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;
import walkingkooka.tree.expression.function.provider.FakeExpressionFunctionProvider;
import walkingkooka.validation.form.provider.FormHandlerProviders;
import walkingkooka.validation.provider.ValidatorProviders;

import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContextTest implements SpreadsheetExpressionEvaluationContextTesting<SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext>,
    SpreadsheetMetadataTesting,
    DecimalNumberContextDelegator {

    private final static SpreadsheetContextSupplier SPREADSHEET_CONTEXT_SUPPLIER = SpreadsheetContextSuppliers.fake();

    private final static int DECIMAL_NUMBER_DIGIT_COUNT = 6;

    static {
        SpreadsheetEnvironmentContext context = SpreadsheetMetadataTesting.SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

        for (final EnvironmentValueName<?> name : SpreadsheetEnvironmentContextFactory.ENVIRONMENT_VALUE_NAMES) {
            if (name.equals(SpreadsheetEnvironmentContextFactory.CONVERTER)) {
                continue;
            }

            context = context.setEnvironmentValue(
                name,
                Cast.to(
                    METADATA_EN_AU.getOrFail(
                        SpreadsheetMetadataPropertyName.fromEnvironmentValueName(name)
                    )
                )
            );
        }

        SPREADSHEET_ENVIRONMENT_CONTEXT = context.setEnvironmentValue(
            SpreadsheetEnvironmentContextFactory.CONVERTER,
            METADATA_EN_AU.getOrFail(
                SpreadsheetMetadataPropertyName.VALIDATION_CONVERTER
            )
        ).setEnvironmentValue(
            SpreadsheetEnvironmentContextFactory.DECIMAL_NUMBER_DIGIT_COUNT,
            DECIMAL_NUMBER_DIGIT_COUNT
        );
    }

    private final static SpreadsheetEnvironmentContext SPREADSHEET_ENVIRONMENT_CONTEXT;

    private final static TerminalContext TERMINAL_CONTEXT = TerminalContexts.fake();

    // with.............................................................................................................

    @Test
    public void testWithNullSpreadsheetContextSupplierFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext.with(
                null,
                LOCALE_CONTEXT,
                SPREADSHEET_ENVIRONMENT_CONTEXT,
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullLocaleContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext.with(
                SPREADSHEET_CONTEXT_SUPPLIER,
                null,
                SPREADSHEET_ENVIRONMENT_CONTEXT,
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetEnvironmentContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext.with(
                SPREADSHEET_CONTEXT_SUPPLIER,
                LOCALE_CONTEXT,
                null,
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullTerminalContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext.with(
                SPREADSHEET_CONTEXT_SUPPLIER,
                LOCALE_CONTEXT,
                SPREADSHEET_ENVIRONMENT_CONTEXT,
                null,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext.with(
                SPREADSHEET_CONTEXT_SUPPLIER,
                LOCALE_CONTEXT,
                SPREADSHEET_ENVIRONMENT_CONTEXT,
                TERMINAL_CONTEXT,
                null,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullProviderContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext.with(
                SPREADSHEET_CONTEXT_SUPPLIER,
                LOCALE_CONTEXT,
                SPREADSHEET_ENVIRONMENT_CONTEXT,
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                null
            )
        );
    }

    // environmentContext...............................................................................................

    @Test
    public void testSetEnvironmentContextWithDifferent() {
        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext context = this.createContext();

        final LineEnding lineEnding = LineEnding.CRNL;

        this.checkNotEquals(
            LINE_ENDING,
            lineEnding
        );

        final EnvironmentContext differentEnvironmentContext = EnvironmentContexts.empty(
            lineEnding,
            LOCALE,
            HAS_NOW,
            EnvironmentContext.ANONYMOUS
        );

        final SpreadsheetExpressionEvaluationContext afterSet = context.setEnvironmentContext(differentEnvironmentContext);
        this.checkNotEquals(
            context,
            afterSet
        );
    }

    // HasLineEndings...................................................................................................

    @Test
    public void testLineEnding() {
        final SpreadsheetEnvironmentContext context = SpreadsheetEnvironmentContexts.basic(
            EnvironmentContexts.empty(
                LINE_ENDING,
                LOCALE,
                HAS_NOW,
                EnvironmentContext.ANONYMOUS
            )
        );

        this.lineEndingAndCheck(
            this.createContext(context),
            context.lineEnding()
        );
    }

    @Test
    public void testSetLineEnding() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.basic(
            EnvironmentContexts.map(
                EnvironmentContexts.empty(
                    LINE_ENDING,
                    LOCALE,
                    HAS_NOW,
                    EnvironmentContext.ANONYMOUS
                )
            )
        );

        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext context = this.createContext(spreadsheetEnvironmentContext);

        final LineEnding lineEnding = LineEnding.CRNL;

        this.checkNotEquals(
            LINE_ENDING,
            lineEnding
        );

        context.setLineEnding(lineEnding);

        this.lineEndingAndCheck(
            context,
            lineEnding
        );
    }

    @Test
    public void testLocale() {
        final SpreadsheetEnvironmentContext context = SpreadsheetEnvironmentContexts.basic(
            EnvironmentContexts.empty(
                LINE_ENDING,
                Locale.FRANCE,
                HAS_NOW,
                EnvironmentContext.ANONYMOUS
            )
        );

        this.localeAndCheck(
            this.createContext(context),
            context.locale()
        );
    }

    @Test
    public void testSetLocale() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.basic(
            EnvironmentContexts.map(
                EnvironmentContexts.empty(
                    LINE_ENDING,
                    Locale.FRANCE,
                    HAS_NOW,
                    EnvironmentContext.ANONYMOUS
                )
            )
        );

        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext context = this.createContext(spreadsheetEnvironmentContext);

        final Locale locale = Locale.GERMAN;
        context.setLocale(locale);

        this.localeAndCheck(
            context,
            locale
        );
    }

    @Test
    public void testEnvironmentValue() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.basic(
            EnvironmentContexts.map(
                SPREADSHEET_ENVIRONMENT_CONTEXT
            )
        );

        final EnvironmentValueName<String> name = EnvironmentValueName.with("Hello");
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
    public void testSetEnvironmentValue() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.basic(
            EnvironmentContexts.map(
                SPREADSHEET_ENVIRONMENT_CONTEXT
            )
        );

        final EnvironmentValueName<String> name = EnvironmentValueName.with("Hello");
        final String value = "Hello World123";

        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext context = this.createContext(spreadsheetEnvironmentContext);
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
    public void testRemoveEnvironmentValue() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.basic(
            EnvironmentContexts.map(
                SPREADSHEET_ENVIRONMENT_CONTEXT
            )
        );

        final EnvironmentValueName<String> name = EnvironmentValueName.with("Hello");
        final String value = "Hello World123";

        spreadsheetEnvironmentContext.setEnvironmentValue(
            name,
            value
        );

        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext context = this.createContext(spreadsheetEnvironmentContext);
        context.removeEnvironmentValue(name);

        this.environmentValueAndCheck(
            context,
            name
        );
    }

    @Test
    public void testUser() {
        final EmailAddress user = EmailAddress.parse("user123@example.com");

        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.basic(
            EnvironmentContexts.empty(
                LINE_ENDING,
                LOCALE,
                HAS_NOW,
                Optional.of(user)
            )
        );

        this.userAndCheck(
            this.createContext(spreadsheetEnvironmentContext),
            user
        );
    }

    // parseFormula.....................................................................................................

    @Test
    public void testParseExpressionDateTimeFails() {
        this.parseExpressionAndFail(
            "1999/12/31 12:58",
            "Invalid character '1' at (12,1) expected EXPRESSION"
        );
    }

    @Test
    public void testParseExpressionNumber() {
        final String text = "123";

        this.parseExpressionAndCheck(
            text,
            SpreadsheetFormulaParserToken.number(
                Lists.of(
                    SpreadsheetFormulaParserToken.digits(text, text)
                ),
                text
            )
        );
    }

    @Test
    public void testParseExpressionApostropheStringFails() {
        this.parseExpressionAndFail(
            "'Hello",
            "Invalid character '\\'' at (1,1) expected BINARY_EXPRESSION | LAMBDA_FUNCTION | NAMED_FUNCTION | \"TRUE\" | \"FALSE\" | LABEL | CELL_RANGE | CELL | GROUP | NEGATIVE | \"#.#E+#;#.#%;#.#;#%;#\" | TEXT | \"#NULL!\" | \"#DIV/0!\" | \"#VALUE!\" | \"#REF!\" | \"#NAME?\" | \"#NAME?\" | \"#NUM!\" | \"#N/A\" | \"#ERROR\" | \"#SPILL!\" | \"#CALC!\""
        );
    }

    @Test
    public void testParseExpressionTimeFails() {
        this.parseExpressionAndFail(
            "12:58:59",
            "Invalid character ':' at (3,1) expected EXPRESSION"
        );
    }

    @Test
    public void testParseExpressionWithDoubleQuotedString() {
        final String text = "\"Hello\"";

        this.parseExpressionAndCheck(
            text,
            SpreadsheetFormulaParserToken.text(
                Lists.of(
                    SpreadsheetFormulaParserToken.doubleQuoteSymbol("\"", "\""),
                    SpreadsheetFormulaParserToken.textLiteral("Hello", "Hello"),
                    SpreadsheetFormulaParserToken.doubleQuoteSymbol("\"", "\"")
                ),
                text
            )
        );
    }

    @Test
    public void testParseExpressionWithAdditionExpression() {
        final String text = "1+2";

        this.parseExpressionAndCheck(
            text,

            SpreadsheetFormulaParserToken.addition(
                Lists.of(
                    SpreadsheetFormulaParserToken.number(
                        Lists.of(
                            SpreadsheetFormulaParserToken.digits("1", "1")
                        ),
                        "1"
                    ),
                    SpreadsheetFormulaParserToken.plusSymbol("+", "+"),
                    SpreadsheetFormulaParserToken.number(
                        Lists.of(
                            SpreadsheetFormulaParserToken.digits("2", "2")
                        ),
                        "2"
                    )
                ),
                text
            )
        );
    }

    // parseValueOrExpression...........................................................................................

    @Test
    public void testParseValueOrExpressionDoubleQuotedStringFails() {
        this.parseValueOrExpressionAndFail(
            "\"abc123\"",
            "Invalid character '\\\"' at (1,1) expected \"\\'\", [STRING] | EQUALS_EXPRESSION | VALUE"
        );
    }

    @Test
    public void testParseValueOrExpressionDate() {
        final String text = "1999/12/31";

        this.parseValueOrExpressionAndCheck(
            text,
            SpreadsheetFormulaParserToken.date(
                Lists.of(
                    SpreadsheetFormulaParserToken.year(1999, "1999"),
                    SpreadsheetFormulaParserToken.textLiteral("/", "/"),
                    SpreadsheetFormulaParserToken.monthNumber(12, "12"),
                    SpreadsheetFormulaParserToken.textLiteral("/", "/"),
                    SpreadsheetFormulaParserToken.dayNumber(31, "31")
                ),
                text
            )
        );
    }

    @Test
    public void testParseValueOrExpressionDateTime() {
        final String text = "1999/12/31 12:58";

        this.parseValueOrExpressionAndCheck(
            text,
            SpreadsheetFormulaParserToken.dateTime(
                Lists.of(
                    SpreadsheetFormulaParserToken.year(1999, "1999"),
                    SpreadsheetFormulaParserToken.textLiteral("/", "/"),
                    SpreadsheetFormulaParserToken.monthNumber(12, "12"),
                    SpreadsheetFormulaParserToken.textLiteral("/", "/"),
                    SpreadsheetFormulaParserToken.dayNumber(31, "31"),
                    SpreadsheetFormulaParserToken.whitespace(" ", " "),
                    SpreadsheetFormulaParserToken.hour(12, "12"),
                    SpreadsheetFormulaParserToken.textLiteral(":", ":"),
                    SpreadsheetFormulaParserToken.minute(58, "58")
                ),
                text
            )
        );
    }

    @Test
    public void testParseValueOrExpressionNumber() {
        final String text = "123";

        this.parseValueOrExpressionAndCheck(
            text,
            SpreadsheetFormulaParserToken.number(
                Lists.of(
                    SpreadsheetFormulaParserToken.digits(text, text)
                ),
                text
            )
        );
    }

    private final static char DECIMAL = '.';

    @Test
    public void testParseValueOrExpressionNumber2() {
        final String text = "1" + DECIMAL + "5";

        this.parseValueOrExpressionAndCheck(
            text,
            SpreadsheetFormulaParserToken.number(
                Lists.of(
                    SpreadsheetFormulaParserToken.digits("1", "1"),
                    SpreadsheetFormulaParserToken.decimalSeparatorSymbol("" + DECIMAL, "" + DECIMAL),
                    SpreadsheetFormulaParserToken.digits("5", "5")
                ),
                text
            )
        );
    }

    @Test
    public void testParseValueOrExpressionApostropheString() {
        final String text = "'Hello";

        this.parseValueOrExpressionAndCheck(
            text,
            SpreadsheetFormulaParserToken.text(
                Lists.of(
                    SpreadsheetFormulaParserToken.apostropheSymbol("'", "'"),
                    SpreadsheetFormulaParserToken.textLiteral("Hello", "Hello")
                ),
                text
            )
        );
    }

    @Test
    public void testParseValueOrExpressionTime() {
        final String text = "12:58:59";

        this.parseValueOrExpressionAndCheck(
            text,
            SpreadsheetFormulaParserToken.time(
                Lists.of(
                    SpreadsheetFormulaParserToken.hour(12, "12"),
                    SpreadsheetFormulaParserToken.textLiteral(":", ":"),
                    SpreadsheetFormulaParserToken.minute(58, "58"),
                    SpreadsheetFormulaParserToken.textLiteral(":", ":"),
                    SpreadsheetFormulaParserToken.seconds(59, "59")
                ),
                text
            )
        );
    }

    @Test
    public void testParseValueOrExpressionEqualsAdditionExpression() {
        final String text = "=1+2";

        this.parseValueOrExpressionAndCheck(
            text,
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
                            SpreadsheetFormulaParserToken.plusSymbol("+", "+"),
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
                text
            )
        );
    }

    @Test
    public void testParseValueOrExpressionAdditionExpressionFails() {
        final String text = "1+2";

        this.parseValueOrExpressionAndFail(
            text,
            "Invalid character '1' at (1,1) expected \"\\'\", [STRING] | EQUALS_EXPRESSION | VALUE"
        );
    }

    // evaluateExpression...............................................................................................

    @Test
    public void testEvaluateExpressionFunctionParameterValueConvertFails() {
        final ExpressionFunctionName functionName = ExpressionFunctionName.with("HelloFunction");

        this.evaluateExpressionAndCheck(
            this.createContext(
                new FakeExpressionFunctionProvider<>() {
                    @Override
                    public ExpressionFunction<?, SpreadsheetExpressionEvaluationContext> expressionFunction(final ExpressionFunctionName name,
                                                                                                            final List<?> values,
                                                                                                            final ProviderContext context) {
                        return new FakeExpressionFunction<>() {
                            @Override
                            public Optional<ExpressionFunctionName> name() {
                                return Optional.of(name);
                            }

                            @Override
                            public List<ExpressionFunctionParameter<?>> parameters(final int count) {
                                return Lists.of(
                                    ExpressionFunctionParameter.NUMBER.setType(Void.class)
                                        .setKinds(
                                            ExpressionFunctionParameterKind.CONVERT_EVALUATE
                                        )
                                );
                            }

                            @Override
                            public Object apply(final List<Object> values,
                                                final SpreadsheetExpressionEvaluationContext context) {
                                ExpressionFunctionParameter.NUMBER.getOrFail(values, 0);
                                throw new UnsupportedOperationException();
                            }
                        };
                    }

                    @Override
                    public ExpressionFunctionInfoSet expressionFunctionInfos() {
                        return SpreadsheetExpressionFunctions.parseInfoSet("https://example.com/HelloFunction HelloFunction");
                    }

                    @Override
                    public CaseSensitivity expressionFunctionNameCaseSensitivity() {
                        return SpreadsheetExpressionFunctions.NAME_CASE_SENSITIVITY;
                    }
                }
            ),
            Expression.call(
                Expression.namedFunction(functionName),
                Lists.of(
                    Expression.value("String1")
                )
            ),
            SpreadsheetErrorKind.VALUE.setMessage(
                "HelloFunction: number: Cannot convert \"String1\" to Void"
            )
        );
    }

    // evaluateFunction.................................................................................................

    @Test
    public void testEvaluateFunctionMissingParameters() {
        final ExpressionFunctionName functionName = ExpressionFunctionName.with("HelloFunction");

        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext context = this.createContext(
            new FakeExpressionFunctionProvider<>() {

                @Override
                public ExpressionFunction<?, SpreadsheetExpressionEvaluationContext> expressionFunction(final ExpressionFunctionName name,
                                                                                                        final List<?> values,
                                                                                                        final ProviderContext context) {
                    return new FakeExpressionFunction<>() {
                        @Override
                        public Optional<ExpressionFunctionName> name() {
                            return Optional.of(functionName);
                        }

                        @Override
                        public List<ExpressionFunctionParameter<?>> parameters(int count) {
                            return Lists.of(
                                ExpressionFunctionParameter.DATE,
                                ExpressionFunctionParameter.DATETIME,
                                ExpressionFunctionParameter.TIME
                            );
                        }

                        @Override
                        public Object apply(final List<Object> parameters,
                                            final SpreadsheetExpressionEvaluationContext context) {
                            this.checkParameterCount(parameters);
                            throw new UnsupportedOperationException();
                        }
                    };
                }

                @Override
                public ExpressionFunctionInfoSet expressionFunctionInfos() {
                    return SpreadsheetExpressionFunctions.parseInfoSet("https://example.com/HelloFunction HelloFunction");
                }

                @Override
                public CaseSensitivity expressionFunctionNameCaseSensitivity() {
                    return SpreadsheetExpressionFunctions.NAME_CASE_SENSITIVITY;
                }
            }
        );

        final ExpressionFunction<?, ExpressionEvaluationContext> function = context.expressionFunction(
            functionName
        );

        this.evaluateFunctionAndCheck(
            context,
            Cast.to(
                function
            ),
            Lists.of(1),
            Cast.to(
                SpreadsheetErrorKind.VALUE.setMessage("HelloFunction: Missing parameter(s): date-time, time")
            )
        );
    }

    @Override
    public void testEvaluateExpressionUnknownFunctionNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEvaluateFunctionWithNullFunctionNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEvaluateFunctionWithNullParametersFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testReferenceWithNullReferenceFails() {
        throw new UnsupportedOperationException();
    }

    // evaluate.........................................................................................................

    @Test
    public void testEvaluateIncompleteExpressionFails() {
        this.evaluateAndCheck(
            this.createContext(),
            "=1+",
            SpreadsheetErrorKind.ERROR.setMessage(
                "End of text, expected LAMBDA_FUNCTION | NAMED_FUNCTION | \"TRUE\" | \"FALSE\" | LABEL | CELL_RANGE | CELL | GROUP | NEGATIVE | \"#.#E+#;#.#%;#.#;#%;#\" | TEXT | \"#NULL!\" | \"#DIV/0!\" | \"#VALUE!\" | \"#REF!\" | \"#NAME?\" | \"#NAME?\" | \"#NUM!\" | \"#N/A\" | \"#ERROR\" | \"#SPILL!\" | \"#CALC!\""
            )
        );
    }

    @Test
    public void testEvaluateApostrophe() {
        this.evaluateAndCheck(
            this.createContext(),
            "'Hello",
            "Hello"
        );
    }

    @Test
    public void testEvaluateDate() {
        this.evaluateAndCheck(
            this.createContext(),
            "1999/12/31",
            LocalDate.of(
                1999,
                12,
                31
            )
        );
    }

    @Test
    public void testEvaluateDateTime() {
        this.evaluateAndCheck(
            this.createContext(),
            "1999/12/31 12:58",
            LocalDateTime.of(
                1999,
                12,
                31,
                12,
                58
            )
        );
    }

    @Test
    public void testEvaluateNumberValue() {
        this.evaluateAndCheck(
            this.createContext(),
            "123.5",
            EXPRESSION_NUMBER_KIND.create(123.5)
        );
    }

    @Test
    public void testEvaluateTime() {
        this.evaluateAndCheck(
            this.createContext(),
            "12:58:59",
            LocalTime.of(
                12,
                58,
                59
            )
        );
    }

    @Test
    public void testEvaluateExpression() {
        this.evaluateAndCheck(
            this.createContext(),
            "=1+2",
            EXPRESSION_NUMBER_KIND.create(3)
        );
    }

    // cell.............................................................................................................

    @Test
    public void testCell() {
        this.cellAndCheck(
            this.createContext()
        );
    }

    // setCell..........................................................................................................

    @Override
    public void testSetCellWithSame() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testSetCellFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createContext()
                .setCell(SpreadsheetExpressionEvaluationContext.NO_CELL)
        );
    }

    // loadCell.........................................................................................................

    @Test
    public void testLoadCellFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createContext()
                .setCell(SpreadsheetExpressionEvaluationContext.NO_CELL)
        );
    }

    // nextEmptyColumn..................................................................................................

    @Test
    public void testNextEmptyColumnFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createContext()
                .nextEmptyColumn(
                    SpreadsheetSelection.A1.toRow()
                )
        );
    }

    // nextEmptyRow.....................................................................................................

    @Test
    public void testNextEmptyRowFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createContext()
                .nextEmptyRow(
                    SpreadsheetSelection.A1.toColumn()
                )
        );
    }

    // resolveLabel.....................................................................................................

    @Test
    public void testResolveLabelWhenMissingSpreadsheetId() {
        this.resolveIfLabelAndCheck(
            this.createContext(),
            SpreadsheetSelection.labelName("HelloLabel")
        );
    }

    @Test
    public void testResolveLabelWhenSpreadsheetIdAvailable() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("HelloLabel");
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;

        final SpreadsheetId spreadsheetId = SpreadsheetId.with(999);

        this.resolveIfLabelAndCheck(
            this.createContext(
                new FakeSpreadsheetContextSupplier() {
                    @Override
                    public Optional<SpreadsheetContext> spreadsheetContext(final SpreadsheetId id) {
                        return Optional.ofNullable(
                            spreadsheetId.equals(id) ?
                                new FakeSpreadsheetContext() {

                                    @Override
                                    public SpreadsheetStoreRepository storeRepository() {
                                        return new FakeSpreadsheetStoreRepository() {
                                            @Override
                                            public SpreadsheetLabelStore labels() {
                                                final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();
                                                store.save(
                                                    label.setLabelMappingReference(cell)
                                                );
                                                return store;
                                            }
                                        };
                                    }

                                } :
                                null
                        );
                    }
                },
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment()
                    .setSpreadsheetId(spreadsheetId)
            ),
            label,
            cell
        );
    }

    // spreadsheetId....................................................................................................

    @Override
    public void testEnvironmentValueNameWithSpreadsheetId() {
        throw new UnsupportedOperationException();
    }

    // setSpreadsheetId.................................................................................................

    @Override
    public void testSetSpreadsheetIdWithSame() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetSpreadsheetMetadataWithDifferentIdFails() {
        throw new UnsupportedOperationException();
    }

    // spreadsheetFormatterContext......................................................................................

    @Test
    public void testSpreadsheetFormatterContextFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createContext()
                .spreadsheetFormatterContext(
                    SpreadsheetExpressionEvaluationContext.NO_CELL
                )
        );
    }

    // EnvironmentValueName.............................................................................................

    @Test
    public void testFireEnvironmentValueNameChangeWithRoundingMode() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment()
            .setEnvironmentValue(
                SpreadsheetEnvironmentContextFactory.FUNCTIONS,
                SpreadsheetExpressionFunctions.parseAliasSet("HelloFunction")
            );

        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext context = this.createContext(spreadsheetEnvironmentContext);

        final DecimalNumberContext decimalNumberContext = context.decimalNumberContext();

        RoundingMode roundingMode = RoundingMode.UP;
        if (roundingMode == decimalNumberContext.mathContext()
            .getRoundingMode()) {
            roundingMode = RoundingMode.CEILING;
        }

        this.setEnvironmentValueAndCheck(
            context,
            SpreadsheetEnvironmentContextFactory.ROUNDING_MODE,
            roundingMode
        );

        final DecimalNumberContext decimalNumberContext2 = context.decimalNumberContext();

        assertNotSame(
            decimalNumberContext,
            decimalNumberContext2,
            "DecimalNumberCoontext should have been recreated with new RoundingMode"
        );

        this.checkEquals(
            roundingMode,
            decimalNumberContext2.mathContext()
                .getRoundingMode(),
            "DecimalNumberContext.roundingMode"
        );
    }

    // ExpressionEvaluationContextTesting................................................................................

    @Override
    public SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext createContext() {
        return this.createContext(
            SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment()
        );
    }

    private SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext createContext(final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider) {
        return createContext(
            SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment()
                .setEnvironmentValue(
                    SpreadsheetEnvironmentContextFactory.FUNCTIONS,
                    SpreadsheetExpressionFunctions.parseAliasSet("HelloFunction")
                ),
            expressionFunctionProvider,
            ProviderContexts.fake()
        );
    }

    private SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext createContext(final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext) {
        return createContext(
            spreadsheetEnvironmentContext,
            EXPRESSION_FUNCTION_PROVIDER,
            ProviderContexts.fake()
        );
    }

    private SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext createContext(final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                                                                    final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider,
                                                                                                    final ProviderContext providerContext) {
        return this.createContext(
            SPREADSHEET_CONTEXT_SUPPLIER,
            spreadsheetEnvironmentContext,
            expressionFunctionProvider,
            providerContext
        );
    }

    private SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext createContext(final SpreadsheetContextSupplier spreadsheetContextSupplier,
                                                                                                    final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext) {
        return this.createContext(
            spreadsheetContextSupplier,
            spreadsheetEnvironmentContext,
            EXPRESSION_FUNCTION_PROVIDER,
            PROVIDER_CONTEXT
        );
    }

    private SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext createContext(final SpreadsheetContextSupplier spreadsheetContextSupplier,
                                                                                                    final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                                                                    final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider,
                                                                                                    final ProviderContext providerContext) {
        return SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext.with(
            spreadsheetContextSupplier,
            LOCALE_CONTEXT,
            spreadsheetEnvironmentContext,
            TERMINAL_CONTEXT,
            SpreadsheetProviders.basic(
                CONVERTER_PROVIDER,
                expressionFunctionProvider,
                SpreadsheetComparatorProviders.fake(),
                SpreadsheetExporterProviders.fake(),
                SpreadsheetFormatterProviders.fake(),
                FormHandlerProviders.fake(),
                SpreadsheetImporterProviders.fake(),
                SPREADSHEET_PARSER_PROVIDER,
                ValidatorProviders.fake()
            ),
            providerContext
        );
    }

    // HasConverter.....................................................................................................

    @Test
    public void testConverter() {
        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext context = this.createContext();

        final ExpressionNumber from = context.expressionNumberKind()
            .create(123);
        final String to = context.convertOrFail(from, String.class);

        this.checkEquals(
            to,
            context.converter()
                .convertOrFail(
                    from,
                    String.class,
                    context
                ),
            () -> "converter with context and context convertOrFail should return the same"
        );
    }

    @Test
    public void testConverterConvertExpressionNumberToString() {
        this.convertAndCheck(
            EXPRESSION_NUMBER_KIND.create(123),
            String.class,
            "123"
        );
    }


    @Test
    public void testConverterConvertStringToExpressionNumber() {
        this.convertAndCheck(
            "123",
            ExpressionNumber.class,
            EXPRESSION_NUMBER_KIND.create(123)
        );
    }

    @Test
    public void testConverterConvertStringToExpressionNumber2() {
        this.convertAndCheck(
            "123.5",
            ExpressionNumber.class,
            EXPRESSION_NUMBER_KIND.create(123.5)
        );
    }

    // DecimalNumberContext.............................................................................................

    @Override
    public int decimalNumberDigitCount() {
        return DECIMAL_NUMBER_DIGIT_COUNT;
    }

    @Override
    public MathContext mathContext() {
        return DECIMAL_NUMBER_CONTEXT.mathContext();
    }

    // DecimalNumberContextDelegator....................................................................................

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return DECIMAL_NUMBER_CONTEXT;
    }

    private final static DecimalNumberContext DECIMAL_NUMBER_CONTEXT = METADATA_EN_AU.decimalNumberContext(
        SpreadsheetExpressionEvaluationContext.NO_CELL,
        LOCALE_CONTEXT
    );

    @Override
    public void testSetLocaleWithDifferent() {
        throw new UnsupportedOperationException();
    }

    // Class............................................................................................................

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext> type() {
        return SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext.class;
    }
}

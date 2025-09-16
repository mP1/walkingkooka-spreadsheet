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
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.FakeDateTimeContext;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.locale.FakeLocaleContext;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColors;
import walkingkooka.spreadsheet.SpreadsheetDescription;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.compare.SpreadsheetComparator;
import walkingkooka.spreadsheet.compare.SpreadsheetComparators;
import walkingkooka.spreadsheet.conditionalformat.SpreadsheetConditionalFormattingRule;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.format.FakeSpreadsheetFormatterProviderSamplesContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSample;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.provider.SpreadsheetProviders;
import walkingkooka.spreadsheet.reference.FakeSpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoaders;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.security.store.SpreadsheetGroupStores;
import walkingkooka.spreadsheet.security.store.SpreadsheetUserStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellRangeStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellRangeStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellReferencesStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStores;
import walkingkooka.spreadsheet.store.SpreadsheetLabelReferencesStores;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.store.SpreadsheetRowStores;
import walkingkooka.spreadsheet.store.repo.FakeSpreadsheetStoreRepository;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.spreadsheet.validation.form.store.SpreadsheetFormStores;
import walkingkooka.storage.Storage;
import walkingkooka.storage.Storages;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ValueExpression;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterKind;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterName;
import walkingkooka.tree.expression.function.FakeExpressionFunction;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionAliasSet;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionInfoSet;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionSelector;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetEngineContextTest implements SpreadsheetEngineContextTesting<BasicSpreadsheetEngineContext>,
    SpreadsheetMetadataTesting {

    private final static AbsoluteUrl SERVER_URL = Url.parseAbsolute("https://example.com/path123");

    private final static String CURRENCY = "CURR";
    private final static char DECIMAL = '.';
    private final static String EXPONENT = "e";
    private final static char GROUP_SEPARATOR = ',';
    private final static String INFINITY = "Infinity!";
    private final static char MINUS = '!';
    private final static char MONETARY_DECIMAL_SEPARATOR = ':';
    private final static String NAN = "Nan!";
    private final static char PERCENT = '#';
    private final static char PERMILL_SYMBOL = '^';
    private final static char PLUS = '@';
    private final static char ZERO_DIGIT = '0';

    private final static char VALUE_SEPARATOR = ',';
    private final static int WIDTH = 1;

    private final static String TEST_CONTEXT_LOADCELL = "test-context-loadCell";

    private final static String TEST_CONTEXT_SERVER_URL = "test-context-serverUrl";

    private final static String TEST_CONTEXT_SPREADSHEET_METADATA = "test-context-spreadsheet-metadata";

    private final static SpreadsheetMetadata METADATA = SpreadsheetMetadata.NON_LOCALE_DEFAULTS
        .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
        .loadFromLocale(
            LocaleContexts.jre(LOCALE)
        ).set(SpreadsheetMetadataPropertyName.DATE_TIME_PARSER, SpreadsheetPattern.parseDateTimeParsePattern("dd/mm/yyyy hh:mm").spreadsheetParserSelector())
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

    private final static SpreadsheetStoreRepository STORE_REPOSITORY = SpreadsheetStoreRepositories.fake();

    private final static SpreadsheetMetadataPropertyName<ExpressionFunctionAliasSet> FUNCTION_ALIASES = SpreadsheetMetadataPropertyName.FORMULA_FUNCTIONS;

    private final static SpreadsheetExpressionReferenceLoader LOADER = new FakeSpreadsheetExpressionReferenceLoader() {
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

    private final static LocaleContext LOCALE_CONTEXT = LocaleContexts.jre(LOCALE);

    private final static SpreadsheetProvider SPREADSHEET_PROVIDER = SpreadsheetProviders.basic(
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
                        return new FakeExpressionFunction<Object, SpreadsheetExpressionEvaluationContext>() {

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

    // with.............................................................................................................

    @Test
    public void testWithNullServerUrlFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetEngineContext.with(
                null,
                METADATA,
                STORE_REPOSITORY,
                FUNCTION_ALIASES,
                ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullMetadataFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetEngineContext.with(
                SERVER_URL,
                null,
                STORE_REPOSITORY,
                FUNCTION_ALIASES,
                ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullStoreRepositoryFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetEngineContext.with(
                SERVER_URL,
                METADATA,
                null,
                FUNCTION_ALIASES,
                ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullFunctionAliasesFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetEngineContext.with(
                SERVER_URL,
                METADATA,
                STORE_REPOSITORY,
                null,
                ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullEnvironmentContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetEngineContext.with(
                SERVER_URL,
                METADATA,
                STORE_REPOSITORY,
                FUNCTION_ALIASES,
                null,
                LOCALE_CONTEXT,
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
            () -> BasicSpreadsheetEngineContext.with(
                SERVER_URL,
                METADATA,
                STORE_REPOSITORY,
                FUNCTION_ALIASES,
                ENVIRONMENT_CONTEXT,
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
            () -> BasicSpreadsheetEngineContext.with(
                SERVER_URL,
                METADATA,
                STORE_REPOSITORY,
                FUNCTION_ALIASES,
                ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
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
            () -> BasicSpreadsheetEngineContext.with(
                SERVER_URL,
                METADATA,
                STORE_REPOSITORY,
                FUNCTION_ALIASES,
                ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
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
            () -> BasicSpreadsheetEngineContext.with(
                SERVER_URL,
                METADATA,
                STORE_REPOSITORY,
                FUNCTION_ALIASES,
                ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                null
            )
        );
    }

    // serverUrl........................................................................................................

    @Test
    public void testServerUrl() {
        this.serverUrlAndCheck(
            this.createContext(),
            SERVER_URL
        );
    }

    // resolveLabel.....................................................................................................

    @Test
    public void testResolveLabelWithLabel() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final SpreadsheetLabelName label = SpreadsheetLabelName.labelName("Label456");

        final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();
        store.save(label.setLabelMappingReference(cell));

        this.resolveLabelAndCheck(
            this.createContext(store),
            label,
            cell
        );
    }

    @Test
    public void testResolveLabelWithLabelToLabelToCell() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final SpreadsheetLabelName label1 = SpreadsheetLabelName.labelName("Label111");
        final SpreadsheetLabelName label2 = SpreadsheetLabelName.labelName("Label222");

        final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();
        store.save(label1.setLabelMappingReference(label2));
        store.save(label2.setLabelMappingReference(cell));

        this.resolveLabelAndCheck(
            this.createContext(store),
            label1,
            cell
        );
    }

    // spreadsheetComparator............................................................................................

    @Test
    public void testSpreadsheetComparator() {
        final SpreadsheetComparator<?> comparator = SpreadsheetComparators.text();

        this.spreadsheetComparatorAndCheck(
            this.createContext(),
            comparator.name(),
            Lists.empty(),
            PROVIDER_CONTEXT,
            comparator
        );
    }

    // parseFormula.....................................................................................................

    @Test
    public void testParseFormulaApostropheString() {
        final String text = "abc123";
        final String formula = "'" + text;
        this.parseFormulaAndCheck(
            formula,
            SpreadsheetFormulaParserToken.text(
                Lists.of(
                    SpreadsheetFormulaParserToken.apostropheSymbol("'", "'"),
                    SpreadsheetFormulaParserToken.textLiteral(text, text)
                ),
                formula
            )
        );
    }

    @Test
    public void testParseFormulaDate() {
        final String text = "31/12/2000";
        this.parseFormulaAndCheck(
            text,
            SpreadsheetFormulaParserToken.date(
                Lists.of(
                    SpreadsheetFormulaParserToken.dayNumber(31, "31"),
                    SpreadsheetFormulaParserToken.textLiteral("/", "/"),
                    SpreadsheetFormulaParserToken.monthNumber(12, "12"),
                    SpreadsheetFormulaParserToken.textLiteral("/", "/"),
                    SpreadsheetFormulaParserToken.year(2000, "2000")
                ),
                text
            )
        );
    }

    @Test
    public void testParseFormulaDateTime() {
        final String text = "31/12/2000 12:58";
        this.parseFormulaAndCheck(
            text,
            SpreadsheetFormulaParserToken.dateTime(
                Lists.of(
                    SpreadsheetFormulaParserToken.dayNumber(31, "31"),
                    SpreadsheetFormulaParserToken.textLiteral("/", "/"),
                    SpreadsheetFormulaParserToken.monthNumber(12, "12"),
                    SpreadsheetFormulaParserToken.textLiteral("/", "/"),
                    SpreadsheetFormulaParserToken.year(2000, "2000"),
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
    public void testParseFormulaNumber() {
        final String text = "123";
        this.parseFormulaAndCheck(
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
    public void testParseFormulaNumber2() {
        final String text = "1" + DECIMAL + "5";
        this.parseFormulaAndCheck(
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
    public void testParseFormulaTime() {
        final String text = "12:58";
        this.parseFormulaAndCheck(
            text,
            SpreadsheetFormulaParserToken.time(
                Lists.of(
                    SpreadsheetFormulaParserToken.hour(12, "12"),
                    SpreadsheetFormulaParserToken.textLiteral(":", ":"),
                    SpreadsheetFormulaParserToken.minute(58, "58")
                ),
                text
            )
        );
    }

    @Test
    public void testParseFormulaExpression() {
        final String text = "=1+2";
        this.parseFormulaAndCheck(
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

    // toExpression.....................................................................................................

    @Test
    public void testToExpression() {
        final BasicSpreadsheetEngineContext context = this.createContext();

        this.toExpressionAndCheck(
            context,
            context.parseFormula(
                TextCursors.charSequence("=1+2"),
                SpreadsheetEngineContext.NO_CELL
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
    public void testEvaluate() {
        this.evaluateAndCheck(
            Expression.add(
                this.expression(1),
                this.expression(2)
            ),
            this.number(1 + 2)
        );
    }

    @Test
    public void testEvaluateWithFunction() {
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

    private final static SpreadsheetCellReference LOAD_CELL_REFERENCE = SpreadsheetSelection.parseCell("Z99");
    private final static Object LOAD_CELL_VALUE = "LoadCellTextValue";

    @Test
    public void testEvaluateWithFunctionContextLoadCell() {
        this.evaluateAndCheck(
            Expression.call(
                Expression.namedFunction(
                    SpreadsheetExpressionFunctions.name(TEST_CONTEXT_LOADCELL)
                ),
                Lists.of(
                    Expression.reference(
                        LOAD_CELL_REFERENCE
                    )
                )
            ),
            LOADER,
            LOAD_CELL_VALUE
        );
    }

    @Test
    public void testEvaluateWithFunctionContextServerUrl() {
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

    @Test
    public void testEvaluateWithFunctionContextSpreadsheetMetadata() {
        this.evaluateAndCheck(
            Expression.call(
                Expression.namedFunction(
                    SpreadsheetExpressionFunctions.name(TEST_CONTEXT_SPREADSHEET_METADATA)
                ),
                Lists.empty()
            ),
            METADATA
        );
    }

    // formatValue......................................................................................................

    @Test
    public void testFormatValue() {
        this.formatValueAndCheck(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY),
            BigDecimal.valueOf(-125.25),
            SpreadsheetPattern.parseNumberFormatPattern("#.#\"Abc123\"")
                .spreadsheetFormatterSelector(),
            SpreadsheetText.with(
                MINUS + "125" + DECIMAL + "3Abc123"
            )
        );
    }

    // formatValueAndStyle..............................................................................................

    @Test
    public void testFormatValueAndStyleWithUnknownFormatterFails() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setText("1")
                .setValue(
                    Optional.of(1)
                )
        );

        final SpreadsheetFormatterSelector formatter = SpreadsheetFormatterSelector.parse("unknown-formatter-404 param1");

        this.formatAndStyleAndCheck(
            this.createContext(
                METADATA,
                SpreadsheetLabelStores.fake(),
                SpreadsheetCellRangeStores.treeMap()
            ),
            cell,
            formatter,
            cell.setFormula(
                cell.formula()
                    .setError(
                        Optional.of(
                            SpreadsheetError.formatterNotFound(
                                formatter.name()
                            )
                        )
                    )
            ).setFormattedValue(
                Optional.of(
                    TextNode.text("#ERROR")
                )
            )
        );
    }

    @Test
    public void testFormatValueAndStyle() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setText("1")
                .setValue(
                    Optional.of(1)
                )
        );

        this.formatAndStyleAndCheck(
            this.createContext(
                METADATA,
                SpreadsheetLabelStores.fake(),
                SpreadsheetCellRangeStores.treeMap()
            ),
            cell,
            SpreadsheetPattern.parseNumberFormatPattern("#.00")
                .spreadsheetFormatterSelector(),
            cell.setFormattedValue(
                Optional.of(
                    TextNode.text("1.00")
                )
            )
        );
    }

    @Test
    public void testFormatValueAndStylePatternsIncludesCurrencySymbol() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setText("1")
                .setValue(
                    Optional.of(1)
                )
        );

        this.formatAndStyleAndCheck(
            this.createContext(
                METADATA,
                SpreadsheetLabelStores.fake(),
                SpreadsheetCellRangeStores.treeMap()
            ),
            cell,
            SpreadsheetPattern.parseNumberFormatPattern("$#.00")
                .spreadsheetFormatterSelector(),
            cell.setFormattedValue(
                Optional.of(
                    TextNode.text("CURR1:00")
                )
            )
        );
    }

    @Test
    public void testFormatValueAndStyleWithConditionalFormattingRule() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setText("1")
                .setValue(
                    Optional.of(1)
                )
        );
        final TextStyle style = TextStyle.EMPTY.set(
            TextStylePropertyName.BACKGROUND_COLOR,
            Color.parse("#123456")
        );

        final SpreadsheetCellRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRules = SpreadsheetCellRangeStores.treeMap();

        rangeToConditionalFormattingRules.addValue(
            SpreadsheetSelection.A1.toCellRange(),
            SpreadsheetConditionalFormattingRule.with(
                SpreadsheetDescription.with("Test Description"),
                1,
                SpreadsheetFormula.EMPTY.setText("=true()")
                    .setExpression(
                        Optional.of(
                            Expression.value(true)
                        )
                    ),
                (c) -> style
            )
        );

        this.formatAndStyleAndCheck(
            this.createContext(
                METADATA,
                SpreadsheetLabelStores.fake(),
                rangeToConditionalFormattingRules
            ),
            cell,
            SpreadsheetPattern.parseNumberFormatPattern("$#.00")
                .spreadsheetFormatterSelector(),
            cell.setFormattedValue(
                Optional.of(
                    TextNode.text("CURR1:00")
                        .setAttributes(
                            style.value()
                        )
                )
            )
        );
    }

    // SpreadsheetFormatterProvider.....................................................................................

    // Default
    //  text-format-pattern
    //    "@"
    //  Hello 123
    @Test
    public void testSpreadsheetFormatterSamples() {
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.TEXT_FORMAT_PATTERN.setValueText("@@");

        this.spreadsheetFormatterSamplesAndCheck(
            selector,
            SpreadsheetFormatterProvider.INCLUDE_SAMPLES,
            new FakeSpreadsheetFormatterProviderSamplesContext() {
                @Override
                public Optional<SpreadsheetCell> cell() {
                    return Optional.empty();
                }

                @Override
                public boolean canConvert(final Object value,
                                          final Class<?> type) {
                    return this.converter.canConvert(
                        value,
                        type,
                        this
                    );
                }

                @Override
                public <T> Either<T, String> convert(final Object value,
                                                     final Class<T> target) {
                    return this.converter.convert(
                        value,
                        target,
                        this
                    );
                }

                private final Converter<ConverterContext> converter = Converters.simple();

                @Override
                public ProviderContext providerContext() {
                    return ProviderContexts.fake();
                }
            },
            SpreadsheetFormatterSample.with(
                "Default",
                SpreadsheetFormatterName.TEXT_FORMAT_PATTERN.setValueText("@"),
                TextNode.text("Hello 123")
            ),
            SpreadsheetFormatterSample.with(
                "Sample",
                selector,
                TextNode.text("Hello World 123Hello World 123")
            )
        );
    }

    // SpreadsheetParserProvider........................................................................................

    @Test
    public void testSpreadsheetFormatterSelector() {
        final SpreadsheetParsePattern pattern = SpreadsheetPattern.parseDateParsePattern("dd/mm/yyyy");

        this.spreadsheetFormatterSelectorAndCheck(
            pattern.spreadsheetParserSelector(),
            pattern.toFormat()
                .spreadsheetFormatterSelector()
        );
    }

    // locale(EnvironmentContext).......................................................................................

    @Test
    public void testLocale() {
        final EnvironmentContext environmentContext = EnvironmentContexts.empty(
            Locale.FRANCE,
            NOW,
            EnvironmentContext.ANONYMOUS
        );

        this.localeAndCheck(
            this.createContext(environmentContext),
            environmentContext.locale()
        );
    }

    @Test
    public void testSetLocale() {
        final EnvironmentContext environmentContext = EnvironmentContexts.map(
            EnvironmentContexts.empty(
                Locale.FRANCE,
                NOW,
                EnvironmentContext.ANONYMOUS
            )
        );

        final BasicSpreadsheetEngineContext context = this.createContext(environmentContext);

        final Locale locale = Locale.GERMAN;
        context.setLocale(locale);

        this.localeAndCheck(
            context,
            locale
        );
    }

    // environmentContext...............................................................................................

    @Test
    public void testCloneEnvironment() {
        final EnvironmentContext environmentContext = EnvironmentContexts.map(
            ENVIRONMENT_CONTEXT
        );

        final EnvironmentValueName<String> name = EnvironmentValueName.with("Hello");
        final String value = "Hello World123";

        environmentContext.setEnvironmentValue(
            name,
            value
        );

        final BasicSpreadsheetEngineContext context = this.createContext(environmentContext);

        this.environmentValueAndCheck(
            environmentContext,
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

    @Test
    public void testEnvironmentValue() {
        final EnvironmentContext environmentContext = EnvironmentContexts.map(
            ENVIRONMENT_CONTEXT
        );

        final EnvironmentValueName<String> name = EnvironmentValueName.with("Hello");
        final String value = "Hello World123";

        environmentContext.setEnvironmentValue(
            name,
            value
        );

        this.environmentValueAndCheck(
            this.createContext(environmentContext),
            name,
            value
        );
    }

    @Test
    public void testSetEnvironmentValue() {
        final EnvironmentContext environmentContext = EnvironmentContexts.map(
            ENVIRONMENT_CONTEXT
        );

        final EnvironmentValueName<String> name = EnvironmentValueName.with("Hello");
        final String value = "Hello World123";

        final BasicSpreadsheetEngineContext context = this.createContext(environmentContext);
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
        final EnvironmentContext environmentContext = EnvironmentContexts.map(
            ENVIRONMENT_CONTEXT
        );

        final EnvironmentValueName<String> name = EnvironmentValueName.with("Hello");
        final String value = "Hello World123";

        environmentContext.setEnvironmentValue(
            name,
            value
        );

        final BasicSpreadsheetEngineContext context = this.createContext(environmentContext);
        context.removeEnvironmentValue(name);

        this.environmentValueAndCheck(
            context,
            name
        );
    }

    @Test
    public void testExpressionEvaluationContextAndEnvironmentValue() {
        final EnvironmentContext environmentContext = EnvironmentContexts.map(
            ENVIRONMENT_CONTEXT
        );

        final EnvironmentValueName<String> name = EnvironmentValueName.with("Hello");
        final String value = "Hello World123";

        environmentContext.setEnvironmentValue(
            name,
            value
        );

        this.environmentValueAndCheck(
            this.createContext(environmentContext)
                .spreadsheetExpressionEvaluationContext(
                    SpreadsheetEngineContext.NO_CELL,
                    SpreadsheetExpressionReferenceLoaders.fake()
                ),
            name,
            value
        );
    }

    @Test
    public void testExpressionEvaluationContextAndNow() {
        final EnvironmentContext environmentContext = EnvironmentContexts.map(
            ENVIRONMENT_CONTEXT
        );

        final BasicSpreadsheetEngineContext context = this.createContext(environmentContext);
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
    public void testExpressionEvaluationContextAndUser() {
        final EnvironmentContext environmentContext = EnvironmentContexts.map(
            ENVIRONMENT_CONTEXT
        );

        final BasicSpreadsheetEngineContext context = this.createContext(environmentContext);
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

    @Test
    public void testSpreadsheetExpressionEvaluationContextScriptingFunctionsEnvironmentContextUpdatable() {
        final BasicSpreadsheetEngineContext context = this.createContext(
            METADATA.set(
                SpreadsheetMetadataPropertyName.SCRIPTING_FUNCTIONS,
                SpreadsheetExpressionFunctions.parseAliasSet("hello")
            )
        );

        final SpreadsheetEngineContext after = context.spreadsheetEngineContext(SpreadsheetMetadataPropertyName.SCRIPTING_FUNCTIONS);
        final SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext = after.spreadsheetExpressionEvaluationContext(
            SpreadsheetExpressionEvaluationContext.NO_CELL,
            SpreadsheetExpressionReferenceLoaders.fake()
        );

        final Locale locale = Locale.GERMAN;
        spreadsheetExpressionEvaluationContext.setLocale(locale);
        this.localeAndCheck(
            spreadsheetExpressionEvaluationContext,
            locale
        );

        final EnvironmentValueName<String> name = EnvironmentValueName.with("Hello");
        final String value = "World111";

        spreadsheetExpressionEvaluationContext.setEnvironmentValue(
            name,
            value
        );
        this.environmentValueAndCheck(
            spreadsheetExpressionEvaluationContext,
            name,
            value
        );

        spreadsheetExpressionEvaluationContext.removeEnvironmentValue(name);

        this.environmentValueAndCheck(
            spreadsheetExpressionEvaluationContext,
            name
        );
    }

    @Test
    public void testSpreadsheetExpressionEvaluationContextFindFunctionsEnvironmentContextReadOnly() {
        this.spreadsheetExpressionEvaluationContextAndReadOnlyCheck(SpreadsheetMetadataPropertyName.FIND_FUNCTIONS);
    }

    @Test
    public void testSpreadsheetExpressionEvaluationContextFormulaFunctionsEnvironmentContextReadOnly() {
        this.spreadsheetExpressionEvaluationContextAndReadOnlyCheck(SpreadsheetMetadataPropertyName.FORMULA_FUNCTIONS);
    }

    @Test
    public void testSpreadsheetExpressionEvaluationContextFormulaFormattingEnvironmentContextReadOnly() {
        this.spreadsheetExpressionEvaluationContextAndReadOnlyCheck(SpreadsheetMetadataPropertyName.FORMATTING_FUNCTIONS);
    }

    @Test
    public void testSpreadsheetExpressionEvaluationContextValidationFunctionsEnvironmentContextReadOnly() {
        this.spreadsheetExpressionEvaluationContextAndReadOnlyCheck(SpreadsheetMetadataPropertyName.VALIDATION_FUNCTIONS);
    }

    private void spreadsheetExpressionEvaluationContextAndReadOnlyCheck(final SpreadsheetMetadataPropertyName<ExpressionFunctionAliasSet> functions) {
        final BasicSpreadsheetEngineContext context = this.createContext(
            METADATA.set(
                functions,
                SpreadsheetExpressionFunctions.parseAliasSet("hello")
            )
        );

        final EnvironmentValueName<String> name = EnvironmentValueName.with("Hello");
        final String value = "World111";
        context.setEnvironmentValue(
            name,
            value
        );

        final SpreadsheetEngineContext after = context.spreadsheetEngineContext(functions);
        final SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext = after.spreadsheetExpressionEvaluationContext(
            SpreadsheetExpressionEvaluationContext.NO_CELL,
            SpreadsheetExpressionReferenceLoaders.fake()
        );

        assertThrows(
            UnsupportedOperationException.class,
            () -> spreadsheetExpressionEvaluationContext.setLocale(
                Locale.GERMAN
            )
        );

        this.localeAndCheck(
            spreadsheetExpressionEvaluationContext,
            LOCALE
        );

        assertThrows(
            UnsupportedOperationException.class,
            () -> spreadsheetExpressionEvaluationContext.setEnvironmentValue(
                name,
                "World222"
            )
        );

        assertThrows(
            UnsupportedOperationException.class,
            () -> spreadsheetExpressionEvaluationContext.removeEnvironmentValue(name)
        );

        this.environmentValueAndCheck(
            spreadsheetExpressionEvaluationContext,
            name,
            value
        );
    }

    // createContext....................................................................................................

    private BasicSpreadsheetEngineContext createContext(final Locale locale) {
        return this.createContext(
            locale,
            PROVIDER_CONTEXT
        );
    }

    private BasicSpreadsheetEngineContext createContext(final SpreadsheetMetadata metadata) {
        return this.createContext(
            metadata,
            EnvironmentContexts.map(
                EnvironmentContexts.empty(
                    LOCALE,
                    NOW,
                    Optional.of(USER)
                )
            ),
            PROVIDER_CONTEXT
        );
    }

    private BasicSpreadsheetEngineContext createContext(final EnvironmentContext environmentContext) {
        return this.createContext(
            METADATA,
            environmentContext,
            ProviderContexts.fake()
        );
    }

    private BasicSpreadsheetEngineContext createContext(final Locale locale,
                                                        final ProviderContext providerContext) {
        return this.createContext(
            METADATA,
            EnvironmentContexts.empty(
                locale,
                NOW,
                Optional.of(USER)
            ),
            providerContext
        );
    }

    private BasicSpreadsheetEngineContext createContext(final SpreadsheetMetadata metadata,
                                                        final EnvironmentContext environmentContext,
                                                        final ProviderContext providerContext) {
        return BasicSpreadsheetEngineContext.with(
            SERVER_URL,
            metadata,
            SpreadsheetStoreRepositories.basic(
                SpreadsheetCellStores.treeMap(),
                SpreadsheetCellReferencesStores.treeMap(),
                SpreadsheetColumnStores.treeMap(),
                SpreadsheetFormStores.fake(),
                SpreadsheetGroupStores.fake(),
                SpreadsheetLabelStores.treeMap(),
                SpreadsheetLabelReferencesStores.treeMap(),
                SpreadsheetMetadataStores.fake(),
                SpreadsheetCellRangeStores.treeMap(),
                SpreadsheetCellRangeStores.treeMap(),
                SpreadsheetRowStores.treeMap(),
                Storages.fake(),
                SpreadsheetUserStores.fake()
            ),
            FUNCTION_ALIASES,
            environmentContext,
            new FakeLocaleContext() {
                @Override
                public Locale locale() {
                    return this.localLocale;
                }

                @Override
                public LocaleContext setLocale(final Locale locale) {
                    Objects.requireNonNull(locale, "locale");
                    this.localLocale = locale;
                    return this;
                }

                private Locale localLocale = environmentContext.locale();
            },
            TERMINAL_CONTEXT,
            SPREADSHEET_PROVIDER,
            providerContext
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createContext(),
            "serverUrl=https://example.com/path123 \"\n" +
                "\" metadata={\n" +
                "  \"autoHideScrollbars\": false,\n" +
                "  \"cellCharacterWidth\": 1,\n" +
                "  \"clipboardExporter\": \"json\",\n" +
                "  \"clipboardImporter\": \"json\",\n" +
                "  \"color1\": \"black\",\n" +
                "  \"color2\": \"white\",\n" +
                "  \"color3\": \"red\",\n" +
                "  \"color4\": \"lime\",\n" +
                "  \"color5\": \"blue\",\n" +
                "  \"color6\": \"yellow\",\n" +
                "  \"color7\": \"magenta\",\n" +
                "  \"color8\": \"cyan\",\n" +
                "  \"color9\": \"maroon\",\n" +
                "  \"color10\": \"green\",\n" +
                "  \"color11\": \"navy\",\n" +
                "  \"color12\": \"olive\",\n" +
                "  \"color13\": \"purple\",\n" +
                "  \"color14\": \"teal\",\n" +
                "  \"color15\": \"silver\",\n" +
                "  \"color16\": \"grey\",\n" +
                "  \"color17\": \"#99f\",\n" +
                "  \"color18\": \"#936\",\n" +
                "  \"color19\": \"#ffc\",\n" +
                "  \"color20\": \"#cff\",\n" +
                "  \"color21\": \"#606\",\n" +
                "  \"color22\": \"#ff8080\",\n" +
                "  \"color23\": \"#06c\",\n" +
                "  \"color24\": \"#ccf\",\n" +
                "  \"color25\": \"navy\",\n" +
                "  \"color26\": \"magenta\",\n" +
                "  \"color27\": \"yellow\",\n" +
                "  \"color28\": \"cyan\",\n" +
                "  \"color29\": \"purple\",\n" +
                "  \"color30\": \"maroon\",\n" +
                "  \"color31\": \"teal\",\n" +
                "  \"color32\": \"blue\",\n" +
                "  \"color33\": \"#0cf\",\n" +
                "  \"color34\": \"#cff\",\n" +
                "  \"color35\": \"#cfc\",\n" +
                "  \"color36\": \"#ff9\",\n" +
                "  \"color37\": \"#9cf\",\n" +
                "  \"color38\": \"#f9c\",\n" +
                "  \"color39\": \"#c9f\",\n" +
                "  \"color40\": \"#fc9\",\n" +
                "  \"color41\": \"#36f\",\n" +
                "  \"color42\": \"#3cc\",\n" +
                "  \"color43\": \"#9c0\",\n" +
                "  \"color44\": \"#fc0\",\n" +
                "  \"color45\": \"#f90\",\n" +
                "  \"color46\": \"#f60\",\n" +
                "  \"color47\": \"#669\",\n" +
                "  \"color48\": \"#969696\",\n" +
                "  \"color49\": \"#036\",\n" +
                "  \"color50\": \"#396\",\n" +
                "  \"color51\": \"#030\",\n" +
                "  \"color52\": \"#330\",\n" +
                "  \"color53\": \"#930\",\n" +
                "  \"color54\": \"#936\",\n" +
                "  \"color55\": \"#339\",\n" +
                "  \"color56\": \"#333\",\n" +
                "  \"colorBlack\": 1,\n" +
                "  \"colorBlue\": 5,\n" +
                "  \"colorCyan\": 8,\n" +
                "  \"colorGreen\": 4,\n" +
                "  \"colorMagenta\": 7,\n" +
                "  \"colorRed\": 3,\n" +
                "  \"colorWhite\": 2,\n" +
                "  \"colorYellow\": 6,\n" +
                "  \"comparators\": \"date, date-time, day-of-month, day-of-week, hour-of-am-pm, hour-of-day, minute-of-hour, month-of-year, nano-of-second, number, seconds-of-minute, text, text-case-insensitive, time, year\",\n" +
                "  \"converters\": \"basic, boolean, boolean-to-text, collection, collection-to-list, color, color-to-color, color-to-number, date-time, date-time-symbols, decimal-number-symbols, environment, error-throwing, error-to-number, expression, form-and-validation, format-pattern-to-string, has-formatter-selector, has-parser-selector, has-spreadsheet-selection, has-style, has-text-node, has-validator-selector, json, jsonTo, locale, locale-to-text, null-to-number, number, number-to-color, number-to-number, number-to-text, plugins, spreadsheet-cell-set, spreadsheet-metadata, spreadsheet-selection-to-spreadsheet-selection, spreadsheet-selection-to-text, spreadsheet-value, style, system, template, text, text-node, text-to-boolean-list, text-to-color, text-to-csv-string-list, text-to-date-list, text-to-date-time-list, text-to-environment-value-name, text-to-error, text-to-expression, text-to-form-name, text-to-json, text-to-locale, text-to-number-list, text-to-spreadsheet-color-name, text-to-spreadsheet-formatter-selector, text-to-spreadsheet-id, text-to-spreadsheet-metadata, text-to-spreadsheet-metadata-color, text-to-spreadsheet-metadata-property-name, text-to-spreadsheet-name, text-to-spreadsheet-selection, text-to-spreadsheet-text, text-to-string-list, text-to-template-value-name, text-to-text, text-to-text-node, text-to-text-style, text-to-text-style-property-name, text-to-time-list, text-to-url, text-to-validation-error, text-to-validator-selector, text-to-value-type, to-boolean, to-json, to-number, to-styleable, to-validation-error-list, url, url-to-hyperlink, url-to-image\",\n" +
                "  \"dateFormatter\": \"date-format-pattern dddd, d mmmm yyyy\",\n" +
                "  \"dateParser\": \"date-parse-pattern dddd, d mmmm yyyy;dddd, d mmmm yy;dddd, d mmmm;d mmmm yyyy;d mmmm yy;d mmmm;d mmm yyyy;d mmm yy;d mmm;d/m/yy;d/m/yyyy;d/m\",\n" +
                "  \"dateTimeFormatter\": \"date-time-format-pattern dddd, d mmmm yyyy \\\\a\\\\t h:mm:ss AM/PM\",\n" +
                "  \"dateTimeOffset\": \"-25569\",\n" +
                "  \"dateTimeParser\": \"date-time-parse-pattern dd/mm/yyyy hh:mm\",\n" +
                "  \"dateTimeSymbols\": {\n" +
                "    \"ampms\": [\n" +
                "      \"am\",\n" +
                "      \"pm\"\n" +
                "    ],\n" +
                "    \"monthNames\": [\n" +
                "      \"January\",\n" +
                "      \"February\",\n" +
                "      \"March\",\n" +
                "      \"April\",\n" +
                "      \"May\",\n" +
                "      \"June\",\n" +
                "      \"July\",\n" +
                "      \"August\",\n" +
                "      \"September\",\n" +
                "      \"October\",\n" +
                "      \"November\",\n" +
                "      \"December\"\n" +
                "    ],\n" +
                "    \"monthNameAbbreviations\": [\n" +
                "      \"Jan.\",\n" +
                "      \"Feb.\",\n" +
                "      \"Mar.\",\n" +
                "      \"Apr.\",\n" +
                "      \"May\",\n" +
                "      \"Jun.\",\n" +
                "      \"Jul.\",\n" +
                "      \"Aug.\",\n" +
                "      \"Sep.\",\n" +
                "      \"Oct.\",\n" +
                "      \"Nov.\",\n" +
                "      \"Dec.\"\n" +
                "    ],\n" +
                "    \"weekDayNames\": [\n" +
                "      \"Sunday\",\n" +
                "      \"Monday\",\n" +
                "      \"Tuesday\",\n" +
                "      \"Wednesday\",\n" +
                "      \"Thursday\",\n" +
                "      \"Friday\",\n" +
                "      \"Saturday\"\n" +
                "    ],\n" +
                "    \"weekDayNameAbbreviations\": [\n" +
                "      \"Sun.\",\n" +
                "      \"Mon.\",\n" +
                "      \"Tue.\",\n" +
                "      \"Wed.\",\n" +
                "      \"Thu.\",\n" +
                "      \"Fri.\",\n" +
                "      \"Sat.\"\n" +
                "    ]\n" +
                "  },\n" +
                "  \"decimalNumberSymbols\": {\n" +
                "    \"negativeSign\": \"!\",\n" +
                "    \"positiveSign\": \"@\",\n" +
                "    \"zeroDigit\": \"0\",\n" +
                "    \"currencySymbol\": \"CURR\",\n" +
                "    \"decimalSeparator\": \".\",\n" +
                "    \"exponentSymbol\": \"e\",\n" +
                "    \"groupSeparator\": \",\",\n" +
                "    \"infinitySymbol\": \"Infinity!\",\n" +
                "    \"monetaryDecimalSeparator\": \":\",\n" +
                "    \"nanSymbol\": \"Nan!\",\n" +
                "    \"percentSymbol\": \"#\",\n" +
                "    \"permillSymbol\": \"^\"\n" +
                "  },\n" +
                "  \"defaultFormHandler\": \"non-null\",\n" +
                "  \"defaultYear\": 1900,\n" +
                "  \"exporters\": \"collection, empty, json\",\n" +
                "  \"expressionNumberKind\": \"BIG_DECIMAL\",\n" +
                "  \"findConverter\": \"collection(text, number, date-time, basic, spreadsheet-value, boolean, error-throwing, color, expression, environment, locale, spreadsheet-metadata, style, text-node, template, url)\",\n" +
                "  \"findFunctions\": \"@\",\n" +
                "  \"findHighlighting\": false,\n" +
                "  \"formHandlers\": \"basic\",\n" +
                "  \"formatters\": \"automatic, badge-error, collection, date-format-pattern, date-time-format-pattern, default-text, expression, general, number-format-pattern, spreadsheet-pattern-collection, text-format-pattern, time-format-pattern\",\n" +
                "  \"formattingConverter\": \"collection(text, number, date-time, basic, spreadsheet-value, boolean, error-throwing, color, expression, environment, locale, plugins, spreadsheet-metadata, style, text-node, template, url)\",\n" +
                "  \"formulaConverter\": \"collection(text, number, date-time, basic, spreadsheet-value, boolean, error-throwing, color, expression, environment, json, locale, template, url)\",\n" +
                "  \"formulaFunctions\": \"@test-context-loadCell, test-context-serverUrl, test-context-spreadsheet-metadata, xyz\",\n" +
                "  \"functions\": \"@\",\n" +
                "  \"generalNumberFormatDigitCount\": 9,\n" +
                "  \"hideZeroValues\": false,\n" +
                "  \"importers\": \"collection, empty, json\",\n" +
                "  \"locale\": \"en-AU\",\n" +
                "  \"numberFormatter\": \"number-format-pattern #,##0.###\",\n" +
                "  \"numberParser\": \"number-parse-pattern #,##0.###;#,##0\",\n" +
                "  \"parsers\": \"date-parse-pattern, date-time-parse-pattern, number-parse-pattern, time-parse-pattern\",\n" +
                "  \"plugins\": \"\",\n" +
                "  \"precision\": 10,\n" +
                "  \"roundingMode\": \"HALF_UP\",\n" +
                "  \"scriptingConverter\": \"collection(text, number, date-time, basic, spreadsheet-value, boolean, error-throwing, color, expression, environment, json, locale, plugins, spreadsheet-metadata, style, text-node, template, url)\",\n" +
                "  \"showFormulaEditor\": true,\n" +
                "  \"showFormulas\": false,\n" +
                "  \"showGridLines\": true,\n" +
                "  \"showHeadings\": true,\n" +
                "  \"sortComparators\": \"date,datetime,day-of-month,day-of-year,hour-of-ampm,hour-of-day,minute-of-hour,month-of-year,nano-of-second,number,seconds-of-minute,text,text-case-insensitive,time,year\",\n" +
                "  \"sortConverter\": \"collection(text, number, date-time, basic, spreadsheet-value, boolean, locale, url)\",\n" +
                "  \"style\": {\n" +
                "    \"backgroundColor\": \"white\",\n" +
                "    \"color\": \"black\",\n" +
                "    \"fontFamily\": \"MS Sans Serif\",\n" +
                "    \"fontSize\": 11,\n" +
                "    \"fontStyle\": \"NORMAL\",\n" +
                "    \"fontVariant\": \"NORMAL\",\n" +
                "    \"height\": \"30px\",\n" +
                "    \"hyphens\": \"NONE\",\n" +
                "    \"marginBottom\": \"none\",\n" +
                "    \"marginLeft\": \"none\",\n" +
                "    \"marginRight\": \"none\",\n" +
                "    \"marginTop\": \"none\",\n" +
                "    \"paddingBottom\": \"none\",\n" +
                "    \"paddingLeft\": \"none\",\n" +
                "    \"paddingRight\": \"none\",\n" +
                "    \"paddingTop\": \"none\",\n" +
                "    \"textAlign\": \"LEFT\",\n" +
                "    \"textJustify\": \"NONE\",\n" +
                "    \"verticalAlign\": \"TOP\",\n" +
                "    \"width\": \"100px\",\n" +
                "    \"wordBreak\": \"NORMAL\",\n" +
                "    \"wordWrap\": \"NORMAL\"\n" +
                "  },\n" +
                "  \"textFormatter\": \"text-format-pattern @\",\n" +
                "  \"timeFormatter\": \"time-format-pattern h:mm:ss AM/PM\",\n" +
                "  \"timeParser\": \"time-parse-pattern h:mm:ss AM/PM;h:mm:ss;h:mm:ss.0;h:mm AM/PM;h:mm\",\n" +
                "  \"twoDigitYear\": 20,\n" +
                "  \"validationConverter\": \"collection(text, number, date-time, basic, spreadsheet-value, boolean, environment, error-throwing, expression, form-and-validation, locale, plugins, template)\",\n" +
                "  \"validationFunctions\": \"@\",\n" +
                "  \"validationValidators\": \"absolute-url, collection, email-address, expression, non-null, text-length, text-mask, validation-choice-list-expression\",\n" +
                "  \"validators\": \"absolute-url, collection, email-address, expression, non-null, text-length, text-mask, validation-choice-list-expression\",\n" +
                "  \"valueSeparator\": \",\",\n" +
                "  \"viewportHome\": \"A1\"\n" +
                "}"
        );
    }

    @Test
    public void testToStringMetadataIncludingAllColorNameAndColorNumberProperties() {
        SpreadsheetMetadata metadata = METADATA;

        for (int i = SpreadsheetColors.MIN; i <= SpreadsheetColors.MAX; i++) {
            metadata = metadata.set(SpreadsheetMetadataPropertyName.numberedColor(i), Color.fromRgb(i));
        }

        this.toStringAndCheck(
            this.createContext(
                metadata,
                SpreadsheetLabelStores.treeMap()
            ),
            "serverUrl=https://example.com/path123 \"\n" +
                "\" metadata={\n" +
                "  \"autoHideScrollbars\": false,\n" +
                "  \"cellCharacterWidth\": 1,\n" +
                "  \"clipboardExporter\": \"json\",\n" +
                "  \"clipboardImporter\": \"json\",\n" +
                "  \"color1\": \"#000001\",\n" +
                "  \"color2\": \"#000002\",\n" +
                "  \"color3\": \"#000003\",\n" +
                "  \"color4\": \"#000004\",\n" +
                "  \"color5\": \"#000005\",\n" +
                "  \"color6\": \"#000006\",\n" +
                "  \"color7\": \"#000007\",\n" +
                "  \"color8\": \"#000008\",\n" +
                "  \"color9\": \"#000009\",\n" +
                "  \"color10\": \"#00000a\",\n" +
                "  \"color11\": \"#00000b\",\n" +
                "  \"color12\": \"#00000c\",\n" +
                "  \"color13\": \"#00000d\",\n" +
                "  \"color14\": \"#00000e\",\n" +
                "  \"color15\": \"#00000f\",\n" +
                "  \"color16\": \"#000010\",\n" +
                "  \"color17\": \"#001\",\n" +
                "  \"color18\": \"#000012\",\n" +
                "  \"color19\": \"#000013\",\n" +
                "  \"color20\": \"#000014\",\n" +
                "  \"color21\": \"#000015\",\n" +
                "  \"color22\": \"#000016\",\n" +
                "  \"color23\": \"#000017\",\n" +
                "  \"color24\": \"#000018\",\n" +
                "  \"color25\": \"#000019\",\n" +
                "  \"color26\": \"#00001a\",\n" +
                "  \"color27\": \"#00001b\",\n" +
                "  \"color28\": \"#00001c\",\n" +
                "  \"color29\": \"#00001d\",\n" +
                "  \"color30\": \"#00001e\",\n" +
                "  \"color31\": \"#00001f\",\n" +
                "  \"color32\": \"#000020\",\n" +
                "  \"color33\": \"#000021\",\n" +
                "  \"color34\": \"#002\",\n" +
                "  \"color35\": \"#000023\",\n" +
                "  \"color36\": \"#000024\",\n" +
                "  \"color37\": \"#000025\",\n" +
                "  \"color38\": \"#000026\",\n" +
                "  \"color39\": \"#000027\",\n" +
                "  \"color40\": \"#000028\",\n" +
                "  \"color41\": \"#000029\",\n" +
                "  \"color42\": \"#00002a\",\n" +
                "  \"color43\": \"#00002b\",\n" +
                "  \"color44\": \"#00002c\",\n" +
                "  \"color45\": \"#00002d\",\n" +
                "  \"color46\": \"#00002e\",\n" +
                "  \"color47\": \"#00002f\",\n" +
                "  \"color48\": \"#000030\",\n" +
                "  \"color49\": \"#000031\",\n" +
                "  \"color50\": \"#000032\",\n" +
                "  \"color51\": \"#003\",\n" +
                "  \"color52\": \"#000034\",\n" +
                "  \"color53\": \"#000035\",\n" +
                "  \"color54\": \"#000036\",\n" +
                "  \"color55\": \"#000037\",\n" +
                "  \"color56\": \"#000038\",\n" +
                "  \"colorBlack\": 1,\n" +
                "  \"colorBlue\": 5,\n" +
                "  \"colorCyan\": 8,\n" +
                "  \"colorGreen\": 4,\n" +
                "  \"colorMagenta\": 7,\n" +
                "  \"colorRed\": 3,\n" +
                "  \"colorWhite\": 2,\n" +
                "  \"colorYellow\": 6,\n" +
                "  \"comparators\": \"date, date-time, day-of-month, day-of-week, hour-of-am-pm, hour-of-day, minute-of-hour, month-of-year, nano-of-second, number, seconds-of-minute, text, text-case-insensitive, time, year\",\n" +
                "  \"converters\": \"basic, boolean, boolean-to-text, collection, collection-to-list, color, color-to-color, color-to-number, date-time, date-time-symbols, decimal-number-symbols, environment, error-throwing, error-to-number, expression, form-and-validation, format-pattern-to-string, has-formatter-selector, has-parser-selector, has-spreadsheet-selection, has-style, has-text-node, has-validator-selector, json, jsonTo, locale, locale-to-text, null-to-number, number, number-to-color, number-to-number, number-to-text, plugins, spreadsheet-cell-set, spreadsheet-metadata, spreadsheet-selection-to-spreadsheet-selection, spreadsheet-selection-to-text, spreadsheet-value, style, system, template, text, text-node, text-to-boolean-list, text-to-color, text-to-csv-string-list, text-to-date-list, text-to-date-time-list, text-to-environment-value-name, text-to-error, text-to-expression, text-to-form-name, text-to-json, text-to-locale, text-to-number-list, text-to-spreadsheet-color-name, text-to-spreadsheet-formatter-selector, text-to-spreadsheet-id, text-to-spreadsheet-metadata, text-to-spreadsheet-metadata-color, text-to-spreadsheet-metadata-property-name, text-to-spreadsheet-name, text-to-spreadsheet-selection, text-to-spreadsheet-text, text-to-string-list, text-to-template-value-name, text-to-text, text-to-text-node, text-to-text-style, text-to-text-style-property-name, text-to-time-list, text-to-url, text-to-validation-error, text-to-validator-selector, text-to-value-type, to-boolean, to-json, to-number, to-styleable, to-validation-error-list, url, url-to-hyperlink, url-to-image\",\n" +
                "  \"dateFormatter\": \"date-format-pattern dddd, d mmmm yyyy\",\n" +
                "  \"dateParser\": \"date-parse-pattern dddd, d mmmm yyyy;dddd, d mmmm yy;dddd, d mmmm;d mmmm yyyy;d mmmm yy;d mmmm;d mmm yyyy;d mmm yy;d mmm;d/m/yy;d/m/yyyy;d/m\",\n" +
                "  \"dateTimeFormatter\": \"date-time-format-pattern dddd, d mmmm yyyy \\\\a\\\\t h:mm:ss AM/PM\",\n" +
                "  \"dateTimeOffset\": \"-25569\",\n" +
                "  \"dateTimeParser\": \"date-time-parse-pattern dd/mm/yyyy hh:mm\",\n" +
                "  \"dateTimeSymbols\": {\n" +
                "    \"ampms\": [\n" +
                "      \"am\",\n" +
                "      \"pm\"\n" +
                "    ],\n" +
                "    \"monthNames\": [\n" +
                "      \"January\",\n" +
                "      \"February\",\n" +
                "      \"March\",\n" +
                "      \"April\",\n" +
                "      \"May\",\n" +
                "      \"June\",\n" +
                "      \"July\",\n" +
                "      \"August\",\n" +
                "      \"September\",\n" +
                "      \"October\",\n" +
                "      \"November\",\n" +
                "      \"December\"\n" +
                "    ],\n" +
                "    \"monthNameAbbreviations\": [\n" +
                "      \"Jan.\",\n" +
                "      \"Feb.\",\n" +
                "      \"Mar.\",\n" +
                "      \"Apr.\",\n" +
                "      \"May\",\n" +
                "      \"Jun.\",\n" +
                "      \"Jul.\",\n" +
                "      \"Aug.\",\n" +
                "      \"Sep.\",\n" +
                "      \"Oct.\",\n" +
                "      \"Nov.\",\n" +
                "      \"Dec.\"\n" +
                "    ],\n" +
                "    \"weekDayNames\": [\n" +
                "      \"Sunday\",\n" +
                "      \"Monday\",\n" +
                "      \"Tuesday\",\n" +
                "      \"Wednesday\",\n" +
                "      \"Thursday\",\n" +
                "      \"Friday\",\n" +
                "      \"Saturday\"\n" +
                "    ],\n" +
                "    \"weekDayNameAbbreviations\": [\n" +
                "      \"Sun.\",\n" +
                "      \"Mon.\",\n" +
                "      \"Tue.\",\n" +
                "      \"Wed.\",\n" +
                "      \"Thu.\",\n" +
                "      \"Fri.\",\n" +
                "      \"Sat.\"\n" +
                "    ]\n" +
                "  },\n" +
                "  \"decimalNumberSymbols\": {\n" +
                "    \"negativeSign\": \"!\",\n" +
                "    \"positiveSign\": \"@\",\n" +
                "    \"zeroDigit\": \"0\",\n" +
                "    \"currencySymbol\": \"CURR\",\n" +
                "    \"decimalSeparator\": \".\",\n" +
                "    \"exponentSymbol\": \"e\",\n" +
                "    \"groupSeparator\": \",\",\n" +
                "    \"infinitySymbol\": \"Infinity!\",\n" +
                "    \"monetaryDecimalSeparator\": \":\",\n" +
                "    \"nanSymbol\": \"Nan!\",\n" +
                "    \"percentSymbol\": \"#\",\n" +
                "    \"permillSymbol\": \"^\"\n" +
                "  },\n" +
                "  \"defaultFormHandler\": \"non-null\",\n" +
                "  \"defaultYear\": 1900,\n" +
                "  \"exporters\": \"collection, empty, json\",\n" +
                "  \"expressionNumberKind\": \"BIG_DECIMAL\",\n" +
                "  \"findConverter\": \"collection(text, number, date-time, basic, spreadsheet-value, boolean, error-throwing, color, expression, environment, locale, spreadsheet-metadata, style, text-node, template, url)\",\n" +
                "  \"findFunctions\": \"@\",\n" +
                "  \"findHighlighting\": false,\n" +
                "  \"formHandlers\": \"basic\",\n" +
                "  \"formatters\": \"automatic, badge-error, collection, date-format-pattern, date-time-format-pattern, default-text, expression, general, number-format-pattern, spreadsheet-pattern-collection, text-format-pattern, time-format-pattern\",\n" +
                "  \"formattingConverter\": \"collection(text, number, date-time, basic, spreadsheet-value, boolean, error-throwing, color, expression, environment, locale, plugins, spreadsheet-metadata, style, text-node, template, url)\",\n" +
                "  \"formulaConverter\": \"collection(text, number, date-time, basic, spreadsheet-value, boolean, error-throwing, color, expression, environment, json, locale, template, url)\",\n" +
                "  \"formulaFunctions\": \"@test-context-loadCell, test-context-serverUrl, test-context-spreadsheet-metadata, xyz\",\n" +
                "  \"functions\": \"@\",\n" +
                "  \"generalNumberFormatDigitCount\": 9,\n" +
                "  \"hideZeroValues\": false,\n" +
                "  \"importers\": \"collection, empty, json\",\n" +
                "  \"locale\": \"en-AU\",\n" +
                "  \"numberFormatter\": \"number-format-pattern #,##0.###\",\n" +
                "  \"numberParser\": \"number-parse-pattern #,##0.###;#,##0\",\n" +
                "  \"parsers\": \"date-parse-pattern, date-time-parse-pattern, number-parse-pattern, time-parse-pattern\",\n" +
                "  \"plugins\": \"\",\n" +
                "  \"precision\": 10,\n" +
                "  \"roundingMode\": \"HALF_UP\",\n" +
                "  \"scriptingConverter\": \"collection(text, number, date-time, basic, spreadsheet-value, boolean, error-throwing, color, expression, environment, json, locale, plugins, spreadsheet-metadata, style, text-node, template, url)\",\n" +
                "  \"showFormulaEditor\": true,\n" +
                "  \"showFormulas\": false,\n" +
                "  \"showGridLines\": true,\n" +
                "  \"showHeadings\": true,\n" +
                "  \"sortComparators\": \"date,datetime,day-of-month,day-of-year,hour-of-ampm,hour-of-day,minute-of-hour,month-of-year,nano-of-second,number,seconds-of-minute,text,text-case-insensitive,time,year\",\n" +
                "  \"sortConverter\": \"collection(text, number, date-time, basic, spreadsheet-value, boolean, locale, url)\",\n" +
                "  \"style\": {\n" +
                "    \"backgroundColor\": \"white\",\n" +
                "    \"color\": \"black\",\n" +
                "    \"fontFamily\": \"MS Sans Serif\",\n" +
                "    \"fontSize\": 11,\n" +
                "    \"fontStyle\": \"NORMAL\",\n" +
                "    \"fontVariant\": \"NORMAL\",\n" +
                "    \"height\": \"30px\",\n" +
                "    \"hyphens\": \"NONE\",\n" +
                "    \"marginBottom\": \"none\",\n" +
                "    \"marginLeft\": \"none\",\n" +
                "    \"marginRight\": \"none\",\n" +
                "    \"marginTop\": \"none\",\n" +
                "    \"paddingBottom\": \"none\",\n" +
                "    \"paddingLeft\": \"none\",\n" +
                "    \"paddingRight\": \"none\",\n" +
                "    \"paddingTop\": \"none\",\n" +
                "    \"textAlign\": \"LEFT\",\n" +
                "    \"textJustify\": \"NONE\",\n" +
                "    \"verticalAlign\": \"TOP\",\n" +
                "    \"width\": \"100px\",\n" +
                "    \"wordBreak\": \"NORMAL\",\n" +
                "    \"wordWrap\": \"NORMAL\"\n" +
                "  },\n" +
                "  \"textFormatter\": \"text-format-pattern @\",\n" +
                "  \"timeFormatter\": \"time-format-pattern h:mm:ss AM/PM\",\n" +
                "  \"timeParser\": \"time-parse-pattern h:mm:ss AM/PM;h:mm:ss;h:mm:ss.0;h:mm AM/PM;h:mm\",\n" +
                "  \"twoDigitYear\": 20,\n" +
                "  \"validationConverter\": \"collection(text, number, date-time, basic, spreadsheet-value, boolean, environment, error-throwing, expression, form-and-validation, locale, plugins, template)\",\n" +
                "  \"validationFunctions\": \"@\",\n" +
                "  \"validationValidators\": \"absolute-url, collection, email-address, expression, non-null, text-length, text-mask, validation-choice-list-expression\",\n" +
                "  \"validators\": \"absolute-url, collection, email-address, expression, non-null, text-length, text-mask, validation-choice-list-expression\",\n" +
                "  \"valueSeparator\": \",\",\n" +
                "  \"viewportHome\": \"A1\"\n" +
                "}"
        );
    }

    @Override
    public BasicSpreadsheetEngineContext createContext() {
        return this.createContext(SpreadsheetLabelStores.treeMap());
    }

    private BasicSpreadsheetEngineContext createContext(final SpreadsheetLabelStore labelStore) {
        return this.createContext(
            METADATA,
            labelStore
        );
    }

    private BasicSpreadsheetEngineContext createContext(final SpreadsheetMetadata metadata,
                                                        final SpreadsheetLabelStore labelStore) {
        return this.createContext(
            metadata,
            labelStore,
            SpreadsheetCellRangeStores.fake()
        );
    }

    private BasicSpreadsheetEngineContext createContext(final SpreadsheetMetadata metadata,
                                                        final SpreadsheetLabelStore labelStore,
                                                        final SpreadsheetCellRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRules) {
        final SpreadsheetCellStore cells = SpreadsheetCellStores.treeMap();
        cells.save(
            LOAD_CELL_REFERENCE.setFormula(
                SpreadsheetFormula.EMPTY
                    .setText("'" + LOAD_CELL_VALUE)
                    .setValue(
                        Optional.of(LOAD_CELL_VALUE)
                    )
            )
        );

        return this.createContext(
            metadata,
            cells,
            labelStore,
            rangeToConditionalFormattingRules
        );
    }


    private BasicSpreadsheetEngineContext createContext(final SpreadsheetMetadata metadata,
                                                        final SpreadsheetCellStore cellStore,
                                                        final SpreadsheetLabelStore labelStore,
                                                        final SpreadsheetCellRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRules) {
        return this.createContext(
            metadata,
            new FakeSpreadsheetStoreRepository() {

                @Override
                public SpreadsheetCellStore cells() {
                    return cellStore;
                }

                @Override
                public SpreadsheetCellRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRules() {
                    return rangeToConditionalFormattingRules;
                }

                @Override
                public SpreadsheetLabelStore labels() {
                    return labelStore;
                }

                @Override
                public Storage storage() {
                    return Storages.fake();
                }
            },
            ENVIRONMENT_CONTEXT
        );
    }

    private BasicSpreadsheetEngineContext createContext(final SpreadsheetMetadata metadata,
                                                        final SpreadsheetStoreRepository repository,
                                                        final EnvironmentContext environmentContext) {
        return BasicSpreadsheetEngineContext.with(
            SERVER_URL,
            metadata,
            repository,
            FUNCTION_ALIASES,
            environmentContext,
            LOCALE_CONTEXT,
            TERMINAL_CONTEXT,
            SPREADSHEET_PROVIDER,
            PROVIDER_CONTEXT
        );
    }

    private ExpressionNumber number(final Number value) {
        return EXPRESSION_NUMBER_KIND.create(value);
    }

    private ValueExpression<?> expression(final Number value) {
        return Expression.value(
            this.number(value)
        );
    }

    @Override
    public DateTimeContext dateTimeContext() {
        return new FakeDateTimeContext() {

            @Override
            public String toString() {
                return "DateTimeContext123";
            }
        };
    }

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return DecimalNumberContexts.basic(
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

    // SpreadsheetProviderTesting.......................................................................................

    @Override
    public BasicSpreadsheetEngineContext createSpreadsheetProvider() {
        return this.createContext();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<BasicSpreadsheetEngineContext> type() {
        return BasicSpreadsheetEngineContext.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}

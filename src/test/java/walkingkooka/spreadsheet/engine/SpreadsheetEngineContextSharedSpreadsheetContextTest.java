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
import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.Converters;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContextDelegator;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContextDelegator;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.http.server.HttpHandler;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.route.Router;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.color.SpreadsheetColors;
import walkingkooka.spreadsheet.compare.SpreadsheetComparator;
import walkingkooka.spreadsheet.compare.SpreadsheetComparators;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.format.provider.FakeSpreadsheetFormatterProviderSamplesContext;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterName;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSample;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetName;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.provider.SpreadsheetProviderDelegator;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.storage.SpreadsheetStorageContext;
import walkingkooka.spreadsheet.store.FakeSpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.store.repo.FakeSpreadsheetStoreRepository;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetErrorKind;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StoragePath;
import walkingkooka.store.Store;
import walkingkooka.terminal.TerminalContexts;
import walkingkooka.text.LineEnding;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.text.TextNode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetEngineContextSharedSpreadsheetContextTest extends SpreadsheetEngineContextSharedTestCase<SpreadsheetEngineContextSharedSpreadsheetContext> {

    private final static SpreadsheetContext SPREADSHEET_CONTEXT = new TestSpreadsheetContext();

    private final static SpreadsheetEngine SPREADSHEET_ENGINE = SpreadsheetEngines.fake();

    // with.............................................................................................................

    @Test
    public void testWithNullSpreadsheetMetadataModeFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetEngineContextSharedSpreadsheetContext.with(
                null,
                SPREADSHEET_CONTEXT,
                TERMINAL_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetEngineContextSharedSpreadsheetContext.with(
                SpreadsheetMetadataMode.FORMULA,
                null,
                TERMINAL_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullTerminalContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetEngineContextSharedSpreadsheetContext.with(
                SpreadsheetMetadataMode.FORMULA,
                SPREADSHEET_CONTEXT,
                null
            )
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

    @Test
    public void testParseFormulaWithCellWithoutParser() {
        final String text = "12:58:59";

        this.parseFormulaAndCheck(
            this.createContext(),
            text,
            Optional.of(
                SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
            ),
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
    public void testParseFormulaWithCellParser() {
        final String text = "12::58::";

        this.parseFormulaAndCheck(
            this.createContext(),
            text,
            Optional.of(
                SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                    .setParser(
                        Optional.of(
                            SpreadsheetPattern.parseTimeParsePattern("hh::mm::")
                                .spreadsheetParserSelector()
                        )
                    )
            ),
            SpreadsheetFormulaParserToken.time(
                Lists.of(
                    SpreadsheetFormulaParserToken.hour(12, "12"),
                    SpreadsheetFormulaParserToken.textLiteral("::", "::"),
                    SpreadsheetFormulaParserToken.minute(58, "58"),
                    SpreadsheetFormulaParserToken.textLiteral("::", "::")
                ),
                text
            )
        );
    }

    // toExpression.....................................................................................................

    @Test
    public void testToExpression2() {
        final SpreadsheetEngineContextSharedSpreadsheetContext context = this.createContext();

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
                SpreadsheetLabelStores.fake()
            ),
            cell,
            formatter,
            cell.setFormula(
                cell.formula()
                    .setError(
                        Optional.of(
                            SpreadsheetErrorKind.FORMATTING.setMessageAndValue(
                                "Unknown formatter unknown-formatter-404",
                                formatter
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
                SpreadsheetLabelStores.fake()
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
                SpreadsheetLabelStores.fake()
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

    // SpreadsheetFormatterProvider.....................................................................................

    // Default
    //  text
    //    "@"
    //  Hello 123
    @Test
    public void testSpreadsheetFormatterSamples() {
        final SpreadsheetFormatterSelector selector = SpreadsheetFormatterName.TEXT.setValueText("@@");

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
                SpreadsheetFormatterName.TEXT.setValueText("@"),
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

    // saveMetadata.....................................................................................................

    @Test
    public void testSaveMetadata() {
        final SpreadsheetEngineContextSharedSpreadsheetContext context = this.createContext();

        final SpreadsheetMetadata saved = context.saveMetadata(
            METADATA.set(
                SpreadsheetMetadataPropertyName.SPREADSHEET_NAME,
                SpreadsheetName.with(this.getClass().getName())
            ).set(
                SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                context.spreadsheetIdOrFail()
            )
        );

        this.spreadsheetMetadataAndCheck(
            context,
            saved
        );
    }

    @Test
    public void testSaveMetadataDifferent() {
        final SpreadsheetEngineContextSharedSpreadsheetContext context = this.createContext();

        final SpreadsheetId spreadsheetId = SpreadsheetId.with(999);

        this.checkNotEquals(
            spreadsheetId,
            context.spreadsheetId()
        );

        final SpreadsheetMetadata saved = context.saveMetadata(
            METADATA.set(
                SpreadsheetMetadataPropertyName.SPREADSHEET_NAME,
                SpreadsheetName.with(this.getClass().getName())
            ).set(
                SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                spreadsheetId
            )
        );

        this.checkNotEquals(
            saved,
            context.spreadsheetMetadata()
        );
    }

    @Test
    public void testSpreadsheetEngine() {
        this.spreadsheetEngineAndCheck(
            this.createContext(),
            SPREADSHEET_ENGINE
        );
    }

    // createContext....................................................................................................
    
    @Override
    SpreadsheetEngineContextSharedSpreadsheetContext createContext(final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext) {
        return this.createContext(
            METADATA,
            spreadsheetEnvironmentContext,
            PROVIDER_CONTEXT
        );
    }

    private SpreadsheetEngineContextSharedSpreadsheetContext createContext(final SpreadsheetMetadata metadata,
                                                                           final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                                           final ProviderContext providerContext) {
        return SpreadsheetEngineContextSharedSpreadsheetContext.with(
            SpreadsheetMetadataMode.FORMULA,
            new TestSpreadsheetContext(
                metadata,
                SpreadsheetStoreRepositories.treeMap(
                    SpreadsheetMetadataStores.fake()
                ),
                spreadsheetEnvironmentContext,
                LOCALE_CONTEXT,
                providerContext
            ),
            TERMINAL_CONTEXT
        );
    }

    private SpreadsheetEngineContextSharedSpreadsheetContext createContext(final SpreadsheetLabelStore labelStore) {
        return this.createContext(
            METADATA,
            labelStore
        );
    }

    private SpreadsheetEngineContextSharedSpreadsheetContext createContext(final SpreadsheetMetadata metadata,
                                                                           final SpreadsheetLabelStore labelStore) {
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
            SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment()
        );
    }


    private SpreadsheetEngineContextSharedSpreadsheetContext createContext(final SpreadsheetMetadata metadata,
                                                                           final SpreadsheetCellStore cellStore,
                                                                           final SpreadsheetLabelStore labelStore,
                                                                           final EnvironmentContext environmentContext) {
        return this.createContext(
            metadata,
            new FakeSpreadsheetStoreRepository() {

                @Override
                public SpreadsheetCellStore cells() {
                    return cellStore;
                }

                @Override
                public SpreadsheetLabelStore labels() {
                    return labelStore;
                }
            },
            environmentContext
        );
    }

    // createContext....................................................................................................

    @Override
    public SpreadsheetEngineContextSharedSpreadsheetContext createContext() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

        spreadsheetEnvironmentContext.setCurrentWorkingDirectory(
            Optional.of(CURRENT_WORKING_DIRECTORY)
        );
        spreadsheetEnvironmentContext.setSpreadsheetId(
            Optional.of(SPREADSHEET_ID)
        );


        return this.createContext(spreadsheetEnvironmentContext);
    }

    private SpreadsheetEngineContextSharedSpreadsheetContext createContext(final SpreadsheetMetadata metadata,
                                                                           final SpreadsheetStoreRepository storeRepository,
                                                                           final EnvironmentContext environmentContext) {
        return SpreadsheetEngineContextSharedSpreadsheetContext.with(
            SpreadsheetMetadataMode.FORMULA,
            new TestSpreadsheetContext(
                metadata,
                storeRepository,
                environmentContext,
                LOCALE_CONTEXT,
                PROVIDER_CONTEXT
            ),
            TERMINAL_CONTEXT
        );
    }

    private static EnvironmentContext environmentContext() {
        final EnvironmentContext context = EnvironmentContexts.map(
            EnvironmentContexts.empty(
                INDENTATION,
                LineEnding.NL,
                Locale.forLanguageTag("en-AU"),
                LocalDateTime::now,
                EnvironmentContext.ANONYMOUS
            )
        );
        context.setEnvironmentValue(
            SpreadsheetEnvironmentContext.CURRENT_WORKING_DIRECTORY,
            SpreadsheetEngineContextSharedSpreadsheetContextTest.CURRENT_WORKING_DIRECTORY
        );
        context.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SERVER_URL,
            SpreadsheetEngineContextSharedSpreadsheetContextTest.SERVER_URL
        );
        return context;
    }

    private final static class TestSpreadsheetContext implements SpreadsheetContext,
        EnvironmentContextDelegator,
        LocaleContextDelegator,
        SpreadsheetProviderDelegator {

        private final static SpreadsheetStoreRepository REPO = new FakeSpreadsheetStoreRepository() {
            @Override
            public SpreadsheetLabelStore labels() {
                return new FakeSpreadsheetLabelStore();
            }
        };

        private final static LocaleContext LOCALE_CONTEXT = LocaleContexts.fake();

        TestSpreadsheetContext() {
            this(
                SpreadsheetEngineContextSharedSpreadsheetContextTest.environmentContext()
            );
        }

        TestSpreadsheetContext(final EnvironmentContext environmentContext) {
            this.metadata = METADATA;
            this.storeRepository = REPO;

            this.environmentContext = environmentContext;
            this.localeContext = LOCALE_CONTEXT;
            this.providerContext = PROVIDER_CONTEXT;
        }

        TestSpreadsheetContext(final SpreadsheetMetadata metadata,
                               final SpreadsheetStoreRepository storeRepository,
                               final EnvironmentContext environmentContext,
                               final LocaleContext localeContext,
                               final ProviderContext providerContext) {
            final SpreadsheetId spreadsheetId = metadata.getOrFail(SpreadsheetMetadataPropertyName.SPREADSHEET_ID);
            if (false == SpreadsheetEngineContextSharedSpreadsheetContextTest.SPREADSHEET_ID.equals(spreadsheetId)) {
                throw new IllegalArgumentException("Invalid SpreadsheetId: " + spreadsheetId + " expected " + SPREADSHEET_ID);
            }
            this.metadata = metadata;
            this.storeRepository = storeRepository;

            this.environmentContext = environmentContext;
            this.localeContext = localeContext;
            this.providerContext = providerContext;
        }

        @Override
        public SpreadsheetStoreRepository storeRepository() {
            return this.storeRepository;
        }

        private final SpreadsheetStoreRepository storeRepository;

        @Override
        public SpreadsheetEngineContext spreadsheetEngineContext() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Router<HttpRequestAttribute<?>, HttpHandler> httpRouter() {
            throw new UnsupportedOperationException();
        }

        @Override
        public SpreadsheetMetadata spreadsheetMetadata() {
            return this.loadMetadataOrFail(this.spreadsheetIdOrFail());
        }

        @Override
        public SpreadsheetMetadata createMetadata(final EmailAddress user,
                                                  final Optional<Locale> locale) {
            Objects.requireNonNull(user, "user");
            Objects.requireNonNull(locale, "locale");

            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<SpreadsheetMetadata> loadMetadata(final SpreadsheetId id) {
            return Optional.ofNullable(
                this.metadata.get(
                    SpreadsheetMetadataPropertyName.SPREADSHEET_ID
                ).equals(Optional.of(id)) ?
                    this.metadata :
                    null
            );
        }

        @Override
        public SpreadsheetMetadata saveMetadata(final SpreadsheetMetadata metadata) {
            Objects.requireNonNull(metadata, "metadata");

            this.metadata = metadata;
            return metadata;
        }

        private SpreadsheetMetadata metadata;


        @Override
        public void deleteMetadata(final SpreadsheetId id) {
            Objects.requireNonNull(id, "id");
            throw new UnsupportedOperationException();
        }

        @Override
        public List<SpreadsheetMetadata> findMetadataBySpreadsheetName(final String name,
                                                                       final int offset,
                                                                       final int count) {
            Objects.requireNonNull(name, "name");
            Store.checkOffsetAndCount(offset, count);

            throw new UnsupportedOperationException();
        }

        @Override
        public SpreadsheetContext cloneEnvironment() {
            return new TestSpreadsheetContext(
                this.metadata,
                this.storeRepository,
                this.environmentContext.cloneEnvironment(),
                this.localeContext,
                this.providerContext
            );
        }

        @Override
        public Optional<StoragePath> currentWorkingDirectory() {
            return this.environmentValue(CURRENT_WORKING_DIRECTORY);
        }

        @Override
        public void setCurrentWorkingDirectory(final Optional<StoragePath> currentWorkingDirectory) {
            this.setOrRemoveEnvironmentValue(
                CURRENT_WORKING_DIRECTORY,
                currentWorkingDirectory
            );
        }

        @Override
        public AbsoluteUrl serverUrl() {
            return SpreadsheetEngineContextSharedSpreadsheetContextTest.SERVER_URL;
        }

        @Override
        public Optional<SpreadsheetId> spreadsheetId() {
            return Optional.of(
                SpreadsheetEngineContextSharedSpreadsheetContextTest.SPREADSHEET_ID
            );
        }

        @Override
        public void setSpreadsheetId(final Optional<SpreadsheetId> id) {
            Objects.requireNonNull(id, "id");

            if (false == this.spreadsheetId().equals(id)) {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        public SpreadsheetContext setEnvironmentContext(final EnvironmentContext environmentContext) {
            Objects.requireNonNull(environmentContext, "environmentContext");

            return this.environmentContext.equals(environmentContext) ?
                this :
                new TestSpreadsheetContext(
                    this.metadata,
                    this.storeRepository,
                    environmentContext,
                    this.localeContext,
                    this.providerContext
                );
        }

        @Override
        public <T> void setEnvironmentValue(final EnvironmentValueName<T> name,
                                            final T value) {
            this.environmentContext.setEnvironmentValue(
                name,
                value
            );
        }

        @Override
        public void removeEnvironmentValue(final EnvironmentValueName<?> name) {
            this.environmentContext.removeEnvironmentValue(name);
        }

        @Override
        public LineEnding lineEnding() {
            return this.environmentContext.lineEnding();
        }

        @Override
        public void setLineEnding(final LineEnding lineEnding) {
            this.environmentContext.setLineEnding(lineEnding);
        }

        @Override
        public Locale locale() {
            return this.environmentContext.locale();
        }

        @Override
        public void setLocale(final Locale locale) {
            this.environmentContext.setLocale(locale);
        }

        @Override
        public void setUser(final Optional<EmailAddress> user) {
            this.environmentContext.setUser(user);
        }

        @Override
        public EnvironmentContext environmentContext() {
            return this.environmentContext;
        }

        private final EnvironmentContext environmentContext;

        @Override
        public Storage<SpreadsheetStorageContext> storage() {
            return STORAGE;
        }

        @Override
        public LocaleContext localeContext() {
            return this.localeContext;
        }

        private final LocaleContext localeContext;

        @Override
        public ProviderContext providerContext() {
            return providerContext;
        }

        private final ProviderContext providerContext;

        @Override
        public SpreadsheetProvider spreadsheetProvider() {
            return SPREADSHEET_PROVIDER;
        }

        @Override
        public SpreadsheetEngine spreadsheetEngine() {
            return SPREADSHEET_ENGINE;
        }

        // Object.......................................................................................................

        @Override
        public int hashCode() {
            return Objects.hash(
                this.metadata,
                this.storeRepository,
                this.environmentContext,
                this.localeContext,
                this.providerContext
            );
        }

        @Override
        public boolean equals(final Object other) {
            return this == other ||
                (other instanceof TestSpreadsheetContext &&
                    this.equals0((TestSpreadsheetContext) other));
        }

        private boolean equals0(final TestSpreadsheetContext other) {
            return this.metadata.equals(other.metadata) &&
                this.storeRepository.equals(other.storeRepository) &&
                this.environmentContext.equals(other.environmentContext) &&
                this.localeContext.equals(other.localeContext) &&
                this.providerContext.equals(other.providerContext);
        }
    }

    // hashCode/equals..................................................................................................

    @Test
    @Override
    public void testEquals() {
        this.checkEquals(
            SpreadsheetEngineContextSharedSpreadsheetContext.with(
                SpreadsheetMetadataMode.FORMULA,
                SPREADSHEET_CONTEXT,
                TERMINAL_CONTEXT
            ),
            SpreadsheetEngineContextSharedSpreadsheetContext.with(
                SpreadsheetMetadataMode.FORMULA,
                SPREADSHEET_CONTEXT,
                TERMINAL_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsDifferentMode() {
        this.checkNotEquals(
            SpreadsheetEngineContextSharedSpreadsheetContext.with(
                SpreadsheetMetadataMode.FORMULA,
                SPREADSHEET_CONTEXT,
                TERMINAL_CONTEXT
            ),
            SpreadsheetEngineContextSharedSpreadsheetContext.with(
                SpreadsheetMetadataMode.FIND,
                SPREADSHEET_CONTEXT,
                TERMINAL_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsDifferentSpreadsheetContext() {
        this.checkNotEquals(
            SpreadsheetEngineContextSharedSpreadsheetContext.with(
                SpreadsheetMetadataMode.FORMULA,
                SPREADSHEET_CONTEXT,
                TERMINAL_CONTEXT
            ),
            SpreadsheetEngineContextSharedSpreadsheetContext.with(
                SpreadsheetMetadataMode.FORMULA,
                new TestSpreadsheetContext(
                    EnvironmentContexts.map(
                        EnvironmentContexts.empty(
                            INDENTATION,
                            LineEnding.CRNL,
                            Locale.FRANCE,
                            LocalDateTime::now,
                            EnvironmentContext.ANONYMOUS
                        )
                    )
                ),
                TERMINAL_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsDifferentTerminalContext() {
        this.checkNotEquals(
            SpreadsheetEngineContextSharedSpreadsheetContext.with(
                SpreadsheetMetadataMode.FORMULA,
                SPREADSHEET_CONTEXT,
                TERMINAL_CONTEXT
            ),
            SpreadsheetEngineContextSharedSpreadsheetContext.with(
                SpreadsheetMetadataMode.FORMULA,
                SPREADSHEET_CONTEXT,
                TerminalContexts.fake()
            )
        );
    }

    @Override
    public SpreadsheetEngineContextSharedSpreadsheetContext createObject() {
        return this.createContext();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createContext(),
            "mode=FORMULA metadata={\n" +
                "  \"spreadsheetId\": \"7b\",\n" +
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
                "  \"converters\": \"basic, boolean, boolean-to-text, collection, collection-to, collection-to-list, color, color-to-color, color-to-number, date-time, date-time-symbols, decimal-number-symbols, environment, error-throwing, error-to-error, error-to-number, expression, form-and-validation, format-pattern-to-string, has-formatter-selector, has-host-address, has-parser-selector, has-spreadsheet-selection, has-style, has-text-node, has-validator-selector, json, json-to, locale, locale-to-text, net, null-to-number, number, number-to-color, number-to-number, number-to-text, optional-to, plugins, spreadsheet-cell-set, spreadsheet-metadata, spreadsheet-selection-to-spreadsheet-selection, spreadsheet-selection-to-text, spreadsheet-value, storage, storage-path-json-to-class, storage-value-info-list-to-text, style, system, template, text, text-node, text-to-boolean-list, text-to-color, text-to-csv-string-list, text-to-date-list, text-to-date-time-list, text-to-email-address, text-to-environment-value-name, text-to-error, text-to-expression, text-to-form-name, text-to-has-host-address, text-to-host-address, text-to-json, text-to-line-ending, text-to-locale, text-to-number-list, text-to-object, text-to-spreadsheet-color-name, text-to-spreadsheet-formatter-selector, text-to-spreadsheet-id, text-to-spreadsheet-metadata, text-to-spreadsheet-metadata-color, text-to-spreadsheet-metadata-property-name, text-to-spreadsheet-name, text-to-spreadsheet-selection, text-to-spreadsheet-text, text-to-storage-path, text-to-string-list, text-to-template-value-name, text-to-text, text-to-text-node, text-to-text-style, text-to-text-style-property-name, text-to-time-list, text-to-url, text-to-url-fragment, text-to-url-query-string, text-to-validation-error, text-to-validator-selector, text-to-value-type, text-to-zone-offset, to-boolean, to-json-node, to-json-text, to-number, to-string, to-styleable, to-validation-checkbox, to-validation-choice, to-validation-choice-list, to-validation-error-list, url, url-to-hyperlink, url-to-image\",\n" +
                "  \"dateFormatter\": \"date dddd, d mmmm yyyy\",\n" +
                "  \"dateParser\": \"date dddd, d mmmm yyyy;dddd, d mmmm yy;dddd, d mmmm;d mmmm yyyy;d mmmm yy;d mmmm;d mmm yyyy;d mmm yy;d mmm;d/m/yy;d/m/yyyy;d/m\",\n" +
                "  \"dateTimeFormatter\": \"date-time dddd, d mmmm yyyy \\\\a\\\\t h:mm:ss AM/PM\",\n" +
                "  \"dateTimeOffset\": \"-25569\",\n" +
                "  \"dateTimeParser\": \"date-time dd/mm/yyyy hh:mm\",\n" +
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
                "  \"decimalNumberDigitCount\": 9,\n" +
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
                "  \"errorFormatter\": \"badge-error default-text\",\n" +
                "  \"exporters\": \"collection, empty, json\",\n" +
                "  \"expressionNumberKind\": \"BIG_DECIMAL\",\n" +
                "  \"findConverter\": \"collection(text, boolean, number, date-time, basic, spreadsheet-value, error-throwing, color, expression, environment, locale, spreadsheet-metadata, style, text-node, template, net)\",\n" +
                "  \"findFunctions\": \"@\",\n" +
                "  \"findHighlighting\": false,\n" +
                "  \"formHandlers\": \"basic\",\n" +
                "  \"formatters\": \"accounting, automatic, badge-error, collection, currency, date, date-time, default-text, expression, full-date, full-date-time, full-time, general, hyperlinking, long-date, long-date-time, long-time, medium-date, medium-date-time, medium-time, number, percent, scientific, short-date, short-date-time, short-time, text, time\",\n" +
                "  \"formattingConverter\": \"collection(text, boolean, number, date-time, basic, spreadsheet-value, error-throwing, color, expression, environment, locale, plugins, spreadsheet-metadata, style, text-node, template, net)\",\n" +
                "  \"formulaConverter\": \"collection(text, boolean, number, date-time, basic, spreadsheet-value, error-throwing, color, expression, environment, json, locale, template, net)\",\n" +
                "  \"formulaFunctions\": \"@test-context-loadCell, test-context-serverUrl, test-context-spreadsheet-metadata, xyz\",\n" +
                "  \"functions\": \"@\",\n" +
                "  \"hideZeroValues\": false,\n" +
                "  \"importers\": \"collection, empty, json\",\n" +
                "  \"locale\": \"en-AU\",\n" +
                "  \"numberFormatter\": \"number #,##0.###\",\n" +
                "  \"numberParser\": \"number #,##0.###;#,##0\",\n" +
                "  \"parsers\": \"date, date-time, general, number, time, whole-number\",\n" +
                "  \"plugins\": \"\",\n" +
                "  \"precision\": 10,\n" +
                "  \"roundingMode\": \"HALF_UP\",\n" +
                "  \"scriptingConverter\": \"collection(text, boolean, number, date-time, basic, spreadsheet-value, error-throwing, color, expression, environment, json, locale, plugins, spreadsheet-metadata, storage, storage-path-json-to-class, style, text-node, text-to-line-ending, template, net)\",\n" +
                "  \"showFormulaEditor\": true,\n" +
                "  \"showFormulas\": false,\n" +
                "  \"showGridLines\": true,\n" +
                "  \"showHeadings\": true,\n" +
                "  \"sortComparators\": \"date,datetime,day-of-month,day-of-year,hour-of-ampm,hour-of-day,minute-of-hour,month-of-year,nano-of-second,number,seconds-of-minute,text,text-case-insensitive,time,year\",\n" +
                "  \"sortConverter\": \"collection(text, boolean, number, date-time, basic, spreadsheet-value, locale, url)\",\n" +
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
                "  \"textFormatter\": \"text @\",\n" +
                "  \"timeFormatter\": \"time h:mm:ss AM/PM\",\n" +
                "  \"timeParser\": \"time h:mm:ss AM/PM;h:mm:ss;h:mm:ss.0;h:mm AM/PM;h:mm\",\n" +
                "  \"twoDigitYear\": 20,\n" +
                "  \"validationConverter\": \"collection(text, boolean, number, date-time, basic, spreadsheet-value, environment, error-throwing, expression, form-and-validation, locale, plugins, template, json)\",\n" +
                "  \"validationFunctions\": \"@\",\n" +
                "  \"validationValidators\": \"absolute-url, checkbox, choice-list, collection, email-address, expression, non-null, text-length, text-mask\",\n" +
                "  \"validators\": \"absolute-url, checkbox, choice-list, collection, email-address, expression, non-null, text-length, text-mask\",\n" +
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
            "mode=FORMULA metadata={\n" +
                "  \"spreadsheetId\": \"7b\",\n" +
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
                "  \"converters\": \"basic, boolean, boolean-to-text, collection, collection-to, collection-to-list, color, color-to-color, color-to-number, date-time, date-time-symbols, decimal-number-symbols, environment, error-throwing, error-to-error, error-to-number, expression, form-and-validation, format-pattern-to-string, has-formatter-selector, has-host-address, has-parser-selector, has-spreadsheet-selection, has-style, has-text-node, has-validator-selector, json, json-to, locale, locale-to-text, net, null-to-number, number, number-to-color, number-to-number, number-to-text, optional-to, plugins, spreadsheet-cell-set, spreadsheet-metadata, spreadsheet-selection-to-spreadsheet-selection, spreadsheet-selection-to-text, spreadsheet-value, storage, storage-path-json-to-class, storage-value-info-list-to-text, style, system, template, text, text-node, text-to-boolean-list, text-to-color, text-to-csv-string-list, text-to-date-list, text-to-date-time-list, text-to-email-address, text-to-environment-value-name, text-to-error, text-to-expression, text-to-form-name, text-to-has-host-address, text-to-host-address, text-to-json, text-to-line-ending, text-to-locale, text-to-number-list, text-to-object, text-to-spreadsheet-color-name, text-to-spreadsheet-formatter-selector, text-to-spreadsheet-id, text-to-spreadsheet-metadata, text-to-spreadsheet-metadata-color, text-to-spreadsheet-metadata-property-name, text-to-spreadsheet-name, text-to-spreadsheet-selection, text-to-spreadsheet-text, text-to-storage-path, text-to-string-list, text-to-template-value-name, text-to-text, text-to-text-node, text-to-text-style, text-to-text-style-property-name, text-to-time-list, text-to-url, text-to-url-fragment, text-to-url-query-string, text-to-validation-error, text-to-validator-selector, text-to-value-type, text-to-zone-offset, to-boolean, to-json-node, to-json-text, to-number, to-string, to-styleable, to-validation-checkbox, to-validation-choice, to-validation-choice-list, to-validation-error-list, url, url-to-hyperlink, url-to-image\",\n" +
                "  \"dateFormatter\": \"date dddd, d mmmm yyyy\",\n" +
                "  \"dateParser\": \"date dddd, d mmmm yyyy;dddd, d mmmm yy;dddd, d mmmm;d mmmm yyyy;d mmmm yy;d mmmm;d mmm yyyy;d mmm yy;d mmm;d/m/yy;d/m/yyyy;d/m\",\n" +
                "  \"dateTimeFormatter\": \"date-time dddd, d mmmm yyyy \\\\a\\\\t h:mm:ss AM/PM\",\n" +
                "  \"dateTimeOffset\": \"-25569\",\n" +
                "  \"dateTimeParser\": \"date-time dd/mm/yyyy hh:mm\",\n" +
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
                "  \"decimalNumberDigitCount\": 9,\n" +
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
                "  \"errorFormatter\": \"badge-error default-text\",\n" +
                "  \"exporters\": \"collection, empty, json\",\n" +
                "  \"expressionNumberKind\": \"BIG_DECIMAL\",\n" +
                "  \"findConverter\": \"collection(text, boolean, number, date-time, basic, spreadsheet-value, error-throwing, color, expression, environment, locale, spreadsheet-metadata, style, text-node, template, net)\",\n" +
                "  \"findFunctions\": \"@\",\n" +
                "  \"findHighlighting\": false,\n" +
                "  \"formHandlers\": \"basic\",\n" +
                "  \"formatters\": \"accounting, automatic, badge-error, collection, currency, date, date-time, default-text, expression, full-date, full-date-time, full-time, general, hyperlinking, long-date, long-date-time, long-time, medium-date, medium-date-time, medium-time, number, percent, scientific, short-date, short-date-time, short-time, text, time\",\n" +
                "  \"formattingConverter\": \"collection(text, boolean, number, date-time, basic, spreadsheet-value, error-throwing, color, expression, environment, locale, plugins, spreadsheet-metadata, style, text-node, template, net)\",\n" +
                "  \"formulaConverter\": \"collection(text, boolean, number, date-time, basic, spreadsheet-value, error-throwing, color, expression, environment, json, locale, template, net)\",\n" +
                "  \"formulaFunctions\": \"@test-context-loadCell, test-context-serverUrl, test-context-spreadsheet-metadata, xyz\",\n" +
                "  \"functions\": \"@\",\n" +
                "  \"hideZeroValues\": false,\n" +
                "  \"importers\": \"collection, empty, json\",\n" +
                "  \"locale\": \"en-AU\",\n" +
                "  \"numberFormatter\": \"number #,##0.###\",\n" +
                "  \"numberParser\": \"number #,##0.###;#,##0\",\n" +
                "  \"parsers\": \"date, date-time, general, number, time, whole-number\",\n" +
                "  \"plugins\": \"\",\n" +
                "  \"precision\": 10,\n" +
                "  \"roundingMode\": \"HALF_UP\",\n" +
                "  \"scriptingConverter\": \"collection(text, boolean, number, date-time, basic, spreadsheet-value, error-throwing, color, expression, environment, json, locale, plugins, spreadsheet-metadata, storage, storage-path-json-to-class, style, text-node, text-to-line-ending, template, net)\",\n" +
                "  \"showFormulaEditor\": true,\n" +
                "  \"showFormulas\": false,\n" +
                "  \"showGridLines\": true,\n" +
                "  \"showHeadings\": true,\n" +
                "  \"sortComparators\": \"date,datetime,day-of-month,day-of-year,hour-of-ampm,hour-of-day,minute-of-hour,month-of-year,nano-of-second,number,seconds-of-minute,text,text-case-insensitive,time,year\",\n" +
                "  \"sortConverter\": \"collection(text, boolean, number, date-time, basic, spreadsheet-value, locale, url)\",\n" +
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
                "  \"textFormatter\": \"text @\",\n" +
                "  \"timeFormatter\": \"time h:mm:ss AM/PM\",\n" +
                "  \"timeParser\": \"time h:mm:ss AM/PM;h:mm:ss;h:mm:ss.0;h:mm AM/PM;h:mm\",\n" +
                "  \"twoDigitYear\": 20,\n" +
                "  \"validationConverter\": \"collection(text, boolean, number, date-time, basic, spreadsheet-value, environment, error-throwing, expression, form-and-validation, locale, plugins, template, json)\",\n" +
                "  \"validationFunctions\": \"@\",\n" +
                "  \"validationValidators\": \"absolute-url, checkbox, choice-list, collection, email-address, expression, non-null, text-length, text-mask\",\n" +
                "  \"validators\": \"absolute-url, checkbox, choice-list, collection, email-address, expression, non-null, text-length, text-mask\",\n" +
                "  \"valueSeparator\": \",\",\n" +
                "  \"viewportHome\": \"A1\"\n" +
                "}"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetEngineContextSharedSpreadsheetContext> type() {
        return SpreadsheetEngineContextSharedSpreadsheetContext.class;
    }

    @Override
    public String typeNameSuffix() {
        return SpreadsheetContext.class.getSimpleName();
    }
}

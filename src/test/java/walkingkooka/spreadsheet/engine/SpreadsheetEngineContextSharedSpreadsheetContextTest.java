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
import walkingkooka.Binary;
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.color.Color;
import walkingkooka.convert.BinaryNumberConverterFunction;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.Converters;
import walkingkooka.currency.CurrencyContext;
import walkingkooka.currency.CurrencyContextDelegator;
import walkingkooka.environment.AuditInfo;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContextDelegator;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.header.MediaTypeDetectors;
import walkingkooka.net.http.server.HttpHandler;
import walkingkooka.net.http.server.HttpHandlerContext;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.route.Router;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.color.SpreadsheetColors;
import walkingkooka.spreadsheet.compare.SpreadsheetComparator;
import walkingkooka.spreadsheet.compare.SpreadsheetComparators;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextDelegator;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContexts;
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
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.meta.SpreadsheetName;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.provider.SpreadsheetProviderDelegator;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
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
import walkingkooka.spreadsheet.validation.SpreadsheetValidationReference;
import walkingkooka.spreadsheet.validation.form.SpreadsheetForms;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetErrorKind;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.Storages;
import walkingkooka.store.Store;
import walkingkooka.store.StoreWatcher;
import walkingkooka.terminal.TerminalContexts;
import walkingkooka.text.LineEnding;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.convert.ExpressionNumberBinaryNumberConverterFunctions;
import walkingkooka.tree.text.TextNode;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.FormName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetEngineContextSharedSpreadsheetContextTest extends SpreadsheetEngineContextSharedTestCase<SpreadsheetEngineContextSharedSpreadsheetContext> {

    private final static SpreadsheetContext SPREADSHEET_CONTEXT = new TestSpreadsheetContext();

    private final static SpreadsheetEngine SPREADSHEET_ENGINE = SpreadsheetEngines.basic();

    static {
        final SpreadsheetEnvironmentContext context = SpreadsheetMetadataTesting.SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();
        context.setSpreadsheetId(
                Optional.of(SPREADSHEET_ID)
            );

        SPREADSHEET_ENVIRONMENT_CONTEXT = context;
    }

    private final static SpreadsheetEnvironmentContext SPREADSHEET_ENVIRONMENT_CONTEXT;

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

    // setEnvironmentContext............................................................................................

    @Test
    @Override
    public void testSetEnvironmentContextWithEqualEnvironmentContext() {
        final SpreadsheetEngineContextSharedSpreadsheetContext before = this.createContext();

        final EnvironmentContext after = before.setEnvironmentContext(SPREADSHEET_ENVIRONMENT_CONTEXT);

        assertNotSame(
            before,
            after
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

        context.saveMetadata(
            METADATA.set(
                SpreadsheetMetadataPropertyName.SPREADSHEET_NAME,
                SpreadsheetName.with(this.getClass().getName())
            ).set(
                SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                spreadsheetId
            )
        );
    }

    @Test
    public void testCreateSpreadsheet() {
        final SpreadsheetEngineContextSharedSpreadsheetContext context = this.createContext();

        final EmailAddress user = EmailAddress.parse("different@example.com");
        this.checkNotEquals(
            USER,
            user,
            "user"
        );

        final Locale locale = Locale.forLanguageTag("FR");
        this.checkNotEquals(
            LOCALE,
            locale,
            "locale"
        );

        final SpreadsheetMetadata metadata = context.createMetadata(
            user,
            Optional.of(locale)
        );

        this.checkNotEquals(
            null,
            metadata
        );

        final SpreadsheetId spreadsheetId = metadata.getOrFail(SpreadsheetMetadataPropertyName.SPREADSHEET_ID);

        this.checkNotEquals(
            null,
            spreadsheetId,
            "spreadsheetId"
        );

        this.localeAndCheck(
            metadata,
            locale
        );

        this.checkEquals(
            user,
            metadata.getOrFail(SpreadsheetMetadataPropertyName.AUDIT_INFO)
                .createdBy(),
            "createdBy"
        );

        this.loadMetadataAndCheck(
            context,
            spreadsheetId,
            metadata
        );
    }

    @Test
    public void testSpreadsheetEngine() {
        this.spreadsheetEngineAndCheck(
            this.createContext(),
            SPREADSHEET_ENGINE
        );
    }

    // SpreadsheetStorageContext........................................................................................

    private final static SpreadsheetCell UNSAVED_CELL = SpreadsheetSelection.A1.setFormula(
        SpreadsheetFormula.EMPTY.setValue(
            Optional.of("Hello World")
        )
    );

    private final static SpreadsheetCell SAVED_CELL = UNSAVED_CELL.setFormattedValue(
        Optional.of(
            TextNode.text("Hello World")
        )
    );

    @Test
    public void testLoadCells() {
        final SpreadsheetEngineContextSharedSpreadsheetContext context = this.createContext();

        this.loadCellsAndCheck(
            context,
            SpreadsheetSelection.A1
        );
    }

    @Test
    public void testSaveCellsAndLoadCells() {
        final SpreadsheetEngineContextSharedSpreadsheetContext context = this.createContext();

        context.saveCells(
            Sets.of(UNSAVED_CELL)
        );

        this.loadCellsAndCheck(
            context,
            SpreadsheetSelection.A1,
            SAVED_CELL
        );
    }

    @Test
    public void testDeleteCells() {
        final SpreadsheetEngineContextSharedSpreadsheetContext context = this.createContext();

        final SpreadsheetCell b2 = SpreadsheetSelection.parseCell("B2")
            .setFormula(
                SpreadsheetFormula.EMPTY.setValue(
                    Optional.of("Hello World B2")
                )
            );

        context.saveCells(
            Sets.of(
                UNSAVED_CELL,
                b2
            )
        );

        context.deleteCells(SpreadsheetSelection.A1);

        this.loadCellsAndCheck(
            context,
            b2.reference(),
            b2.setFormattedValue(
                Optional.of(
                    TextNode.text("Hello World B2")
                )
            )
        );
    }

    @Test
    public void testAddCellWatcher() {
        final SpreadsheetEngineContextSharedSpreadsheetContext context = this.createContext();

        this.fired = false;

        context.addCellWatcher(
            new StoreWatcher<SpreadsheetCell>() {
                @Override
                public void onValueChange(final Optional<SpreadsheetCell> oldValue,
                                          final Optional<SpreadsheetCell> newValue) {
                    checkEquals(
                        Optional.empty(),
                        oldValue,
                        "oldValue"
                    );
                    checkEquals(
                        Optional.of(SAVED_CELL),
                        newValue,
                        "newValue"
                    );
                    SpreadsheetEngineContextSharedSpreadsheetContextTest.this.fired = true;
                }
            }
        );

        context.saveCells(
            Sets.of(UNSAVED_CELL)
        );

        this.checkEquals(
            true,
            this.fired,
            "fired"
        );
    }

    @Test
    public void testAddCellWatcherOnce() {
        final SpreadsheetEngineContextSharedSpreadsheetContext context = this.createContext();

        this.fired = false;

        context.addCellWatcherOnce(
            new StoreWatcher<SpreadsheetCell>() {
                @Override
                public void onValueChange(final Optional<SpreadsheetCell> oldValue,
                                          final Optional<SpreadsheetCell> newValue) {
                    checkEquals(
                        Optional.empty(),
                        oldValue,
                        "oldValue"
                    );
                    checkEquals(
                        Optional.of(SAVED_CELL),
                        newValue,
                        "newValue"
                    );
                    SpreadsheetEngineContextSharedSpreadsheetContextTest.this.fired = true;
                }
            }
        );

        context.saveCells(
            Sets.of(UNSAVED_CELL)
        );

        this.checkEquals(
            true,
            this.fired,
            "fired"
        );

        context.saveCells(
            Sets.of(UNSAVED_CELL)
        );
    }

    private final static Form<SpreadsheetValidationReference> FORM = SpreadsheetForms.form(
        FormName.with("FormName111")
    ).setFields(
        Lists.of(
            SpreadsheetForms.field(SpreadsheetSelection.A1)
        )
    );

    private final static Form<SpreadsheetValidationReference> FORM2 = SpreadsheetForms.form(
        FormName.with("FormName222")
    ).setFields(
        Lists.of(
            SpreadsheetForms.field(SpreadsheetSelection.A1)
        )
    );

    @Test
    public void testLoadForm() {
        final SpreadsheetEngineContextSharedSpreadsheetContext context = this.createContext();

        this.loadFormAndCheck(
            context,
            FORM.name()
        );
    }

    @Test
    public void testSaveFormAndLoadForms() {
        final SpreadsheetEngineContextSharedSpreadsheetContext context = this.createContext();

        context.saveForm(FORM);

        this.loadFormAndCheck(
            context,
            FORM.name(),
            FORM
        );
    }

    @Test
    public void testDeleteForm() {
        final SpreadsheetEngineContextSharedSpreadsheetContext context = this.createContext();

        context.saveForm(FORM);
        context.deleteForm(FORM.name());

        this.loadFormAndCheck(
            context,
            FORM.name()
        );
    }

    @Test
    public void testAddFormWatcher() {
        final SpreadsheetEngineContextSharedSpreadsheetContext context = this.createContext();

        this.fired = false;

        context.addFormWatcher(
            new StoreWatcher<Form<SpreadsheetValidationReference>>() {
                @Override
                public void onValueChange(final Optional<Form<SpreadsheetValidationReference>> oldValue,
                                          final Optional<Form<SpreadsheetValidationReference>> newValue) {
                    checkEquals(
                        Optional.empty(),
                        oldValue,
                        "oldValue"
                    );
                    checkEquals(
                        Optional.of(FORM),
                        newValue,
                        "newValue"
                    );
                    SpreadsheetEngineContextSharedSpreadsheetContextTest.this.fired = true;
                }
            }
        );

        context.saveForm(FORM);

        this.checkEquals(
            true,
            this.fired,
            "fired"
        );
    }

    @Test
    public void testAddFormWatcherOnce() {
        final SpreadsheetEngineContextSharedSpreadsheetContext context = this.createContext();

        this.fired = false;

        context.addFormWatcherOnce(
            new StoreWatcher<Form<SpreadsheetValidationReference>>() {
                @Override
                public void onValueChange(final Optional<Form<SpreadsheetValidationReference>> oldValue,
                                          final Optional<Form<SpreadsheetValidationReference>> newValue) {
                    checkEquals(
                        Optional.empty(),
                        oldValue,
                        "oldValue"
                    );
                    checkEquals(
                        Optional.of(FORM),
                        newValue,
                        "newValue"
                    );
                    SpreadsheetEngineContextSharedSpreadsheetContextTest.this.fired = true;
                }
            }
        );

        context.saveForm(FORM);

        this.checkEquals(
            true,
            this.fired,
            "fired"
        );

        context.saveForm(FORM2);
    }

    private final static SpreadsheetLabelMapping MAPPING1 = SpreadsheetSelection.labelName("Label111")
        .setLabelMappingReference(SpreadsheetSelection.A1);

    private final static SpreadsheetLabelMapping MAPPING2 = SpreadsheetSelection.labelName("Label222")
        .setLabelMappingReference(SpreadsheetSelection.A1);

    @Test
    public void testLoadLabel() {
        final SpreadsheetEngineContextSharedSpreadsheetContext context = this.createContext();

        this.loadLabelAndCheck(
            context,
            MAPPING1.label()
        );
    }

    @Test
    public void testSaveLabelAndLoadLabel() {
        final SpreadsheetEngineContextSharedSpreadsheetContext context = this.createContext();

        context.saveLabel(MAPPING1);

        this.loadLabelAndCheck(
            context,
            MAPPING1.label(),
            MAPPING1
        );
    }

    @Test
    public void testDeleteLabel() {
        final SpreadsheetEngineContextSharedSpreadsheetContext context = this.createContext();

        context.saveLabel(MAPPING1);
        context.deleteLabel(MAPPING1.label());

        context.saveLabel(MAPPING2);

        this.loadLabelAndCheck(
            context,
            MAPPING1.label()
        );
        this.loadLabelAndCheck(
            context,
            MAPPING2.label(),
            MAPPING2
        );

    }

    @Test
    public void testAddLabelWatcher() {
        final SpreadsheetEngineContextSharedSpreadsheetContext context = this.createContext();

        this.fired = false;

        context.addLabelWatcher(
            new StoreWatcher<SpreadsheetLabelMapping>() {
                @Override
                public void onValueChange(final Optional<SpreadsheetLabelMapping> oldValue,
                                          final Optional<SpreadsheetLabelMapping> newValue) {
                    checkEquals(
                        Optional.empty(),
                        oldValue,
                        "oldValue"
                    );
                    checkEquals(
                        Optional.of(MAPPING1),
                        newValue,
                        "newValue"
                    );
                    SpreadsheetEngineContextSharedSpreadsheetContextTest.this.fired = true;
                }
            }
        );

        context.saveLabel(MAPPING1);

        this.checkEquals(
            true,
            this.fired,
            "fired"
        );
    }

    @Test
    public void testAddLabelWatcherOnce() {
        final SpreadsheetEngineContextSharedSpreadsheetContext context = this.createContext();

        this.fired = false;

        context.addLabelWatcherOnce(
            new StoreWatcher<SpreadsheetLabelMapping>() {
                @Override
                public void onValueChange(final Optional<SpreadsheetLabelMapping> oldValue,
                                          final Optional<SpreadsheetLabelMapping> newValue) {
                    checkEquals(
                        Optional.empty(),
                        oldValue,
                        "oldValue"
                    );
                    checkEquals(
                        Optional.of(MAPPING1),
                        newValue,
                        "newValue"
                    );
                    SpreadsheetEngineContextSharedSpreadsheetContextTest.this.fired = true;
                }
            }
        );

        context.saveLabel(MAPPING1);

        this.checkEquals(
            true,
            this.fired,
            "fired"
        );

        context.saveLabel(MAPPING2);
    }

    private boolean fired;

    // storage..........................................................................................................

    @Test
    public final void testStorage() {
        this.storageAndCheck(
            this.createContext(),
            Storages.treeMapStore()
        );
    }

    @Test
    public void testSaveStorageAndLoadStorage() {
        final SpreadsheetEngineContextSharedSpreadsheetContext context = this.createContext();

        final StoragePath path = StoragePath.parse("/path1/file2");

        final StorageValue value = StorageValue.with(path)
            .setValue(
                Optional.of(111)
            );

        this.saveStorageAndCheck(
            context,
            value,
            value
        );

        this.loadStorageAndCheck(
            context,
            path,
            value
        );
    }

    // createContext....................................................................................................
    
    @Override
    SpreadsheetEngineContextSharedSpreadsheetContext createContext(final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext) {
        spreadsheetEnvironmentContext.setSpreadsheetId(
            Optional.of(SPREADSHEET_ID)
        );

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
                                                                           final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext) {
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
            spreadsheetEnvironmentContext
        );
    }

    // createContext....................................................................................................

    @Override
    public SpreadsheetEngineContextSharedSpreadsheetContext createContext() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.basic(
            Storages.treeMapStore(),
            SpreadsheetMetadataTesting.SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment()
        );
        spreadsheetEnvironmentContext.setSpreadsheetId(
            Optional.of(SPREADSHEET_ID)
        );

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
                                                                           final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext) {
        return SpreadsheetEngineContextSharedSpreadsheetContext.with(
            SpreadsheetMetadataMode.FORMULA,
            new TestSpreadsheetContext(
                metadata,
                storeRepository,
                spreadsheetEnvironmentContext,
                LOCALE_CONTEXT,
                PROVIDER_CONTEXT
            ),
            TERMINAL_CONTEXT
        );
    }

    private static SpreadsheetEnvironmentContext spreadsheetEnvironmentContext() {
        final EnvironmentContext context = EnvironmentContexts.map(
            EnvironmentContexts.empty(
                CHARSET,
                CURRENCY,
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
        context.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            SpreadsheetEngineContextSharedSpreadsheetContextTest.SPREADSHEET_ID
        );
        return SpreadsheetEnvironmentContexts.basic(
            STORAGE,
            context
        );
    }

    private final static class TestSpreadsheetContext implements SpreadsheetContext,
        SpreadsheetEnvironmentContextDelegator,
        CurrencyContextDelegator,
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
                SpreadsheetEngineContextSharedSpreadsheetContextTest.spreadsheetEnvironmentContext()
            );
        }

        TestSpreadsheetContext(final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext) {
            this.metadata = METADATA;
            this.storeRepository = REPO;

            this.spreadsheetEnvironmentContext = spreadsheetEnvironmentContext;
            this.localeContext = LOCALE_CONTEXT;
            this.providerContext = PROVIDER_CONTEXT;
        }

        TestSpreadsheetContext(final SpreadsheetMetadata metadata,
                               final SpreadsheetStoreRepository storeRepository,
                               final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                               final LocaleContext localeContext,
                               final ProviderContext providerContext) {
            final SpreadsheetId spreadsheetId = metadata.getOrFail(SpreadsheetMetadataPropertyName.SPREADSHEET_ID);
            if (false == SpreadsheetEngineContextSharedSpreadsheetContextTest.SPREADSHEET_ID.equals(spreadsheetId)) {
                throw new IllegalArgumentException("Invalid SpreadsheetId: " + spreadsheetId + " expected " + SPREADSHEET_ID);
            }
            this.metadata = metadata;
            this.storeRepository = storeRepository;

            this.spreadsheetEnvironmentContext = spreadsheetEnvironmentContext;
            this.localeContext = localeContext;
            this.providerContext = providerContext;
        }

        @Override
        public MediaType detect(final String filename,
                                final Binary content) {
            return MediaTypeDetectors.binary()
                .detect(
                    filename,
                    content
                );
        }

        @Override
        public BinaryNumberConverterFunction<SpreadsheetConverterContext> multiplier() {
            return ExpressionNumberBinaryNumberConverterFunctions.multiply();
        }

        @Override
        public SpreadsheetStoreRepository storeRepository() {
            return this.storeRepository;
        }

        private final SpreadsheetStoreRepository storeRepository;

        @Override
        public SpreadsheetEngineContext spreadsheetEngineContext() {
            return SpreadsheetEngineContexts.spreadsheetEnvironmentContext(
                MEDIA_TYPE_DETECTOR,
                MULTIPLIER,
                SPREADSHEET_ENGINE,
                (SpreadsheetId spreadsheetId) -> Optional.ofNullable(
                    SpreadsheetEngineContextSharedSpreadsheetContextTest.SPREADSHEET_ID.equals(spreadsheetId) ?
                        this :
                        null
                ), // SpreadsheetContextSupplier
                this.currencyContext()
                    .setLocaleContext(this.localeContext()),
                SpreadsheetEnvironmentContexts.basic(
                    this.storage(),
                    this.spreadsheetEnvironmentContext
                ),
                this, // SpreadsheetMetadataContext
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                this.providerContext
            );
        }

        @Override
        public Router<HttpRequestAttribute<?>, HttpHandler<HttpHandlerContext>> httpRouter() {
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

            SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY.set(
                SpreadsheetMetadataPropertyName.AUDIT_INFO,
                AuditInfo.create(
                    user,
                    HAS_NOW.now()
                )
            );
            if (locale.isPresent()) {
                metadata = metadata.set(
                    SpreadsheetMetadataPropertyName.LOCALE,
                    locale.get()
                );
            }
            return this.saveMetadata(metadata);
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

            SpreadsheetMetadata saved = metadata;
            if (metadata.id().isEmpty()) {
                saved = metadata.set(
                    SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                    SpreadsheetEngineContextSharedSpreadsheetContextTest.SPREADSHEET_ID
                );
            }

            this.metadata = saved;
            return saved;
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
        public Runnable addMetadataWatcher(final StoreWatcher<SpreadsheetMetadata> watcher) {
            Objects.requireNonNull(watcher, "watcher");

            throw new UnsupportedOperationException();
        }

        @Override
        public Runnable addMetadataWatcherOnce(final StoreWatcher<SpreadsheetMetadata> watcher) {
            Objects.requireNonNull(watcher, "watcher");

            throw new UnsupportedOperationException();
        }

        @Override
        public SpreadsheetContext cloneEnvironment() {
            return new TestSpreadsheetContext(
                this.metadata,
                this.storeRepository,
                this.spreadsheetEnvironmentContext.cloneEnvironment(),
                this.localeContext,
                this.providerContext
            );
        }

        @Override
        public SpreadsheetContext setEnvironmentContext(final EnvironmentContext environmentContext) {
            Objects.requireNonNull(environmentContext, "environmentContext");

            final SpreadsheetEnvironmentContext before = this.spreadsheetEnvironmentContext;
            final SpreadsheetEnvironmentContext after = before.setEnvironmentContext(environmentContext);

            return before == after ?
                this :
                new TestSpreadsheetContext(
                    this.metadata,
                    this.storeRepository,
                    after,
                    this.localeContext,
                    this.providerContext
                );
        }

        @Override
        public <T> void setEnvironmentValue(final EnvironmentValueName<T> name,
                                            final T value) {
            this.spreadsheetEnvironmentContext.setEnvironmentValue(
                name,
                value
            );
        }

        @Override
        public void removeEnvironmentValue(final EnvironmentValueName<?> name) {
            this.spreadsheetEnvironmentContext.removeEnvironmentValue(name);
        }

        @Override
        public Currency currency() {
            return this.spreadsheetEnvironmentContext.currency();
        }

        @Override
        public void setCurrency(final Currency currency) {
            this.spreadsheetEnvironmentContext.setCurrency(currency);
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
        public void setUser(final Optional<EmailAddress> user) {
            this.spreadsheetEnvironmentContext.setUser(user);
        }

        @Override
        public EnvironmentContext environmentContext() {
            return this.spreadsheetEnvironmentContext;
        }

        @Override
        public Storage<SpreadsheetStorageContext> storage() {
            return this.spreadsheetEnvironmentContext.storage();
        }

        private final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext;

        @Override
        public SpreadsheetEnvironmentContext spreadsheetEnvironmentContext() {
//            return SpreadsheetEnvironmentContexts.basic(
//                STORAGE,
//                this.spreadsheetEnvironmentContext
//            );
            return this.spreadsheetEnvironmentContext;
        }

        @Override
        public CurrencyContext currencyContext() {
            return CURRENCY_CONTEXT;
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
                this.spreadsheetEnvironmentContext,
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
                this.spreadsheetEnvironmentContext.equals(other.spreadsheetEnvironmentContext) &&
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
                SpreadsheetMetadataMode.QUERY,
                SPREADSHEET_CONTEXT,
                TERMINAL_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsDifferentSpreadsheetContext() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.basic(
            Storages.fake(),
            EnvironmentContexts.map(
                EnvironmentContexts.empty(
                    CHARSET,
                    CURRENCY,
                    INDENTATION,
                    LineEnding.CRNL,
                    Locale.FRANCE,
                    LocalDateTime::now,
                    EnvironmentContext.ANONYMOUS
                )
            )
        );

        spreadsheetEnvironmentContext.setSpreadsheetId(
            Optional.of(SPREADSHEET_ID)
        );

        this.checkNotEquals(
            SpreadsheetEngineContextSharedSpreadsheetContext.with(
                SpreadsheetMetadataMode.FORMULA,
                SPREADSHEET_CONTEXT,
                TERMINAL_CONTEXT
            ),
            SpreadsheetEngineContextSharedSpreadsheetContext.with(
                SpreadsheetMetadataMode.FORMULA,
                new TestSpreadsheetContext(
                    spreadsheetEnvironmentContext
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
            "mode=FORMULA"
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
            "mode=FORMULA"
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

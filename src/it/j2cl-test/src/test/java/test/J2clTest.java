/*
 * Copyright © 2020 Miroslav Pokorny
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
 */
package test;


import com.google.j2cl.junit.apt.J2clTestInput;
import org.junit.Assert;
import org.junit.Test;

import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.color.Color;
import walkingkooka.convert.Converters;
import walkingkooka.convert.provider.ConverterProvider;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.datetime.HasNow;
import walkingkooka.environment.AuditInfo;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColors;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetStrings;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.convert.SpreadsheetConvertersConverterProviders;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngines;
import walkingkooka.spreadsheet.export.SpreadsheetExporterProviders;
import walkingkooka.spreadsheet.expression.FakeSpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.importer.SpreadsheetImporterProviders;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProvider;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.provider.SpreadsheetProviders;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.security.store.SpreadsheetGroupStores;
import walkingkooka.spreadsheet.security.store.SpreadsheetUserStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellRangeStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellReferencesStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStores;
import walkingkooka.spreadsheet.store.SpreadsheetLabelReferencesStores;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.store.SpreadsheetRowStores;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.spreadsheet.validation.form.store.SpreadsheetFormStores;
import walkingkooka.storage.FakeStorageContext;
import walkingkooka.storage.Storages;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionEvaluationContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviders;
import walkingkooka.tree.text.Length;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;
import walkingkooka.validation.form.provider.FormHandlerProviders;
import walkingkooka.validation.provider.ValidatorProviders;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

// copied parse Sample
@J2clTestInput(J2clTest.class)
public class J2clTest {

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;

    private final static HasNow NOW = LocalDateTime::now;

    private final static SpreadsheetLabelNameResolver LABEL_NAME_RESOLVER = SpreadsheetLabelNameResolvers.fake();

    private final static ProviderContext PROVIDER_CONTEXT = ProviderContexts.fake();

    @Test
    public void testMetadataNonLocaleDefaults() {
        Assert.assertNotEquals(null, SpreadsheetMetadata.NON_LOCALE_DEFAULTS);
    }

    // see CellSpreadsheetExpressionEvaluationContext#validatorContext
    @Test
    public void testCellSpreadsheetExpressionEvaluationContext() {
        SpreadsheetExpressionEvaluationContexts.cell(
                Optional.empty(),
                new FakeSpreadsheetExpressionEvaluationContext() {
                    @Override
                    public Optional<SpreadsheetCell> cell() {
                        return Optional.empty();
                    }
                }
        );
    }

    @Test
    public void testFormula() {
        final SpreadsheetEngine engine = engine();
        final SpreadsheetEngineContext engineContext = engineContext();

        final SpreadsheetCell unsaved = SpreadsheetSelection.A1.setFormula(
                SpreadsheetFormula.EMPTY.setText("=12")
        );

        final SpreadsheetDelta delta = engine.saveCell(
                unsaved,
                engineContext
        );

        // a1=12
        checkEquals(
                SpreadsheetDelta.EMPTY.setCells(
                                Sets.of(
                                        unsaved.setFormula(
                                                unsaved.formula()
                                                        .setToken(
                                                                Optional.of(
                                                                        SpreadsheetFormulaParserToken.expression(
                                                                                Lists.of(
                                                                                        SpreadsheetFormulaParserToken.equalsSymbol("=", "="),
                                                                                        SpreadsheetFormulaParserToken.number(
                                                                                                Lists.of(
                                                                                                        SpreadsheetFormulaParserToken.digits("12", "12")
                                                                                                ),
                                                                                                "12"
                                                                                        )
                                                                                ),
                                                                                "=12"
                                                                        )
                                                                )
                                                        ).setExpression(
                                                                Optional.of(
                                                                        Expression.value(
                                                                                EXPRESSION_NUMBER_KIND.create(12)
                                                                        )
                                                                )
                                                        ).setValue(
                                                                Optional.of(
                                                                        EXPRESSION_NUMBER_KIND.create(12)
                                                                )
                                                        )
                                        ).setFormattedValue(
                                                Optional.of(
                                                        TextNode.text("12.0")
                                                )
                                        )
                                )
                        ).setColumnWidths(
                                Maps.of(
                                        SpreadsheetSelection.A1.column(),
                                        50.0
                                )
                        ).setRowHeights(
                                Maps.of(
                                        SpreadsheetSelection.A1.row(),
                                        50.0
                                )
                        ).setColumnCount(OptionalInt.of(1))
                        .setRowCount(OptionalInt.of(1)),
                delta,
                "saved A1=12"
        );
    }

    private static void checkEquals(final Object expected,
                                    final Object actual,
                                    final String message) {
        Assert.assertEquals(
                message,
                expected,
                actual
        );
    }

    private static SpreadsheetMetadata metadata() {
        if (null == metadata) {
            SpreadsheetMetadata m = SpreadsheetMetadata.EMPTY
                    .set(
                            SpreadsheetMetadataPropertyName.AUDIT_INFO,
                            AuditInfo.with(
                                    EmailAddress.parse("creator@example.com"),
                                    LocalDateTime.of(1999, 12, 31, 12, 58, 59),
                                    EmailAddress.parse("modified@example.com"),
                                    LocalDateTime.of(2000, 1, 2, 3, 4, 5)
                            )
                    ).set(SpreadsheetMetadataPropertyName.CELL_CHARACTER_WIDTH, 10)
                    .set(
                            SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_SYMBOLS,
                            DecimalNumberSymbols.with(
                                    '-',
                                    '+',
                                    '0',
                                    "$AUD",
                                    '.',
                                    "E",
                                    ',',
                                    "Infinity!",
                                    ':',
                                    "Nan!",
                                    '%',
                                    '^'
                            )
                    ).set(SpreadsheetMetadataPropertyName.DATE_FORMATTER, SpreadsheetPattern.parseDateFormatPattern("DD/MM/YYYY").spreadsheetFormatterSelector())
                    .set(SpreadsheetMetadataPropertyName.DATE_PARSER, SpreadsheetPattern.parseDateParsePattern("DD/MM/YYYYDDMMYYYY").spreadsheetParserSelector())
                    .set(SpreadsheetMetadataPropertyName.DATE_TIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
                    .set(SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER, SpreadsheetPattern.parseDateTimeFormatPattern("DD/MM/YYYY hh:mm").spreadsheetFormatterSelector())
                    .set(SpreadsheetMetadataPropertyName.DATE_TIME_PARSER, SpreadsheetPattern.parseDateTimeParsePattern("DD/MM/YYYY hh:mmDDMMYYYYHHMMDDMMYYYY HHMM").spreadsheetParserSelector())
                    .set(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, 1900)
                    .set(SpreadsheetMetadataPropertyName.ERROR_FORMATTER, SpreadsheetPattern.parseTextFormatPattern("\"Error\" @").spreadsheetFormatterSelector())
                    .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, EXPRESSION_NUMBER_KIND)
                    .set(SpreadsheetMetadataPropertyName.FORMATTING_CONVERTER, ConverterSelector.parse("collection(text, number, date-time, basic, spreadsheet-value, boolean, error-throwing, color, expression, environment, locale, plugins, style, text-node, template, url)"))
                    .set(SpreadsheetMetadataPropertyName.FORMULA_CONVERTER, ConverterSelector.parse("collection(text, number, date-time, basic, spreadsheet-value, boolean, error-throwing, color, expression, environment, locale, template, url)"))
                    .set(SpreadsheetMetadataPropertyName.FROZEN_COLUMNS, SpreadsheetSelection.parseColumnRange("A:B"))
                    .set(SpreadsheetMetadataPropertyName.FROZEN_ROWS, SpreadsheetSelection.parseRowRange("1:2"))
                    .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.forLanguageTag("EN-AU"))

                    .set(SpreadsheetMetadataPropertyName.NUMBER_FORMATTER, SpreadsheetPattern.parseNumberFormatPattern("#0.0").spreadsheetFormatterSelector())
                    .set(SpreadsheetMetadataPropertyName.GENERAL_NUMBER_FORMAT_DIGIT_COUNT, SpreadsheetFormatterContext.DEFAULT_GENERAL_FORMAT_NUMBER_DIGIT_COUNT)
                    .set(SpreadsheetMetadataPropertyName.NUMBER_PARSER, SpreadsheetPattern.parseNumberParsePattern("#").spreadsheetParserSelector())
                    .set(SpreadsheetMetadataPropertyName.PRECISION, 123)
                    .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.FLOOR)
                    .set(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(123))
                    .set(
                            SpreadsheetMetadataPropertyName.STYLE,
                            TextStyle.EMPTY.set(TextStylePropertyName.WIDTH, Length.pixel(50.0))
                                    .set(TextStylePropertyName.HEIGHT, Length.pixel(50.0)))
                    .set(SpreadsheetMetadataPropertyName.TEXT_FORMATTER, SpreadsheetPattern.parseTextFormatPattern("@@").spreadsheetFormatterSelector())
                    .set(SpreadsheetMetadataPropertyName.TIME_FORMATTER, SpreadsheetPattern.parseTimeFormatPattern("hh:mm").spreadsheetFormatterSelector())
                    .set(SpreadsheetMetadataPropertyName.TIME_PARSER, SpreadsheetPattern.parseTimeParsePattern("hh:mmhh:mm:ss.000").spreadsheetParserSelector())
                    .set(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR, 31)
                    .set(SpreadsheetMetadataPropertyName.VALUE_SEPARATOR, ',');

            for (int i = SpreadsheetColors.MIN; i < SpreadsheetColors.MAX + 1; i++) {
                m = m.set(SpreadsheetMetadataPropertyName.numberedColor(i), Color.fromRgb(i));
            }

            metadata = m;
        }
        return metadata;
    }

    private static SpreadsheetMetadata metadata;

    private static SpreadsheetEngine engine() {
        return SpreadsheetEngines.basic();
    }

    private static SpreadsheetEngineContext engineContext() {
        final SpreadsheetMetadata metadata = metadata();
        final SpreadsheetFormatterProvider spreadsheetFormatterProvider = SpreadsheetFormatterProviders.spreadsheetFormatters();
        final SpreadsheetParserProvider spreadsheetParserProvider = SpreadsheetParserProviders.spreadsheetParsePattern(spreadsheetFormatterProvider);
        final ConverterProvider converterProvider = SpreadsheetConvertersConverterProviders.spreadsheetConverters(
            (ProviderContext p) -> metadata.dateTimeConverter(
                spreadsheetFormatterProvider,
                spreadsheetParserProvider,
                p
            )
        );

        return new FakeSpreadsheetEngineContext() {

            @Override
            public SpreadsheetMetadata spreadsheetMetadata() {
                return metadata;
            }

            @Override
            public SpreadsheetFormulaParserToken parseFormula(final TextCursor formula,
                                                              final Optional<SpreadsheetCell> cell) {
                return Cast.to(
                        SpreadsheetFormulaParsers.expression()
                                .orFailIfCursorNotEmpty(ParserReporters.basic())
                                .parse(
                                        formula,
                                        metadata.spreadsheetParserContext(
                                                cell,
                                                this.localeContext,
                                                NOW
                                        )
                                ) // TODO should fetch parse metadata prop
                                .get()
                );
            }

            @Override
            public SpreadsheetParser spreadsheetParser(final SpreadsheetParserSelector selector,
                                                       final ProviderContext context) {
                return spreadsheetParserProvider.spreadsheetParser(
                        selector,
                        context
                );
            }

            @Override
            public SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext(final Optional<SpreadsheetCell> cell,
                                                                                                 final SpreadsheetExpressionReferenceLoader loader) {
                return new SampleSpreadsheetExpressionEvaluationContext(
                        cell,
                        ExpressionEvaluationContexts.basic(
                                EXPRESSION_NUMBER_KIND,
                                n -> ExpressionFunctionProviders.fake()
                                        .expressionFunction(
                                                n,
                                                Lists.empty(),
                                                PROVIDER_CONTEXT
                                        ),
                                (r) -> {
                                    throw new UnsupportedOperationException();
                                },
                                (r) -> {
                                    throw new UnsupportedOperationException();
                                },
                                SpreadsheetExpressionEvaluationContexts.referenceNotFound(),
                                CaseSensitivity.INSENSITIVE,
                                metadata.spreadsheetConverterContext(
                                        SpreadsheetMetadata.NO_CELL,
                                        SpreadsheetMetadata.NO_VALIDATION_REFERENCE,
                                        SpreadsheetMetadataPropertyName.FORMULA_CONVERTER,
                                        LABEL_NAME_RESOLVER,
                                        converterProvider,
                                        this.localeContext,
                                        PROVIDER_CONTEXT
                                ),
                                LocaleContexts.fake()
                        )
                );
            }

            @Override
            public Optional<Expression> toExpression(final SpreadsheetFormulaParserToken token) {
                Objects.requireNonNull(token, "token");

                return token.toExpression(
                        new FakeExpressionEvaluationContext() {
                            @Override
                            public ExpressionNumberKind expressionNumberKind() {
                                return EXPRESSION_NUMBER_KIND;
                            }
                        }
                );
            }

            @Override
            public Optional<TextNode> formatValue(final SpreadsheetCell cell,
                                                  final Optional<Object> value,
                                                  final Optional<SpreadsheetFormatterSelector> formatter) {
                checkEquals(
                        false,
                        value.orElse(null) instanceof Optional,
                        "Value must not be optional" + value
                );

                return formatter.map(
                        (SpreadsheetFormatterSelector s) ->
                                spreadsheetFormatterProvider.spreadsheetFormatter(
                                        s,
                                        PROVIDER_CONTEXT
                                )
                ).orElseGet(
                        () -> metadata.spreadsheetFormatter(
                                spreadsheetFormatterProvider,
                                PROVIDER_CONTEXT
                        )
                ).format(
                        value,
                    value,
                    metadata.spreadsheetFormatterContext(
                        Optional.of(cell),
                        (final Optional<Object> v) -> {
                            throw new UnsupportedOperationException();
                        },
                        LABEL_NAME_RESOLVER,
                        this.localeContext,
                        SpreadsheetProviders.basic(
                            converterProvider,
                            ExpressionFunctionProviders.fake(),
                            SpreadsheetComparatorProviders.fake(),
                            SpreadsheetExporterProviders.fake(),
                            spreadsheetFormatterProvider,
                            FormHandlerProviders.fake(),
                            SpreadsheetImporterProviders.fake(),
                            SpreadsheetParserProviders.fake(),
                            ValidatorProviders.fake()
                        ),
                        PROVIDER_CONTEXT
                    )
                );
            }

            @Override
            public SpreadsheetCell formatValueAndStyle(final SpreadsheetCell cell,
                                                       final Optional<SpreadsheetFormatterSelector> formatter) {
                return cell.setFormattedValue(
                        Optional.of(
                                this.formatValue(
                                        cell,
                                        cell.formula()
                                                .errorOrValue(),
                                        formatter
                                ).map(
                                        f -> cell.style()
                                                .replace(f)
                                ).orElse(TextNode.EMPTY_TEXT)
                        )
                );
            }

            @Override
            public Optional<DateTimeSymbols> dateTimeSymbolsForLocale(final Locale locale) {
                return this.localeContext.dateTimeSymbolsForLocale(locale);
            }

            @Override
            public Optional<DecimalNumberSymbols> decimalNumberSymbolsForLocale(final Locale locale) {
                return this.localeContext.decimalNumberSymbolsForLocale(locale);
            }

            private final LocaleContext localeContext = LocaleContexts.jre(
                    Locale.forLanguageTag("EN-AU")
            );

            @Override
            public SpreadsheetStoreRepository storeRepository() {
                return this.storeRepository;
            }

            private final SpreadsheetStoreRepository storeRepository = SpreadsheetStoreRepositories.basic(
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
                    Storages.tree(),
                    SpreadsheetUserStores.fake()
            );

            @Override
            public ProviderContext providerContext() {
                return ProviderContexts.fake();
            }
        };
    }

    static class SampleSpreadsheetExpressionEvaluationContext extends FakeSpreadsheetExpressionEvaluationContext {

        SampleSpreadsheetExpressionEvaluationContext(final Optional<SpreadsheetCell> cell,
                                                     final ExpressionEvaluationContext context) {
            this.cell = cell;
            this.context = context;
        }

        @Override
        public Optional<SpreadsheetCell> cell() {
            return this.cell;
        }

        private final Optional<SpreadsheetCell> cell;

        @Override
        public Optional<Optional<Object>> reference(final ExpressionReference reference) {
            return this.context.reference(reference);
        }

        @Override
        public boolean isText(final Object value) {
            return SpreadsheetStrings.isText(value);
        }

        @Override
        public SpreadsheetExpressionEvaluationContext setCell(final Optional<SpreadsheetCell> cell) {
            return SpreadsheetExpressionEvaluationContexts.cell(
                    cell,
                    this
            );
        }

        private final ExpressionEvaluationContext context;
    }
}



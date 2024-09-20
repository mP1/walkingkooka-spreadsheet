package test;

import com.google.gwt.junit.client.GWTTestCase;

import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.color.Color;
import walkingkooka.convert.Converters;
import walkingkooka.convert.provider.ConverterProvider;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColors;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.convert.SpreadsheetConvertersConverterProviders;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngines;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProvider;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.security.store.SpreadsheetGroupStores;
import walkingkooka.spreadsheet.security.store.SpreadsheetUserStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellRangeStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStores;
import walkingkooka.spreadsheet.store.SpreadsheetExpressionReferenceStores;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.store.SpreadsheetRowStores;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviders;
import walkingkooka.tree.text.Length;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@walkingkooka.j2cl.locale.LocaleAware
public class TestGwtTest extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "test.Test";
    }

    public void testAssertEquals() {
        assertEquals(
                1,
                1
        );
    }

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;

    private static final Supplier<LocalDateTime> NOW = LocalDateTime::now;

    private final static SpreadsheetLabelNameResolver LABEL_NAME_RESOLVER = SpreadsheetLabelNameResolvers.fake();

    private final static ProviderContext PROVIDER_CONTEXT = ProviderContexts.fake();
    public void testWithCellReference() {
        final SpreadsheetEngine engine = engine();
        final SpreadsheetEngineContext engineContext = engineContext(engine);

        engine.saveCell(
                SpreadsheetSelection.parseCell("A1")
                        .setFormula(
                                SpreadsheetFormula.EMPTY
                                        .setText("=12+B2")
                        ),
                engineContext
        );

        final SpreadsheetDelta delta = engine.saveCell(
                SpreadsheetSelection.parseCell("B2")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("=34")
                        ),
                engineContext
        );

        final Set<String> saved = delta.cells()
                .stream()
                .map(c -> c.formula().value().get().toString())
                .collect(Collectors.toCollection(SortedSets::tree));

        // a1=12+b2
        // a1=12+34
        // b2=34
        checkEquals(
                Sets.of("46", "34"),
                saved,
                "saved formula values"
        );
    }

    private static void checkEquals(final Object expected,
                                    final Object actual,
                                    final String message) {
        assertEquals(
                message,
                expected,
                actual
        );
    }

    private static SpreadsheetMetadata metadata() {
        if (null == metadata) {
            SpreadsheetMetadata m = SpreadsheetMetadata.EMPTY
                    .set(SpreadsheetMetadataPropertyName.CELL_CHARACTER_WIDTH, 10)
                    .set(SpreadsheetMetadataPropertyName.CREATE_DATE_TIME, LocalDateTime.of(2000, 12, 31, 12, 58, 59))
                    .set(SpreadsheetMetadataPropertyName.CREATOR, EmailAddress.parse("creator@example.com"))
                    .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "$AUD")
                    .set(SpreadsheetMetadataPropertyName.DATE_FORMATTER, SpreadsheetPattern.parseDateFormatPattern("DD/MM/YYYY").spreadsheetFormatterSelector())
                    .set(SpreadsheetMetadataPropertyName.DATE_PARSER, SpreadsheetPattern.parseDateParsePattern("DD/MM/YYYYDDMMYYYY").spreadsheetParserSelector())
                    .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.JAVA_EPOCH_OFFSET)
                    .set(SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER, SpreadsheetPattern.parseDateTimeFormatPattern("DD/MM/YYYY hh:mm").spreadsheetFormatterSelector())
                    .set(SpreadsheetMetadataPropertyName.DATE_TIME_PARSER, SpreadsheetPattern.parseDateTimeParsePattern("DD/MM/YYYY hh:mmDDMMYYYYHHMMDDMMYYYY HHMM").spreadsheetParserSelector())
                    .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, '.')
                    .set(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, 1900)
                    .set(SpreadsheetMetadataPropertyName.EXPRESSION_CONVERTER, ConverterSelector.parse("collection (error-to-number, error-throwing, general)"))
                    .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, EXPRESSION_NUMBER_KIND)
                    .set(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, "E")
                    .set(SpreadsheetMetadataPropertyName.FORMAT_CONVERTER, ConverterSelector.parse("collection (error-to-number, error-to-string, general)"))
                    .set(SpreadsheetMetadataPropertyName.FROZEN_COLUMNS, SpreadsheetSelection.parseColumnRange("A:B"))
                    .set(SpreadsheetMetadataPropertyName.FROZEN_ROWS, SpreadsheetSelection.parseRowRange("1:2"))
                    .set(SpreadsheetMetadataPropertyName.GROUP_SEPARATOR, ',')
                    .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.forLanguageTag("EN-AU"))
                    .set(SpreadsheetMetadataPropertyName.MODIFIED_BY, EmailAddress.parse("modified@example.com"))
                    .set(SpreadsheetMetadataPropertyName.MODIFIED_DATE_TIME, LocalDateTime.of(1999, 12, 31, 12, 58, 59))
                    .set(SpreadsheetMetadataPropertyName.NEGATIVE_SIGN, '-')
                    .set(SpreadsheetMetadataPropertyName.NUMBER_FORMATTER, SpreadsheetPattern.parseNumberFormatPattern("#0.0").spreadsheetFormatterSelector())
                    .set(SpreadsheetMetadataPropertyName.GENERAL_NUMBER_FORMAT_DIGIT_COUNT, 8)
                    .set(SpreadsheetMetadataPropertyName.NUMBER_PARSER, SpreadsheetPattern.parseNumberParsePattern("#").spreadsheetParserSelector())
                    .set(SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL, '%')
                    .set(SpreadsheetMetadataPropertyName.POSITIVE_SIGN, '+')
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

    private static SpreadsheetEngineContext engineContext(final SpreadsheetEngine engine) {
        final SpreadsheetMetadata metadata = metadata();
        final SpreadsheetFormatterProvider spreadsheetFormatterProvider = SpreadsheetFormatterProviders.spreadsheetFormatPattern();
        final SpreadsheetParserProvider spreadsheetParserProvider = SpreadsheetParserProviders.spreadsheetParsePattern(spreadsheetFormatterProvider);
        final ConverterProvider converterProvider = SpreadsheetConvertersConverterProviders.spreadsheetConverters(
                metadata,
                spreadsheetFormatterProvider,
                spreadsheetParserProvider
        );

        return new FakeSpreadsheetEngineContext() {

            @Override
            public SpreadsheetMetadata spreadsheetMetadata() {
                return metadata;
            }

            @Override
            public SpreadsheetParserToken parseFormula(final TextCursor formula) {
                return Cast.to(
                        SpreadsheetParsers.expression()
                                .orFailIfCursorNotEmpty(ParserReporters.basic())
                                .parse(
                                        formula,
                                        metadata.parserContext(NOW)
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
            public Object evaluate(final Expression node,
                                   final Optional<SpreadsheetCell> cell) {
                return node.toValue(
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
                                this.references(),
                                SpreadsheetExpressionEvaluationContexts.referenceNotFound(),
                                CaseSensitivity.INSENSITIVE,
                                metadata.expressionSpreadsheetConverterContext(
                                        NOW,
                                        LABEL_NAME_RESOLVER,
                                        converterProvider,
                                        PROVIDER_CONTEXT
                                )
                        )
                );
            }

            private Function<ExpressionReference, Optional<Optional<Object>>> references() {
                return SpreadsheetEngines.expressionReferenceFunction(
                        engine,
                        this
                );
            }

            @Override
            public Optional<Expression> toExpression(final SpreadsheetParserToken token) {
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
            public SpreadsheetCell formatValueAndStyle(final SpreadsheetCell cell,
                                                       final Optional<SpreadsheetFormatter> formatter) {
                return cell.setFormattedValue(
                        Optional.of(
                                this.formatValue(
                                        cell.formula()
                                                .value()
                                                .get(),
                                        formatter.orElse(
                                                this.spreadsheetMetadata()
                                                        .formatter(
                                                                spreadsheetFormatterProvider,
                                                                PROVIDER_CONTEXT
                                                        )
                                        )
                                ).map(
                                        f -> cell.style()
                                                .replace(f)
                                ).orElse(TextNode.EMPTY_TEXT)
                        )
                );
            }

            @Override
            public Optional<TextNode> formatValue(final Object value,
                                                  final SpreadsheetFormatter formatter) {
                checkEquals(false, value instanceof Optional, "Value must not be optional" + value);

                return formatter.format(
                        value,
                        metadata.formatterContext(
                                converterProvider,
                                spreadsheetFormatterProvider,
                                NOW,
                                LABEL_NAME_RESOLVER,
                                PROVIDER_CONTEXT
                        )
                );
            }

            @Override
            public SpreadsheetStoreRepository storeRepository() {
                return this.storeRepository;
            }

            private final SpreadsheetStoreRepository storeRepository = SpreadsheetStoreRepositories.basic(
                    SpreadsheetCellStores.treeMap(),
                    SpreadsheetExpressionReferenceStores.treeMap(),
                    SpreadsheetColumnStores.treeMap(),
                    SpreadsheetGroupStores.fake(),
                    SpreadsheetLabelStores.treeMap(),
                    SpreadsheetExpressionReferenceStores.treeMap(),
                    SpreadsheetMetadataStores.fake(),
                    SpreadsheetCellRangeStores.treeMap(),
                    SpreadsheetCellRangeStores.treeMap(),
                    SpreadsheetRowStores.treeMap(),
                    SpreadsheetUserStores.fake()
            );
        };
    }
}

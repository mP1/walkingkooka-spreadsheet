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
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.SpreadsheetDescription;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.SpreadsheetExpressionFunctionNames;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.SpreadsheetValueType;
import walkingkooka.spreadsheet.SpreadsheetViewportRectangle;
import walkingkooka.spreadsheet.SpreadsheetViewportWindows;
import walkingkooka.spreadsheet.compare.SpreadsheetColumnOrRowSpreadsheetComparatorNamesList;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorNameList;
import walkingkooka.spreadsheet.conditionalformat.SpreadsheetConditionalFormattingRule;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverters;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContexts;
import walkingkooka.spreadsheet.format.FakeSpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.provider.SpreadsheetProviders;
import walkingkooka.spreadsheet.reference.AnchoredSpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReferencePath;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetViewport;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportAnchor;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportNavigation;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportNavigationList;
import walkingkooka.spreadsheet.security.store.SpreadsheetGroupStores;
import walkingkooka.spreadsheet.security.store.SpreadsheetUserStores;
import walkingkooka.spreadsheet.store.FakeSpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellRangeStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellRangeStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellReferencesStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellReferencesStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStores;
import walkingkooka.spreadsheet.store.SpreadsheetExpressionReferenceStore;
import walkingkooka.spreadsheet.store.SpreadsheetExpressionReferenceStores;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStores;
import walkingkooka.spreadsheet.store.repo.FakeSpreadsheetStoreRepository;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.expression.ExpressionNumberConverters;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionPurityContext;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterKind;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterName;
import walkingkooka.tree.expression.function.FakeExpressionFunction;
import walkingkooka.tree.expression.function.UnknownExpressionFunctionException;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionAliasSet;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionInfo;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionInfoSet;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;
import walkingkooka.tree.expression.function.provider.FakeExpressionFunctionProvider;
import walkingkooka.tree.text.FontWeight;
import walkingkooka.tree.text.Length;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("PointlessArithmeticExpression")
public final class BasicSpreadsheetEngineTest extends BasicSpreadsheetEngineTestCase<BasicSpreadsheetEngine>
        implements SpreadsheetEngineTesting<BasicSpreadsheetEngine>,
        SpreadsheetMetadataTesting {
    private final static String FORMATTED_PATTERN_SUFFIX = "FORMATTED_PATTERN_SUFFIX";

    private final static String DATE_PATTERN = "yyyy/mm/dd";
    private final static String TIME_PATTERN = "hh:mm";
    private final static String DATETIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;
    private final static String NUMBER_PATTERN = "#";
    private final static String TEXT_PATTERN = "@";

    private final static String CURRENCY_SYMBOL = "$";
    private final static char DECIMAL_SEPARATOR = '.';
    private final static String EXPONENT_SYMBOL = "E";
    private final static char GROUP_SEPARATOR = ',';
    private final static char NEGATIVE_SIGN = '-';
    private final static char PERCENTAGE_SYMBOL = '%';
    private final static char POSITIVE_SIGN = '+';

    private final static SpreadsheetFormatterContext SPREADSHEET_TEXT_FORMAT_CONTEXT = new FakeSpreadsheetFormatterContext() {
        @Override
        public boolean canConvert(final Object value,
                                  final Class<?> type) {
            return this.converter.canConvert(value, type, this);
        }

        @Override
        public <T> Either<T, String> convert(final Object value,
                                             final Class<T> target) {
            return this.converter.convert(value, target, this);
        }

        private final Converter<SpreadsheetConverterContext> converter = ExpressionNumberConverters.numberOrExpressionNumberToNumber()
                .cast(SpreadsheetConverterContext.class)
                .to(
                        Number.class,
                        Converters.collection(
                                Lists.of(
                                        Converters.simple()
                                                .cast(SpreadsheetConverterContext.class),
                                        SpreadsheetConverters.errorToString()
                                                .cast(SpreadsheetConverterContext.class),
                                        SpreadsheetConverters.errorToNumber()
                                                .cast(SpreadsheetConverterContext.class),
                                        Converters.localDateToLocalDateTime()
                                                .cast(SpreadsheetConverterContext.class),
                                        Converters.localTimeToLocalDateTime()
                                                .cast(SpreadsheetConverterContext.class),
                                        ExpressionNumberConverters.toNumberOrExpressionNumber(
                                                Converters.numberToNumber()
                                        ),
                                        Converters.objectToString()
                                )
                        )
                );

        @Override
        public char decimalSeparator() {
            return DECIMAL_SEPARATOR;
        }

        @Override
        public ExpressionNumberKind expressionNumberKind() {
            return EXPRESSION_NUMBER_KIND;
        }

        @Override
        public char negativeSign() {
            return NEGATIVE_SIGN;
        }

        @Override
        public char positiveSign() {
            return POSITIVE_SIGN;
        }

        @Override
        public MathContext mathContext() {
            return METADATA.mathContext();
        }
    };

    private final static int DEFAULT_YEAR = 1900;
    private final static int TWO_DIGIT_YEAR = 20;
    private final static char VALUE_SEPARATOR = ',';

    private final static SpreadsheetLabelName LABEL = SpreadsheetLabelName.labelName("Label123");
    private final static SpreadsheetCellReference LABEL_CELL = SpreadsheetSelection.parseCell("Z99");

    private final static double COLUMN_WIDTH = 50;

    /**
     * Helper that converts a references string into a map.
     * <pre>
     * A1=B2,C3:D4,Label5;
     * </pre>
     */
    private static Map<SpreadsheetCellReference, Set<SpreadsheetExpressionReference>> references(final String references) {
        final Map<SpreadsheetCellReference, Set<SpreadsheetExpressionReference>> cellToReferences = Maps.sorted();

        for (final String entry : references.split(";")) {
            final int equalsSign = entry.indexOf('=');
            if (-1 == equalsSign) {
                throw new IllegalArgumentException("Missing '=' within " + CharSequences.quoteAndEscape(entry));
            }

            cellToReferences.put(
                    SpreadsheetSelection.parseCell(entry.substring(0, equalsSign)),
                    Arrays.stream(
                                    entry.substring(equalsSign + 1)
                                            .split(",")
                            ).map(SpreadsheetSelection::parseExpressionReference)
                            .collect(Collectors.toCollection(() -> SortedSets.tree(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR)))
            );
        }

        return cellToReferences;
    }

    private final static Map<SpreadsheetColumnReference, Double> COLUMN_A_WIDTH = columnWidths("A");
    private static final AbsoluteUrl SERVER_URL = Url.parseAbsolute("http://server123");

    private static final TextStyle STYLE = TextStyle.EMPTY.set(
            TextStylePropertyName.FONT_WEIGHT,
            FontWeight.BOLD
    );

    private static Map<SpreadsheetColumnReference, Double> columnWidths(final String columns) {
        final Map<SpreadsheetColumnReference, Double> map = Maps.sorted();

        Arrays.stream(columns.split(","))
                .forEach(c ->
                        map.put(
                                SpreadsheetSelection.parseColumn(c),
                                COLUMN_WIDTH
                        )
                );

        return map;
    }

    private final static double ROW_HEIGHT = 30;

    private final static Map<SpreadsheetRowReference, Double> ROW_1_HEIGHT = rowHeights("1");

    private static Map<SpreadsheetRowReference, Double> rowHeights(final String rows) {
        final Map<SpreadsheetRowReference, Double> map = Maps.sorted();

        Arrays.stream(rows.split(","))
                .forEach(r ->
                        map.put(
                                SpreadsheetSelection.parseRow(r),
                                ROW_HEIGHT
                        )
                );

        return map;
    }

    private final static double VIEWPORT_WIDTH = COLUMN_WIDTH * 5;

    private final static double VIEWPORT_HEIGHT = ROW_HEIGHT * 5;

    private final static String TEST_FILTER_CELLS_PREDICATE = "BasicSpreadsheetEngineTestFilterCellsPredicate";

    private final static String TEST_NUMBER_PARAMETER = "BasicSpreadsheetEngineTestNumberParameter";

    private final static String TEST_STRING_PARAMETER = "BasicSpreadsheetEngineTestStringParameter";

    private final static String TEST_SUM = "BasicSpreadsheetEngineTestSum";

    private final static String TEST_VALUE = "BasicSpreadsheetEngineTestValue";

    private final static ExpressionFunctionProvider EXPRESSION_FUNCTION_PROVIDER = new FakeExpressionFunctionProvider() {

        @Override
        public ExpressionFunction<?, ExpressionEvaluationContext> expressionFunction(final ExpressionFunctionName name,
                                                                                     final List<?> values,
                                                                                     final ProviderContext context) {
            switch (name.value()) {
                case TEST_FILTER_CELLS_PREDICATE:
                    return new FakeExpressionFunction<>() {
                        @Override
                        public Object apply(final List<Object> parameters,
                                            final ExpressionEvaluationContext context) {
                            assertEquals(
                                    Lists.empty(),
                                    parameters,
                                    "parameters"
                            );

                            return Boolean.valueOf(
                                    SpreadsheetExpressionEvaluationContext.class.cast(context)
                                            .cellOrFail()
                                            .formula()
                                            .text()
                            );
                        }

                        @Override
                        public List<ExpressionFunctionParameter<?>> parameters(final int count) {
                            return Lists.of(
                                    ExpressionFunctionParameterName.with("parameters")
                                            .variable(Object.class)
                            );
                        }

                        @Override
                        public boolean isPure(final ExpressionPurityContext context) {
                            return false;
                        }
                    };
                case TEST_NUMBER_PARAMETER:
                    return new FakeExpressionFunction<>() {
                        @Override
                        public Object apply(final List<Object> parameters,
                                            final ExpressionEvaluationContext context) {
                            return NUMBER.getOrFail(
                                    parameters,
                                    0
                            );
                        }

                        private final ExpressionFunctionParameter<ExpressionNumber> NUMBER = ExpressionFunctionParameterName.with("parameters")
                                .required(ExpressionNumber.class)
                                .setKinds(ExpressionFunctionParameterKind.CONVERT_EVALUATE_RESOLVE_REFERENCES);

                        @Override
                        public List<ExpressionFunctionParameter<?>> parameters(final int count) {
                            return Lists.of(NUMBER);
                        }

                        @Override
                        public boolean isPure(final ExpressionPurityContext context) {
                            return false;
                        }
                    };
                case TEST_STRING_PARAMETER:
                    return new FakeExpressionFunction<>() {
                        @Override
                        public Object apply(final List<Object> parameters,
                                            final ExpressionEvaluationContext context) {
                            return STRING.getOrFail(
                                    parameters,
                                    0
                            );
                        }

                        private final ExpressionFunctionParameter<String> STRING = ExpressionFunctionParameterName.with("parameters")
                                .required(String.class)
                                .setKinds(ExpressionFunctionParameterKind.CONVERT_EVALUATE_RESOLVE_REFERENCES);

                        @Override
                        public List<ExpressionFunctionParameter<?>> parameters(final int count) {
                            return Lists.of(STRING);
                        }

                        @Override
                        public boolean isPure(final ExpressionPurityContext context) {
                            return false;
                        }
                    };
                case TEST_SUM:
                    return new FakeExpressionFunction<>() {
                        @Override
                        public Object apply(final List<Object> parameters,
                                            final ExpressionEvaluationContext context) {
                            return parameters.stream()
                                    .filter(Objects::nonNull)
                                    .map(ExpressionNumber.class::cast)
                                    .reduce(
                                            context.expressionNumberKind()
                                                    .zero(),
                                            (l, r) -> l.add(r, context)
                                    );
                        }

                        @Override
                        public List<ExpressionFunctionParameter<?>> parameters(final int count) {
                            return Lists.of(
                                    ExpressionFunctionParameterName.with("parameters")
                                            .variable(Object.class)
                                            .setKinds(ExpressionFunctionParameterKind.CONVERT_EVALUATE_FLATTEN_RESOLVE_REFERENCES)
                            );
                        }
                    };
                case TEST_VALUE:
                    return new FakeExpressionFunction<>() {
                        @Override
                        public Object apply(final List<Object> parameters,
                                            final ExpressionEvaluationContext context) {
                            return VALUE;
                        }

                        @Override
                        public List<ExpressionFunctionParameter<?>> parameters(final int count) {
                            return Lists.of(
                                    ExpressionFunctionParameterName.with("parameters")
                                            .variable(Object.class)
                            );
                        }

                        @Override
                        public boolean isPure(final ExpressionPurityContext context) {
                            return false;
                        }
                    };
                default:
                    throw new UnknownExpressionFunctionException(name);
            }
        }

        @Override
        public ExpressionFunctionInfoSet expressionFunctionInfos() {
            return ExpressionFunctionInfoSet.with(
                    Sets.of(
                            info(TEST_FILTER_CELLS_PREDICATE),
                            info(TEST_NUMBER_PARAMETER),
                            info(TEST_STRING_PARAMETER),
                            info(TEST_SUM),
                            info(TEST_VALUE)
                    )
            );
        }

        private ExpressionFunctionInfo info(final String name) {
            return ExpressionFunctionInfo.with(
                    Url.parseAbsolute("https://example.com/" + name),
                    ExpressionFunctionName.with(name).setCaseSensitivity(SpreadsheetExpressionFunctionNames.CASE_SENSITIVITY)
            );
        }

        @Override
        public CaseSensitivity expressionFunctionNameCaseSensitivity() {
            return SpreadsheetExpressionFunctionNames.CASE_SENSITIVITY;
        }
    };

    static {
        final String suffix = " \"" + FORMATTED_PATTERN_SUFFIX + "\"";

        METADATA = METADATA_EN_AU
                .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, CURRENCY_SYMBOL)
                .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, DECIMAL_SEPARATOR)
                .set(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, EXPONENT_SYMBOL)
                .set(SpreadsheetMetadataPropertyName.GROUP_SEPARATOR, GROUP_SEPARATOR)
                .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
                .set(SpreadsheetMetadataPropertyName.NEGATIVE_SIGN, NEGATIVE_SIGN)
                .set(SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL, PERCENTAGE_SYMBOL)
                .set(SpreadsheetMetadataPropertyName.POSITIVE_SIGN, POSITIVE_SIGN)
                .set(SpreadsheetMetadataPropertyName.VALUE_SEPARATOR, VALUE_SEPARATOR)
                .set(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, DEFAULT_YEAR)
                .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, EXPRESSION_NUMBER_KIND)
                .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.HALF_UP)
                .set(SpreadsheetMetadataPropertyName.PRECISION, 7)
                .set(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR, TWO_DIGIT_YEAR)
                .set(
                        SpreadsheetMetadataPropertyName.FIND_FUNCTIONS,
                        ExpressionFunctionAliasSet.parse(
                                TEST_FILTER_CELLS_PREDICATE
                        )
                ).set(
                        SpreadsheetMetadataPropertyName.FORMULA_FUNCTIONS,
                        ExpressionFunctionAliasSet.parse(
                                        TEST_NUMBER_PARAMETER +
                                        "," +
                                        TEST_STRING_PARAMETER +
                                        "," +
                                        TEST_SUM +
                                        "," +
                                        TEST_VALUE
                        )
                ).set(SpreadsheetMetadataPropertyName.DATE_FORMATTER, SpreadsheetPattern.parseDateFormatPattern(DATE_PATTERN + suffix).spreadsheetFormatterSelector())
                .set(SpreadsheetMetadataPropertyName.DATE_PARSER, SpreadsheetPattern.parseDateParsePattern(DATE_PATTERN + ";dd/mm").spreadsheetParserSelector())
                .set(SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER, SpreadsheetPattern.parseDateTimeFormatPattern(DATETIME_PATTERN + suffix).spreadsheetFormatterSelector())
                .set(SpreadsheetMetadataPropertyName.DATE_TIME_PARSER, SpreadsheetPattern.parseDateTimeParsePattern(DATETIME_PATTERN).spreadsheetParserSelector())
                .set(SpreadsheetMetadataPropertyName.NUMBER_FORMATTER, SpreadsheetPattern.parseNumberFormatPattern(NUMBER_PATTERN + suffix).spreadsheetFormatterSelector())
                .set(SpreadsheetMetadataPropertyName.NUMBER_PARSER, SpreadsheetPattern.parseNumberParsePattern(NUMBER_PATTERN).spreadsheetParserSelector())
                .set(SpreadsheetMetadataPropertyName.TEXT_FORMATTER, SpreadsheetPattern.parseTextFormatPattern(TEXT_PATTERN + suffix).spreadsheetFormatterSelector())
                .set(SpreadsheetMetadataPropertyName.TIME_FORMATTER, SpreadsheetPattern.parseTimeFormatPattern(TIME_PATTERN + suffix).spreadsheetFormatterSelector())
                .set(SpreadsheetMetadataPropertyName.TIME_PARSER, SpreadsheetPattern.parseTimeParsePattern(TIME_PATTERN).spreadsheetParserSelector())
                .set(SpreadsheetMetadataPropertyName.STYLE, TextStyle.EMPTY
                        .set(TextStylePropertyName.WIDTH, Length.parsePixels(COLUMN_WIDTH + "px"))
                        .set(TextStylePropertyName.HEIGHT, Length.parsePixels(ROW_HEIGHT + "px"))
                );
    }

    private final static SpreadsheetMetadata METADATA;

    private static Object VALUE;

    // loadCells........................................................................................................

    @Test
    public void testLoadMultipleCellRangesCellWhenEmpty() {
        this.loadCellFailCheck(
                SpreadsheetSelection.A1,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY
        );
    }

    @Test
    public void testLoadMultipleCellRangesWithFormulaWithInvalidValueFails() {
        this.loadCellFails(
                "1.X",
                SpreadsheetErrorKind.ERROR.setMessage("Invalid character \'1\' at 0 expected \"\\\'\", [STRING] | EQUALS_EXPRESSION | VALUE")
        );
    }

    @Test
    public void testLoadMultipleCellRangesWithFormulaExpressionErrorFails() {
        this.loadCellFails(
                "=1+",
                SpreadsheetErrorKind.ERROR.setMessage(
                        "End of text at (4,1) expected LAMBDA_FUNCTION | NAMED_FUNCTION | \"true\" | \"false\" | LABEL | CELL_RANGE | CELL | GROUP | NEGATIVE | \"#.#E+#;#.#%;#.#;#%;#\" | TEXT | \"#NULL!\" | \"#DIV/0!\" | \"#VALUE!\" | \"#REF!\" | \"#NAME?\" | \"#NAME?\" | \"#NUM!\" | \"#N/A\" | \"#ERROR\" | \"#SPILL!\" | \"#CALC!\""
                )
        );
    }

    @Test
    public void testLoadMultipleCellRangesWithFormulaWithInvalidLabelFails() {
        this.loadCellFails(
                "=UnknownLabel",
                SpreadsheetError.selectionNotFound(SpreadsheetSelection.labelName("UnknownLabel"))
        );
    }

    @Test
    public void testLoadMultipleCellRangesWithDivideByZeroFails() {
        this.loadCellFails(
                "=1/0",
                SpreadsheetErrorKind.DIV0.setMessage("Division by zero")
        );
    }

    @Test
    public void testLoadMultipleCellRangesWithUnknownFunctionFails() {
        this.loadCellFails(
                "=unknownFunction()",
                SpreadsheetError.functionNotFound(
                        ExpressionFunctionName.with("unknownFunction")
                                .setCaseSensitivity(SpreadsheetExpressionFunctionNames.CASE_SENSITIVITY)
                )
        );
    }

    private void loadCellFails(final String formulaText,
                               final SpreadsheetError error) {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;

        context.storeRepository()
                .cells()
                .save(a1.setFormula(SpreadsheetFormula.EMPTY.setText(formulaText)));

        final String errorMessage = error.message();

        this.loadCellAndCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                error,
                error.kind().text() + " " + FORMATTED_PATTERN_SUFFIX, // formatted text
                errorMessage
        );
    }

    @Test
    public void testLoadMultipleCellRangesFormulaWithMissingCellReference() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;

        context.storeRepository()
                .cells()
                .save(a1.setFormula(SpreadsheetFormula.EMPTY.setText("=Z99")));

        this.loadCellAndCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                EXPRESSION_NUMBER_KIND.zero(),
                " " + FORMATTED_PATTERN_SUFFIX
        );
    }

    @Test
    public void testLoadMultipleCellRangesFormulaWithMissingCellReference2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;

        context.storeRepository()
                .cells()
                .save(a1.setFormula(SpreadsheetFormula.EMPTY.setText("=2+Z99")));

        this.loadCellAndCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                EXPRESSION_NUMBER_KIND.create(2),
                "2 " + FORMATTED_PATTERN_SUFFIX
        );
    }

    @Test
    public void testLoadMultipleCellRangesFormulaWithUnknownLabel() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);


        final SpreadsheetLabelMapping labelMapping = LABEL.setLabelMappingReference(LABEL_CELL);
        context.storeRepository()
                .labels()
                .save(labelMapping);

        this.checkEquals(
                SpreadsheetDelta.EMPTY
                        .setCells(SpreadsheetDelta.NO_CELLS)
                        .setDeletedCells(
                                Sets.of(LABEL_CELL)
                        ).setLabels(
                                Sets.of(LABEL.setLabelMappingReference(LABEL_CELL))
                        ).setColumnCount(
                                OptionalInt.of(0)
                        ).setRowCount(
                                OptionalInt.of(0)
                        ),
                engine.loadCells(
                        LABEL_CELL,
                        SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                        SpreadsheetDeltaProperties.ALL,
                        context
                )
        );
    }

    @Test
    public void testLoadMultipleCellRangesFormulaWithFunctionMissingCellNumberParameter() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;

        context.storeRepository()
                .cells()
                .save(
                        a1.setFormula(
                                SpreadsheetFormula.EMPTY
                                        .setText("=BasicSpreadsheetEngineTestNumberParameter(A2)")
                        )
                );

        this.loadCellAndCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                EXPRESSION_NUMBER_KIND.zero(),
                " " + FORMATTED_PATTERN_SUFFIX
        );
    }

    @Test
    public void testLoadMultipleCellRangesFormulaWithFunctionMissingCellStringParameter() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;

        context.storeRepository()
                .cells()
                .save(
                        a1.setFormula(
                                SpreadsheetFormula.EMPTY
                                        .setText("=BasicSpreadsheetEngineTestStringParameter(A2)")
                        )
                );

        this.loadCellAndCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                SpreadsheetError.selectionNotFound(
                        SpreadsheetSelection.parseCell("A2")
                ).setNameString(),
                "#NAME? " + FORMATTED_PATTERN_SUFFIX
        );
    }

    @Test
    public void testLoadMultipleCellRangesFormulaWithLabelToMissingCell() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        this.checkEquals(
                Optional.empty(),
                context.storeRepository()
                        .cells()
                        .load(LABEL_CELL)
        );

        final SpreadsheetLabelMapping labelMapping = context.storeRepository()
                .labels()
                .save(LABEL.setLabelMappingReference(LABEL_CELL));

        this.checkEquals(
                SpreadsheetDelta.EMPTY
                        .setCells(SpreadsheetDelta.NO_CELLS)
                        .setDeletedCells(
                                Sets.of(LABEL_CELL)
                        )
                        .setLabels(
                                Sets.of(labelMapping)
                        ).setColumnCount(
                                OptionalInt.of(0)
                        ).setRowCount(
                                OptionalInt.of(0)
                        ),
                engine.loadCells(
                        LABEL,
                        SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                        SpreadsheetDeltaProperties.ALL,
                        context
                )
        );
    }

    @Test
    public void testLoadMultipleCellRangesSkipEvaluate() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("$B$2");
        context.storeRepository()
                .cells()
                .save(this.cell(b2, "=1+2"));

        this.loadCellAndWithoutValueOrErrorCheck(
                engine,
                b2,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context
        );
    }

    @Test
    public void testLoadMultipleCellRangesCellWithoutFormatter() {
        this.cellStoreSaveAndLoadCellAndCheck(
                "=1+2",
                1 + 2,
                SpreadsheetCell.NO_FORMATTER,
                "3 " + FORMATTED_PATTERN_SUFFIX
        );
    }

    @Test
    public void testLoadMultipleCellRangesCellWithFormatter() {
        this.cellStoreSaveAndLoadCellAndCheck(
                "=1+2",
                1 + 2,
                Optional.of(
                        SpreadsheetPattern.parseNumberFormatPattern("# \"" + FORMATTED_PATTERN_SUFFIX + "\"")
                                .spreadsheetFormatterSelector()
                ),
                "3 " + FORMATTED_PATTERN_SUFFIX
        );
    }

    @Test
    public void testLoadMultipleCellRangesWithErrorAndFormatter() {
        this.cellStoreSaveAndLoadCellAndCheck(
                "=1/0",
                SpreadsheetErrorKind.DIV0.setMessage("Division by zero"),
                Optional.of(
                        SpreadsheetPattern.parseTextFormatPattern("@ \"" + FORMATTED_PATTERN_SUFFIX + "\"")
                                .spreadsheetFormatterSelector()
                ),
                "#DIV/0! " + FORMATTED_PATTERN_SUFFIX
        );
    }

    private void cellStoreSaveAndLoadCellAndCheck(final String formulaText,
                                                  final Object value,
                                                  final Optional<SpreadsheetFormatterSelector> formatter,
                                                  final String formattedText) {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("$B$2");
        context.storeRepository()
                .cells()
                .save(
                        this.cell(
                                        b2,
                                        formulaText
                                )
                                .setFormatter(formatter)
                );
        this.loadCellAndCheck(
                engine,
                b2,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                value instanceof Number ?
                        EXPRESSION_NUMBER_KIND.create((Number)value) :
                        value,
                formattedText
        );
    }

    @Test
    public void testLoadMultipleCellRangesComputeIfNecessaryCachesCell() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("$B$2");
        context.storeRepository()
                .cells()
                .save(this.cell(b2, "=1+2"));

        final SpreadsheetCell first = this.loadCellOrFail(
                engine,
                b2,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context
        );

        final SpreadsheetCell second = this.loadCellOrFail(
                engine,
                b2,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context
        );

        assertSame(first, second, "different instances of SpreadsheetCell returned not cached");
    }

    @Test
    public void testLoadMultipleCellRangesParserFails() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("$B$2");
        context.storeRepository()
                .cells()
                .save(
                        this.cell(
                                b2,
                                "=1+2"
                        ).setParser(
                                Optional.of(
                                        SpreadsheetPattern.parseNumberParsePattern("#")
                                                .spreadsheetParserSelector()
                                )
                        )
                );

        this.loadCellAndErrorCheck(
                engine,
                b2,
                SpreadsheetEngineEvaluation.FORCE_RECOMPUTE,
                context,
                "Invalid character '=' at 0"
        );
    }

    @Test
    public void testLoadMultipleCellRangesParser() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("$B$2");
        context.storeRepository()
                .cells()
                .save(
                        this.cell(
                                b2,
                                "123"
                        ).setParser(
                                Optional.of(
                                        SpreadsheetPattern.parseNumberParsePattern("$#;#")
                                                .spreadsheetParserSelector()
                                )
                        )
                );

        this.loadCellAndCheck(
                engine,
                b2,
                SpreadsheetEngineEvaluation.FORCE_RECOMPUTE,
                context,
                EXPRESSION_NUMBER_KIND.create(123),
                "123 FORMATTED_PATTERN_SUFFIX"
        );
    }

    @Test
    public void testLoadMultipleCellRangesComputeIfNecessaryKeepsExpression() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("$B$2");
        context.storeRepository()
                .cells()
                .save(this.cell(b2, "1/2"));

        final SpreadsheetCell first = this.loadCellOrFail(engine, b2, SpreadsheetEngineEvaluation.FORCE_RECOMPUTE, context);
        this.cellFormulaValueAndCheck(first, LocalDate.of(DEFAULT_YEAR, 2, 1));

        final int defaultYear = DEFAULT_YEAR + 100;

        final SpreadsheetCell second = this.loadCellOrFail(
                engine,
                b2,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                this.createContext(defaultYear, engine, context.storeRepository()));

        assertSame(first, second, "same instances of SpreadsheetCell returned should have new expression and value");

        this.cellFormulaValueAndCheck(
                second,
                LocalDate.of(1900, 2, 1)
        );
        this.cellFormattedValueAndCheck(
                second,
                "1900/02/01 FORMATTED_PATTERN_SUFFIX"
        );
    }

    @Test
    public void testLoadMultipleCellRangesComputeIfNecessaryHonoursExpressionIsPure() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("$B$2");
        context.storeRepository()
                .cells()
                .save(this.cell(b2, "1/2"));

        final SpreadsheetCell first = this.loadCellOrFail(
                engine,
                b2,
                SpreadsheetEngineEvaluation.FORCE_RECOMPUTE,
                context
        );
        this.cellFormulaValueAndCheck(
                first,
                LocalDate.of(DEFAULT_YEAR, 2, 1)
        );

        final int defaultYear = DEFAULT_YEAR + 100;

        final SpreadsheetCell second = this.loadCellOrFail(
                engine,
                b2,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                this.createContext(defaultYear, engine, context.storeRepository())
        );

        assertSame(first, second, "same instances of SpreadsheetCell returned should have new expression and value");

        this.cellFormulaValueAndCheck(
                second,
                LocalDate.of(1900, 2, 1)
        );
        this.cellFormattedValueAndCheck(
                second,
                "1900/02/01 FORMATTED_PATTERN_SUFFIX"
        );
    }

    @Test
    public void testLoadMultipleCellRangesComputeIfNecessaryHonoursFunctionIsPure() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("$B$2");
        context.storeRepository()
                .cells()
                .save(
                        this.cell(
                                b2,
                                "=BasicSpreadsheetEngineTestValue()"
                        )
                );

        final Object value = EXPRESSION_NUMBER_KIND.one();
        VALUE = value;

        final SpreadsheetCell first = this.loadCellOrFail(
                engine,
                b2,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context
        );
        this.cellFormulaValueAndCheck(
                first,
                value
        );
        this.cellFormattedValueAndCheck(
                first,
                "1 FORMATTED_PATTERN_SUFFIX"
        );

        final Object value2 = EXPRESSION_NUMBER_KIND.create(2);
        VALUE = value2;

        final SpreadsheetCell second = this.loadCellOrFail(
                engine,
                b2,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context
        );
        this.cellFormulaValueAndCheck(
                second,
                value2
        );
        this.cellFormattedValueAndCheck(
                second,
                "2 FORMATTED_PATTERN_SUFFIX"
        );
    }

    @Test
    public void testLoadMultipleCellRangesComputeIfNecessaryCachesCellWithInvalidFormulaAndErrorCached() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("$B$2");
        context.storeRepository()
                .cells()
                .save(this.cell(b2, "=1+2+"));

        final SpreadsheetCell first = this.loadCellOrFail(engine,
                b2,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context);
        this.checkNotEquals(
                SpreadsheetFormula.NO_VALUE,
                first.formula()
                        .error(),
                () -> "Expected error absent=" + first
        );

        final SpreadsheetCell second = this.loadCellOrFail(engine, b2, SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY, context);
        assertSame(first, second, "different instances of SpreadsheetCell returned not cached");

        this.cellFormattedValueAndCheck(
                second,
                "#ERROR " + FORMATTED_PATTERN_SUFFIX
        );
    }

    @Test
    public void testLoadMultipleCellRangesForceRecomputeIgnoresValue() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("$B$2");
        context.storeRepository()
                .cells()
                .save(
                        this.cell(
                                b2,
                                "1/2"
                        )
                );

        final SpreadsheetCell first = this.loadCellOrFail(
                engine,
                b2,
                SpreadsheetEngineEvaluation.FORCE_RECOMPUTE,
                context
        );
        this.cellFormulaValueAndCheck(
                first,
                LocalDate.of(
                        DEFAULT_YEAR,
                        2,
                        1
                )
        );

        final int defaultYear = DEFAULT_YEAR + 100;

        final SpreadsheetCell second = this.loadCellOrFail(
                engine,
                b2,
                SpreadsheetEngineEvaluation.FORCE_RECOMPUTE,
                this.createContext(
                        defaultYear,
                        engine,
                        context.storeRepository()
                )
        );

        assertNotSame(first, second, "same instances of SpreadsheetCell returned should have new expression and value");
        this.cellFormulaValueAndCheck(
                second,
                LocalDate.of(defaultYear, 2, 1)
        );
        this.cellFormattedValueAndCheck(
                second,
                "2000/02/01 FORMATTED_PATTERN_SUFFIX"
        );
    }

    @Test
    public void testLoadMultipleCellRangesForceRecomputeIgnoresCache() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("$B$2");
        cellStore.save(this.cell(b2, "=1"));

        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("$C$3");
        cellStore.save(this.cell(c3, "=" + b2));

        final SpreadsheetCell first = this.loadCellOrFail(
                engine,
                b2,
                SpreadsheetEngineEvaluation.FORCE_RECOMPUTE,
                context
        );

        cellStore.save(
                this.cell(
                        b2,
                        "=999"
                )
        );

        final SpreadsheetCell second = this.loadCellOrFail(
                engine,
                b2,
                SpreadsheetEngineEvaluation.FORCE_RECOMPUTE,
                context
        );
        assertNotSame(
                first,
                second,
                "different instances of SpreadsheetCell returned not cached"
        );
        this.checkEquals(
                Optional.of(
                        EXPRESSION_NUMBER_KIND.create(999)
                ),
                second.formula().value(),
                "first should have value updated to 999 and not 1 the original value."
        );
    }

    @Test
    public void testLoadMultipleCellRangesForceRecomputeIgnoresPreviousError() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCell unsaved = this.cell(
                a1,
                "=1+$B$2"
        );
        final Set<SpreadsheetCell> saved = engine.saveCell(
                        unsaved,
                        context
                ).cells();

        this.checkEquals(
                this.formatCell(
                        unsaved,
                        EXPRESSION_NUMBER_KIND.one()
                ),
                saved.iterator().next()
        );

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("B2");
        context.storeRepository()
                .cells()
                .save(
                        this.cell(
                                b2,
                                "=99"
                        )
                );

        this.loadCellAndCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.FORCE_RECOMPUTE,
                context,
                EXPRESSION_NUMBER_KIND.create(1 + 99),
                "100 FORMATTED_PATTERN_SUFFIX"
        );
    }

    @Test
    public void testLoadMultipleCellRangesWithComputeThenSkipEvaluate() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("$B$2");
        context.storeRepository()
                .cells()
                .save(this.cell(b2, "=1+2"));

        final SpreadsheetCell first = this.loadCellOrFail(
                engine,
                b2,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context
        );
        final SpreadsheetCell second = this.loadCellOrFail(
                engine,
                b2,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context
        );

        assertSame(
                first,
                second,
                "different instances of SpreadsheetCell returned not cached"
        );
    }

    @Test
    public void testLoadMultipleCellRangesManyCellsFormulasWithoutCrossCellReferences() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("$B$2");
        final SpreadsheetCellReference c2 = SpreadsheetSelection.parseCell("$C$2");
        final SpreadsheetCellReference d2 = SpreadsheetSelection.parseCell("$D$2");

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();
        cellStore.save(this.cell(b2, "=1+2"));
        cellStore.save(this.cell(c2, "=3+4"));
        cellStore.save(this.cell(d2, "=5+6"));

        this.loadCellAndCheck(
                engine,
                b2,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                1 + 2
        );
        this.loadCellAndCheck(
                engine,
                c2,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                3 + 4
        );
        this.loadCellAndCheck(
                engine,
                d2,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                5 + 6
        );
    }

    @Test
    public void testLoadMultipleCellRangesFormulaWithCrossCellReferences() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("$B$2");
        final SpreadsheetCellReference c2 = SpreadsheetSelection.parseCell("$C$2");

        VALUE = BigDecimal.ZERO;

        engine.saveCell(
                this.cell(
                        b2,
                        "=1+2+BasicSpreadsheetEngineTestValue()"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        c2,
                        "=3+4+" + b2
                ),
                context
        );

        // updating this counter results in $A having its value recomputed forcing a cascade update of $b and $c
        VALUE = 100;

        // dont need to load C2, because B2 was unchanged.
        this.loadCellAndCheck(
                engine,
                b2,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        formatCell(
                                                b2,
                                                "=1+2+BasicSpreadsheetEngineTestValue()",
                                                100 + 3
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("B")
                        ).setRowHeights(
                                rowHeights("2")
                        ).setColumnCount(
                                OptionalInt.of(3)
                        ).setRowCount(
                                OptionalInt.of(2)
                        )
        );
    }

    @Test
    public void testLoadMultipleCellRangesFormulaWithCrossCellReferences2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("$B$2");
        final SpreadsheetCellReference c2 = SpreadsheetSelection.parseCell("$C$2");
        final SpreadsheetCellReference d2 = SpreadsheetSelection.parseCell("$D$2");

        VALUE = BigDecimal.ZERO;

        engine.saveCell(
                this.cell(
                        b2,
                        "=1+2+BasicSpreadsheetEngineTestValue()"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        c2,
                        "=3+4+" + b2
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        d2,
                        "=5+6+" + b2
                ),
                context
        );

        // updating this counter results in $A having its value recomputed forcing a cascade update of $b and $c
        VALUE = 100;

        // should only load B2,
        // C2, D2 will not have changed and shouldnt be loaded.
        this.loadCellAndCheck(
                engine,
                b2,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        formatCell(
                                                b2,
                                                "=1+2+BasicSpreadsheetEngineTestValue()",
                                                100 + 3
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("B")
                        ).setRowHeights(
                                rowHeights("2")
                        ).setColumnCount(
                                OptionalInt.of(4)
                        ).setRowCount(
                                OptionalInt.of(2)
                        )
        );
    }

    @Test
    public void testLoadMultipleCellRangesWhereValueLabelInvalidFails() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1.toAbsolute();
        context.storeRepository()
                .cells()
                .save(
                        this.cell(
                                a1,
                                "=INVALIDLABEL"
                        )
                );

        this.loadCellAndErrorCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "Label not found"
        );
    }

    @Test
    public void testLoadMultipleCellRangesWhereValueIsCellReference() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1.toAbsolute();
        final SpreadsheetCellReference b1 = SpreadsheetSelection.parseCell("$B$1");

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();
        cellStore.save(
                this.cell(
                        a1,
                        "=B1"
                )
        );
        cellStore.save(
                this.cell(
                        b1,
                        "=3+4"
                )
        );

        // formula
        this.loadCellAndCheck(
                engine,
                b1,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                3 + 4
        );

        // reference to B1 which has formula
        this.loadCellAndCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                3 + 4
        );
    }

    @Test
    public void testLoadMultipleCellRangesWhereValueIsLabel() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1.toAbsolute();
        final SpreadsheetCellReference b1 = SpreadsheetSelection.parseCell("$B$1");

        final SpreadsheetStoreRepository repository = context.storeRepository();

        final SpreadsheetCellStore cellStore = repository.cells();
        cellStore.save(
                this.cell(
                        a1,
                        "=" + LABEL.value()
                )
        );
        cellStore.save(
                this.cell(
                        b1,
                        "=3+4"
                )
        );

        repository.labels()
                .save(
                        SpreadsheetLabelMapping.with(LABEL, b1)
                );

        // formula
        this.loadCellAndCheck(
                engine,
                b1,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                3 + 4
        );

        // reference to B1 which has formula
        this.loadCellAndCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                3 + 4
        );
    }

    @Test
    public void testLoadCellWithSpreadsheetDeltaPropertiesCells() {
        this.loadCellAndCheck(
                SpreadsheetDeltaProperties.CELLS
        );
    }

    @Test
    public void testLoadCellWithSpreadsheetDeltaPropertiesCellsLabel() {
        this.loadCellAndCheck(
                SpreadsheetDeltaProperties.CELLS,
                SpreadsheetDeltaProperties.LABELS
        );
    }

    @Test
    public void testLoadCellWithSpreadsheetDeltaPropertiesCellsColumnWidths() {
        this.loadCellAndCheck(
                SpreadsheetDeltaProperties.CELLS,
                SpreadsheetDeltaProperties.COLUMN_WIDTHS
        );
    }

    @Test
    public void testLoadCellWithSpreadsheetDeltaPropertiesCellsRowHeights() {
        this.loadCellAndCheck(
                SpreadsheetDeltaProperties.CELLS,
                SpreadsheetDeltaProperties.ROW_HEIGHTS
        );
    }

    @Test
    public void testLoadCellWithSpreadsheetDeltaPropertiesAll() {
        this.loadCellAndCheck(SpreadsheetDeltaProperties.ALL);
    }

    private void loadCellAndCheck(final SpreadsheetDeltaProperties... deltaProperties) {
        this.loadCellAndCheck(
                Sets.of(deltaProperties)
        );
    }

    private void loadCellAndCheck(final Set<SpreadsheetDeltaProperties> deltaProperties) {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;

        final SpreadsheetCell cell = this.cell(a1, "=1+2");

        context.storeRepository()
                .cells()
                .save(cell);

        final SpreadsheetLabelMapping label = LABEL.setLabelMappingReference(a1);

        context.storeRepository()
                .labels()
                .save(label);

        final SpreadsheetDelta delta = engine.loadCells(
                a1,
                SpreadsheetEngineEvaluation.FORCE_RECOMPUTE,
                deltaProperties,
                context
        );

        if (deltaProperties.contains(SpreadsheetDeltaProperties.CELLS)) {
            this.checkNotEquals(
                    Sets.empty(),
                    delta.cells(),
                    () -> "cells should have cell, " + deltaProperties
            );
        } else {
            this.checkEquals(
                    Sets.empty(),
                    delta.cells(),
                    () -> "cells should be empty, " + deltaProperties
            );
        }

        this.checkEquals(
                deltaProperties.contains(SpreadsheetDeltaProperties.LABELS) ? Sets.of(label) : Sets.empty(),
                delta.labels(),
                () -> "labels, " + deltaProperties
        );

        this.checkEquals(
                deltaProperties.contains(SpreadsheetDeltaProperties.COLUMN_WIDTHS) ?
                        Maps.of(a1.column(), COLUMN_WIDTH) :
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                delta.columnWidths(),
                () -> "columnWidths, " + deltaProperties
        );

        this.checkEquals(
                deltaProperties.contains(SpreadsheetDeltaProperties.ROW_HEIGHTS) ?
                        Maps.of(a1.row(), ROW_HEIGHT) :
                        SpreadsheetDelta.NO_ROW_HEIGHTS,
                delta.rowHeights(),
                () -> "rowHeights, " + deltaProperties
        );
    }

    private void saveRule(final boolean result,
                          final int priority,
                          final TextStyle style,
                          final SpreadsheetCellReference cell,
                          final SpreadsheetCellRangeStore<SpreadsheetConditionalFormattingRule> rules) {
        rules.addValue(cell.cellRange(cell), rule(result, priority, style));
    }

    private SpreadsheetConditionalFormattingRule rule(final boolean result,
                                                      final int priority,
                                                      final TextStyle style) {


        return SpreadsheetConditionalFormattingRule.with(SpreadsheetDescription.with(priority + "=" + result),
                priority,
                SpreadsheetFormula.EMPTY
                        .setText(
                                String.valueOf(result)
                        ).setExpression(
                                Optional.of(
                                        Expression.value(result)
                                )
                        ),
                (c) -> style);
    }

    @Test
    public void testLoadMultipleCellRangesWithCellRange() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCell b2 = cellStore.save(
                this.cell(
                        SpreadsheetSelection.parseCell("B2"),
                        "=22"
                )
        );

        final SpreadsheetCell c3 = cellStore.save(
                this.cell(
                        SpreadsheetSelection.parseCell("c3"),
                        "=33"
                )
        );

        this.checkEquals(
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(b2, EXPRESSION_NUMBER_KIND.create(22)),
                                        this.formatCell(c3, EXPRESSION_NUMBER_KIND.create(33))
                                )
                        ).setDeletedCells(
                                Sets.of(
                                        SpreadsheetSelection.parseCell("B3"),
                                        SpreadsheetSelection.parseCell("C2")
                                )
                        ).setColumnWidths(
                                columnWidths("B,C")
                        ).setRowHeights(
                                rowHeights("2,3")
                        ).setColumnCount(
                                OptionalInt.of(3)
                        ).setRowCount(
                                OptionalInt.of(3)
                        ),
                engine.loadCells(
                        SpreadsheetSelection.parseCellRange("B2:C3"),
                        SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                        SpreadsheetDeltaProperties.ALL,
                        context
                )
        );
    }

    // saveCell.........................................................................................................

    @Test
    public void testSaveCellWithEmptyFormula() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell("a1", "");
        final SpreadsheetCell a1Formatted = this.formatCell(a1);
        this.saveCellAndCheck(
                engine,
                a1,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(a1Formatted)
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        );

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetCellReferencesStore cellReferenceStore = repository.cellReferences();

        this.loadCellStoreAndCheck(cellStore, a1Formatted);
        this.loadLabelStoreAndCheck(labelStore);
        this.countAndCheck(cellReferenceStore, 0);
    }

    @Test
    public void testSaveCellWithFormulaSelfReferenceCycle() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell(
                "a1",
                "=A1"
        );
        final SpreadsheetCell a1Formatted = this.formatCell(
                a1,
                SpreadsheetError.cycle(a1.reference())
        );
        this.saveCellAndCheck(
                engine,
                a1,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(a1Formatted)
                        ).setReferences(references("A1=A1")
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        );

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetCellReferencesStore cellReferenceStore = repository.cellReferences();

        this.loadCellStoreAndCheck(cellStore, a1Formatted);
        this.loadLabelStoreAndCheck(labelStore);
        this.countAndCheck(
                cellReferenceStore,
                1
        );
    }

    @Test
    public void testSaveCellWithEmptyFormulaTwice() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell("a1", "");
        final SpreadsheetCell a1Formatted = this.formatCell(a1);
        this.saveCellAndCheck(
                engine,
                a1,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(a1Formatted)
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        );

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetCellReferencesStore cellReferenceStore = repository.cellReferences();

        this.loadCellStoreAndCheck(cellStore, a1Formatted);
        this.loadLabelStoreAndCheck(labelStore);
        this.countAndCheck(cellReferenceStore, 0);

        this.saveCellAndCheck(
                engine,
                a1,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(a1Formatted)
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        );
    }

    @Test
    public void testSaveCellWithFormulaWithInvalidDate() {
        this.saveCellWithErrorAndCheck(
                "1999/99/31",
                SpreadsheetErrorKind.VALUE.setMessage(
                        "Invalid value for MonthOfYear (valid values 1 - 12): 99"
                )
        );
    }

    @Test
    public void testSaveCellWithFormulaWithInvalidDateTime() {
        this.saveCellWithErrorAndCheck(
                "1999/99/31 12:58",
                SpreadsheetErrorKind.VALUE.setMessage("Invalid value for MonthOfYear (valid values 1 - 12): 99")
        );
    }

    @Test
    public void testSaveCellWithFormulaWithInvalidTime() {
        this.saveCellWithErrorAndCheck(
                "12:99",
                SpreadsheetErrorKind.VALUE.setMessage("Invalid value for MinuteOfHour (valid values 0 - 59): 99")
        );
    }

    private void saveCellWithErrorAndCheck(final String formula,
                                           final SpreadsheetError error) {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell("a1", formula);
        final SpreadsheetCell a1Formatted = this.formatCell(
                a1,
                error
        );

        this.saveCellAndCheck(
                engine,
                a1,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        a1Formatted
                                )
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        );

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetCellReferencesStore cellReferenceStore = repository.cellReferences();

        this.loadCellStoreAndCheck(cellStore, a1Formatted);
        this.loadLabelStoreAndCheck(labelStore);
        this.countAndCheck(cellReferenceStore, 0);
    }

    @Test
    public void testSaveCellWithFormulaOnlyMathExpression() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell("a1", "=1+2");
        final SpreadsheetCell a1Formatted = this.formatCell(
                a1,
                3
        );
        this.saveCellAndCheck(
                engine,
                a1,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        a1Formatted
                                )
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        );

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetCellReferencesStore cellReferenceStore = repository.cellReferences();

        this.loadCellStoreAndCheck(cellStore, a1Formatted);
        this.loadLabelStoreAndCheck(labelStore);
        this.countAndCheck(cellReferenceStore, 0);
    }

    @Test
    public void testSaveCellWithFormulaWithUnknownCellReference() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetCellReferencesStore cellReferenceStore = repository.cellReferences();

        final SpreadsheetCell a1 = this.cell(
                "a1",
                "=$B$2+99"
        );
        final SpreadsheetCell a1Formatted = this.formatCell(
                a1,
                99
        );

        this.saveCellAndCheck(
                engine,
                a1,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        a1Formatted
                                )
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        );

        this.loadCellStoreAndCheck(
                cellStore,
                a1Formatted
        );
        this.loadLabelStoreAndCheck(labelStore);

        // verify references all ways are present in the store.
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("$B$2");

        this.loadReferencesAndCheck(
               cellReferenceStore,
               b2.toRelative(),
               a1.reference()
        );
        this.findReferencesWithCellAndCheck(
                cellReferenceStore,
                a1.reference(),
                b2
        );
        this.loadReferencesAndCheck(
                cellReferenceStore,
                a1.reference()
        );
        this.findReferencesWithCellAndCheck(
                cellReferenceStore,
                b2
        );
    }

    @Test
    public void testSaveCellWithIgnoresPreviousErrorComputesValue() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("$B$2");

        final SpreadsheetCell cell = this.cell(
                b2,
                SpreadsheetFormula.EMPTY
                        .setText("=1+2")
                        .setValue(
                                Optional.of(
                                        SpreadsheetErrorKind.VALUE.setMessage("error!")
                                )
                        )
        );

        this.saveCellAndCheck(
                engine,
                cell,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                cell,
                                                1 + 2
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("B")
                        ).setRowHeights(
                                rowHeights("2")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(2)
                        )
        );
    }

    @Test
    public void testSaveCellWithSecondTimeWithDifferentStyle() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("$B$2");

        final SpreadsheetCell cell = b2.setFormula(
                SpreadsheetFormula.EMPTY
                        .setText("=1+2")
        );

        final SpreadsheetCell cellWithValue = this.formatCell(
                cell,
                1 + 2,
                TextStyle.EMPTY
        );

        this.saveCellAndCheck(
                engine,
                cell,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        cellWithValue
                                )
                        ).setColumnWidths(
                                columnWidths("B")
                        ).setRowHeights(
                                rowHeights("2")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(2)
                        )
        );

        final TextStyle newStyle = TextStyle.EMPTY
                .set(
                        TextStylePropertyName.COLOR,
                        Color.parse("#123456")
                );

        this.saveCellAndCheck(
                engine,
                cellWithValue.setStyle(newStyle),
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                cell.setStyle(newStyle),
                                                1 + 2,
                                                newStyle
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("B")
                        ).setRowHeights(
                                rowHeights("2")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(2)
                        )
        );
    }

    @Test
    public void testSaveCellWithMultipleIndependentUnreferenced() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetCellReferencesStore cellReferenceStore = repository.cellReferences();

        final SpreadsheetCell a1 = this.cell(
                "$A$1",
                "=1+2"
        );
        final SpreadsheetCell a1Formatted = this.formatCell(
                a1,
                3
        );

        this.saveCellAndCheck(
                engine,
                a1,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(a1Formatted)
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        );

        final SpreadsheetCell b2 = this.cell(
                "$B$2",
                "=3+4"
        );
        final SpreadsheetCell b2Formatted = this.formatCell(
                b2,
                7
        );

        this.saveCellAndCheck(
                engine,
                b2,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        b2Formatted
                                )
                        ).setColumnWidths(
                                columnWidths("B")
                        ).setRowHeights(
                                rowHeights("2")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(2)
                        )
        );

        final SpreadsheetCell c3 = this.cell(
                "$C$3",
                "=5+6"
        );
        final SpreadsheetCell c3Formatted = this.formatCell(
                c3,
                11
        );

        this.saveCellAndCheck(
                engine,
                c3,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(c3Formatted)
                        ).setColumnWidths(
                                columnWidths("C")
                        ).setRowHeights(
                                rowHeights("3")
                        ).setColumnCount(
                                OptionalInt.of(3)
                        ).setRowCount(
                                OptionalInt.of(3)
                        )
        );

        this.loadCellStoreAndCheck(cellStore, a1Formatted, b2Formatted, c3Formatted);
        this.loadLabelStoreAndCheck(labelStore);
        this.countAndCheck(cellReferenceStore, 0);

        this.loadReferencesAndCheck(cellReferenceStore, a1.reference()); // references to A1 -> none
        this.findReferencesWithCellAndCheck(cellReferenceStore, a1.reference()); // references parse A1 -> none

        this.loadReferencesAndCheck(cellReferenceStore, b2.reference()); // references to B2 -> none
        this.findReferencesWithCellAndCheck(cellReferenceStore, b2.reference()); // references parse B2 -> none

        this.loadReferencesAndCheck(cellReferenceStore, c3.reference()); // references to C3 -> none
        this.findReferencesWithCellAndCheck(cellReferenceStore, c3.reference()); // references parse C3 -> none
    }

    @Test
    public void testSaveCellWithFormulaLabelCycleToSelf() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelMapping mapping = LABEL.setLabelMappingReference(SpreadsheetSelection.A1);

        engine.saveLabel(
                mapping,
                context
        );

        final SpreadsheetCell a1 = this.cell(
                "A1",
                "=" + LABEL
        );
        this.saveCellAndCheck(
                engine,
                a1,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                a1,
                                                SpreadsheetError.cycle(
                                                        a1.reference()
                                                )
                                        )
                                )
                        ).setReferences(references("A1=Label123")
                        ).setLabels(
                                Sets.of(mapping)
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        );
    }

    @Test
    public void testSaveCellWithLabelToSelf() {
        final SpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelMapping mapping = LABEL.setLabelMappingReference(SpreadsheetSelection.A1);

        engine.saveLabel(
                mapping,
                context
        );

        final SpreadsheetCell a1 = this.cell(
                "A1",
                "=LABEL123"
        );

        this.saveCellsAndCheck(
                engine,
                Sets.of(a1),
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                a1,
                                                SpreadsheetError.cycle(
                                                        a1.reference()
                                                )
                                        )
                                )
                        ).setReferences(references("A1=LABEL123")
                        ).setLabels(
                                Sets.of(mapping)
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        );
    }

    @Test
    public void testSaveCellWithLabelReference() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelName unknown = SpreadsheetSelection.labelName("LABELXYZ");

        final SpreadsheetCell a1 = this.cell("a1", "=1+" + unknown);
        final SpreadsheetCell a1Formatted = this.formatCell(
                a1,
                SpreadsheetError.selectionNotFound(unknown)
        );
        this.saveCellAndCheck(
                engine,
                a1,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        a1Formatted
                                )
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        );

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetCellReferencesStore cellReferenceStore = repository.cellReferences();
        final SpreadsheetExpressionReferenceStore<SpreadsheetLabelName> labelReferencesStore = repository.labelReferences();

        this.loadCellStoreAndCheck(cellStore, a1Formatted);
        this.loadLabelStoreAndCheck(labelStore);
        this.countAndCheck(cellReferenceStore, 0);

        this.loadReferencesAndCheck(labelReferencesStore, unknown, a1.reference());
    }

    @Test
    public void testSaveCellWithTwiceLaterCellReferencesPrevious() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell(
                "$A$1",
                "=1+2"
        );
        engine.saveCell(
                a1,
                context
        );

        final SpreadsheetCellReference a1Reference = SpreadsheetSelection.parseCell("$A$1");
        final SpreadsheetCell b2 = this.cell(
                "$B$2",
                "=5+" + a1Reference
        );

        this.saveCellAndCheck(
                engine,
                b2,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                b2,
                                                5 + 3
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("B")
                        ).setRowHeights(
                                rowHeights("2")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(2)
                        )
        );

        final SpreadsheetCellReferencesStore cellReferenceStore = context.storeRepository()
                .cellReferences();

        this.loadReferencesAndCheck(
                cellReferenceStore,
                a1.reference(),
                b2.reference()
        );
        this.findReferencesWithCellAndCheck(
                cellReferenceStore,
                a1.reference()
        );

        this.loadReferencesAndCheck(
                cellReferenceStore,
                b2.reference()
        );
        this.findReferencesWithCellAndCheck(
                cellReferenceStore,
                b2.reference(),
                a1Reference
        );
    }

    @Test
    public void testSaveCellWithTwiceLaterCellReferencesPrevious2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell(
                "$A$1",
                "=1+C3"
        );
        engine.saveCell(
                a1,
                context
        );

        final SpreadsheetCell b2 = this.cell(
                "$B$2",
                "=5+A1"
        );
        engine.saveCell(
                b2,
                context
        );

        final SpreadsheetCell c3 = this.cell(
                "$C$3",
                "=10"
        );

        this.saveCellAndCheck(
                engine,
                c3,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                a1,
                                                1 + 10
                                        ),
                                        this.formatCell(
                                                b2,
                                                5 + 1 + 10
                                        ),
                                        this.formatCell(
                                                c3,
                                                10
                                        )
                                )
                        ).setReferences(references("A1=B2;C3=A1")
                        ).setColumnWidths(
                                columnWidths("A,B,C")
                        ).setRowHeights(
                                rowHeights("1,2,3")
                        ).setColumnCount(
                                OptionalInt.of(3)
                        ).setRowCount(
                                OptionalInt.of(3)
                        )
        );
    }

    @Test
    public void testSaveCellWithTwiceLaterReferencesPreviousAgain() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell(
                "$A$1",
                "=1+2"
        );
        engine.saveCell(
                a1,
                context
        );

        final SpreadsheetCell b2 = this.cell(
                "$B$2",
                "=5+$A$1"
        );
        final SpreadsheetCell b2Formatted = this.formatCell(
                b2,
                5 + 1 + 2
        );

        this.saveCellAndCheck(
                engine,
                b2,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        b2Formatted
                                )
                        ).setColumnWidths(
                                columnWidths("B")
                        ).setRowHeights(
                                rowHeights("2")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(2)
                        )
        );

        this.saveCellAndCheck(
                engine,
                b2Formatted,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        b2Formatted
                                )
                        ).setColumnWidths(
                                columnWidths("B")
                        ).setRowHeights(
                                rowHeights("2")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(2)
                        )
        );
    }

    @Test
    public void testSaveCellWithIndirectCycle() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell(
                "a1",
                "=b2"
        );

        this.saveCellAndCheck(
                engine,
                a1,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                a1,
                                                0
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        );

        final SpreadsheetCell b2 = this.cell(
                "b2",
                "=a1"
        );

        final SpreadsheetCell a1Formatted = this.formatCell(
                a1,
                SpreadsheetError.cycle(
                        b2.reference()
                )
        );

        final SpreadsheetCell b2Formatted = this.formatCell(
                b2,
                SpreadsheetError.cycle(
                        b2.reference()
                )
        );

        this.saveCellAndCheck(
                engine,
                b2,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        a1Formatted,
                                        b2Formatted
                                )
                        ).setReferences(references("A1=B2;B2=A1")
                        ).setColumnWidths(
                                columnWidths("A,B")
                        ).setRowHeights(
                                rowHeights("1,2")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(2)
                        )
        );

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetCellReferencesStore cellReferenceStore = repository.cellReferences();

        this.loadCellStoreAndCheck(
                cellStore,
                a1Formatted,
                b2Formatted
        );
        this.loadLabelStoreAndCheck(labelStore);
        this.countAndCheck(
                cellReferenceStore,
                2
        ); // b2 -> a1 & a1 -> b2
    }

    @Test
    public void testSaveCellWithDoubleIndirectCycle() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell(
                "a1",
                "=b2"
        );

        this.saveCellAndCheck(
                engine,
                a1,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                a1,
                                                0
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        );

        final SpreadsheetCell b2 = this.cell(
                "b2",
                "=c3"
        );

        this.saveCellAndCheck(
                engine,
                b2,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                a1,
                                                0
                                        ),
                                        this.formatCell(
                                                b2,
                                                0
                                        )
                                )
                        ).setReferences(references("B2=A1")
                        ).setColumnWidths(
                                columnWidths("A,B")
                        ).setRowHeights(
                                rowHeights("1,2")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(2)
                        )
        );

        final SpreadsheetCell c3 = this.cell(
                "c3",
                "=a1"
        );

        final SpreadsheetCell a1Formatted = this.formatCell(
                a1,
                SpreadsheetError.cycle(
                        c3.reference()
                )
        );

        final SpreadsheetCell b2Formatted = this.formatCell(
                b2,
                SpreadsheetError.cycle(
                        c3.reference()
                )
        );

        final SpreadsheetCell c3Formatted = this.formatCell(
                c3,
                SpreadsheetError.cycle(
                        c3.reference()
                )
        );

        this.saveCellAndCheck(
                engine,
                c3,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        a1Formatted,
                                        b2Formatted,
                                        c3Formatted
                                )
                        ).setReferences(references("A1=C3;B2=A1;C3=B2")
                        ).setColumnWidths(
                                columnWidths("A,B,C")
                        ).setRowHeights(
                                rowHeights("1,2,3")
                        ).setColumnCount(
                                OptionalInt.of(3)
                        ).setRowCount(
                                OptionalInt.of(3)
                        )
        );

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetCellReferencesStore cellReferenceStore = repository.cellReferences();

        this.loadCellStoreAndCheck(
                cellStore,
                a1Formatted,
                b2Formatted,
                c3Formatted
        );
        this.loadLabelStoreAndCheck(labelStore);
        this.countAndCheck(
                cellReferenceStore,
                3
        );
    }

    @Test
    public void testSaveCellWithReferencesUpdated() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference b2Reference = SpreadsheetSelection.parseCell("$B$2");
        final SpreadsheetCell a1 = this.cell(
                "$A$1",
                "=" + b2Reference + "+5"
        );
        engine.saveCell(
                a1,
                context
        );

        final SpreadsheetCell b2 = this.cell(
                "$B$2",
                "=1+2"
        );

        this.saveCellAndCheck(
                engine,
                b2,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                a1,
                                                1 + 2 + 5
                                        ),
                                        this.formatCell(
                                                b2,
                                                1 + 2
                                        )
                                )
                        ).setReferences(references("B2=A1")
                        ).setColumnWidths(
                                columnWidths("A,B")
                        ).setRowHeights(
                                rowHeights("1,2")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(2)
                        )
        );

        final SpreadsheetCellReferencesStore cellReferenceStore = context.storeRepository()
                .cellReferences();

        this.loadReferencesAndCheck(
                cellReferenceStore,
                b2Reference,
                a1.reference()
        );
        this.findReferencesWithCellAndCheck(
                cellReferenceStore,
                a1.reference(),
                b2Reference
        );
        this.loadReferencesAndCheck(
                cellReferenceStore,
                a1.reference()
        );
        this.findReferencesWithCellAndCheck(
                cellReferenceStore,
                b2.reference()
        );
    }

    @Test
    public void testSaveCellWithFormulaLabelReference() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelMapping labelMapping = context.storeRepository()
                .labels()
                .save(
                        SpreadsheetLabelMapping.with(
                                SpreadsheetSelection.labelName("LABELA1"),
                                SpreadsheetSelection.A1)
                );

        final SpreadsheetCell a1 = this.cell(
                "$A$1",
                "=10"
        );
        engine.saveCell(a1, context);

        final SpreadsheetCell b2 = this.cell(
                "$B$2",
                "=5+LABELA1"
        );

        this.saveCellAndCheck(
                engine,
                b2,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                b2,
                                                5 + 10
                                        )
                                )
                        ).setLabels(
                                Sets.of(
                                        labelMapping
                                )
                        ).setColumnWidths(
                                columnWidths("B")
                        ).setRowHeights(
                                rowHeights("2")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(2)
                        )
        );
    }

    @Test
    public void testSaveCellWithFormulaLabelReference2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetExpressionReferenceStore<SpreadsheetLabelName> labelReferencesStore = repository.labelReferences();

        final SpreadsheetCell b2 = this.cell(
                "$B$2",
                "=5"
        );

        final SpreadsheetLabelName labelB2 = SpreadsheetSelection.labelName("LABELB2");
        labelStore.save(
                SpreadsheetLabelMapping.with(
                        labelB2,
                        b2.reference()
                )
        );

        final SpreadsheetCell a1 = this.cell(
                "$A$1",
                "=10+" + labelB2
        );
        engine.saveCell(
                a1,
                context
        );

        final SpreadsheetCellReferencesStore cellReferenceStore = repository.cellReferences();

        this.loadReferencesAndCheck(cellReferenceStore, a1.reference());
        this.findReferencesWithCellAndCheck(cellReferenceStore, a1.reference());

        this.loadReferencesAndCheck(cellReferenceStore, b2.reference());
        this.findReferencesWithCellAndCheck(cellReferenceStore, b2.reference());

        this.loadReferencesAndCheck(labelReferencesStore, labelB2, a1.reference());

        this.saveCellAndCheck(
                engine,
                b2,
                context,
                SpreadsheetDelta.EMPTY.setCells(
                        Sets.of(
                                this.formatCell(
                                        a1,
                                        10 + 5
                                ),
                                this.formatCell(
                                        b2,
                                        5
                                )
                        )
                ).setLabels(
                        Sets.of(
                                labelB2.setLabelMappingReference(b2.reference())
                        )
                ).setReferences(references("B2=LABELB2")
                ).setColumnWidths(
                        columnWidths("A,B")
                ).setRowHeights(
                        rowHeights("1,2")
                ).setColumnCount(
                        OptionalInt.of(2)
                ).setRowCount(
                        OptionalInt.of(2)
                )
        );
    }

    @Test
    public void testSaveCellWithReplacesCellReferences() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell d4 = this.cell(
                "$D$4",
                "=20"
        );
        engine.saveCell(d4, context);

        final SpreadsheetCell e5 = this.cell(
                "$E$5",
                "=30"
        );
        engine.saveCell(e5, context);

        engine.saveCell(
                this.cell(
                        "$A$1",
                        "=10+" + d4.reference()
                ),
                context
        );

        final SpreadsheetCell a1 = this.cell(
                "$A$1",
                "=40+" + e5.reference()
        );

        this.saveCellAndCheck(
                engine,
                a1,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                a1,
                                                40 + 30
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(5)
                        ).setRowCount(
                                OptionalInt.of(5)
                        )
        );

        final SpreadsheetCellReferencesStore cellReferenceStore = context
                .storeRepository()
                .cellReferences();

        this.loadReferencesAndCheck(
                cellReferenceStore,
                a1.reference()
        );
        this.findReferencesWithCellAndCheck(
                cellReferenceStore,
                a1.reference(),
                e5.reference()
        );

        this.loadReferencesAndCheck(
                cellReferenceStore,
                d4.reference()
        );
        this.findReferencesWithCellAndCheck(
                cellReferenceStore,
                d4.reference()
        );
    }

    @Test
    public void testSaveCellWithReplacesLabelReferences() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellReferencesStore cellReferenceStore = repository.cellReferences();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetExpressionReferenceStore<SpreadsheetLabelName> labelReferencesStore = repository.labelReferences();

        final SpreadsheetLabelName labelB2 = SpreadsheetSelection.labelName("LABELB2");
        final SpreadsheetLabelMapping labelMappingLabelB2 = labelStore.save(
                SpreadsheetLabelMapping.with(
                        labelB2,
                        SpreadsheetSelection.parseCell("B2")
                )
        );

        final SpreadsheetLabelName labelD4 = SpreadsheetSelection.labelName("LABELD4");
        final SpreadsheetLabelMapping labelMappingLabelD4 = labelStore.save(
                SpreadsheetLabelMapping.with(
                        labelD4,
                        SpreadsheetSelection.parseCell("D4")
                )
        );

        final SpreadsheetCell d4 = this.cell(
                "$D$4",
                "=20"
        );
        engine.saveCell(
                d4,
                context
        );

        engine.saveCell(
                this.cell(
                        "$A$1",
                        "=10+" + labelB2
                ),
                context
        );

        final SpreadsheetCell a1 = this.cell(
                "$A$1",
                "=40+" + labelD4
        );

        this.saveCellAndCheck(
                engine,
                a1,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                a1,
                                                40 + 20
                                        )
                                )
                        ).setLabels(
                                Sets.of(
                                        labelMappingLabelB2,
                                        labelMappingLabelD4
                                )
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(4)
                        ).setRowCount(
                                OptionalInt.of(4)
                        )
        );

        this.loadReferencesAndCheck(
                cellReferenceStore,
                a1.reference()
        );
        this.findReferencesWithCellAndCheck(
                cellReferenceStore,
                a1.reference()
        );

        this.loadReferencesAndCheck(
                cellReferenceStore,
                d4.reference()
        );
        this.findReferencesWithCellAndCheck(
                cellReferenceStore,
                d4.reference()
        );

        this.loadReferencesAndCheck(
                labelReferencesStore,
                labelB2
        );
        this.loadReferencesAndCheck(
                labelReferencesStore,
                labelD4,
                a1.reference()
        );
    }

    @Test
    public void testSaveCellWithReplacesCellAndLabelReferences() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();

        final SpreadsheetCellReferencesStore cellReferenceStore = repository.cellReferences();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetExpressionReferenceStore<SpreadsheetLabelName> labelReferencesStore = repository.labelReferences();

        final SpreadsheetLabelName labelB2 = SpreadsheetSelection.labelName("LABELB2");
        final SpreadsheetCellReference b2Reference = SpreadsheetSelection.parseCell("B2");
        final SpreadsheetLabelMapping labelMappingB2 = labelStore.save(
                SpreadsheetLabelMapping.with(
                        labelB2,
                        b2Reference
                )
        );

        final SpreadsheetLabelName labelD4 = SpreadsheetSelection.labelName("LABELD4");
        final SpreadsheetLabelMapping labelMappingD4 = labelStore.save(
                SpreadsheetLabelMapping.with(
                        labelD4,
                        SpreadsheetSelection.parseCell("D4")
                )
        );

        final SpreadsheetCell d4 = this.cell(
                "$D$4",
                "=20"
        );
        engine.saveCell(
                d4,
                context
        );

        final SpreadsheetCell e5 = this.cell(
                "$E$5",
                "=30"
        );
        engine.saveCell(
                e5,
                context
        );

        engine.saveCell(
                this.cell(
                        "$A$1",
                        "=10+" + labelB2 + "+C2"
                ),
                context
        );

        final SpreadsheetCellReference e5Reference = SpreadsheetSelection.parseCell("$E$5");
        final SpreadsheetCell a1 = this.cell(
                "$A$1",
                "=40+" + labelD4 + "+" + e5Reference
        );
        this.saveCellAndCheck(
                engine,
                a1,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                a1,
                                                40 + 20 + 30
                                        )
                                )
                        ).setLabels(
                                Sets.of(
                                        labelMappingB2,
                                        labelMappingD4
                                )
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(5)
                        ).setRowCount(
                                OptionalInt.of(5)
                        )
        );

        this.loadReferencesAndCheck(
                cellReferenceStore,
                a1.reference()
        );
        this.findReferencesWithCellAndCheck(
                cellReferenceStore,
                a1.reference(),
                e5Reference
        );

        this.loadReferencesAndCheck(
                cellReferenceStore,
                d4.reference()
        );
        this.findReferencesWithCellAndCheck(
                cellReferenceStore,
                d4.reference()
        );

        this.loadReferencesAndCheck(
                labelReferencesStore,
                labelB2
        );
        this.loadReferencesAndCheck(
                labelReferencesStore,
                labelD4,
                a1.reference()
        );
    }

    // saveCell tests with non expression formula's only value literals.................................................

    @Test
    public void testSaveCellWithFormulaApostropheString() {
        this.saveCellAndLoadAndFormattedCheck(
                "'Hello",
                "Hello"
        );
    }

    @Test
    public void testSaveCellWithFormulaDateLiteral() {
        this.saveCellAndLoadAndFormattedCheck(
                "1999/12/31",
                LocalDate.of(1999, 12, 31)
        );
    }

    @Test
    public void testSaveCellWithFormulaDateTimeLiteral() {
        this.saveCellAndLoadAndFormattedCheck(
                "1999/12/31 12:34",
                LocalDateTime.of(
                        LocalDate.of(1999, 12, 31),
                        LocalTime.of(12, 34)
                )
        );
    }

    @Test
    public void testSaveCellWithFormulaNumberLiteral() {
        this.saveCellAndLoadAndFormattedCheck(
                "123",
                123
        );
    }

    @Test
    public void testSaveCellWithFormulaNumber() {
        this.saveCellAndLoadAndFormattedCheck(
                "=123",
                123
        );
    }

    @Test
    public void testSaveCellWithFormulaNumberMath() {
        this.saveCellAndLoadAndFormattedCheck(
                "=123+456.75",
                123 + 456.75
        );
    }

    @Test
    public void testSaveCellWithFormulaNumberGreaterThan() {
        this.saveCellAndLoadAndFormattedCheck(
                "=123>45",
                true
        );
    }

    @Test
    public void testSaveCellWithFormulaNumberLessThanEquals() {
        this.saveCellAndLoadAndFormattedCheck(
                "=123<=45",
                false
        );
    }

    @Test
    public void testSaveCellWithFormulaStringEqualsSameCase() {
        this.saveCellAndLoadAndFormattedCheck(
                "=\"hello\"=\"hello\"",
                true
        );
    }

    @Test
    public void testSaveCellWithFormulaStringEqualsDifferentCase() {
        this.saveCellAndLoadAndFormattedCheck(
                "=\"hello\"=\"HELLO\"",
                true
        );
    }

    @Test
    public void testSaveCellWithFormulaStringEqualsDifferent() {
        this.saveCellAndLoadAndFormattedCheck(
                "=\"hello\"=\"different\"",
                false
        );
    }

    @Test
    public void testSaveCellWithFormulaStringNotEqualsSameCase() {
        this.saveCellAndLoadAndFormattedCheck(
                "=\"hello\"<>\"hello\"",
                false
        );
    }

    @Test
    public void testSaveCellWithFormulaStringNotEqualsDifferentCase() {
        this.saveCellAndLoadAndFormattedCheck(
                "=\"hello\"<>\"HELLO\"",
                false
        );
    }

    @Test
    public void testSaveCellWithFormulaStringNotEqualsDifferent() {
        this.saveCellAndLoadAndFormattedCheck(
                "=\"hello\"<>\"different\"",
                true
        );
    }

    @Test
    public void testSaveCellWithFormulaTimeLiteral() {
        this.saveCellAndLoadAndFormattedCheck(
                "12:34",
                LocalTime.of(12, 34)
        );
    }

    private void saveCellAndLoadAndFormattedCheck(final String formula,
                                                  final Object value) {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell("a1", formula);
        final SpreadsheetCell a1Formatted = this.formatCell(
                a1,
                value
        );

        final SpreadsheetDelta result = engine.saveCell(a1, context);
        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetDelta expected = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(
                                a1Formatted
                        )
                ).setColumnWidths(
                        columnWidths("A")
                ).setRowHeights(
                        rowHeights("1")
                ).setColumnCount(
                        OptionalInt.of(
                                engine.columnCount(context)
                        )
                ).setRowCount(
                        OptionalInt.of(
                                engine.rowCount(context)
                        )
                );
        this.checkEquals(
                expected,
                result,
                () -> "saveCell " + a1
        );

        this.loadCellStoreAndCheck(
                cellStore,
                a1Formatted
        );
    }

    @Test
    public void testSaveCellWithTwice() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell("a1", "'Hello");
        final SpreadsheetCell a1Formatted = this.formatCell(
                a1,
                "Hello"
        );

        final SpreadsheetDelta saved = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(
                                a1Formatted
                        )
                ).setColumnWidths(
                        columnWidths("A")
                ).setRowHeights(
                        rowHeights("1")
                ).setColumnCount(
                        OptionalInt.of(1)
                ).setRowCount(
                        OptionalInt.of(1)
                );

        this.saveCellAndCheck(
                engine,
                a1,
                context,
                saved
        );

        this.saveCellAndCheck(
                engine,
                a1,
                context,
                saved
        );

        this.loadCellStoreAndCheck(context.storeRepository().cells(), a1Formatted);
    }

    // saveCells........................................................................................................

    @Test
    public void testSaveCellsWithNoCells() {
        this.saveCellsAndCheck(
                this.createSpreadsheetEngine(),
                SpreadsheetDelta.NO_CELLS,
                SpreadsheetEngineContexts.fake(),
                SpreadsheetDelta.EMPTY
        );
    }

    @Test
    public void testSaveCellsWithOnlyValues() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell("a1", "=1+2");
        final SpreadsheetCell a1Formatted = this.formatCell(
                a1,
                3
        );

        final SpreadsheetCell b2 = this.cell("b2", "=4+5");
        final SpreadsheetCell b2Formatted = this.formatCell(
                b2,
                9
        );

        this.saveCellsAndCheck(
                engine,
                Sets.of(
                        a1, b2
                ),
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        a1Formatted,
                                        b2Formatted
                                )
                        ).setColumnWidths(
                                columnWidths("A,B")
                        ).setRowHeights(
                                rowHeights("1,2")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(2)
                        )
        );

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetCellReferencesStore cellReferenceStore = repository.cellReferences();

        this.loadCellStoreAndCheck(
                cellStore,
                a1Formatted,
                b2Formatted
        );
        this.loadLabelStoreAndCheck(labelStore);
        this.countAndCheck(cellReferenceStore, 0);
    }

    @Test
    public void testSaveCellsWithIndirectCycle() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell(
                "a1",
                "=b2"
        );
        final SpreadsheetCell b2 = this.cell(
                "b2",
                "=a1"
        );

        final SpreadsheetCell a1Formatted = this.formatCell(
                a1,
                SpreadsheetError.cycle(
                        b2.reference()
                )
        );

        final SpreadsheetCell b2Formatted = this.formatCell(
                b2,
                SpreadsheetError.cycle(
                        b2.reference()
                )
        );

        this.saveCellsAndCheck(
                engine,
                Sets.of(
                        a1, b2
                ),
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        a1Formatted,
                                        b2Formatted
                                )
                        ).setReferences(references("A1=B2;B2=A1")
                        ).setColumnWidths(
                                columnWidths("A,B")
                        ).setRowHeights(
                                rowHeights("1,2")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(2)
                        )
        );

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetCellReferencesStore cellReferenceStore = repository.cellReferences();

        this.loadCellStoreAndCheck(
                cellStore,
                a1Formatted,
                b2Formatted
        );
        this.loadLabelStoreAndCheck(labelStore);
        this.countAndCheck(
                cellReferenceStore,
                2
        ); // b2 -> a1 & a1 -> b2
    }

    @Test
    public void testSaveCellsWithDoubleIndirectCycle() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell(
                "a1",
                "=b2"
        );
        final SpreadsheetCell b2 = this.cell(
                "b2",
                "=c3"
        );
        final SpreadsheetCell c3 = this.cell(
                "c3",
                "=a1"
        );

        final SpreadsheetCell a1Formatted = this.formatCell(
                a1,
                SpreadsheetError.cycle(
                        c3.reference()
                )
        );

        final SpreadsheetCell b2Formatted = this.formatCell(
                b2,
                SpreadsheetError.cycle(
                        c3.reference()
                )
        );

        final SpreadsheetCell c3Formatted = this.formatCell(
                c3,
                SpreadsheetError.cycle(
                        c3.reference()
                )
        );

        this.saveCellsAndCheck(
                engine,
                Sets.of(
                        a1, b2, c3
                ),
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        a1Formatted,
                                        b2Formatted,
                                        c3Formatted
                                )
                        ).setReferences(references("A1=C3;B2=A1;C3=B2")
                        ).setColumnWidths(
                                columnWidths("A,B,C")
                        ).setRowHeights(
                                rowHeights("1,2,3")
                        ).setColumnCount(
                                OptionalInt.of(3)
                        ).setRowCount(
                                OptionalInt.of(3)
                        )
        );

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetCellReferencesStore cellReferenceStore = repository.cellReferences();

        this.loadCellStoreAndCheck(
                cellStore,
                a1Formatted,
                b2Formatted,
                c3Formatted
        );
        this.loadLabelStoreAndCheck(labelStore);
        this.countAndCheck(
                cellReferenceStore,
                3
        );
    }

    @Test
    public void testSaveCellsWithOnlyWithCrossReferences() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell(
                "a1",
                "=100"
        );
        final SpreadsheetCell a1Formatted = this.formatCell(
                a1,
                100
        );

        final SpreadsheetCell b2 = this.cell(
                "b2",
                "=a1+2"
        );
        final SpreadsheetCell b2Formatted = this.formatCell(
                b2,
                100 + 2
        );

        this.saveCellsAndCheck(
                engine,
                Sets.of(
                        a1, b2
                ),
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        a1Formatted,
                                        b2Formatted
                                )
                        ).setColumnWidths(
                                columnWidths("A,B")
                        ).setRowHeights(
                                rowHeights("1,2")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(2)
                        )
        );

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetCellReferencesStore cellReferenceStore = repository.cellReferences();

        this.loadCellStoreAndCheck(
                cellStore,
                a1Formatted,
                b2Formatted
        );
        this.loadLabelStoreAndCheck(labelStore);
        this.countAndCheck(cellReferenceStore, 1); // b2 -> a1
    }

    @Test
    public void testSaveCellsWithOnlyWithCrossReferences2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell("a1", "=b2+1");
        final SpreadsheetCell a1Formatted = this.formatCell(
                a1,
                1000 + 1
        );

        final SpreadsheetCell b2 = this.cell("b2", "=1000");
        final SpreadsheetCell b2Formatted = this.formatCell(
                b2,
                1000
        );

        this.saveCellsAndCheck(
                engine,
                Sets.of(
                        a1, b2
                ),
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        a1Formatted,
                                        b2Formatted
                                )
                        ).setReferences(references("B2=A1")
                        ).setColumnWidths(
                                columnWidths("A,B")
                        ).setRowHeights(
                                rowHeights("1,2")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(2)
                        )
        );

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetCellReferencesStore cellReferenceStore = repository.cellReferences();

        this.loadCellStoreAndCheck(
                cellStore,
                a1Formatted,
                b2Formatted
        );
        this.loadLabelStoreAndCheck(labelStore);
        this.countAndCheck(cellReferenceStore, 1); // b2 -> a1
    }

    @Test
    public void testSaveCellsWithOnlyWithCrossReferences3() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell(
                "a1",
                "=b2+100"
        );
        final SpreadsheetCell a1Formatted = this.formatCell(
                a1,
                1000 + 100
        );

        final SpreadsheetCell b2 = this.cell(
                "b2",
                "=1000"
        );
        final SpreadsheetCell b2Formatted = this.formatCell(
                b2,
                1000
        );

        final SpreadsheetCell c3 = this.cell(
                "c3",
                "=a1+1"
        );
        final SpreadsheetCell c3Formatted = this.formatCell(
                c3,
                1000 + 100 + 1
        );

        this.saveCellsAndCheck(
                engine,
                Sets.of(
                        a1, b2, c3
                ),
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        a1Formatted,
                                        b2Formatted,
                                        c3Formatted
                                )
                        ).setReferences(references("A1=C3;B2=A1")
                        ).setColumnWidths(
                                columnWidths("A,B,C")
                        ).setRowHeights(
                                rowHeights("1,2,3")
                        ).setColumnCount(
                                OptionalInt.of(3)
                        ).setRowCount(
                                OptionalInt.of(3)
                        )
        );

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetCellReferencesStore cellReferenceStore = repository.cellReferences();

        this.loadCellStoreAndCheck(
                cellStore,
                a1Formatted,
                b2Formatted,
                c3Formatted
        );
        this.loadLabelStoreAndCheck(labelStore);
        this.countAndCheck(
                cellReferenceStore,
                2
        );
    }

    // deleteCell.......................................................................................................

    @Test
    public void testDeleteCellsMatchingCellReference() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;

        engine.saveCell(
                this.cell(
                        a1,
                        "=123"
                ),
                context
        );

        this.deleteCellAndCheck(
                engine,
                a1,
                context,
                SpreadsheetDelta.EMPTY
                        .setDeletedCells(
                                Sets.of(a1)
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(0)
                        ).setRowCount(
                                OptionalInt.of(0)
                        )
        );
    }

    @Test
    public void testDeleteCellsMatchingCellRange() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        engine.saveCell(
                this.cell(
                        a1, "=111"
                ),
                context
        );

        final SpreadsheetCellReference a2 = SpreadsheetSelection.parseCell("A2");
        engine.saveCell(
                this.cell(
                        a2,
                        "=222"
                ),
                context
        );

        this.deleteCellAndCheck(
                engine,
                SpreadsheetSelection.parseCellRange("A1:A2"),
                context,
                SpreadsheetDelta.EMPTY
                        .setDeletedCells(
                                Sets.of(a1, a2)
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1,2")
                        ).setColumnCount(
                                OptionalInt.of(0)
                        ).setRowCount(
                                OptionalInt.of(0)
                        )
        );
    }

    @Test
    public void testDeleteCellsMatchingColumn() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        engine.saveCell(
                this.cell(
                        a1, "=111"
                ),
                context
        );

        final SpreadsheetCellReference a2 = SpreadsheetSelection.parseCell("A2");
        engine.saveCell(
                this.cell(
                        a2,
                        "=222"
                ),
                context
        );

        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("C3");
        engine.saveCell(
                this.cell(
                        c3,
                        "=333"
                ),
                context
        );

        this.deleteCellAndCheck(
                engine,
                SpreadsheetSelection.parseColumn("A"),
                context,
                SpreadsheetDelta.EMPTY
                        .setDeletedCells(
                                Sets.of(a1, a2)
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1,2")
                        ).setColumnCount(
                                OptionalInt.of(3)
                        ).setRowCount(
                                OptionalInt.of(3)
                        )
        );
    }

    @Test
    public void testDeleteCellsWhereCellFormulaWithCellReferences() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("$A$1");
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("$B$2");

        engine.saveCell(
                this.cell(
                        a1,
                        "=99+" + b2
                ),
                context
        );

        this.deleteCellAndCheck(
                engine,
                a1,
                context,
                SpreadsheetDelta.EMPTY
                        .setDeletedCells(
                                Sets.of(a1)
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(0)
                        ).setRowCount(
                                OptionalInt.of(0)
                        )
        );

        final SpreadsheetCellReferencesStore cellReferenceStore = context.storeRepository()
                .cellReferences();

        this.loadReferencesAndCheck(
                cellReferenceStore,
                a1
        );
        this.findReferencesWithCellAndCheck(
                cellReferenceStore,
                a1
        );

        this.loadReferencesAndCheck(
                cellReferenceStore,
                b2
        );
        this.findReferencesWithCellAndCheck(
                cellReferenceStore,
                b2
        );
    }

    @Test
    public void testDeleteCellsWhereCellWithCellExternalReferences() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);
        final SpreadsheetCellReference b2Reference = SpreadsheetSelection.parseCell("$B$2");

        final SpreadsheetCell a1 = this.cell(
                "$A$1",
                "=1+" + b2Reference
        );
        engine.saveCell(
                a1,
                context
        );

        final SpreadsheetCell b2 = this.cell(
                b2Reference,
                "=20"
        );
        engine.saveCell(
                b2,
                context
        );

        this.deleteCellAndCheck(
                engine,
                b2.reference(),
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                a1,
                                                1 // https://github.com/mP1/walkingkooka-spreadsheet/issues/2549
                                        )
                                )
                        ).setReferences(references("B2=A1")
                        ).setDeletedCells(
                                Sets.of(
                                        b2.reference()
                                )
                        ).setColumnWidths(
                                columnWidths("A,B")
                        ).setRowHeights(
                                rowHeights("1,2")
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        );

        final SpreadsheetCellReferencesStore cellReferenceStore = context.storeRepository()
                .cellReferences();

        this.loadReferencesAndCheck(
                cellReferenceStore,
                a1.reference()
        );
        this.findReferencesWithCellAndCheck(
                cellReferenceStore,
                a1.reference(),
                b2Reference
        );

        this.loadReferencesAndCheck(
                cellReferenceStore,
                b2.reference(),
                a1.reference()
        );
        this.findReferencesWithCellAndCheck(
                cellReferenceStore,
                a1.reference(),
                b2Reference
        );
    }

    @Test
    public void testDeleteCellsIncludesColumn() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetColumnStore columnStore = context.storeRepository()
                .columns();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;

        final SpreadsheetColumn a = a1.column()
                .column();

        columnStore.save(a);
        columnStore.save(
                SpreadsheetSelection.parseColumn("B")
                        .column()
        );

        engine.saveCell(this.cell(a1, "=123"), context);

        this.deleteCellAndCheck(
                engine,
                a1,
                context,
                SpreadsheetDelta.EMPTY
                        .setColumns(Sets.of(a))
                        .setDeletedCells(Sets.of(a1))
                        .setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(0)
                        ).setRowCount(
                                OptionalInt.of(0)
                        )
        );
    }

    @Test
    public void testDeleteCellWithLabelReferences() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetCellReferencesStore cellReferenceStore = repository.cellReferences();
        final SpreadsheetExpressionReferenceStore<SpreadsheetLabelName> labelReferencesStore = repository.labelReferences();

        final SpreadsheetLabelName labelB2 = SpreadsheetSelection.labelName("LABELB2");
        final SpreadsheetCell b2 = this.cell(
                "$B$2",
                "=20"
        );
        labelStore.save(
                SpreadsheetLabelMapping.with(
                        labelB2,
                        b2.reference()
                )
        );

        final SpreadsheetCell a1 = this.cell(
                "$A$1",
                "=1+" + labelB2
        );
        engine.saveCell(
                a1,
                context
        );

        engine.saveCell(
                b2,
                context
        );

        this.deleteCellAndCheck(
                engine,
                a1.reference(),
                context,
                SpreadsheetDelta.EMPTY
                        .setDeletedCells(
                                Sets.of(
                                        a1.reference()
                                )
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(2)
                        )
        );

        this.loadReferencesAndCheck(
                cellReferenceStore,
                a1.reference()
        );
        this.findReferencesWithCellAndCheck(
                cellReferenceStore,
                a1.reference()
        );
        this.loadReferencesAndCheck(
                cellReferenceStore,
                b2.reference()
        );
        this.findReferencesWithCellAndCheck(
                cellReferenceStore,
                b2.reference()
        );
        this.loadReferencesAndCheck(
                labelReferencesStore,
                labelB2
        );
    }

    @Test
    public void testDeleteCellWithLabelReferrers() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetCellReferencesStore cellReferenceStore = repository.cellReferences();
        final SpreadsheetExpressionReferenceStore<SpreadsheetLabelName> labelReferencesStore = repository.labelReferences();

        final SpreadsheetLabelName labelB2 = SpreadsheetSelection.labelName("LABELB2");
        final SpreadsheetCell b2 = this.cell("$B$2", "=20");
        final SpreadsheetLabelMapping labelMapping = SpreadsheetLabelMapping.with(
                labelB2,
                b2.reference()
        );
        labelStore.save(labelMapping);

        final SpreadsheetCell a1 = this.cell("$A$1", "=1+" + labelB2);
        engine.saveCell(a1, context);

        engine.saveCell(b2, context);

        this.deleteCellAndCheck(
                engine,
                b2.reference(),
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                a1,
                                                SpreadsheetError.selectionNotFound(labelB2)
                                        )
                                )
                        ).setDeletedCells(
                                Sets.of(
                                        b2.reference()
                                )
                        ).setLabels(
                                Sets.of(labelMapping)
                        ).setReferences(references("B2=LABELB2")
                        ).setColumnWidths(
                                columnWidths("A,B")
                        ).setRowHeights(
                                rowHeights("1,2")
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        );

        this.loadReferencesAndCheck(cellReferenceStore, a1.reference());
        this.findReferencesWithCellAndCheck(cellReferenceStore, a1.reference());

        this.loadReferencesAndCheck(cellReferenceStore, b2.reference());
        this.findReferencesWithCellAndCheck(cellReferenceStore, b2.reference());

        this.loadReferencesAndCheck(labelReferencesStore, labelB2, a1.reference());
    }

    @Test
    public void testDeleteCellWithRow() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetRowStore rowStore = context.storeRepository()
                .rows();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;

        final SpreadsheetRow a = a1.row()
                .row();

        rowStore.save(a);
        rowStore.save(
                SpreadsheetSelection.parseRow("2")
                        .row()
        );

        engine.saveCell(this.cell(a1, "=123"), context);

        this.deleteCellAndCheck(
                engine,
                a1,
                context,
                SpreadsheetDelta.EMPTY
                        .setRows(
                                Sets.of(a)
                        ).setDeletedCells(
                                Sets.of(a1)
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(0)
                        ).setRowCount(
                                OptionalInt.of(0)
                        )
        );
    }

    // loadColumn......................................................................................................

    @Test
    public void testLoadColumnMissingColumn() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        this.checkEquals(
                SpreadsheetDelta.EMPTY,
                engine.loadColumn(
                        SpreadsheetSelection.parseColumn("Z"),
                        context
                )
        );
    }

    @Test
    public void testLoadColumn() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetColumnReference columnReference = SpreadsheetSelection.parseColumn("Z");
        final SpreadsheetColumn column = columnReference.column()
                .setHidden(true);

        context.storeRepository()
                .columns()
                .save(column);

        this.checkEquals(
                SpreadsheetDelta.EMPTY
                        .setColumns(
                                Sets.of(column)
                        ),
                engine.loadColumn(
                        columnReference,
                        context
                )
        );
    }

    // saveColumn......................................................................................................

    @Test
    public void testSaveColumnWithoutCells() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        this.addCellSaveWatcherAndDeleteWatcherThatThrowsUOE(context);

        final SpreadsheetColumnReference reference = SpreadsheetSelection.parseColumn("B");

        engine.saveColumn(
                reference.column(),
                context
        );

        this.countAndCheck(
                context.storeRepository()
                        .columns(),
                1
        );
    }

    @Test
    public void testSaveColumnWithCells() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetColumnReference reference = SpreadsheetSelection.parseColumn("B");

        final SpreadsheetCell cell = this.cell(
                reference.setRow(SpreadsheetSelection.parseRow("2")),
                "=1+2"
        );

        context.storeRepository()
                .cells()
                .save(cell);

        engine.saveColumn(
                reference.column(),
                context
        );

        this.countAndCheck(
                context.storeRepository()
                        .columns(),
                1
        );

        this.loadCellAndCheck(
                engine,
                cell.reference(),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                EXPRESSION_NUMBER_KIND.create(3),
                "3 " + FORMATTED_PATTERN_SUFFIX
        );
    }

    // https://github.com/mP1/walkingkooka-spreadsheet/issues/2022
    @Test
    public void testSaveColumnHiddenTheUnhiddenWithCells() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetColumnReference reference = SpreadsheetSelection.parseColumn("B");

        final SpreadsheetCell cell = this.cell(
                reference.setRow(SpreadsheetSelection.parseRow("2")),
                "=1+2"
        );

        context.storeRepository()
                .cells()
                .save(cell);

        final SpreadsheetColumn column = reference.column();

        engine.saveColumn(
                column.setHidden(true),
                context
        );

        this.countAndCheck(
                context.storeRepository()
                        .columns(),
                1
        );

        // cell B2 in hidden column B should not load.
        this.loadCellFailCheck(
                engine,
                cell.reference(),
                context
        );

        this.checkEquals(
                SpreadsheetDelta.EMPTY
                        .setColumns(
                                Sets.of(
                                        column
                                )
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(2)
                        ),
                engine.saveColumn(
                        column.setHidden(false),
                        context
                )
        );

        this.loadCellAndCheck(
                engine,
                cell.reference(),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                EXPRESSION_NUMBER_KIND.create(3),
                "3 " + FORMATTED_PATTERN_SUFFIX
        );
    }

    // deleteColumn....................................................................................................

    @Test
    public void testDeleteColumnWithZeroNothingDeleted() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("B2");

        engine.saveCell(this.cell(b2, "=99+0"), context);

        this.addCellSaveWatcherAndDeleteWatcherThatThrowsUOE(context);

        this.deleteColumnsAndCheck(
                engine,
                b2.column(),
                0,
                context
        );

        this.countAndCheck(context.storeRepository().cells(), 1);
    }

    @Test
    public void testDeleteColumnWithNoCellsRefreshed() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b = SpreadsheetSelection.parseCell("B2"); // B2
        final SpreadsheetCellReference c = SpreadsheetSelection.parseCell("C3"); // C3

        engine.saveCell(
                this.cell(
                        a,
                        "=1+2"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        b,
                        "=3+4"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        c,
                        "=5+6"
                ),
                context
        );

        this.deleteColumnsAndCheck(
                engine,
                c.column(),
                1,
                context,
                SpreadsheetDelta.EMPTY
                        .setDeletedCells(
                                Sets.of(c)
                        ).setColumnWidths(
                                columnWidths("C")
                        ).setRowHeights(
                                rowHeights("3")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(2)
                        )
        );

        this.countAndCheck(context.storeRepository().cells(), 2);
    }

    @Test
    public void testDeleteColumnWithColumnsAfterCellsRefreshed() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b = SpreadsheetSelection.parseCell("B2"); // B2
        final SpreadsheetCellReference c = SpreadsheetSelection.parseCell("C3"); // C3

        engine.saveCell(
                this.cell(
                        a,
                        "=1+2"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        b,
                        "=3+4"
                ),
                context
        ); // deleted/replaced by $c
        engine.saveCell(
                this.cell(
                        c,
                        "=5+6"
                ),
                context
        ); // becomes b3

        this.deleteColumnsAndCheck(
                engine,
                b.column(),
                1,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "b3",
                                                "=5+6",
                                                5 + 6
                                        )
                                )
                        ).setDeletedCells(
                                Sets.of(b, c)
                        ).setColumnWidths(
                                columnWidths("B,C")
                        ).setRowHeights(
                                rowHeights("2,3")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(3)
                        )
        );

        this.countAndCheck(context.storeRepository().cells(), 2);
    }

    @Test
    public void testDeleteColumnWithColumnsAfterCellsRefreshedAddition() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck(
                "=5+6",
                5 + 6
        );
    }

    @Test
    public void testDeleteColumnWithColumnsAfterCellsRefreshedExpressionNumber() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck(
                "=55.5",
                55.5
        );
    }

    @Test
    public void testDeleteColumnWithColumnsAfterCellsRefreshedExpressionNumber2() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck(
                "=55",
                55
        );
    }

    @Test
    public void testDeleteColumnWithColumnsAfterCellsRefreshedDivision() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck(
                "=9/3",
                9 / 3
        );
    }

    @Test
    public void testDeleteColumnWithColumnsAfterCellsRefreshedEqualsTrue() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck(
                "=8=8",
                true
        );
    }

    @Test
    public void testDeleteColumnWithColumnsAfterCellsRefreshedEqualsFalse() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck(
                "=8=7",
                false
        );
    }

    @Test
    public void testDeleteColumnWithColumnsAfterCellsRefreshedFunction() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck(
                "=BasicSpreadsheetEngineTestSum(1,99)",
                1 + 99
        );
    }

    @Test
    public void testDeleteColumnWithColumnsAfterCellsRefreshedGreaterThanTrue() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck(
                "=8>7",
                true
        );
    }

    @Test
    public void testDeleteColumnWithColumnsAfterCellsRefreshedGreaterThanFalse() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck(
                "=7>8",
                false
        );
    }

    @Test
    public void testDeleteColumnWithColumnsAfterCellsRefreshedGreaterThanEqualsTrue() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck(
                "=8>=7",
                true
        );
    }

    @Test
    public void testDeleteColumnWithColumnsAfterCellsRefreshedGreaterThanEqualsFalse() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck(
                "=7>=8",
                false
        );
    }

    @Test
    public void testDeleteColumnWithColumnsAfterCellsRefreshedGroup() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck(
                "=(99)",
                99
        );
    }

    @Test
    public void testDeleteColumnWithColumnsAfterCellsRefreshedLessThanTrue() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck(
                "=8<9",
                true
        );
    }

    @Test
    public void testDeleteColumnWithColumnsAfterCellsRefreshedLessThanFalse() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck(
                "=7<6",
                false
        );
    }

    @Test
    public void testDeleteColumnWithColumnsAfterCellsRefreshedLessThanEqualsTrue() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck(
                "=8<=8",
                true
        );
    }

    @Test
    public void testDeleteColumnWithColumnsAfterCellsRefreshedLessThanEqualsFalse() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck(
                "=8<=7",
                false
        );
    }

    @Test
    public void testDeleteColumnWithColumnsAfterCellsRefreshedMultiplication() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck(
                "=9*3",
                9 * 3
        );
    }

    @Test
    public void testDeleteColumnWithColumnsAfterCellsRefreshedNegative() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck(
                "=-99",
                -99
        );
    }

    @Test
    public void testDeleteColumnWithColumnsAfterCellsRefreshedNotEqualsTrue() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck(
                "=8<>7",
                true
        );
    }

    @Test
    public void testDeleteColumnWithColumnsAfterCellsRefreshedNotEqualsFalse() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck(
                "=8<>8",
                false
        );
    }

    @Test
    public void testDeleteColumnWithColumnsAfterCellsRefreshedPercentage() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck(
                "=120%",
                1.2
        );
    }

    @Test
    public void testDeleteColumnWithColumnsAfterCellsRefreshedSubtraction() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck(
                "=9-7",
                9 - 7
        );
    }

    @Test
    public void testDeleteColumnWithColumnsAfterCellsRefreshedText() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck(
                "=\"ABC123\"",
                "ABC123"
        );
    }

    @Test
    public void testDeleteColumnWithColumnsAfterCellsRefreshedAdditionWithWhitespace() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck(
                "=1 + 2",
                1 + 2
        );
    }

    private void deleteColumnColumnsAfterCellsRefreshedAndCheck(final String formula,
                                                                final Object value) {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("B2"); // B2
        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("C3"); // C3

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+2"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        b2,
                        "=3+4"
                ),
                context
        ); // deleted/replaced by $c
        engine.saveCell(
                this.cell(
                        c3,
                        formula
                ),
                context
        ); // becomes b3

        this.deleteColumnsAndCheck(
                engine,
                b2.column(),
                1,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell("b3", formula, value)
                                )
                        ).setDeletedCells(
                                Sets.of(b2, c3)
                        ).setColumnWidths(
                                columnWidths("B,C")
                        ).setRowHeights(
                                rowHeights("2,3")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(3)
                        )
        );

        this.countAndCheck(context.storeRepository().cells(), 2);
    }

    @Test
    public void testDeleteColumnWithColumnsAfterCellsRefreshed2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("B2"); //replaced by $c$3
        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("C3");
        final SpreadsheetCellReference z99 = SpreadsheetSelection.parseCell("Z99");

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+2"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        b2,
                        "=3+4"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        c3,
                        "=5+6"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        z99,
                        "=7+8"
                ),
                context
        );

        final int count = 1;
        this.deleteColumnsAndCheck(
                engine,
                b2.column(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "B3",
                                                "=5+6",
                                                5 + 6
                                        ),
                                        this.formatCell(
                                                "Y99",
                                                "=7+8",
                                                7 + 8
                                        )
                                )
                        ).setDeletedCells(
                                Sets.of(b2, c3, z99)
                        ).setColumnWidths(
                                columnWidths("B,C,Y,Z")
                        ).setRowHeights(
                                rowHeights("2,3,99")
                        ).setColumnCount(
                                OptionalInt.of(25)
                        ).setRowCount(
                                OptionalInt.of(99)
                        )
        );

        this.countAndCheck(context.storeRepository().cells(), 3);
    }

    @Test
    public void testDeleteColumnWithLabelsToCellReference() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference e2 = SpreadsheetSelection.parseCell("E2");

        engine.saveCell(
                this.cell(
                        a1,
                        "=99+0"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        e2,
                        "=2+0+" + LABEL
                ),
                context
        );

        final SpreadsheetLabelMapping mapping = labelStore.save(
                SpreadsheetLabelMapping.with(
                        LABEL,
                        a1
                )
        );

        final int count = 1;
        this.deleteColumnsAndCheck(
                engine,
                e2.column()
                        .add(-1),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                e2.addColumn(-1),
                                                "=2+0+" + LABEL,
                                                2 + 99
                                        )
                                )
                        ).setLabels(
                                Sets.of(mapping)
                        ).setDeletedCells(
                                Sets.of(e2)
                        ).setColumnWidths(
                                columnWidths("D,E")
                        ).setRowHeights(
                                rowHeights("2")
                        ).setColumnCount(
                                OptionalInt.of(4)
                        ).setRowCount(
                                OptionalInt.of(2)
                        )
        );

        this.countAndCheck(cellStore, 2);

        this.loadLabelAndCheck(labelStore, LABEL, a1);
    }

    @Test
    public void testDeleteColumnWithLabelsToCellReferencedFixed() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1.toAbsolute();
        final SpreadsheetCellReference b1 = SpreadsheetSelection.parseCell("$B$1"); // replaced by C1
        final SpreadsheetCellReference c1 = SpreadsheetSelection.parseCell("$C$1"); // A3 DELETED
        final SpreadsheetCellReference n9 = SpreadsheetSelection.parseCell("$N$9");
        final SpreadsheetCellReference o10 = SpreadsheetSelection.parseCell("$O$10");

        engine.saveCell(this.cell(a1, "=1+" + LABEL), context);
        engine.saveCell(this.cell(b1, "=2+0"), context);
        engine.saveCell(this.cell(c1, "=3+0"), context);
        engine.saveCell(this.cell(n9, "=4+" + LABEL), context);
        engine.saveCell(this.cell(o10, "=99+0"), context); // LABEL=

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, o10));

        final int count = 1;
        this.deleteColumnsAndCheck(
                engine,
                b1.column(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                a1,
                                                "=1+" + LABEL,
                                                1 + 99 + 0
                                        ),
                                        this.formatCell(
                                                c1.addColumn(-count),
                                                "=3+0",
                                                3 + 0
                                        ),
                                        this.formatCell(
                                                n9.addColumn(-count),
                                                "=4+" + LABEL,
                                                4 + 99 + 0
                                        ),
                                        this.formatCell(
                                                o10.addColumn(-count),
                                                "=99+0",
                                                99 + 0
                                        )
                                )
                        ).setLabels(
                                Sets.of(
                                        LABEL.setLabelMappingReference(SpreadsheetSelection.parseCell("$N$10"))
                                )
                        ).setReferences(references("N10=Label123")
                        ).setDeletedCells(
                                Sets.of(c1, n9, o10)
                        ).setColumnWidths(
                                columnWidths("A,B,C,M,N,O")
                        ).setRowHeights(
                                rowHeights("1,9,10")
                        ).setColumnCount(
                                OptionalInt.of(14)
                        ).setRowCount(
                                OptionalInt.of(10)
                        )
        ); // old $b delete, $c,$d columns -1.

        this.loadLabelAndCheck(labelStore, LABEL, o10.addColumn(-count));

        this.countAndCheck(cellStore, 4);
    }

    @Test
    public void testDeleteColumnWithLabelToDeletedCell() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a = SpreadsheetSelection.A1.toAbsolute();
        final SpreadsheetCellReference b = SpreadsheetSelection.parseCell("$B$1");

        engine.saveCell(
                this.cell(
                        a,
                        "=1+0"
                ),
                context
        );
        labelStore.save(
                SpreadsheetLabelMapping.with(LABEL, b)
        );

        final int count = 1;
        this.deleteColumnsAndCheck(
                engine,
                b.column(),
                count,
                context,
                SpreadsheetDelta.EMPTY.setDeletedLabels(
                        Sets.of(LABEL)
                ).setColumnCount(
                        OptionalInt.of(1)
                ).setRowCount(
                        OptionalInt.of(1)
                )
        ); // $b delete, $c columns -1.

        this.loadLabelFailCheck(
                labelStore,
                LABEL
        );

        this.countAndCheck(
                cellStore,
                1
        );
    }

    @Test
    public void testDeleteColumnWithCellReferences() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1.toAbsolute();
        final SpreadsheetCellReference b1 = SpreadsheetSelection.parseCell("$B$1");
        final SpreadsheetCellReference k1 = SpreadsheetSelection.parseCell("$K$1"); // A10 deleted
        final SpreadsheetCellReference n9 = SpreadsheetSelection.parseCell("$N$9"); // H13 moved
        final SpreadsheetCellReference o10 = SpreadsheetSelection.parseCell("$O$10"); // I14 moved

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+" + n9
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        b1,
                        "=2"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        k1,
                        "=3"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        n9,
                        "=4"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        o10,
                        "=5+" + b1
                ),
                context
        ); // =5+2

        final int count = 1;
        this.deleteColumnsAndCheck(
                engine,
                k1.column(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                a1,
                                                "=1+" + n9.addColumn(-count),
                                                1 + 4
                                        ),
                                        this.formatCell(
                                                n9.addColumn(-count),
                                                "=4",
                                                4
                                        ),
                                        this.formatCell(
                                                o10.addColumn(-count),
                                                "=5+" + b1,
                                                5 + 2
                                        )
                                )
                        ).setReferences(references("M9=A1")
                        ).setDeletedCells(
                                Sets.of(k1, n9, o10)
                        ).setColumnWidths(
                                columnWidths("A,K,M,N,O")
                        ).setRowHeights(
                                rowHeights("1,9,10")
                        ).setColumnCount(
                                OptionalInt.of(14)
                        ).setRowCount(
                                OptionalInt.of(10)
                        )
        ); // $c delete

        this.countAndCheck(context.storeRepository().cells(), 4);
    }

    @Test
    public void testDeleteColumnWithCellReferences2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1.toAbsolute();
        final SpreadsheetCellReference b1 = SpreadsheetSelection.parseCell("$B$1");
        final SpreadsheetCellReference k1 = SpreadsheetSelection.parseCell("$K$1"); // A10 deleted
        final SpreadsheetCellReference n9 = SpreadsheetSelection.parseCell("$N$9"); // H13 moved
        final SpreadsheetCellReference o10 = SpreadsheetSelection.parseCell("$O$10"); // I14 moved

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+" + n9
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        b1,
                        "=2"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        k1,
                        "=3"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        n9,
                        "=4"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        o10,
                        "=5+" + b1
                ),
                context
        ); // =5+2

        final int count = 2;
        this.deleteColumnsAndCheck(
                engine,
                k1.column(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                a1,
                                                "=1+" + n9.addColumn(-count),
                                                1 + 4
                                        ),
                                        this.formatCell(
                                                n9.addColumn(-count),
                                                "=4",
                                                4
                                        ),
                                        this.formatCell(
                                                o10.addColumn(-count),
                                                "=5+" + b1,
                                                5 + 2
                                        )
                                )
                        ).setReferences(references("L9=A1")
                        ).setDeletedCells(
                                Sets.of(k1, n9, o10)
                        ).setColumnWidths(
                                columnWidths("A,K,L,M,N,O")
                        ).setRowHeights(
                                rowHeights("1,9,10")
                        ).setColumnCount(
                                OptionalInt.of(13)
                        ).setRowCount(
                                OptionalInt.of(10)
                        )
        ); // $c deleted, old-d & old-e refreshed

        this.countAndCheck(
                context.storeRepository()
                        .cells(),
                4
        );
    }

    @Test
    public void testDeleteColumnWithCellReferencesToDeletedCell() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1.toAbsolute();
        final SpreadsheetCellReference b1 = SpreadsheetSelection.parseCell("$B$1");

        engine.saveCell(this.cell(a1, "=1+" + b1), context);
        engine.saveCell(this.cell(b1, "=2"), context);

        final int count = 1;
        this.deleteColumnsAndCheck(
                engine,
                b1.column(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                a1,
                                                "=1+#REF!",
                                                SpreadsheetError.selectionDeleted()
                                        )
                                )
                        ).setDeletedCells(
                                Sets.of(b1)
                        ).setColumnWidths(
                                columnWidths("A,B")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        ); // $b delete

        this.countAndCheck(
                context.storeRepository()
                        .cells(),
                1
        );
    }

    @Test
    public void testDeleteColumnWithSeveral() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1.toAbsolute();
        final SpreadsheetCellReference k1 = SpreadsheetSelection.parseCell("$K$1"); // DELETED
        final SpreadsheetCellReference l1 = SpreadsheetSelection.parseCell("$L$1"); // DELETED
        final SpreadsheetCellReference m3 = SpreadsheetSelection.parseCell("$M$3");
        final SpreadsheetCellReference u4 = SpreadsheetSelection.parseCell("$U$4");
        final SpreadsheetCellReference v5 = SpreadsheetSelection.parseCell("$V$5");

        engine.saveCell(
                this.cell(
                        a1,
                        "=1"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        k1,
                        "=2"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        l1,
                        "=3"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        m3,
                        "=4"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        u4,
                        "=5"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        v5,
                        "=6"
                ),
                context
        );

        final int count = 5;
        this.deleteColumnsAndCheck(
                engine,
                SpreadsheetReferenceKind.ABSOLUTE.column(7),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                m3.addColumn(-count),
                                                "=4",
                                                4
                                        ),
                                        this.formatCell(
                                                u4.addColumn(-count),
                                                "=5",
                                                5
                                        ),
                                        this.formatCell(
                                                v5.addColumn(-count),
                                                "=6",
                                                6
                                        )
                                )
                        ).setDeletedCells(
                                Sets.of(k1, l1, m3, u4, v5)
                        ).setColumnWidths(
                                columnWidths("H,K,L,M,P,Q,U,V")
                        ).setRowHeights(
                                rowHeights("1,3,4,5")
                        ).setColumnCount(
                                OptionalInt.of(17)
                        ).setRowCount(
                                OptionalInt.of(5)
                        )
        ); // $k1 & $c

        this.countAndCheck(
                context.storeRepository()
                        .cells(),
                4
        );
    }

    // loadRow..........................................................................................................

    @Test
    public void testLoadRowWithMissingRow() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        this.checkEquals(
                SpreadsheetDelta.EMPTY,
                engine.loadRow(
                        SpreadsheetSelection.parseRow("999"),
                        context
                )
        );
    }

    @Test
    public void testLoadRow() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetRowReference row999 = SpreadsheetSelection.parseRow("999");
        final SpreadsheetRow row = row999.row()
                .setHidden(true);

        context.storeRepository()
                .rows()
                .save(row);

        this.checkEquals(
                SpreadsheetDelta.EMPTY
                        .setRows(
                                Sets.of(row)
                        ),
                engine.loadRow(
                        row999,
                        context
                )
        );
    }

    // saveRow..........................................................................................................

    @Test
    public void testSaveRowWithoutCells() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        this.addCellSaveWatcherAndDeleteWatcherThatThrowsUOE(context);

        final SpreadsheetRowReference row2 = SpreadsheetSelection.parseRow("2");

        engine.saveRow(
                row2.row(),
                context
        );

        this.countAndCheck(
                context.storeRepository()
                        .rows(),
                1
        );
    }

    @Test
    public void testSaveRowWithCells() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetRowReference row2 = SpreadsheetSelection.parseRow("2");

        final SpreadsheetCell cell = this.cell(
                row2.setColumn(
                        SpreadsheetSelection.parseColumn("B")
                ),
                "=1+2"
        );

        context.storeRepository()
                .cells()
                .save(cell);

        engine.saveRow(
                row2.row(),
                context
        );

        this.countAndCheck(
                context.storeRepository()
                        .rows(),
                1
        );

        this.loadCellAndCheck(
                engine,
                cell.reference(),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                EXPRESSION_NUMBER_KIND.create(3),
                "3 " + FORMATTED_PATTERN_SUFFIX
        );
    }

    // https://github.com/mP1/walkingkooka-spreadsheet/issues/2023
    @Test
    public void testSaveRowHiddenTheUnhiddenWithCells() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetRowReference row2 = SpreadsheetSelection.parseRow("2");

        final SpreadsheetCell cell = this.cell(
                row2.setColumn(
                        SpreadsheetSelection.parseColumn("B")
                ),
                "=1+2"
        );

        context.storeRepository()
                .cells()
                .save(cell);

        final SpreadsheetRow row = row2.row();

        engine.saveRow(
                row.setHidden(true),
                context
        );

        this.countAndCheck(
                context.storeRepository()
                        .rows(),
                1
        );

        // cell B2 in hidden row B should not load.
        this.loadCellFailCheck(
                engine,
                cell.reference(),
                context
        );

        this.checkEquals(
                SpreadsheetDelta.EMPTY
                        .setRows(
                                Sets.of(
                                        row
                                )
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(2)
                        ),
                engine.saveRow(
                        row.setHidden(false),
                        context
                )
        );

        this.loadCellAndCheck(
                engine,
                cell.reference(),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                EXPRESSION_NUMBER_KIND.create(3),
                "3 " + FORMATTED_PATTERN_SUFFIX
        );
    }

    // deleteRow........................................................................................................

    @Test
    public void testDeleteRowsWithNone() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a2 = SpreadsheetSelection.parseCell("$A$2");

        engine.saveCell(
                this.cell(
                        a2,
                        "=99+0"
                ),
                context
        );

        this.addCellSaveWatcherAndDeleteWatcherThatThrowsUOE(context);

        this.deleteRowsAndCheck(
                engine,
                a2.row(),
                0,
                context);

        this.countAndCheck(
                context.storeRepository()
                        .cells(),
                1
        );
    }

    @Test
    public void testDeleteRowsWithOne() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference a2 = SpreadsheetSelection.parseCell("A2");
        final SpreadsheetCellReference a3 = SpreadsheetSelection.parseCell("A3");

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+2"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        a2,
                        "=3+4"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        a3,
                        "=5+6"
                ),
                context
        );

        this.deleteRowsAndCheck(
                engine,
                a2.row(),
                1,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                a3.addRow(-1),
                                                "=5+6",
                                                5 + 6
                                        )
                                )
                        ).setDeletedCells(
                                Sets.of(a3)
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("2,3")
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(2)
                        )
        );

        this.countAndCheck(
                context.storeRepository()
                        .cells(),
                2
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a3.addRow(-1),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=5+6",
                5 + 6
        );
    }

    @Test
    public void testDeleteRowsWithOne2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1; //
        final SpreadsheetCellReference a2 = SpreadsheetSelection.parseCell("A2"); // replaced by c
        final SpreadsheetCellReference a3 = SpreadsheetSelection.parseCell("A3"); // DELETED
        final SpreadsheetCellReference b10 = SpreadsheetSelection.parseCell("B10"); // moved

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+2"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        a2,
                        "=3+4"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        a3,
                        "=5+6"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        b10,
                        "=7+8"
                ),
                context
        );

        final int count = 1;
        this.deleteRowsAndCheck(
                engine,
                a2.row(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                a3.addRow(-count),
                                                "=5+6",
                                                5 + 6
                                        ),
                                        this.formatCell(
                                                b10.addRow(-count),
                                                "=7+8",
                                                7 + 8
                                        )
                                )
                        ).setDeletedCells(
                                Sets.of(a3, b10)
                        ).setColumnWidths(
                                columnWidths("A,B")
                        ).setRowHeights(
                                rowHeights("2,3,9,10")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(9)
                        )
        ); // $b delete

        this.countAndCheck(
                context.storeRepository()
                        .cells(),
                3
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+2",
                1 + 2
        );
    }

    @Test
    public void testDeleteRowsWithMany() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1.toAbsolute(); //
        final SpreadsheetCellReference a2 = SpreadsheetSelection.parseCell("$A$2");
        final SpreadsheetCellReference a3 = SpreadsheetSelection.parseCell("$A$3");
        // DELETED
        final SpreadsheetCellReference b10 = SpreadsheetSelection.parseCell("$B$10");
        // moved

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+2"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        a2,
                        "=3+4"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        a3,
                        "=5+6"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        b10,
                        "=7+8"
                ),
                context
        );

        final int count = 2;
        this.deleteRowsAndCheck(
                engine,
                a2.row(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                b10.addRow(-count),
                                                "=7+8",
                                                7 + 8
                                        )
                                )
                        ).setDeletedCells(
                                Sets.of(a2, a3, b10)
                        ).setColumnWidths(
                                columnWidths("A,B")
                        ).setRowHeights(
                                rowHeights("2,3,8,10")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(8)
                        )
        );

        this.countAndCheck(
                context.storeRepository()
                        .cells(),
                2
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+2",
                1 + 2
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                b10.addRow(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=7+8",
                7 + 8
        );
    }

    // delete row with labels to cell references........................................................................

    @Test
    public void testDeleteRowsWithLabelsToCellUnmodified() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("$A$1");
        final SpreadsheetCellReference a2 = SpreadsheetSelection.parseCell("$A$2");
        final SpreadsheetCellReference a6 = SpreadsheetSelection.parseCell("$A$6");

        labelStore.save(
                SpreadsheetLabelMapping.with(LABEL, a1)
        );

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+0"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        a2,
                        "=20+0+" + LABEL
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        a6,
                        "=99+0"
                ),
                context
        );

        final int count = 2;
        this.deleteRowsAndCheck(
                engine,
                a2.row().add(count),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "$A$4",
                                                "=99+0",
                                                99 + 0
                                        )
                                )
                        ).setDeletedCells(
                                Sets.of(a6)
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("4,6")
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(4)
                        )
        ); // $c moved, $b unmodified label refs $a also unmodified.

        this.countAndCheck(
                cellStore,
                3
        );

        this.loadLabelAndCheck(
                labelStore, 
                LABEL,
                a1
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0",
                1
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a2,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=20+0+" + LABEL,
                20 + 0 + 1
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a6.addRow(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=99+0",
                99
        );
    }

    @Test
    public void testDeleteRowsWithLabelsToCellFixed() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("$A$1");
        final SpreadsheetCellReference a6 = SpreadsheetSelection.parseCell("$A$6");

        labelStore.save(
                SpreadsheetLabelMapping.with(
                        LABEL,
                        a6
                )
        );

        cellStore.save(
                this.cell(
                        a1,
                        "=1+0+" + LABEL
                )
        );
        cellStore.save(
                this.cell(
                        a6,
                        "=2+0"
                )
        );

        final int count = 2;
        this.deleteRowsAndCheck(
                engine,
                a1.row().add(1),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(Sets.of(
                                        this.formatCell(
                                                a1,
                                                "=1+0+" + LABEL,
                                                1 + 0 + 2 + 0
                                        ),
                                        this.formatCell(
                                                a6.addRow(-count),
                                                "=2+0",
                                                2 + 0
                                        )
                                )
                        ).setLabels(
                                Sets.of(
                                        LABEL.setLabelMappingReference(SpreadsheetSelection.parseCell("$A$4"))
                                )
                        ).setReferences(references("A4=Label123")
                        ).setDeletedCells(
                                Sets.of(a6)
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1,4,6")
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(4)
                        )
        ); // $b moved

        this.countAndCheck(cellStore, 2);

        this.loadLabelAndCheck(labelStore, LABEL, a6.addRow(-count));

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "=1+0+" + LABEL,
                3
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a6.addRow(-count),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "=2+0",
                2
        );
    }

    @Test
    public void testDeleteRowsWithLabelToCellReferenceDeleted() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("$A$1");
        final SpreadsheetCellReference a6 = SpreadsheetSelection.parseCell("$A$6");

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+" + a6
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        a6,
                        "=2+0"
                ),
                context
        );

        this.deleteRowsAndCheck(
                engine,
                a6.row(),
                1,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                a1,
                                                "=1+#REF!",
                                                SpreadsheetError.selectionDeleted()
                                        )
                                )
                        ).setDeletedCells(
                                Sets.of(a6)
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1,6")
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        );

        this.countAndCheck(context.storeRepository().cells(), 1);

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+#REF!",
                SpreadsheetError.selectionDeleted()
        ); // reference should have been fixed.
    }

    @Test
    public void testDeleteRowsWithCellReferencesFixed() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("$A$1"); // A1
        final SpreadsheetCellReference a2 = SpreadsheetSelection.parseCell("$A$2"); // B1
        final SpreadsheetCellReference a11 = SpreadsheetSelection.parseCell("$A$11"); // A10 deleted
        final SpreadsheetCellReference i14 = SpreadsheetSelection.parseCell("$I$14"); // H13 moved
        final SpreadsheetCellReference j15 = SpreadsheetSelection.parseCell("$J$15"); // I14 moved

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+" + i14
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        a2,
                        "=2"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        a11,
                        "=3"
                ),
                context
        ); // DELETED
        engine.saveCell(
                this.cell(
                        i14,
                        "=4"
                ),
                context
        ); // REFRESHED
        engine.saveCell(
                this.cell(
                        j15,
                        "=5+" + a2
                ),
                context
        ); // REFRESHED =5+2

        final int count = 1;
        this.deleteRowsAndCheck(
                engine,
                a11.row(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "$A$1",
                                                "=1+$I$13",
                                                1 + 4
                                        ),
                                        this.formatCell(
                                                "$I$13",
                                                "=4",
                                                4
                                        ),
                                        this.formatCell(
                                                "$J$14",
                                                "=5+$A$2",
                                                5 + 2
                                        )
                                )
                        ).setReferences(references("I13=A1")
                        ).setDeletedCells(
                                Sets.of(a11, i14, j15)
                        ).setColumnWidths(
                                columnWidths("A,I,J")
                        ).setRowHeights(
                                rowHeights("1,11,13,14,15")
                        ).setColumnCount(
                                OptionalInt.of(10)
                        ).setRowCount(
                                OptionalInt.of(14)
                        )
        ); // $a11 delete

        this.countAndCheck(
                context.storeRepository()
                        .cells(),
                4
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+" + i14.addRow(-count),
                1 + 4
        ); // reference should have been fixed.

        this.loadCellAndCheck(
                engine,
                a2,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                2
        );

        this.loadCellAndCheck(
                engine,
                i14.addRow(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                4
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                j15.addRow(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=5+" + a2,
                5 + 2
        );
    }

    @Test
    public void testDeleteRowsWithCellReferencesFixed2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("$A$1");
        final SpreadsheetCellReference a2 = SpreadsheetSelection.parseCell("$A$2");
        final SpreadsheetCellReference a11 = SpreadsheetSelection.parseCell("$A$11");// DELETED
        final SpreadsheetCellReference i14 = SpreadsheetSelection.parseCell("$I$14"); // MOVED
        final SpreadsheetCellReference j15 = SpreadsheetSelection.parseCell("$J$15"); // MOVED

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+" + i14
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        a2,
                        "=2"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        a11,
                        "=3"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        i14,
                        "=4"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        j15,
                        "=5+" + a2
                ),
                context
        ); // =5+2

        final int count = 2;
        this.deleteRowsAndCheck(
                engine,
                a11.row(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "$A$1",
                                                "=1+$I$12",
                                                1 + 4
                                        ),
                                        this.formatCell(
                                                "$I$12",
                                                "=4",
                                                4
                                        ),
                                        this.formatCell(
                                                "$J$13",
                                                "=5+$A$2",
                                                5 + 2
                                        )
                                )
                        ).setReferences(references("I12=A1")
                        ).setDeletedCells(
                                Sets.of(a11, i14, j15)
                        ).setColumnWidths(
                                columnWidths("A,I,J")
                        ).setRowHeights(
                                rowHeights("1,11,12,13,14,15")
                        ).setColumnCount(
                                OptionalInt.of(10)
                        ).setRowCount(
                                OptionalInt.of(13)
                        )
        ); // $c delete

        this.countAndCheck(
                context.storeRepository()
                        .cells(),
                4
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+" + i14.addRow(-count),
                1 + 4
        ); // reference should have been fixed.

        this.loadCellAndCheck(
                engine,
                a2,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                2
        );

        this.loadCellAndCheck(
                engine,
                i14.addRow(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                4
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                j15.addRow(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=5+" + a2,
                5 + 2
        );
    }

    // delete range....................................................................................

    @Test
    public void testDeleteRowsWithLabelsToRangeUnmodified() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1.toAbsolute();
        final SpreadsheetCellReference a6 = SpreadsheetSelection.parseCell("$A$6");
        final SpreadsheetCellReference a11 = SpreadsheetSelection.parseCell("$A$11");
        final SpreadsheetCellReference a16 = SpreadsheetSelection.parseCell("$A$16");

        final SpreadsheetCellRangeReference ab = a1.cellRange(a6);
        labelStore.save(
                SpreadsheetLabelMapping.with(
                        LABEL,
                        ab
                )
        );

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+0"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        a11,
                        "=20+0+BasicSpreadsheetEngineTestSum(" + LABEL + ")"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        a16,
                        "=99+0"
                ),
                context
        ); // DELETED

        final int count = 2;
        this.deleteRowsAndCheck(
                engine,
                a16.row(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setDeletedCells(
                                Sets.of(a16)
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("16")
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(11)
                        )
        );

        this.countAndCheck(
                cellStore,
                2
        ); // a&c
        this.countAndCheck(
                labelStore,
                1
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0",
                1
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a11,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=20+0+BasicSpreadsheetEngineTestSum(" + LABEL + ")",
                21
        );

        this.loadLabelAndCheck(
                labelStore,
                LABEL,
                ab
        );
    }

    @Test
    public void testDeleteRowsWithLabelsToRangeDeleted() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1.toAbsolute();
        final SpreadsheetCellReference a6 = SpreadsheetSelection.parseCell("$A$6");
        final SpreadsheetCellReference a11 = SpreadsheetSelection.parseCell("$A$11");

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+0"
                ),
                context
        );

        final SpreadsheetCellRangeReference bc = a6.cellRange(a11);
        labelStore.save(
                SpreadsheetLabelMapping.with(LABEL, bc)
        );

        final int count = a11.row()
                .value()
                - a6.row()
                .value() +
                1;
        this.deleteRowsAndCheck(
                engine,
                a6.row(),
                count,
                context,
                SpreadsheetDelta.EMPTY.setDeletedLabels(
                                Sets.of(LABEL)
                        ).setColumnCount(OptionalInt.of(1))
                        .setRowCount(OptionalInt.of(1))
        ); // b..c deleted

        this.countAndCheck(
                cellStore,
                1
        ); // a
        this.countAndCheck(
                labelStore,
                0
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0",
                1
        );
    }

    @Test
    public void testDeleteRowsWithLabelsToRangeDeleted2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1.toAbsolute();
        final SpreadsheetCellReference a6 = SpreadsheetSelection.parseCell("$A$6");
        final SpreadsheetCellReference a11 = SpreadsheetSelection.parseCell("$A$11");
        final SpreadsheetCellReference a21 = SpreadsheetSelection.parseCell("$A$21");

        final SpreadsheetCellRangeReference bc = a6.cellRange(a11);
        labelStore.save(
                SpreadsheetLabelMapping.with(
                        LABEL,
                        bc
                )
        );

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+0"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        a21,
                        "=20+0"
                ),
                context
        );

        final int count = a11.row().value() - a6.row().value() + 1;
        this.deleteRowsAndCheck(
                engine,
                a6.row(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                a21.addRow(-count),
                                                "=20+0",
                                                20 + 0
                                        )
                                )
                        ).setDeletedCells(
                                Sets.of(a21)
                        ).setDeletedLabels(
                                Sets.of(LABEL)
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("15,21")
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(15)
                        )
        ); // b..c deleted, d moved

        this.countAndCheck(
                cellStore,
                2
        ); // a&d
        this.countAndCheck(
                labelStore,
                0
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0",
                1
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a21.addRow(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=20+0",
                20
        );
    }

    @Test
    public void testDeleteRowsWithLabelsToRangeDeleted3() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1.toAbsolute();
        final SpreadsheetCellReference a6 = SpreadsheetSelection.parseCell("$A$6");
        final SpreadsheetCellReference a11 = SpreadsheetSelection.parseCell("$A$11");

        final SpreadsheetCellRangeReference a6a11 = a6.cellRange(a11);
        labelStore.save(
                SpreadsheetLabelMapping.with(
                        LABEL,
                        a6a11
                )
        );

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+0+" + LABEL
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        a6,
                        "=20+0"
                ),
                context
        );

        final int count = a11.row().value() - a6.row().value() + 1;
        this.deleteRowsAndCheck(
                engine,
                a6.row(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        formatCell(
                                                a1,
                                                "=1+0+" + LABEL,
                                                SpreadsheetError.selectionNotFound(LABEL)
                                        )
                                )
                        ).setDeletedLabels(
                                Sets.of(LABEL)
                        ).setDeletedCells(
                                Sets.of(a6)
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1,6")
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        ); // b..c deleted

        this.countAndCheck(cellStore, 1); // a
        this.countAndCheck(labelStore, 0);

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0+" + LABEL,
                SpreadsheetError.selectionNotFound(LABEL)
        );
    }

    @Test
    public void testDeleteRowsWithLabelsToRangeFixed() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1.toAbsolute();
        final SpreadsheetCellReference a6 = SpreadsheetSelection.parseCell("$A$6");
        final SpreadsheetCellReference a11 = SpreadsheetSelection.parseCell("$A$11");
        final SpreadsheetCellReference a16 = SpreadsheetSelection.parseCell("$A$16");
        final SpreadsheetCellReference a21 = SpreadsheetSelection.parseCell("$A$21");

        final SpreadsheetCellRangeReference a16a21 = a16.cellRange(a21);
        labelStore.save(
                SpreadsheetLabelMapping.with(
                        LABEL,
                        a16a21
                )
        );

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+0+BasicSpreadsheetEngineTestSum(" + LABEL + ")"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        a16,
                        "=20+0"
                ),
                context
        );

        final int count = a11.row().value() - a6.row().value() + 1;
        this.deleteRowsAndCheck(
                engine,
                a6.row(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                a1,
                                                "=1+0+BasicSpreadsheetEngineTestSum(" + LABEL + ")",
                                                1 + 0 + 20 + 0
                                        ),
                                        this.formatCell(
                                                a16.addRow(-count),
                                                "=20+0",
                                                20 + 0
                                        )
                                )
                        ).setLabels(
                                Sets.of(
                                        LABEL.setLabelMappingReference(
                                                SpreadsheetSelection.parseCellRange("$A$10:$A$15")
                                        )
                                )
                        ).setReferences(references("A10=Label123")
                        ).setDeletedCells(
                                Sets.of(a16)
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1,10,16")
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(10)
                        )
        ); // b..c deleted, d moved

        this.countAndCheck(cellStore, 2); // a&d

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0+BasicSpreadsheetEngineTestSum(" + LABEL + ")",
                1 + 20
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a16.addRow(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=20+0",
                20
        );

        this.countAndCheck(
                labelStore,
                1
        );
        final SpreadsheetCellReference begin = a16.addRow(-count);
        final SpreadsheetCellReference end = a21.addRow(-count);
        this.loadLabelAndCheck(
                labelStore,
                LABEL,
                begin.cellRange(end)
        );
    }

    @SuppressWarnings("unused")
    @Test
    public void testDeleteRowsWithLabelsToRangeFixed2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelStore labelStore = context.storeRepository()
                .labels();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1.toAbsolute();
        final SpreadsheetCellReference a6 = SpreadsheetSelection.parseCell("$A$6");
        final SpreadsheetCellReference a11 = SpreadsheetSelection.parseCell("$A$11");
        final SpreadsheetCellReference a16 = SpreadsheetSelection.parseCell("$A$16");
        final SpreadsheetCellReference a21 = SpreadsheetSelection.parseCell("$A$21");

        final SpreadsheetCellRangeReference a11a21 = a11.cellRange(a21);
        labelStore.save(
                SpreadsheetLabelMapping.with(
                        LABEL,
                        a11a21
                )
        );

        final int count = a21.row().value() - a16.row().value() + 1;
        this.deleteRowsAndCheck(
                engine,
                a16.row(),
                count,
                context,
                SpreadsheetDelta.EMPTY.setLabels(
                                Sets.of(
                                        LABEL.setLabelMappingReference(
                                                SpreadsheetSelection.parseCellRange("$A$11:$A$16")
                                        )
                                )
                        ).setColumnCount(OptionalInt.of(0))
                        .setRowCount(OptionalInt.of(0))
        ); // b..c deleted, d moved

        this.countAndCheck(
                labelStore,
                1
        );
        this.loadLabelAndCheck(
                labelStore,
                LABEL,
                a11.cellRange(a16)
        );
    }

    @Test
    public void testDeleteRowsWithLabelsToRangeFixed3() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelStore labelStore = context.storeRepository()
                .labels();

        final SpreadsheetCellReference a6 = SpreadsheetSelection.parseCell("$A$6");
        final SpreadsheetCellReference a11 = SpreadsheetSelection.parseCell("$A$11");
        final SpreadsheetCellReference a16 = SpreadsheetSelection.parseCell("$A$16");

        final SpreadsheetCellRangeReference a6a16 = a6.cellRange(a16);
        labelStore.save(
                SpreadsheetLabelMapping.with(LABEL, a6a16)
        );

        final int count = 1;
        this.deleteRowsAndCheck(
                engine,
                a11.row(),
                count,
                context,
                SpreadsheetDelta.EMPTY.setLabels(
                                Sets.of(
                                        LABEL.setLabelMappingReference(
                                                SpreadsheetSelection.parseCellRange("$A$6:$A$15")
                                        )
                                )
                        ).setColumnCount(OptionalInt.of(0))
                        .setRowCount(OptionalInt.of(0))
        ); // b..c deleted, d moved

        this.countAndCheck(
                labelStore,
                1
        );

        final SpreadsheetCellReference end = a16.addRow(-count);
        this.loadLabelAndCheck(
                labelStore,
                LABEL,
                a6.cellRange(end)
        );
    }

    @SuppressWarnings("unused")
    @Test
    public void testDeleteRowsWithLabelsToRangeFixed4() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelStore labelStore = context.storeRepository()
                .labels();

        final SpreadsheetCellReference a6 = SpreadsheetSelection.parseCell("$A$6");  // delete
        final SpreadsheetCellReference a11 = SpreadsheetSelection.parseCell("$A$11"); // range delete
        final SpreadsheetCellReference a16 = SpreadsheetSelection.parseCell("$A$16"); // range delete
        final SpreadsheetCellReference a21 = SpreadsheetSelection.parseCell("$A$21"); // range
        final SpreadsheetCellReference a26 = SpreadsheetSelection.parseCell("$A$26"); // range

        final SpreadsheetCellRangeReference a11ToA26 = a11.cellRange(a26);
        labelStore.save(
                SpreadsheetLabelMapping.with(
                        LABEL,
                        a11ToA26
                )
        );

        final int count = a16.row().value() - a6.row().value();
        this.deleteRowsAndCheck(
                engine,
                a6.row(),
                count,
                context,
                SpreadsheetDelta.EMPTY.setLabels(
                                Sets.of(
                                        LABEL.setLabelMappingReference(
                                                SpreadsheetSelection.parseCellRange("$A$6:$A$11")
                                        )
                                )
                        ).setColumnCount(OptionalInt.of(0))
                        .setRowCount(OptionalInt.of(0))
        );

        this.countAndCheck(
                labelStore,
                1
        );

        this.loadLabelAndCheck(
                labelStore,
                LABEL,
                a6.cellRange(a11)
        );
    }

    // deleteColumn.....................................................................................................

    @Test
    public void testDeleteColumnsWithNone() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference b1 = SpreadsheetSelection.parseCell("$B$1");

        engine.saveCell(
                this.cell(
                        b1,
                        "=99+0"
                ),
                context
        );

        this.addCellSaveWatcherAndDeleteWatcherThatThrowsUOE(context);

        engine.deleteColumns(
                b1.column(),
                0,
                context
        );

        this.countAndCheck(
                context.storeRepository()
                        .cells(),
                1
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                b1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=99+0",
                99
        );
    }

    @Test
    public void testDeleteColumnsWithOne() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("$A1");
        final SpreadsheetCellReference b1 = SpreadsheetSelection.parseCell("$B$1");
        final SpreadsheetCellReference c1 = SpreadsheetSelection.parseCell("$C$1");

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+2"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        b1,
                        "=3+4"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        c1,
                        "=5+6"
                ),
                context
        );

        this.deleteColumnsAndCheck(
                engine,
                b1.column(),
                1,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "$B$1",
                                                "=5+6",
                                                5 + 6
                                        )
                                )
                        ).setDeletedCells(
                                Sets.of(c1)
                        ).setColumnWidths(
                                columnWidths("B,C")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        );

        this.countAndCheck(context.storeRepository().cells(), 2);

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+2",
                1 + 2
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                b1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=5+6",
                5 + 6
        );
    }

    @Test
    public void testDeleteColumnsWithOne2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("$A$1"); //
        final SpreadsheetCellReference b1 = SpreadsheetSelection.parseCell("$B$1"); // replaced by c1
        final SpreadsheetCellReference c1 = SpreadsheetSelection.parseCell("$C$1"); // DELETED
        final SpreadsheetCellReference j2 = SpreadsheetSelection.parseCell("$J$2"); // moved

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+2"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        b1,
                        "=3+4"
                ),
                context
        ); // DELETE
        engine.saveCell(
                this.cell(
                        c1,
                        "=5+6"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        j2,
                        "=7+8"
                ),
                context
        );

        final int count = 1;

        this.deleteColumnsAndCheck(
                engine,
                b1.column(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "$B$1",
                                                "=5+6",
                                                5 + 6
                                        ),
                                        this.formatCell(
                                                "$I$2",
                                                "=7+8",
                                                7 + 8
                                        )
                                )
                        ).setDeletedCells(
                                Sets.of(c1, j2)
                        ).setColumnWidths(
                                columnWidths("B,C,I,J")
                        ).setRowHeights(
                                rowHeights("1,2")
                        ).setColumnCount(
                                OptionalInt.of(9)
                        ).setRowCount(
                                OptionalInt.of(2)
                        )
        ); // $b delete

        this.countAndCheck(
                context.storeRepository().cells(),
                3
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+2",
                1 + 2
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                b1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=5+6",
                5 + 6
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                j2.addColumn(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=7+8",
                7 + 8
        );
    }

    @Test
    public void testDeleteColumnsWithMany() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("$A$1"); //
        final SpreadsheetCellReference b1 = SpreadsheetSelection.parseCell("$B$1"); // DELETED
        final SpreadsheetCellReference c1 = SpreadsheetSelection.parseCell("$C$1"); // DELETED
        final SpreadsheetCellReference j2 = SpreadsheetSelection.parseCell("$J$2"); // MOVED

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+2"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        b1,
                        "=3+4"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        c1,
                        "=5+6"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        j2,
                        "=7+8"
                ),
                context
        );

        final int count = 2;
        this.deleteColumnsAndCheck(
                engine,
                b1.column(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "$H$2",
                                                "=7+8",
                                                7 + 8
                                        )
                                )
                        ).setDeletedCells(
                                Sets.of(b1, c1, j2)
                        ).setColumnWidths(
                                columnWidths("B,C,H,J")
                        ).setRowHeights(
                                rowHeights("1,2")
                        ).setColumnCount(
                                OptionalInt.of(8)
                        ).setRowCount(
                                OptionalInt.of(2)
                        )
        ); // $b, $c deleted

        this.countAndCheck(context.storeRepository().cells(), 2);

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+2",
                1 + 2
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                j2.addColumn(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=7+8",
                7 + 8
        );
    }

    // delete column with labels to cell references.....................................................................

    @Test
    public void testDeleteColumnsWithLabelsToCellUnmodified() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("$A$1");
        final SpreadsheetCellReference b1 = SpreadsheetSelection.parseCell("$B$1");
        final SpreadsheetCellReference f1 = SpreadsheetSelection.parseCell("$F$1");

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, a1));

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+0"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        b1,
                        "=20+0+" + LABEL
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        f1,
                        "=99+0"
                ),
                context
        );

        final int count = 2;
        this.deleteColumnsAndCheck(
                engine,
                b1.column().add(2),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "$D$1",
                                                "=99+0",
                                                99 + 0
                                        )
                                )
                        ).setDeletedCells(
                                Sets.of(f1)
                        ).setColumnWidths(
                                columnWidths("D,F")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(4)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        );

        this.countAndCheck(
                cellStore,
                3
        );

        this.loadLabelAndCheck(
                labelStore,
                LABEL,
                a1
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0",
                1
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                b1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=20+0+" + LABEL,
                21
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                f1.addColumn(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=99+0",
                99
        );
    }

    @Test
    public void testDeleteColumnsWithLabelsToCellFixed() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("$A$1");
        final SpreadsheetCellReference e1 = SpreadsheetSelection.parseCell("$E$1");

        labelStore.save(
                SpreadsheetLabelMapping.with(
                        LABEL,
                        e1
                )
        );

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+0+" + LABEL
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        e1,
                        "=2+0"
                ),
                context
        );

        final int count = 2;
        this.deleteColumnsAndCheck(
                engine,
                a1.column().add(1),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "$A$1",
                                                "=1+0+" + LABEL,
                                                1 + 0 + 2 + 0
                                        ),
                                        this.formatCell(
                                                "$C$1",
                                                "=2+0",
                                                2 + 0
                                        )
                                )
                        ).setLabels(
                                Sets.of(
                                        LABEL.setLabelMappingReference(SpreadsheetSelection.parseCell("$C$1"))
                                )
                        ).setReferences(references("C1=Label123")
                        ).setDeletedCells(
                                Sets.of(e1)
                        ).setColumnWidths(
                                columnWidths("A,C,E")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(3)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        ); // $b moved

        this.countAndCheck(
                cellStore,
                2
        );

        this.loadLabelAndCheck(
                labelStore,
                LABEL,
                e1.addColumn(-count)
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0+" + LABEL,
                3
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                e1.addColumn(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=2+0",
                2
        );
    }

    @Test
    public void testDeleteColumnsWithLabelToCellReferenceDeleted() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("$A$1");
        final SpreadsheetCellReference e1 = SpreadsheetSelection.parseCell("$E$1");

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+" + e1
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        e1,
                        "=2+0"
                ),
                context
        );

        this.deleteColumnsAndCheck(
                engine,
                e1.column(),
                1,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                a1,
                                                "=1+#REF!",
                                                SpreadsheetError.selectionDeleted()
                                        )
                                )
                        ).setDeletedCells(
                                Sets.of(e1)
                        ).setColumnWidths(
                                columnWidths("A,E")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        ); // $v delete

        this.countAndCheck(
                context.storeRepository()
                        .cells(),
                1
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+#REF!",
                SpreadsheetError.selectionDeleted()
        ); // reference should have been fixed.
    }

    @Test
    public void testDeleteColumnsWithCellReferencesFixed() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("$A$1");
        final SpreadsheetCellReference b1 = SpreadsheetSelection.parseCell("$B$1");
        final SpreadsheetCellReference k1 = SpreadsheetSelection.parseCell("$K$1"); // DELETED
        final SpreadsheetCellReference n9 = SpreadsheetSelection.parseCell("$N$9"); // MOVED
        final SpreadsheetCellReference o10 = SpreadsheetSelection.parseCell("$O$10"); // MOVED

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+" + n9
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        b1,
                        "=2"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        k1,
                        "=3"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        n9,
                        "=4"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        o10,
                        "=5+" + b1
                ),
                context
        ); // =5+2

        final int count = 1;
        this.deleteColumnsAndCheck(
                engine,
                k1.column(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "$A$1",
                                                "=1+$M$9",
                                                1 + 4
                                        ),
                                        this.formatCell(
                                                "$M$9",
                                                "=4",
                                                4
                                        ),
                                        this.formatCell(
                                                "$N$10",
                                                "=5+" + b1,
                                                5 + 2
                                        )
                                )
                        ).setReferences(references("M9=A1")
                        ).setDeletedCells(
                                Sets.of(k1, n9, o10)
                        ).setColumnWidths(
                                columnWidths("A,K,M,N,O")
                        ).setRowHeights(
                                rowHeights("1,9,10")
                        ).setColumnCount(
                                OptionalInt.of(14)
                        ).setRowCount(
                                OptionalInt.of(10)
                        )
        ); // $c delete

        this.countAndCheck(context.storeRepository().cells(), 4);

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+" + n9.addColumn(-count),

                1 + 4
        ); // reference should have been fixed.

        this.loadCellAndCheck(
                engine,
                b1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                2
        );

        this.loadCellAndCheck(
                engine,
                n9.addColumn(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                4
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                o10.addColumn(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=5+" + b1,
                5 + 2
        );
    }

    @Test
    public void testDeleteColumnsWithCellReferencesFixed2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("$A$1");
        final SpreadsheetCellReference b1 = SpreadsheetSelection.parseCell("$B$1");
        final SpreadsheetCellReference k1 = SpreadsheetSelection.parseCell("$K$1");
        final SpreadsheetCellReference n9 = SpreadsheetSelection.parseCell("$N$9");
        final SpreadsheetCellReference o10 = SpreadsheetSelection.parseCell("$O$10");

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+" + n9
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        b1,
                        "=2"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        k1,
                        "=3"
                ),
                context
        ); // DELETED
        engine.saveCell(
                this.cell(
                        n9,
                        "=4"
                ),
                context
        ); // MOVED
        engine.saveCell(
                this.cell(
                        o10,
                        "=5+" + b1
                ),
                context
        ); // MOVED

        final int count = 2;
        this.deleteColumnsAndCheck(
                engine,
                k1.column(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "$A$1",
                                                "=1+$L$9",
                                                1 + 4
                                        ),
                                        this.formatCell(
                                                "$L$9",
                                                "=4",
                                                4
                                        ),
                                        this.formatCell(
                                                "$M$10",
                                                "=5+$B$1",
                                                5 + 2
                                        )
                                )
                        ).setReferences(references("L9=A1")
                        ).setDeletedCells(
                                Sets.of(k1, n9, o10)
                        ).setColumnWidths(
                                columnWidths("A,K,L,M,N,O")
                        ).setRowHeights(
                                rowHeights("1,9,10")
                        ).setColumnCount(
                                OptionalInt.of(13)
                        ).setRowCount(
                                OptionalInt.of(10)
                        )
        ); // $c delete

        this.countAndCheck(
                context.storeRepository()
                        .cells(),
                4
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+" + n9.addColumn(-count),
                1 + 4
        ); // reference should have been fixed.

        this.loadCellAndCheck(
                engine,
                b1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                2
        );

        this.loadCellAndCheck(
                engine,
                n9.addColumn(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                4
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                o10.addColumn(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=5+" + b1,
                5 + 2
        );
    }

    // delete range.....................................................................................................

    @Test
    public void testDeleteColumnsWithLabelsToRangeUnmodified() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1.toAbsolute();
        final SpreadsheetCellReference f1 = SpreadsheetSelection.parseCell("$F$1");
        final SpreadsheetCellReference k1 = SpreadsheetSelection.parseCell("$K$1");
        final SpreadsheetCellReference p1 = SpreadsheetSelection.parseCell("$P$1");

        final SpreadsheetCellRangeReference ab = a1.cellRange(f1);
        labelStore.save(
                SpreadsheetLabelMapping.with(
                        LABEL,
                        ab
                )
        );

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+0"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        k1,
                        "=20+0+BasicSpreadsheetEngineTestSum(" + LABEL + ")"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        p1,
                        "=99+0"
                ),
                context
        ); // deleted!!!

        final int count = 2;
        this.deleteColumnsAndCheck(
                engine,
                p1.column(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setDeletedCells(
                                Sets.of(p1)
                        ).setColumnWidths(
                                columnWidths("P")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(11)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        ); // $d moved

        this.countAndCheck(
                cellStore,
                2
        ); // a&c
        this.countAndCheck(
                labelStore,
                1
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0",
                1
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                k1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=20+0+BasicSpreadsheetEngineTestSum(" + LABEL + ")",
                21
        );

        this.loadLabelAndCheck(
                labelStore,
                LABEL,
                ab
        );
    }

    @Test
    public void testDeleteColumnsWithLabelsToRangeDeleted() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1.toAbsolute();
        final SpreadsheetCellReference f1 = SpreadsheetSelection.parseCell("$F$1");
        final SpreadsheetCellReference k1 = SpreadsheetSelection.parseCell("$K$1");

        final SpreadsheetCellRangeReference f1k1 = f1.cellRange(k1);
        labelStore.save(
                SpreadsheetLabelMapping.with(LABEL, f1k1)
        );

        engine.saveCell(this.cell(a1, "=1+0"), context);

        final int count = k1.column().value() - f1.column().value() + 1;
        this.deleteColumnsAndCheck(
                engine,
                f1.column(),
                count,
                context,
                SpreadsheetDelta.EMPTY.setDeletedLabels(
                                Sets.of(LABEL)
                        ).setColumnCount(OptionalInt.of(1))
                        .setRowCount(OptionalInt.of(1))
        ); // b..c deleted, d moved

        this.countAndCheck(cellStore, 1); // a
        this.countAndCheck(labelStore, 0);

        this.loadCellAndFormulaAndValueCheck(engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0",
                1
        );
    }

    @Test
    public void testDeleteColumnsWithLabelsToRangeDeleted2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1.toAbsolute();
        final SpreadsheetCellReference f1 = SpreadsheetSelection.parseCell("$F$1");
        final SpreadsheetCellReference k1 = SpreadsheetSelection.parseCell("$K$1");
        final SpreadsheetCellReference u1 = SpreadsheetSelection.parseCell("$U$1");

        final SpreadsheetCellRangeReference bc = f1.cellRange(k1);
        labelStore.save(
                SpreadsheetLabelMapping.with(
                        LABEL,
                        bc
                )
        );

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+0"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        u1,
                        "=20+0"
                ),
                context
        );

        final int count = k1.column().value() - f1.column().value() + 1;
        this.deleteColumnsAndCheck(
                engine,
                f1.column(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                u1.addColumn(-count),
                                                "=20+0",
                                                20 + 0
                                        )
                                )
                        ).setDeletedCells(
                                Sets.of(u1)
                        ).setDeletedLabels(
                                Sets.of(LABEL)
                        ).setColumnWidths(
                                columnWidths("O,U")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(15)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        ); // b..c deleted, d moved

        this.countAndCheck(
                cellStore,
                2
        ); // a&d
        this.countAndCheck(
                labelStore,
                0
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0",
                1
        );
    }

    @Test
    public void testDeleteColumnsWithLabelsToRangeDeleted3() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1.toAbsolute();
        final SpreadsheetCellReference f1 = SpreadsheetSelection.parseCell("$F$1");
        final SpreadsheetCellReference k1 = SpreadsheetSelection.parseCell("$K$1");

        final SpreadsheetCellRangeReference f1k1 = f1.cellRange(k1);
        labelStore.save(
                SpreadsheetLabelMapping.with(
                        LABEL,
                        f1k1
                )
        );

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+0+" + LABEL
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        f1,
                        "=20+0"
                ),
                context
        );

        final int count = k1.column().value() - f1.column().value() + 1;
        this.deleteColumnsAndCheck(
                engine,
                f1.column(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "$A$1",
                                                "=1+0+" + LABEL,
                                                SpreadsheetError.selectionNotFound(LABEL)
                                        )
                                )
                        ).setDeletedCells(
                                Sets.of(f1)
                        ).setDeletedLabels(
                                Sets.of(LABEL)
                        ).setColumnWidths(
                                columnWidths("A,F")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        ); // b..c deleted

        this.countAndCheck(
                cellStore,
                1
        ); // a
        this.countAndCheck(
                labelStore,
                0
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0+" + LABEL,
                SpreadsheetError.selectionNotFound(LABEL)
        );
    }

    @Test
    public void testDeleteColumnsWithLabelsToRangeFixed() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1.toAbsolute();
        final SpreadsheetCellReference f1 = SpreadsheetSelection.parseCell("$F$1");
        final SpreadsheetCellReference k1 = SpreadsheetSelection.parseCell("$K$1");
        final SpreadsheetCellReference p1 = SpreadsheetSelection.parseCell("$P$1");
        final SpreadsheetCellReference u1 = SpreadsheetSelection.parseCell("$U$1");

        final SpreadsheetCellRangeReference p1u1 = p1.cellRange(u1);
        labelStore.save(
                SpreadsheetLabelMapping.with(
                        LABEL,
                        p1u1
                )
        );

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+0+BasicSpreadsheetEngineTestSum(" + LABEL + ")"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        p1,
                        "=20+0"
                ),
                context
        );

        final int count = k1.column().value() - f1.column().value() + 1;

        this.deleteColumnsAndCheck(
                engine,
                f1.column(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                a1,
                                                "=1+0+BasicSpreadsheetEngineTestSum(" + LABEL + ")",
                                                1 + 0 + 20 + 0
                                        ),
                                        this.formatCell(
                                                p1.addColumn(-count),
                                                "=20+0",
                                                20 + 0
                                        )
                                )
                        ).setLabels(
                                Sets.of(
                                        LABEL.setLabelMappingReference(
                                                SpreadsheetSelection.parseCellRange("$J$1:$O$1")
                                        )
                                )
                        ).setReferences(references("J1=Label123")
                        ).setDeletedCells(
                                Sets.of(p1)
                        ).setColumnWidths(
                                columnWidths("A,J,P")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(10)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        ); // b..c deleted, d moved

        this.countAndCheck(
                cellStore,
                2
        ); // a&d

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0+BasicSpreadsheetEngineTestSum(" + LABEL + ")",
                1 + 20
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                p1.addColumn(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=20+0",
                20
        );

        this.countAndCheck(
                labelStore,
                1
        );

        final SpreadsheetCellReference begin = p1.addColumn(-count);
        final SpreadsheetCellReference end = u1.addColumn(-count);
        this.loadLabelAndCheck(
                labelStore,
                LABEL,
                begin.cellRange(end)
        );
    }

    @SuppressWarnings("unused")
    @Test
    public void testDeleteColumnsWithLabelsToRangeFixed2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1.toAbsolute();
        final SpreadsheetCellReference f1 = SpreadsheetSelection.parseCell("$F$1");
        final SpreadsheetCellReference k1 = SpreadsheetSelection.parseCell("$K$1");
        final SpreadsheetCellReference p1 = SpreadsheetSelection.parseCell("$P$1");
        final SpreadsheetCellReference u1 = SpreadsheetSelection.parseCell("$U$1");

        final SpreadsheetCellRangeReference k1u1 = k1.cellRange(u1);
        labelStore.save(
                SpreadsheetLabelMapping.with(
                        LABEL,
                        k1u1
                )
        );

        final int count = u1.column().value() - p1.column().value() + 1;

        this.deleteColumnsAndCheck(
                engine,
                p1.column(),
                count,
                context,
                SpreadsheetDelta.EMPTY.setLabels(
                                Sets.of(
                                        LABEL.setLabelMappingReference(
                                                SpreadsheetSelection.parseCellRange("$K$1:$P$1")
                                        )
                                )
                        ).setColumnCount(OptionalInt.of(0))
                        .setRowCount(OptionalInt.of(0))
        );

        this.countAndCheck(
                labelStore,
                1
        );
        this.loadLabelAndCheck(
                labelStore,
                LABEL,
                k1.cellRange(p1)
        );
    }

    @Test
    public void testDeleteColumnsWithLabelsToRangeFixed3() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelStore labelStore = context.storeRepository()
                .labels();

        final SpreadsheetCellReference f1 = SpreadsheetSelection.parseCell("$F$1");
        final SpreadsheetCellReference k1 = SpreadsheetSelection.parseCell("$K$1");
        final SpreadsheetCellReference p1 = SpreadsheetSelection.parseCell("$P$1");

        final SpreadsheetCellRangeReference bd = f1.cellRange(p1);
        labelStore.save(
                SpreadsheetLabelMapping.with(LABEL, bd)
        );

        final int count = 1;
        this.deleteColumnsAndCheck(
                engine,
                k1.column(),
                count,
                context,
                SpreadsheetDelta.EMPTY.setLabels(
                                Sets.of(
                                        LABEL.setLabelMappingReference(
                                                SpreadsheetSelection.parseCellRange("$F$1:$O$1")
                                        )
                                )
                        ).setColumnCount(OptionalInt.of(0))
                        .setRowCount(OptionalInt.of(0))
        ); // b..c deleted, d moved

        this.countAndCheck(
                labelStore,
                1
        );

        final SpreadsheetCellReference end = p1.addColumn(-count);
        this.loadLabelAndCheck(
                labelStore,
                LABEL,
                f1.cellRange(end)
        );
    }

    @SuppressWarnings("unused")
    @Test
    public void testDeleteColumnsWithLabelsToRangeFixed4() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelStore labelStore = context.storeRepository()
                .labels();

        final SpreadsheetCellReference f1 = SpreadsheetSelection.parseCell("$F$1");// delete
        final SpreadsheetCellReference k1 = SpreadsheetSelection.parseCell("$K$1"); // range delete
        final SpreadsheetCellReference p1 = SpreadsheetSelection.parseCell("$P$1");// range delete
        final SpreadsheetCellReference u1 = SpreadsheetSelection.parseCell("$U$1");
        final SpreadsheetCellReference z1 = SpreadsheetSelection.parseCell("$Z$1"); // range

        final SpreadsheetCellRangeReference k1z1 = k1.cellRange(z1);
        labelStore.save(
                SpreadsheetLabelMapping.with(
                        LABEL,
                        k1z1
                )
        );

        final int count = p1.column().value() - f1.column().value();
        this.deleteColumnsAndCheck(
                engine,
                f1.column(),
                count,
                context,
                SpreadsheetDelta.EMPTY.setLabels(
                                Sets.of(
                                        LABEL.setLabelMappingReference(
                                                SpreadsheetSelection.parseCellRange("$F$1:$K$1")
                                        )
                                )
                        ).setColumnCount(OptionalInt.of(0))
                        .setRowCount(OptionalInt.of(0))
        );

        this.countAndCheck(
                labelStore,
                1
        );

        this.loadLabelAndCheck(
                labelStore,
                LABEL,
                f1.cellRange(k1)
        );
    }

    // insertColumn....................................................................................................

    @Test
    public void testInsertColumnsWithZero() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference cv1 = SpreadsheetSelection.parseCell("$CV$1");

        engine.saveCell(
                this.cell(
                        cv1,
                        "=99+0"
                ),
                context
        );

        this.addCellSaveWatcherAndDeleteWatcherThatThrowsUOE(context);

        this.insertColumnsAndCheck(
                engine,
                cv1.column(),
                0,
                context
        );

        this.countAndCheck(context.storeRepository().cells(), 1);

        this.loadCellAndFormulaAndValueCheck(
                engine,
                cv1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=99+0",
                99
        );
    }

    @Test
    public void testInsertColumn() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("$A$1"); // A1
        final SpreadsheetCellReference b1 = SpreadsheetSelection.parseCell("$B$1"); // MOVED
        final SpreadsheetCellReference c1 = SpreadsheetSelection.parseCell("$C$1"); // MOVED

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+2"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        b1,
                        "=3+4"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        c1,
                        "=5+6"
                ),
                context
        );

        final int count = 1;
        this.insertColumnsAndCheck(
                engine,
                b1.column(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "$C$1",
                                                "=3+4",
                                                3 + 4
                                        ),
                                        this.formatCell(
                                                "$D$1",
                                                "=5+6",
                                                5 + 6
                                        )
                                )
                        ).setDeletedCells(
                                Sets.of(b1)
                        ).setColumnWidths(
                                columnWidths("B,C,D")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(4)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        );

        this.countAndCheck(
                context.storeRepository()
                        .cells(),
                3
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+2",
                1 + 2
        );
    }

    @Test
    public void testInsertColumn2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("$A$1"); // A1
        final SpreadsheetCellReference b1 = SpreadsheetSelection.parseCell("$B$1"); // MOVED
        final SpreadsheetCellReference c1 = SpreadsheetSelection.parseCell("$C$1"); // MOVED

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+2"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        b1,
                        "=3+4"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        c1,
                        "=5+6"
                ),
                context
        );

        final int count = 1;
        this.insertColumnsAndCheck(
                engine,
                b1.column(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "$C$1",
                                                "=3+4",
                                                3 + 4
                                        ),
                                        this.formatCell(
                                                "$D$1",
                                                "=5+6",
                                                5 + 6
                                        )
                                )
                        ).setDeletedCells(
                                Sets.of(b1)
                        ).setColumnWidths(
                                columnWidths("B,C,D")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(4)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        ); // $b insert

        this.countAndCheck(
                context.storeRepository()
                        .cells(),
                3
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+2",
                1 + 2
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                b1.addColumn(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=3+4",
                3 + 4
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                c1.addColumn(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=5+6",
                5 + 6
        );
    }

    @Test
    public void testInsertColumnsWithLabelToCell() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference c2 = SpreadsheetSelection.parseCell("$C$2"); //
        final SpreadsheetCellReference e4 = SpreadsheetSelection.parseCell("$E$4"); // moved

        final SpreadsheetLabelMapping mapping = labelStore.save(
                SpreadsheetLabelMapping.with(
                        LABEL,
                        c2
                )
        );

        engine.saveCell(
                this.cell(
                        c2,
                        "=100"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        e4,
                        "=2+" + LABEL
                ),
                context
        );

        final int count = 1;
        this.insertColumnsAndCheck(
                engine,
                e4.column(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "$F$4",
                                                "=2+" + LABEL,
                                                2 + 100
                                        )
                                )
                        ).setLabels(
                                Sets.of(mapping)
                        ).setDeletedCells(
                                Sets.of(e4)
                        ).setColumnWidths(
                                columnWidths("E,F")
                        ).setRowHeights(
                                rowHeights("4")
                        ).setColumnCount(
                                OptionalInt.of(6)
                        ).setRowCount(
                                OptionalInt.of(4)
                        )
        ); // $b insert

        this.loadLabelAndCheck(
                labelStore,
                LABEL,
                c2
        );

        this.countAndCheck(
                cellStore,
                2
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                c2,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=100",
                100
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                e4.addColumn(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=2+" + LABEL,
                2 + 100
        );
    }

    @Test
    public void testInsertColumnsWithLabelToCell2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("$A$1"); // A1
        final SpreadsheetCellReference b1 = SpreadsheetSelection.parseCell("$B$1"); // moved
        final SpreadsheetCellReference c1 = SpreadsheetSelection.parseCell("$C$1"); // MOVED
        final SpreadsheetCellReference n9 = SpreadsheetSelection.parseCell("$N$9"); // moved

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, n9));

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+" + LABEL
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        b1,
                        "=2+0"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        c1,
                        "=3+0+" + LABEL
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        n9,
                        "=99+0"
                ),
                context
        );

        final int count = 1;
        this.insertColumnsAndCheck(engine,
                b1.column(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                a1,
                                                "=1+" + LABEL,
                                                1 + 99 + 0
                                        ),
                                        this.formatCell(
                                                "$C$1",
                                                "=2+0",
                                                2 + 0
                                        ),
                                        this.formatCell(
                                                "$D$1",
                                                "=3+0+" + LABEL,
                                                3 + 0 + 99 + 0
                                        ),
                                        this.formatCell(
                                                "$O$9",
                                                "=99+0",
                                                99 + 0
                                        )
                                )
                        ).setReferences(references("O9=Label123")
                        ).setLabels(
                                Sets.of(
                                        LABEL.setLabelMappingReference(
                                                SpreadsheetSelection.parseCell("$O$9")
                                        )
                                )
                        ).setDeletedCells(
                                Sets.of(b1, n9)
                        ).setColumnWidths(
                                columnWidths("A,B,C,D,N,O")
                        ).setRowHeights(
                                rowHeights("1,9")
                        ).setColumnCount(
                                OptionalInt.of(15)
                        ).setRowCount(
                                OptionalInt.of(9)
                        )
        ); // $b insert

        this.loadLabelAndCheck(
                labelStore,
                LABEL,
                n9.addColumn(count)
        );

        this.countAndCheck(
                cellStore,
                4
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+" + LABEL,
                1 + 99
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                b1.addColumn(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=2+0",
                2 + 0
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                c1.addColumn(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=3+0+" + LABEL,
                3 + 99
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                n9.addColumn(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=99+0",
                99 + 0
        );
    }

    @Test
    public void testInsertColumnsWithLabelToRangeUnchanged() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("$A$1"); // A1
        final SpreadsheetCellReference f6 = SpreadsheetSelection.parseCell("$F$6"); // moved

        final SpreadsheetCellRangeReference a1b2 = a1.cellRange(a1.add(1, 1));
        final SpreadsheetLabelMapping mapping = labelStore.save(
                SpreadsheetLabelMapping.with(
                        LABEL,
                        a1b2
                )
        );

        engine.saveCell(
                this.cell(
                        a1,
                        "=99+0"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        f6,
                        "=2+0+BasicSpreadsheetEngineTestSum(" + LABEL +")"
                ),
                context
        );

        final int count = 1;
        this.insertColumnsAndCheck(
                engine,
                f6.column(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "$G$6",
                                                "=2+0+BasicSpreadsheetEngineTestSum(" + LABEL+")",
                                                2 + 0 + 99 + 0
                                        )
                                )
                        ).setLabels(
                                Sets.of(mapping)
                        ).setDeletedCells(
                                Sets.of(f6)
                        ).setColumnWidths(
                                columnWidths("F,G")
                        ).setRowHeights(
                                rowHeights("6")
                        ).setColumnCount(
                                OptionalInt.of(7)
                        ).setRowCount(
                                OptionalInt.of(6)
                        )
        ); // $b insert

        this.countAndCheck(labelStore, 1);

        this.loadLabelAndCheck(labelStore, LABEL, a1b2);

        this.countAndCheck(cellStore, 2);

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=99+0",
                99 + 0
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                f6.addColumn(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=2+0+BasicSpreadsheetEngineTestSum(" + LABEL +")",
                2 + 99
        );
    }

    @Test
    public void testInsertColumnsWithLabelToRangeUpdated() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1.toAbsolute();
        final SpreadsheetCellReference f1 = SpreadsheetSelection.parseCell("$F$1");
        final SpreadsheetCellReference k1 = SpreadsheetSelection.parseCell("$K$1");
        final SpreadsheetCellReference p1 = SpreadsheetSelection.parseCell("$P$1");
        final SpreadsheetCellReference u1 = SpreadsheetSelection.parseCell("$U$1");

        labelStore.save(
                SpreadsheetLabelMapping.with(
                        LABEL,
                        k1.cellRange(p1)
                )
        );

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+BasicSpreadsheetEngineTestSum(" + LABEL + ")"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        k1,
                        "=99+0"
                ),
                context
        );

        this.insertColumnsAndCheck(
                engine,
                f1.column(),
                k1.column().value() - f1.column().value(),
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                a1,
                                                "=1+BasicSpreadsheetEngineTestSum(" + LABEL + ")",
                                                1 + 99 + 0
                                        ),
                                        this.formatCell(
                                                "$P$1",
                                                "=99+0",
                                                99 + 0
                                        )
                                )
                        ).setReferences(references("P1=Label123")
                        ).setLabels(
                                Sets.of(
                                        LABEL.setLabelMappingReference(
                                                SpreadsheetSelection.parseCellRange("$P$1:$U$1")
                                        )
                                )
                        ).setDeletedCells(
                                Sets.of(k1)
                        ).setColumnWidths(
                                columnWidths("A,K,P")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(16)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        ); // $b insert

        this.countAndCheck(
                labelStore,
                1
        );
        this.loadLabelAndCheck(
                labelStore,
                LABEL,
                p1.cellRange(u1)
        );

        this.countAndCheck(
                cellStore,
                2
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+BasicSpreadsheetEngineTestSum(" + LABEL +")",
                1 + 99
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                p1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=99+0",
                99 + 0
        );
    }

    @Test
    public void testInsertColumnsWithCellReferences() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("$A$1"); // A1
        final SpreadsheetCellReference b1 = SpreadsheetSelection.parseCell("$B$1"); // B1
        final SpreadsheetCellReference k1 = SpreadsheetSelection.parseCell("$K$1"); // moved
        final SpreadsheetCellReference n9 = SpreadsheetSelection.parseCell("$N$9"); // moved

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+0+" + n9
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        b1,
                        "=2+0"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        k1,
                        "=3+0"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        n9,
                        "=4+0+" + b1
                ),
                context
        );

        final int count = 1;
        this.insertColumnsAndCheck(
                engine,
                k1.column(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "$A$1",
                                                "=1+0+$O$9",
                                                1 + 0 + 4 + 0 + 2 + 0
                                        ),
                                        this.formatCell(
                                                "$L$1",
                                                "=3+0",
                                                3 + 0
                                        ),
                                        this.formatCell(
                                                "$O$9",
                                                "=4+0+" + b1,
                                                4 + 0 + 2 + 0
                                        )
                                )
                        ).setReferences(references("O9=A1")
                        ).setDeletedCells(
                                Sets.of(k1, n9)
                        ).setColumnWidths(
                                columnWidths("A,K,L,N,O")
                        ).setRowHeights(
                                rowHeights("1,9")
                        ).setColumnCount(
                                OptionalInt.of(15)
                        ).setRowCount(
                                OptionalInt.of(9)
                        )
        ); // $c insert

        this.countAndCheck(
                context.storeRepository()
                        .cells(),
                4
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0+" + n9.addColumn(count),
                1 + 0 + 4 + 2
        ); // reference should have been fixed.

        this.loadCellAndFormulaAndValueCheck(
                engine,
                b1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=2+0",
                2
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                k1.addColumn(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=3+0",
                3
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                n9.addColumn(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=4+0+" + b1,
                4 + 2
        );
    }

    @Test
    public void testInsertColumnsWithCellReferences2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("$A$1"); // A1
        final SpreadsheetCellReference b1 = SpreadsheetSelection.parseCell("$B$1"); //
        final SpreadsheetCellReference k1 = SpreadsheetSelection.parseCell("$K$1"); // moved
        final SpreadsheetCellReference n9 = SpreadsheetSelection.parseCell("$N$9"); // moved

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+0+" + n9
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        b1,
                        "=2+0"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        k1,
                        "=3+0"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        n9,
                        "=4+0+" + b1
                ),
                context
        ); // =5+2

        final int count = 2;
        this.insertColumnsAndCheck(
                engine,
                k1.column(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "$A$1",
                                                "=1+0+$P$9",
                                                1 + 0 + 4 + 0 + 2 + 0
                                        ),
                                        this.formatCell(
                                                "$M$1",
                                                "=3+0",
                                                3 + 0
                                        ),
                                        this.formatCell(
                                                "$P$9",
                                                "=4+0+" + b1,
                                                4 + 0 + 2 + 0
                                        )
                                )
                        ).setReferences(references("P9=A1")
                        ).setDeletedCells(
                                Sets.of(k1, n9)
                        ).setColumnWidths(
                                columnWidths("A,K,M,N,P")
                        ).setRowHeights(
                                rowHeights("1,9")
                        ).setColumnCount(
                                OptionalInt.of(16)
                        ).setRowCount(
                                OptionalInt.of(9)
                        )
        ); // $c insert

        this.countAndCheck(
                context.storeRepository()
                        .cells(),
                4
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0+" + n9.addColumn(count),
                1 + 0 + 4 + 2
        ); // reference should have been fixed.

        final Number number = 2 + 0;
        this.loadCellAndFormulaAndValueCheck(
                engine,
                b1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=2+0",
                number
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                k1.addColumn(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=3+0",
                3 + 0
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                n9.addColumn(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=4+0+" + b1,
                4 + 0 + 2
        );
    }

    @Test
    public void testInsertColumnsWithSeveral() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("$A$1"); //
        final SpreadsheetCellReference k1 = SpreadsheetSelection.parseCell("$K$1"); // MOVED
        final SpreadsheetCellReference l1 = SpreadsheetSelection.parseCell("$L$1"); // MOVED
        final SpreadsheetCellReference m3 = SpreadsheetSelection.parseCell("$M$3"); // MOVED
        final SpreadsheetCellReference u4 = SpreadsheetSelection.parseCell("$U$4"); // MOVED

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+0"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        k1,
                        "=2+0"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        l1,
                        "=3+0"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        m3,
                        "=4+0"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        u4,
                        "=5+0"
                ),
                context
        );

        final int count = 5;
        this.insertColumnsAndCheck(
                engine,
                SpreadsheetReferenceKind.ABSOLUTE.column(7),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "$P$1",
                                                "=2+0",
                                                2 + 0
                                        ),
                                        this.formatCell(
                                                "$Q$1",
                                                "=3+0",
                                                3 + 0
                                        ),
                                        this.formatCell(
                                                "$R$3",
                                                "=4+0",
                                                4 + 0
                                        ),
                                        this.formatCell(
                                                "$Z$4",
                                                "=5+0",
                                                5 + 0
                                        )
                                )
                        ).setDeletedCells(
                                Sets.of(k1, l1, m3, u4)
                        ).setColumnWidths(
                                columnWidths("K,L,M,P,Q,R,U,Z")
                        ).setRowHeights(
                                rowHeights("1,3,4")
                        ).setColumnCount(
                                OptionalInt.of(26)
                        ).setRowCount(
                                OptionalInt.of(4)
                        )
        ); // $b & $c

        this.countAndCheck(
                context.storeRepository()
                        .cells(),
                5
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0",
                1 + 0
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                k1.addColumn(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=2+0",
                2 + 0
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                l1.addColumn(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=3+0",
                3 + 0
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                m3.addColumn(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=4+0",
                4 + 0
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                u4.addColumn(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=5+0",
                5 + 0
        );
    }

    @Test
    public void testInsertColumnsWithColumns() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = SpreadsheetSelection.parseCell("B1"); // MOVED

        final SpreadsheetColumn b = b1.column()
                .column();
        engine.saveColumn(b, context);

        final SpreadsheetColumn c = SpreadsheetSelection.parseColumn("c")
                .column();
        engine.saveColumn(c, context);

        engine.saveCell(
                this.cell(
                        a1,
                        ""
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        b1,
                        ""
                ),
                context
        );

        final int count = 1;
        this.insertColumnsAndCheck(
                engine,
                b1.column(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell("C1", "")
                                )
                        ).setColumns(
                                Sets.of(c)
                        ).setDeletedCells(
                                Sets.of(b1)
                        ).setDeletedColumns(
                                Sets.of(b.reference())
                        ).setColumnWidths(
                                columnWidths("B,C")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(3)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        );

        this.countAndCheck(
                context.storeRepository()
                        .cells(),
                2
        );
    }

    @Test
    public void testInsertColumnsWithRows() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = SpreadsheetSelection.parseCell("B1"); // MOVED

        final SpreadsheetRow row2 = b1.row()
                .row();
        engine.saveRow(row2, context);

        final SpreadsheetRow row3 = SpreadsheetSelection.parseRow("2")
                .row();
        engine.saveRow(
                row3,
                context
        );

        engine.saveCell(
                this.cell(
                        a1,
                        ""
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        b1,
                        ""
                ),
                context
        );

        this.insertColumnsAndCheck(
                engine,
                b1.column(),
                1,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "C1",
                                                ""
                                        )
                                )
                        ).setRows(
                                Sets.of(row2)
                        ).setDeletedCells(
                                Sets.of(b1)
                        ).setColumnWidths(
                                columnWidths("B,C")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(3)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        );

        this.countAndCheck(
                context.storeRepository()
                        .cells(),
                2
        );
    }

    // insertRow....................................................................................................

    @Test
    public void testInsertRowsWithZero() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("$A$100"); // A3

        engine.saveCell(this.cell(reference, "=99+0"), context);

        this.addCellSaveWatcherAndDeleteWatcherThatThrowsUOE(context);

        this.insertRowsAndCheck(
                engine,
                reference.row(),
                0,
                context
        );

        this.countAndCheck(
                context.storeRepository()
                        .cells(),
                1
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                reference,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=99+0",
                99
        );
    }

    @Test
    public void testInsertRows() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("$A$1"); // A1
        final SpreadsheetCellReference a2 = SpreadsheetSelection.parseCell("$A$2"); // MOVED
        final SpreadsheetCellReference a3 = SpreadsheetSelection.parseCell("$A$3"); // MOVED

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+2"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        a2,
                        "=3+4"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        a3,
                        "=5+6"
                ),
                context
        );

        final int count = 1;
        this.insertRowsAndCheck(
                engine,
                a2.row(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "$A$3",
                                                "=3+4",
                                                3 + 4
                                        ),
                                        this.formatCell(
                                                "$A$4",
                                                "=5+6",
                                                5 + 6
                                        )
                                )
                        )
                        .setDeletedCells(
                                Sets.of(
                                        a2
                                )
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("2,3,4")
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(4)
                        )
        );

        this.countAndCheck(
                context.storeRepository()
                        .cells(),
                3
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+2",
                1 + 2
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a2.addRow(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=3+4",
                3 + 4
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a3.addRow(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=5+6",
                5 + 6
        );
    }

    @Test
    public void testInsertRows2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("$A$1"); // A1
        final SpreadsheetCellReference a2 = SpreadsheetSelection.parseCell("$A$2"); // MOVED
        final SpreadsheetCellReference a3 = SpreadsheetSelection.parseCell("$A$3"); // MOVED

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+2"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        a2,
                        "=3+4"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        a3,
                        "=5+6"
                ),
                context
        );

        final int count = 1;
        this.insertRowsAndCheck(
                engine,
                a2.row(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "$A$3",
                                                "=3+4",
                                                3 + 4
                                        ),
                                        this.formatCell(
                                                "$A$4",
                                                "=5+6",
                                                5 + 6
                                        )
                                )
                        ).setDeletedCells(
                                Sets.of(
                                        a2
                                )
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("2,3,4")
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(4)
                        )
        ); // $b insert

        this.countAndCheck(
                context.storeRepository()
                        .cells(),
                3
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+2",
                1 + 2
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a2.addRow(+count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=3+4",
                3 + 4
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a3.addRow(+count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=5+6",
                5 + 6
        );
    }

    @Test
    public void testInsertRowsWithLabelToCell() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference b3 = SpreadsheetSelection.parseCell("$B$3"); //
        final SpreadsheetCellReference d5 = SpreadsheetSelection.parseCell("$D$5"); // moved

        final SpreadsheetLabelMapping mapping = labelStore.save(
                SpreadsheetLabelMapping.with(
                        LABEL,
                        b3
                )
        );

        engine.saveCell(
                this.cell(
                        b3,
                        "=100"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        d5,
                        "=2+" + LABEL
                ),
                context
        );

        final int count = 1;
        this.insertRowsAndCheck(
                engine,
                d5.row(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "$D$6",
                                                "=2+" + LABEL,
                                                2 + 100
                                        )
                                )
                        ).setLabels(
                                Sets.of(mapping)
                        ).setDeletedCells(
                                Sets.of(d5)
                        ).setColumnWidths(
                                columnWidths("D")
                        ).setRowHeights(
                                rowHeights("5,6")
                        ).setColumnCount(
                                OptionalInt.of(4)
                        ).setRowCount(
                                OptionalInt.of(6)
                        )
        ); // $b insert

        this.loadLabelAndCheck(labelStore, LABEL, b3);

        this.countAndCheck(cellStore, 2);

        this.loadCellAndFormulaAndValueCheck(
                engine,
                b3,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=100",
                100
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                d5.addRow(+count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=2+" + LABEL,
                2 + 100
        );
    }

    @Test
    public void testInsertRowsWithLabelToCell2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("$A$1"); // A1
        final SpreadsheetCellReference a2 = SpreadsheetSelection.parseCell("$A$2"); // moved
        final SpreadsheetCellReference a3 = SpreadsheetSelection.parseCell("$A$3"); // MOVED
        final SpreadsheetCellReference i14 = SpreadsheetSelection.parseCell("$I$14"); // moved

        labelStore.save(
                SpreadsheetLabelMapping.with(
                        LABEL,
                        i14
                )
        );

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+" + LABEL
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        a2,
                        "=2+0"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        a3,
                        "=3+0+" + LABEL
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        i14,
                        "=99+0"
                ),
                context
        );

        final int count = 1;
        this.insertRowsAndCheck(
                engine,
                a2.row(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "$A$1",
                                                "=1+" + LABEL,
                                                1 + 99 + 0
                                        ),
                                        this.formatCell(
                                                "$A$3",
                                                "=2+0",
                                                2 + 0
                                        ),
                                        this.formatCell(
                                                "$A$4",
                                                "=3+0+" + LABEL,
                                                3 + 0 + 99
                                        ),
                                        this.formatCell(
                                                "$I$15",
                                                "=99+0",
                                                99 + 0
                                        ) // $b insert
                                )
                        ).setLabels(
                                Sets.of(
                                        SpreadsheetLabelMapping.with(
                                                LABEL,
                                                SpreadsheetSelection.parseCell("$I$15")
                                        )
                                )
                        ).setReferences(references("I15=Label123")
                        ).setDeletedCells(
                                Sets.of(a2, i14)
                        ).setColumnWidths(
                                columnWidths("A,I")
                        ).setRowHeights(
                                rowHeights("1,2,3,4,14,15")
                        ).setColumnCount(
                                OptionalInt.of(9)
                        ).setRowCount(
                                OptionalInt.of(15)
                        )
        );

        this.loadLabelAndCheck(
                labelStore,
                LABEL,
                i14.addRow(+count)
        );

        this.countAndCheck(
                cellStore,
                4
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+" + LABEL,
                1 + 99
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a2.addRow(+count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=2+0",
                2 + 0
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a3.addRow(+count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=3+0+" + LABEL,
                3 + 99
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                i14.addRow(+count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=99+0",
                99 + 0
        );
    }

    @Test
    public void testInsertRowsWithLabelToRangeUnchanged() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("$A$1"); //
        final SpreadsheetCellReference f6 = SpreadsheetSelection.parseCell("$F$6"); // moved

        final SpreadsheetCellRangeReference a1b2 = a1.cellRange(
                a1.add(1, 1)
        );
        final SpreadsheetLabelMapping mapping = labelStore.save(
                SpreadsheetLabelMapping.with(
                        LABEL,
                        a1b2
                )
        );

        engine.saveCell(
                this.cell(
                        a1,
                        "=99+0"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        f6,
                        "=2+0+BasicSpreadsheetEngineTestSum(" + LABEL + ")"
                ),
                context
        );

        final int count = 1;
        this.insertRowsAndCheck(
                engine,
                f6.row(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "$F$7",
                                                "=2+0+BasicSpreadsheetEngineTestSum(" + LABEL + ")",
                                                2 + 0 + 99 + 0
                                        ) // $b insert
                                )
                        ).setLabels(
                                Sets.of(mapping)
                        ).setDeletedCells(
                                Sets.of(f6)
                        ).setColumnWidths(
                                columnWidths("F")
                        ).setRowHeights(
                                rowHeights("6,7")
                        ).setColumnCount(
                                OptionalInt.of(6)
                        ).setRowCount(
                                OptionalInt.of(7)
                        )
        );

        this.countAndCheck(
                labelStore,
                1
        );

        this.loadLabelAndCheck(
                labelStore,
                LABEL,
                a1b2
        );

        this.countAndCheck(
                cellStore,
                2
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=99+0",
                99 + 0
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                f6.addRow(+count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=2+0+BasicSpreadsheetEngineTestSum(" + LABEL + ")",
                2 + 99
        );
    }

    @Test
    public void testInsertRowsWithLabelToRangeUpdated() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1.toAbsolute();
        final SpreadsheetCellReference a6 = SpreadsheetSelection.parseCell("$A$6");
        final SpreadsheetCellReference a11 = SpreadsheetSelection.parseCell("$A$11");
        final SpreadsheetCellReference a16 = SpreadsheetSelection.parseCell("$A$16");
        final SpreadsheetCellReference a21 = SpreadsheetSelection.parseCell("$A$21");

        labelStore.save(
                SpreadsheetLabelMapping.with(
                        LABEL,
                        a11.cellRange(a16)
                )
        );

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+BasicSpreadsheetEngineTestSum(" + LABEL + ")"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        a11,
                        "=99+0"
                ),
                context
        );

        this.insertRowsAndCheck(
                engine,
                a6.row(),
                a11.row().value() - a6.row().value(),
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                a1,
                                                "=1+BasicSpreadsheetEngineTestSum(" + LABEL + ")",
                                                1 + 99 + 0
                                        ),
                                        this.formatCell(
                                                "$A$16",
                                                "=99+0",
                                                99 + 0
                                        )// $b insert
                                )
                        ).setLabels(
                                Sets.of(
                                        LABEL.setLabelMappingReference(
                                                SpreadsheetSelection.parseCellRange("$A$16:$A$21")
                                        )
                                )
                        ).setReferences(references("A16=Label123")
                        ).setDeletedCells(
                                Sets.of(a11)
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1,11,16")
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(16)
                        )
        );

        this.countAndCheck(
                labelStore,
                1
        );
        this.loadLabelAndCheck(
                labelStore,
                LABEL,
                a16.cellRange(a21)
        );

        this.countAndCheck(
                cellStore,
                2
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+BasicSpreadsheetEngineTestSum(" + LABEL + ")",
                1 + 99
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a16,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=99+0",
                99 + 0
        );
    }

    @Test
    public void testInsertRowsWithCellReferences() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("$A$1"); // A1
        final SpreadsheetCellReference a2 = SpreadsheetSelection.parseCell("$A$2"); // A2
        final SpreadsheetCellReference a11 = SpreadsheetSelection.parseCell("$A$11"); // moved
        final SpreadsheetCellReference i14 = SpreadsheetSelection.parseCell("$I$14"); // moved

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+0+" + i14
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        a2,
                        "=2+0"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        a11,
                        "=3+0"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        i14,
                        "=4+0+" + a2
                ),
                context
        );

        final int count = 1;
        this.insertRowsAndCheck(
                engine,
                a11.row(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "$A$1",
                                                "=1+0+$I$15",
                                                3 + 4
                                        ),
                                        this.formatCell(
                                                "$A$12",
                                                "=3+0",
                                                3 + 0
                                        ),
                                        this.formatCell(
                                                "$I$15",
                                                "=4+0+" + a2,
                                                4 + 0 + 2 + 0
                                        )// $c insert
                                )
                        ).setReferences(references("I15=A1")
                        ).setDeletedCells(
                                Sets.of(a11, i14)
                        ).setColumnWidths(
                                columnWidths("A,I")
                        ).setRowHeights(
                                rowHeights("1,11,12,14,15")
                        ).setColumnCount(
                                OptionalInt.of(9)
                        ).setRowCount(
                                OptionalInt.of(15)
                        )
        );

        this.countAndCheck(
                context.storeRepository()
                        .cells(),
                4
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0+" + i14.addRow(+count),
                1 + 0 + 4 + 2
        ); // reference should have been fixed.

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a2,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=2+0",
                2
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a11.addRow(+count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=3+0",
                3
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                i14.addRow(+count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=4+0+" + a2,
                4 + 2
        );
    }

    @Test
    public void testInsertRowsWithCellReferences2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("$A$1"); // A1
        final SpreadsheetCellReference a2 = SpreadsheetSelection.parseCell("$A$2"); // A2
        final SpreadsheetCellReference a11 = SpreadsheetSelection.parseCell("$A$11"); // moved
        final SpreadsheetCellReference i14 = SpreadsheetSelection.parseCell("$I$14"); // moved

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+0+" + i14
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        a2,
                        "=2+0"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        a11,
                        "=3+0"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        i14,
                        "=4+0+" + a2
                ),
                context
        ); // =5+2

        final int count = 2;
        this.insertRowsAndCheck(
                engine,
                a11.row(),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell("$A$1", "=1+0+$I$16", 1 + 0 + 4 + 0 + 2 + 0),
                                        this.formatCell("$A$13", "=3+0", 3 + 0),
                                        this.formatCell("$I$16", "=4+0+" + a2, 4 + 0 + 2 + 0)  // $c insert
                                )
                        ).setReferences(references("I16=A1")
                        ).setDeletedCells(
                                Sets.of(a11, i14)
                        ).setColumnWidths(
                                columnWidths("A,I")
                        ).setRowHeights(
                                rowHeights("1,11,13,14,16")
                        ).setColumnCount(
                                OptionalInt.of(9)
                        ).setRowCount(
                                OptionalInt.of(16)
                        )
        );

        this.countAndCheck(
                context.storeRepository()
                        .cells(),
                4
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0+" + i14.addRow(+count),
                1 + 0 + 4 + 2
        ); // reference should have been fixed.

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a2,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=2+0",
                2 + 0
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a11.addRow(+count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=3+0",
                3 + 0
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                i14.addRow(+count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=4+0+" + a2,
                4 + 0 + 2
        );
    }

    @Test
    public void testInsertRowsWithSeveral() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("$A$1"); // A1
        final SpreadsheetCellReference a11 = SpreadsheetSelection.parseCell("$A$11"); // MOVED
        final SpreadsheetCellReference a12 = SpreadsheetSelection.parseCell("$A$12"); // MOVED
        final SpreadsheetCellReference c13 = SpreadsheetSelection.parseCell("$C$13"); // MOVED
        final SpreadsheetCellReference d21 = SpreadsheetSelection.parseCell("$D$21"); // MOVED

        engine.saveCell(
                this.cell(
                        a1,
                        "=1+0"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        a11,
                        "=2+0"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        a12,
                        "=3+0"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        c13,
                        "=4+0"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        d21,
                        "=5+0"
                ),
                context
        );

        final int count = 5;
        this.insertRowsAndCheck(
                engine,
                SpreadsheetReferenceKind.ABSOLUTE.row(7),
                count,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "$A$16",
                                                "=2+0",
                                                2 + 0
                                        ),
                                        this.formatCell(
                                                "$A$17",
                                                "=3+0",
                                                3 + 0
                                        ),
                                        this.formatCell(
                                                "$C$18",
                                                "=4+0",
                                                4 + 0
                                        ),
                                        this.formatCell(
                                                "$D$26",
                                                "=5+0",
                                                5 + 0
                                        )
                                )
                        ).setDeletedCells(
                                Sets.of(a11, a12, c13, d21)
                        ).setColumnWidths(
                                columnWidths("A,C,D")
                        ).setRowHeights(
                                rowHeights("11,12,13,16,17,18,21,26")
                        ).setColumnCount(
                                OptionalInt.of(4)
                        ).setRowCount(
                                OptionalInt.of(26)
                        )
        ); // $b & $c

        this.countAndCheck(
                context.storeRepository()
                        .cells(),
                5
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a1,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0",
                1 + 0
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a11.addRow(+count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=2+0",
                2 + 0
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                a12.addRow(+count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=3+0",
                3 + 0);

        this.loadCellAndFormulaAndValueCheck(
                engine,
                c13.addRow(+count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=4+0",
                4 + 0
        );

        this.loadCellAndFormulaAndValueCheck(
                engine,
                d21.addRow(+count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=5+0",
                5 + 0
        );
    }

    @Test
    public void testInsertRowsWithColumns() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;

        final SpreadsheetColumn a = a1.column().column();
        engine.saveColumn(
                a,
                context
        );

        engine.saveCell(
                this.cell(
                        a1,
                        ""
                ),
                context
        );

        this.insertRowsAndCheck(
                engine,
                a1.row(),
                1,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "A2",
                                                ""
                                        )
                                )
                        ).setColumns(
                                Sets.of(a)
                        ).setDeletedCells(
                                Sets.of(a1)
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1,2")
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(2)
                        )
        );

        this.countAndCheck(
                context.storeRepository().cells(),
                1
        );
    }

    @Test
    public void testInsertRowsWithRows() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;

        final SpreadsheetRow row1 = a1.row()
                .row();
        engine.saveRow(
                row1,
                context
        );

        final SpreadsheetRow row2 = SpreadsheetSelection.parseRow("2")
                .row();
        engine.saveRow(row2, context);

        final SpreadsheetRow row3 = SpreadsheetSelection.parseRow("3")
                .row();
        engine.saveRow(
                row3,
                context
        );

        engine.saveCell(
                this.cell(
                        a1,
                        ""
                ),
                context
        );

        this.insertRowsAndCheck(
                engine,
                a1.row(),
                1,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "A2",
                                                ""
                                        )
                                )
                        ).setRows(
                                Sets.of(row2)
                        ).setDeletedCells(
                                Sets.of(a1)
                        ).setDeletedRows(
                                Sets.of(
                                        row1.reference()
                                )
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1,2")
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(2)
                        )
        );

        this.countAndCheck(
                context.storeRepository()
                        .cells(),
                1
        );
    }

    // loadMultipleCellRanges...........................................................................................

    @Test
    public void testLoadMultipleCellRangesNothing() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        this.loadMultipleCellRangesAndCheck(
                engine,
                "A1:B2",
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                SpreadsheetDeltaProperties.ALL,
                context
        );
    }

    @Test
    public void testLoadMultipleCellRanges() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCell b2 = this.cell(
                "b2",
                "=2"
        );
        cellStore.save(b2);

        final SpreadsheetCell c3 = this.cell(
                "c3",
                "=3"
        );
        cellStore.save(c3);

        final SpreadsheetViewportWindows window = SpreadsheetViewportWindows.parse("b2:c3");

        this.loadMultipleCellRangesAndCheck(
                engine,
                window.cellRanges(),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                b2,
                                                2
                                        ),
                                        this.formatCell(
                                                c3,
                                                3
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("B,C")
                        ).setRowHeights(
                                rowHeights("2,3")
                        ).setColumnCount(
                                OptionalInt.of(3)
                        ).setRowCount(
                                OptionalInt.of(3)
                        ).setWindow(window)
        );
    }

    @Test
    public void testLoadMultipleCellRanges2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("a1");
        cellStore.save(
                a1.setFormula(
                        SpreadsheetFormula.EMPTY.setText("=1")
                )
        );

        cellStore.save(
                SpreadsheetSelection.parseCell("c3")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("=3")
                        )
        );

        final SpreadsheetCellReference d4 = SpreadsheetSelection.parseCell("d4");
        cellStore.save(
                d4.setFormula(
                        SpreadsheetFormula.EMPTY.setText("=4")
                )
        );

        final SpreadsheetViewportWindows window = SpreadsheetViewportWindows.parse("A1:B2,D4:E5");

        // c3 must not be returned
        this.loadMultipleCellRangesAndCheck(
                engine,
                window.cellRanges(),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.loadCellOrFail(
                                                engine,
                                                a1,
                                                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                                                context
                                        ),
                                        this.loadCellOrFail(
                                                engine,
                                                d4,
                                                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                                                context
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("A,D")
                        ).setRowHeights(
                                rowHeights("1,4")
                        ).setColumnCount(
                                OptionalInt.of(4)
                        ).setRowCount(
                                OptionalInt.of(4)
                        ).setWindow(window)
        );
    }

    @Test
    public void testLoadMultipleCellRangesNothingWithColumns() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetColumnStore columnStore = context.storeRepository()
                .columns();

        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("C3");
        final SpreadsheetColumn c = c3.column()
                .column();

        columnStore.save(
                SpreadsheetSelection.parseColumn("a")
                        .column()
        );
        columnStore.save(c);
        columnStore.save(
                SpreadsheetSelection.parseColumn("d")
                        .column()
        );

        final SpreadsheetViewportWindows window = SpreadsheetViewportWindows.parse("B2:C3");

        this.loadMultipleCellRangesAndCheck(
                engine,
                window.cellRanges(),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(SpreadsheetDelta.NO_CELLS)
                        .setColumns(
                                Sets.of(c)
                        ).setColumnCount(
                                OptionalInt.of(0)
                        ).setRowCount(
                                OptionalInt.of(0)
                        ).setWindow(window)
        );
    }

    @Test
    public void testLoadMultipleCellRangesNothingWithLabels() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelStore labelStore = context.storeRepository()
                .labels();

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("C3");
        final SpreadsheetLabelName label = SpreadsheetLabelName.labelName("LabelC3");

        labelStore.save(label.setLabelMappingReference(b2));

        final SpreadsheetViewportWindows window = SpreadsheetViewportWindows.parse("B2:C3");

        this.loadMultipleCellRangesAndCheck(
                engine,
                window.cellRanges(),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(SpreadsheetDelta.NO_CELLS)
                        .setLabels(
                                Sets.of(
                                        label.setLabelMappingReference(b2)
                                )
                        ).setColumnCount(
                                OptionalInt.of(0)
                        ).setRowCount(
                                OptionalInt.of(0)
                        ).setWindow(window)
        );
    }

    @Test
    public void testLoadMultipleCellRangesNothingWithRows() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetRowStore rowStore = context.storeRepository()
                .rows();

        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("C3");
        final SpreadsheetRow c = c3.row()
                .row();

        rowStore.save(
                SpreadsheetSelection.parseRow("1")
                        .row()
        );
        rowStore.save(c);
        rowStore.save(
                SpreadsheetSelection.parseRow("4")
                        .row()
        );

        final SpreadsheetViewportWindows window = SpreadsheetViewportWindows.parse("B2:C3");

        this.loadMultipleCellRangesAndCheck(
                engine,
                window.cellRanges(),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(SpreadsheetDelta.NO_CELLS)
                        .setRows(
                                Sets.of(c)
                        ).setColumnCount(
                                OptionalInt.of(0)
                        ).setRowCount(
                                OptionalInt.of(0)
                        ).setWindow(window)
        );
    }

    @Test
    public void testLoadMultipleCellRangesWithDivideByZero() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCell z9 = this.cell("z9", "=0/0");
        cellStore.save(z9);

        final SpreadsheetViewportWindows window = SpreadsheetViewportWindows.parse("z9");

        this.loadMultipleCellRangesAndCheck(
                engine,
                window.cellRanges(),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                z9,
                                                SpreadsheetErrorKind.DIV0.setMessage("Division by zero")
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("Z")
                        ).setRowHeights(
                                rowHeights("9")
                        ).setColumnCount(
                                OptionalInt.of(26)
                        ).setRowCount(
                                OptionalInt.of(9)
                        ).setWindow(window)
        );
    }

    @Test
    public void testLoadMultipleCellRangesWithCellReferences() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCell b2 = this.cell(
                "b2",
                "=c3*2"
        );
        cellStore.save(b2);

        final SpreadsheetCell c3 = this.cell(
                "c3",
                "=2"
        );
        cellStore.save(c3);

        final SpreadsheetViewportWindows window = SpreadsheetViewportWindows.parse("b2:c3");

        this.loadMultipleCellRangesAndCheck(
                engine,
                window.cellRanges(),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                b2,
                                                4
                                        ),
                                        this.formatCell(
                                                c3,
                                                2
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("B,C")
                        ).setRowHeights(
                                rowHeights("2,3")
                        ).setColumnCount(
                                OptionalInt.of(3)
                        ).setRowCount(
                                OptionalInt.of(3)
                        ).setWindow(window)
        );
    }

    @Test
    public void testLoadMultipleCellRangesWithReferencesToReferences() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCell a1 = this.cell(
                "a1",
                "=b2+100"
        );
        cellStore.save(a1);

        final SpreadsheetCell b2 = this.cell(
                "b2",
                "=c3+10"
        );
        cellStore.save(b2);

        final SpreadsheetCell c3 = this.cell(
                "c3",
                "=1"
        );
        cellStore.save(c3);

        final SpreadsheetViewportWindows window = SpreadsheetViewportWindows.parse("a1:c3");

        this.loadMultipleCellRangesAndCheck(
                engine,
                window.cellRanges(),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                a1,
                                                1 + 10 + 100
                                        ),
                                        this.formatCell(
                                                b2,
                                                1 + 10
                                        ),
                                        this.formatCell(
                                                c3,
                                                1
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("A,B,C")
                        ).setRowHeights(
                                rowHeights("1,2,3")
                        ).setColumnCount(
                                OptionalInt.of(3)
                        ).setRowCount(
                                OptionalInt.of(3)
                        ).setWindow(window)
        );
    }

    @Test
    public void testLoadMultipleCellRangesWithReferencesToReferences2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCell a1 = this.cell(
                "a1",
                "=c3+100"
        );
        cellStore.save(a1);

        final SpreadsheetCell b2 = this.cell(
                "b2",
                "=a1+10"
        );
        cellStore.save(b2);

        final SpreadsheetCell c3 = this.cell(
                "c3",
                "=1"
        );
        cellStore.save(c3);

        final SpreadsheetViewportWindows window = SpreadsheetViewportWindows.parse("a1:c3");

        this.loadMultipleCellRangesAndCheck(
                engine,
                window.cellRanges(),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                a1,
                                                1 + 100
                                        ),
                                        this.formatCell(
                                                b2,
                                                1 + 100 + 10
                                        ),
                                        this.formatCell(
                                                c3,
                                                1
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("A,B,C")
                        ).setRowHeights(
                                rowHeights("1,2,3")
                        ).setColumnCount(
                                OptionalInt.of(3)
                        ).setRowCount(
                                OptionalInt.of(3)
                        ).setWindow(window)
        );
    }

    @Test
    public void testLoadMultipleCellRangesWithLabels() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCell c3 = this.cell("c3", "=1");
        cellStore.save(c3);

        final SpreadsheetCell d4 = this.cell("D4", "=2");
        cellStore.save(d4);

        final SpreadsheetLabelStore labelStore = context.storeRepository()
                .labels();

        final SpreadsheetLabelName label = SpreadsheetLabelName.labelName("LabelD4");

        labelStore.save(
                label.setLabelMappingReference(
                        d4.reference()
                )
        );

        final SpreadsheetViewportWindows window = SpreadsheetViewportWindows.parse("c3:d4");

        this.loadMultipleCellRangesAndCheck(
                engine,
                window.cellRanges(),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                c3,
                                                1
                                        ),
                                        this.formatCell(
                                                d4,
                                                2
                                        )
                                )
                        )
                        .setWindow(window)
                        .setLabels(
                                Sets.of(
                                        label.setLabelMappingReference(d4.reference())
                                )
                        ).setReferences(
                                references("D4=LabelD4")
                        ).setColumnWidths(
                                columnWidths("C,D")
                        ).setRowHeights(
                                rowHeights("3,4")
                        ).setColumnCount(
                                OptionalInt.of(4)
                        ).setRowCount(
                                OptionalInt.of(4)
                        )
        );
    }

    @Test
    public void testLoadMultipleCellRangesOnlyLabelsToCell() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelStore labelStore = context.storeRepository()
                .labels();

        final SpreadsheetLabelName label = SpreadsheetLabelName.labelName("LabelC3");
        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("c3");
        final SpreadsheetLabelMapping mapping = labelStore.save(
                label.setLabelMappingReference(c3)
        );

        final SpreadsheetViewportWindows window = SpreadsheetViewportWindows.parse("b2:d4");

        this.loadMultipleCellRangesAndCheck(
                engine,
                window.cellRanges(),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY
                        .setWindow(window)
                        .setColumnCount(
                                OptionalInt.of(0)
                        ).setRowCount(
                                OptionalInt.of(0)
                        ).setLabels(
                                Sets.of(mapping)
                        )
        );
    }

    @Test
    public void testLoadMultipleCellRangesOnlyLabelsToRange() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelStore labelStore = context.storeRepository()
                .labels();

        final SpreadsheetLabelName labelC3d4 = SpreadsheetLabelName.labelName("LabelC3d4");
        final SpreadsheetCellRangeReference c3d4 = SpreadsheetSelection.parseCellRange("c3:d4");
        final SpreadsheetLabelMapping mappingC3d4 = labelStore.save(
                labelC3d4.setLabelMappingReference(c3d4)
        );

        final SpreadsheetViewportWindows window = SpreadsheetViewportWindows.parse("b2:e5");

        this.loadMultipleCellRangesAndCheck(
                engine,
                window.cellRanges(),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY
                        .setLabels(
                                Sets.of(mappingC3d4)
                        ).setColumnCount(
                                OptionalInt.of(0)
                        ).setRowCount(
                                OptionalInt.of(0)
                        ).setWindow(window)
        );
    }

    @Test
    public void testLoadMultipleCellRangesOnlyLabelsToCellAndRange() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelStore labelStore = context.storeRepository()
                .labels();

        final SpreadsheetLabelName labelC3 = SpreadsheetLabelName.labelName("LabelC3");
        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("c3");
        final SpreadsheetLabelMapping mappingC3 = labelStore.save(labelC3.setLabelMappingReference(c3));

        final SpreadsheetLabelName labelC3d4 = SpreadsheetLabelName.labelName("LabelC3d4");
        final SpreadsheetCellRangeReference c3d4 = SpreadsheetSelection.parseCellRange("c3:d4");
        final SpreadsheetLabelMapping mappingC3d4 = labelStore.save(
                labelC3d4.setLabelMappingReference(c3d4)
        );

        final SpreadsheetViewportWindows window = SpreadsheetViewportWindows.parse("b2:e5");

        this.loadMultipleCellRangesAndCheck(
                engine,
                window.cellRanges(),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY
                        .setLabels(
                                Sets.of(
                                        mappingC3,
                                        mappingC3d4
                                )
                        ).setColumnCount(
                                OptionalInt.of(0)
                        ).setRowCount(
                                OptionalInt.of(0)
                        ).setWindow(window)
        );
    }

    @Test
    public void testLoadMultipleCellRangesWithLabelsLabelWithoutCell() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCell c3 = this.cell("c3", "=1");
        cellStore.save(c3);

        final SpreadsheetLabelStore labelStore = context.storeRepository()
                .labels();

        final SpreadsheetLabelName label = SpreadsheetLabelName.labelName("LabelD4");
        final SpreadsheetCellReference d4 = SpreadsheetSelection.parseCell("d4");
        labelStore.save(
                label.setLabelMappingReference(d4)
        );

        final SpreadsheetViewportWindows window = SpreadsheetViewportWindows.parse("c3:d4");

        this.loadMultipleCellRangesAndCheck(
                engine,
                window.cellRanges(),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                c3,
                                                1
                                        )
                                )
                        ).setLabels(
                                Sets.of(
                                        label.setLabelMappingReference(d4)
                                )
                        ).setColumnWidths(
                                columnWidths("C")
                        ).setRowHeights(
                                rowHeights("3")
                        ).setColumnCount(
                                OptionalInt.of(3)
                        ).setRowCount(
                                OptionalInt.of(3)
                        ).setWindow(window)
        );
    }

    @Test
    public void testLoadMultipleCellRangesFiltersHiddenColumns() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        // this must not appear in the loaded result because column:B is hidden.
        final SpreadsheetCell b2 = this.cell(
                "b2",
                "=2"
        );
        cellStore.save(b2);

        final SpreadsheetCell c3 = this.cell(
                "c3",
                "=3"
        );
        cellStore.save(c3);

        final SpreadsheetCell d4 = this.cell(
                "d4",
                "=4"
        );
        cellStore.save(d4);

        final SpreadsheetViewportWindows window = SpreadsheetViewportWindows.parse("a1:c3");

        final SpreadsheetColumn bHidden = b2.reference()
                .column()
                .column()
                .setHidden(true);
        engine.saveColumn(
                bHidden,
                context
        );

        final SpreadsheetColumn c = c3.reference()
                .column()
                .column()
                .setHidden(false);
        engine.saveColumn(
                c,
                context
        );

        this.loadMultipleCellRangesAndCheck(
                engine,
                window.cellRanges(),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                c3,
                                                3
                                        )
                                )
                        ).setColumns(
                                Sets.of(
                                        bHidden,
                                        c
                                )
                        ).setColumnWidths(
                                columnWidths("C")
                        ).setRowHeights(
                                rowHeights("2,3")
                        ).setColumnCount(
                                OptionalInt.of(4)
                        ).setRowCount(
                                OptionalInt.of(4)
                        ).setWindow(window)
        );
    }

    @Test
    public void testLoadMultipleCellRangesFiltersHiddenRows() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        // this must not appear in the loaded result because row:2 is hidden.
        final SpreadsheetCell b2 = this.cell(
                "b2",
                "=2"
        );
        cellStore.save(b2);

        final SpreadsheetCell c3 = this.cell(
                "c3",
                "=3"
        );
        cellStore.save(c3);

        final SpreadsheetCell d4 = this.cell(
                "d4",
                "=4"
        );
        cellStore.save(d4);

        final SpreadsheetViewportWindows window = SpreadsheetViewportWindows.parse("a1:c3");

        final SpreadsheetRow row2Hidden = b2.reference()
                .row()
                .row()
                .setHidden(true);
        engine.saveRow(
                row2Hidden,
                context
        );

        final SpreadsheetRow row3 = c3.reference()
                .row()
                .row()
                .setHidden(false);
        engine.saveRow(
                row3,
                context
        );

        this.loadMultipleCellRangesAndCheck(
                engine,
                window.cellRanges(),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                c3,
                                                3
                                        )
                                )
                        ).setRows(
                                Sets.of(
                                        row2Hidden,
                                        row3
                                )
                        ).setColumnWidths(
                                columnWidths("B,C")
                        ).setRowHeights(
                                rowHeights("3")
                        ).setColumnCount(
                                OptionalInt.of(4)
                        ).setRowCount(
                                OptionalInt.of(4)
                        ).setWindow(window)
        );
    }

    // fillCells........................................................................................................

    // fill deletes.....................................................................................................

    @Test
    public void testFillCellsDeleteOneCell() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference f6 = SpreadsheetSelection.parseCell("$F$6");
        final SpreadsheetCell cell = this.cell(
                f6,
                "=1+0"
        );

        cellStore.save(cell);

        final SpreadsheetCellRangeReference f6f6 = f6.cellRange(f6);

        this.fillCellsAndCheck(
                engine,
                SpreadsheetDelta.NO_CELLS,
                f6f6,
                f6f6,
                context,
                SpreadsheetDelta.EMPTY
                        .setDeletedCells(
                                Sets.of(f6)
                        ).setColumnWidths(
                                columnWidths("F")
                        ).setRowHeights(
                                rowHeights("6")
                        ).setColumnCount(
                                OptionalInt.of(0)
                        ).setRowCount(
                                OptionalInt.of(0)
                        )
        );

        this.countAndCheck(
                cellStore,
                0
        ); // a deleted
    }

    @Test
    public void testFillCellsDeleteOneCell2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference f6 = SpreadsheetSelection.parseCell("$F$6");
        final SpreadsheetCell cellF6 = this.cell(
                f6,
                "=1+0"
        );
        cellStore.save(cellF6);

        final SpreadsheetCellReference k11 = SpreadsheetSelection.parseCell("$K$11");
        final SpreadsheetCell cellK11 = this.cell(
                k11,
                "=2+0"
        );
        cellStore.save(cellK11);

        final SpreadsheetCellRangeReference f6f6 = f6.cellRange(f6);

        this.fillCellsAndCheck(
                engine,
                SpreadsheetDelta.NO_CELLS,
                f6f6,
                f6f6,
                context,
                SpreadsheetDelta.EMPTY
                        .setDeletedCells(
                                Sets.of(f6)
                        ).setColumnWidths(
                                columnWidths("F")
                        ).setRowHeights(
                                rowHeights("6")
                        ).setColumnCount(
                                OptionalInt.of(11)
                        ).setRowCount(
                                OptionalInt.of(11)
                        )
        );

        this.countAndCheck(
                cellStore,
                1
        ); // a deleted

        this.loadCellAndCheck(
                engine,
                k11,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                k11,
                                                "=2+0",
                                                2
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("K")
                        ).setRowHeights(
                                rowHeights("11")
                        ).setColumnCount(
                                OptionalInt.of(11)
                        ).setRowCount(
                                OptionalInt.of(11)
                        )
        );
    }

    @Test
    public void testFillCellsDeletesManyCells() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference f6 = SpreadsheetSelection.parseCell("$F$6");
        final SpreadsheetCell cellF6 = this.cell(
                f6,
                "=1+0"
        );
        cellStore.save(cellF6);

        final SpreadsheetCellReference g7 = SpreadsheetSelection.parseCell("$g$7");
        final SpreadsheetCell cellG7 = this.cell(
                g7,
                "=2+0"
        );
        cellStore.save(cellG7);

        final SpreadsheetCellRangeReference rangeAtoB = f6.cellRange(g7);

        this.fillCellsAndCheck(
                engine,
                SpreadsheetDelta.NO_CELLS,
                rangeAtoB,
                rangeAtoB,
                context,
                SpreadsheetDelta.EMPTY.setDeletedCells(
                        Sets.of(f6, g7)
                ).setColumnWidths(
                        columnWidths("F,G")
                ).setRowHeights(
                        rowHeights("6,7")
                ).setColumnCount(
                        OptionalInt.of(0)
                ).setRowCount(
                        OptionalInt.of(0)
                )
        );

        this.countAndCheck(cellStore, 0); // a deleted
    }

    @Test
    public void testFillCellsDeletesManyCells2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference f6 = SpreadsheetSelection.parseCell("$F$6");
        final SpreadsheetCell cellF6 = this.cell(
                f6,
                "=1+0"
        );
        cellStore.save(cellF6);

        final SpreadsheetCellReference g7 = SpreadsheetSelection.parseCell("$g$7");
        final SpreadsheetCell cellg7 = this.cell(
                g7,
                "=2+0"
        );
        cellStore.save(cellg7);

        final SpreadsheetCellRangeReference rangeAtoB = f6.cellRange(g7);

        final SpreadsheetCellReference k11 = SpreadsheetSelection.parseCell("$K$11");
        final SpreadsheetCell cellK11 = this.cell(
                k11,
                "=3+0"
        );
        cellStore.save(cellK11);

        this.fillCellsAndCheck(
                engine,
                SpreadsheetDelta.NO_CELLS,
                rangeAtoB,
                rangeAtoB,
                context,
                SpreadsheetDelta.EMPTY
                        .setDeletedCells(
                                Sets.of(f6, g7)
                        ).setColumnWidths(
                                columnWidths("F,G")
                        ).setRowHeights(
                                rowHeights("6,7")
                        ).setColumnCount(
                                OptionalInt.of(11)
                        ).setRowCount(
                                OptionalInt.of(11)
                        )
        );

        this.countAndCheck(cellStore, 1); // a&b deleted, leaving c

        this.loadCellAndCheck(
                engine,
                k11,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                k11,
                                                "=3+0",
                                                3
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("K")
                        ).setRowHeights(
                                rowHeights("11")
                        ).setColumnCount(
                                OptionalInt.of(11)
                        ).setRowCount(
                                OptionalInt.of(11)
                        )
        );
    }

    // fill save with missing cells......................................................................................

    @Test
    public void testFillCellsSaveWithMissingCells() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("$B$2");
        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("$C$3");

        final SpreadsheetCell cellB2 = this.cell(
                b2,
                "=1+0"
        );
        final SpreadsheetCell cellC3 = this.cell(
                c3,
                "=2+0"
        );

        final SpreadsheetCellRangeReference range = b2.cellRange(c3);

        this.fillCellsAndCheck(
                engine,
                Sets.of(cellB2, cellC3),
                range,
                range,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                b2,
                                                "=1+0",
                                                1
                                        ),
                                        this.formatCell(
                                                c3,
                                                "=2+0",
                                                2
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("B,C")
                        ).setRowHeights(
                                rowHeights("2,3")
                        ).setColumnCount(
                                OptionalInt.of(3)
                        ).setRowCount(
                                OptionalInt.of(3)
                        )
        );

        this.countAndCheck(
                context.storeRepository()
                        .cells(),
                2
        ); // a + b saved

        this.loadCellAndCheck(
                engine,
                b2,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                b2,
                                                "=1+0",
                                                1
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("B")
                        ).setRowHeights(
                                rowHeights("2")
                        ).setColumnCount(
                                OptionalInt.of(3)
                        ).setRowCount(
                                OptionalInt.of(3)
                        )
        );

        this.loadCellAndCheck(
                engine,
                c3,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                c3,
                                                "=2+0",
                                                2
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("C")
                        ).setRowHeights(
                                rowHeights("3")
                        ).setColumnCount(
                                OptionalInt.of(3)
                        ).setRowCount(
                                OptionalInt.of(3)
                        )
        );
    }

    @Test
    public void testFillCellsSaveWithMissingCells2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("$B$2");
        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("$C$3");

        final SpreadsheetCell cellB2 = this.cell(
                b2,
                "=1+0"
        );
        final SpreadsheetCell cellC3 = this.cell(
                c3,
                "=2+0"
        );

        final SpreadsheetCellRangeReference range = b2.cellRange(c3);

        final SpreadsheetCellReference k11 = SpreadsheetSelection.parseCell("$K$11");
        final SpreadsheetCell cellK11 = this.cell(
                k11,
                "=3+0"
        );
        cellStore.save(cellK11);

        this.fillCellsAndCheck(
                engine,
                Sets.of(cellB2, cellC3),
                range,
                range,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                b2,
                                                "=1+0",
                                                1
                                        ),
                                        this.formatCell(
                                                c3,
                                                "=2+0",
                                                2
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("B,C")
                        ).setRowHeights(
                                rowHeights("2,3")
                        ).setColumnCount(
                                OptionalInt.of(11)
                        ).setRowCount(
                                OptionalInt.of(11)
                        )
        );

        this.countAndCheck(
                cellStore,
                3
        ); // a + b saved + c

        this.loadCellAndCheck(
                engine,
                b2,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                b2,
                                                "=1+0",
                                                1
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("B")
                        ).setRowHeights(
                                rowHeights("2")
                        ).setColumnCount(
                                OptionalInt.of(11)
                        ).setRowCount(
                                OptionalInt.of(11)
                        )
        ); // fill should have evaluated.

        this.loadCellAndCheck(
                engine,
                c3,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                c3,
                                                "=2+0",
                                                2
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("C")
                        ).setRowHeights(
                                rowHeights("3")
                        ).setColumnCount(
                                OptionalInt.of(11)
                        ).setRowCount(
                                OptionalInt.of(11)
                        )
        );

        this.loadCellAndCheck(
                engine,
                k11,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        cellK11
                                )
                        ).setColumnWidths(
                                columnWidths("K")
                        ).setRowHeights(
                                rowHeights("11")
                        ).setColumnCount(
                                OptionalInt.of(11)
                        ).setRowCount(
                                OptionalInt.of(11)
                        )
        );
    }

    // fill moves cell..................................................................................................

    @Test
    public void testFillCellsRangeOneEmptyCells2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("$B$2");
        final SpreadsheetCellReference c2 = SpreadsheetSelection.parseCell("$C$2");
        final SpreadsheetCellReference b3 = SpreadsheetSelection.parseCell("$B$3");

        final SpreadsheetCell cellB2 = this.cell(
                b2,
                "=1+0"
        );
        final SpreadsheetCell cellC2 = this.cell(
                c2,
                "=2+0"
        );
        final SpreadsheetCell cellB3 = this.cell(
                b3,
                "=3+0"
        );

        cellStore.save(cellB2);
        cellStore.save(cellC2);
        cellStore.save(cellB3);

        this.fillCellsAndCheck(
                engine,
                SpreadsheetDelta.NO_CELLS,
                b2.cellRange(b2),
                SpreadsheetCellRangeReference.bounds(Lists.of(c2)),
                context,
                SpreadsheetDelta.EMPTY
                        .setDeletedCells(
                                Sets.of(c2)
                        ).setColumnWidths(
                                columnWidths("C")
                        ).setRowHeights(
                                rowHeights("2")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(3)
                        )
        );

        this.countAndCheck(cellStore, 2); // a + c, b deleted

        this.loadCellAndCheck(
                engine,
                b2,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                b2,
                                                "=1+0",
                                                1
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("B")
                        ).setRowHeights(
                                rowHeights("2")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(3)
                        )
        );

        this.loadCellAndCheck(
                engine,
                b3,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                b3,
                                                "=3+0",
                                                3
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("B")
                        ).setRowHeights(
                                rowHeights("3")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(3)
                        )
        );
    }

    @Test
    public void testFillCellsRangeTwoEmptyCells() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("$B$2");
        final SpreadsheetCellReference c2 = SpreadsheetSelection.parseCell("$C$2");
        final SpreadsheetCellReference b3 = SpreadsheetSelection.parseCell("$B$3");

        final SpreadsheetCell cellB2 = this.cell(
                b2,
                "=1+0"
        );
        final SpreadsheetCell cellC2 = this.cell(
                c2,
                "=2+0"
        );
        final SpreadsheetCell cellB3 = this.cell(
                b3,
                "=3+0"
        );

        cellStore.save(cellB2);
        cellStore.save(cellC2);
        cellStore.save(cellB3);

        this.fillCellsAndCheck(
                engine,
                SpreadsheetDelta.NO_CELLS,
                b2.cellRange(b2),
                SpreadsheetCellRangeReference.bounds(
                        Lists.of(
                                b2,
                                c2
                        )
                ),
                context,
                SpreadsheetDelta.EMPTY
                        .setDeletedCells(
                                Sets.of(b2, c2)
                        ).setColumnWidths(
                                columnWidths("B,C")
                        ).setRowHeights(
                                rowHeights("2")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(3)
                        )
        );

        this.countAndCheck(
                cellStore,
                1
        );

        this.loadCellAndCheck(
                engine,
                b3,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(b3, "=3+0", 3)
                                )
                        ).setColumnWidths(
                                columnWidths("B")
                        ).setRowHeights(
                                rowHeights("3")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(3)
                        )
        );
    }

    // fill moves 1 cell................................................................................................

    @Test
    public void testFillCellsAddition() {
        this.fillCellsAndCheck(
                "=1+0",
                1 + 0
        );
    }

    @Test
    public void testFillCellsExpressionNumber() {
        this.fillCellsAndCheck(
                "=99.5",
                99.5
        );
    }

    @Test
    public void testFillCellsExpressionNumber2() {
        this.fillCellsAndCheck(
                "=99",
                99
        );
    }

    @Test
    public void testFillCellsDivision() {
        this.fillCellsAndCheck(
                "=10/5",
                10 / 5
        );
    }

    @Test
    public void testFillCellsEqualsTrue() {
        this.fillCellsAndCheck(
                "=10=10",
                true
        );
    }

    @Test
    public void testFillCellsEqualsFalse() {
        this.fillCellsAndCheck(
                "=10=9",
                false
        );
    }

    @Test
    public void testFillCellsGreaterThanTrue() {
        this.fillCellsAndCheck(
                "=10>9",
                true
        );
    }

    @Test
    public void testFillCellsGreaterThanFalse() {
        this.fillCellsAndCheck(
                "=10>11",
                false
        );
    }

    @Test
    public void testFillCellsGreaterThanEqualsTrue() {
        this.fillCellsAndCheck(
                "=10>=10",
                true
        );
    }

    @Test
    public void testFillCellsGreaterThanEqualsFalse() {
        this.fillCellsAndCheck(
                "=10>=11",
                false
        );
    }

    @Test
    public void testFillCellsFunction() {
        this.fillCellsAndCheck(
                "=BasicSpreadsheetEngineTestSum(1,99)",
                1 + 99
        );
    }

    @Test
    public void testFillCellsGroup() {
        this.fillCellsAndCheck(
                "=(99)",
                99
        );
    }

    @Test
    public void testFillCellsLessThanTrue() {
        this.fillCellsAndCheck(
                "=10<11",
                true
        );
    }

    @Test
    public void testFillCellsLessThanFalse() {
        this.fillCellsAndCheck(
                "=10<9",
                false
        );
    }

    @Test
    public void testFillCellsLessThanEqualsTrue() {
        this.fillCellsAndCheck(
                "=10<=10",
                true
        );
    }

    @Test
    public void testFillCellsLessThanEqualsFalse() {
        this.fillCellsAndCheck(
                "=10<=9",
                false
        );
    }

    @Test
    public void testFillCellsMultiplication() {
        this.fillCellsAndCheck(
                "=6*7",
                6 * 7
        );
    }

    @Test
    public void testFillCellsNegative() {
        this.fillCellsAndCheck(
                "=-123",
                -123
        );
    }

    @Test
    public void testFillCellsNotEqualsTrue() {
        this.fillCellsAndCheck(
                "=10<>9",
                true
        );
    }

    @Test
    public void testFillCellsNotEqualsFalse() {
        this.fillCellsAndCheck(
                "=10<>10",
                false
        );
    }

    @Test
    public void testFillCellsPercentage() {
        this.fillCellsAndCheck(
                "=123.5%",
                123.5 / 100
        );
    }

    @Test
    public void testFillCellsSubtraction() {
        this.fillCellsAndCheck(
                "=13-4",
                13 - 4
        );
    }

    @Test
    public void testFillCellsText() {
        this.fillCellsAndCheck(
                "=\"abc123\"",
                "abc123"
        );
    }

    @Test
    public void testFillCellsAdditionWithWhitespace() {
        this.fillCellsAndCheck(
                "=1 + 2",
                1 + 2
        );
    }

    private void fillCellsAndCheck(final String formulaText, final Object expected) {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference k21 = SpreadsheetSelection.parseCell("$K$21");
        final SpreadsheetCellReference l22 = SpreadsheetSelection.parseCell("$L$22");
        final SpreadsheetCellReference m23 = SpreadsheetSelection.parseCell("$M$23");

        final SpreadsheetCell cellK21 = this.cell(
                k21,
                formulaText
        );
        final SpreadsheetCell cellL22 = this.cell(
                l22,
                "=2+0"
        );
        final SpreadsheetCell cellM23 = this.cell(
                m23,
                "=3+0"
        );

        cellStore.save(cellK21);
        cellStore.save(cellL22);
        cellStore.save(cellM23);

        final SpreadsheetCellReference ae41 = SpreadsheetSelection.parseCell("$AE$41");

        this.fillCellsAndCheck(
                engine,
                Lists.of(cellK21),
                k21.cellRange(k21),
                ae41.cellRange(ae41),
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                ae41,
                                                formulaText,
                                                expected
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("AE")
                        ).setRowHeights(
                                rowHeights("41")
                        ).setColumnCount(
                                OptionalInt.of(31)
                        ).setRowCount(
                                OptionalInt.of(41)
                        )
        );

        this.countAndCheck(cellStore, 3 + 1);
    }

    @Test
    public void testFillCellsRepeatCellInto2x2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference k21 = SpreadsheetSelection.parseCell("$K$21");
        final SpreadsheetCellReference l22 = SpreadsheetSelection.parseCell("$L$22");
        final SpreadsheetCellReference m23 = SpreadsheetSelection.parseCell("$M$23");

        final SpreadsheetCell cellK21 = this.cell(
                k21,
                "=1+0"
        );
        final SpreadsheetCell cellL22 = this.cell(
                l22,
                "=2+0"
        );
        final SpreadsheetCell cellM23 = this.cell(
                m23,
                "=3+0"
        );

        cellStore.save(cellK21);
        cellStore.save(cellL22);
        cellStore.save(cellM23);

        final SpreadsheetCellReference ae41 = SpreadsheetSelection.parseCell("$AE$41");

        this.fillCellsAndCheck(
                engine,
                Lists.of(cellK21, cellL22),
                SpreadsheetCellRangeReference.bounds(Lists.of(k21, l22)),
                ae41.cellRange(ae41.add(2, 2)),
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                ae41,
                                                "=1+0",
                                                1 + 0
                                        ),
                                        this.formatCell(
                                                ae41.add(1, 1),
                                                "=2+0",
                                                2 + 0
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("AE,AF")
                        ).setRowHeights(
                                rowHeights("41,42")
                        ).setColumnCount(
                                OptionalInt.of(32)
                        ).setRowCount(
                                OptionalInt.of(42)
                        )
        );

        this.countAndCheck(cellStore, 3 + 2);
    }

    @Test
    public void testFillCells2x2CellInto1x1() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference k21 = SpreadsheetSelection.parseCell("$K$21");
        final SpreadsheetCellReference l22 = SpreadsheetSelection.parseCell("$L$22");
        final SpreadsheetCellReference m23 = SpreadsheetSelection.parseCell("$M$23");

        final SpreadsheetCell cellK21 = this.cell(
                k21,
                "=1+0"
        );
        final SpreadsheetCell cellL22 = this.cell(
                l22,
                "=2+0"
        );
        final SpreadsheetCell cellM23 = this.cell(
                m23,
                "=3+0"
        );

        cellStore.save(cellK21);
        cellStore.save(cellL22);
        cellStore.save(cellM23);

        final SpreadsheetCellReference d = SpreadsheetSelection.parseCell("$AE$41");

        this.fillCellsAndCheck(
                engine,
                Lists.of(
                        cellK21,
                        cellL22
                ),
                SpreadsheetCellRangeReference.bounds(
                        Lists.of(
                                k21,
                                l22
                        )
                ),
                d.cellRange(
                        d.add(1, 1)
                ),
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                d,
                                                "=1+0",
                                                1 + 0
                                        ),
                                        this.formatCell(
                                                d.add(1, 1),
                                                "=2+0",
                                                2 + 0
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("AE,AF")
                        ).setRowHeights(
                                rowHeights("41,42")
                        ).setColumnCount(
                                OptionalInt.of(32)
                        ).setRowCount(
                                OptionalInt.of(42)
                        )
        );

        this.countAndCheck(
                cellStore,
                3 + 2
        );
    }

    @Test
    public void testFillCells2x2Into2x2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference k21 = SpreadsheetSelection.parseCell("$K$21");
        final SpreadsheetCellReference l22 = SpreadsheetSelection.parseCell("$L$22");
        final SpreadsheetCellReference m23 = SpreadsheetSelection.parseCell("$M$23");

        final SpreadsheetCell cellK21 = this.cell(
                k21,
                "=1+0"
        );
        final SpreadsheetCell cellL22 = this.cell(
                l22,
                "=2+0"
        );
        final SpreadsheetCell cellM23 = this.cell(
                m23,
                "=3+0"
        );

        cellStore.save(cellK21);
        cellStore.save(cellL22);
        cellStore.save(cellM23);

        final SpreadsheetCellReference d = SpreadsheetSelection.parseCell("$AE$41");

        this.fillCellsAndCheck(
                engine,
                Lists.of(cellK21, cellL22),
                SpreadsheetCellRangeReference.bounds(Lists.of(k21, l22)),
                d.cellRange(d.add(2, 2)),
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                d,
                                                "=1+0",
                                                1 + 0
                                        ),
                                        this.formatCell(
                                                d.add(1, 1),
                                                "=2+0",
                                                2 + 0
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("AE,AF")
                        ).setRowHeights(
                                rowHeights("41,42")
                        ).setColumnCount(
                                OptionalInt.of(32)
                        ).setRowCount(
                                OptionalInt.of(42)
                        )
        );

        this.countAndCheck(
                cellStore,
                3 + 2
        );
    }

    @Test
    public void testFillCells2x2Into7x2Gives6x2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference k21 = SpreadsheetSelection.parseCell("$K$21");
        final SpreadsheetCellReference l22 = SpreadsheetSelection.parseCell("$L$22");
        final SpreadsheetCellReference m23 = SpreadsheetSelection.parseCell("$M$23");

        final SpreadsheetCell cellK21 = this.cell(
                k21,
                "=1+0"
        );
        final SpreadsheetCell cellL22 = this.cell(
                l22,
                "=2+0"
        );
        final SpreadsheetCell cellM23 = this.cell(
                m23,
                "=3+0"
        );

        cellStore.save(cellK21);
        cellStore.save(cellL22);
        cellStore.save(cellM23);

        final SpreadsheetCellReference ae41 = SpreadsheetSelection.parseCell("$AE$41");

        this.fillCellsAndCheck(
                engine,
                Lists.of(
                        cellK21,
                        cellL22
                ),
                SpreadsheetCellRangeReference.bounds(
                        Lists.of(
                                k21,
                                l22
                        )
                ),
                ae41.cellRange(
                        ae41.add(6, 1)
                ),
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                ae41,
                                                "=1+0",
                                                1 + 0
                                        ),
                                        this.formatCell(
                                                ae41.add(1, 1),
                                                "=2+0",
                                                2 + 0
                                        ),
                                        this.formatCell(
                                                ae41.add(2, 0),
                                                "=1+0",
                                                1 + 0
                                        ),
                                        this.formatCell(
                                                ae41.add(3, 1),
                                                "=2+0",
                                                2 + 0
                                        ),
                                        this.formatCell(
                                                ae41.add(4, 0),
                                                "=1+0",
                                                1 + 0
                                        ),
                                        this.formatCell(
                                                ae41.add(5, 1),
                                                "=2+0",
                                                2 + 0
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("AE,AF,AG,AH,AI,AJ")
                        ).setRowHeights(
                                rowHeights("41,42")
                        ).setColumnCount(
                                OptionalInt.of(36)
                        ).setRowCount(
                                OptionalInt.of(42)
                        )
        );

        this.countAndCheck(
                cellStore,
                3 + 6
        );
    }

    @Test
    public void testFillCells2x2Into2x7Gives2x6() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference k21 = SpreadsheetSelection.parseCell("$K$21");
        final SpreadsheetCellReference l22 = SpreadsheetSelection.parseCell("$L$22");
        final SpreadsheetCellReference m23 = SpreadsheetSelection.parseCell("$M$23");

        final SpreadsheetCell cellK21 = this.cell(
                k21,
                "=1+0"
        );
        final SpreadsheetCell cellL22 = this.cell(
                l22,
                "=2+0"
        );
        final SpreadsheetCell cellM23 = this.cell(
                m23,
                "=3+0"
        );

        cellStore.save(cellK21);
        cellStore.save(cellL22);
        cellStore.save(cellM23);

        final SpreadsheetCellReference d = SpreadsheetSelection.parseCell("$AE$41");

        this.fillCellsAndCheck(
                engine,
                Lists.of(
                        cellK21,
                        cellL22
                ),
                SpreadsheetCellRangeReference.bounds(
                        Lists.of(
                                k21,
                                l22
                        )
                ),
                d.cellRange(
                        d.add(1, 6)
                ),
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                d,
                                                "=1+0",
                                                1 + 0
                                        ),
                                        this.formatCell(
                                                d.add(1, 1),
                                                "=2+0",
                                                2 + 0
                                        ),
                                        this.formatCell(
                                                d.addRow(2),
                                                "=1+0",
                                                1 + 0
                                        ),
                                        this.formatCell(
                                                d.add(1, 3),
                                                "=2+0",
                                                2 + 0
                                        ),
                                        this.formatCell(
                                                d.addRow(4),
                                                "=1+0",
                                                1 + 0
                                        ),
                                        this.formatCell(
                                                d.add(1, 5),
                                                "=2+0",
                                                2 + 0
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("AE,AF")
                        ).setRowHeights(
                                rowHeights("41,42,43,44,45,46")
                        ).setColumnCount(
                                OptionalInt.of(32)
                        ).setRowCount(
                                OptionalInt.of(46)
                        )
        );

        this.countAndCheck(cellStore, 3 + 6);
    }

    @Test
    public void testFillCellsAbsoluteCellReference() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference k21 = SpreadsheetSelection.parseCell("$K$21");
        final SpreadsheetCellReference l22 = SpreadsheetSelection.parseCell("$L$22");

        final SpreadsheetCell cellK21 = this.cell(
                k21,
                "=1+0"
        );
        final SpreadsheetCell cellL22 = this.cell(
                l22,
                "=" + k21
        );

        cellStore.save(cellK21);
        cellStore.save(cellL22);

        final SpreadsheetCellReference d = SpreadsheetSelection.parseCell("$AE$41");

        this.fillCellsAndCheck(
                engine,
                Lists.of(cellL22),
                l22.cellRange(l22),
                d.cellRange(d),
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                k21,
                                                "=1+0",
                                                1 + 0
                                        ),
                                        this.formatCell(
                                                d,
                                                "=" + k21,
                                                1 + 0
                                        )
                                )
                        ).setReferences(references("$K$21=AE41")
                        ).setColumnWidths(
                                columnWidths("K,AE")
                        ).setRowHeights(
                                rowHeights("21,41")
                        ).setColumnCount(
                                OptionalInt.of(31)
                        ).setRowCount(
                                OptionalInt.of(41)
                        )
        );

        this.countAndCheck(cellStore, 2 + 1);
    }

    @Test
    public void testFillCellsExpressionRelativeCellReferenceFixed() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCell cellB = this.cell(
                "B2",
                "=2"
        );
        final SpreadsheetCell cellC = this.cell(
                "C3",
                "=3+B2"
        );

        cellStore.save(cellB);
        cellStore.save(cellC);

        this.fillCellsAndCheck(
                engine,
                Lists.of(
                        cellB,
                        cellC
                ),
                cellB.reference()
                        .cellRange(
                                cellC.reference()
                        ),
                SpreadsheetSelection.parseCellRange("E5:F6"),
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "E5",
                                                "=2",
                                                2 + 0
                                        ),
                                        this.formatCell(
                                                "F6",
                                                "=3+E5",
                                                3 + 2
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("E,F")
                        ).setRowHeights(
                                rowHeights("5,6")
                        ).setColumnCount(
                                OptionalInt.of(6)
                        ).setRowCount(
                                OptionalInt.of(6)
                        )
        );

        this.countAndCheck(cellStore, 2 + 2);
    }

    @Test
    public void testFillCellsExternalCellReferencesRefreshed() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference b = SpreadsheetSelection.parseCell("b1");
        final SpreadsheetCell cellB = this.cell(
                b,
                "=2+0"
        ); // copied to C1
        final SpreadsheetCellReference c = SpreadsheetSelection.parseCell("C1"); // fillCells dest...
        final SpreadsheetCell cellA = this.cell(
                "a1",
                "=10+" + c
        );

        engine.saveCell(
                cellA,
                context
        );
        engine.saveCell(
                cellB,
                context
        );

        this.fillCellsAndCheck(
                engine,
                Lists.of(cellB),
                b.cellRange(b),
                c.cellRange(c),
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                cellA.reference(),
                                                "=10+" + c,
                                                10 + 2 + 0
                                        ), // external reference to copied
                                        this.formatCell(
                                                c,
                                                "=2+0",
                                                2 + 0
                                        )
                                )
                        ).setReferences(references("C1=A1")
                        ).setColumnWidths(
                                columnWidths("A,C")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(3)
                        ).setRowCount(
                                OptionalInt.of(1)
                        )
        ); // copied

        this.countAndCheck(cellStore, 2 + 1);
    }

    @Test
    public void testFillCellsWithColumns() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repo = context.storeRepository();

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("b2");
        final SpreadsheetCell cellB2 = this.cell(b2, "");

        final SpreadsheetColumnStore columnStore = repo.columns();
        final SpreadsheetColumn b = b2.column()
                .column();
        columnStore.save(b);

        final SpreadsheetColumn c = SpreadsheetSelection.parseColumn("c")
                .column();
        columnStore.save(c);

        final SpreadsheetCellStore cellStore = repo.cells();

        engine.saveCell(cellB2, context);

        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("c3");

        this.fillCellsAndCheck(
                engine,
                Lists.of(cellB2),
                b2.cellRange(b2),
                c3.cellRange(c3),
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                c3,
                                                ""
                                        )
                                )
                        )
                        .setColumns(
                                Sets.of(c)
                        ).setColumnWidths(
                                columnWidths("C")
                        ).setRowHeights(
                                rowHeights("3")
                        ).setColumnCount(
                                OptionalInt.of(3)
                        ).setRowCount(
                                OptionalInt.of(3)
                        )
        ); // copied

        this.countAndCheck(cellStore, 1 + 1);
    }

    @Test
    public void testFillCellsWithRows() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repo = context.storeRepository();

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("b2");
        final SpreadsheetCell cellB2 = this.cell(b2, "");

        final SpreadsheetRowStore rowStore = repo.rows();
        final SpreadsheetRow row2 = b2.row()
                .row();
        rowStore.save(row2);

        final SpreadsheetRow row3 = SpreadsheetSelection.parseRow("3")
                .row();
        rowStore.save(row3);

        final SpreadsheetCellStore cellStore = repo.cells();

        engine.saveCell(cellB2, context);

        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("c3");

        this.fillCellsAndCheck(
                engine,
                Lists.of(cellB2),
                b2.cellRange(b2),
                c3.cellRange(c3),
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                c3,
                                                ""
                                        )
                                )
                        )
                        .setRows(
                                Sets.of(row3)
                        ).setColumnWidths(
                                columnWidths("C")
                        ).setRowHeights(
                                rowHeights("3")
                        ).setColumnCount(
                                OptionalInt.of(3)
                        ).setRowCount(
                                OptionalInt.of(3)
                        )
        ); // copied

        this.countAndCheck(cellStore, 1 + 1);
    }

    // filterCells......................................................................................................

    @Test
    public void testFilterCells() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1
                .setFormula(
                        SpreadsheetFormula.EMPTY.setText("true")
                );
        final SpreadsheetCell b2 = SpreadsheetSelection.A1
                .setFormula(
                        SpreadsheetFormula.EMPTY.setText("false")
                );

        this.filterCellsAndCheck(
                this.createSpreadsheetEngine(),
                Sets.of(
                        a1,
                        b2
                ),
                SpreadsheetValueType.ANY,
                Expression.call(
                        Expression.namedFunction(
                                ExpressionFunctionName.with(TEST_FILTER_CELLS_PREDICATE)
                                        .setCaseSensitivity(SpreadsheetExpressionFunctionNames.CASE_SENSITIVITY)
                        ),
                        Expression.NO_CHILDREN
                ),
                this.createContext(),
                a1
        );
    }

    // findCells........................................................................................................

    @Test
    public void testFindCells() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a2 = SpreadsheetSelection.parseCell("a2");
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("b2");
        final SpreadsheetCellReference c2 = SpreadsheetSelection.parseCell("c2");

        final SpreadsheetCellReference a3 = SpreadsheetSelection.parseCell("a3");
        final SpreadsheetCellReference b3 = SpreadsheetSelection.parseCell("b3");
        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("c3");

        final SpreadsheetCellReference a4 = SpreadsheetSelection.parseCell("a4");
        final SpreadsheetCellReference b4 = SpreadsheetSelection.parseCell("b4");
        final SpreadsheetCellReference c4 = SpreadsheetSelection.parseCell("c4");

        final SpreadsheetDelta saved = engine.saveCells(
                Sets.of(
                        SpreadsheetSelection.parseCell("B1")
                                .setFormula(
                                        SpreadsheetFormula.EMPTY.setText("=-999")
                                ),
                        a2.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=1")
                        ),
                        b2.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=2")
                        ),
                        c2.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=3")
                        ),
                        a3.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=4")
                        ),
                        b3.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=5")
                        ),
                        c3.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=6")
                        ),
                        a4.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=7")
                        ),
                        b4.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=8")
                        ),
                        c4.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=9")
                        )
                ),
                context
        );

        this.findCellsAndCheck(
                engine,
                SpreadsheetSelection.parseCellRange("A2:C4"),
                SpreadsheetCellRangeReferencePath.LRTD,
                0, // offset
                10, // count
                SpreadsheetValueType.ANY,
                Expression.value(true), // match everything
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY.setCells(
                                Sets.of(
                                        saved.cell(a2).get(),
                                        saved.cell(b2).get(),
                                        saved.cell(c2).get(),
                                        saved.cell(a3).get(),
                                        saved.cell(b3).get(),
                                        saved.cell(c3).get(),
                                        saved.cell(a4).get(),
                                        saved.cell(b4).get(),
                                        saved.cell(c4).get()
                                )
                        ).setColumnWidths(columnWidths("A,B,C"))
                        .setRowHeights(rowHeights("2,3,4"))
                        .setColumnCount(OptionalInt.of(3))
                        .setRowCount(OptionalInt.of(4))
        );
    }

    @Test
    public void testFindCellsSkipsMissingCells() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a2 = SpreadsheetSelection.parseCell("a2");
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("b2");
        final SpreadsheetCellReference c2 = SpreadsheetSelection.parseCell("c2");

        final SpreadsheetDelta saved = engine.saveCells(
                Sets.of(
                        a2.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=1")
                        ),
                        b2.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=2")
                        ),
                        c2.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=3")
                        )
                ),
                context
        );

        this.findCellsAndCheck(
                engine,
                SpreadsheetSelection.parseCellRange("A1:C2"),
                SpreadsheetCellRangeReferencePath.LRTD,
                0, // offset
                20, // count
                SpreadsheetValueType.ANY,
                Expression.value(true), // match everything
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY.setCells(
                                Sets.of(
                                        saved.cell(a2).get(),
                                        saved.cell(b2).get(),
                                        saved.cell(c2).get()
                                )
                        ).setColumnWidths(columnWidths("A,B,C"))
                        .setRowHeights(rowHeights("2"))
                        .setColumnCount(OptionalInt.of(3))
                        .setRowCount(OptionalInt.of(2))
        );
    }

    @Test
    public void testFindCellsSkipsEmptyCells() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a2 = SpreadsheetSelection.parseCell("a2");
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("b2");
        final SpreadsheetCellReference c2 = SpreadsheetSelection.parseCell("c2");

        final SpreadsheetDelta saved = engine.saveCells(
                Sets.of(
                        a2.setFormula(
                                SpreadsheetFormula.EMPTY.setText("")
                        ),
                        b2.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=2")
                        ),
                        c2.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=3")
                        )
                ),
                context
        );

        this.findCellsAndCheck(
                engine,
                SpreadsheetSelection.parseCellRange("A1:C2"),
                SpreadsheetCellRangeReferencePath.LRTD,
                0, // offset
                20, // count
                SpreadsheetValueType.ANY,
                Expression.value(true), // match everything
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY.setCells(
                                Sets.of(
                                        saved.cell(b2).get(),
                                        saved.cell(c2).get()
                                )
                        ).setColumnWidths(columnWidths("B,C"))
                        .setRowHeights(rowHeights("2"))
                        .setColumnCount(OptionalInt.of(3))
                        .setRowCount(OptionalInt.of(2))
        );
    }

    @Test
    public void testFindCellsWithCount() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a2 = SpreadsheetSelection.parseCell("a2");
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("b2");
        final SpreadsheetCellReference c2 = SpreadsheetSelection.parseCell("c2");

        final SpreadsheetCellReference a3 = SpreadsheetSelection.parseCell("a3");
        final SpreadsheetCellReference b3 = SpreadsheetSelection.parseCell("b3");
        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("c3");

        final SpreadsheetCellReference a4 = SpreadsheetSelection.parseCell("a4");
        final SpreadsheetCellReference b4 = SpreadsheetSelection.parseCell("b4");
        final SpreadsheetCellReference c4 = SpreadsheetSelection.parseCell("c4");

        final SpreadsheetDelta saved = engine.saveCells(
                Sets.of(
                        SpreadsheetSelection.parseCell("B1")
                                .setFormula(
                                        SpreadsheetFormula.EMPTY.setText("=-999")
                                ),
                        a2.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=1")
                        ),
                        b2.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=2")
                        ),
                        c2.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=3")
                        ),
                        a3.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=4")
                        ),
                        b3.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=5")
                        ),
                        c3.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=6")
                        ),
                        a4.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=7")
                        ),
                        b4.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=8")
                        ),
                        c4.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=9")
                        )
                ),
                context
        );

        this.findCellsAndCheck(
                engine,
                SpreadsheetSelection.parseCellRange("A2:C4"),
                SpreadsheetCellRangeReferencePath.LRTD,
                0, // offset
                3, // count
                SpreadsheetValueType.ANY,
                Expression.value(true), // match everything
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY.setCells(
                                Sets.of(
                                        saved.cell(a2).get(),
                                        saved.cell(b2).get(),
                                        saved.cell(c2).get()
                                )
                        ).setColumnWidths(columnWidths("A,B,C"))
                        .setRowHeights(rowHeights("2"))
                        .setColumnCount(OptionalInt.of(3))
                        .setRowCount(OptionalInt.of(4))
        );
    }

    @Test
    public void testFindCellsWithValueTypeFiltering() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a2 = SpreadsheetSelection.parseCell("a2");
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("b2");
        final SpreadsheetCellReference c2 = SpreadsheetSelection.parseCell("c2");

        final SpreadsheetCellReference a3 = SpreadsheetSelection.parseCell("a3");
        final SpreadsheetCellReference b3 = SpreadsheetSelection.parseCell("b3");
        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("c3");

        final SpreadsheetCellReference a4 = SpreadsheetSelection.parseCell("a4");
        final SpreadsheetCellReference b4 = SpreadsheetSelection.parseCell("b4");
        final SpreadsheetCellReference c4 = SpreadsheetSelection.parseCell("c4");

        final SpreadsheetDelta saved = engine.saveCells(
                Sets.of(
                        SpreadsheetSelection.parseCell("B1")
                                .setFormula(
                                        SpreadsheetFormula.EMPTY.setText("=-999")
                                                .setValue(
                                                        Optional.of(999)
                                                )
                                ),
                        a2.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=1")
                                        .setValue(
                                                Optional.of(1)
                                        )
                        ),
                        b2.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=\"Hello2\"")
                                        .setValue(
                                                Optional.of("Hello2")
                                        )
                        ),
                        c2.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=\"Hello3\"")
                                        .setValue(
                                                Optional.of("Hello3")
                                        )
                        ),
                        a3.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=4")
                                        .setValue(
                                                Optional.of(4)
                                        )
                        ),
                        b3.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=5")
                                        .setValue(
                                                Optional.of(5)
                                        )
                        ),
                        c3.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=6")
                                        .setValue(
                                                Optional.of(6)
                                        )
                        ),
                        a4.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=7")
                                        .setValue(
                                                Optional.of(7)
                                        )
                        ),
                        b4.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=8")
                                        .setValue(
                                                Optional.of(8)
                                        )
                        ),
                        c4.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=9")
                                        .setValue(
                                                Optional.of(9)
                                        )
                        )
                ),
                context
        );

        this.findCellsAndCheck(
                engine,
                SpreadsheetSelection.parseCellRange("A2:C4"),
                SpreadsheetCellRangeReferencePath.LRTD,
                0, // offset
                10, // count
                SpreadsheetValueType.NUMBER,
                Expression.value(true), // match everything
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY.setCells(
                                Sets.of(
                                        saved.cell(a2).get(),
                                        saved.cell(a3).get(),
                                        saved.cell(b3).get(),
                                        saved.cell(c3).get(),
                                        saved.cell(a4).get(),
                                        saved.cell(b4).get(),
                                        saved.cell(c4).get()
                                )
                        ).setColumnWidths(columnWidths("A,B,C"))
                        .setRowHeights(rowHeights("2,3,4"))
                        .setColumnCount(OptionalInt.of(3))
                        .setRowCount(OptionalInt.of(4))
        );
    }

    @Test
    public void testFindCellsWithValueTypeFilteringAndCount() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a2 = SpreadsheetSelection.parseCell("a2");
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("b2");
        final SpreadsheetCellReference c2 = SpreadsheetSelection.parseCell("c2");

        final SpreadsheetCellReference a3 = SpreadsheetSelection.parseCell("a3");
        final SpreadsheetCellReference b3 = SpreadsheetSelection.parseCell("b3");
        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("c3");

        final SpreadsheetCellReference a4 = SpreadsheetSelection.parseCell("a4");
        final SpreadsheetCellReference b4 = SpreadsheetSelection.parseCell("b4");
        final SpreadsheetCellReference c4 = SpreadsheetSelection.parseCell("c4");

        final SpreadsheetDelta saved = engine.saveCells(
                Sets.of(
                        SpreadsheetSelection.parseCell("B1")
                                .setFormula(
                                        SpreadsheetFormula.EMPTY.setText("=-999")
                                                .setValue(
                                                        Optional.of(999)
                                                )
                                ),
                        a2.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=1")
                                        .setValue(
                                                Optional.of(1)
                                        )
                        ),
                        b2.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=\"Hello2\"")
                                        .setValue(
                                                Optional.of("Hello2")
                                        )
                        ),
                        c2.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=\"Hello3\"")
                                        .setValue(
                                                Optional.of("Hello3")
                                        )
                        ),
                        a3.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=4")
                                        .setValue(
                                                Optional.of(4)
                                        )
                        ),
                        b3.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=5")
                                        .setValue(
                                                Optional.of(5)
                                        )
                        ),
                        c3.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=6")
                                        .setValue(
                                                Optional.of(6)
                                        )
                        ),
                        a4.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=7")
                                        .setValue(
                                                Optional.of(7)
                                        )
                        ),
                        b4.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=8")
                                        .setValue(
                                                Optional.of(8)
                                        )
                        ),
                        c4.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=9")
                                        .setValue(
                                                Optional.of(9)
                                        )
                        )
                ),
                context
        );

        this.findCellsAndCheck(
                engine,
                SpreadsheetSelection.parseCellRange("A2:C4"),
                SpreadsheetCellRangeReferencePath.LRTD,
                0, // offset
                3, //count
                SpreadsheetValueType.NUMBER,
                Expression.value(true), // match everything
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY.setCells(
                                Sets.of(
                                        saved.cell(a2).get(),
                                        saved.cell(a3).get(),
                                        saved.cell(b3).get()
                                )
                        ).setColumnWidths(columnWidths("A,B"))
                        .setRowHeights(rowHeights("2,3"))
                        .setColumnCount(OptionalInt.of(3))
                        .setRowCount(OptionalInt.of(4))
        );
    }

    @Test
    public void testFindCellsWithValueTypeFilteringAndCount2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a2 = SpreadsheetSelection.parseCell("a2");
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("b2");
        final SpreadsheetCellReference c2 = SpreadsheetSelection.parseCell("c2");

        final SpreadsheetCellReference a3 = SpreadsheetSelection.parseCell("a3");
        final SpreadsheetCellReference b3 = SpreadsheetSelection.parseCell("b3");
        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("c3");

        final SpreadsheetCellReference a4 = SpreadsheetSelection.parseCell("a4");
        final SpreadsheetCellReference b4 = SpreadsheetSelection.parseCell("b4");
        final SpreadsheetCellReference c4 = SpreadsheetSelection.parseCell("c4");

        final SpreadsheetDelta saved = engine.saveCells(
                Sets.of(
                        SpreadsheetSelection.parseCell("B1")
                                .setFormula(
                                        SpreadsheetFormula.EMPTY.setText("=-999")
                                                .setValue(
                                                        Optional.of(999)
                                                )
                                ),
                        a2.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=\"Hello1\"")
                                        .setValue(
                                                Optional.of("Hello1")
                                        )
                        ),
                        b2.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=\"Hello2\"")
                                        .setValue(
                                                Optional.of("Hello2")
                                        )
                        ),
                        c2.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=\"Hello3\"")
                                        .setValue(
                                                Optional.of("Hello3")
                                        )
                        ),
                        a3.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=4")
                                        .setValue(
                                                Optional.of(4)
                                        )
                        ),
                        b3.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=5")
                                        .setValue(
                                                Optional.of(5)
                                        )
                        ),
                        c3.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=6")
                                        .setValue(
                                                Optional.of(6)
                                        )
                        ),
                        a4.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=7")
                                        .setValue(
                                                Optional.of(7)
                                        )
                        ),
                        b4.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=8")
                                        .setValue(
                                                Optional.of(8)
                                        )
                        ),
                        c4.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=9")
                                        .setValue(
                                                Optional.of(9)
                                        )
                        )
                ),
                context
        );

        this.findCellsAndCheck(
                engine,
                SpreadsheetSelection.parseCellRange("A2:C4"),
                SpreadsheetCellRangeReferencePath.LRTD,
                0, // offset
                3, // max
                SpreadsheetValueType.NUMBER,
                Expression.value(true), // match everything
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY.setCells(
                                Sets.of(
                                        saved.cell(a3).get(),
                                        saved.cell(b3).get(),
                                        saved.cell(c3).get()
                                )
                        ).setColumnWidths(columnWidths("A,B,C"))
                        .setRowHeights(rowHeights("3"))
                        .setColumnCount(OptionalInt.of(3))
                        .setRowCount(OptionalInt.of(4))
        );
    }

    @Test
    public void testFindCellsWithPathRlbuValueTypeFilteringAndCount2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a2 = SpreadsheetSelection.parseCell("a2");
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("b2");
        final SpreadsheetCellReference c2 = SpreadsheetSelection.parseCell("c2");

        final SpreadsheetCellReference a3 = SpreadsheetSelection.parseCell("a3");
        final SpreadsheetCellReference b3 = SpreadsheetSelection.parseCell("b3");
        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("c3");

        final SpreadsheetCellReference a4 = SpreadsheetSelection.parseCell("a4");
        final SpreadsheetCellReference b4 = SpreadsheetSelection.parseCell("b4");
        final SpreadsheetCellReference c4 = SpreadsheetSelection.parseCell("c4");

        final SpreadsheetDelta saved = engine.saveCells(
                Sets.of(
                        a2.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=1")
                        ),
                        b2.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=2")
                        ),
                        c2.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=3")
                        ),
                        a3.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=4")
                        ),
                        b3.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=5")
                                        .setValue(
                                                Optional.of(5)
                                        )
                        ),
                        c3.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=\"Hello6\"")
                                        .setValue(
                                                Optional.of("Hello6")
                                        )
                        ),
                        a4.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=7")
                                        .setValue(
                                                Optional.of(7)
                                        )
                        ),
                        b4.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=\"Hello8\"")
                                        .setValue(
                                                Optional.of("Hello8")
                                        )
                        ),
                        c4.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=9")
                                        .setValue(
                                                Optional.of(9)
                                        )
                        )
                ),
                context
        );

        this.findCellsAndCheck(
                engine,
                SpreadsheetSelection.parseCellRange("A2:C4"),
                SpreadsheetCellRangeReferencePath.RLBU,
                0, // offset
                3, // count
                SpreadsheetValueType.NUMBER,
                Expression.value(true), // match everything
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY.setCells(
                                Sets.of(
                                        saved.cell(c4).get(),
                                        saved.cell(a4).get(),
                                        saved.cell(b3).get()
                                )
                        ).setColumnWidths(columnWidths("A,B,C"))
                        .setRowHeights(rowHeights("3,4"))
                        .setColumnCount(OptionalInt.of(3))
                        .setRowCount(OptionalInt.of(4))
        );
    }

    @Test
    public void testFindCellsWithPathRlbuValueTypeFilteringOffsetAndCount() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a2 = SpreadsheetSelection.parseCell("a2");
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("b2");
        final SpreadsheetCellReference c2 = SpreadsheetSelection.parseCell("c2");

        final SpreadsheetCellReference a3 = SpreadsheetSelection.parseCell("a3");
        final SpreadsheetCellReference b3 = SpreadsheetSelection.parseCell("b3");
        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("c3");

        final SpreadsheetCellReference a4 = SpreadsheetSelection.parseCell("a4");
        final SpreadsheetCellReference b4 = SpreadsheetSelection.parseCell("b4");
        final SpreadsheetCellReference c4 = SpreadsheetSelection.parseCell("c4");

        final SpreadsheetDelta saved = engine.saveCells(
                Sets.of(
                        a2.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=1")
                        ),
                        b2.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=2")
                        ),
                        c2.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=3")
                        ),
                        a3.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=4")
                        ),
                        b3.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=5")
                                        .setValue(
                                                Optional.of(5)
                                        )
                        ),
                        c3.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=\"Hello6\"")
                                        .setValue(
                                                Optional.of("Hello6")
                                        )
                        ),
                        a4.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=7")
                                        .setValue(
                                                Optional.of(7)
                                        )
                        ),
                        b4.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=\"Hello8\"")
                                        .setValue(
                                                Optional.of("Hello8")
                                        )
                        ),
                        c4.setFormula(
                                SpreadsheetFormula.EMPTY.setText("=9")
                                        .setValue(
                                                Optional.of(9)
                                        )
                        )
                ),
                context
        );

        this.findCellsAndCheck(
                engine,
                SpreadsheetSelection.parseCellRange("A2:C4"),
                SpreadsheetCellRangeReferencePath.RLBU,
                0, // offset
                3, // count
                SpreadsheetValueType.NUMBER,
                Expression.value(true), // match everything
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY.setCells(
                                Sets.of(
                                        saved.cell(c4).get(), // 0
                                        saved.cell(a4).get(), // 1
                                        saved.cell(b3).get() // 2
                                )
                        ).setColumnWidths(columnWidths("A,B,C"))
                        .setRowHeights(rowHeights("3,4"))
                        .setColumnCount(OptionalInt.of(3))
                        .setRowCount(OptionalInt.of(4))
        );

        this.findCellsAndCheck(
                engine,
                SpreadsheetSelection.parseCellRange("A2:C4"),
                SpreadsheetCellRangeReferencePath.RLBU,
                1, // offset
                2, // count
                SpreadsheetValueType.NUMBER,
                Expression.value(true), // match everything
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY.setCells(
                                Sets.of(
                                        saved.cell(a4).get(), // 1
                                        saved.cell(b3).get() // 2
                                )
                        ).setColumnWidths(columnWidths("A,B"))
                        .setRowHeights(rowHeights("3,4"))
                        .setColumnCount(OptionalInt.of(3))
                        .setRowCount(OptionalInt.of(4))
        );

        this.findCellsAndCheck(
                engine,
                SpreadsheetSelection.parseCellRange("A2:C4"),
                SpreadsheetCellRangeReferencePath.RLBU,
                2, // offset
                1, // count
                SpreadsheetValueType.NUMBER,
                Expression.value(true), // match everything
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY.setCells(
                                Sets.of(
                                        saved.cell(b3).get() // 2
                                )
                        ).setColumnWidths(columnWidths("B"))
                        .setRowHeights(rowHeights("3"))
                        .setColumnCount(OptionalInt.of(3))
                        .setRowCount(OptionalInt.of(4))
        );

        this.findCellsAndCheck(
                engine,
                SpreadsheetSelection.parseCellRange("A2:C4"),
                SpreadsheetCellRangeReferencePath.RLBU,
                3, // offset
                0, // count
                SpreadsheetValueType.NUMBER,
                Expression.value(true), // match everything
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY.setColumnCount(OptionalInt.of(3))
                        .setRowCount(OptionalInt.of(4))
        );
    }

    // sortCells........................................................................................................

    @Test
    public void testSortCellsInvalidComparatorFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> this.createSpreadsheetEngine()
                        .sortCells(
                                SpreadsheetSelection.A1.toCellRange(),
                                SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.parse("A=day-of-month,month-of-year,year"),
                                SpreadsheetDeltaProperties.ALL,
                                new FakeSpreadsheetEngineContext() {
                                    @Override
                                    public SpreadsheetStoreRepository storeRepository() {
                                        return SpreadsheetStoreRepositories.basic(
                                                SpreadsheetCellStores.treeMap(),
                                                SpreadsheetCellReferencesStores.treeMap(),
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
                                    }

                                    @Override
                                    public SpreadsheetMetadata spreadsheetMetadata() {
                                        return METADATA.set(
                                                SpreadsheetMetadataPropertyName.SORT_COMPARATORS,
                                                SpreadsheetComparatorNameList.parse("day-of-month")
                                        );
                                    }

                                    @Override
                                    public SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext(final Optional<SpreadsheetCell> cell,
                                                                                                                         final SpreadsheetExpressionReferenceLoader loader) {
                                        return SpreadsheetExpressionEvaluationContexts.fake();
                                    }
                                }
                        )
        );

        // day-of-month is available others are absent
        this.checkEquals(
                "Invalid comparators: month-of-year,year",
                thrown.getMessage()
        );
    }

    @Test
    public void testSortWithColumnsNothingChanged() {
        final SpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell(
                "A1",
                "'1a"
        );
        final SpreadsheetCell b2 = this.cell(
                "B2",
                "'2b"
        );

        engine.saveCells(
                Sets.of(
                        a1,
                        b2
                ),
                context
        );

        // because the cells were not moved the result should have no cells.
        this.sortCellsAndCheck(
                engine,
                "A1:B2", // cell-range
                "A=text", // comparators
                SpreadsheetDeltaProperties.ALL, // delta-properties
                context,
                SpreadsheetDelta.EMPTY.setColumnCount(
                        OptionalInt.of(2)
                ).setRowCount(
                        OptionalInt.of(2)
                )
        );
    }

    @Test
    public void testSortWithColumnsRowsSwapped() {
        final SpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        engine.saveCells(
                Sets.of(
                        this.cell(
                                "A1",
                                "'BBB"
                        ),
                        this.cell(
                                "A2",
                                "'AAA"
                        )
                ),
                context
        );

        this.sortCellsAndCheck(
                engine,
                "A1:B2", // cell-range
                "A=text", // comparators
                SpreadsheetDeltaProperties.ALL, // delta-properties
                context,
                SpreadsheetDelta.EMPTY.setCells(
                        Sets.of(
                                this.formatCell(
                                        "A1",
                                        "'AAA",
                                        "AAA"
                                ),
                                this.formatCell(
                                        "A2",
                                        "'BBB",
                                        "BBB"
                                )
                        )
                ).setColumnWidths(
                        columnWidths("A")
                ).setRowHeights(
                        rowHeights("1,2")
                ).setColumnCount(
                        OptionalInt.of(1)
                ).setRowCount(
                        OptionalInt.of(2)
                )
        );
    }

    @Test
    public void testSortWithRowsColumnsSwapped() {
        final SpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        engine.saveCells(
                Sets.of(
                        this.cell(
                                "A1",
                                "'BBB"
                        ),
                        this.cell(
                                "B1",
                                "'AAA"
                        )
                ),
                context
        );

        this.sortCellsAndCheck(
                engine,
                "A1:B2", // cell-range
                "1=text", // comparators
                SpreadsheetDeltaProperties.ALL, // delta-properties
                context,
                SpreadsheetDelta.EMPTY.setCells(
                        Sets.of(
                                this.formatCell(
                                        "A1",
                                        "'AAA",
                                        "AAA"
                                ),
                                this.formatCell(
                                        "B1",
                                        "'BBB",
                                        "BBB"
                                )
                        )
                ).setColumnWidths(
                        columnWidths("A,B")
                ).setRowHeights(
                        rowHeights("1")
                ).setColumnCount(
                        OptionalInt.of(2)
                ).setRowCount(
                        OptionalInt.of(1)
                )
        );
    }

    @Test
    public void testSortWithColumnsRowsSwappedIncludesSortedCellReferences() {
        final SpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        engine.saveCells(
                Sets.of(
                        this.cell(
                                "A1",
                                "'BBB"
                        ),
                        this.cell(
                                "B1",
                                "=A1"
                        ),
                        this.cell(
                                "A2",
                                "'AAA"
                        ),
                        this.cell(
                                "B2",
                                "=A2"
                        )
                ),
                context
        );

        this.sortCellsAndCheck(
                engine,
                "A1:B2", // cell-range
                "A=text", // comparators
                SpreadsheetDeltaProperties.ALL, // delta-properties
                context,
                SpreadsheetDelta.EMPTY.setCells(
                        Sets.of(
                                this.formatCell(
                                        "A1",
                                        "'AAA",
                                        "AAA"
                                ),
                                this.formatCell(
                                        "B1",
                                        "=A1",
                                        "AAA"
                                ),
                                this.formatCell(
                                        "A2",
                                        "'BBB",
                                        "BBB"
                                ),
                                this.formatCell(
                                        "B2",
                                        "=A2",
                                        "BBB"
                                )
                        )
                ).setReferences(references("A1=B1;A2=B2")
                ).setColumnWidths(
                        columnWidths("A,B")
                ).setRowHeights(
                        rowHeights("1,2")
                ).setColumnCount(
                        OptionalInt.of(2)
                ).setRowCount(
                        OptionalInt.of(2)
                )
        );
    }

    @Test
    public void testSortWithColumnsRowsSwappedReferencedByCellsOutsideSortRange() {
        final SpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        engine.saveCells(
                Sets.of(
                        this.cell(
                                "A1",
                                "'BBB"
                        ),
                        this.cell(
                                "A2",
                                "'AAA"
                        ),
                        this.cell(
                                "B1",
                                "=A1"
                        ),
                        this.cell(
                                "B2",
                                "=A2"
                        )
                ),
                context
        );

        this.sortCellsAndCheck(
                engine,
                "A1:A2", // cell-range
                "A=text", // comparators
                SpreadsheetDeltaProperties.ALL, // delta-properties
                context,
                SpreadsheetDelta.EMPTY.setCells(
                        Sets.of(
                                this.formatCell(
                                        "A1",
                                        "'AAA",
                                        "AAA"
                                ),
                                this.formatCell(
                                        "A2",
                                        "'BBB",
                                        "BBB"
                                ),
                                this.formatCell(
                                        "B1",
                                        "=A1",
                                        "AAA"
                                ),
                                this.formatCell(
                                        "B2",
                                        "=A2",
                                        "BBB"
                                )
                        )
                ).setReferences(references("A1=B1;A2=B2")
                ).setColumnWidths(
                        columnWidths("A,B")
                ).setRowHeights(
                        rowHeights("1,2")
                ).setColumnCount(
                        OptionalInt.of(2)
                ).setRowCount(
                        OptionalInt.of(2)
                )
        );
    }

    @Test
    public void testSortWithColumnsRowsSwappedLabelNotUpdated() {
        final SpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        engine.saveCells(
                Sets.of(
                        this.cell(
                                "A1",
                                "'BBB"
                        ),
                        this.cell(
                                "A2",
                                "'AAA"
                        )
                ),
                context
        );

        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");
        engine.saveLabel(
                label.setLabelMappingReference(SpreadsheetSelection.A1),
                context
        );

        this.sortCellsAndCheck(
                engine,
                "A1:B2", // cell-range
                "A=text", // comparators
                SpreadsheetDeltaProperties.ALL, // delta-properties
                context,
                SpreadsheetDelta.EMPTY.setCells(
                        Sets.of(
                                this.formatCell(
                                        "A1",
                                        "'AAA",
                                        "AAA"
                                ),
                                this.formatCell(
                                        "A2",
                                        "'BBB",
                                        "BBB"
                                )
                        )
                ).setLabels(
                        Sets.of(
                                label.setLabelMappingReference(SpreadsheetSelection.A1)
                        )
                ).setReferences(references("A1=Label123")
                ).setColumnWidths(
                        columnWidths("A")
                ).setRowHeights(
                        rowHeights("1,2")
                ).setColumnCount(
                        OptionalInt.of(1)
                ).setRowCount(
                        OptionalInt.of(2)
                )
        );
    }

    // loadFormulaReferences............................................................................................

    @Test
    public void testLoadFormulaReferencesWheCellAbsent() {
        final SpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        // note the cell reference is not included in the response
        this.loadFormulaReferencesAndCheck(
                engine,
                SpreadsheetSelection.A1,
                0, // offset
                100, // count
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY.setColumnCount(OptionalInt.of(0))
                        .setRowCount(OptionalInt.of(0))
        );
    }

    @Test
    public void testLoadFormulaReferencesWhenWithoutFormulaReferences() {
        final SpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell(
                "A1",
                "=111"
        );

        engine.saveCells(
                Sets.of(a1),
                context
        );

        // note the cell reference is not included in the response
        this.loadFormulaReferencesAndCheck(
                engine,
                a1.reference(),
                0, // offset
                100, // count
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY.setColumnCount(OptionalInt.of(1))
                        .setRowCount(OptionalInt.of(1))
        );
    }

    @Test
    public void testLoadFormulaReferencesWithReferenceToMissingCell() {
        final SpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell(
                "A1",
                "=111+b2"
        );

        engine.saveCells(
                Sets.of(a1),
                context
        );

        this.loadFormulaReferencesAndCheck(
                engine,
                SpreadsheetSelection.A1,
                0, // offset
                100, // count
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY.setCells(
                                Sets.of(
                                        SpreadsheetSelection.parseCell("B2")
                                                .setFormula(SpreadsheetFormula.EMPTY)
                                )
                        ).setColumnWidths(columnWidths("B"))
                        .setRowHeights(rowHeights("2"))
                        .setColumnCount(OptionalInt.of(1))
                        .setRowCount(OptionalInt.of(1))
        );
    }

    @Test
    public void testLoadFormulaReferencesWithReferenceToExistingCell() {
        final SpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell(
                "A1",
                "=111+b2"
        );
        final SpreadsheetCell b2 = this.cell(
                "B2",
                "=222"
        );

        engine.saveCells(
                Sets.of(
                        a1,
                        b2
                ),
                context
        );

        this.loadFormulaReferencesAndCheck(
                engine,
                SpreadsheetSelection.A1,
                0, // offset
                100, // count
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY.setCells(
                                Sets.of(
                                        this.formatCell(
                                                "B2",
                                                "=222",
                                                222
                                        )
                                )
                        ).setColumnWidths(columnWidths("B")) // B2 not included in output cells
                        .setRowHeights(rowHeights("2"))
                        .setColumnCount(OptionalInt.of(2))
                        .setRowCount(OptionalInt.of(2))
        );
    }

    @Test
    public void testLoadFormulaReferencesWithFormulaIncludingLabel() {
        final SpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        // points to cell
        final SpreadsheetLabelMapping mappingLabel123 = LABEL.setLabelMappingReference(
                SpreadsheetSelection.parseCell("B2")
        );

        engine.saveLabel(
                mappingLabel123,
                context
        );

        final SpreadsheetCell a1 = this.cell(
                "a1",
                "=" + LABEL
        );

        engine.saveCells(
                Sets.of(a1),
                context
        );

        this.loadFormulaReferencesAndCheck(
                engine,
                SpreadsheetSelection.A1,
                0, // offset
                100, // count
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY.setColumnCount(OptionalInt.of(1))
                        .setRowCount(OptionalInt.of(1))
        );
    }

    @Test
    public void testLoadFormulaReferencesWithExternalFormulaLabel() {
        final SpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        // points to cell
        final SpreadsheetLabelMapping mappingLabel123 = LABEL.setLabelMappingReference(SpreadsheetSelection.A1);

        engine.saveLabel(
                mappingLabel123,
                context
        );

        final SpreadsheetCell a1 = this.cell(
                "A1",
                "=111"
        );

        engine.saveCells(
                Sets.of(
                        a1
                ),
                context
        );

        this.loadFormulaReferencesAndCheck(
                engine,
                SpreadsheetSelection.A1,
                0, // offset
                100, // count
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY.setColumnCount(OptionalInt.of(1))
                        .setRowCount(OptionalInt.of(1))
        );
    }

    @Test
    public void testLoadFormulaReferencesWithExternalFormulaReferencesWithLabel() {
        final SpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        // points to cell
        final SpreadsheetCell a1 = this.cell(
                "A1",
                "=B2+111"
        );

        final SpreadsheetCell b2 = this.cell(
                "B2",
                "=222"
        );

        engine.saveCells(
                Sets.of(
                        a1,
                        b2
                ),
                context
        );


        final SpreadsheetLabelMapping mapping = LABEL.setLabelMappingReference(
                b2.reference()
        );

        engine.saveLabel(
                mapping,
                context
        );


        this.loadFormulaReferencesAndCheck(
                engine,
                SpreadsheetSelection.A1,
                0, // offset
                100, // count
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY.setCells(
                                Sets.of(
                                        this.formatCell(
                                                "B2",
                                                "=222",
                                                222
                                        )
                                )
                        ).setLabels(
                                Sets.of(mapping)
                        ).setColumnWidths(columnWidths("B"))
                        .setRowHeights(rowHeights("2"))
                        .setColumnCount(OptionalInt.of(2))
                        .setRowCount(OptionalInt.of(2))
        );
    }

    @Test
    public void testLoadReferencesWithCellReferenceAndLabels() {
        final SpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell(
                "A1",
                "=111"
        );
        final SpreadsheetCell b2 = this.cell(
                "B2",
                "=222+A1"
        );

        engine.saveCells(
                Sets.of(
                        a1,
                        b2
                ),
                context
        );

        final SpreadsheetLabelMapping mapping = LABEL.setLabelMappingReference(
                SpreadsheetSelection.A1
        );

        engine.saveLabel(
                mapping,
                context
        );

        // because the cells were not moved the result should have no cells.
        this.loadFormulaReferencesAndCheck(
                engine,
                b2.reference(),
                0, // offset
                100, // count
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY.setCells(
                                Sets.of(
                                        this.formatCell(
                                                "A1",
                                                "=111",
                                                111
                                        )
                                )
                        ).setLabels(
                                Sets.of(mapping)
                        ).setColumnWidths(columnWidths("A")) // B2 not included in output cells
                        .setRowHeights(rowHeights("1"))
                        .setColumnCount(OptionalInt.of(2))
                        .setRowCount(OptionalInt.of(2))
        );
    }

    @Test
    public void testLoadReferencesWithManyFormulaCellReferences() {
        final SpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell(
                "A1",
                "=111+B2+C3+D4"
        );
        final SpreadsheetCell b2 = this.cell(
                "B2",
                "=222"
        );
        final SpreadsheetCell c3 = this.cell(
                "c3",
                "=333"
        );
        final SpreadsheetCell d4 = this.cell(
                "D4",
                "=444"
        );

        engine.saveCells(
                Sets.of(
                        a1,
                        b2,
                        c3,
                        d4
                ),
                context
        );

        // because the cells were not moved the result should have no cells.
        this.loadFormulaReferencesAndCheck(
                engine,
                SpreadsheetSelection.A1,
                0, // offset
                100, // count
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY.setCells(
                                Sets.of(
                                        this.formatCell(
                                                "B2",
                                                "=222",
                                                222
                                        ),
                                        this.formatCell(
                                                "C3",
                                                "=333",
                                                333
                                        ),
                                        this.formatCell(
                                                "D4",
                                                "=444",
                                                444
                                        )
                                )
                        ).setColumnWidths(columnWidths("B,C,D")) // B2 not included in output cells
                        .setRowHeights(rowHeights("2,3,4"))
                        .setColumnCount(OptionalInt.of(4))
                        .setRowCount(OptionalInt.of(4))
        );
    }

    @Test
    public void testLoadReferencesWithManyFormulaCellReferencesAndOffset() {
        final SpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell(
                "A1",
                "=111+B2+C3+D4"
        );
        final SpreadsheetCell b2 = this.cell(
                "B2",
                "=222"
        );
        final SpreadsheetCell c3 = this.cell(
                "c3",
                "=333"
        );
        final SpreadsheetCell d4 = this.cell(
                "D4",
                "=444"
        );

        engine.saveCells(
                Sets.of(
                        a1,
                        b2,
                        c3,
                        d4
                ),
                context
        );

        // because the cells were not moved the result should have no cells.
        this.loadFormulaReferencesAndCheck(
                engine,
                SpreadsheetSelection.A1,
                1, // offset
                100, // count
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY.setCells(
                                Sets.of(
                                        this.formatCell(
                                                "C3",
                                                "=333",
                                                333
                                        ),
                                        this.formatCell(
                                                "D4",
                                                "=444",
                                                444
                                        )
                                )
                        ).setColumnWidths(columnWidths("C,D")) // B2 not included in output cells
                        .setRowHeights(rowHeights("3,4"))
                        .setColumnCount(OptionalInt.of(4))
                        .setRowCount(OptionalInt.of(4))
        );
    }

    @Test
    public void testLoadReferencesWithManyFormulaCellReferencesAndOffsetAndCount() {
        final SpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell(
                "A1",
                "=111+B2+C3+D4+E5"
        );
        final SpreadsheetCell b2 = this.cell(
                "B2",
                "=222"
        );
        final SpreadsheetCell c3 = this.cell(
                "c3",
                "=333"
        );
        final SpreadsheetCell d4 = this.cell(
                "D4",
                "=444"
        );
        final SpreadsheetCell e5 = this.cell(
                "E5",
                "=555"
        );

        engine.saveCells(
                Sets.of(
                        a1,
                        b2,
                        c3,
                        d4,
                        e5
                ),
                context
        );

        // because the cells were not moved the result should have no cells.
        this.loadFormulaReferencesAndCheck(
                engine,
                SpreadsheetSelection.A1,
                1, // offset skips B2
                2, // count includes C3, D4, stops before E5
                SpreadsheetDeltaProperties.ALL,
                context,
                SpreadsheetDelta.EMPTY.setCells(
                                Sets.of(
                                        this.formatCell(
                                                "C3",
                                                "=333",
                                                333
                                        ),
                                        this.formatCell(
                                                "D4",
                                                "=444",
                                                444
                                        )
                                )
                        ).setColumnWidths(columnWidths("C,D")) // B2 not included in output cells
                        .setRowHeights(rowHeights("3,4"))
                        .setColumnCount(OptionalInt.of(5))
                        .setRowCount(OptionalInt.of(5))
        );
    }

    //  loadLabel.......................................................................................................

    @Test
    public void testLoadLabelWithUnknownLabelFails() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("LABEL123");
        final SpreadsheetLabelMapping mapping = SpreadsheetLabelMapping.with(
                label,
                SpreadsheetSelection.parseCell("B2")
        );

        this.saveLabelAndCheck(
                engine,
                mapping,
                context
        );

        this.loadLabelAndFailCheck(
                engine,
                SpreadsheetSelection.labelName("UnknownLabel"),
                this.createContext()
        );
    }

    //  saveLabel.......................................................................................................

    @Test
    public void testSaveLabelWithCycleFails() {
        final SpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelName label1 = SpreadsheetSelection.labelName("Label111");
        final SpreadsheetLabelName label2 = SpreadsheetSelection.labelName("Label222");

        final SpreadsheetLabelMapping mapping1 = label1.setLabelMappingReference(label2);
        final SpreadsheetLabelMapping mapping2 = label2.setLabelMappingReference(label1);

        engine.saveLabel(
                mapping1,
                context
        );

        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> engine.saveLabel(
                        mapping2,
                        context
                )
        );

        this.checkEquals(
                "Cycle detected for \"Label222\" -> \"Label111\" -> \"Label222\"",
                thrown.getMessage()
        );

        // mapping2 should not have been saved
        this.countAndCheck(
                context.storeRepository()
                        .labels(),
                1
        );

        this.loadLabelAndCheck(
                engine,
                label2,
                context,
                SpreadsheetDelta.EMPTY
        );
    }

    @Test
    public void testSaveLabelAndLoadFromLabelStore() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("LABEL123");
        final SpreadsheetLabelMapping mapping = SpreadsheetLabelMapping.with(
                label,
                SpreadsheetSelection.parseCell("B2")
        );

        this.saveLabelAndCheck(
                engine,
                mapping,
                context
        );

        this.loadLabelAndCheck(
                context.storeRepository().labels(),
                label,
                mapping
        );
    }

    @Test
    public void testSaveLabelAndLoadLabel() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("LABEL123");
        final SpreadsheetLabelMapping mapping = SpreadsheetLabelMapping.with(
                label,
                SpreadsheetSelection.parseCell("B2")
        );

        this.saveLabelAndCheck(
                engine,
                mapping,
                context
        );

        this.loadLabelAndCheck(
                engine,
                label,
                context,
                mapping
        );
    }

    @Test
    public void testSaveLabelWithoutCellReferences() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("LABEL123");
        final SpreadsheetLabelMapping mapping = SpreadsheetLabelMapping.with(
                label,
                SpreadsheetSelection.parseCell("B2")
        );

        engine.saveCell(
                this.cell(
                        "B2",
                        "=99"
                ),
                context
        );

        this.saveLabelAndCheck(
                engine,
                mapping,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "B2",
                                                "=99",
                                                99
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("B")
                        ).setRowHeights(
                                rowHeights("2")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(2)
                        ).setLabels(
                                Sets.of(mapping)
                        )
        );

        engine.saveCell(this.cell("A1", label + "+1"), context);
    }

    @Test
    public void testSaveLabelRefreshesCellReferences() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("LABEL123");
        final SpreadsheetLabelMapping mapping = SpreadsheetLabelMapping.with(
                label,
                SpreadsheetSelection.parseCell("B2")
        );

        engine.saveCell(
                this.cell(
                        "A1",
                        "=" + label + "+1"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        "B2",
                        "=99"
                ),
                context
        );

        this.saveLabelAndCheck(
                engine,
                mapping,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "A1",
                                                "=" + label + "+1",
                                                99 + 1
                                        ),
                                        this.formatCell(
                                                "B2",
                                                "=99",
                                                99
                                        )
                                )
                        ).setColumnWidths(
                                columnWidths("A,B")
                        ).setRowHeights(
                                rowHeights("1,2")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(2)
                        ).setLabels(
                                Sets.of(mapping)
                        )
        );
    }

    @Test
    public void testSaveLabelRefreshesCellReferencesAndColumns() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("LABEL123");
        final SpreadsheetLabelMapping mapping = SpreadsheetLabelMapping.with(label, SpreadsheetSelection.parseCell("B2"));

        final SpreadsheetColumn a = SpreadsheetSelection.parseColumn("a")
                .column();
        engine.saveColumn(a, context);

        final SpreadsheetColumn b = SpreadsheetSelection.parseColumn("b")
                .column();
        engine.saveColumn(b, context);

        final SpreadsheetColumn c = SpreadsheetSelection.parseColumn("c")
                .column();
        engine.saveColumn(c, context);

        engine.saveCell(
                this.cell(
                        "A1",
                        "=" + label + "+1"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        "B2",
                        "=99"
                ),
                context
        );

        this.saveLabelAndCheck(
                engine,
                mapping,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "A1",
                                                "=" + label + "+1",
                                                99 + 1
                                        ),
                                        this.formatCell(
                                                "B2",
                                                "=99",
                                                99
                                        )
                                )
                        ).setColumns(
                                Sets.of(
                                        a,
                                        b
                                )
                        ).setColumnWidths(
                                columnWidths("A,B")
                        ).setRowHeights(
                                rowHeights("1,2")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(2)
                        ).setLabels(
                                Sets.of(mapping)
                        )
        );
    }

    @Test
    public void testSaveLabelRefreshesCellReferencesAndRows() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("LABEL123");
        final SpreadsheetLabelMapping mapping = SpreadsheetLabelMapping.with(
                label,
                SpreadsheetSelection.parseCell("B2")
        );

        final SpreadsheetRow row1 = SpreadsheetSelection.parseRow("1")
                .row();
        engine.saveRow(row1, context);

        final SpreadsheetRow row2 = SpreadsheetSelection.parseRow("2")
                .row();
        engine.saveRow(row2, context);

        final SpreadsheetRow row3 = SpreadsheetSelection.parseRow("3")
                .row();
        engine.saveRow(row3, context);

        engine.saveCell(
                this.cell(
                        "A1",
                        "=" + label + "+1"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        "B2",
                        "=99"
                ),
                context
        );

        this.saveLabelAndCheck(
                engine,
                mapping,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "A1",
                                                "=" + label + "+1",
                                                99 + 1
                                        ),
                                        this.formatCell(
                                                "B2",
                                                "=99",
                                                99
                                        )
                                )
                        ).setRows(
                                Sets.of(
                                        row1,
                                        row2
                                )
                        ).setColumnWidths(
                                columnWidths("A,B")
                        ).setRowHeights(
                                rowHeights("1,2")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(2)
                        ).setLabels(
                                Sets.of(mapping)
                        )
        );
    }

    @Test
    public void testSaveLabelForCell() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        engine.saveCell(
                this.cell(
                        "A1",
                        "=123"
                ), context
        );

        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("LABEL123");
        final SpreadsheetLabelMapping mapping = SpreadsheetLabelMapping.with(
                label,
                SpreadsheetSelection.A1
        );

        this.saveLabelAndCheck(
                engine,
                mapping,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "A1",
                                                "=123",
                                                123
                                        )
                                )
                        ).setColumnWidths(
                                COLUMN_A_WIDTH
                        ).setRowHeights(
                                ROW_1_HEIGHT
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(1)
                        ).setLabels(
                                Sets.of(mapping)
                        )
        );
    }

    @Test
    public void testSaveLabelDifferentSameCell() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        engine.saveCell(
                this.cell(
                        "A1",
                        "=111"
                ), context
        );

        final SpreadsheetLabelName label1 = SpreadsheetSelection.labelName("LABEL111");
        final SpreadsheetLabelMapping mapping1 = SpreadsheetLabelMapping.with(
                label1,
                SpreadsheetSelection.A1
        );

        this.saveLabelAndCheck(
                engine,
                mapping1,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "A1",
                                                "=111",
                                                111
                                        )
                                )
                        ).setColumnWidths(
                                COLUMN_A_WIDTH
                        ).setRowHeights(
                                ROW_1_HEIGHT
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(1)
                        ).setLabels(
                                Sets.of(mapping1)
                        )
        );

        final SpreadsheetLabelName label2 = SpreadsheetSelection.labelName("LABEL222");
        final SpreadsheetLabelMapping mapping2 = SpreadsheetLabelMapping.with(
                label2,
                SpreadsheetSelection.A1
        );

        this.saveLabelAndCheck(
                engine,
                mapping2,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "A1",
                                                "=111",
                                                111
                                        )
                                )
                        ).setColumnWidths(
                                COLUMN_A_WIDTH
                        ).setRowHeights(
                                ROW_1_HEIGHT
                        ).setColumnCount(
                                OptionalInt.of(1)
                        ).setRowCount(
                                OptionalInt.of(1)
                        ).setLabels(
                                Sets.of(
                                        mapping1,
                                        mapping2
                                )
                        )
        );
    }

    //  deleteLabel.......................................................................................................

    @Test
    public void testDeleteLabelAndLoadFromLabelStore() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("LABEL123");
        final SpreadsheetLabelMapping mapping = SpreadsheetLabelMapping.with(
                label,
                SpreadsheetSelection.parseCell("B2")
        );

        this.saveLabelAndCheck(
                engine,
                mapping,
                context
        );

        this.deleteLabelAndCheck(
                engine,
                label,
                context
        );

        this.loadLabelFailCheck(context.storeRepository().labels(), label);
    }

    @Test
    public void testDeleteLabelRefreshesCell() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("LABEL123");
        final SpreadsheetLabelMapping mapping = SpreadsheetLabelMapping.with(
                label,
                SpreadsheetSelection.parseCell("B2")
        );

        engine.saveCell(
                this.cell(
                        "A1",
                        "=" + label + "+1"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        "B2",
                        "=99"
                ),
                context
        );

        engine.saveLabel(
                mapping,
                context
        );

        this.deleteLabelAndCheck(
                engine,
                label,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "A1",
                                                "=" + label + "+1",
                                                SpreadsheetError.selectionNotFound(label)
                                        )
                                )
                        ).setDeletedLabels(
                                Sets.of(label)
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(2)
                        )
        );

        this.loadLabelFailCheck(context.storeRepository().labels(), label);
    }

    @Test
    public void testDeleteLabelRefreshesCellAndColumns() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("LABEL123");
        final SpreadsheetLabelMapping mapping = SpreadsheetLabelMapping.with(
                label,
                SpreadsheetSelection.parseCell("B2")
        );

        final SpreadsheetColumn a = SpreadsheetSelection.parseColumn("A")
                .column();
        engine.saveColumn(a, context);

        final SpreadsheetColumn b = SpreadsheetSelection.parseColumn("B")
                .column();
        engine.saveColumn(b, context);

        engine.saveCell(
                this.cell(
                        "A1",
                        "=" + label + "+1"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        "B2",
                        "=99"
                ),
                context
        );

        engine.saveLabel(mapping, context);

        this.deleteLabelAndCheck(
                engine,
                label,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "A1",
                                                "=" + label + "+1",
                                                SpreadsheetError.selectionNotFound(label)
                                        )
                                )
                        ).setDeletedLabels(
                                Sets.of(label)
                        ).setColumns(
                                Sets.of(a)
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(2)
                        )
        );

        this.loadLabelFailCheck(
                context.storeRepository().labels(),
                label
        );
    }

    @Test
    public void testDeleteLabelRefreshesCellAndRows() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("LABEL123");
        final SpreadsheetLabelMapping mapping = SpreadsheetLabelMapping.with(
                label,
                SpreadsheetSelection.parseCell("B2")
        );

        final SpreadsheetRow row1 = SpreadsheetSelection.parseRow("1")
                .row();
        engine.saveRow(
                row1,
                context
        );

        final SpreadsheetRow row2 = SpreadsheetSelection.parseRow("2")
                .row();
        engine.saveRow(
                row2,
                context
        );

        engine.saveCell(
                this.cell(
                        "A1",
                        "=" + label + "+1"
                ),
                context
        );
        engine.saveCell(
                this.cell(
                        "B2",
                        "=99"
                ),
                context
        );

        engine.saveLabel(mapping, context);

        this.deleteLabelAndCheck(
                engine,
                label,
                context,
                SpreadsheetDelta.EMPTY
                        .setCells(
                                Sets.of(
                                        this.formatCell(
                                                "A1",
                                                "=" + label + "+1",
                                                SpreadsheetError.selectionNotFound(label)
                                        )
                                )
                        ).setDeletedLabels(
                                Sets.of(label)
                        ).setRows(
                                Sets.of(row1)
                        ).setColumnWidths(
                                columnWidths("A")
                        ).setRowHeights(
                                rowHeights("1")
                        ).setColumnCount(
                                OptionalInt.of(2)
                        ).setRowCount(
                                OptionalInt.of(2)
                        )
        );

        this.loadLabelFailCheck(
                context.storeRepository().labels(),
                label
        );
    }

    // findLabelsWithReferenceWithNullReference.........................................................................

    @Test
    public void testFindLabelsWithReference() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        engine.saveLabel(
                SpreadsheetSelection.labelName("Label1")
                        .setLabelMappingReference(SpreadsheetSelection.A1),
                context
        );

        engine.saveLabel(
                SpreadsheetSelection.labelName("Label2")
                        .setLabelMappingReference(
                                SpreadsheetSelection.parseCell("B2")
                        ),
                context
        );

        final SpreadsheetLabelMapping mapping3 = SpreadsheetSelection.labelName("Label3")
                .setLabelMappingReference(
                        SpreadsheetSelection.parseCell("C3")
                );
        engine.saveLabel(
                mapping3,
                context
        );

        engine.saveLabel(
                SpreadsheetSelection.labelName("Label4")
                        .setLabelMappingReference(
                                SpreadsheetSelection.parseCell("D4")
                        ),
                context
        );

        engine.saveLabel(
                SpreadsheetSelection.labelName("Label5")
                        .setLabelMappingReference(
                                SpreadsheetSelection.parseCellRange("E5:F6")
                        ),
                context
        );

        this.findLabelsWithReferenceAndCheck(
                engine,
                SpreadsheetSelection.parseCellRange("B2:E5"),
                1, // offset skips mapping 2
                1, // count
                context,
                mapping3
        );
    }

    // findReferencesWithCell...........................................................................................

    @Test
    public void testFindReferencesWithCell() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        final SpreadsheetCell a1 = this.cell(
                "a1",
                "=1"
        );
        final SpreadsheetCell b2 = this.cell(
                "b2",
                "=10+a1"
        );
        final SpreadsheetCell c3 = this.cell(
                "c3",
                "=100+a1"
        );
        final SpreadsheetCell d4 = this.cell(
                "d4",
                "=1000+a1"
        );

        engine.saveCells(
                Sets.of(
                        a1,
                        b2,
                        c3,
                        d4,
                        this.cell(
                                "e5",
                                "=999"
                        )
                ),
                context
        );

        this.findReferencesAndCheck(
                engine,
                SpreadsheetSelection.A1,
                0, // offset
                10, // count
                context,
                SpreadsheetDelta.EMPTY.setCells(
                        Sets.of(
                                this.formatCell(
                                        b2,
                                        10 + 1
                                ),
                                this.formatCell(
                                        c3,
                                        100 + 1
                                ),
                                this.formatCell(
                                        d4,
                                        1000 + 1
                                )
                        )
                )
        );
    }

    @Test
    public void testFindReferencesWithCellAndOffsetAndCount() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        final SpreadsheetCell a1 = this.cell(
                "a1",
                "=1"
        );
        final SpreadsheetCell b2 = this.cell(
                "b2",
                "=10+a1"
        );
        final SpreadsheetCell c3 = this.cell(
                "c3",
                "=100+a1"
        );
        final SpreadsheetCell d4 = this.cell(
                "d4",
                "=1000+a1"
        );
        final SpreadsheetCell e5 = this.cell(
                "e5",
                "=10000+a1"
        );

        engine.saveCells(
                Sets.of(
                        a1,
                        b2,
                        c3,
                        d4,
                        e5,
                        this.cell(
                                "z99",
                                "=999"
                        )
                ),
                context
        );

        // b2 skipped by offset, d4= not included by count
        this.findReferencesAndCheck(
                engine,
                SpreadsheetSelection.A1,
                1, // offset
                3, // count
                context,
                SpreadsheetDelta.EMPTY.setCells(
                        Sets.of(
                                // skips b2 because of offset=1, c3, d4 appear in results, e5 ignored because of count
                                this.formatCell(
                                        c3,
                                        100 + 1
                                ),
                                this.formatCell(
                                        d4,
                                        1000 + 1
                                )
                        )
                )
        );
    }

    @Test
    public void testFindReferencesWithCellRange() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        final SpreadsheetCell a1 = this.cell(
                "a1",
                "=1"
        );
        final SpreadsheetCell b2 = this.cell(
                "b2",
                "=10"
        );
        final SpreadsheetCell c3 = this.cell(
                "c3",
                "=100+a1"
        );
        final SpreadsheetCell d4 = this.cell(
                "d4",
                "=1000+b2"
        );

        engine.saveCells(
                Sets.of(
                        a1,
                        b2,
                        c3,
                        d4,
                        this.cell(
                                "e5",
                                "=999"
                        )
                ),
                context
        );

        this.findReferencesAndCheck(
                engine,
                SpreadsheetSelection.parseCellRange("A1:B2"),
                0, // offset
                3, // count
                context,
                SpreadsheetDelta.EMPTY.setCells(
                        Sets.of(
                                this.formatCell(
                                        c3,
                                        100 + 1
                                ),
                                this.formatCell(
                                        d4,
                                        1000 + 1
                                )
                        )
                )
        );
    }

    @Test
    public void testFindReferencesWithLabelToCellRange() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        final SpreadsheetCell a1 = this.cell(
                "a1",
                "=1"
        );
        final SpreadsheetCell b2 = this.cell(
                "b2",
                "=10"
        );
        final SpreadsheetCell c3 = this.cell(
                "c3",
                "=100+a1"
        );
        final SpreadsheetCell d4 = this.cell(
                "d4",
                "=1000+b2"
        );

        engine.saveCells(
                Sets.of(
                        a1,
                        b2,
                        c3,
                        d4,
                        this.cell(
                                "e5",
                                "=999"
                        )
                ),
                context
        );

        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");

        engine.saveLabel(
                label.setLabelMappingReference(SpreadsheetSelection.parseCellRange("A1:B2")),
                context
        );

        this.findReferencesAndCheck(
                engine,
                label,
                0, // offset
                3, // count
                context,
                SpreadsheetDelta.EMPTY.setCells(
                        Sets.of(
                                this.formatCell(
                                        c3,
                                        100 + 1
                                ),
                                this.formatCell(
                                        d4,
                                        1000 + 1
                                )
                        )
                )
        );
    }

    @Test
    public void testFindReferencesWithCellWhereFormulaIncludesCycle() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        final SpreadsheetCell a1 = this.cell(
                "a1",
                "=1+a1"
        );
        final SpreadsheetCell b2 = this.cell(
                "b2",
                "=10+a1"
        );

        engine.saveCells(
                Sets.of(
                        a1,
                        b2,
                        this.cell(
                                "c3",
                                "=999"
                        )
                ),
                context
        );

        this.findReferencesAndCheck(
                engine,
                SpreadsheetSelection.A1,
                0, // offset
                3, // count
                context,
                SpreadsheetDelta.EMPTY.setCells(
                        Sets.of(
                                this.formatCell(
                                        a1,
                                        SpreadsheetError.cycle(a1.reference())
                                ),
                                this.formatCell(
                                        b2,
                                        SpreadsheetError.cycle(a1.reference())
                                )
                        )
                )
        );
    }

    // columnWidth, rowHeight...........................................................................................

    @Test
    public void testColumnWidth() {
        final SpreadsheetColumnReference column = SpreadsheetSelection.parseColumn("Z");
        final double expected = 150.5;

        this.columnWidthAndCheck2(
                column,
                METADATA,
                expected,
                expected
        );
    }

    @Test
    public void testColumnWidthDefaults() {
        final SpreadsheetColumnReference column = SpreadsheetSelection.parseColumn("Z");
        final double expected = 150.5;

        this.columnWidthAndCheck2(
                column,
                METADATA.set(
                        SpreadsheetMetadataPropertyName.STYLE,
                        METADATA.getOrFail(SpreadsheetMetadataPropertyName.STYLE)
                                .set(
                                        TextStylePropertyName.WIDTH,
                                        Length.pixel(expected)
                                )
                ),
                0,
                expected);
    }

    private void columnWidthAndCheck2(final SpreadsheetColumnReference column,
                                      final SpreadsheetMetadata metadata,
                                      final double maxColumnWidth,
                                      final double expected) {
        this.columnWidthAndCheck(
                this.createSpreadsheetEngine(),
                column,
                this.createContext(
                        metadata,
                        new FakeSpreadsheetCellStore() {
                            @Override
                            public double maxColumnWidth(final SpreadsheetColumnReference c) {
                                checkEquals(column, c);
                                return maxColumnWidth;
                            }
                        }),
                expected
        );
    }

    // rowHeight........................................................................................................

    @Test
    public void testRowHeight() {
        this.rowHeightAndCheck2(
                SpreadsheetSelection.parseRow("987"),
                METADATA,
                150.5
        );
    }

    @Test
    public void testRowHeightDefaults() {
        final SpreadsheetRowReference row = SpreadsheetSelection.parseRow("987");
        final double expected = 150.5;

        this.rowHeightAndCheck2(
                row,
                METADATA.set(
                        SpreadsheetMetadataPropertyName.STYLE,
                        METADATA.getOrFail(SpreadsheetMetadataPropertyName.STYLE)
                                .set(
                                        TextStylePropertyName.HEIGHT,
                                        Length.pixel(expected)
                                )
                ),
                expected
        );
    }

    private void rowHeightAndCheck2(final SpreadsheetRowReference row,
                                    final SpreadsheetMetadata metadata,
                                    final double expected) {
        this.rowHeightAndCheck(
                this.createSpreadsheetEngine(),
                row,
                this.createContext(
                        metadata,
                        new FakeSpreadsheetCellStore() {
                            @Override
                            public double maxRowHeight(final SpreadsheetRowReference c) {
                                checkEquals(row, c);
                                return expected;
                            }
                        }),
                expected
        );
    }

    // columnCount.... .................................................................................................

    @Test
    public void testColumnCount() {
        final SpreadsheetCellStore cellStore = SpreadsheetCellStores.treeMap();

        final SpreadsheetEngineContext context = this.createContext(
                cellStore
        );

        cellStore.save(
                SpreadsheetSelection.parseCell("B99")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("'Hello")
                        )
        );

        this.columnCountAndCheck(
                this.createSpreadsheetEngine(),
                context,
                2
        );
    }

    // rowCount.... .................................................................................................

    @Test
    public void testRowCount() {
        final SpreadsheetCellStore cellStore = SpreadsheetCellStores.treeMap();

        final SpreadsheetEngineContext context = this.createContext(
                cellStore
        );

        cellStore.save(
                SpreadsheetSelection.parseCell("B99")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("'Hello")
                        )
        );

        this.rowCountAndCheck(
                this.createSpreadsheetEngine(),
                context,
                99
        );
    }

    // allColumnWidths .................................................................................................

    @Test
    public void testAllColumnWidths() {
        final SpreadsheetCellStore cellStore = SpreadsheetCellStores.treeMap();

        final SpreadsheetEngineContext context = this.createContext(
                METADATA.set(
                        SpreadsheetMetadataPropertyName.STYLE,
                        TextStyle.EMPTY.set(
                                TextStylePropertyName.WIDTH,
                                Length.pixel(COLUMN_WIDTH)
                        )
                ),
                cellStore
        );

        final SpreadsheetStoreRepository repo = context.storeRepository();

        repo.columns()
                .saveColumns(
                        Sets.of(
                                SpreadsheetColumn.with(
                                        SpreadsheetSelection.parseColumn("A")
                                ).setHidden(true),
                                SpreadsheetColumn.with(
                                        SpreadsheetSelection.parseColumn("B")
                                ).setHidden(false)
                        )
                );

        final double aWidth = 100;

        cellStore.save(
                SpreadsheetSelection.A1
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("'HiddenColumn")
                        ).setStyle(
                                TextStyle.EMPTY.set(
                                        TextStylePropertyName.WIDTH,
                                        Length.pixel(aWidth)
                                )
                        )
        );

        final double cWidth = 300;
        cellStore.save(
                SpreadsheetSelection.parseCell("C3")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("'Hello")
                        ).setStyle(
                                TextStyle.EMPTY.set(
                                        TextStylePropertyName.WIDTH,
                                        Length.pixel(cWidth)
                                )
                        )
        );
        cellStore.save(
                SpreadsheetSelection.parseCell("D4")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("'Hello2")
                        )
        );
    }

    // allRowHeights ...................................................................................................

    @Test
    public void testAllRowHeights() {
        final SpreadsheetCellStore cellStore = SpreadsheetCellStores.treeMap();

        final SpreadsheetEngineContext context = this.createContext(
                METADATA.set(
                        SpreadsheetMetadataPropertyName.STYLE,
                        TextStyle.EMPTY.set(
                                TextStylePropertyName.HEIGHT,
                                Length.pixel(ROW_HEIGHT)
                        )
                ),
                cellStore
        );

        final SpreadsheetStoreRepository repo = context.storeRepository();

        repo.rows()
                .saveRows(
                        Sets.of(
                                SpreadsheetRow.with(
                                        SpreadsheetSelection.parseRow("1")
                                ).setHidden(true),
                                SpreadsheetRow.with(
                                        SpreadsheetSelection.parseRow("2")
                                ).setHidden(false)
                        )
                );

        final double aHeight = 100;

        cellStore.save(
                SpreadsheetSelection.A1
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("'HiddenRow")
                        ).setStyle(
                                TextStyle.EMPTY.set(
                                        TextStylePropertyName.HEIGHT,
                                        Length.pixel(aHeight)
                                )
                        )
        );

        final double cHeight = 300;
        cellStore.save(
                SpreadsheetSelection.parseCell("C3")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("'Hello")
                        ).setStyle(
                                TextStyle.EMPTY.set(
                                        TextStylePropertyName.HEIGHT,
                                        Length.pixel(cHeight)
                                )
                        )
        );
        cellStore.save(
                SpreadsheetSelection.parseCell("D4")
                        .setFormula(
                                SpreadsheetFormula.EMPTY.setText("'Hello2")
                        )
        );
    }

    // widths top left .................................................................................................

    @Test
    public void testWindowWithLeft() {
        this.windowAndCheck(
                "A1",
                COLUMN_WIDTH,
                ROW_HEIGHT,
                "A1"
        );
    }

    @Test
    public void testWindowWithLeft2() {
        this.windowAndCheck(
                "A1",
                3 * COLUMN_WIDTH,
                ROW_HEIGHT,
                "A1:C1"
        );
    }

    @Test
    public void testWindowWithLeft3() {
        this.windowAndCheck(
                "A1",
                COLUMN_WIDTH - 1,
                ROW_HEIGHT,
                "A1"
        );
    }

    @Test
    public void testWindowWithLeft4() {
        this.windowAndCheck(
                "A1",
                COLUMN_WIDTH + 1,
                ROW_HEIGHT,
                "A1:B1"
        );
    }

    @Test
    public void testWindowWithLeft5() {
        this.windowAndCheck(
                "A1",
                COLUMN_WIDTH * 4 - 1,
                ROW_HEIGHT,
                "A1:D1"
        );
    }

    @Test
    public void testWindowWithLeft6() {
        this.windowAndCheck(
                "A1",
                COLUMN_WIDTH * 4 + 1,
                ROW_HEIGHT,
                "A1:E1"
        );
    }

    @Test
    public void testWindowWithMidX() {
        this.windowAndCheck(
                "M1",
                COLUMN_WIDTH,
                ROW_HEIGHT,
                "M1"
        );
    }

    @Test
    public void testWindowWithMidX2() {
        this.windowAndCheck(
                "M1",
                3 * COLUMN_WIDTH,
                ROW_HEIGHT,
                "M1:O1"
        );
    }

    @Test
    public void testWindowWithMidX3() {
        this.windowAndCheck(
                "M1",
                COLUMN_WIDTH - 1,
                ROW_HEIGHT,
                "M1"
        );
    }

    @Test
    public void testWindowWithMidX4() {
        this.windowAndCheck(
                "M1",
                COLUMN_WIDTH + 1,
                ROW_HEIGHT,
                "M1:N1"
        );
    }

    @Test
    public void testWindowWithMidX5() {
        this.windowAndCheck(
                "M1",
                COLUMN_WIDTH * 4 - 1,
                ROW_HEIGHT,
                "M1:P1"
        );
    }

    @Test
    public void testWindowWithMidX6() {
        this.windowAndCheck(
                "M1",
                COLUMN_WIDTH * 4 + 1,
                ROW_HEIGHT,
                "M1:Q1"
        );
    }

    // widths top right .................................................................................................

    @Test
    public void testWindowWithRight() {
        this.windowAndCheck(
                "XFD1",
                COLUMN_WIDTH,
                ROW_HEIGHT,
                "XFD1"
        );
    }

    @Test
    public void testWindowWithRight2() {
        this.windowAndCheck(
                "XFD1",
                3 * COLUMN_WIDTH,
                ROW_HEIGHT,
                "XFB1:XFD1"
        );
    }

    @Test
    public void testWindowWithRight3() {
        this.windowAndCheck(
                "XFD1",
                COLUMN_WIDTH - 1,
                ROW_HEIGHT,
                "XFD1"
        );
    }

    @Test
    public void testWindowWithRight4() {
        this.windowAndCheck(
                "XFD1",
                COLUMN_WIDTH + 1,
                ROW_HEIGHT,
                "XFC1:XFD1"
        );
    }

    @Test
    public void testWindowWithRight5() {
        this.windowAndCheck(
                "XFD1",
                COLUMN_WIDTH * 4 - 1,
                ROW_HEIGHT,
                "XFA1:XFD1"
        );
    }

    @Test
    public void testWindowWithRight6() {
        this.windowAndCheck(
                "XFD1",
                COLUMN_WIDTH * 4 + 1,
                ROW_HEIGHT,
                "XEZ1:XFD1"
        );
    }

    // heights top left .................................................................................................

    @Test
    public void testWindowWithTop() {
        this.windowAndCheck(
                "A1",
                COLUMN_WIDTH,
                ROW_HEIGHT,
                "A1"
        );
    }

    @Test
    public void testWindowWithTop2() {
        this.windowAndCheck(
                "A1",
                COLUMN_WIDTH,
                3 * ROW_HEIGHT,
                "A1:A3"
        );
    }

    @Test
    public void testWindowWithTop3() {
        this.windowAndCheck(
                "A1",
                COLUMN_WIDTH,
                ROW_HEIGHT - 1,
                "A1"
        );
    }

    @Test
    public void testWindowWithTop4() {
        this.windowAndCheck(
                "A1",
                COLUMN_WIDTH,
                ROW_HEIGHT + 1,
                "A1:A2"
        );
    }

    @Test
    public void testWindowWithTop5() {
        this.windowAndCheck(
                "A1",
                COLUMN_WIDTH,
                ROW_HEIGHT * 4 - 1,
                "A1:A4"
        );
    }

    @Test
    public void testWindowWithTop6() {
        this.windowAndCheck(
                "A1",
                COLUMN_WIDTH,
                ROW_HEIGHT * 4 + 1,
                "A1:A5"
        );
    }

    @Test
    public void testWindowWithMidY() {
        this.windowAndCheck(
                "A10",
                COLUMN_WIDTH,
                ROW_HEIGHT,
                "A10"
        );
    }

    @Test
    public void testWindowWithMidY2() {
        this.windowAndCheck(
                "A10",
                COLUMN_WIDTH,
                3 * ROW_HEIGHT,
                "A10:A12"
        );
    }

    @Test
    public void testWindowWithMidY3() {
        this.windowAndCheck(
                "A10",
                COLUMN_WIDTH,
                ROW_HEIGHT - 1,
                "A10"
        );
    }

    @Test
    public void testWindowWithMidY4() {
        this.windowAndCheck(
                "A10",
                COLUMN_WIDTH,
                ROW_HEIGHT + 1,
                "A10:A11"
        );
    }

    @Test
    public void testWindowWithMidY5() {
        this.windowAndCheck(
                "A10",
                COLUMN_WIDTH,
                ROW_HEIGHT * 4 - 1,
                "A10:A13"
        );
    }

    @Test
    public void testWindowWithMidY6() {
        this.windowAndCheck(
                "A10",
                COLUMN_WIDTH,
                ROW_HEIGHT * 4 + 1,
                "A10:A14"
        );
    }

    // heights top right .................................................................................................

    @Test
    public void testWindowWithBottom() {
        this.windowAndCheck(
                "A1048576",
                COLUMN_WIDTH,
                ROW_HEIGHT,
                "A1048576"
        );
    }

    @Test
    public void testWindowWithBottom2() {
        this.windowAndCheck(
                "A1048576",
                COLUMN_WIDTH,
                3 * ROW_HEIGHT,
                "A1048574:A1048576"
        );
    }

    @Test
    public void testWindowWithBottom3() {
        this.windowAndCheck(
                "A1048576",
                COLUMN_WIDTH,
                ROW_HEIGHT - 1,
                "A1048576"
        );
    }

    @Test
    public void testWindowWithBottom4() {
        this.windowAndCheck(
                "A1048576",
                COLUMN_WIDTH,
                ROW_HEIGHT + 1,
                "A1048575:A1048576"
        );
    }

    @Test
    public void testWindowWithBottom5() {
        this.windowAndCheck(
                "A1048576",
                COLUMN_WIDTH,
                ROW_HEIGHT * 4 - 1,
                "A1048573:A1048576"
        );
    }

    @Test
    public void testWindowWithBottom6() {
        this.windowAndCheck(
                "A1048576",
                COLUMN_WIDTH,
                ROW_HEIGHT * 4 + 1,
                "A1048572:A1048576"
        );
    }

    // window with selection within.....................................................................................

    @Test
    public void testWindowWithSelectionCellWithin() {
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseCell("B2"),
                "B2:D4"
        );
    }

    @Test
    public void testWindowWithSelectionCellWithin2() {
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseCell("C3"),
                "B2:D4"
        );
    }

    @Test
    public void testWindowWithSelectionCellWithin3() {
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseCell("D4"),
                "B2:D4"
        );
    }

    @Test
    public void testWindowWithSelectionColumnWithin() {
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseColumn("B"),
                "B2:D4"
        );
    }

    @Test
    public void testWindowWithSelectionColumnWithin2() {
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseColumn("B"),
                "B2:D4"
        );
    }

    @Test
    public void testWindowWithSelectionColumnWithin3() {
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseColumn("C"),
                "B2:D4"
        );
    }

    @Test
    public void testWindowWithSelectionRowWithin() {
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseRow("2"),
                "B2:D4"
        );
    }

    @Test
    public void testWindowWithSelectionRowWithin2() {
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseRow("3"),
                "B2:D4"
        );
    }

    @Test
    public void testWindowWithSelectionRowWithin3() {
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseRow("4"),
                "B2:D4"
        );
    }

    // window Selection Outside..........................................................................................

    @Test
    public void testWindowWithSelectionCellLeft() {
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseCell("A2"),
                "A2:C4"
        );
    }

    @Test
    public void testWindowWithSelectionCellLeft2() {
        this.windowAndCheck(
                "C3",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseCell("A3"),
                "A3:C5"
        );
    }

    @Test
    public void testWindowWithSelectionCellRight() {
        // B2:D4 -> 1
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseCell("E2"),
                "C2:E4"
        );
    }

    @Test
    public void testWindowWithSelectionCellRight2() {
        // B2:D4 -> 2
        // BCD:234
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseCell("F2"),
                "D2:F4"
        );
    }

    @Test
    public void testWindowWithSelectionCellTop() {
        // BCD:234
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseCell("B1"),
                "B1:D3"
        );
    }

    @Test
    public void testWindowWithSelectionCellTop2() {
        this.windowAndCheck(
                "C3",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseCell("C1"),
                "C1:E3"
        );
    }

    @Test
    public void testWindowWithSelectionCellBottom() {
        // B2:D4 -> 1
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseCell("B5"),
                "B3:D5"
        );
    }

    @Test
    public void testWindowWithSelectionCellBottom2() {
        // B2:D4 -> 2
        // BCD:234
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseCell("B6"),
                "B4:D6"
        );
    }

    @Test
    public void testWindowWithSelectionCellTopLeft() {
        // C3:E5
        this.windowAndCheck(
                "C3",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseCell("A2"),
                "A2:C4"
        );
    }

    @Test
    public void testWindowWithSelectionCellBottomRight() {
        // C3:E5
        this.windowAndCheck(
                "C3",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseCell("G6"),
                "E4:G6"
        );
    }

    @Test
    public void testWindowWithSelectionColumnLeft() {
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseColumn("A"),
                "A2:C4"
        );
    }

    @Test
    public void testWindowWithSelectionColumnLeft2() {
        this.windowAndCheck(
                "C3",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseColumn("A"),
                "A3:C5"
        );
    }

    @Test
    public void testWindowWithSelectionColumnRight() {
        // B2:D4 -> 1
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseColumn("E"),
                "C2:E4"
        );
    }

    @Test
    public void testWindowWithSelectionColumnRight2() {
        // B2:D4 -> 2
        // BCD:234
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseColumn("F"),
                "D2:F4"
        );
    }

    @Test
    public void testWindowWithSelectionRowTop() {
        // BCD:234
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseRow("1"),
                "B1:D3"
        );
    }

    @Test
    public void testWindowWithSelectionRowTop2() {
        this.windowAndCheck(
                "C3",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseRow("1"),
                "C1:E3"
        );
    }

    @Test
    public void testWindowWithSelectionRowBottom() {
        // B2:D4 -> 1
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseRow("5"),
                "B3:D5"
        );
    }

    @Test
    public void testWindowWithSelectionRowBottom2() {
        // B2:D4 -> 2
        // BCD:234
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseRow("6"),
                "B4:D6"
        );
    }

    @Test
    public void testWindowWithSelectionColumnReferenceLeft() {
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseColumnRange("A"),
                "A2:C4"
        );
    }

    @Test
    public void testWindowWithSelectionColumnReferenceLeft2() {
        this.windowAndCheck(
                "C3",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseColumn("A"),
                "A3:C5"
        );
    }

    @Test
    public void testWindowWithSelectionColumnReferenceLeft3() {
        this.windowAndCheck(
                "C3",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseColumn("B"),
                "B3:D5"
        );
    }

    @Test
    public void testWindowWithSelectionColumnReferenceReferenceRight() {
        // B2:D4 -> 1
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseColumn("E"),
                "C2:E4"
        );
    }

    @Test
    public void testWindowWithSelectionColumnReferenceReferenceRight2() {
        // B2:D4 -> 1
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseColumn("F"),
                "D2:F4"
        );
    }

    @Test
    public void testWindowWithSelectionColumnReferenceReferenceRight3() {
        // B2:D4 -> 2
        // BCD:234
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseColumn("F"),
                "D2:F4"
        );
    }

    @Test
    public void testWindowWithSelectionColumnReferenceReferenceRight4() {
        // B2:D4 -> 2
        // BCD:234
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseColumn("G"),
                "E2:G4"
        );
    }

    @Test
    public void testWindowWithSelectionCellRangeLeft() {
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseCellRange("A2"),
                "A2:C4"
        );
    }

    @Test
    public void testWindowWithSelectionCellRangeLeft2() {
        this.windowAndCheck(
                "C3",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseCellRange("A3"),
                "A3:C5"
        );
    }

    @Test
    public void testWindowWithSelectionCellRangeRight() {
        // B2:D4 -> 1
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseCellRange("E2"),
                "C2:E4"
        );
    }

    @Test
    public void testWindowWithSelectionCellRangeRight2() {
        // B2:D4 -> 2
        // BCD:234
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseCellRange("F2"),
                "D2:F4"
        );
    }

    @Test
    public void testWindowWithSelectionCellRangeTop() {
        // BCD:234
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseCellRange("B1"),
                "B1:D3"
        );
    }

    @Test
    public void testWindowWithSelectionCellRangeTop2() {
        this.windowAndCheck(
                "C3",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseCellRange("C1"),
                "C1:E3"
        );
    }

    @Test
    public void testWindowWithSelectionCellRangeBottom() {
        // B2:D4 -> 1
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseCellRange("B5"),
                "B3:D5"
        );
    }

    @Test
    public void testWindowWithSelectionCellRangeBottom2() {
        // B2:D4 -> 2
        // BCD:234
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseCellRange("B6"),
                "B4:D6"
        );
    }

    @Test
    public void testWindowWithSelectionCellRangeTopLeft() {
        // C3:E5
        this.windowAndCheck(
                "C3",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseCellRange("A2"),
                "A2:C4"
        );
    }

    @Test
    public void testWindowWithSelectionCellRangeBottomRight() {
        // C3:E5
        this.windowAndCheck(
                "C3",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                SpreadsheetSelection.parseCellRange("G6"),
                "E4:G6"
        );
    }

    // window column/row hidden.........................................................................................

    @Test
    public void testWindowWithColumnHidden() {
        final SpreadsheetViewportRectangle viewportRectangle = SpreadsheetViewportRectangle.with(
                SpreadsheetSelection.A1,
                COLUMN_WIDTH * 4,
                ROW_HEIGHT * 2
        );

        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        this.windowAndCheck(
                engine,
                viewportRectangle,
                false, // includeFrozenColumnsRows
                SpreadsheetEngine.NO_SELECTION,
                context,
                "A1:D2"
        );

        final SpreadsheetColumnStore columnStore = context.storeRepository()
                .columns();

        columnStore.save(
                SpreadsheetSelection.parseColumn("A")
                        .column()
                        .setHidden(true)
        );

        columnStore.save(
                SpreadsheetSelection.parseColumn("B")
                        .column()
                        .setHidden(true)
        );

        this.windowAndCheck(
                engine,
                viewportRectangle,
                false, // includeFrozenColumnsRows
                SpreadsheetEngine.NO_SELECTION,
                context,
                "A1:F2"
        );
    }

    @Test
    public void testWindowWithRowHidden() {
        final SpreadsheetViewportRectangle viewportRectangle = SpreadsheetViewportRectangle.with(
                SpreadsheetSelection.A1,
                COLUMN_WIDTH * 4,
                ROW_HEIGHT * 2
        );

        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        this.windowAndCheck(
                engine,
                viewportRectangle,
                false, // includeFrozenColumnsRows
                SpreadsheetEngine.NO_SELECTION,
                context,
                "A1:D2"
        );

        final SpreadsheetRowStore rowStore = context.storeRepository()
                .rows();

        rowStore.save(
                SpreadsheetSelection.parseRow("1")
                        .row()
                        .setHidden(true)
        );

        rowStore.save(
                SpreadsheetSelection.parseRow("2")
                        .row()
                        .setHidden(true)
        );

        this.windowAndCheck(
                engine,
                viewportRectangle,
                false, // includeFrozenColumnsRows
                SpreadsheetEngine.NO_SELECTION,
                context,
                "A1:D4"
        );
    }

    // window helpers....................................................................................................

    private void windowAndCheck(final String cellOrLabel,
                                final double width,
                                final double height,
                                final String range) {
        this.windowAndCheck(
                cellOrLabel,
                width,
                height,
                SpreadsheetEngine.NO_SELECTION,
                range
        );
    }

    private void windowAndCheck(final String cellOrLabel,
                                final double width,
                                final double height,
                                final SpreadsheetSelection selection,
                                final String range) {
        this.windowAndCheck(
                cellOrLabel,
                width,
                height,
                Optional.of(selection),
                range
        );
    }

    private void windowAndCheck(final String cell,
                                final double width,
                                final double height,
                                final Optional<SpreadsheetSelection> selection,
                                final String range) {
        this.windowAndCheck(
                this.createSpreadsheetEngine(),
                SpreadsheetSelection.parseCell(cell)
                        .viewportRectangle(
                                width,
                                height
                        ),
                false, // includeFrozenColumnsAndRows
                selection,
                this.createContext(),
                range
        );
    }

    // window with frozen columns / rows.................................................................................

    @Test
    public void testWindowWithIgnoreFrozenColumnsFrozenRows() {
        this.windowAndCheck(
                "A1",
                COLUMN_WIDTH * 4,
                ROW_HEIGHT * 3,
                0, // frozenColumns
                0, // frozenRows
                SpreadsheetEngine.NO_SELECTION,
                "A1:D3"
        );
    }

    @Test
    public void testWindowWithIgnoreFrozenColumnsFrozenRows2() {
        this.windowAndCheck(
                "B2",
                COLUMN_WIDTH * 4,
                ROW_HEIGHT * 3,
                0, // frozenColumns
                0, // frozenRows
                SpreadsheetEngine.NO_SELECTION,
                "B2:E4"
        );
    }

    // window with frozen columns / rows.................................................................................

    @Test
    public void testWindowWithFrozenColumnsFrozenRows() {
        this.windowAndCheck(
                "Z99",
                COLUMN_WIDTH * 2,
                ROW_HEIGHT * 2,
                2, // frozenColumns
                2, // frozenRows
                "A1:B2"
        );
    }

    @Test
    public void testWindowWithFrozenColumnsFrozenRows2() {
        this.windowAndCheck(
                "Z99",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                3, // frozenColumns
                3, // frozenRows
                "A1:C3"
        );
    }

    @Test
    public void testWindowWithFrozenColumnsFrozenRowsOnly() {
        this.windowAndCheck(
                "Z99",
                COLUMN_WIDTH * 2,
                ROW_HEIGHT * 2,
                9, // frozenColumns
                9, // frozenRows
                "A1:B2"
        );
    }

    @Test
    public void testWindowWithFrozenColumnsFrozenRowsOnly2() {
        this.windowAndCheck(
                "Z99",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                9, // frozenColumns
                9, // frozenRows
                "A1:C3"
        );
    }

    // A1
    // A2
    // A3
    @Test
    public void testWindowWithFrozenColumnsOnlyInvalidOverlappingHome() {
        this.windowAndCheck(
                "A1",
                COLUMN_WIDTH * 1,
                ROW_HEIGHT * 3,
                1, // frozenColumns
                0, // frozenRows
                "A1:A3"
        );
    }

    // A1
    // A2
    // A3
    @Test
    public void testWindowWithFrozenColumnsOnly() {
        this.windowAndCheck(
                "B1",
                COLUMN_WIDTH * 1,
                ROW_HEIGHT * 3,
                1, // frozenColumns
                0, // frozenRows
                "A1:A3"
        );
    }

    // A1 B1
    // A2 B2
    // A3 B3
    @Test
    public void testWindowWithFrozenColumnsOnly2() {
        this.windowAndCheck(
                "B1",
                COLUMN_WIDTH * 2,
                ROW_HEIGHT * 3,
                2, // frozenColumns
                0, // frozenRows
                "A1:B3"
        );
    }

    // A1 B1 C1
    // A2 B2 C2
    // A3 B3 C3
    @Test
    public void testWindowWithFrozenColumnsOnly3() {
        this.windowAndCheck(
                "B1",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                3, // frozenColumns
                0, // frozenRows
                "A1:C3"
        );
    }

    // A1 B1 C1
    // A2 B2 C2
    // A3 B3 C3
    @Test
    public void testWindowWithFrozenColumnsOnly4() {
        this.windowAndCheck(
                "B1",
                COLUMN_WIDTH * 3,
                ROW_HEIGHT * 3,
                99, // frozenColumns
                0, // frozenRows
                "A1:C3"
        );
    }

    // A1  b1 c1 d1
    // A2  b2 c2 d2
    // A3  b3 c3 d3
    @Test
    public void testWindowWithFrozenColumns() {
        this.windowAndCheck(
                "B1",
                COLUMN_WIDTH * 4,
                ROW_HEIGHT * 3,
                1, // frozenColumns
                0, // frozenRows
                "A1:A3,B1:D3"
        );
    }

    // A1 B1  c1 d1
    // A2 B2  c2 d2
    // A3 B3  c3 d3
    @Test
    public void testWindowWithFrozenColumns2() {
        this.windowAndCheck(
                "c1",
                COLUMN_WIDTH * 4,
                ROW_HEIGHT * 3,
                2, // frozenColumns
                0, // frozenRows
                "A1:B3,C1:D3"
        );
    }

    // A1 B1  f1 g1
    // A2 B2  f2 g2
    // A3 B3  f3 g3
    @Test
    public void testWindowWithFrozenColumnsNonFrozenGap() {
        this.windowAndCheck(
                "f1",
                COLUMN_WIDTH * 4,
                ROW_HEIGHT * 3,
                2, // frozenColumns
                0, // frozenRows
                "A1:B3,F1:G3"
        );
    }

    // A1 B1 C1 D1
    @Test
    public void testWindowWithFrozenRowsOnlyInvalidOverlappingHome() {
        this.windowAndCheck(
                "A1",
                COLUMN_WIDTH * 4,
                ROW_HEIGHT * 1,
                0, // frozenColumns
                1, // frozenRows
                "A1:D1"
        );
    }

    // A1 B1 C1 D1
    @Test
    public void testWindowWithFrozenRowsOnly() {
        this.windowAndCheck(
                "A2",
                COLUMN_WIDTH * 4,
                ROW_HEIGHT * 1,
                0, // frozenColumns
                1, // frozenRows
                "A1:D1"
        );
    }

    // A1 B1 C1 D1
    // A2 B2 C2 D2
    @Test
    public void testWindowWithFrozenRowsOnly2() {
        this.windowAndCheck(
                "A2",
                COLUMN_WIDTH * 4,
                ROW_HEIGHT * 2,
                0, // frozenColumns
                2, // frozenRows
                "A1:D2"
        );
    }

    // A1 B1 C1 D1
    // A2 B2 C2 D2
    @Test
    public void testWindowWithFrozenRowsOnly3() {
        this.windowAndCheck(
                "A2",
                COLUMN_WIDTH * 4,
                ROW_HEIGHT * 2,
                0, // frozenColumns
                99, // frozenRows
                "A1:D2"
        );
    }

    // A1 B1 C1 D1
    //
    // a2 b2 c2 d2
    // a3 b3 c3 d3
    @Test
    public void testWindowWithFrozenRows() {
        this.windowAndCheck(
                "A2",
                COLUMN_WIDTH * 4,
                ROW_HEIGHT * 3,
                0, // frozenColumns
                1, // frozenRows
                "A1:D1,A2:D3"
        );
    }


    // A1 B1 C1 D1
    // A2 B2 C2 D2
    //
    // a3 b3 c3 d3
    @Test
    public void testWindowWithFrozenRows2() {
        this.windowAndCheck(
                "a3",
                COLUMN_WIDTH * 4,
                ROW_HEIGHT * 3,
                0, // frozenColumns
                2, // frozenRows
                "A1:D2,A3:D3"
        );
    }


    // A1 B1 C1 D1
    // A2 B2 C2 D2
    //
    // a3 b3 c3 d3
    // a4 b4 c4 d4
    // a5 b5 c5 d5
    @Test
    public void testWindowWithFrozenRowsNonFrozenGap() {
        this.windowAndCheck(
                "a3",
                COLUMN_WIDTH * 4,
                ROW_HEIGHT * 5,
                0, // frozenColumns
                2, // frozenRows
                "A1:D2,A3:D5"
        );
    }

    // A1 B1 C1 D1
    // A2 B2 C2 D2
    //
    // a6 b6 c6 d6
    // a7 b7 c7 d7
    // a8 b8 c8 d8
    @Test
    public void testWindowWithFrozenRowsNonFrozenGap2() {
        this.windowAndCheck(
                "a6",
                COLUMN_WIDTH * 4,
                ROW_HEIGHT * 5,
                0, // frozenColumns
                2, // frozenRows
                "A1:D2,A6:D8"
        );
    }

    // A1 B1 C1 D1
    // A2 B2 C2 D2
    //
    // a3 b3 c3 d3
    // a4 b4 c4 d4
    // a5 b5 c5 d5
    @Test
    public void testWindowWithOnlyFrozenRowsInvalidHome() {
        this.windowAndCheck(
                "A1",
                COLUMN_WIDTH * 4,
                ROW_HEIGHT * 5,
                0, // frozenColumns
                2, // frozenRows
                "A1:D2,A3:D5"
        );
    }

    // A1   B1 C1 D1
    //
    // A2   b2 c2 d2
    // A3   b3 c3 d3
    // A4   b4 c4 d4
    @Test
    public void testWindowWithFrozenColumnsFrozenRowsNonFrozen() {
        this.windowAndCheck(
                "b2",
                COLUMN_WIDTH * 4,
                ROW_HEIGHT * 4,
                1, // frozenColumns
                1, // frozenRows
                "A1,B1:D1,A2:A4,B2:D4"
        );
    }

    // A1   B1 C1 D1
    // A2   B2 C2 D2
    //
    // A3   b3 c3 d3
    // A4   b4 c4 d4
    @Test
    public void testWindowWithFrozenColumnsFrozenRowsNonFrozen2() {
        this.windowAndCheck(
                "b3",
                COLUMN_WIDTH * 4,
                ROW_HEIGHT * 4,
                1, // frozenColumns
                2, // frozenRows
                "A1:A2,B1:D2,A3:A4,B3:D4"
        );
    }

    // A1 B1   C1 D1
    //
    // A2 B2   c2 d2
    // A3 B3   c3 d3
    // A4 B4   c4 d4
    @Test
    public void testWindowWithFrozenColumnsFrozenRowsNonFrozen3() {
        this.windowAndCheck(
                "c2",
                COLUMN_WIDTH * 4,
                ROW_HEIGHT * 4,
                2, // frozenColumns
                1, // frozenRows
                "A1:B1,C1:D1,A2:B4,C2:D4"
        );
    }

    // A1 B1   C1 D1
    // A2 B2   c2 d2
    //
    // A3 B3   c3 d3
    // A4 B4   c4 d4
    @Test
    public void testWindowWithFrozenColumnsFrozenRowsNonFrozen4() {
        this.windowAndCheck(
                "c3",
                COLUMN_WIDTH * 4,
                ROW_HEIGHT * 4,
                2, // frozenColumns
                2, // frozenRows
                "A1:B2,C1:D2,A3:B4,C3:D4"
        );
    }

    // A1 B1   C1 D1
    // A2 B2   c2 d2
    //
    // A3 B3   c3 d3
    // A4 B4   c4 d4
    @Test
    public void testWindowWithFrozenColumnsFrozenRowsNonFrozenInvalidHome() {
        this.windowAndCheck(
                "A1",
                COLUMN_WIDTH * 4,
                ROW_HEIGHT * 4,
                2, // frozenColumns
                2, // frozenRows
                "A1:B2,C1:D2,A3:B4,C3:D4"
        );
    }

    // A1 B1   C1 D1
    // A2 B2   C2 D2
    // A3 B3   C3 D3
    //
    // A8 B8   c8 d8

    @Test
    public void testWindowWithFrozenColumnsFrozenRowsNonFrozenGap() {
        this.windowAndCheck(
                "c8",
                COLUMN_WIDTH * 4,
                ROW_HEIGHT * 4,
                2, // frozenColumns
                3, // frozenRows
                "A1:B3,C1:D3,A8:B8,C8:D8"
        );
    }

    // A1 B1   C1 D1
    // A2 B2   C2 D2
    // A3 B3   C3 D3
    //
    // A8 B8   c8 d8
    // A9 B9   c9 d9

    @Test
    public void testWindowWithFrozenColumnsFrozenRowsNonFrozenGap2() {
        this.windowAndCheck(
                "c8",
                COLUMN_WIDTH * 4,
                ROW_HEIGHT * 5,
                2, // frozenColumns
                3, // frozenRows
                "A1:B3,C1:D3,A8:B9,C8:D9"
        );
    }


    private void windowAndCheck(final String cellOrLabel,
                                final double width,
                                final double height,
                                final int frozenColumns,
                                final int frozenRows,
                                final String range) {
        this.windowAndCheck(
                cellOrLabel,
                width,
                height,
                frozenColumns,
                frozenRows,
                SpreadsheetEngine.NO_SELECTION,
                range
        );
    }

    // window selection .................................................................................................

    // A1 B1 C1   D1 E1
    // A2 B2 C2   D2 E2
    // A3 B3 C3   D3 E3
    //
    // A4 B4 C4   D4 E4
    // A5 B5 C5   D5 E5
    @Test
    public void testWindowWithFrozenColumnsFrozenRowsNonFrozenFrozenColumn() {
        this.windowAndCheck(
                "D4",
                COLUMN_WIDTH * 5,
                ROW_HEIGHT * 5,
                3, // frozenColumns
                3, // frozenRows
                SpreadsheetSelection.parseColumn("A"),
                "A1:C3,D1:E3,A4:C5,D4:E5"
        );
    }

    @Test
    public void testWindowWithFrozenColumnsFrozenRowsNonFrozenNonFrozenColumn() {
        this.windowAndCheck(
                "D4",
                COLUMN_WIDTH * 5,
                ROW_HEIGHT * 5,
                3, // frozenColumns
                3, // frozenRows
                SpreadsheetSelection.parseColumn("D"),
                "A1:C3,D1:E3,A4:C5,D4:E5"
        );
    }

    @Test
    public void testWindowWithFrozenColumnsFrozenRowsNonFrozenFrozenRow() {
        this.windowAndCheck(
                "D4",
                COLUMN_WIDTH * 5,
                ROW_HEIGHT * 5,
                3, // frozenColumns
                3, // frozenRows
                SpreadsheetSelection.parseRow("1"),
                "A1:C3,D1:E3,A4:C5,D4:E5"
        );
    }

    @Test
    public void testWindowWithFrozenColumnsFrozenRowsNonFrozenNonFrozenRow() {
        this.windowAndCheck(
                "D4",
                COLUMN_WIDTH * 5,
                ROW_HEIGHT * 5,
                3, // frozenColumns
                3, // frozenRows
                SpreadsheetSelection.parseRow("4"),
                "A1:C3,D1:E3,A4:C5,D4:E5"
        );
    }

    @Test
    public void testWindowWithFrozenColumnsFrozenRowsNonFrozenFrozenCell() {
        this.windowAndCheck(
                "D4",
                COLUMN_WIDTH * 5,
                ROW_HEIGHT * 5,
                3, // frozenColumns
                3, // frozenRows
                SpreadsheetSelection.A1,
                "A1:C3,D1:E3,A4:C5,D4:E5"
        );
    }

    @Test
    public void testWindowWithFrozenColumnsFrozenRowsNonFrozenNonFrozenCell() {
        this.windowAndCheck(
                "D4",
                COLUMN_WIDTH * 5,
                ROW_HEIGHT * 5,
                3, // frozenColumns
                3, // frozenRows
                SpreadsheetSelection.parseCell("D4"),
                "A1:C3,D1:E3,A4:C5,D4:E5"
        );
    }

    @Test
    public void testWindowWithFrozenColumnsFrozenRowsNonFrozenNonFrozenCell2() {
        this.windowAndCheck(
                "D4",
                COLUMN_WIDTH * 5,
                ROW_HEIGHT * 5,
                3, // frozenColumns
                3, // frozenRows
                SpreadsheetSelection.parseCell("E5"),
                "A1:C3,D1:E3,A4:C5,D4:E5"
        );
    }

    // window selection pan.............................................................................................

    // A1 B1 C1   D1 E1
    // A2 B2 C2   D2 E2
    // A3 B3 C3   D3 E3
    //
    // A4 B4 C4   D4 E4 F4
    // A5 B5 C5   D5 E5

    @Test
    public void testWindowWithFrozenColumnsFrozenRowsNonFrozenPanNonFrozenCell() {
        this.windowAndCheck(
                "D4",
                COLUMN_WIDTH * 5,
                ROW_HEIGHT * 5,
                3, // frozenColumns
                3, // frozenRows
                SpreadsheetSelection.parseCell("F4"),
                "A1:C3,D1:E3,A4:C5,E4:F5"
        );
    }

    @Test
    public void testWindowWithFrozenColumnsFrozenRowsNonFrozenPanNonFrozenCell2() {
        this.windowAndCheck(
                "D4",
                COLUMN_WIDTH * 5,
                ROW_HEIGHT * 5,
                3, // frozenColumns
                3, // frozenRows
                SpreadsheetSelection.parseCell("G4"),
                "A1:C3,D1:E3,A4:C5,F4:G5"
        );
    }

    // A1 B1   C1  D1 E1
    // A2 B2   C2  D2 E2
    //
    // A3 B3   C3  D3 E3
    //
    // A4 B4   C4  D4 E4
    // A5 B5   C5  D5 E5
    @Test
    public void testWindowWithFrozenColumnsFrozenRowsNonFrozenPanNonFrozenCellPartialWidth() {
        this.windowAndCheck(
                "C4",
                COLUMN_WIDTH * 4 - 2,
                ROW_HEIGHT * 4,
                2, // frozenColumns
                2, // frozenRows
                SpreadsheetSelection.parseCell("D4"),
                "A1:B2,D1:E2,A4:B5,D4:E5"
        );
    }

    @Test
    public void testWindowWithFrozenColumnsFrozenRowsNonFrozenPanNonFrozenCellPartialWidth2() {
        this.windowAndCheck(
                "C4",
                COLUMN_WIDTH * 4 - 1,
                ROW_HEIGHT * 4,
                2, // frozenColumns
                2, // frozenRows
                SpreadsheetSelection.parseCell("D4"),
                "A1:B2,D1:E2,A4:B5,D4:E5"
        );
    }

    // A1 B1   C1  D1 E1
    // A2 B2   C2  D2 E2
    //
    // A3 B3   C3  D3 E3
    //
    // A4 B4   C4  D4 E4
    // A5 B5   C5  D5 E5
    @Test
    public void testWindowWithFrozenColumnsFrozenRowsNonFrozenPanNonFrozenCellPartialHeight() {
        this.windowAndCheck(
                "D3",
                COLUMN_WIDTH * 4,
                ROW_HEIGHT * 4 - 2,
                2, // frozenColumns
                2, // frozenRows
                SpreadsheetSelection.parseCell("D4"),
                "A1:B2,D1:E2,A4:B5,D4:E5"
        );
    }

    @Test
    public void testWindowWithFrozenColumnsFrozenRowsNonFrozenPanNonFrozenCellPartialHeight2() {
        this.windowAndCheck(
                "D3",
                COLUMN_WIDTH * 4,
                ROW_HEIGHT * 4 - 1,
                2, // frozenColumns
                2, // frozenRows
                SpreadsheetSelection.parseCell("D4"),
                "A1:B2,D1:E2,A4:B5,D4:E5"
        );
    }

    private void windowAndCheck(final String cell,
                                final double width,
                                final double height,
                                final int frozenColumns,
                                final int frozenRows,
                                final SpreadsheetSelection selection,
                                final String window) {
        this.windowAndCheck(
                cell,
                width,
                height,
                frozenColumns,
                frozenRows,
                Optional.of(selection),
                window
        );
    }

    private void windowAndCheck(final String cell,
                                final double width,
                                final double height,
                                final int frozenColumns,
                                final int frozenRows,
                                final Optional<SpreadsheetSelection> selection,
                                final String window) {
        final SpreadsheetMetadata metadata = METADATA.setOrRemove(
                SpreadsheetMetadataPropertyName.FROZEN_COLUMNS,
                frozenColumns > 0 ?
                        SpreadsheetReferenceKind.RELATIVE.firstColumn().columnRange(SpreadsheetReferenceKind.RELATIVE.column(frozenColumns - 1)) :
                        null
        ).setOrRemove(
                SpreadsheetMetadataPropertyName.FROZEN_ROWS,
                frozenRows > 0 ?
                        SpreadsheetReferenceKind.RELATIVE.firstRow().rowRange(SpreadsheetReferenceKind.RELATIVE.row(frozenRows - 1)) :
                        null
        );

        this.windowAndCheck(
                this.createSpreadsheetEngine(),
                SpreadsheetSelection.parseCell(cell)
                        .viewportRectangle(
                                width,
                                height
                        ),
                true, // includeFrozenColumnsAndRows
                selection,
                this.createContext(metadata),
                window
        );
    }

    @Test
    public void testWindowWithSelectionLabel() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        final SpreadsheetCellReference home = SpreadsheetSelection.parseCell("B2");
        engine.saveLabel(
                LABEL.setLabelMappingReference(
                        home
                ),
                context
        );

        this.windowAndCheck(
                engine,
                home.viewportRectangle(
                        COLUMN_WIDTH * 3,
                        ROW_HEIGHT * 3
                ), // viewport
                false, // includeFrozenColumnsRows
                Optional.of(LABEL), // selection
                context,
                "B2:D4"
        );
    }

    @Test
    public void testWindowWithSelectionLabelToCellRange() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        final SpreadsheetCellReference home = SpreadsheetSelection.parseCell("B2");
        engine.saveLabel(
                LABEL.setLabelMappingReference(
                        home.toCellRange()
                ),
                context
        );

        this.windowAndCheck(
                engine,
                home.viewportRectangle(
                        COLUMN_WIDTH * 3,
                        ROW_HEIGHT * 3
                ), // viewport
                false, // includeFrozenColumnsRows
                Optional.of(LABEL), // selection
                context,
                "B2:D4"
        );
    }

    @Test
    public void testWindowWithSelectionLabelToCellRangeManyCells() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        final SpreadsheetCellReference home = SpreadsheetSelection.parseCell("B2");
        engine.saveLabel(
                LABEL.setLabelMappingReference(
                        SpreadsheetSelection.parseCellRange("B2:B3")
                ),
                context
        );

        this.windowAndCheck(
                engine,
                home.viewportRectangle(
                        COLUMN_WIDTH * 3,
                        ROW_HEIGHT * 3
                ), // viewport
                false, // includeFrozenColumnsRows
                Optional.of(LABEL), // selection
                context,
                "B2:D4"
        );
    }

    //  navigate........................................................................................................

    @Test
    public void testNavigateWithSelectionHiddenAndMissingNavigation() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        final SpreadsheetColumnReference column = SpreadsheetSelection.parseColumn("B");

        final SpreadsheetColumnStore store = context.storeRepository()
                .columns();

        store.save(
                column.column()
                        .setHidden(true)
        );

        final SpreadsheetViewport viewport = SpreadsheetSelection.A1
                .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                .viewport()
                .setAnchoredSelection(
                        Optional.of(
                                column.setDefaultAnchor()
                        )
                );

        this.navigateAndCheck(
                engine,
                viewport,
                context,
                SpreadsheetSelection.A1.viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                        .viewport()
        );
    }

    @Test
    public void testNavigateWithSelectionMissingNavigation() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        final SpreadsheetViewport viewport = SpreadsheetSelection.A1
                .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                .viewport()
                .setAnchoredSelection(
                        Optional.of(
                                SpreadsheetSelection.parseColumn("B")
                                        .setDefaultAnchor()
                        )
                );

        this.navigateAndCheck(
                engine,
                viewport,
                context,
                Optional.of(viewport)
        );
    }

    @Test
    public void testNavigateWithSelectionLabelEmptyNavigations() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        context.storeRepository()
                .labels()
                .save(LABEL.setLabelMappingReference(SpreadsheetSelection.parseCell("B2")));

        final SpreadsheetViewport viewport = SpreadsheetSelection.A1
                .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                .viewport()
                .setAnchoredSelection(
                        Optional.of(
                                LABEL.setDefaultAnchor()
                        )
                );

        this.navigateAndCheck(
                engine,
                viewport,
                context,
                viewport
        );
    }

    @Test
    public void testNavigateWithCellRightColumn() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        this.navigateAndCheck(
                engine,
                SpreadsheetSelection.A1
                        .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                        .viewport()
                        .setAnchoredSelection(
                                Optional.of(
                                        SpreadsheetSelection.parseCell("B2")
                                                .setDefaultAnchor()
                                )
                        ).setNavigations(
                                SpreadsheetViewportNavigationList.EMPTY.concat(
                                        SpreadsheetViewportNavigation.rightColumn()
                                )
                        ),
                context,
                SpreadsheetSelection.A1
                        .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                        .viewport()
                        .setAnchoredSelection(
                                Optional.of(
                                        SpreadsheetSelection.parseCell("C2")
                                                .setDefaultAnchor()
                                )
                        )
        );
    }

    @Test
    public void testNavigateWithRightPixels() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        this.navigateAndCheck(
                engine,
                SpreadsheetSelection.A1
                        .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                        .viewport()
                        .setNavigations(
                                SpreadsheetViewportNavigationList.EMPTY.concat(
                                        SpreadsheetViewportNavigation.rightPixel(
                                                3 * (int) COLUMN_WIDTH - 1
                                        )
                                )
                        ),
                context,
                SpreadsheetSelection.A1
                        .addColumn(3)
                        .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                        .viewport()

        );
    }

    @Test
    public void testNavigateWithCellRightPixels() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        final Optional<AnchoredSpreadsheetSelection> selection = Optional.of(
                SpreadsheetSelection.parseCell("B2")
                        .setDefaultAnchor()
        );

        this.navigateAndCheck(
                engine,
                SpreadsheetSelection.A1
                        .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                        .viewport()
                        .setAnchoredSelection(selection)
                        .setNavigations(
                                SpreadsheetViewportNavigationList.EMPTY.concat(
                                        SpreadsheetViewportNavigation.rightPixel(
                                                3 * (int) COLUMN_WIDTH - 1
                                        )
                                )
                        ),
                context,
                SpreadsheetSelection.A1
                        .addColumn(3)
                        .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                        .viewport()
                        .setAnchoredSelection(selection)
        );
    }

    @Test
    public void testNavigateWithLabelUnchanged() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        final SpreadsheetCellReference selection = SpreadsheetSelection.A1;

        context.storeRepository()
                .labels()
                .save(LABEL.setLabelMappingReference(selection));

        final SpreadsheetViewport viewport = SpreadsheetSelection.A1
                .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                .viewport()
                .setAnchoredSelection(
                        Optional.of(
                                selection.setDefaultAnchor()
                        )
                );

        this.navigateAndCheck(
                engine,
                viewport.setNavigations(
                        SpreadsheetViewportNavigationList.EMPTY.concat(
                                SpreadsheetViewportNavigation.leftColumn()
                        )
                ),
                context,
                viewport
        );
    }

    @Test
    public void testNavigateWithLabelToCell() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        final SpreadsheetCellReference selection = SpreadsheetSelection.A1;

        context.storeRepository()
                .labels()
                .save(LABEL.setLabelMappingReference(selection));

        this.navigateAndCheck(
                engine,
                SpreadsheetSelection.A1
                        .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                        .viewport()
                        .setAnchoredSelection(
                                Optional.of(
                                        selection.setDefaultAnchor()
                                )
                        ).setNavigations(
                                SpreadsheetViewportNavigationList.EMPTY.concat(
                                        SpreadsheetViewportNavigation.rightColumn()
                                )
                        ),
                context,
                SpreadsheetSelection.A1
                        .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                        .viewport()
                        .setAnchoredSelection(
                                Optional.of(
                                        SpreadsheetSelection.parseCell("B1")
                                                .setDefaultAnchor()
                                )
                        )
        );
    }

    @Test
    public void testNavigateWithLabelToRange() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        final SpreadsheetCellRangeReference selection = SpreadsheetSelection.parseCellRange("A1:B1");

        context.storeRepository()
                .labels()
                .save(LABEL.setLabelMappingReference(selection));

        this.navigateAndCheck(
                engine,
                SpreadsheetSelection.A1
                        .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                        .viewport()
                        .setAnchoredSelection(
                                Optional.of(
                                        selection.setAnchor(SpreadsheetViewportAnchor.TOP_LEFT)
                                )
                        ).setNavigations(
                                SpreadsheetViewportNavigationList.EMPTY.concat(
                                        SpreadsheetViewportNavigation.extendRightColumn()
                                )
                        ),
                context,
                SpreadsheetSelection.A1
                        .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                        .viewport()
                        .setAnchoredSelection(
                                Optional.of(
                                        SpreadsheetSelection.parseCellRange("A1:C1")
                                                .setAnchor(SpreadsheetViewportAnchor.TOP_LEFT)
                                )
                        )
        );
    }

    @Test
    public void testNavigateWithHiddenColumns() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        final SpreadsheetColumnStore store = context.storeRepository()
                .columns();

        store.save(
                SpreadsheetSelection.parseColumn("A")
                        .column()
                        .setHidden(true)
        );

        store.save(
                SpreadsheetSelection.parseColumn("B")
                        .column()
                        .setHidden(true)
        );

        this.navigateAndCheck(
                engine,
                SpreadsheetSelection.A1
                        .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                        .viewport()
                        .setAnchoredSelection(
                                Optional.of(
                                        SpreadsheetSelection.parseColumn("B")
                                                .setDefaultAnchor()
                                )
                        ).setNavigations(
                                SpreadsheetViewportNavigationList.EMPTY.concat(
                                        SpreadsheetViewportNavigation.leftColumn()
                                )
                        ),
                context,
                SpreadsheetSelection.A1
                        .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                        .viewport()
        );
    }

    @Test
    public void testNavigateWithSkipsHiddenColumn() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        context.storeRepository()
                .columns()
                .save(
                        SpreadsheetSelection.parseColumn("B")
                                .column()
                                .setHidden(true)
                );

        this.navigateAndCheck(
                engine,
                SpreadsheetSelection.A1
                        .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                        .viewport()
                        .setAnchoredSelection(
                                Optional.of(
                                        SpreadsheetSelection.parseColumn("A")
                                                .setDefaultAnchor()
                                )
                        )
                        .setNavigations(
                                SpreadsheetViewportNavigationList.EMPTY.concat(
                                        SpreadsheetViewportNavigation.rightColumn()
                                )
                        ),
                context,
                SpreadsheetSelection.A1
                        .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                        .viewport()
                        .setAnchoredSelection(
                                Optional.of(
                                        SpreadsheetSelection.parseColumn("C")
                                                .setDefaultAnchor()
                                )
                        )
        );
    }

    @Test
    public void testNavigateWithSkipsHiddenRow() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        context.storeRepository()
                .rows()
                .save(
                        SpreadsheetSelection.parseRow("3")
                                .row()
                                .setHidden(true)
                );

        this.navigateAndCheck(
                engine,
                SpreadsheetSelection.A1
                        .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                        .viewport()
                        .setAnchoredSelection(
                                Optional.of(
                                        SpreadsheetSelection.parseRow("2")
                                                .setDefaultAnchor()
                                )
                        )
                        .setNavigations(
                                SpreadsheetViewportNavigationList.EMPTY.concat(
                                        SpreadsheetViewportNavigation.downRow()
                                )
                        ),
                context,
                SpreadsheetSelection.A1
                        .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                        .viewport()
                        .setAnchoredSelection(
                                Optional.of(
                                        SpreadsheetSelection.parseRow("4")
                                                .setDefaultAnchor()
                                )
                        )
        );
    }

    //  helpers.........................................................................................................

    @Override
    public BasicSpreadsheetEngine createSpreadsheetEngine() {
        return BasicSpreadsheetEngine.INSTANCE;
    }

    @Override
    public SpreadsheetEngineContext createContext() {
        return this.createContext(SpreadsheetEngines.fake());
    }

    private SpreadsheetEngineContext createContext(final SpreadsheetCellStore cellStore) {
        return this.createContext(
                METADATA,
                cellStore
        );
    }

    private SpreadsheetEngineContext createContext(final SpreadsheetMetadata metadata) {
        return this.createContext(
                metadata,
                SpreadsheetCellStores.treeMap()
        );
    }

    private SpreadsheetEngineContext createContext(final SpreadsheetMetadata metadata,
                                                   final SpreadsheetCellStore cellStore) {
        return this.createContext(
                DEFAULT_YEAR,
                SpreadsheetEngines.fake(),
                metadata,
                new FakeSpreadsheetStoreRepository() {
                    @Override
                    public SpreadsheetCellStore cells() {
                        return cellStore;
                    }

                    @Override
                    public SpreadsheetColumnStore columns() {
                        return this.columnStore;
                    }

                    private final SpreadsheetColumnStore columnStore = SpreadsheetColumnStores.treeMap();

                    @Override
                    public SpreadsheetLabelStore labels() {
                        return this.labels;
                    }

                    private final SpreadsheetLabelStore labels = SpreadsheetLabelStores.treeMap();

                    @Override
                    public SpreadsheetRowStore rows() {
                        return this.rowStore;
                    }

                    private final SpreadsheetRowStore rowStore = SpreadsheetRowStores.treeMap();
                }
        );
    }

    private SpreadsheetEngineContext createContext(final SpreadsheetEngine engine) {
        return this.createContext(
                DEFAULT_YEAR,
                engine
        );
    }

    private SpreadsheetEngineContext createContext(final int defaultYear,
                                                   final SpreadsheetEngine engine) {
        return this.createContext(
                defaultYear,
                engine,
                SpreadsheetStoreRepositories.basic(
                        SpreadsheetCellStores.treeMap(),
                        SpreadsheetCellReferencesStores.treeMap(),
                        SpreadsheetColumnStores.treeMap(),
                        SpreadsheetGroupStores.fake(),
                        SpreadsheetLabelStores.treeMap(),
                        SpreadsheetExpressionReferenceStores.treeMap(),
                        SpreadsheetMetadataStores.fake(),
                        SpreadsheetCellRangeStores.treeMap(),
                        SpreadsheetCellRangeStores.treeMap(),
                        SpreadsheetRowStores.treeMap(),
                        SpreadsheetUserStores.fake()
                )
        );
    }

    private SpreadsheetEngineContext createContext(final int defaultYear,
                                                   final SpreadsheetEngine engine,
                                                   final SpreadsheetStoreRepository storeRepository) {
        return this.createContext(
                defaultYear,
                engine,
                METADATA,
                storeRepository
        );
    }

    private SpreadsheetEngineContext createContext(final int defaultYear,
                                                   final SpreadsheetEngine engine,
                                                   final SpreadsheetMetadata metadata, // required by ranges tests with frozen columns/rows.
                                                   final SpreadsheetStoreRepository storeRepository) {
        return SpreadsheetEngineContexts.basic(
                SERVER_URL,
                metadata.set(
                        SpreadsheetMetadataPropertyName.DEFAULT_YEAR,
                        defaultYear
                ),
                storeRepository,
                SpreadsheetMetadataPropertyName.FORMULA_FUNCTIONS,
                SpreadsheetProviders.basic(
                        CONVERTER_PROVIDER,
                        EXPRESSION_FUNCTION_PROVIDER,
                        SPREADSHEET_COMPARATOR_PROVIDER,
                        SPREADSHEET_EXPORTER_PROVIDER,
                        SPREADSHEET_FORMATTER_PROVIDER,
                        SPREADSHEET_IMPORTER_PROVIDER,
                        SPREADSHEET_PARSER_PROVIDER
                ), // SpreadsheetProvider
                PROVIDER_CONTEXT
        );
    }

    private SpreadsheetCell loadCellAndCheck(final SpreadsheetEngine engine,
                                             final SpreadsheetCellReference reference,
                                             final SpreadsheetEngineEvaluation evaluation,
                                             final SpreadsheetEngineContext context,
                                             final Number value) {
        return this.loadCellAndCheck(
                engine,
                reference,
                evaluation,
                context,
                EXPRESSION_NUMBER_KIND.create(value),
                value + " " + FORMATTED_PATTERN_SUFFIX
        );
    }

    /**
     * The #formattedCell overloads parse the formulaText into tokens, set a default style and format any given value.
     * The parsed formula text is never evaluated to produce a value
     */
    private SpreadsheetCell formatCell(final String cell,
                                       final String formulaText) {
        return this.formatCell(
                SpreadsheetSelection.parseCell(cell),
                formulaText
        );
    }

    private SpreadsheetCell formatCell(final SpreadsheetCellReference cell,
                                       final String formulaText) {
        return this.formatCell(
                this.cell(cell, formulaText),
                Optional.empty(),
                STYLE
        );
    }

    private SpreadsheetCell formatCell(final String cell,
                                       final String formulaText,
                                       final Object value) {
        return this.formatCell(
                SpreadsheetSelection.parseCell(cell),
                formulaText,
                value
        );
    }

    private SpreadsheetCell formatCell(final SpreadsheetCellReference cell,
                                       final String formulaText,
                                       final Object value) {
        return this.formatCell(
                this.cell(cell, formulaText),
                value
        );
    }

    /**
     * Makes a {@link SpreadsheetCell} updating the formula expression and expected value and then formats the cell adding styling etc,
     * mimicking the very actions that happen during evaluation.
     */
    private SpreadsheetCell formatCell(final SpreadsheetCell cell) {
        return this.formatCell(
                cell,
                Optional.empty(),
                STYLE
        );
    }

    private SpreadsheetCell formatCell(final SpreadsheetCell cell,
                                       final Object value) {
        return this.formatCell(
                cell,
                value,
                STYLE
        );
    }

    private SpreadsheetCell formatCell(final SpreadsheetCell cell,
                                       final Object value,
                                       final TextStyle style) {
        return this.formatCell(
                cell,
                Optional.of(
                        value instanceof Number ?
                        EXPRESSION_NUMBER_KIND.create((Number)value) :
                        value
                ),
                style
        );
    }

    private SpreadsheetCell formatCell(final SpreadsheetCell cell,
                                       final Optional<Object> value,
                                       final TextStyle style) {
        SpreadsheetCell result = cell.setFormula(
                this.parseFormula(
                        cell.formula()
                ).setValue(value)
        );

        if (value.isPresent()) {
            final TextNode formattedText = METADATA.spreadsheetFormatter(
                    SPREADSHEET_FORMATTER_PROVIDER,
                    PROVIDER_CONTEXT
            ).format(
                    value.get(),
                    SPREADSHEET_TEXT_FORMAT_CONTEXT
            ).orElseThrow(
                    () -> new AssertionError("Failed to format " + CharSequences.quoteIfChars(value.get()))
            );

            result = result.setFormattedValue(
                    Optional.of(
                            style.replace(formattedText)
                                    .root()
                    )
            );
        }

        return result;
    }

    /**
     * Assumes the formula is syntactically correct and updates the cell.
     */
    private SpreadsheetFormula parseFormula(final SpreadsheetFormula formula) {
        final String text = formula.text();

        final SpreadsheetFormulaParserToken token =
                text.isEmpty() ?
                        null :
                        SpreadsheetFormulaParsers.valueOrExpression(
                                        METADATA.spreadsheetParser(
                                                SPREADSHEET_PARSER_PROVIDER,
                                                PROVIDER_CONTEXT
                                        )
                                ).orFailIfCursorNotEmpty(ParserReporters.basic())
                                .parse(TextCursors.charSequence(text),
                                        SpreadsheetParserContexts.basic(
                                                this.dateTimeContext(),
                                                ExpressionNumberContexts.basic(
                                                        EXPRESSION_NUMBER_KIND,
                                                        this.decimalNumberContext()
                                                ),
                                                VALUE_SEPARATOR
                                        )
                                ).orElseThrow(() -> new AssertionError("Failed to parseFormula " + CharSequences.quote(text)))
                                .cast(SpreadsheetFormulaParserToken.class);
        SpreadsheetFormula parsedFormula = formula;
        if (null != token) {
            parsedFormula = parsedFormula.setToken(Optional.of(token));

            try {
                parsedFormula = parsedFormula.setExpression(
                        token.toExpression(
                                new FakeExpressionEvaluationContext() {

                                    @Override
                                    public int defaultYear() {
                                        return DEFAULT_YEAR;
                                    }

                                    @Override
                                    public ExpressionNumberKind expressionNumberKind() {
                                        return EXPRESSION_NUMBER_KIND;
                                    }

                                    @Override
                                    public MathContext mathContext() {
                                        return METADATA.mathContext();
                                    }

                                    @Override
                                    public int twoDigitYear() {
                                        return TWO_DIGIT_YEAR;
                                    }
                                }
                        )
                );
            } catch (final Exception fail) {
                parsedFormula = parsedFormula.setValue(
                        Optional.of(
                                SpreadsheetErrorKind.VALUE.setMessage(
                                        fail.getMessage()
                                )
                        )
                );
            }
        }

        return parsedFormula;
    }

    private void loadCellStoreAndCheck(final SpreadsheetCellStore store,
                                       final SpreadsheetCell... cells) {
        this.checkEquals(
                Lists.of(cells),
                store.all(),
                () -> "loaded all cells in " + store
        );
    }

    private void loadLabelStoreAndCheck(final SpreadsheetLabelStore store,
                                        final SpreadsheetLabelMapping... mappings) {
        this.checkEquals(
                Lists.of(mappings),
                store.all(),
                () -> "loaded all label mappings in " + store
        );
    }

    private <E extends SpreadsheetExpressionReference & Comparable<E>> void loadReferencesAndCheck(final SpreadsheetExpressionReferenceStore<E> store,
                                                                                                   final E cell,
                                                                                                   final SpreadsheetCellReference... out) {
        this.checkEquals(
                Optional.ofNullable(out.length == 0 ? null : Sets.of(out)),
                store.load(cell),
                () -> "references to " + cell
        );
    }

    private void findReferencesWithCellAndCheck(final SpreadsheetCellReferencesStore store,
                                                final SpreadsheetCellReference cell,
                                                final SpreadsheetCellReference... expected) {
        this.checkEquals(
                Sets.of(expected),
                store.findReferencesWithCell(
                        cell,
                        0, // offset
                        Integer.MAX_VALUE // count
                ),
                () -> "findReferences with " + cell
        );
    }

    private SpreadsheetCell cell(final String cell,
                                 final String formula) {
        return this.cell(
                SpreadsheetSelection.parseCell(cell),
                formula
        );
    }

    private SpreadsheetCell cell(final SpreadsheetCellReference cell,
                                 final String formula) {
        return this.cell(
                cell,
                SpreadsheetFormula.EMPTY
                        .setText(formula)
        );
    }

    private SpreadsheetCell cell(final SpreadsheetCellReference cell,
                                 final SpreadsheetFormula formula) {
        return cell.setFormula(formula)
                .setStyle(STYLE);
    }

    private void addCellSaveWatcherAndDeleteWatcherThatThrowsUOE(final SpreadsheetEngineContext context) {
        final SpreadsheetCellStore store = context.storeRepository()
                .cells();

        store.addSaveWatcher((ignored) -> {
            throw new UnsupportedOperationException();
        });
        store.addDeleteWatcher((ignored) -> {
            throw new UnsupportedOperationException();
        });
    }

    private SpreadsheetCell loadCellAndFormulaAndValueCheck(final SpreadsheetEngine engine,
                                                            final SpreadsheetCellReference reference,
                                                            final SpreadsheetEngineEvaluation evaluation,
                                                            final SpreadsheetEngineContext context,
                                                            final String formulaText,
                                                            final Number value) {
        return this.loadCellAndFormulaAndValueCheck(
                engine,
                reference,
                evaluation,
                context,
                formulaText,
                (Object)EXPRESSION_NUMBER_KIND.create(value)
        );
    }
    // class............................................................................................................

    @Override
    public Class<BasicSpreadsheetEngine> type() {
        return BasicSpreadsheetEngine.class;
    }

    @Override
    public String typeNameSuffix() {
        return "";
    }
}

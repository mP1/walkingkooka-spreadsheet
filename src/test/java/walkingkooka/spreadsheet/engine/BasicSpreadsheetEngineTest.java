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
import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCellFormat;
import walkingkooka.spreadsheet.SpreadsheetDescription;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetViewport;
import walkingkooka.spreadsheet.conditionalformat.SpreadsheetConditionalFormattingRule;
import walkingkooka.spreadsheet.format.FakeSpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatException;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePatterns;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.store.SpreadsheetExpressionReferenceStore;
import walkingkooka.spreadsheet.reference.store.SpreadsheetExpressionReferenceStores;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.reference.store.SpreadsheetRangeStore;
import walkingkooka.spreadsheet.reference.store.SpreadsheetRangeStores;
import walkingkooka.spreadsheet.security.store.SpreadsheetGroupStores;
import walkingkooka.spreadsheet.security.store.SpreadsheetUserStores;
import walkingkooka.spreadsheet.store.FakeSpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.repo.FakeSpreadsheetStoreRepository;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContexts;
import walkingkooka.tree.expression.ExpressionEvaluationException;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;
import walkingkooka.tree.expression.FunctionExpressionName;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionContext;
import walkingkooka.tree.expression.function.FakeExpressionFunction;
import walkingkooka.tree.expression.function.UnknownExpressionFunctionException;
import walkingkooka.tree.text.FontStyle;
import walkingkooka.tree.text.FontWeight;
import walkingkooka.tree.text.Length;
import walkingkooka.tree.text.TextDecorationLine;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;
import walkingkooka.tree.text.TextStylePropertyValueException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("PointlessArithmeticExpression")
public final class BasicSpreadsheetEngineTest extends BasicSpreadsheetEngineTestCase<BasicSpreadsheetEngine>
        implements SpreadsheetEngineTesting<BasicSpreadsheetEngine> {

    private final static String FORMATTED_PATTERN_SUFFIX = "FORMATTED_PATTERN_SUFFIX";

    private final static Optional<Color> COLOR = Optional.of(Color.BLACK);

    private final static String PATTERN = "$text+" + FORMATTED_PATTERN_SUFFIX;
    private final static String PATTERN_COLOR = "$text+" + FORMATTED_PATTERN_SUFFIX + "+" + COLOR.get();
    private final static String PATTERN_FORMAT_FAIL = "<none>";

    private final static String DATE_PATTERN = "yyyy/mm/dd";
    private final static String TIME_PATTERN = "hh:mm";
    private final static String DATETIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;
    private final static String NUMBER_PATTERN = "#";
    private final static String TEXT_PATTERN = "@";

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

        private final Converter converter = ExpressionNumber.fromConverter(
                Converters.collection(
                        Lists.of(
                                Converters.simple(),
                                Converters.localDateLocalDateTime(),
                                Converters.localTimeLocalDateTime(),
                                Converters.numberNumber(),
                                Converters.objectString()
                        )
                )
        );

        @Override
        public char decimalSeparator() {
            return '.';
        }

        @Override
        public char negativeSign() {
            return '-';
        }

        @Override
        public char positiveSign() {
            return '+';
        }

        @Override
        public MathContext mathContext() {
            return MATH_CONTEXT;
        }
    };

    private final static MathContext MATH_CONTEXT = MathContext.DECIMAL32;

    private final static int DEFAULT_YEAR = 1900;
    private final static int TWO_DIGIT_YEAR = 20;
    private final static char VALUE_SEPARATOR = ';';

    private final static SpreadsheetLabelName LABEL = SpreadsheetLabelName.labelName("Label123");
    private final static SpreadsheetCellReference LABEL_CELL = SpreadsheetCellReference.parseCellReference("Z99");

    private final static double WIDTH = 50;
    private final static double HEIGHT = 30;

    @Test
    public void testWithNullMetadataFails() {
        assertThrows(NullPointerException.class, () -> BasicSpreadsheetEngine.with(null));
    }

    // loadCell.........................................................................................................

    @Test
    public void testLoadCellCellWhenEmpty() {
        this.loadCellFailCheck(cellReference(1, 1), SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY);
    }

    @Test
    public void testLoadCellUnknown() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference reference = SpreadsheetCellReference.parseCellReference("K99");
        assertEquals(Optional.empty(), context.storeRepository().cells().load(reference));

        assertEquals(
                SpreadsheetDelta.with(SpreadsheetDelta.NO_CELLS)
                        .setLabels(SpreadsheetDelta.NO_LABELS),
                engine.loadCell(reference, SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY, context)
        );
    }

    @Test
    public void testLoadCellUnknownWithLabel() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        assertEquals(Optional.empty(), context.storeRepository().cells().load(LABEL_CELL));

        context.storeRepository()
                .labels()
                .save(LABEL.mapping(LABEL_CELL));

        assertEquals(
                SpreadsheetDelta.with(SpreadsheetDelta.NO_CELLS)
                        .setLabels(Sets.of(LABEL.mapping(LABEL_CELL))),
                engine.loadCell(LABEL_CELL, SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY, context)
        );
    }

    @Test
    public void testLoadCellSkipEvaluate() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference cellReference = this.cellReference(1, 1);
        context.storeRepository()
                .cells()
                .save(this.cell(cellReference, "=1+2"));

        this.loadCellAndCheckWithoutValueOrError(engine,
                cellReference,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context);
    }

    @Test
    public void testLoadCellWithoutFormatPattern() {
        this.cellStoreSaveAndLoadCellAndCheck(SpreadsheetCell.NO_FORMAT, FORMATTED_PATTERN_SUFFIX);
    }

    @Test
    public void testLoadCellWithFormatPattern() {
        this.cellStoreSaveAndLoadCellAndCheck(Optional.of(SpreadsheetCellFormat.with(PATTERN)),
                FORMATTED_PATTERN_SUFFIX);
    }

    @Test
    public void testLoadCellWithFormatPatternAndFormatter() {
        final String pattern = "Custom";
        final String suffix = "CustomSuffix";
        this.cellStoreSaveAndLoadCellAndCheck(Optional.of(SpreadsheetCellFormat.with(pattern)
                        .setFormatter(Optional.of(this.formatter(pattern, SpreadsheetText.WITHOUT_COLOR, suffix)))),
                suffix);
    }

    private void cellStoreSaveAndLoadCellAndCheck(final Optional<SpreadsheetCellFormat> format,
                                                  final String patternSuffix) {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference cellReference = this.cellReference(1, 1);
        context.storeRepository()
                .cells()
                .save(this.cell(cellReference, "=1+2")
                        .setFormat(format));

        this.loadCellAndCheckFormatted2(engine,
                cellReference,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                number(1 + 2),
                patternSuffix);
    }

    @Test
    public void testLoadCellComputeIfNecessaryCachesCell() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference cellReference = this.cellReference(1, 1);
        context.storeRepository()
                .cells()
                .save(this.cell(cellReference, "=1+2"));

        final SpreadsheetCell first = this.loadCellOrFail(engine,
                cellReference,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context);
        final SpreadsheetCell second = this.loadCellOrFail(engine,
                cellReference,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context);

        assertSame(first, second, "different instances of SpreadsheetCell returned not cached");
    }

    @Test
    public void testLoadCellComputeIfNecessaryKeepsExpression() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference(1, 1);
        context.storeRepository()
                .cells()
                .save(this.cell(a, "1/2"));

        final SpreadsheetCell first = this.loadCellOrFail(engine, a, SpreadsheetEngineEvaluation.FORCE_RECOMPUTE, context);
        this.checkValueOrError(first, LocalDate.of(DEFAULT_YEAR, 2, 1));

        final int defaultYear = DEFAULT_YEAR + 100;

        final SpreadsheetCell second = this.loadCellOrFail(
                engine,
                a,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                this.createContext(defaultYear, engine, context.storeRepository()));

        assertSame(first, second, "same instances of SpreadsheetCell returned should have new expression and value");
    }

    @Test
    public void testLoadCellComputeIfNecessaryCachesCellWithInvalidFormulaAndErrorCached() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference cellReference = this.cellReference(1, 1);
        context.storeRepository()
                .cells()
                .save(this.cell(cellReference, "=1+2+"));

        final SpreadsheetCell first = this.loadCellOrFail(engine,
                cellReference,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context);
        assertNotEquals(SpreadsheetFormula.NO_ERROR, first.formula().error(), () -> "Expected error absent=" + first);

        final SpreadsheetCell second = this.loadCellOrFail(engine, cellReference, SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY, context);
        assertSame(first, second, "different instances of SpreadsheetCell returned not cached");
    }

    @Test
    public void testLoadCellForceRecomputeIgnoresExpression() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference(1, 1);
        context.storeRepository()
                .cells()
                .save(this.cell(a, "1/2"));

        final SpreadsheetCell first = this.loadCellOrFail(engine, a, SpreadsheetEngineEvaluation.FORCE_RECOMPUTE, context);
        this.checkValueOrError(first, LocalDate.of(DEFAULT_YEAR, 2, 1));

        final int defaultYear = DEFAULT_YEAR + 100;

        final SpreadsheetCell second = this.loadCellOrFail(
                engine,
                a,
                SpreadsheetEngineEvaluation.FORCE_RECOMPUTE,
                this.createContext(defaultYear, engine, context.storeRepository()));

        assertNotSame(first, second, "same instances of SpreadsheetCell returned should have new expression and value");
        this.checkValueOrError(
                second,
                LocalDate.of(defaultYear, 2, 1)
        );
    }

    @Test
    public void testLoadCellForceRecomputeIgnoresCache() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference a = this.cellReference(1, 1);
        cellStore.save(this.cell(a, "=1"));

        final SpreadsheetCellReference b = this.cellReference(2, 2);
        cellStore.save(this.cell(b, "=" + a));

        final SpreadsheetCell first = this.loadCellOrFail(engine, a, SpreadsheetEngineEvaluation.FORCE_RECOMPUTE, context);

        cellStore.save(this.cell(a, "=999"));

        final SpreadsheetCell second = this.loadCellOrFail(engine, a, SpreadsheetEngineEvaluation.FORCE_RECOMPUTE, context);
        assertNotSame(first, second, "different instances of SpreadsheetCell returned not cached");
        assertEquals(Optional.of(number(999)),
                second.formula().value(),
                "first should have value updated to 999 and not 1 the original value.");
    }

    @Test
    public void testLoadCellForceRecomputeIgnoresPreviousError() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference("A1");
        final Set<SpreadsheetCell> saved = engine.saveCell(this.cell(a, "=1+$B$2"), context)
                .cells();

        final SpreadsheetCell withError = saved.iterator().next();
        assertNotEquals(SpreadsheetFormula.NO_ERROR,
                withError.formula().error(),
                () -> "cell should have error because B2 reference is unknown=" + withError);

        final SpreadsheetCellReference b = this.cellReference("B2");
        context.storeRepository()
                .cells()
                .save(this.cell(b, "=99"));

        this.loadCellAndCheckValue(engine,
                a,
                SpreadsheetEngineEvaluation.FORCE_RECOMPUTE,
                context,
                number(1 + 99));
    }

    @Test
    public void testLoadCellComputeThenSkipEvaluate() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference cellReference = this.cellReference(1, 1);
        context.storeRepository()
                .cells()
                .save(this.cell(cellReference, "=1+2"));

        final SpreadsheetCell first = this.loadCellOrFail(engine,
                cellReference,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context);
        final SpreadsheetCell second = this.loadCellOrFail(engine,
                cellReference,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context);

        assertSame(first, second, "different instances of SpreadsheetCell returned not cached");
    }

    @Test
    public void testLoadCellManyWithoutCrossReferences() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference(1, 1);
        final SpreadsheetCellReference b = this.cellReference(2, 1);
        final SpreadsheetCellReference c = this.cellReference(3, 1);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();
        cellStore.save(this.cell(a, "=1+2"));
        cellStore.save(this.cell(b, "=3+4"));
        cellStore.save(this.cell(c, "=5+6"));

        this.loadCellAndCheckFormatted2(engine,
                a,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                number(1 + 2),
                FORMATTED_PATTERN_SUFFIX);
        this.loadCellAndCheckFormatted2(engine,
                b,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                number(3 + 4),
                FORMATTED_PATTERN_SUFFIX);
        this.loadCellAndCheckFormatted2(engine,
                c,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                number(5 + 6),
                FORMATTED_PATTERN_SUFFIX);
    }

    @Test
    public void testLoadCellWithCrossReferences() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference(1, 1);
        final SpreadsheetCellReference b = this.cellReference(2, 1);
        final SpreadsheetCellReference c = this.cellReference(3, 1);

        this.counter = BigDecimal.ZERO;

        engine.saveCell(this.cell(a, "=1+2+BasicSpreadsheetEngineTestCounter()"), context);
        engine.saveCell(this.cell(b, "=3+4+" + a), context);
        engine.saveCell(this.cell(c, "=5+6+" + a), context);

        // updating this counter results in $A having its value recomputed forcing a cascade update of $b and $c
        this.counter = number(100);

        this.loadCellAndCheck(engine,
                a,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                formattedCellWithValue(a, "=1+2+BasicSpreadsheetEngineTestCounter()", number(100 + 3)),
                formattedCellWithValue(b, "=3+4+" + a, number(3 + 4 + 103)),
                formattedCellWithValue(c, "=5+6+" + a, number(5 + 6 + 103)));
    }

    @Test
    public void testLoadCellValueCellReferenceInvalidFails() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        context.storeRepository()
                .cells()
                .save(this.cell(a, "=X99"));

        this.loadCellAndCheckError(engine,
                a,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "Unknown cell reference");
    }

    @Test
    public void testLoadCellValueLabelInvalidFails() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        context.storeRepository()
                .cells()
                .save(this.cell(a, "=INVALIDLABEL"));

        this.loadCellAndCheckError(engine,
                a,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "Unknown label");
    }

    @Test
    public void testLoadCellValueIsCellReference() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // B1

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();
        cellStore.save(this.cell(a, "=B1"));
        cellStore.save(this.cell(b, "=3+4"));

        // formula
        this.loadCellAndCheckFormatted2(engine,
                b,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                number(3 + 4),
                FORMATTED_PATTERN_SUFFIX);

        // reference to B1 which has formula
        this.loadCellAndCheckFormatted2(engine,
                a,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                number(3 + 4),
                FORMATTED_PATTERN_SUFFIX);
    }

    @Test
    public void testLoadCellValueIsLabel() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // B1

        final SpreadsheetStoreRepository repository = context.storeRepository();

        final SpreadsheetCellStore cellStore = repository.cells();
        cellStore.save(this.cell(a, "=" + LABEL.value()));
        cellStore.save(this.cell(b, "=3+4"));

        repository.labels()
                .save(SpreadsheetLabelMapping.with(LABEL, b));

        // formula
        this.loadCellAndCheckFormatted2(engine,
                b,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                number(3 + 4),
                FORMATTED_PATTERN_SUFFIX);

        // reference to B1 which has formula
        this.loadCellAndCheckFormatted2(engine,
                a,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                number(3 + 4),
                FORMATTED_PATTERN_SUFFIX);
    }

    @Test
    public void testLoadCellWithConditionalFormattingRule() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();

        final SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> rules = repository.rangeToConditionalFormattingRules();
        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1

        // rule 3 is ignored because it returns false, rule 2 short circuits the conditional testing ...
        final TextStyle italics = TextStyle.with(Maps.of(TextStylePropertyName.FONT_STYLE, FontStyle.ITALIC));
        this.saveRule(true,
                1,
                italics,
                a,
                rules);

        this.saveRule(true,
                2,
                TextStyle.with(Maps.of(TextStylePropertyName.TEXT_DECORATION_LINE, TextDecorationLine.UNDERLINE)),
                a,
                rules);
        this.saveRule(false,
                3,
                TextStyle.with(Maps.of(TextStylePropertyName.FONT_STYLE, FontStyle.ITALIC, TextStylePropertyName.FONT_WEIGHT, FontWeight.BOLD, TextStylePropertyName.TEXT_DECORATION_LINE, TextDecorationLine.UNDERLINE)),
                a,
                rules);

        repository.cells()
                .save(this.cell(a, "=3+4"));

        final SpreadsheetCell cell = this.loadCellAndCheckFormatted2(engine,
                a,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                number(3 + 4),
                FORMATTED_PATTERN_SUFFIX);

        // UNDERLINED from conditional formatting rule #2.
        assertEquals(Optional.of(italics.replace(TextNode.text("7 " + FORMATTED_PATTERN_SUFFIX)).root()),
                cell.formatted(),
                () -> "TextStyle should include underline if correct rule was applied=" + cell);
    }

    private void saveRule(final boolean result,
                          final int priority,
                          final TextStyle style,
                          final SpreadsheetCellReference cell,
                          final SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> rules) {
        rules.addValue(cell.spreadsheetRange(cell), rule(result, priority, style));
    }

    private SpreadsheetConditionalFormattingRule rule(final boolean result,
                                                      final int priority,
                                                      final TextStyle style) {


        return SpreadsheetConditionalFormattingRule.with(SpreadsheetDescription.with(priority + "=" + result),
                priority,
                SpreadsheetFormula.with(String.valueOf(result)).setExpression(Optional.of(Expression.booleanExpression(result))),
                (c) -> style);
    }

    // saveCell....................................................................................................

    @Test
    public void testSaveCellEmptyFormula() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell("a1", "");
        final SpreadsheetCell a1Formatted = this.formattedCellWithValue(a1, "");
        this.saveCellAndCheck(engine,
                a1,
                context,
                a1Formatted);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> cellReferenceStore = repository.cellReferences();

        this.loadCellStoreAndCheck(cellStore, a1Formatted);
        this.loadLabelStoreAndCheck(labelStore);
        this.countAndCheck(cellReferenceStore, 0);
    }

    @Test
    public void testSaveCellInvalidDate() {
        this.saveCellWithErrorAndCheck(
                "1999/99/31",
                "Invalid value for MonthOfYear (valid values 1 - 12): 99"
        );
    }

    @Test
    public void testSaveCellInvalidDateTime() {
        this.saveCellWithErrorAndCheck(
                "1999/99/31 12:58",
                "Invalid value for MonthOfYear (valid values 1 - 12): 99"
        );
    }

    @Test
    public void testSaveCellInvalidTime() {
        this.saveCellWithErrorAndCheck(
                "12:99",
                "Invalid value for MinuteOfHour (valid values 0 - 59): 99"
        );
    }

    private void saveCellWithErrorAndCheck(final String formula, final String errorMessage) {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell("a1", formula);
        final SpreadsheetCell a1Formatted = this.formattedCellWithError(a1, errorMessage);
        this.saveCellAndCheck(engine,
                a1,
                context,
                a1Formatted);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> cellReferenceStore = repository.cellReferences();

        this.loadCellStoreAndCheck(cellStore, a1Formatted);
        this.loadLabelStoreAndCheck(labelStore);
        this.countAndCheck(cellReferenceStore, 0);
    }

    @Test
    public void testSaveCellWithoutReferences() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell("a1", "=1+2");
        final SpreadsheetCell a1Formatted = this.formattedCellWithValue(a1, number(3));
        this.saveCellAndCheck(engine,
                a1,
                context,
                a1Formatted);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> cellReferenceStore = repository.cellReferences();

        this.loadCellStoreAndCheck(cellStore, a1Formatted);
        this.loadLabelStoreAndCheck(labelStore);
        this.countAndCheck(cellReferenceStore, 0);
    }

    @Test
    public void testSaveCellWithUnknownReference() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> cellReferenceStore = repository.cellReferences();

        final SpreadsheetCell a1 = this.cell("a1", "=$B$2+99");
        final SpreadsheetCell a1Formatted = this.formattedCellWithError(a1, "Unknown cell reference $B$2");
        this.saveCellAndCheck(engine,
                a1,
                context,
                a1Formatted);

        this.loadCellStoreAndCheck(cellStore, a1Formatted);
        this.loadLabelStoreAndCheck(labelStore);

        // verify references all ways are present in the store.
        final SpreadsheetCellReference b2 = SpreadsheetExpressionReference.parseCellReference("$B$2");

        this.loadReferencesAndCheck(cellReferenceStore, a1.reference(), b2.toRelative()); // references from A1 -> B2
        this.loadReferrersAndCheck(cellReferenceStore, a1.reference()); // references to A1 -> none

        this.loadReferencesAndCheck(cellReferenceStore, b2); // references to B2 -> none
        this.loadReferrersAndCheck(cellReferenceStore, b2, a1.reference()); // references from B2 -> A1
    }

    @Test
    public void testSaveCellIgnoresPreviousErrorComputesValue() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference cellReference = this.cellReference(1, 1);

        final SpreadsheetCell cell = this.cell(cellReference,
                SpreadsheetFormula.with("=1+2")
                        .setError(Optional.of(SpreadsheetError.with("error!"))));

        this.saveCellAndCheck(engine,
                cell,
                context,
                this.formattedCellWithValue(cell, number(1 + 2)));
    }

    @Test
    public void testSaveCellMultipleIndependentUnreferenced() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> cellReferenceStore = repository.cellReferences();

        final SpreadsheetCell a1 = this.cell("$A$1", "=1+2");
        final SpreadsheetCell a1Formatted = this.formattedCellWithValue(a1, number(3));

        this.saveCellAndCheck(engine,
                a1,
                context,
                a1Formatted);

        final SpreadsheetCell b2 = this.cell("$B$2", "=3+4");
        final SpreadsheetCell b2Formatted = this.formattedCellWithValue(b2, number(7));

        this.saveCellAndCheck(engine,
                b2,
                context,
                b2Formatted);

        final SpreadsheetCell c3 = this.cell("$C$3", "=5+6");
        final SpreadsheetCell c3Formatted = this.formattedCellWithValue(c3, number(11));

        this.saveCellAndCheck(engine,
                c3,
                context,
                c3Formatted);

        this.loadCellStoreAndCheck(cellStore, a1Formatted, b2Formatted, c3Formatted);
        this.loadLabelStoreAndCheck(labelStore);
        this.countAndCheck(cellReferenceStore, 0);

        this.loadReferencesAndCheck(cellReferenceStore, a1.reference()); // references to A1 -> none
        this.loadReferrersAndCheck(cellReferenceStore, a1.reference()); // references from A1 -> none

        this.loadReferencesAndCheck(cellReferenceStore, b2.reference()); // references to B2 -> none
        this.loadReferrersAndCheck(cellReferenceStore, b2.reference()); // references from B2 -> none

        this.loadReferencesAndCheck(cellReferenceStore, c3.reference()); // references to C3 -> none
        this.loadReferrersAndCheck(cellReferenceStore, c3.reference()); // references from C3 -> none
    }

    @Test
    public void testSaveCellWithLabelReference() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelName unknown = SpreadsheetExpressionReference.labelName("LABELXYZ");

        final SpreadsheetCell a1 = this.cell("a1", "=1+" + unknown);
        final SpreadsheetCell a1Formatted = this.formattedCellWithError(a1, "Unknown label: " + unknown);
        this.saveCellAndCheck(engine,
                a1,
                context,
                a1Formatted);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> cellReferenceStore = repository.cellReferences();
        final SpreadsheetExpressionReferenceStore<SpreadsheetLabelName> labelReferencesStore = repository.labelReferences();

        this.loadCellStoreAndCheck(cellStore, a1Formatted);
        this.loadLabelStoreAndCheck(labelStore);
        this.countAndCheck(cellReferenceStore, 0);

        this.loadReferencesAndCheck(labelReferencesStore, unknown, a1.reference());
    }

    @Test
    public void testSaveCellTwiceLaterReferencesPrevious() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell("$A$1", "=1+2");
        engine.saveCell(a1, context);

        final SpreadsheetCellReference a1Reference = SpreadsheetCellReference.parseCellReference("$A$1");
        final SpreadsheetCell b2 = this.cell("$B$2", "=5+" + a1Reference);

        this.saveCellAndCheck(engine,
                b2,
                context,
                this.formattedCellWithValue(b2, number(5 + 3)));

        final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> cellReferenceStore = context.storeRepository()
                .cellReferences();

        this.loadReferencesAndCheck(cellReferenceStore, a1.reference());
        this.loadReferrersAndCheck(cellReferenceStore, a1.reference(), b2.reference());

        this.loadReferencesAndCheck(cellReferenceStore, b2.reference(), a1Reference.toRelative());
        this.loadReferrersAndCheck(cellReferenceStore, b2.reference());
    }

    @Test
    public void testSaveCellTwiceLaterReferencesPrevious2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell("$A$1", "=1+C3");
        engine.saveCell(a1, context);

        final SpreadsheetCell b2 = this.cell("$B$2", "=5+A1");
        engine.saveCell(b2, context);

        final SpreadsheetCell c3 = this.cell("$C$3", "=10");

        this.saveCellAndCheck(engine,
                c3,
                context,
                this.formattedCellWithValue(a1, number(1 + 10)),
                this.formattedCellWithValue(b2, number(5 + 1 + 10)),
                this.formattedCellWithValue(c3, number(10)));
    }

    @Test
    public void testSaveCellTwiceLaterReferencesPreviousAgain() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell a1 = this.cell("$A$1", "=1+2");
        engine.saveCell(a1, context);

        final SpreadsheetCell b2 = this.cell("$B$2", "=5+$A$1");
        final SpreadsheetCell b2Formatted = this.formattedCellWithValue(b2, number(5 + 1 + 2));

        this.saveCellAndCheck(engine,
                b2,
                context,
                b2Formatted);

        this.saveCellAndCheck(engine,
                b2Formatted,
                context);
    }

    @Test
    public void testSaveCellReferencesUpdated() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference b2Reference = SpreadsheetCellReference.parseCellReference("$B2$2");
        final SpreadsheetCell a1 = this.cell("$A$1", "=" + b2Reference + "+5");
        engine.saveCell(a1, context);

        final SpreadsheetCell b2 = this.cell("$B$2", "=1+2");
        this.saveCellAndCheck(engine,
                b2,
                context,
                this.formattedCellWithValue(a1, number(1 + 2 + 5)),
                this.formattedCellWithValue(b2, number(1 + 2)));

        final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> cellReferenceStore = context.storeRepository()
                .cellReferences();

        this.loadReferencesAndCheck(cellReferenceStore, a1.reference(), b2Reference.toRelative());
        this.loadReferrersAndCheck(cellReferenceStore, a1.reference());

        this.loadReferencesAndCheck(cellReferenceStore, b2.reference());
        this.loadReferrersAndCheck(cellReferenceStore, b2.reference(), a1.reference());
    }

    @Test
    public void testSaveCellLabelReference() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        context.storeRepository()
                .labels()
                .save(SpreadsheetLabelMapping.with(SpreadsheetExpressionReference.labelName("LABELA1"), this.cellReference("A1")));

        final SpreadsheetCell a1 = this.cell("$A$1", "=10");
        engine.saveCell(a1, context);

        final SpreadsheetCell b2 = this.cell("$B$2", "=5+LABELA1");
        this.saveCellAndCheck(engine,
                b2,
                context,
                this.formattedCellWithValue(b2, number(5 + 10)));
    }

    @Test
    public void testSaveCellLabelReference2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetExpressionReferenceStore<SpreadsheetLabelName> labelReferencesStore = repository.labelReferences();

        final SpreadsheetCell b2 = this.cell("$B$2", "=5");

        final SpreadsheetLabelName labelB2 = SpreadsheetExpressionReference.labelName("LABELB2");
        labelStore.save(SpreadsheetLabelMapping.with(labelB2, b2.reference()));

        final SpreadsheetCell a1 = this.cell("$A$1", "=10+" + labelB2);
        engine.saveCell(a1, context);

        final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> cellReferenceStore = repository.cellReferences();

        this.loadReferencesAndCheck(cellReferenceStore, a1.reference());
        this.loadReferrersAndCheck(cellReferenceStore, a1.reference());

        this.loadReferencesAndCheck(cellReferenceStore, b2.reference());
        this.loadReferrersAndCheck(cellReferenceStore, b2.reference());

        this.loadReferencesAndCheck(labelReferencesStore, labelB2, a1.reference());

        this.saveCellAndCheck(engine,
                b2,
                context,
                SpreadsheetDelta.with(
                        Sets.of(
                                this.formattedCellWithValue(a1, number(10 + 5)),
                                this.formattedCellWithValue(b2, number(5))
                        )
                ).setLabels(
                        Sets.of(
                                labelB2.mapping(b2.reference())
                        )
                )
        );
    }

    @Test
    public void testSaveCellReplacesCellReferences() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell d4 = this.cell("$D$4", "=20");
        engine.saveCell(d4, context);

        final SpreadsheetCell e5 = this.cell("$E$5", "=30");
        engine.saveCell(e5, context);

        engine.saveCell(this.cell("$A$1", "=10+" + d4.reference()), context);

        final SpreadsheetCell a1 = this.cell("$A$1", "=40+" + e5.reference());
        this.saveCellAndCheck(engine,
                a1,
                context,
                this.formattedCellWithValue(a1, number(40 + 30)));

        final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> cellReferenceStore = context
                .storeRepository()
                .cellReferences();

        this.loadReferencesAndCheck(cellReferenceStore, a1.reference(), e5.reference());
        this.loadReferrersAndCheck(cellReferenceStore, a1.reference());

        this.loadReferencesAndCheck(cellReferenceStore, d4.reference());
        this.loadReferrersAndCheck(cellReferenceStore, d4.reference());
    }

    @Test
    public void testSaveCellReplacesLabelReferences() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> cellReferenceStore = repository.cellReferences();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetExpressionReferenceStore<SpreadsheetLabelName> labelReferencesStore = repository.labelReferences();

        final SpreadsheetLabelName labelB2 = SpreadsheetExpressionReference.labelName("LABELB2");
        labelStore.save(SpreadsheetLabelMapping.with(labelB2, this.cellReference("B2")));

        final SpreadsheetLabelName labelD4 = SpreadsheetExpressionReference.labelName("LABELD4");
        labelStore.save(SpreadsheetLabelMapping.with(labelD4, this.cellReference("D4")));

        final SpreadsheetCell d4 = this.cell("$D$4", "=20");
        engine.saveCell(d4, context);

        engine.saveCell(this.cell("$A$1", "=10+" + labelB2), context);

        final SpreadsheetCell a1 = this.cell("$A$1", "=40+" + labelD4);
        this.saveCellAndCheck(engine,
                a1,
                context,
                this.formattedCellWithValue(a1, number(40 + 20)));

        this.loadReferencesAndCheck(cellReferenceStore, a1.reference());
        this.loadReferrersAndCheck(cellReferenceStore, a1.reference());

        this.loadReferencesAndCheck(cellReferenceStore, d4.reference());
        this.loadReferrersAndCheck(cellReferenceStore, d4.reference());

        this.loadReferencesAndCheck(labelReferencesStore, labelB2);
        this.loadReferencesAndCheck(labelReferencesStore, labelD4, a1.reference());
    }

    @Test
    public void testSaveCellReplacesCellAndLabelReferences() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();

        final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> cellReferenceStore = repository.cellReferences();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetExpressionReferenceStore<SpreadsheetLabelName> labelReferencesStore = repository.labelReferences();

        final SpreadsheetLabelName labelB2 = SpreadsheetExpressionReference.labelName("LABELB2");
        final SpreadsheetCellReference b2Reference = this.cellReference("B2");
        labelStore.save(SpreadsheetLabelMapping.with(labelB2, b2Reference));

        final SpreadsheetLabelName labelD4 = SpreadsheetExpressionReference.labelName("LABELD4");
        labelStore.save(SpreadsheetLabelMapping.with(labelD4, this.cellReference("D4")));

        final SpreadsheetCell d4 = this.cell("$D$4", "=20");
        engine.saveCell(d4, context);

        final SpreadsheetCell e5 = this.cell("$E$5", "=30");
        engine.saveCell(e5, context);

        engine.saveCell(this.cell("$A$1", "=10+" + labelB2 + "+C2"), context);

        final SpreadsheetCellReference e5Reference = SpreadsheetCellReference.parseCellReference("$E$5");
        final SpreadsheetCell a1 = this.cell("$A$1", "=40+" + labelD4 + "+" + e5Reference);
        this.saveCellAndCheck(engine,
                a1,
                context,
                this.formattedCellWithValue(a1, number(40 + 20 + 30)));

        this.loadReferencesAndCheck(cellReferenceStore, a1.reference(), e5Reference.toRelative());
        this.loadReferrersAndCheck(cellReferenceStore, a1.reference());

        this.loadReferencesAndCheck(cellReferenceStore, d4.reference());
        this.loadReferrersAndCheck(cellReferenceStore, d4.reference());

        this.loadReferencesAndCheck(labelReferencesStore, labelB2);
        this.loadReferencesAndCheck(labelReferencesStore, labelD4, a1.reference());
    }

    // saveCell tests with non expression formula's only value literals.................................................

    @Test
    public void testSaveCellFormulaApostropheString() {
        this.saveCellAndLoadAndFormattedCheck(
                "'Hello",
                "Hello"
        );
    }

    @Test
    public void testSaveCellFormulaNumber() {
        this.saveCellAndLoadAndFormattedCheck(
                "123",
                this.expressionNumberKind().create(123)
        );
    }

    @Test
    public void testSaveCellFormulaDate() {
        this.saveCellAndLoadAndFormattedCheck(
                "1999/12/31",
                LocalDate.of(1999, 12, 31)
        );
    }

    @Test
    public void testSaveCellFormulaDateTime() {
        this.saveCellAndLoadAndFormattedCheck(
                "1999/12/31 12:34",
                LocalDateTime.of(
                        LocalDate.of(1999, 12, 31),
                        LocalTime.of(12, 34)
                )
        );
    }

    @Test
    public void testSaveCellFormulaTime() {
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
        final SpreadsheetCell a1Formatted = this.formattedCellWithValue(
                a1,
                value
        );
        this.saveCellAndCheck(engine,
                a1,
                context,
                a1Formatted);

        this.loadCellStoreAndCheck(context.storeRepository().cells(), a1Formatted);
    }

    // deleteCell....................................................................................................

    @Test
    public void testDeleteCellWithReferences() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a1 = SpreadsheetExpressionReference.parseCellReference("$A$1");
        final SpreadsheetCellReference b2 = SpreadsheetExpressionReference.parseCellReference("$B$2");

        engine.saveCell(this.cell(a1, "=99+" + b2), context);
        this.deleteCellAndCheck(engine, a1, context);

        final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> cellReferenceStore = context.storeRepository()
                .cellReferences();

        this.loadReferencesAndCheck(cellReferenceStore, a1);
        this.loadReferrersAndCheck(cellReferenceStore, a1);

        this.loadReferencesAndCheck(cellReferenceStore, b2);
        this.loadReferrersAndCheck(cellReferenceStore, b2);
    }

    @Test
    public void testDeleteCellWithCellReferrers() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);
        final SpreadsheetCellReference b2Reference = SpreadsheetCellReference.parseCellReference("$B$2");

        final SpreadsheetCell a1 = this.cell("$A$1", "=1+" + b2Reference);
        engine.saveCell(a1, context);

        final SpreadsheetCell b2 = this.cell("$B$2", "=20");
        engine.saveCell(b2, context);

        this.deleteCellAndCheck(engine,
                b2.reference(),
                context,
                this.formattedCellWithError(a1, "Unknown cell reference " + b2Reference));

        final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> cellReferenceStore = context.storeRepository()
                .cellReferences();

        this.loadReferencesAndCheck(cellReferenceStore, a1.reference(), b2Reference.toRelative());
        this.loadReferrersAndCheck(cellReferenceStore, a1.reference());

        this.loadReferencesAndCheck(cellReferenceStore, b2.reference());
        this.loadReferrersAndCheck(cellReferenceStore, b2.reference(), a1.reference());
    }

    @Test
    public void testDeleteCellWithLabelReferences() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> cellReferenceStore = repository.cellReferences();
        final SpreadsheetExpressionReferenceStore<SpreadsheetLabelName> labelReferencesStore = repository.labelReferences();

        final SpreadsheetLabelName labelB2 = SpreadsheetExpressionReference.labelName("LABELB2");
        final SpreadsheetCell b2 = this.cell("$B$2", "=20");
        labelStore.save(SpreadsheetLabelMapping.with(labelB2, b2.reference()));

        final SpreadsheetCell a1 = this.cell("$A$1", "=1+" + labelB2);
        engine.saveCell(a1, context);

        engine.saveCell(b2, context);

        this.deleteCellAndCheck(engine,
                a1.reference(),
                context);

        this.loadReferencesAndCheck(cellReferenceStore, a1.reference());
        this.loadReferrersAndCheck(cellReferenceStore, a1.reference());

        this.loadReferencesAndCheck(cellReferenceStore, b2.reference());
        this.loadReferrersAndCheck(cellReferenceStore, b2.reference());

        this.loadReferencesAndCheck(labelReferencesStore, labelB2);
    }

    @Test
    public void testDeleteCellWithLabelReferrers() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetLabelStore labelStore = repository.labels();
        final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> cellReferenceStore = repository.cellReferences();
        final SpreadsheetExpressionReferenceStore<SpreadsheetLabelName> labelReferencesStore = repository.labelReferences();

        final SpreadsheetLabelName labelB2 = SpreadsheetExpressionReference.labelName("LABELB2");
        final SpreadsheetCell b2 = this.cell("$B$2", "=20");
        labelStore.save(SpreadsheetLabelMapping.with(labelB2, b2.reference()));

        final SpreadsheetCell a1 = this.cell("$A$1", "=1+" + labelB2);
        engine.saveCell(a1, context);

        engine.saveCell(b2, context);

        this.deleteCellAndCheck(engine,
                b2.reference(),
                context,
                this.formattedCellWithError(a1, "Unknown cell reference " + labelB2));

        this.loadReferencesAndCheck(cellReferenceStore, a1.reference());
        this.loadReferrersAndCheck(cellReferenceStore, a1.reference());

        this.loadReferencesAndCheck(cellReferenceStore, b2.reference());
        this.loadReferrersAndCheck(cellReferenceStore, b2.reference());

        this.loadReferencesAndCheck(labelReferencesStore, labelB2, a1.reference());
    }

    // deleteColumn....................................................................................................

    @Test
    public void testDeleteColumnZeroNothingDeleted() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference reference = this.cellReference(99, 0); // A3

        engine.saveCell(this.cell(reference, "=99+0"), context);

        this.addFailingCellSaveWatcherAndDeleteWatcher(context);

        this.deleteColumnsAndCheck(engine,
                reference.column(),
                0,
                context);

        this.countAndCheck(context.storeRepository().cells(), 1);
    }

    @Test
    public void testDeleteColumnNoCellsRefreshed() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference("A1"); // A1
        final SpreadsheetCellReference b = this.cellReference("B2"); // B2
        final SpreadsheetCellReference c = this.cellReference("C3"); // C3

        engine.saveCell(this.cell(a, "=1+2"), context);
        engine.saveCell(this.cell(b, "=3+4"), context);
        engine.saveCell(this.cell(c, "=5+6"), context);

        this.deleteColumnsAndCheck(engine,
                c.column(),
                1,
                context);

        this.countAndCheck(context.storeRepository().cells(), 2);
    }

    @Test
    public void testDeleteColumnColumnsAfterCellsRefreshed() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference("A1"); // A1
        final SpreadsheetCellReference b = this.cellReference("B2"); // B2
        final SpreadsheetCellReference c = this.cellReference("C3"); // C3

        engine.saveCell(this.cell(a, "=1+2"), context);
        engine.saveCell(this.cell(b, "=3+4"), context); // deleted/replaced by $c
        engine.saveCell(this.cell(c, "=5+6"), context); // becomes b3

        this.deleteColumnsAndCheck(engine,
                b.column(),
                1,
                context,
                this.formattedCellWithValue("b3", "=5+6", number(5 + 6)));

        this.countAndCheck(context.storeRepository().cells(), 2);
    }

    @Test
    public void testDeleteColumnColumnsAfterCellsRefreshedAddition() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck("=5+6", number(5 + 6));
    }

    @Test
    public void testDeleteColumnColumnsAfterCellsRefreshedExpressionNumber() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck("=55.5", number(55.5));
    }

    @Test
    public void testDeleteColumnColumnsAfterCellsRefreshedExpressionNumber2() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck("=55", number(55));
    }

    @Test
    public void testDeleteColumnColumnsAfterCellsRefreshedDivision() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck("=9/3", number(9 / 3));
    }

    @Test
    public void testDeleteColumnColumnsAfterCellsRefreshedEqualsTrue() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck("=8==8", true);
    }

    @Test
    public void testDeleteColumnColumnsAfterCellsRefreshedEqualsFalse() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck("=8==7", false);
    }

    @Test
    public void testDeleteColumnColumnsAfterCellsRefreshedFunction() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck("=BasicSpreadsheetEngineTestSum(1;99)", number(1 + 99));
    }

    @Test
    public void testDeleteColumnColumnsAfterCellsRefreshedGreaterThanTrue() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck("=8>7", true);
    }

    @Test
    public void testDeleteColumnColumnsAfterCellsRefreshedGreaterThanFalse() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck("=7>8", false);
    }

    @Test
    public void testDeleteColumnColumnsAfterCellsRefreshedGreaterThanEqualsTrue() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck("=8>=7", true);
    }

    @Test
    public void testDeleteColumnColumnsAfterCellsRefreshedGreaterThanEqualsFalse() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck("=7>=8", false);
    }

    @Test
    public void testDeleteColumnColumnsAfterCellsRefreshedGroup() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck("=(99)", number(99));
    }

    @Test
    public void testDeleteColumnColumnsAfterCellsRefreshedLessThanTrue() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck("=8<9", true);
    }

    @Test
    public void testDeleteColumnColumnsAfterCellsRefreshedLessThanFalse() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck("=7<6", false);
    }

    @Test
    public void testDeleteColumnColumnsAfterCellsRefreshedLessThanEqualsTrue() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck("=8<=8", true);
    }

    @Test
    public void testDeleteColumnColumnsAfterCellsRefreshedLessThanEqualsFalse() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck("=8<=7", false);
    }

    @Test
    public void testDeleteColumnColumnsAfterCellsRefreshedMultiplication() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck("=9*3", number(9 * 3));
    }

    @Test
    public void testDeleteColumnColumnsAfterCellsRefreshedNegative() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck("=-99", number(-99));
    }

    @Test
    public void testDeleteColumnColumnsAfterCellsRefreshedNotEqualsTrue() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck("=8!=7", true);
    }

    @Test
    public void testDeleteColumnColumnsAfterCellsRefreshedNotEqualsFalse() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck("=8!=8", false);
    }

    @Test
    public void testDeleteColumnColumnsAfterCellsRefreshedPercentage() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck("=120%", number(1.2));
    }

    @Test
    public void testDeleteColumnColumnsAfterCellsRefreshedSubtraction() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck("=9-7", number(9 - 7));
    }

    @Test
    public void testDeleteColumnColumnsAfterCellsRefreshedText() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck("=\"ABC123\"", "ABC123");
    }

    @Test
    public void testDeleteColumnColumnsAfterCellsRefreshedAdditionWithWhitespace() {
        this.deleteColumnColumnsAfterCellsRefreshedAndCheck("=1 + 2", number(1 + 2));
    }

    private void deleteColumnColumnsAfterCellsRefreshedAndCheck(final String formula,
                                                                final Object value) {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference("A1"); // A1
        final SpreadsheetCellReference b = this.cellReference("B2"); // B2
        final SpreadsheetCellReference c = this.cellReference("C3"); // C3

        engine.saveCell(this.cell(a, "=1+2"), context);
        engine.saveCell(this.cell(b, "=3+4"), context); // deleted/replaced by $c
        engine.saveCell(this.cell(c, formula), context); // becomes b3

        this.deleteColumnsAndCheck(engine,
                b.column(),
                1,
                context,
                this.formattedCellWithValue("b3", formula, value));

        this.countAndCheck(context.storeRepository().cells(), 2);
    }

    @Test
    public void testDeleteColumnColumnsAfterCellsRefreshed2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference("A1");
        final SpreadsheetCellReference b = this.cellReference("B2"); //replaced by $c
        final SpreadsheetCellReference c = this.cellReference("C3");
        final SpreadsheetCellReference d = this.cellReference("Z99");// B99 moved

        engine.saveCell(this.cell(a, "=1+2"), context);
        engine.saveCell(this.cell(b, "=3+4"), context);
        engine.saveCell(this.cell(c, "=5+6"), context);
        engine.saveCell(this.cell(d, "=7+8"), context);

        final int count = 1;
        this.deleteColumnsAndCheck(engine,
                b.column(),
                count,
                context,
                this.formattedCellWithValue("B3", "=5+6", number(5 + 6)),
                this.formattedCellWithValue("Y99", "=7+8", number(7 + 8)));

        this.countAndCheck(context.storeRepository().cells(), 3);
    }

    @Test
    public void testDeleteColumnWithLabelsToCellReferenceIgnored() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a = SpreadsheetExpressionReference.parseCellReference("A1"); // A1
        final SpreadsheetCellReference b = SpreadsheetExpressionReference.parseCellReference("E2"); // E2

        engine.saveCell(this.cell(a, "=99+0"), context);
        engine.saveCell(this.cell(b, "=2+0+" + LABEL), context);

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, a));

        final int count = 1;
        this.deleteColumnsAndCheck(engine,
                b.column().add(-1),
                count,
                context,
                this.formattedCellWithValue(b.addColumn(-1), "=2+0+" + LABEL, number(2 + 99)));

        this.countAndCheck(cellStore, 2);

        this.loadLabelAndCheck(labelStore, LABEL, a);
    }

    @Test
    public void testDeleteColumnWithLabelsToCellReferencedFixed() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // A2 replaced by c
        final SpreadsheetCellReference c = this.cellReference(2, 0); // A3 DELETED
        final SpreadsheetCellReference d = this.cellReference(13, 8); // B8 moved
        final SpreadsheetCellReference e = this.cellReference(14, 9); // C9 moved LABEL=

        engine.saveCell(this.cell(a, "=1+" + LABEL), context);
        engine.saveCell(this.cell(b, "=2+0"), context);
        engine.saveCell(this.cell(c, "=3+0"), context);
        engine.saveCell(this.cell(d, "=4+" + LABEL), context);
        engine.saveCell(this.cell(e, "=99+0"), context); // LABEL=

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, e));

        final int count = 1;
        this.deleteColumnsAndCheck(engine,
                b.column(),
                count,
                context,
                SpreadsheetDelta.with(
                        Sets.of(
                                this.formattedCellWithValue(a, "=1+" + LABEL, number(1 + 99 + 0)),
                                this.formattedCellWithValue(c.addColumn(-count), "=3+0", number(3 + 0)),
                                this.formattedCellWithValue(d.addColumn(-count), "=4+" + LABEL, number(4 + 99 + 0)),
                                this.formattedCellWithValue(e.addColumn(-count), "=99+0", number(99 + 0))
                        )
                ).setLabels(
                        Sets.of(
                                LABEL.mapping(SpreadsheetCellReference.parseCellReference("N10"))
                        )
                )
        ); // old $b delete, $c,$d columns -1.

        this.loadLabelAndCheck(labelStore, LABEL, e.addColumn(-count));

        this.countAndCheck(cellStore, 4);
    }

    @Test
    public void testDeleteColumnWithLabelToDeletedCell() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // A2

        engine.saveCell(this.cell(a, "=1+0"), context);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, b));

        final int count = 1;
        this.deleteColumnsAndCheck(engine,
                b.column(),
                count,
                context); // $b delete, $c,$d columns -1.

        this.loadLabelFailCheck(labelStore, LABEL);

        this.countAndCheck(cellStore, 1);
    }

    @Test
    public void testDeleteColumnWithCellReferences() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // A2
        final SpreadsheetCellReference c = this.cellReference(10, 0); // A10 deleted
        final SpreadsheetCellReference d = this.cellReference(13, 8); // H13 moved
        final SpreadsheetCellReference e = this.cellReference(14, 9); // I14 moved

        engine.saveCell(this.cell(a, "=1+" + d), context);
        engine.saveCell(this.cell(b, "=2"), context);
        engine.saveCell(this.cell(c, "=3"), context);
        engine.saveCell(this.cell(d, "=4"), context);
        engine.saveCell(this.cell(e, "=5+" + b), context); // =5+2

        final int count = 1;
        this.deleteColumnsAndCheck(engine,
                c.column(),
                count,
                context,
                this.formattedCellWithValue(a, "=1+" + d.addColumn(-count), number(1 + 4)),
                this.formattedCellWithValue(d.addColumn(-count), "=4", number(4)),
                this.formattedCellWithValue(e.addColumn(-count), "=5+" + b, number(5 + 2))); // $c delete

        this.countAndCheck(context.storeRepository().cells(), 4);
    }

    @Test
    public void testDeleteColumnWithCellReferences2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // A2
        final SpreadsheetCellReference c = this.cellReference(10, 0); // A10 deleted
        final SpreadsheetCellReference d = this.cellReference(13, 8); // H13 moved
        final SpreadsheetCellReference e = this.cellReference(14, 9); // I14 moved

        engine.saveCell(this.cell(a, "=1+" + d), context);
        engine.saveCell(this.cell(b, "=2"), context);
        engine.saveCell(this.cell(c, "=3"), context);
        engine.saveCell(this.cell(d, "=4"), context);
        engine.saveCell(this.cell(e, "=5+" + b), context); // =5+2

        final int count = 2;
        this.deleteColumnsAndCheck(engine,
                c.column(),
                count,
                context,
                this.formattedCellWithValue(a, "=1+" + d.addColumn(-count), number(1 + 4)),
                this.formattedCellWithValue(d.addColumn(-count), "=4", number(4)),
                this.formattedCellWithValue(e.addColumn(-count), "=5+" + b, number(5 + 2))); // $c deleted, old-d & old-e refreshed

        this.countAndCheck(context.storeRepository().cells(), 4);
    }

    @Test
    public void testDeleteColumnWithCellReferencesToDeletedCell() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // A2

        engine.saveCell(this.cell(a, "=1+" + b), context);
        engine.saveCell(this.cell(b, "=2"), context);

        final int count = 1;
        this.deleteColumnsAndCheck(engine,
                b.column(),
                count,
                context,
                this.formattedCellWithError(a, "=1+InvalidCellReference(\"" + b + "\")", "Invalid cell reference: " + b)); // $b delete

        this.countAndCheck(context.storeRepository().cells(), 1);
    }

    @Test
    public void testDeleteColumnSeveral() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(10, 0); // DELETED
        final SpreadsheetCellReference c = this.cellReference(11, 0); // DELETED
        final SpreadsheetCellReference d = this.cellReference(12, 2); // C4
        final SpreadsheetCellReference e = this.cellReference(20, 3); // T3
        final SpreadsheetCellReference f = this.cellReference(21, 4); // U4

        engine.saveCell(this.cell(a, "=1"), context);
        engine.saveCell(this.cell(b, "=2"), context);
        engine.saveCell(this.cell(c, "=3"), context);
        engine.saveCell(this.cell(d, "=4"), context);
        engine.saveCell(this.cell(e, "=5"), context);
        engine.saveCell(this.cell(f, "=6"), context);

        final int count = 5;
        this.deleteColumnsAndCheck(engine,
                this.column(7),
                count,
                context,
                this.formattedCellWithValue(d.addColumn(-count), "=4", number(4)),
                this.formattedCellWithValue(e.addColumn(-count), "=5", number(5)),
                this.formattedCellWithValue(f.addColumn(-count), "=6", number(6))); // $b & $c

        this.countAndCheck(context.storeRepository().cells(), 4);
    }

    // deleteRow....................................................................................................

    @Test
    public void testDeleteRowsNone() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference reference = this.cellReference(0, 1); // A2

        engine.saveCell(this.cell(reference, "=99+0"), context);

        this.addFailingCellSaveWatcherAndDeleteWatcher(context);

        this.deleteRowsAndCheck(engine,
                reference.row(),
                0,
                context);

        this.countAndCheck(context.storeRepository().cells(), 1);
    }

    @Test
    public void testDeleteRowsOne() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference("A1");
        final SpreadsheetCellReference b = this.cellReference("A2");
        final SpreadsheetCellReference c = this.cellReference("A3");

        engine.saveCell(this.cell(a, "=1+2"), context);
        engine.saveCell(this.cell(b, "=3+4"), context);
        engine.saveCell(this.cell(c, "=5+6"), context);

        this.deleteRowsAndCheck(engine,
                b.row(),
                1,
                context,
                this.formattedCellWithValue(c.addRow(-1), "=5+6", number(5 + 6)));

        this.countAndCheck(context.storeRepository().cells(), 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                c.addRow(-1),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=5+6",
                number(5 + 6));
    }

    @Test
    public void testDeleteRowsOne2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference("A1"); //
        final SpreadsheetCellReference b = this.cellReference("A2"); // replaced by c
        final SpreadsheetCellReference c = this.cellReference("A3"); // DELETED
        final SpreadsheetCellReference d = this.cellReference("B10"); // moved

        engine.saveCell(this.cell(a, "=1+2"), context);
        engine.saveCell(this.cell(b, "=3+4"), context);
        engine.saveCell(this.cell(c, "=5+6"), context);
        engine.saveCell(this.cell(d, "=7+8"), context);

        final int count = 1;
        this.deleteRowsAndCheck(engine,
                b.row(),
                count,
                context,
                this.formattedCellWithValue(c.addRow(-count), "=5+6", number(5 + 6)),
                this.formattedCellWithValue(d.addRow(-count), "=7+8", number(7 + 8))); // $b delete

        this.countAndCheck(context.storeRepository().cells(), 3);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+2",
                number(1 + 2));
    }

    @Test
    public void testDeleteRowsMany() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); //
        final SpreadsheetCellReference b = this.cellReference(0, 1); // replaced by c
        final SpreadsheetCellReference c = this.cellReference(0, 2); // DELETED
        final SpreadsheetCellReference d = this.cellReference(1, 9); // moved

        engine.saveCell(this.cell(a, "=1+2"), context);
        engine.saveCell(this.cell(b, "=3+4"), context);
        engine.saveCell(this.cell(c, "=5+6"), context);
        engine.saveCell(this.cell(d, "=7+8"), context);

        final int count = 2;
        this.deleteRowsAndCheck(engine,
                b.row(),
                count,
                context,
                this.formattedCellWithValue(d.addRow(-count), "=7+8", number(7 + 8)));

        this.countAndCheck(context.storeRepository().cells(), 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+2",
                number(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.addRow(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=7+8",
                number(7 + 8));
    }

    // delete row with labels to cell references..................................................................

    @Test
    public void testDeleteRowsWithLabelsToCellUnmodified() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a = this.cellReference("$A$1");
        final SpreadsheetCellReference b = this.cellReference("$A$2");
        final SpreadsheetCellReference c = this.cellReference("$A$6");

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, a));

        engine.saveCell(this.cell(a, "=1+0"), context);
        engine.saveCell(this.cell(b, "=20+0+" + LABEL), context);
        engine.saveCell(this.cell(c, "=99+0"), context);

        final int count = 2;
        this.deleteRowsAndCheck(engine,
                b.row().add(count),
                count,
                context,
                this.formattedCellWithValue("$A$4", "=99+0", number(99 + 0))); // $c moved, $b unmodified label refs $a also unmodified.

        this.countAndCheck(cellStore, 3);

        this.loadLabelAndCheck(labelStore, LABEL, a);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0",
                number(1));

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=20+0+" + LABEL,
                number(20 + 0 + 1));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.addRow(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=99+0",
                number(99));
    }

    @Test
    public void testDeleteRowsWithLabelsToCellFixed() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a = this.cellReference("$A$1");
        final SpreadsheetCellReference b = this.cellReference("$A$6");

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, b));

        cellStore.save(this.cell(a, "=1+0+" + LABEL));
        cellStore.save(this.cell(b, "=2+0"));

        final int count = 2;
        this.deleteRowsAndCheck(engine,
                a.row().add(1),
                count,
                context,
                SpreadsheetDelta.with(Sets.of(
                        this.formattedCellWithValue(a, "=1+0+" + LABEL, number(1 + 0 + 2 + 0)),
                        this.formattedCellWithValue(b.addRow(-count), "=2+0", number(2 + 0)))
                ).setLabels(
                        Sets.of(
                                LABEL.mapping(SpreadsheetCellReference.parseCellReference("A4"))
                        )
                )
        ); // $b moved

        this.countAndCheck(cellStore, 2);

        this.loadLabelAndCheck(labelStore, LABEL, b.addRow(-count));

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "=1+0+" + LABEL,
                number(3));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.addRow(-count),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "=2+0",
                number(2));
    }

    @Test
    public void testDeleteRowsWithLabelToCellReferenceDeleted() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference("$A$1");
        final SpreadsheetCellReference b = this.cellReference("$A$6");

        engine.saveCell(this.cell(a, "=1+" + b), context);
        engine.saveCell(this.cell(b, "=2+0"), context);

        this.deleteRowsAndCheck(engine,
                b.row(),
                1,
                context,
                this.formattedCellWithError(a, "=1+InvalidCellReference(\"" + b + "\")", "Invalid cell reference: $A$6"));

        this.countAndCheck(context.storeRepository().cells(), 1);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+InvalidCellReference(\"" + b + "\")",
                "Invalid cell reference: " + b); // reference should have been fixed.
    }

    @Test
    public void testDeleteRowsWithCellReferencesFixed() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference("$A$1"); // A1
        final SpreadsheetCellReference b = this.cellReference("$A$2"); // B1
        final SpreadsheetCellReference c = this.cellReference("$A$11"); // A10 deleted
        final SpreadsheetCellReference d = this.cellReference("$I$14"); // H13 moved
        final SpreadsheetCellReference e = this.cellReference("$J$15"); // I14 moved

        engine.saveCell(this.cell(a, "=1+" + d), context);
        engine.saveCell(this.cell(b, "=2"), context);
        engine.saveCell(this.cell(c, "=3"), context); // DELETED
        engine.saveCell(this.cell(d, "=4"), context); // REFRESHED
        engine.saveCell(this.cell(e, "=5+" + b), context); // REFRESHED =5+2

        final int count = 1;
        this.deleteRowsAndCheck(engine,
                c.row(),
                count,
                context,
                this.formattedCellWithValue("$A$1", "=1+$I$13", number(1 + 4)),
                this.formattedCellWithValue("$I$13", "=4", number(4)),
                this.formattedCellWithValue("$J$14", "=5+$A$2", number(5 + 2))); // $c delete

        this.countAndCheck(context.storeRepository().cells(), 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+" + d.addRow(-count),
                number(1 + 4)); // reference should have been fixed.

        this.loadCellAndCheckFormatted2(engine,
                b,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                number(2),
                FORMATTED_PATTERN_SUFFIX);

        this.loadCellAndCheckFormatted2(engine,
                d.addRow(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                number(4),
                FORMATTED_PATTERN_SUFFIX);

        this.loadCellAndCheckFormulaAndValue(engine,
                e.addRow(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=5+" + b,
                number(5 + 2));
    }

    @Test
    public void testDeleteRowsWithCellReferencesFixed2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference("$A$1");
        final SpreadsheetCellReference b = this.cellReference("$A$2");
        final SpreadsheetCellReference c = this.cellReference("$A$11");// DELETED
        final SpreadsheetCellReference d = this.cellReference("$I$14"); // MOVED
        final SpreadsheetCellReference e = this.cellReference("$J$15"); // MOVED

        engine.saveCell(this.cell(a, "=1+" + d), context);
        engine.saveCell(this.cell(b, "=2"), context);
        engine.saveCell(this.cell(c, "=3"), context);
        engine.saveCell(this.cell(d, "=4"), context);
        engine.saveCell(this.cell(e, "=5+" + b), context); // =5+2

        final int count = 2;
        this.deleteRowsAndCheck(engine,
                c.row(),
                count,
                context,
                this.formattedCellWithValue("$A$1", "=1+$I$12", number(1 + 4)),
                this.formattedCellWithValue("$I$12", "=4", number(4)),
                this.formattedCellWithValue("$J$13", "=5+$A$2", number(5 + 2))); // $c delete

        this.countAndCheck(
                context.storeRepository()
                        .cells(),
                4
        );

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+" + d.addRow(-count),
                number(1 + 4)); // reference should have been fixed.

        this.loadCellAndCheckFormatted2(engine,
                b,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                number(2),
                FORMATTED_PATTERN_SUFFIX);

        this.loadCellAndCheckFormatted2(engine,
                d.addRow(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                number(4),
                FORMATTED_PATTERN_SUFFIX);

        this.loadCellAndCheckFormulaAndValue(engine,
                e.addRow(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=5+" + b,
                number(5 + 2));
    }

    // delete range....................................................................................

    @Test
    public void testDeleteRowsWithLabelsToRangeUnmodified() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a = this.cellReference(0, 0);
        final SpreadsheetCellReference b = this.cellReference(0, 5);
        final SpreadsheetCellReference c = this.cellReference(0, 10);
        final SpreadsheetCellReference d = this.cellReference(0, 15);

        final SpreadsheetRange ab = a.spreadsheetRange(b);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, ab));

        engine.saveCell(this.cell(a, "=1+0"), context);
        engine.saveCell(this.cell(c, "=20+0+" + LABEL), context);
        engine.saveCell(this.cell(d, "=99+0"), context); // DELETED

        final int count = 2;
        this.deleteRowsAndCheck(engine,
                d.row(),
                count,
                context);

        this.countAndCheck(cellStore, 2); // a&c
        this.countAndCheck(labelStore, 1);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0",
                number(1));

        this.loadCellAndCheckFormulaAndValue(engine,
                c,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=20+0+" + LABEL,
                number(21));

        this.loadLabelAndCheck(labelStore, LABEL, ab);
    }

    @Test
    public void testDeleteRowsWithLabelsToRangeDeleted() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a = this.cellReference(0, 0);
        final SpreadsheetCellReference b = this.cellReference(0, 5);
        final SpreadsheetCellReference c = this.cellReference(0, 10);

        engine.saveCell(this.cell(a, "=1+0"), context);

        final SpreadsheetRange bc = b.spreadsheetRange(c);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, bc));

        final int count = c.row().value() - b.row().value() + 1;
        this.deleteRowsAndCheck(engine,
                b.row(),
                count,
                context); // b..c deleted

        this.countAndCheck(cellStore, 1); // a
        this.countAndCheck(labelStore, 0);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0",
                number(1));
    }

    @Test
    public void testDeleteRowsWithLabelsToRangeDeleted2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a = this.cellReference(0, 0);
        final SpreadsheetCellReference b = this.cellReference(0, 5);
        final SpreadsheetCellReference c = this.cellReference(0, 10);
        final SpreadsheetCellReference d = this.cellReference(0, 20);

        final SpreadsheetRange bc = b.spreadsheetRange(c);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, bc));

        engine.saveCell(this.cell(a, "=1+0"), context);
        engine.saveCell(this.cell(d, "=20+0"), context);

        final int count = c.row().value() - b.row().value() + 1;
        this.deleteRowsAndCheck(engine,
                b.row(),
                count,
                context,
                this.formattedCellWithValue(d.addRow(-count), "=20+0", number(20 + 0))); // b..c deleted, d moved

        this.countAndCheck(cellStore, 2); // a&d
        this.countAndCheck(labelStore, 0);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0",
                number(1));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.addRow(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=20+0",
                number(20));
    }

    @Test
    public void testDeleteRowsWithLabelsToRangeDeleted3() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a = this.cellReference(0, 0);
        final SpreadsheetCellReference b = this.cellReference(0, 5);
        final SpreadsheetCellReference c = this.cellReference(0, 10);

        final SpreadsheetRange bc = b.spreadsheetRange(c);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, bc));

        engine.saveCell(this.cell(a, "=1+0+" + LABEL), context);
        engine.saveCell(this.cell(b, "=20+0"), context);

        final int count = c.row().value() - b.row().value() + 1;
        this.deleteRowsAndCheck(engine,
                b.row(),
                count,
                context,
                formattedCellWithError(a, "=1+0+" + LABEL, "Unknown label: " + LABEL)); // b..c deleted

        this.countAndCheck(cellStore, 1); // a
        this.countAndCheck(labelStore, 0);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0+" + LABEL,
                "Unknown label: " + LABEL);
    }

    @Test
    public void testDeleteRowsWithLabelsToRangeFixed() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a = this.cellReference(0, 0);
        final SpreadsheetCellReference b = this.cellReference(0, 5);
        final SpreadsheetCellReference c = this.cellReference(0, 10);
        final SpreadsheetCellReference d = this.cellReference(0, 15);
        final SpreadsheetCellReference e = this.cellReference(0, 20);

        final SpreadsheetRange de = d.spreadsheetRange(e);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, de));

        engine.saveCell(this.cell(a, "=1+0+" + LABEL), context);
        engine.saveCell(this.cell(d, "=20+0"), context);

        final int count = c.row().value() - b.row().value() + 1;
        this.deleteRowsAndCheck(
                engine,
                b.row(),
                count,
                context,
                SpreadsheetDelta.with(
                        Sets.of(
                                this.formattedCellWithValue(d.addRow(-count), "=20+0", number(20 + 0))
                        )
                ).setLabels(
                        Sets.of(
                                LABEL.mapping(SpreadsheetCellReference.parseCellReference("A10"))
                        )
                )
        ); // b..c deleted, d moved

        this.countAndCheck(cellStore, 2); // a&d

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0+" + LABEL,
                number(1 + 20));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.addRow(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=20+0",
                number(20));

        this.countAndCheck(labelStore, 1);
        final SpreadsheetCellReference begin = d.addRow(-count);
        final SpreadsheetCellReference end = e.addRow(-count);
        this.loadLabelAndCheck(labelStore, LABEL, begin.spreadsheetRange(end));
    }

    @SuppressWarnings("unused")
    @Test
    public void testDeleteRowsWithLabelsToRangeFixed2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelStore labelStore = context.storeRepository()
                .labels();

        final SpreadsheetCellReference a = this.cellReference(0, 0);
        final SpreadsheetCellReference b = this.cellReference(0, 5);
        final SpreadsheetCellReference c = this.cellReference(0, 10);
        final SpreadsheetCellReference d = this.cellReference(0, 15);
        final SpreadsheetCellReference e = this.cellReference(0, 20);

        final SpreadsheetRange ce = c.spreadsheetRange(e);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, ce));

        final int count = e.row().value() - d.row().value() + 1;
        this.deleteRowsAndCheck(engine,
                d.row(),
                count,
                context); // b..c deleted, d moved

        this.countAndCheck(labelStore, 1);
        this.loadLabelAndCheck(labelStore, LABEL, c.spreadsheetRange(d));
    }

    @Test
    public void testDeleteRowsWithLabelsToRangeFixed3() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelStore labelStore = context.storeRepository()
                .labels();

        final SpreadsheetCellReference b = this.cellReference(0, 5);
        final SpreadsheetCellReference c = this.cellReference(0, 10);
        final SpreadsheetCellReference d = this.cellReference(0, 15);

        final SpreadsheetRange bd = b.spreadsheetRange(d);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, bd));

        final int count = 1;
        this.deleteRowsAndCheck(engine,
                c.row(),
                count,
                context); // b..c deleted, d moved

        this.countAndCheck(labelStore, 1);

        final SpreadsheetCellReference end = d.addRow(-count);
        this.loadLabelAndCheck(labelStore, LABEL, b.spreadsheetRange(end));
    }

    @SuppressWarnings("unused")
    @Test
    public void testDeleteRowsWithLabelsToRangeFixed4() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelStore labelStore = context.storeRepository()
                .labels();

        final SpreadsheetCellReference a = this.cellReference(0, 5);  // delete
        final SpreadsheetCellReference b = this.cellReference(0, 10); // range delete
        final SpreadsheetCellReference c = this.cellReference(0, 15); // range delete
        final SpreadsheetCellReference d = this.cellReference(0, 20); // range
        final SpreadsheetCellReference e = this.cellReference(0, 25); // range

        final SpreadsheetRange be = b.spreadsheetRange(e);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, be));

        final int count = c.row().value() - a.row().value();
        this.deleteRowsAndCheck(engine,
                a.row(),
                count,
                context);

        this.countAndCheck(labelStore, 1);

        this.loadLabelAndCheck(labelStore, LABEL, a.spreadsheetRange(b));
    }

    // deleteColumn....................................................................................................

    @Test
    public void testDeleteColumnsNone() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference reference = this.cellReference(1, 0); // A2

        engine.saveCell(this.cell(reference, "=99+0"), context);

        this.addFailingCellSaveWatcherAndDeleteWatcher(context);

        engine.deleteColumns(reference.column(), 0, context);

        this.countAndCheck(context.storeRepository().cells(), 1);

        this.loadCellAndCheckFormulaAndValue(engine,
                reference,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=99+0",
                number(99));
    }

    @Test
    public void testDeleteColumnsOne() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference("$A1");
        final SpreadsheetCellReference b = this.cellReference(1, 0); // B1
        final SpreadsheetCellReference c = this.cellReference(2, 0); // C1

        engine.saveCell(this.cell(a, "=1+2"), context);
        engine.saveCell(this.cell(b, "=3+4"), context);
        engine.saveCell(this.cell(c, "=5+6"), context);

        this.deleteColumnsAndCheck(engine,
                b.column(),
                1,
                context,
                this.formattedCellWithValue("$B$1", "=5+6", number(5 + 6)));

        this.countAndCheck(context.storeRepository().cells(), 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+2",
                number(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=5+6",
                number(5 + 6));
    }

    @Test
    public void testDeleteColumnsOne2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference("$A$1"); //
        final SpreadsheetCellReference b = this.cellReference("$B$1"); // replaced by c
        final SpreadsheetCellReference c = this.cellReference("$C$1"); // DELETED
        final SpreadsheetCellReference d = this.cellReference("$J$2"); // moved

        engine.saveCell(this.cell(a, "=1+2"), context);
        engine.saveCell(this.cell(b, "=3+4"), context); // DELETE
        engine.saveCell(this.cell(c, "=5+6"), context);
        engine.saveCell(this.cell(d, "=7+8"), context);

        final int count = 1;
        this.deleteColumnsAndCheck(engine,
                b.column(),
                count,
                context,
                this.formattedCellWithValue("$B$1", "=5+6", number(5 + 6)),
                this.formattedCellWithValue("$I$2", "=7+8", number(7 + 8))); // $b delete

        this.countAndCheck(context.storeRepository().cells(), 3);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+2",
                number(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=5+6",
                number(5 + 6));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.addColumn(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=7+8",
                number(7 + 8));
    }

    @Test
    public void testDeleteColumnsMany() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference("$A$1"); //
        final SpreadsheetCellReference b = this.cellReference("$B$1"); // DELETED
        final SpreadsheetCellReference c = this.cellReference("$C$1"); // DELETED
        final SpreadsheetCellReference d = this.cellReference("$J$2"); // MOVED

        engine.saveCell(this.cell(a, "=1+2"), context);
        engine.saveCell(this.cell(b, "=3+4"), context);
        engine.saveCell(this.cell(c, "=5+6"), context);
        engine.saveCell(this.cell(d, "=7+8"), context);

        final int count = 2;
        this.deleteColumnsAndCheck(engine,
                b.column(),
                count,
                context,
                this.formattedCellWithValue("$H$2", "=7+8", number(7 + 8))); // $b, $c deleted

        this.countAndCheck(context.storeRepository().cells(), 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+2",
                number(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.addColumn(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=7+8",
                number(7 + 8));
    }

    // delete column with labels to cell references..................................................................

    @Test
    public void testDeleteColumnsWithLabelsToCellUnmodified() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a = this.cellReference("$A$1");
        final SpreadsheetCellReference b = this.cellReference("$B$1");
        final SpreadsheetCellReference c = this.cellReference("$F$1");

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, a));

        engine.saveCell(this.cell(a, "=1+0"), context);
        engine.saveCell(this.cell(b, "=20+0+" + LABEL), context);
        engine.saveCell(this.cell(c, "=99+0"), context);

        final int count = 2;
        this.deleteColumnsAndCheck(engine,
                b.column().add(2),
                count,
                context,
                this.formattedCellWithValue("$D$1", "=99+0", number(99 + 0)));

        this.countAndCheck(cellStore, 3);

        this.loadLabelAndCheck(labelStore, LABEL, a);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0",
                number(1));

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=20+0+" + LABEL,
                number(21));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.addColumn(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=99+0",
                number(99));
    }

    @Test
    public void testDeleteColumnsWithLabelsToCellFixed() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a = this.cellReference("$A$1");
        final SpreadsheetCellReference b = this.cellReference("$E$1");

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, b));

        engine.saveCell(this.cell(a, "=1+0+" + LABEL), context);
        engine.saveCell(this.cell(b, "=2+0"), context);

        final int count = 2;
        this.deleteColumnsAndCheck(
                engine,
                a.column().add(1),
                count,
                context,
                SpreadsheetDelta.with(
                        Sets.of(this.formattedCellWithValue("$C$1", "=2+0", number(2 + 0)))
                ).setLabels(
                        Sets.of(
                                LABEL.mapping(SpreadsheetCellReference.parseCellReference("C1"))
                        )
                )
        ); // $b moved

        this.countAndCheck(cellStore, 2);

        this.loadLabelAndCheck(labelStore, LABEL, b.addColumn(-count));

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0+" + LABEL,
                number(3));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.addColumn(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=2+0",
                number(2));
    }

    @Test
    public void testDeleteColumnsWithLabelToCellReferenceDeleted() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference("$A$1");
        final SpreadsheetCellReference b = this.cellReference("$E$1");

        engine.saveCell(this.cell(a, "=1+" + b), context);
        engine.saveCell(this.cell(b, "=2+0"), context);

        this.deleteColumnsAndCheck(engine,
                b.column(),
                1,
                context,
                this.formattedCellWithError(a, "=1+InvalidCellReference(\"" + b + "\")", "Invalid cell reference: $E$1")); // $v delete

        this.countAndCheck(context.storeRepository().cells(), 1);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+InvalidCellReference(\"" + b + "\")",
                "Invalid cell reference: " + b); // reference should have been fixed.
    }

    @Test
    public void testDeleteColumnsWithCellReferencesFixed() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference("$A$1");
        final SpreadsheetCellReference b = this.cellReference("$B$1");
        final SpreadsheetCellReference c = this.cellReference("$K$1"); // DELETED
        final SpreadsheetCellReference d = this.cellReference("$N$9"); // MOVED
        final SpreadsheetCellReference e = this.cellReference("$O$10"); // MOVED

        engine.saveCell(this.cell(a, "=1+" + d), context);
        engine.saveCell(this.cell(b, "=2"), context);
        engine.saveCell(this.cell(c, "=3"), context);
        engine.saveCell(this.cell(d, "=4"), context);
        engine.saveCell(this.cell(e, "=5+" + b), context); // =5+2

        final int count = 1;
        this.deleteColumnsAndCheck(engine,
                c.column(),
                count,
                context,
                this.formattedCellWithValue("$A$1", "=1+$M$9", number(1 + 4)),
                this.formattedCellWithValue("$M$9", "=4", number(4)),
                this.formattedCellWithValue("$N$10", "=5+" + b, number(5 + 2))); // $c delete

        this.countAndCheck(context.storeRepository().cells(), 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+" + d.addColumn(-count),
                number(1 + 4)); // reference should have been fixed.

        this.loadCellAndCheckFormatted2(engine,
                b,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                number(2),
                FORMATTED_PATTERN_SUFFIX);

        this.loadCellAndCheckFormatted2(engine,
                d.addColumn(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                number(4),
                FORMATTED_PATTERN_SUFFIX);

        this.loadCellAndCheckFormulaAndValue(engine,
                e.addColumn(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=5+" + b,
                number(5 + 2));
    }

    @Test
    public void testDeleteColumnsWithCellReferencesFixed2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference("$A$1"); // A1
        final SpreadsheetCellReference b = this.cellReference("$B$1"); // B1
        final SpreadsheetCellReference c = this.cellReference("$K$1"); // J1 deleted
        final SpreadsheetCellReference d = this.cellReference("$N$9"); // M8 moved
        final SpreadsheetCellReference e = this.cellReference("$O$10"); // N9 moved

        engine.saveCell(this.cell(a, "=1+" + d), context);
        engine.saveCell(this.cell(b, "=2"), context);
        engine.saveCell(this.cell(c, "=3"), context); // DELETED
        engine.saveCell(this.cell(d, "=4"), context); // MOVED
        engine.saveCell(this.cell(e, "=5+" + b), context); // MOVED

        final int count = 2;
        this.deleteColumnsAndCheck(engine,
                c.column(),
                count,
                context,
                this.formattedCellWithValue("$A$1", "=1+$L$9", number(1 + 4)),
                this.formattedCellWithValue("$L$9", "=4", number(4)),
                this.formattedCellWithValue("$M$10", "=5+$B$1", number(5 + 2))); // $c delete

        this.countAndCheck(context.storeRepository().cells(), 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+" + d.addColumn(-count),
                number(1 + 4)); // reference should have been fixed.

        this.loadCellAndCheckFormatted2(engine,
                b,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                number(2),
                FORMATTED_PATTERN_SUFFIX);

        this.loadCellAndCheckFormatted2(engine,
                d.addColumn(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                number(4),
                FORMATTED_PATTERN_SUFFIX);

        this.loadCellAndCheckFormulaAndValue(engine,
                e.addColumn(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=5+" + b,
                number(5 + 2));
    }

    // delete range....................................................................................

    @Test
    public void testDeleteColumnsWithLabelsToRangeUnmodified() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a = this.cellReference(0, 0);
        final SpreadsheetCellReference b = this.cellReference(5, 0);
        final SpreadsheetCellReference c = this.cellReference(10, 0);
        final SpreadsheetCellReference d = this.cellReference(15, 0);

        final SpreadsheetRange ab = a.spreadsheetRange(b);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, ab));

        engine.saveCell(this.cell(a, "=1+0"), context);
        engine.saveCell(this.cell(c, "=20+0+" + LABEL), context);
        engine.saveCell(this.cell(d, "=99+0"), context); // deleted!!!

        final int count = 2;
        this.deleteColumnsAndCheck(engine,
                d.column(),
                count,
                context); // $d moved

        this.countAndCheck(cellStore, 2); // a&c
        this.countAndCheck(labelStore, 1);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0",
                number(1));

        this.loadCellAndCheckFormulaAndValue(engine,
                c,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=20+0+" + LABEL,
                number(21));

        this.loadLabelAndCheck(labelStore, LABEL, ab);
    }

    @Test
    public void testDeleteColumnsWithLabelsToRangeDeleted() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a = this.cellReference(0, 0);
        final SpreadsheetCellReference b = this.cellReference(5, 0);
        final SpreadsheetCellReference c = this.cellReference(10, 0);

        final SpreadsheetRange bc = b.spreadsheetRange(c);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, bc));

        engine.saveCell(this.cell(a, "=1+0"), context);

        final int count = c.column().value() - b.column().value() + 1;
        this.deleteColumnsAndCheck(engine,
                b.column(),
                count,
                context); // b..c deleted, d moved

        this.countAndCheck(cellStore, 1); // a
        this.countAndCheck(labelStore, 0);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0",
                number(1));
    }

    @Test
    public void testDeleteColumnsWithLabelsToRangeDeleted2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a = this.cellReference(0, 0);
        final SpreadsheetCellReference b = this.cellReference(5, 0);
        final SpreadsheetCellReference c = this.cellReference(10, 0);
        final SpreadsheetCellReference d = this.cellReference(20, 0);

        final SpreadsheetRange bc = b.spreadsheetRange(c);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, bc));

        engine.saveCell(this.cell(a, "=1+0"), context);
        engine.saveCell(this.cell(d, "=20+0"), context);

        final int count = c.column().value() - b.column().value() + 1;
        this.deleteColumnsAndCheck(engine,
                b.column(),
                count,
                context,
                this.formattedCellWithValue(d.addColumn(-count), "=20+0", number(20 + 0))); // b..c deleted, d moved

        this.countAndCheck(cellStore, 2); // a&d
        this.countAndCheck(labelStore, 0);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0",
                number(1));
    }

    @Test
    public void testDeleteColumnsWithLabelsToRangeDeleted3() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a = this.cellReference(0, 0);
        final SpreadsheetCellReference b = this.cellReference(5, 0);
        final SpreadsheetCellReference c = this.cellReference(10, 0);

        final SpreadsheetRange bc = b.spreadsheetRange(c);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, bc));

        engine.saveCell(this.cell(a, "=1+0+" + LABEL), context);
        engine.saveCell(this.cell(b, "=20+0"), context);

        final int count = c.column().value() - b.column().value() + 1;
        this.deleteColumnsAndCheck(engine,
                b.column(),
                count,
                context,
                this.formattedCellWithError("$A$1", "=1+0+" + LABEL, "Unknown label: " + LABEL)); // b..c deleted

        this.countAndCheck(cellStore, 1); // a
        this.countAndCheck(labelStore, 0);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0+" + LABEL,
                "Unknown label: " + LABEL);
    }

    @Test
    public void testDeleteColumnsWithLabelsToRangeFixed() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a = this.cellReference(0, 0);
        final SpreadsheetCellReference b = this.cellReference(5, 0);
        final SpreadsheetCellReference c = this.cellReference(10, 0);
        final SpreadsheetCellReference d = this.cellReference(15, 0);
        final SpreadsheetCellReference e = this.cellReference(20, 0);

        final SpreadsheetRange de = d.spreadsheetRange(e);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, de));

        engine.saveCell(this.cell(a, "=1+0+" + LABEL), context);
        engine.saveCell(this.cell(d, "=20+0"), context);

        final int count = c.column().value() - b.column().value() + 1;
        this.deleteColumnsAndCheck(engine,
                b.column(),
                count,
                context,
                SpreadsheetDelta.with(
                        Sets.of(
                                this.formattedCellWithValue(d.addColumn(-count), "=20+0", number(20 + 0)))
                ).setLabels(
                        Sets.of(
                                LABEL.mapping(SpreadsheetCellReference.parseCellReference("J1"))
                        )
                )
        ); // b..c deleted, d moved

        this.countAndCheck(cellStore, 2); // a&d

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0+" + LABEL,
                number(1 + 20));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.addColumn(-count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=20+0",
                number(20));

        this.countAndCheck(labelStore, 1);
        final SpreadsheetCellReference begin = d.addColumn(-count);
        final SpreadsheetCellReference end = e.addColumn(-count);
        this.loadLabelAndCheck(labelStore, LABEL, begin.spreadsheetRange(end));
    }

    @SuppressWarnings("unused")
    @Test
    public void testDeleteColumnsWithLabelsToRangeFixed2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a = this.cellReference(0, 0);
        final SpreadsheetCellReference b = this.cellReference(5, 0);
        final SpreadsheetCellReference c = this.cellReference(10, 0);
        final SpreadsheetCellReference d = this.cellReference(15, 0);
        final SpreadsheetCellReference e = this.cellReference(20, 0);

        final SpreadsheetRange ce = c.spreadsheetRange(e);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, ce));

        final int count = e.column().value() - d.column().value() + 1;
        this.deleteColumnsAndCheck(engine,
                d.column(),
                count,
                context); // b..c deleted, d moved

        this.countAndCheck(labelStore, 1);
        this.loadLabelAndCheck(labelStore, LABEL, c.spreadsheetRange(d));
    }

    @Test
    public void testDeleteColumnsWithLabelsToRangeFixed3() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelStore labelStore = context.storeRepository()
                .labels();

        final SpreadsheetCellReference b = this.cellReference(5, 0);
        final SpreadsheetCellReference c = this.cellReference(10, 0);
        final SpreadsheetCellReference d = this.cellReference(15, 0);

        final SpreadsheetRange bd = b.spreadsheetRange(d);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, bd));

        final int count = 1;
        this.deleteColumnsAndCheck(engine,
                c.column(),
                count,
                context); // b..c deleted, d moved

        this.countAndCheck(labelStore, 1);

        final SpreadsheetCellReference end = d.addColumn(-count);
        this.loadLabelAndCheck(labelStore, LABEL, b.spreadsheetRange(end));
    }

    @SuppressWarnings("unused")
    @Test
    public void testDeleteColumnsWithLabelsToRangeFixed4() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelStore labelStore = context.storeRepository()
                .labels();

        final SpreadsheetCellReference a = this.cellReference(5, 0);  // delete
        final SpreadsheetCellReference b = this.cellReference(10, 0); // range delete
        final SpreadsheetCellReference c = this.cellReference(15, 0); // range delete
        final SpreadsheetCellReference d = this.cellReference(20, 0); // range
        final SpreadsheetCellReference e = this.cellReference(25, 0); // range

        final SpreadsheetRange be = b.spreadsheetRange(e);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, be));

        final int count = c.column().value() - a.column().value();
        this.deleteColumnsAndCheck(engine,
                a.column(),
                count,
                context);

        this.countAndCheck(labelStore, 1);

        this.loadLabelAndCheck(labelStore, LABEL, a.spreadsheetRange(b));
    }

    // insertColumn....................................................................................................

    @Test
    public void testInsertColumnsZero() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference reference = this.cellReference("$CV$1");

        engine.saveCell(this.cell(reference, "=99+0"),
                context);

        this.addFailingCellSaveWatcherAndDeleteWatcher(context);

        this.insertColumnsAndCheck(engine,
                reference.column(),
                0,
                context);

        this.countAndCheck(context.storeRepository().cells(), 1);

        this.loadCellAndCheckFormulaAndValue(engine,
                reference,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=99+0",
                number(99));
    }

    @Test
    public void testInsertColumns() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference("$A$1"); // A1
        final SpreadsheetCellReference b = this.cellReference("$B$1"); // MOVED
        final SpreadsheetCellReference c = this.cellReference("$C$1"); // MOVED

        engine.saveCell(this.cell(a, "=1+2"), context);
        engine.saveCell(this.cell(b, "=3+4"), context);
        engine.saveCell(this.cell(c, "=5+6"), context);

        final int count = 1;
        this.insertColumnsAndCheck(engine,
                b.column(),
                count,
                context,
                this.formattedCellWithValue("$C$1", "=3+4", number(3 + 4)),
                this.formattedCellWithValue("$D$1", "=5+6", number(5 + 6)));

        this.countAndCheck(context.storeRepository().cells(), 3);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+2",
                number(1 + 2));
    }

    @Test
    public void testInsertColumns2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference("$A$1"); // A1
        final SpreadsheetCellReference b = this.cellReference("$B$1"); // MOVED
        final SpreadsheetCellReference c = this.cellReference("$C$1"); // MOVED

        engine.saveCell(this.cell(a, "=1+2"), context);
        engine.saveCell(this.cell(b, "=3+4"), context);
        engine.saveCell(this.cell(c, "=5+6"), context);

        final int count = 1;
        this.insertColumnsAndCheck(engine,
                b.column(),
                count,
                context,
                this.formattedCellWithValue("$C$1", "=3+4", number(3 + 4)),
                this.formattedCellWithValue("$D$1", "=5+6", number(5 + 6))); // $b insert

        this.countAndCheck(context.storeRepository().cells(), 3);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+2",
                number(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.addColumn(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=3+4",
                number(3 + 4));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.addColumn(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=5+6",
                number(5 + 6));
    }

    @Test
    public void testInsertColumnsWithLabelToCellIgnored() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a = this.cellReference("$C$2"); //
        final SpreadsheetCellReference b = this.cellReference("$E$4"); // moved

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, a));

        engine.saveCell(this.cell(a, "=100"), context);
        engine.saveCell(this.cell(b, "=2+" + LABEL), context);

        final int count = 1;
        this.insertColumnsAndCheck(engine,
                b.column(),
                count,
                context,
                this.formattedCellWithValue("$F$4", "=2+" + LABEL, number(2 + 100))); // $b insert

        this.loadLabelAndCheck(labelStore, LABEL, a);

        this.countAndCheck(cellStore, 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=100",
                number(100));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.addColumn(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=2+" + LABEL,
                number(2 + 100));
    }

    @Test
    public void testInsertColumnsWithLabelToCell() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a = this.cellReference("$A$1"); // A1
        final SpreadsheetCellReference b = this.cellReference("$B$1"); // moved
        final SpreadsheetCellReference c = this.cellReference("$C$1"); // MOVED
        final SpreadsheetCellReference d = this.cellReference("$N$9"); // moved

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, d));

        engine.saveCell(this.cell(a, "=1+" + LABEL), context);
        engine.saveCell(this.cell(b, "=2+0"), context);
        engine.saveCell(this.cell(c, "=3+0+" + LABEL), context);
        engine.saveCell(this.cell(d, "=99+0"), context);

        final int count = 1;
        this.insertColumnsAndCheck(engine,
                b.column(),
                count,
                context,
                SpreadsheetDelta.with(
                        Sets.of(this.formattedCellWithValue("$C$1", "=2+0", number(2 + 0)),
                                this.formattedCellWithValue("$D$1", "=3+0+" + LABEL, number(3 + 0 + 99 + 0)),
                                this.formattedCellWithValue("$O$9", "=99+0", number(99 + 0))
                        )
                ).setLabels(
                        Sets.of(
                                LABEL.mapping(SpreadsheetCellReference.parseCellReference("O9"))
                        )
                )
        ); // $b insert

        this.loadLabelAndCheck(labelStore, LABEL, d.addColumn(count));

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+" + LABEL,
                number(1 + 99));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.addColumn(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=2+0",
                number(2 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.addColumn(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=3+0+" + LABEL,
                number(3 + 99));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.addColumn(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=99+0",
                number(99 + 0));
    }

    @Test
    public void testInsertColumnsWithLabelToRangeUnchanged() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a = this.cellReference("$A$1"); // A1
        final SpreadsheetCellReference b = this.cellReference("$F$6"); // moved

        final SpreadsheetRange a1 = a.spreadsheetRange(a.add(1, 1));
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, a1));

        engine.saveCell(this.cell(a, "=99+0"), context);
        engine.saveCell(this.cell(b, "=2+0+" + LABEL), context);

        final int count = 1;
        this.insertColumnsAndCheck(engine,
                b.column(),
                count,
                context,
                this.formattedCellWithValue("$G$6", "=2+0+" + LABEL, number(2 + 0 + 99 + 0))); // $b insert

        this.countAndCheck(labelStore, 1);

        this.loadLabelAndCheck(labelStore, LABEL, a1);

        this.countAndCheck(cellStore, 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=99+0",
                number(99 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.addColumn(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=2+0+" + LABEL,
                number(2 + 99));
    }

    @Test
    public void testInsertColumnsWithLabelToRangeUpdated() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a = this.cellReference(0, 0);
        final SpreadsheetCellReference b = this.cellReference(5, 0);
        final SpreadsheetCellReference c = this.cellReference(10, 0);
        final SpreadsheetCellReference d = this.cellReference(15, 0);
        final SpreadsheetCellReference e = this.cellReference(20, 0);

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, c.spreadsheetRange(d)));

        engine.saveCell(this.cell(a, "=1+" + LABEL), context);
        engine.saveCell(this.cell(c, "=99+0"), context);

        this.insertColumnsAndCheck(
                engine,
                b.column(),
                c.column().value() - b.column().value(),
                context,
                SpreadsheetDelta.with(
                        Sets.of(
                                this.formattedCellWithValue("$P$1", "=99+0", number(99 + 0))
                        )
                ).setLabels(
                        Sets.of(
                                LABEL.mapping(SpreadsheetCellReference.parseCellReference("P1"))
                        )
                )
        ); // $b insert

        this.countAndCheck(labelStore, 1);
        this.loadLabelAndCheck(labelStore, LABEL, d.spreadsheetRange(e));

        this.countAndCheck(cellStore, 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+" + LABEL,
                number(1 + 99));

        this.loadCellAndCheckFormulaAndValue(engine,
                d,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=99+0",
                number(99 + 0));
    }

    @Test
    public void testInsertColumnsWithCellReferences() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference("$A$1"); // A1
        final SpreadsheetCellReference b = this.cellReference("$B$1"); // B1
        final SpreadsheetCellReference c = this.cellReference("$K$1"); // moved
        final SpreadsheetCellReference d = this.cellReference("$N$9"); // moved

        engine.saveCell(this.cell(a, "=1+0+" + d), context);
        engine.saveCell(this.cell(b, "=2+0"), context);
        engine.saveCell(this.cell(c, "=3+0"), context);
        engine.saveCell(this.cell(d, "=4+0+" + b), context);

        final int count = 1;
        this.insertColumnsAndCheck(engine,
                c.column(),
                count,
                context,
                this.formattedCellWithValue("$A$1", "=1+0+$O$9", number(1 + 0 + 4 + 0 + 2 + 0)),
                this.formattedCellWithValue("$L$1", "=3+0", number(3 + 0)),
                this.formattedCellWithValue("$O$9", "=4+0+" + b, number(4 + 0 + 2 + 0))); // $c insert

        this.countAndCheck(context.storeRepository().cells(), 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0+" + d.addColumn(count),
                number(1 + 0 + 4 + 2)); // reference should have been fixed.

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=2+0",
                number(2));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.addColumn(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=3+0",
                number(3));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.addColumn(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=4+0+" + b,
                number(4 + 2));
    }

    @Test
    public void testInsertColumnsWithCellReferences2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference("$A$1"); // A1
        final SpreadsheetCellReference b = this.cellReference("$B$1"); //
        final SpreadsheetCellReference c = this.cellReference("$K$1"); // moved
        final SpreadsheetCellReference d = this.cellReference("$N$9"); // moved

        engine.saveCell(this.cell(a, "=1+0+" + d), context);
        engine.saveCell(this.cell(b, "=2+0"), context);
        engine.saveCell(this.cell(c, "=3+0"), context);
        engine.saveCell(this.cell(d, "=4+0+" + b), context); // =5+2

        final int count = 2;
        this.insertColumnsAndCheck(engine,
                c.column(),
                count,
                context,
                this.formattedCellWithValue("$A$1", "=1+0+$P$9", number(1 + 0 + 4 + 0 + 2 + 0)),
                this.formattedCellWithValue("$M$1", "=3+0", number(3 + 0)),
                this.formattedCellWithValue("$P$9", "=4+0+" + b, number(4 + 0 + 2 + 0))); // $c insert

        this.countAndCheck(context.storeRepository().cells(), 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0+" + d.addColumn(count),
                number(1 + 0 + 4 + 2)); // reference should have been fixed.

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=2+0",
                number(2 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.addColumn(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=3+0",
                number(3 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.addColumn(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=4+0+" + b,
                number(4 + 0 + 2));
    }

    @Test
    public void testInsertColumnsSeveral() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference("$A$1"); //
        final SpreadsheetCellReference b = this.cellReference("$K$1"); // MOVED
        final SpreadsheetCellReference c = this.cellReference("$L$1"); // MOVED
        final SpreadsheetCellReference d = this.cellReference("$M$3"); // MOVED
        final SpreadsheetCellReference e = this.cellReference("$U$4"); // MOVED

        engine.saveCell(this.cell(a, "=1+0"), context);
        engine.saveCell(this.cell(b, "=2+0"), context);
        engine.saveCell(this.cell(c, "=3+0"), context);
        engine.saveCell(this.cell(d, "=4+0"), context);
        engine.saveCell(this.cell(e, "=5+0"), context);

        final int count = 5;
        this.insertColumnsAndCheck(engine,
                this.column(7),
                count,
                context,
                this.formattedCellWithValue("$P$1", "=2+0", number(2 + 0)),
                this.formattedCellWithValue("$Q$1", "=3+0", number(3 + 0)),
                this.formattedCellWithValue("$R$3", "=4+0", number(4 + 0)),
                this.formattedCellWithValue("$Z$4", "=5+0", number(5 + 0))); // $b & $c

        this.countAndCheck(context.storeRepository().cells(), 5);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0",
                number(1 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.addColumn(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=2+0",
                number(2 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.addColumn(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=3+0",
                number(3 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.addColumn(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=4+0",
                number(4 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                e.addColumn(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=5+0",
                number(5 + 0));
    }

    // insertRow....................................................................................................

    @Test
    public void testInsertRowsZero() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference reference = this.cellReference("$A$100"); // A3

        engine.saveCell(this.cell(reference, "=99+0"), context);

        this.addFailingCellSaveWatcherAndDeleteWatcher(context);

        this.insertRowsAndCheck(engine,
                reference.row(),
                0,
                context);

        this.countAndCheck(context.storeRepository().cells(), 1);

        this.loadCellAndCheckFormulaAndValue(engine,
                reference,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=99+0",
                number(99));
    }

    @Test
    public void testInsertRows() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference("$A$1"); // A1
        final SpreadsheetCellReference b = this.cellReference("$A$2"); // MOVED
        final SpreadsheetCellReference c = this.cellReference("$A$3"); // MOVED

        engine.saveCell(this.cell(a, "=1+2"), context);
        engine.saveCell(this.cell(b, "=3+4"), context);
        engine.saveCell(this.cell(c, "=5+6"), context);

        final int count = 1;
        this.insertRowsAndCheck(engine,
                b.row(),
                count,
                context,
                this.formattedCellWithValue("$A$3", "=3+4", number(3 + 4)),
                this.formattedCellWithValue("$A$4", "=5+6", number(5 + 6)));

        this.countAndCheck(context.storeRepository().cells(), 3);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+2",
                number(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.addRow(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=3+4",
                number(3 + 4));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.addRow(count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=5+6",
                number(5 + 6));
    }

    @Test
    public void testInsertRows2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference("$A$1"); // A1
        final SpreadsheetCellReference b = this.cellReference("$A$2"); // MOVED
        final SpreadsheetCellReference c = this.cellReference("$A$3"); // MOVED

        engine.saveCell(this.cell(a, "=1+2"), context);
        engine.saveCell(this.cell(b, "=3+4"), context);
        engine.saveCell(this.cell(c, "=5+6"), context);

        final int count = 1;
        this.insertRowsAndCheck(engine,
                b.row(),
                count,
                context,
                this.formattedCellWithValue("$A$3", "=3+4", number(3 + 4)),
                this.formattedCellWithValue("$A$4", "=5+6", number(5 + 6))); // $b insert

        this.countAndCheck(context.storeRepository().cells(), 3);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+2",
                number(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.addRow(+count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=3+4",
                number(3 + 4));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.addRow(+count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=5+6",
                number(5 + 6));
    }

    @Test
    public void testInsertRowsWithLabelToCellIgnored() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a = this.cellReference("$B$3"); //
        final SpreadsheetCellReference b = this.cellReference("$D$5"); // moved

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, a));

        engine.saveCell(this.cell(a, "=100"), context);
        engine.saveCell(this.cell(b, "=2+" + LABEL), context);

        final int count = 1;
        this.insertRowsAndCheck(engine,
                b.row(),
                count,
                context,
                this.formattedCellWithValue("$D$6", "=2+" + LABEL, number(2 + 100))); // $b insert

        this.loadLabelAndCheck(labelStore, LABEL, a);

        this.countAndCheck(cellStore, 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=100",
                number(100));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.addRow(+count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=2+" + LABEL,
                number(2 + 100));
    }

    @Test
    public void testInsertRowsWithLabelToCell() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a = this.cellReference("$A$1"); // A1
        final SpreadsheetCellReference b = this.cellReference("$A$2"); // moved
        final SpreadsheetCellReference c = this.cellReference("$A$3"); // MOVED
        final SpreadsheetCellReference d = this.cellReference("$I$14"); // moved

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, d));

        engine.saveCell(this.cell(a, "=1+" + LABEL), context);
        engine.saveCell(this.cell(b, "=2+0"), context);
        engine.saveCell(this.cell(c, "=3+0+" + LABEL), context);
        engine.saveCell(this.cell(d, "=99+0"), context);

        final int count = 1;
        this.insertRowsAndCheck(engine,
                b.row(),
                count,
                context,
                this.formattedCellWithValue("$A$3", "=2+0", number(2 + 0)),
                this.formattedCellWithValue("$A$4", "=3+0+" + LABEL, number(3 + 0 + 99)),
                this.formattedCellWithValue("$I$15", "=99+0", number(99 + 0))); // $b insert

        this.loadLabelAndCheck(labelStore, LABEL, d.addRow(+count));

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+" + LABEL,
                number(1 + 99));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.addRow(+count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=2+0",
                number(2 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.addRow(+count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=3+0+" + LABEL,
                number(3 + 99));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.addRow(+count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=99+0",
                number(99 + 0));
    }

    @Test
    public void testInsertRowsWithLabelToRangeUnchanged() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a = this.cellReference("$A$1"); //
        final SpreadsheetCellReference b = this.cellReference("$F$6"); // moved

        final SpreadsheetRange a1 = a.spreadsheetRange(a.add(1, 1));
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, a1));

        engine.saveCell(this.cell(a, "=99+0"), context);
        engine.saveCell(this.cell(b, "=2+0+" + LABEL), context);

        final int count = 1;
        this.insertRowsAndCheck(engine,
                b.row(),
                count,
                context,
                this.formattedCellWithValue("$F$7", "=2+0+" + LABEL, number(2 + 0 + 99 + 0))); // $b insert

        this.countAndCheck(labelStore, 1);

        this.loadLabelAndCheck(labelStore, LABEL, a1);

        this.countAndCheck(cellStore, 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=99+0",
                number(99 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.addRow(+count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=2+0+" + LABEL,
                number(2 + 99));
    }

    @Test
    public void testInsertRowsWithLabelToRangeUpdated() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetCellStore cellStore = repository.cells();
        final SpreadsheetLabelStore labelStore = repository.labels();

        final SpreadsheetCellReference a = this.cellReference(0, 0);
        final SpreadsheetCellReference b = this.cellReference(0, 5);
        final SpreadsheetCellReference c = this.cellReference(0, 10);
        final SpreadsheetCellReference d = this.cellReference(0, 15);
        final SpreadsheetCellReference e = this.cellReference(0, 20);

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, c.spreadsheetRange(d)));

        engine.saveCell(this.cell(a, "=1+" + LABEL), context);
        engine.saveCell(this.cell(c, "=99+0"), context);

        this.insertRowsAndCheck(engine,
                b.row(),
                c.row().value() - b.row().value(),
                context,
                this.formattedCellWithValue("$A$16", "=99+0", number(99 + 0))); // $b insert

        this.countAndCheck(labelStore, 1);
        this.loadLabelAndCheck(labelStore, LABEL, d.spreadsheetRange(e));

        this.countAndCheck(cellStore, 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+" + LABEL,
                number(1 + 99));

        this.loadCellAndCheckFormulaAndValue(engine,
                d,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=99+0",
                number(99 + 0));
    }

    @Test
    public void testInsertRowsWithCellReferences() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference("$A$1"); // A1
        final SpreadsheetCellReference b = this.cellReference("$A$2"); // A2
        final SpreadsheetCellReference c = this.cellReference("$A$11"); // moved
        final SpreadsheetCellReference d = this.cellReference("$I$14"); // moved

        engine.saveCell(this.cell(a, "=1+0+" + d), context);
        engine.saveCell(this.cell(b, "=2+0"), context);
        engine.saveCell(this.cell(c, "=3+0"), context);
        engine.saveCell(this.cell(d, "=4+0+" + b), context);

        final int count = 1;
        this.insertRowsAndCheck(engine,
                c.row(),
                count,
                context,
                this.formattedCellWithValue("$A$1", "=1+0+$I$15", number(3 + 4)),
                this.formattedCellWithValue("$A$12", "=3+0", number(3 + 0)),
                this.formattedCellWithValue("$I$15", "=4+0+" + b, number(4 + 0 + 2 + 0))); // $c insert

        this.countAndCheck(context.storeRepository().cells(), 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0+" + d.addRow(+count),
                number(1 + 0 + 4 + 2)); // reference should have been fixed.

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=2+0",
                number(2));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.addRow(+count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=3+0",
                number(3));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.addRow(+count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=4+0+" + b,
                number(4 + 2));
    }

    @Test
    public void testInsertRowsWithCellReferences2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference("$A$1"); // A1
        final SpreadsheetCellReference b = this.cellReference("$A$2"); // A2
        final SpreadsheetCellReference c = this.cellReference("$A$11"); // moved
        final SpreadsheetCellReference d = this.cellReference("$I$14"); // moved

        engine.saveCell(this.cell(a, "=1+0+" + d), context);
        engine.saveCell(this.cell(b, "=2+0"), context);
        engine.saveCell(this.cell(c, "=3+0"), context);
        engine.saveCell(this.cell(d, "=4+0+" + b), context); // =5+2

        final int count = 2;
        this.insertRowsAndCheck(engine,
                c.row(),
                count,
                context,
                this.formattedCellWithValue("$A$1", "=1+0+$I$16", number(1 + 0 + 4 + 0 + 2 + 0)),
                this.formattedCellWithValue("$A$13", "=3+0", number(3 + 0)),
                this.formattedCellWithValue("$I$16", "=4+0+" + b, number(4 + 0 + 2 + 0))); // $c insert

        this.countAndCheck(context.storeRepository().cells(), 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0+" + d.addRow(+count),
                number(1 + 0 + 4 + 2)); // reference should have been fixed.

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=2+0",
                number(2 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.addRow(+count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=3+0",
                number(3 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.addRow(+count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=4+0+" + b,
                number(4 + 0 + 2));
    }

    @Test
    public void testInsertRowsSeveral() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference("$A$1"); // A1
        final SpreadsheetCellReference b = this.cellReference("$A$11"); // MOVED
        final SpreadsheetCellReference c = this.cellReference("$A$12"); // MOVED
        final SpreadsheetCellReference d = this.cellReference("$C$13"); // MOVED
        final SpreadsheetCellReference e = this.cellReference("$D$21"); // MOVED

        engine.saveCell(this.cell(a, "=1+0"), context);
        engine.saveCell(this.cell(b, "=2+0"), context);
        engine.saveCell(this.cell(c, "=3+0"), context);
        engine.saveCell(this.cell(d, "=4+0"), context);
        engine.saveCell(this.cell(e, "=5+0"), context);

        final int count = 5;
        this.insertRowsAndCheck(engine,
                this.row(7),
                count,
                context,
                this.formattedCellWithValue("$A$16", "=2+0", number(2 + 0)),
                this.formattedCellWithValue("$A$17", "=3+0", number(3 + 0)),
                this.formattedCellWithValue("$C$18", "=4+0", number(4 + 0)),
                this.formattedCellWithValue("$D$26", "=5+0", number(5 + 0))); // $b & $c

        this.countAndCheck(context.storeRepository().cells(), 5);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=1+0",
                number(1 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.addRow(+count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=2+0",
                number(2 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.addRow(+count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=3+0",
                number(3 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.addRow(+count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=4+0",
                number(4 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                e.addRow(+count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "=5+0",
                number(5 + 0));
    }

    // loadCells........................................................................................................

    @Test
    public void testLoadCellsNothing() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        this.loadCellsAndCheck(
                engine,
                "A1:B2",
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context
        );
    }

    @Test
    public void testLoadCellsNothingWithLabels() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelStore labelStore = context.storeRepository()
                .labels();

        final SpreadsheetCellReference b2 = SpreadsheetCellReference.parseCellReference("C3");
        final SpreadsheetLabelName label = SpreadsheetLabelName.labelName("LabelC3");

        labelStore.save(label.mapping(b2));

        final SpreadsheetRange range = SpreadsheetRange.parseRange("B2:C3");

        this.loadCellsAndCheck(
                engine,
                range,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                SpreadsheetDelta.with(SpreadsheetDelta.NO_CELLS)
                        .setWindow(Lists.of(range))
                        .setLabels(
                                Sets.of(
                                        label.mapping(b2)
                                )
                        )
        );
    }

    @Test
    public void testLoadCells() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCell b2 = this.cell("b2", "=2");
        cellStore.save(b2);

        final SpreadsheetCell c3 = this.cell("c3", "=3");
        cellStore.save(c3);

        this.loadCellsAndCheck(
                engine,
                "b2:c3",
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                this.formattedCellWithValue(b2, this.expressionNumberKind().create(2)),
                this.formattedCellWithValue(c3, this.expressionNumberKind().create(3))
        );
    }

    @Test
    public void testLoadCellsWithReferences() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCell b2 = this.cell("b2", "=c3*2");
        cellStore.save(b2);

        final SpreadsheetCell c3 = this.cell("c3", "=2");
        cellStore.save(c3);

        this.loadCellsAndCheck(
                engine,
                "b2:c3",
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                this.formattedCellWithValue(b2, this.expressionNumberKind().create(4)),
                this.formattedCellWithValue(c3, this.expressionNumberKind().create(2))
        );
    }

    @Test
    public void testLoadCellsWithLabels() {
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

        labelStore.save(label.mapping(d4.reference()));

        final SpreadsheetRange range = SpreadsheetRange.parseRange("c3:d4");

        this.loadCellsAndCheck(
                engine,
                range,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                SpreadsheetDelta.with(Sets.of(
                        this.formattedCellWithValue(c3, this.expressionNumberKind().create(1)),
                        this.formattedCellWithValue(d4, this.expressionNumberKind().create(2))
                ))
                        .setWindow(Lists.of(range))
                        .setLabels(
                                Sets.of(
                                        label.mapping(d4.reference())
                                )
                        )
        );
    }

    @Test
    public void testLoadCellsWithLabelsLabelWithoutCell() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCell c3 = this.cell("c3", "=1");
        cellStore.save(c3);

        final SpreadsheetLabelStore labelStore = context.storeRepository()
                .labels();

        final SpreadsheetLabelName label = SpreadsheetLabelName.labelName("LabelD4");
        final SpreadsheetCellReference d4 = SpreadsheetCellReference.parseCellReference("d4");
        labelStore.save(label.mapping(d4));

        final SpreadsheetRange range = SpreadsheetRange.parseRange("c3:d4");

        this.loadCellsAndCheck(
                engine,
                range,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                SpreadsheetDelta.with(Sets.of(
                        this.formattedCellWithValue(c3, this.expressionNumberKind().create(1))
                ))
                        .setWindow(Lists.of(range))
                        .setLabels(
                                Sets.of(
                                        label.mapping(d4)
                                )
                        )
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

        final SpreadsheetCellReference a = this.cellReference(5, 5);
        final SpreadsheetCell cellA = this.cell(a, "=1+0");

        cellStore.save(cellA);

        final SpreadsheetRange rangeA = a.spreadsheetRange(a);

        this.fillCellsAndCheck(engine,
                SpreadsheetDelta.NO_CELLS,
                rangeA,
                rangeA,
                context);

        this.countAndCheck(cellStore, 0); // a deleted
    }

    @Test
    public void testFillCellsDeleteOneCell2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference a = this.cellReference(5, 5);
        final SpreadsheetCell cellA = this.cell(a, "=1+0");
        cellStore.save(cellA);

        final SpreadsheetCellReference b = this.cellReference(10, 10);
        final SpreadsheetCell cellB = this.cell(b, "=2+0");
        cellStore.save(cellB);

        final SpreadsheetRange rangeA = a.spreadsheetRange(a);

        this.fillCellsAndCheck(engine,
                SpreadsheetDelta.NO_CELLS,
                rangeA,
                rangeA,
                context);

        this.countAndCheck(cellStore, 1); // a deleted

        this.loadCellAndCheck(engine,
                b,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                this.formattedCellWithValue(b, "=2+0", number(2)));
    }

    @Test
    public void testFillCellsDeletesManyCells() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference a = this.cellReference(5, 5);
        final SpreadsheetCell cellA = this.cell(a, "=1+0");
        cellStore.save(cellA);

        final SpreadsheetCellReference b = this.cellReference(6, 6);
        final SpreadsheetCell cellB = this.cell(b, "=2+0");
        cellStore.save(cellB);

        final SpreadsheetRange rangeA = a.spreadsheetRange(b);

        this.fillCellsAndCheck(engine,
                SpreadsheetDelta.NO_CELLS,
                rangeA,
                rangeA,
                context);

        this.countAndCheck(cellStore, 0); // a deleted
    }

    @Test
    public void testFillCellsDeletesManyCells2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference a = this.cellReference(5, 5);
        final SpreadsheetCell cellA = this.cell(a, "=1+0");
        cellStore.save(cellA);

        final SpreadsheetCellReference b = this.cellReference(6, 6);
        final SpreadsheetCell cellB = this.cell(b, "=2+0");
        cellStore.save(cellB);

        final SpreadsheetRange rangeA = a.spreadsheetRange(b);

        final SpreadsheetCellReference c = this.cellReference(10, 10);
        final SpreadsheetCell cellC = this.cell(c, "=3+0");
        cellStore.save(cellC);

        this.fillCellsAndCheck(engine,
                SpreadsheetDelta.NO_CELLS,
                rangeA,
                rangeA,
                context);

        this.countAndCheck(cellStore, 1); // a&b deleted, leaving c

        this.loadCellAndCheck(engine,
                c,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                this.formattedCellWithValue(c, "=3+0", number(3)));
    }

    // fill save with missing cells......................................................................................

    @Test
    public void testFillCellsSaveWithMissingCells() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference(1, 1);
        final SpreadsheetCellReference b = this.cellReference(2, 2);

        final SpreadsheetCell cellA = this.cell(a, "=1+0");
        final SpreadsheetCell cellB = this.cell(b, "=2+0");

        final SpreadsheetRange range = a.spreadsheetRange(b);

        this.fillCellsAndCheck(engine,
                Sets.of(cellA, cellB),
                range,
                range,
                context,
                this.formattedCellWithValue(a, "=1+0", number(1)),
                this.formattedCellWithValue(b, "=2+0", number(2)));

        this.countAndCheck(context.storeRepository().cells(), 2); // a + b saved

        this.loadCellAndCheck(engine,
                a,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                this.formattedCellWithValue(a, "=1+0", number(1)));

        this.loadCellAndCheck(engine,
                b,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                this.formattedCellWithValue(b, "=2+0", number(2)));
    }

    @Test
    public void testFillCellsSaveWithMissingCells2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference a = this.cellReference(1, 1);
        final SpreadsheetCellReference b = this.cellReference(2, 2);

        final SpreadsheetCell cellA = this.cell(a, "=1+0");
        final SpreadsheetCell cellB = this.cell(b, "=2+0");

        final SpreadsheetRange range = a.spreadsheetRange(b);

        final SpreadsheetCellReference c = this.cellReference(10, 10);
        final SpreadsheetCell cellC = this.cell(c, "=3+0");
        cellStore.save(cellC);

        this.fillCellsAndCheck(engine,
                Sets.of(cellA, cellB),
                range,
                range,
                context,
                this.formattedCellWithValue(a, "=1+0", number(1)),
                this.formattedCellWithValue(b, "=2+0", number(2)));

        this.countAndCheck(cellStore, 3); // a + b saved + c

        this.loadCellAndCheck(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                this.formattedCellWithValue(a, "=1+0", number(1))); // fill should have evaluated.

        this.loadCellAndCheck(engine,
                b,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                this.formattedCellWithValue(b, "=2+0", number(2)));

        this.loadCellAndCheck(engine,
                c,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                cellC);
    }

    // fill moves cell..................................................................................................

    @Test
    public void testFillCellsRangeOneEmptyCells2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference a = this.cellReference(1, 1);
        final SpreadsheetCellReference b = this.cellReference(2, 1);
        final SpreadsheetCellReference c = this.cellReference(1, 2);

        final SpreadsheetCell cellA = this.cell(a, "=1+0");
        final SpreadsheetCell cellB = this.cell(b, "=2+0");
        final SpreadsheetCell cellC = this.cell(c, "=3+0");

        cellStore.save(cellA);
        cellStore.save(cellB);
        cellStore.save(cellC);

        this.fillCellsAndCheck(engine,
                SpreadsheetDelta.NO_CELLS,
                a.spreadsheetRange(a),
                SpreadsheetRange.fromCells(Lists.of(b)),
                context);

        this.countAndCheck(cellStore, 2); // a + c, b deleted

        this.loadCellAndCheck(engine,
                a,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                this.formattedCellWithValue(a, "=1+0", number(1)));

        this.loadCellAndCheck(engine,
                c,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                this.formattedCellWithValue(c, "=3+0", number(3)));
    }

    @Test
    public void testFillCellsRangeTwoEmptyCells() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference a = this.cellReference(1, 1);
        final SpreadsheetCellReference b = this.cellReference(2, 1);
        final SpreadsheetCellReference c = this.cellReference(1, 2);

        final SpreadsheetCell cellA = this.cell(a, "=1+0");
        final SpreadsheetCell cellB = this.cell(b, "=2+0");
        final SpreadsheetCell cellC = this.cell(c, "=3+0");

        cellStore.save(cellA);
        cellStore.save(cellB);
        cellStore.save(cellC);

        this.fillCellsAndCheck(engine,
                SpreadsheetDelta.NO_CELLS,
                a.spreadsheetRange(a),
                SpreadsheetRange.fromCells(Lists.of(a, b)),
                context);

        this.countAndCheck(cellStore, 1);

        this.loadCellAndCheck(engine,
                c,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                this.formattedCellWithValue(c, "=3+0", number(3)));
    }

    // fill moves 1 cell................................................................................................

    @Test
    public void testFillCellsAddition() {
        this.fillCellsAndCheck("=1+0", number(1 + 0));
    }

    @Test
    public void testFillCellsExpressionNumber() {
        this.fillCellsAndCheck("=99.5", number(99.5));
    }

    @Test
    public void testFillCellsExpressionNumber2() {
        this.fillCellsAndCheck("=99", number(99));
    }

    @Test
    public void testFillCellsDivision() {
        this.fillCellsAndCheck("=10/5", number(10 / 5));
    }

    @Test
    public void testFillCellsEqualsTrue() {
        this.fillCellsAndCheck("=10==10", true);
    }

    @Test
    public void testFillCellsEqualsFalse() {
        this.fillCellsAndCheck("=10==9", false);
    }

    @Test
    public void testFillCellsGreaterThanTrue() {
        this.fillCellsAndCheck("=10>9", true);
    }

    @Test
    public void testFillCellsGreaterThanFalse() {
        this.fillCellsAndCheck("=10>11", false);
    }

    @Test
    public void testFillCellsGreaterThanEqualsTrue() {
        this.fillCellsAndCheck("=10>=10", true);
    }

    @Test
    public void testFillCellsGreaterThanEqualsFalse() {
        this.fillCellsAndCheck("=10>=11", false);
    }

    @Test
    public void testFillCellsFunction() {
        this.fillCellsAndCheck("=BasicSpreadsheetEngineTestSum(1;99)", number(1 + 99));
    }

    @Test
    public void testFillCellsGroup() {
        this.fillCellsAndCheck("=(99)", number(99));
    }

    @Test
    public void testFillCellsLessThanTrue() {
        this.fillCellsAndCheck("=10<11", true);
    }

    @Test
    public void testFillCellsLessThanFalse() {
        this.fillCellsAndCheck("=10<9", false);
    }

    @Test
    public void testFillCellsLessThanEqualsTrue() {
        this.fillCellsAndCheck("=10<=10", true);
    }

    @Test
    public void testFillCellsLessThanEqualsFalse() {
        this.fillCellsAndCheck("=10<=9", false);
    }

    @Test
    public void testFillCellsMultiplication() {
        this.fillCellsAndCheck("=6*7", number(6 * 7));
    }

    @Test
    public void testFillCellsNegative() {
        this.fillCellsAndCheck("=-123", number(-123));
    }

    @Test
    public void testFillCellsNotEqualsTrue() {
        this.fillCellsAndCheck("=10!=9", true);
    }

    @Test
    public void testFillCellsNotEqualsFalse() {
        this.fillCellsAndCheck("=10!=10", false);
    }

    @Test
    public void testFillCellsPercentage() {
        this.fillCellsAndCheck("=123.5%", number(123.5 / 100));
    }

    @Test
    public void testFillCellsSubtraction() {
        this.fillCellsAndCheck("=13-4", number(13 - 4));
    }

    @Test
    public void testFillCellsText() {
        this.fillCellsAndCheck("=\"abc123\"", "abc123");
    }

    @Test
    public void testFillCellsAdditionWithWhitespace() {
        this.fillCellsAndCheck("=1 + 2", number(1 + 2));
    }

    private void fillCellsAndCheck(final String formulaText, final Object expected) {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference a = this.cellReference(10, 20);
        final SpreadsheetCellReference b = this.cellReference(11, 21);
        final SpreadsheetCellReference c = this.cellReference(12, 22);

        final SpreadsheetCell cellA = this.cell(a, formulaText);
        final SpreadsheetCell cellB = this.cell(b, "=2+0");
        final SpreadsheetCell cellC = this.cell(c, "=3+0");

        cellStore.save(cellA);
        cellStore.save(cellB);
        cellStore.save(cellC);

        final SpreadsheetCellReference d = this.cellReference(30, 40);

        this.fillCellsAndCheck(engine,
                Lists.of(cellA),
                a.spreadsheetRange(a),
                d.spreadsheetRange(d),
                context,
                this.formattedCellWithValue(d, formulaText, expected));

        this.countAndCheck(cellStore, 3 + 1);
    }

    @Test
    public void testFillCellsRepeatCellInto2x2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference a = this.cellReference(10, 20);
        final SpreadsheetCellReference b = this.cellReference(11, 21);
        final SpreadsheetCellReference c = this.cellReference(12, 22);

        final SpreadsheetCell cellA = this.cell(a, "=1+0");
        final SpreadsheetCell cellB = this.cell(b, "=2+0");
        final SpreadsheetCell cellC = this.cell(c, "=3+0");

        cellStore.save(cellA);
        cellStore.save(cellB);
        cellStore.save(cellC);

        final SpreadsheetCellReference d = this.cellReference(30, 40);

        this.fillCellsAndCheck(engine,
                Lists.of(cellA, cellB),
                SpreadsheetRange.fromCells(Lists.of(a, b)),
                d.spreadsheetRange(d.add(2, 2)),
                context,
                this.formattedCellWithValue(d, "=1+0", number(1 + 0)),
                this.formattedCellWithValue(d.add(1, 1), "=2+0", number(2 + 0)));

        this.countAndCheck(cellStore, 3 + 2);
    }

    @Test
    public void testFillCells2x2CellInto1x1() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference a = this.cellReference(10, 20);
        final SpreadsheetCellReference b = this.cellReference(11, 21);
        final SpreadsheetCellReference c = this.cellReference(12, 22);

        final SpreadsheetCell cellA = this.cell(a, "=1+0");
        final SpreadsheetCell cellB = this.cell(b, "=2+0");
        final SpreadsheetCell cellC = this.cell(c, "=3+0");

        cellStore.save(cellA);
        cellStore.save(cellB);
        cellStore.save(cellC);

        final SpreadsheetCellReference d = this.cellReference(30, 40);

        this.fillCellsAndCheck(engine,
                Lists.of(cellA, cellB),
                SpreadsheetRange.fromCells(Lists.of(a, b)),
                d.spreadsheetRange(d.add(1, 1)),
                context,
                this.formattedCellWithValue(d, "=1+0", number(1 + 0)),
                this.formattedCellWithValue(d.add(1, 1), "=2+0", number(2 + 0)));

        this.countAndCheck(cellStore, 3 + 2);
    }

    @Test
    public void testFillCells2x2Into2x2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference a = this.cellReference(10, 20);
        final SpreadsheetCellReference b = this.cellReference(11, 21);
        final SpreadsheetCellReference c = this.cellReference(12, 22);

        final SpreadsheetCell cellA = this.cell(a, "=1+0");
        final SpreadsheetCell cellB = this.cell(b, "=2+0");
        final SpreadsheetCell cellC = this.cell(c, "=3+0");

        cellStore.save(cellA);
        cellStore.save(cellB);
        cellStore.save(cellC);

        final SpreadsheetCellReference d = this.cellReference(30, 40);

        this.fillCellsAndCheck(
                engine,
                Lists.of(cellA, cellB),
                SpreadsheetRange.fromCells(Lists.of(a, b)),
                d.spreadsheetRange(d.add(2, 2)),
                context,
                this.formattedCellWithValue(d, "=1+0", number(1 + 0)),
                this.formattedCellWithValue(d.add(1, 1), "=2+0", number(2 + 0)));

        this.countAndCheck(cellStore, 3 + 2);
    }

    @Test
    public void testFillCells2x2Into7x2Gives6x2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference a = this.cellReference(10, 20);
        final SpreadsheetCellReference b = this.cellReference(11, 21);
        final SpreadsheetCellReference c = this.cellReference(12, 22);

        final SpreadsheetCell cellA = this.cell(a, "=1+0");
        final SpreadsheetCell cellB = this.cell(b, "=2+0");
        final SpreadsheetCell cellC = this.cell(c, "=3+0");

        cellStore.save(cellA);
        cellStore.save(cellB);
        cellStore.save(cellC);

        final SpreadsheetCellReference d = this.cellReference(30, 40);

        this.fillCellsAndCheck(engine,
                Lists.of(cellA, cellB),
                SpreadsheetRange.fromCells(Lists.of(a, b)),
                d.spreadsheetRange(d.add(6, 1)),
                context,
                this.formattedCellWithValue(d, "=1+0", number(1 + 0)),
                this.formattedCellWithValue(d.add(1, 1), "=2+0", number(2 + 0)),
                this.formattedCellWithValue(d.add(2, 0), "=1+0", number(1 + 0)),
                this.formattedCellWithValue(d.add(3, 1), "=2+0", number(2 + 0)),
                this.formattedCellWithValue(d.add(4, 0), "=1+0", number(1 + 0)),
                this.formattedCellWithValue(d.add(5, 1), "=2+0", number(2 + 0)));

        this.countAndCheck(cellStore, 3 + 6);
    }

    @Test
    public void testFillCells2x2Into2x7Gives2x6() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference a = this.cellReference(10, 20);
        final SpreadsheetCellReference b = this.cellReference(11, 21);
        final SpreadsheetCellReference c = this.cellReference(12, 22);

        final SpreadsheetCell cellA = this.cell(a, "=1+0");
        final SpreadsheetCell cellB = this.cell(b, "=2+0");
        final SpreadsheetCell cellC = this.cell(c, "=3+0");

        cellStore.save(cellA);
        cellStore.save(cellB);
        cellStore.save(cellC);

        final SpreadsheetCellReference d = this.cellReference(30, 40);

        this.fillCellsAndCheck(engine,
                Lists.of(cellA, cellB),
                SpreadsheetRange.fromCells(Lists.of(a, b)),
                d.spreadsheetRange(d.add(1, 6)),
                context,
                this.formattedCellWithValue(d, "=1+0", number(1 + 0)),
                this.formattedCellWithValue(d.add(1, 1), "=2+0", number(2 + 0)),
                this.formattedCellWithValue(d.addRow(2), "=1+0", number(1 + 0)),
                this.formattedCellWithValue(d.add(1, 3), "=2+0", number(2 + 0)),
                this.formattedCellWithValue(d.addRow(4), "=1+0", number(1 + 0)),
                this.formattedCellWithValue(d.add(1, 5), "=2+0", number(2 + 0)));

        this.countAndCheck(cellStore, 3 + 6);
    }

    @Test
    public void testFillCellsAbsoluteReference() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference a = this.cellReference(10, 20);
        final SpreadsheetCellReference b = this.cellReference(11, 21);

        final SpreadsheetCell cellA = this.cell(a, "=1+0");
        final SpreadsheetCell cellB = this.cell(b, "=" + a);

        cellStore.save(cellA);
        cellStore.save(cellB);

        final SpreadsheetCellReference d = this.cellReference(30, 40);

        this.fillCellsAndCheck(engine,
                Lists.of(cellB),
                b.spreadsheetRange(b),
                d.spreadsheetRange(d),
                context,
                this.formattedCellWithValue(a, "=1+0", number(1 + 0)),
                this.formattedCellWithValue(d, "=" + a, number(1 + 0)));

        this.countAndCheck(cellStore, 2 + 1);
    }

    @Test
    public void testFillCellsExpressionRelativeReferenceFixed() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCell cellB = this.cell("B2", "=2");
        final SpreadsheetCell cellC = this.cell("C3", "=3+B2");

        cellStore.save(cellB);
        cellStore.save(cellC);

        this.fillCellsAndCheck(engine,
                Lists.of(cellB, cellC),
                cellB.reference().spreadsheetRange(cellC.reference()),
                SpreadsheetExpressionReference.parseRange("E5:F6"),
                context,
                this.formattedCellWithValue("E5", "=2", number(2 + 0)),
                this.formattedCellWithValue("F6", "=3+E5", number(3 + 2)));

        this.countAndCheck(cellStore, 2 + 2);
    }

    @Test
    public void testFillCellsExternalReferencesRefreshed() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetCellReference b = this.cellReference("b1");
        final SpreadsheetCell cellB = this.cell(b, "=2+0"); // copied to C1
        final SpreadsheetCellReference c = this.cellReference("C1"); // fillCells dest...
        final SpreadsheetCell cellA = this.cell("a1", "=10+" + c);

        engine.saveCell(cellA, context);
        engine.saveCell(cellB, context);

        this.fillCellsAndCheck(engine,
                Lists.of(cellB),
                b.spreadsheetRange(b),
                c.spreadsheetRange(c),
                context,
                this.formattedCellWithValue(cellA.reference(), "=10+" + c, number(10 + 2 + 0)), // external reference to copied
                this.formattedCellWithValue(c, "=2+0", number(2 + 0))); // copied

        this.countAndCheck(cellStore, 2 + 1);
    }

    //  loadLabel.......................................................................................................

    @Test
    public void testLoadLabelUnknownFails() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetLabelName label = SpreadsheetExpressionReference.labelName("LABEL123");
        final SpreadsheetLabelMapping mapping = SpreadsheetLabelMapping.with(label, SpreadsheetExpressionReference.parseCellReference("B2"));

        this.saveLabelAndCheck(engine,
                mapping,
                context);

        this.loadLabelAndFailCheck(
                engine,
                SpreadsheetExpressionReference.labelName("UnknownLabel"),
                this.createContext()
        );
    }

    //  saveLabel.......................................................................................................

    @Test
    public void testSaveLabelAndLoadFromLabelStore() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelName label = SpreadsheetExpressionReference.labelName("LABEL123");
        final SpreadsheetLabelMapping mapping = SpreadsheetLabelMapping.with(label, SpreadsheetExpressionReference.parseCellReference("B2"));

        this.saveLabelAndCheck(engine,
                mapping,
                context);

        this.loadLabelAndCheck(context.storeRepository().labels(),
                label,
                mapping);
    }

    @Test
    public void testSaveLabelAndLoadLabel() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelName label = SpreadsheetExpressionReference.labelName("LABEL123");
        final SpreadsheetLabelMapping mapping = SpreadsheetLabelMapping.with(label, SpreadsheetExpressionReference.parseCellReference("B2"));

        this.saveLabelAndCheck(engine,
                mapping,
                context);

        this.loadLabelAndCheck(engine,
                label,
                context,
                mapping);
    }

    @Test
    public void testSaveLabelWithoutReferences() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetLabelName label = SpreadsheetExpressionReference.labelName("LABEL123");
        final SpreadsheetLabelMapping mapping = SpreadsheetLabelMapping.with(label, SpreadsheetExpressionReference.parseCellReference("B2"));

        engine.saveCell(this.cell("B2", "=99"), context);

        this.saveLabelAndCheck(engine,
                mapping,
                context);

        engine.saveCell(this.cell("A1", label + "+1"), context);
    }

    @Test
    public void testSaveLabelRefreshesReferences() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelName label = SpreadsheetExpressionReference.labelName("LABEL123");
        final SpreadsheetLabelMapping mapping = SpreadsheetLabelMapping.with(label, SpreadsheetExpressionReference.parseCellReference("B2"));

        engine.saveCell(this.cell("A1", "=" + label + "+1"), context);
        engine.saveCell(this.cell("B2", "=99"), context);

        this.saveLabelAndCheck(engine,
                mapping,
                context,
                this.formattedCellWithValue("A1", "=" + label + "+1", number(99 + 1)));
    }

    //  removeLabel.......................................................................................................

    @Test
    public void testRemoveLabelAndLoadFromLabelStore() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelName label = SpreadsheetExpressionReference.labelName("LABEL123");
        final SpreadsheetLabelMapping mapping = SpreadsheetLabelMapping.with(label, SpreadsheetExpressionReference.parseCellReference("B2"));

        this.saveLabelAndCheck(engine,
                mapping,
                context);

        this.removeLabelAndCheck(engine,
                label,
                context);

        this.loadLabelFailCheck(context.storeRepository().labels(), label);
    }

    @Test
    public void testRemoveLabelRefreshesCell() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetLabelName label = SpreadsheetExpressionReference.labelName("LABEL123");
        final SpreadsheetLabelMapping mapping = SpreadsheetLabelMapping.with(label, SpreadsheetExpressionReference.parseCellReference("B2"));

        engine.saveCell(this.cell("A1", "=" + label + "+1"), context);
        engine.saveCell(this.cell("B2", "=99"), context);

        engine.saveLabel(mapping, context);

        this.removeLabelAndCheck(engine,
                label,
                context,
                this.formattedCellWithError("A1", "=" + label + "+1", "Unknown label: " + label));

        this.loadLabelFailCheck(context.storeRepository().labels(), label);
    }

    // columnWidth, rowHeight...........................................................................................

    @Test
    public void testColumnWidth() {
        final SpreadsheetColumnReference column = SpreadsheetColumnReference.parseColumn("Z");
        final double expected = 150.5;

        this.columnWidthAndCheck2(
                column,
                this.metadata(),
                expected,
                expected);
    }

    @Test
    public void testColumnWidthDefaults() {
        final SpreadsheetColumnReference column = SpreadsheetColumnReference.parseColumn("Z");
        final double expected = 150.5;

        SpreadsheetMetadata metadata = SpreadsheetMetadata.NON_LOCALE_DEFAULTS;
        final TextStyle style = metadata.getOrFail(SpreadsheetMetadataPropertyName.STYLE)
                .set(TextStylePropertyName.WIDTH, Length.pixel(expected));
        metadata = metadata.set(SpreadsheetMetadataPropertyName.STYLE, style);

        this.columnWidthAndCheck2(
                column,
                metadata,
                0,
                expected);
    }

    private void columnWidthAndCheck2(final SpreadsheetColumnReference column,
                                      final SpreadsheetMetadata metadata,
                                      final double maxColumnWidth,
                                      final double expected) {
        this.columnWidthAndCheck(
                BasicSpreadsheetEngine.with(metadata),
                column,
                this.createContext(new FakeSpreadsheetCellStore() {
                    @Override
                    public double maxColumnWidth(final SpreadsheetColumnReference c) {
                        assertEquals(column, c);
                        return maxColumnWidth;
                    }
                }),
                expected
        );
    }

    @Test
    public void testColumnWidthDefaultMissing() {
        final SpreadsheetColumnReference column = SpreadsheetColumnReference.parseColumn("Z");
        assertThrows(
                TextStylePropertyValueException.class,
                () -> BasicSpreadsheetEngine.with(SpreadsheetMetadata.EMPTY)
                        .columnWidth(
                                column,
                                this.createContext(new FakeSpreadsheetCellStore() {
                                    @Override
                                    public double maxColumnWidth(final SpreadsheetColumnReference c) {
                                        assertEquals(column, c);
                                        return 0;
                                    }
                                })));
    }

    // rowHeight........................................................................................................

    @Test
    public void testRowHeight() {
        this.rowHeightAndCheck2(
                SpreadsheetRowReference.parseRow("987"),
                SpreadsheetMetadata.EMPTY,
                150.5
        );
    }

    @Test
    public void testRowHeightDefaults() {
        final SpreadsheetRowReference row = SpreadsheetRowReference.parseRow("987");
        final double expected = 150.5;

        final SpreadsheetMetadata metadata = SpreadsheetMetadata.NON_LOCALE_DEFAULTS;
        final TextStyle style = metadata.getOrFail(SpreadsheetMetadataPropertyName.STYLE)
                .set(TextStylePropertyName.HEIGHT, Length.pixel(expected));

        this.rowHeightAndCheck2(
                row,
                metadata.set(SpreadsheetMetadataPropertyName.STYLE, style),
                expected
        );
    }

    private void rowHeightAndCheck2(final SpreadsheetRowReference row,
                                    final SpreadsheetMetadata metadata,
                                    final double expected) {
        this.rowHeightAndCheck(
                BasicSpreadsheetEngine.with(metadata),
                row,
                this.createContext(new FakeSpreadsheetCellStore() {
                    @Override
                    public double maxRowHeight(final SpreadsheetRowReference c) {
                        assertEquals(row, c);
                        return expected;
                    }
                }),
                expected
        );
    }

    @Test
    public void testRowHeightDefaultMissing() {
        final SpreadsheetRowReference row = SpreadsheetRowReference.parseRow("999");
        assertThrows(TextStylePropertyValueException.class,
                () -> BasicSpreadsheetEngine.with(SpreadsheetMetadata.EMPTY)
                        .rowHeight(
                                row,
                                this.createContext(new FakeSpreadsheetCellStore() {

                                    @Override
                                    public double maxRowHeight(final SpreadsheetRowReference r) {
                                        assertEquals(row, r);
                                        return 0;
                                    }
                                })));
    }

    // widths top left .................................................................................................

    @Test
    public void testRangeLeft() {
        this.rangeAndCheck(
                "A1", 0, 0, WIDTH, HEIGHT,
                "A1"
        );
    }

    @Test
    public void testRangeLeft2() {
        this.rangeAndCheck(
                "A1", 0, 0, 3 * WIDTH, HEIGHT,
                "A1:C1"
        );
    }

    @Test
    public void testRangeLeft3() {
        this.rangeAndCheck(
                "A1", 0, 0, WIDTH - 1, HEIGHT,
                "A1"
        );
    }

    @Test
    public void testRangeLeft4() {
        this.rangeAndCheck(
                "A1", 0, 0, WIDTH + 1, HEIGHT,
                "A1:B1"
        );
    }

    @Test
    public void testRangeLeft5() {
        this.rangeAndCheck(
                "A1", 0, 0, WIDTH * 4 - 1, HEIGHT,
                "A1:D1"
        );
    }

    @Test
    public void testRangeLeft6() {
        this.rangeAndCheck(
                "A1", 0, 0, WIDTH * 4 + 1, HEIGHT,
                "A1:E1"
        );
    }

    @Test
    public void testRangeLeftNegativeXOffset() {
        this.rangeAndCheck(
                "A1", -1, 0, WIDTH, HEIGHT,
                "A1"
        );
    }

    @Test
    public void testRangeLeftNegativeXOffset2() {
        this.rangeAndCheck(
                "A1", -1, 0, 3 * WIDTH, HEIGHT,
                "A1:C1"
        );
    }

    @Test
    public void testRangeLeftNegativeXOffset3() {
        this.rangeAndCheck(
                "A1", -1, 0, WIDTH - 1, HEIGHT,
                "A1"
        );
    }

    @Test
    public void testRangeLeftNegativeXOffset4() {
        this.rangeAndCheck(
                "A1", -1, 0, WIDTH + 1, HEIGHT,
                "A1:B1"
        );
    }

    @Test
    public void testRangeLeftNegativeXOffset5() {
        this.rangeAndCheck(
                "A1", -1, 0, WIDTH * 4 - 1, HEIGHT,
                "A1:D1"
        );
    }

    @Test
    public void testRangeLeftNegativeXOffset6() {
        this.rangeAndCheck(
                "A1", -1, 0, WIDTH * 4 + 1, HEIGHT,
                "A1:E1"
        );
    }

    @Test
    public void testRangeLeftPositiveXOffset() {
        this.rangeAndCheck(
                "A1", 1, 0, WIDTH, HEIGHT,
                "A1:B1"
        );
    }

    @Test
    public void testRangeLeftPositiveXOffset2() {
        this.rangeAndCheck(
                "A1", 1, 0, 3 * WIDTH, HEIGHT,
                "A1:D1"
        );
    }

    @Test
    public void testRangeLeftPositiveXOffset3() {
        this.rangeAndCheck(
                "A1", 1, 0, WIDTH - 1, HEIGHT,
                "A1"
        );
    }

    @Test
    public void testRangeLeftPositiveXOffset4() {
        this.rangeAndCheck(
                "A1", 1, 0, WIDTH + 1, HEIGHT,
                "A1:B1"
        );
    }

    @Test
    public void testRangeLeftPositiveXOffset5() {
        this.rangeAndCheck(
                "A1", 1, 0, WIDTH * 4 - 1, HEIGHT,
                "A1:D1"
        );
    }

    @Test
    public void testRangeLeftPositiveXOffset6() {
        this.rangeAndCheck(
                "A1", 1, 0, WIDTH * 4 + 1, HEIGHT,
                "A1:E1"
        );
    }

    @Test
    public void testRangeMidX() {
        this.rangeAndCheck(
                "M1", 0, 0, WIDTH, HEIGHT,
                "M1"
        );
    }

    @Test
    public void testRangeMidX2() {
        this.rangeAndCheck(
                "M1", 0, 0, 3 * WIDTH, HEIGHT,
                "M1:O1"
        );
    }

    @Test
    public void testRangeMidX3() {
        this.rangeAndCheck(
                "M1", 0, 0, WIDTH - 1, HEIGHT,
                "M1"
        );
    }

    @Test
    public void testRangeMidX4() {
        this.rangeAndCheck(
                "M1", 0, 0, WIDTH + 1, HEIGHT,
                "M1:N1"
        );
    }

    @Test
    public void testRangeMidX5() {
        this.rangeAndCheck(
                "M1", 0, 0, WIDTH * 4 - 1, HEIGHT,
                "M1:P1"
        );
    }

    @Test
    public void testRangeMidX6() {
        this.rangeAndCheck(
                "M1", 0, 0, WIDTH * 4 + 1, HEIGHT,
                "M1:Q1"
        );
    }

    @Test
    public void testRangeMidXNegativeXOffset() {
        this.rangeAndCheck(
                "M1", -1, 0, WIDTH, HEIGHT,
                "L1:M1"
        );
    }

    @Test
    public void testRangeMidXNegativeXOffset2() {
        this.rangeAndCheck(
                "M1", -1, 0, 3 * WIDTH, HEIGHT,
                "L1:O1"
        );
    }

    @Test
    public void testRangeMidXNegativeXOffset3() {
        this.rangeAndCheck(
                "M1", -1, 0, WIDTH - 1, HEIGHT,
                "L1:M1"
        );
    }

    @Test
    public void testRangeMidXNegativeXOffset4() {
        this.rangeAndCheck(
                "M1", -1, 0, WIDTH + 1, HEIGHT,
                "L1:M1"
        );
    }

    @Test
    public void testRangeMidXNegativeXOffset5() {
        this.rangeAndCheck(
                "M1", -1, 0, WIDTH * 4 - 1, HEIGHT,
                "L1:P1"
        );
    }

    @Test
    public void testRangeMidXNegativeXOffset6() {
        this.rangeAndCheck(
                "M1", -1, 0, WIDTH * 4 + 1, HEIGHT,
                "L1:P1"
        );
    }

    @Test
    public void testRangeMidXPositiveXOffset() {
        this.rangeAndCheck(
                "M1", 1, 0, WIDTH, HEIGHT,
                "M1:N1"
        );
    }

    @Test
    public void testRangeMidXPositiveXOffset2() {
        this.rangeAndCheck(
                "M1", 1, 0, 3 * WIDTH, HEIGHT,
                "M1:P1"
        );
    }

    @Test
    public void testRangeMidXPositiveXOffset3() {
        this.rangeAndCheck(
                "M1", 1, 0, WIDTH - 1, HEIGHT,
                "M1"
        );
    }

    @Test
    public void testRangeMidXPositiveXOffset4() {
        this.rangeAndCheck(
                "M1", 1, 0, WIDTH + 1, HEIGHT,
                "M1:N1"
        );
    }

    @Test
    public void testRangeMidXPositiveXOffset5() {
        this.rangeAndCheck(
                "M1", 1, 0, WIDTH * 4 - 1, HEIGHT,
                "M1:P1"
        );
    }

    @Test
    public void testRangeMidXPositiveXOffset6() {
        this.rangeAndCheck(
                "M1", 1, 0, WIDTH * 4 + 1, HEIGHT,
                "M1:Q1"
        );
    }

    // widths top right .................................................................................................

    @Test
    public void testRangeRight() {
        this.rangeAndCheck(
                "XFD1", 0, 0, WIDTH, HEIGHT,
                "XFD1"
        );
    }

    @Test
    public void testRangeRight2() {
        this.rangeAndCheck(
                "XFD1", 0, 0, 3 * WIDTH, HEIGHT,
                "XFB1:XFD1"
        );
    }

    @Test
    public void testRangeRight3() {
        this.rangeAndCheck(
                "XFD1", 0, 0, WIDTH - 1, HEIGHT,
                "XFD1"
        );
    }

    @Test
    public void testRangeRight4() {
        this.rangeAndCheck(
                "XFD1", 0, 0, WIDTH + 1, HEIGHT,
                "XFC1:XFD1"
        );
    }

    @Test
    public void testRangeRight5() {
        this.rangeAndCheck(
                "XFD1", 0, 0, WIDTH * 4 - 1, HEIGHT,
                "XFA1:XFD1"
        );
    }

    @Test
    public void testRangeRight6() {
        this.rangeAndCheck(
                "XFD1", 0, 0, WIDTH * 4 + 1, HEIGHT,
                "XEZ1:XFD1"
        );
    }

    @Test
    public void testRangeRightNegativeXOffset() {
        this.rangeAndCheck(
                "XFD1", -1, 0, WIDTH, HEIGHT,
                "XFC1:XFD1"
        );
    }

    @Test
    public void testRangeRightNegativeXOffset2() {
        this.rangeAndCheck(
                "XFD1", -1, 0, 3 * WIDTH, HEIGHT,
                "XFA1:XFD1"
        );
    }

    @Test
    public void testRangeRightNegativeXOffset3() {
        this.rangeAndCheck(
                "XFD1", -1, 0, WIDTH - 1, HEIGHT,
                "XFC1:XFD1"
        );
    }

    @Test
    public void testRangeRightNegativeXOffset4() {
        this.rangeAndCheck(
                "XFD1", -1, 0, WIDTH + 1, HEIGHT,
                "XFC1:XFD1"
        );
    }

    @Test
    public void testRangeRightNegativeXOffset5() {
        this.rangeAndCheck(
                "XFD1", -1, 0, WIDTH * 4 - 1, HEIGHT,
                "XEZ1:XFD1"
        );
    }

    @Test
    public void testRangeRightNegativeXOffset6() {
        this.rangeAndCheck(
                "XFD1", -1, 0, WIDTH * 4 + 1, HEIGHT,
                "XEZ1:XFD1"
        );
    }

    @Test
    public void testRangeRightPositiveXOffset() {
        this.rangeAndCheck(
                "XFD1", 1, 0, WIDTH, HEIGHT,
                "XFD1"
        );
    }

    @Test
    public void testRangeRightPositiveXOffset2() {
        this.rangeAndCheck(
                "XFD1", 1, 0, 3 * WIDTH, HEIGHT,
                "XFB1:XFD1"
        );
    }

    @Test
    public void testRangeRightPositiveXOffset3() {
        this.rangeAndCheck(
                "XFD1", 1, 0, WIDTH - 1, HEIGHT,
                "XFD1"
        );
    }

    @Test
    public void testRangeRightPositiveXOffset4() {
        this.rangeAndCheck(
                "XFD1", 1, 0, WIDTH + 1, HEIGHT,
                "XFC1:XFD1"
        );
    }

    @Test
    public void testRangeRightPositiveXOffset5() {
        this.rangeAndCheck(
                "XFD1", 1, 0, WIDTH * 4 - 1, HEIGHT,
                "XFA1:XFD1"
        );
    }

    @Test
    public void testRangeRightPositiveXOffset6() {
        this.rangeAndCheck(
                "XFD1", 1, 0, WIDTH * 4 + 1, HEIGHT,
                "XEZ1:XFD1"
        );
    }

    @Test
    public void testRangeRightNegativeXOffset11() {
        this.rangeAndCheck(
                "XFD1", -2 * WIDTH, 0, WIDTH, HEIGHT,
                "XFB1"
        );
    }

    @Test
    public void testRangeRightNegativeXOffset12() {
        this.rangeAndCheck(
                "XFD1", -2 * WIDTH, 0, 3 * WIDTH, HEIGHT,
                "XFB1:XFD1"
        );
    }

    @Test
    public void testRangeRightNegativeXOffset13() {
        this.rangeAndCheck(
                "XFD1", -2 * WIDTH, 0, WIDTH - 1, HEIGHT,
                "XFB1"
        );
    }

    @Test
    public void testRangeRightNegativeXOffset14() {
        this.rangeAndCheck(
                "XFD1", -2 * WIDTH, 0, WIDTH + 1, HEIGHT,
                "XFB1:XFC1"
        );
    }

    @Test
    public void testRangeRightNegativeXOffset15() {
        this.rangeAndCheck(
                "XFD1", -2 * WIDTH, 0, WIDTH * 4 - 1, HEIGHT,
                "XFA1:XFD1"
        );
    }

    @Test
    public void testRangeRightNegativeXOffset16() {
        this.rangeAndCheck(
                "XFD1", -2 * WIDTH, 0, WIDTH * 4 + 1, HEIGHT,
                "XEZ1:XFD1"
        );
    }

    @Test
    public void testRangeRightPositiveXOffset11() {
        this.rangeAndCheck(
                "XFD1", 2 * WIDTH, 0, WIDTH, HEIGHT,
                "XFD1"
        );
    }

    @Test
    public void testRangeRightPositiveXOffset12() {
        this.rangeAndCheck(
                "XFD1", 2 * WIDTH, 0, 3 * WIDTH, HEIGHT,
                "XFB1:XFD1"
        );
    }

    @Test
    public void testRangeRightPositiveXOffset13() {
        this.rangeAndCheck(
                "XFD1", 2 * WIDTH, 0, WIDTH - 1, HEIGHT,
                "XFD1"
        );
    }

    @Test
    public void testRangeRightPositiveXOffset14() {
        this.rangeAndCheck(
                "XFD1", 2 * WIDTH, 0, WIDTH + 1, HEIGHT,
                "XFC1:XFD1"
        );
    }

    @Test
    public void testRangeRightPositiveXOffset15() {
        this.rangeAndCheck(
                "XFD1", 2 * WIDTH, 0, WIDTH * 4 - 1, HEIGHT,
                "XFA1:XFD1"
        );
    }

    @Test
    public void testRangeRightPositiveXOffset16() {
        this.rangeAndCheck(
                "XFD1", 2 * WIDTH, 0, WIDTH * 4 + 1, HEIGHT,
                "XEZ1:XFD1"
        );
    }

    @Test
    public void testRangeRightNegativeXOffset21() {
        this.rangeAndCheck(
                "XFD1", 2 * WIDTH + -1, 0, WIDTH, HEIGHT,
                "XFD1"
        );
    }

    @Test
    public void testRangeRightNegativeXOffset22() {
        this.rangeAndCheck(
                "XFD1", 2 * WIDTH + -1, 0, 3 * WIDTH, HEIGHT,
                "XFB1:XFD1"
        );
    }

    @Test
    public void testRangeRightNegativeXOffset23() {
        this.rangeAndCheck(
                "XFD1", 2 * WIDTH + -1, 0, WIDTH - 1, HEIGHT,
                "XFD1"
        );
    }

    @Test
    public void testRangeRightNegativeXOffset24() {
        this.rangeAndCheck(
                "XFD1", 2 * WIDTH + -1, 0, WIDTH + 1, HEIGHT,
                "XFC1:XFD1"
        );
    }

    @Test
    public void testRangeRightNegativeXOffset25() {
        this.rangeAndCheck(
                "XFD1", 2 * WIDTH + -1, 0, WIDTH * 4 - 1, HEIGHT,
                "XFA1:XFD1"
        );
    }

    @Test
    public void testRangeRightNegativeXOffset26() {
        this.rangeAndCheck(
                "XFD1", 2 * WIDTH + -1, 0, WIDTH * 4 + 1, HEIGHT,
                "XEZ1:XFD1"
        );
    }

    @Test
    public void testRangeRightPositiveXOffset21() {
        this.rangeAndCheck(
                "XFD1", 2 * WIDTH + 1, 0, WIDTH, HEIGHT,
                "XFD1"
        );
    }

    @Test
    public void testRangeRightPositiveXOffset22() {
        this.rangeAndCheck(
                "XFD1", 2 * WIDTH + 1, 0, 3 * WIDTH, HEIGHT,
                "XFB1:XFD1"
        );
    }

    @Test
    public void testRangeRightPositiveXOffset23() {
        this.rangeAndCheck(
                "XFD1", 2 * WIDTH + 1, 0, WIDTH - 1, HEIGHT,
                "XFD1"
        );
    }

    @Test
    public void testRangeRightPositiveXOffset24() {
        this.rangeAndCheck(
                "XFD1", 2 * WIDTH + 1, 0, WIDTH + 1, HEIGHT,
                "XFC1:XFD1"
        );
    }

    @Test
    public void testRangeRightPositiveXOffset25() {
        this.rangeAndCheck(
                "XFD1", 2 * WIDTH + 1, 0, WIDTH * 4 - 1, HEIGHT,
                "XFA1:XFD1"
        );
    }

    @Test
    public void testRangeRightPositiveXOffset26() {
        this.rangeAndCheck(
                "XFD1", 2 * WIDTH + 1, 0, WIDTH * 4 + 1, HEIGHT,
                "XEZ1:XFD1"
        );
    }

    // heights top left .................................................................................................

    @Test
    public void testRangeTop() {
        this.rangeAndCheck(
                "A1", 0, 0, WIDTH, HEIGHT,
                "A1"
        );
    }

    @Test
    public void testRangeTop2() {
        this.rangeAndCheck(
                "A1", 0, 0, WIDTH, 3 * HEIGHT,
                "A1:A3"
        );
    }

    @Test
    public void testRangeTop3() {
        this.rangeAndCheck(
                "A1", 0, 0, WIDTH, HEIGHT - 1,
                "A1"
        );
    }

    @Test
    public void testRangeTop4() {
        this.rangeAndCheck(
                "A1", 0, 0, WIDTH, HEIGHT + 1,
                "A1:A2"
        );
    }

    @Test
    public void testRangeTop5() {
        this.rangeAndCheck(
                "A1", 0, 0, WIDTH, HEIGHT * 4 - 1,
                "A1:A4"
        );
    }

    @Test
    public void testRangeTop6() {
        this.rangeAndCheck(
                "A1", 0, 0, WIDTH, HEIGHT * 4 + 1,
                "A1:A5"
        );
    }

    @Test
    public void testRangeTopNegativeYOffset() {
        this.rangeAndCheck(
                "A1", 0, -1, WIDTH, HEIGHT,
                "A1"
        );
    }

    @Test
    public void testRangeTopNegativeYOffset2() {
        this.rangeAndCheck(
                "A1", 0, -1, WIDTH, 3 * HEIGHT,
                "A1:A3"
        );
    }

    @Test
    public void testRangeTopNegativeYOffset3() {
        this.rangeAndCheck(
                "A1", 0, -1, WIDTH, HEIGHT - 1,
                "A1"
        );
    }

    @Test
    public void testRangeTopNegativeYOffset4() {
        this.rangeAndCheck(
                "A1", 0, -1, WIDTH, HEIGHT + 1,
                "A1:A2"
        );
    }

    @Test
    public void testRangeTopNegativeYOffset5() {
        this.rangeAndCheck(
                "A1", 0, -1, WIDTH, HEIGHT * 4 - 1,
                "A1:A4"
        );
    }

    @Test
    public void testRangeTopNegativeYOffset6() {
        this.rangeAndCheck(
                "A1", 0, -1, WIDTH, HEIGHT * 4 + 1,
                "A1:A5"
        );
    }

    @Test
    public void testRangeTopPositiveYOffset() {
        this.rangeAndCheck(
                "A1", 0, 1, WIDTH, HEIGHT,
                "A1:A2"
        );
    }

    @Test
    public void testRangeTopPositiveYOffset2() {
        this.rangeAndCheck(
                "A1", 0, 1, WIDTH, 3 * HEIGHT,
                "A1:A4"
        );
    }

    @Test
    public void testRangeTopPositiveYOffset3() {
        this.rangeAndCheck(
                "A1", 0, 1, WIDTH, HEIGHT - 1,
                "A1"
        );
    }

    @Test
    public void testRangeTopPositiveYOffset4() {
        this.rangeAndCheck(
                "A1", 0, 1, WIDTH, HEIGHT + 1,
                "A1:A2"
        );
    }

    @Test
    public void testRangeTopPositiveYOffset5() {
        this.rangeAndCheck(
                "A1", 0, 1, WIDTH, HEIGHT * 4 - 1,
                "A1:A4"
        );
    }

    @Test
    public void testRangeTopPositiveYOffset6() {
        this.rangeAndCheck(
                "A1", 0, 1, WIDTH, HEIGHT * 4 + 1,
                "A1:A5"
        );
    }

    @Test
    public void testRangeMidY() {
        this.rangeAndCheck(
                "A10", 0, 0, WIDTH, HEIGHT,
                "A10"
        );
    }

    @Test
    public void testRangeMidY2() {
        this.rangeAndCheck(
                "A10", 0, 0, WIDTH, 3 * HEIGHT,
                "A10:A12"
        );
    }

    @Test
    public void testRangeMidY3() {
        this.rangeAndCheck(
                "A10", 0, 0, WIDTH, HEIGHT - 1,
                "A10"
        );
    }

    @Test
    public void testRangeMidY4() {
        this.rangeAndCheck(
                "A10", 0, 0, WIDTH, HEIGHT + 1,
                "A10:A11"
        );
    }

    @Test
    public void testRangeMidY5() {
        this.rangeAndCheck(
                "A10", 0, 0, WIDTH, HEIGHT * 4 - 1,
                "A10:A13"
        );
    }

    @Test
    public void testRangeMidY6() {
        this.rangeAndCheck(
                "A10", 0, 0, WIDTH, HEIGHT * 4 + 1,
                "A10:A14"
        );
    }

    @Test
    public void testRangeMidYNegativeYOffset() {
        this.rangeAndCheck(
                "A10", 0, -1, WIDTH, HEIGHT,
                "A9:A10"
        );
    }

    @Test
    public void testRangeMidYNegativeYOffset2() {
        this.rangeAndCheck(
                "A10", 0, -1, WIDTH, 3 * HEIGHT,
                "A9:A12"
        );
    }

    @Test
    public void testRangeMidYNegativeYOffset3() {
        this.rangeAndCheck(
                "A10", 0, -1, WIDTH, HEIGHT - 1,
                "A9:A10"
        );
    }

    @Test
    public void testRangeMidYNegativeYOffset4() {
        this.rangeAndCheck(
                "A10", 0, -1, WIDTH, HEIGHT + 1,
                "A9:A10"
        );
    }

    @Test
    public void testRangeMidYNegativeYOffset5() {
        this.rangeAndCheck(
                "A10", 0, -1, WIDTH, HEIGHT * 4 - 1,
                "A9:A13"
        );
    }

    @Test
    public void testRangeMidYNegativeYOffset6() {
        this.rangeAndCheck(
                "A10", 0, -1, WIDTH, HEIGHT * 4 + 1,
                "A9:A13"
        );
    }

    @Test
    public void testRangeMidYPositiveYOffset() {
        this.rangeAndCheck(
                "A10", 0, 1, WIDTH, HEIGHT,
                "A10:A11"
        );
    }

    @Test
    public void testRangeMidYPositiveYOffset2() {
        this.rangeAndCheck(
                "A10", 0, 1, WIDTH, 3 * HEIGHT,
                "A10:A13"
        );
    }

    @Test
    public void testRangeMidYPositiveYOffset3() {
        this.rangeAndCheck(
                "A10", 0, 1, WIDTH, HEIGHT - 1,
                "A10"
        );
    }

    @Test
    public void testRangeMidYPositiveYOffset4() {
        this.rangeAndCheck(
                "A10", 0, 1, WIDTH, HEIGHT + 1,
                "A10:A11"
        );
    }

    @Test
    public void testRangeMidYPositiveYOffset5() {
        this.rangeAndCheck(
                "A10", 0, 1, WIDTH, HEIGHT * 4 - 1,
                "A10:A13"
        );
    }

    @Test
    public void testRangeMidYPositiveYOffset6() {
        this.rangeAndCheck(
                "A10", 0, 1, WIDTH, HEIGHT * 4 + 1,
                "A10:A14"
        );
    }

    // heights top right .................................................................................................

    @Test
    public void testRangeBottom() {
        this.rangeAndCheck(
                "A1048576", 0, 0, WIDTH, HEIGHT,
                "A1048576"
        );
    }

    @Test
    public void testRangeBottom2() {
        this.rangeAndCheck(
                "A1048576", 0, 0, WIDTH, 3 * HEIGHT,
                "A1048574:A1048576"
        );
    }

    @Test
    public void testRangeBottom3() {
        this.rangeAndCheck(
                "A1048576", 0, 0, WIDTH, HEIGHT - 1,
                "A1048576"
        );
    }

    @Test
    public void testRangeBottom4() {
        this.rangeAndCheck(
                "A1048576", 0, 0, WIDTH, HEIGHT + 1,
                "A1048575:A1048576"
        );
    }

    @Test
    public void testRangeBottom5() {
        this.rangeAndCheck(
                "A1048576", 0, 0, WIDTH, HEIGHT * 4 - 1,
                "A1048573:A1048576"
        );
    }

    @Test
    public void testRangeBottom6() {
        this.rangeAndCheck(
                "A1048576", 0, 0, WIDTH, HEIGHT * 4 + 1,
                "A1048572:A1048576"
        );
    }

    @Test
    public void testRangeBottomNegativeYOffset() {
        this.rangeAndCheck(
                "A1048576", 0, -1, WIDTH, HEIGHT,
                "A1048575:A1048576"
        );
    }

    @Test
    public void testRangeBottomNegativeYOffset2() {
        this.rangeAndCheck(
                "A1048576", 0, -1, WIDTH, 3 * HEIGHT,
                "A1048573:A1048576"
        );
    }

    @Test
    public void testRangeBottomNegativeYOffset3() {
        this.rangeAndCheck(
                "A1048576", 0, -1, WIDTH, HEIGHT - 1,
                "A1048575:A1048576"
        );
    }

    @Test
    public void testRangeBottomNegativeYOffset4() {
        this.rangeAndCheck(
                "A1048576", 0, -1, WIDTH, HEIGHT + 1,
                "A1048575:A1048576"
        );
    }

    @Test
    public void testRangeBottomNegativeYOffset5() {
        this.rangeAndCheck(
                "A1048576", 0, -1, WIDTH, HEIGHT * 4 - 1,
                "A1048572:A1048576"
        );
    }

    @Test
    public void testRangeBottomNegativeYOffset6() {
        this.rangeAndCheck(
                "A1048576", 0, -1, WIDTH, HEIGHT * 4 + 1,
                "A1048572:A1048576"
        );
    }

    @Test
    public void testRangeBottomPositiveYOffset() {
        this.rangeAndCheck(
                "A1048576", 0, 1, WIDTH, HEIGHT,
                "A1048576"
        );
    }

    @Test
    public void testRangeBottomPositiveYOffset2() {
        this.rangeAndCheck(
                "A1048576", 0, 1, WIDTH, 3 * HEIGHT,
                "A1048574:A1048576"
        );
    }

    @Test
    public void testRangeBottomPositiveYOffset3() {
        this.rangeAndCheck(
                "A1048576", 0, 1, WIDTH, HEIGHT - 1,
                "A1048576"
        );
    }

    @Test
    public void testRangeBottomPositiveYOffset4() {
        this.rangeAndCheck(
                "A1048576", 0, 1, WIDTH, HEIGHT + 1,
                "A1048575:A1048576"
        );
    }

    @Test
    public void testRangeBottomPositiveYOffset5() {
        this.rangeAndCheck(
                "A1048576", 0, 1, WIDTH, HEIGHT * 4 - 1,
                "A1048573:A1048576"
        );
    }

    @Test
    public void testRangeBottomPositiveYOffset6() {
        this.rangeAndCheck(
                "A1048576", 0, 1, WIDTH, HEIGHT * 4 + 1,
                "A1048572:A1048576"
        );
    }

    @Test
    public void testRangeBottomNegativeYOffset11() {
        this.rangeAndCheck(
                "A1048576", 0, -2 * HEIGHT, WIDTH, HEIGHT,
                "A1048574"
        );
    }

    @Test
    public void testRangeBottomNegativeYOffset12() {
        this.rangeAndCheck(
                "A1048576", 0, -2 * HEIGHT, WIDTH, 3 * HEIGHT,
                "A1048574:A1048576"
        );
    }

    @Test
    public void testRangeBottomNegativeYOffset13() {
        this.rangeAndCheck(
                "A1048576", 0, -2 * HEIGHT, WIDTH, HEIGHT - 1,
                "A1048574"
        );
    }

    @Test
    public void testRangeBottomNegativeYOffset14() {
        this.rangeAndCheck(
                "A1048576", 0, -2 * HEIGHT, WIDTH, HEIGHT + 1,
                "A1048575:A1048574"
        );
    }

    @Test
    public void testRangeBottomNegativeYOffset15() {
        this.rangeAndCheck(
                "A1048576", 0, -2 * HEIGHT, WIDTH, HEIGHT * 4 - 1,
                "A1048573:A1048576"
        );
    }

    @Test
    public void testRangeBottomNegativeYOffset16() {
        this.rangeAndCheck(
                "A1048576", 0, -2 * HEIGHT, WIDTH, HEIGHT * 4 + 1,
                "A1048572:A1048576"
        );
    }

    @Test
    public void testRangeBottomPositiveYOffset11() {
        this.rangeAndCheck(
                "A1048576", 0, 2 * HEIGHT, WIDTH, HEIGHT,
                "A1048576"
        );
    }

    @Test
    public void testRangeBottomPositiveYOffset12() {
        this.rangeAndCheck(
                "A1048576", 0, 2 * HEIGHT, WIDTH, 3 * HEIGHT,
                "A1048574:A1048576"
        );
    }

    @Test
    public void testRangeBottomPositiveYOffset13() {
        this.rangeAndCheck(
                "A1048576", 0, 2 * HEIGHT, WIDTH, HEIGHT - 1,
                "A1048576"
        );
    }

    @Test
    public void testRangeBottomPositiveYOffset14() {
        this.rangeAndCheck(
                "A1048576", 0, 2 * HEIGHT, WIDTH, HEIGHT + 1,
                "A1048575:A1048576"
        );
    }

    @Test
    public void testRangeBottomPositiveYOffset15() {
        this.rangeAndCheck(
                "A1048576", 0, 2 * HEIGHT, WIDTH, HEIGHT * 4 - 1,
                "A1048573:A1048576"
        );
    }

    @Test
    public void testRangeBottomPositiveYOffset16() {
        this.rangeAndCheck(
                "A1048576", 0, 2 * HEIGHT, WIDTH, HEIGHT * 4 + 1,
                "A1048572:A1048576"
        );
    }

    @Test
    public void testRangeBottomNegativeYOffset21() {
        this.rangeAndCheck(
                "A1048576", 0, -2 * HEIGHT + -1, WIDTH, HEIGHT,
                "A1048573:A1048574"
        );
    }

    @Test
    public void testRangeBottomNegativeYOffset22() {
        this.rangeAndCheck(
                "A1048576", 0, -2 * HEIGHT + -1, WIDTH, 3 * HEIGHT,
                "A1048573:A1048576"
        );
    }

    @Test
    public void testRangeBottomNegativeYOffset23() {
        this.rangeAndCheck(
                "A1048576", 0, -2 * HEIGHT + -1, WIDTH, HEIGHT - 1,
                "A1048573:A1048574"
        );
    }

    @Test
    public void testRangeBottomNegativeYOffset24() {
        this.rangeAndCheck(
                "A1048576", 0, -2 * HEIGHT + -1, WIDTH, HEIGHT + 1,
                "A1048573:A1048574"
        );
    }

    @Test
    public void testRangeBottomNegativeYOffset25() {
        this.rangeAndCheck(
                "A1048576", 0, -2 * HEIGHT + -1, WIDTH, HEIGHT * 4 - 1,
                "A1048572:A1048576"
        );
    }

    @Test
    public void testRangeBottomNegativeYOffset26() {
        this.rangeAndCheck(
                "A1048576", 0, -2 * HEIGHT + -1, WIDTH, HEIGHT * 4 + 1,
                "A1048572:A1048576"
        );
    }

    @Test
    public void testRangeBottomPositiveYOffset21() {
        this.rangeAndCheck(
                "A1048576", 0, 2 * HEIGHT + 1, WIDTH, HEIGHT,
                "A1048576"
        );
    }

    @Test
    public void testRangeBottomPositiveYOffset22() {
        this.rangeAndCheck(
                "A1048576", 0, 2 * HEIGHT + 1, WIDTH, 3 * HEIGHT,
                "A1048574:A1048576"
        );
    }

    @Test
    public void testRangeBottomPositiveYOffset23() {
        this.rangeAndCheck(
                "A1048576", 0, 2 * HEIGHT + 1, WIDTH, HEIGHT - 1,
                "A1048576"
        );
    }

    @Test
    public void testRangeBottomPositiveYOffset24() {
        this.rangeAndCheck(
                "A1048576", 0, 2 * HEIGHT + 1, WIDTH, HEIGHT + 1,
                "A1048575:A1048576"
        );
    }

    @Test
    public void testRangeBottomPositiveYOffset25() {
        this.rangeAndCheck(
                "A1048576", 0, 2 * HEIGHT + 1, WIDTH, HEIGHT * 4 - 1,
                "A1048573:A1048576"
        );
    }

    @Test
    public void testRangeBottomPositiveYOffset26() {
        this.rangeAndCheck(
                "A1048576", 0, 2 * HEIGHT + 1, WIDTH, HEIGHT * 4 + 1,
                "A1048572:A1048576"
        );
    }

    private void rangeAndCheck(final String cellOrLabel,
                               final double xOffset,
                               final double yOffset,
                               final double width,
                               final double height,
                               final String range) {
        this.rangeAndCheck(
                SpreadsheetViewport.with(
                        SpreadsheetExpressionReference.parseCellReferenceOrLabelName(cellOrLabel),
                        xOffset,
                        yOffset,
                        width,
                        height
                ),
                SpreadsheetRange.parseRange(range)
        );
    }

    private void rangeAndCheck(final SpreadsheetViewport viewport,
                               final SpreadsheetRange range) {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(engine);

        assertEquals(
                range,
                engine.range(viewport, context),
                () -> "viewportRange " + viewport
        );
    }

    //  helpers.........................................................................................................

    @Override
    public BasicSpreadsheetEngine createSpreadsheetEngine() {
        return BasicSpreadsheetEngine.with(this.metadata());
    }

    @Override
    public SpreadsheetEngineContext createContext() {
        return this.createContext(SpreadsheetEngines.fake());
    }

    private SpreadsheetEngineContext createContext(final SpreadsheetCellStore cellStore) {
        return this.createContext(
                DEFAULT_YEAR,
                SpreadsheetEngines.fake(),
                new FakeSpreadsheetStoreRepository() {
                    @Override
                    public SpreadsheetCellStore cells() {
                        return cellStore;
                    }
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
                        SpreadsheetExpressionReferenceStores.treeMap(),
                        SpreadsheetGroupStores.fake(),
                        SpreadsheetLabelStores.treeMap(),
                        SpreadsheetExpressionReferenceStores.treeMap(),
                        SpreadsheetMetadataStores.fake(),
                        SpreadsheetRangeStores.treeMap(),
                        SpreadsheetRangeStores.treeMap(),
                        SpreadsheetUserStores.fake()
                )
        );
    }

    private SpreadsheetEngineContext createContext(final int defaultYear,
                                                   final SpreadsheetEngine engine,
                                                   final SpreadsheetStoreRepository storeRepository) {
        return new FakeSpreadsheetEngineContext() {

            @Override
            public SpreadsheetCellReference resolveCellReference(final SpreadsheetExpressionReference reference) {
                if (reference.isCellReference()) {
                    return (SpreadsheetCellReference) reference;
                }
                assertEquals(LABEL, reference.toString());
                return LABEL_CELL;
            }

            public SpreadsheetMetadata metadata() {
                return BasicSpreadsheetEngineTest.this.metadata()
                        .set(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, defaultYear);
            }

            @Override
            public int defaultYear() {
                return this.metadata()
                        .getOrFail(SpreadsheetMetadataPropertyName.DEFAULT_YEAR);
            }

            @Override
            public SpreadsheetParserToken parseFormula(final String formula) {
                return SpreadsheetParsers.valueOrExpression(BasicSpreadsheetEngineTest.this.metadata().parser())
                        .orFailIfCursorNotEmpty(ParserReporters.basic())
                        .parse(
                                TextCursors.charSequence(formula),
                                SpreadsheetParserContexts.basic(
                                        DateTimeContexts.fake(),
                                        converterContext(),
                                        this.metadata().expressionNumberKind(),
                                        VALUE_SEPARATOR
                                )
                        )
                        .get()
                        .cast(SpreadsheetParserToken.class);
            }

            @Override
            public Object evaluate(final Expression node) {
                // throw an exception which is an "error" when the invalidCellReference function appears in a formula and executed
                final Function<FunctionExpressionName, ExpressionFunction<?, ExpressionFunctionContext>> functions = (name) -> {
                    assertEquals(SpreadsheetFormula.INVALID_CELL_REFERENCE.value(), "InvalidCellReference");
                    switch (name.value()) {
                        case "InvalidCellReference":
                            return new FakeExpressionFunction<>() {
                                @Override
                                public Object apply(final List<Object> parameters,
                                                    final ExpressionFunctionContext context) {
                                    throw new ExpressionEvaluationException("Invalid cell reference: " + parameters.get(0).toString());
                                }

                                @Override
                                public boolean resolveReferences() {
                                    return true;
                                }
                            };
                        case "BasicSpreadsheetEngineTestSum":
                            return new FakeExpressionFunction<>() {
                                @Override
                                public Object apply(final List<Object> parameters,
                                                    final ExpressionFunctionContext context) {
                                    return parameters.stream()
                                            .map(ExpressionNumber.class::cast)
                                            .reduce(context.expressionNumberKind().create(0), (l, r) -> l.add(r, context));
                                }

                                @Override
                                public boolean resolveReferences() {
                                    return true;
                                }
                            };
                        case "BasicSpreadsheetEngineTestCounter":
                            return new FakeExpressionFunction<>() {
                                @Override
                                public Object apply(final List<Object> parameters,
                                                    final ExpressionFunctionContext context) {
                                    return BasicSpreadsheetEngineTest.this.counter;
                                }

                                @Override
                                public boolean resolveReferences() {
                                    return true;
                                }
                            };
                        default:
                            throw new UnknownExpressionFunctionException(name);
                    }
                };

                return node.toValue(
                        ExpressionEvaluationContexts.basic(
                                this.metadata().expressionNumberKind(),
                                functions,
                                SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionFunction.with(
                                        engine,
                                        storeRepository.labels(),
                                        this
                                ),
                                converterContext())
                );
            }

            @Override
            public <T> Either<T, String> convert(final Object value, final Class<T> target) {
                assertEquals(Boolean.class, target, "Only support converting to Boolean=" + value);
                return Either.left(target.cast(Boolean.parseBoolean(String.valueOf(value))));
            }

            @Override
            public MathContext mathContext() {
                return MATH_CONTEXT;
            }

            @Override
            public SpreadsheetFormatter parsePattern(final String pattern) {
                if (PATTERN_COLOR.equals(pattern)) {
                    return formatter(pattern, COLOR, FORMATTED_PATTERN_SUFFIX);
                }
                if (PATTERN.equals(pattern)) {
                    return formatter(pattern, SpreadsheetText.WITHOUT_COLOR, FORMATTED_PATTERN_SUFFIX);
                }
                if (PATTERN_FORMAT_FAIL.equals(pattern)) {
                    return new SpreadsheetFormatter() {
                        @Override
                        public boolean canFormat(final Object value,
                                                 final SpreadsheetFormatterContext context) throws SpreadsheetFormatException {
                            return true;
                        }

                        @Override
                        public Optional<SpreadsheetText> format(final Object value, final SpreadsheetFormatterContext context) throws SpreadsheetFormatException {
                            return Optional.empty();
                        }
                    };
                }

                throw new AssertionError("Unknown pattern=" + pattern + " expected one of " + PATTERN_FORMAT_FAIL + "|" + PATTERN + "|" + PATTERN_COLOR);
            }

            @Override
            public Optional<SpreadsheetText> format(final Object value,
                                                    final SpreadsheetFormatter formatter) {
                assertFalse(value instanceof Optional, () -> "Value must not be optional" + value);
                return formatter.format(Cast.to(value), SPREADSHEET_TEXT_FORMAT_CONTEXT);
            }

            @Override
            public SpreadsheetStoreRepository storeRepository() {
                return storeRepository;
            }
        };
    }

    private Object counter;

    private SpreadsheetFormatter formatter(final String pattern,
                                           final Optional<Color> color,
                                           final String suffix) {
        return new SpreadsheetFormatter() {

            @Override
            public boolean canFormat(final Object value,
                                     final SpreadsheetFormatterContext context) throws SpreadsheetFormatException {
                return value instanceof BigDecimal;
            }

            @Override
            public Optional<SpreadsheetText> format(final Object value,
                                                    final SpreadsheetFormatterContext context) throws SpreadsheetFormatException {
                assertNotNull(value, "value");
                assertSame(SPREADSHEET_TEXT_FORMAT_CONTEXT, context, "Wrong SpreadsheetFormatterContext passed");

                return Optional.of(SpreadsheetText.with(color, value + " " + suffix));
            }

            @Override
            public String toString() {
                return pattern;
            }
        };
    }

    private SpreadsheetCell loadCellAndCheckFormatted2(final SpreadsheetEngine engine,
                                                       final SpreadsheetCellReference reference,
                                                       final SpreadsheetEngineEvaluation evaluation,
                                                       final SpreadsheetEngineContext context,
                                                       final Object value,
                                                       final String suffix) {
        final SpreadsheetCell cell = this.loadCellAndCheckValue(engine, reference, evaluation, context, value);
        this.checkFormattedText(cell, value + " " + suffix);
        return cell;
    }

    private SpreadsheetCell formattedCellWithValue(final String reference,
                                                   final String formulaText,
                                                   final Object value) {
        return this.formattedCellWithValue(SpreadsheetExpressionReference.parseCellReference(reference),
                formulaText,
                value);
    }

    private SpreadsheetCell formattedCellWithValue(final SpreadsheetCellReference reference,
                                                   final String formulaText,
                                                   final Object value) {
        return this.formattedCellWithValue(this.cell(reference, formulaText),
                value);
    }

    /**
     * Makes a {@link SpreadsheetCell} updating the formula expression and expected value and then formats the cell adding styling etc,
     * mimicking the very actions that happen during evaluation.
     */
    private SpreadsheetCell formattedCellWithValue(final SpreadsheetCell cell,
                                                   final Object value) {

        final SpreadsheetText formattedText = this.metadata()
                .formatter()
                .format(value, SPREADSHEET_TEXT_FORMAT_CONTEXT)
                .orElseThrow(() -> new AssertionError("Failed to format " + CharSequences.quoteIfChars(value)));
        final Optional<TextNode> formattedCell = Optional.of(this.style()
                .replace(formattedText.toTextNode())
                .root());

        return cell.setFormula(this.parseFormula(cell.formula())
                .setValue(Optional.of(value)))
                .setFormatted(formattedCell);
    }

    private SpreadsheetCell formattedCellWithError(final String reference,
                                                   final String formulaText,
                                                   final String message) {
        return this.formattedCellWithError(SpreadsheetExpressionReference.parseCellReference(reference),
                formulaText,
                message);
    }

    private SpreadsheetCell formattedCellWithError(final SpreadsheetCellReference reference,
                                                   final String formulaText,
                                                   final String message) {
        return this.formattedCellWithError(this.cell(reference, formulaText),
                message);
    }

    /**
     * Makes a {@link SpreadsheetCell} updating the formula expression and setting the error and formatted cell and style.
     */
    private SpreadsheetCell formattedCellWithError(final SpreadsheetCell cell,
                                                   final String errorMessage) {
        final Optional<TextNode> formattedCell = Optional.of(this.style()
                .replace(TextNode.text(errorMessage))
                .root());

        return cell.setFormula(this.parseFormula(cell.formula())
                .setError(Optional.of(SpreadsheetError.with(errorMessage))))
                .setFormatted(formattedCell);
    }

    /**
     * Assumes the formula is syntactically correct and updates the cell.
     */
    private SpreadsheetFormula parseFormula(final SpreadsheetFormula formula) {
        final String text = formula.text();
        final ExpressionNumberKind expressionNumberKind = this.expressionNumberKind();

        final SpreadsheetParserToken token =
                text.isEmpty() ?
                        null :
                        SpreadsheetParsers.valueOrExpression(BasicSpreadsheetEngineTest.this.metadata().parser())
                                .parse(TextCursors.charSequence(text),
                                        SpreadsheetParserContexts.basic(
                                                this.dateTimeContext(),
                                                this.decimalNumberContext(),
                                                expressionNumberKind,
                                                VALUE_SEPARATOR
                                        )
                                ).orElseThrow(() -> new AssertionError("Failed to parseFormula " + CharSequences.quote(text)))
                                .cast(SpreadsheetParserToken.class);
        SpreadsheetFormula parsedFormula = formula;
        if (null == token) {
            parsedFormula = formula.setToken(BasicSpreadsheetEngine.EMPTY_TOKEN)
                    .setExpression(BasicSpreadsheetEngine.EMPTY_EXPRESSION);
        } else {
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
                                        return expressionNumberKind;
                                    }

                                    @Override
                                    public int twoDigitYear() {
                                        return TWO_DIGIT_YEAR;
                                    }
                                }
                        )
                );
            } catch (final Exception fail) {
                parsedFormula = parsedFormula.setError(
                        Optional.of(SpreadsheetError.with(fail.getMessage()))
                );
            }
        }

        return parsedFormula;
    }

    private void loadCellStoreAndCheck(final SpreadsheetCellStore store,
                                       final SpreadsheetCell... cells) {
        assertEquals(Lists.of(cells),
                store.all(),
                () -> "all cells in " + store);
    }

    private void loadLabelStoreAndCheck(final SpreadsheetLabelStore store,
                                        final SpreadsheetLabelMapping... mappings) {
        assertEquals(Lists.of(mappings),
                store.all(),
                () -> "all mappings in " + store);
    }

    private <E extends SpreadsheetCellReferenceOrLabelName & Comparable<E>>
    void loadReferencesAndCheck(final SpreadsheetExpressionReferenceStore<E> store,
                                final E cell,
                                final SpreadsheetCellReference... out) {
        assertEquals(Optional.ofNullable(out.length == 0 ? null : Sets.of(out)),
                store.load(cell),
                "references to " + cell);
    }

    private void loadReferrersAndCheck(final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store,
                                       final SpreadsheetCellReference cell,
                                       final SpreadsheetCellReference... out) {
        assertEquals(Sets.of(out),
                store.loadReferred(cell),
                "referrers from " + cell);
    }

    private ExpressionNumber number(final Number number) {
        return this.expressionNumberKind().create(number);
    }

    private SpreadsheetMetadata metadata() {
        final String suffix = " " + '"' + FORMATTED_PATTERN_SUFFIX + '"';

        return SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.forLanguageTag("EN-AU"))
                .loadFromLocale()
                .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.EXCEL_1900_DATE_SYSTEM_OFFSET)
                .set(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, DEFAULT_YEAR)
                .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, ExpressionNumberKind.DEFAULT)
                .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.HALF_UP)
                .set(SpreadsheetMetadataPropertyName.PRECISION, 7)
                .set(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR, TWO_DIGIT_YEAR)
                .set(SpreadsheetMetadataPropertyName.DATE_FORMAT_PATTERN, SpreadsheetParsePatterns.parseDateFormatPattern(DATE_PATTERN + suffix))
                .set(SpreadsheetMetadataPropertyName.DATE_PARSE_PATTERNS, SpreadsheetParsePatterns.parseDateParsePatterns(DATE_PATTERN + ";dd/mm"))
                .set(SpreadsheetMetadataPropertyName.DATETIME_FORMAT_PATTERN, SpreadsheetParsePatterns.parseDateTimeFormatPattern(DATETIME_PATTERN + suffix))
                .set(SpreadsheetMetadataPropertyName.DATETIME_PARSE_PATTERNS, SpreadsheetParsePatterns.parseDateTimeParsePatterns(DATETIME_PATTERN))
                .set(SpreadsheetMetadataPropertyName.NUMBER_FORMAT_PATTERN, SpreadsheetParsePatterns.parseNumberFormatPattern(NUMBER_PATTERN + suffix))
                .set(SpreadsheetMetadataPropertyName.NUMBER_PARSE_PATTERNS, SpreadsheetParsePatterns.parseNumberParsePatterns(NUMBER_PATTERN))
                .set(SpreadsheetMetadataPropertyName.TEXT_FORMAT_PATTERN, SpreadsheetParsePatterns.parseTextFormatPattern(TEXT_PATTERN + suffix))
                .set(SpreadsheetMetadataPropertyName.TIME_FORMAT_PATTERN, SpreadsheetParsePatterns.parseTimeFormatPattern(TIME_PATTERN + suffix))
                .set(SpreadsheetMetadataPropertyName.TIME_PARSE_PATTERNS, SpreadsheetParsePatterns.parseTimeParsePatterns(TIME_PATTERN))
                .set(SpreadsheetMetadataPropertyName.STYLE, TextStyle.EMPTY
                        .set(TextStylePropertyName.WIDTH, Length.parsePixels(WIDTH + "px"))
                        .set(TextStylePropertyName.HEIGHT, Length.parsePixels(HEIGHT + "px"))
                );
    }

    private SpreadsheetColumnReference column(final int column) {
        return SpreadsheetReferenceKind.ABSOLUTE.column(column);
    }

    private SpreadsheetRowReference row(final int row) {
        return SpreadsheetReferenceKind.ABSOLUTE.row(row);
    }

    private SpreadsheetCellReference cellReference(final String reference) {
        return SpreadsheetExpressionReference.parseCellReference(reference);
    }

    private SpreadsheetCellReference cellReference(final int column, final int row) {
        return SpreadsheetReferenceKind.ABSOLUTE.column(column).setRow(SpreadsheetReferenceKind.ABSOLUTE.row(row));
    }

    private SpreadsheetCell cell(final String reference, final String formula) {
        return this.cell(SpreadsheetExpressionReference.parseCellReference(reference), formula);
    }

    private SpreadsheetCell cell(final SpreadsheetCellReference reference, final String formula) {
        return this.cell(reference, SpreadsheetFormula.with(formula));
    }

    private SpreadsheetCell cell(final SpreadsheetCellReference reference, final SpreadsheetFormula formula) {
        return SpreadsheetCell.with(reference, formula)
                .setStyle(this.style());
    }

    private TextStyle style() {
        return TextStyle.with(Maps.of(TextStylePropertyName.FONT_WEIGHT, FontWeight.BOLD));
    }

    private void addFailingCellSaveWatcherAndDeleteWatcher(final SpreadsheetEngineContext context) {
        final SpreadsheetCellStore store = context.storeRepository()
                .cells();

        store.addSaveWatcher((ignored) -> {
            throw new UnsupportedOperationException();
        });
        store.addDeleteWatcher((ignored) -> {
            throw new UnsupportedOperationException();
        });
    }

    @Override
    public Class<BasicSpreadsheetEngine> type() {
        return BasicSpreadsheetEngine.class;
    }

    // TypeNameTesting..........................................................................................

    @Override
    public String typeNameSuffix() {
        return "";
    }
}

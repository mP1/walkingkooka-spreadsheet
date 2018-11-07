package walkingkooka.spreadsheet.engine;

import org.junit.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.color.Color;
import walkingkooka.convert.Converters;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCellFormat;
import walkingkooka.spreadsheet.SpreadsheetDescription;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.spreadsheet.conditionalformat.SpreadsheetConditionalFormattingRule;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.store.range.SpreadsheetRangeStore;
import walkingkooka.spreadsheet.store.range.SpreadsheetRangeStores;
import walkingkooka.spreadsheet.style.SpreadsheetCellStyle;
import walkingkooka.spreadsheet.style.SpreadsheetTextStyle;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserContexts;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParsers;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;
import walkingkooka.text.spreadsheetformat.SpreadsheetFormattedText;
import walkingkooka.text.spreadsheetformat.SpreadsheetTextFormatContext;
import walkingkooka.text.spreadsheetformat.SpreadsheetTextFormatContexts;
import walkingkooka.text.spreadsheetformat.SpreadsheetTextFormatter;
import walkingkooka.text.spreadsheetformat.SpreadsheetTextFormatters;
import walkingkooka.tree.expression.ExpressionEvaluationContexts;
import walkingkooka.tree.expression.ExpressionEvaluationException;
import walkingkooka.tree.expression.ExpressionNode;
import walkingkooka.tree.expression.ExpressionNodeName;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public final class BasicSpreadsheetEngineTest extends SpreadsheetEngineTestCase<BasicSpreadsheetEngine> {

    private final static String PATTERN_SUFFIX = "PATTERN_SUFFIX";
    private final static String DEFAULT_SUFFIX = "DEFAULT_SUFFIX";

    private final static Optional<Color> COLOR = Optional.of(Color.BLACK);

    private final static String PATTERN_DEFAULT = "$text+" + DEFAULT_SUFFIX;
    private final static String PATTERN = "$text+" + PATTERN_SUFFIX;
    private final static String PATTERN_COLOR = "$text+" + PATTERN_SUFFIX + "+" + COLOR.get();
    private final static String PATTERN_FORMAT_FAIL = "<none>";

    @Test(expected = NullPointerException.class)
    public void testNullIdFails() {
        BasicSpreadsheetEngine.with(null, this.cellStore(), this.labelStore(), this.conditionalFormattingRules());
    }

    @Test(expected = NullPointerException.class)
    public void testNullCellStoreFails() {
        BasicSpreadsheetEngine.with(this.id(), null, this.labelStore(), this.conditionalFormattingRules());
    }

    @Test(expected = NullPointerException.class)
    public void testNullLabelStoreFails() {
        BasicSpreadsheetEngine.with(this.id(), this.cellStore(), null, this.conditionalFormattingRules());
    }

    @Test(expected = NullPointerException.class)
    public void testNullConditionalFormattingRulesFails() {
        BasicSpreadsheetEngine.with(this.id(), this.cellStore(), this.labelStore(), null);
    }

    @Test
    public void testLoadCellCellWhenEmpty() {
        this.loadCellFailCheck(cellReference(1, 1), SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY);
    }

    @Test
    public void testSaveCellAndLoadCellSkipEvaluate() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference cellReference = this.cellReference(1, 1);
        cellStore.save(this.cell(cellReference, "1+2"));

        this.loadCellAndCheckWithoutValueOrError(engine,
                cellReference,
                SpreadsheetEngineLoading.SKIP_EVALUATE,
                context);
    }

    @Test
    public void testSaveCellAndLoadCellWithoutPattern() {
        this.saveCellAndLoadCellAndCheck(SpreadsheetCell.NO_FORMAT, DEFAULT_SUFFIX);
    }

    @Test
    public void testSaveCellAndLoadCellWithPattern() {
        this.saveCellAndLoadCellAndCheck(Optional.of(SpreadsheetCellFormat.with(PATTERN, SpreadsheetCellFormat.NO_FORMATTER)),
                PATTERN_SUFFIX);
    }

    @Test
    public void testSaveCellAndLoadCellWithPatternAndFormatter() {
        final String pattern = "Custom";
        final String suffix = "CustomSuffix";
        this.saveCellAndLoadCellAndCheck(Optional.of(SpreadsheetCellFormat.with(pattern,
                Optional.of(this.formatter(pattern, SpreadsheetFormattedText.WITHOUT_COLOR, suffix)))),
                suffix);
    }

    private void saveCellAndLoadCellAndCheck(final Optional<SpreadsheetCellFormat> format,
                                             final String patternSuffix) {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference cellReference = this.cellReference(1, 1);
        cellStore.save(this.cell(cellReference, "1+2")
                .setFormat(format));

        this.loadCellAndCheckFormatted2(engine,
                cellReference,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(1 + 2),
                patternSuffix);
    }

    @Test
    public void testLoadCellComputeIfNecessaryCachesCell() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference cellReference = this.cellReference(1, 1);
        cellStore.save(this.cell(cellReference, "1+2"));

        final SpreadsheetCell first = this.loadCellOrFail(engine,
                cellReference,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context);
        final SpreadsheetCell second = this.loadCellOrFail(engine,
                cellReference,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context);

        assertSame("different instances of SpreadsheetCell returned not cached", first, second);
    }

    @Test
    public void testLoadCellComputeIfNecessaryCachesCellWithInvalidFormulaAndErrorCached() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference cellReference = this.cellReference(1, 1);
        cellStore.save(this.cell(cellReference, "1+2+"));

        final SpreadsheetCell first = this.loadCellOrFail(engine,
                cellReference,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context);
        assertNotEquals("Expected error absent=" + first, SpreadsheetFormula.NO_ERROR, first.formula().error());

        final SpreadsheetCell second = this.loadCellOrFail(engine, cellReference, SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY, context);
        assertSame("different instances of SpreadsheetCell returned not cached", first, second);
    }

    @Test
    public void testLoadCellForceRecomputeIgnoresCache() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference(1, 1);
        cellStore.save(this.cell(a, "1"));

        final SpreadsheetCellReference b = this.cellReference(2, 2);
        cellStore.save(this.cell(b, "" + a));

        final SpreadsheetCell first = this.loadCellOrFail(engine, a, SpreadsheetEngineLoading.FORCE_RECOMPUTE, context);

        cellStore.save(this.cell(a, "999"));

        final SpreadsheetCell second = this.loadCellOrFail(engine, a, SpreadsheetEngineLoading.FORCE_RECOMPUTE, context);
        assertNotSame("different instances of SpreadsheetCell returned not cached", first, second);
        assertEquals("first should have value updated to 999 and not 1 the original value.", Optional.of(BigDecimal.valueOf(999)), second.formula().value());
    }

    @Test
    public void testLoadCellComputeThenSkipEvaluate() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference cellReference = this.cellReference(1, 1);
        cellStore.save(this.cell(cellReference, "1+2"));

        final SpreadsheetCell first = this.loadCellOrFail(engine,
                cellReference,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context);
        final SpreadsheetCell second = this.loadCellOrFail(engine,
                cellReference,
                SpreadsheetEngineLoading.SKIP_EVALUATE,
                context);

        assertSame("different instances of SpreadsheetCell returned not cached", first, second);
    }

    @Test
    public void testSaveCellAndLoadCellMany() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference(1, 1);
        final SpreadsheetCellReference b = this.cellReference(2, 1);
        final SpreadsheetCellReference c = this.cellReference(3, 1);

        cellStore.save(this.cell(a, "1+2"));
        cellStore.save(this.cell(b, "3+4"));
        cellStore.save(this.cell(c, "5+6"));

        this.loadCellAndCheckFormatted2(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(1 + 2),
                DEFAULT_SUFFIX);
        this.loadCellAndCheckFormatted2(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(3 + 4),
                DEFAULT_SUFFIX);
        this.loadCellAndCheckFormatted2(engine,
                c,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(5 + 6),
                DEFAULT_SUFFIX);
    }

    @Test
    public void testLoadCellValueCellReferenceInvalidFails() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        cellStore.save(this.cell(a, "X99"));

        this.loadCellAndCheckError(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "Unknown cell reference");
    }

    @Test
    public void testLoadCellValueLabelInvalidFails() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        cellStore.save(this.cell(a, "INVALIDLABEL"));

        this.loadCellAndCheckError(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "Unknown label");
    }

    @Test
    public void testLoadCellValueIsCellReference() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // B1

        cellStore.save(this.cell(a, "B1"));
        cellStore.save(this.cell(b, "3+4"));

        // formula
        this.loadCellAndCheckFormatted2(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(3 + 4),
                DEFAULT_SUFFIX);

        // reference to B1 which has formula
        this.loadCellAndCheckFormatted2(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(3 + 4),
                DEFAULT_SUFFIX);
    }

    @Test
    public void testLoadCallValueIsLabel() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // B1

        cellStore.save(this.cell(a, LABEL.value()));
        cellStore.save(this.cell(b, "3+4"));

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, b));

        // formula
        this.loadCellAndCheckFormatted2(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(3 + 4),
                DEFAULT_SUFFIX);

        // reference to B1 which has formula
        this.loadCellAndCheckFormatted2(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(3 + 4),
                DEFAULT_SUFFIX);
    }

    @Test
    public void testLoadCallWithConditionalFormattingRule() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();

        final SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> rules = this.conditionalFormattingRules();

        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore, rules);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1

        // rule 3 is ignored because it returns false, rule 2 short circuits the conditional testing ...
        this.saveRule(true,
                1,
                style(SpreadsheetTextStyle.NO_BOLD, SpreadsheetTextStyle.ITALICS, SpreadsheetTextStyle.NO_UNDERLINE),
                a,
                rules);
        this.saveRule(true,
                2,
                style(SpreadsheetTextStyle.NO_BOLD, SpreadsheetTextStyle.NO_ITALICS, SpreadsheetTextStyle.UNDERLINE),
                a,
                rules);
        this.saveRule(false,
                3,
                style(SpreadsheetTextStyle.BOLD, SpreadsheetTextStyle.ITALICS, SpreadsheetTextStyle.UNDERLINE),
                a,
                rules);

        cellStore.save(this.cell(a, "3+4"));

        final SpreadsheetCell cell = this.loadCellAndCheckFormatted2(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(3 + 4),
                DEFAULT_SUFFIX);
        final SpreadsheetCellStyle style = cell.formatted().get().style();
        // UNDERLINED from conditional formatting rule #2.
        assertEquals("Style should include underline if correct rule was applied=" + cell,
                style(SpreadsheetTextStyle.NO_BOLD, SpreadsheetTextStyle.NO_ITALICS, SpreadsheetTextStyle.UNDERLINE),
                style);
    }

    private void saveRule(final boolean result, final int priority, final SpreadsheetCellStyle style,
                          final SpreadsheetCellReference cell, final SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> rules) {
        rules.saveValue(SpreadsheetRange.from(Lists.of(cell)), rule(result, priority, style));
    }

    private SpreadsheetConditionalFormattingRule rule(final boolean result,
                                                      final int priority,
                                                      final SpreadsheetCellStyle style) {


        return SpreadsheetConditionalFormattingRule.with(SpreadsheetDescription.with(priority + "=" + result),
                priority,
                SpreadsheetFormula.with(String.valueOf(result)).setExpression(Optional.of(ExpressionNode.booleanNode(result))),
                (c) -> style);
    }

    // deleteColumn....................................................................................................

    @Test
    public void testDeleteColumnZero() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference reference = this.cellReference(99, 0); // A3

        cellStore.save(this.cell(reference, "99+0"));

        engine.deleteColumns(reference.column(), 0, context);
        this.countAndCheck(cellStore, 1);

        this.loadCellAndCheckFormulaAndValue(engine,
                reference,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "99+0",
                BigDecimal.valueOf(99));
    }

    @Test
    public void testDeleteColumn() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // A2
        final SpreadsheetCellReference c = this.cellReference(2, 0); // A3

        cellStore.save(this.cell(a, "1+2"));
        cellStore.save(this.cell(b, "3+4"));
        cellStore.save(this.cell(c, "5+6"));

        engine.deleteColumns(b.column(), 1, context);
        this.countAndCheck(cellStore, 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+2",
                BigDecimal.valueOf(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "5+6",
                BigDecimal.valueOf(5 + 6));
    }

    @Test
    public void testDeleteColumn2() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // A2 replaced by c
        final SpreadsheetCellReference c = this.cellReference(2, 0); // A3 DELETED
        final SpreadsheetCellReference d = this.cellReference(99, 1); // B99 moved

        cellStore.save(this.cell(a, "1+2"));
        cellStore.save(this.cell(b, "3+4"));
        cellStore.save(this.cell(c, "5+6"));
        cellStore.save(this.cell(d, "7+8"));

        final int count = 1;
        engine.deleteColumns(b.column(), count, context); // $b delete, $c,$d columns -1.
        this.countAndCheck(cellStore, 3);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+2",
                BigDecimal.valueOf(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "5+6",
                BigDecimal.valueOf(5 + 6));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(-count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "7+8",
                BigDecimal.valueOf(7 + 8));
    }

    @Test
    public void testDeleteColumnWithLabelsToCellReferenceIgnored() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(5, 1); // E2

        cellStore.save(this.cell(a, "99+0"));
        cellStore.save(this.cell(b, "2+0+" + LABEL));

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, a));

        final int count = 1;
        engine.deleteColumns(b.column().add(-1), count, context); // delete column before $b

        this.loadLabelAndCheck(labelStore, LABEL, a);

        this.countAndCheck(cellStore, 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "99+0",
                BigDecimal.valueOf(99 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(-1, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "2+0+" + LABEL,
                BigDecimal.valueOf(2 + 99));
    }

    @Test
    public void testDeleteColumnWithLabelsToCellReferencedFixed() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // A2 replaced by c
        final SpreadsheetCellReference c = this.cellReference(2, 0); // A3 DELETED
        final SpreadsheetCellReference d = this.cellReference(13, 8); // B8 moved
        final SpreadsheetCellReference e = this.cellReference(14, 9); // C9 moved LABEL=

        cellStore.save(this.cell(a, "1+" + LABEL));
        cellStore.save(this.cell(b, "2+0"));
        cellStore.save(this.cell(c, "3+0"));
        cellStore.save(this.cell(d, "4+" + LABEL));
        cellStore.save(this.cell(e, "99+0")); // LABEL=

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, e));

        final int count = 1;
        engine.deleteColumns(b.column(), count, context); // $b delete, $c,$d columns -1.

        this.loadLabelAndCheck(labelStore, LABEL, e.add(-count, 0));

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+" + LABEL,
                BigDecimal.valueOf(1 + 99));

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "3+0",
                BigDecimal.valueOf(3));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(-count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "4+" + LABEL,
                BigDecimal.valueOf(4 + 99));

        this.loadCellAndCheckFormulaAndValue(engine,
                e.add(-count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "99+0",
                BigDecimal.valueOf(99 + 0));
    }

    @Test
    public void testDeleteColumnWithLabelToDeletedCell() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // A2

        cellStore.save(this.cell(a, "1+0"));
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, b));

        final int count = 1;
        engine.deleteColumns(b.column(), count, context); // $b delete, $c,$d columns -1.

        this.loadLabelFailCheck(labelStore, LABEL);

        this.countAndCheck(cellStore, 1);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+0",
                BigDecimal.valueOf(1 + 0));
    }

    @Test
    public void testDeleteColumnWithCellReferences() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // A2
        final SpreadsheetCellReference c = this.cellReference(10, 0); // A10 deleted
        final SpreadsheetCellReference d = this.cellReference(13, 8); // H13 moved
        final SpreadsheetCellReference e = this.cellReference(14, 9); // I14 moved

        cellStore.save(this.cell(a, "1+" + d));
        cellStore.save(this.cell(b, "2"));
        cellStore.save(this.cell(c, "3"));
        cellStore.save(this.cell(d, "4"));
        cellStore.save(this.cell(e, "5+" + b)); // =5+2

        final int count = 1;
        engine.deleteColumns(c.column(), count, context); // $c delete

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+" + d.add(-count, 0),
                BigDecimal.valueOf(1 + 4)); // reference should have been fixed.

        this.loadCellAndCheckFormatted2(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(2),
                DEFAULT_SUFFIX);

        this.loadCellAndCheckFormatted2(engine,
                d.add(-count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(4),
                DEFAULT_SUFFIX);

        this.loadCellAndCheckFormulaAndValue(engine,
                e.add(-count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "5+" + b,
                BigDecimal.valueOf(5 + 2));
    }

    @Test
    public void testDeleteColumnWithCellReferences2() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // A2
        final SpreadsheetCellReference c = this.cellReference(10, 0); // A10 deleted
        final SpreadsheetCellReference d = this.cellReference(13, 8); // H13 moved
        final SpreadsheetCellReference e = this.cellReference(14, 9); // I14 moved

        cellStore.save(this.cell(a, "1+" + d));
        cellStore.save(this.cell(b, "2"));
        cellStore.save(this.cell(c, "3"));
        cellStore.save(this.cell(d, "4"));
        cellStore.save(this.cell(e, "5+" + b)); // =5+2

        final int count = 2;
        engine.deleteColumns(c.column(), count, context); // $c delete

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+" + d.add(-count, 0),
                BigDecimal.valueOf(1 + 4)); // reference should have been fixed.

        this.loadCellAndCheckFormatted2(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(2),
                DEFAULT_SUFFIX);

        this.loadCellAndCheckFormatted2(engine,
                d.add(-count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(4),
                DEFAULT_SUFFIX);

        this.loadCellAndCheckFormulaAndValue(engine,
                e.add(-count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "5+" + b,
                BigDecimal.valueOf(5 + 2));
    }

    @Test
    public void testDeleteColumnWithCellReferencesToDeletedCell() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // A2

        cellStore.save(this.cell(a, "1+" + b));
        cellStore.save(this.cell(b, "2"));

        final int count = 1;
        engine.deleteColumns(b.column(), count, context); // $c delete

        this.countAndCheck(cellStore, 1);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+InvalidCellReference(" + b + ")",
                "Invalid cell reference " + b); // reference should have been fixed.
    }

    @Test
    public void testDeleteColumnSeveral() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(10, 0); // A2 DELETED
        final SpreadsheetCellReference c = this.cellReference(11, 0); // A3 DELETED
        final SpreadsheetCellReference d = this.cellReference(12, 2); // C4
        final SpreadsheetCellReference e = this.cellReference(20, 3); // T3
        final SpreadsheetCellReference f = this.cellReference(21, 4); // U4

        cellStore.save(this.cell(a, "1"));
        cellStore.save(this.cell(b, "2"));
        cellStore.save(this.cell(c, "3"));
        cellStore.save(this.cell(d, "4"));
        cellStore.save(this.cell(e, "5"));
        cellStore.save(this.cell(f, "6"));

        final int count = 5;
        engine.deleteColumns(this.column(7), count, context); // $b & $c

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckFormatted2(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(1),
                DEFAULT_SUFFIX);

        this.loadCellAndCheckFormatted2(engine,
                d.add(-count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(4),
                DEFAULT_SUFFIX);

        this.loadCellAndCheckFormatted2(engine,
                e.add(-count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(5),
                DEFAULT_SUFFIX);

        this.loadCellAndCheckFormatted2(engine,
                f.add(-count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(6),
                DEFAULT_SUFFIX);
    }

    // deleteRow....................................................................................................

    @Test
    public void testDeleteRowsNone() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference reference = this.cellReference(0, 1); // A2

        cellStore.save(this.cell(reference, "99+0"));

        engine.deleteRows(reference.row(), 0, context);

        this.countAndCheck(cellStore, 1);

        this.loadCellAndCheckFormulaAndValue(engine,
                reference,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "99+0",
                BigDecimal.valueOf(99));
    }

    @Test
    public void testDeleteRowsOne() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(0, 1); // B1
        final SpreadsheetCellReference c = this.cellReference(0, 2); // C1

        cellStore.save(this.cell(a, "1+2"));
        cellStore.save(this.cell(b, "3+4"));
        cellStore.save(this.cell(c, "5+6"));

        engine.deleteRows(b.row(), 1, context);

        this.countAndCheck(cellStore, 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+2",
                BigDecimal.valueOf(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "5+6",
                BigDecimal.valueOf(5 + 6));
    }

    @Test
    public void testDeleteRowsOne2() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); //
        final SpreadsheetCellReference b = this.cellReference(0, 1); // replaced by c
        final SpreadsheetCellReference c = this.cellReference(0, 2); // DELETED
        final SpreadsheetCellReference d = this.cellReference(1, 9); // moved

        cellStore.save(this.cell(a, "1+2"));
        cellStore.save(this.cell(b, "3+4"));
        cellStore.save(this.cell(c, "5+6"));
        cellStore.save(this.cell(d, "7+8"));

        final int count = 1;
        engine.deleteRows(b.row(), count, context); // $b delete

        this.countAndCheck(cellStore, 3);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+2",
                BigDecimal.valueOf(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "5+6",
                BigDecimal.valueOf(5 + 6));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(0, -count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "7+8",
                BigDecimal.valueOf(7 + 8));
    }

    @Test
    public void testDeleteRowsMany() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); //
        final SpreadsheetCellReference b = this.cellReference(0, 1); // replaced by c
        final SpreadsheetCellReference c = this.cellReference(0, 2); // DELETED
        final SpreadsheetCellReference d = this.cellReference(1, 9); // moved

        cellStore.save(this.cell(a, "1+2"));
        cellStore.save(this.cell(b, "3+4"));
        cellStore.save(this.cell(c, "5+6"));
        cellStore.save(this.cell(d, "7+8"));

        final int count = 2;
        engine.deleteRows(b.row(), count, context); // $b, $c deleted

        this.countAndCheck(cellStore, 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+2",
                BigDecimal.valueOf(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(0, -count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "7+8",
                BigDecimal.valueOf(7 + 8));
    }

    // delete row with labels to cell references..................................................................

    @Test
    public void testDeleteRowsWithLabelsToCellUnmodified() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(0, 1); // B1
        final SpreadsheetCellReference c = this.cellReference(0, 5); // E1

        cellStore.save(this.cell(a, "1+0"));
        cellStore.save(this.cell(b, "20+0+" + LABEL));
        cellStore.save(this.cell(c, "99+0"));

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, a));

        final int count = 2;
        engine.deleteRows(b.row().add(2), count, context); // $c moved, $b unmodified label refs $a also unmodified.

        this.countAndCheck(cellStore, 3);

        this.loadLabelAndCheck(labelStore, LABEL, a);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+0",
                BigDecimal.valueOf(1));

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "20+0+" + LABEL,
                BigDecimal.valueOf(21));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(0, -count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "99+0",
                BigDecimal.valueOf(99));
    }

    @Test
    public void testDeleteRowsWithLabelsToCellFixed() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(0, 5); // B1

        cellStore.save(this.cell(a, "1+0+" + LABEL));
        cellStore.save(this.cell(b, "2+0"));

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, b));

        final int count = 2;
        engine.deleteRows(a.row().add(1), count, context); // $b moved

        this.countAndCheck(cellStore, 2);

        this.loadLabelAndCheck(labelStore, LABEL, b.add(0, -count));

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+0+" + LABEL,
                BigDecimal.valueOf(3));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(0, -count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "2+0",
                BigDecimal.valueOf(2));
    }

    @Test
    public void testDeleteRowsWithLabelToCellReferenceDeleted() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(0, 5); // B1

        cellStore.save(this.cell(a, "1+" + b));
        cellStore.save(this.cell(b, "2+0"));

        engine.deleteRows(b.row(), 1, context); // $v delete

        this.countAndCheck(cellStore, 1);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+InvalidCellReference(" + b + ")",
                "Invalid cell reference " + b); // reference should have been fixed.
    }

    @Test
    public void testDeleteRowsWithCellReferencesFixed() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(0, 1); // B1
        final SpreadsheetCellReference c = this.cellReference(0, 10); // A10 deleted
        final SpreadsheetCellReference d = this.cellReference(8, 13); // H13 moved
        final SpreadsheetCellReference e = this.cellReference(9, 14); // I14 moved

        cellStore.save(this.cell(a, "1+" + d));
        cellStore.save(this.cell(b, "2"));
        cellStore.save(this.cell(c, "3"));
        cellStore.save(this.cell(d, "4"));
        cellStore.save(this.cell(e, "5+" + b)); // =5+2

        final int count = 1;
        engine.deleteRows(c.row(), count, context); // $c delete

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+" + d.add(0, -count),
                BigDecimal.valueOf(1 + 4)); // reference should have been fixed.

        this.loadCellAndCheckFormatted2(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(2),
                DEFAULT_SUFFIX);

        this.loadCellAndCheckFormatted2(engine,
                d.add(0, -count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(4),
                DEFAULT_SUFFIX);

        this.loadCellAndCheckFormulaAndValue(engine,
                e.add(0, -count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "5+" + b,
                BigDecimal.valueOf(5 + 2));
    }

    @Test
    public void testDeleteRowsWithCellReferencesFixed2() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(0, 1); // B1
        final SpreadsheetCellReference c = this.cellReference(0, 10); // J1 deleted
        final SpreadsheetCellReference d = this.cellReference(8, 13); // M8 moved
        final SpreadsheetCellReference e = this.cellReference(9, 14); // N9 moved

        cellStore.save(this.cell(a, "1+" + d));
        cellStore.save(this.cell(b, "2"));
        cellStore.save(this.cell(c, "3"));
        cellStore.save(this.cell(d, "4"));
        cellStore.save(this.cell(e, "5+" + b)); // =5+2

        final int count = 2;
        engine.deleteRows(c.row(), count, context); // $c delete

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+" + d.add(0, -count),
                BigDecimal.valueOf(1 + 4)); // reference should have been fixed.

        this.loadCellAndCheckFormatted2(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(2),
                DEFAULT_SUFFIX);

        this.loadCellAndCheckFormatted2(engine,
                d.add(0, -count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(4),
                DEFAULT_SUFFIX);

        this.loadCellAndCheckFormulaAndValue(engine,
                e.add(0, -count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "5+" + b,
                BigDecimal.valueOf(5 + 2));
    }

    // delete range....................................................................................

    @Test
    public void testDeleteRowsWithLabelsToRangeUnmodified() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0);
        final SpreadsheetCellReference b = this.cellReference(0, 5);
        final SpreadsheetCellReference c = this.cellReference(0, 10);
        final SpreadsheetCellReference d = this.cellReference(0, 15);

        cellStore.save(this.cell(a, "1+0"));
        cellStore.save(this.cell(c, "20+0+" + LABEL));
        cellStore.save(this.cell(d, "99+0")); // deleted!!!

        final SpreadsheetRange ab = range(a, b);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, ab));

        final int count = 2;
        engine.deleteRows(d.row(), count, context); // $d moved

        this.countAndCheck(cellStore, 2); // a&c
        this.countAndCheck(labelStore, 1);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+0",
                BigDecimal.valueOf(1));

        this.loadCellAndCheckFormulaAndValue(engine,
                c,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "20+0+" + LABEL,
                BigDecimal.valueOf(21));

        this.loadLabelAndCheck(labelStore, LABEL, ab);
    }

    @Test
    public void testDeleteRowsWithLabelsToRangeDeleted() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0);
        final SpreadsheetCellReference b = this.cellReference(0, 5);
        final SpreadsheetCellReference c = this.cellReference(0, 10);

        cellStore.save(this.cell(a, "1+0"));

        final SpreadsheetRange bc = range(b, c);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, bc));

        final int count = c.row().value() - b.row().value() + 1;
        engine.deleteRows(b.row(), count, context); // b..c deleted, d moved

        this.countAndCheck(cellStore, 1); // a
        this.countAndCheck(labelStore, 0);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+0",
                BigDecimal.valueOf(1));
    }

    @Test
    public void testDeleteRowsWithLabelsToRangeDeleted2() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0);
        final SpreadsheetCellReference b = this.cellReference(0, 5);
        final SpreadsheetCellReference c = this.cellReference(0, 10);
        final SpreadsheetCellReference d = this.cellReference(0, 20);

        cellStore.save(this.cell(a, "1+0"));
        cellStore.save(this.cell(d, "20+0"));

        final SpreadsheetRange bc = range(b, c);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, bc));

        final int count = c.row().value() - b.row().value() + 1;
        engine.deleteRows(b.row(), count, context); // b..c deleted, d moved

        this.countAndCheck(cellStore, 2); // a&d
        this.countAndCheck(labelStore, 0);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+0",
                BigDecimal.valueOf(1));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(0, -count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "20+0",
                BigDecimal.valueOf(20));
    }

    @Test
    public void testDeleteRowsWithLabelsToRangeDeleted3() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0);
        final SpreadsheetCellReference b = this.cellReference(0, 5);
        final SpreadsheetCellReference c = this.cellReference(0, 10);

        cellStore.save(this.cell(a, "1+0+" + LABEL));
        cellStore.save(this.cell(b, "20+0"));

        final SpreadsheetRange bc = range(b, c);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, bc));

        final int count = c.row().value() - b.row().value() + 1;
        engine.deleteRows(b.row(), count, context); // b..c deleted

        this.countAndCheck(cellStore, 1); // a
        this.countAndCheck(labelStore, 0);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+0+" + LABEL,
                "Unknown label " + LABEL);
    }

    @Test
    public void testDeleteRowsWithLabelsToRangeFixed() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0);
        final SpreadsheetCellReference b = this.cellReference(0, 5);
        final SpreadsheetCellReference c = this.cellReference(0, 10);
        final SpreadsheetCellReference d = this.cellReference(0, 15);
        final SpreadsheetCellReference e = this.cellReference(0, 20);

        cellStore.save(this.cell(a, "1+0+" + LABEL));
        cellStore.save(this.cell(d, "20+0"));

        final SpreadsheetRange de = range(d, e);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, de));

        final int count = c.row().value() - b.row().value() + 1;
        engine.deleteRows(b.row(), count, context); // b..c deleted, d moved

        this.countAndCheck(cellStore, 2); // a&d

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+0+" + LABEL,
                BigDecimal.valueOf(1 + 20));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(0, -count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "20+0",
                BigDecimal.valueOf(20));

        this.countAndCheck(labelStore, 1);
        this.loadLabelAndCheck(labelStore, LABEL, range(d.add(0, -count), e.add(0, -count)));
    }

    @Test
    public void testDeleteRowsWithLabelsToRangeFixed2() {
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0);
        final SpreadsheetCellReference b = this.cellReference(0, 5);
        final SpreadsheetCellReference c = this.cellReference(0, 10);
        final SpreadsheetCellReference d = this.cellReference(0, 15);
        final SpreadsheetCellReference e = this.cellReference(0, 20);

        final SpreadsheetRange ce = range(c, e);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, ce));

        final int count = e.row().value() - d.row().value() + 1;
        engine.deleteRows(d.row(), count, context); // b..c deleted, d moved

        this.countAndCheck(labelStore, 1);
        this.loadLabelAndCheck(labelStore, LABEL, range(c, d));
    }

    @Test
    public void testDeleteRowsWithLabelsToRangeFixed3() {
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference b = this.cellReference(0, 5);
        final SpreadsheetCellReference c = this.cellReference(0, 10);
        final SpreadsheetCellReference d = this.cellReference(0, 15);

        final SpreadsheetRange bd = range(b, d);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, bd));

        final int count = 1;
        engine.deleteRows(c.row(), count, context); // b..c deleted, d moved

        this.countAndCheck(labelStore, 1);

        this.loadLabelAndCheck(labelStore, LABEL, range(b, d.add(0, -count)));
    }

    @Test
    public void testDeleteRowsWithLabelsToRangeFixed4() {
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 5);  // delete
        final SpreadsheetCellReference b = this.cellReference(0, 10); // range delete
        final SpreadsheetCellReference c = this.cellReference(0, 15); // range delete
        final SpreadsheetCellReference d = this.cellReference(0, 20); // range
        final SpreadsheetCellReference e = this.cellReference(0, 25); // range

        final SpreadsheetRange be = range(b, e);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, be));

        final int count = c.row().value() - a.row().value();
        engine.deleteRows(a.row(), count, context);

        this.countAndCheck(labelStore, 1);

        this.loadLabelAndCheck(labelStore, LABEL, range(a, b));
    }

    // deleteColumn....................................................................................................

    @Test
    public void testDeleteColumnsNone() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference reference = this.cellReference(1, 0); // A2

        cellStore.save(this.cell(reference, "99+0"));

        engine.deleteColumns(reference.column(), 0, context);

        this.countAndCheck(cellStore, 1);

        this.loadCellAndCheckFormulaAndValue(engine,
                reference,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "99+0",
                BigDecimal.valueOf(99));
    }

    @Test
    public void testDeleteColumnsOne() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // B1
        final SpreadsheetCellReference c = this.cellReference(2, 0); // C1

        cellStore.save(this.cell(a, "1+2"));
        cellStore.save(this.cell(b, "3+4"));
        cellStore.save(this.cell(c, "5+6"));

        engine.deleteColumns(b.column(), 1, context);

        this.countAndCheck(cellStore, 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+2",
                BigDecimal.valueOf(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "5+6",
                BigDecimal.valueOf(5 + 6));
    }

    @Test
    public void testDeleteColumnsOne2() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); //
        final SpreadsheetCellReference b = this.cellReference(1, 0); // replaced by c
        final SpreadsheetCellReference c = this.cellReference(2, 0); // DELETED
        final SpreadsheetCellReference d = this.cellReference(9, 1); // moved

        cellStore.save(this.cell(a, "1+2"));
        cellStore.save(this.cell(b, "3+4"));
        cellStore.save(this.cell(c, "5+6"));
        cellStore.save(this.cell(d, "7+8"));

        final int count = 1;
        engine.deleteColumns(b.column(), count, context); // $b delete

        this.countAndCheck(cellStore, 3);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+2",
                BigDecimal.valueOf(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "5+6",
                BigDecimal.valueOf(5 + 6));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(-count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "7+8",
                BigDecimal.valueOf(7 + 8));
    }

    @Test
    public void testDeleteColumnsMany() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); //
        final SpreadsheetCellReference b = this.cellReference(1, 0); // replaced by c
        final SpreadsheetCellReference c = this.cellReference(2, 0); // DELETED
        final SpreadsheetCellReference d = this.cellReference(9, 1); // moved

        cellStore.save(this.cell(a, "1+2"));
        cellStore.save(this.cell(b, "3+4"));
        cellStore.save(this.cell(c, "5+6"));
        cellStore.save(this.cell(d, "7+8"));

        final int count = 2;
        engine.deleteColumns(b.column(), count, context); // $b, $c deleted

        this.countAndCheck(cellStore, 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+2",
                BigDecimal.valueOf(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(-count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "7+8",
                BigDecimal.valueOf(7 + 8));
    }

    // delete column with labels to cell references..................................................................

    @Test
    public void testDeleteColumnsWithLabelsToCellUnmodified() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // B1
        final SpreadsheetCellReference c = this.cellReference(5, 0); // E1

        cellStore.save(this.cell(a, "1+0"));
        cellStore.save(this.cell(b, "20+0+" + LABEL));
        cellStore.save(this.cell(c, "99+0"));

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, a));

        final int count = 2;
        engine.deleteColumns(b.column().add(2), count, context); // $c moved, $b unmodified label refs $a also unmodified.

        this.countAndCheck(cellStore, 3);

        this.loadLabelAndCheck(labelStore, LABEL, a);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+0",
                BigDecimal.valueOf(1));

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "20+0+" + LABEL,
                BigDecimal.valueOf(21));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(-count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "99+0",
                BigDecimal.valueOf(99));
    }

    @Test
    public void testDeleteColumnsWithLabelsToCellFixed() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(5, 0); // B1

        cellStore.save(this.cell(a, "1+0+" + LABEL));
        cellStore.save(this.cell(b, "2+0"));

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, b));

        final int count = 2;
        engine.deleteColumns(a.column().add(1), count, context); // $b moved

        this.countAndCheck(cellStore, 2);

        this.loadLabelAndCheck(labelStore, LABEL, b.add(-count, 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+0+" + LABEL,
                BigDecimal.valueOf(3));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(-count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "2+0",
                BigDecimal.valueOf(2));
    }

    @Test
    public void testDeleteColumnsWithLabelToCellReferenceDeleted() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(5, 0); // B1

        cellStore.save(this.cell(a, "1+" + b));
        cellStore.save(this.cell(b, "2+0"));

        engine.deleteColumns(b.column(), 1, context); // $v delete

        this.countAndCheck(cellStore, 1);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+InvalidCellReference(" + b + ")",
                "Invalid cell reference " + b); // reference should have been fixed.
    }

    @Test
    public void testDeleteColumnsWithCellReferencesFixed() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // B1
        final SpreadsheetCellReference c = this.cellReference(10, 0); // A10 deleted
        final SpreadsheetCellReference d = this.cellReference(13, 8); // H13 moved
        final SpreadsheetCellReference e = this.cellReference(14, 9); // I14 moved

        cellStore.save(this.cell(a, "1+" + d));
        cellStore.save(this.cell(b, "2"));
        cellStore.save(this.cell(c, "3"));
        cellStore.save(this.cell(d, "4"));
        cellStore.save(this.cell(e, "5+" + b)); // =5+2

        final int count = 1;
        engine.deleteColumns(c.column(), count, context); // $c delete

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+" + d.add(-count, 0),
                BigDecimal.valueOf(1 + 4)); // reference should have been fixed.

        this.loadCellAndCheckFormatted2(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(2),
                DEFAULT_SUFFIX);

        this.loadCellAndCheckFormatted2(engine,
                d.add(-count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(4),
                DEFAULT_SUFFIX);

        this.loadCellAndCheckFormulaAndValue(engine,
                e.add(-count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "5+" + b,
                BigDecimal.valueOf(5 + 2));
    }

    @Test
    public void testDeleteColumnsWithCellReferencesFixed2() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // B1
        final SpreadsheetCellReference c = this.cellReference(10, 0); // J1 deleted
        final SpreadsheetCellReference d = this.cellReference(13, 8); // M8 moved
        final SpreadsheetCellReference e = this.cellReference(14, 9); // N9 moved

        cellStore.save(this.cell(a, "1+" + d));
        cellStore.save(this.cell(b, "2"));
        cellStore.save(this.cell(c, "3"));
        cellStore.save(this.cell(d, "4"));
        cellStore.save(this.cell(e, "5+" + b)); // =5+2

        final int count = 2;
        engine.deleteColumns(c.column(), count, context); // $c delete

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+" + d.add(-count, 0),
                BigDecimal.valueOf(1 + 4)); // reference should have been fixed.

        this.loadCellAndCheckFormatted2(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(2),
                DEFAULT_SUFFIX);

        this.loadCellAndCheckFormatted2(engine,
                d.add(-count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(4),
                DEFAULT_SUFFIX);

        this.loadCellAndCheckFormulaAndValue(engine,
                e.add(-count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "5+" + b,
                BigDecimal.valueOf(5 + 2));
    }

    // delete range....................................................................................

    @Test
    public void testDeleteColumnsWithLabelsToRangeUnmodified() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0);
        final SpreadsheetCellReference b = this.cellReference(5, 0);
        final SpreadsheetCellReference c = this.cellReference(10, 0);
        final SpreadsheetCellReference d = this.cellReference(15, 0);

        cellStore.save(this.cell(a, "1+0"));
        cellStore.save(this.cell(c, "20+0+" + LABEL));
        cellStore.save(this.cell(d, "99+0")); // deleted!!!

        final SpreadsheetRange ab = range(a, b);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, ab));

        final int count = 2;
        engine.deleteColumns(d.column(), count, context); // $d moved

        this.countAndCheck(cellStore, 2); // a&c
        this.countAndCheck(labelStore, 1);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+0",
                BigDecimal.valueOf(1));

        this.loadCellAndCheckFormulaAndValue(engine,
                c,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "20+0+" + LABEL,
                BigDecimal.valueOf(21));

        this.loadLabelAndCheck(labelStore, LABEL, ab);
    }

    @Test
    public void testDeleteColumnsWithLabelsToRangeDeleted() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0);
        final SpreadsheetCellReference b = this.cellReference(5, 0);
        final SpreadsheetCellReference c = this.cellReference(10, 0);

        cellStore.save(this.cell(a, "1+0"));

        final SpreadsheetRange bc = range(b, c);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, bc));

        final int count = c.column().value() - b.column().value() + 1;
        engine.deleteColumns(b.column(), count, context); // b..c deleted, d moved

        this.countAndCheck(cellStore, 1); // a
        this.countAndCheck(labelStore, 0);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+0",
                BigDecimal.valueOf(1));
    }

    @Test
    public void testDeleteColumnsWithLabelsToRangeDeleted2() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0);
        final SpreadsheetCellReference b = this.cellReference(5, 0);
        final SpreadsheetCellReference c = this.cellReference(10, 0);
        final SpreadsheetCellReference d = this.cellReference(20, 0);

        cellStore.save(this.cell(a, "1+0"));
        cellStore.save(this.cell(d, "20+0"));

        final SpreadsheetRange bc = range(b, c);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, bc));

        final int count = c.column().value() - b.column().value() + 1;
        engine.deleteColumns(b.column(), count, context); // b..c deleted, d moved

        this.countAndCheck(cellStore, 2); // a&d
        this.countAndCheck(labelStore, 0);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+0",
                BigDecimal.valueOf(1));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(-count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "20+0",
                BigDecimal.valueOf(20));
    }

    @Test
    public void testDeleteColumnsWithLabelsToRangeDeleted3() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0);
        final SpreadsheetCellReference b = this.cellReference(5, 0);
        final SpreadsheetCellReference c = this.cellReference(10, 0);

        cellStore.save(this.cell(a, "1+0+" + LABEL));
        cellStore.save(this.cell(b, "20+0"));

        final SpreadsheetRange bc = range(b, c);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, bc));

        final int count = c.column().value() - b.column().value() + 1;
        engine.deleteColumns(b.column(), count, context); // b..c deleted

        this.countAndCheck(cellStore, 1); // a
        this.countAndCheck(labelStore, 0);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+0+" + LABEL,
                "Unknown label " + LABEL);
    }

    @Test
    public void testDeleteColumnsWithLabelsToRangeFixed() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0);
        final SpreadsheetCellReference b = this.cellReference(5, 0);
        final SpreadsheetCellReference c = this.cellReference(10, 0);
        final SpreadsheetCellReference d = this.cellReference(15, 0);
        final SpreadsheetCellReference e = this.cellReference(20, 0);

        cellStore.save(this.cell(a, "1+0+" + LABEL));
        cellStore.save(this.cell(d, "20+0"));

        final SpreadsheetRange de = range(d, e);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, de));

        final int count = c.column().value() - b.column().value() + 1;
        engine.deleteColumns(b.column(), count, context); // b..c deleted, d moved

        this.countAndCheck(cellStore, 2); // a&d

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+0+" + LABEL,
                BigDecimal.valueOf(1 + 20));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(-count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "20+0",
                BigDecimal.valueOf(20));

        this.countAndCheck(labelStore, 1);
        this.loadLabelAndCheck(labelStore, LABEL, range(d.add(-count, 0), e.add(-count, 0)));
    }

    @Test
    public void testDeleteColumnsWithLabelsToRangeFixed2() {
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0);
        final SpreadsheetCellReference b = this.cellReference(5, 0);
        final SpreadsheetCellReference c = this.cellReference(10, 0);
        final SpreadsheetCellReference d = this.cellReference(15, 0);
        final SpreadsheetCellReference e = this.cellReference(20, 0);

        final SpreadsheetRange ce = range(c, e);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, ce));

        final int count = e.column().value() - d.column().value() + 1;
        engine.deleteColumns(d.column(), count, context); // b..c deleted, d moved

        this.countAndCheck(labelStore, 1);
        this.loadLabelAndCheck(labelStore, LABEL, range(c, d));
    }

    @Test
    public void testDeleteColumnsWithLabelsToRangeFixed3() {
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference b = this.cellReference(5, 0);
        final SpreadsheetCellReference c = this.cellReference(10, 0);
        final SpreadsheetCellReference d = this.cellReference(15, 0);

        final SpreadsheetRange bd = range(b, d);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, bd));

        final int count = 1;
        engine.deleteColumns(c.column(), count, context); // b..c deleted, d moved

        this.countAndCheck(labelStore, 1);

        this.loadLabelAndCheck(labelStore, LABEL, range(b, d.add(-count, 0)));
    }

    @Test
    public void testDeleteColumnsWithLabelsToRangeFixed4() {
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(5, 0);  // delete
        final SpreadsheetCellReference b = this.cellReference(10, 0); // range delete
        final SpreadsheetCellReference c = this.cellReference(15, 0); // range delete
        final SpreadsheetCellReference d = this.cellReference(20, 0); // range
        final SpreadsheetCellReference e = this.cellReference(25, 0); // range

        final SpreadsheetRange be = range(b, e);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, be));

        final int count = c.column().value() - a.column().value();
        engine.deleteColumns(a.column(), count, context);

        this.countAndCheck(labelStore, 1);

        this.loadLabelAndCheck(labelStore, LABEL, range(a, b));
    }

    // insertColumn....................................................................................................

    @Test
    public void testInsertColumnsZero() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference reference = this.cellReference(99, 0); // A3

        cellStore.save(this.cell(reference, "99+0"));

        engine.insertColumns(reference.column(), 0, context);

        this.countAndCheck(cellStore, 1);

        this.loadCellAndCheckFormulaAndValue(engine,
                reference,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "99+0",
                BigDecimal.valueOf(99));
    }

    @Test
    public void testInsertColumns() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // A2
        final SpreadsheetCellReference c = this.cellReference(2, 0); // A3

        cellStore.save(this.cell(a, "1+2"));
        cellStore.save(this.cell(b, "3+4"));
        cellStore.save(this.cell(c, "5+6"));

        final int count = 1;
        engine.insertColumns(b.column(), count, context);

        this.countAndCheck(cellStore, 3);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+2",
                BigDecimal.valueOf(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "3+4",
                BigDecimal.valueOf(3 + 4));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "5+6",
                BigDecimal.valueOf(5 + 6));
    }

    @Test
    public void testInsertColumns2() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // A2 MOVED
        final SpreadsheetCellReference c = this.cellReference(2, 0); // A3 MOVED

        cellStore.save(this.cell(a, "1+2"));
        cellStore.save(this.cell(b, "3+4"));
        cellStore.save(this.cell(c, "5+6"));

        final int count = 1;
        engine.insertColumns(b.column(), count, context); // $b insert

        this.countAndCheck(cellStore, 3);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+2",
                BigDecimal.valueOf(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "3+4",
                BigDecimal.valueOf(3 + 4));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "5+6",
                BigDecimal.valueOf(5 + 6));
    }

    @Test
    public void testInsertColumnsWithLabelToCellIgnored() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(2, 1); // A1
        final SpreadsheetCellReference b = this.cellReference(4, 3); // A2 moved

        cellStore.save(this.cell(a, "100"));
        cellStore.save(this.cell(b, "2+" + LABEL));

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, a));

        final int count = 1;
        engine.insertColumns(b.column(), count, context); // $b insert

        this.loadLabelAndCheck(labelStore, LABEL, a);

        this.countAndCheck(cellStore, 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "100",
                BigDecimal.valueOf(100));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "2+" + LABEL,
                BigDecimal.valueOf(2 + 100));
    }

    @Test
    public void testInsertColumnsWithLabelToCell() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // A2 moved
        final SpreadsheetCellReference c = this.cellReference(2, 0); // A3 MOVED
        final SpreadsheetCellReference d = this.cellReference(13, 8); // B8 moved

        cellStore.save(this.cell(a, "1+" + LABEL));
        cellStore.save(this.cell(b, "2+0"));
        cellStore.save(this.cell(c, "3+0+" + LABEL));
        cellStore.save(this.cell(d, "99+0"));

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, d));

        final int count = 1;
        engine.insertColumns(b.column(), count, context); // $b insert

        this.loadLabelAndCheck(labelStore, LABEL, d.add(count, 0));

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+" + LABEL,
                BigDecimal.valueOf(1 + 99));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "2+0",
                BigDecimal.valueOf(2 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "3+0+" + LABEL,
                BigDecimal.valueOf(3 + 99));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "99+0",
                BigDecimal.valueOf(99 + 0));
    }

    @Test
    public void testInsertColumnsWithLabelToRangeUnchanged() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(5, 5); // A2 moved

        cellStore.save(this.cell(a, "99+0"));
        cellStore.save(this.cell(b, "2+0+" + LABEL));

        final SpreadsheetRange a1 = SpreadsheetRange.with(a, a.add(1, 1));
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, a1));

        final int count = 1;
        engine.insertColumns(b.column(), count, context); // $b insert

        this.countAndCheck(labelStore, 1);

        this.loadLabelAndCheck(labelStore, LABEL, a1);

        this.countAndCheck(cellStore, 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "99+0",
                BigDecimal.valueOf(99 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "2+0+" + LABEL,
                BigDecimal.valueOf(2 + 99));
    }

    @Test
    public void testInsertColumnsWithLabelToRangeUpdated() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0);
        final SpreadsheetCellReference b = this.cellReference(5, 0);
        final SpreadsheetCellReference c = this.cellReference(10, 0);
        final SpreadsheetCellReference d = this.cellReference(15, 0);
        final SpreadsheetCellReference e = this.cellReference(20, 0);

        cellStore.save(this.cell(a, "1+" + LABEL));
        cellStore.save(this.cell(c, "99+0"));

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, SpreadsheetRange.with(c, d)));

        engine.insertColumns(b.column(), c.column().value() - b.column().value(), context); // $b insert

        this.countAndCheck(labelStore, 1);
        this.loadLabelAndCheck(labelStore, LABEL, SpreadsheetRange.with(d, e));

        this.countAndCheck(cellStore, 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+" + LABEL,
                BigDecimal.valueOf(1 + 99));

        this.loadCellAndCheckFormulaAndValue(engine,
                d,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "99+0",
                BigDecimal.valueOf(99 + 0));
    }

    @Test
    public void testInsertColumnsWithCellReferences() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // A2
        final SpreadsheetCellReference c = this.cellReference(10, 0); // A10 moved
        final SpreadsheetCellReference d = this.cellReference(13, 8); // H13 moved

        cellStore.save(this.cell(a, "1+0+" + d));
        cellStore.save(this.cell(b, "2+0"));
        cellStore.save(this.cell(c, "3+0"));
        cellStore.save(this.cell(d, "4+0+" + b));

        final int count = 1;
        engine.insertColumns(c.column(), count, context); // $c insert

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+0+" + d.add(count, 0),
                BigDecimal.valueOf(1 + 0 + 4 + 2)); // reference should have been fixed.

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "2+0",
                BigDecimal.valueOf(2));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "3+0",
                BigDecimal.valueOf(3));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "4+0+" + b,
                BigDecimal.valueOf(4 + 2));
    }

    @Test
    public void testInsertColumnsWithCellReferences2() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // A2
        final SpreadsheetCellReference c = this.cellReference(10, 0); // A10 moved
        final SpreadsheetCellReference d = this.cellReference(13, 8); // H13 moved

        cellStore.save(this.cell(a, "1+0+" + d));
        cellStore.save(this.cell(b, "2+0"));
        cellStore.save(this.cell(c, "3+0"));
        cellStore.save(this.cell(d, "4+0+" + b)); // =5+2

        final int count = 2;
        engine.insertColumns(c.column(), count, context); // $c insert

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+0+" + d.add(count, 0),
                BigDecimal.valueOf(1 + 0 + 4 + 2)); // reference should have been fixed.

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "2+0",
                BigDecimal.valueOf(2 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "3+0",
                BigDecimal.valueOf(3 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "4+0+" + b,
                BigDecimal.valueOf(4 + 0 + 2));
    }

    @Test
    public void testInsertColumnsSeveral() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); //
        final SpreadsheetCellReference b = this.cellReference(10, 0); // MOVED
        final SpreadsheetCellReference c = this.cellReference(11, 0); // MOVED
        final SpreadsheetCellReference d = this.cellReference(12, 2); // MOVED
        final SpreadsheetCellReference e = this.cellReference(20, 3); // MOVED

        cellStore.save(this.cell(a, "1+0"));
        cellStore.save(this.cell(b, "2+0"));
        cellStore.save(this.cell(c, "3+0"));
        cellStore.save(this.cell(d, "4+0"));
        cellStore.save(this.cell(e, "5+0"));

        final int count = 5;
        engine.insertColumns(this.column(7), count, context); // $b & $c

        this.countAndCheck(cellStore, 5);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+0",
                BigDecimal.valueOf(1 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "2+0",
                BigDecimal.valueOf(2 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "3+0",
                BigDecimal.valueOf(3 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "4+0",
                BigDecimal.valueOf(4 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                e.add(count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "5+0",
                BigDecimal.valueOf(5 + 0));
    }

    // insertRow....................................................................................................

    @Test
    public void testInsertRowsZero() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference reference = this.cellReference(0, 99); // A3

        cellStore.save(this.cell(reference, "99+0"));

        engine.insertRows(reference.row(), 0, context);

        this.countAndCheck(cellStore, 1);

        this.loadCellAndCheckFormulaAndValue(engine,
                reference,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "99+0",
                BigDecimal.valueOf(99));
    }

    @Test
    public void testInsertRows() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(0, 1); // A2
        final SpreadsheetCellReference c = this.cellReference(0, 2); // A3

        cellStore.save(this.cell(a, "1+2"));
        cellStore.save(this.cell(b, "3+4"));
        cellStore.save(this.cell(c, "5+6"));

        final int count = 1;
        engine.insertRows(b.row(), count, context);

        this.countAndCheck(cellStore, 3);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+2",
                BigDecimal.valueOf(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(0, count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "3+4",
                BigDecimal.valueOf(3 + 4));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(0, count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "5+6",
                BigDecimal.valueOf(5 + 6));
    }

    @Test
    public void testInsertRows2() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(0, 1); // A2 MOVED
        final SpreadsheetCellReference c = this.cellReference(0, 2); // A3 MOVED

        cellStore.save(this.cell(a, "1+2"));
        cellStore.save(this.cell(b, "3+4"));
        cellStore.save(this.cell(c, "5+6"));

        final int count = 1;
        engine.insertRows(b.row(), count, context); // $b insert

        this.countAndCheck(cellStore, 3);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+2",
                BigDecimal.valueOf(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(0, +count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "3+4",
                BigDecimal.valueOf(3 + 4));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(0, +count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "5+6",
                BigDecimal.valueOf(5 + 6));
    }

    @Test
    public void testInsertRowsWithLabelToCellIgnored() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(1, 2); // A1
        final SpreadsheetCellReference b = this.cellReference(3, 4); // A2 moved

        cellStore.save(this.cell(a, "100"));
        cellStore.save(this.cell(b, "2+" + LABEL));

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, a));

        final int count = 1;
        engine.insertRows(b.row(), count, context); // $b insert

        this.loadLabelAndCheck(labelStore, LABEL, a);

        this.countAndCheck(cellStore, 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "100",
                BigDecimal.valueOf(100));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(0, +count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "2+" + LABEL,
                BigDecimal.valueOf(2 + 100));
    }

    @Test
    public void testInsertRowsWithLabelToCell() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(0, 1); // A2 moved
        final SpreadsheetCellReference c = this.cellReference(0, 2); // A3 MOVED
        final SpreadsheetCellReference d = this.cellReference(8, 13); // B8 moved

        cellStore.save(this.cell(a, "1+" + LABEL));
        cellStore.save(this.cell(b, "2+0"));
        cellStore.save(this.cell(c, "3+0+" + LABEL));
        cellStore.save(this.cell(d, "99+0"));

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, d));

        final int count = 1;
        engine.insertRows(b.row(), count, context); // $b insert

        this.loadLabelAndCheck(labelStore, LABEL, d.add(0, +count));

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+" + LABEL,
                BigDecimal.valueOf(1 + 99));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(0, +count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "2+0",
                BigDecimal.valueOf(2 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(0, +count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "3+0+" + LABEL,
                BigDecimal.valueOf(3 + 99));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(0, +count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "99+0",
                BigDecimal.valueOf(99 + 0));
    }

    @Test
    public void testInsertRowsWithLabelToRangeUnchanged() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(5, 5); // A2 moved

        cellStore.save(this.cell(a, "99+0"));
        cellStore.save(this.cell(b, "2+0+" + LABEL));

        final SpreadsheetRange a1 = SpreadsheetRange.with(a, a.add(1, 1));
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, a1));

        final int count = 1;
        engine.insertRows(b.row(), count, context); // $b insert

        this.countAndCheck(labelStore, 1);

        this.loadLabelAndCheck(labelStore, LABEL, a1);

        this.countAndCheck(cellStore, 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "99+0",
                BigDecimal.valueOf(99 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(0, +count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "2+0+" + LABEL,
                BigDecimal.valueOf(2 + 99));
    }

    @Test
    public void testInsertRowsWithLabelToRangeUpdated() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0);
        final SpreadsheetCellReference b = this.cellReference(0, 5);
        final SpreadsheetCellReference c = this.cellReference(0, 10);
        final SpreadsheetCellReference d = this.cellReference(0, 15);
        final SpreadsheetCellReference e = this.cellReference(0, 20);

        cellStore.save(this.cell(a, "1+" + LABEL));
        cellStore.save(this.cell(c, "99+0"));

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, SpreadsheetRange.with(c, d)));

        engine.insertRows(b.row(), c.row().value() - b.row().value(), context); // $b insert

        this.countAndCheck(labelStore, 1);
        this.loadLabelAndCheck(labelStore, LABEL, SpreadsheetRange.with(d, e));

        this.countAndCheck(cellStore, 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+" + LABEL,
                BigDecimal.valueOf(1 + 99));

        this.loadCellAndCheckFormulaAndValue(engine,
                d,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "99+0",
                BigDecimal.valueOf(99 + 0));
    }

    @Test
    public void testInsertRowsWithCellReferences() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(0, 1); // A2
        final SpreadsheetCellReference c = this.cellReference(0, 10); // A10 moved
        final SpreadsheetCellReference d = this.cellReference(8, 13); // H13 moved

        cellStore.save(this.cell(a, "1+0+" + d));
        cellStore.save(this.cell(b, "2+0"));
        cellStore.save(this.cell(c, "3+0"));
        cellStore.save(this.cell(d, "4+0+" + b));

        final int count = 1;
        engine.insertRows(c.row(), count, context); // $c insert

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+0+" + d.add(0, +count),
                BigDecimal.valueOf(1 + 0 + 4 + 2)); // reference should have been fixed.

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "2+0",
                BigDecimal.valueOf(2));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(0, +count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "3+0",
                BigDecimal.valueOf(3));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(0, +count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "4+0+" + b,
                BigDecimal.valueOf(4 + 2));
    }

    @Test
    public void testInsertRowsWithCellReferences2() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(0, 1); // A2
        final SpreadsheetCellReference c = this.cellReference(0, 10); // A10 moved
        final SpreadsheetCellReference d = this.cellReference(8, 13); // H13 moved

        cellStore.save(this.cell(a, "1+0+" + d));
        cellStore.save(this.cell(b, "2+0"));
        cellStore.save(this.cell(c, "3+0"));
        cellStore.save(this.cell(d, "4+0+" + b)); // =5+2

        final int count = 2;
        engine.insertRows(c.row(), count, context); // $c insert

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+0+" + d.add(0, +count),
                BigDecimal.valueOf(1 + 0 + 4 + 2)); // reference should have been fixed.

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "2+0",
                BigDecimal.valueOf(2 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(0, +count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "3+0",
                BigDecimal.valueOf(3 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(0, +count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "4+0+" + b,
                BigDecimal.valueOf(4 + 0 + 2));
    }

    @Test
    public void testInsertRowsSeveral() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(0, 10); // A2 MOVED
        final SpreadsheetCellReference c = this.cellReference(0, 11); // A3 MOVED
        final SpreadsheetCellReference d = this.cellReference(2, 12); // C4 MOVED
        final SpreadsheetCellReference e = this.cellReference(3, 20); // T3 MOVED

        cellStore.save(this.cell(a, "1+0"));
        cellStore.save(this.cell(b, "2+0"));
        cellStore.save(this.cell(c, "3+0"));
        cellStore.save(this.cell(d, "4+0"));
        cellStore.save(this.cell(e, "5+0"));

        final int count = 5;
        engine.insertRows(this.row(7), count, context); // $b & $c

        this.countAndCheck(cellStore, 5);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+0",
                BigDecimal.valueOf(1 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(0, +count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "2+0",
                BigDecimal.valueOf(2 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(0, +count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "3+0",
                BigDecimal.valueOf(3 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(0, +count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "4+0",
                BigDecimal.valueOf(4 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                e.add(0, +count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "5+0",
                BigDecimal.valueOf(5 + 0));
    }

    // copy....................................................................................................

    @Test
    public void testCopyOneCellInto1x1() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference(10, 20);
        final SpreadsheetCellReference b = this.cellReference(11, 21);
        final SpreadsheetCellReference c = this.cellReference(12, 22);

        final SpreadsheetCell cellA = this.cell(a, "1+0");
        final SpreadsheetCell cellB = this.cell(b, "2+0");
        final SpreadsheetCell cellC = this.cell(c, "3+0");

        cellStore.save(cellA);
        cellStore.save(cellB);
        cellStore.save(cellC);

        final SpreadsheetCellReference d = this.cellReference(30, 40);

        engine.copy(Lists.of(cellA), SpreadsheetRange.with(d, d.add(1, 1)), context);

        this.countAndCheck(cellStore, 3 + 1);

        this.loadCellAndCheckFormulaAndValue(engine,
                d,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+0",
                BigDecimal.valueOf(1 + 0));
    }

    @Test
    public void testCopyOneCellInto2x2() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference(10, 20);
        final SpreadsheetCellReference b = this.cellReference(11, 21);
        final SpreadsheetCellReference c = this.cellReference(12, 22);

        final SpreadsheetCell cellA = this.cell(a, "1+0");
        final SpreadsheetCell cellB = this.cell(b, "2+0");
        final SpreadsheetCell cellC = this.cell(c, "3+0");

        cellStore.save(cellA);
        cellStore.save(cellB);
        cellStore.save(cellC);

        final SpreadsheetCellReference d = this.cellReference(30, 40);

        engine.copy(Lists.of(cellA, cellB), SpreadsheetRange.with(d, d.add(2, 2)), context);

        this.countAndCheck(cellStore, 3 + 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                d,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+0",
                BigDecimal.valueOf(1 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(1, 1),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "2+0",
                BigDecimal.valueOf(2 + 0));
    }

    @Test
    public void testCopy2x2CellInto1x1() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference(10, 20);
        final SpreadsheetCellReference b = this.cellReference(11, 21);
        final SpreadsheetCellReference c = this.cellReference(12, 22);

        final SpreadsheetCell cellA = this.cell(a, "1+0");
        final SpreadsheetCell cellB = this.cell(b, "2+0");
        final SpreadsheetCell cellC = this.cell(c, "3+0");

        cellStore.save(cellA);
        cellStore.save(cellB);
        cellStore.save(cellC);

        final SpreadsheetCellReference d = this.cellReference(30, 40);

        engine.copy(Lists.of(cellA, cellB), SpreadsheetRange.with(d, d.add(1, 1)), context);

        this.countAndCheck(cellStore, 3 + 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                d,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+0",
                BigDecimal.valueOf(1 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(1, 1),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "2+0",
                BigDecimal.valueOf(2 + 0));
    }

    @Test
    public void testCopy2x2CellInto2x2() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference(10, 20);
        final SpreadsheetCellReference b = this.cellReference(11, 21);
        final SpreadsheetCellReference c = this.cellReference(12, 22);

        final SpreadsheetCell cellA = this.cell(a, "1+0");
        final SpreadsheetCell cellB = this.cell(b, "2+0");
        final SpreadsheetCell cellC = this.cell(c, "3+0");

        cellStore.save(cellA);
        cellStore.save(cellB);
        cellStore.save(cellC);

        final SpreadsheetCellReference d = this.cellReference(30, 40);

        engine.copy(Lists.of(cellA, cellB), SpreadsheetRange.with(d, d.add(2, 2)), context);

        this.countAndCheck(cellStore, 3 + 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                d,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+0",
                BigDecimal.valueOf(1 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(1, 1),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "2+0",
                BigDecimal.valueOf(2 + 0));
    }

    @Test
    public void testCopy2x2CellInto7x2Gives6x2() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference(10, 20);
        final SpreadsheetCellReference b = this.cellReference(11, 21);
        final SpreadsheetCellReference c = this.cellReference(12, 22);

        final SpreadsheetCell cellA = this.cell(a, "1+0");
        final SpreadsheetCell cellB = this.cell(b, "2+0");
        final SpreadsheetCell cellC = this.cell(c, "3+0");

        cellStore.save(cellA);
        cellStore.save(cellB);
        cellStore.save(cellC);

        final SpreadsheetCellReference d = this.cellReference(30, 40);

        engine.copy(Lists.of(cellA, cellB), SpreadsheetRange.with(d, d.add(7, 2)), context);

        this.countAndCheck(cellStore, 3 + 6);

        this.loadCellAndCheckFormulaAndValue(engine,
                d,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+0",
                BigDecimal.valueOf(1 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(1, 1),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "2+0",
                BigDecimal.valueOf(2 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(2, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+0",
                BigDecimal.valueOf(1 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(3, 1),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "2+0",
                BigDecimal.valueOf(2 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(4, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+0",
                BigDecimal.valueOf(1 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(5, 1),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "2+0",
                BigDecimal.valueOf(2 + 0));
    }

    @Test
    public void testCopy2x2CellInto2x7Gives2x6() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference(10, 20);
        final SpreadsheetCellReference b = this.cellReference(11, 21);
        final SpreadsheetCellReference c = this.cellReference(12, 22);

        final SpreadsheetCell cellA = this.cell(a, "1+0");
        final SpreadsheetCell cellB = this.cell(b, "2+0");
        final SpreadsheetCell cellC = this.cell(c, "3+0");

        cellStore.save(cellA);
        cellStore.save(cellB);
        cellStore.save(cellC);

        final SpreadsheetCellReference d = this.cellReference(30, 40);

        engine.copy(Lists.of(cellA, cellB), SpreadsheetRange.with(d, d.add(2, 7)), context);

        this.countAndCheck(cellStore, 3 + 6);

        this.loadCellAndCheckFormulaAndValue(engine,
                d,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+0",
                BigDecimal.valueOf(1 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(1, 1),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "2+0",
                BigDecimal.valueOf(2 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(0, 2),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+0",
                BigDecimal.valueOf(1 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(1, 3),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "2+0",
                BigDecimal.valueOf(2 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(0, 4),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "1+0",
                BigDecimal.valueOf(1 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(1, 5),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "2+0",
                BigDecimal.valueOf(2 + 0));
    }

    @Test
    public void testCopyAbsoluteReference() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference(10, 20);
        final SpreadsheetCellReference b = this.cellReference(11, 21);

        final SpreadsheetCell cellA = this.cell(a, "1+0");
        final SpreadsheetCell cellB = this.cell(b, "" + a);

        cellStore.save(cellA);
        cellStore.save(cellB);

        final SpreadsheetCellReference d = this.cellReference(30, 40);

        engine.copy(Lists.of(cellB), SpreadsheetRange.with(d, d.add(1, 1)), context);

        this.countAndCheck(cellStore, 2 + 1);

        this.loadCellAndCheckFormulaAndValue(engine,
                d,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "" + a,
                BigDecimal.valueOf(1 + 0));
    }

    @Test
    public void testCopyRelativeReferenceFixed() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference(10, 20);
        final SpreadsheetCellReference b = SpreadsheetReferenceKind.RELATIVE.column(11)
                .setRow(SpreadsheetReferenceKind.RELATIVE.row(21));

        final SpreadsheetCell cellA = this.cell(a, "" + b);
        final SpreadsheetCell cellB = this.cell(b, "2+0");

        cellStore.save(cellA);
        cellStore.save(cellB);

        final SpreadsheetCellReference d = SpreadsheetReferenceKind.RELATIVE.column(30)
                .setRow(SpreadsheetReferenceKind.RELATIVE.row(40));

        engine.copy(Lists.of(cellA, cellB), SpreadsheetRange.with(d, d.add(2, 2)), context);

        this.countAndCheck(cellStore, 2 + 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                d,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "" + d.add(1, 1),
                BigDecimal.valueOf(2 + 0));
        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(1, 1),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                context,
                "2+0",
                BigDecimal.valueOf(2 + 0));
    }

    //  helpers.......................................................................................................

    @Override
    protected BasicSpreadsheetEngine createSpreadsheetEngine() {
        return this.createSpreadsheetEngine(this.cellStore());
    }

    private BasicSpreadsheetEngine createSpreadsheetEngine(final SpreadsheetCellStore cellStore) {
        return this.createSpreadsheetEngine(cellStore, this.labelStore());
    }

    private BasicSpreadsheetEngine createSpreadsheetEngine(final SpreadsheetLabelStore labelStore) {
        return this.createSpreadsheetEngine(this.cellStore(), labelStore);
    }

    private BasicSpreadsheetEngine createSpreadsheetEngine(final SpreadsheetCellStore cellStore,
                                                           final SpreadsheetLabelStore labelStore) {
        return this.createSpreadsheetEngine(cellStore, labelStore, this.conditionalFormattingRules());
    }

    private BasicSpreadsheetEngine createSpreadsheetEngine(final SpreadsheetCellStore cellStore,
                                                           final SpreadsheetLabelStore labelStore,
                                                           final SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> rules) {
        return BasicSpreadsheetEngine.with(this.id(), cellStore, labelStore, rules);
    }

    @Override
    protected SpreadsheetEngineContext createContext() {
        return SpreadsheetEngineContexts.fake();
    }

    private SpreadsheetEngineContext createContext(final BasicSpreadsheetEngine engine) {
        return this.createContext(SpreadsheetLabelStores.fake(), engine);
    }

    private SpreadsheetEngineContext createContext(final SpreadsheetLabelStore labelStore,
                                                   final BasicSpreadsheetEngine engine) {
        return new FakeSpreadsheetEngineContext() {

            @Override
            public SpreadsheetParserToken parseFormula(final String formula) {
                return SpreadsheetParsers.expression()
                        .orFailIfCursorNotEmpty(ParserReporters.basic())
                        .parse(TextCursors.charSequence(formula), SpreadsheetParserContexts.basic(decimalNumberContext()))
                        .get();
            }

            @Override
            public Object evaluate(final ExpressionNode node) {
                // throw an exception which is an "error" when the invalidCellReference function appears in a formula and executed
                final BiFunction<ExpressionNodeName, List<Object>, Object> functions = (name, params) -> {
                    if (name.value().equals(SpreadsheetFormula.INVALID_CELL_REFERENCE.value())) {
                        throw new ExpressionEvaluationException("Invalid cell reference " + params.get(0).toString());
                    }
                    throw new UnsupportedOperationException(name + "(" + params.stream().map(p -> p.toString()).collect(Collectors.joining(",")) + ")");
                };

                return node.toValue(ExpressionEvaluationContexts.basic(functions,
                        SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction.with(engine, labelStore, this),
                        MathContext.UNLIMITED,
                        Converters.simple(),
                        decimalNumberContext()));
            }

            @Override
            public <T> T convert(final Object value, final Class<T> target) {
                assertEquals("Only support converting to Boolean=" + value, Boolean.class, target);
                return target.cast(Boolean.parseBoolean(String.valueOf(value)));
            }

            @Override
            public SpreadsheetTextFormatter<?> parseFormatPattern(final String pattern) {
                if (PATTERN_COLOR.equals(pattern)) {
                    return formatter(pattern, COLOR, PATTERN_SUFFIX);
                }
                if (PATTERN.equals(pattern)) {
                    return formatter(pattern, SpreadsheetFormattedText.WITHOUT_COLOR, PATTERN_SUFFIX);
                }
                if (PATTERN_FORMAT_FAIL.equals(pattern)) {
                    return SpreadsheetTextFormatters.fixed(Object.class, Optional.empty());
                }

                throw new AssertionError("Unknown pattern=" + pattern + " expected one of " + PATTERN_FORMAT_FAIL + "|" + PATTERN + "|" + PATTERN_COLOR);
            }

            @Override
            public SpreadsheetTextFormatter<?> defaultSpreadsheetTextFormatter() {
                return formatter(PATTERN_DEFAULT, SpreadsheetFormattedText.WITHOUT_COLOR, DEFAULT_SUFFIX);
            }

            @Override
            public Optional<SpreadsheetFormattedText> format(final Object value,
                                                             final SpreadsheetTextFormatter<?> formatter) {
                assertFalse("Value must not be optional" + value, value instanceof Optional);
                return formatter.format(Cast.to(value), SpreadsheetTextFormatContexts.fake());
            }
        };
    }

    private SpreadsheetTextFormatter<?> formatter(final String pattern,
                                                  final Optional<Color> color,
                                                  final String suffix) {
        return new SpreadsheetTextFormatter<Object>() {
            @Override
            public Class<Object> type() {
                return Object.class;
            }

            @Override
            public Optional<SpreadsheetFormattedText> format(final Object value,
                                                             final SpreadsheetTextFormatContext context) {
                return Optional.of(SpreadsheetFormattedText.with(color, value + " " + suffix));
            }

            @Override
            public String toString() {
                return pattern;
            }
        };
    }

    private SpreadsheetCell loadCellAndCheckFormatted2(final SpreadsheetEngine engine,
                                                       final SpreadsheetCellReference reference,
                                                       final SpreadsheetEngineLoading loading,
                                                       final SpreadsheetEngineContext context,
                                                       final Object value,
                                                       final String suffix) {
        final SpreadsheetCell cell = this.loadCellAndCheckValue(engine, reference, loading, context, value);
        this.checkFormattedText(cell, value + " " + suffix);
        return cell;
    }

    private SpreadsheetId id() {
        return SpreadsheetId.with(123);
    }

    private SpreadsheetCellStore cellStore() {
        return SpreadsheetCellStores.basic();
    }

    private SpreadsheetLabelStore labelStore() {
        return SpreadsheetLabelStores.basic();
    }

    private SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> conditionalFormattingRules() {
        return SpreadsheetRangeStores.basic();
    }

    private SpreadsheetColumnReference column(final int column) {
        return SpreadsheetReferenceKind.ABSOLUTE.column(column);
    }

    private SpreadsheetRowReference row(final int row) {
        return SpreadsheetReferenceKind.ABSOLUTE.row(row);
    }

    private SpreadsheetCellReference cellReference(final int column, final int row) {
        return SpreadsheetReferenceKind.ABSOLUTE.column(column).setRow(SpreadsheetReferenceKind.ABSOLUTE.row(row));
    }

    private SpreadsheetRange range(final int beginColumn, final int beginRow, final int endColumn, final int endRow) {
        return range(cellReference(beginColumn, beginRow), cellReference(endColumn, endRow));
    }

    private SpreadsheetRange range(final SpreadsheetCellReference begin, final int endColumn, final int endRow) {
        return range(begin, cellReference(endColumn, endRow));
    }

    private SpreadsheetRange range(final SpreadsheetCellReference begin, final SpreadsheetCellReference end) {
        return SpreadsheetRange.with(begin, end);
    }

    private SpreadsheetCell cell(final SpreadsheetCellReference reference, final String formula) {
        return SpreadsheetCell.with(reference, SpreadsheetFormula.with(formula), this.style(), SpreadsheetCell.NO_FORMAT, SpreadsheetCell.NO_FORMATTED_CELL);
    }

    private SpreadsheetCellStyle style() {
        return style(SpreadsheetTextStyle.BOLD,
                SpreadsheetTextStyle.NO_ITALICS,
                SpreadsheetTextStyle.NO_UNDERLINE);
    }

    private SpreadsheetCellStyle style(final Optional<Boolean> bold,
                                       final Optional<Boolean> italics,
                                       final Optional<Boolean> underline) {
        return SpreadsheetCellStyle.EMPTY.setText(SpreadsheetTextStyle.EMPTY
                .setBold(bold)
                .setItalics(italics)
                .setUnderline(underline));
    }

    @Override
    protected Class<BasicSpreadsheetEngine> type() {
        return BasicSpreadsheetEngine.class;
    }
}

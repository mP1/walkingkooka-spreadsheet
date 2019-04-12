package walkingkooka.spreadsheet.engine;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.color.Color;
import walkingkooka.convert.Converters;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCellFormat;
import walkingkooka.spreadsheet.SpreadsheetDescription;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetFormattedCell;
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
import walkingkooka.spreadsheet.store.reference.SpreadsheetReferenceStore;
import walkingkooka.spreadsheet.store.reference.SpreadsheetReferenceStores;
import walkingkooka.spreadsheet.style.SpreadsheetCellStyle;
import walkingkooka.spreadsheet.style.SpreadsheetTextStyle;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
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
import walkingkooka.tree.expression.ExpressionReference;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetEngineTest extends BasicSpreadsheetEngineTestCase<BasicSpreadsheetEngine>
        implements SpreadsheetEngineTesting<BasicSpreadsheetEngine> {

    private final static String FORMATTED_PATTERN_SUFFIX = "FORMATTED_PATTERN_SUFFIX";
    private final static String FORMATTED_DEFAULT_SUFFIX = "FORMATTED_DEFAULT_SUFFIX";

    private final static Optional<Color> COLOR = Optional.of(Color.BLACK);

    private final static String PATTERN_DEFAULT = "$text+" + FORMATTED_DEFAULT_SUFFIX;
    private final static String PATTERN = "$text+" + FORMATTED_PATTERN_SUFFIX;
    private final static String PATTERN_COLOR = "$text+" + FORMATTED_PATTERN_SUFFIX + "+" + COLOR.get();
    private final static String PATTERN_FORMAT_FAIL = "<none>";

    private final static SpreadsheetTextFormatContext SPREADSHEET_TEXT_FORMAT_CONTEXT = SpreadsheetTextFormatContexts.fake();

    @Test
    public void testWithNullIdFails() {
        assertThrows(NullPointerException.class, () -> {
            BasicSpreadsheetEngine.with(null,
                    this.cellStore(),
                    this.labelStore(),
                    this.conditionalFormattingRules(),
                    this.cellReferencesStore(),
                    this.labelReferencesStore(),
                    this.rangeToCellStore());
        });
    }

    @Test
    public void testWithNullCellStoreFails() {
        assertThrows(NullPointerException.class, () -> {
            BasicSpreadsheetEngine.with(this.id(),
                    null,
                    this.labelStore(),
                    this.conditionalFormattingRules(),
                    this.cellReferencesStore(),
                    this.labelReferencesStore(),
                    this.rangeToCellStore());
        });
    }

    @Test
    public void testWithNullLabelStoreFails() {
        assertThrows(NullPointerException.class, () -> {
            BasicSpreadsheetEngine.with(this.id(),
                    this.cellStore(),
                    null,
                    this.conditionalFormattingRules(),
                    this.cellReferencesStore(),
                    this.labelReferencesStore(),
                    this.rangeToCellStore());
        });
    }

    @Test
    public void testWithNullConditionalFormattingRulesFails() {
        assertThrows(NullPointerException.class, () -> {
            BasicSpreadsheetEngine.with(this.id(),
                    this.cellStore(),
                    this.labelStore(),
                    null,
                    this.cellReferencesStore(),
                    this.labelReferencesStore(),
                    this.rangeToCellStore());
        });
    }

    @Test
    public void testWithNullCellReferencesStoreFails() {
        assertThrows(NullPointerException.class, () -> {
            BasicSpreadsheetEngine.with(this.id(),
                    this.cellStore(),
                    this.labelStore(),
                    this.conditionalFormattingRules(),
                    null,
                    this.labelReferencesStore(),
                    this.rangeToCellStore());
        });
    }

    @Test
    public void testWithNullLabelReferencesStoreFails() {
        assertThrows(NullPointerException.class, () -> {
            BasicSpreadsheetEngine.with(this.id(),
                    this.cellStore(),
                    this.labelStore(),
                    this.conditionalFormattingRules(),
                    this.cellReferencesStore(),
                    null,
                    this.rangeToCellStore());
        });
    }

    @Test
    public void testWithNullRangeToCellStoreFails() {
        assertThrows(NullPointerException.class, () -> {
            BasicSpreadsheetEngine.with(this.id(),
                    this.cellStore(),
                    this.labelStore(),
                    this.conditionalFormattingRules(),
                    this.cellReferencesStore(),
                    this.labelReferencesStore(),
                    null);
        });
    }

    // loadCell................................................................................................

    @Test
    public void testLoadCellCellWhenEmpty() {
        this.loadCellFailCheck(cellReference(1, 1), SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY);
    }

    @Test
    public void testLoadCellSkipEvaluate() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference cellReference = this.cellReference(1, 1);
        cellStore.save(this.cell(cellReference, "1+2"));

        this.loadCellAndCheckWithoutValueOrError(engine,
                cellReference,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context);
    }

    @Test
    public void testLoadCellWithoutFormatPattern() {
        this.cellStoreSaveAndLoadCellAndCheck(SpreadsheetCell.NO_FORMAT, FORMATTED_DEFAULT_SUFFIX);
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
                        .setFormatter(Optional.of(this.formatter(pattern, SpreadsheetFormattedText.WITHOUT_COLOR, suffix)))),
                suffix);
    }

    private void cellStoreSaveAndLoadCellAndCheck(final Optional<SpreadsheetCellFormat> format,
                                                  final String patternSuffix) {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference cellReference = this.cellReference(1, 1);
        cellStore.save(this.cell(cellReference, "1+2")
                .setFormat(format));

        this.loadCellAndCheckFormatted2(engine,
                cellReference,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
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
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context);
        final SpreadsheetCell second = this.loadCellOrFail(engine,
                cellReference,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context);

        assertSame(first, second, "different instances of SpreadsheetCell returned not cached");
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
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context);
        assertNotEquals(SpreadsheetFormula.NO_ERROR, first.formula().error(), () -> "Expected error absent=" + first);

        final SpreadsheetCell second = this.loadCellOrFail(engine, cellReference, SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY, context);
        assertSame(first, second, "different instances of SpreadsheetCell returned not cached");
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

        final SpreadsheetCell first = this.loadCellOrFail(engine, a, SpreadsheetEngineEvaluation.FORCE_RECOMPUTE, context);

        cellStore.save(this.cell(a, "999"));

        final SpreadsheetCell second = this.loadCellOrFail(engine, a, SpreadsheetEngineEvaluation.FORCE_RECOMPUTE, context);
        assertNotSame(first, second, "different instances of SpreadsheetCell returned not cached");
        assertEquals(Optional.of(BigDecimal.valueOf(999)),
                second.formula().value(),
                "first should have value updated to 999 and not 1 the original value.");
    }

    @Test
    public void testLoadCellForceRecomputeIgnoresPreviousError() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference a = this.cellReference("A1");
        final Set<SpreadsheetCell> saved = engine.saveCell(this.cell(a, "1+$B$2"), context);

        final SpreadsheetCell withError = saved.iterator().next();
        assertNotEquals(SpreadsheetFormula.NO_ERROR,
                withError.formula().error(),
                () -> "cell should have error because B2 reference is unknown=" + withError);

        final SpreadsheetCellReference b = this.cellReference("B2");
        cellStore.save(this.cell(b, "99"));

        this.loadCellAndCheckValue(engine,
                a,
                SpreadsheetEngineEvaluation.FORCE_RECOMPUTE,
                context,
                BigDecimal.valueOf(1 + 99));
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
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(1 + 2),
                FORMATTED_DEFAULT_SUFFIX);
        this.loadCellAndCheckFormatted2(engine,
                b,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(3 + 4),
                FORMATTED_DEFAULT_SUFFIX);
        this.loadCellAndCheckFormatted2(engine,
                c,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(5 + 6),
                FORMATTED_DEFAULT_SUFFIX);
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
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
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
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
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
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(3 + 4),
                FORMATTED_DEFAULT_SUFFIX);

        // reference to B1 which has formula
        this.loadCellAndCheckFormatted2(engine,
                a,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(3 + 4),
                FORMATTED_DEFAULT_SUFFIX);
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
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(3 + 4),
                FORMATTED_DEFAULT_SUFFIX);

        // reference to B1 which has formula
        this.loadCellAndCheckFormatted2(engine,
                a,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(3 + 4),
                FORMATTED_DEFAULT_SUFFIX);
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
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                BigDecimal.valueOf(3 + 4),
                FORMATTED_DEFAULT_SUFFIX);
        final SpreadsheetCellStyle style = cell.formatted().get().style();
        // UNDERLINED from conditional formatting rule #2.
        assertEquals(style(SpreadsheetTextStyle.NO_BOLD, SpreadsheetTextStyle.NO_ITALICS, SpreadsheetTextStyle.UNDERLINE),
                style,
                () -> "Style should include underline if correct rule was applied=" + cell);
    }

    private void saveRule(final boolean result, final int priority, final SpreadsheetCellStyle style,
                          final SpreadsheetCellReference cell, final SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> rules) {
        rules.addValue(SpreadsheetRange.cell(cell), rule(result, priority, style));
    }

    private SpreadsheetConditionalFormattingRule rule(final boolean result,
                                                      final int priority,
                                                      final SpreadsheetCellStyle style) {


        return SpreadsheetConditionalFormattingRule.with(SpreadsheetDescription.with(priority + "=" + result),
                priority,
                SpreadsheetFormula.with(String.valueOf(result)).setExpression(Optional.of(ExpressionNode.booleanNode(result))),
                (c) -> style);
    }

    // saveCell....................................................................................................

    @Test
    public void testSaveCellWithoutReferences() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferenceStore = this.cellReferencesStore();

        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore, cellReferenceStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCell a1 = this.cell("a1", "1+2");
        final SpreadsheetCell a1Formatted = this.formattedCellWithValue(a1, BigDecimal.valueOf(3));
        this.saveCellAndCheck(engine,
                a1,
                context,
                a1Formatted);

        this.loadCellStoreAndCheck(cellStore, a1Formatted);
        this.loadLabelStoreAndCheck(labelStore);
        this.countAndCheck(cellReferenceStore, 0);
    }

    @Test
    public void testSaveCellWithUnknownReference() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferenceStore = this.cellReferencesStore();

        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore,
                labelStore,
                cellReferenceStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCell a1 = this.cell("a1", "$B$2+99");
        final SpreadsheetCell a1Formatted = this.formattedCellWithError(a1, "Unknown cell reference $B$2");
                this.saveCellAndCheck(engine,
                a1,
                context,
                a1Formatted);

        this.loadCellStoreAndCheck(cellStore, a1Formatted);
        this.loadLabelStoreAndCheck(labelStore);

        // verify references all ways are present in the store.
        final SpreadsheetCellReference b2 = SpreadsheetCellReference.parse("$B$2");

        this.loadReferencesAndCheck(cellReferenceStore, a1.reference(), b2); // references from A1 -> B2
        this.loadReferrersAndCheck(cellReferenceStore, a1.reference()); // references to A1 -> none

        this.loadReferencesAndCheck(cellReferenceStore, b2); // references to B2 -> none
        this.loadReferrersAndCheck(cellReferenceStore, b2, a1.reference()); // references from B2 -> A1
    }

    @Test
    public void testSaveCellIgnoresPreviousErrorComputesValue() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCellReference cellReference = this.cellReference(1, 1);

        final SpreadsheetCell cell = SpreadsheetCell.with(cellReference,
                SpreadsheetFormula.with("1+2")
                        .setError(Optional.of(SpreadsheetError.with("error!"))),
                this.style());

        this.saveCellAndCheck(engine,
                cell,
                context,
                this.formattedCellWithValue(cell, BigDecimal.valueOf(1 + 2)));
    }

    @Test
    public void testSaveCellMultipleIndependentUnreferenced() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferenceStore = this.cellReferencesStore();

        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore,
                labelStore,
                cellReferenceStore);

        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCell a1 = this.cell("$A$1", "1+2");
        final SpreadsheetCell a1Formatted = this.formattedCellWithValue(a1, BigDecimal.valueOf(3));

        this.saveCellAndCheck(engine,
                a1,
                context,
                a1Formatted);

        final SpreadsheetCell b2 = this.cell("$B$2", "3+4");
        final SpreadsheetCell b2Formatted = this.formattedCellWithValue(b2, BigDecimal.valueOf(7));

        this.saveCellAndCheck(engine,
                b2,
                context,
                b2Formatted);

        final SpreadsheetCell c3 = this.cell("$C$3", "5+6");
        final SpreadsheetCell c3Formatted = this.formattedCellWithValue(c3, BigDecimal.valueOf(11));

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
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferenceStore = this.cellReferencesStore();
        final SpreadsheetReferenceStore<SpreadsheetLabelName> labelReferenceStore = this.labelReferencesStore();

        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore,
                labelStore,
                cellReferenceStore,
                labelReferenceStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCell a1 = this.cell("a1", "1+LABELB2");
        final SpreadsheetCell a1Formatted = this.formattedCellWithError(a1, "Unknown label LABELB2");
        this.saveCellAndCheck(engine,
                a1,
                context,
                a1Formatted);

        this.loadCellStoreAndCheck(cellStore, a1Formatted);
        this.loadLabelStoreAndCheck(labelStore);
        this.countAndCheck(cellReferenceStore, 0);

        final SpreadsheetLabelName labelB2 = SpreadsheetLabelName.with("LABELB2");
        this.loadReferencesAndCheck(labelReferenceStore, labelB2, a1.reference());
    }

    @Test
    public void testSaveCellTwiceLaterReferencesPrevious() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferenceStore = this.cellReferencesStore();

        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore,
                labelStore,
                cellReferenceStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCell a1 = this.cell("$A$1", "1+2");
        engine.saveCell(a1, context);

        final SpreadsheetCell b2 = this.cell("$B$2", "5+$A$1");

        this.saveCellAndCheck(engine,
                b2,
                context,
                this.formattedCellWithValue(b2, BigDecimal.valueOf(5 + 3)));

        this.loadReferencesAndCheck(cellReferenceStore, a1.reference());
        this.loadReferrersAndCheck(cellReferenceStore, a1.reference(), b2.reference());

        this.loadReferencesAndCheck(cellReferenceStore, b2.reference(), a1.reference());
        this.loadReferrersAndCheck(cellReferenceStore, b2.reference());
    }

    @Test
    public void testSaveCellTwiceLaterReferencesPrevious2() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferenceStore = this.cellReferencesStore();

        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore,
                labelStore,
                cellReferenceStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCell a1 = this.cell("$A$1", "1+C3");
        engine.saveCell(a1, context);

        final SpreadsheetCell b2 = this.cell("$B$2", "5+A1");
        engine.saveCell(b2, context);

        final SpreadsheetCell c3 = this.cell("$C$3", "10");

        this.saveCellAndCheck(engine,
                c3,
                context,
                this.formattedCellWithValue(a1, BigDecimal.valueOf(1 + 10)),
                this.formattedCellWithValue(b2, BigDecimal.valueOf(5 + 1 + 10)),
                this.formattedCellWithValue(c3, BigDecimal.valueOf(10)));
    }

    @Test
    public void testSaveCellTwiceLaterReferencesPreviousAgain() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferenceStore = this.cellReferencesStore();

        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore,
                labelStore,
                cellReferenceStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCell a1 = this.cell("$A$1", "1+2");
        engine.saveCell(a1, context);

        final SpreadsheetCell b2 = this.cell("$B$2", "5+$A$1");
        final SpreadsheetCell b2Formatted = this.formattedCellWithValue(b2, BigDecimal.valueOf(5 + 1 + 2));

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
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferenceStore = this.cellReferencesStore();

        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore,
                labelStore,
                cellReferenceStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCell a1 = this.cell("$A$1", "$B$2+5");
        engine.saveCell(a1, context);

        final SpreadsheetCell b2 = this.cell("$B$2", "1+2");
        this.saveCellAndCheck(engine,
                b2,
                context,
                this.formattedCellWithValue(a1, BigDecimal.valueOf(1 + 2 + 5)),
                this.formattedCellWithValue(b2, BigDecimal.valueOf(1 + 2)));

        this.loadReferencesAndCheck(cellReferenceStore, a1.reference(), b2.reference());
        this.loadReferrersAndCheck(cellReferenceStore, a1.reference());

        this.loadReferencesAndCheck(cellReferenceStore, b2.reference());
        this.loadReferrersAndCheck(cellReferenceStore, b2.reference(), a1.reference());
    }

    @Test
    public void testSaveCellLabelReference() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferenceStore = this.cellReferencesStore();
        final SpreadsheetReferenceStore<SpreadsheetLabelName> labelReferencesStore = this.labelReferencesStore();

        labelStore.save(SpreadsheetLabelMapping.with(SpreadsheetLabelName.with("LABELA1"), this.cellReference("A1")));

        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore,
                labelStore,
                cellReferenceStore,
                labelReferencesStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCell a1 = this.cell("$A$1", "10");
        engine.saveCell(a1, context);

        final SpreadsheetCell b2 = this.cell("$B$2", "5+LABELA1");
        this.saveCellAndCheck(engine,
                b2,
                context,
                this.formattedCellWithValue(b2, BigDecimal.valueOf(5 + 10)));
    }

    @Test
    public void testSaveCellLabelReference2() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferenceStore = this.cellReferencesStore();
        final SpreadsheetReferenceStore<SpreadsheetLabelName> labelReferencesStore = this.labelReferencesStore();

        final SpreadsheetLabelName labelB2 = SpreadsheetLabelName.with("LABELB2");
        final SpreadsheetCell b2 = this.cell("$B$2", "5");

        labelStore.save(SpreadsheetLabelMapping.with(labelB2, b2.reference()));

        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore,
                labelStore,
                cellReferenceStore,
                labelReferencesStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCell a1 = this.cell("$A$1", "10+" + labelB2);
        engine.saveCell(a1, context);

        this.loadReferencesAndCheck(cellReferenceStore, a1.reference());
        this.loadReferrersAndCheck(cellReferenceStore, a1.reference());

        this.loadReferencesAndCheck(cellReferenceStore, b2.reference());
        this.loadReferrersAndCheck(cellReferenceStore, b2.reference());

        this.loadReferencesAndCheck(labelReferencesStore, labelB2, a1.reference());

        this.saveCellAndCheck(engine,
                b2,
                context,
                this.formattedCellWithValue(a1, BigDecimal.valueOf(10 + 5)),
                this.formattedCellWithValue(b2, BigDecimal.valueOf(5)));
    }

    @Test
    public void testSaveCellReplacesCellReferences() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferenceStore = this.cellReferencesStore();

        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore,
                labelStore,
                cellReferenceStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCell d4 = this.cell("$D$4", "20");
        engine.saveCell(d4, context);

        final SpreadsheetCell e5 = this.cell("$E$5", "30");
        engine.saveCell(e5, context);

        engine.saveCell(this.cell("$A$1", "10+" + d4.reference()), context);

        final SpreadsheetCell a1 = this.cell("$A$1", "40+" + e5.reference());
        this.saveCellAndCheck(engine,
                a1,
                context,
                this.formattedCellWithValue(a1, BigDecimal.valueOf(40 + 30)));

        this.loadReferencesAndCheck(cellReferenceStore, a1.reference(), e5.reference());
        this.loadReferrersAndCheck(cellReferenceStore, a1.reference());

        this.loadReferencesAndCheck(cellReferenceStore, d4.reference());
        this.loadReferrersAndCheck(cellReferenceStore, d4.reference());
    }

    @Test
    public void testSaveCellReplacesLabelReferences() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferenceStore = this.cellReferencesStore();
        final SpreadsheetReferenceStore<SpreadsheetLabelName> labelReferencesStore = this.labelReferencesStore();

        final SpreadsheetLabelName labelB2 = SpreadsheetLabelName.with("LABELB2");
        labelStore.save(SpreadsheetLabelMapping.with(labelB2, this.cellReference("B2")));

        final SpreadsheetLabelName labelD4 = SpreadsheetLabelName.with("LABELD4");
        labelStore.save(SpreadsheetLabelMapping.with(labelD4, this.cellReference("D4")));

        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore,
                labelStore,
                cellReferenceStore,
                labelReferencesStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCell d4 = this.cell("$D$4", "20");
        engine.saveCell(d4, context);

        engine.saveCell(this.cell("$A$1", "10+" + labelB2), context);

        final SpreadsheetCell a1 = this.cell("$A$1", "40+" + labelD4);
        this.saveCellAndCheck(engine,
                a1,
                context,
                this.formattedCellWithValue(a1, BigDecimal.valueOf(40 + 20)));

        this.loadReferencesAndCheck(cellReferenceStore, a1.reference());
        this.loadReferrersAndCheck(cellReferenceStore, a1.reference());

        this.loadReferencesAndCheck(cellReferenceStore, d4.reference());
        this.loadReferrersAndCheck(cellReferenceStore, d4.reference());

        this.loadReferencesAndCheck(labelReferencesStore, labelB2);
        this.loadReferencesAndCheck(labelReferencesStore, labelD4, a1.reference());
    }

    @Test
    public void testSaveCellReplacesCellAndLabelReferences() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferenceStore = this.cellReferencesStore();
        final SpreadsheetReferenceStore<SpreadsheetLabelName> labelReferencesStore = this.labelReferencesStore();

        final SpreadsheetLabelName labelB2 = SpreadsheetLabelName.with("LABELB2");
        final SpreadsheetCellReference b2Reference = this.cellReference("B2");
        labelStore.save(SpreadsheetLabelMapping.with(labelB2, b2Reference));

        final SpreadsheetLabelName labelD4 = SpreadsheetLabelName.with("LABELD4");
        labelStore.save(SpreadsheetLabelMapping.with(labelD4, this.cellReference("D4")));

        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore,
                labelStore,
                cellReferenceStore,
                labelReferencesStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCell d4 = this.cell("$D$4", "20");
        engine.saveCell(d4, context);

        final SpreadsheetCell e5 = this.cell("$E$5", "30");
        engine.saveCell(e5, context);

        engine.saveCell(this.cell("$A$1", "10+" + labelB2 + "+C2"), context);

        final SpreadsheetCell a1 = this.cell("$A$1", "40+" + labelD4 + "+$E$5");
        this.saveCellAndCheck(engine,
                a1,
                context,
                this.formattedCellWithValue(a1, BigDecimal.valueOf(40 + 20 + 30)));

        this.loadReferencesAndCheck(cellReferenceStore, a1.reference(), e5.reference());
        this.loadReferrersAndCheck(cellReferenceStore, a1.reference());

        this.loadReferencesAndCheck(cellReferenceStore, d4.reference());
        this.loadReferrersAndCheck(cellReferenceStore, d4.reference());

        this.loadReferencesAndCheck(labelReferencesStore, labelB2);
        this.loadReferencesAndCheck(labelReferencesStore, labelD4, a1.reference());
    }

    // deleteCell....................................................................................................

    @Test
    public void testDeleteCellWithReferences() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferenceStore = this.cellReferencesStore();

        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore,
                labelStore,
                cellReferenceStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a1 = SpreadsheetCellReference.parse("$A$1");
        final SpreadsheetCellReference b2 = SpreadsheetCellReference.parse("$B$2");

        engine.saveCell(this.cell(a1, "99+" + b2), context);
        this.deleteCellAndCheck(engine, a1, context);

        this.loadReferencesAndCheck(cellReferenceStore, a1);
        this.loadReferrersAndCheck(cellReferenceStore, a1);

        this.loadReferencesAndCheck(cellReferenceStore, b2);
        this.loadReferrersAndCheck(cellReferenceStore, b2);
    }

    @Test
    public void testDeleteCellWithCellReferrers() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferenceStore = this.cellReferencesStore();

        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore,
                labelStore,
                cellReferenceStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCell a1 = this.cell("$A$1", "1+$B$2");
        engine.saveCell(a1, context);

        final SpreadsheetCell b2 = this.cell("$B$2", "20");
        engine.saveCell(b2, context);

        this.deleteCellAndCheck(engine,
                b2.reference(),
                context,
                this.formattedCellWithError(a1, "Unknown cell reference $B$2"));

        this.loadReferencesAndCheck(cellReferenceStore, a1.reference(), b2.reference());
        this.loadReferrersAndCheck(cellReferenceStore, a1.reference());

        this.loadReferencesAndCheck(cellReferenceStore, b2.reference());
        this.loadReferrersAndCheck(cellReferenceStore, b2.reference(), a1.reference());
    }

    @Test
    public void testDeleteCellWithLabelReferences() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferenceStore = this.cellReferencesStore();
        final SpreadsheetReferenceStore<SpreadsheetLabelName> labelReferencesStore = this.labelReferencesStore();

        final SpreadsheetLabelName labelB2 = SpreadsheetLabelName.with("LABELB2");
        final SpreadsheetCell b2 = this.cell("$B$2", "20");
        labelStore.save(SpreadsheetLabelMapping.with(labelB2, b2.reference()));

        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore,
                labelStore,
                cellReferenceStore,
                labelReferencesStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCell a1 = this.cell("$A$1", "1+" + labelB2);
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
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferenceStore = this.cellReferencesStore();
        final SpreadsheetReferenceStore<SpreadsheetLabelName> labelReferencesStore = this.labelReferencesStore();

        final SpreadsheetLabelName labelB2 = SpreadsheetLabelName.with("LABELB2");
        final SpreadsheetCell b2 = this.cell("$B$2", "20");
        labelStore.save(SpreadsheetLabelMapping.with(labelB2, b2.reference()));

        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore,
                labelStore,
                cellReferenceStore,
                labelReferencesStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCell a1 = this.cell("$A$1", "1+" + labelB2);
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
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference reference = this.cellReference(99, 0); // A3

        engine.saveCell(this.cell(reference, "99+0"), context);

        this.deleteColumnsAndCheck(engine,
                reference.column(),
                0,
                context);

        this.countAndCheck(cellStore, 1);
    }

    @Test
    public void testDeleteColumnNoCellsRefreshed() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference("A1"); // A1
        final SpreadsheetCellReference b = this.cellReference("B2"); // B2
        final SpreadsheetCellReference c = this.cellReference("C3"); // C3

        engine.saveCell(this.cell(a, "1+2"), context);
        engine.saveCell(this.cell(b, "3+4"), context);
        engine.saveCell(this.cell(c, "5+6"), context);

        this.deleteColumnsAndCheck(engine,
                c.column(),
                1,
                context);

        this.countAndCheck(cellStore, 2);
    }

    @Test
    public void testDeleteColumnColumnsAfterCellsRefreshed() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference("A1"); // A1
        final SpreadsheetCellReference b = this.cellReference("B2"); // B2
        final SpreadsheetCellReference c = this.cellReference("C3"); // C3

        engine.saveCell(this.cell(a, "1+2"), context);
        engine.saveCell(this.cell(b, "3+4"), context); // deleted/replaced by $c
        engine.saveCell(this.cell(c, "5+6"), context); // becomes b3

        this.deleteColumnsAndCheck(engine,
                b.column(),
                1,
                context,
                this.formattedCellWithValue("b3", "5+6", BigDecimal.valueOf(5+6)));

        this.countAndCheck(cellStore, 2);
    }

    @Test
    public void testDeleteColumnColumnsAfterCellsRefreshed2() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference("A1");
        final SpreadsheetCellReference b = this.cellReference("B2"); //replaced by $c
        final SpreadsheetCellReference c = this.cellReference("C3");
        final SpreadsheetCellReference d = this.cellReference("Z99");// B99 moved

        engine.saveCell(this.cell(a, "1+2"), context);
        engine.saveCell(this.cell(b, "3+4"), context);
        engine.saveCell(this.cell(c, "5+6"), context);
        engine.saveCell(this.cell(d, "7+8"), context);

        final int count = 1;
        this.deleteColumnsAndCheck(engine,
                b.column(),
                count,
                context,
                this.formattedCellWithValue("B3", "5+6", BigDecimal.valueOf(5+6)),
                this.formattedCellWithValue("Y99", "7+8", BigDecimal.valueOf(7+8)));

        this.countAndCheck(cellStore, 3);
    }

    @Test
    public void testDeleteColumnWithLabelsToCellReferenceIgnored() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = SpreadsheetCellReference.parse("A1"); // A1
        final SpreadsheetCellReference b = SpreadsheetCellReference.parse("E2"); // E2

        engine.saveCell(this.cell(a, "99+0"), context);
        engine.saveCell(this.cell(b, "2+0+" + LABEL), context);

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, a));

        final int count = 1;
        this.deleteColumnsAndCheck(engine,
                b.column().add(-1),
                count,
                context,
                this.formattedCellWithValue(b.add(-1, 0), "2+0+"+ LABEL, BigDecimal.valueOf(2 + 99)));

        this.countAndCheck(cellStore, 2);

        this.loadLabelAndCheck(labelStore, LABEL, a);
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

        engine.saveCell(this.cell(a, "1+" + LABEL), context);
        engine.saveCell(this.cell(b, "2+0"), context);
        engine.saveCell(this.cell(c, "3+0"), context);
        engine.saveCell(this.cell(d, "4+" + LABEL), context);
        engine.saveCell(this.cell(e, "99+0"), context); // LABEL=

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, e));

        final int count = 1;
        this.deleteColumnsAndCheck(engine,
                b.column(),
                count,
                context,
                this.formattedCellWithValue(a, "1+" +LABEL,BigDecimal.valueOf(1 + 99 + 0)),
                this.formattedCellWithValue(c.add(-count, 0), "3+0",BigDecimal.valueOf(3 + 0)),
                this.formattedCellWithValue(d.add(-count, 0), "4+" + LABEL,BigDecimal.valueOf(4 + 99 + 0)),
                this.formattedCellWithValue(e.add(-count, 0), "99+0",BigDecimal.valueOf(99+0))); // old $b delete, $c,$d columns -1.

        this.loadLabelAndCheck(labelStore, LABEL, e.add(-count, 0));

        this.countAndCheck(cellStore, 4);
    }

    @Test
    public void testDeleteColumnWithLabelToDeletedCell() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // A2

        engine.saveCell(this.cell(a, "1+0"), context);
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
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // A2
        final SpreadsheetCellReference c = this.cellReference(10, 0); // A10 deleted
        final SpreadsheetCellReference d = this.cellReference(13, 8); // H13 moved
        final SpreadsheetCellReference e = this.cellReference(14, 9); // I14 moved

        engine.saveCell(this.cell(a, "1+" + d), context);
        engine.saveCell(this.cell(b, "2"), context);
        engine.saveCell(this.cell(c, "3"), context);
        engine.saveCell(this.cell(d, "4"), context);
        engine.saveCell(this.cell(e, "5+" + b), context); // =5+2

        final int count = 1;
        this.deleteColumnsAndCheck(engine,
                c.column(),
                count,
                context,
                this.formattedCellWithValue(a, "1+" + d.add(-count, 0), BigDecimal.valueOf(1 + 4)),
                this.formattedCellWithValue(d.add(-count, 0), "4", BigDecimal.valueOf(4)),
                this.formattedCellWithValue(e.add(-count, 0), "5+" + b, BigDecimal.valueOf(5 + 2))); // $c delete

        this.countAndCheck(cellStore, 4);
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

        engine.saveCell(this.cell(a, "1+" + d), context);
        engine.saveCell(this.cell(b, "2"), context);
        engine.saveCell(this.cell(c, "3"), context);
        engine.saveCell(this.cell(d, "4"), context);
        engine.saveCell(this.cell(e, "5+" + b), context); // =5+2

        final int count = 2;
        this.deleteColumnsAndCheck(engine,
                c.column(),
                count,
                context,
                this.formattedCellWithValue(a, "1+" + d.add(-count, 0), BigDecimal.valueOf(1 + 4)),
                this.formattedCellWithValue(d.add(-count, 0), "4", BigDecimal.valueOf(4)),
                this.formattedCellWithValue(e.add(-count, 0), "5+" + b, BigDecimal.valueOf(5+ 2))); // $c deleted, old-d & old-e refreshed

        this.countAndCheck(cellStore, 4);
    }

    @Test
    public void testDeleteColumnWithCellReferencesToDeletedCell() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // A2

        engine.saveCell(this.cell(a, "1+" + b), context);
        engine.saveCell(this.cell(b, "2"), context);

        final int count = 1;
        this.deleteColumnsAndCheck(engine,
                b.column(),
                count,
                context,
                this.formattedCellWithError(a, "1+InvalidCellReference(\""+b+"\")", "Invalid cell reference: " + b)); // $b delete

        this.countAndCheck(cellStore, 1);
    }

    @Test
    public void testDeleteColumnSeveral() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(10, 0); // DELETED
        final SpreadsheetCellReference c = this.cellReference(11, 0); // DELETED
        final SpreadsheetCellReference d = this.cellReference(12, 2); // C4
        final SpreadsheetCellReference e = this.cellReference(20, 3); // T3
        final SpreadsheetCellReference f = this.cellReference(21, 4); // U4

        engine.saveCell(this.cell(a, "1"), context);
        engine.saveCell(this.cell(b, "2"), context);
        engine.saveCell(this.cell(c, "3"), context);
        engine.saveCell(this.cell(d, "4"), context);
        engine.saveCell(this.cell(e, "5"), context);
        engine.saveCell(this.cell(f, "6"), context);

        final int count = 5;
        this.deleteColumnsAndCheck(engine,
                this.column(7),
                count,
                context,
                this.formattedCellWithValue(d.add(-count, 0), "4", BigDecimal.valueOf(4)),
                this.formattedCellWithValue(e.add(-count, 0), "5", BigDecimal.valueOf(5)),
                this.formattedCellWithValue(f.add(-count, 0), "6", BigDecimal.valueOf(6))); // $b & $c

        this.countAndCheck(cellStore, 4);
    }

    // deleteRow....................................................................................................

    @Test
    public void testDeleteRowsNone() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference reference = this.cellReference(0, 1); // A2

        engine.saveCell(this.cell(reference, "99+0"), context);

        this.deleteRowsAndCheck(engine,
                reference.row(),
                0,
                context);

        this.countAndCheck(cellStore, 1);
    }

    @Test
    public void testDeleteRowsOne() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference("A1");
        final SpreadsheetCellReference b = this.cellReference("A2");
        final SpreadsheetCellReference c = this.cellReference("A3");

        engine.saveCell(this.cell(a, "1+2"), context);
        engine.saveCell(this.cell(b, "3+4"), context);
        engine.saveCell(this.cell(c, "5+6"), context);

        this.deleteRowsAndCheck(engine,
                b.row(),
                1,
                context,
                this.formattedCellWithValue(c.add(0, -1), "5+6", BigDecimal.valueOf(5+6)));

        this.countAndCheck(cellStore, 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(0, -1),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
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

        final SpreadsheetCellReference a = this.cellReference("A1"); //
        final SpreadsheetCellReference b = this.cellReference("A2"); // replaced by c
        final SpreadsheetCellReference c = this.cellReference("A3"); // DELETED
        final SpreadsheetCellReference d = this.cellReference("B10"); // moved

        engine.saveCell(this.cell(a, "1+2"), context);
        engine.saveCell(this.cell(b, "3+4"), context);
        engine.saveCell(this.cell(c, "5+6"), context);
        engine.saveCell(this.cell(d, "7+8"), context);

        final int count = 1;
        this.deleteRowsAndCheck(engine,
                b.row(),
                count,
                context,
                this.formattedCellWithValue(c.add(0, - count), "5+6", BigDecimal.valueOf(5+6) ),
                this.formattedCellWithValue(d.add(0, - count), "7+8", BigDecimal.valueOf(7+8) )); // $b delete

        this.countAndCheck(cellStore, 3);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "1+2",
                BigDecimal.valueOf(1 + 2));
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

        engine.saveCell(this.cell(a, "1+2"), context);
        engine.saveCell(this.cell(b, "3+4"), context);
        engine.saveCell(this.cell(c, "5+6"), context);
        engine.saveCell(this.cell(d, "7+8"), context);

        final int count = 2;
        this.deleteRowsAndCheck(engine,
                b.row(),
                count,
                context,
                this.formattedCellWithValue(d.add(0, - count), "7+8", BigDecimal.valueOf(7+8) ));

        this.countAndCheck(cellStore, 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "1+2",
                BigDecimal.valueOf(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(0, -count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
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

        final SpreadsheetCellReference a = this.cellReference("$A$1");
        final SpreadsheetCellReference b = this.cellReference("$A$2");
        final SpreadsheetCellReference c = this.cellReference("$A$6");

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, a));

        engine.saveCell(this.cell(a, "1+0"), context);
        engine.saveCell(this.cell(b, "20+0+" + LABEL), context);
        engine.saveCell(this.cell(c, "99+0"), context);

        final int count = 2;
        this.deleteRowsAndCheck(engine,
                b.row().add(count),
                count,
                context,
                this.formattedCellWithValue("$A$4", "99+0", BigDecimal.valueOf(99+0))); // $c moved, $b unmodified label refs $a also unmodified.

        this.countAndCheck(cellStore, 3);

        this.loadLabelAndCheck(labelStore, LABEL, a);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "1+0",
                BigDecimal.valueOf(1));

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "20+0+" + LABEL,
                BigDecimal.valueOf(20+0+1));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(0, -count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
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

        final SpreadsheetCellReference a = this.cellReference("$A$1");
        final SpreadsheetCellReference b = this.cellReference("$A$6");

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, b));

        cellStore.save(this.cell(a, "1+0+" + LABEL));
        cellStore.save(this.cell(b, "2+0"));

        final int count = 2;
        this.deleteRowsAndCheck(engine,
                a.row().add(1),
                count,
                context,
                this.formattedCellWithValue(a, "1+0+"+ LABEL, BigDecimal.valueOf(1+0+2+0)),
                this.formattedCellWithValue(b.add(0, -count), "2+0", BigDecimal.valueOf(2+0))); // $b moved

        this.countAndCheck(cellStore, 2);

        this.loadLabelAndCheck(labelStore, LABEL, b.add(0, -count));

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "1+0+" + LABEL,
                BigDecimal.valueOf(3));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(0, -count),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
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

        final SpreadsheetCellReference a = this.cellReference("$A$1");
        final SpreadsheetCellReference b = this.cellReference("$A$6");

        engine.saveCell(this.cell(a, "1+" + b), context);
        engine.saveCell(this.cell(b, "2+0"), context);

        this.deleteRowsAndCheck(engine,
                b.row(),
                1,
                context,
                this.formattedCellWithError(a, "1+InvalidCellReference(\""+b+ "\")", "Invalid cell reference: $A$6"));

        this.countAndCheck(cellStore, 1);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "1+InvalidCellReference(\"" + b + "\")",
                "Invalid cell reference: " + b); // reference should have been fixed.
    }

    @Test
    public void testDeleteRowsWithCellReferencesFixed() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference("$A$1"); // A1
        final SpreadsheetCellReference b = this.cellReference("$A$2"); // B1
        final SpreadsheetCellReference c = this.cellReference("$A$11"); // A10 deleted
        final SpreadsheetCellReference d = this.cellReference("$I$14"); // H13 moved
        final SpreadsheetCellReference e = this.cellReference("$J$15"); // I14 moved

        engine.saveCell(this.cell(a, "1+" + d), context);
        engine.saveCell(this.cell(b, "2"), context);
        engine.saveCell(this.cell(c, "3"), context); // DELETED
        engine.saveCell(this.cell(d, "4"), context); // REFRESHED
        engine.saveCell(this.cell(e, "5+" + b), context); // REFRESHED =5+2

        final int count = 1;
        this.deleteRowsAndCheck(engine,
                c.row(),
                count,
                context,
                this.formattedCellWithValue("$A$1", "1+$I$13", BigDecimal.valueOf(1+4)),
                this.formattedCellWithValue("$I$13", "4", BigDecimal.valueOf(4)),
                this.formattedCellWithValue("$J$14", "5+$A$2", BigDecimal.valueOf(5 + 2))); // $c delete

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "1+" + d.add(0, -count),
                BigDecimal.valueOf(1 + 4)); // reference should have been fixed.

        this.loadCellAndCheckFormatted2(engine,
                b,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                BigDecimal.valueOf(2),
                FORMATTED_DEFAULT_SUFFIX);

        this.loadCellAndCheckFormatted2(engine,
                d.add(0, -count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                BigDecimal.valueOf(4),
                FORMATTED_DEFAULT_SUFFIX);

        this.loadCellAndCheckFormulaAndValue(engine,
                e.add(0, -count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
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

        final SpreadsheetCellReference a = this.cellReference("$A$1");
        final SpreadsheetCellReference b = this.cellReference("$A$2");
        final SpreadsheetCellReference c = this.cellReference("$A$11");// DELETED
        final SpreadsheetCellReference d = this.cellReference("$I$14"); // MOVED
        final SpreadsheetCellReference e = this.cellReference("$J$15"); // MOVED

        engine.saveCell(this.cell(a, "1+" + d), context);
        engine.saveCell(this.cell(b, "2"), context);
        engine.saveCell(this.cell(c, "3"), context);
        engine.saveCell(this.cell(d, "4"), context);
        engine.saveCell(this.cell(e, "5+" + b), context); // =5+2

        final int count = 2;
        this.deleteRowsAndCheck(engine,
                c.row(),
                count,
                context,
                this.formattedCellWithValue("$A$1", "1+$I$12", BigDecimal.valueOf(1+4)),
                this.formattedCellWithValue("$I$12", "4", BigDecimal.valueOf(4)),
                this.formattedCellWithValue("$J$13", "5+$A$2", BigDecimal.valueOf(5+2))); // $c delete

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "1+" + d.add(0, -count),
                BigDecimal.valueOf(1 + 4)); // reference should have been fixed.

        this.loadCellAndCheckFormatted2(engine,
                b,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                BigDecimal.valueOf(2),
                FORMATTED_DEFAULT_SUFFIX);

        this.loadCellAndCheckFormatted2(engine,
                d.add(0, -count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                BigDecimal.valueOf(4),
                FORMATTED_DEFAULT_SUFFIX);

        this.loadCellAndCheckFormulaAndValue(engine,
                e.add(0, -count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
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

        final SpreadsheetRange ab = range(a, b);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, ab));

        engine.saveCell(this.cell(a, "1+0"), context);
        engine.saveCell(this.cell(c, "20+0+" + LABEL), context);
        engine.saveCell(this.cell(d, "99+0"), context); // DELETED

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
                "1+0",
                BigDecimal.valueOf(1));

        this.loadCellAndCheckFormulaAndValue(engine,
                c,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
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

        engine.saveCell(this.cell(a, "1+0"), context);

        final SpreadsheetRange bc = range(b, c);
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
        
        final SpreadsheetRange bc = range(b, c);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, bc));
        
        engine.saveCell(this.cell(a, "1+0"), context);
        engine.saveCell(this.cell(d, "20+0"), context);

        final int count = c.row().value() - b.row().value() + 1;
        this.deleteRowsAndCheck(engine,
                b.row(),
                count,
                context,
                this.formattedCellWithValue(d.add(0, -count), "20+0", BigDecimal.valueOf(20+0))); // b..c deleted, d moved

        this.countAndCheck(cellStore, 2); // a&d
        this.countAndCheck(labelStore, 0);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "1+0",
                BigDecimal.valueOf(1));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(0, -count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
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

        final SpreadsheetRange bc = range(b, c);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, bc));
        
        engine.saveCell(this.cell(a, "1+0+" + LABEL), context);
        engine.saveCell(this.cell(b, "20+0"), context);
        
        final int count = c.row().value() - b.row().value() + 1;
        this.deleteRowsAndCheck(engine,
                b.row(),
                count,
                context,
                formattedCellWithError(a, "1+0+"+LABEL, "Unknown label " + LABEL)); // b..c deleted

        this.countAndCheck(cellStore, 1); // a
        this.countAndCheck(labelStore, 0);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
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

        final SpreadsheetRange de = range(d, e);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, de));

        engine.saveCell(this.cell(a, "1+0+" + LABEL), context);
        engine.saveCell(this.cell(d, "20+0"), context);
        
        final int count = c.row().value() - b.row().value() + 1;
        this.deleteRowsAndCheck(engine,
                b.row(),
                count,
                context,
                this.formattedCellWithValue(d.add(0, -count), "20+0", BigDecimal.valueOf(20+0))); // b..c deleted, d moved

        this.countAndCheck(cellStore, 2); // a&d

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "1+0+" + LABEL,
                BigDecimal.valueOf(1 + 20));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(0, -count),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
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
        this.deleteRowsAndCheck(engine,
                d.row(),
                count,
                context); // b..c deleted, d moved

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
        this.deleteRowsAndCheck(engine,
                c.row(),
                count,
                context); // b..c deleted, d moved

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
        this.deleteRowsAndCheck(engine,
                a.row(),
                count,
                context);

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

        engine.saveCell(this.cell(reference, "99+0"), context);

        engine.deleteColumns(reference.column(), 0, context);

        this.countAndCheck(cellStore, 1);

        this.loadCellAndCheckFormulaAndValue(engine,
                reference,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
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

        final SpreadsheetCellReference a = this.cellReference("$A1");
        final SpreadsheetCellReference b = this.cellReference(1, 0); // B1
        final SpreadsheetCellReference c = this.cellReference(2, 0); // C1

        engine.saveCell(this.cell(a, "1+2"), context);
        engine.saveCell(this.cell(b, "3+4"), context);
        engine.saveCell(this.cell(c, "5+6"), context);

        this.deleteColumnsAndCheck(engine,
                b.column(),
                1,
                context,
                this.formattedCellWithValue("$B$1", "5+6", BigDecimal.valueOf(5+6)));

        this.countAndCheck(cellStore, 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "1+2",
                BigDecimal.valueOf(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
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

        final SpreadsheetCellReference a = this.cellReference("$A$1"); //
        final SpreadsheetCellReference b = this.cellReference("$B$1"); // replaced by c
        final SpreadsheetCellReference c = this.cellReference("$C$1"); // DELETED
        final SpreadsheetCellReference d = this.cellReference("$J$2"); // moved

        engine.saveCell(this.cell(a, "1+2"), context);
        engine.saveCell(this.cell(b, "3+4"), context); // DELETE
        engine.saveCell(this.cell(c, "5+6"), context);
        engine.saveCell(this.cell(d, "7+8"), context);

        final int count = 1;
        this.deleteColumnsAndCheck(engine,
                b.column(),
                count,
                context,
                this.formattedCellWithValue("$B$1", "5+6", BigDecimal.valueOf(5+6)),
                this.formattedCellWithValue("$I$2", "7+8", BigDecimal.valueOf(7+8))); // $b delete

        this.countAndCheck(cellStore, 3);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "1+2",
                BigDecimal.valueOf(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "5+6",
                BigDecimal.valueOf(5 + 6));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(-count, 0),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
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

        final SpreadsheetCellReference a = this.cellReference("$A$1"); //
        final SpreadsheetCellReference b = this.cellReference("$B$1"); // DELETED
        final SpreadsheetCellReference c = this.cellReference("$C$1"); // DELETED
        final SpreadsheetCellReference d = this.cellReference("$J$2"); // MOVED

        engine.saveCell(this.cell(a, "1+2"), context);
        engine.saveCell(this.cell(b, "3+4"), context);
        engine.saveCell(this.cell(c, "5+6"), context);
        engine.saveCell(this.cell(d, "7+8"), context);

        final int count = 2;
        this.deleteColumnsAndCheck(engine,
                b.column(),
                count,
                context,
                this.formattedCellWithValue("$H$2", "7+8", BigDecimal.valueOf(7+8))); // $b, $c deleted

        this.countAndCheck(cellStore, 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "1+2",
                BigDecimal.valueOf(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(-count, 0),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
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

        final SpreadsheetCellReference a = this.cellReference("$A$1");
        final SpreadsheetCellReference b = this.cellReference("$B$1");
        final SpreadsheetCellReference c = this.cellReference("$F$1");
        
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, a));
        
        engine.saveCell(this.cell(a, "1+0"), context);
        engine.saveCell(this.cell(b, "20+0+" + LABEL), context);
        engine.saveCell(this.cell(c, "99+0"), context);

        final int count = 2;
        this.deleteColumnsAndCheck(engine,
                b.column().add(2),
                count,
                context,
                this.formattedCellWithValue("$D$1", "99+0", BigDecimal.valueOf(99+0)));

        this.countAndCheck(cellStore, 3);

        this.loadLabelAndCheck(labelStore, LABEL, a);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "1+0",
                BigDecimal.valueOf(1));

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "20+0+" + LABEL,
                BigDecimal.valueOf(21));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(-count, 0),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
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

        final SpreadsheetCellReference a = this.cellReference("$A$1");
        final SpreadsheetCellReference b = this.cellReference("$E$1");

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, b));

        engine.saveCell(this.cell(a, "1+0+" + LABEL), context);
        engine.saveCell(this.cell(b, "2+0"), context);
        
        final int count = 2;
        this.deleteColumnsAndCheck(engine,
                a.column().add(1),
                count,
                context,
                this.formattedCellWithValue("$C$1", "2+0", BigDecimal.valueOf(2+0))); // $b moved

        this.countAndCheck(cellStore, 2);

        this.loadLabelAndCheck(labelStore, LABEL, b.add(-count, 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "1+0+" + LABEL,
                BigDecimal.valueOf(3));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(-count, 0),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
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

        final SpreadsheetCellReference a = this.cellReference("$A$1");
        final SpreadsheetCellReference b = this.cellReference("$E$1");

        engine.saveCell(this.cell(a, "1+" + b), context);
        engine.saveCell(this.cell(b, "2+0"), context);

        this.deleteColumnsAndCheck(engine,
                b.column(),
                1,
                context,
                this.formattedCellWithError(a, "1+InvalidCellReference(\""+b+"\")", "Invalid cell reference: $E$1")); // $v delete

        this.countAndCheck(cellStore, 1);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "1+InvalidCellReference(\"" + b + "\")",
                "Invalid cell reference: " + b); // reference should have been fixed.
    }

    @Test
    public void testDeleteColumnsWithCellReferencesFixed() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);
        final SpreadsheetEngineContext context = this.createContext(labelStore, engine);

        final SpreadsheetCellReference a = this.cellReference("$A$1");
        final SpreadsheetCellReference b = this.cellReference("$B$1");
        final SpreadsheetCellReference c = this.cellReference("$K$1"); // DELETED
        final SpreadsheetCellReference d = this.cellReference("$N$9"); // MOVED
        final SpreadsheetCellReference e = this.cellReference("$O$10"); // MOVED

        engine.saveCell(this.cell(a, "1+" + d), context);
        engine.saveCell(this.cell(b, "2"), context);
        engine.saveCell(this.cell(c, "3"), context);
        engine.saveCell(this.cell(d, "4"), context);
        engine.saveCell(this.cell(e, "5+" + b), context); // =5+2

        final int count = 1;
        this.deleteColumnsAndCheck(engine,
                c.column(),
                count,
                context,
                this.formattedCellWithValue("$A$1", "1+$M$9", BigDecimal.valueOf(1+4)),
                this.formattedCellWithValue("$M$9", "4", BigDecimal.valueOf(4)),
                this.formattedCellWithValue("$N$10", "5+" + b, BigDecimal.valueOf(5+2))); // $c delete

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "1+" + d.add(-count, 0),
                BigDecimal.valueOf(1 + 4)); // reference should have been fixed.

        this.loadCellAndCheckFormatted2(engine,
                b,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                BigDecimal.valueOf(2),
                FORMATTED_DEFAULT_SUFFIX);

        this.loadCellAndCheckFormatted2(engine,
                d.add(-count, 0),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                BigDecimal.valueOf(4),
                FORMATTED_DEFAULT_SUFFIX);

        this.loadCellAndCheckFormulaAndValue(engine,
                e.add(-count, 0),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
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

        final SpreadsheetCellReference a = this.cellReference("$A$1"); // A1
        final SpreadsheetCellReference b = this.cellReference("$B$1"); // B1
        final SpreadsheetCellReference c = this.cellReference("$K$1"); // J1 deleted
        final SpreadsheetCellReference d = this.cellReference("$N$9"); // M8 moved
        final SpreadsheetCellReference e = this.cellReference("$O$10"); // N9 moved

        engine.saveCell(this.cell(a, "1+" + d), context);
        engine.saveCell(this.cell(b, "2"), context);
        engine.saveCell(this.cell(c, "3"), context); // DELETED
        engine.saveCell(this.cell(d, "4"), context); // MOVED
        engine.saveCell(this.cell(e, "5+" + b), context); // MOVED

        final int count = 2;
        this.deleteColumnsAndCheck(engine,
                c.column(),
                count,
                context,
                this.formattedCellWithValue("$A$1", "1+$L$9" , BigDecimal.valueOf(1+4)),
                this.formattedCellWithValue("$L$9", "4", BigDecimal.valueOf(4)),
                this.formattedCellWithValue("$M$10", "5+$B$1", BigDecimal.valueOf(5+2))); // $c delete

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "1+" + d.add(-count, 0),
                BigDecimal.valueOf(1 + 4)); // reference should have been fixed.

        this.loadCellAndCheckFormatted2(engine,
                b,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                BigDecimal.valueOf(2),
                FORMATTED_DEFAULT_SUFFIX);

        this.loadCellAndCheckFormatted2(engine,
                d.add(-count, 0),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                BigDecimal.valueOf(4),
                FORMATTED_DEFAULT_SUFFIX);

        this.loadCellAndCheckFormulaAndValue(engine,
                e.add(-count, 0),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
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

        final SpreadsheetRange ab = range(a, b);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, ab));

        engine.saveCell(this.cell(a, "1+0"), context);
        engine.saveCell(this.cell(c, "20+0+" + LABEL), context);
        engine.saveCell(this.cell(d, "99+0"), context); // deleted!!!

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
                "1+0",
                BigDecimal.valueOf(1));

        this.loadCellAndCheckFormulaAndValue(engine,
                c,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
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

        final SpreadsheetRange bc = range(b, c);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, bc));

        engine.saveCell(this.cell(a, "1+0"), context);

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
        
        final SpreadsheetRange bc = range(b, c);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, bc));
        
        engine.saveCell(this.cell(a, "1+0"), context);
        engine.saveCell(this.cell(d, "20+0"), context);
        
        final int count = c.column().value() - b.column().value() + 1;
        this.deleteColumnsAndCheck(engine,
                b.column(),
                count,
                context,
                this.formattedCellWithValue(d.add(-count, 0), "20+0", BigDecimal.valueOf(20+0))); // b..c deleted, d moved

        this.countAndCheck(cellStore, 2); // a&d
        this.countAndCheck(labelStore, 0);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "1+0",
                BigDecimal.valueOf(1));
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
        
        final SpreadsheetRange bc = range(b, c);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, bc));
        
        engine.saveCell(this.cell(a, "1+0+" + LABEL), context);
        engine.saveCell(this.cell(b, "20+0"), context);

        final int count = c.column().value() - b.column().value() + 1;
        this.deleteColumnsAndCheck(engine,
                b.column(),
                count,
                context,
                this.formattedCellWithError("$A$1", "1+0+" + LABEL, "Unknown label " + LABEL)); // b..c deleted

        this.countAndCheck(cellStore, 1); // a
        this.countAndCheck(labelStore, 0);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
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
        
        final SpreadsheetRange de = range(d, e);
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, de));
        
        engine.saveCell(this.cell(a, "1+0+" + LABEL), context);
        engine.saveCell(this.cell(d, "20+0"), context);

        final int count = c.column().value() - b.column().value() + 1;
        this.deleteColumnsAndCheck(engine,
                b.column(),
                count,
                context,
                this.formattedCellWithValue(d.add(-count, 0), "20+0", BigDecimal.valueOf(20+0))); // b..c deleted, d moved

        this.countAndCheck(cellStore, 2); // a&d

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context,
                "1+0+" + LABEL,
                BigDecimal.valueOf(1 + 20));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(-count, 0),
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
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
        this.deleteColumnsAndCheck(engine,
                d.column(),
                count,
                context); // b..c deleted, d moved

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
        this.deleteColumnsAndCheck(engine,
                c.column(),
                count,
                context); // b..c deleted, d moved

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
        this.deleteColumnsAndCheck(engine,
                a.column(),
                count,
                context);

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
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
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
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "1+2",
                BigDecimal.valueOf(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(count, 0),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "3+4",
                BigDecimal.valueOf(3 + 4));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(count, 0),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
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
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "1+2",
                BigDecimal.valueOf(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(count, 0),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "3+4",
                BigDecimal.valueOf(3 + 4));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(count, 0),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
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
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "100",
                BigDecimal.valueOf(100));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(count, 0),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
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
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "1+" + LABEL,
                BigDecimal.valueOf(1 + 99));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(count, 0),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "2+0",
                BigDecimal.valueOf(2 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(count, 0),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "3+0+" + LABEL,
                BigDecimal.valueOf(3 + 99));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(count, 0),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
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
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "99+0",
                BigDecimal.valueOf(99 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(count, 0),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
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
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "1+" + LABEL,
                BigDecimal.valueOf(1 + 99));

        this.loadCellAndCheckFormulaAndValue(engine,
                d,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
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
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "1+0+" + d.add(count, 0),
                BigDecimal.valueOf(1 + 0 + 4 + 2)); // reference should have been fixed.

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "2+0",
                BigDecimal.valueOf(2));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(count, 0),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "3+0",
                BigDecimal.valueOf(3));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(count, 0),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
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
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "1+0+" + d.add(count, 0),
                BigDecimal.valueOf(1 + 0 + 4 + 2)); // reference should have been fixed.

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "2+0",
                BigDecimal.valueOf(2 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(count, 0),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "3+0",
                BigDecimal.valueOf(3 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(count, 0),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
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
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "1+0",
                BigDecimal.valueOf(1 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(count, 0),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "2+0",
                BigDecimal.valueOf(2 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(count, 0),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "3+0",
                BigDecimal.valueOf(3 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(count, 0),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "4+0",
                BigDecimal.valueOf(4 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                e.add(count, 0),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
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
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
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
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "1+2",
                BigDecimal.valueOf(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(0, count),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "3+4",
                BigDecimal.valueOf(3 + 4));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(0, count),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
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
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "1+2",
                BigDecimal.valueOf(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(0, +count),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "3+4",
                BigDecimal.valueOf(3 + 4));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(0, +count),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
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
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "100",
                BigDecimal.valueOf(100));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(0, +count),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
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
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "1+" + LABEL,
                BigDecimal.valueOf(1 + 99));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(0, +count),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "2+0",
                BigDecimal.valueOf(2 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(0, +count),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "3+0+" + LABEL,
                BigDecimal.valueOf(3 + 99));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(0, +count),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
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
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "99+0",
                BigDecimal.valueOf(99 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(0, +count),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
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
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "1+" + LABEL,
                BigDecimal.valueOf(1 + 99));

        this.loadCellAndCheckFormulaAndValue(engine,
                d,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
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
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "1+0+" + d.add(0, +count),
                BigDecimal.valueOf(1 + 0 + 4 + 2)); // reference should have been fixed.

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "2+0",
                BigDecimal.valueOf(2));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(0, +count),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "3+0",
                BigDecimal.valueOf(3));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(0, +count),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
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
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "1+0+" + d.add(0, +count),
                BigDecimal.valueOf(1 + 0 + 4 + 2)); // reference should have been fixed.

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "2+0",
                BigDecimal.valueOf(2 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(0, +count),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "3+0",
                BigDecimal.valueOf(3 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(0, +count),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
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
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "1+0",
                BigDecimal.valueOf(1 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(0, +count),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "2+0",
                BigDecimal.valueOf(2 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(0, +count),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "3+0",
                BigDecimal.valueOf(3 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(0, +count),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "4+0",
                BigDecimal.valueOf(4 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                e.add(0, +count),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                context,
                "5+0",
                BigDecimal.valueOf(5 + 0));
    }

    // copyCells....................................................................................................

    @Test
    public void testCopyCellsOneCellInto1x1() {
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

        this.copyCellsAndCheck(engine,
                Lists.of(cellA),
                SpreadsheetRange.cell(d),
                context,
                this.formattedCellWithValue(d, "1+0", BigDecimal.valueOf(1 + 0)));

        this.countAndCheck(cellStore, 3 + 1);
    }

    @Test
    public void testCopyCellsOneCellInto2x2() {
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

        this.copyCellsAndCheck(engine,
                Lists.of(cellA, cellB),
                SpreadsheetRange.with(d, d.add(2, 2)),
                context,
                this.formattedCellWithValue(d, "1+0", BigDecimal.valueOf(1 + 0)),
                this.formattedCellWithValue(d.add(1, 1), "2+0", BigDecimal.valueOf(2 + 0)));

        this.countAndCheck(cellStore, 3 + 2);
    }

    @Test
    public void testCopyCells2x2CellInto1x1() {
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

        this.copyCellsAndCheck(engine,
                Lists.of(cellA, cellB),
                SpreadsheetRange.with(d, d.add(1, 1)),
                context,
                this.formattedCellWithValue(d, "1+0", BigDecimal.valueOf(1 + 0)),
                this.formattedCellWithValue(d.add(1, 1), "2+0", BigDecimal.valueOf(2 + 0)));

        this.countAndCheck(cellStore, 3 + 2);
    }

    @Test
    public void testCopyCells2x2CellInto2x2() {
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

        this.copyCellsAndCheck(
                engine,
                Lists.of(cellA, cellB),
                SpreadsheetRange.with(d, d.add(2, 2)),
                context,
                this.formattedCellWithValue(d, "1+0", BigDecimal.valueOf(1 + 0)),
                this.formattedCellWithValue(d.add(1, 1), "2+0", BigDecimal.valueOf(2 + 0)));

        this.countAndCheck(cellStore, 3 + 2);
    }

    @Test
    public void testCopyCells2x2CellInto7x2Gives6x2() {
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

        this.copyCellsAndCheck(engine,
                Lists.of(cellA, cellB),
                SpreadsheetRange.with(d, d.add(6, 1)),
                context,
                this.formattedCellWithValue(d, "1+0", BigDecimal.valueOf(1 + 0)),
                this.formattedCellWithValue(d.add(1, 1), "2+0", BigDecimal.valueOf(2 + 0)),
                this.formattedCellWithValue(d.add(2, 0), "1+0", BigDecimal.valueOf(1 + 0)),
                this.formattedCellWithValue(d.add(3, 1), "2+0", BigDecimal.valueOf(2 + 0)),
                this.formattedCellWithValue(d.add(4, 0), "1+0", BigDecimal.valueOf(1 + 0)),
                this.formattedCellWithValue(d.add(5, 1), "2+0", BigDecimal.valueOf(2 + 0)));

        this.countAndCheck(cellStore, 3 + 6);
    }

    @Test
    public void testCopyCells2x2CellInto2x7Gives2x6() {
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

        this.copyCellsAndCheck(engine,
                Lists.of(cellA, cellB),
                SpreadsheetRange.with(d, d.add(1, 6)),
                context,
                this.formattedCellWithValue(d, "1+0", BigDecimal.valueOf(1 + 0)),
                this.formattedCellWithValue(d.add(1, 1), "2+0", BigDecimal.valueOf(2 + 0)),
                this.formattedCellWithValue(d.add(0, 2), "1+0", BigDecimal.valueOf(1 + 0)),
                this.formattedCellWithValue(d.add(1, 3), "2+0", BigDecimal.valueOf(2 + 0)),
                this.formattedCellWithValue(d.add(0, 4), "1+0", BigDecimal.valueOf(1 + 0)),
                this.formattedCellWithValue(d.add(1, 5), "2+0", BigDecimal.valueOf(2 + 0)));

        this.countAndCheck(cellStore, 3 + 6);
    }

    @Test
    public void testCopyCellsAbsoluteReference() {
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

        this.copyCellsAndCheck(engine,
                Lists.of(cellB),
                SpreadsheetRange.cell(d),
                context,
                this.formattedCellWithValue(a, "1+0", BigDecimal.valueOf(1 + 0)),
                this.formattedCellWithValue(d, "" + a, BigDecimal.valueOf(1 + 0)));

        this.countAndCheck(cellStore, 2 + 1);
    }

    @Test
    public void testCopyCellsExpressionRelativeReferenceFixed() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell cellB = this.cell("B2", "2");
        final SpreadsheetCell cellC = this.cell("C3", "3+B2");

        cellStore.save(cellB);
        cellStore.save(cellC);

        this.copyCellsAndCheck(engine,
                Lists.of(cellB, cellC),
                SpreadsheetRange.parse("E5:F6"),
                context,
                this.formattedCellWithValue("E5", "2", BigDecimal.valueOf(2 + 0)),
                this.formattedCellWithValue("F6", "3+E5", BigDecimal.valueOf(3 + 2)));

        this.countAndCheck(cellStore, 2 + 2);
    }

    @Test
    public void testCopyCellsExternalReferencesRefreshed() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);
        final SpreadsheetEngineContext context = this.createContext(engine);

        final SpreadsheetCell cellB = this.cell("b1", "2+0"); // copied to C1
        final SpreadsheetCellReference c = this.cellReference("C1"); // copyCell dest...
        final SpreadsheetCell cellA = this.cell("a1", "10+" + c);

        engine.saveCell(cellA, context);
        engine.saveCell(cellB, context);

        this.copyCellsAndCheck(engine,
                Lists.of(cellB),
                SpreadsheetRange.with(c, c),
                context,
                this.formattedCellWithValue(cellA.reference(), "10+" + c, BigDecimal.valueOf(10 + 2 + 0)), // external reference to copied
                this.formattedCellWithValue(c, "2+0", BigDecimal.valueOf(2 + 0))); // copied

        this.countAndCheck(cellStore, 2 + 1);
    }

    //  helpers.......................................................................................................

    @Override
    public BasicSpreadsheetEngine createSpreadsheetEngine() {
        return this.createSpreadsheetEngine(this.cellStore());
    }

    private BasicSpreadsheetEngine createSpreadsheetEngine(final SpreadsheetCellStore cellStore) {
        return this.createSpreadsheetEngine(cellStore,
                SpreadsheetLabelStores.readOnly(this.labelStore()));
    }

    private BasicSpreadsheetEngine createSpreadsheetEngine(final SpreadsheetLabelStore labelStore) {
        return this.createSpreadsheetEngine(this.cellStore(),
                labelStore);
    }

    private BasicSpreadsheetEngine createSpreadsheetEngine(final SpreadsheetCellStore cellStore,
                                                           final SpreadsheetLabelStore labelStore) {
        return this.createSpreadsheetEngine(cellStore,
                labelStore,
                this.conditionalFormattingRules());
    }

    private BasicSpreadsheetEngine createSpreadsheetEngine(final SpreadsheetCellStore cellStore,
                                                           final SpreadsheetLabelStore labelStore,
                                                           final SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> rules) {
        return  BasicSpreadsheetEngine.with(this.id(),
                cellStore,
                labelStore,
                rules,
                this.cellReferencesStore(),
                this.labelReferencesStore(),
                SpreadsheetRangeStores.readOnly(this.rangeToCellStore()));
    }

    private BasicSpreadsheetEngine createSpreadsheetEngine(final SpreadsheetCellStore cellStore,
                                                           final SpreadsheetLabelStore labelStore,
                                                           final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferencesStore) {
        return this.createSpreadsheetEngine(cellStore,
                labelStore,
                cellReferencesStore,
                this.labelReferencesStore());
    }

    private BasicSpreadsheetEngine createSpreadsheetEngine(final SpreadsheetCellStore cellStore,
                                                           final SpreadsheetLabelStore labelStore,
                                                           final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferencesStore,
                                                           final SpreadsheetReferenceStore<SpreadsheetLabelName> labelReferencesStore) {
        return BasicSpreadsheetEngine.with(this.id(),
                cellStore,
                labelStore,
                this.conditionalFormattingRules(),
                cellReferencesStore,
                labelReferencesStore,
                SpreadsheetRangeStores.readOnly(this.rangeToCellStore()));
    }

    @Override
    public SpreadsheetEngineContext createContext() {
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
                        .get().cast();
            }

            @Override
            public Object evaluate(final ExpressionNode node) {
                // throw an exception which is an "error" when the invalidCellReference function appears in a formula and executed
                final BiFunction<ExpressionNodeName, List<Object>, Object> functions = (name, params) -> {
                    if (name.value().equals(SpreadsheetFormula.INVALID_CELL_REFERENCE.value())) {
                        throw new ExpressionEvaluationException("Invalid cell reference: " + params.get(0).toString());
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
                assertEquals(Boolean.class, target, "Only support converting to Boolean=" + value);
                return target.cast(Boolean.parseBoolean(String.valueOf(value)));
            }

            @Override
            public SpreadsheetTextFormatter<?> parseFormatPattern(final String pattern) {
                if (PATTERN_COLOR.equals(pattern)) {
                    return formatter(pattern, COLOR, FORMATTED_PATTERN_SUFFIX);
                }
                if (PATTERN.equals(pattern)) {
                    return formatter(pattern, SpreadsheetFormattedText.WITHOUT_COLOR, FORMATTED_PATTERN_SUFFIX);
                }
                if (PATTERN_FORMAT_FAIL.equals(pattern)) {
                    return SpreadsheetTextFormatters.fixed(Object.class, Optional.empty());
                }

                throw new AssertionError("Unknown pattern=" + pattern + " expected one of " + PATTERN_FORMAT_FAIL + "|" + PATTERN + "|" + PATTERN_COLOR);
            }

            @Override
            public SpreadsheetTextFormatter<?> defaultSpreadsheetTextFormatter() {
                return BasicSpreadsheetEngineTest.this.defaultSpreadsheetTextFormatter();
            }

            @Override
            public Optional<SpreadsheetFormattedText> format(final Object value,
                                                             final SpreadsheetTextFormatter<?> formatter) {
                assertFalse(value instanceof Optional, () -> "Value must not be optional" + value);
                return formatter.format(Cast.to(value), SPREADSHEET_TEXT_FORMAT_CONTEXT);
            }
        };
    }

    private SpreadsheetTextFormatter<Object> defaultSpreadsheetTextFormatter() {
        return this.formatter(PATTERN_DEFAULT,
                SpreadsheetFormattedText.WITHOUT_COLOR,
                FORMATTED_DEFAULT_SUFFIX);
    }

    private SpreadsheetTextFormatter<Object> formatter(final String pattern,
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
                assertNotNull(value, "value");
                assertSame(SPREADSHEET_TEXT_FORMAT_CONTEXT, context, "Wrong SpreadsheetTextFormatContext passed");

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
        return this.formattedCellWithValue(SpreadsheetCellReference.parse(reference),
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

        final SpreadsheetFormattedText formattedText = this.defaultSpreadsheetTextFormatter().format(value, SPREADSHEET_TEXT_FORMAT_CONTEXT)
                .orElseThrow(() -> new AssertionError("Failed to format " + CharSequences.quoteIfChars(value)));
        final Optional<SpreadsheetFormattedCell> formattedCell = Optional.of(SpreadsheetFormattedCell.with(formattedText.text(), this.style()));

        return cell.setFormula(cell.formula()
                .setExpression(this.parseFormula(cell.formula()))
                .setValue(Optional.of(value)))
                .setFormatted(formattedCell);
    }

    private SpreadsheetCell formattedCellWithError(final String reference,
                                                   final String formulaText,
                                                   final String message) {
        return this.formattedCellWithError(SpreadsheetCellReference.parse(reference),
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
        final Optional<SpreadsheetFormattedCell> formattedCell = Optional.of(SpreadsheetFormattedCell.with(errorMessage, this.style()));

        return cell.setFormula(cell.formula()
                .setExpression(this.parseFormula(cell.formula()))
                .setError(Optional.of(SpreadsheetError.with(errorMessage))))
                .setFormatted(formattedCell);
    }

    /**
     * Assumes the formula is syntactically correct and updates the cell.
     */
    private Optional<ExpressionNode> parseFormula(final SpreadsheetFormula formula) {
        final String formulaText = formula.text();
        final SpreadsheetParserToken token = SpreadsheetParsers.expression()
                .parse(TextCursors.charSequence(formulaText),
                        SpreadsheetParserContexts.basic(decimalNumberContext()))
                .orElseThrow(() -> new AssertionError("Failed to parse " + CharSequences.quote(formulaText)))
                .cast();
        return token.expressionNode();
    }

    private void loadCellStoreAndCheck(final SpreadsheetCellStore store,
                                       final SpreadsheetCell...cells) {
        assertEquals(Lists.of(cells),
                store.all(),
                () -> "all cells in " + store);
    }

    private void loadLabelStoreAndCheck(final SpreadsheetLabelStore store,
                                        final SpreadsheetLabelMapping...mappings) {
        assertEquals(Lists.of(mappings),
                store.all(),
                () -> "all mappings in " + store);
    }

    private <E extends ExpressionReference & Comparable<E>> void loadReferencesAndCheck(final SpreadsheetReferenceStore<E> store,
                                                                                        final E cell,
                                                                                        final SpreadsheetCellReference... out) {
        assertEquals(Optional.ofNullable(out.length == 0 ? null : Sets.of(out)),
                store.load(cell),
                "references to " + cell);
    }

    private void loadReferrersAndCheck(final SpreadsheetReferenceStore<SpreadsheetCellReference> store,
                                       final SpreadsheetCellReference cell,
                                       final SpreadsheetCellReference... out) {
        assertEquals(Sets.of(out),
                store.loadReferred(cell),
                "referrers from " + cell);
    }

    private SpreadsheetId id() {
        return SpreadsheetId.with(123);
    }

    private SpreadsheetCellStore cellStore() {
        return SpreadsheetCellStores.treeMap();
    }

    private SpreadsheetLabelStore labelStore() {
        return SpreadsheetLabelStores.treeMap();
    }

    private SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> conditionalFormattingRules() {
        return SpreadsheetRangeStores.treeMap();
    }

    private SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferencesStore() {
        return SpreadsheetReferenceStores.treeMap();
    }

    private SpreadsheetReferenceStore<SpreadsheetLabelName> labelReferencesStore() {
        return SpreadsheetReferenceStores.treeMap();
    }

    private SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCellStore() {
        return SpreadsheetRangeStores.treeMap();
    }

    private SpreadsheetColumnReference column(final int column) {
        return SpreadsheetReferenceKind.ABSOLUTE.column(column);
    }

    private SpreadsheetRowReference row(final int row) {
        return SpreadsheetReferenceKind.ABSOLUTE.row(row);
    }

    private SpreadsheetCellReference cellReference(final String reference) {
        return SpreadsheetCellReference.parse(reference);
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

    private SpreadsheetCell cell(final String reference, final String formula) {
        return this.cell(SpreadsheetCellReference.parse(reference), formula);
    }

    private SpreadsheetCell cell(final SpreadsheetCellReference reference, final String formula) {
        return SpreadsheetCell.with(reference, SpreadsheetFormula.with(formula), this.style());
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
    public Class<BasicSpreadsheetEngine> type() {
        return BasicSpreadsheetEngine.class;
    }

    // TypeNameTesting..........................................................................................

    @Override
    public String typeNameSuffix() {
        return "";
    }
}

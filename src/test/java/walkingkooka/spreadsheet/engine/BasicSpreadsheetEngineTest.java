package walkingkooka.spreadsheet.engine;

import org.junit.Test;
import walkingkooka.convert.Converters;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStores;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserContexts;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParsers;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionEvaluationException;
import walkingkooka.tree.expression.ExpressionNodeName;

import java.math.BigInteger;
import java.math.MathContext;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public final class BasicSpreadsheetEngineTest extends SpreadsheetEngineTestCase<BasicSpreadsheetEngine> {

    @Test(expected = NullPointerException.class)
    public void testNullIdFails() {
        BasicSpreadsheetEngine.with(null, this.cellStore(), this.labelStore(), this.parser(), this.parserContext(), this.evaluationContextFactory());
    }

    @Test(expected = NullPointerException.class)
    public void testNullCellStoreFails() {
        BasicSpreadsheetEngine.with(this.id(), null, this.labelStore(), this.parser(), this.parserContext(), this.evaluationContextFactory());
    }

    @Test(expected = NullPointerException.class)
    public void testNullLabelStoreFails() {
        BasicSpreadsheetEngine.with(this.id(), this.cellStore(), null,this.parser(), this.parserContext(), this.evaluationContextFactory());
    }

    @Test(expected = NullPointerException.class)
    public void testNullParserFails() {
        BasicSpreadsheetEngine.with(this.id(), this.cellStore(), this.labelStore(),null, this.parserContext(), this.evaluationContextFactory());
    }

    @Test(expected = NullPointerException.class)
    public void testNullParserContextFails() {
        BasicSpreadsheetEngine.with(this.id(), this.cellStore(), this.labelStore(), this.parser(), null, this.evaluationContextFactory());
    }

    @Test(expected = NullPointerException.class)
    public void testNullEvaluationContextFactoryFails() {
        BasicSpreadsheetEngine.with(this.id(), this.cellStore(), this.labelStore(), this.parser(), this.parserContext(), null);
    }

    @Test
    public void testLoadCellCellWhenEmpty() {
        this.loadCellFailCheck(cellReference(1, 1), SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY);
    }

    @Test
    public void testSaveCellAndLoadCellSkipEvaluate() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);

        final SpreadsheetCellReference cellReference = this.cellReference(1, 1);
        cellStore.save(SpreadsheetCell.with(cellReference, SpreadsheetFormula.with("1+2")));

        this.loadCellAndCheckWithoutValueOrError(engine,
                cellReference,
                SpreadsheetEngineLoading.SKIP_EVALUATE);
    }

    @Test
    public void testSaveCellAndLoadCell() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);

        final SpreadsheetCellReference cellReference = this.cellReference(1, 1);
        cellStore.save(SpreadsheetCell.with(cellReference, SpreadsheetFormula.with("1+2")));

        this.loadCellAndCheckValue(engine,
                cellReference,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(1+2));
    }

    @Test
    public void testLoadCellComputeIfNecessaryCachesCell() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);

        final SpreadsheetCellReference cellReference = this.cellReference(1, 1);
        cellStore.save(SpreadsheetCell.with(cellReference, SpreadsheetFormula.with("1+2")));

        final SpreadsheetCell first = this.loadCellOrFail(engine, cellReference, SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY);
        final SpreadsheetCell second = this.loadCellOrFail(engine, cellReference, SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY);

        assertSame("different instances of SpreadsheetCell returned not cached", first, second);
    }

    @Test
    public void testLoadCellComputeIfNecessaryCachesCellWithInvalidFormulaAndErrorCached() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);

        final SpreadsheetCellReference cellReference = this.cellReference(1, 1);
        cellStore.save(SpreadsheetCell.with(cellReference, SpreadsheetFormula.with("1+2+")));

        final SpreadsheetCell first = this.loadCellOrFail(engine, cellReference, SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY);
        assertNotEquals("Expected error absent=" + first, SpreadsheetCell.NO_ERROR, first.error());

        final SpreadsheetCell second = this.loadCellOrFail(engine, cellReference, SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY);
        assertSame("different instances of SpreadsheetCell returned not cached", first, second);
    }

    @Test
    public void testLoadCellForceRecomputeIgnoresCache() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);

        final SpreadsheetCellReference cellReference = this.cellReference(1, 1);
        cellStore.save(SpreadsheetCell.with(cellReference, SpreadsheetFormula.with("1+2")));

        final SpreadsheetCell first = this.loadCellOrFail(engine, cellReference, SpreadsheetEngineLoading.FORCE_RECOMPUTE);
        final SpreadsheetCell second = this.loadCellOrFail(engine, cellReference, SpreadsheetEngineLoading.FORCE_RECOMPUTE);

        assertNotSame("different instances of SpreadsheetCell returned not cached", first, second);
    }

    @Test
    public void testLoadCellForceRecomputeIgnoresCache2() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);

        final SpreadsheetCellReference cellReference = this.cellReference(1, 1);
        cellStore.save(SpreadsheetCell.with(cellReference, SpreadsheetFormula.with("1+2")));

        final SpreadsheetCell first = this.loadCellOrFail(engine, cellReference, SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY);
        final SpreadsheetCell second = this.loadCellOrFail(engine, cellReference, SpreadsheetEngineLoading.FORCE_RECOMPUTE);

        assertNotSame("different instances of SpreadsheetCell returned not cached", first, second);
    }

    @Test
    public void testLoadCellComputeThenSkipEvaluate() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);

        final SpreadsheetCellReference cellReference = this.cellReference(1, 1);
        cellStore.save(SpreadsheetCell.with(cellReference, SpreadsheetFormula.with("1+2")));

        final SpreadsheetCell first = this.loadCellOrFail(engine, cellReference, SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY);
        final SpreadsheetCell second = this.loadCellOrFail(engine, cellReference, SpreadsheetEngineLoading.SKIP_EVALUATE);

        assertSame("different instances of SpreadsheetCell returned not cached", first, second);
    }

    @Test
    public void testSaveCellAndLoadCellMany() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);

        final SpreadsheetCellReference a = this.cellReference(1, 1);
        final SpreadsheetCellReference b = this.cellReference(2, 1);
        final SpreadsheetCellReference c = this.cellReference(3, 1);

        cellStore.save(SpreadsheetCell.with(a, SpreadsheetFormula.with("1+2")));
        cellStore.save(SpreadsheetCell.with(b, SpreadsheetFormula.with("3+4")));
        cellStore.save(SpreadsheetCell.with(c, SpreadsheetFormula.with("5+6")));

        this.loadCellAndCheckValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(1+2));
        this.loadCellAndCheckValue(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(3+4));
        this.loadCellAndCheckValue(engine,
                c,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(5+6));
    }

    @Test
    public void testLoadCellValueCellReferenceInvalidFails() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        cellStore.save(SpreadsheetCell.with(a, SpreadsheetFormula.with("X99")));

        this.loadCellAndCheckError(engine, a, SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY, "Unknown cell reference");
    }

    @Test
    public void testLoadCellValueLabelInvalidFails() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        cellStore.save(SpreadsheetCell.with(a, SpreadsheetFormula.with("INVALIDLABEL")));

        this.loadCellAndCheckError(engine, a, SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY, "Unknown label");
    }

    @Test
    public void testLoadCellValueIsCellReference() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // B1

        cellStore.save(SpreadsheetCell.with(a, SpreadsheetFormula.with("B1")));
        cellStore.save(SpreadsheetCell.with(b, SpreadsheetFormula.with("3+4")));

        // formula
        this.loadCellAndCheckValue(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(3+4));

        // reference to B1 which has formula
        this.loadCellAndCheckValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(3+4));
    }

    @Test
    public void testLoadCallValueIsLabel() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // B1

        cellStore.save(SpreadsheetCell.with(a, SpreadsheetFormula.with(LABEL.value())));
        cellStore.save(SpreadsheetCell.with(b, SpreadsheetFormula.with("3+4")));

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, b));

        // formula
        this.loadCellAndCheckValue(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(3+4));

        // reference to B1 which has formula
        this.loadCellAndCheckValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(3+4));
    }

    // deleteColumn....................................................................................................

    @Test
    public void testDeleteColumnZero() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

        final SpreadsheetCellReference reference = this.cellReference(99, 0); // A3

        cellStore.save(this.cell(reference, "99+0"));

        engine.deleteColumns(reference.column(), 0);
        this.countAndCheck(cellStore, 1);

        this.loadCellAndCheckFormulaAndValue(engine,
                reference,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "99+0",
                BigInteger.valueOf(99));
    }

    @Test
    public void testDeleteColumn() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // A2
        final SpreadsheetCellReference c = this.cellReference(2, 0); // A3

        cellStore.save(this.cell(a, "1+2"));
        cellStore.save(this.cell(b, "3+4"));
        cellStore.save(this.cell(c, "5+6"));

        engine.deleteColumns(b.column(), 1);
        this.countAndCheck(cellStore, 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "1+2",
                BigInteger.valueOf(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "5+6",
                BigInteger.valueOf(5 + 6));
    }

    @Test
    public void testDeleteColumn2() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // A2 replaced by c
        final SpreadsheetCellReference c = this.cellReference(2, 0); // A3 DELETED
        final SpreadsheetCellReference d = this.cellReference(99, 1); // B99 moved

        cellStore.save(this.cell(a, "1+2"));
        cellStore.save(this.cell(b, "3+4"));
        cellStore.save(this.cell(c, "5+6"));
        cellStore.save(this.cell(d, "7+8"));

        final int count = 1;
        engine.deleteColumns(b.column(), count); // $b delete, $c,$d columns -1.
        this.countAndCheck(cellStore, 3);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "1+2",
                BigInteger.valueOf(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "5+6",
                BigInteger.valueOf(5 + 6));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(-count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "7+8",
                BigInteger.valueOf(7 + 8));
    }

    @Test
    public void testDeleteColumnWithLabels() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

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
        engine.deleteColumns(b.column(), count); // $b delete, $c,$d columns -1.

        this.loadLabelAndCheck(labelStore, LABEL, e.add(-count, 0));

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "1+" + LABEL,
                BigInteger.valueOf(1 + 99));

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "3+0",
                BigInteger.valueOf(3));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(-count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "4+" + LABEL,
                BigInteger.valueOf(4 + 99));

        this.loadCellAndCheckFormulaAndValue(engine,
                e.add(-count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "99+0",
                BigInteger.valueOf(99 + 0));
    }

    @Test
    public void testDeleteColumnWithLabelToDeletedCell() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // A2

        cellStore.save(this.cell(a, "1+0"));
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, b));

        final int count = 1;
        engine.deleteColumns(b.column(), count); // $b delete, $c,$d columns -1.

        this.loadLabelFailCheck(labelStore, LABEL);

        this.countAndCheck(cellStore, 1);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "1+0",
                BigInteger.valueOf(1 + 0));
    }

    @Test
    public void testDeleteColumnWithReferences() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

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
        engine.deleteColumns(c.column(), count); // $c delete

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "1+" + d.add(-count, 0),
                BigInteger.valueOf(1 + 4)); // reference should have been fixed.

        this.loadCellAndCheckValue(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(2));

        this.loadCellAndCheckValue(engine,
                d.add(-count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(4));

        this.loadCellAndCheckFormulaAndValue(engine,
                e.add(-count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "5+" + b,
                BigInteger.valueOf(5 + 2));
    }

    @Test
    public void testDeleteColumnWithReferences2() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

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
        engine.deleteColumns(c.column(), count); // $c delete

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "1+" + d.add(-count, 0),
                BigInteger.valueOf(1 + 4)); // reference should have been fixed.

        this.loadCellAndCheckValue(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(2));

        this.loadCellAndCheckValue(engine,
                d.add(-count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(4));

        this.loadCellAndCheckFormulaAndValue(engine,
                e.add(-count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "5+" + b,
                BigInteger.valueOf(5 + 2));
    }

    @Test
    public void testDeleteColumnWithReferencesToDeletedCell() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // A2

        cellStore.save(this.cell(a, "1+" + b));
        cellStore.save(this.cell(b, "2"));

        final int count = 1;
        engine.deleteColumns(b.column(), count); // $c delete

        this.countAndCheck(cellStore, 1);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "1+InvalidCellReference(" + b + ")",
                "Invalid cell reference " + b); // reference should have been fixed.
    }

    @Test
    public void testDeleteColumnSeveral() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

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
        engine.deleteColumns(this.column(7), count); // $b & $c

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(1));

        this.loadCellAndCheckValue(engine,
                d.add(-count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(4));

        this.loadCellAndCheckValue(engine,
                e.add(-count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(5));

        this.loadCellAndCheckValue(engine,
                f.add(-count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(6));
    }

    // deleteRow....................................................................................................

    @Test
    public void testDeleteRowZero() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

        final SpreadsheetCellReference reference = this.cellReference(0, 1); // A2

        cellStore.save(this.cell(reference, "99+0"));

        engine.deleteRows(reference.row(), 0);

        this.countAndCheck(cellStore, 1);

        this.loadCellAndCheckFormulaAndValue(engine,
                reference,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "99+0",
                BigInteger.valueOf(99));
    }

    @Test
    public void testDeleteRow() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(0, 1); // B1
        final SpreadsheetCellReference c = this.cellReference(0, 2); // C1

        cellStore.save(this.cell(a, "1+2"));
        cellStore.save(this.cell(b, "3+4"));
        cellStore.save(this.cell(c, "5+6"));

        engine.deleteRows(b.row(), 1);

        this.countAndCheck(cellStore, 2);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "1+2",
                BigInteger.valueOf(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "5+6",
                BigInteger.valueOf(5 + 6));
    }

    @Test
    public void testDeleteRow2() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

        final SpreadsheetCellReference a = this.cellReference(0, 0); //
        final SpreadsheetCellReference b = this.cellReference(0, 1); // replaced by c
        final SpreadsheetCellReference c = this.cellReference(0, 2); // DELETED
        final SpreadsheetCellReference d = this.cellReference(1, 9); // moved

        cellStore.save(this.cell(a, "1+2"));
        cellStore.save(this.cell(b, "3+4"));
        cellStore.save(this.cell(c, "5+6"));
        cellStore.save(this.cell(d, "7+8"));

        final int count = 1;
        engine.deleteRows(b.row(), count); // $b delete

        this.countAndCheck(cellStore, 3);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "1+2",
                BigInteger.valueOf(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "5+6",
                BigInteger.valueOf(5 + 6));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(0, -count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "7+8",
                BigInteger.valueOf(7 + 8));
    }

    @Test
    public void testDeleteRowWithLabels() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(0, 1); // B1 replaced by c
        final SpreadsheetCellReference c = this.cellReference(0, 2); // C1 DELETED
        final SpreadsheetCellReference d = this.cellReference(8, 13); // I13 moved
        final SpreadsheetCellReference e = this.cellReference(9, 14); // J14 moved LABEL=

        cellStore.save(this.cell(a, "1+" + LABEL));
        cellStore.save(this.cell(b, "2+0"));
        cellStore.save(this.cell(c, "3+0"));
        cellStore.save(this.cell(d, "4+" + LABEL));
        cellStore.save(this.cell(e, "99+0")); // LABEL=

        labelStore.save(SpreadsheetLabelMapping.with(LABEL, e));

        final int count = 1;
        engine.deleteRows(b.row(), count); // $b delete, $c,$d columns -1.

        this.countAndCheck(cellStore, 4);

        this.loadLabelAndCheck(labelStore, LABEL, e.add(0, -count));

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "1+" + LABEL,
                BigInteger.valueOf(1 + 99));

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "3+0",
                BigInteger.valueOf(3));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(0, -count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "4+" + LABEL,
                BigInteger.valueOf(4 + 99));

        this.loadCellAndCheckFormulaAndValue(engine,
                e.add(0, -count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "99+0",
                BigInteger.valueOf(99 + 0));
    }

    @Test
    public void testDeleteRowWithLabelToDeletedCell() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(0, 1); // B1

        cellStore.save(this.cell(a, "1+0"));
        labelStore.save(SpreadsheetLabelMapping.with(LABEL, b));

        final int count = 1;
        engine.deleteRows(b.row(), count); // $b delete

        this.loadLabelFailCheck(labelStore, LABEL);

        this.countAndCheck(cellStore, 1);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "1+0",
                BigInteger.valueOf(1 + 0));
    }

    @Test
    public void testDeleteRowWithReferences() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

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
        engine.deleteRows(c.row(), count); // $c delete

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "1+" + d.add(0, -count),
                BigInteger.valueOf(1 + 4)); // reference should have been fixed.

        this.loadCellAndCheckValue(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(2));

        this.loadCellAndCheckValue(engine,
                d.add(0, -count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(4));

        this.loadCellAndCheckFormulaAndValue(engine,
                e.add(0, -count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "5+" + b,
                BigInteger.valueOf(5 + 2));
    }

    @Test
    public void testDeleteRowWithReferences2() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

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
        engine.deleteRows(c.row(), count); // $c delete

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "1+" + d.add(0, -count),
                BigInteger.valueOf(1 + 4)); // reference should have been fixed.

        this.loadCellAndCheckValue(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(2));

        this.loadCellAndCheckValue(engine,
                d.add(0, -count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(4));

        this.loadCellAndCheckFormulaAndValue(engine,
                e.add(0, -count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "5+" + b,
                BigInteger.valueOf(5 + 2));
    }

    @Test
    public void testDeleteRowWithReferencesToDeletedCell() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(0, 1); // B1

        cellStore.save(this.cell(a, "1+" + b));
        cellStore.save(this.cell(b, "2"));

        final int count = 1;
        engine.deleteRows(b.row(), count); // $c delete

        this.countAndCheck(cellStore, 1);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "1+InvalidCellReference(" + b + ")",
                "Invalid cell reference " + b); // reference should have been fixed.;
    }

    @Test
    public void testDeleteRowSeveral() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(0, 10); // H1 DELETED
        final SpreadsheetCellReference c = this.cellReference(0, 11); // I1 DELETED
        final SpreadsheetCellReference d = this.cellReference(2, 12); // L3
        final SpreadsheetCellReference e = this.cellReference(3, 20); // T4
        final SpreadsheetCellReference f = this.cellReference(4, 21); // U5

        cellStore.save(this.cell(a, "1"));
        cellStore.save(this.cell(b, "2"));
        cellStore.save(this.cell(c, "3"));
        cellStore.save(this.cell(d, "4"));
        cellStore.save(this.cell(e, "5"));
        cellStore.save(this.cell(f, "6"));

        final int count = 5;
        engine.deleteRows(this.row(7), count); // $b & $c

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(1));

        this.loadCellAndCheckValue(engine,
                d.add(0, -count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(4));

        this.loadCellAndCheckValue(engine,
                e.add(0, -count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(5));

        this.loadCellAndCheckValue(engine,
                f.add(0, -count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(6));
    }

    // insertColumn....................................................................................................

    @Test
    public void testInsertColumnZero() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

        final SpreadsheetCellReference reference = this.cellReference(99, 0); // A3

        cellStore.save(this.cell(reference, "99+0"));

        engine.insertColumns(reference.column(), 0);

        this.countAndCheck(cellStore, 1);

        this.loadCellAndCheckFormulaAndValue(engine,
                reference,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "99+0",
                BigInteger.valueOf(99));
    }

    @Test
    public void testInsertColumn() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // A2
        final SpreadsheetCellReference c = this.cellReference(2, 0); // A3

        cellStore.save(this.cell(a, "1+2"));
        cellStore.save(this.cell(b, "3+4"));
        cellStore.save(this.cell(c, "5+6"));

        final int count = 1;
        engine.insertColumns(b.column(), count);

        this.countAndCheck(cellStore, 3);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "1+2",
                BigInteger.valueOf(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "3+4",
                BigInteger.valueOf(3 + 4));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "5+6",
                BigInteger.valueOf(5 + 6));
    }

    @Test
    public void testInsertColumn2() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // A2 MOVED
        final SpreadsheetCellReference c = this.cellReference(2, 0); // A3 MOVED

        cellStore.save(this.cell(a, "1+2"));
        cellStore.save(this.cell(b, "3+4"));
        cellStore.save(this.cell(c, "5+6"));

        final int count = 1;
        engine.insertColumns(b.column(), count); // $b insert, $c,$d columns -1.

        this.countAndCheck(cellStore, 3);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "1+2",
                BigInteger.valueOf(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(+count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "3+4",
                BigInteger.valueOf(3 + 4));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(+count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "5+6",
                BigInteger.valueOf(5 + 6));
    }

    @Test
    public void testInsertColumnWithLabels() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

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
        engine.insertColumns(b.column(), count); // $b insert

        this.loadLabelAndCheck(labelStore, LABEL, d.add(+count, 0));

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "1+" + LABEL,
                BigInteger.valueOf(1 + 99));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(+count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "2+0",
                BigInteger.valueOf(2 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(+count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "3+0+" + LABEL,
                BigInteger.valueOf(3 + 99));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(+count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "99+0",
                BigInteger.valueOf(99 + 0));
    }

    @Test
    public void testInsertColumnWithReferences() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // A2
        final SpreadsheetCellReference c = this.cellReference(10, 0); // A10 moved
        final SpreadsheetCellReference d = this.cellReference(13, 8); // H13 moved

        cellStore.save(this.cell(a, "1+0+" + d));
        cellStore.save(this.cell(b, "2+0"));
        cellStore.save(this.cell(c, "3+0"));
        cellStore.save(this.cell(d, "4+0+" + b));

        final int count = 1;
        engine.insertColumns(c.column(), count); // $c insert

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "1+0+" + d.add(+count, 0),
                BigInteger.valueOf(1 + 0 + 4 + 2)); // reference should have been fixed.

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "2+0",
                BigInteger.valueOf(2));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(+count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "3+0",
                BigInteger.valueOf(3));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(+count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "4+0+" + b,
                BigInteger.valueOf(4 + 2));
    }

    @Test
    public void testInsertColumnWithReferences2() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(1, 0); // A2
        final SpreadsheetCellReference c = this.cellReference(10, 0); // A10 moved
        final SpreadsheetCellReference d = this.cellReference(13, 8); // H13 moved

        cellStore.save(this.cell(a, "1+0+" + d));
        cellStore.save(this.cell(b, "2+0"));
        cellStore.save(this.cell(c, "3+0"));
        cellStore.save(this.cell(d, "4+0+" + b)); // =5+2

        final int count = 2;
        engine.insertColumns(c.column(), count); // $c insert

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "1+0+" + d.add(+count, 0),
                BigInteger.valueOf(1 + 0 + 4 + 2)); // reference should have been fixed.

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "2+0",
                BigInteger.valueOf(2 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(+count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "3+0",
                BigInteger.valueOf(3 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(+count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "4+0+" + b,
                BigInteger.valueOf(4 + 0 + 2));
    }

    @Test
    public void testInsertColumnSeveral() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(10, 0); // A2 MOVED
        final SpreadsheetCellReference c = this.cellReference(11, 0); // A3 MOVED
        final SpreadsheetCellReference d = this.cellReference(12, 2); // C4 MOVED
        final SpreadsheetCellReference e = this.cellReference(20, 3); // T3 MOVED

        cellStore.save(this.cell(a, "1+0"));
        cellStore.save(this.cell(b, "2+0"));
        cellStore.save(this.cell(c, "3+0"));
        cellStore.save(this.cell(d, "4+0"));
        cellStore.save(this.cell(e, "5+0"));

        final int count = 5;
        engine.insertColumns(this.column(7), count); // $b & $c

        this.countAndCheck(cellStore, 5);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "1+0",
                BigInteger.valueOf(1 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(+count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "2+0",
                BigInteger.valueOf(2 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(+count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "3+0",
                BigInteger.valueOf(3 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(+count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "4+0",
                BigInteger.valueOf(4 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                e.add(+count, 0),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "5+0",
                BigInteger.valueOf(5 + 0));
    }

    // insertRow....................................................................................................

    @Test
    public void testInsertRowZero() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

        final SpreadsheetCellReference reference = this.cellReference(0, 99); // A3

        cellStore.save(this.cell(reference, "99+0"));

        engine.insertRows(reference.row(), 0);

        this.countAndCheck(cellStore, 1);

        this.loadCellAndCheckFormulaAndValue(engine,
                reference,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "99+0",
                BigInteger.valueOf(99));
    }

    @Test
    public void testInsertRow() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(0, 1); // A2
        final SpreadsheetCellReference c = this.cellReference(0, 2); // A3

        cellStore.save(this.cell(a, "1+2"));
        cellStore.save(this.cell(b, "3+4"));
        cellStore.save(this.cell(c, "5+6"));

        final int count = 1;
        engine.insertRows(b.row(), count);

        this.countAndCheck(cellStore, 3);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "1+2",
                BigInteger.valueOf(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(0, count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "3+4",
                BigInteger.valueOf(3 + 4));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(0, count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "5+6",
                BigInteger.valueOf(5 + 6));
    }

    @Test
    public void testInsertRow2() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(0, 1); // A2 MOVED
        final SpreadsheetCellReference c = this.cellReference(0, 2); // A3 MOVED

        cellStore.save(this.cell(a, "1+2"));
        cellStore.save(this.cell(b, "3+4"));
        cellStore.save(this.cell(c, "5+6"));

        final int count = 1;
        engine.insertRows(b.row(), count); // $b insert

        this.countAndCheck(cellStore, 3);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "1+2",
                BigInteger.valueOf(1 + 2));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(0, +count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "3+4",
                BigInteger.valueOf(3 + 4));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(0, +count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "5+6",
                BigInteger.valueOf(5 + 6));
    }

    @Test
    public void testInsertRowWithLabels() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

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
        engine.insertRows(b.row(), count); // $b insert

        this.loadLabelAndCheck(labelStore, LABEL, d.add(0, +count));

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "1+" + LABEL,
                BigInteger.valueOf(1 + 99));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(0, +count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "2+0",
                BigInteger.valueOf(2 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(0, +count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "3+0+" + LABEL,
                BigInteger.valueOf(3 + 99));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(0, +count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "99+0",
                BigInteger.valueOf(99 + 0));
    }

    @Test
    public void testInsertRowWithReferences() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(0, 1); // A2
        final SpreadsheetCellReference c = this.cellReference(0, 10); // A10 moved
        final SpreadsheetCellReference d = this.cellReference(8, 13); // H13 moved

        cellStore.save(this.cell(a, "1+0+" + d));
        cellStore.save(this.cell(b, "2+0"));
        cellStore.save(this.cell(c, "3+0"));
        cellStore.save(this.cell(d, "4+0+" + b));

        final int count = 1;
        engine.insertRows(c.row(), count); // $c insert

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "1+0+" + d.add(0, +count),
                BigInteger.valueOf(1 + 0 + 4 + 2)); // reference should have been fixed.

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "2+0",
                BigInteger.valueOf(2));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(0, +count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "3+0",
                BigInteger.valueOf(3));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(0, +count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "4+0+" + b,
                BigInteger.valueOf(4 + 2));
    }

    @Test
    public void testInsertRowWithReferences2() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

        final SpreadsheetCellReference a = this.cellReference(0, 0); // A1
        final SpreadsheetCellReference b = this.cellReference(0, 1); // A2
        final SpreadsheetCellReference c = this.cellReference(0, 10); // A10 moved
        final SpreadsheetCellReference d = this.cellReference(8, 13); // H13 moved

        cellStore.save(this.cell(a, "1+0+" + d));
        cellStore.save(this.cell(b, "2+0"));
        cellStore.save(this.cell(c, "3+0"));
        cellStore.save(this.cell(d, "4+0+" + b)); // =5+2

        final int count = 2;
        engine.insertRows(c.row(), count); // $c insert

        this.countAndCheck(cellStore, 4);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "1+0+" + d.add(0, +count),
                BigInteger.valueOf(1 + 0 + 4 + 2)); // reference should have been fixed.

        this.loadCellAndCheckFormulaAndValue(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "2+0",
                BigInteger.valueOf(2 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(0, +count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "3+0",
                BigInteger.valueOf(3 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(0, +count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "4+0+" + b,
                BigInteger.valueOf(4 + 0 + 2));
    }

    @Test
    public void testInsertRowSeveral() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetLabelStore labelStore = this.labelStore();
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine(cellStore, labelStore);

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
        engine.insertRows(this.row(7), count); // $b & $c

        this.countAndCheck(cellStore, 5);

        this.loadCellAndCheckFormulaAndValue(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "1+0",
                BigInteger.valueOf(1 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                b.add(0, +count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "2+0",
                BigInteger.valueOf(2 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                c.add(0, +count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "3+0",
                BigInteger.valueOf(3 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                d.add(0, +count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "4+0",
                BigInteger.valueOf(4 + 0));

        this.loadCellAndCheckFormulaAndValue(engine,
                e.add(0, +count),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                "5+0",
                BigInteger.valueOf(5 + 0));
    }

    //  helpers.......................................................................................................
    
    @Override
    BasicSpreadsheetEngine createSpreadsheetEngine() {
        return this.createSpreadsheetEngine(this.cellStore());
    }

    private BasicSpreadsheetEngine createSpreadsheetEngine(final SpreadsheetCellStore cellStore) {
        return this.createSpreadsheetEngine(cellStore, this.labelStore());
    }

    private BasicSpreadsheetEngine createSpreadsheetEngine(final SpreadsheetCellStore cellStore,
                                                           final SpreadsheetLabelStore labelStore) {
        return BasicSpreadsheetEngine.with(this.id(), cellStore, labelStore, this.parser(), this.parserContext(), this.evaluationContextFactory(labelStore));
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

    private Parser<SpreadsheetParserToken, SpreadsheetParserContext> parser() {
        final Parser<SpreadsheetParserToken, SpreadsheetParserContext> number = Parsers.<SpreadsheetParserContext>bigInteger(10)
                .transform((numberParserToken, parserContext) -> SpreadsheetParserToken.bigInteger(numberParserToken.value(), numberParserToken.text()))
                .cast();

        return SpreadsheetParsers.expression(number);
    }

    private SpreadsheetParserContext parserContext() {
        return SpreadsheetParserContexts.basic();
    }

    private Function<SpreadsheetEngine, ExpressionEvaluationContext> evaluationContextFactory() {
        return this.evaluationContextFactory(SpreadsheetLabelStores.fake());
    }

    private Function<SpreadsheetEngine, ExpressionEvaluationContext> evaluationContextFactory(final SpreadsheetLabelStore labelStore) {
        final BiFunction<ExpressionNodeName, List<Object>, Object> functions = (name, params) -> {
            if(name.value().equals(SpreadsheetFormula.INVALID_CELL_REFERENCE.value())) {
                throw new ExpressionEvaluationException("Invalid cell reference " + params.get(0).toString());
            }
            throw new UnsupportedOperationException(name+ "(" + params.stream().map(p->p.toString()).collect(Collectors.joining(",")) + ")");
        };

        return SpreadsheetEngines.spreadsheetEngineExpressionEvaluationContextFunction(
                functions,
                labelStore,
                MathContext.DECIMAL32,
                Converters.simple()
        );
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

    private SpreadsheetCell cell(final SpreadsheetCellReference reference, final String formula) {
        return SpreadsheetCell.with(reference, SpreadsheetFormula.with(formula));
    }

    @Override
    protected Class<BasicSpreadsheetEngine> type() {
        return BasicSpreadsheetEngine.class;
    }
}

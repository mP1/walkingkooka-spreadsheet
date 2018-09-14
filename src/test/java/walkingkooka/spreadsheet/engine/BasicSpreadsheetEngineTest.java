package walkingkooka.spreadsheet.engine;

import org.junit.Test;
import walkingkooka.convert.Converters;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserContexts;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParsers;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;

import java.math.BigInteger;
import java.math.MathContext;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public final class BasicSpreadsheetEngineTest extends SpreadsheetEngineTestCase<BasicSpreadsheetEngine> {

    @Test(expected = NullPointerException.class)
    public void testNullIdFails() {
        BasicSpreadsheetEngine.with(null, this.cellStore(), this.parser(), this.parserContext(), this.evaluationContext());
    }

    @Test(expected = NullPointerException.class)
    public void testNullCellStoreFails() {
        BasicSpreadsheetEngine.with(this.id(), null, this.parser(), this.parserContext(), this.evaluationContext());
    }

    @Test(expected = NullPointerException.class)
    public void testNullParserFails() {
        BasicSpreadsheetEngine.with(this.id(), this.cellStore(), null, this.parserContext(), this.evaluationContext());
    }

    @Test(expected = NullPointerException.class)
    public void testNullParserContextFails() {
        BasicSpreadsheetEngine.with(this.id(), this.cellStore(), this.parser(), null, this.evaluationContext());
    }

    @Test(expected = NullPointerException.class)
    public void testNullEvaluationContextFails() {
        BasicSpreadsheetEngine.with(this.id(), this.cellStore(), this.parser(), this.parserContext(), null);
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

        this.loadCellAndCheck(engine,
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

        this.loadCellAndCheck(engine,
                a,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(1+2));
        this.loadCellAndCheck(engine,
                b,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(3+4));
        this.loadCellAndCheck(engine,
                c,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(5+6));
    }

    @Test
    public void testSetAndGetLabel() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        engine.setLabel(LABEL, REFERENCE);
        this.labelAndCheck(engine, LABEL, REFERENCE);
    }

    @Test
    public void testSetAndRemoveAndGetLabel() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();
        engine.setLabel(LABEL, REFERENCE);
        engine.removeLabel(LABEL);
        this.labelFails(engine, LABEL);
    }

    @Override
    BasicSpreadsheetEngine createSpreadsheetEngine() {
        return BasicSpreadsheetEngine.with(this.id(), this.cellStore(), this.parser(), this.parserContext(), this.evaluationContext());
    }

    private BasicSpreadsheetEngine createSpreadsheetEngine(final SpreadsheetCellStore cellStore) {
        return BasicSpreadsheetEngine.with(this.id(), cellStore, this.parser(), this.parserContext(), this.evaluationContext());
    }

    private SpreadsheetId id() {
        return SpreadsheetId.with(123);
    }

    private SpreadsheetCellStore cellStore() {
        return SpreadsheetCellStores.basic();
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

    private ExpressionEvaluationContext evaluationContext() {
        return new FakeExpressionEvaluationContext() {
            @Override
            public MathContext mathContext() {
                return MathContext.DECIMAL32;
            }

            @Override
            public <T> T convert(final Object value, final Class<T> target) {
                return Converters.simple().convert(value, target);
            }
        };
    }

    private SpreadsheetCellReference cellReference(final int column, final int row) {
        return SpreadsheetCellReference.with(SpreadsheetReferenceKind.ABSOLUTE.column(column), SpreadsheetReferenceKind.ABSOLUTE.row(row));
    }

    @Override
    protected Class<BasicSpreadsheetEngine> type() {
        return BasicSpreadsheetEngine.class;
    }
}

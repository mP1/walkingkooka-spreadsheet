package walkingkooka.spreadsheet.engine;

import org.junit.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.convert.Converters;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserContexts;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParsers;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionEvaluationContexts;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;

import java.math.BigInteger;
import java.math.MathContext;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public final class BasicSpreadsheetEngineTest extends SpreadsheetEngineTestCase<BasicSpreadsheetEngine> {

    @Test
    public void testLoadWhenEmpty() {
        this.loadAndCheck(Sets.of(cellReference(1, 1)), SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY);
    }

    @Test
    public void testSetAndLoad() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();

        final SpreadsheetCellReference cellReference = this.cellReference(1, 1);
        engine.set(SpreadsheetCell.with(cellReference, SpreadsheetFormula.with("1+2")));

        this.loadAndCheck(engine,
                Sets.of(cellReference),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(1+2));
    }

    @Test
    public void testLoadComputeIfNecessaryCachesCell() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();

        final SpreadsheetCellReference cellReference = this.cellReference(1, 1);
        engine.set(SpreadsheetCell.with(cellReference, SpreadsheetFormula.with("1+2")));

        final SpreadsheetCell first = this.loadFirst(engine, cellReference, SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY);
        final SpreadsheetCell second = this.loadFirst(engine, cellReference, SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY);

        assertSame("different instances of SpreadsheetCell returned not cached", first, second);
    }

    @Test
    public void testLoadComputeIfNecessaryCachesCellWithInvalidFormulaAndErrorCached() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();

        final SpreadsheetCellReference cellReference = this.cellReference(1, 1);
        engine.set(SpreadsheetCell.with(cellReference, SpreadsheetFormula.with("1+2+")));

        final SpreadsheetCell first = this.loadFirst(engine, cellReference, SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY);
        assertNotEquals("Expected error absent=" + first, SpreadsheetCell.NO_ERROR, first.error());

        final SpreadsheetCell second = this.loadFirst(engine, cellReference, SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY);
        assertSame("different instances of SpreadsheetCell returned not cached", first, second);
    }

    @Test
    public void testLoadForceRecomputeIgnoresCache() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();

        final SpreadsheetCellReference cellReference = this.cellReference(1, 1);
        engine.set(SpreadsheetCell.with(cellReference, SpreadsheetFormula.with("1+2")));

        final SpreadsheetCell first = this.loadFirst(engine, cellReference, SpreadsheetEngineLoading.FORCE_RECOMPUTE);
        final SpreadsheetCell second = this.loadFirst(engine, cellReference, SpreadsheetEngineLoading.FORCE_RECOMPUTE);

        assertNotSame("different instances of SpreadsheetCell returned not cached", first, second);
    }

    @Test
    public void testLoadForceRecomputeIgnoresCache2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();

        final SpreadsheetCellReference cellReference = this.cellReference(1, 1);
        engine.set(SpreadsheetCell.with(cellReference, SpreadsheetFormula.with("1+2")));

        final SpreadsheetCell first = this.loadFirst(engine, cellReference, SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY);
        final SpreadsheetCell second = this.loadFirst(engine, cellReference, SpreadsheetEngineLoading.FORCE_RECOMPUTE);

        assertNotSame("different instances of SpreadsheetCell returned not cached", first, second);
    }

    private SpreadsheetCell loadFirst(final BasicSpreadsheetEngine engine, final SpreadsheetCellReference reference, final SpreadsheetEngineLoading loading) {
        return engine.load(Sets.of(reference),
                loading)
                .iterator()
                .next();
    }

    @Test
    public void testSetAndLoadManyCells() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();

        final SpreadsheetCellReference a = this.cellReference(1, 1);
        final SpreadsheetCellReference b = this.cellReference(2, 1);
        final SpreadsheetCellReference c = this.cellReference(3, 1);

        engine.set(SpreadsheetCell.with(a, SpreadsheetFormula.with("1+2")));
        engine.set(SpreadsheetCell.with(b, SpreadsheetFormula.with("3+4")));
        engine.set(SpreadsheetCell.with(c, SpreadsheetFormula.with("5+6")));

        this.loadAndCheck(engine,
                Sets.of(a, b, c),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(1+2), BigInteger.valueOf(3+4), BigInteger.valueOf(5+6));
    }

    @Test
    public void testDeleteUnknownCellIgnored() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();

        final SpreadsheetCellReference cellReference = this.cellReference(1, 1);
        engine.set(SpreadsheetCell.with(cellReference, SpreadsheetFormula.with("1+2")));

        engine.delete(this.cellReference(99, 99));

        this.loadAndCheck(engine,
                Sets.of(cellReference),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(1+2));
    }

    @Test
    public void testDeleteUnknownIgnored2() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();

        final SpreadsheetCellReference a = this.cellReference(1, 1);
        final SpreadsheetCellReference b = this.cellReference(2, 1);
        final SpreadsheetCellReference c = this.cellReference(3, 1);

        engine.set(SpreadsheetCell.with(a, SpreadsheetFormula.with("1+2")));
        engine.set(SpreadsheetCell.with(b, SpreadsheetFormula.with("3+4")));
        engine.set(SpreadsheetCell.with(c, SpreadsheetFormula.with("5+6")));

        engine.delete(this.cellReference(99, 99));

        this.loadAndCheck(engine,
                Sets.of(a, b, c),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(1+2), BigInteger.valueOf(3+4), BigInteger.valueOf(5+6));
    }

    @Test
    public void testDeleteExisting() {
        final BasicSpreadsheetEngine engine = this.createSpreadsheetEngine();

        final SpreadsheetCellReference a = this.cellReference(1, 1);
        final SpreadsheetCellReference b = this.cellReference(2, 1);
        final SpreadsheetCellReference c = this.cellReference(3, 1);

        engine.set(SpreadsheetCell.with(a, SpreadsheetFormula.with("1+2")));
        engine.set(SpreadsheetCell.with(b, SpreadsheetFormula.with("3+4")));
        engine.set(SpreadsheetCell.with(c, SpreadsheetFormula.with("5+6")));

        engine.delete(b);

        this.loadAndCheck(engine,
                Sets.of(a, b, c),
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                BigInteger.valueOf(1+2), BigInteger.valueOf(5+6));
    }

    @Override
    BasicSpreadsheetEngine createSpreadsheetEngine() {
        return BasicSpreadsheetEngine.with(this.id(), this.parser(), this.parserContext(), this.evaluationContext());
    }

    private SpreadsheetId id() {
        return SpreadsheetId.with(123);
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

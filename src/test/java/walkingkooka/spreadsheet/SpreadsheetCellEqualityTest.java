package walkingkooka.spreadsheet;

import org.junit.*;
import walkingkooka.test.*;
import walkingkooka.tree.expression.*;

public final class SpreadsheetCellEqualityTest extends HashCodeEqualsDefinedEqualityTestCase<SpreadsheetCell> {

    @Test
    public void testDifferentSpreadsheetCell() {
        this.checkNotEquals(this.cell("different"));
    }

    @Override
    protected SpreadsheetCell createObject() {
        return this.cell("text");
    }

    private SpreadsheetCell cell(final String text) {
        return SpreadsheetCell.with(this.formula(text));
    }

    private SpreadsheetFormula formula(final String text) {
        return SpreadsheetFormula.with(ExpressionNode.text(text));
    }
}

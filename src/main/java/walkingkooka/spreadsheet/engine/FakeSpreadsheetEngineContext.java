package walkingkooka.spreadsheet.engine;

import walkingkooka.FakeDecimalNumberContext;
import walkingkooka.test.Fake;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserToken;
import walkingkooka.tree.expression.ExpressionNode;

import java.util.Objects;

public class FakeSpreadsheetEngineContext implements SpreadsheetEngineContext, Fake {

    @Override
    public SpreadsheetParserToken parseFormula(final String formula) {
        Objects.requireNonNull(formula, "formula");
        throw new UnsupportedOperationException();
    }

    @Override
    public Object evaluate(final ExpressionNode node) {
        Objects.requireNonNull(node, "node");
        throw new UnsupportedOperationException();
    }
}

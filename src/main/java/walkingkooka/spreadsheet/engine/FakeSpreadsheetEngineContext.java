package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.format.SpreadsheetFormattedText;
import walkingkooka.spreadsheet.format.SpreadsheetTextFormatter;
import walkingkooka.test.Fake;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserToken;
import walkingkooka.tree.expression.ExpressionNode;

import java.util.Objects;
import java.util.Optional;

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

    @Override
    public <T> T convert(Object value, Class<T> target) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(target, "target");
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetTextFormatter<?> parseFormatPattern(final String pattern) {
        Objects.requireNonNull(pattern, "pattern");
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetTextFormatter<?> defaultSpreadsheetTextFormatter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetFormattedText> format(final Object value,
                                                     final SpreadsheetTextFormatter<?> formatter) {
        throw new UnsupportedOperationException();
    }
}

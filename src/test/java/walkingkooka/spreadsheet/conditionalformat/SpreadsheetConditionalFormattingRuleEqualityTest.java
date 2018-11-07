package walkingkooka.spreadsheet.conditionalformat;

import org.junit.Test;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetDescription;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.style.SpreadsheetCellStyle;
import walkingkooka.test.HashCodeEqualsDefinedEqualityTestCase;
import walkingkooka.tree.expression.ExpressionNode;

import java.util.Optional;
import java.util.function.Function;

public final class SpreadsheetConditionalFormattingRuleEqualityTest extends HashCodeEqualsDefinedEqualityTestCase<SpreadsheetConditionalFormattingRule> {

    @Test
    public void testDifferentDescription() {
        this.checkNotEquals(SpreadsheetConditionalFormattingRule.with(SpreadsheetDescription.with("different description"),
                priority(),
                formula(),
                style()));
    }

    @Test
    public void testDifferentPriority() {
        this.checkNotEquals(SpreadsheetConditionalFormattingRule.with(description(),
                999,
                formula(),
                style()));
    }

    @Test
    public void testDifferentFormula() {
        this.checkNotEquals(SpreadsheetConditionalFormattingRule.with(description(),
                priority(),
                SpreadsheetFormula.with("999").setExpression(Optional.of(ExpressionNode.longNode(99))),
                style()));
    }

    @Test
    public void testDifferentStyle() {
        this.checkNotEquals(SpreadsheetConditionalFormattingRule.with(description(),
                priority(),
                formula(),
                (c) -> null));
    }

    @Override
    protected SpreadsheetConditionalFormattingRule createObject() {
        return SpreadsheetConditionalFormattingRule.with(description(), priority(), formula(), style());
    }

    private SpreadsheetDescription description() {
        return SpreadsheetDescription.with("description#");
    }

    private int priority() {
        return 123;
    }

    private SpreadsheetFormula formula() {
        return SpreadsheetFormula.with("1+2")
                .setExpression(Optional.of(ExpressionNode.longNode(3)));
    }

    private Function<SpreadsheetCell, SpreadsheetCellStyle> style() {
        return FUNCTION;
    }

    private final Function<SpreadsheetCell, SpreadsheetCellStyle> FUNCTION = new Function<SpreadsheetCell, SpreadsheetCellStyle>() {

        @Override
        public SpreadsheetCellStyle apply(final SpreadsheetCell spreadsheetCell) {
            throw new UnsupportedOperationException();
        }

        public String toString() {
            return "style";
        }
    };
}

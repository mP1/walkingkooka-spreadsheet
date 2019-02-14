package walkingkooka.spreadsheet.conditionalformat;

import org.junit.jupiter.api.Test;
import walkingkooka.compare.ComparatorTesting;
import walkingkooka.spreadsheet.SpreadsheetDescription;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.tree.expression.ExpressionNode;

import java.util.Optional;

public final class SpreadsheetConditionalFormattingRulePriorityDescendingComparatorTest implements ComparatorTesting<SpreadsheetConditionalFormattingRulePriorityDescendingComparator,
        SpreadsheetConditionalFormattingRule> {

    @Test
    public void testEqual() {
        this.compareAndCheckEqual(this.createRule(1));
    }

    @Test
    public void testHigherIsBefore() {
        this.compareAndCheckLess(this.createRule(123), this.createRule(45));
    }

    private SpreadsheetConditionalFormattingRule createRule(final int priority) {
        return SpreadsheetConditionalFormattingRule.with(SpreadsheetDescription.with("description"),
                priority,
                SpreadsheetFormula.with("1").setExpression(Optional.of(ExpressionNode.longNode(1))),
                (c) -> null);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createComparator(), "SpreadsheetConditionalFormattingRule.priority DESC");
    }

    @Override
    public SpreadsheetConditionalFormattingRulePriorityDescendingComparator createComparator() {
        return SpreadsheetConditionalFormattingRulePriorityDescendingComparator.INSTANCE;
    }

    @Override
    public Class<SpreadsheetConditionalFormattingRulePriorityDescendingComparator> type() {
        return SpreadsheetConditionalFormattingRulePriorityDescendingComparator.class;
    }
}

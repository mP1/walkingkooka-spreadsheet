package walkingkooka.spreadsheet.conditionalformat;

import org.junit.Assert;
import org.junit.Test;
import walkingkooka.compare.ComparatorTestCase;
import walkingkooka.spreadsheet.SpreadsheetDescription;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.tree.expression.ExpressionNode;

import java.util.Optional;

public final class SpreadsheetConditionalFormattingRulePriorityDescendingComparatorTest extends ComparatorTestCase<SpreadsheetConditionalFormattingRulePriorityDescendingComparator,
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

    @Override
    public void testToString() {
        Assert.assertEquals("SpreadsheetConditionalFormattingRule.priority DESC", this.createComparator().toString());
    }

    @Override
    protected SpreadsheetConditionalFormattingRulePriorityDescendingComparator createComparator() {
        return SpreadsheetConditionalFormattingRulePriorityDescendingComparator.INSTANCE;
    }

    @Override
    protected Class<SpreadsheetConditionalFormattingRulePriorityDescendingComparator> type() {
        return SpreadsheetConditionalFormattingRulePriorityDescendingComparator.class;
    }
}

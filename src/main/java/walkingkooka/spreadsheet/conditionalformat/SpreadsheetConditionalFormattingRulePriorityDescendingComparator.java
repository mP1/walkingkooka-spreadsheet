package walkingkooka.spreadsheet.conditionalformat;

import java.util.Comparator;

/**
 * A {@link Comparator} that may be used to sort rules from highest priority to lowest.
 */
final class SpreadsheetConditionalFormattingRulePriorityDescendingComparator implements Comparator<SpreadsheetConditionalFormattingRule> {

    /**
     * Singleton
     */
    final static SpreadsheetConditionalFormattingRulePriorityDescendingComparator INSTANCE = new SpreadsheetConditionalFormattingRulePriorityDescendingComparator();

    /**
     * Private ctor use singleton.
     */
    private SpreadsheetConditionalFormattingRulePriorityDescendingComparator() {
        super();
    }

    @Override
    public int compare(final SpreadsheetConditionalFormattingRule first, final SpreadsheetConditionalFormattingRule second) {
        return second.priority() - first.priority();
    }

    @Override
    public String toString() {
        return SpreadsheetConditionalFormattingRule.class.getSimpleName() + ".priority DESC";
    }
}

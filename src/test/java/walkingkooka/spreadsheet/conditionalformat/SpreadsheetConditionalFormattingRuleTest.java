package walkingkooka.spreadsheet.conditionalformat;

import org.junit.Test;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetDescription;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.style.SpreadsheetCellStyle;
import walkingkooka.test.PublicClassTestCase;
import walkingkooka.tree.expression.ExpressionNode;

import java.util.Optional;
import java.util.function.Function;

import static junit.framework.TestCase.assertSame;
import static org.junit.Assert.assertEquals;


public final class SpreadsheetConditionalFormattingRuleTest extends PublicClassTestCase<SpreadsheetConditionalFormattingRule> {

    @Test(expected = NullPointerException.class)
    public void testWithNullDescriptionFails() {
        SpreadsheetConditionalFormattingRule.with(null, priority(), formula(), style());
    }

    @Test(expected = NullPointerException.class)
    public void testWithNullFormulaFails() {
        SpreadsheetConditionalFormattingRule.with(description(), priority(), null, style());
    }

    @Test(expected = SpreadsheetConditionalFormattingException.class)
    public void testWithNullFormulaWithoutExpressionFails() {
        SpreadsheetConditionalFormattingRule.with(description(), priority(), this.formulaUncompiled(), style());
    }

    @Test(expected = NullPointerException.class)
    public void testWithNullStyleFails() {
        SpreadsheetConditionalFormattingRule.with(description(), priority(), formula(), null);
    }

    @Test
    public void testWith() {
        final SpreadsheetConditionalFormattingRule rule = SpreadsheetConditionalFormattingRule.with(description(), priority(), formula(), style());
        this.check(rule, description(), priority(), formula(), style());
    }

    // setDescription................................................................................

    @Test(expected = NullPointerException.class)
    public void testSetDescriptionNullFails() {
        this.createRule().setDescription(null);
    }

    @Test
    public void testSetDescriptionSame() {
        final SpreadsheetConditionalFormattingRule rule = this.createRule();
        assertSame(rule, rule.setDescription(description()));
    }

    @Test
    public void testSetDescriptionDifferent() {
        final SpreadsheetConditionalFormattingRule rule = this.createRule();
        final SpreadsheetDescription description = SpreadsheetDescription.with("different");
        final SpreadsheetConditionalFormattingRule different = rule.setDescription(description);
        checkDescription(different, description);
        checkPriority(different);
        checkFormula(different);
        checkStyle(different);
    }

    // setPriority................................................................................
    @Test
    public void testSetPrioritySame() {
        final SpreadsheetConditionalFormattingRule rule = this.createRule();
        assertSame(rule, rule.setPriority(priority()));
    }

    @Test
    public void testSetPriorityDifferent() {
        final SpreadsheetConditionalFormattingRule rule = this.createRule();
        final int priority = 999;
        final SpreadsheetConditionalFormattingRule different = rule.setPriority(priority);
        checkDescription(different);
        checkPriority(different, priority);
        checkFormula(different);
        checkStyle(different);
    }

    // setFormula................................................................................

    @Test(expected = NullPointerException.class)
    public void testSetFormulaNullFails() {
        this.createRule().setFormula(null);
    }

    @Test(expected = SpreadsheetConditionalFormattingException.class)
    public void testSetFormulaUncompiledFails() {
        this.createRule().setFormula(this.formulaUncompiled());
    }

    @Test
    public void testSetFormulaSame() {
        final SpreadsheetConditionalFormattingRule rule = this.createRule();
        assertSame(rule, rule.setFormula(formula()));
    }

    @Test
    public void testSetFormulaDifferent() {
        final SpreadsheetConditionalFormattingRule rule = this.createRule();
        final SpreadsheetFormula formula = SpreadsheetFormula.with("99").setExpression(Optional.of(ExpressionNode.text("\"99\"")));
        final SpreadsheetConditionalFormattingRule different = rule.setFormula(formula);
        checkDescription(different);
        checkPriority(different);
        checkFormula(different, formula);
        checkStyle(different);
    }

    // setStyle................................................................................

    @Test(expected = NullPointerException.class)
    public void testSetStyleNullFails() {
        this.createRule().setStyle(null);
    }

    @Test
    public void testSetStyleSame() {
        final SpreadsheetConditionalFormattingRule rule = this.createRule();
        assertSame(rule, rule.setStyle(style()));
    }

    @Test
    public void testSetStyleDifferent() {
        final SpreadsheetConditionalFormattingRule rule = this.createRule();
        final Function<SpreadsheetCell, SpreadsheetCellStyle> style = (c) -> null;
        final SpreadsheetConditionalFormattingRule different = rule.setStyle(style);
        checkDescription(different);
        checkPriority(different);
        checkFormula(different);
        checkStyle(different, style);
    }

    // toString...............................................................................................

    @Test
    public void testToString() {
        assertEquals("\"description#\" 123 123 style", this.createRule().toString());
    }

    private SpreadsheetConditionalFormattingRule createRule() {
        return SpreadsheetConditionalFormattingRule.with(description(), priority(), formula(), style());
    }

    private SpreadsheetDescription description() {
        return SpreadsheetDescription.with("description#");
    }

    private int priority() {
        return 123;
    }

    private SpreadsheetFormula formula() {
        return this.formulaUncompiled().setExpression(Optional.of(ExpressionNode.longNode(123)));
    }

    private SpreadsheetFormula formulaUncompiled() {
        return SpreadsheetFormula.with("123");
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

    private void check(final SpreadsheetConditionalFormattingRule rule,
                       final SpreadsheetDescription description,
                       final int priority,
                       final SpreadsheetFormula formula,
                       final Function<SpreadsheetCell, SpreadsheetCellStyle> style) {
        checkDescription(rule, description);
        checkPriority(rule, priority);
        checkFormula(rule, formula);
        checkStyle(rule, style);
    }

    private void checkDescription(final SpreadsheetConditionalFormattingRule rule) {
        checkDescription(rule, description());
    }

    private void checkDescription(final SpreadsheetConditionalFormattingRule rule,
                                  final SpreadsheetDescription description) {
        assertEquals("rule", description, rule.description());
    }

    private void checkPriority(final SpreadsheetConditionalFormattingRule rule) {
        checkPriority(rule, priority());
    }

    private void checkPriority(final SpreadsheetConditionalFormattingRule rule,
                               final int priority) {
        assertEquals("priority", priority, rule.priority());
    }

    private void checkFormula(final SpreadsheetConditionalFormattingRule rule) {
        checkFormula(rule, formula());
    }

    private void checkFormula(final SpreadsheetConditionalFormattingRule rule,
                              final SpreadsheetFormula formula) {
        assertEquals("formula", formula, rule.formula());
    }

    private void checkStyle(final SpreadsheetConditionalFormattingRule rule) {
        checkStyle(rule, style());
    }

    private void checkStyle(final SpreadsheetConditionalFormattingRule rule,
                            final Function<SpreadsheetCell, SpreadsheetCellStyle> style) {
        assertEquals("style", style, rule.style());
    }

    @Override
    protected Class<SpreadsheetConditionalFormattingRule> type() {
        return SpreadsheetConditionalFormattingRule.class;
    }
}

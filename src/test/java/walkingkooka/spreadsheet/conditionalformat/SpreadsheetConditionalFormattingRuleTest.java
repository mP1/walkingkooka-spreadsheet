package walkingkooka.spreadsheet.conditionalformat;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetDescription;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.style.SpreadsheetCellStyle;
import walkingkooka.test.ClassTestCase;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.test.ToStringTesting;
import walkingkooka.tree.expression.ExpressionNode;
import walkingkooka.type.MemberVisibility;

import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetConditionalFormattingRuleTest extends ClassTestCase<SpreadsheetConditionalFormattingRule>
        implements HashCodeEqualsDefinedTesting<SpreadsheetConditionalFormattingRule>,
        ToStringTesting<SpreadsheetConditionalFormattingRule> {

    @Test
    public void testWithNullDescriptionFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetConditionalFormattingRule.with(null, priority(), formula(), style());
        });
    }

    @Test
    public void testWithNullFormulaFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetConditionalFormattingRule.with(description(), priority(), null, style());
        });
    }

    @Test
    public void testWithNullFormulaWithoutExpressionFails() {
        assertThrows(SpreadsheetConditionalFormattingException.class, () -> {
            SpreadsheetConditionalFormattingRule.with(description(), priority(), this.formulaUncompiled(), style());
        });
    }

    @Test
    public void testWithNullStyleFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetConditionalFormattingRule.with(description(), priority(), formula(), null);
        });
    }

    @Test
    public void testWith() {
        final SpreadsheetConditionalFormattingRule rule = SpreadsheetConditionalFormattingRule.with(description(), priority(), formula(), style());
        this.check(rule, description(), priority(), formula(), style());
    }

    // setDescription................................................................................

    @Test
    public void testSetDescriptionNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createObject().setDescription(null);
        });
    }

    @Test
    public void testSetDescriptionSame() {
        final SpreadsheetConditionalFormattingRule rule = this.createObject();
        assertSame(rule, rule.setDescription(description()));
    }

    @Test
    public void testSetDescriptionDifferent() {
        final SpreadsheetConditionalFormattingRule rule = this.createObject();
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
        final SpreadsheetConditionalFormattingRule rule = this.createObject();
        assertSame(rule, rule.setPriority(priority()));
    }

    @Test
    public void testSetPriorityDifferent() {
        final SpreadsheetConditionalFormattingRule rule = this.createObject();
        final int priority = 999;
        final SpreadsheetConditionalFormattingRule different = rule.setPriority(priority);
        checkDescription(different);
        checkPriority(different, priority);
        checkFormula(different);
        checkStyle(different);
    }

    // setFormula................................................................................

    @Test
    public void testSetFormulaNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createObject().setFormula(null);
        });
    }

    @Test
    public void testSetFormulaUncompiledFails() {
        assertThrows(SpreadsheetConditionalFormattingException.class, () -> {
            this.createObject().setFormula(this.formulaUncompiled());
        });
    }

    @Test
    public void testSetFormulaSame() {
        final SpreadsheetConditionalFormattingRule rule = this.createObject();
        assertSame(rule, rule.setFormula(formula()));
    }

    @Test
    public void testSetFormulaDifferent() {
        final SpreadsheetConditionalFormattingRule rule = this.createObject();
        final SpreadsheetFormula formula = SpreadsheetFormula.with("99").setExpression(Optional.of(ExpressionNode.text("\"99\"")));
        final SpreadsheetConditionalFormattingRule different = rule.setFormula(formula);
        checkDescription(different);
        checkPriority(different);
        checkFormula(different, formula);
        checkStyle(different);
    }

    // setStyle................................................................................

    @Test
    public void testSetStyleNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createObject().setStyle(null);
        });
    }

    @Test
    public void testSetStyleSame() {
        final SpreadsheetConditionalFormattingRule rule = this.createObject();
        assertSame(rule, rule.setStyle(style()));
    }

    @Test
    public void testSetStyleDifferent() {
        final SpreadsheetConditionalFormattingRule rule = this.createObject();
        final Function<SpreadsheetCell, SpreadsheetCellStyle> style = (c) -> null;
        final SpreadsheetConditionalFormattingRule different = rule.setStyle(style);
        checkDescription(different);
        checkPriority(different);
        checkFormula(different);
        checkStyle(different, style);
    }

    // equals ...........................................................................................

    @Test
    public void testEqualsDifferentDescription() {
        this.checkNotEquals(SpreadsheetConditionalFormattingRule.with(SpreadsheetDescription.with("different description"),
                priority(),
                formula(),
                style()));
    }

    @Test
    public void testEqualsDifferentPriority() {
        this.checkNotEquals(SpreadsheetConditionalFormattingRule.with(description(),
                999,
                formula(),
                style()));
    }

    @Test
    public void testEqualsDifferentFormula() {
        this.checkNotEquals(SpreadsheetConditionalFormattingRule.with(description(),
                priority(),
                SpreadsheetFormula.with("999").setExpression(Optional.of(ExpressionNode.longNode(99))),
                style()));
    }

    @Test
    public void testEqualsDifferentStyle() {
        this.checkNotEquals(SpreadsheetConditionalFormattingRule.with(description(),
                priority(),
                formula(),
                (c) -> null));
    }

    // toString...............................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createObject(), "\"description#\" 123 123 style");
    }

    @Override
    public SpreadsheetConditionalFormattingRule createObject() {
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
        assertEquals(description, rule.description(), "rule");
    }

    private void checkPriority(final SpreadsheetConditionalFormattingRule rule) {
        checkPriority(rule, priority());
    }

    private void checkPriority(final SpreadsheetConditionalFormattingRule rule,
                               final int priority) {
        assertEquals(priority, rule.priority(), "priority");
    }

    private void checkFormula(final SpreadsheetConditionalFormattingRule rule) {
        checkFormula(rule, formula());
    }

    private void checkFormula(final SpreadsheetConditionalFormattingRule rule,
                              final SpreadsheetFormula formula) {
        assertEquals(formula, rule.formula(), "formula");
    }

    private void checkStyle(final SpreadsheetConditionalFormattingRule rule) {
        checkStyle(rule, style());
    }

    private void checkStyle(final SpreadsheetConditionalFormattingRule rule,
                            final Function<SpreadsheetCell, SpreadsheetCellStyle> style) {
        assertEquals(style, rule.style(), "style");
    }

    @Override
    public Class<SpreadsheetConditionalFormattingRule> type() {
        return SpreadsheetConditionalFormattingRule.class;
    }

    @Override
    protected MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }
}

/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.spreadsheet.conditionalformat;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetDescription;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.text.TextStyle;

import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetConditionalFormattingRuleTest implements ClassTesting2<SpreadsheetConditionalFormattingRule>,
    HashCodeEqualsDefinedTesting2<SpreadsheetConditionalFormattingRule>,
    ToStringTesting<SpreadsheetConditionalFormattingRule> {

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;

    @Test
    public void testWithNullDescriptionFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetConditionalFormattingRule.with(
                null,
                priority(),
                formula(),
                style()
            )
        );
    }

    @Test
    public void testWithNullFormulaFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetConditionalFormattingRule.with(
                description(),
                priority(),
                null,
                style()
            )
        );
    }

    @Test
    public void testWithNullFormulaWithoutExpressionFails() {
        assertThrows(
            SpreadsheetConditionalFormattingException.class,
            () -> SpreadsheetConditionalFormattingRule.with(
                description(),
                priority(),
                this.formulaUncompiled(),
                style()
            )
        );
    }

    @Test
    public void testWithNullTextStyleFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetConditionalFormattingRule.with(
                description(),
                priority(),
                formula(),
                null
            )
        );
    }

    @Test
    public void testWith() {
        final SpreadsheetConditionalFormattingRule rule = SpreadsheetConditionalFormattingRule.with(
            description(),
            priority(),
            formula(),
            style()
        );
        this.check(
            rule,
            description(),
            priority(),
            formula(),
            style()
        );
    }

    // setDescription...................................................................................................

    @Test
    public void testSetDescriptionNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .setDescription(null)
        );
    }

    @Test
    public void testSetDescriptionSame() {
        final SpreadsheetConditionalFormattingRule rule = this.createObject();
        assertSame(
            rule,
            rule.setDescription(description())
        );
    }

    @Test
    public void testSetDescriptionDifferent() {
        final SpreadsheetConditionalFormattingRule rule = this.createObject();
        final SpreadsheetDescription description = SpreadsheetDescription.with("different");
        final SpreadsheetConditionalFormattingRule different = rule.setDescription(description);
        descriptionAndCheck(different, description);
        priorityAndCheck(different);
        formulaAndCheck(different);
        textStyleAndCheck(different);
    }

    // setPriority......................................................................................................
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
        descriptionAndCheck(different);
        priorityAndCheck(different, priority);
        formulaAndCheck(different);
        textStyleAndCheck(different);
    }

    // setFormula.......................................................................................................

    @Test
    public void testSetFormulaNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .setFormula(null)
        );
    }

    @Test
    public void testSetFormulaUncompiledFails() {
        assertThrows(
            SpreadsheetConditionalFormattingException.class,
            () -> this.createObject()
                .setFormula(this.formulaUncompiled())
        );
    }

    @Test
    public void testSetFormulaSame() {
        final SpreadsheetConditionalFormattingRule rule = this.createObject();
        assertSame(
            rule,
            rule.setFormula(formula())
        );
    }

    @Test
    public void testSetFormulaDifferent() {
        final SpreadsheetConditionalFormattingRule rule = this.createObject();
        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY
            .setText("99")
            .setExpression(
                Optional.of(
                    Expression.value("\"99\"")
                )
            );
        final SpreadsheetConditionalFormattingRule different = rule.setFormula(formula);
        descriptionAndCheck(different);
        priorityAndCheck(different);
        formulaAndCheck(different, formula);
        textStyleAndCheck(different);
    }

    // setStyle.........................................................................................................

    @Test
    public void testSetStyleNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .setStyle(null)
        );
    }

    @Test
    public void testSetStyleSame() {
        final SpreadsheetConditionalFormattingRule rule = this.createObject();
        assertSame(
            rule,
            rule.setStyle(
                rule.style()
            )
        );
    }

    @Test
    public void testSetStyleDifferent() {
        final SpreadsheetConditionalFormattingRule rule = this.createObject();
        final Function<SpreadsheetCell, TextStyle> style = (c) -> null;
        final SpreadsheetConditionalFormattingRule different = rule.setStyle(style);
        descriptionAndCheck(different);
        priorityAndCheck(different);
        formulaAndCheck(different);
        textStyleAndCheck(different, style);
    }

    private void check(final SpreadsheetConditionalFormattingRule rule,
                       final SpreadsheetDescription description,
                       final int priority,
                       final SpreadsheetFormula formula,
                       final Function<SpreadsheetCell, TextStyle> style) {
        descriptionAndCheck(rule, description);
        priorityAndCheck(rule, priority);
        formulaAndCheck(rule, formula);
        textStyleAndCheck(rule, style);
    }

    private void descriptionAndCheck(final SpreadsheetConditionalFormattingRule rule) {
        this.descriptionAndCheck(
            rule,
            description()
        );
    }

    private void descriptionAndCheck(final SpreadsheetConditionalFormattingRule rule,
                                     final SpreadsheetDescription description) {
        this.checkEquals(
            description,
            rule.description(),
            "rule"
        );
    }

    private SpreadsheetDescription description() {
        return SpreadsheetDescription.with("description#");
    }

    private void priorityAndCheck(final SpreadsheetConditionalFormattingRule rule) {
        this.priorityAndCheck(
            rule,
            priority()
        );
    }

    private void priorityAndCheck(final SpreadsheetConditionalFormattingRule rule,
                                  final int priority) {
        this.checkEquals(
            priority,
            rule.priority(),
            "priority"
        );
    }

    @SuppressWarnings("SameReturnValue")
    private int priority() {
        return 123;
    }

    private void formulaAndCheck(final SpreadsheetConditionalFormattingRule rule) {
        this.formulaAndCheck(
            rule,
            formula()
        );
    }

    private void formulaAndCheck(final SpreadsheetConditionalFormattingRule rule,
                                 final SpreadsheetFormula formula) {
        this.checkEquals(
            formula,
            rule.formula(),
            "formula"
        );
    }

    private SpreadsheetFormula formula() {
        return this.formulaUncompiled()
            .setExpression(
                Optional.of(
                    Expression.value(
                        EXPRESSION_NUMBER_KIND.create(123)
                    )
                )
            );
    }

    private SpreadsheetFormula formulaUncompiled() {
        return SpreadsheetFormula.EMPTY
            .setText("123");
    }

    private void textStyleAndCheck(final SpreadsheetConditionalFormattingRule rule) {
        textStyleAndCheck(
            rule,
            style()
        );
    }

    private void textStyleAndCheck(final SpreadsheetConditionalFormattingRule rule,
                                   final Function<SpreadsheetCell, TextStyle> style) {
        this.checkEquals(
            style,
            rule.style(),
            "style"
        );
    }

    private Function<SpreadsheetCell, TextStyle> style() {
        return FUNCTION;
    }

    private final Function<SpreadsheetCell, TextStyle> FUNCTION = new Function<>() {

        @Override
        public TextStyle apply(final SpreadsheetCell spreadsheetCell) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return "style";
        }
    };

    // equals ..........................................................................................................

    @Test
    public void testEqualsDifferentDescription() {
        this.checkNotEquals(
            SpreadsheetConditionalFormattingRule.with(
                SpreadsheetDescription.with("different description"),
                priority(),
                formula(),
                style()
            )
        );
    }

    @Test
    public void testEqualsDifferentPriority() {
        this.checkNotEquals(
            SpreadsheetConditionalFormattingRule.with(description(),
                999,
                formula(),
                style()
            )
        );
    }

    @Test
    public void testEqualsDifferentFormula() {
        this.checkNotEquals(
            SpreadsheetConditionalFormattingRule.with(description(),
                priority(),
                SpreadsheetFormula.EMPTY
                    .setText("999")
                    .setExpression(
                        Optional.of(
                            Expression.value(
                                EXPRESSION_NUMBER_KIND.create(99)
                            )
                        )
                    ),
                style()
            )
        );
    }

    @Test
    public void testEqualsDifferentTextStyle() {
        this.checkNotEquals(
            SpreadsheetConditionalFormattingRule.with(
                description(),
                priority(),
                formula(),
                (c) -> null)
        );
    }

    @Override
    public SpreadsheetConditionalFormattingRule createObject() {
        return SpreadsheetConditionalFormattingRule.with(
            description(),
            priority(),
            formula(),
            style()
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createObject(),
            "\"description#\" 123 123  style"
        );
    }

    // Class............................................................................................................

    @Override
    public Class<SpreadsheetConditionalFormattingRule> type() {
        return SpreadsheetConditionalFormattingRule.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}

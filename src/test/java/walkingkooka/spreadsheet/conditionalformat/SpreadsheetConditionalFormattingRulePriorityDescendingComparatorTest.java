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
import walkingkooka.compare.ComparatorTesting2;
import walkingkooka.spreadsheet.SpreadsheetDescription;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.util.Optional;

public final class SpreadsheetConditionalFormattingRulePriorityDescendingComparatorTest implements ComparatorTesting2<SpreadsheetConditionalFormattingRulePriorityDescendingComparator,
    SpreadsheetConditionalFormattingRule> {

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;

    @Test
    public void testEqual() {
        this.compareAndCheckEquals(this.createRule(1));
    }

    @Test
    public void testHigherIsBefore() {
        this.compareAndCheckLess(this.createRule(123), this.createRule(45));
    }

    private SpreadsheetConditionalFormattingRule createRule(final int priority) {
        return SpreadsheetConditionalFormattingRule.with(
            SpreadsheetDescription.with("description"),
            priority,
            SpreadsheetFormula.EMPTY
                .setText("1")
                .setExpression(
                    Optional.of(
                        Expression.value(
                            EXPRESSION_NUMBER_KIND.one()
                        )
                    )
                ),
            (c) -> null
        );
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

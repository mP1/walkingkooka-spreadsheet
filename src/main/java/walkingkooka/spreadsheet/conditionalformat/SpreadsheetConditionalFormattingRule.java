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

import walkingkooka.Cast;
import walkingkooka.ToStringBuilder;
import walkingkooka.ToStringBuilderOption;
import walkingkooka.UsesToStringBuilder;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetDescription;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.tree.text.TextStyle;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

/**
 * Represents a single conditional rule.
 */
public final class SpreadsheetConditionalFormattingRule implements UsesToStringBuilder {

    /**
     * {@see SpreadsheetConditionalFormattingRulePriorityDescendingComparator}
     */
    public final static Comparator<SpreadsheetConditionalFormattingRule> PRIORITY_COMPARATOR = SpreadsheetConditionalFormattingRulePriorityDescendingComparator.INSTANCE;

    /**
     * Factory that creates a {@link SpreadsheetConditionalFormattingRule}
     */
    public static SpreadsheetConditionalFormattingRule with(final SpreadsheetDescription description,
                                                            final int priority,
                                                            final SpreadsheetFormula formula,
                                                            final Function<SpreadsheetCell, TextStyle> style) {
        checkDescription(description);
        checkFormula(formula);
        checkStyle(style);

        return new SpreadsheetConditionalFormattingRule(description, priority, formula, style);
    }

    /**
     * Private ctor use static factory or constant.
     */
    private SpreadsheetConditionalFormattingRule(final SpreadsheetDescription description,
                                                 final int priority,
                                                 final SpreadsheetFormula formula,
                                                 final Function<SpreadsheetCell, TextStyle> style) {
        super();
        this.description = description;
        this.priority = priority;
        this.formula = formula;
        this.style = style;
    }

    // description...................................................................................................

    public SpreadsheetDescription description() {
        return this.description;
    }

    public SpreadsheetConditionalFormattingRule setDescription(final SpreadsheetDescription description) {
        checkDescription(description);

        return this.description.equals(description) ?
            this :
            this.replace(description, this.priority, this.formula, this.style);
    }

    private final SpreadsheetDescription description;

    private static void checkDescription(final SpreadsheetDescription description) {
        Objects.requireNonNull(description, "description");
    }

    // priority...................................................................................................

    public int priority() {
        return this.priority;
    }

    public SpreadsheetConditionalFormattingRule setPriority(final int priority) {
        return this.priority == priority ?
            this :
            this.replace(this.description, priority, this.formula, this.style);
    }

    private final int priority;

    // formula...................................................................................................

    public SpreadsheetFormula formula() {
        return this.formula;
    }

    public SpreadsheetConditionalFormattingRule setFormula(final SpreadsheetFormula formula) {
        checkFormula(formula);

        return this.formula.equals(formula) ?
            this :
            this.replace(this.description, this.priority, formula, this.style);
    }

    private final SpreadsheetFormula formula;

    /**
     * Fails if the formula is null or missing a compiled expression.
     */
    private static void checkFormula(final SpreadsheetFormula formula) {
        Objects.requireNonNull(formula, "formula");
        if (!formula.expression().isPresent()) {
            throw new SpreadsheetConditionalFormattingException("Formula missing compiled expression=" + formula);
        }
    }

    // style...................................................................................................

    public Function<SpreadsheetCell, TextStyle> style() {
        return this.style;
    }

    public SpreadsheetConditionalFormattingRule setStyle(final Function<SpreadsheetCell, TextStyle> style) {
        checkStyle(style);

        return this.style.equals(style) ?
            this :
            this.replace(this.description, this.priority, this.formula, style);
    }

    private final Function<SpreadsheetCell, TextStyle> style;

    private static void checkStyle(final Function<SpreadsheetCell, TextStyle> style) {
        Objects.requireNonNull(style, "style");
    }

    // factory..................................................................................

    /**
     * Factory that unconditionally creates a {@link SpreadsheetConditionalFormattingRule}.
     */
    private SpreadsheetConditionalFormattingRule replace(final SpreadsheetDescription description,
                                                         final int priority,
                                                         final SpreadsheetFormula formula,
                                                         final Function<SpreadsheetCell, TextStyle> style) {
        return new SpreadsheetConditionalFormattingRule(description, priority, formula, style);
    }

    // HashCodeEqualsDefined.........................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.description, this.priority, this.formula, this.style);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetConditionalFormattingRule &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetConditionalFormattingRule other) {
        return this.description.equals(other.description) &&
            this.priority == other.priority &&
            this.formula.equals(other.formula) &&
            this.style.equals(other.style);
    }

    @Override
    public String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    @Override
    public void buildToString(final ToStringBuilder builder) {
        builder.disable(ToStringBuilderOption.QUOTE);
        builder.disable(ToStringBuilderOption.SKIP_IF_DEFAULT_VALUE);
        builder.separator(" ");
        builder.value(this.description);
        builder.value(this.priority);
        builder.value(this.formula);
        builder.value(this.style);
    }
}

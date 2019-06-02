package walkingkooka.spreadsheet.conditionalformat;

import walkingkooka.Cast;
import walkingkooka.build.tostring.ToStringBuilder;
import walkingkooka.build.tostring.ToStringBuilderOption;
import walkingkooka.build.tostring.UsesToStringBuilder;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetDescription;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.tree.text.TextProperties;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

/**
 * Represents a single conditional rule.
 */
public final class SpreadsheetConditionalFormattingRule implements HashCodeEqualsDefined, UsesToStringBuilder {

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
                                                            final Function<SpreadsheetCell, TextProperties> textProperties) {
        checkDescription(description);
        checkFormula(formula);
        checkTextProperties(textProperties);

        return new SpreadsheetConditionalFormattingRule(description, priority, formula, textProperties);
    }

    /**
     * Private ctor use static factory or constant.
     */
    private SpreadsheetConditionalFormattingRule(final SpreadsheetDescription description,
                                                 final int priority,
                                                 final SpreadsheetFormula formula,
                                                 final Function<SpreadsheetCell, TextProperties> textProperties) {
        super();
        this.description = description;
        this.priority = priority;
        this.formula = formula;
        this.textProperties = textProperties;
    }

    // description...................................................................................................

    public SpreadsheetDescription description() {
        return this.description;
    }

    public SpreadsheetConditionalFormattingRule setDescription(final SpreadsheetDescription description) {
        checkDescription(description);

        return this.description.equals(description) ?
                this :
                this.replace(description, this.priority, this.formula, this.textProperties);
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
                this.replace(this.description, priority, this.formula, this.textProperties);
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
                this.replace(this.description, this.priority, formula, this.textProperties);
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

    // textProperties...................................................................................................

    public Function<SpreadsheetCell, TextProperties> textProperties() {
        return this.textProperties;
    }

    public SpreadsheetConditionalFormattingRule setTextProperties(final Function<SpreadsheetCell, TextProperties> textProperties) {
        checkTextProperties(textProperties);

        return this.textProperties.equals(textProperties) ?
                this :
                this.replace(this.description, this.priority, this.formula, textProperties);
    }

    private final Function<SpreadsheetCell, TextProperties> textProperties;

    private static void checkTextProperties(final Function<SpreadsheetCell, TextProperties> textProperties) {
        Objects.requireNonNull(textProperties, "textProperties");
    }

    // factory..................................................................................

    /**
     * Factory that unconditionally creates a {@link SpreadsheetConditionalFormattingRule}.
     */
    private SpreadsheetConditionalFormattingRule replace(final SpreadsheetDescription description,
                                                         final int priority,
                                                         final SpreadsheetFormula formula,
                                                         final Function<SpreadsheetCell, TextProperties> textProperties) {
        return new SpreadsheetConditionalFormattingRule(description, priority, formula, textProperties);
    }

    // HashCodeEqualsDefined.........................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.description, this.priority, this.formula, this.textProperties);
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
                this.textProperties.equals(other.textProperties);
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
        builder.value(this.textProperties);
    }
}

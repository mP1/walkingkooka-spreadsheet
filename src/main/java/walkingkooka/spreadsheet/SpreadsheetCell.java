/*
 * Copyright 2018 Miroslav Pokorny (github.com/mP1)
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
 *
 */

package walkingkooka.spreadsheet;

import walkingkooka.Cast;
import walkingkooka.build.tostring.ToStringBuilder;
import walkingkooka.build.tostring.UsesToStringBuilder;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.expression.ExpressionNode;

import java.util.Objects;
import java.util.Optional;

/**
 * A spreadsheet cell including its formula, and other attributes such as format, styles and more.
 */
public final class SpreadsheetCell implements HashCodeEqualsDefined, Comparable<SpreadsheetCell>, UsesToStringBuilder {

    /**
     * No expression constant.
     */
    public final static Optional<ExpressionNode> NO_EXPRESSION = Optional.empty();

    /**
     * No error constant.
     */
    public final static Optional<SpreadsheetError> NO_ERROR = Optional.empty();

    /**
     * No value constant.
     */
    public final static Optional<Object> NO_VALUE = Optional.empty();

    /**
     * Factory that creates a new {@link SpreadsheetCell}
     */
    public static SpreadsheetCell with(final SpreadsheetCellReference reference,
                                       final SpreadsheetFormula formula) {
        checkReference(reference);
        checkFormula(formula);

        return new SpreadsheetCell(reference, formula, NO_EXPRESSION, NO_VALUE, NO_ERROR);
    }

    private static void checkReference(final SpreadsheetCellReference reference) {
        Objects.requireNonNull(reference, "reference");
    }

    private static void checkFormula(final SpreadsheetFormula formula) {
        Objects.requireNonNull(formula, "formula");
    }

    private static void checkExpression(final Optional<ExpressionNode> expression) {
        Objects.requireNonNull(expression, "expression");
    }

    private static void checkError(final Optional<SpreadsheetError> error) {
        Objects.requireNonNull(error, "error");
    }

    private static void checkValue(final Optional<Object> value) {
        Objects.requireNonNull(value, "value");
    }

    private SpreadsheetCell(final SpreadsheetCellReference reference,
                            final SpreadsheetFormula formula,
                            final Optional<ExpressionNode> expression,
                            final Optional<Object> value,
                            final Optional<SpreadsheetError> error) {
        super();

        this.reference = reference;
        this.formula = formula;
        this.expression = expression;
        this.value = value;
        this.error = error;
    }

    // reference .............................................................................................

    public SpreadsheetCellReference reference() {
        return this.reference;
    }

    public SpreadsheetCell setReference(final SpreadsheetCellReference reference) {
        checkReference(reference);

        return this.reference.equals(reference) ?
                this :
                this.replace(reference, this.formula, NO_EXPRESSION, NO_VALUE, NO_ERROR);
    }

    private final SpreadsheetCellReference reference;

    // formula .............................................................................................

    public SpreadsheetFormula formula() {
        return this.formula;
    }

    public SpreadsheetCell setFormula(final SpreadsheetFormula formula) {
        checkFormula(formula);

        return this.formula.equals(formula) ?
                this :
                this.replace(this.reference, formula, NO_EXPRESSION, NO_VALUE, NO_ERROR);
    }

    private final SpreadsheetFormula formula;

    // expression .............................................................................................

    public Optional<ExpressionNode> expression() {
        return this.expression;
    }

    public SpreadsheetCell setExpression(final Optional<ExpressionNode> expression) {
        checkExpression(expression);

        return this.expression.equals(expression) ?
                this :
                this.replace(this.reference, this.formula, expression, NO_VALUE, NO_ERROR);
    }

    /**
     * The expression parsed from the text form of this formula.
     */
    private Optional<ExpressionNode> expression;

    // value .............................................................................................

    public Optional<Object> value() {
        return this.value;
    }

    public SpreadsheetCell setValue(final Optional<Object> value) {
        checkValue(value);

        return this.value.equals(value) ?
                this :
                this.replace(this.reference, this.formula, this.expression, value, NO_ERROR);
    }

    /**
     * The value parsed from the text form of this formula.
     */
    private Optional<Object> value;
    
    // error .............................................................................................

    public Optional<SpreadsheetError> error() {
        return this.error;
    }

    public SpreadsheetCell setError(final Optional<SpreadsheetError> error) {
        checkError(error);

        return this.error.equals(error) ?
                this :
                this.replace(this.reference,
                        this.formula,
                        this.expression,
                        error.isPresent() ? NO_VALUE : this.value, // if error is present clear the value.
                        error);
    }

    /**
     * The error parsed from the text form of this formula.
     */
    private Optional<SpreadsheetError> error;
    
    // internal factory .............................................................................................

    private SpreadsheetCell replace(final SpreadsheetCellReference reference,
                                    final SpreadsheetFormula formula,
                                    final Optional<ExpressionNode> expression,
                                    final Optional<Object> value,
                                    final Optional<SpreadsheetError> error) {
        return new SpreadsheetCell(reference, formula, expression, value, error);
    }

    // Comparable.................................................................................................

    @Override
    public int compareTo(final SpreadsheetCell other) {
        return this.reference().compareTo(other.reference());
    }

    // HashCodeEqualsDefined..........................................................................................

    @Override
    public int hashCode() {
        return this.formula.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetCell &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetCell other) {
        return this.reference.equals(other.reference()) &&
                this.formula.equals(other.formula()) &&
                this.expression.equals((other.expression)) &&
                this.value.equals((other.value)) &&
                this.error.equals(other.error);
    }

    @Override
    public String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    @Override
    public void buildToString(final ToStringBuilder builder) {
        builder.label(this.reference.toString())
                .value(this.formula);

        if(this.value.isPresent()) {
            builder.surroundValues("(=", ")")
                    .value(new Object[]{this.value});
        }
        if(this.error.isPresent()) {
            builder.surroundValues("(", ")")
                    .value(new Object[]{this.error});
        }
    }
}

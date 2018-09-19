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
     * Factory that creates a new {@link SpreadsheetCell}
     */
    public static SpreadsheetCell with(final SpreadsheetCellReference reference,
                                       final SpreadsheetFormula formula) {
        checkReference(reference);
        checkFormula(formula);

        return new SpreadsheetCell(reference, formula);
    }

    private static void checkReference(final SpreadsheetCellReference reference) {
        Objects.requireNonNull(reference, "reference");
    }

    private static void checkFormula(final SpreadsheetFormula formula) {
        Objects.requireNonNull(formula, "formula");
    }

    /**
     * Private ctor
     */
    private SpreadsheetCell(final SpreadsheetCellReference reference,
                            final SpreadsheetFormula formula) {
        super();

        this.reference = reference;
        this.formula = formula;
    }

    // reference .............................................................................................

    public SpreadsheetCellReference reference() {
        return this.reference;
    }

    public SpreadsheetCell setReference(final SpreadsheetCellReference reference) {
        checkReference(reference);

        return this.reference.equals(reference) ?
                this :
                this.replace(reference, this.formula);
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
                this.replace(this.reference, formula);
    }

    private final SpreadsheetFormula formula;


    private SpreadsheetCell replace(final SpreadsheetCellReference reference, final SpreadsheetFormula formula) {
        return new SpreadsheetCell(reference, formula);
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
                this.formula.equals(other.formula());
    }

    @Override
    public String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    @Override
    public void buildToString(final ToStringBuilder builder) {
        builder.label(this.reference.toString())
                .value(this.formula);
    }
}

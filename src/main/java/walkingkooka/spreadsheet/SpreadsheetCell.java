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

import walkingkooka.*;
import walkingkooka.test.*;
import walkingkooka.tree.expression.*;

import java.util.*;

/**
 * A spreadsheet cell including its formula, and other attributes such as format, styles and more.
 */
public final class SpreadsheetCell implements HashCodeEqualsDefined {

    public static SpreadsheetCell with(final SpreadsheetFormula formula) {
        Objects.requireNonNull(formula, "formula");

        return new SpreadsheetCell(formula);
    }

    private SpreadsheetCell(final SpreadsheetFormula formula) {
        super();

        this.formula = formula;
    }

    public SpreadsheetFormula formula() {
        return this.formula;
    }

    public SpreadsheetCell setFormula(final SpreadsheetFormula formula) {
        Objects.requireNonNull(formula, "formula");

        return this.formula.equals(formula) ?
               this :
               this.replace(formula);
    }

    private SpreadsheetFormula formula;

    private SpreadsheetCell replace(final SpreadsheetFormula formula) {
        return new SpreadsheetCell(formula);
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

    private boolean equals0(final SpreadsheetCell id) {
        return this.formula.equals(id.formula());
    }

    @Override
    public String toString() {
        return this.formula.toString();
    }
}

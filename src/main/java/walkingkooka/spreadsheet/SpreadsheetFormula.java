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
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetFunctionName;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserToken;
import walkingkooka.tree.expression.*;

import java.util.*;

/**
 * A spreadsheet formula.
 */
public final class SpreadsheetFormula implements HashCodeEqualsDefined, Value<String> {

    /**
     * A function that replaces cell references with expressions that become invalid due to a deleted row or column.
     */
    public final static SpreadsheetFunctionName INVALID_CELL_REFERENCE = SpreadsheetFunctionName.with("InvalidCellReference");

    /**
     * A {@link SpreadsheetParserToken} that holds the {@link #INVALID_CELL_REFERENCE} function name.
     */
    public final static SpreadsheetParserToken INVALID_CELL_REFERENCE_PARSER_TOKEN = SpreadsheetParserToken.functionName(INVALID_CELL_REFERENCE, INVALID_CELL_REFERENCE.toString());

    /**
     * Factory that creates a new {@link SpreadsheetFormula}
     */
    public static SpreadsheetFormula with(final String value) {
        checkValue(value);

        return new SpreadsheetFormula(value);
    }

    private SpreadsheetFormula(final String value) {
        super();

        this.value = value;
    }

    // Value ....................................................................................................

    @Override
    public String value() {
        return this.value;
    }

    public SpreadsheetFormula setValue(final String value) {
        checkValue(value);
        return this.value.equals(value) ?
               this :
               this.replace(value);
    }

    private String value;

    private static void checkValue(final String value) {
        Objects.requireNonNull(value, "value");
    }

    private static SpreadsheetFormula replace(final String value) {
        return new SpreadsheetFormula(value);
    }

    // HashCodeEqualsDefined..........................................................................................

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
               other instanceof SpreadsheetFormula &&
               this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetFormula id) {
        return this.value.equals(id.value());
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}

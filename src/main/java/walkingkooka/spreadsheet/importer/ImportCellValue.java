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

package walkingkooka.spreadsheet.importer;

import walkingkooka.Cast;
import walkingkooka.Value;
import walkingkooka.spreadsheet.OptionalSpreadsheetValue;
import walkingkooka.spreadsheet.OptionalTextNode;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.format.OptionalSpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.parser.OptionalSpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.HasSpreadsheetReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.text.CharSequences;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.text.TextStyle;

import java.util.Objects;

/**
 * Holds a single cell or patch value. This is necessary to support clipboard operations such as PASTING of a cell range
 * of only formulas as well as PASTING a cell range of entire cells.
 */
public final class ImportCellValue implements HasSpreadsheetReference<SpreadsheetCellReference>,
        Value<Object>,
        TreePrintable {

    public static ImportCellValue cell(final SpreadsheetCell cell) {
        Objects.requireNonNull(cell, "cell");

        return new ImportCellValue(
                cell.reference(),
                cell
        );
    }

    public static ImportCellValue formula(final SpreadsheetCellReference cell,
                                          final SpreadsheetFormula formula) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(formula, "formula");

        return new ImportCellValue(
                cell,
                formula
        );
    }

    public static ImportCellValue formatter(final SpreadsheetCellReference cell,
                                            final OptionalSpreadsheetFormatterSelector formatterSelector) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(formatterSelector, "formatterSelector");

        return new ImportCellValue(
                cell,
                formatterSelector
        );
    }

    public static ImportCellValue parser(final SpreadsheetCellReference cell,
                                         final OptionalSpreadsheetParserSelector parserSelector) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(parserSelector, "parserSelector");

        return new ImportCellValue(
                cell,
                parserSelector
        );
    }

    public static ImportCellValue textStyle(final SpreadsheetCellReference cell,
                                            final TextStyle textStyle) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(textStyle, "textStyle");

        return new ImportCellValue(
                cell,
                textStyle
        );
    }

    public static ImportCellValue value(final SpreadsheetCellReference cell,
                                        final OptionalSpreadsheetValue<?> value) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(value, "value");

        return new ImportCellValue(
                cell,
                value
        );
    }

    public static ImportCellValue formattedValue(final SpreadsheetCellReference cell,
                                                 final OptionalTextNode value) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(value, "value");

        return new ImportCellValue(
                cell,
                value
        );
    }

    private ImportCellValue(final SpreadsheetCellReference reference,
                            final Object value) {
        this.reference = reference;
        this.value = value;
    }

    // HasSpreadsheetReference..........................................................................................

    @Override
    public SpreadsheetCellReference reference() {
        return this.reference;
    }

    private final SpreadsheetCellReference reference;

    // Value............................................................................................................

    @Override
    public Object value() {
        return this.value;
    }

    private final Object value;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.reference,
                this.value
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof ImportCellValue &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final ImportCellValue other) {
        return this.reference.equals(other.reference()) &&
                this.value.equals(other.value);
    }

    @Override
    public String toString() {
        return this.reference + "=" + CharSequences.quoteIfChars(this.value);
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.reference.toString());

        printer.indent();
        {
            TreePrintable.printTreeOrToString(
                    this.value,
                    printer
            );
            printer.lineStart();
        }
        printer.outdent();
    }
}

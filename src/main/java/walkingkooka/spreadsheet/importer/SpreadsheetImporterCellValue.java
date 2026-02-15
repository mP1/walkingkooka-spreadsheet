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
import walkingkooka.spreadsheet.format.provider.OptionalSpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.parser.provider.OptionalSpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.HasSpreadsheetReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.value.OptionalSpreadsheetValue;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.text.CharSequences;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.text.OptionalTextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.util.OptionalCurrency;
import walkingkooka.validation.OptionalValueType;

import java.util.Objects;

/**
 * Holds a single cell or patch value. This is necessary to support clipboard operations such as PASTING of a cell range
 * of only formulas as well as PASTING a cell range of entire cells.
 */
public final class SpreadsheetImporterCellValue implements HasSpreadsheetReference<SpreadsheetCellReference>,
    Value<Object>,
    TreePrintable {

    public static SpreadsheetImporterCellValue cell(final SpreadsheetCell cell) {
        Objects.requireNonNull(cell, "cell");

        return new SpreadsheetImporterCellValue(
            cell.reference(),
            cell
        );
    }

    public static SpreadsheetImporterCellValue currency(final SpreadsheetCellReference cell,
                                                        final OptionalCurrency currency) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(currency, "currency");

        return new SpreadsheetImporterCellValue(
            cell,
            currency
        );
    }

    public static SpreadsheetImporterCellValue formula(final SpreadsheetCellReference cell,
                                                       final SpreadsheetFormula formula) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(formula, "formula");

        return new SpreadsheetImporterCellValue(
            cell,
            formula
        );
    }

    public static SpreadsheetImporterCellValue formatter(final SpreadsheetCellReference cell,
                                                         final OptionalSpreadsheetFormatterSelector formatterSelector) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(formatterSelector, "formatterSelector");

        return new SpreadsheetImporterCellValue(
            cell,
            formatterSelector
        );
    }

    public static SpreadsheetImporterCellValue parser(final SpreadsheetCellReference cell,
                                                      final OptionalSpreadsheetParserSelector parserSelector) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(parserSelector, "parserSelector");

        return new SpreadsheetImporterCellValue(
            cell,
            parserSelector
        );
    }

    public static SpreadsheetImporterCellValue textStyle(final SpreadsheetCellReference cell,
                                                         final TextStyle textStyle) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(textStyle, "textStyle");

        return new SpreadsheetImporterCellValue(
            cell,
            textStyle
        );
    }

    public static SpreadsheetImporterCellValue value(final SpreadsheetCellReference cell,
                                                     final OptionalSpreadsheetValue<?> value) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(value, "value");

        return new SpreadsheetImporterCellValue(
            cell,
            value
        );
    }

    public static SpreadsheetImporterCellValue formattedValue(final SpreadsheetCellReference cell,
                                                              final OptionalTextNode value) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(value, "value");

        return new SpreadsheetImporterCellValue(
            cell,
            value
        );
    }

    public static SpreadsheetImporterCellValue valueType(final SpreadsheetCellReference cell,
                                                         final OptionalValueType valueType) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(valueType, "valueType");

        return new SpreadsheetImporterCellValue(
            cell,
            valueType
        );
    }

    private SpreadsheetImporterCellValue(final SpreadsheetCellReference reference,
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
            other instanceof SpreadsheetImporterCellValue &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetImporterCellValue other) {
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

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
import walkingkooka.spreadsheet.style.SpreadsheetCellStyle;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Objects;
import java.util.Optional;

/**
 * A spreadsheet cell including its formula, and other attributes such as format, styles and more.
 */
public final class SpreadsheetCell implements HashCodeEqualsDefined, Comparable<SpreadsheetCell>, UsesToStringBuilder {

    /**
     * Holds an absent {@link SpreadsheetCellFormat}.
     */
    public final static Optional<SpreadsheetCellFormat> NO_FORMAT = Optional.empty();

    /**
     * Holds an absent {@link SpreadsheetFormattedCell}.
     */
    public final static Optional<SpreadsheetFormattedCell> NO_FORMATTED_CELL = Optional.empty();

    /**
     * Factory that creates a new {@link SpreadsheetCell}
     */
    public static SpreadsheetCell with(final SpreadsheetCellReference reference,
                                       final SpreadsheetFormula formula,
                                       final SpreadsheetCellStyle style,
                                       final Optional<SpreadsheetCellFormat> format,
                                       final Optional<SpreadsheetFormattedCell> formatted) {
        checkReference(reference);
        checkFormula(formula);
        checkStyle(style);
        checkFormat(format);
        checkFormatted(formatted);

        return new SpreadsheetCell(reference, formula, style, format, formatted);
    }

    private static void checkReference(final SpreadsheetCellReference reference) {
        Objects.requireNonNull(reference, "reference");
    }

    private static void checkFormula(final SpreadsheetFormula formula) {
        Objects.requireNonNull(formula, "formula");
    }

    private static void checkStyle(final SpreadsheetCellStyle style) {
        Objects.requireNonNull(style, "style");
    }

    private static void checkFormat(final Optional<SpreadsheetCellFormat> format) {
        Objects.requireNonNull(format, "format");
    }

    private static void checkFormatted(final Optional<SpreadsheetFormattedCell> formatted) {
        Objects.requireNonNull(formatted, "formatted");
    }

    /**
     * Private ctor
     */
    private SpreadsheetCell(final SpreadsheetCellReference reference,
                            final SpreadsheetFormula formula,
                            final SpreadsheetCellStyle style,
                            final Optional<SpreadsheetCellFormat> format,
                            final Optional<SpreadsheetFormattedCell> formatted) {
        super();

        this.reference = reference;
        this.formula = formula;
        this.style = style;
        this.format = format;
        this.formatted = formatted;
    }

    // reference .............................................................................................

    public SpreadsheetCellReference reference() {
        return this.reference;
    }

    public SpreadsheetCell setReference(final SpreadsheetCellReference reference) {
        checkReference(reference);

        return this.reference.equals(reference) ?
                this :
                this.replace(reference, this.formula, this.style, this.format, NO_FORMATTED_CELL);
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
                this.replace(this.reference, formula, this.style, this.format, NO_FORMATTED_CELL);
    }

    private final SpreadsheetFormula formula;

    // style .............................................................................................

    public SpreadsheetCellStyle style() {
        return this.style;
    }

    public SpreadsheetCell setStyle(final SpreadsheetCellStyle style) {
        checkStyle(style);

        return this.style.equals(style) ?
                this :
                this.replace(this.reference, this.formula, style, this.format, NO_FORMATTED_CELL);
    }

    private final SpreadsheetCellStyle style;

    // format .............................................................................................

    public Optional<SpreadsheetCellFormat> format() {
        return this.format;
    }

    public SpreadsheetCell setFormat(final Optional<SpreadsheetCellFormat> format) {
        checkFormat(format);

        return this.format.equals(format) ?
                this :
                this.replace(this.reference, this.formula, this.style, format, NO_FORMATTED_CELL);
    }

    private final Optional<SpreadsheetCellFormat> format;

    // formatted .............................................................................................

    public Optional<SpreadsheetFormattedCell> formatted() {
        return this.formatted;
    }

    public SpreadsheetCell setFormatted(final Optional<SpreadsheetFormattedCell> formatted) {
        checkFormatted(formatted);

        return this.formatted.equals(formatted) ?
                this :
                this.replace(this.reference, this.formula, this.style, this.format, formatted);
    }

    private final Optional<SpreadsheetFormattedCell> formatted;

    // replace..........................................................................................................

    /**
     * Replacing any of the properties other than formatted will clear formatted
     */
    private SpreadsheetCell replace(final SpreadsheetCellReference reference,
                                    final SpreadsheetFormula formula,
                                    final SpreadsheetCellStyle style,
                                    final Optional<SpreadsheetCellFormat> format,
                                    final Optional<SpreadsheetFormattedCell> formatted) {
        return new SpreadsheetCell(reference, formula, style, format, formatted);
    }

    // Comparable.................................................................................................

    @Override
    public int compareTo(final SpreadsheetCell other) {
        return this.reference().compareTo(other.reference());
    }

    // HashCodeEqualsDefined..........................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.reference, this.formula, this.style, this.format, this.formatted);
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
                this.style.equals(other.style) &&
                this.format.equals(other.format) &&
                this.formatted.equals(other.formatted);
    }

    @Override
    public String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    @Override
    public void buildToString(final ToStringBuilder builder) {
        builder.label(this.reference.toString())
                .value(this.formula)
                .value(this.style)
                .value(this.format)
                .value(this.formatted);
    }
}

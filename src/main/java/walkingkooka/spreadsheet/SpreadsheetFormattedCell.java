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
import walkingkooka.build.tostring.ToStringBuilderOption;
import walkingkooka.build.tostring.UsesToStringBuilder;
import walkingkooka.spreadsheet.style.SpreadsheetCellStyle;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.text.HasText;

import java.util.Objects;

/**
 * Holds the text and style for a cell.
 */
public final class SpreadsheetFormattedCell implements HasText, HashCodeEqualsDefined, UsesToStringBuilder {

    /**
     * Creates a {@link SpreadsheetFormattedCell}
     */
    public static SpreadsheetFormattedCell with(final String text, final SpreadsheetCellStyle style) {
        checkText(text);
        checkStyle(style);

        return new SpreadsheetFormattedCell(text, style);
    }

    private static void checkText(final String text) {
        Objects.requireNonNull(text, "text");
    }

    private static void checkStyle(final SpreadsheetCellStyle style) {
        Objects.requireNonNull(style, "style");
    }

    /**
     * Private ctor use factory.
     */
    private SpreadsheetFormattedCell(final String text, final SpreadsheetCellStyle style) {
        super();
        this.text = text;
        this.style = style;
    }

    @Override
    public String text() {
        return this.text;
    }

    public SpreadsheetFormattedCell setText(final String text) {
        checkText(text);

        return this.text.equals(text) ?
                this :
                this.replace(text, this.style);
    }

    private final String text;

    public SpreadsheetCellStyle style() {
        return this.style;
    }

    public SpreadsheetFormattedCell setStyle(final SpreadsheetCellStyle style) {
        checkStyle(style);

        return this.style.equals(style) ?
                this :
                this.replace(this.text, style);
    }

    private final SpreadsheetCellStyle style;

    private SpreadsheetFormattedCell replace(final String text, final SpreadsheetCellStyle style) {
        return new SpreadsheetFormattedCell(text, style);
    }

    // Object ............................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.style, this.text);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetFormattedCell &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetFormattedCell other) {
        return this.style.equals(other.style) &&
                this.text.equals(other.text);
    }

    @Override
    public String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    @Override
    public void buildToString(ToStringBuilder builder) {
        builder.separator(" ");
        builder.enable(ToStringBuilderOption.QUOTE);
        builder.value(this.text);
        builder.value(this.style);
    }
}

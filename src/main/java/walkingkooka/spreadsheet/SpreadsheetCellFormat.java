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

package walkingkooka.spreadsheet;

import walkingkooka.Cast;
import walkingkooka.ToStringBuilder;
import walkingkooka.ToStringBuilderOption;
import walkingkooka.UsesToStringBuilder;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.FromJsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.ToJsonNodeContext;

import java.util.Objects;
import java.util.Optional;

/**
 * Holds the pattern and compiled formatter for a cell.
 */
public final class SpreadsheetCellFormat implements HashCodeEqualsDefined,
        UsesToStringBuilder {

    /**
     * A constant holding no formatter.
     */
    public final static Optional<SpreadsheetFormatter> NO_FORMATTER = Optional.empty();

    /**
     * Creates a {@link SpreadsheetCellFormat}
     */
    public static SpreadsheetCellFormat with(final String pattern) {
        checkPattern(pattern);

        return new SpreadsheetCellFormat(pattern, NO_FORMATTER);
    }

    private static void checkPattern(final String pattern) {
        Objects.requireNonNull(pattern, "pattern");
    }

    static SpreadsheetCellFormat with(final String pattern,
                                      final Optional<SpreadsheetFormatter> formatter) {
        return new SpreadsheetCellFormat(pattern, formatter);
    }

    /**
     * Private ctor use factory.
     */
    private SpreadsheetCellFormat(final String pattern,
                                  final Optional<SpreadsheetFormatter> formatter) {
        super();
        this.pattern = pattern;
        this.formatter = formatter;
    }

    public String pattern() {
        return this.pattern;
    }

    public SpreadsheetCellFormat setPattern(final String pattern) {
        checkPattern(pattern);

        return this.pattern.equals(pattern) ?
                this :
                this.replace(pattern, NO_FORMATTER);
    }

    private final String pattern;

    // formatter...........................................................................

    public Optional<SpreadsheetFormatter> formatter() {
        return this.formatter;
    }

    public SpreadsheetCellFormat setFormatter(final Optional<SpreadsheetFormatter> formatter) {
        Objects.requireNonNull(formatter, "formatter");

        return this.formatter.equals(formatter) ?
                this :
                this.replace(this.pattern, formatter);
    }

    /**
     * The cached or compiled form of the {@link #pattern}
     */
    private final Optional<SpreadsheetFormatter> formatter;

    // replace.............................................................................

    /**
     * Factory that creates a new {@link SpreadsheetCellFormat}
     */
    private SpreadsheetCellFormat replace(final String pattern,
                                          final Optional<SpreadsheetFormatter> formatter) {
        return new SpreadsheetCellFormat(pattern, formatter);
    }

    // JsonNodeContext .................................................................................................

    /**
     * Factory that creates a {@link SpreadsheetCellFormat} from a {@link JsonNode}.
     */
    static SpreadsheetCellFormat fromJsonNode(final JsonNode node,
                                              final FromJsonNodeContext context) {
        return with(node.stringValueOrFail());
    }

    JsonNode toJsonNode(final ToJsonNodeContext context) {
        return JsonNode.string(this.pattern); // formatter not serialized.
    }

    static {
        JsonNodeContext.register("spreadsheet-cell-format",
                SpreadsheetCellFormat::fromJsonNode,
                SpreadsheetCellFormat::toJsonNode,
                SpreadsheetCellFormat.class);
    }

    // Object ..........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.pattern, this.formatter);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetCellFormat &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetCellFormat other) {
        return this.pattern.equals(other.pattern) &&
                this.formatter.equals(other.formatter);
    }

    @Override
    public String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    @Override
    public void buildToString(ToStringBuilder builder) {
        builder.separator(" ");
        builder.enable(ToStringBuilderOption.QUOTE);
        builder.value(this.pattern);
        builder.value(this.formatter);
    }
}

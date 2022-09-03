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
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;

/**
 * Holds the pattern and compiled formatter for a cell.
 */
public final class SpreadsheetCellFormat implements UsesToStringBuilder {

    /**
     * Creates a {@link SpreadsheetCellFormat}
     */
    public static SpreadsheetCellFormat with(final String pattern) {
        checkPattern(pattern);

        return new SpreadsheetCellFormat(pattern);
    }

    private static void checkPattern(final String pattern) {
        Objects.requireNonNull(pattern, "pattern");
    }

    /**
     * Private ctor use factory.
     */
    private SpreadsheetCellFormat(final String pattern) {
        super();
        this.pattern = pattern;
    }

    public String pattern() {
        return this.pattern;
    }

    public SpreadsheetCellFormat setPattern(final String pattern) {
        checkPattern(pattern);

        return this.pattern.equals(pattern) ?
                this :
                new SpreadsheetCellFormat(pattern);
    }

    private final String pattern;

    // JsonNodeContext .................................................................................................

    /**
     * Factory that creates a {@link SpreadsheetCellFormat} from a {@link JsonNode}.
     */
    static SpreadsheetCellFormat unmarshall(final JsonNode node,
                                            final JsonNodeUnmarshallContext context) {
        return with(node.stringOrFail());
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(this.pattern); // formatter not serialized.
    }

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetCellFormat.class),
                SpreadsheetCellFormat::unmarshall,
                SpreadsheetCellFormat::marshall,
                SpreadsheetCellFormat.class
        );
    }

    // Object ..........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.pattern);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetCellFormat &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetCellFormat other) {
        return this.pattern.equals(other.pattern);
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
    }
}

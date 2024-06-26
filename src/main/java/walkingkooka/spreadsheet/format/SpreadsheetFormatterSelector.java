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

package walkingkooka.spreadsheet.format;

import walkingkooka.naming.HasName;
import walkingkooka.plugin.PluginSelector;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternKind;
import walkingkooka.text.HasText;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;
import java.util.Optional;

/**
 * Contains the {@link SpreadsheetFormatterName} and some text which may contain the pattern text for {@link SpreadsheetPatternSpreadsheetFormatter}.
 */
public final class SpreadsheetFormatterSelector implements HasName<SpreadsheetFormatterName>,
        HasText,
        TreePrintable {

    /**
     * Parses the given text into a {@link SpreadsheetFormatterSelector}.
     * <br>
     * Note the format is formatter-name SPACE optional-text, for {@link SpreadsheetPatternSpreadsheetFormatter} the text will hold the raw pattern, without
     * any need for encoding of any kind.
     * <pre>
     * text-format-pattern @
     * </pre>
     */
    public static SpreadsheetFormatterSelector parse(final String text) {
        return new SpreadsheetFormatterSelector(
                PluginSelector.parse(
                        text,
                        SpreadsheetFormatterName::with
                )
        );
    }

    /**
     * Factory that creates a new {@link SpreadsheetFormatterSelector}.
     */
    public static SpreadsheetFormatterSelector with(final SpreadsheetFormatterName name,
                                                    final String text) {
        return new SpreadsheetFormatterSelector(
                PluginSelector.with(
                        name,
                        text
                )
        );
    }

    private SpreadsheetFormatterSelector(final PluginSelector<SpreadsheetFormatterName> selector) {
        this.selector = selector;
    }

    // HasName..........................................................................................................

    @Override
    public SpreadsheetFormatterName name() {
        return this.selector.name();
    }

    /**
     * Would be setter that returns a {@link SpreadsheetFormatterSelector} with the given {@link SpreadsheetFormatterName},
     * creating a new instance if necessary.
     */
    public SpreadsheetFormatterSelector setName(final SpreadsheetFormatterName name) {
        Objects.requireNonNull(name, "name");

        return this.name().equals(name) ?
                this :
                new SpreadsheetFormatterSelector(
                        PluginSelector.with(
                                name,
                                this.text()
                        )
                );
    }

    // HasText..........................................................................................................

    /**
     * If the {@link SpreadsheetFormatterName} identifies a {@link SpreadsheetPatternSpreadsheetFormatter}, this will
     * hold the pattern text itself.
     */
    @Override
    public String text() {
        return this.selector.text();
    }

    private final PluginSelector<SpreadsheetFormatterName> selector;

    // spreadsheetFormatPattern.........................................................................................

    /**
     * Returns a {@link SpreadsheetFormatPattern} if this selector is for a {@link SpreadsheetPatternSpreadsheetFormatter},
     * providing the text is not empty and a valid pattern.
     */
    public Optional<SpreadsheetFormatPattern> spreadsheetFormatPattern() {
        if (null == this.spreadsheetFormatPattern) {
            final SpreadsheetPatternKind patternKind = this.name().patternKind;

            SpreadsheetFormatPattern formatPattern;

            try {
                formatPattern = null == patternKind ?
                        null :
                        patternKind.parse(
                                this.text()
                        ).toFormat();
            } catch (final RuntimeException fail) {
                formatPattern = null;
            }


            this.spreadsheetFormatPattern = Optional.ofNullable(formatPattern);
        }

        return this.spreadsheetFormatPattern;
    }

    private Optional<SpreadsheetFormatPattern> spreadsheetFormatPattern;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.selector.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetFormatterSelector && this.equals0((SpreadsheetFormatterSelector) other);
    }

    private boolean equals0(final SpreadsheetFormatterSelector other) {
        return this.selector.equals(other.selector);
    }

    /**
     * Note it is intentional that the {@link #text()} is not quoted, to ensure {@link #parse(String)} and {@link #toString()}
     * are roundtrippable.
     */
    @Override
    public String toString() {
        return this.selector.toString();
    }

    // JsonNodeContext..................................................................................................

    /**
     * Factory that creates a {@link SpreadsheetFormatterSelector} from a {@link JsonNode}.
     */
    static SpreadsheetFormatterSelector unmarshall(final JsonNode node,
                                                   final JsonNodeUnmarshallContext context) {
        return parse(node.stringOrFail());
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(
                this.toString()
        );
    }

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetFormatterSelector.class),
                SpreadsheetFormatterSelector::unmarshall,
                SpreadsheetFormatterSelector::marshall,
                SpreadsheetFormatterSelector.class
        );
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        this.selector.printTree(printer);
    }
}

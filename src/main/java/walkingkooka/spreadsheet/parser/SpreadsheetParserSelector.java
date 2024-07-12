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

package walkingkooka.spreadsheet.parser;

import walkingkooka.plugin.PluginSelector;
import walkingkooka.plugin.PluginSelectorLike;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternKind;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;
import java.util.Optional;

/**
 * Contains the {@link SpreadsheetParserName} and some text which may contain the pattern text for {@link SpreadsheetParsePattern}.
 */
public final class SpreadsheetParserSelector implements PluginSelectorLike<SpreadsheetParserName> {

    /**
     * Parses the given text into a {@link SpreadsheetParserSelector}.
     * <br>
     * Note the format is spreadsheet-parser-name SPACE optional-text, for {@link SpreadsheetParsePattern} the text will hold the raw pattern, without
     * any need for encoding of any kind.
     * <pre>
     * number-parse-pattern $0.00
     * </pre>
     */
    public static SpreadsheetParserSelector parse(final String text) {
        return new SpreadsheetParserSelector(
                PluginSelector.parse(
                        text,
                        SpreadsheetParserName::with
                )
        );
    }

    /**
     * Factory that creates a new {@link SpreadsheetParserSelector}.
     */
    public static SpreadsheetParserSelector with(final SpreadsheetParserName name,
                                                 final String text) {
        return new SpreadsheetParserSelector(
                PluginSelector.with(
                        name,
                        text
                )
        );
    }

    private SpreadsheetParserSelector(final PluginSelector<SpreadsheetParserName> selector) {
        this.selector = selector;
    }

    // HasName..........................................................................................................

    @Override
    public SpreadsheetParserName name() {
        return this.selector.name();
    }

    /**
     * Would be setter that returns a {@link SpreadsheetParserSelector} with the given {@link SpreadsheetParserName},
     * creating a new instance if necessary.
     */
    public SpreadsheetParserSelector setName(final SpreadsheetParserName name) {
        Objects.requireNonNull(name, "name");

        return this.name().equals(name) ?
                this :
                new SpreadsheetParserSelector(
                        PluginSelector.with(
                                name,
                                this.text()
                        )
                );
    }

    // HasText..........................................................................................................

    /**
     * If the {@link SpreadsheetParserName} identifies a {@link SpreadsheetParsePattern}, this will
     * hold the pattern text itself.
     */
    @Override
    public String text() {
        return this.selector.text();
    }

    private final PluginSelector<SpreadsheetParserName> selector;

    // spreadsheetParsePattern.........................................................................................

    /**
     * Returns a {@link SpreadsheetParsePattern} providing the text is not empty and a valid pattern.
     */
    public Optional<SpreadsheetParsePattern> spreadsheetParsePattern() {
        if (null == this.spreadsheetParserPattern) {
            final SpreadsheetPatternKind patternKind = this.name().patternKind;

            SpreadsheetParsePattern parsePattern;

            try {
                parsePattern = null == patternKind ?
                        null :
                        (SpreadsheetParsePattern)
                                patternKind.parse(this.text());
            } catch (final RuntimeException fail) {
                parsePattern = null;
            }


            this.spreadsheetParserPattern = Optional.ofNullable(parsePattern);
        }

        return this.spreadsheetParserPattern;
    }

    private Optional<SpreadsheetParsePattern> spreadsheetParserPattern;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.selector.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetParserSelector && this.equals0((SpreadsheetParserSelector) other);
    }

    private boolean equals0(final SpreadsheetParserSelector other) {
        return this.selector.equals(other.selector);
    }

    /**
     * Note it is intentional that the {@link #text()} is not quoted, to ensure {@link #parse(String)} and {@link #toString()}
     * are round-trippable.
     */
    @Override
    public String toString() {
        return this.selector.toString();
    }

    // JsonNodeContext..................................................................................................

    /**
     * Factory that creates a {@link SpreadsheetParserSelector} from a {@link JsonNode}.
     */
    static SpreadsheetParserSelector unmarshall(final JsonNode node,
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
                JsonNodeContext.computeTypeName(SpreadsheetParserSelector.class),
                SpreadsheetParserSelector::unmarshall,
                SpreadsheetParserSelector::marshall,
                SpreadsheetParserSelector.class
        );
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        this.selector.printTree(printer);
    }
}

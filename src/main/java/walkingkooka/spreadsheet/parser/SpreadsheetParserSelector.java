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

import walkingkooka.InvalidCharacterException;
import walkingkooka.plugin.PluginSelector;
import walkingkooka.plugin.PluginSelectorLike;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternKind;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;
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
    @Override
    public SpreadsheetParserSelector setName(final SpreadsheetParserName name) {
        Objects.requireNonNull(name, "name");

        return this.name().equals(name) ?
            this :
            new SpreadsheetParserSelector(
                PluginSelector.with(
                    name,
                    this.valueText()
                )
            );
    }

    // HasText..........................................................................................................

    /**
     * If the {@link SpreadsheetParserName} identifies a {@link SpreadsheetParsePattern}, this will
     * hold the pattern text itself.
     */
    @Override
    public String valueText() {
        return this.selector.valueText();
    }

    @Override
    public SpreadsheetParserSelector setValueText(final String text) {
        final PluginSelector<SpreadsheetParserName> different = this.selector.setValueText(text);
        return this.selector.equals(different) ?
            this :
            new SpreadsheetParserSelector(different);
    }

    private final PluginSelector<SpreadsheetParserName> selector;

    @Override
    public SpreadsheetParserSelector setValues(final List<?> values) {
        final PluginSelector<SpreadsheetParserName> different = this.selector.setValues(values);
        return this.selector.equals(different) ?
            this :
            new SpreadsheetParserSelector(different);
    }

    /**
     * Parses the text as an expression that may contain String literals, numbers or {@link SpreadsheetParserName}.
     */
    public SpreadsheetParser evaluateValueText(final SpreadsheetParserProvider provider,
                                               final ProviderContext context) {
        Objects.requireNonNull(provider, "provider");
        Objects.requireNonNull(context, "context");

        return this.selector.evaluateValueText(
            SpreadsheetParserPluginHelper.INSTANCE::parseName,
            provider::spreadsheetParser,
            context
        );
    }

    // spreadsheetParsePattern.........................................................................................

    /**
     * Returns a {@link SpreadsheetParsePattern} providing the text is not empty and a valid pattern.
     */
    public Optional<SpreadsheetParsePattern> spreadsheetParsePattern() {
        if (null == this.spreadsheetParsePattern) {
            final SpreadsheetPatternKind patternKind = this.name()
                .patternKind;

            this.spreadsheetParsePattern = Optional.ofNullable(
                null == patternKind ?
                    null :
                    tryParse(patternKind)
            );
        }

        return this.spreadsheetParsePattern;
    }

    private SpreadsheetParsePattern tryParse(final SpreadsheetPatternKind kind) {
        final String text = this.valueText();

        try {
            return (SpreadsheetParsePattern) kind.parse(text);
        } catch (final InvalidCharacterException cause) {
            throw cause.setTextAndPosition(
                this.toString(),
                this.name()
                    .value().length() +
                    (text.isEmpty() ? 0 : 1) +
                    cause.position()
            );
        }
    }

    private Optional<SpreadsheetParsePattern> spreadsheetParsePattern;

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
        return this.selector.marshall(context);
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

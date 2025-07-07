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

import walkingkooka.plugin.PluginSelector;
import walkingkooka.plugin.PluginSelectorLike;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;
import java.util.Objects;

/**
 * Selects a {@link SpreadsheetImporter}.
 */
public final class SpreadsheetImporterSelector implements PluginSelectorLike<SpreadsheetImporterName> {

    /**
     * A parser that returns a {@link SpreadsheetImporterName}.
     */
    private final static Parser<ParserContext> NAME_PARSER = Parsers.initialAndPartCharPredicateString(
        (c) -> SpreadsheetImporterName.isChar(0, c),
        (c) -> SpreadsheetImporterName.isChar(1, c),
        1,
        SpreadsheetImporterName.MAX_LENGTH
    );

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetImporterSelector.class),
            SpreadsheetImporterSelector::unmarshall,
            SpreadsheetImporterSelector::marshall,
            SpreadsheetImporterSelector.class
        );
    }

    private final PluginSelector<SpreadsheetImporterName> selector;

    // HasName..........................................................................................................

    private SpreadsheetImporterSelector(final PluginSelector<SpreadsheetImporterName> selector) {
        this.selector = selector;
    }

    /**
     * Parses the given text into a {@link SpreadsheetImporterSelector}.
     */
    public static SpreadsheetImporterSelector parse(final String text) {
        return new SpreadsheetImporterSelector(
            PluginSelector.parse(
                text,
                SpreadsheetImporterName::with
            )
        );
    }

    // HasText..........................................................................................................

    /**
     * Factory that creates a new {@link SpreadsheetImporterSelector}.
     */
    public static SpreadsheetImporterSelector with(final SpreadsheetImporterName name,
                                                   final String text) {
        return new SpreadsheetImporterSelector(
            PluginSelector.with(
                name,
                text
            )
        );
    }

    /**
     * Factory that creates a {@link SpreadsheetImporterSelector} from a {@link JsonNode}.
     */
    static SpreadsheetImporterSelector unmarshall(final JsonNode node,
                                                  final JsonNodeUnmarshallContext context) {
        return parse(node.stringOrFail());
    }

    // HasName..........................................................................................................

    @Override
    public SpreadsheetImporterName name() {
        return this.selector.name();
    }

    /**
     * Would be setter that returns a {@link SpreadsheetImporterSelector} with the given {@link SpreadsheetImporterName},
     * creating a new instance if necessary.
     */
    @Override
    public SpreadsheetImporterSelector setName(final SpreadsheetImporterName name) {
        Objects.requireNonNull(name, "name");

        return this.name().equals(name) ?
            this :
            new SpreadsheetImporterSelector(
                PluginSelector.with(
                    name,
                    this.valueText()
                )
            );
    }

    // value............................................................................................................

    /**
     * If the {@link SpreadsheetImporterName} identifies a {@link SpreadsheetImporter}
     */
    @Override
    public String valueText() {
        return this.selector.valueText();
    }

    @Override
    public SpreadsheetImporterSelector setValueText(final String text) {
        final PluginSelector<SpreadsheetImporterName> different = this.selector.setValueText(text);
        return this.selector.equals(different) ?
            this :
            new SpreadsheetImporterSelector(different);
    }

    @Override
    public SpreadsheetImporterSelector setValues(final List<?> values) {
        final PluginSelector<SpreadsheetImporterName> different = this.selector.setValues(values);
        return this.selector.equals(different) ?
            this :
            new SpreadsheetImporterSelector(different);
    }

    /**
     * Parses the {@link #valueText()} as an expression that may contain String literals, numbers or {@link SpreadsheetImporterName}.
     */
    public SpreadsheetImporter evaluateValueText(final SpreadsheetImporterProvider provider,
                                                 final ProviderContext context) {
        Objects.requireNonNull(provider, "provider");
        Objects.requireNonNull(context, "context");

        return this.selector.evaluateValueText(
            SpreadsheetImporterPluginHelper.INSTANCE::parseName,
            provider::spreadsheetImporter,
            context
        );
    }

    @Override
    public int hashCode() {
        return this.selector.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetImporterSelector && this.equals0((SpreadsheetImporterSelector) other);
    }

    // JsonNodeContext..................................................................................................

    private boolean equals0(final SpreadsheetImporterSelector other) {
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

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return this.selector.marshall(context);
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        this.selector.printTree(printer);
    }
}

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

package walkingkooka.spreadsheet.export.provider;

import walkingkooka.plugin.PluginSelector;
import walkingkooka.plugin.PluginSelectorLike;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.export.SpreadsheetExporter;
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
 * Selects a {@link SpreadsheetExporter}.
 */
public final class SpreadsheetExporterSelector implements PluginSelectorLike<SpreadsheetExporterName> {

    /**
     * A parser that returns a {@link SpreadsheetExporterName}.
     */
    private final static Parser<ParserContext> NAME_PARSER = Parsers.initialAndPartCharPredicateString(
        (c) -> SpreadsheetExporterName.isChar(0, c),
        (c) -> SpreadsheetExporterName.isChar(1, c),
        1,
        SpreadsheetExporterName.MAX_LENGTH
    );

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetExporterSelector.class),
            SpreadsheetExporterSelector::unmarshall,
            SpreadsheetExporterSelector::marshall,
            SpreadsheetExporterSelector.class
        );
    }

    private final PluginSelector<SpreadsheetExporterName> selector;

    // HasName..........................................................................................................

    private SpreadsheetExporterSelector(final PluginSelector<SpreadsheetExporterName> selector) {
        this.selector = selector;
    }

    /**
     * Parses the given text into a {@link SpreadsheetExporterSelector}.
     */
    public static SpreadsheetExporterSelector parse(final String text) {
        return new SpreadsheetExporterSelector(
            PluginSelector.parse(
                text,
                SpreadsheetExporterName::with
            )
        );
    }

    // HasText..........................................................................................................

    /**
     * Factory that creates a new {@link SpreadsheetExporterSelector}.
     */
    public static SpreadsheetExporterSelector with(final SpreadsheetExporterName name,
                                                   final String text) {
        return new SpreadsheetExporterSelector(
            PluginSelector.with(
                name,
                text
            )
        );
    }

    /**
     * Factory that creates a {@link SpreadsheetExporterSelector} from a {@link JsonNode}.
     */
    static SpreadsheetExporterSelector unmarshall(final JsonNode node,
                                                  final JsonNodeUnmarshallContext context) {
        return parse(node.stringOrFail());
    }

    @Override
    public SpreadsheetExporterName name() {
        return this.selector.name();
    }

    // setValue.........................................................................................................

    /**
     * Would be setter that returns a {@link SpreadsheetExporterSelector} with the given {@link SpreadsheetExporterName},
     * creating a new instance if necessary.
     */
    @Override
    public SpreadsheetExporterSelector setName(final SpreadsheetExporterName name) {
        Objects.requireNonNull(name, "name");

        return this.name().equals(name) ?
            this :
            new SpreadsheetExporterSelector(
                PluginSelector.with(
                    name,
                    this.valueText()
                )
            );
    }

    /**
     * If the {@link SpreadsheetExporterName} identifies a {@link SpreadsheetExporter}
     */
    @Override
    public String valueText() {
        return this.selector.valueText();
    }

    @Override
    public SpreadsheetExporterSelector setValueText(final String text) {
        final PluginSelector<SpreadsheetExporterName> different = this.selector.setValueText(text);
        return this.selector.equals(different) ?
            this :
            new SpreadsheetExporterSelector(different);
    }

    @Override
    public SpreadsheetExporterSelector setValues(final List<?> values) {
        final PluginSelector<SpreadsheetExporterName> different = this.selector.setValues(values);
        return this.selector.equals(different) ?
            this :
            new SpreadsheetExporterSelector(different);
    }

    /**
     * Parses the text as an expression that may contain String literals, numbers or {@link SpreadsheetExporterName}.
     */
    public SpreadsheetExporter evaluateValueText(final SpreadsheetExporterProvider provider,
                                                 final ProviderContext context) {
        Objects.requireNonNull(provider, "provider");
        Objects.requireNonNull(context, "context");

        return this.selector.evaluateValueText(
            SpreadsheetExporterPluginHelper.INSTANCE::parseName,
            provider::spreadsheetExporter,
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
            other instanceof SpreadsheetExporterSelector && this.equals0((SpreadsheetExporterSelector) other);
    }

    // JsonNodeContext..................................................................................................

    private boolean equals0(final SpreadsheetExporterSelector other) {
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

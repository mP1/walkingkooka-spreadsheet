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
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.cursor.parser.StringParserToken;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;
import java.util.Objects;

/**
 * Selects a {@link SpreadsheetCellImporter}.
 */
public final class SpreadsheetCellImporterSelector implements PluginSelectorLike<SpreadsheetCellImporterName> {

    /**
     * A parser that returns a {@link SpreadsheetCellImporterName}.
     */
    private final static Parser<ParserContext> NAME_PARSER = Parsers.stringInitialAndPartCharPredicate(
            (c) -> SpreadsheetCellImporterName.isChar(0, c),
            (c) -> SpreadsheetCellImporterName.isChar(1, c),
            1,
            SpreadsheetCellImporterName.MAX_LENGTH
    );

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetCellImporterSelector.class),
                SpreadsheetCellImporterSelector::unmarshall,
                SpreadsheetCellImporterSelector::marshall,
                SpreadsheetCellImporterSelector.class
        );
    }

    private final PluginSelector<SpreadsheetCellImporterName> selector;

    // HasName..........................................................................................................

    private SpreadsheetCellImporterSelector(final PluginSelector<SpreadsheetCellImporterName> selector) {
        this.selector = selector;
    }

    /**
     * Parses the given text into a {@link SpreadsheetCellImporterSelector}.
     */
    public static SpreadsheetCellImporterSelector parse(final String text) {
        return new SpreadsheetCellImporterSelector(
                PluginSelector.parse(
                        text,
                        SpreadsheetCellImporterName::with
                )
        );
    }

    // HasText..........................................................................................................

    /**
     * Factory that creates a new {@link SpreadsheetCellImporterSelector}.
     */
    public static SpreadsheetCellImporterSelector with(final SpreadsheetCellImporterName name,
                                                       final String text) {
        return new SpreadsheetCellImporterSelector(
                PluginSelector.with(
                        name,
                        text
                )
        );
    }

    /**
     * Factory that creates a {@link SpreadsheetCellImporterSelector} from a {@link JsonNode}.
     */
    static SpreadsheetCellImporterSelector unmarshall(final JsonNode node,
                                                      final JsonNodeUnmarshallContext context) {
        return parse(node.stringOrFail());
    }

    @Override
    public SpreadsheetCellImporterName name() {
        return this.selector.name();
    }

    // setValue.........................................................................................................

    /**
     * Would be setter that returns a {@link SpreadsheetCellImporterSelector} with the given {@link SpreadsheetCellImporterName},
     * creating a new instance if necessary.
     */
    public SpreadsheetCellImporterSelector setName(final SpreadsheetCellImporterName name) {
        Objects.requireNonNull(name, "name");

        return this.name().equals(name) ?
                this :
                new SpreadsheetCellImporterSelector(
                        PluginSelector.with(
                                name,
                                this.text()
                        )
                );
    }

    /**
     * If the {@link SpreadsheetCellImporterName} identifies a {@link SpreadsheetCellImporter}
     */
    @Override
    public String text() {
        return this.selector.text();
    }

    @Override
    public SpreadsheetCellImporterSelector setText(final String text) {
        final PluginSelector<SpreadsheetCellImporterName> different = this.selector.setText(text);
        return this.selector.equals(different) ?
                this :
                new SpreadsheetCellImporterSelector(different);
    }

    // Object...........................................................................................................

    @Override
    public SpreadsheetCellImporterSelector setValues(final List<?> values) {
        final PluginSelector<SpreadsheetCellImporterName> different = this.selector.setValues(values);
        return this.selector.equals(different) ?
                this :
                new SpreadsheetCellImporterSelector(different);
    }

    /**
     * Parses the text as an expression that may contain String literals, numbers or {@link SpreadsheetCellImporterName}.
     */
    public SpreadsheetCellImporter evaluateText(final SpreadsheetCellImporterProvider provider,
                                                final ProviderContext context) {
        Objects.requireNonNull(provider, "provider");
        Objects.requireNonNull(context, "context");

        return this.selector.evaluateText(
                (final TextCursor cursor, final ParserContext c) -> NAME_PARSER.parse(
                        cursor,
                        c
                ).map(
                        (final ParserToken token) ->
                                SpreadsheetCellImporterName.with(
                                        token.cast(StringParserToken.class)
                                                .value()
                                )
                ),
                provider::spreadsheetCellImporter,
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
                other instanceof SpreadsheetCellImporterSelector && this.equals0((SpreadsheetCellImporterSelector) other);
    }

    // JsonNodeContext..................................................................................................

    private boolean equals0(final SpreadsheetCellImporterSelector other) {
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

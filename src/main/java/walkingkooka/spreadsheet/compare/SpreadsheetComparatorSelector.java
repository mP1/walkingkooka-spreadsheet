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

package walkingkooka.spreadsheet.compare;

import walkingkooka.Cast;
import walkingkooka.plugin.PluginSelector;
import walkingkooka.plugin.PluginSelectorLike;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;
import java.util.Objects;

/**
 * Contains the {@link SpreadsheetComparatorName} and some text which may contain an expression for a {@link SpreadsheetComparator}.
 */
public final class SpreadsheetComparatorSelector implements PluginSelectorLike<SpreadsheetComparatorName> {

    /**
     * Parses the given text into a {@link SpreadsheetComparatorSelector}. Note the text following the {@link SpreadsheetComparatorName} is not validated in any form and simply stored.
     */
    public static SpreadsheetComparatorSelector parse(final String text) {
        return new SpreadsheetComparatorSelector(
            PluginSelector.parse(
                text,
                SpreadsheetComparatorName::with
            )
        );
    }

    /**
     * Factory that creates a new {@link SpreadsheetComparatorSelector}.
     */
    public static SpreadsheetComparatorSelector with(final SpreadsheetComparatorName name,
                                                     final String text) {
        return new SpreadsheetComparatorSelector(
            PluginSelector.with(
                name,
                text
            )
        );
    }

    private SpreadsheetComparatorSelector(final PluginSelector<SpreadsheetComparatorName> selector) {
        this.selector = selector;
    }

    // HasName..........................................................................................................

    @Override
    public SpreadsheetComparatorName name() {
        return this.selector.name();
    }

    /**
     * Would be setter that returns a {@link SpreadsheetComparatorSelector} with the given {@link SpreadsheetComparatorName},
     * creating a new instance if necessary.
     */
    @Override
    public SpreadsheetComparatorSelector setName(final SpreadsheetComparatorName name) {
        Objects.requireNonNull(name, "name");

        return this.name().equals(name) ?
            this :
            new SpreadsheetComparatorSelector(
                PluginSelector.with(
                    name,
                    this.valueText()
                )
            );
    }

    // value............................................................................................................

    /**
     * If the {@link SpreadsheetComparatorName} identifies a {@link SpreadsheetComparator}, this will
     * hold the pattern text itself.
     */
    @Override
    public String valueText() {
        return this.selector.valueText();
    }

    @Override
    public SpreadsheetComparatorSelector setValueText(final String text) {
        final PluginSelector<SpreadsheetComparatorName> different = this.selector.setValueText(text);
        return this.selector.equals(different) ?
            this :
            new SpreadsheetComparatorSelector(different);
    }

    private final PluginSelector<SpreadsheetComparatorName> selector;

    @Override
    public SpreadsheetComparatorSelector setValues(final List<?> values) {
        final PluginSelector<SpreadsheetComparatorName> different = this.selector.setValues(values);
        return this.selector.equals(different) ?
            this :
            new SpreadsheetComparatorSelector(different);
    }

    // evaluateValueText................................................................................................

    /**
     * Parses the {@link #valueText()} as an expression that contains an optional parameter list which may include
     * <ul>
     * <li>{@link SpreadsheetComparatorName}</li>
     * <li>double literals including negative or leading minus signs.</li>
     * <li>a double-quoted string literal</li>
     * </ul>
     * Sample text.
     * <pre>
     * number-to-number
     * collection ( number-to-boolean, number-number, string-to-local-date "yyyy-mm-dd")
     * </pre>
     * The {@link SpreadsheetComparatorProvider} will be used to fetch {@link SpreadsheetComparator} with any parameters.
     */
    public <C extends SpreadsheetComparatorContext> SpreadsheetComparator<C> evaluateValueText(final SpreadsheetComparatorProvider provider,
                                                                                               final ProviderContext context) {
        Objects.requireNonNull(provider, "provider");
        Objects.requireNonNull(context, "context");

        return this.selector.evaluateValueText(
            SpreadsheetComparatorPluginHelper.INSTANCE::parseName,
            (n, v, c) -> Cast.to(
                provider.spreadsheetComparator(
                    n,
                    v,
                    c
                )
            ),
            context
        );
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.selector.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetComparatorSelector && this.equals0((SpreadsheetComparatorSelector) other);
    }

    private boolean equals0(final SpreadsheetComparatorSelector other) {
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
     * Factory that creates a {@link SpreadsheetComparatorSelector} from a {@link JsonNode}.
     */
    static SpreadsheetComparatorSelector unmarshall(final JsonNode node,
                                                    final JsonNodeUnmarshallContext context) {
        return parse(node.stringOrFail());
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return this.selector.marshall(context);
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetComparatorSelector.class),
            SpreadsheetComparatorSelector::unmarshall,
            SpreadsheetComparatorSelector::marshall,
            SpreadsheetComparatorSelector.class
        );
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        this.selector.printTree(printer);
    }
}
    

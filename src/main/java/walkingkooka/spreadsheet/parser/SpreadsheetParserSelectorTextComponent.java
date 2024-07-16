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

import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.plugin.PluginSelectorTextComponent;
import walkingkooka.plugin.PluginSelectorTextComponentLike;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;
import java.util.Optional;

/**
 * Can be used to represent a single token with a parser pattern or large text representation.
 * For a date pattern such as (ignore added spaces) would have 5 tokens, the year, slash, month, slash and day.
 * <pre>
 * yyyy / mm / dd
 * </pre>
 */
public final class SpreadsheetParserSelectorTextComponent implements PluginSelectorTextComponentLike<SpreadsheetParserSelectorTextComponentAlternative> {

    /**
     * A constant representing no alternatives.
     */
    public final static List<SpreadsheetParserSelectorTextComponentAlternative> NO_ALTERNATIVES = Lists.empty();

    /**
     * This method is intended to only be called by {@link SpreadsheetParser} that contain a {@link SpreadsheetFormatParserToken}.
     */
    static Optional<List<SpreadsheetParserSelectorTextComponent>> textComponents(final SpreadsheetFormatParserToken token,
                                                                                 final SpreadsheetParserContext context) {
        return Optional.of(
                SpreadsheetParserSelectorTextComponentSpreadsheetFormatParserTokenVisitor.textComponents(
                        token,
                        context
                )
        );
    }

    /**
     * General purpose factory that creates a new {@link SpreadsheetParserSelectorTextComponent}.
     */
    public static SpreadsheetParserSelectorTextComponent with(final String label,
                                                              final String text,
                                                              final List<SpreadsheetParserSelectorTextComponentAlternative> alternatives) {
        return new SpreadsheetParserSelectorTextComponent(
                PluginSelectorTextComponent.with(
                        label,
                        text,
                        alternatives
                )
        );
    }

    private SpreadsheetParserSelectorTextComponent(final PluginSelectorTextComponent<SpreadsheetParserSelectorTextComponentAlternative> component) {
        this.component = component;
    }

    @Override
    public String label() {
        return this.component.label();
    }

    @Override
    public String text() {
        return this.component.text();
    }

    @Override
    public List<SpreadsheetParserSelectorTextComponentAlternative> alternatives() {
        return this.component.alternatives();
    }

    private final PluginSelectorTextComponent<SpreadsheetParserSelectorTextComponentAlternative> component;

    // HashCodeEqualsDefined..........................................................................................

    @Override
    public int hashCode() {
        return this.component.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetParserSelectorTextComponent &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetParserSelectorTextComponent other) {
        return this.component.equals(other.component);
    }

    @Override
    public String toString() {
        return this.component.toString();
    }

    // json.............................................................................................................

    static SpreadsheetParserSelectorTextComponent unmarshall(final JsonNode node,
                                                             final JsonNodeUnmarshallContext context) {
        return PluginSelectorTextComponentLike.unmarshall(
                node,
                context,
                SpreadsheetParserSelectorTextComponent::with,
                SpreadsheetParserSelectorTextComponentAlternative.class
        );
    }

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetParserSelectorTextComponent.class),
                SpreadsheetParserSelectorTextComponent::unmarshall,
                SpreadsheetParserSelectorTextComponent::marshall,
                SpreadsheetParserSelectorTextComponent.class
        );
    }
}

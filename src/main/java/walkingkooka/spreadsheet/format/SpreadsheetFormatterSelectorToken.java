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

import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.plugin.PluginSelectorToken;
import walkingkooka.plugin.PluginSelectorTokenLike;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;

/**
 * Can be used to represent a single token with a format pattern or large text representation.
 * For a date pattern such as (ignore added spaces) would have 5 tokens, the year, slash, month, slash and day.
 * <pre>
 * yyyy / mm / dd
 * </pre>
 */
public final class SpreadsheetFormatterSelectorToken implements PluginSelectorTokenLike<SpreadsheetFormatterSelectorTokenAlternative> {

    /**
     * A constant representing no alternatives.
     */
    public final static List<SpreadsheetFormatterSelectorTokenAlternative> NO_ALTERNATIVES = Lists.empty();

    /**
     * This method is intended to only be called by {@link SpreadsheetFormatter} that contain a {@link SpreadsheetFormatParserToken}.
     */
    static List<SpreadsheetFormatterSelectorToken> tokens(final SpreadsheetFormatParserToken token) {
        return SpreadsheetFormatterSelectorTokensSpreadsheetFormatParserTokenVisitor.tokens(token);
    }

    /**
     * General purpose factory that creates a new {@link SpreadsheetFormatterSelectorToken}.
     */
    public static SpreadsheetFormatterSelectorToken with(final String label,
                                                         final String text,
                                                         final List<SpreadsheetFormatterSelectorTokenAlternative> alternatives) {
        return new SpreadsheetFormatterSelectorToken(
            PluginSelectorToken.with(
                label,
                text,
                alternatives
            )
        );
    }

    private SpreadsheetFormatterSelectorToken(final PluginSelectorToken<SpreadsheetFormatterSelectorTokenAlternative> component) {
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
    public List<SpreadsheetFormatterSelectorTokenAlternative> alternatives() {
        return this.component.alternatives();
    }

    private final PluginSelectorToken<SpreadsheetFormatterSelectorTokenAlternative> component;

    // HashCodeEqualsDefined..........................................................................................

    @Override
    public int hashCode() {
        return this.component.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetFormatterSelectorToken &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetFormatterSelectorToken other) {
        return this.component.equals(other.component);
    }

    @Override
    public String toString() {
        return this.component.toString();
    }

    // json.............................................................................................................

    static SpreadsheetFormatterSelectorToken unmarshall(final JsonNode node,
                                                        final JsonNodeUnmarshallContext context) {
        return PluginSelectorTokenLike.unmarshall(
            node,
            context,
            SpreadsheetFormatterSelectorToken::with,
            SpreadsheetFormatterSelectorTokenAlternative.class
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetFormatterSelectorToken.class),
            SpreadsheetFormatterSelectorToken::unmarshall,
            SpreadsheetFormatterSelectorToken::marshall,
            SpreadsheetFormatterSelectorToken.class
        );
    }
}

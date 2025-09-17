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

package walkingkooka.spreadsheet.parser.provider;

import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.plugin.PluginSelectorToken;
import walkingkooka.plugin.PluginSelectorTokenLike;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;

/**
 * Can be used to represent a single token with a parser pattern or large text representation.
 * For a date pattern such as (ignore added spaces) would have 5 tokens, the year, slash, month, slash and day.
 * <pre>
 * yyyy / mm / dd
 * </pre>
 */
public final class SpreadsheetParserSelectorToken implements PluginSelectorTokenLike<SpreadsheetParserSelectorTokenAlternative> {

    /**
     * A constant representing no alternatives.
     */
    public final static List<SpreadsheetParserSelectorTokenAlternative> NO_ALTERNATIVES = Lists.empty();

    /**
     * This method is intended to only be called by {@link SpreadsheetParser} that contain a {@link SpreadsheetFormatParserToken}.
     */
    public static List<SpreadsheetParserSelectorToken> tokens(final ParserToken token) {
        return SpreadsheetParserSelectorTokensSpreadsheetFormatParserTokenVisitor.tokens(token);
    }

    /**
     * General purpose factory that creates a new {@link SpreadsheetParserSelectorToken}.
     */
    public static SpreadsheetParserSelectorToken with(final String label,
                                                      final String text,
                                                      final List<SpreadsheetParserSelectorTokenAlternative> alternatives) {
        return new SpreadsheetParserSelectorToken(
            PluginSelectorToken.with(
                label,
                text,
                alternatives
            )
        );
    }

    private SpreadsheetParserSelectorToken(final PluginSelectorToken<SpreadsheetParserSelectorTokenAlternative> component) {
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
    public List<SpreadsheetParserSelectorTokenAlternative> alternatives() {
        return this.component.alternatives();
    }

    private final PluginSelectorToken<SpreadsheetParserSelectorTokenAlternative> component;

    // HashCodeEqualsDefined..........................................................................................

    @Override
    public int hashCode() {
        return this.component.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetParserSelectorToken &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetParserSelectorToken other) {
        return this.component.equals(other.component);
    }

    @Override
    public String toString() {
        return this.component.toString();
    }

    // json.............................................................................................................

    static SpreadsheetParserSelectorToken unmarshall(final JsonNode node,
                                                     final JsonNodeUnmarshallContext context) {
        return PluginSelectorTokenLike.unmarshall(
            node,
            context,
            SpreadsheetParserSelectorToken::with,
            SpreadsheetParserSelectorTokenAlternative.class
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetParserSelectorToken.class),
            SpreadsheetParserSelectorToken::unmarshall,
            SpreadsheetParserSelectorToken::marshall,
            SpreadsheetParserSelectorToken.class
        );
    }
}

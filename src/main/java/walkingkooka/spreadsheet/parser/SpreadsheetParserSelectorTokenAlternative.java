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
import walkingkooka.plugin.PluginSelectorTokenAlternative;
import walkingkooka.plugin.PluginSelectorTokenAlternativeLike;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

/**
 * A single alternative for a {@link SpreadsheetParserSelectorToken}.
 */
public final class SpreadsheetParserSelectorTokenAlternative implements PluginSelectorTokenAlternativeLike {
    public static SpreadsheetParserSelectorTokenAlternative with(final String label,
                                                                 final String text) {
        return new SpreadsheetParserSelectorTokenAlternative(
            PluginSelectorTokenAlternative.with(
                label,
                text
            )
        );
    }

    private SpreadsheetParserSelectorTokenAlternative(final PluginSelectorTokenAlternative alternative) {
        this.alternative = alternative;
    }

    @Override
    public String label() {
        return this.alternative.label();
    }

    @Override
    public String text() {
        return this.alternative.text();
    }

    private final PluginSelectorTokenAlternative alternative;

    // HashCodeEqualsDefined..........................................................................................

    @Override
    public int hashCode() {
        return this.alternative.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetParserSelectorTokenAlternative &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetParserSelectorTokenAlternative other) {
        return this.alternative.equals(other.alternative);
    }

    @Override
    public String toString() {
        return CharSequences.quoteAndEscape(this.label()) + " " + CharSequences.quoteAndEscape(this.text());
    }

    // json.............................................................................................................

    static SpreadsheetParserSelectorTokenAlternative unmarshall(final JsonNode node,
                                                                final JsonNodeUnmarshallContext context) {
        return PluginSelectorTokenAlternativeLike.unmarshall(
            node,
            context,
            SpreadsheetParserSelectorTokenAlternative::with
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetParserSelectorTokenAlternative.class),
            SpreadsheetParserSelectorTokenAlternative::unmarshall,
            SpreadsheetParserSelectorTokenAlternative::marshall,
            SpreadsheetParserSelectorTokenAlternative.class
        );
    }
}

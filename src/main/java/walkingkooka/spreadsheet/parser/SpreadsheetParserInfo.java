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
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.plugin.PluginInfoLike;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;

/**
 * Provides a few bits of info describing a {@link Parser}. The {@link AbsoluteUrl} must be a unique identifier,
 * with the {@link SpreadsheetParserName} being a shorter human friendly reference.
 */
public final class SpreadsheetParserInfo implements PluginInfoLike<SpreadsheetParserInfo, SpreadsheetParserName> {

    public static SpreadsheetParserInfo parse(final String text) {
        return PluginInfoLike.parse(
                text,
                SpreadsheetParserName::with,
                SpreadsheetParserInfo::with
        );
    }

    public static SpreadsheetParserInfo with(final AbsoluteUrl url,
                                             final SpreadsheetParserName name) {
        return new SpreadsheetParserInfo(
                Objects.requireNonNull(url, "url"),
                Objects.requireNonNull(name, "name")
        );
    }

    private SpreadsheetParserInfo(final AbsoluteUrl url,
                                  final SpreadsheetParserName name) {
        this.url = url;
        this.name = name;
    }

    // HasAbsoluteUrl...................................................................................................

    @Override
    public AbsoluteUrl url() {
        return this.url;
    }

    private final AbsoluteUrl url;

    // HasName..........................................................................................................

    @Override
    public SpreadsheetParserName name() {
        return this.name;
    }

    @Override
    public SpreadsheetParserInfo setName(final SpreadsheetParserName name) {
        Objects.requireNonNull(name, "name");

        return this.name.equals(name) ?
                this :
                new SpreadsheetParserInfo(
                        this.url,
                        name
                );
    }

    private final SpreadsheetParserName name;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.url,
                this.name
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetParserInfo &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetParserInfo other) {
        return this.url.equals(other.url) &&
                this.name.equals(other.name);
    }

    @Override
    public String toString() {
        return PluginInfoLike.toString(this);
    }

    // Json.............................................................................................................

    static void register() {
        // required to FORCE json register
        SpreadsheetParserName.DATE_PARSER_PATTERN.value();
    }

    static SpreadsheetParserInfo unmarshall(final JsonNode node,
                                            final JsonNodeUnmarshallContext context) {
        return PluginInfoLike.unmarshall(
                node,
                context,
                SpreadsheetParserName::with,
                SpreadsheetParserInfo::with
        );
    }

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetParserInfo.class),
                SpreadsheetParserInfo::unmarshall,
                SpreadsheetParserInfo::marshall,
                SpreadsheetParserInfo.class
        );
    }
}

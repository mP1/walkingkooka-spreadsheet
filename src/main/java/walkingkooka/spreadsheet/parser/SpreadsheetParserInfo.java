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
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.plugin.PluginInfo;
import walkingkooka.plugin.PluginInfoLike;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

/**
 * Captures a unique {@link AbsoluteUrl} and {@link SpreadsheetParserName} for a {@link SpreadsheetParser}.
 */
public final class SpreadsheetParserInfo implements PluginInfoLike<SpreadsheetParserInfo, SpreadsheetParserName>,
    HateosResource<SpreadsheetParserName> {

    public static SpreadsheetParserInfo parse(final String text) {
        return new SpreadsheetParserInfo(
            PluginInfo.parse(
                text,
                SpreadsheetParserName::with
            )
        );
    }

    public static SpreadsheetParserInfo with(final AbsoluteUrl url,
                                             final SpreadsheetParserName name) {
        return new SpreadsheetParserInfo(
            PluginInfo.with(
                url,
                name
            )
        );
    }

    private SpreadsheetParserInfo(final PluginInfo<SpreadsheetParserName> pluginInfo) {
        this.pluginInfo = pluginInfo;
    }

    // HasAbsoluteUrl...................................................................................................

    @Override
    public AbsoluteUrl url() {
        return this.pluginInfo.url();
    }

    // HasName..........................................................................................................

    @Override
    public SpreadsheetParserName name() {
        return this.pluginInfo.name();
    }

    @Override
    public SpreadsheetParserInfo setName(final SpreadsheetParserName name) {
        return this.name().equals(name) ?
            this :
            new SpreadsheetParserInfo(
                this.pluginInfo.setName(name)
            );
    }

    private final PluginInfo<SpreadsheetParserName> pluginInfo;

    // Comparable.......................................................................................................

    @Override
    public int compareTo(final SpreadsheetParserInfo other) {
        return this.pluginInfo.compareTo(other.pluginInfo);
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.pluginInfo.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetParserInfo &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetParserInfo other) {
        return this.pluginInfo.equals(other.pluginInfo);
    }

    @Override
    public String toString() {
        return this.pluginInfo.toString();
    }

    // Json.............................................................................................................

    static void register() {
        // helps force registry of json marshaller
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(this.toString());
    }

    static SpreadsheetParserInfo unmarshall(final JsonNode node,
                                            final JsonNodeUnmarshallContext context) {
        return SpreadsheetParserInfo.parse(
            node.stringOrFail()
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetParserInfo.class),
            SpreadsheetParserInfo::unmarshall,
            SpreadsheetParserInfo::marshall,
            SpreadsheetParserInfo.class
        );
        SpreadsheetParserName.with("hello"); // trigger static init and json marshall/unmarshall registry
    }
}

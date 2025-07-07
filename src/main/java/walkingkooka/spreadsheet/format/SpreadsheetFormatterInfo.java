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
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.plugin.PluginInfo;
import walkingkooka.plugin.PluginInfoLike;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

/**
 * Captures a unique {@link AbsoluteUrl} and {@link SpreadsheetFormatterName} for a {@link SpreadsheetFormatter}.
 */
public final class SpreadsheetFormatterInfo implements PluginInfoLike<SpreadsheetFormatterInfo, SpreadsheetFormatterName>,
    HateosResource<SpreadsheetFormatterName> {

    public static SpreadsheetFormatterInfo parse(final String text) {
        return new SpreadsheetFormatterInfo(
            PluginInfo.parse(
                text,
                SpreadsheetFormatterName::with
            )
        );
    }

    public static SpreadsheetFormatterInfo with(final AbsoluteUrl url,
                                                final SpreadsheetFormatterName name) {
        return new SpreadsheetFormatterInfo(
            PluginInfo.with(
                url,
                name
            )
        );
    }

    private SpreadsheetFormatterInfo(final PluginInfo<SpreadsheetFormatterName> pluginInfo) {
        this.pluginInfo = pluginInfo;
    }

    // HasAbsoluteUrl...................................................................................................

    @Override
    public AbsoluteUrl url() {
        return this.pluginInfo.url();
    }

    // HasName..........................................................................................................

    @Override
    public SpreadsheetFormatterName name() {
        return this.pluginInfo.name();
    }

    @Override
    public SpreadsheetFormatterInfo setName(final SpreadsheetFormatterName name) {
        return this.name().equals(name) ?
            this :
            new SpreadsheetFormatterInfo(
                this.pluginInfo.setName(name)
            );
    }

    private final PluginInfo<SpreadsheetFormatterName> pluginInfo;

    // Comparable.......................................................................................................

    @Override
    public int compareTo(final SpreadsheetFormatterInfo other) {
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
            other instanceof SpreadsheetFormatterInfo &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetFormatterInfo other) {
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

    static SpreadsheetFormatterInfo unmarshall(final JsonNode node,
                                               final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormatterInfo.parse(
            node.stringOrFail()
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetFormatterInfo.class),
            SpreadsheetFormatterInfo::unmarshall,
            SpreadsheetFormatterInfo::marshall,
            SpreadsheetFormatterInfo.class
        );
        SpreadsheetFormatterName.with("hello"); // trigger static init and json marshall/unmarshall registry
    }
}
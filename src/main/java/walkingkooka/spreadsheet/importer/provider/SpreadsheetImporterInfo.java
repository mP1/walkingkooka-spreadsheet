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

package walkingkooka.spreadsheet.importer.provider;

import walkingkooka.Cast;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.plugin.PluginInfo;
import walkingkooka.plugin.PluginInfoLike;
import walkingkooka.spreadsheet.importer.SpreadsheetImporter;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

/**
 * Captures a unique {@link AbsoluteUrl} and {@link SpreadsheetImporterName} for a {@link SpreadsheetImporter}.
 */
public final class SpreadsheetImporterInfo implements PluginInfoLike<SpreadsheetImporterInfo, SpreadsheetImporterName>,
    HateosResource<SpreadsheetImporterName> {

    public static SpreadsheetImporterInfo parse(final String text) {
        return new SpreadsheetImporterInfo(
            PluginInfo.parse(
                text,
                SpreadsheetImporterName::with
            )
        );
    }

    public static SpreadsheetImporterInfo with(final AbsoluteUrl url,
                                               final SpreadsheetImporterName name) {
        return new SpreadsheetImporterInfo(
            PluginInfo.with(
                url,
                name
            )
        );
    }

    private SpreadsheetImporterInfo(final PluginInfo<SpreadsheetImporterName> pluginInfo) {
        this.pluginInfo = pluginInfo;
    }

    // HasAbsoluteUrl...................................................................................................

    @Override
    public AbsoluteUrl url() {
        return this.pluginInfo.url();
    }

    // HasName..........................................................................................................

    @Override
    public SpreadsheetImporterName name() {
        return this.pluginInfo.name();
    }

    @Override
    public SpreadsheetImporterInfo setName(final SpreadsheetImporterName name) {
        return this.name().equals(name) ?
            this :
            new SpreadsheetImporterInfo(
                this.pluginInfo.setName(name)
            );
    }

    private final PluginInfo<SpreadsheetImporterName> pluginInfo;

    // Comparable.......................................................................................................

    @Override
    public int compareTo(final SpreadsheetImporterInfo other) {
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
            other instanceof SpreadsheetImporterInfo &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetImporterInfo other) {
        return this.pluginInfo.equals(other.pluginInfo);
    }

    @Override
    public String toString() {
        return this.pluginInfo.toString();
    }

    // Json.............................................................................................................

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(this.toString());
    }

    static SpreadsheetImporterInfo unmarshall(final JsonNode node,
                                              final JsonNodeUnmarshallContext context) {
        return SpreadsheetImporterInfo.parse(
            node.stringOrFail()
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetImporterInfo.class),
            SpreadsheetImporterInfo::unmarshall,
            SpreadsheetImporterInfo::marshall,
            SpreadsheetImporterInfo.class
        );
    }
}

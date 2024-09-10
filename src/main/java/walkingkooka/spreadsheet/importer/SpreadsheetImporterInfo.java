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

import walkingkooka.Cast;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.plugin.PluginInfoLike;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;

/**
 * Provides a few bits of info describing a {@link SpreadsheetImporter}. The {@link AbsoluteUrl} must be a unique identifier,
 * with the {@link SpreadsheetImporterName} being a shorter human friendly reference.
 */
public final class SpreadsheetImporterInfo implements PluginInfoLike<SpreadsheetImporterInfo, SpreadsheetImporterName> {

    public static SpreadsheetImporterInfo parse(final String text) {
        return PluginInfoLike.parse(
                text,
                SpreadsheetImporterName::with,
                SpreadsheetImporterInfo::with
        );
    }

    public static SpreadsheetImporterInfo with(final AbsoluteUrl url,
                                               final SpreadsheetImporterName name) {
        return new SpreadsheetImporterInfo(
                Objects.requireNonNull(url, "url"),
                Objects.requireNonNull(name, "name")
        );
    }

    private SpreadsheetImporterInfo(final AbsoluteUrl url,
                                    final SpreadsheetImporterName name) {
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
    public SpreadsheetImporterName name() {
        return this.name;
    }

    private final SpreadsheetImporterName name;

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
                other instanceof SpreadsheetImporterInfo &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetImporterInfo other) {
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
        SpreadsheetImporterName.COLLECTION.value();
    }

    static SpreadsheetImporterInfo unmarshall(final JsonNode node,
                                              final JsonNodeUnmarshallContext context) {
        return PluginInfoLike.unmarshall(
                node,
                context,
                SpreadsheetImporterName::with,
                SpreadsheetImporterInfo::with
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

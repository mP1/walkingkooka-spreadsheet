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
 * Provides a few bits of info describing a {@link SpreadsheetCellImporter}. The {@link AbsoluteUrl} must be a unique identifier,
 * with the {@link SpreadsheetCellImporterName} being a shorter human friendly reference.
 */
public final class SpreadsheetCellImporterInfo implements PluginInfoLike<SpreadsheetCellImporterInfo, SpreadsheetCellImporterName> {

    public static SpreadsheetCellImporterInfo parse(final String text) {
        return PluginInfoLike.parse(
                text,
                SpreadsheetCellImporterName::with,
                SpreadsheetCellImporterInfo::with
        );
    }

    public static SpreadsheetCellImporterInfo with(final AbsoluteUrl url,
                                                   final SpreadsheetCellImporterName name) {
        return new SpreadsheetCellImporterInfo(
                Objects.requireNonNull(url, "url"),
                Objects.requireNonNull(name, "name")
        );
    }

    private SpreadsheetCellImporterInfo(final AbsoluteUrl url,
                                        final SpreadsheetCellImporterName name) {
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
    public SpreadsheetCellImporterName name() {
        return this.name;
    }

    private final SpreadsheetCellImporterName name;

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
                other instanceof SpreadsheetCellImporterInfo &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetCellImporterInfo other) {
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
    }

    static SpreadsheetCellImporterInfo unmarshall(final JsonNode node,
                                                  final JsonNodeUnmarshallContext context) {
        return PluginInfoLike.unmarshall(
                node,
                context,
                SpreadsheetCellImporterName.class,
                SpreadsheetCellImporterInfo::with
        );
    }

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetCellImporterInfo.class),
                SpreadsheetCellImporterInfo::unmarshall,
                SpreadsheetCellImporterInfo::marshall,
                SpreadsheetCellImporterInfo.class
        );
    }
}

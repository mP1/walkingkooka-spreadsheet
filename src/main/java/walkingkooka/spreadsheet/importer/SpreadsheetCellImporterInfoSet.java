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

import walkingkooka.collect.iterator.Iterators;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.plugin.PluginInfoSetLike;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * A read only {@link Set} of {@link SpreadsheetCellImporterInfo} sorted by {@link SpreadsheetCellImporterName}.
 */
public final class SpreadsheetCellImporterInfoSet extends AbstractSet<SpreadsheetCellImporterInfo>
        implements PluginInfoSetLike<SpreadsheetCellImporterInfo, SpreadsheetCellImporterName> {

    static {
        Sets.registerImmutableType(SpreadsheetCellImporterInfoSet.class);
    }

    /**
     * Parses the CSV text into a {@link SpreadsheetCellImporterInfoSet}.
     */
    public static SpreadsheetCellImporterInfoSet parse(final String text) {
        return PluginInfoSetLike.parse(
                text,
                SpreadsheetCellImporterInfo::parse,
                SpreadsheetCellImporterInfoSet::with
        );
    }

    /**
     * Factory that creates a {@link SpreadsheetCellImporterInfoSet} with the provided {@link SpreadsheetCellImporterInfo}.
     */
    public static SpreadsheetCellImporterInfoSet with(final Set<SpreadsheetCellImporterInfo> infos) {
        Objects.requireNonNull(infos, "infos");

        final Set<SpreadsheetCellImporterInfo> copy = Sets.sorted(HateosResource.comparator());
        copy.addAll(infos);
        return new SpreadsheetCellImporterInfoSet(copy);
    }

    private SpreadsheetCellImporterInfoSet(final Set<SpreadsheetCellImporterInfo> infos) {
        this.infos = infos;
    }

    // AbstractSet......................................................................................................

    @Override
    public Iterator<SpreadsheetCellImporterInfo> iterator() {
        return Iterators.readOnly(
                this.infos.iterator()
        );
    }

    @Override
    public int size() {
        return this.infos.size();
    }

    private final Set<SpreadsheetCellImporterInfo> infos;

    // json.............................................................................................................

    static {
        SpreadsheetCellImporterInfo.register(); // force registry of json marshaller

        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetCellImporterInfoSet.class),
                SpreadsheetCellImporterInfoSet::unmarshall,
                SpreadsheetCellImporterInfoSet::marshall,
                SpreadsheetCellImporterInfoSet.class
        );
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshallCollection(this);
    }

    // @VisibleForTesting
    static SpreadsheetCellImporterInfoSet unmarshall(final JsonNode node,
                                                     final JsonNodeUnmarshallContext context) {

        return with(
                context.unmarshallSet(
                        node,
                        SpreadsheetCellImporterInfo.class
                ));
    }
}

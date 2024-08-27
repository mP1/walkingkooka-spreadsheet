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
 * A read only {@link Set} of {@link SpreadsheetImporterInfo} sorted by {@link SpreadsheetImporterName}.
 */
public final class SpreadsheetImporterInfoSet extends AbstractSet<SpreadsheetImporterInfo>
        implements PluginInfoSetLike<SpreadsheetImporterInfo, SpreadsheetImporterName> {

    static {
        Sets.registerImmutableType(SpreadsheetImporterInfoSet.class);
    }

    /**
     * Parses the CSV text into a {@link SpreadsheetImporterInfoSet}.
     */
    public static SpreadsheetImporterInfoSet parse(final String text) {
        return PluginInfoSetLike.parse(
                text,
                SpreadsheetImporterInfo::parse,
                SpreadsheetImporterInfoSet::with
        );
    }

    /**
     * Factory that creates a {@link SpreadsheetImporterInfoSet} with the provided {@link SpreadsheetImporterInfo}.
     */
    public static SpreadsheetImporterInfoSet with(final Set<SpreadsheetImporterInfo> infos) {
        Objects.requireNonNull(infos, "infos");

        final Set<SpreadsheetImporterInfo> copy = Sets.sorted(HateosResource.comparator());
        copy.addAll(infos);
        return new SpreadsheetImporterInfoSet(copy);
    }

    private SpreadsheetImporterInfoSet(final Set<SpreadsheetImporterInfo> infos) {
        this.infos = infos;
    }

    // AbstractSet......................................................................................................

    @Override
    public Iterator<SpreadsheetImporterInfo> iterator() {
        return Iterators.readOnly(
                this.infos.iterator()
        );
    }

    @Override
    public int size() {
        return this.infos.size();
    }

    private final Set<SpreadsheetImporterInfo> infos;

    // json.............................................................................................................

    static {
        SpreadsheetImporterInfo.register(); // force registry of json marshaller

        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetImporterInfoSet.class),
                SpreadsheetImporterInfoSet::unmarshall,
                SpreadsheetImporterInfoSet::marshall,
                SpreadsheetImporterInfoSet.class
        );
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshallCollection(this);
    }

    // @VisibleForTesting
    static SpreadsheetImporterInfoSet unmarshall(final JsonNode node,
                                                 final JsonNodeUnmarshallContext context) {

        return with(
                context.unmarshallSet(
                        node,
                        SpreadsheetImporterInfo.class
                ));
    }
}

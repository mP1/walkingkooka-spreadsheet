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

package walkingkooka.spreadsheet.export;

import walkingkooka.collect.iterator.Iterators;
import walkingkooka.collect.set.ImmutableSetDefaults;
import walkingkooka.collect.set.SortedSets;
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
import java.util.TreeSet;

/**
 * A read only {@link Set} of {@link SpreadsheetExporterInfo} sorted by {@link SpreadsheetExporterName}.
 */
public final class SpreadsheetExporterInfoSet extends AbstractSet<SpreadsheetExporterInfo>
        implements PluginInfoSetLike<SpreadsheetExporterInfo, SpreadsheetExporterName>,
        ImmutableSetDefaults<SpreadsheetExporterInfoSet, SpreadsheetExporterInfo> {

    /**
     * Parses the CSV text into a {@link SpreadsheetExporterInfoSet}.
     */
    public static SpreadsheetExporterInfoSet parse(final String text) {
        return PluginInfoSetLike.parse(
                text,
                SpreadsheetExporterInfo::parse,
                SpreadsheetExporterInfoSet::with
        );
    }

    /**
     * Factory that creates a {@link SpreadsheetExporterInfoSet} with the provided {@link SpreadsheetExporterInfo}.
     */
    public static SpreadsheetExporterInfoSet with(final Set<SpreadsheetExporterInfo> infos) {
        Objects.requireNonNull(infos, "infos");

        final Set<SpreadsheetExporterInfo> copy = SortedSets.tree(HateosResource.comparator());
        copy.addAll(infos);
        return new SpreadsheetExporterInfoSet(copy);
    }

    private SpreadsheetExporterInfoSet(final Set<SpreadsheetExporterInfo> infos) {
        this.infos = infos;
    }

    // AbstractSet......................................................................................................

    @Override
    public Iterator<SpreadsheetExporterInfo> iterator() {
        return Iterators.readOnly(
                this.infos.iterator()
        );
    }

    @Override
    public int size() {
        return this.infos.size();
    }

    private final Set<SpreadsheetExporterInfo> infos;

    // ImmutableSet.....................................................................................................

    @Override
    public SpreadsheetExporterInfoSet setElements(final Set<SpreadsheetExporterInfo> elements) {
        final SpreadsheetExporterInfoSet copy = with(elements);
        return this.equals(copy) ?
                this :
                copy;
    }

    @Override
    public Set<SpreadsheetExporterInfo> toSet() {
        return new TreeSet<>(
                this.infos
        );
    }

    // json.............................................................................................................

    static {
        SpreadsheetExporterInfo.register(); // force registry of json marshaller

        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetExporterInfoSet.class),
                SpreadsheetExporterInfoSet::unmarshall,
                SpreadsheetExporterInfoSet::marshall,
                SpreadsheetExporterInfoSet.class
        );
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshallCollection(this);
    }

    // @VisibleForTesting
    static SpreadsheetExporterInfoSet unmarshall(final JsonNode node,
                                                 final JsonNodeUnmarshallContext context) {

        return with(
                context.unmarshallSet(
                        node,
                        SpreadsheetExporterInfo.class
                ));
    }
}

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

package walkingkooka.spreadsheet.compare;

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
 * A read only {@link Set} of {@link SpreadsheetComparatorInfo} sorted by {@link SpreadsheetComparatorName}.
 */
public final class SpreadsheetComparatorInfoSet extends AbstractSet<SpreadsheetComparatorInfo> implements PluginInfoSetLike<SpreadsheetComparatorInfo, SpreadsheetComparatorName> {

    /**
     * Parses the CSV text into a {@link SpreadsheetComparatorInfoSet}.
     */
    public static SpreadsheetComparatorInfoSet parse(final String text) {
        return PluginInfoSetLike.parse(
                text,
                SpreadsheetComparatorInfo::parse,
                SpreadsheetComparatorInfoSet::with
        );
    }

    /**
     * Factory that creates a {@link SpreadsheetComparatorInfoSet} with the provided {@link SpreadsheetComparatorInfo}.
     */
    public static SpreadsheetComparatorInfoSet with(final Set<SpreadsheetComparatorInfo> infos) {
        Objects.requireNonNull(infos, "infos");

        final Set<SpreadsheetComparatorInfo> copy = Sets.sorted(HateosResource.comparator());
        copy.addAll(infos);
        return new SpreadsheetComparatorInfoSet(copy);
    }

    private SpreadsheetComparatorInfoSet(final Set<SpreadsheetComparatorInfo> infos) {
        this.infos = infos;
    }

    // AbstractSet......................................................................................................

    @Override
    public Iterator<SpreadsheetComparatorInfo> iterator() {
        return Iterators.readOnly(
                this.infos.iterator()
        );
    }

    @Override
    public int size() {
        return this.infos.size();
    }

    private final Set<SpreadsheetComparatorInfo> infos;

    // json.............................................................................................................

    static {
        SpreadsheetComparatorInfo.register(); // for registry of json marshaller

        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetComparatorInfoSet.class),
                SpreadsheetComparatorInfoSet::unmarshall,
                SpreadsheetComparatorInfoSet::marshall,
                SpreadsheetComparatorInfoSet.class
        );
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshallCollection(this);
    }

    // @VisibleForTesting
    static SpreadsheetComparatorInfoSet unmarshall(final JsonNode node,
                                                   final JsonNodeUnmarshallContext context) {
        return with(
                context.unmarshallSet(
                        node,
                        SpreadsheetComparatorInfo.class
                )
        );
    }

    // toString.........................................................................................................

    @Override
    public String toString() {
        return PluginInfoSetLike.toString(this);
    }
}

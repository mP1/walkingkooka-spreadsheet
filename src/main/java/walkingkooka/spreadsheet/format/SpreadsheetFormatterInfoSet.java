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
 * A read only {@link Set} of {@link SpreadsheetFormatterInfo} sorted by {@link SpreadsheetFormatterName}.
 */
public final class SpreadsheetFormatterInfoSet extends AbstractSet<SpreadsheetFormatterInfo>
        implements PluginInfoSetLike<SpreadsheetFormatterInfo, SpreadsheetFormatterName> {

    /**
     * Parses the CSV text into a {@link SpreadsheetFormatterInfoSet}.
     */
    public static SpreadsheetFormatterInfoSet parse(final String text) {
        return PluginInfoSetLike.parse(
                text,
                SpreadsheetFormatterInfo::parse,
                SpreadsheetFormatterInfoSet::with
        );
    }
    
    /**
     * Factory that creates a {@link SpreadsheetFormatterInfoSet} with the provided {@link SpreadsheetFormatterInfo}.
     */
    public static SpreadsheetFormatterInfoSet with(final Set<SpreadsheetFormatterInfo> infos) {
        Objects.requireNonNull(infos, "infos");

        final Set<SpreadsheetFormatterInfo> copy = Sets.sorted(HateosResource.comparator());
        copy.addAll(infos);
        return new SpreadsheetFormatterInfoSet(copy);
    }

    private SpreadsheetFormatterInfoSet(final Set<SpreadsheetFormatterInfo> infos) {
        this.infos = infos;
    }

    // AbstractSet......................................................................................................

    @Override
    public Iterator<SpreadsheetFormatterInfo> iterator() {
        return Iterators.readOnly(
                this.infos.iterator()
        );
    }

    @Override
    public int size() {
        return this.infos.size();
    }

    private final Set<SpreadsheetFormatterInfo> infos;

    // json.............................................................................................................

    static {
        SpreadsheetFormatterInfo.register(); // for registry of json marshaller

        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetFormatterInfoSet.class),
                SpreadsheetFormatterInfoSet::unmarshall,
                SpreadsheetFormatterInfoSet::marshall,
                SpreadsheetFormatterInfoSet.class
        );
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshallCollection(this);
    }

    // @VisibleForTesting
    static SpreadsheetFormatterInfoSet unmarshall(final JsonNode node,
                                                  final JsonNodeUnmarshallContext context) {

        return with(
                context.unmarshallSet(
                        node,
                        SpreadsheetFormatterInfo.class
                ));
    }

    // toString.........................................................................................................

    @Override
    public String toString() {
        return PluginInfoSetLike.toString(this);
    }
}

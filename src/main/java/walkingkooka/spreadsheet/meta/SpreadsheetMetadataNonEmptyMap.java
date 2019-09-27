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

package walkingkooka.spreadsheet.meta;

import walkingkooka.collect.map.Maps;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A read only sorted view of metadata entries.
 */
final class SpreadsheetMetadataNonEmptyMap extends AbstractMap<SpreadsheetMetadataPropertyName<?>, Object> {

    static {
        Maps.registerImmutableType(SpreadsheetMetadataNonEmptyMap.class);
    }

    /**
     * An empty {@link SpreadsheetMetadataNonEmptyMap}.
     */
    static final SpreadsheetMetadataNonEmptyMap EMPTY = new SpreadsheetMetadataNonEmptyMap(SpreadsheetMetadataNonEmptyMapEntrySet.EMPTY);

    /**
     * Factory that takes a copy if the given {@link Map} is not a {@link SpreadsheetMetadataNonEmptyMap}.
     */
    static SpreadsheetMetadataNonEmptyMap with(final Map<SpreadsheetMetadataPropertyName<?>, Object> map) {
        Objects.requireNonNull(map, "map");

        return map instanceof SpreadsheetMetadataNonEmptyMap ?
                (SpreadsheetMetadataNonEmptyMap) map :
                with0(map);
    }

    private static SpreadsheetMetadataNonEmptyMap with0(final Map<SpreadsheetMetadataPropertyName<?>, Object> map) {
        return with1(SpreadsheetMetadataNonEmptyMapEntrySet.with(map));
    }

    private static SpreadsheetMetadataNonEmptyMap with1(final SpreadsheetMetadataNonEmptyMapEntrySet entrySet) {
        return entrySet.isEmpty() ?
                EMPTY :
                withSpreadsheetMetadataMapEntrySet(entrySet);
    }

    static SpreadsheetMetadataNonEmptyMap withSpreadsheetMetadataMapEntrySet(final SpreadsheetMetadataNonEmptyMapEntrySet entrySet) {
        return new SpreadsheetMetadataNonEmptyMap(entrySet);
    }

    private SpreadsheetMetadataNonEmptyMap(final SpreadsheetMetadataNonEmptyMapEntrySet entries) {
        super();
        this.entries = entries;
    }

    @Override
    public Set<Entry<SpreadsheetMetadataPropertyName<?>, Object>> entrySet() {
        return this.entries;
    }

    final SpreadsheetMetadataNonEmptyMapEntrySet entries;

    // SpreadsheetMetadataVisitor.......................................................................................

    void accept(final SpreadsheetMetadataVisitor visitor) {
        this.entries.accept(visitor);
    }

    // JsonNodeContext..................................................................................................

    static SpreadsheetMetadataNonEmptyMap unmarshall(final JsonNode json,
                                                     final JsonNodeUnmarshallContext context) {
        return SpreadsheetMetadataNonEmptyMap.with1(SpreadsheetMetadataNonEmptyMapEntrySet.fromJson(json, context));
    }

    JsonNode marshall(final JsonNodeMarshallContext context) {
        return this.entries.toJson(context);
    }
}

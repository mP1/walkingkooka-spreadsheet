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

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A read only sorted view of metadata entries.
 */
final class SpreadsheetMetadataMap extends AbstractMap<SpreadsheetMetadataPropertyName<?>, Object> {

    static {
        Maps.registerImmutableType(SpreadsheetMetadataMap.class);
    }

    /**
     * An empty {@link SpreadsheetMetadataMap}.
     */
    static final SpreadsheetMetadataMap EMPTY = new SpreadsheetMetadataMap(SpreadsheetMetadataMapEntrySet.EMPTY);

    /**
     * Factory that takes a copy if the given {@link Map} is not a {@link SpreadsheetMetadataMap}.
     */
    static SpreadsheetMetadataMap with(final Map<SpreadsheetMetadataPropertyName<?>, Object> map) {
        Objects.requireNonNull(map, "map");

        return map instanceof SpreadsheetMetadataMap ?
                (SpreadsheetMetadataMap) map :
                with0(map);
    }

    private static SpreadsheetMetadataMap with0(final Map<SpreadsheetMetadataPropertyName<?>, Object> map) {
        return with1(SpreadsheetMetadataMapEntrySet.with(map));
    }

    private static SpreadsheetMetadataMap with1(final SpreadsheetMetadataMapEntrySet entrySet) {
        return entrySet.isEmpty() ?
                EMPTY :
                withSpreadsheetMetadataMapEntrySet(entrySet);
    }

    static SpreadsheetMetadataMap withSpreadsheetMetadataMapEntrySet(final SpreadsheetMetadataMapEntrySet entrySet) {
        return new SpreadsheetMetadataMap(entrySet);
    }

    private SpreadsheetMetadataMap(final SpreadsheetMetadataMapEntrySet entries) {
        super();
        this.entries = entries;
    }

    @Override
    public Set<Entry<SpreadsheetMetadataPropertyName<?>, Object>> entrySet() {
        return this.entries;
    }

    final SpreadsheetMetadataMapEntrySet entries;

    // HasJsonNode......................................................................................................

    static SpreadsheetMetadataMap fromJson(final JsonNode json) {
        return SpreadsheetMetadataMap.with1(SpreadsheetMetadataMapEntrySet.fromJson(json));
    }

    JsonNode toJson() {
        return this.entries.toJson();
    }
}

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

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.tree.json.JsonNode;

import java.util.Map;
import java.util.Optional;

/**
 * A {@link SpreadsheetMetadata} with no properties and values.
 */
final class EmptySpreadsheetMetadata extends SpreadsheetMetadata {

    /**
     * Lazy singleton necessary to avoid race conditions to a init'd static field
     */
    static EmptySpreadsheetMetadata instance() {
        if (null == instance) {
            instance = new EmptySpreadsheetMetadata();
        }
        return instance;
    }

    private static EmptySpreadsheetMetadata instance;

    /**
     * Private ctor
     */
    private EmptySpreadsheetMetadata() {
        super();
    }

    /**
     * Always returns true
     */
    @Override
    public boolean isEmpty() {
        return true;
    }

    // Value............................................................................................................

    @Override
    public Map<SpreadsheetMetadataPropertyName<?>, Object> value() {
        return Maps.empty();
    }

    // get/set/remove...................................................................................................

    @Override
    <V> Optional<V> get0(final SpreadsheetMetadataPropertyName<V> propertyName) {
        return Optional.empty();
    }

    @Override
    <V> SpreadsheetMetadata set0(final SpreadsheetMetadataPropertyName<V> propertyName, final V value) {
        return NonEmptySpreadsheetMetadata.with(SpreadsheetMetadataMap.withSpreadsheetMetadataMapEntrySet(SpreadsheetMetadataMapEntrySet.withList(Lists.of(Maps.entry(propertyName, value)))));
    }

    @Override
    SpreadsheetMetadata remove0(final SpreadsheetMetadataPropertyName<?> propertyName) {
        return this;
    }

    // Object..........................................................................................................

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    final boolean canBeEquals(final Object other) {
        return other instanceof EmptySpreadsheetMetadata;
    }

    @Override
    boolean equals0(final SpreadsheetMetadata other) {
        return true; // singleton
    }

    @Override
    public String toString() {
        return "";
    }

    // HasJsonNode......................................................................................................

    @Override
    public JsonNode toJsonNode() {
        return JsonNode.object();
    }
}

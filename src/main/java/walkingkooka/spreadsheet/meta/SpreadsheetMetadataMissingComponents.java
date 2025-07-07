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

import walkingkooka.collect.set.SortedSets;

import java.util.Set;

/**
 * Used to aggregate that all the required properties are present, tracking those that are missing.
 * This is used only by {@link SpreadsheetMetadata} methods such as getting a {@link walkingkooka.convert.Converter}.
 */
final class SpreadsheetMetadataMissingComponents {

    static SpreadsheetMetadataMissingComponents with(final SpreadsheetMetadata metadata) {
        return new SpreadsheetMetadataMissingComponents(metadata);
    }

    private SpreadsheetMetadataMissingComponents(final SpreadsheetMetadata metadata) {
        super();
        this.metadata = metadata;
    }

    <T> T getOrNull(final SpreadsheetMetadataPropertyName<T> propertyName) {
        return this.metadata.getOrGetDefaults(propertyName)
            .orElseGet(() -> this.addMissing(propertyName));
    }

    final SpreadsheetMetadata metadata;

    private <T> T addMissing(final SpreadsheetMetadataPropertyName<?> propertyName) {
        this.missing.add(propertyName);
        return null;
    }

    void reportIfMissing() {
        final Set<SpreadsheetMetadataPropertyName<?>> missing = this.missing;
        if (false == missing.isEmpty()) {
            throw new MissingMetadataPropertiesException(missing);
        }
    }

    void addMissing(final MissingMetadataPropertiesException missing) {
        this.missing.addAll(missing.missing);
    }

    final Set<SpreadsheetMetadataPropertyName<?>> missing = SortedSets.tree();

    @Override
    public String toString() {
        return this.missing.toString();
    }
}

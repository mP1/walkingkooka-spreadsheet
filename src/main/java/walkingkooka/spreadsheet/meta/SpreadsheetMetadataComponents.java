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
import walkingkooka.text.CharSequences;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

final class SpreadsheetMetadataComponents {

    static SpreadsheetMetadataComponents with(final SpreadsheetMetadata metadata) {
        return new SpreadsheetMetadataComponents(metadata);
    }

    private SpreadsheetMetadataComponents(final SpreadsheetMetadata metadata) {
        super();
        this.metadata = metadata;
    }

    <T> T getOrNull(final SpreadsheetMetadataPropertyName<T> propertyName) {
        return this.getOrElse(propertyName, this::defaultNull);
    }

    private <T> T defaultNull() {
        return null;
    }

    <T> T getOrElse(final SpreadsheetMetadataPropertyName<T> propertyName,
                    final Supplier<T> defaultValue) {
        return this.metadata.get0(propertyName)
                .orElseGet(() -> {
                    final T value = defaultValue.get();
                    if (null == value) {
                        this.addMissing(propertyName);
                    }
                    return value;
                });
    }

    final SpreadsheetMetadata metadata;

    private <T> T addMissing(final SpreadsheetMetadataPropertyName<T> propertyName) {
        this.missing.add(propertyName);
        return null;
    }

    void reportIfMissing() {
        final List<SpreadsheetMetadataPropertyName<?>> missing = this.missing;
        if (!missing.isEmpty()) {
            throw new IllegalStateException(missing.stream()
                    .map(Object::toString)
                    .map(CharSequences::quoteAndEscape)
                    .collect(Collectors.joining(", ", "Required properties ", " missing.")));
        }
    }

    final List<SpreadsheetMetadataPropertyName<?>> missing = Lists.array();

    @Override
    public String toString() {
        return this.missing.toString();
    }
}

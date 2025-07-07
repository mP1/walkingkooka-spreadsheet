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

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * A {@link Function} view of a {@link Map} wrapping get results in an {@link Optional}.
 */
final class SpreadsheetMetadataColorFunction<K, V> implements Function<K, Optional<V>> {

    static <K, V> SpreadsheetMetadataColorFunction<K, V> with(final Map<K, V> values) {
        return new SpreadsheetMetadataColorFunction<>(values);
    }

    private SpreadsheetMetadataColorFunction(final Map<K, V> values) {
        super();
        this.values = values;
    }

    @Override
    public Optional<V> apply(final K key) {
        return Optional.ofNullable(
            this.values.get(key)
        );
    }

    private final Map<K, V> values;

    @Override
    public String toString() {
        return this.values.toString();
    }
}

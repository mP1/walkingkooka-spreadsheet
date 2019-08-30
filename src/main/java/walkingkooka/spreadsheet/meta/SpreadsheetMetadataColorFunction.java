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

import walkingkooka.color.Color;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A {@link Function} view of a {@link Map} of {@link Color}
 */
final class SpreadsheetMetadataColorFunction<K> implements Function<K, Optional<Color>> {

    static  <K> SpreadsheetMetadataColorFunction<K> with(final Map<K, Color> colors) {
        return new SpreadsheetMetadataColorFunction<>(colors);
    }

    private SpreadsheetMetadataColorFunction(final Map<K, Color> colors) {
        super();
        this.colors = colors;
    }

    @Override
    public Optional<Color> apply(final K key) {
        return Optional.ofNullable(this.colors.get(key));
    }

    private final Map<K, Color> colors;

    @Override
    public String toString() {
        return this.colors.toString();
    }
}

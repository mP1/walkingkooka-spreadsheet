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

package walkingkooka.spreadsheet.hateos;

import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.store.SpreadsheetStore;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A base {@link HateosHandler} that only implements {@link #handle(Comparable, Optional, Map)}.
 */
abstract class SpreadsheetStoreHateosHandler2<K extends Comparable<K>, V extends HateosResource<K>> extends SpreadsheetStoreHateosHandler<K, V>
        implements HateosHandler<K, V, V> {

    SpreadsheetStoreHateosHandler2(final SpreadsheetStore<K, V> store) {
        super(store);
    }

    @Override
    public final Optional<V> handleCollection(final Range<K> ids,
                                              final Optional<V> value,
                                              final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(ids, "ids");
        checkResource(value);
        checkParameters(parameters);

        throw new UnsupportedOperationException();
    }
}

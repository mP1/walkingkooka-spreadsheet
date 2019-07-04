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

import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.store.SpreadsheetStore;

import java.util.Map;
import java.util.Optional;

/**
 * A {@link HateosHandler} that loads a {@link HateosResource value} given an id.
 */
final class SpreadsheetStoreLoadHateosHandler<K extends Comparable<K>, V extends HateosResource<K>> extends SpreadsheetStoreHateosHandler2<K, V> {

    static <K extends Comparable<K>, V extends HateosResource<K>> SpreadsheetStoreLoadHateosHandler<K, V> with(final SpreadsheetStore<K, V> store) {
        checkStore(store);

        return new SpreadsheetStoreLoadHateosHandler(store);
    }

    private SpreadsheetStoreLoadHateosHandler(final SpreadsheetStore<K, V> store) {
        super(store);
    }

    @Override
    public Optional<V> handle(final K id,
                              final Optional<V> value,
                              final Map<HttpRequestAttribute<?>, Object> parameters) {
        checkId(id);
        this.checkResourceEmpty(value);
        this.checkParameters(parameters);

        return this.store.load(id);
    }

    @Override
    String operation() {
        return "load";
    }
}

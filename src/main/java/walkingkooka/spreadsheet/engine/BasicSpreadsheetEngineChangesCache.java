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

package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Map;
import java.util.Objects;

/**
 * A cache that contains the parameters such as the reference and value of an operation. A status is also kept to track whether the operation has been performed.
 */
final class BasicSpreadsheetEngineChangesCache<S extends SpreadsheetSelection, V> {

    static <S extends SpreadsheetSelection, V> BasicSpreadsheetEngineChangesCache<S, V> getOrCreate(
        final S reference,
        final Map<S, BasicSpreadsheetEngineChangesCache<S, V>> referenceToValue,
        final BasicSpreadsheetEngineChangesCacheStatus<S> initialStatus) {
        Objects.requireNonNull(reference, "reference");
        Objects.requireNonNull(referenceToValue, "referenceToValue");
        Objects.requireNonNull(initialStatus, "initialStatus");

        BasicSpreadsheetEngineChangesCache<S, V> cache = referenceToValue.get(reference);
        if (null == cache) {
            cache = new BasicSpreadsheetEngineChangesCache<>(
                reference,
                initialStatus
            );

            referenceToValue.put(
                reference,
                cache
            );
        }

        return cache;
    }

    private BasicSpreadsheetEngineChangesCache(final S reference,
                                               final BasicSpreadsheetEngineChangesCacheStatus<S> initialStatus) {
        this.reference = reference;
        this.status = initialStatus;
    }

    /**
     * The {@link walkingkooka.spreadsheet.reference.SpreadsheetCellReference} or {@link walkingkooka.spreadsheet.reference.SpreadsheetLabelName}.
     */
    final S reference;

    /**
     * Updates a cache with an initially loaded value which will never be null.
     */
    BasicSpreadsheetEngineChangesCache<S, V> loadingOrMissing(final V value) {
        return null != value ?
            this.loading(value) :
            this.deleted();
    }

    BasicSpreadsheetEngineChangesCache<S, V> loading(final V value) {
        final BasicSpreadsheetEngineChangesCacheStatus<S> oldStatus = this.status;
        final BasicSpreadsheetEngineChangesCacheStatus<S> newStatus = null != value ?
            oldStatus.loading() :
            oldStatus.deleted();

        // if status changed

        if (oldStatus != newStatus) {
            this.setStatus(newStatus);
        }
        this.value = value;

        return this;
    }

    BasicSpreadsheetEngineChangesCache<S, V> loadedOrMissing(final V value) {
        return null != value ?
            this.loaded(value) :
            this.deleted();
    }

    BasicSpreadsheetEngineChangesCache<S, V> loaded(final V value) {
        Objects.requireNonNull(value, "value");

        final BasicSpreadsheetEngineChangesCacheStatus<S> oldStatus = this.status;

        // unloaded -> loaded = loaded
        // loaded -> different SpreadsheetCell -> saved
        // loaded -> same SpreadsheetCell -> loaded
        final BasicSpreadsheetEngineChangesCacheStatus<S> newStatus = oldStatus.isUnloaded() || Objects.equals(this.value, value) ?
            oldStatus.loaded() :
            oldStatus.saved();

        // if status changed

        if (oldStatus != newStatus) {
            this.setStatus(newStatus);
            this.value = value;
        }

        return this;
    }

    BasicSpreadsheetEngineChangesCache<S, V> saving(final V value) {
        Objects.requireNonNull(value, "value");

        this.value = value;

        return this;
    }

    BasicSpreadsheetEngineChangesCache<S, V> saved(final V value) {
        final BasicSpreadsheetEngineChangesCacheStatus<S> oldStatus = this.status;
        final BasicSpreadsheetEngineChangesCacheStatus<S> newStatus = oldStatus.saved();

        // if status changed
        if (oldStatus != newStatus || false == Objects.equals(this.value, value)) {
            this.setStatus(newStatus);
            this.value = value;
        }

        return this;
    }

    BasicSpreadsheetEngineChangesCache<S, V> deleted() {
        return this.setStatus(
            this.status.deleted()
        );
    }

    BasicSpreadsheetEngineChangesCache<S, V> forceReferencesRefresh() {
        return this.setStatus(
            this.status.forceReferencesRefresh()
        );
    }

    BasicSpreadsheetEngineChangesCache<S, V> referencesRefreshed() {
        return this.setStatus(
            this.status.referencesRefreshed()
        );
    }

    V valueOrNull() {
        return this.value;
    }

    /**
     * Getter that returns the loaded or saved value.
     */
    V value() {
        return this.status.value(
            this.reference,
            this.value
        );
    }

    private V value;

    // status..........................................................................................................

    BasicSpreadsheetEngineChangesCache<S, V> setStatus(final BasicSpreadsheetEngineChangesCacheStatus<S> status) {
        Objects.requireNonNull(status, "status");

        BasicSpreadsheetEngineChangesCacheStatus<S> previous = this.status;

        if (previous != status) {

            // new status of REFERENCE shouldnt replace non REFERENCE status
            final BasicSpreadsheetEngineChangesCacheStatus<S> save;
            if (previous.isReference()) {
                save = status;
            } else {
                save = status.toNonReference();
            }

            if (previous != save) {
                this.status = save;
            }
        }

        return this;
    }

    BasicSpreadsheetEngineChangesCacheStatus<S> status() {
        return this.status;
    }

    private BasicSpreadsheetEngineChangesCacheStatus<S> status;

    // Object...........................................................................................................

    @Override
    public String toString() {
        final S reference = this.reference;
        final V value = this.value;

        final StringBuilder b = new StringBuilder();
        if (null != value) {
            b.append(value);
        } else {
            b.append(reference)
                .append(" value=null");
        }

        b.append(" status=")
            .append(this.status);
        return b.toString();
    }
}

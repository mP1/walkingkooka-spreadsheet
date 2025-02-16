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

import walkingkooka.ToStringBuilder;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Map;
import java.util.Objects;

/**
 * A cache that contains the parameters such as the reference and value of an operation. A status is also kept to track whether the operation has been performed.
 */
final class BasicSpreadsheetEngineChangesCache<S extends SpreadsheetSelection, V> {

    static <S extends SpreadsheetSelection, V> BasicSpreadsheetEngineChangesCache<S, V> getOrCreate(
            final S reference,
            final Map<S, BasicSpreadsheetEngineChangesCache<S, V>> referenceToValue) {
        Objects.requireNonNull(reference, "reference");


        BasicSpreadsheetEngineChangesCache<S, V> cache = referenceToValue.get(reference);
        if (null == cache) {
            cache = new BasicSpreadsheetEngineChangesCache<>(reference);

            referenceToValue.put(
                    reference,
                    cache
            );
        }

        return cache;
    }

    private BasicSpreadsheetEngineChangesCache(final S reference) {
        this.reference = reference;
    }

    /**
     * The {@link walkingkooka.spreadsheet.reference.SpreadsheetCellReference} or {@link walkingkooka.spreadsheet.reference.SpreadsheetLabelName}.
     */
    final S reference;

    BasicSpreadsheetEngineChangesCache<S, V> load() {
        return this.status == BasicSpreadsheetEngineChangesCacheStatus.SAVE ?
                this :
                this.setStatus(BasicSpreadsheetEngineChangesCacheStatus.LOAD);
    }

    BasicSpreadsheetEngineChangesCache<S, V> load(final V value) {
        Objects.requireNonNull(value, "value");

        this.value = value;

        return this.status == BasicSpreadsheetEngineChangesCacheStatus.SAVE ?
                this :
                this.setStatus(BasicSpreadsheetEngineChangesCacheStatus.LOAD);
    }

    BasicSpreadsheetEngineChangesCache<S, V> loadCellReference(final V value) {
        Objects.requireNonNull(value, "value");

        this.value = value;
        return this.status == BasicSpreadsheetEngineChangesCacheStatus.SAVE || this.status == BasicSpreadsheetEngineChangesCacheStatus.LOAD ?
                this : // unchanged
                this.setStatus(BasicSpreadsheetEngineChangesCacheStatus.LOAD_REFERENCE);
    }

    BasicSpreadsheetEngineChangesCache<S, V> save() {
        return this.setStatus(BasicSpreadsheetEngineChangesCacheStatus.SAVE);
    }

    BasicSpreadsheetEngineChangesCache<S, V> save(final V value) {
        Objects.requireNonNull(value, "value");

        if (false == Objects.equals(this.value, value)) {
            this.committed = false;
        }
        this.value = value;
        return this.setStatus(BasicSpreadsheetEngineChangesCacheStatus.SAVE);
    }

    BasicSpreadsheetEngineChangesCache<S, V> delete() {
        this.value = null;
        return this.setStatus(BasicSpreadsheetEngineChangesCacheStatus.DELETE);
    }

    BasicSpreadsheetEngineChangesCache<S, V> missing() {
        this.value = null;
        return this.setStatus(BasicSpreadsheetEngineChangesCacheStatus.MISSING);
    }

    BasicSpreadsheetEngineChangesCache<S, V> setStatus(final BasicSpreadsheetEngineChangesCacheStatus status) {
        Objects.requireNonNull(status, "operation");
        this.status = status;

        return this;
    }

    boolean isLoad() {
        return this.status == BasicSpreadsheetEngineChangesCacheStatus.LOAD;
    }

    boolean isloadReference() {
        return this.status == BasicSpreadsheetEngineChangesCacheStatus.LOAD_REFERENCE;
    }

    boolean isSave() {
        return this.status == BasicSpreadsheetEngineChangesCacheStatus.SAVE;
    }

    boolean isLoadOrSave() {
        return this.isLoad() || this.isSave();
    }

    boolean isDelete() {
        return this.status == BasicSpreadsheetEngineChangesCacheStatus.DELETE;
    }

    boolean isMissing() {
        return this.status == BasicSpreadsheetEngineChangesCacheStatus.MISSING;
    }

    boolean isDeleteOrMissing() {
        return this.isDelete() || this.isMissing();
    }

    private BasicSpreadsheetEngineChangesCacheStatus status;

    /**
     * Getter that returns the loaded or saved value.
     */
    V value() {
        this.status.value();

        return this.value;
    }

    private V value;

    /**
     * Setter supporting fluent setter.
     */
    BasicSpreadsheetEngineChangesCache<S, V> setCommitted(final boolean committed) {
        this.committed = committed;
        return this;
    }

    /**
     * Indicates an unsaved or undeleted entity.
     */
    boolean committed = false;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .value(this.reference)
                .labelSeparator("=")
                .value(this.value)
                .label("status")
                .value(this.status)
                .label("committed")
                .value(this.committed)
                .build();
    }
}

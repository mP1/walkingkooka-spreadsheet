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
 * A cache of an SAVE/UPDATE or DELETE operation upon a reference and value. A save flag is used rather than testing the
 * value for null to avoid mistakes like a SAVE without the actual value.
 */
final class BasicSpreadsheetEngineChangesCache<S extends SpreadsheetSelection, V> {

    static <S extends SpreadsheetSelection, V> BasicSpreadsheetEngineChangesCache<S, V> getOrCreate(
            final S reference,
            final V value,
            final Map<S, BasicSpreadsheetEngineChangesCache<S, V>> referenceToValue) {
        Objects.requireNonNull(reference, "reference");

        BasicSpreadsheetEngineChangesCache<S, V> cache = referenceToValue.get(reference);
        if (null == cache) {
            cache = new BasicSpreadsheetEngineChangesCache<>(
                    reference,
                    value
            );
            referenceToValue.put(
                    reference,
                    cache
            );
        } else {
            // if extra save with the DIFFERENT cell clear $committed
            if(null != value) {
                //cache.committed = false == value.equals(cache.value);

                // clear committed if new save value happens
                if(false == value.equals(cache.value)) {
                    cache.committed = false;
                }
            }
        }

        if(null != value) {
            cache.save = true; // must be save when value provided.
        }

        return cache;
    }

    private BasicSpreadsheetEngineChangesCache(final S reference,
                                               final V value) {
        this.reference = reference;
        this.value = value;
    }

    /**
     * The {@link walkingkooka.spreadsheet.reference.SpreadsheetCellReference} or {@link walkingkooka.spreadsheet.reference.SpreadsheetLabelName}.
     */
    final S reference;

    BasicSpreadsheetEngineChangesCache<S, V> save() {
        this.save = true;
        return this;
    }

    BasicSpreadsheetEngineChangesCache<S, V> delete() {
        this.save = false;
        return this;
    }

    /**
     * Only true for SAVE/UPDATES will be false for DELETES.
     */
    boolean save;

    /**
     * When {@link #save is true, could be null if an existing cell hasnt been loaded such as when iterating over
     * {@link walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping}.
     */
    V value;

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
                .label("save")
                .value(this.save)
                .label("committed")
                .value(this.committed)
                .build();
    }
}

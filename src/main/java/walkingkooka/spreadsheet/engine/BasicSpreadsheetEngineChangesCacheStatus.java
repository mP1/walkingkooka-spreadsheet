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

/**
 * Base class for a status that accompanies a {@link BasicSpreadsheetEngineChangesCache}.
 */
interface BasicSpreadsheetEngineChangesCacheStatus<S extends SpreadsheetSelection> {

    /**
     * Returns true for status that have not received their value.
     */
    boolean isMissingValue();

    /**
     * Returns true if the status is a reference (its name should begin with REFERENCE).
     */
    boolean isReference();

    /**
     * Returns true if the cell/label is unloaded and its value needs to be loaded from its store.
     */
    boolean isUnloaded();

    /**
     * Returns true if the cell is loading and its value needs to be loaded from its store.
     */
    boolean isLoading();

    /**
     * Returns true if the cell is being saved but its formula has not been evaluated.
     */
    boolean isSaving();

    /**
     * Returns true if the cell/label has been deleted and has no value.
     */
    boolean isDeleted();

    /**
     * Returns true if this cell/label has not had its references refreshed.
     */
    boolean isRefreshable();

    /**
     * Returns true if this cell/label has been loaded/saved/deleted. This will return false if the status is LOADING/REFERENCE_LOADING.
     */
    boolean isReferenceRefreshable();

    /**
     * Only status which have had their references refreshed will return true. They will have names ending in REFERENCES_REFRESHED.
     */
    boolean isReferencesRefreshed();

    BasicSpreadsheetEngineChangesCacheStatus<S> toNonReference();

    BasicSpreadsheetEngineChangesCacheStatus<S> loading();

    /**
     * Helpers transition UNLOADED to LOADED, all other status should throw an {@link Error}.
     */
    BasicSpreadsheetEngineChangesCacheStatus<S> loaded();

    BasicSpreadsheetEngineChangesCacheStatus<S> saved();

    BasicSpreadsheetEngineChangesCacheStatus<S> deleted();

    BasicSpreadsheetEngineChangesCacheStatus<S> forceReferencesRefresh();

    BasicSpreadsheetEngineChangesCacheStatus<S> referencesRefreshed();

    /**
     * Getter that returns a value if one is present, complaining if a value is not available, eg is deleted.
     */
    default <T> T value(final S selection,
                        final T value) {
        if (this.isMissingValue()) {
            throw new Error("Value missing for " + selection + " " + this);
        }

        return value;
    }

    int priority();

    default boolean containsOrEqual(final String left,
                                    final String right) {
        return left.equals(right) ||
            left.contains(right);
    }
}

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

import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;

/**
 * All statuses possible during a column's lifecycle.
 */
enum BasicSpreadsheetEngineChangesCacheStatusColumn implements BasicSpreadsheetEngineChangesCacheStatus<SpreadsheetColumnReference> {

    SAVED(0),

    DELETED(1);

    BasicSpreadsheetEngineChangesCacheStatusColumn(final int priority) {
        this.priority = priority;
    }

    @Override
    public boolean isUnloaded() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isLoading() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSaving() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDeleted() {
        return this == DELETED;
    }

    @Override
    public boolean isMissingValue() {
        return null == DELETED;
    }

    @Override
    public boolean isReference() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRefreshable() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isReferenceRefreshable() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isReferencesRefreshed() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BasicSpreadsheetEngineChangesCacheStatusColumn toNonReference() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BasicSpreadsheetEngineChangesCacheStatusColumn loading() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BasicSpreadsheetEngineChangesCacheStatusColumn loaded() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BasicSpreadsheetEngineChangesCacheStatusColumn saved() {
        return SAVED;
    }

    @Override
    public BasicSpreadsheetEngineChangesCacheStatusColumn deleted() {
        return DELETED;
    }

    @Override
    public BasicSpreadsheetEngineChangesCacheStatusColumn forceReferencesRefresh() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BasicSpreadsheetEngineChangesCacheStatusColumn referencesRefreshed() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int priority() {
        return this.priority;
    }

    private final int priority;
}

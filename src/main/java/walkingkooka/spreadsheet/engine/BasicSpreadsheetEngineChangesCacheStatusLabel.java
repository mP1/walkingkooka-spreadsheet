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

import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;

/**
 * All statuses possible during a label's lifecycle.
 */
enum BasicSpreadsheetEngineChangesCacheStatusLabel implements BasicSpreadsheetEngineChangesCacheStatus<SpreadsheetLabelName> {

    /**
     * A label about to be loaded, with no {@link SpreadsheetLabelName}.
     */
    UNLOADED(0),

    /**
     * A label that been loaded.
     */
    LOADED(1),

    /**
     * A loaded label that has had its references also refreshed.
     */
    LOADED_REFERENCES_REFRESHED(2),

    SAVED(1),

    SAVED_REFERENCES_REFRESHED(3),

    DELETED(1),

    DELETED_REFERENCES_REFRESHED(2),

    REFERENCE_UNLOADED(0),

    REFERENCE_LOADED(1),

    REFERENCE_LOADED_REFERENCES_REFRESHED(2),

    REFERENCE_SAVED(2),

    REFERENCE_SAVED_REFERENCES_REFRESHED(3),

    REFERENCE_DELETED(1),

    REFERENCE_DELETED_REFERENCES_REFRESHED(2);

    BasicSpreadsheetEngineChangesCacheStatusLabel(final int priority) {
        final String name = this.name();

        this.isReference = name.startsWith("REFERENCE_");

        this.isMissingValue = this.containsOrEqual(
            name,
            "UNLOAD"
        ) ||
            this.containsOrEqual(
                name,
                "DELETE"
            );

        this.isDeleted = this.containsOrEqual(
            name,
            "DELETE"
        );

        this.referencesRefreshed = name.endsWith("REFERENCES_REFRESHED");

        this.priority = priority;
    }

    @Override
    public boolean isUnloaded() {
        return UNLOADED == this || REFERENCE_UNLOADED == this;
    }

    @Override
    public boolean isLoading() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSaving() {
        return false;
    }

    @Override
    public boolean isDeleted() {
        return this.isDeleted;
    }

    private final boolean isDeleted;

    @Override
    public final boolean isMissingValue() {
        return this.isMissingValue;
    }

    private final boolean isMissingValue;

    @Override
    public final boolean isReference() {
        return this.isReference;
    }

    private final boolean isReference;

    @Override
    public boolean isRefreshable() {
        return this == UNLOADED ||
            this == LOADED ||
            this == SAVED ||
            this == DELETED ||
            this == REFERENCE_UNLOADED ||
            this == REFERENCE_LOADED ||
            this == REFERENCE_SAVED ||
            this == REFERENCE_DELETED;
    }

    @Override
    public boolean isReferenceRefreshable() {
        return false == this.isUnloaded() &&
            false == this.isSaving() &&
            this.isRefreshable();
    }

    @Override
    public boolean isReferencesRefreshed() {
        return this.referencesRefreshed;
    }

    private final boolean referencesRefreshed;

    @Override
    public BasicSpreadsheetEngineChangesCacheStatusLabel loading() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BasicSpreadsheetEngineChangesCacheStatusLabel loaded() {
        return this.isReference ?
            REFERENCE_LOADED :
            LOADED;
    }

    @Override
    public final BasicSpreadsheetEngineChangesCacheStatusLabel saved() {
        return this.isReference ?
            REFERENCE_SAVED :
            SAVED;
    }

    @Override
    public final BasicSpreadsheetEngineChangesCacheStatusLabel deleted() {
        return this.isReference ?
            REFERENCE_DELETED :
            DELETED;
    }

    @Override
    public final BasicSpreadsheetEngineChangesCacheStatusLabel forceReferencesRefresh() {
        final BasicSpreadsheetEngineChangesCacheStatusLabel newStatus;

        switch (this) {
            case UNLOADED:
                newStatus = this;
                break;
            case LOADED:
            case LOADED_REFERENCES_REFRESHED:
                newStatus = LOADED;
                break;
            case SAVED:
            case SAVED_REFERENCES_REFRESHED:
                newStatus = SAVED;
                break;
            case DELETED:
            case DELETED_REFERENCES_REFRESHED:
                newStatus = DELETED;
                break;
            case REFERENCE_UNLOADED:
                newStatus = this;
                break;
            case REFERENCE_LOADED:
            case REFERENCE_LOADED_REFERENCES_REFRESHED:
                newStatus = REFERENCE_LOADED;
                break;
            case REFERENCE_SAVED:
            case REFERENCE_SAVED_REFERENCES_REFRESHED:
                newStatus = REFERENCE_SAVED;
                break;
            case REFERENCE_DELETED:
            case REFERENCE_DELETED_REFERENCES_REFRESHED:
                newStatus = REFERENCE_DELETED;
                break;
            default:
                throw new Error("Unhandled status " + this);
        }

        return newStatus;
    }

    @Override
    public final BasicSpreadsheetEngineChangesCacheStatusLabel referencesRefreshed() {
        final BasicSpreadsheetEngineChangesCacheStatusLabel newStatus;

        switch (this) {
            case LOADED:
                newStatus = LOADED_REFERENCES_REFRESHED;
                break;
            case SAVED:
                newStatus = SAVED_REFERENCES_REFRESHED;
                break;
            case DELETED:
                newStatus = DELETED_REFERENCES_REFRESHED;
                break;
            case REFERENCE_LOADED:
                newStatus = REFERENCE_LOADED_REFERENCES_REFRESHED;
                break;
            case REFERENCE_SAVED:
                newStatus = REFERENCE_SAVED_REFERENCES_REFRESHED;
                break;
            case REFERENCE_DELETED:
                newStatus = REFERENCE_DELETED_REFERENCES_REFRESHED;
                break;
            default:
                throw new Error("This label should not have refreshed its references " + this);
        }

        return newStatus;
    }

    @Override
    public BasicSpreadsheetEngineChangesCacheStatusLabel toNonReference() {
        if (null == this.nonReference) {
            this.nonReference = valueOf(
                this.name()
                    .replace(
                        "REFERENCE_",
                        ""
                    )
            );
        }
        return this.nonReference;
    }

    private BasicSpreadsheetEngineChangesCacheStatusLabel nonReference;

    @Override
    public final int priority() {
        return this.priority;
    }

    private final int priority;
}

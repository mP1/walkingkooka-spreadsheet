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

import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;

/**
 * All statuses possible during a cell's lifecycle.
 */
enum BasicSpreadsheetEngineChangesCacheStatusCell implements BasicSpreadsheetEngineChangesCacheStatus<SpreadsheetCellReference> {

    /**
     * A marker that indicates the {@link SpreadsheetCellReference} needs to be loaded and evaluated.
     * The value will be null.
     */
    UNLOADED(0),

    /**
     * A cell that has not yet been loaded, with the value containing the unevaluated cell.
     */
    LOADING(1),

    /**
     * A cell that has been loaded and evaluated
     */
    LOADED(2),

    /**
     * A {@link #LOADED} that has also had its references refreshed.
     */
    LOADED_REFERENCES_REFRESHED(3),

    /**
     * A cell that is being saved but has not yet been fully evaluated. The value will contain the un-evaluated cell.
     */
    SAVING(1),

    /**
     * A cell that has been evaluated and saved.
     */
    SAVED(2),

    /**
     * A {@link #SAVED} that has also had its references refreshed.
     */
    SAVED_REFERENCES_REFRESHED(3),

    /**
     * A cell that has been deleted.
     */
    DELETED(1),

    /**
     * A deleted cell that has also had its references refreshed.
     */
    DELETED_REFERENCES_REFRESHED(2),

    REFERENCE_UNLOADED(0),

    REFERENCE_LOADING(1),

    REFERENCE_LOADED(2),

    REFERENCE_LOADED_REFERENCES_REFRESHED(3),

    REFERENCE_SAVING(1),

    REFERENCE_SAVED(2),

    REFERENCE_SAVED_REFERENCES_REFRESHED(3),

    REFERENCE_DELETED(0),

    REFERENCE_DELETED_REFERENCES_REFRESHED(1);

    BasicSpreadsheetEngineChangesCacheStatusCell(final int priority) {
        final String name = this.name();

        this.isReference = name.startsWith("REFERENCE_");

        this.isMissingValue = this.containsOrEqual(name, "UNLOAD") ||
            // LOADING must have a cell but it requires being evaluated.
            this.containsOrEqual(name, "SAVING") ||
            this.containsOrEqual(name, "DELETE");

        this.isDeleted = this.containsOrEqual(name, "DELETE");

        this.referencesRefreshed = name.endsWith("REFERENCES_REFRESHED");

        this.priority = priority;
    }

    @Override
    public boolean isUnloaded() {
        return this == UNLOADED || this == REFERENCE_UNLOADED;
    }

    @Override
    public boolean isLoading() {
        return this == LOADING || this == REFERENCE_LOADING;
    }

    @Override
    public boolean isSaving() {
        return this == SAVING || this == REFERENCE_SAVING;
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
            this == LOADING ||
            this == LOADED ||
            this == SAVING ||
            this == SAVED ||
            this == DELETED ||
            this == REFERENCE_UNLOADED ||
            this == REFERENCE_LOADING ||
            this == REFERENCE_LOADED ||
            this == REFERENCE_SAVING ||
            this == REFERENCE_SAVED ||
            this == REFERENCE_DELETED;
    }

    @Override
    public boolean isReferenceRefreshable() {
        return false == this.isUnloaded() &&
            false == this.isLoading() &&
            false == this.isSaving() &&
            this.isRefreshable();
    }

    @Override
    public boolean isReferencesRefreshed() {
        return this.referencesRefreshed;
    }

    private final boolean referencesRefreshed;

    @Override
    public BasicSpreadsheetEngineChangesCacheStatusCell loading() {
        return this.isReference ?
            REFERENCE_LOADING :
            LOADING;
    }

    @Override
    public BasicSpreadsheetEngineChangesCacheStatusCell loaded() {
        return this.isReference ?
            REFERENCE_LOADED :
            LOADED;
    }

    @Override
    public final BasicSpreadsheetEngineChangesCacheStatusCell saved() {
        return this.isReference ?
            REFERENCE_SAVED :
            SAVED;
    }

    @Override
    public final BasicSpreadsheetEngineChangesCacheStatusCell deleted() {
        return this.isReference ?
            REFERENCE_DELETED :
            DELETED;
    }

    @Override
    public final BasicSpreadsheetEngineChangesCacheStatusCell forceReferencesRefresh() {
        final BasicSpreadsheetEngineChangesCacheStatusCell newStatus;

        switch (this) {
            case UNLOADED:
            case LOADING:
                newStatus = this;
                break;
            case LOADED:
            case LOADED_REFERENCES_REFRESHED:
                newStatus = LOADED;
                break;
            case SAVING:
                newStatus = this;
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
            case REFERENCE_LOADING:
                newStatus = this;
                break;
            case REFERENCE_LOADED:
            case REFERENCE_LOADED_REFERENCES_REFRESHED:
                newStatus = REFERENCE_LOADED;
                break;
            case REFERENCE_SAVING:
                newStatus = this;
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
    public final BasicSpreadsheetEngineChangesCacheStatusCell referencesRefreshed() {
        final BasicSpreadsheetEngineChangesCacheStatusCell newStatus;

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
                throw new Error("This cell should not have refreshed its references " + this);
        }

        return newStatus;
    }

    @Override
    public BasicSpreadsheetEngineChangesCacheStatusCell toNonReference() {
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

    private BasicSpreadsheetEngineChangesCacheStatusCell nonReference;

    @Override
    public final int priority() {
        return this.priority;
    }

    private final int priority;
}

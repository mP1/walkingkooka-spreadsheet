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

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;

import java.util.Arrays;

public final class BasicSpreadsheetEngineChangesCacheStatusCellTest extends BasicSpreadsheetEngineChangesCacheStatusTestCase<BasicSpreadsheetEngineChangesCacheStatusCell,
    SpreadsheetCellReference> {

    // isLoading........................................................................................................

    @Test
    public void testIsLoading() {
        for (final BasicSpreadsheetEngineChangesCacheStatusCell label : BasicSpreadsheetEngineChangesCacheStatusCell.values()) {
            this.isLoadingAndCheck(
                label,
                label.name().contains("LOADI")
            );
        }
    }

    // isLoaded.........................................................................................................

    @Test
    public void testIsLoadedAll() {
        for (BasicSpreadsheetEngineChangesCacheStatusCell status : this.values()) {
            this.isLoadingAndCheck(
                status,
                status.name()
                    .contains("LOADIN")
            );
        }
    }

    @Test
    public void testIsLoadedWithLoading() {
        this.isLoadingAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADING,
            true
        );
    }

    @Test
    public void testIsLoadedWithLoaded() {
        this.isLoadingAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADED,
            false
        );
    }

    @Test
    public void testIsLoadedWithLoadedReferencesRefreshed() {
        this.isLoadingAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsLoadedWithSaved() {
        this.isLoadingAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVED,
            false
        );
    }

    @Test
    public void testIsLoadedWithSavedReferencesRefreshed() {
        this.isLoadingAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsLoadedWithDeleted() {
        this.isLoadingAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.DELETED,
            false
        );
    }

    @Test
    public void testIsLoadedWithDeletedReferencesRefreshed() {
        this.isLoadingAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.DELETED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsLoadedWithReferenceLoading() {
        this.isLoadingAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADING,
            true
        );
    }

    @Test
    public void testIsLoadedWithReferenceLoaded() {
        this.isLoadingAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADED,
            false
        );
    }

    @Test
    public void testIsLoadedWithReferenceLoadedReferencesRefreshed() {
        this.isLoadingAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsLoadedWithReferenceSaved() {
        this.isLoadingAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_SAVED,
            false
        );
    }

    @Test
    public void testIsLoadedWithReferenceSavedReferencesRefreshed() {
        this.isLoadingAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsLoadedWithReferenceDeleted() {
        this.isLoadingAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_DELETED,
            false
        );
    }

    @Test
    public void testIsLoadedWithReferenceDeletedReferencesRefreshed() {
        this.isLoadingAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_DELETED_REFERENCES_REFRESHED,
            false
        );
    }


    // isSaving.........................................................................................................

    @Test
    public void testIsSaving() {
        for (final BasicSpreadsheetEngineChangesCacheStatusCell label : BasicSpreadsheetEngineChangesCacheStatusCell.values()) {
            this.isSavingAndCheck(
                label,
                label.name().contains("SAVIN")
            );
        }
    }

    private void isSavingAndCheck(final BasicSpreadsheetEngineChangesCacheStatusCell status,
                                  final boolean expected) {
        this.checkEquals(
            expected,
            status.isSaving(),
            status::toString
        );
    }

    // isDeleted........................................................................................................

    @Test
    public void testIsDeleted() {
        for (final BasicSpreadsheetEngineChangesCacheStatusCell label : BasicSpreadsheetEngineChangesCacheStatusCell.values()) {
            this.isDeletedAndCheck(
                label,
                label.name().contains("DELETE")
            );
        }
    }

    // isMissingValue...................................................................................................

    @Test
    public void testIsMissingValueAll() {
        for (final BasicSpreadsheetEngineChangesCacheStatusCell status : this.values()) {
            status.isMissingValue();
        }
    }

    @Test
    public void testIsMissingValueWithUnloaded() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.UNLOADED,
            true
        );
    }

    @Test
    public void testIsMissingValueWithLoading() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADING,
            false
        );
    }

    @Test
    public void testIsMissingValueWithLoaded() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithLoadedReferencesRefreshed() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithSaving() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVING,
            true
        );
    }

    @Test
    public void testIsMissingValueWithSaved() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithSavedReferencesRefreshed() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithDeleted() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.DELETED,
            true
        );
    }

    @Test
    public void testIsMissingValueWithDeletedReferencesRefreshed() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.DELETED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsMissingValueWithReferenceUnloaded() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_UNLOADED,
            true
        );
    }

    @Test
    public void testIsMissingValueWithReferenceLoading() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADING,
            false
        );
    }

    @Test
    public void testIsMissingValueWithReferenceLoaded() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithReferenceLoadedReferencesRefreshed() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithReferenceSaving() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_SAVING,
            true
        );
    }

    @Test
    public void testIsMissingValueWithReferenceSaved() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_SAVED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithReferenceSavedReferencesRefreshed() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithReferenceDeleted() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_DELETED,
            true
        );
    }

    @Test
    public void testIsMissingValueWithReferenceDeletedReferencesRefreshed() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_DELETED_REFERENCES_REFRESHED,
            true
        );
    }

    // isReference......................................................................................................

    @Test
    public void testIsReferenceWithLoading() {
        this.isReferenceAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADING,
            false
        );
    }

    @Test
    public void testIsReferenceWithLoaded() {
        this.isReferenceAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADED,
            false
        );
    }

    @Test
    public void testIsReferenceWithLoadedReferencesRefreshed() {
        this.isReferenceAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceWithSaving() {
        this.isReferenceAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVING,
            false
        );
    }

    @Test
    public void testIsReferenceWithSaved() {
        this.isReferenceAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVED,
            false
        );
    }

    @Test
    public void testIsReferenceWithSavedReferencesRefreshed() {
        this.isReferenceAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceWithDeleted() {
        this.isReferenceAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.DELETED,
            false
        );
    }

    @Test
    public void testIsReferenceWithDeletedReferencesRefreshed() {
        this.isReferenceAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.DELETED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceWithReferenceLoading() {
        this.isReferenceAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADING,
            true
        );
    }

    @Test
    public void testIsReferenceWithReferenceLoaded() {
        this.isReferenceAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADED,
            true
        );
    }

    @Test
    public void testIsReferenceWithReferenceLoadedReferencesRefreshed() {
        this.isReferenceAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsReferenceWithReferenceSaving() {
        this.isReferenceAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_SAVING,
            true
        );
    }

    @Test
    public void testIsReferenceWithReferenceSaved() {
        this.isReferenceAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_SAVED,
            true
        );
    }

    @Test
    public void testIsReferenceWithReferenceSavedReferencesRefreshed() {
        this.isReferenceAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_SAVED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsReferenceWithReferenceDeleted() {
        this.isReferenceAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_DELETED,
            true
        );
    }

    @Test
    public void testIsReferenceWithReferenceDeletedReferencesRefreshed() {
        this.isReferenceAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_DELETED_REFERENCES_REFRESHED,
            true
        );
    }

    // isReferencesRefreshed............................................................................................

    @Test
    public void testIsReferencesRefreshedAll() {
        for (BasicSpreadsheetEngineChangesCacheStatusCell status : this.values()) {
            this.isReferencesRefreshedAndCheck(
                status,
                status.name()
                    .contains("REFERENCES_REFRESHED")
            );
        }
    }

    @Test
    public void testIsReferencesRefreshedWithLoading() {
        this.isReferencesRefreshedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADING,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithLoaded() {
        this.isReferencesRefreshedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADED,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithLoadedReferencesRefreshed() {
        this.isReferencesRefreshedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsReferencesRefreshedWithSaving() {
        this.isReferencesRefreshedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVING,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithSaved() {
        this.isReferencesRefreshedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVED,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithSavedReferencesRefreshed() {
        this.isReferencesRefreshedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsReferencesRefreshedWithDeleted() {
        this.isReferencesRefreshedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.DELETED,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithDeletedReferencesRefreshed() {
        this.isReferencesRefreshedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.DELETED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsReferencesRefreshedWithReferenceLoading() {
        this.isReferencesRefreshedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADING,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithReferenceLoaded() {
        this.isReferencesRefreshedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADED,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithReferenceLoadedReferencesRefreshed() {
        this.isReferencesRefreshedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsReferencesRefreshedWithReferenceSaving() {
        this.isReferencesRefreshedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_SAVING,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithReferenceSaved() {
        this.isReferencesRefreshedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_SAVED,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithReferenceSavedReferencesRefreshed() {
        this.isReferencesRefreshedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_SAVED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsReferencesRefreshedWithReferenceDeleted() {
        this.isReferencesRefreshedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_DELETED,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithReferenceDeletedReferencesRefreshed() {
        this.isReferencesRefreshedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_DELETED_REFERENCES_REFRESHED,
            true
        );
    }

    // isRefreshable....................................................................................................

    @Test
    public void testIsRefreshableWithLoading() {
        this.isRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADING,
            true
        );
    }

    @Test
    public void testIsRefreshableWithLoaded() {
        this.isRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADED,
            true
        );
    }

    @Test
    public void testIsRefreshableWithLoadedReferencesRefreshed() {
        this.isRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsRefreshableWithSaving() {
        this.isRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVING,
            true
        );
    }

    @Test
    public void testIsRefreshableWithSaved() {
        this.isRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVED,
            true
        );
    }

    @Test
    public void testIsRefreshableWithSavedReferencesRefreshed() {
        this.isRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsRefreshableWithDeleted() {
        this.isRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.DELETED,
            true
        );
    }

    @Test
    public void testIsRefreshableWithDeletedReferencesRefreshed() {
        this.isRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.DELETED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsRefreshableWithReferenceLoading() {
        this.isRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADING,
            true
        );
    }

    @Test
    public void testIsRefreshableWithReferenceLoaded() {
        this.isRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADED,
            true
        );
    }

    @Test
    public void testIsRefreshableWithReferenceLoadedReferencesRefreshed() {
        this.isRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsRefreshableWithReferenceSaving() {
        this.isRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_SAVING,
            true
        );
    }

    @Test
    public void testIsRefreshableWithReferenceSaved() {
        this.isRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_SAVED,
            true
        );
    }

    @Test
    public void testIsRefreshableWithReferenceSavedReferencesRefreshed() {
        this.isRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsRefreshableWithReferenceDeleted() {
        this.isRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_DELETED,
            true
        );
    }

    @Test
    public void testIsRefreshableWithReferenceDeletedReferencesRefreshed() {
        this.isRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_DELETED_REFERENCES_REFRESHED,
            false
        );
    }

    // isReferenceRefreshable...........................................................................................

    @Test
    public void testIsReferenceRefreshableWithUnloaded() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.UNLOADED,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithLoading() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADING,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithLoaded() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADED,
            true
        );
    }

    @Test
    public void testIsReferenceRefreshableWithLoadedReferencesRefreshed() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithSaving() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVING,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithSaved() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVED,
            true
        );
    }

    @Test
    public void testIsReferenceRefreshableWithSavedReferencesRefreshed() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithDeleted() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.DELETED,
            true
        );
    }

    @Test
    public void testIsReferenceRefreshableWithDeletedReferencesRefreshed() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.DELETED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceUnloading() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_UNLOADED,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceLoading() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADING,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceLoaded() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADED,
            true
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceLoadedReferencesRefreshed() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceSaving() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_SAVING,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceSaved() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_SAVED,
            true
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceSavedReferencesRefreshed() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceDeleted() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_DELETED,
            true
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceDeletedReferencesRefreshed() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_DELETED_REFERENCES_REFRESHED,
            false
        );
    }

    // loaded...........................................................................................................

    @Test
    public void testLoadedWithLoading() {
        this.loadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADING,
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADED
        );
    }

    @Test
    public void testLoadedWithLoaded() {
        this.loadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADED,
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADED
        );
    }

    @Test
    public void testLoadedWithLoadedReferencesRefreshed() {
        this.loadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADED_REFERENCES_REFRESHED,
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADED
        );
    }

    @Test
    public void testLoadedWithSaved() {
        this.loadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVED,
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADED
        );
    }

    @Test
    public void testLoadedWithSavedReferencesRefreshed() {
        this.loadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVED_REFERENCES_REFRESHED,
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADED
        );
    }

    @Test
    public void testLoadedWithDeleted() {
        this.loadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.DELETED,
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADED
        );
    }

    @Test
    public void testLoadedWithDeletedReferencesRefreshed() {
        this.loadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.DELETED_REFERENCES_REFRESHED,
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADED
        );
    }

    @Test
    public void testLoadedWithReferenceLoading() {
        this.loadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADING,
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADED
        );
    }

    @Test
    public void testLoadedWithReferenceLoaded() {
        this.loadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADED
        );
    }

    @Test
    public void testLoadedWithReferenceLoadedReferencesRefreshed() {
        this.loadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADED_REFERENCES_REFRESHED,
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADED
        );
    }

    @Test
    public void testLoadedWithReferenceSaved() {
        this.loadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_SAVED,
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADED
        );
    }

    @Test
    public void testLoadedWithReferenceSavedReferencesRefreshed() {
        this.loadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_SAVED_REFERENCES_REFRESHED,
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADED
        );
    }

    @Test
    public void testLoadedWithReferenceDeleted() {
        this.loadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADED
        );
    }

    @Test
    public void testLoadedWithReferenceDeletedReferencesRefreshed() {
        this.loadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_DELETED_REFERENCES_REFRESHED,
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADED
        );
    }

    // loaded...........................................................................................................

    @Test
    public void testSavedWithLoading() {
        this.savedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADING,
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVED
        );
    }

    @Test
    public void testSavedWithLoaded() {
        this.savedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADED,
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVED
        );
    }

    @Test
    public void testSavedWithLoadedReferencesRefreshed() {
        this.savedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADED_REFERENCES_REFRESHED,
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVED
        );
    }

    @Test
    public void testSavedWithSaved() {
        this.savedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVED
        );
    }

    @Test
    public void testSavedWithSavedReferencesRefreshed() {
        this.savedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVED_REFERENCES_REFRESHED,
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVED
        );
    }

    @Test
    public void testSavedWithDeleted() {
        this.savedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.DELETED,
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVED
        );
    }

    @Test
    public void testSavedWithDeletedReferencesRefreshed() {
        this.savedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.DELETED_REFERENCES_REFRESHED,
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVED
        );
    }

    @Test
    public void testSavedWithReferenceLoading() {
        this.savedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADING,
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_SAVED
        );
    }

    @Test
    public void testSavedWithReferenceLoaded() {
        this.savedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADED,
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_SAVED
        );
    }

    @Test
    public void testSavedWithReferenceLoadedReferencesRefreshed() {
        this.savedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADED_REFERENCES_REFRESHED,
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_SAVED
        );
    }

    @Test
    public void testSavedWithReferenceSaved() {
        this.savedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_SAVED
        );
    }

    @Test
    public void testSavedWithReferenceSavedReferencesRefreshed() {
        this.savedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_SAVED_REFERENCES_REFRESHED,
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_SAVED
        );
    }

    @Test
    public void testSavedWithReferenceDeleted() {
        this.savedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_DELETED,
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_SAVED
        );
    }

    @Test
    public void testSavedWithReferenceDeletedReferencesRefreshed() {
        this.savedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_DELETED_REFERENCES_REFRESHED,
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_SAVED
        );
    }

    // forceReferencesRefresh...........................................................................................

    @Test
    public void testForceReferencesRefreshAll() {
        Arrays.stream(this.values())
            .forEach(BasicSpreadsheetEngineChangesCacheStatus::forceReferencesRefresh);
    }

    @Override
    BasicSpreadsheetEngineChangesCacheStatusCell[] values() {
        return BasicSpreadsheetEngineChangesCacheStatusCell.values();
    }

    // class............................................................................................................

    @Override
    public Class<BasicSpreadsheetEngineChangesCacheStatusCell> type() {
        return BasicSpreadsheetEngineChangesCacheStatusCell.class;
    }
}

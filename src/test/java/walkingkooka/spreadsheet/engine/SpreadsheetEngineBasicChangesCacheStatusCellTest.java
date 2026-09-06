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

public final class SpreadsheetEngineBasicChangesCacheStatusCellTest extends SpreadsheetEngineBasicChangesCacheStatusTestCase<SpreadsheetEngineBasicChangesCacheStatusCell,
    SpreadsheetCellReference> {

    // isLoading........................................................................................................

    @Test
    public void testIsLoading() {
        for (final SpreadsheetEngineBasicChangesCacheStatusCell label : SpreadsheetEngineBasicChangesCacheStatusCell.values()) {
            this.isLoadingAndCheck(
                label,
                label.name().contains("LOADI")
            );
        }
    }

    // isLoaded.........................................................................................................

    @Test
    public void testIsLoadedAll() {
        for (SpreadsheetEngineBasicChangesCacheStatusCell status : this.values()) {
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
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADING,
            true
        );
    }

    @Test
    public void testIsLoadedWithLoaded() {
        this.isLoadingAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADED,
            false
        );
    }

    @Test
    public void testIsLoadedWithLoadedReferencesRefreshed() {
        this.isLoadingAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsLoadedWithSaved() {
        this.isLoadingAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVED,
            false
        );
    }

    @Test
    public void testIsLoadedWithSavedReferencesRefreshed() {
        this.isLoadingAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsLoadedWithDeleted() {
        this.isLoadingAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.DELETED,
            false
        );
    }

    @Test
    public void testIsLoadedWithDeletedReferencesRefreshed() {
        this.isLoadingAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.DELETED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsLoadedWithReferenceLoading() {
        this.isLoadingAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADING,
            true
        );
    }

    @Test
    public void testIsLoadedWithReferenceLoaded() {
        this.isLoadingAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADED,
            false
        );
    }

    @Test
    public void testIsLoadedWithReferenceLoadedReferencesRefreshed() {
        this.isLoadingAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsLoadedWithReferenceSaved() {
        this.isLoadingAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_SAVED,
            false
        );
    }

    @Test
    public void testIsLoadedWithReferenceSavedReferencesRefreshed() {
        this.isLoadingAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsLoadedWithReferenceDeleted() {
        this.isLoadingAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_DELETED,
            false
        );
    }

    @Test
    public void testIsLoadedWithReferenceDeletedReferencesRefreshed() {
        this.isLoadingAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_DELETED_REFERENCES_REFRESHED,
            false
        );
    }


    // isSaving.........................................................................................................

    @Test
    public void testIsSaving() {
        for (final SpreadsheetEngineBasicChangesCacheStatusCell label : SpreadsheetEngineBasicChangesCacheStatusCell.values()) {
            this.isSavingAndCheck(
                label,
                label.name().contains("SAVIN")
            );
        }
    }

    private void isSavingAndCheck(final SpreadsheetEngineBasicChangesCacheStatusCell status,
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
        for (final SpreadsheetEngineBasicChangesCacheStatusCell label : SpreadsheetEngineBasicChangesCacheStatusCell.values()) {
            this.isDeletedAndCheck(
                label,
                label.name().contains("DELETE")
            );
        }
    }

    // isMissingValue...................................................................................................

    @Test
    public void testIsMissingValueAll() {
        for (final SpreadsheetEngineBasicChangesCacheStatusCell status : this.values()) {
            status.isMissingValue();
        }
    }

    @Test
    public void testIsMissingValueWithUnloaded() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.UNLOADED,
            true
        );
    }

    @Test
    public void testIsMissingValueWithLoading() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADING,
            false
        );
    }

    @Test
    public void testIsMissingValueWithLoaded() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithLoadedReferencesRefreshed() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithSaving() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVING,
            true
        );
    }

    @Test
    public void testIsMissingValueWithSaved() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithSavedReferencesRefreshed() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithDeleted() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.DELETED,
            true
        );
    }

    @Test
    public void testIsMissingValueWithDeletedReferencesRefreshed() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.DELETED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsMissingValueWithReferenceUnloaded() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_UNLOADED,
            true
        );
    }

    @Test
    public void testIsMissingValueWithReferenceLoading() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADING,
            false
        );
    }

    @Test
    public void testIsMissingValueWithReferenceLoaded() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithReferenceLoadedReferencesRefreshed() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithReferenceSaving() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_SAVING,
            true
        );
    }

    @Test
    public void testIsMissingValueWithReferenceSaved() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_SAVED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithReferenceSavedReferencesRefreshed() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithReferenceDeleted() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_DELETED,
            true
        );
    }

    @Test
    public void testIsMissingValueWithReferenceDeletedReferencesRefreshed() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_DELETED_REFERENCES_REFRESHED,
            true
        );
    }

    // isReference......................................................................................................

    @Test
    public void testIsReferenceWithLoading() {
        this.isReferenceAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADING,
            false
        );
    }

    @Test
    public void testIsReferenceWithLoaded() {
        this.isReferenceAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADED,
            false
        );
    }

    @Test
    public void testIsReferenceWithLoadedReferencesRefreshed() {
        this.isReferenceAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceWithSaving() {
        this.isReferenceAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVING,
            false
        );
    }

    @Test
    public void testIsReferenceWithSaved() {
        this.isReferenceAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVED,
            false
        );
    }

    @Test
    public void testIsReferenceWithSavedReferencesRefreshed() {
        this.isReferenceAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceWithDeleted() {
        this.isReferenceAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.DELETED,
            false
        );
    }

    @Test
    public void testIsReferenceWithDeletedReferencesRefreshed() {
        this.isReferenceAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.DELETED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceWithReferenceLoading() {
        this.isReferenceAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADING,
            true
        );
    }

    @Test
    public void testIsReferenceWithReferenceLoaded() {
        this.isReferenceAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADED,
            true
        );
    }

    @Test
    public void testIsReferenceWithReferenceLoadedReferencesRefreshed() {
        this.isReferenceAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsReferenceWithReferenceSaving() {
        this.isReferenceAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_SAVING,
            true
        );
    }

    @Test
    public void testIsReferenceWithReferenceSaved() {
        this.isReferenceAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_SAVED,
            true
        );
    }

    @Test
    public void testIsReferenceWithReferenceSavedReferencesRefreshed() {
        this.isReferenceAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_SAVED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsReferenceWithReferenceDeleted() {
        this.isReferenceAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_DELETED,
            true
        );
    }

    @Test
    public void testIsReferenceWithReferenceDeletedReferencesRefreshed() {
        this.isReferenceAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_DELETED_REFERENCES_REFRESHED,
            true
        );
    }

    // isReferencesRefreshed............................................................................................

    @Test
    public void testIsReferencesRefreshedAll() {
        for (SpreadsheetEngineBasicChangesCacheStatusCell status : this.values()) {
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
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADING,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithLoaded() {
        this.isReferencesRefreshedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADED,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithLoadedReferencesRefreshed() {
        this.isReferencesRefreshedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsReferencesRefreshedWithSaving() {
        this.isReferencesRefreshedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVING,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithSaved() {
        this.isReferencesRefreshedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVED,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithSavedReferencesRefreshed() {
        this.isReferencesRefreshedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsReferencesRefreshedWithDeleted() {
        this.isReferencesRefreshedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.DELETED,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithDeletedReferencesRefreshed() {
        this.isReferencesRefreshedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.DELETED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsReferencesRefreshedWithReferenceLoading() {
        this.isReferencesRefreshedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADING,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithReferenceLoaded() {
        this.isReferencesRefreshedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADED,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithReferenceLoadedReferencesRefreshed() {
        this.isReferencesRefreshedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsReferencesRefreshedWithReferenceSaving() {
        this.isReferencesRefreshedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_SAVING,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithReferenceSaved() {
        this.isReferencesRefreshedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_SAVED,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithReferenceSavedReferencesRefreshed() {
        this.isReferencesRefreshedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_SAVED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsReferencesRefreshedWithReferenceDeleted() {
        this.isReferencesRefreshedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_DELETED,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithReferenceDeletedReferencesRefreshed() {
        this.isReferencesRefreshedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_DELETED_REFERENCES_REFRESHED,
            true
        );
    }

    // isRefreshable....................................................................................................

    @Test
    public void testIsRefreshableWithLoading() {
        this.isRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADING,
            true
        );
    }

    @Test
    public void testIsRefreshableWithLoaded() {
        this.isRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADED,
            true
        );
    }

    @Test
    public void testIsRefreshableWithLoadedReferencesRefreshed() {
        this.isRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsRefreshableWithSaving() {
        this.isRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVING,
            true
        );
    }

    @Test
    public void testIsRefreshableWithSaved() {
        this.isRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVED,
            true
        );
    }

    @Test
    public void testIsRefreshableWithSavedReferencesRefreshed() {
        this.isRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsRefreshableWithDeleted() {
        this.isRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.DELETED,
            true
        );
    }

    @Test
    public void testIsRefreshableWithDeletedReferencesRefreshed() {
        this.isRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.DELETED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsRefreshableWithReferenceLoading() {
        this.isRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADING,
            true
        );
    }

    @Test
    public void testIsRefreshableWithReferenceLoaded() {
        this.isRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADED,
            true
        );
    }

    @Test
    public void testIsRefreshableWithReferenceLoadedReferencesRefreshed() {
        this.isRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsRefreshableWithReferenceSaving() {
        this.isRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_SAVING,
            true
        );
    }

    @Test
    public void testIsRefreshableWithReferenceSaved() {
        this.isRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_SAVED,
            true
        );
    }

    @Test
    public void testIsRefreshableWithReferenceSavedReferencesRefreshed() {
        this.isRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsRefreshableWithReferenceDeleted() {
        this.isRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_DELETED,
            true
        );
    }

    @Test
    public void testIsRefreshableWithReferenceDeletedReferencesRefreshed() {
        this.isRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_DELETED_REFERENCES_REFRESHED,
            false
        );
    }

    // isReferenceRefreshable...........................................................................................

    @Test
    public void testIsReferenceRefreshableWithUnloaded() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.UNLOADED,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithLoading() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADING,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithLoaded() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADED,
            true
        );
    }

    @Test
    public void testIsReferenceRefreshableWithLoadedReferencesRefreshed() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithSaving() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVING,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithSaved() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVED,
            true
        );
    }

    @Test
    public void testIsReferenceRefreshableWithSavedReferencesRefreshed() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithDeleted() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.DELETED,
            true
        );
    }

    @Test
    public void testIsReferenceRefreshableWithDeletedReferencesRefreshed() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.DELETED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceUnloading() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_UNLOADED,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceLoading() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADING,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceLoaded() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADED,
            true
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceLoadedReferencesRefreshed() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceSaving() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_SAVING,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceSaved() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_SAVED,
            true
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceSavedReferencesRefreshed() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceDeleted() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_DELETED,
            true
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceDeletedReferencesRefreshed() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_DELETED_REFERENCES_REFRESHED,
            false
        );
    }

    // loaded...........................................................................................................

    @Test
    public void testLoadedWithLoading() {
        this.loadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADING,
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADED
        );
    }

    @Test
    public void testLoadedWithLoaded() {
        this.loadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADED,
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADED
        );
    }

    @Test
    public void testLoadedWithLoadedReferencesRefreshed() {
        this.loadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADED_REFERENCES_REFRESHED,
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADED
        );
    }

    @Test
    public void testLoadedWithSaved() {
        this.loadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVED,
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADED
        );
    }

    @Test
    public void testLoadedWithSavedReferencesRefreshed() {
        this.loadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVED_REFERENCES_REFRESHED,
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADED
        );
    }

    @Test
    public void testLoadedWithDeleted() {
        this.loadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.DELETED,
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADED
        );
    }

    @Test
    public void testLoadedWithDeletedReferencesRefreshed() {
        this.loadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.DELETED_REFERENCES_REFRESHED,
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADED
        );
    }

    @Test
    public void testLoadedWithReferenceLoading() {
        this.loadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADING,
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADED
        );
    }

    @Test
    public void testLoadedWithReferenceLoaded() {
        this.loadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADED
        );
    }

    @Test
    public void testLoadedWithReferenceLoadedReferencesRefreshed() {
        this.loadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADED_REFERENCES_REFRESHED,
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADED
        );
    }

    @Test
    public void testLoadedWithReferenceSaved() {
        this.loadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_SAVED,
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADED
        );
    }

    @Test
    public void testLoadedWithReferenceSavedReferencesRefreshed() {
        this.loadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_SAVED_REFERENCES_REFRESHED,
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADED
        );
    }

    @Test
    public void testLoadedWithReferenceDeleted() {
        this.loadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADED
        );
    }

    @Test
    public void testLoadedWithReferenceDeletedReferencesRefreshed() {
        this.loadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_DELETED_REFERENCES_REFRESHED,
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADED
        );
    }

    // loaded...........................................................................................................

    @Test
    public void testSavedWithLoading() {
        this.savedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADING,
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVED
        );
    }

    @Test
    public void testSavedWithLoaded() {
        this.savedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADED,
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVED
        );
    }

    @Test
    public void testSavedWithLoadedReferencesRefreshed() {
        this.savedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADED_REFERENCES_REFRESHED,
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVED
        );
    }

    @Test
    public void testSavedWithSaved() {
        this.savedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVED
        );
    }

    @Test
    public void testSavedWithSavedReferencesRefreshed() {
        this.savedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVED_REFERENCES_REFRESHED,
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVED
        );
    }

    @Test
    public void testSavedWithDeleted() {
        this.savedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.DELETED,
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVED
        );
    }

    @Test
    public void testSavedWithDeletedReferencesRefreshed() {
        this.savedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.DELETED_REFERENCES_REFRESHED,
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVED
        );
    }

    @Test
    public void testSavedWithReferenceLoading() {
        this.savedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADING,
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_SAVED
        );
    }

    @Test
    public void testSavedWithReferenceLoaded() {
        this.savedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADED,
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_SAVED
        );
    }

    @Test
    public void testSavedWithReferenceLoadedReferencesRefreshed() {
        this.savedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADED_REFERENCES_REFRESHED,
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_SAVED
        );
    }

    @Test
    public void testSavedWithReferenceSaved() {
        this.savedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_SAVED
        );
    }

    @Test
    public void testSavedWithReferenceSavedReferencesRefreshed() {
        this.savedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_SAVED_REFERENCES_REFRESHED,
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_SAVED
        );
    }

    @Test
    public void testSavedWithReferenceDeleted() {
        this.savedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_DELETED,
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_SAVED
        );
    }

    @Test
    public void testSavedWithReferenceDeletedReferencesRefreshed() {
        this.savedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_DELETED_REFERENCES_REFRESHED,
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_SAVED
        );
    }

    // forceReferencesRefresh...........................................................................................

    @Test
    public void testForceReferencesRefreshAll() {
        Arrays.stream(this.values())
            .forEach(SpreadsheetEngineBasicChangesCacheStatus::forceReferencesRefresh);
    }

    @Override
    SpreadsheetEngineBasicChangesCacheStatusCell[] values() {
        return SpreadsheetEngineBasicChangesCacheStatusCell.values();
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetEngineBasicChangesCacheStatusCell> type() {
        return SpreadsheetEngineBasicChangesCacheStatusCell.class;
    }
}

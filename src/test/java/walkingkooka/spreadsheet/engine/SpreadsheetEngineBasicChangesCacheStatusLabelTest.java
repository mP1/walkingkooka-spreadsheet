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
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetEngineBasicChangesCacheStatusLabelTest extends SpreadsheetEngineBasicChangesCacheStatusTestCase<SpreadsheetEngineBasicChangesCacheStatusLabel,
    SpreadsheetLabelName> {

    // isDeleted........................................................................................................

    @Test
    public void testIsDeleted() {
        for (final SpreadsheetEngineBasicChangesCacheStatusLabel label : SpreadsheetEngineBasicChangesCacheStatusLabel.values()) {
            this.isDeletedAndCheck(
                label,
                label.name().contains("DELETE")
            );
        }
    }

    // isUnloaded........................................................................................................

    @Test
    public void testIsUnloadedAll() {
        for (SpreadsheetEngineBasicChangesCacheStatusLabel status : this.values()) {
            this.isUnloadedAndCheck(
                status,
                status.name()
                    .contains("UNL")
            );
        }
    }

    @Test
    public void testIsUnloadedWithUnloaded() {
        this.isUnloadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.UNLOADED,
            true
        );
    }

    @Test
    public void testIsUnloadedWithLoaded() {
        this.isUnloadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.LOADED,
            false
        );
    }

    @Test
    public void testIsUnloadedWithLoadedReferencesRefreshed() {
        this.isUnloadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsUnloadedWithSaved() {
        this.isUnloadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.SAVED,
            false
        );
    }

    @Test
    public void testIsUnloadedWithSavedReferencesRefreshed() {
        this.isUnloadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsUnloadedWithDeleted() {
        this.isUnloadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.DELETED,
            false
        );
    }

    @Test
    public void testIsUnloadedWithDeletedReferencesRefreshed() {
        this.isUnloadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.DELETED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsUnloadedWithReferenceLoading() {
        this.isUnloadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_UNLOADED,
            true
        );
    }

    @Test
    public void testIsUnloadedWithReferenceLoaded() {
        this.isUnloadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_LOADED,
            false
        );
    }

    @Test
    public void testIsUnloadedWithReferenceLoadedReferencesRefreshed() {
        this.isUnloadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsUnloadedWithReferenceSaved() {
        this.isUnloadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_SAVED,
            false
        );
    }

    @Test
    public void testIsUnloadedWithReferenceSavedReferencesRefreshed() {
        this.isUnloadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsUnloadedWithReferenceDeleted() {
        this.isUnloadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_DELETED,
            false
        );
    }

    @Test
    public void testIsUnloadedWithReferenceDeletedReferencesRefreshed() {
        this.isUnloadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_DELETED_REFERENCES_REFRESHED,
            false
        );
    }

    // isLoading........................................................................................................

    @Test
    public void testIsLoading() {
        assertThrows(
            UnsupportedOperationException.class,
            SpreadsheetEngineBasicChangesCacheStatusLabel.UNLOADED::isLoading
        );
    }

    // isMissingValue....................................................................................................

    @Test
    public void testIsMissingValueAll() {
        for (final SpreadsheetEngineBasicChangesCacheStatusLabel status : this.values()) {
            status.isMissingValue();
        }
    }

    @Test
    public void testIsMissingValueWithUnloading() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.UNLOADED,
            true
        );
    }


    @Test
    public void testIsMissingValueWithLoaded() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.LOADED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithLoadedReferencesRefreshed() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithSaved() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.SAVED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithSavedReferencesRefreshed() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithDeleted() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.DELETED,
            true
        );
    }

    @Test
    public void testIsMissingValueWithDeletedReferencesRefreshed() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.DELETED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsMissingValueWithReferenceUnloaded() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_UNLOADED,
            true
        );
    }

    @Test
    public void testIsMissingValueWithReferenceLoaded() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_LOADED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithReferenceLoadedReferencesRefreshed() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithReferenceSaved() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_SAVED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithReferenceSavedReferencesRefreshed() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithReferenceDeleted() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_DELETED,
            true
        );
    }

    @Test
    public void testIsMissingValueWithReferenceDeletedReferencesRefreshed() {
        this.isMissingValueAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_DELETED_REFERENCES_REFRESHED,
            true
        );
    }

    // isReference......................................................................................................

    @Test
    public void testIsReferenceWithUnloading() {
        this.isReferenceAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.UNLOADED,
            false
        );
    }

    @Test
    public void testIsReferenceWithLoaded() {
        this.isReferenceAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.LOADED,
            false
        );
    }

    @Test
    public void testIsReferenceWithLoadedReferencesRefreshed() {
        this.isReferenceAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceWithSaved() {
        this.isReferenceAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.SAVED,
            false
        );
    }

    @Test
    public void testIsReferenceWithSavedReferencesRefreshed() {
        this.isReferenceAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceWithDeleted() {
        this.isReferenceAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.DELETED,
            false
        );
    }

    @Test
    public void testIsReferenceWithDeletedReferencesRefreshed() {
        this.isReferenceAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.DELETED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceWithReferenceUnloaded() {
        this.isReferenceAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_UNLOADED,
            true
        );
    }

    @Test
    public void testIsReferenceWithReferenceLoaded() {
        this.isReferenceAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_LOADED,
            true
        );
    }

    @Test
    public void testIsReferenceWithReferenceLoadedReferencesRefreshed() {
        this.isReferenceAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_LOADED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsReferenceWithReferenceSaved() {
        this.isReferenceAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_SAVED,
            true
        );
    }

    @Test
    public void testIsReferenceWithReferenceSavedReferencesRefreshed() {
        this.isReferenceAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_SAVED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsReferenceWithReferenceDeleted() {
        this.isReferenceAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_DELETED,
            true
        );
    }

    @Test
    public void testIsReferenceWithReferenceDeletedReferencesRefreshed() {
        this.isReferenceAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_DELETED_REFERENCES_REFRESHED,
            true
        );
    }

    // isReferencesRefreshed............................................................................................

    @Test
    public void testIsReferencesRefreshedAll() {
        for (SpreadsheetEngineBasicChangesCacheStatusLabel status : this.values()) {
            this.isReferencesRefreshedAndCheck(
                status,
                status.name()
                    .contains("REFERENCES_REFRESHED")
            );
        }
    }

    @Test
    public void testIsReferencesRefreshedWithUnloaded() {
        this.isReferencesRefreshedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.UNLOADED,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithLoaded() {
        this.isReferencesRefreshedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.LOADED,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithLoadedReferencesRefreshed() {
        this.isReferencesRefreshedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.LOADED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsReferencesRefreshedWithSaved() {
        this.isReferencesRefreshedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.SAVED,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithSavedReferencesRefreshed() {
        this.isReferencesRefreshedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.SAVED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsReferencesRefreshedWithDeleted() {
        this.isReferencesRefreshedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.DELETED,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithDeletedReferencesRefreshed() {
        this.isReferencesRefreshedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.DELETED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsReferencesRefreshedWithReferenceUnloaded() {
        this.isReferencesRefreshedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_UNLOADED,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithReferenceLoaded() {
        this.isReferencesRefreshedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_LOADED,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithReferenceLoadedReferencesRefreshed() {
        this.isReferencesRefreshedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_LOADED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsReferencesRefreshedWithReferenceSaved() {
        this.isReferencesRefreshedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_SAVED,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithReferenceSavedReferencesRefreshed() {
        this.isReferencesRefreshedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_SAVED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsReferencesRefreshedWithReferenceDeleted() {
        this.isReferencesRefreshedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_DELETED,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithReferenceDeletedReferencesRefreshed() {
        this.isReferencesRefreshedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_DELETED_REFERENCES_REFRESHED,
            true
        );
    }

    // isRefreshable....................................................................................................

    @Test
    public void testIsRefreshableWithUnloaded() {
        this.isRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.UNLOADED,
            true
        );
    }

    @Test
    public void testIsRefreshableWithLoaded() {
        this.isRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.LOADED,
            true
        );
    }

    @Test
    public void testIsRefreshableWithLoadedReferencesRefreshed() {
        this.isRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsRefreshableWithSaved() {
        this.isRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.SAVED,
            true
        );
    }

    @Test
    public void testIsRefreshableWithSavedReferencesRefreshed() {
        this.isRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsRefreshableWithDeleted() {
        this.isRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.DELETED,
            true
        );
    }

    @Test
    public void testIsRefreshableWithDeletedReferencesRefreshed() {
        this.isRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.DELETED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsRefreshableWithReferenceUnloaded() {
        this.isRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_UNLOADED,
            true
        );
    }

    @Test
    public void testIsRefreshableWithReferenceLoaded() {
        this.isRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_LOADED,
            true
        );
    }

    @Test
    public void testIsRefreshableWithReferenceLoadedReferencesRefreshed() {
        this.isRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsRefreshableWithReferenceSaved() {
        this.isRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_SAVED,
            true
        );
    }

    @Test
    public void testIsRefreshableWithReferenceSavedReferencesRefreshed() {
        this.isRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsRefreshableWithReferenceDeleted() {
        this.isRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_DELETED,
            true
        );
    }

    @Test
    public void testIsRefreshableWithReferenceDeletedReferencesRefreshed() {
        this.isRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_DELETED_REFERENCES_REFRESHED,
            false
        );
    }


    // isReferenceRefreshable....................................................................................................

    @Test
    public void testIsReferenceRefreshableWithUnloaded() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.UNLOADED,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithLoaded() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.LOADED,
            true
        );
    }

    @Test
    public void testIsReferenceRefreshableWithLoadedReferencesRefreshed() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithSaved() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.SAVED,
            true
        );
    }

    @Test
    public void testIsReferenceRefreshableWithSavedReferencesRefreshed() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithDeleted() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.DELETED,
            true
        );
    }

    @Test
    public void testIsReferenceRefreshableWithDeletedReferencesRefreshed() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.DELETED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceUnloaded() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_UNLOADED,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceLoaded() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_LOADED,
            true
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceLoadedReferencesRefreshed() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceSaved() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_SAVED,
            true
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceSavedReferencesRefreshed() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceDeleted() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_DELETED,
            true
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceDeletedReferencesRefreshed() {
        this.isReferenceRefreshableAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_DELETED_REFERENCES_REFRESHED,
            false
        );
    }

    // deleted..........................................................................................................

    @Test
    public void testDeletedWithUnloaded() {
        this.deletedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.UNLOADED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.DELETED
        );
    }

    @Test
    public void testDeletedWithLoaded() {
        this.deletedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.LOADED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.DELETED
        );
    }

    @Test
    public void testDeletedWithLoadedReferencesRefreshed() {
        this.deletedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.LOADED_REFERENCES_REFRESHED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.DELETED
        );
    }

    @Test
    public void testDeletedWithSaved() {
        this.deletedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.SAVED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.DELETED
        );
    }

    @Test
    public void testDeletedWithSavedReferencesRefreshed() {
        this.deletedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.SAVED_REFERENCES_REFRESHED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.DELETED
        );
    }

    @Test
    public void testDeletedWithDeleted() {
        this.deletedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.DELETED
        );
    }

    @Test
    public void testDeletedWithDeletedReferencesRefreshed() {
        this.deletedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.DELETED_REFERENCES_REFRESHED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.DELETED
        );
    }

    @Test
    public void testDeletedWithReferenceUnloaded() {
        this.deletedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_UNLOADED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_DELETED
        );
    }

    @Test
    public void testDeletedWithReferenceLoaded() {
        this.deletedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_LOADED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_DELETED
        );
    }

    @Test
    public void testDeletedWithReferenceLoadedReferencesRefreshed() {
        this.deletedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_LOADED_REFERENCES_REFRESHED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_DELETED
        );
    }

    @Test
    public void testDeletedWithReferenceSaved() {
        this.deletedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_SAVED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_DELETED
        );
    }

    @Test
    public void testDeletedWithReferenceSavedReferencesRefreshed() {
        this.deletedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_SAVED_REFERENCES_REFRESHED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_DELETED
        );
    }

    @Test
    public void testDeletedWithReferenceDeleted() {
        this.deletedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_DELETED
        );
    }

    @Test
    public void testDeletedWithReferenceDeletedReferencesRefreshed() {
        this.deletedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_DELETED_REFERENCES_REFRESHED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_DELETED
        );
    }

    // loaded...........................................................................................................

    @Test
    public void testLoadedWithUnloaded() {
        this.loadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.UNLOADED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.LOADED
        );
    }

    @Test
    public void testLoadedWithLoaded() {
        this.loadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.LOADED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.LOADED
        );
    }

    @Test
    public void testLoadedWithLoadedReferencesRefreshed() {
        this.loadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.LOADED_REFERENCES_REFRESHED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.LOADED
        );
    }

    @Test
    public void testLoadedWithSaved() {
        this.loadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.SAVED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.LOADED
        );
    }

    @Test
    public void testLoadedWithSavedReferencesRefreshed() {
        this.loadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.SAVED_REFERENCES_REFRESHED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.LOADED
        );
    }

    @Test
    public void testLoadedWithDeleted() {
        this.loadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.DELETED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.LOADED
        );
    }

    @Test
    public void testLoadedWithDeletedReferencesRefreshed() {
        this.loadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.DELETED_REFERENCES_REFRESHED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.LOADED
        );
    }

    @Test
    public void testLoadedWithReferenceUnloaded() {
        this.loadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_UNLOADED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_LOADED
        );
    }

    @Test
    public void testLoadedWithReferenceLoaded() {
        this.loadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_LOADED
        );
    }

    @Test
    public void testLoadedWithReferenceLoadedReferencesRefreshed() {
        this.loadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_LOADED_REFERENCES_REFRESHED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_LOADED
        );
    }

    @Test
    public void testLoadedWithReferenceSaved() {
        this.loadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_SAVED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_LOADED
        );
    }

    @Test
    public void testLoadedWithReferenceSavedReferencesRefreshed() {
        this.loadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_SAVED_REFERENCES_REFRESHED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_LOADED
        );
    }

    @Test
    public void testLoadedWithReferenceDeleted() {
        this.loadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_LOADED
        );
    }

    @Test
    public void testLoadedWithReferenceDeletedReferencesRefreshed() {
        this.loadedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_DELETED_REFERENCES_REFRESHED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_LOADED
        );
    }

    // loaded...........................................................................................................

    @Test
    public void testSavedWithUnloaded() {
        this.savedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.UNLOADED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.SAVED
        );
    }

    @Test
    public void testSavedWithLoaded() {
        this.savedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.LOADED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.SAVED
        );
    }

    @Test
    public void testSavedWithLoadedReferencesRefreshed() {
        this.savedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.LOADED_REFERENCES_REFRESHED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.SAVED
        );
    }

    @Test
    public void testSavedWithSaved() {
        this.savedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.SAVED
        );
    }

    @Test
    public void testSavedWithSavedReferencesRefreshed() {
        this.savedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.SAVED_REFERENCES_REFRESHED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.SAVED
        );
    }

    @Test
    public void testSavedWithDeleted() {
        this.savedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.DELETED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.SAVED
        );
    }

    @Test
    public void testSavedWithDeletedReferencesRefreshed() {
        this.savedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.DELETED_REFERENCES_REFRESHED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.SAVED
        );
    }

    @Test
    public void testSavedWithReferenceUnloaded() {
        this.savedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_UNLOADED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_SAVED
        );
    }

    @Test
    public void testSavedWithReferenceLoaded() {
        this.savedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_LOADED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_SAVED
        );
    }

    @Test
    public void testSavedWithReferenceLoadedReferencesRefreshed() {
        this.savedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_LOADED_REFERENCES_REFRESHED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_SAVED
        );
    }

    @Test
    public void testSavedWithReferenceSaved() {
        this.savedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_SAVED
        );
    }

    @Test
    public void testSavedWithReferenceSavedReferencesRefreshed() {
        this.savedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_SAVED_REFERENCES_REFRESHED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_SAVED
        );
    }

    @Test
    public void testSavedWithReferenceDeleted() {
        this.savedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_DELETED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_SAVED
        );
    }

    @Test
    public void testSavedWithReferenceDeletedReferencesRefreshed() {
        this.savedAndCheck(
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_DELETED_REFERENCES_REFRESHED,
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_SAVED
        );
    }

    // forceReferencesRefresh...........................................................................................

    @Test
    public void testForceReferencesRefreshAll() {
        Arrays.stream(this.values())
            .forEach(SpreadsheetEngineBasicChangesCacheStatus::forceReferencesRefresh);
    }

    @Override
    SpreadsheetEngineBasicChangesCacheStatusLabel[] values() {
        return SpreadsheetEngineBasicChangesCacheStatusLabel.values();
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetEngineBasicChangesCacheStatusLabel> type() {
        return SpreadsheetEngineBasicChangesCacheStatusLabel.class;
    }
}

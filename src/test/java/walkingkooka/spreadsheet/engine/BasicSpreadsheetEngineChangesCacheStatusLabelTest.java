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

public final class BasicSpreadsheetEngineChangesCacheStatusLabelTest extends BasicSpreadsheetEngineChangesCacheStatusTestCase<BasicSpreadsheetEngineChangesCacheStatusLabel,
    SpreadsheetLabelName> {

    // isDeleted........................................................................................................

    @Test
    public void testIsDeleted() {
        for (final BasicSpreadsheetEngineChangesCacheStatusLabel label : BasicSpreadsheetEngineChangesCacheStatusLabel.values()) {
            this.isDeletedAndCheck(
                label,
                label.name().contains("DELETE")
            );
        }
    }

    // isUnloaded........................................................................................................

    @Test
    public void testIsUnloadedAll() {
        for (BasicSpreadsheetEngineChangesCacheStatusLabel status : this.values()) {
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
            BasicSpreadsheetEngineChangesCacheStatusLabel.UNLOADED,
            true
        );
    }

    @Test
    public void testIsUnloadedWithLoaded() {
        this.isUnloadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.LOADED,
            false
        );
    }

    @Test
    public void testIsUnloadedWithLoadedReferencesRefreshed() {
        this.isUnloadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsUnloadedWithSaved() {
        this.isUnloadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.SAVED,
            false
        );
    }

    @Test
    public void testIsUnloadedWithSavedReferencesRefreshed() {
        this.isUnloadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsUnloadedWithDeleted() {
        this.isUnloadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.DELETED,
            false
        );
    }

    @Test
    public void testIsUnloadedWithDeletedReferencesRefreshed() {
        this.isUnloadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.DELETED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsUnloadedWithReferenceLoading() {
        this.isUnloadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_UNLOADED,
            true
        );
    }

    @Test
    public void testIsUnloadedWithReferenceLoaded() {
        this.isUnloadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_LOADED,
            false
        );
    }

    @Test
    public void testIsUnloadedWithReferenceLoadedReferencesRefreshed() {
        this.isUnloadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsUnloadedWithReferenceSaved() {
        this.isUnloadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_SAVED,
            false
        );
    }

    @Test
    public void testIsUnloadedWithReferenceSavedReferencesRefreshed() {
        this.isUnloadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsUnloadedWithReferenceDeleted() {
        this.isUnloadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_DELETED,
            false
        );
    }

    @Test
    public void testIsUnloadedWithReferenceDeletedReferencesRefreshed() {
        this.isUnloadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_DELETED_REFERENCES_REFRESHED,
            false
        );
    }

    // isLoading........................................................................................................

    @Test
    public void testIsLoading() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> BasicSpreadsheetEngineChangesCacheStatusLabel.UNLOADED.isLoading()
        );
    }

    // isMissingValue....................................................................................................

    @Test
    public void testIsMissingValueAll() {
        for (final BasicSpreadsheetEngineChangesCacheStatusLabel status : this.values()) {
            status.isMissingValue();
        }
    }

    @Test
    public void testIsMissingValueWithUnloading() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.UNLOADED,
            true
        );
    }


    @Test
    public void testIsMissingValueWithLoaded() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.LOADED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithLoadedReferencesRefreshed() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithSaved() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.SAVED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithSavedReferencesRefreshed() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithDeleted() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.DELETED,
            true
        );
    }

    @Test
    public void testIsMissingValueWithDeletedReferencesRefreshed() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.DELETED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsMissingValueWithReferenceUnloaded() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_UNLOADED,
            true
        );
    }

    @Test
    public void testIsMissingValueWithReferenceLoaded() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_LOADED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithReferenceLoadedReferencesRefreshed() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithReferenceSaved() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_SAVED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithReferenceSavedReferencesRefreshed() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsMissingValueWithReferenceDeleted() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_DELETED,
            true
        );
    }

    @Test
    public void testIsMissingValueWithReferenceDeletedReferencesRefreshed() {
        this.isMissingValueAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_DELETED_REFERENCES_REFRESHED,
            true
        );
    }

    // isReference......................................................................................................

    @Test
    public void testIsReferenceWithUnloading() {
        this.isReferenceAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.UNLOADED,
            false
        );
    }

    @Test
    public void testIsReferenceWithLoaded() {
        this.isReferenceAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.LOADED,
            false
        );
    }

    @Test
    public void testIsReferenceWithLoadedReferencesRefreshed() {
        this.isReferenceAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceWithSaved() {
        this.isReferenceAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.SAVED,
            false
        );
    }

    @Test
    public void testIsReferenceWithSavedReferencesRefreshed() {
        this.isReferenceAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceWithDeleted() {
        this.isReferenceAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.DELETED,
            false
        );
    }

    @Test
    public void testIsReferenceWithDeletedReferencesRefreshed() {
        this.isReferenceAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.DELETED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceWithReferenceUnloaded() {
        this.isReferenceAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_UNLOADED,
            true
        );
    }

    @Test
    public void testIsReferenceWithReferenceLoaded() {
        this.isReferenceAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_LOADED,
            true
        );
    }

    @Test
    public void testIsReferenceWithReferenceLoadedReferencesRefreshed() {
        this.isReferenceAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_LOADED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsReferenceWithReferenceSaved() {
        this.isReferenceAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_SAVED,
            true
        );
    }

    @Test
    public void testIsReferenceWithReferenceSavedReferencesRefreshed() {
        this.isReferenceAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_SAVED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsReferenceWithReferenceDeleted() {
        this.isReferenceAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_DELETED,
            true
        );
    }

    @Test
    public void testIsReferenceWithReferenceDeletedReferencesRefreshed() {
        this.isReferenceAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_DELETED_REFERENCES_REFRESHED,
            true
        );
    }

    // isReferencesRefreshed............................................................................................

    @Test
    public void testIsReferencesRefreshedAll() {
        for (BasicSpreadsheetEngineChangesCacheStatusLabel status : this.values()) {
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
            BasicSpreadsheetEngineChangesCacheStatusLabel.UNLOADED,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithLoaded() {
        this.isReferencesRefreshedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.LOADED,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithLoadedReferencesRefreshed() {
        this.isReferencesRefreshedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.LOADED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsReferencesRefreshedWithSaved() {
        this.isReferencesRefreshedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.SAVED,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithSavedReferencesRefreshed() {
        this.isReferencesRefreshedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.SAVED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsReferencesRefreshedWithDeleted() {
        this.isReferencesRefreshedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.DELETED,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithDeletedReferencesRefreshed() {
        this.isReferencesRefreshedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.DELETED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsReferencesRefreshedWithReferenceUnloaded() {
        this.isReferencesRefreshedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_UNLOADED,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithReferenceLoaded() {
        this.isReferencesRefreshedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_LOADED,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithReferenceLoadedReferencesRefreshed() {
        this.isReferencesRefreshedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_LOADED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsReferencesRefreshedWithReferenceSaved() {
        this.isReferencesRefreshedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_SAVED,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithReferenceSavedReferencesRefreshed() {
        this.isReferencesRefreshedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_SAVED_REFERENCES_REFRESHED,
            true
        );
    }

    @Test
    public void testIsReferencesRefreshedWithReferenceDeleted() {
        this.isReferencesRefreshedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_DELETED,
            false
        );
    }

    @Test
    public void testIsReferencesRefreshedWithReferenceDeletedReferencesRefreshed() {
        this.isReferencesRefreshedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_DELETED_REFERENCES_REFRESHED,
            true
        );
    }

    // isRefreshable....................................................................................................

    @Test
    public void testIsRefreshableWithUnloaded() {
        this.isRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.UNLOADED,
            true
        );
    }

    @Test
    public void testIsRefreshableWithLoaded() {
        this.isRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.LOADED,
            true
        );
    }

    @Test
    public void testIsRefreshableWithLoadedReferencesRefreshed() {
        this.isRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsRefreshableWithSaved() {
        this.isRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.SAVED,
            true
        );
    }

    @Test
    public void testIsRefreshableWithSavedReferencesRefreshed() {
        this.isRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsRefreshableWithDeleted() {
        this.isRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.DELETED,
            true
        );
    }

    @Test
    public void testIsRefreshableWithDeletedReferencesRefreshed() {
        this.isRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.DELETED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsRefreshableWithReferenceUnloaded() {
        this.isRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_UNLOADED,
            true
        );
    }

    @Test
    public void testIsRefreshableWithReferenceLoaded() {
        this.isRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_LOADED,
            true
        );
    }

    @Test
    public void testIsRefreshableWithReferenceLoadedReferencesRefreshed() {
        this.isRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsRefreshableWithReferenceSaved() {
        this.isRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_SAVED,
            true
        );
    }

    @Test
    public void testIsRefreshableWithReferenceSavedReferencesRefreshed() {
        this.isRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsRefreshableWithReferenceDeleted() {
        this.isRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_DELETED,
            true
        );
    }

    @Test
    public void testIsRefreshableWithReferenceDeletedReferencesRefreshed() {
        this.isRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_DELETED_REFERENCES_REFRESHED,
            false
        );
    }


    // isReferenceRefreshable....................................................................................................

    @Test
    public void testIsReferenceRefreshableWithUnloaded() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.UNLOADED,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithLoaded() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.LOADED,
            true
        );
    }

    @Test
    public void testIsReferenceRefreshableWithLoadedReferencesRefreshed() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithSaved() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.SAVED,
            true
        );
    }

    @Test
    public void testIsReferenceRefreshableWithSavedReferencesRefreshed() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithDeleted() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.DELETED,
            true
        );
    }

    @Test
    public void testIsReferenceRefreshableWithDeletedReferencesRefreshed() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.DELETED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceUnloaded() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_UNLOADED,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceLoaded() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_LOADED,
            true
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceLoadedReferencesRefreshed() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_LOADED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceSaved() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_SAVED,
            true
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceSavedReferencesRefreshed() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_SAVED_REFERENCES_REFRESHED,
            false
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceDeleted() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_DELETED,
            true
        );
    }

    @Test
    public void testIsReferenceRefreshableWithReferenceDeletedReferencesRefreshed() {
        this.isReferenceRefreshableAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_DELETED_REFERENCES_REFRESHED,
            false
        );
    }

    // deleted..........................................................................................................

    @Test
    public void testDeletedWithUnloaded() {
        this.deletedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.UNLOADED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.DELETED
        );
    }

    @Test
    public void testDeletedWithLoaded() {
        this.deletedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.LOADED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.DELETED
        );
    }

    @Test
    public void testDeletedWithLoadedReferencesRefreshed() {
        this.deletedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.LOADED_REFERENCES_REFRESHED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.DELETED
        );
    }

    @Test
    public void testDeletedWithSaved() {
        this.deletedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.SAVED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.DELETED
        );
    }

    @Test
    public void testDeletedWithSavedReferencesRefreshed() {
        this.deletedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.SAVED_REFERENCES_REFRESHED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.DELETED
        );
    }

    @Test
    public void testDeletedWithDeleted() {
        this.deletedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.DELETED
        );
    }

    @Test
    public void testDeletedWithDeletedReferencesRefreshed() {
        this.deletedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.DELETED_REFERENCES_REFRESHED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.DELETED
        );
    }

    @Test
    public void testDeletedWithReferenceUnloaded() {
        this.deletedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_UNLOADED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_DELETED
        );
    }

    @Test
    public void testDeletedWithReferenceLoaded() {
        this.deletedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_LOADED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_DELETED
        );
    }

    @Test
    public void testDeletedWithReferenceLoadedReferencesRefreshed() {
        this.deletedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_LOADED_REFERENCES_REFRESHED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_DELETED
        );
    }

    @Test
    public void testDeletedWithReferenceSaved() {
        this.deletedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_SAVED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_DELETED
        );
    }

    @Test
    public void testDeletedWithReferenceSavedReferencesRefreshed() {
        this.deletedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_SAVED_REFERENCES_REFRESHED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_DELETED
        );
    }

    @Test
    public void testDeletedWithReferenceDeleted() {
        this.deletedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_DELETED
        );
    }

    @Test
    public void testDeletedWithReferenceDeletedReferencesRefreshed() {
        this.deletedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_DELETED_REFERENCES_REFRESHED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_DELETED
        );
    }

    // loaded...........................................................................................................

    @Test
    public void testLoadedWithUnloaded() {
        this.loadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.UNLOADED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.LOADED
        );
    }

    @Test
    public void testLoadedWithLoaded() {
        this.loadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.LOADED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.LOADED
        );
    }

    @Test
    public void testLoadedWithLoadedReferencesRefreshed() {
        this.loadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.LOADED_REFERENCES_REFRESHED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.LOADED
        );
    }

    @Test
    public void testLoadedWithSaved() {
        this.loadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.SAVED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.LOADED
        );
    }

    @Test
    public void testLoadedWithSavedReferencesRefreshed() {
        this.loadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.SAVED_REFERENCES_REFRESHED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.LOADED
        );
    }

    @Test
    public void testLoadedWithDeleted() {
        this.loadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.DELETED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.LOADED
        );
    }

    @Test
    public void testLoadedWithDeletedReferencesRefreshed() {
        this.loadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.DELETED_REFERENCES_REFRESHED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.LOADED
        );
    }

    @Test
    public void testLoadedWithReferenceUnloaded() {
        this.loadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_UNLOADED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_LOADED
        );
    }

    @Test
    public void testLoadedWithReferenceLoaded() {
        this.loadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_LOADED
        );
    }

    @Test
    public void testLoadedWithReferenceLoadedReferencesRefreshed() {
        this.loadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_LOADED_REFERENCES_REFRESHED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_LOADED
        );
    }

    @Test
    public void testLoadedWithReferenceSaved() {
        this.loadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_SAVED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_LOADED
        );
    }

    @Test
    public void testLoadedWithReferenceSavedReferencesRefreshed() {
        this.loadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_SAVED_REFERENCES_REFRESHED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_LOADED
        );
    }

    @Test
    public void testLoadedWithReferenceDeleted() {
        this.loadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_LOADED
        );
    }

    @Test
    public void testLoadedWithReferenceDeletedReferencesRefreshed() {
        this.loadedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_DELETED_REFERENCES_REFRESHED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_LOADED
        );
    }

    // loaded...........................................................................................................

    @Test
    public void testSavedWithUnloaded() {
        this.savedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.UNLOADED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.SAVED
        );
    }

    @Test
    public void testSavedWithLoaded() {
        this.savedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.LOADED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.SAVED
        );
    }

    @Test
    public void testSavedWithLoadedReferencesRefreshed() {
        this.savedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.LOADED_REFERENCES_REFRESHED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.SAVED
        );
    }

    @Test
    public void testSavedWithSaved() {
        this.savedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.SAVED
        );
    }

    @Test
    public void testSavedWithSavedReferencesRefreshed() {
        this.savedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.SAVED_REFERENCES_REFRESHED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.SAVED
        );
    }

    @Test
    public void testSavedWithDeleted() {
        this.savedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.DELETED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.SAVED
        );
    }

    @Test
    public void testSavedWithDeletedReferencesRefreshed() {
        this.savedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.DELETED_REFERENCES_REFRESHED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.SAVED
        );
    }

    @Test
    public void testSavedWithReferenceUnloaded() {
        this.savedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_UNLOADED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_SAVED
        );
    }

    @Test
    public void testSavedWithReferenceLoaded() {
        this.savedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_LOADED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_SAVED
        );
    }

    @Test
    public void testSavedWithReferenceLoadedReferencesRefreshed() {
        this.savedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_LOADED_REFERENCES_REFRESHED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_SAVED
        );
    }

    @Test
    public void testSavedWithReferenceSaved() {
        this.savedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_SAVED
        );
    }

    @Test
    public void testSavedWithReferenceSavedReferencesRefreshed() {
        this.savedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_SAVED_REFERENCES_REFRESHED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_SAVED
        );
    }

    @Test
    public void testSavedWithReferenceDeleted() {
        this.savedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_DELETED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_SAVED
        );
    }

    @Test
    public void testSavedWithReferenceDeletedReferencesRefreshed() {
        this.savedAndCheck(
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_DELETED_REFERENCES_REFRESHED,
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_SAVED
        );
    }

    // forceReferencesRefresh...........................................................................................

    @Test
    public void testForceReferencesRefreshAll() {
        Arrays.stream(this.values())
            .forEach(BasicSpreadsheetEngineChangesCacheStatus::forceReferencesRefresh);
    }

    @Override
    BasicSpreadsheetEngineChangesCacheStatusLabel[] values() {
        return BasicSpreadsheetEngineChangesCacheStatusLabel.values();
    }

    // class............................................................................................................

    @Override
    public Class<BasicSpreadsheetEngineChangesCacheStatusLabel> type() {
        return BasicSpreadsheetEngineChangesCacheStatusLabel.class;
    }
}

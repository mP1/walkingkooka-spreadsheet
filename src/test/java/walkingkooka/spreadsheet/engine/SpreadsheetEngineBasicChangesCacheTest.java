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
import walkingkooka.Cast;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionMaps;
import walkingkooka.spreadsheet.value.SpreadsheetCell;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class SpreadsheetEngineBasicChangesCacheTest implements ClassTesting<SpreadsheetEngineBasicChangesCache<?, ?>> {

    // cell............................................................................................................

    @Test
    public void testCellLoadedThenRefreshReferences() {
        final SpreadsheetCellReference cellReference = SpreadsheetSelection.A1;

        final SpreadsheetEngineBasicChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = SpreadsheetEngineBasicChangesCache.getOrCreate(
            cellReference,
            SpreadsheetSelectionMaps.cell(),
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADING
        );

        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY);
        cache.loading(cell);
        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADING
        );

        cache.loaded(cell);
        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADED
        );

        this.isRefreshableAndCheck(
            cache,
            true
        );

        cache.referencesRefreshed();

        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADED_REFERENCES_REFRESHED
        );
    }

    @Test
    public void testCellMissingThenRefreshReferences() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;

        final SpreadsheetEngineBasicChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = SpreadsheetEngineBasicChangesCache.getOrCreate(
            cell,
            SpreadsheetSelectionMaps.cell(),
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADING
        );

        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusCell.LOADING
        );

        cache.deleted();

        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusCell.DELETED
        );

        this.isRefreshableAndCheck(
            cache,
            true
        );

        cache.referencesRefreshed();

        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusCell.DELETED_REFERENCES_REFRESHED
        );
    }

    @Test
    public void testCellSavedThenRefreshReferences() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;

        final SpreadsheetEngineBasicChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = SpreadsheetEngineBasicChangesCache.getOrCreate(
            cell,
            SpreadsheetSelectionMaps.cell(),
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVING
        );

        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVING
        );

        cache.saved(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
        );
        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVED
        );

        this.isRefreshableAndCheck(
            cache,
            true
        );

        cache.referencesRefreshed();

        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVED_REFERENCES_REFRESHED
        );
    }

    @Test
    public void testCellReferenceLoadedThenRefreshReferences() {
        final SpreadsheetCellReference cellReference = SpreadsheetSelection.A1;

        final SpreadsheetEngineBasicChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = SpreadsheetEngineBasicChangesCache.getOrCreate(
            cellReference,
            SpreadsheetSelectionMaps.cell(),
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADED
        );

        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY);
        cache.loading(cell);

        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADING
        );

        cache.loaded(cell);
        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADED
        );

        this.isRefreshableAndCheck(
            cache,
            true
        );

        cache.referencesRefreshed();

        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADED_REFERENCES_REFRESHED
        );
    }

    @Test
    public void testCellReferenceMissingThenRefreshReferences() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;

        final SpreadsheetEngineBasicChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = SpreadsheetEngineBasicChangesCache.getOrCreate(
            cell,
            SpreadsheetSelectionMaps.cell(),
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADED
        );

        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_LOADED
        );

        cache.deleted();

        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_DELETED
        );

        this.isRefreshableAndCheck(
            cache,
            true
        );

        cache.referencesRefreshed();

        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_DELETED_REFERENCES_REFRESHED
        );
    }

    @Test
    public void testCellReferenceSavedThenRefreshReferences() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;

        final SpreadsheetEngineBasicChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = SpreadsheetEngineBasicChangesCache.getOrCreate(
            cell,
            SpreadsheetSelectionMaps.cell(),
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_SAVING
        );

        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_SAVING
        );

        cache.saved(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
        );
        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_SAVED
        );

        this.isRefreshableAndCheck(
            cache,
            true
        );

        cache.referencesRefreshed();

        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_SAVED_REFERENCES_REFRESHED
        );
    }

    // label............................................................................................................

    @Test
    public void testLabelLoadedThenRefreshReferences() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("DifferentLabel111");

        final SpreadsheetEngineBasicChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache = SpreadsheetEngineBasicChangesCache.getOrCreate(
            label,
            SpreadsheetSelectionMaps.label(),
            SpreadsheetEngineBasicChangesCacheStatusLabel.UNLOADED
        );

        final SpreadsheetLabelMapping mapping = label.setLabelMappingReference(SpreadsheetSelection.A1);

        cache.loaded(mapping);
        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusLabel.LOADED
        );

        this.isRefreshableAndCheck(
            cache,
            true
        );

        cache.referencesRefreshed();

        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusLabel.LOADED_REFERENCES_REFRESHED
        );
    }

    @Test
    public void testLabelMissingThenRefreshReferences() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("DifferentLabel111");

        final SpreadsheetEngineBasicChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache = SpreadsheetEngineBasicChangesCache.getOrCreate(
            label,
            SpreadsheetSelectionMaps.label(),
            SpreadsheetEngineBasicChangesCacheStatusLabel.UNLOADED
        );

        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusLabel.UNLOADED
        );

        cache.deleted();

        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusLabel.DELETED
        );

        this.isRefreshableAndCheck(
            cache,
            true
        );

        cache.referencesRefreshed();

        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusLabel.DELETED_REFERENCES_REFRESHED
        );
    }

    @Test
    public void testLabelSavedThenRefreshReferences() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("DifferentLabel111");

        final SpreadsheetEngineBasicChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache = SpreadsheetEngineBasicChangesCache.getOrCreate(
            label,
            SpreadsheetSelectionMaps.label(),
            SpreadsheetEngineBasicChangesCacheStatusLabel.SAVED
        );

        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusLabel.SAVED
        );

        cache.saved(
            label.setLabelMappingReference(SpreadsheetSelection.A1)
        );
        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusLabel.SAVED
        );

        this.isRefreshableAndCheck(
            cache,
            true
        );

        cache.referencesRefreshed();

        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusLabel.SAVED_REFERENCES_REFRESHED
        );
    }

    @Test
    public void testLabelReferenceLoadedThenRefreshReferences() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("DifferentLabel111");

        final SpreadsheetEngineBasicChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache = SpreadsheetEngineBasicChangesCache.getOrCreate(
            label,
            SpreadsheetSelectionMaps.label(),
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_UNLOADED
        );

        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_UNLOADED
        );

        final SpreadsheetLabelMapping mapping = label.setLabelMappingReference(SpreadsheetSelection.A1);

        cache.loaded(mapping);
        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_LOADED
        );

        this.isRefreshableAndCheck(
            cache,
            true
        );

        cache.referencesRefreshed();

        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_LOADED_REFERENCES_REFRESHED
        );
    }

    @Test
    public void testLabelReferenceMissingThenRefreshReferences() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("DifferentLabel111");

        final SpreadsheetEngineBasicChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache = SpreadsheetEngineBasicChangesCache.getOrCreate(
            label,
            SpreadsheetSelectionMaps.label(),
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_LOADED
        );

        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_LOADED
        );

        cache.deleted();

        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_DELETED
        );

        this.isRefreshableAndCheck(
            cache,
            true
        );

        cache.referencesRefreshed();

        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_DELETED_REFERENCES_REFRESHED
        );
    }

    @Test
    public void testLabelReferenceSavedThenRefreshReferences() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("DifferentLabel111");

        final SpreadsheetEngineBasicChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache = SpreadsheetEngineBasicChangesCache.getOrCreate(
            label,
            SpreadsheetSelectionMaps.label(),
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_SAVED
        );

        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_SAVED
        );

        cache.saved(
            label.setLabelMappingReference(SpreadsheetSelection.A1)
        );
        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_SAVED
        );

        this.isRefreshableAndCheck(
            cache,
            true
        );

        cache.referencesRefreshed();

        this.statusAndCheck(
            cache,
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_SAVED_REFERENCES_REFRESHED
        );
    }

    private void isRefreshableAndCheck(final SpreadsheetEngineBasicChangesCache<?, ?> cache,
                                       final boolean expected) {
        this.checkEquals(
            expected,
            cache.status().isRefreshable(),
            cache::toString
        );
    }

    private <S extends SpreadsheetSelection> void statusAndCheck(final SpreadsheetEngineBasicChangesCache<S, ?> cache,
                                                                 final SpreadsheetEngineBasicChangesCacheStatus<S> expected) {
        assertSame(
            expected,
            cache.status()
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetEngineBasicChangesCache<?, ?>> type() {
        return Cast.to(SpreadsheetEngineBasicChangesCache.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}

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
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionMaps;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class BasicSpreadsheetEngineChangesCacheTest implements ClassTesting<BasicSpreadsheetEngineChangesCache<?, ?>> {

    // cell............................................................................................................

    @Test
    public void testCellLoadedThenRefreshReferences() {
        final SpreadsheetCellReference cellReference = SpreadsheetSelection.A1;

        final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = BasicSpreadsheetEngineChangesCache.getOrCreate(
            cellReference,
            SpreadsheetSelectionMaps.cell(),
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADING
        );

        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY);
        cache.loading(cell);
        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADING
        );

        cache.loaded(cell);
        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADED
        );

        this.isRefreshableAndCheck(
            cache,
            true
        );

        cache.referencesRefreshed();

        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADED_REFERENCES_REFRESHED
        );
    }

    @Test
    public void testCellMissingThenRefreshReferences() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;

        final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = BasicSpreadsheetEngineChangesCache.getOrCreate(
            cell,
            SpreadsheetSelectionMaps.cell(),
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADING
        );

        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusCell.LOADING
        );

        cache.deleted();

        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusCell.DELETED
        );

        this.isRefreshableAndCheck(
            cache,
            true
        );

        cache.referencesRefreshed();

        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusCell.DELETED_REFERENCES_REFRESHED
        );
    }

    @Test
    public void testCellSavedThenRefreshReferences() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;

        final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = BasicSpreadsheetEngineChangesCache.getOrCreate(
            cell,
            SpreadsheetSelectionMaps.cell(),
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVING
        );

        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVING
        );

        cache.saved(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
        );
        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVED
        );

        this.isRefreshableAndCheck(
            cache,
            true
        );

        cache.referencesRefreshed();

        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVED_REFERENCES_REFRESHED
        );
    }

    @Test
    public void testCellReferenceLoadedThenRefreshReferences() {
        final SpreadsheetCellReference cellReference = SpreadsheetSelection.A1;

        final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = BasicSpreadsheetEngineChangesCache.getOrCreate(
            cellReference,
            SpreadsheetSelectionMaps.cell(),
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADED
        );

        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY);
        cache.loading(cell);

        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADING
        );

        cache.loaded(cell);
        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADED
        );

        this.isRefreshableAndCheck(
            cache,
            true
        );

        cache.referencesRefreshed();

        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADED_REFERENCES_REFRESHED
        );
    }

    @Test
    public void testCellReferenceMissingThenRefreshReferences() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;

        final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = BasicSpreadsheetEngineChangesCache.getOrCreate(
            cell,
            SpreadsheetSelectionMaps.cell(),
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADED
        );

        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_LOADED
        );

        cache.deleted();

        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_DELETED
        );

        this.isRefreshableAndCheck(
            cache,
            true
        );

        cache.referencesRefreshed();

        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_DELETED_REFERENCES_REFRESHED
        );
    }

    @Test
    public void testCellReferenceSavedThenRefreshReferences() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;

        final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = BasicSpreadsheetEngineChangesCache.getOrCreate(
            cell,
            SpreadsheetSelectionMaps.cell(),
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_SAVING
        );

        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_SAVING
        );

        cache.saved(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
        );
        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_SAVED
        );

        this.isRefreshableAndCheck(
            cache,
            true
        );

        cache.referencesRefreshed();

        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_SAVED_REFERENCES_REFRESHED
        );
    }

    // label............................................................................................................

    @Test
    public void testLabelLoadedThenRefreshReferences() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("DifferentLabel111");

        final BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache = BasicSpreadsheetEngineChangesCache.getOrCreate(
            label,
            SpreadsheetSelectionMaps.label(),
            BasicSpreadsheetEngineChangesCacheStatusLabel.UNLOADED
        );

        final SpreadsheetLabelMapping mapping = label.setLabelMappingReference(SpreadsheetSelection.A1);

        cache.loaded(mapping);
        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusLabel.LOADED
        );

        this.isRefreshableAndCheck(
            cache,
            true
        );

        cache.referencesRefreshed();

        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusLabel.LOADED_REFERENCES_REFRESHED
        );
    }

    @Test
    public void testLabelMissingThenRefreshReferences() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("DifferentLabel111");

        final BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache = BasicSpreadsheetEngineChangesCache.getOrCreate(
            label,
            SpreadsheetSelectionMaps.label(),
            BasicSpreadsheetEngineChangesCacheStatusLabel.UNLOADED
        );

        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusLabel.UNLOADED
        );

        cache.deleted();

        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusLabel.DELETED
        );

        this.isRefreshableAndCheck(
            cache,
            true
        );

        cache.referencesRefreshed();

        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusLabel.DELETED_REFERENCES_REFRESHED
        );
    }

    @Test
    public void testLabelSavedThenRefreshReferences() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("DifferentLabel111");

        final BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache = BasicSpreadsheetEngineChangesCache.getOrCreate(
            label,
            SpreadsheetSelectionMaps.label(),
            BasicSpreadsheetEngineChangesCacheStatusLabel.SAVED
        );

        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusLabel.SAVED
        );

        cache.saved(
            label.setLabelMappingReference(SpreadsheetSelection.A1)
        );
        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusLabel.SAVED
        );

        this.isRefreshableAndCheck(
            cache,
            true
        );

        cache.referencesRefreshed();

        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusLabel.SAVED_REFERENCES_REFRESHED
        );
    }

    @Test
    public void testLabelReferenceLoadedThenRefreshReferences() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("DifferentLabel111");

        final BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache = BasicSpreadsheetEngineChangesCache.getOrCreate(
            label,
            SpreadsheetSelectionMaps.label(),
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_UNLOADED
        );

        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_UNLOADED
        );

        final SpreadsheetLabelMapping mapping = label.setLabelMappingReference(SpreadsheetSelection.A1);

        cache.loaded(mapping);
        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_LOADED
        );

        this.isRefreshableAndCheck(
            cache,
            true
        );

        cache.referencesRefreshed();

        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_LOADED_REFERENCES_REFRESHED
        );
    }

    @Test
    public void testLabelReferenceMissingThenRefreshReferences() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("DifferentLabel111");

        final BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache = BasicSpreadsheetEngineChangesCache.getOrCreate(
            label,
            SpreadsheetSelectionMaps.label(),
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_LOADED
        );

        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_LOADED
        );

        cache.deleted();

        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_DELETED
        );

        this.isRefreshableAndCheck(
            cache,
            true
        );

        cache.referencesRefreshed();

        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_DELETED_REFERENCES_REFRESHED
        );
    }

    @Test
    public void testLabelReferenceSavedThenRefreshReferences() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("DifferentLabel111");

        final BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache = BasicSpreadsheetEngineChangesCache.getOrCreate(
            label,
            SpreadsheetSelectionMaps.label(),
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_SAVED
        );

        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_SAVED
        );

        cache.saved(
            label.setLabelMappingReference(SpreadsheetSelection.A1)
        );
        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_SAVED
        );

        this.isRefreshableAndCheck(
            cache,
            true
        );

        cache.referencesRefreshed();

        this.statusAndCheck(
            cache,
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_SAVED_REFERENCES_REFRESHED
        );
    }

    private void isRefreshableAndCheck(final BasicSpreadsheetEngineChangesCache<?, ?> cache,
                                       final boolean expected) {
        this.checkEquals(
            expected,
            cache.status().isRefreshable(),
            cache::toString
        );
    }

    private <S extends SpreadsheetSelection> void statusAndCheck(final BasicSpreadsheetEngineChangesCache<S, ?> cache,
                                                                 final BasicSpreadsheetEngineChangesCacheStatus<S> expected) {
        assertSame(
            expected,
            cache.status()
        );
    }

    // class............................................................................................................

    @Override
    public Class<BasicSpreadsheetEngineChangesCache<?, ?>> type() {
        return Cast.to(BasicSpreadsheetEngineChangesCache.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}

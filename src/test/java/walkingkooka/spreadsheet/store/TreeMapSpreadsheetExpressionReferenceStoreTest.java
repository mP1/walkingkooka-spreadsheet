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

package walkingkooka.spreadsheet.store;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class TreeMapSpreadsheetExpressionReferenceStoreTest extends SpreadsheetExpressionReferenceStoreTestCase<TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference>, SpreadsheetCellReference> {

    @Override
    public void testAddSaveWatcherAndSave() {
    }

    @Override
    public void testAddSaveWatcherAndSaveTwiceFiresOnce() {
    }

    // save.............................................................................................................

    @Test
    public void testSave() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();

        store.saveCells(
            a1,
            Sets.of(b1)
        );

        this.referenceToCellsAndCheck(
            store,
            Maps.of(
                a1,
                Sets.of(b1)
            )
        );
        this.cellToReferencesAndCheck(
            store,
            Maps.of(
                b1,
                Sets.of(a1)
            )
        );
    }

    @Test
    public void testSave2() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();

        store.saveCells(
            a1,
            Sets.of(b1, c1)
        );

        this.referenceToCellsAndCheck(
            store,
            Maps.of(
                a1,
                Sets.of(
                    b1,
                    c1
                )
            )
        );
        this.cellToReferencesAndCheck(
            store,
            Maps.of(
                b1,
                Sets.of(a1),
                c1,
                Sets.of(a1)
            )
        );
    }

    @Test
    public void testSave3() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();

        store.saveCells(
            a1,
            Sets.of(
                b1,
                c1
            )
        );

        this.referenceToCellsAndCheck(
            store,
            Maps.of(
                a1,
                Sets.of(
                    b1,
                    c1
                )
            )
        );
        this.cellToReferencesAndCheck(
            store,
            Maps.of(
                b1,
                Sets.of(a1),
                c1,
                Sets.of(a1)
            )
        );
    }

    @Test
    public void testSaveCellSelfReference() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;

        store.saveCells(
            a1,
            Sets.of(a1)
        );

        this.referenceToCellsAndCheck(
            store,
            Maps.of(
                a1,
                Sets.of(a1)
            )
        );
        this.cellToReferencesAndCheck(
            store,
            Maps.of(
                a1,
                Sets.of(a1)
            )
        );
    }

    @Test
    public void testSave3Absolute() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1.toAbsolute();
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();

        store.saveCells(
            a1,
            Sets.of(
                b1.toAbsolute(),
                c1.toAbsolute()
            )
        );

        this.referenceToCellsAndCheck(
            store,
            Maps.of(
                a1,
                Sets.of(
                    b1,
                    c1
                )
            )
        );
        this.cellToReferencesAndCheck(
            store,
            Maps.of(
                b1.toRelative(),
                Sets.of(
                    a1.toRelative()
                ),
                c1.toRelative(),
                Sets.of(
                    a1.toRelative()
                )
            )
        );
    }

    @Test
    public void testSaveAndLoad() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();

        store.saveCells(
            a1,
            Sets.of(b1)
        );

        this.loadAndCheck(
            store,
            a1,
            b1
        ); // a1 --> b1
        this.findReferencesWithCellAndCheck(
            store,
            b1,
            0, // offset
            2, // count
            a1
        ); // b1 --> a1

        this.loadAndCheck(
            store,
            b1
        ); // b1 -> nothing
        this.findReferencesWithCellAndCheck(
            store,
            a1,
            0, // offset
            1 // count
        ); // a1 --> nothing
    }

    @Test
    public void testSaveAndLoad2() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();
        store.saveCells(
            a1,
            Sets.of(b1)
        );

        final SpreadsheetCellReference c1 = this.c1();
        store.saveCells(
            b1,
            Sets.of(c1)
        );

        this.loadAndCheck(
            store,
            a1,
            b1
        );
        this.loadAndCheck(
            store,
            b1,
            c1
        );
        this.loadFailCheck(
            store,
            c1
        );

        this.findReferencesWithCellAndCheck(
            store,
            b1,
            0, // offset
            2, // count
            a1
        );
        this.findReferencesWithCellAndCheck(
            store,
            c1,
            0, // offset
            2, // count
            b1
        );
    }

    @Test
    public void testSaveAndLoad3() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();

        store.saveCells(
            a1,
            Sets.of(b1, c1)
        );
        store.saveCells(
            b1,
            Sets.of(c1)
        );

        this.loadAndCheck(
            store,
            a1, b1, c1
        );
        this.loadAndCheck(
            store,
            b1, c1
        );
        this.loadFailCheck(
            store,
            c1
        );

        this.findReferencesWithCellAndCheck(
            store,
            b1,
            0, // offset
            2, // count
            a1
        );
        this.findReferencesWithCellAndCheck(
            store,
            c1,
            0, // offset
            3, // count
            a1, b1
        );
    }

    @Test
    public void testSaveAndLoadDifferentReferenceKind() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();

        store.saveCells(
            a1,
            Sets.of(b1, c1)
        );
        store.saveCells(
            b1,
            Sets.of(c1)
        );

        final SpreadsheetCellReference absoluteB1 = b1.toAbsolute();
        this.checkNotEquals(b1, absoluteB1, "expected same cell but relative/absolute");
        this.checkEquals(
            true,
            absoluteB1.equalsIgnoreReferenceKind(b1),
            () -> absoluteB1 + " " + b1
        );

        this.loadAndCheck(
            store,
            a1, b1, c1
        );
        this.loadAndCheck(
            store,
            b1, c1
        );
        this.loadFailCheck(
            store,
            c1
        );

        this.findReferencesWithCellAndCheck(
            store,
            b1,
            0, // offset
            2, // count
            a1
        );
        this.findReferencesWithCellAndCheck(
            store,
            absoluteB1,
            0, // offset
            2, // count
            a1
        );
        this.findReferencesWithCellAndCheck(
            store,
            c1,
            0, // offset
            2, // count
            a1, b1
        );
    }

    @Test
    public void testSaveAndLoadDifferentReferenceKind2() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();

        store.saveCells(
            a1,
            Sets.of(
                b1,
                c1
            )
        );
        store.saveCells(
            b1,
            Sets.of(c1)
        );

        final SpreadsheetCellReference absoluteB1 = b1.toAbsolute();
        this.checkNotEquals(b1, absoluteB1, "expected same cell but relative/absolute");
        this.checkEquals(
            true,
            absoluteB1.equalsIgnoreReferenceKind(b1),
            () -> absoluteB1 + " " + b1
        );

        this.loadAndCheck(
            store,
            a1.toAbsolute(),
            b1, c1
        );
        this.loadAndCheck(
            store,
            b1.toAbsolute(),
            c1
        );
        this.loadFailCheck(
            store,
            c1.toAbsolute()
        );

        this.findReferencesWithCellAndCheck(
            store,
            b1,
            0, // offset
            2, // count
            a1
        );
        this.findReferencesWithCellAndCheck(
            store,
            absoluteB1,
            0, // offset
            1, // count
            a1
        );
        this.findReferencesWithCellAndCheck(
            store,
            c1,
            0, // offset
            3, // count
            a1, b1
        );
    }

    // delete...........................................................................................................

    @Test
    public void testDeleteAfterSaveCells() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();

        store.saveCells(
            a1,
            Sets.of(b1)
        );

        store.delete(a1);

        this.cellToReferencesAndCheck(
            store,
            Maps.empty()
        );
        this.referenceToCellsAndCheck(
            store,
            Maps.of(
                a1,
                Sets.of(b1)
            )
        );
    }

    @Test
    public void testDeleteAfterSaveCells2() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        final SpreadsheetCellReference d1 = this.d1();

        store.saveCells(
            a1,
            Sets.of(b1)
        );
        store.saveCells(
            c1,
            Sets.of(d1)
        );

        store.delete(a1);

        this.cellToReferencesAndCheck(
            store,
            Maps.of(
                d1,
                Sets.of(c1)
            )
        );
        this.referenceToCellsAndCheck(
            store,
            Maps.of(
                a1,
                Sets.of(b1),
                c1,
                Sets.of(d1)
            )
        );
    }

    @Test
    public void testDeleteAfterSaveCellsAbsolute() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();

        store.saveCells(
            a1,
            Sets.of(b1)
        );

        store.delete(a1.toAbsolute());

        this.cellToReferencesAndCheck(
            store,
            Maps.empty()
        );
        this.referenceToCellsAndCheck(
            store,
            Maps.of(
                a1,
                Sets.of(b1)
            )
        );
    }

    @Test
    public void testDeleteAfterSaveLabels() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetLabelName> store = TreeMapSpreadsheetExpressionReferenceStore.create();

        final SpreadsheetLabelName label1 = SpreadsheetSelection.labelName("Label1");

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;

        store.saveCells(
            label1,
            Sets.of(a1)
        );

        store.delete(label1);

        this.cellToReferencesAndCheck(
            store,
            Maps.empty()
        );
        this.referenceToCellsAndCheck(
            store,
            Maps.of(
                label1,
                Sets.of(a1)
            )
        );
    }

    // ids..............................................................................................................

    @Test
    public final void testIds() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b = this.b1();
        final SpreadsheetCellReference c = this.c1();
        final SpreadsheetCellReference f = this.f99();

        store.saveCells(a, Sets.of(f));
        store.saveCells(b, Sets.of(f));
        store.saveCells(c, Sets.of(f));

        this.idsAndCheck(
            store,
            0, // from
            3, // to
            a, b, c // expected
        );
    }

    @Test
    public final void testIdsAbsolute() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a = SpreadsheetSelection.A1.toAbsolute();
        final SpreadsheetCellReference b = this.b1().toAbsolute();
        final SpreadsheetCellReference c = this.c1().toAbsolute();
        final SpreadsheetCellReference f = this.f99().toAbsolute();

        store.saveCells(a, Sets.of(f));
        store.saveCells(b, Sets.of(f));
        store.saveCells(c, Sets.of(f));

        this.idsAndCheck(
            store,
            0, // from
            3, // to
            a.toRelative(), b.toRelative(), c.toRelative() // expected
        );
    }

    @Test
    public final void testIdsWindow() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b = this.b1();
        final SpreadsheetCellReference c = this.c1();
        final SpreadsheetCellReference d = this.d1();

        final SpreadsheetCellReference f = this.f99();
        final SpreadsheetCellReference g = this.g99();
        final SpreadsheetCellReference h = this.h99();
        final SpreadsheetCellReference i = this.i99();

        store.saveCells(a, Sets.of(f));
        store.saveCells(b, Sets.of(g));
        store.saveCells(c, Sets.of(h));
        store.saveCells(d, Sets.of(i));

        this.idsAndCheck(
            store,
            1, // from
            2, // to
            b, c // expected
        );
    }

    @Test
    public void testIdsWithoutCells() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();

        store.saveCells(a1, Sets.of(b1));

        this.loadAndCheck(store, a1, b1); // a1 --> b1
        this.findReferencesWithCellAndCheck(
            store,
            b1,
            0, // offset
            2, // count
            a1
        ); // b1 --> a1

        this.loadAndCheck(store, b1); // b1 -> nothing
        this.findReferencesWithCellAndCheck(
            store,
            a1,
            0, // offset
            1 // count
        ); // a1 --> nothing

        this.idsAndCheck(store, 0, 2, a1); // b1 doesnt exist because it has 0 references to it
    }

    // values...........................................................................................................

    @Test
    public final void testValues() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b = this.b1();
        final SpreadsheetCellReference c = this.c1();

        final Set<SpreadsheetCellReference> f = Sets.of(this.f99());
        final Set<SpreadsheetCellReference> g = Sets.of(this.g99());
        final Set<SpreadsheetCellReference> hi = Sets.of(this.h99(), this.i99());

        store.saveCells(a, f);
        store.saveCells(b, g);
        store.saveCells(c, hi);

        //noinspection unchecked
        this.valuesAndCheck(
            store,
            0,
            3,
            f,
            g,
            hi
        );
    }

    @Test
    public final void testValuesAbsolute() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a = SpreadsheetSelection.A1.toAbsolute();
        final SpreadsheetCellReference b = this.b1().toAbsolute();
        final SpreadsheetCellReference c = this.c1().toAbsolute();

        final Set<SpreadsheetCellReference> f = Sets.of(this.f99());
        final Set<SpreadsheetCellReference> g = Sets.of(this.g99());
        final Set<SpreadsheetCellReference> hi = Sets.of(this.h99(), this.i99());

        store.saveCells(a, f);
        store.saveCells(b, g);
        store.saveCells(c, hi);

        //noinspection unchecked
        this.valuesAndCheck(
            store,
            0,
            3,
            f,
            g,
            hi
        );
    }

    @Test
    public final void testValuesWindow() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b = this.b1();
        final SpreadsheetCellReference c = this.c1();
        final SpreadsheetCellReference d = this.d1();

        final Set<SpreadsheetCellReference> f = Sets.of(this.f99());
        final Set<SpreadsheetCellReference> g = Sets.of(this.g99());
        final Set<SpreadsheetCellReference> h = Sets.of(this.h99());
        final Set<SpreadsheetCellReference> i = Sets.of(this.i99());

        store.saveCells(a, f);
        store.saveCells(b, g);
        store.saveCells(c, h);
        store.saveCells(d, i);

        //noinspection unchecked
        this.valuesAndCheck(
            store,
            1,
            2,
            g,
            h
        );
    }

    @Test
    public void testValuesWithoutCells() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();

        store.saveCells(a1, Sets.of(b1));

        this.loadAndCheck(store, a1, b1); // a1 --> b1
        this.findReferencesWithCellAndCheck(
            store,
            b1,
            0, // offset
            2, // count
            a1
        ); // b1 --> a1

        this.loadAndCheck(store, b1); // b1 -> nothing
        this.findReferencesWithCellAndCheck(
            store,
            a1,
            0, // offset
            2 // count
        ); // a1 --> nothing

        //noinspection unchecked
        this.valuesAndCheck(
            store,
            0,
            2,
            Sets.of(b1)
        );
    }

    // saveCells .......................................................................................................

    @Test
    @Override
    public void testSaveCells() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();
        store.saveCells(
            a1,
            Sets.of(b1)
        );

        this.cellToReferencesAndCheck(
            store,
            Maps.of(
                b1,
                Sets.of(a1)
            )
        );
        this.referenceToCellsAndCheck(
            store,
            Maps.of(
                a1,
                Sets.of(b1)
            )
        );
    }

    @Test
    public void testSaveCellsAbsolute() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1.toAbsolute();
        final SpreadsheetCellReference b1 = this.b1();
        store.saveCells(
            a1,
            Sets.of(b1)
        );

        this.cellToReferencesAndCheck(
            store,
            Maps.of(
                b1.toRelative(),
                Sets.of(
                    a1.toRelative()
                )
            )
        );
        this.referenceToCellsAndCheck(
            store,
            Maps.of(
                a1.toRelative(),
                Sets.of(
                    b1.toRelative()
                )
            )
        );
    }

    @Test
    public void testSaveCellsReplaceSome() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();
        store.saveCells(a1, Sets.of(b1));

        final SpreadsheetCellReference c1 = this.c1();
        store.saveCells(
            a1,
            Sets.of(c1)
        );

        this.cellToReferencesAndCheck(
            store,
            Maps.of(
                c1,
                Sets.of(a1)
            )
        );
        this.referenceToCellsAndCheck(
            store,
            Maps.of(
                a1,
                Sets.of(c1)
            )
        );
    }

    @Test
    public void testSaveCellsReplaceAll() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();
        store.saveCells(a1, Sets.of(b1));

        final SpreadsheetCellReference c1 = this.c1();
        final SpreadsheetCellReference d1 = this.d1();
        store.saveCells(
            c1,
            Sets.of(d1)
        );

        final SpreadsheetCellReference e1 = this.b1();
        store.saveCells(
            a1,
            Sets.of(e1)
        );

        this.cellToReferencesAndCheck(
            store,
            Maps.of(
                d1,
                Sets.of(c1),
                e1,
                Sets.of(a1)
            )
        );
        this.referenceToCellsAndCheck(
            store,
            Maps.of(
                a1,
                Sets.of(e1),
                c1,
                Sets.of(d1)
            )
        );
    }

    @Test
    public void testSaveCellsReplaceSomeAddCellWatcherRemoveCellWatcher() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();

        store.saveCells(
            a1,
            Sets.of(
                b1,
                c1
            )
        );

        final SpreadsheetCellReference d1 = this.d1();

        final List<ReferenceAndSpreadsheetCellReference<SpreadsheetCellReference>> add = Lists.array();
        store.addAddCellWatcher(add::add);

        final List<ReferenceAndSpreadsheetCellReference<SpreadsheetCellReference>> remove = Lists.array();
        store.addRemoveCellWatcher(remove::add);

        store.saveCells(a1, Sets.of(c1, d1));

        this.checkEquals(
            Lists.of(ReferenceAndSpreadsheetCellReference.with(a1, d1)),
            add,
            "add"
        );
        this.checkEquals(
            Lists.of(ReferenceAndSpreadsheetCellReference.with(a1, b1)),
            remove,
            "remove"
        );

        this.cellToReferencesAndCheck(
            store,
            Maps.of(
                c1,
                Sets.of(a1),
                d1,
                Sets.of(a1)
            )
        );
        this.referenceToCellsAndCheck(
            store,
            Maps.of(
                a1,
                Sets.of(
                    c1,
                    d1
                )
            )
        );
    }

    @Test
    public void testSaveCellsReplaceAllAddCellWatcherRemoveCellWatcher() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();
        store.saveCells(a1, Sets.of(b1));

        final List<ReferenceAndSpreadsheetCellReference<SpreadsheetCellReference>> add = Lists.array();
        store.addAddCellWatcher(add::add);

        final List<ReferenceAndSpreadsheetCellReference<SpreadsheetCellReference>> remove = Lists.array();
        store.addRemoveCellWatcher(remove::add);

        final SpreadsheetCellReference c1 = this.c1();
        store.saveCells(a1, Sets.of(c1));

        this.checkEquals(
            Lists.of(
                ReferenceAndSpreadsheetCellReference.with(
                    a1,
                    c1
                )
            ),
            add,
            "add"
        );
        this.checkEquals(
            Lists.of(
                ReferenceAndSpreadsheetCellReference.with(
                    a1,
                    b1
                )
            ),
            remove,
            "remove"
        );

        this.cellToReferencesAndCheck(
            store,
            Maps.of(
                c1,
                Sets.of(a1)
            )
        );
        this.referenceToCellsAndCheck(
            store,
            Maps.of(
                a1,
                Sets.of(c1)
            )
        );
    }

    // addCell..........................................................................................................

    @Test
    public void testAddCell() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                a1,
                b1
            )
        );

        this.cellToReferencesAndCheck(
            store,
            Maps.of(
                b1,
                Sets.of(a1)
            )
        );
        this.referenceToCellsAndCheck(
            store,
            Maps.of(
                a1,
                Sets.of(b1)
            )
        );
    }

    @Test
    public void testAddCell2() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(a1, b1)
        );

        final SpreadsheetCellReference c1 = this.c1();
        final SpreadsheetCellReference d1 = this.d1();
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(c1, d1)
        );

        this.cellToReferencesAndCheck(
            store,
            Maps.of(
                b1,
                Sets.of(a1),
                d1,
                Sets.of(c1)
            )
        );
        this.referenceToCellsAndCheck(
            store,
            Maps.of(
                a1,
                Sets.of(b1),
                c1,
                Sets.of(d1)
            )
        );
    }

    @Test
    public void testAddCellAbsolute() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1.toAbsolute();
        final SpreadsheetCellReference b1 = this.b1();
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(a1, b1)
        );

        this.cellToReferencesAndCheck(
            store,
            Maps.of(
                b1.toRelative(),
                Sets.of(a1.toRelative())
            )
        );
        this.referenceToCellsAndCheck(
            store,
            Maps.of(
                a1,
                Sets.of(b1)
            )
        );
    }

    @Test
    public void testAddCellSameTarget() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(a1, b1)
        );

        final SpreadsheetCellReference c1 = this.c1();
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(a1, c1)
        );

        this.cellToReferencesAndCheck(
            store,
            Maps.of(
                b1,
                Sets.of(a1),
                c1,
                Sets.of(a1)
            )
        );
        this.referenceToCellsAndCheck(
            store,
            Maps.of(
                a1,
                Sets.of(
                    b1,
                    c1
                )
            )
        );
    }

    @Test
    public void testSaveCellsAddCellAndLoad() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();
        store.saveCells(
            a1,
            Sets.of(b1)
        );

        final SpreadsheetCellReference c1 = this.c1();
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(a1, c1)
        );

        this.loadAndCheck(
            store,
            a1,
            b1, c1
        );
        this.loadFailCheck(store, b1);
        this.loadFailCheck(store, c1);

        this.findReferencesWithCellAndCheck(
            store,
            a1,
            0, // offset
            1 // count
        );
        this.findReferencesWithCellAndCheck(
            store,
            b1,
            0, // offset
            2, // count
            a1
        );
        this.findReferencesWithCellAndCheck(
            store,
            c1,
            0, // offset
            2, // count
            a1
        );
    }

    @Test
    public void testSaveCellsAddCellAndLoad2() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();
        store.saveCells(a1, Sets.of(b1));

        final SpreadsheetCellReference c1 = this.c1();
        store.addCell(ReferenceAndSpreadsheetCellReference.with(a1, c1));

        final SpreadsheetCellReference d1 = this.d1();
        final SpreadsheetCellReference e1 = this.e1();
        store.saveCells(d1, Sets.of(e1, a1, b1, c1));

        this.loadAndCheck(store, a1, b1, c1);
        this.loadAndCheck(store, d1, a1, b1, c1, e1);

        this.findReferencesWithCellAndCheck(
            store,
            a1,
            0, // offset
            2, // count
            d1
        );
        this.findReferencesWithCellAndCheck(
            store,
            b1,
            0, // offset
            2, // count
            a1, d1
        );
        this.findReferencesWithCellAndCheck(
            store,
            c1,
            0, // offset
            3, // count
            a1, d1
        );
        this.findReferencesWithCellAndCheck(
            store,
            e1,
            0, // offset
            2, // count
            d1
        );
    }

    // removeCell.......................................................................................................

    @Test
    public void testRemoveCell() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();
        store.saveCells(
            a1,
            Sets.of(b1)
        );

        store.removeCell(ReferenceAndSpreadsheetCellReference.with(a1, b1));

        this.cellToReferencesAndCheck(store, Maps.empty());
        this.referenceToCellsAndCheck(store, Maps.empty());
    }

    @Test
    public void testRemoveCell2() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        final SpreadsheetCellReference d1 = this.d1();

        store.saveCells(
            a1,
            Sets.of(b1)
        );
        store.saveCells(
            c1,
            Sets.of(d1)
        );

        store.removeCell(
            ReferenceAndSpreadsheetCellReference.with(c1, d1)
        );

        this.cellToReferencesAndCheck(
            store,
            Maps.of(
                b1,
                Sets.of(a1)
            )
        );
        this.referenceToCellsAndCheck(
            store,
            Maps.of(
                a1,
                Sets.of(b1)
            )
        );
    }

    @Test
    public void testRemoveCellAbsolute() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();
        store.saveCells(
            a1,
            Sets.of(b1)
        );

        store.removeCell(
            ReferenceAndSpreadsheetCellReference.with(
                a1.toAbsolute(),
                b1
            )
        );

        this.cellToReferencesAndCheck(
            store,
            Maps.empty()
        );
        this.referenceToCellsAndCheck(
            store,
            Maps.empty()
        );
    }

    @Test
    public void testRemoveCellManyValues() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        store.saveCells(
            a1,
            Sets.of(
                b1,
                c1
            )
        );

        store.removeCell(
            ReferenceAndSpreadsheetCellReference.with(a1, c1)
        );

        this.cellToReferencesAndCheck(
            store,
            Maps.of(
                b1,
                Sets.of(a1)
            )
        );
        this.referenceToCellsAndCheck(
            store,
            Maps.of(
                a1,
                Sets.of(b1)
            )
        );
    }

    @Test
    public void testRemoveCellManyValuesAbsolute() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        store.saveCells(
            a1,
            Sets.of(
                b1,
                c1
            )
        );

        store.removeCell(
            ReferenceAndSpreadsheetCellReference.with(
                a1.toAbsolute(),
                c1
            )
        );

        this.cellToReferencesAndCheck(
            store,
            Maps.of(
                b1,
                Sets.of(a1)
            )
        );
        this.referenceToCellsAndCheck(
            store,
            Maps.of(
                a1,
                Sets.of(b1)
            )
        );
    }

    @Test
    public void testRemoveCellUnknownAndLoad() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        store.saveCells(
            a1,
            Sets.of(b1)
        );

        store.removeCell(
            ReferenceAndSpreadsheetCellReference.with(a1, c1)
        );

        this.loadAndCheck(
            store,
            a1,
            b1
        );
        this.loadFailCheck(store, b1);
        this.loadFailCheck(store, c1);

        this.findReferencesWithCellAndCheck(
            store,
            a1,
            0, // offset
            2 // count
        );
        this.findReferencesWithCellAndCheck(
            store,
            b1,
            0, // offset
            2, // count
            a1
        );
        this.findReferencesWithCellAndCheck(
            store,
            c1,
            0, // offset
            2 // count
        );
    }

    @Test
    public void testSaveCellsRemoveCellAndLoad() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        store.saveCells(
            a1,
            Sets.of(b1, c1)
        );

        store.removeCell(
            ReferenceAndSpreadsheetCellReference.with(a1, c1)
        );

        this.loadAndCheck(store, a1, b1);
        this.loadFailCheck(store, b1);
        this.loadFailCheck(store, c1);

        this.findReferencesWithCellAndCheck(
            store,
            a1,
            0, // offset
            1 // count
        );
        this.findReferencesWithCellAndCheck(
            store,
            b1,
            0, // offset
            2, // count
            a1
        );
        this.findReferencesWithCellAndCheck(
            store,
            c1,
            0, // offset
            2 // count
        );
    }

    // addCell & removeCell..............................................................................................

    @Test
    public void testSaveCellsAndAddCellAndRemoveCell() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        final SpreadsheetCellReference d1 = this.d1();
        final SpreadsheetCellReference e1 = this.e1();

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(a1, b1)
        );
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(a1, c1)
        );

        store.saveCells(
            a1,
            Sets.of(c1, d1, e1)
        );

        this.loadAndCheck(store, a1, c1, d1, e1);
        this.loadFailCheck(store, b1);
        this.loadFailCheck(store, c1);
        this.loadFailCheck(store, d1);
        this.loadFailCheck(store, e1);

        this.findReferencesWithCellAndCheck(
            store,
            a1,
            0, // offset
            1 // count
        );
        this.findReferencesWithCellAndCheck(
            store,
            b1,
            0, // offset
            1 // count
        );
        this.findReferencesWithCellAndCheck(
            store,
            c1,
            0, // offset
            2, // count
            a1
        );
        this.findReferencesWithCellAndCheck(
            store,
            d1,
            0, // offset
            2, // count
            a1
        );
        this.findReferencesWithCellAndCheck(
            store,
            e1,
            0, // offset
            2, // count
            a1
        );
    }

    @Test
    public void testSaveCellsAndAddCellAndRemoveCell2() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        final SpreadsheetCellReference d1 = this.d1();
        final SpreadsheetCellReference e1 = this.e1();

        store.addCell(ReferenceAndSpreadsheetCellReference.with(a1, b1));
        store.addCell(ReferenceAndSpreadsheetCellReference.with(a1, c1));

        store.saveCells(a1, Sets.of(c1, d1, e1));
        store.saveCells(b1, Sets.of(c1, d1, e1));

        store.removeCell(ReferenceAndSpreadsheetCellReference.with(a1, e1));

        this.loadAndCheck(store, a1, c1, d1);
        this.loadAndCheck(store, b1, c1, d1, e1);
        this.loadFailCheck(store, c1);
        this.loadFailCheck(store, d1);
        this.loadFailCheck(store, e1);

        this.findReferencesWithCellAndCheck(
            store,
            a1,
            0, // offset
            1 // count
        );
        this.findReferencesWithCellAndCheck(
            store,
            b1,
            0, // offset
            1 // count
        );
        this.findReferencesWithCellAndCheck(
            store,
            c1,
            0, // offset
            2, // count
            a1, b1
        );
        this.findReferencesWithCellAndCheck(
            store,
            d1,
            0, // offset
            3, // count
            a1, b1
        );
        this.findReferencesWithCellAndCheck(
            store,
            e1,
            0, // offset
            2, // count
            b1
        );
    }

    @Test
    public void testSaveCellsAndAddCellAndRemoveCell3() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        final SpreadsheetCellReference d1 = this.d1();
        final SpreadsheetCellReference e1 = this.e1();

        store.addCell(ReferenceAndSpreadsheetCellReference.with(a1, b1));
        store.addCell(ReferenceAndSpreadsheetCellReference.with(a1, c1));
        store.removeCell(ReferenceAndSpreadsheetCellReference.with(a1, b1));

        this.loadAndCheck(store, a1, c1);
        this.loadFailCheck(store, b1);
        this.loadFailCheck(store, c1);
        this.loadFailCheck(store, d1);
        this.loadFailCheck(store, e1);

        this.findReferencesWithCellAndCheck(
            store,
            a1,
            0, // offset
            1 // count
        );
        this.findReferencesWithCellAndCheck(
            store,
            b1,
            0, // offset
            2 // count
        );
        this.findReferencesWithCellAndCheck(
            store,
            c1,
            0, // offset
            2, // count
            a1
        );
        this.findReferencesWithCellAndCheck(
            store,
            d1,
            0, // offset
            1 // count
        );
        this.findReferencesWithCellAndCheck(
            store,
            e1,
            0, // offset
            2 // count
        );
    }

    // findCellsWithReference...........................................................................................

    @Test
    public void testFindCellsWithReference() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(a1, b1)
        );
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(a1, c1)
        );

        this.findCellsWithReferenceAndCheck(
            store,
            a1,
            0, // offset
            2, // count
            b1, c1
        );

        store.removeCell(ReferenceAndSpreadsheetCellReference.with(b1, a1));
    }

    @Test
    public void testFindCellsWithReference2() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetLabelName> store = TreeMapSpreadsheetExpressionReferenceStore.create();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("B2");
        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("C3");

        final SpreadsheetLabelName label1 = SpreadsheetSelection.labelName("Label1");
        final SpreadsheetLabelName label2 = SpreadsheetSelection.labelName("Label2");
        final SpreadsheetLabelName label3 = SpreadsheetSelection.labelName("Label3");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label1,
                a1
            )
        );
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label2,
                b2
            )
        );
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label3,
                c3
            )
        );

        this.findCellsWithReferenceAndCheck(
            store,
            label1,
            0, // offset
            3, // count
            a1
        );
    }

    @Test
    public void testFindCellsWithReferenceAndOffset() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetLabelName> store = TreeMapSpreadsheetExpressionReferenceStore.create();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("B2");
        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("C3");

        final SpreadsheetLabelName label1 = SpreadsheetSelection.labelName("Label1");
        final SpreadsheetLabelName label2 = SpreadsheetSelection.labelName("Label2");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label1,
                a1
            )
        );
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label1,
                b2
            )
        );
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label2,
                c3
            )
        );

        this.findCellsWithReferenceAndCheck(
            store,
            label1,
            1, // offset
            2, // count
            b2
        );
    }

    @Test
    public void testFindCellsWithReferenceAndCount() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetLabelName> store = TreeMapSpreadsheetExpressionReferenceStore.create();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("B2");
        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("C3");

        final SpreadsheetLabelName label1 = SpreadsheetSelection.labelName("Label1");
        final SpreadsheetLabelName label2 = SpreadsheetSelection.labelName("Label2");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label1,
                a1
            )
        );
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label1,
                b2
            )
        );
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label2,
                c3
            )
        );

        this.findCellsWithReferenceAndCheck(
            store,
            label1,
            0, // offset
            1, // count
            a1
        );
    }

    @Test
    public void testFindCellsWithReferenceAndOffsetAndCount() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetLabelName> store = TreeMapSpreadsheetExpressionReferenceStore.create();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("B2");
        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("C3");
        final SpreadsheetCellReference d4 = SpreadsheetSelection.parseCell("D4");
        final SpreadsheetCellReference e5 = SpreadsheetSelection.parseCell("E5");

        final SpreadsheetLabelName label1 = SpreadsheetSelection.labelName("Label1");
        final SpreadsheetLabelName label2 = SpreadsheetSelection.labelName("Label2");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label1,
                a1
            )
        );
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label1,
                b2
            )
        );
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label1,
                c3
            )
        );
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label1,
                d4
            )
        );
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label2,
                e5
            )
        );

        this.findCellsWithReferenceAndCheck(
            store,
            label1,
            1, // offset
            2, // count
            b2, c3
        );
    }

    // countCellsWithReference..........................................................................................

    @Test
    public void testCountCellsWithReference() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(a1, b1)
        );
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(a1, c1)
        );

        this.countCellsWithReferenceAndCheck(
            store,
            a1,
            2
        );
    }

    @Test
    public void testCountCellsWithReferenceWithMissingCell() {
        this.countCellsWithReferenceAndCheck(
            this.createStore(),
            SpreadsheetSelection.A1,
            0
        );
    }

    // findReferencesWithCell...........................................................................................

    @Test
    public void testFindReferencesWithCell() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(b1, a1)
        );
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(c1, a1)
        );

        this.findReferencesWithCellAndCheck(
            store,
            a1,
            0, // offset
            2, // count
            b1, c1
        );

        store.removeCell(ReferenceAndSpreadsheetCellReference.with(b1, a1));
    }

    // removeReferencesWithCell.........................................................................................

    @Test
    public void testRemoveReferencesWithCell() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetLabelName> store = TreeMapSpreadsheetExpressionReferenceStore.create();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetLabelName b1 = SpreadsheetSelection.labelName("Label1");
        final SpreadsheetLabelName c1 = SpreadsheetSelection.labelName("Label2");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(b1, a1)
        );
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(c1, a1)
        );

        this.findReferencesWithCellAndCheck(
            store,
            a1,
            0, // offset
            2, // count
            b1, c1
        );

        this.removeReferencesWithCellAndCheck(
            store,
            a1
        );
    }

    // count............................................................................................................

    @Test
    public void testCountWhenSaveReferencesAddCellRemoveCell() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        store.saveCells(
            a1,
            Sets.of(b1, c1)
        );

        store.removeCell(
            ReferenceAndSpreadsheetCellReference.with(a1, c1)
        );
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(a1, c1)
        );

        this.countAndCheck(store, 1);
    }

    // ToStringTesting..................................................................................................

    @Test
    public void testToString() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        store.saveCells(
            SpreadsheetSelection.A1,
            Sets.of(
                this.b1(),
                this.c1()
            )
        );

        store.saveCells(
            SpreadsheetSelection.parseCell("e5"),
            Sets.of(
                SpreadsheetSelection.A1,
                this.c1()
            )
        );

        this.toStringAndCheck(
            store,
            "referenceToCells=A1=B1, C1, E5=A1, C1 cellToReferences=A1=E5, B1=A1, C1=A1, E5"
        );
    }

    // SpreadsheetExpressionReferenceStoreTesting.......................................................................

    @Override
    public SpreadsheetCellReference id() {
        return SpreadsheetSelection.A1;
    }

    private SpreadsheetCellReference b1() {
        return SpreadsheetSelection.parseCell("b1");
    }

    private SpreadsheetCellReference c1() {
        return SpreadsheetSelection.parseCell("c1");
    }

    private SpreadsheetCellReference d1() {
        return SpreadsheetSelection.parseCell("d1");
    }

    private SpreadsheetCellReference e1() {
        return SpreadsheetSelection.parseCell("e1");
    }

    private SpreadsheetCellReference f99() {
        return SpreadsheetSelection.parseCell("f99");
    }

    private SpreadsheetCellReference g99() {
        return SpreadsheetSelection.parseCell("g99");
    }

    private SpreadsheetCellReference h99() {
        return SpreadsheetSelection.parseCell("h99");
    }

    private SpreadsheetCellReference i99() {
        return SpreadsheetSelection.parseCell("i99");
    }

    private SpreadsheetCellReference j99() {
        return SpreadsheetSelection.parseCell("j99");
    }

    @Override
    public Set<SpreadsheetCellReference> value() {
        return Sets.of(this.f99(), this.g99(), this.i99(), this.j99());
    }

    private <T extends SpreadsheetExpressionReference> void referenceToCellsAndCheck(final TreeMapSpreadsheetExpressionReferenceStore<T> store,
                                                                                     final Map<T, Set<SpreadsheetCellReference>> cellToReferences) {
        this.checkEquals(
            cellToReferences,
            store.referenceToCells,
            "referenceToCells"
        );
    }

    private <T extends SpreadsheetExpressionReference> void cellToReferencesAndCheck(final TreeMapSpreadsheetExpressionReferenceStore<T> store,
                                                                                     final Map<SpreadsheetCellReference, Set<T>> cellToReferences) {
        this.checkEquals(
            cellToReferences,
            store.cellToReferences,
            "cellToReferences"
        );
    }

    // StoreTesting.....................................................................................................

    @Override
    public TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> createStore() {
        return TreeMapSpreadsheetExpressionReferenceStore.create();
    }

    // class............................................................................................................

    @Override
    public Class<TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference>> type() {
        return Cast.to(TreeMapSpreadsheetExpressionReferenceStore.class);
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNamePrefix() {
        return TreeMap.class.getSimpleName();
    }
}

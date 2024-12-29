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

    @Test
    public void testSave() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();

        store.saveReferences(a1, Sets.of(b1));

        this.checkTargetToReferences(store, Maps.of(a1, Sets.of(b1)));
        this.checkReferenceToTargets(store, Maps.of(b1, Sets.of(a1)));
    }

    @Test
    public void testSave2() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();

        store.saveReferences(a1, Sets.of(b1, c1));

        this.checkTargetToReferences(store, Maps.of(a1, Sets.of(b1, c1)));
        this.checkReferenceToTargets(store, Maps.of(b1, Sets.of(a1), c1, Sets.of(a1)));
    }

    @Test
    public void testSave3() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();

        store.saveReferences(a1, Sets.of(b1, c1));

        this.checkTargetToReferences(store, Maps.of(a1, Sets.of(b1, c1)));
        this.checkReferenceToTargets(store, Maps.of(b1, Sets.of(a1), c1, Sets.of(a1)));
    }

    @Test
    public void testSave3Absolute() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1().toAbsolute();
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();

        store.saveReferences(a1, Sets.of(b1.toAbsolute(), c1.toAbsolute()));

        this.checkTargetToReferences(store, Maps.of(a1, Sets.of(b1, c1)));
        this.checkReferenceToTargets(store, Maps.of(b1, Sets.of(a1), c1, Sets.of(a1)));
    }

    @Test
    public void testSaveAndLoad() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();

        store.saveReferences(a1, Sets.of(b1));

        this.loadAndCheck(store, a1, b1); // a1 --> b1
        this.loadReferredAndCheck(store, b1, a1); // b1 --> a1

        this.loadAndCheck(store, b1); // b1 -> nothing
        this.loadReferredAndCheck(store, a1); // a1 --> nothing
    }

    @Test
    public void testSaveAndLoad2() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        store.saveReferences(a1, Sets.of(b1));

        final SpreadsheetCellReference c1 = this.c1();
        store.saveReferences(b1, Sets.of(c1));

        this.loadAndCheck(store, a1, b1);
        this.loadAndCheck(store, b1, c1);
        this.loadFailCheck(store, c1);

        this.loadReferredAndCheck(store, b1, a1);
        this.loadReferredAndCheck(store, c1, b1);
    }

    @Test
    public void testSaveAndLoad3() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();

        store.saveReferences(a1, Sets.of(b1, c1));
        store.saveReferences(b1, Sets.of(c1));

        this.loadAndCheck(store, a1, b1, c1);
        this.loadAndCheck(store, b1, c1);
        this.loadFailCheck(store, c1);

        this.loadReferredAndCheck(store, b1, a1);
        this.loadReferredAndCheck(store, c1, a1, b1);
    }

    @Test
    public void testSaveAndLoadDifferentReferenceKind() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();

        store.saveReferences(a1, Sets.of(b1, c1));
        store.saveReferences(b1, Sets.of(c1));

        final SpreadsheetCellReference absoluteB1 = b1.toAbsolute();
        this.checkNotEquals(b1, absoluteB1, "expected same cell but relative/absolute");
        this.checkEquals(true, absoluteB1.equalsIgnoreReferenceKind(b1), () -> absoluteB1 + " " + b1);

        this.loadAndCheck(store, a1, b1, c1);
        this.loadAndCheck(store, b1, c1);
        this.loadFailCheck(store, c1);

        this.loadReferredAndCheck(store, b1, a1);
        this.loadReferredAndCheck(store, absoluteB1, a1);
        this.loadReferredAndCheck(store, c1, a1, b1);
    }

    @Test
    public void testSaveAndLoadDifferentReferenceKind2() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();

        store.saveReferences(a1, Sets.of(b1, c1));
        store.saveReferences(b1, Sets.of(c1));

        final SpreadsheetCellReference absoluteB1 = b1.toAbsolute();
        this.checkNotEquals(b1, absoluteB1, "expected same cell but relative/absolute");
        this.checkEquals(true, absoluteB1.equalsIgnoreReferenceKind(b1), () -> absoluteB1 + " " + b1);

        this.loadAndCheck(store, a1.toAbsolute(), b1, c1);
        this.loadAndCheck(store, b1.toAbsolute(), c1);
        this.loadFailCheck(store, c1.toAbsolute());

        this.loadReferredAndCheck(store, b1, a1);
        this.loadReferredAndCheck(store, absoluteB1, a1);
        this.loadReferredAndCheck(store, c1, a1, b1);
    }

    // delete.........................................................................................

    @Test
    public void testDeleteAfterSaveReferences() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();

        store.saveReferences(a1, Sets.of(b1));

        store.delete(a1);

        this.checkReferenceToTargets(store, Maps.empty());
        this.checkTargetToReferences(store, Maps.empty());
    }

    @Test
    public void testDeleteAfterSaveReferences2() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        final SpreadsheetCellReference d1 = this.d1();

        store.saveReferences(a1, Sets.of(b1));
        store.saveReferences(c1, Sets.of(d1));

        store.delete(a1);

        this.checkReferenceToTargets(store, Maps.of(d1, Sets.of(c1)));
        this.checkTargetToReferences(store, Maps.of(c1, Sets.of(d1)));
    }

    @Test
    public void testDeleteAfterSaveReferencesAbsolute() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();

        store.saveReferences(a1, Sets.of(b1));

        store.delete(a1.toAbsolute());

        this.checkReferenceToTargets(store, Maps.empty());
        this.checkTargetToReferences(store, Maps.empty());
    }

    // ids.................................................................................

    @Test
    public final void testIds() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a = this.a1();
        final SpreadsheetCellReference b = this.b1();
        final SpreadsheetCellReference c = this.c1();
        final SpreadsheetCellReference f = this.f99();

        store.saveReferences(a, Sets.of(f));
        store.saveReferences(b, Sets.of(f));
        store.saveReferences(c, Sets.of(f));

        this.idsAndCheck(store, 0, 3, a, b, c);
    }

    @Test
    public final void testIdsAbsolute() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a = this.a1().toAbsolute();
        final SpreadsheetCellReference b = this.b1().toAbsolute();
        final SpreadsheetCellReference c = this.c1().toAbsolute();
        final SpreadsheetCellReference f = this.f99().toAbsolute();

        store.saveReferences(a, Sets.of(f));
        store.saveReferences(b, Sets.of(f));
        store.saveReferences(c, Sets.of(f));

        this.idsAndCheck(store, 0, 3, a, b, c);
    }

    @Test
    public final void testIdsWindow() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a = this.a1();
        final SpreadsheetCellReference b = this.b1();
        final SpreadsheetCellReference c = this.c1();
        final SpreadsheetCellReference d = this.d1();

        final SpreadsheetCellReference f = this.f99();
        final SpreadsheetCellReference g = this.g99();
        final SpreadsheetCellReference h = this.h99();
        final SpreadsheetCellReference i = this.i99();

        store.saveReferences(a, Sets.of(f));
        store.saveReferences(b, Sets.of(g));
        store.saveReferences(c, Sets.of(h));
        store.saveReferences(d, Sets.of(i));

        this.idsAndCheck(store, 1, 2, b, c);
    }

    @Test
    public void testIdsWithoutReferences() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();

        store.saveReferences(a1, Sets.of(b1));

        this.loadAndCheck(store, a1, b1); // a1 --> b1
        this.loadReferredAndCheck(store, b1, a1); // b1 --> a1

        this.loadAndCheck(store, b1); // b1 -> nothing
        this.loadReferredAndCheck(store, a1); // a1 --> nothing

        this.idsAndCheck(store, 0, 2, a1); // b1 doesnt exist because it has 0 references to it
    }

    // values.................................................................................

    @Test
    public final void testValues() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a = this.a1();
        final SpreadsheetCellReference b = this.b1();
        final SpreadsheetCellReference c = this.c1();

        final Set<SpreadsheetCellReference> f = Sets.of(this.f99());
        final Set<SpreadsheetCellReference> g = Sets.of(this.g99());
        final Set<SpreadsheetCellReference> hi = Sets.of(this.h99(), this.i99());

        store.saveReferences(a, f);
        store.saveReferences(b, g);
        store.saveReferences(c, hi);

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

        final SpreadsheetCellReference a = this.a1().toAbsolute();
        final SpreadsheetCellReference b = this.b1().toAbsolute();
        final SpreadsheetCellReference c = this.c1().toAbsolute();

        final Set<SpreadsheetCellReference> f = Sets.of(this.f99());
        final Set<SpreadsheetCellReference> g = Sets.of(this.g99());
        final Set<SpreadsheetCellReference> hi = Sets.of(this.h99(), this.i99());

        store.saveReferences(a, f);
        store.saveReferences(b, g);
        store.saveReferences(c, hi);

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

        final SpreadsheetCellReference a = this.a1();
        final SpreadsheetCellReference b = this.b1();
        final SpreadsheetCellReference c = this.c1();
        final SpreadsheetCellReference d = this.d1();

        final Set<SpreadsheetCellReference> f = Sets.of(this.f99());
        final Set<SpreadsheetCellReference> g = Sets.of(this.g99());
        final Set<SpreadsheetCellReference> h = Sets.of(this.h99());
        final Set<SpreadsheetCellReference> i = Sets.of(this.i99());

        store.saveReferences(a, f);
        store.saveReferences(b, g);
        store.saveReferences(c, h);
        store.saveReferences(d, i);

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
    public void testValuesWithoutReferences() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();

        store.saveReferences(a1, Sets.of(b1));

        this.loadAndCheck(store, a1, b1); // a1 --> b1
        this.loadReferredAndCheck(store, b1, a1); // b1 --> a1

        this.loadAndCheck(store, b1); // b1 -> nothing
        this.loadReferredAndCheck(store, a1); // a1 --> nothing

        //noinspection unchecked
        this.valuesAndCheck(
                store,
                0,
                2,
                Sets.of(b1)
        );
    }

    // saveReference ......................................................................................

    @Test
    @Override
    public void testSaveReferences() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        store.saveReferences(a1, Sets.of(b1));

        this.checkReferenceToTargets(store, Maps.of(b1, Sets.of(a1)));
        this.checkTargetToReferences(store, Maps.of(a1, Sets.of(b1)));
    }

    @Test
    public void testSaveReferencesAbsolute() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1().toAbsolute();
        final SpreadsheetCellReference b1 = this.b1();
        store.saveReferences(a1, Sets.of(b1));

        this.checkReferenceToTargets(store, Maps.of(b1, Sets.of(a1)));
        this.checkTargetToReferences(store, Maps.of(a1, Sets.of(b1)));
    }

    @Test
    public void testSaveReferencesReplaceSome() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        store.saveReferences(a1, Sets.of(b1));

        final SpreadsheetCellReference c1 = this.c1();
        store.saveReferences(a1, Sets.of(c1));

        this.checkReferenceToTargets(store, Maps.of(c1, Sets.of(a1)));
        this.checkTargetToReferences(store, Maps.of(a1, Sets.of(c1)));
    }

    @Test
    public void testSaveReferencesReplaceAll() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        store.saveReferences(a1, Sets.of(b1));

        final SpreadsheetCellReference c1 = this.c1();
        final SpreadsheetCellReference d1 = this.d1();
        store.saveReferences(c1, Sets.of(d1));

        final SpreadsheetCellReference e1 = this.b1();
        store.saveReferences(a1, Sets.of(e1));

        this.checkReferenceToTargets(store, Maps.of(d1, Sets.of(c1), e1, Sets.of(a1)));
        this.checkTargetToReferences(store, Maps.of(a1, Sets.of(e1), c1, Sets.of(d1)));
    }

    @Test
    public void testSaveReferencesReplaceSomeAddReferenceWatcherRemoveReferenceWatcher() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();

        store.saveReferences(a1, Sets.of(b1, c1));

        final SpreadsheetCellReference d1 = this.d1();

        final List<TargetAndSpreadsheetCellReference<SpreadsheetCellReference>> add = Lists.array();
        store.addAddReferenceWatcher(add::add);

        final List<TargetAndSpreadsheetCellReference<SpreadsheetCellReference>> remove = Lists.array();
        store.addRemoveReferenceWatcher(remove::add);

        store.saveReferences(a1, Sets.of(c1, d1));

        this.checkEquals(Lists.of(TargetAndSpreadsheetCellReference.with(a1, d1)),
                add,
                "add");
        this.checkEquals(Lists.of(TargetAndSpreadsheetCellReference.with(a1, b1)),
                remove,
                "remove");

        this.checkReferenceToTargets(store, Maps.of(c1, Sets.of(a1), d1, Sets.of(a1)));
        this.checkTargetToReferences(store, Maps.of(a1, Sets.of(c1, d1)));
    }

    @Test
    public void testSaveReferencesReplaceAllAddReferenceWatcherRemoveReferenceWatcher() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        store.saveReferences(a1, Sets.of(b1));

        final List<TargetAndSpreadsheetCellReference<SpreadsheetCellReference>> add = Lists.array();
        store.addAddReferenceWatcher(add::add);

        final List<TargetAndSpreadsheetCellReference<SpreadsheetCellReference>> remove = Lists.array();
        store.addRemoveReferenceWatcher(remove::add);

        final SpreadsheetCellReference c1 = this.c1();
        store.saveReferences(a1, Sets.of(c1));

        this.checkEquals(Lists.of(TargetAndSpreadsheetCellReference.with(a1, c1)), add, "add");
        this.checkEquals(Lists.of(TargetAndSpreadsheetCellReference.with(a1, b1)), remove, "remove");

        this.checkReferenceToTargets(store, Maps.of(c1, Sets.of(a1)));
        this.checkTargetToReferences(store, Maps.of(a1, Sets.of(c1)));
    }

    // addReference ......................................................................................

    @Test
    public void testAddReference() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        store.addReference(TargetAndSpreadsheetCellReference.with(a1, b1));

        this.checkReferenceToTargets(store, Maps.of(b1, Sets.of(a1)));
        this.checkTargetToReferences(store, Maps.of(a1, Sets.of(b1)));
    }

    @Test
    public void testAddReference2() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        store.addReference(TargetAndSpreadsheetCellReference.with(a1, b1));

        final SpreadsheetCellReference c1 = this.c1();
        final SpreadsheetCellReference d1 = this.d1();
        store.addReference(TargetAndSpreadsheetCellReference.with(c1, d1));

        this.checkReferenceToTargets(store, Maps.of(b1, Sets.of(a1), d1, Sets.of(c1)));
        this.checkTargetToReferences(store, Maps.of(a1, Sets.of(b1), c1, Sets.of(d1)));
    }

    @Test
    public void testAddReferenceAbsolute() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1().toAbsolute();
        final SpreadsheetCellReference b1 = this.b1();
        store.addReference(TargetAndSpreadsheetCellReference.with(a1, b1));

        this.checkReferenceToTargets(store, Maps.of(b1, Sets.of(a1)));
        this.checkTargetToReferences(store, Maps.of(a1, Sets.of(b1)));
    }

    @Test
    public void testAddReferenceSameTarget() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        store.addReference(TargetAndSpreadsheetCellReference.with(a1, b1));

        final SpreadsheetCellReference c1 = this.c1();
        store.addReference(TargetAndSpreadsheetCellReference.with(a1, c1));

        this.checkReferenceToTargets(store, Maps.of(b1, Sets.of(a1), c1, Sets.of(a1)));
        this.checkTargetToReferences(store, Maps.of(a1, Sets.of(b1, c1)));
    }

    @Test
    public void testSaveReferencesAddReferenceAndLoad() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        store.saveReferences(a1, Sets.of(b1));

        final SpreadsheetCellReference c1 = this.c1();
        store.addReference(TargetAndSpreadsheetCellReference.with(a1, c1));

        this.loadAndCheck(store, a1, b1, c1);
        this.loadFailCheck(store, b1);
        this.loadFailCheck(store, c1);

        this.loadReferredAndCheck(store, a1);
        this.loadReferredAndCheck(store, b1, a1);
        this.loadReferredAndCheck(store, c1, a1);
    }

    @Test
    public void testSaveReferencesAddReferenceAndLoad2() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        store.saveReferences(a1, Sets.of(b1));

        final SpreadsheetCellReference c1 = this.c1();
        store.addReference(TargetAndSpreadsheetCellReference.with(a1, c1));

        final SpreadsheetCellReference d1 = this.d1();
        final SpreadsheetCellReference e1 = this.e1();
        store.saveReferences(d1, Sets.of(e1, a1, b1, c1));

        this.loadAndCheck(store, a1, b1, c1);
        this.loadAndCheck(store, d1, a1, b1, c1, e1);

        this.loadReferredAndCheck(store, a1, d1);
        this.loadReferredAndCheck(store, b1, a1, d1);
        this.loadReferredAndCheck(store, c1, a1, d1);
        this.loadReferredAndCheck(store, e1, d1);
    }

    // removeReference.................................................................................

    @Test
    public void testRemoveReference() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        store.saveReferences(a1, Sets.of(b1));

        store.removeReference(TargetAndSpreadsheetCellReference.with(a1, b1));

        this.checkReferenceToTargets(store, Maps.empty());
        this.checkTargetToReferences(store, Maps.empty());
    }

    @Test
    public void testRemoveReference2() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        final SpreadsheetCellReference d1 = this.d1();

        store.saveReferences(a1, Sets.of(b1));
        store.saveReferences(c1, Sets.of(d1));

        store.removeReference(TargetAndSpreadsheetCellReference.with(c1, d1));

        this.checkReferenceToTargets(store, Maps.of(b1, Sets.of(a1)));
        this.checkTargetToReferences(store, Maps.of(a1, Sets.of(b1)));
    }

    @Test
    public void testRemoveReferenceAbsolute() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        store.saveReferences(a1, Sets.of(b1));

        store.removeReference(TargetAndSpreadsheetCellReference.with(a1.toAbsolute(), b1));

        this.checkReferenceToTargets(store, Maps.empty());
        this.checkTargetToReferences(store, Maps.empty());
    }

    @Test
    public void testRemoveReferenceManyValues() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        store.saveReferences(a1, Sets.of(b1, c1));

        store.removeReference(TargetAndSpreadsheetCellReference.with(a1, c1));

        this.checkReferenceToTargets(store, Maps.of(b1, Sets.of(a1)));
        this.checkTargetToReferences(store, Maps.of(a1, Sets.of(b1)));
    }

    @Test
    public void testRemoveReferenceManyValuesAbsolute() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        store.saveReferences(a1, Sets.of(b1, c1));

        store.removeReference(TargetAndSpreadsheetCellReference.with(a1.toAbsolute(), c1));

        this.checkReferenceToTargets(store, Maps.of(b1, Sets.of(a1)));
        this.checkTargetToReferences(store, Maps.of(a1, Sets.of(b1)));
    }

    @Test
    public void testRemoveReferenceUnknownAndLoad() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        store.saveReferences(a1, Sets.of(b1));

        store.removeReference(TargetAndSpreadsheetCellReference.with(a1, c1));

        this.loadAndCheck(store, a1, b1);
        this.loadFailCheck(store, b1);
        this.loadFailCheck(store, c1);

        this.loadReferredAndCheck(store, a1);
        this.loadReferredAndCheck(store, b1, a1);
        this.loadReferredAndCheck(store, c1);
    }

    @Test
    public void testSaveReferencesRemoveReferenceAndLoad() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        store.saveReferences(a1, Sets.of(b1, c1));

        store.removeReference(TargetAndSpreadsheetCellReference.with(a1, c1));

        this.loadAndCheck(store, a1, b1);
        this.loadFailCheck(store, b1);
        this.loadFailCheck(store, c1);

        this.loadReferredAndCheck(store, a1);
        this.loadReferredAndCheck(store, b1, a1);
        this.loadReferredAndCheck(store, c1);
    }

    // addReference & removeReference.................................................................................

    @Test
    public void testSaveReferencesAndAddReferenceAndRemoveReference() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        final SpreadsheetCellReference d1 = this.d1();
        final SpreadsheetCellReference e1 = this.e1();

        store.addReference(TargetAndSpreadsheetCellReference.with(a1, b1));
        store.addReference(TargetAndSpreadsheetCellReference.with(a1, c1));

        store.saveReferences(a1, Sets.of(c1, d1, e1));

        this.loadAndCheck(store, a1, c1, d1, e1);
        this.loadFailCheck(store, b1);
        this.loadFailCheck(store, c1);
        this.loadFailCheck(store, d1);
        this.loadFailCheck(store, e1);

        this.loadReferredAndCheck(store, a1);
        this.loadReferredAndCheck(store, b1);
        this.loadReferredAndCheck(store, c1, a1);
        this.loadReferredAndCheck(store, d1, a1);
        this.loadReferredAndCheck(store, e1, a1);
    }

    @Test
    public void testSaveReferencesAndAddReferenceAndRemoveReference2() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        final SpreadsheetCellReference d1 = this.d1();
        final SpreadsheetCellReference e1 = this.e1();

        store.addReference(TargetAndSpreadsheetCellReference.with(a1, b1));
        store.addReference(TargetAndSpreadsheetCellReference.with(a1, c1));

        store.saveReferences(a1, Sets.of(c1, d1, e1));
        store.saveReferences(b1, Sets.of(c1, d1, e1));

        store.removeReference(TargetAndSpreadsheetCellReference.with(a1, e1));

        this.loadAndCheck(store, a1, c1, d1);
        this.loadAndCheck(store, b1, c1, d1, e1);
        this.loadFailCheck(store, c1);
        this.loadFailCheck(store, d1);
        this.loadFailCheck(store, e1);

        this.loadReferredAndCheck(store, a1);
        this.loadReferredAndCheck(store, b1);
        this.loadReferredAndCheck(store, c1, a1, b1);
        this.loadReferredAndCheck(store, d1, a1, b1);
        this.loadReferredAndCheck(store, e1, b1);
    }

    @Test
    public void testSaveReferencesAndAddReferenceAndRemoveReference3() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        final SpreadsheetCellReference d1 = this.d1();
        final SpreadsheetCellReference e1 = this.e1();

        store.addReference(TargetAndSpreadsheetCellReference.with(a1, b1));
        store.addReference(TargetAndSpreadsheetCellReference.with(a1, c1));
        store.removeReference(TargetAndSpreadsheetCellReference.with(a1, b1));

        this.loadAndCheck(store, a1, c1);
        this.loadFailCheck(store, b1);
        this.loadFailCheck(store, c1);
        this.loadFailCheck(store, d1);
        this.loadFailCheck(store, e1);

        this.loadReferredAndCheck(store, a1);
        this.loadReferredAndCheck(store, b1);
        this.loadReferredAndCheck(store, c1, a1);
        this.loadReferredAndCheck(store, d1);
        this.loadReferredAndCheck(store, e1);
    }

    @Test
    public void testLoadReferredDefensivelyCopied() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();

        store.addReference(TargetAndSpreadsheetCellReference.with(b1, a1));
        store.addReference(TargetAndSpreadsheetCellReference.with(c1, a1));

        final Set<SpreadsheetCellReference> referred = store.loadReferred(a1);
        this.checkEquals(Sets.of(b1, c1), referred, "Incorrect load referred.");

        store.removeReference(TargetAndSpreadsheetCellReference.with(b1, a1));
        this.checkEquals(Sets.of(b1, c1), referred, "Past referred snapshot should not have changed.");
    }

    // count.................................................................................

    @Test
    public void testSaveReferencesAddReferenceRemoveReferenceAndCount() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        store.saveReferences(a1, Sets.of(b1, c1));

        store.removeReference(TargetAndSpreadsheetCellReference.with(a1, c1));
        store.addReference(TargetAndSpreadsheetCellReference.with(a1, c1));

        this.countAndCheck(store, 1);
    }

    // ToStringTesting.............................................................

    @Test
    public void testToString() {
        final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        store.saveReferences(a1, Sets.of(b1, c1));

        this.toStringAndCheck(store, "{A1=[B1, C1]}");
    }

    // SpreadsheetExpressionReferenceStoreTesting.............................................................

    @Override
    public SpreadsheetCellReference id() {
        return this.a1();
    }

    private SpreadsheetCellReference a1() {
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

    private void checkTargetToReferences(final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> map,
                                         final Map<SpreadsheetCellReference, Set<SpreadsheetCellReference>> targetToReferences) {
        this.checkEquals(targetToReferences, map.targetToReferences, "targetToReferences");
    }

    private void checkReferenceToTargets(final TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> map,
                                         final Map<SpreadsheetCellReference, Set<SpreadsheetCellReference>> referenceToTargets) {
        this.checkEquals(referenceToTargets, map.referenceToTargets, "referenceToTargets");
    }

    // StoreTesting.................................................................................

    @Override
    public TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference> createStore() {
        return TreeMapSpreadsheetExpressionReferenceStore.create();
    }

    @Override
    public Class<TreeMapSpreadsheetExpressionReferenceStore<SpreadsheetCellReference>> type() {
        return Cast.to(TreeMapSpreadsheetExpressionReferenceStore.class);
    }

    // TypeNameTesting.................................................................................

    @Override
    public String typeNamePrefix() {
        return TreeMap.class.getSimpleName();
    }
}

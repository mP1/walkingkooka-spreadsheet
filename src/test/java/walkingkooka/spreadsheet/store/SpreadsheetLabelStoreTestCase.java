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
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import static org.junit.jupiter.api.Assertions.assertSame;

public abstract class SpreadsheetLabelStoreTestCase<S extends SpreadsheetLabelStore> implements SpreadsheetLabelStoreTesting<S> {

    final static SpreadsheetCellReference REFERENCE = SpreadsheetReferenceKind.ABSOLUTE.column(1).setRow(SpreadsheetReferenceKind.ABSOLUTE.row(2));
    final static SpreadsheetLabelName LABEL = SpreadsheetSelection.labelName("label123");

    @Test
    public final void testLoadUnknownFails() {
        this.loadAndCheck(
            this.createStore(),
            LABEL
        );
    }

    @Test
    public void testSaveAndLoad() {
        final S store = this.createStore();

        final SpreadsheetLabelMapping mapping = SpreadsheetLabelMapping.with(LABEL, REFERENCE);
        store.save(mapping);

        assertSame(mapping, store.loadOrFail(LABEL));
    }

    @Test
    public void testSaveDeleteLoad() {
        final S store = this.createStore();

        final SpreadsheetLabelMapping mapping = SpreadsheetLabelMapping.with(LABEL, REFERENCE);
        store.save(mapping);
        store.delete(mapping.label());

        this.loadAndCheck(
            store,
            LABEL
        );
    }

    @Test
    public void testCount() {
        final S store = this.createStore();

        final SpreadsheetLabelMapping a = this.mapping("a", 1, 2);
        final SpreadsheetLabelMapping b = this.mapping("b", 4, 8);
        final SpreadsheetLabelMapping c = this.mapping("c", 88, 99);

        store.save(a);
        store.save(b);
        store.save(c);

        this.countAndCheck(store, 3);
    }

    @Test
    public void testIds() {
        final S store = this.createStore();

        final SpreadsheetLabelMapping a = this.mapping("a", 1, 2);
        final SpreadsheetLabelMapping b = this.mapping("b", 4, 8);
        final SpreadsheetLabelMapping c = this.mapping("c", 88, 99);

        store.save(a);
        store.save(b);
        store.save(c);

        this.idsAndCheck(store, 0, 3, a.label(), b.label(), c.label());
    }

    @Test
    public void testIdsWindow() {
        final S store = this.createStore();

        final SpreadsheetLabelMapping a = this.mapping("a", 1, 2);
        final SpreadsheetLabelMapping b = this.mapping("b", 4, 8);
        final SpreadsheetLabelMapping c = this.mapping("c", 77, 88);
        final SpreadsheetLabelMapping d = this.mapping("d", 88, 99);

        store.save(a);
        store.save(b);
        store.save(c);
        store.save(d);

        this.idsAndCheck(store, 1, 2, b.label(), c.label());
    }

    @Test
    public void testValues() {
        final S store = this.createStore();

        final SpreadsheetLabelMapping a = this.mapping("a", 1, 2);
        final SpreadsheetLabelMapping b = this.mapping("b", 4, 8);
        final SpreadsheetLabelMapping c = this.mapping("c", 88, 99);

        store.save(a);
        store.save(b);
        store.save(c);

        this.valuesAndCheck(
            store,
            0,
            3,
            a,
            b,
            c
        );
    }

    @Test
    public void testValuesWindow() {
        final S store = this.createStore();

        final SpreadsheetLabelMapping a = this.mapping("a", 1, 2);
        final SpreadsheetLabelMapping b = this.mapping("b", 4, 8);
        final SpreadsheetLabelMapping c = this.mapping("c", 77, 88);
        final SpreadsheetLabelMapping d = this.mapping("d", 88, 99);

        store.save(a);
        store.save(b);
        store.save(c);
        store.save(d);

        this.valuesAndCheck(
            store,
            1,
            2,
            b,
            c
        );
    }

    final SpreadsheetCellReference cell(final int column, final int row) {
        return SpreadsheetReferenceKind.ABSOLUTE.column(column)
            .setRow(SpreadsheetReferenceKind.ABSOLUTE.row(row));
    }

    final SpreadsheetLabelMapping mapping(final String label, final int column, final int row) {
        return SpreadsheetLabelMapping.with(SpreadsheetSelection.labelName(label), cell(column, row));
    }
}

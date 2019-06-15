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

package walkingkooka.spreadsheet.store.label;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.parser.SpreadsheetCellReference;
import walkingkooka.spreadsheet.parser.SpreadsheetLabelName;
import walkingkooka.spreadsheet.parser.SpreadsheetReferenceKind;

import static org.junit.jupiter.api.Assertions.assertSame;

public abstract class SpreadsheetLabelStoreTestCase<S extends SpreadsheetLabelStore> implements SpreadsheetLabelStoreTesting<S> {

    final static SpreadsheetCellReference REFERENCE = SpreadsheetReferenceKind.ABSOLUTE.column(1).setRow(SpreadsheetReferenceKind.ABSOLUTE.row(2));
    final static SpreadsheetLabelName LABEL = SpreadsheetLabelName.with("label123");

    @Test
    public final void testLoadUnknownFails() {
        this.loadFailCheck(LABEL);
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

        this.loadFailCheck(store, LABEL);
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

        this.idsAndCheck(store, 0, 3, a.id(), b.id(), c.id());
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

        this.idsAndCheck(store, 1, 2, b.id(), c.id());
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

        this.valuesAndCheck(store, a.id(), 3, a, b, c);
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

        this.valuesAndCheck(store, b.id(), 2, b, c);
    }

    final SpreadsheetCellReference cell(final int column, final int row) {
        return SpreadsheetReferenceKind.ABSOLUTE.column(column)
                .setRow(SpreadsheetReferenceKind.ABSOLUTE.row(row));
    }

    final SpreadsheetLabelMapping mapping(final String label, final int column, final int row) {
        return SpreadsheetLabelMapping.with(SpreadsheetLabelName.with(label), cell(column, row));
    }
}

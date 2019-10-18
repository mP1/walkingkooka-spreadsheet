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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCellFormat;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.store.StoreTesting;
import walkingkooka.tree.text.FontWeight;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

walkingkooka.reflect.*;

public abstract class SpreadsheetCellStoreTestCase<S extends SpreadsheetCellStore> implements StoreTesting<S, SpreadsheetCellReference, SpreadsheetCell>,
        TypeNameTesting<S> {

    final static SpreadsheetCellReference REFERENCE = SpreadsheetReferenceKind.ABSOLUTE.column(1).setRow(SpreadsheetReferenceKind.ABSOLUTE.row(2));

    SpreadsheetCellStoreTestCase() {
        super();
    }

    @Test
    public final void testLoadUnknown() {
        this.loadFailCheck(REFERENCE);
    }

    @Test
    public final void testSaveAndLoad() {
        final S store = this.createStore();

        final SpreadsheetCellReference reference = this.cellReference(1, 2);
        final SpreadsheetCell cell = this.cell(reference);
        assertEquals(cell, store.save(cell), "incorrect key returned");

        assertSame(cell, store.loadOrFail(reference));
    }

    @Test
    public final void testSaveDeleteLoad() {
        final S store = this.createStore();

        final SpreadsheetCellReference reference = this.cellReference(1, 2);
        final SpreadsheetCell cell = this.cell(reference);
        store.save(cell);
        store.delete(reference);

        this.loadFailCheck(store, reference);
    }

    @Test
    public final void testCount() {
        final S store = this.createStore();

        store.save(this.cell(1, 2));
        store.save(this.cell(3, 4));

        this.countAndCheck(store, 2);
    }

    @Test
    public final void testCount2() {
        final S store = this.createStore();

        final SpreadsheetCellReference cell = this.cellReference(1, 2);
        store.save(this.cell(cell));
        store.save(this.cell(3, 4));
        store.delete(cell);

        this.countAndCheck(store, 2 - 1);
    }

    @Test
    public final void testRows() {
        final S store = this.createStore();

        store.save(this.cell(1, 2));
        store.save(this.cell(1, 99));
        store.save(this.cell(1, 5));

        this.rowsAndCheck(store, 99);
    }

    @Test
    public final void testColumns() {
        final S store = this.createStore();

        store.save(this.cell(1, 1));
        store.save(this.cell(99, 1));
        store.save(this.cell(98, 2));

        this.columnsAndCheck(store, 99);
    }

    @Test
    public final void testRowNullFails() {
        assertThrows(NullPointerException.class, () -> this.createStore().row(null));
    }

    @Test
    public final void testRow() {
        final S store = this.createStore();

        final SpreadsheetCell a = this.cell(11, 1);
        final SpreadsheetCell b = this.cell(22, 1);
        final SpreadsheetCell c = this.cell(11, 2);
        final SpreadsheetCell d = this.cell(22, 2);

        store.save(a);
        store.save(b);
        store.save(c);
        store.save(d);

        checkEquals("row 1", store.row(a.reference().row()), a, b);
        checkEquals("row 2", store.row(c.reference().row()), c, d);
        checkEquals("row 99", store.row(SpreadsheetColumnOrRowReference.parseRow("99")));
    }

    @Test
    public final void testColumnNullFails() {
        assertThrows(NullPointerException.class, () -> this.createStore().column(null));
    }

    @Test
    public final void testColumn() {
        final S store = this.createStore();

        final SpreadsheetCell a = this.cell(1, 11);
        final SpreadsheetCell b = this.cell(1, 22);
        final SpreadsheetCell c = this.cell(2, 11);
        final SpreadsheetCell d = this.cell(2, 22);

        store.save(a);
        store.save(b);
        store.save(c);
        store.save(d);

        checkEquals("column 1", store.column(a.reference().column()), a, b);
        checkEquals("column 2", store.column(c.reference().column()), c, d);
        checkEquals("column 99", store.column(SpreadsheetColumnOrRowReference.parseColumn("ZZ")));
    }

    @Test
    public final void testIds() {
        final S store = this.createStore();

        final SpreadsheetCell a = this.cell("a1");
        final SpreadsheetCell b = this.cell("b2");
        final SpreadsheetCell c = this.cell("c3");

        store.save(a);
        store.save(b);
        store.save(c);

        this.idsAndCheck(store,
                0,
                3,
                a.reference(), b.reference(), c.reference());
    }

    @Test
    public final void testIdsWindow() {
        final S store = this.createStore();

        final SpreadsheetCell a = this.cell("a1");
        final SpreadsheetCell b = this.cell("b2");
        final SpreadsheetCell c = this.cell("c3");
        final SpreadsheetCell d = this.cell("d4");

        store.save(a);
        store.save(b);
        store.save(c);
        store.save(d);

        this.idsAndCheck(store,
                1,
                2,
                b.reference(), c.reference());
    }

    @Test
    public final void testValues() {
        final S store = this.createStore();

        final SpreadsheetCell a = this.cell("a1");
        final SpreadsheetCell b = this.cell("b2");
        final SpreadsheetCell c = this.cell("c3");

        store.save(a);
        store.save(b);
        store.save(c);

        this.valuesAndCheck(store,
                a.reference(),
                3,
                a, b, c);
    }

    @Test
    public final void testValuesWindow() {
        final S store = this.createStore();

        final SpreadsheetCell a = this.cell("a1");
        final SpreadsheetCell b = this.cell("b2");
        final SpreadsheetCell c = this.cell("c3");
        final SpreadsheetCell d = this.cell("d4");

        store.save(a);
        store.save(b);
        store.save(c);
        store.save(d);

        this.valuesAndCheck(store,
                b.reference(),
                2,
                b, c);
    }

    @Override
    public final SpreadsheetCellReference id() {
        return SpreadsheetExpressionReference.parseCellReference("A1");
    }

    @Override
    public SpreadsheetCell value() {
        return this.cell(this.id());
    }

    private void checkEquals(final String message, final Collection<SpreadsheetCell> cells, final SpreadsheetCell... expected) {
        final Set<SpreadsheetCell> actual = Sets.sorted();
        actual.addAll(cells);

        final Set<SpreadsheetCell> expectedSets = Sets.sorted();
        expectedSets.addAll(Lists.of(expected));

        assertEquals(expectedSets, actual, message);
    }

    private SpreadsheetCell cell(final int column, final int row) {
        return this.cell(this.cellReference(column, row));
    }

    private SpreadsheetCell cell(final String reference) {
        return this.cell(SpreadsheetExpressionReference.parseCellReference(reference));
    }

    private SpreadsheetCell cell(final SpreadsheetCellReference cellReference) {
        return SpreadsheetCell.with(cellReference, this.formula())
                .setStyle(this.style())
                .setFormat(this.format())
                .setFormatted(this.formatted());
    }

    final SpreadsheetCellReference cellReference(final int column, final int row) {
        return SpreadsheetExpressionReference.cellReference(SpreadsheetReferenceKind.ABSOLUTE.column(column),
                SpreadsheetReferenceKind.ABSOLUTE.row(row));
    }

    final void rowsAndCheck(final SpreadsheetCellStore store, final int row) {
        assertEquals(row, store.rows(), () -> "rows for store=" + store);
    }

    final void columnsAndCheck(final SpreadsheetCellStore store, final int column) {
        assertEquals(column, store.columns(), () -> "columns for store=" + store);
    }

    private SpreadsheetFormula formula() {
        return SpreadsheetFormula.with("1+2");
    }

    private TextStyle style() {
        return TextStyle.with(Maps.of(TextStylePropertyName.FONT_WEIGHT, FontWeight.BOLD));
    }

    @SuppressWarnings("SameReturnValue")
    private Optional<SpreadsheetCellFormat> format() {
        return SpreadsheetCell.NO_FORMAT;
    }

    @SuppressWarnings("SameReturnValue")
    private Optional<TextNode> formatted() {
        return SpreadsheetCell.NO_FORMATTED_CELL;
    }

    // TypeNameTesting..................................................................

    @Override
    public String typeNameSuffix() {
        return SpreadsheetCellStore.class.getSimpleName();
    }
}

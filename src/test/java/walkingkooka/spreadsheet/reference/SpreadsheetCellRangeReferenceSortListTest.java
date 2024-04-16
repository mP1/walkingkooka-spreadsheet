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

package walkingkooka.spreadsheet.reference;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.collect.list.ListTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellRangeReferenceSortListTest implements ListTesting,
        ClassTesting<SpreadsheetCellRangeReferenceSortList>,
        HashCodeEqualsDefinedTesting2<SpreadsheetCellRangeReferenceSortList> {

    @Test
    public void testWithColumn() {
        final SpreadsheetCellRangeReferenceSortList list = SpreadsheetCellRangeReferenceSortList.with(
                SpreadsheetColumnOrRowReferenceKind.COLUMN,
                3
        );

        this.checkEquals(
                SpreadsheetColumnOrRowReferenceKind.COLUMN,
                list.columnOrRow,
                "columnOrRow"
        );

        this.checkEquals(
                Lists.of(
                        null, null, null
                ),
                Lists.of(
                        list.cells
                )
        );
    }

    @Test
    public void testWithRow() {
        final SpreadsheetCellRangeReferenceSortList list = SpreadsheetCellRangeReferenceSortList.with(
                SpreadsheetColumnOrRowReferenceKind.ROW,
                4
        );

        this.checkEquals(
                SpreadsheetColumnOrRowReferenceKind.ROW,
                list.columnOrRow,
                "columnOrRow"
        );

        this.checkEquals(
                Lists.of(
                        null, null, null, null
                ),
                Lists.of(
                        list.cells
                )
        );
    }

    @Test
    public void testSize() {
        final SpreadsheetCellRangeReferenceSortList list = SpreadsheetCellRangeReferenceSortList.with(
                SpreadsheetColumnOrRowReferenceKind.ROW,
                4
        );

        this.sizeAndCheck(
                list,
                4
        );
        this.isEmptyAndCheck(
                list,
                false
        );
    }

    @Test
    public void testGet() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY);
        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
                .setFormula(SpreadsheetFormula.EMPTY);
        final SpreadsheetCell a3 = SpreadsheetSelection.parseCell("A3")
                .setFormula(SpreadsheetFormula.EMPTY);

        final SpreadsheetCellRangeReferenceSortList list = SpreadsheetCellRangeReferenceSortList.with(
                SpreadsheetColumnOrRowReferenceKind.COLUMN,
                3
        );

        list.cells[0] = a1;
        list.cells[1] = a2;
        list.cells[2] = a3;

        this.getAndCheck(
                list,
                0,
                a1
        );
        this.getAndCheck(
                list,
                1,
                a2
        );
        this.getAndCheck(
                list,
                2,
                a3
        );
    }

    @Test
    public void testSet() {
        final SpreadsheetCellRangeReferenceSortList list = SpreadsheetCellRangeReferenceSortList.with(
                SpreadsheetColumnOrRowReferenceKind.COLUMN,
                3
        );

        final SpreadsheetCell a3 = SpreadsheetSelection.parseCell("A3")
                .setFormula(SpreadsheetFormula.EMPTY);

        this.setAndGetCheck(
                list,
                2,
                a3,
                null // replaced nothing
        );

        this.checkEquals(
                Lists.of(
                        null,
                        null,
                        a3
                ),
                list
        );
    }

    @Test
    public void testSetNull() {
        final SpreadsheetCellRangeReferenceSortList list = SpreadsheetCellRangeReferenceSortList.with(
                SpreadsheetColumnOrRowReferenceKind.COLUMN,
                3
        );

        final SpreadsheetCell a3 = SpreadsheetSelection.parseCell("A3")
                .setFormula(SpreadsheetFormula.EMPTY);

        list.cells[2] = a3;

        this.setAndGetCheck(
                list,
                2,
                null,
                a3 // replaced nothing
        );

        this.checkEquals(
                Lists.of(
                        null,
                        null,
                        null
                ),
                list
        );
    }

    @Test
    public void testSetThatReplaced() {
        final SpreadsheetCellRangeReferenceSortList list = SpreadsheetCellRangeReferenceSortList.with(
                SpreadsheetColumnOrRowReferenceKind.COLUMN,
                3
        );

        final SpreadsheetCell a1 = SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY);
        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
                .setFormula(SpreadsheetFormula.EMPTY);
        final SpreadsheetCell a3 = SpreadsheetSelection.parseCell("A3")
                .setFormula(SpreadsheetFormula.EMPTY);


        list.cells[0] = a1;
        list.cells[1] = a2;
        list.cells[2] = a3;

        final SpreadsheetCell newA3 = SpreadsheetSelection.parseCell("A3")
                .setFormula(SpreadsheetFormula.EMPTY.setText("'New"));

        this.setAndGetCheck(
                list,
                2,
                newA3,
                a3
        );

        this.checkEquals(
                Lists.of(
                        a1,
                        a2,
                        newA3
                ),
                list
        );
    }

    @Test
    public void testAddFails() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> SpreadsheetCellRangeReferenceSortList.with(
                        SpreadsheetColumnOrRowReferenceKind.COLUMN,
                        4
                ).add(null)
        );
    }

    @Test
    public void testAddIndexFails() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> SpreadsheetCellRangeReferenceSortList.with(
                        SpreadsheetColumnOrRowReferenceKind.COLUMN,
                        4
                ).add(1, null)
        );
    }

    @Test
    public void testRemoveElementFails() {
        final SpreadsheetCellRangeReferenceSortList list = SpreadsheetCellRangeReferenceSortList.with(
                SpreadsheetColumnOrRowReferenceKind.COLUMN,
                3
        );

        final SpreadsheetCell a3 = SpreadsheetSelection.parseCell("A3")
                .setFormula(SpreadsheetFormula.EMPTY);

        list.cells[2] = a3;

        this.removeFails(
                list,
                a3
        );

        this.checkEquals(
                Lists.of(
                        null,
                        null,
                        a3
                ),
                list
        );
    }

    @Test
    public void testRemoveIndexFails() {
        final SpreadsheetCellRangeReferenceSortList list = SpreadsheetCellRangeReferenceSortList.with(
                SpreadsheetColumnOrRowReferenceKind.COLUMN,
                3
        );

        final SpreadsheetCell a3 = SpreadsheetSelection.parseCell("A3")
                .setFormula(SpreadsheetFormula.EMPTY);

        list.cells[2] = a3;

        this.removeIndexFails(
                list,
                2
        );

        this.checkEquals(
                Lists.of(
                        null,
                        null,
                        a3
                ),
                list
        );
    }

    // equals............................................................................................................

    @Test
    public void testEqualsDifferentSize() {
        this.checkNotEquals(
                SpreadsheetCellRangeReferenceSortList.with(
                        SpreadsheetColumnOrRowReferenceKind.COLUMN,
                        4
                )
        );
    }

    @Test
    public void testEqualsDifferentElementsSameSize() {
        final SpreadsheetCellRangeReferenceSortList list = SpreadsheetCellRangeReferenceSortList.with(
                SpreadsheetColumnOrRowReferenceKind.COLUMN,
                3
        );
        list.set(
                0,
                SpreadsheetSelection.A1.setFormula(
                        SpreadsheetFormula.EMPTY.setText("Different")
                )
        );

        final SpreadsheetCellRangeReferenceSortList other = SpreadsheetCellRangeReferenceSortList.with(
                SpreadsheetColumnOrRowReferenceKind.COLUMN,
                3
        );
        other.set(
                0,
                SpreadsheetSelection.A1.setFormula(
                        SpreadsheetFormula.EMPTY.setText("Different2")
                )
        );

        this.checkNotEquals(
                list,
                other
        );
    }


    @Override
    public SpreadsheetCellRangeReferenceSortList createObject() {
        return SpreadsheetCellRangeReferenceSortList.with(
                SpreadsheetColumnOrRowReferenceKind.COLUMN,
                3
        );
    }

    // Class............................................................................................................

    @Override
    public Class<SpreadsheetCellRangeReferenceSortList> type() {
        return SpreadsheetCellRangeReferenceSortList.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

}

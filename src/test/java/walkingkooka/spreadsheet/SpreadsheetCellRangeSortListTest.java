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

package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.collect.list.ListTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellRangeSortListTest implements ListTesting,
    ClassTesting<SpreadsheetCellRangeSortList>,
    HashCodeEqualsDefinedTesting2<SpreadsheetCellRangeSortList> {

    @Test
    public void testWithColumnA() {
        final SpreadsheetColumnReference a = SpreadsheetSelection.parseColumn("A");

        final SpreadsheetCellRangeSortList list = SpreadsheetCellRangeSortList.with(
            a,
            3
        );

        this.checkEquals(
            a,
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
    public void testWithColumnB() {
        final SpreadsheetColumnReference b = SpreadsheetSelection.parseColumn("B");

        final SpreadsheetCellRangeSortList list = SpreadsheetCellRangeSortList.with(
            b,
            3
        );

        this.checkEquals(
            b,
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
    public void testWithRow1() {
        final SpreadsheetRowReference row1 = SpreadsheetSelection.parseRow("1");

        final SpreadsheetCellRangeSortList list = SpreadsheetCellRangeSortList.with(
            row1,
            4
        );

        this.checkEquals(
            row1,
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
    public void testWithRow2() {
        final SpreadsheetRowReference row2 = SpreadsheetSelection.parseRow("2");

        final SpreadsheetCellRangeSortList list = SpreadsheetCellRangeSortList.with(
            row2,
            4
        );

        this.checkEquals(
            row2,
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
        final SpreadsheetCellRangeSortList list = SpreadsheetCellRangeSortList.with(
            SpreadsheetSelection.parseRow("2"),
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

        final SpreadsheetCellRangeSortList list = SpreadsheetCellRangeSortList.with(
            SpreadsheetSelection.parseColumn("A"),
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
        final SpreadsheetCellRangeSortList list = SpreadsheetCellRangeSortList.with(
            SpreadsheetSelection.parseColumn("A"),
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
        final SpreadsheetCellRangeSortList list = SpreadsheetCellRangeSortList.with(
            SpreadsheetSelection.parseColumn("A"),
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
        final SpreadsheetCellRangeSortList list = SpreadsheetCellRangeSortList.with(
            SpreadsheetSelection.parseColumn("A"),
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
            () -> SpreadsheetCellRangeSortList.with(
                SpreadsheetSelection.parseColumn("A"),
                4
            ).add(null)
        );
    }

    @Test
    public void testAddIndexFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> SpreadsheetCellRangeSortList.with(
                SpreadsheetSelection.parseColumn("A"),
                4
            ).add(1, null)
        );
    }

    @Test
    public void testRemoveElementFails() {
        final SpreadsheetCellRangeSortList list = SpreadsheetCellRangeSortList.with(
            SpreadsheetSelection.parseColumn("A"),
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
        final SpreadsheetCellRangeSortList list = SpreadsheetCellRangeSortList.with(
            SpreadsheetSelection.parseColumn("A"),
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
            SpreadsheetCellRangeSortList.with(
                SpreadsheetSelection.parseColumn("A"),
                4
            )
        );
    }

    @Test
    public void testEqualsDifferentElementsSameSize() {
        final SpreadsheetCellRangeSortList list = SpreadsheetCellRangeSortList.with(
            SpreadsheetSelection.parseColumn("A"),
            3
        );
        list.set(
            0,
            SpreadsheetSelection.A1.setFormula(
                SpreadsheetFormula.EMPTY.setText("Different")
            )
        );

        final SpreadsheetCellRangeSortList other = SpreadsheetCellRangeSortList.with(
            SpreadsheetSelection.parseColumn("A"),
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
    public SpreadsheetCellRangeSortList createObject() {
        return SpreadsheetCellRangeSortList.with(
            SpreadsheetSelection.parseColumn("A"),
            3
        );
    }

    // Class............................................................................................................

    @Override
    public Class<SpreadsheetCellRangeSortList> type() {
        return SpreadsheetCellRangeSortList.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

}

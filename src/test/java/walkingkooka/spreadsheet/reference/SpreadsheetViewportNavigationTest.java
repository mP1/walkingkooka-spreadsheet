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
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetViewportNavigationTest implements TreePrintableTesting,
        ClassTesting<SpreadsheetViewportNavigation> {

    // compact..........................................................................................................

    @Test
    public void testCompactNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetViewportNavigation.compact(null)
        );
    }

    @Test
    public void testCompactEmpty() {
        this.compactAndCheck(
                Lists.empty()
        );
    }

    @Test
    public void testCompactOne() {
        this.compactAndCheck(
                SpreadsheetViewportNavigation.upRow()
        );
    }

    @Test
    public void testCompactOne2() {
        this.compactAndCheck(
                SpreadsheetViewportNavigation.rightColumn()
        );
    }

    @Test
    public void testCompactManyNoOpposites() {
        this.compactAndCheck(
                SpreadsheetViewportNavigation.leftColumn()
        );
    }

    @Test
    public void testCompactManyNoOpposites2() {
        this.compactAndCheck(
                SpreadsheetViewportNavigation.rightColumn(),
                SpreadsheetViewportNavigation.rightColumn()
        );
    }

    @Test
    public void testCompactManyNoOpposites3() {
        this.compactAndCheck(
                SpreadsheetViewportNavigation.upRow(),
                SpreadsheetViewportNavigation.extendUpRow()
        );
    }

    @Test
    public void testCompactManyNoOpposites4() {
        this.compactAndCheck(
                SpreadsheetViewportNavigation.downRow(),
                SpreadsheetViewportNavigation.extendUpRow()
        );
    }

    @Test
    public void testCompactManyNoOpposites5() {
        this.compactAndCheck(
                SpreadsheetViewportNavigation.leftColumn(),
                SpreadsheetViewportNavigation.extendRightColumn(),
                SpreadsheetViewportNavigation.upRow(),
                SpreadsheetViewportNavigation.extendDownRow()
        );
    }

    @Test
    public void testCompactLeftRight() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.leftColumn(),
                        SpreadsheetViewportNavigation.rightColumn()
                )
        );
    }

    @Test
    public void testCompactUpDown() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.upRow(),
                        SpreadsheetViewportNavigation.downRow()
                )
        );
    }

    @Test
    public void testCompactRightLeft() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.rightColumn(),
                        SpreadsheetViewportNavigation.leftColumn()
                )
        );
    }

    @Test
    public void testCompactDownUp() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.downRow(),
                        SpreadsheetViewportNavigation.upRow()
                )
        );
    }


    @Test
    public void testCompactExtendLeftExtendRight() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.extendLeftColumn(),
                        SpreadsheetViewportNavigation.extendRightColumn()
                )
        );
    }

    @Test
    public void testCompactExtendUpExtendDown() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.extendUpRow(),
                        SpreadsheetViewportNavigation.extendDownRow()
                )
        );
    }

    @Test
    public void testCompactExtendRightExtendLeft() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.extendRightColumn(),
                        SpreadsheetViewportNavigation.extendLeftColumn()
                )
        );
    }

    @Test
    public void testCompactExtendDownExtendUp() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.extendDownRow(),
                        SpreadsheetViewportNavigation.extendUpRow()
                )
        );
    }

    @Test
    public void testCompactLeftRightLeftRight() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.leftColumn(),
                        SpreadsheetViewportNavigation.rightColumn(),
                        SpreadsheetViewportNavigation.leftColumn(),
                        SpreadsheetViewportNavigation.rightColumn()
                )
        );
    }

    @Test
    public void testCompactLeftRightLeftRightLeft() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.leftColumn(),
                        SpreadsheetViewportNavigation.rightColumn(),
                        SpreadsheetViewportNavigation.leftColumn(),
                        SpreadsheetViewportNavigation.rightColumn(),
                        SpreadsheetViewportNavigation.leftColumn()
                ),
                SpreadsheetViewportNavigation.leftColumn()
        );
    }

    @Test
    public void testCompactLeftRightLeftRightRight() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.leftColumn(),
                        SpreadsheetViewportNavigation.rightColumn(),
                        SpreadsheetViewportNavigation.leftColumn(),
                        SpreadsheetViewportNavigation.rightColumn(),
                        SpreadsheetViewportNavigation.rightColumn()
                ),
                SpreadsheetViewportNavigation.rightColumn()
        );
    }

    @Test
    public void testCompactLeftRightLeftRightUp() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.leftColumn(),
                        SpreadsheetViewportNavigation.rightColumn(),
                        SpreadsheetViewportNavigation.leftColumn(),
                        SpreadsheetViewportNavigation.rightColumn(),
                        SpreadsheetViewportNavigation.upRow()
                ),
                SpreadsheetViewportNavigation.upRow()
        );
    }

    @Test
    public void testCompactLeftUpRightDown() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.leftColumn(),
                        SpreadsheetViewportNavigation.upRow(),
                        SpreadsheetViewportNavigation.rightColumn(),
                        SpreadsheetViewportNavigation.downRow()
                )
        );
    }

    @Test
    public void testCompactExtendLeftExtendUpExtendRightExtendDown() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.extendLeftColumn(),
                        SpreadsheetViewportNavigation.extendUpRow(),
                        SpreadsheetViewportNavigation.extendRightColumn(),
                        SpreadsheetViewportNavigation.extendDownRow()
                )
        );
    }

    @Test
    public void testCompactExtendLeftUpExtendRightDown() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.extendLeftColumn(),
                        SpreadsheetViewportNavigation.upRow(),
                        SpreadsheetViewportNavigation.extendRightColumn(),
                        SpreadsheetViewportNavigation.downRow()
                )
        );
    }

    @Test
    public void testCompactExtendLeftUpExtendRightDownDown() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.extendLeftColumn(),
                        SpreadsheetViewportNavigation.upRow(),
                        SpreadsheetViewportNavigation.extendRightColumn(),
                        SpreadsheetViewportNavigation.downRow(),
                        SpreadsheetViewportNavigation.downRow()
                ),
                SpreadsheetViewportNavigation.downRow()
        );
    }

    @Test
    public void testCompactExtendLeftUpExtendRightDownRightExtendLeft() {
        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.extendLeftColumn(),
                        SpreadsheetViewportNavigation.upRow(),
                        SpreadsheetViewportNavigation.extendRightColumn(),
                        SpreadsheetViewportNavigation.downRow(),
                        SpreadsheetViewportNavigation.rightColumn(),
                        SpreadsheetViewportNavigation.extendLeftColumn()
                ),
                SpreadsheetViewportNavigation.rightColumn(),
                SpreadsheetViewportNavigation.extendLeftColumn()
        );
    }

    @Test
    public void testCompactExtendLeftUpExtendRightDownRightExtendLeftSelectCell() {
        final SpreadsheetViewportNavigation cell = SpreadsheetViewportNavigation.cell(SpreadsheetSelection.A1);

        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.extendLeftColumn(),
                        SpreadsheetViewportNavigation.upRow(),
                        SpreadsheetViewportNavigation.extendRightColumn(),
                        SpreadsheetViewportNavigation.downRow(),
                        SpreadsheetViewportNavigation.rightColumn(),
                        SpreadsheetViewportNavigation.extendLeftColumn(),
                        cell
                ),
                cell
        );
    }

    @Test
    public void testCompactExtendLeftUpExtendRightDownRightExtendLeftSelectCellDownRow() {
        final SpreadsheetViewportNavigation cell = SpreadsheetViewportNavigation.cell(SpreadsheetSelection.A1);

        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.extendLeftColumn(),
                        SpreadsheetViewportNavigation.upRow(),
                        SpreadsheetViewportNavigation.extendRightColumn(),
                        SpreadsheetViewportNavigation.downRow(),
                        SpreadsheetViewportNavigation.rightColumn(),
                        SpreadsheetViewportNavigation.extendLeftColumn(),
                        cell,
                        SpreadsheetViewportNavigation.downRow()
                ),
                cell,
                SpreadsheetViewportNavigation.downRow()
        );
    }

    @Test
    public void testCompactExtendLeftUpExtendRightDownRightExtendLeftSelectColumn() {
        final SpreadsheetViewportNavigation column = SpreadsheetViewportNavigation.column(SpreadsheetSelection.A1.column());

        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.extendLeftColumn(),
                        SpreadsheetViewportNavigation.upRow(),
                        SpreadsheetViewportNavigation.extendRightColumn(),
                        SpreadsheetViewportNavigation.downRow(),
                        SpreadsheetViewportNavigation.rightColumn(),
                        SpreadsheetViewportNavigation.extendLeftColumn(),
                        column
                ),
                column
        );
    }

    @Test
    public void testCompactExtendLeftUpExtendRightDownRightExtendLeftSelectColumnDownRow() {
        final SpreadsheetViewportNavigation column = SpreadsheetViewportNavigation.column(SpreadsheetSelection.A1.column());

        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.extendLeftColumn(),
                        SpreadsheetViewportNavigation.upRow(),
                        SpreadsheetViewportNavigation.extendRightColumn(),
                        SpreadsheetViewportNavigation.downRow(),
                        SpreadsheetViewportNavigation.rightColumn(),
                        SpreadsheetViewportNavigation.extendLeftColumn(),
                        column,
                        SpreadsheetViewportNavigation.downRow()
                ),
                column,
                SpreadsheetViewportNavigation.downRow()
        );
    }

    @Test
    public void testCompactUpRowSelectCellDownRowExtendCell() {
        final SpreadsheetViewportNavigation cell = SpreadsheetViewportNavigation.cell(SpreadsheetSelection.A1);
        final SpreadsheetViewportNavigation extendCell = SpreadsheetViewportNavigation.extendCell(SpreadsheetSelection.parseCell("B2"));

        this.compactAndCheck(
                Lists.of(
                        SpreadsheetViewportNavigation.upRow(),
                        cell,
                        SpreadsheetViewportNavigation.downRow(),
                        extendCell
                ),
                cell,
                SpreadsheetViewportNavigation.downRow(),
                extendCell
        );
    }

    private void compactAndCheck(final SpreadsheetViewportNavigation... expected) {
        this.compactAndCheck(
                Lists.of(expected),
                expected
        );
    }


    private void compactAndCheck(final List<SpreadsheetViewportNavigation> in,
                                 final SpreadsheetViewportNavigation... expected) {
        this.compactAndCheck(
                in,
                Lists.of(expected)
        );
    }

    private void compactAndCheck(final List<SpreadsheetViewportNavigation> in,
                                 final List<SpreadsheetViewportNavigation> expected) {
        this.checkEquals(
                expected,
                SpreadsheetViewportNavigation.compact(in),
                () -> "compact " + in
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetViewportNavigation> type() {
        return SpreadsheetViewportNavigation.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}

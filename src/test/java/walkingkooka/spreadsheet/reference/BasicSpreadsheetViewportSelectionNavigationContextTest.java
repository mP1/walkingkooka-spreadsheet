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
import walkingkooka.ToStringTesting;
import walkingkooka.predicate.Predicates;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.CharacterConstant;

import java.util.TreeSet;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetViewportSelectionNavigationContextTest implements ClassTesting<BasicSpreadsheetViewportSelectionNavigationContext>,
        SpreadsheetViewportSelectionNavigationContextTesting<BasicSpreadsheetViewportSelectionNavigationContext>,
        ToStringTesting<BasicSpreadsheetViewportSelectionNavigationContext> {

    @Test
    public void testWithNullColumnHiddenFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetViewportSelectionNavigationContext.with(
                        null,
                        Predicates.fake()
                )
        );
    }

    @Test
    public void testWithNullRowHiddenFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetViewportSelectionNavigationContext.with(
                        Predicates.fake(),
                        null
                )
        );
    }

    @Test
    public void testIsColumnHidden() {
        final SpreadsheetColumnReference column = SpreadsheetSelection.parseColumn("B");

        final BasicSpreadsheetViewportSelectionNavigationContext context = BasicSpreadsheetViewportSelectionNavigationContext.with(
                Predicates.is(column),
                Predicates.fake()
        );
        this.isColumnHiddenAndCheck(
                context,
                column,
                true
        );

        this.isColumnHiddenAndCheck(
                context,
                column.add(1),
                false
        );
    }

    @Test
    public void testIsRowHidden() {
        final SpreadsheetRowReference row = SpreadsheetSelection.parseRow("123");

        final BasicSpreadsheetViewportSelectionNavigationContext context = BasicSpreadsheetViewportSelectionNavigationContext.with(
                Predicates.fake(),
                Predicates.is(row)
        );

        this.isRowHiddenAndCheck(
                context,
                row,
                true
        );

        this.isRowHiddenAndCheck(
                context,
                row.add(1),
                false
        );
    }

    // leftColumn.......................................................................................................

    @Test
    public void testLeftFirstColumnNotHidden() {
        this.leftColumnAndCheck(
                "",
                "A",
                "A"
        );
    }

    @Test
    public void testLeftFirstColumnHidden() {
        this.leftColumnAndCheck(
                "A",
                "A"
        );
    }

    @Test
    public void testLeftAllColumnsHiddenIncludingGiven() {
        this.leftColumnAndCheck(
                "A,B,C",
                "C"
        );
    }

    @Test
    public void testLeftColumn() {
        this.leftColumnAndCheck(
                "",
                "B",
                "A"
        );
    }

    @Test
    public void testLeftColumn2() {
        this.leftColumnAndCheck(
                "",
                "D",
                "C"
        );
    }

    @Test
    public void testLeftColumnFirstColumn() {
        this.leftColumnAndCheck(
                "",
                "A",
                "A"
        );
    }

    @Test
    public void testLeftColumnSkips() {
        this.leftColumnAndCheck(
                "C,D",
                "E",
                "B"
        );
    }

    @Test
    public void testLeftColumnSkipsFirstColumn() {
        this.leftColumnAndCheck(
                "B,C",
                "D",
                "A"
        );
    }

    @Test
    public void testLeftColumnAllLeftHidden() {
        this.leftColumnAndCheck(
                "A,B",
                "C",
                "C"
        );
    }

    private void leftColumnAndCheck(final String columnHidden,
                                    final String column) {
        this.leftColumnAndCheck(
                BasicSpreadsheetViewportSelectionNavigationContext.with(
                        columnHidden(columnHidden),
                        Predicates.fake()
                ),
                column
        );
    }

    private void leftColumnAndCheck(final String columnHidden,
                                    final String column,
                                    final String expected) {
        this.leftColumnAndCheck(
                BasicSpreadsheetViewportSelectionNavigationContext.with(
                        columnHidden(columnHidden),
                        Predicates.fake()
                ),
                column,
                expected
        );
    }

    // rightColumn......................................................................................................

    @Test
    public void testRightLastColumnHidden() {
        final SpreadsheetColumnReference last = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        this.rightColumnAndCheck(
                last.toString(),
                last.toString()
        );
    }

    @Test
    public void testRightAllColumnsHiddenIncludingGiven() {
        final SpreadsheetColumnReference last = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        this.rightColumnAndCheck(
                last.add(-2) + "," + last.add(-1) + "," + last,
                last.add(-2).toString()
        );
    }

    @Test
    public void testRightColumn() {
        this.rightColumnAndCheck(
                "B",
                "B",
                "C"
        );
    }

    @Test
    public void testRightColumn2() {
        this.rightColumnAndCheck(
                "C",
                "B",
                "D"
        );
    }

    @Test
    public void testRightColumnSkips() {
        this.rightColumnAndCheck(
                "B,C",
                "B",
                "D"
        );
    }

    @Test
    public void testRightColumnSkipsLastColumn() {
        final SpreadsheetColumnReference last = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        this.rightColumnAndCheck(
                last.add(-2) + "," + last.add(-1),
                last.add(-3).toString(),
                last.toString()
        );
    }

    @Test
    public void testRightColumnAllRightHidden() {
        final SpreadsheetColumnReference last = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        this.rightColumnAndCheck(
                last.add(-1) + "," + last,
                last.add(-2).toString(),
                last.add(-2).toString()
        );
    }

    private void rightColumnAndCheck(final String columnHidden,
                                     final String column) {
        this.rightColumnAndCheck(
                BasicSpreadsheetViewportSelectionNavigationContext.with(
                        columnHidden(columnHidden),
                        Predicates.fake()
                ),
                column
        );
    }

    private void rightColumnAndCheck(final String columnHidden,
                                     final String column,
                                     final String expected) {
        this.rightColumnAndCheck(
                BasicSpreadsheetViewportSelectionNavigationContext.with(
                        columnHidden(columnHidden),
                        Predicates.fake()
                ),
                column,
                expected
        );
    }

    private Predicate<SpreadsheetColumnReference> columnHidden(final String columns) {
        return Predicates.setContains(
                new TreeSet<>(
                        CharacterConstant.COMMA.parse(
                                columns,
                                SpreadsheetSelection::parseColumn
                        )
                )
        );
    }

    // upRow...................................................................................................

    @Test
    public void testUpFirstRowNotHidden() {
        this.upRowAndCheck(
                "",
                "1",
                "1"
        );
    }

    @Test
    public void testUpFirstRowHidden() {
        this.upRowAndCheck(
                "1",
                "1"
        );
    }

    @Test
    public void testUpAllRowsHiddenIncludingGiven() {
        this.upRowAndCheck(
                "1,2,3",
                "3"
        );
    }

    @Test
    public void testUpRow() {
        this.upRowAndCheck(
                "",
                "2",
                "1"
        );
    }

    @Test
    public void testUpRow2() {
        this.upRowAndCheck(
                "",
                "4",
                "3"
        );
    }

    @Test
    public void testUpRowFirstRow() {
        this.upRowAndCheck(
                "",
                "1",
                "1"
        );
    }

    @Test
    public void testUpRowSkips() {
        this.upRowAndCheck(
                "3,4",
                "5",
                "2"
        );
    }

    @Test
    public void testUpRowSkipsFirstRow() {
        this.upRowAndCheck(
                "2,3",
                "4",
                "1"
        );
    }

    @Test
    public void testUpRowAllUpHidden() {
        this.upRowAndCheck(
                "1,2",
                "3",
                "3"
        );
    }

    private void upRowAndCheck(final String rowHidden,
                               final String row) {
        this.upRowAndCheck(
                BasicSpreadsheetViewportSelectionNavigationContext.with(
                        Predicates.fake(),
                        this.rowHidden(rowHidden)
                ),
                row
        );
    }

    private void upRowAndCheck(final String rowHidden,
                               final String row,
                               final String expected) {
        this.upRowAndCheck(
                BasicSpreadsheetViewportSelectionNavigationContext.with(
                        Predicates.fake(),
                        this.rowHidden(rowHidden)
                ),
                row,
                expected
        );
    }

    // downRow..........................................................................................................

    @Test
    public void testDownLastRowHidden() {
        final SpreadsheetRowReference last = SpreadsheetReferenceKind.RELATIVE.lastRow();

        this.downRowAndCheck(
                last.toString(),
                last.toString()
        );
    }

    @Test
    public void testDownAllRowsHiddenIncludingGiven() {
        final SpreadsheetRowReference last = SpreadsheetReferenceKind.RELATIVE.lastRow();

        this.downRowAndCheck(
                last.add(-2) + "," + last.add(-1) + "," + last,
                last.add(-2).toString()
        );
    }

    @Test
    public void testDownRow() {
        this.downRowAndCheck(
                "2",
                "2",
                "3"
        );
    }

    @Test
    public void testDownRow2() {
        this.downRowAndCheck(
                "3",
                "2",
                "4"
        );
    }

    @Test
    public void testDownRowSkips() {
        this.downRowAndCheck(
                "2,3",
                "2",
                "4"
        );
    }

    @Test
    public void testDownRowSkipsLastRow() {
        final SpreadsheetRowReference last = SpreadsheetReferenceKind.RELATIVE.lastRow();

        this.downRowAndCheck(
                last.add(-2) + "," + last.add(-1),
                last.add(-3).toString(),
                last.toString()
        );
    }

    @Test
    public void testDownRowAllDownHidden() {
        final SpreadsheetRowReference last = SpreadsheetReferenceKind.RELATIVE.lastRow();

        this.downRowAndCheck(
                last.add(-1) + "," + last,
                last.add(-2).toString(),
                last.add(-2).toString()
        );
    }

    private void downRowAndCheck(final String rowHidden,
                                 final String row) {
        this.downRowAndCheck(
                BasicSpreadsheetViewportSelectionNavigationContext.with(
                        Predicates.fake(),
                        rowHidden(rowHidden)
                ),
                row
        );
    }

    private void downRowAndCheck(final String rowHidden,
                                 final String row,
                                 final String expected) {
        this.downRowAndCheck(
                BasicSpreadsheetViewportSelectionNavigationContext.with(
                        Predicates.fake(),
                        rowHidden(rowHidden)
                ),
                row,
                expected
        );
    }

    private Predicate<SpreadsheetRowReference> rowHidden(final String rows) {
        return Predicates.setContains(
                new TreeSet<>(
                        CharacterConstant.COMMA.parse(
                                rows,
                                SpreadsheetSelection::parseRow
                        )
                )
        );
    }

    // Object...........................................................................................................

    @Test
    public void testToString() {
        final Predicate<SpreadsheetColumnReference> column = Predicates.fake();
        final Predicate<SpreadsheetRowReference> row = Predicates.fake();

        this.toStringAndCheck(
                BasicSpreadsheetViewportSelectionNavigationContext.with(
                        column,
                        row
                ),
                column + " " + row
        );
    }

    @Override
    public BasicSpreadsheetViewportSelectionNavigationContext createContext() {
        return BasicSpreadsheetViewportSelectionNavigationContext.with(
                Predicates.fake(),
                Predicates.fake()
        );
    }

    @Override
    public Class<BasicSpreadsheetViewportSelectionNavigationContext> type() {
        return BasicSpreadsheetViewportSelectionNavigationContext.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}

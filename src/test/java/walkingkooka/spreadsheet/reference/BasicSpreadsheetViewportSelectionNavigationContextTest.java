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

    // leftColumnSkipHidden...................................................................................................

    @Test
    public void testLeftFirstColumnNotHidden() {
        this.leftColumnSkipHiddenAndCheck(
                "",
                "A",
                "A"
        );
    }

    @Test
    public void testLeftFirstColumnHidden() {
        this.leftColumnSkipHiddenAndCheck(
                "A",
                "A"
        );
    }

    @Test
    public void testLeftAllColumnsHiddenIncludingGiven() {
        this.leftColumnSkipHiddenAndCheck(
                "A,B,C",
                "C"
        );
    }

    @Test
    public void testLeftColumnSkipHidden() {
        this.leftColumnSkipHiddenAndCheck(
                "",
                "B",
                "A"
        );
    }

    @Test
    public void testLeftColumnSkipHidden2() {
        this.leftColumnSkipHiddenAndCheck(
                "",
                "D",
                "C"
        );
    }

    @Test
    public void testLeftColumnSkipHiddenFirstColumn() {
        this.leftColumnSkipHiddenAndCheck(
                "",
                "A",
                "A"
        );
    }

    @Test
    public void testLeftColumnSkipHiddenSkips() {
        this.leftColumnSkipHiddenAndCheck(
                "C,D",
                "E",
                "B"
        );
    }

    @Test
    public void testLeftColumnSkipHiddenSkipsFirstColumn() {
        this.leftColumnSkipHiddenAndCheck(
                "B,C",
                "D",
                "A"
        );
    }

    @Test
    public void testLeftColumnSkipHiddenAllLeftHidden() {
        this.leftColumnSkipHiddenAndCheck(
                "A,B",
                "C",
                "C"
        );
    }

    private void leftColumnSkipHiddenAndCheck(final String columnHidden,
                                              final String column) {
        this.leftColumnSkipHiddenAndCheck(
                BasicSpreadsheetViewportSelectionNavigationContext.with(
                        columnHidden(columnHidden),
                        Predicates.fake()
                ),
                column
        );
    }

    private void leftColumnSkipHiddenAndCheck(final String columnHidden,
                                              final String column,
                                              final String expected) {
        this.leftColumnSkipHiddenAndCheck(
                BasicSpreadsheetViewportSelectionNavigationContext.with(
                        columnHidden(columnHidden),
                        Predicates.fake()
                ),
                column,
                expected
        );
    }

    // rightColumnSkipHidden...................................................................................................

    @Test
    public void testRightLastColumnHidden() {
        final SpreadsheetColumnReference last = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        this.rightColumnSkipHiddenAndCheck(
                last.toString(),
                last.toString()
        );
    }

    @Test
    public void testRightAllColumnsHiddenIncludingGiven() {
        final SpreadsheetColumnReference last = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        this.rightColumnSkipHiddenAndCheck(
                last.add(-2) + "," + last.add(-1) + "," + last,
                last.add(-2).toString()
        );
    }

    @Test
    public void testRightColumnSkipHidden() {
        this.rightColumnSkipHiddenAndCheck(
                "B",
                "B",
                "C"
        );
    }

    @Test
    public void testRightColumnSkipHidden2() {
        this.rightColumnSkipHiddenAndCheck(
                "C",
                "B",
                "D"
        );
    }

    @Test
    public void testRightColumnSkipHiddenSkips() {
        this.rightColumnSkipHiddenAndCheck(
                "B,C",
                "B",
                "D"
        );
    }

    @Test
    public void testRightColumnSkipHiddenSkipsLastColumn() {
        final SpreadsheetColumnReference last = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        this.rightColumnSkipHiddenAndCheck(
                last.add(-2) + "," + last.add(-1),
                last.add(-3).toString(),
                last.toString()
        );
    }

    @Test
    public void testRightColumnSkipHiddenAllRightHidden() {
        final SpreadsheetColumnReference last = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        this.rightColumnSkipHiddenAndCheck(
                last.add(-1) + "," + last,
                last.add(-2).toString(),
                last.add(-2).toString()
        );
    }

    private void rightColumnSkipHiddenAndCheck(final String columnHidden,
                                               final String column) {
        this.rightColumnSkipHiddenAndCheck(
                BasicSpreadsheetViewportSelectionNavigationContext.with(
                        columnHidden(columnHidden),
                        Predicates.fake()
                ),
                column
        );
    }

    private void rightColumnSkipHiddenAndCheck(final String columnHidden,
                                               final String column,
                                               final String expected) {
        this.rightColumnSkipHiddenAndCheck(
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

    // upRowSkipHidden...................................................................................................

    @Test
    public void testUpFirstRowNotHidden() {
        this.upRowSkipHiddenAndCheck(
                "",
                "1",
                "1"
        );
    }

    @Test
    public void testUpFirstRowHidden() {
        this.upRowSkipHiddenAndCheck(
                "1",
                "1"
        );
    }

    @Test
    public void testUpAllRowsHiddenIncludingGiven() {
        this.upRowSkipHiddenAndCheck(
                "1,2,3",
                "3"
        );
    }

    @Test
    public void testUpRowSkipHidden() {
        this.upRowSkipHiddenAndCheck(
                "",
                "2",
                "1"
        );
    }

    @Test
    public void testUpRowSkipHidden2() {
        this.upRowSkipHiddenAndCheck(
                "",
                "4",
                "3"
        );
    }

    @Test
    public void testUpRowSkipHiddenFirstRow() {
        this.upRowSkipHiddenAndCheck(
                "",
                "1",
                "1"
        );
    }

    @Test
    public void testUpRowSkipHiddenSkips() {
        this.upRowSkipHiddenAndCheck(
                "3,4",
                "5",
                "2"
        );
    }

    @Test
    public void testUpRowSkipHiddenSkipsFirstRow() {
        this.upRowSkipHiddenAndCheck(
                "2,3",
                "4",
                "1"
        );
    }

    @Test
    public void testUpRowSkipHiddenAllUpHidden() {
        this.upRowSkipHiddenAndCheck(
                "1,2",
                "3",
                "3"
        );
    }

    private void upRowSkipHiddenAndCheck(final String rowHidden,
                                         final String row) {
        this.upRowSkipHiddenAndCheck(
                BasicSpreadsheetViewportSelectionNavigationContext.with(
                        Predicates.fake(),
                        this.rowHidden(rowHidden)
                ),
                row
        );
    }

    private void upRowSkipHiddenAndCheck(final String rowHidden,
                                         final String row,
                                         final String expected) {
        this.upRowSkipHiddenAndCheck(
                BasicSpreadsheetViewportSelectionNavigationContext.with(
                        Predicates.fake(),
                        this.rowHidden(rowHidden)
                ),
                row,
                expected
        );
    }

    // downRowSkipHidden...................................................................................................

    @Test
    public void testDownLastRowHidden() {
        final SpreadsheetRowReference last = SpreadsheetReferenceKind.RELATIVE.lastRow();

        this.downRowSkipHiddenAndCheck(
                last.toString(),
                last.toString()
        );
    }

    @Test
    public void testDownAllRowsHiddenIncludingGiven() {
        final SpreadsheetRowReference last = SpreadsheetReferenceKind.RELATIVE.lastRow();

        this.downRowSkipHiddenAndCheck(
                last.add(-2) + "," + last.add(-1) + "," + last,
                last.add(-2).toString()
        );
    }

    @Test
    public void testDownRowSkipHidden() {
        this.downRowSkipHiddenAndCheck(
                "2",
                "2",
                "3"
        );
    }

    @Test
    public void testDownRowSkipHidden2() {
        this.downRowSkipHiddenAndCheck(
                "3",
                "2",
                "4"
        );
    }

    @Test
    public void testDownRowSkipHiddenSkips() {
        this.downRowSkipHiddenAndCheck(
                "2,3",
                "2",
                "4"
        );
    }

    @Test
    public void testDownRowSkipHiddenSkipsLastRow() {
        final SpreadsheetRowReference last = SpreadsheetReferenceKind.RELATIVE.lastRow();

        this.downRowSkipHiddenAndCheck(
                last.add(-2) + "," + last.add(-1),
                last.add(-3).toString(),
                last.toString()
        );
    }

    @Test
    public void testDownRowSkipHiddenAllDownHidden() {
        final SpreadsheetRowReference last = SpreadsheetReferenceKind.RELATIVE.lastRow();

        this.downRowSkipHiddenAndCheck(
                last.add(-1) + "," + last,
                last.add(-2).toString(),
                last.add(-2).toString()
        );
    }

    private void downRowSkipHiddenAndCheck(final String rowHidden,
                                           final String row) {
        this.downRowSkipHiddenAndCheck(
                BasicSpreadsheetViewportSelectionNavigationContext.with(
                        Predicates.fake(),
                        rowHidden(rowHidden)
                ),
                row
        );
    }

    private void downRowSkipHiddenAndCheck(final String rowHidden,
                                           final String row,
                                           final String expected) {
        this.downRowSkipHiddenAndCheck(
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

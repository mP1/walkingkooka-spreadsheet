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
import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.Range;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.net.UrlFragment;
import walkingkooka.net.http.server.hateos.HateosResourceTesting;
import walkingkooka.predicate.Predicates;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportAnchor;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportRectangle;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportWindows;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.validation.ValidationReferenceTesting;
import walkingkooka.visit.Visiting;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellReferenceTest extends SpreadsheetCellReferenceOrRangeTestCase<SpreadsheetCellReference>
    implements ComparableTesting2<SpreadsheetCellReference>,
    HateosResourceTesting<SpreadsheetCellReference, String>,
    CanReplaceReferencesTesting<SpreadsheetCellReference>,
    ValidationReferenceTesting {

    private final static int COLUMN = 123;
    private final static int ROW = 456;

    @Test
    public void testWithNullColumnFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetCellReference.with(null, row()));
    }

    @Test
    public void testWithNullRowFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetCellReference.with(column(), null));
    }

    @Test
    public void testWith() {
        final SpreadsheetColumnReference column = this.column();
        final SpreadsheetRowReference row = this.row();
        final SpreadsheetCellReference cell = SpreadsheetCellReference.with(column, row);
        this.checkColumn(cell, column);
        this.checkRow(cell, row);
    }

    // notFound.........................................................................................................

    @Test
    public void testNotFound() {
        this.notFoundTextAndCheck(
            SpreadsheetSelection.parseCell("Z99"),
            "Cell not found: \"Z99\""
        );
    }

    // text............................................................................................................

    @Test
    public void testText() {
        this.textAndCheck("A1");
    }

    // setColumn..................................................................................................

    @Test
    public void testSetColumnNullFails() {
        assertThrows(NullPointerException.class, () -> this.createSelection().setColumn(null));
    }

    @Test
    public void testSetColumnSame() {
        final SpreadsheetCellReference cell = this.createSelection();
        assertSame(cell, cell.setColumn(this.column(COLUMN)));
    }

    @Test
    public void testSetColumnDifferent() {
        final SpreadsheetCellReference cell = this.createSelection();
        final SpreadsheetColumnReference differentColumn = this.column(99);
        final SpreadsheetCellReference different = cell.setColumn(differentColumn);
        this.checkRow(different, this.row());
        this.checkColumn(different, differentColumn);
    }

    // setRow..................................................................................................

    @Test
    public void testSetRowNullFails() {
        assertThrows(NullPointerException.class, () -> this.createSelection().setRow(null));
    }

    @Test
    public void testSetRowSame() {
        final SpreadsheetCellReference cell = this.createSelection();
        assertSame(cell, cell.setRow(this.row(ROW)));
    }

    @Test
    public void testSetRowDifferent() {
        final SpreadsheetCellReference cell = this.createSelection();
        final SpreadsheetRowReference differentRow = this.row(99);
        final SpreadsheetCellReference different = cell.setRow(differentRow);
        this.checkColumn(different, this.column());
        this.checkRow(different, differentRow);
    }

    // count............................................................................................................

    @Test
    public void testCountA1() {
        this.countAndCheck("A1", 1);
    }

    @Test
    public void testCountB2() {
        this.countAndCheck("B2", 1);
    }

    // isAll..........................................................................................................

    @Test
    public void testIsAll() {
        this.isAllAndCheck("A1", false);
    }

    // isFirst..........................................................................................................

    @Test
    public void testIsFirstA1() {
        this.isFirstAndCheck("A1", true);
    }

    @Test
    public void testIsFirstA1Absolute() {
        this.isFirstAndCheck("$A$1", true);
    }

    @Test
    public void testIsFirstA2() {
        this.isFirstAndCheck("A2", false);
    }

    // isLast..........................................................................................................

    @Test
    public void testIsLastA1() {
        this.isLastAndCheck("A1", false);
    }

    @Test
    public void testIsLastA1Absolute() {
        this.isLastAndCheck("$A$1", false);
    }

    @Test
    public void testIsLast() {
        this.isLastAndCheck(
            SpreadsheetReferenceKind.RELATIVE.lastColumn().setRow(SpreadsheetReferenceKind.RELATIVE.lastRow()),
            true
        );
    }

    // SetFormula.......................................................................................................

    @Test
    public void testSetFormula() {
        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY.setText("=1");
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("B2");

        final SpreadsheetCell cell = reference.setFormula(formula);

        this.checkEquals(
            reference,
            cell.reference(),
            "reference"
        );
        this.checkEquals(
            formula,
            cell.formula(),
            "formula"
        );
    }

    // comparatorNamesBoundsCheck.......................................................................................

    @Test
    public void testComparatorNamesBoundsCheckWithColumnComparatorsOutOfBoundsFails() {
        this.comparatorNamesBoundsCheckAndCheckFails(
            "A1",
            "A=TEXT;B=TEXT;ZZ=TEXT",
            "Invalid column(s) B, ZZ are not within A1"
        );
    }

    @Test
    public void testComparatorNamesBoundsCheckWithRowComparatorsOutOfBoundsFails() {
        this.comparatorNamesBoundsCheckAndCheckFails(
            "A1",
            "1=TEXT;2=TEXT;99=TEXT",
            "Invalid row(s) 2, 99 are not within A1"
        );
    }

    @Test
    public void testComparatorNamesBoundsCheckWithColumns() {
        this.comparatorNamesBoundsCheckAndCheck(
            "A1",
            "A=text UP"
        );
    }

    @Test
    public void testComparatorNamesBoundsCheckWithRows() {
        this.comparatorNamesBoundsCheckAndCheck(
            "A1",
            "1=text UP"
        );
    }

    // toCell...........................................................................................................

    @Test
    public void testToCell() {
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("Z9");

        this.toCellAndCheck(
            reference,
            reference
        );
    }

    // toCellRangeResolvingLabels........................................................................................

    @Test
    public void testToCellRange() {
        this.toCellRangeAndCheck(
            "A1",
            "A1"
        );
    }

    @Test
    public void testToCellRange2() {
        this.toCellRangeAndCheck(
            "B2",
            "B2"
        );
    }

    // toColumn.........................................................................................................

    @Test
    public void testToColumn() {
        this.toColumnAndCheck(
            SpreadsheetSelection.A1,
            SpreadsheetSelection.parseColumn("A")
        );
    }

    // toColumnRange....................................................................................................

    @Test
    public void testToColumnRange() {
        this.toColumnRangeAndCheck(
            SpreadsheetSelection.A1,
            SpreadsheetSelection.parseColumnRange("A")
        );
    }

    @Test
    public void testToColumnRange2() {
        this.toColumnRangeAndCheck(
            SpreadsheetSelection.parseCell("B2"),
            SpreadsheetSelection.parseColumnRange("B")
        );
    }

    // toColumnOrColumnRange............................................................................................

    @Test
    public void testToColumnOrColumnRange() {
        final SpreadsheetCellReference selection = this.createSelection();
        this.toColumnOrColumnRangeAndCheck(
            selection,
            selection.toColumn()
        );
    }

    // toRow............................................................................................................

    @Test
    public void testToRow() {
        this.toRowAndCheck(
            SpreadsheetSelection.A1,
            SpreadsheetSelection.parseRow("1")
        );
    }

    // toRowRange.......................................................................................................

    @Test
    public void testToRowRange() {
        this.toRowRangeAndCheck(
            SpreadsheetSelection.A1,
            SpreadsheetSelection.parseRowRange("1")
        );
    }

    @Test
    public void testToRowRange2() {
        this.toRowRangeAndCheck(
            SpreadsheetSelection.parseCellRange("B2"),
            SpreadsheetSelection.parseRowRange("2")
        );
    }

    // toRowOrRowRange..................................................................................................

    @Test
    public void testToRowOrRowRange() {
        final SpreadsheetCellReference selection = this.createSelection();
        this.toRowOrRowRangeAndCheck(
            selection,
            selection.toRow()
        );
    }

    // toScalar.........................................................................................................

    @Test
    public void testToScalar() {
        this.toScalarAndCheck(
            this.createSelection()
        );
    }

    // SpreadsheetViewportRectangle.....................................................................................

    @Test
    public void testViewportRectangle() {
        final SpreadsheetCellReference selection = this.createSelection();
        final double width = 30.5;
        final double height = 40.5;

        this.viewportRectangleAndCheck(
            selection,
            width,
            height,
            SpreadsheetViewportRectangle.with(
                selection,
                width,
                height
            )
        );
    }

    private void viewportRectangleAndCheck(final SpreadsheetCellReference selection,
                                           final double width,
                                           final double height,
                                           final SpreadsheetViewportRectangle expected) {
        this.checkEquals(
            expected,
            selection.viewportRectangle(width, height),
            () -> selection + " viewportRectangle " + width + "," + height
        );
    }

    // containsAll......................................................................................................

    @Test
    public void testContainsAllInside() {
        this.containsAllAndCheck(
            SpreadsheetSelection.A1,
            SpreadsheetViewportWindows.parse("A1"),
            true
        );
    }

    @Test
    public void testContainsAllInsideAndOutside() {
        this.containsAllAndCheck(
            SpreadsheetSelection.A1,
            SpreadsheetViewportWindows.parse("A1,B2"),
            false
        );
    }

    @Test
    public void testContainsAllOutside() {
        this.containsAllAndCheck(
            SpreadsheetSelection.A1,
            SpreadsheetViewportWindows.parse("B2"),
            false
        );
    }

    // test.............................................................................................................

    @Test
    public void testTestSameCell() {
        this.testTrue(this.createSelection());
    }

    @Test
    public void testtestEqualsDifferentCell() {
        this.testFalse(
            SpreadsheetSelection.A1,
            SpreadsheetSelection.parseCell("B2")
        );
    }

    @Test
    public void testtestEqualsDifferentColumn() {
        this.testFalse(
            SpreadsheetSelection.A1,
            SpreadsheetSelection.parseColumn("B")
        );
    }

    @Test
    public void testtestEqualsDifferentRow() {
        this.testFalse(
            SpreadsheetSelection.A1,
            SpreadsheetSelection.parseRow("2")
        );
    }

    // testCell.........................................................................................................

    @Test
    public void testTestCellDifferentColumnFalse() {
        final SpreadsheetCellReference selection = this.createSelection();
        this.testCellAndCheck(
            selection,
            selection.setColumn(selection.column().add(1)),
            false
        );
    }

    @Test
    public void testTestCellDifferentRowFalse() {
        final SpreadsheetCellReference selection = this.createSelection();
        this.testCellAndCheck(
            selection,
            selection.setRow(selection.row().add(1)),
            false
        );
    }

    @Test
    public void testTestCellDifferentColumnKindTrue() {
        final SpreadsheetCellReference selection = this.createSelection();
        this.testCellAndCheck(
            selection,
            selection.setRow(selection.row().add(1)),
            false
        );
    }

    @Test
    public void testTestCellDifferentRowKindTrue() {
        final SpreadsheetCellReference selection = this.createSelection();
        final SpreadsheetRowReference row = selection.row();
        final SpreadsheetReferenceKind kind = row.referenceKind();

        this.testCellAndCheck(
            selection,
            selection.setRow(row.setReferenceKind(kind.flip())),
            true
        );
    }

    // toRelative........................................................................................................

    @Test
    public void testToRelativeAlreadyAbsolute() {
        this.toRelativeAndCheck0(SpreadsheetSelection.parseCell("$B$2"));
    }

    @Test
    public void testToRelativeRelative() {
        this.toRelativeAndCheck0(SpreadsheetSelection.parseCell("B2"));
    }

    @Test
    public void testToRelativeMixed() {
        this.toRelativeAndCheck0(SpreadsheetSelection.parseCell("$B2"));
    }

    @Test
    public void testToRelativeMixed2() {
        this.toRelativeAndCheck0(SpreadsheetSelection.parseCell("B$2"));
    }

    private void toRelativeAndCheck0(final SpreadsheetCellReference reference) {
        this.toRelativeOrAbsoluteAndCheck(reference,
            reference.toRelative(),
            SpreadsheetReferenceKind.RELATIVE);
    }

    // toAbsolute.......................................................................................................

    @Test
    public void testToAbsoluteAlreadyAbsolute() {
        this.toAbsoluteAndCheck(SpreadsheetSelection.parseCell("$B$2"));
    }

    @Test
    public void testToAbsoluteRelative() {
        this.toAbsoluteAndCheck(SpreadsheetSelection.parseCell("B2"));
    }

    @Test
    public void testToAbsoluteMixed() {
        this.toAbsoluteAndCheck(SpreadsheetSelection.parseCell("$B2"));
    }

    @Test
    public void testToAbsoluteMixed2() {
        this.toAbsoluteAndCheck(SpreadsheetSelection.parseCell("B$2"));
    }

    private void toAbsoluteAndCheck(final SpreadsheetCellReference reference) {
        this.toRelativeOrAbsoluteAndCheck(reference,
            reference.toAbsolute(),
            SpreadsheetReferenceKind.ABSOLUTE);
    }

    private void toRelativeOrAbsoluteAndCheck(final SpreadsheetCellReference reference,
                                              final SpreadsheetCellReference to,
                                              final SpreadsheetReferenceKind kind) {
        final SpreadsheetColumnReference column = reference.column();
        final SpreadsheetRowReference row = reference.row();

        this.checkColumn(to, column.setReferenceKind(kind));
        this.checkRow(to, row.setReferenceKind(kind));

        this.checkColumn(reference, column);
        this.checkRow(reference, row);
    }

    // addColumn .......................................................................................................

    @Test
    public void testAddColumnZero() {
        final SpreadsheetCellReference cell = this.createSelection();
        assertSame(cell, cell.addColumn(0));
    }

    @Test
    public void testAddColumnNonZero() {
        final SpreadsheetCellReference cell = this.createSelection();
        final int delta = 10;

        final SpreadsheetCellReference different = cell.addColumn(delta);
        this.checkColumn(different, this.column().setValue(COLUMN + delta));
        this.checkRow(different, this.row());
    }

    // addColumnSaturated................................................................................................

    @Test
    public void testAddColumnSaturationUnderflows() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("B2");
        this.checkEquals(
            SpreadsheetSelection.parseCell("A2"),
            cell.addColumnSaturated(-3)
        );
    }

    @Test
    public void testAddColumnSaturationOverflows() {
        final SpreadsheetRowReference row = SpreadsheetSelection.parseRow("2");
        final SpreadsheetCellReference cell = SpreadsheetReferenceKind.RELATIVE.column(SpreadsheetColumnReference.MAX_VALUE - 2)
            .setRow(row);
        this.checkEquals(
            SpreadsheetReferenceKind.RELATIVE.lastColumn()
                .setRow(row),
            cell.addColumnSaturated(+3)
        );
    }

    @Test
    public void testAddColumnSaturation() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("B2");
        this.checkEquals(
            SpreadsheetSelection.parseCell("D2"),
            cell.addColumnSaturated(+2)
        );
    }

    // addRow .............................................................................................

    @Test
    public void testAddRowZero() {
        final SpreadsheetCellReference cell = this.createSelection();
        assertSame(cell, cell.addRow(0));
    }

    @Test
    public void testAddRowNonZero() {
        final SpreadsheetCellReference cell = this.createSelection();
        final int delta = 10;

        final SpreadsheetCellReference different = cell.addRow(delta);
        this.checkRow(different, this.row().setValue(ROW + delta));
        this.checkColumn(different, this.column());
    }

    // addRowSaturated................................................................................................

    @Test
    public void testAddRowSaturationUnderflows() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("B3");
        this.checkEquals(
            SpreadsheetSelection.parseCell("B1"),
            cell.addRowSaturated(-3)
        );
    }

    @Test
    public void testAddRowSaturationOverflows() {
        final SpreadsheetColumnReference column = SpreadsheetSelection.parseColumn("B");
        final SpreadsheetCellReference cell = SpreadsheetReferenceKind.RELATIVE.row(SpreadsheetRowReference.MAX_VALUE - 2)
            .setColumn(column);
        this.checkEquals(
            column.setRow(SpreadsheetReferenceKind.RELATIVE.lastRow()),
            cell.addRowSaturated(+3)
        );
    }

    @Test
    public void testAddRowSaturation() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("B2");
        this.checkEquals(
            SpreadsheetSelection.parseCell("B4"),
            cell.addRowSaturated(+2)
        );
    }

    // add .............................................................................................

    @Test
    public void testAddColumnRowColumnZeroAndRowZero() {
        final SpreadsheetCellReference cell = this.createSelection();
        assertSame(cell, cell.add(0, 0));
    }

    @Test
    public void testAddColumnRowColumnNonZeroAndRowNonZero() {
        final SpreadsheetCellReference cell = this.createSelection();
        final int column = 10;
        final int row = 100;

        final SpreadsheetCellReference different = cell.add(column, row);
        this.checkColumn(different, this.column().setValue(COLUMN + column));
        this.checkRow(different, this.row().setValue(ROW + row));
    }

    @Test
    public void testAddColumnRowColumnNonZero() {
        final SpreadsheetCellReference cell = this.createSelection();
        final int column = 10;

        final SpreadsheetCellReference different = cell.add(column, 0);
        this.checkColumn(different, this.column().setValue(COLUMN + column));
        this.checkRow(different, this.row());
    }

    @Test
    public void testAddColumnRowRowNonZero() {
        final SpreadsheetCellReference cell = this.createSelection();
        final int row = 100;

        final SpreadsheetCellReference different = cell.add(0, row);
        this.checkColumn(different, this.column());
        this.checkRow(different, this.row().setValue(ROW + row));
    }

    // addIfRelative....................................................................................................

    @Test
    public void testAddIfRelativeZero() {
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("B2");
        assertSame(
            reference,
            reference.addIfRelative(
                0,
                0
            )
        );
    }

    @Test
    public void testAddIfRelativeAbsoluteColumn() {
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("$B2");

        assertSame(
            reference,
            reference.addIfRelative(
                1,
                0
            )
        );
    }

    @Test
    public void testAddIfRelativeAbsoluteRow() {
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("B$2");

        assertSame(
            reference,
            reference.addIfRelative(
                0,
                1
            )
        );
    }

    @Test
    public void testAddIfRelativeAbsoluteColumnRow() {
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("$B$2");

        assertSame(
            reference,
            reference.addIfRelative(
                1,
                1
            )
        );
    }

    @Test
    public void testAddIfRelativeColumn() {
        this.addIfRelativeAndCheck(
            SpreadsheetSelection.parseCell("B2"),
            1,
            0,
            SpreadsheetSelection.parseCell("C2")
        );
    }

    @Test
    public void testAddIfRelativeRow() {
        this.addIfRelativeAndCheck(
            SpreadsheetSelection.parseCell("B2"),
            0,
            1,
            SpreadsheetSelection.parseCell("B3")
        );
    }

    @Test
    public void testAddIfRelativeColumnRow() {
        this.addIfRelativeAndCheck(
            SpreadsheetSelection.parseCell("D4"),
            -1,
            -2,
            SpreadsheetSelection.parseCell("C2")
        );
    }

    // replaceReferencesMapper..........................................................................................

    @Test
    public void testReplaceReferencesMapperCell() {
        this.replaceReferencesMapperAndCheck(
            SpreadsheetSelection.A1,
            SpreadsheetSelection.parseCell("B4"),
            1,
            3
        );
    }

    @Test
    public void testReplaceReferencesMapperCell2() {
        this.replaceReferencesMapperAndCheck(
            SpreadsheetSelection.parseCell("B4"),
            SpreadsheetSelection.A1,
            -1,
            -3
        );
    }

    @Test
    public void testReplaceReferencesMapperCell3() {
        this.replaceReferencesMapperAndCheck(
            SpreadsheetSelection.A1,
            SpreadsheetSelection.A1,
            0,
            0
        );
    }

    @Test
    public void testReplaceReferencesMapperColumn() {
        this.replaceReferencesMapperAndCheck(
            "B4",
            SpreadsheetSelection.parseColumn("E"),
            3,
            0
        );
    }

    @Test
    public void testReplaceReferencesMapperRow() {
        this.replaceReferencesMapperAndCheck(
            "B4",
            SpreadsheetSelection.parseRow("8"),
            0,
            4
        );
    }

    @Test
    public void testReplaceReferencesMapperCellRange() {
        this.replaceReferencesMapperAndCheck(
            SpreadsheetSelection.A1,
            SpreadsheetSelection.parseCellRange("B4:D9"),
            1,
            3
        );
    }

    @Test
    public void testReplaceReferenceMapperWithAbsoluteCell() {
        final SpreadsheetCellReference from = SpreadsheetSelection.A1;
        final SpreadsheetCellReference to = SpreadsheetSelection.parseCell("B4");

        final SpreadsheetCellReference unmoved = SpreadsheetSelection.parseCell("$C$3");

        this.checkEquals(
            Optional.of(
                unmoved
            ),
            from.replaceReferencesMapper(to)
                .get()
                .apply(unmoved)
        );
    }

    // CanReplaceReference..............................................................................................

    @Test
    public void testReplaceReferenceMapperReturnsEmpty() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetSelection.A1.replaceReferences(
                (r) -> Optional.empty()
            )
        );

        this.checkEquals(
            "Mapper must return a cell",
            thrown.getMessage()
        );
    }

    @Test
    public void testReplaceReferenceSelf() {
        this.replaceReferencesAndCheck(
            SpreadsheetSelection.parseCell("B2"),
            Optional::of
        );
    }

    @Test
    public void testReplaceReference() {
        this.replaceReferencesAndCheck(
            SpreadsheetSelection.parseCell("B2"),
            (r) -> Optional.of(r.add(1, 2)),
            SpreadsheetSelection.parseCell("C4")
        );
    }

    @Override
    public SpreadsheetCellReference createReplaceReference() {
        return this.createSelection();
    }

    // Compare..........................................................................................................

    @Test
    public void testCompareToSameColumnSameRowDifferentReferenceKinds() {
        this.compareToAndCheckEquals(
            this.cell(SpreadsheetReferenceKind.ABSOLUTE, COLUMN, SpreadsheetReferenceKind.ABSOLUTE, ROW),
            this.cell(SpreadsheetReferenceKind.RELATIVE, COLUMN, SpreadsheetReferenceKind.RELATIVE, ROW));
    }

    @Test
    public void testCompareToSameColumnDifferentRow() {
        this.compareToAndCheckLess(
            this.cell(SpreadsheetReferenceKind.ABSOLUTE, COLUMN, SpreadsheetReferenceKind.ABSOLUTE, ROW),
            this.cell(COLUMN, ROW + 10));
    }

    @Test
    public void testCompareToSameColumnDifferentReferenceKindDifferentRow() {
        this.compareToAndCheckLess(
            this.cell(SpreadsheetReferenceKind.ABSOLUTE, COLUMN, SpreadsheetReferenceKind.ABSOLUTE, ROW),
            this.cell(SpreadsheetReferenceKind.RELATIVE, COLUMN, SpreadsheetReferenceKind.ABSOLUTE, ROW + 10));
    }

    @Test
    public void testCompareToDifferentColumnSameRow() {
        this.compareToAndCheckLess(this.cell(COLUMN + 10, ROW));
    }

    @Test
    public void testCompareToDifferentColumnDifferentReferenceKindDifferentRow() {
        this.compareToAndCheckLess(this.cell(SpreadsheetReferenceKind.RELATIVE, COLUMN + 10, SpreadsheetReferenceKind.ABSOLUTE, ROW));
    }

    private SpreadsheetCellReference cell(final int column, final int row) {
        return this.cell(SpreadsheetReferenceKind.ABSOLUTE, column, SpreadsheetReferenceKind.ABSOLUTE, row);
    }

    private SpreadsheetCellReference cell(final SpreadsheetReferenceKind columnKind,
                                          final int column,
                                          final SpreadsheetReferenceKind rowKind,
                                          final int row) {
        return columnKind.column(column).setRow(rowKind.row(row));
    }

    @Test
    public void testCompareSortArray() {
        final SpreadsheetCellReference b3 = SpreadsheetSelection.parseCell("b3");
        final SpreadsheetCellReference c2 = SpreadsheetSelection.parseCell("C2");

        this.compareToArraySortAndCheck(
            c2, b3,
            c2, b3
        );

        this.compareToArraySortAndCheck(
            b3, c2,
            c2, b3
        );
    }

    @Test
    public void testCompareSortArray2() {
        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("$b$2");
        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("c3");

        this.compareToArraySortAndCheck(
            b2, a1, c3,
            a1, b2, c3
        );

        this.compareToArraySortAndCheck(
            c3, b2, a1,
            a1, b2, c3
        );
    }

    // testCell.........................................................................................................

    @Test
    public void testTestCellDifferent() {
        this.testWithNullColumnFails();
    }

    // testCellRange....................................................................................................

    @Test
    public void testToCellRangeNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.cell(1, 1)
                .cellRange((SpreadsheetCellReference) null)
        );
    }

    @Test
    public void testToCellRangeOne() {
        final SpreadsheetCellReference lower = this.cell(1, 1);
        final SpreadsheetCellRangeReference range = lower.cellRange(lower);

        this.checkEquals(
            Range.singleton(lower),
            range.range()
        );
    }

    @Test
    public void testToCellRangeLeftTopRightBottom() {
        final int left = 1;
        final int top = 2;
        final int right = 3;
        final int bottom = 4;

        this.toCellRangeAndCheck(
            this.cell(left, top),
            this.cell(right, bottom),
            left, top,
            right, bottom
        );
    }

    @Test
    public void testToCellRangeLeftBottomRightTop() {
        final int left = 1;
        final int top = 2;
        final int right = 3;
        final int bottom = 4;

        this.toCellRangeAndCheck(
            this.cell(left, bottom),
            this.cell(right, top),
            left, top,
            right, bottom
        );
    }

    @Test
    public void testToCellRangeRightTopLeftBottom() {
        final int left = 1;
        final int top = 2;
        final int right = 3;
        final int bottom = 4;

        this.toCellRangeAndCheck(
            this.cell(right, top),
            this.cell(left, bottom),
            left, top,
            right, bottom
        );
    }

    private void toCellRangeAndCheck(final SpreadsheetCellReference cell,
                                     final SpreadsheetCellReference other,
                                     final int left,
                                     final int top,
                                     final int right,
                                     final int bottom) {
        final Range<SpreadsheetCellReference> expected = Range.greaterThanEquals(
            this.cell(left, top)
        ).and(
            Range.lessThanEquals(
                this.cell(right, bottom)
            )
        );

        final Range<SpreadsheetCellReference> range = cell.range(other);
        this.checkEquals(
            expected,
            range,
            () -> cell + " range " + other
        );


        this.checkEquals(
            SpreadsheetCellRangeReference.with(expected),
            cell.cellRange(other),
            () -> cell + " cellRange " + other
        );
    }

    // toCellRangeResolvingLabels......................................................................................................

    @Test
    public void testToCellRangeAbsolute() {
        this.toCellRangeAndCheck(
            SpreadsheetSelection.parseCell("$B$2"),
            SpreadsheetSelection.parseCellRange("$B$2")
        );
    }

    @Test
    public void testToCellRangeRelative() {
        final String text = "C3";

        this.toCellRangeAndCheck(
            SpreadsheetSelection.parseCell(text),
            SpreadsheetSelection.parseCellRange(text)
        );
    }

    private void toCellRangeAndCheck(final SpreadsheetCellReference reference,
                                     final SpreadsheetCellRangeReference range) {
        this.checkEquals(
            range,
            reference.toCellRange(),
            () -> reference + " toCellRange()"
        );
    }

    // testCellRange.....................................................................................................

    @Test
    public void testTestCellRangeBeforeAbove() {
        this.testCellRangeAndCheck(
            "B3",
            "C3:E5",
            false
        );
    }

    @Test
    public void testTestCellRangeAbove() {
        this.testCellRangeAndCheck(
            "D2",
            "C3:E5",
            false
        );
    }

    @Test
    public void testTestCellRangeAfterAbove() {
        this.testCellRangeAndCheck(
            "E2",
            "C3:E5",
            false
        );
    }

    @Test
    public void testTestCellRangeTopLeft() {
        this.testCellRangeAndCheck(
            "C3",
            "C3:E5",
            true
        );
    }

    @Test
    public void testTestCellRangeTopCenter() {
        this.testCellRangeAndCheck(
            "D3",
            "C3:E5",
            true
        );
    }

    @Test
    public void testTestCellRangeTopRight() {
        this.testCellRangeAndCheck(
            "E3",
            "C3:E5",
            true
        );
    }

    @Test
    public void testTestCellRangeMiddleLeft() {
        this.testCellRangeAndCheck(
            "C4",
            "C3:E5",
            true
        );
    }

    @Test
    public void testTestCellRangeMiddleCenter() {
        this.testCellRangeAndCheck(
            "D4",
            "C3:E5",
            true
        );
    }

    @Test
    public void testTestCellRangeMiddleRight() {
        this.testCellRangeAndCheck(
            "E4",
            "C3:E5",
            true
        );
    }

    @Test
    public void testTestCellRangeBottomLeft() {
        this.testCellRangeAndCheck(
            "D5",
            "C3:E5",
            true
        );
    }

    @Test
    public void testTestCellRangeBottomCenter() {
        this.testCellRangeAndCheck(
            "D5",
            "C3:E5",
            true
        );
    }

    @Test
    public void testTestCellRangeBottomRight() {
        this.testCellRangeAndCheck(
            "E5",
            "C3:E5",
            true
        );
    }

    @Test
    public void testTestCellRangeBeforeBelow() {
        this.testCellRangeAndCheck(
            "B6",
            "C3:E5",
            false
        );
    }

    @Test
    public void testTestCellRangeBelow() {
        this.testCellRangeAndCheck(
            "D6",
            "C3:E5",
            false
        );
    }

    @Test
    public void testTestCellRangeAfterBelow() {
        this.testCellRangeAndCheck(
            "E6",
            "C3:E5",
            false
        );
    }

    @Test
    public void testTestCellRangeBeforeMiddle() {
        this.testCellRangeAndCheck(
            "A4",
            "C3:E5",
            false
        );
    }

    @Test
    public void testTestCellRangeAfterMiddle() {
        this.testCellRangeAndCheck(
            "F4",
            "C3:E5",
            false
        );
    }

    // testColumn.......................................................................................................

    @Test
    public void testTestColumnBefore() {
        this.testColumnAndCheck(
            "C3",
            "B",
            false
        );
    }

    @Test
    public void testTestColumnAfter() {
        this.testColumnAndCheck(
            "C3",
            "D",
            false
        );
    }

    @Test
    public void testTestColumn() {
        this.testColumnAndCheck(
            "C3",
            "C",
            true
        );
    }

    // testRow.......................................................................................................

    @Test
    public void testTestRowAbove() {
        this.testRowAndCheck(
            "C3",
            "2",
            false
        );
    }

    @Test
    public void testTestRowBelow() {
        this.testRowAndCheck(
            "C3",
            "4",
            false
        );
    }

    @Test
    public void testTestRow() {
        this.testRowAndCheck(
            "C3",
            "3",
            true
        );
    }

    // ParseStringTesting...............................................................................................

    @Test
    public void testParseInvalidCellReferenceFails() {
        this.parseStringFails("Invalid",
            IllegalArgumentException.class);
    }

    @Test
    public void testParseInvalidColumnFails() {
        this.parseStringFails(
            "XFEA",
            new IllegalColumnArgumentException("Invalid column \"XFEA\" not between \"A\" and \"XFE\"")
        );
    }

    @Test
    public void testParseInvalidColumnFails2() {
        this.parseStringFails(
            "ABCDEFG",
            new IllegalColumnArgumentException("Invalid column \"ABCDEFG\" not between \"A\" and \"XFE\"")
        );
    }

    @Test
    public void testParseInvalidRowFails() {
        this.parseStringFails(
            "B1048577",
            new IllegalRowArgumentException("Invalid row=1048576 not between 0 and 1048576")
        );
    }

    @Test
    public void testParseInvalidRowFails2() {
        this.parseStringFails(
            "B12345678",
            new IllegalRowArgumentException("Invalid row=12345677 not between 0 and 1048576")
        );
    }

    @Test
    public void testParseStarFails() {
        this.parseStringFails(
            "*",
            IllegalArgumentException.class
        );
    }

    @Test
    public void testParseCellRangeReferenceFails() {
        final String text = "A1:B2";

        this.parseStringFails(
            text,
            new InvalidCharacterException(
                text,
                text.indexOf(':')
            )
        );
    }

    @Test
    public void testParseCellReferenceRelative() {
        this.parseStringAndCheck("A98",
            SpreadsheetSelection.column(0, SpreadsheetReferenceKind.RELATIVE)
                .setRow(SpreadsheetSelection.row(97, SpreadsheetReferenceKind.RELATIVE)));
    }

    @Test
    public void testParseCellReferenceAbsolute() {
        this.parseStringAndCheck("$A$98",
            SpreadsheetSelection.column(0, SpreadsheetReferenceKind.ABSOLUTE)
                .setRow(SpreadsheetSelection.row(97, SpreadsheetReferenceKind.ABSOLUTE)));
    }

    @Test
    public void testParseCellReferenceLastColumn() {
        this.parseStringAndCheck("XFD2",
            SpreadsheetReferenceKind.RELATIVE.lastColumn()
                .setRow(SpreadsheetReferenceKind.RELATIVE.row(1))
        );
    }

    @Test
    public void testParseCellReferenceLastRow() {
        this.parseStringAndCheck("B1048576",
            SpreadsheetReferenceKind.RELATIVE.column(1)
                .setRow(SpreadsheetReferenceKind.RELATIVE.lastRow())
        );
    }

    // JsonNodeMarshallingTesting.......................................................................................

    @Test
    public void testUnmarshallString() {
        this.unmarshallAndCheck(JsonNode.string("$A$1"),
            SpreadsheetSelection.parseCell("$A$1"));
    }

    // isHidden.........................................................................................................

    @Test
    public void testIsHiddenColumnHidden() {
        this.isHiddenAndCheck(
            "A1",
            Predicates.is(SpreadsheetSelection.parseColumn("A")),
            Predicates.never(),
            true
        );
    }

    @Test
    public void testIsHiddenRowHidden() {
        this.isHiddenAndCheck(
            "A1",
            Predicates.never(),
            Predicates.is(SpreadsheetSelection.parseRow("1")),
            true
        );
    }

    @Test
    public void testIsHiddenNeitherColumnOrRowNotHidden() {
        this.isHiddenAndCheck(
            "A1",
            Predicates.never(),
            Predicates.never(),
            false
        );
    }

    // navigate.........................................................................................................

    @Test
    public void testLeftColumn() {
        this.leftColumnAndCheck(
            "B2",
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "A2"
        );
    }

    @Test
    public void testLeftColumnFirst() {
        this.leftColumnAndCheck(
            "A2",
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "A2"
        );
    }

    @Test
    public void testLeftColumnLast() {
        final SpreadsheetColumnReference column = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        this.leftColumnAndCheck(
            column + "1",
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            column.add(-1) + "1"
        );
    }

    @Test
    public void testLeftColumnSkipsHidden() {
        this.leftColumnAndCheck(
            "D1",
            SpreadsheetViewportAnchor.NONE,
            "C",
            NO_HIDDEN_ROWS,
            "B1"
        );
    }

    @Test
    public void testLeftColumnHiddenRow() {
        this.leftColumnAndCheck(
            "D1",
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            "1",
            ""
        );
    }

    @Test
    public void testLeftPixels() {
        this.leftPixelsAndCheck(
            "E4",
            SpreadsheetViewportAnchor.LEFT,
            50,
            "D",
            Maps.of("B", 50.0, "C", 50.0),
            NO_HIDDEN_ROWS,
            Maps.empty(),
            "B4"
        );
    }

    @Test
    public void testUpRow() {
        this.upRowAndCheck(
            "B2",
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "B1"
        );
    }

    @Test
    public void testUpRowFirst() {
        this.upRowAndCheck(
            "B1",
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "B1"
        );
    }

    @Test
    public void testUpRowLast() {
        final SpreadsheetRowReference row = SpreadsheetReferenceKind.RELATIVE.lastRow();

        this.upRowAndCheck(
            "B" + row,
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "B" + row.add(-1)
        );
    }

    @Test
    public void testUpRowSkipsHidden() {
        this.upRowAndCheck(
            "B4",
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            "3",
            "B2"
        );
    }

    @Test
    public void testUpRowIgnoresHiddenColumn() {
        this.upRowAndCheck(
            "B2",
            SpreadsheetViewportAnchor.NONE,
            "B",
            NO_HIDDEN_ROWS,
            ""
        );
    }

    @Test
    public void testUpPixels() {
        this.upPixelsAndCheck(
            "E5",
            SpreadsheetViewportAnchor.LEFT,
            50,
            NO_HIDDEN_COLUMNS,
            Maps.empty(),
            "4",
            Map.of("3", 50.0, "2", 50.0),
            "E2"
        );
    }

    @Test
    public void testRightColumn() {
        this.rightColumnAndCheck(
            "B2",
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "C2"
        );
    }

    @Test
    public void testRightColumnFirst() {
        this.rightColumnAndCheck(
            "A2",
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "B2"
        );
    }

    @Test
    public void testRightColumnLast() {
        final SpreadsheetColumnReference column = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        this.rightColumnAndCheck(
            column + "1",
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            column + "1"
        );
    }

    @Test
    public void testRightColumnSkipsHidden() {
        this.rightColumnAndCheck(
            "B1",
            SpreadsheetViewportAnchor.NONE,
            "C",
            NO_HIDDEN_ROWS,
            "D1"
        );
    }

    @Test
    public void testRightColumnIgnoresHiddenRow() {
        this.rightColumnAndCheck(
            "D1",
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            "1",
            ""
        );
    }

    @Test
    public void testRightPixels() {
        this.rightPixelsAndCheck(
            "E2",
            SpreadsheetViewportAnchor.LEFT,
            50,
            "F",
            Maps.of("E", 5.0, "G", 50.0, "H", 50.0),
            NO_HIDDEN_ROWS,
            Maps.empty(),
            "H2"
        );
    }

    @Test
    public void testDownRow() {
        this.downRowAndCheck(
            "B2",
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "B3"
        );
    }

    @Test
    public void testDownRowFirst() {
        this.downRowAndCheck(
            "B1",
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "B2"
        );
    }

    @Test
    public void testDownRowLast() {
        final SpreadsheetRowReference row = SpreadsheetReferenceKind.RELATIVE.lastRow();

        this.downRowAndCheck(
            "B" + row,
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "B" + row
        );
    }

    @Test
    public void testDownRowSkipsHidden() {
        this.downRowAndCheck(
            "B2",
            SpreadsheetViewportAnchor.NONE,
            NO_HIDDEN_COLUMNS,
            "3",
            "B4"
        );
    }

    @Test
    public void testDownPixels() {
        this.downPixelsAndCheck(
            "B2",
            SpreadsheetViewportAnchor.LEFT,
            50,
            NO_HIDDEN_COLUMNS,
            Maps.empty(),
            "3",
            Maps.of("4", 50.0, "5", 5.0),
            "B5"
        );
    }

    // extendRange......................................................................................................

    @Test
    public void testExtendRange() {
        this.extendRangeAndCheck(
            "B2",
            "C3",
            "B2:C3"
        );
    }

    @Test
    public void testExtendRange2() {
        this.extendRangeAndCheck(
            "C3",
            "B2",
            "B2:C3"
        );
    }

    @Test
    public void testExtendRangeSame() {
        this.extendRangeAndCheck(
            "A1",
            "A1"
        );
    }

    @Test
    public void testExtendRangeSame2() {
        this.extendRangeAndCheck(
            "B2",
            "B2"
        );
    }

    @Override
    SpreadsheetCellRangeReference parseRange(final String range) {
        return SpreadsheetSelection.parseCellRange(range);
    }

    // extendXXXX.......................................................................................................

    @Test
    public void testExtendLeftColumn() {
        this.extendLeftColumnAndCheck(
            "C3",
            SpreadsheetViewportAnchor.CELL_RANGE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "B3:C3",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT
        );
    }

    @Test
    public void testExtendLeftColumnFirst() {
        final String cell = "A1";

        this.extendLeftColumnAndCheck(
            cell,
            SpreadsheetViewportAnchor.CELL_RANGE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            cell,
            SpreadsheetViewportAnchor.CELL
        );
    }

    @Test
    public void testExtendLeftColumnFirst2() {
        final String cell = "A2";

        this.extendLeftColumnAndCheck(
            cell,
            SpreadsheetViewportAnchor.CELL_RANGE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            cell,
            SpreadsheetViewportAnchor.CELL
        );
    }

    @Test
    public void testExtendLeftPixels() {
        this.extendLeftPixelsAndCheck(
            "E5",
            SpreadsheetViewportAnchor.TOP_RIGHT,
            50,
            "D",
            Maps.of("C", 50.0, "B", 50.0),
            NO_HIDDEN_ROWS,
            Maps.empty(),
            "B5:E5",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT
        );
    }

    @Test
    public void testExtendUpRow() {
        this.extendUpRowAndCheck(
            "C3",
            SpreadsheetViewportAnchor.CELL_RANGE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "C2:C3",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT
        );
    }

    @Test
    public void testExtendUpRowFirst() {
        final String cell = "A1";

        this.extendUpRowAndCheck(
            cell,
            SpreadsheetViewportAnchor.CELL_RANGE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            cell,
            SpreadsheetViewportAnchor.CELL
        );
    }

    @Test
    public void testExtendUpRowFirstRow() {
        final String cell = "B1";

        this.extendUpRowAndCheck(
            cell,
            SpreadsheetViewportAnchor.CELL,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            cell,
            SpreadsheetViewportAnchor.CELL
        );
    }

    @Test
    public void testExtendUpPixels() {
        this.extendUpPixelsAndCheck(
            "F6",
            SpreadsheetViewportAnchor.CELL,
            50,
            NO_HIDDEN_COLUMNS,
            Maps.empty(),
            "5",
            Maps.of("4", 50.0, "3", 50.0),
            "F3:F6",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT
        );
    }

    @Test
    public void testExtendRightColumn() {
        this.extendRightColumnAndCheck(
            "C3",
            SpreadsheetViewportAnchor.CELL,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "C3:D3",
            SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    @Test
    public void testExtendRightColumnLast() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseRow("1")
            .setColumn(
                SpreadsheetReferenceKind.RELATIVE.lastColumn()
            );

        this.extendRightColumnAndCheck(
            cell.toString(),
            SpreadsheetViewportAnchor.CELL_RANGE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            cell.toString(),
            SpreadsheetViewportAnchor.CELL
        );
    }

    @Test
    public void testExtendRightColumnLast2() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseRow("2")
            .setColumn(
                SpreadsheetReferenceKind.RELATIVE.lastColumn()
            );

        this.extendRightColumnAndCheck(
            cell.toString(),
            SpreadsheetViewportAnchor.CELL_RANGE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            cell.toString(),
            SpreadsheetViewportAnchor.CELL
        );
    }

    @Test
    public void testExtendRightPixels() {
        this.extendRightPixelsAndCheck(
            "B2",
            SpreadsheetViewportAnchor.TOP_LEFT,
            50,
            "C",
            Maps.of("D", 50.0, "E", 50.0),
            NO_HIDDEN_ROWS,
            Maps.empty(),
            "B2:E2",
            SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    @Test
    public void testExtendDownRow() {
        this.extendDownRowAndCheck(
            "B2",
            SpreadsheetViewportAnchor.CELL_RANGE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            "B2:B3",
            SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    @Test
    public void testExtendDownRowLast() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseColumn("A")
            .setRow(
                SpreadsheetReferenceKind.RELATIVE.lastRow()
            );

        this.extendDownRowAndCheck(
            cell.toString(),
            SpreadsheetViewportAnchor.CELL_RANGE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            cell.toString(),
            SpreadsheetViewportAnchor.CELL
        );
    }

    @Test
    public void testExtendDownRowLast2() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseColumn("B")
            .setRow(
                SpreadsheetReferenceKind.RELATIVE.lastRow()
            );

        this.extendDownRowAndCheck(
            cell.toString(),
            SpreadsheetViewportAnchor.CELL_RANGE,
            NO_HIDDEN_COLUMNS,
            NO_HIDDEN_ROWS,
            cell.toString(),
            SpreadsheetViewportAnchor.CELL
        );
    }

    @Test
    public void testExtendDownPixels() {
        this.extendDownPixelsAndCheck(
            "B2",
            SpreadsheetViewportAnchor.CELL,
            50,
            NO_HIDDEN_COLUMNS,
            Maps.empty(),
            "3",
            Maps.of("4", 50.0, "5", 50.0),
            "B2:B5",
            SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    // focused..........................................................................................................

    @Test
    public void testFocused() {
        this.focusedAndCheck(
            "A1",
            SpreadsheetViewportAnchor.NONE,
            "A1"
        );
    }

    @Test
    public void testFocused2() {
        this.focusedAndCheck(
            "$B$2",
            SpreadsheetViewportAnchor.NONE,
            "$B$2"
        );
    }

    // SpreadsheetSelectionVisitor.......................................................................................

    @Test
    public void testSpreadsheetSelectionVisitorAccept() {
        final StringBuilder b = new StringBuilder();
        final SpreadsheetCellReference selection = this.createSelection();

        new FakeSpreadsheetSelectionVisitor() {
            @Override
            protected Visiting startVisit(final SpreadsheetSelection s) {
                assertSame(selection, s);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final SpreadsheetSelection s) {
                assertSame(selection, s);
                b.append("2");
            }

            @Override
            protected void visit(final SpreadsheetCellReference s) {
                assertSame(selection, s);
                b.append("3");
            }
        }.accept(selection);
        this.checkEquals("132", b.toString());
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            SpreadsheetSelection.parseCell("A12"),
            "cell A12" + EOL
        );
    }

    // hasUrlFragment...................................................................................................

    @Test
    public void testUrlFragment2() {
        this.urlFragmentAndCheck(
            SpreadsheetSelection.parseCellOrCellRange("A1:B2"),
            UrlFragment.parse("A1:B2")
        );
    }

    @Test
    public void testUrlFragmentAllCells() {
        this.urlFragmentAndCheck(
            SpreadsheetSelection.ALL_CELLS,
            UrlFragment.parse("*")
        );
    }

    // toParserToken....................................................................................................

    @Test
    public void testToParserToken() {
        final String text = "B2";

        this.toParserTokenAndCheck(
            SpreadsheetSelection.parseCell(text),
            SpreadsheetFormulaParserToken.cell(
                Lists.of(
                    SpreadsheetSelection.parseColumn("B")
                        .toParserToken(),
                    SpreadsheetSelection.parseRow("2")
                        .toParserToken()
                ),
                text
            ),
            SpreadsheetFormulaParsers.cell()
        );
    }

    @Test
    public void testToParserTokenAbsolute() {
        final String text = "$C$3";

        this.toParserTokenAndCheck(
            SpreadsheetSelection.parseCell(text),
            SpreadsheetFormulaParserToken.cell(
                Lists.of(
                    SpreadsheetSelection.parseColumn("$C")
                        .toParserToken(),
                    SpreadsheetSelection.parseRow("$3")
                        .toParserToken()
                ),
                text
            ),
            SpreadsheetFormulaParsers.cell()
        );
    }

    // equalsIgnoreReferenceKind........................................................................................

    @Test
    public void testEqualsIgnoreReferenceKindDifferentKind() {
        this.equalsIgnoreReferenceKindAndCheck(
            "A1",
            "$A$1",
            true
        );
    }

    @Test
    public void testEqualsIgnoreReferenceKindDifferent() {
        this.equalsIgnoreReferenceKindAndCheck(
            "A1",
            "B2",
            false
        );
    }

    // compare..........................................................................................................

    @Test
    public void testArraySort() {
        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("$B2");
        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("c$3");
        final SpreadsheetCellReference d4 = SpreadsheetSelection.parseCell("$D$4");

        this.compareToArraySortAndCheck(c3, a1, d4, b2,
            a1, b2, c3, d4);
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createSelection(), "$DT$457");
    }

    // toStringMaybeStar................................................................................................

    @Test
    public void testToStringMaybeStar() {
        this.toStringMaybeStarAndCheck(
            SpreadsheetSelection.A1
        );
    }

    @Override
    SpreadsheetCellReference createSelection() {
        return SpreadsheetCellReference.with(column(), row());
    }

    private SpreadsheetColumnReference column() {
        return this.column(COLUMN);
    }

    private SpreadsheetColumnReference column(final int value) {
        return SpreadsheetSelection.column(value, SpreadsheetReferenceKind.ABSOLUTE);
    }

    private SpreadsheetRowReference row() {
        return this.row(ROW);
    }

    private SpreadsheetRowReference row(final int value) {
        return SpreadsheetSelection.row(value, SpreadsheetReferenceKind.ABSOLUTE);
    }

    private void checkColumn(final SpreadsheetCellReference cell, final SpreadsheetColumnReference column) {
        this.checkEquals(column, cell.column(), "column");
    }

    private void checkRow(final SpreadsheetCellReference cell, final SpreadsheetRowReference row) {
        this.checkEquals(row, cell.row(), "row");
    }

    @Override
    public SpreadsheetCellReference createComparable() {
        return this.createSelection();
    }

    @Override
    public boolean compareAndEqualsMatch() {
        return false;
    }

    @Override
    public Class<SpreadsheetCellReference> type() {
        return SpreadsheetCellReference.class;
    }

    // HatoesResourceTesting............................................................................................

    @Override
    public SpreadsheetCellReference createHateosResource() {
        return this.createSelection();
    }

    // ParseStringTesting.........................................................................................

    @Override
    public SpreadsheetCellReference parseString(final String text) {
        return SpreadsheetSelection.parseCell(text);
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetCellReference unmarshall(final JsonNode from,
                                               final JsonNodeUnmarshallContext context) {
        return SpreadsheetCellReference.unmarshallCellReference(from, context);
    }
}

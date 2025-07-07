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
import walkingkooka.test.ParseStringTesting;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellRangeReferencePathTest implements ParseStringTesting<SpreadsheetCellRangeReferencePath>,
    ClassTesting<SpreadsheetCellRangeReferencePath> {

    @Test
    public void testParseWithUnknownFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetCellRangeReferencePath.parse("123?")
        );
        this.checkEquals(
            "Got \"123?\" expected one of LRTD, RLTD, LRBU, RLBU, TDLR, TDRL, BULR, BURL",
            thrown.getMessage()
        );
    }

    @Test
    public void testParseLRTD() {
        this.parseStringAndCheck(
            "LRTD",
            SpreadsheetCellRangeReferencePath.LRTD
        );
    }

    @Test
    public void testParseAllValues() {
        for (final SpreadsheetCellRangeReferencePath path : SpreadsheetCellRangeReferencePath.values()) {
            this.parseStringAndCheck(
                path.name(),
                path
            );
        }
    }

    @Override
    public SpreadsheetCellRangeReferencePath parseString(final String text) {
        return SpreadsheetCellRangeReferencePath.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> thrown) {
        return thrown;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }

    // sort.............................................................................................................

    private final static SpreadsheetCellReference A1 = SpreadsheetSelection.A1;

    private final static SpreadsheetCellReference A2 = SpreadsheetSelection.parseCell("A2");

    private final static SpreadsheetCellReference A3 = SpreadsheetSelection.parseCell("A3");

    private final static SpreadsheetCellReference B1 = SpreadsheetSelection.parseCell("B1");

    private final static SpreadsheetCellReference B2 = SpreadsheetSelection.parseCell("B2");

    private final static SpreadsheetCellReference B3 = SpreadsheetSelection.parseCell("B3");

    private final static SpreadsheetCellReference C1 = SpreadsheetSelection.parseCell("C1");

    private final static SpreadsheetCellReference C2 = SpreadsheetSelection.parseCell("C2");

    private final static SpreadsheetCellReference C3 = SpreadsheetSelection.parseCell("C3");


    @Test
    public void testLRTD() {
        this.sortAndCompare(
            SpreadsheetCellRangeReferencePath.LRTD,
            List.of(
                C3,
                C2,
                C1,
                B3,
                B2,
                B1,
                A3,
                A2,
                A1
            ),
            A1,
            B1,
            C1,
            A2,
            B2,
            C2,
            A3,
            B3,
            C3
        );
    }

    @Test
    public void testRLTD() {
        this.sortAndCompare(
            SpreadsheetCellRangeReferencePath.RLTD,
            List.of(
                C3,
                C2,
                C1,
                B3,
                B2,
                B1,
                A3,
                A2,
                A1
            ),
            C1,
            B1,
            A1,
            C2,
            B2,
            A2,
            C3,
            B3,
            A3
        );
    }

    @Test
    public void testLRBU() {
        this.sortAndCompare(
            SpreadsheetCellRangeReferencePath.LRBU,
            List.of(
                C3,
                C2,
                C1,
                B3,
                B2,
                B1,
                A3,
                A2,
                A1
            ),
            A3,
            B3,
            C3,
            A2,
            B2,
            C2,
            A1,
            B1,
            C1
        );
    }

    @Test
    public void testRLBU() {
        this.sortAndCompare(
            SpreadsheetCellRangeReferencePath.RLBU,
            List.of(
                C3,
                C2,
                C1,
                B3,
                B2,
                B1,
                A3,
                A2,
                A1
            ),
            C3,
            B3,
            A3,
            C2,
            B2,
            A2,
            C1,
            B1,
            A1
        );
    }

    @Test
    public void testTDLR() {
        this.sortAndCompare(
            SpreadsheetCellRangeReferencePath.TDLR,
            List.of(
                C3,
                C2,
                C1,
                B3,
                B2,
                B1,
                A3,
                A2,
                A1
            ),
            A1,
            A2,
            A3,
            B1,
            B2,
            B3,
            C1,
            C2,
            C3
        );
    }

    @Test
    public void testTDRL() {
        this.sortAndCompare(
            SpreadsheetCellRangeReferencePath.TDRL,
            List.of(
                C3,
                C2,
                C1,
                B3,
                B2,
                B1,
                A3,
                A2,
                A1
            ),
            C1,
            C2,
            C3,
            B1,
            B2,
            B3,
            A1,
            A2,
            A3
        );
    }

    @Test
    public void testBULR() {
        this.sortAndCompare(
            SpreadsheetCellRangeReferencePath.BULR,
            List.of(
                C3,
                C2,
                C1,
                B3,
                B2,
                B1,
                A3,
                A2,
                A1
            ),
            A3,
            A2,
            A1,
            B3,
            B2,
            B1,
            C3,
            C2,
            C1
        );
    }

    @Test
    public void testBURL() {
        this.sortAndCompare(
            SpreadsheetCellRangeReferencePath.BURL,
            List.of(
                C3,
                C2,
                C1,
                B3,
                B2,
                B1,
                A3,
                A2,
                A1
            ),
            C3,
            C2,
            C1,
            B3,
            B2,
            B1,
            A3,
            A2,
            A1
        );
    }

    private void sortAndCompare(final SpreadsheetCellRangeReferencePath direction,
                                final List<SpreadsheetCellReference> in,
                                final SpreadsheetCellReference... expected) {
        final SpreadsheetCellReference[] inArray = in.toArray(new SpreadsheetCellReference[0]);
        Arrays.sort(inArray, direction.comparator());

        this.checkEquals(
            List.of(expected),
            List.of(inArray),
            direction + " sort " + in
        );
    }

    @Test
    public void testSortDifferent() {
        final List<SpreadsheetCellReference> input = List.of(
            C3,
            C2,
            C1,
            B3,
            B2,
            B1,
            A3,
            A2,
            A1
        );
        final List<SpreadsheetCellRangeReferencePath> directions = Lists.of(
            SpreadsheetCellRangeReferencePath.values()
        );
        for (final SpreadsheetCellRangeReferencePath direction1 : directions) {
            final List<SpreadsheetCellReference> sorted1 = Lists.array();
            sorted1.addAll(input);
            sorted1.sort(direction1.comparator());

            for (final SpreadsheetCellRangeReferencePath direction2 : directions) {
                if (direction1 != direction2) {
                    final List<SpreadsheetCellReference> sorted2 = Lists.array();
                    sorted2.addAll(input);
                    sorted2.sort(direction2.comparator());

                    this.checkNotEquals(
                        sorted1,
                        sorted2,
                        () -> direction1 + " " + direction2
                    );
                }
            }
        }
    }

    @Test
    public void testComparatorToString() {
        for (final SpreadsheetCellRangeReferencePath path : SpreadsheetCellRangeReferencePath.values()) {
            this.checkEquals(
                path.toString(),
                path.comparator().toString()
            );
        }
    }

    // labelText........................................................................................................

    @Test
    public void testLRTDLabelText() {
        this.labelTextAndCheck(
            SpreadsheetCellRangeReferencePath.LRTD,
            "left-right top-down"
        );
    }

    @Test
    public void testRLTDLabelText() {
        this.labelTextAndCheck(
            SpreadsheetCellRangeReferencePath.RLTD,
            "right-left top-down"
        );
    }

    @Test
    public void testLRBULabelText() {
        this.labelTextAndCheck(
            SpreadsheetCellRangeReferencePath.LRBU,
            "left-right bottom-up"
        );
    }

    private void labelTextAndCheck(final SpreadsheetCellRangeReferencePath path,
                                   final String expected) {
        this.checkEquals(
            expected,
            path.labelText(),
            path::toString
        );
    }

    // first...........................................................................................................

    @Test
    public void testFirstLRTD() {
        this.firstAndCheck(
            SpreadsheetCellRangeReferencePath.LRTD,
            "A1:C3",
            "A1"
        );
    }

    @Test
    public void testFirstRLTD() {
        this.firstAndCheck(
            SpreadsheetCellRangeReferencePath.RLTD,
            "A1:C3",
            "C1"
        );
    }

    @Test
    public void testFirstLRBU() {
        this.firstAndCheck(
            SpreadsheetCellRangeReferencePath.LRBU,
            "A1:C3",
            "A3"
        );
    }

    @Test
    public void testFirstRLBU() {
        this.firstAndCheck(
            SpreadsheetCellRangeReferencePath.RLBU,
            "A1:C3",
            "C3"
        );
    }

    @Test
    public void testFirstTDLR() {
        this.firstAndCheck(
            SpreadsheetCellRangeReferencePath.TDLR,
            "A1:C3",
            "A1"
        );
    }

    @Test
    public void testFirstTDRL() {
        this.firstAndCheck(
            SpreadsheetCellRangeReferencePath.TDRL,
            "A1:C3",
            "C1"
        );
    }

    @Test
    public void testFirstBULR() {
        this.firstAndCheck(
            SpreadsheetCellRangeReferencePath.BULR,
            "A1:C3",
            "A3"
        );
    }

    @Test
    public void testFirstBURL() {
        this.firstAndCheck(
            SpreadsheetCellRangeReferencePath.BURL,
            "A1:C3",
            "C3"
        );
    }


    private void firstAndCheck(final SpreadsheetCellRangeReferencePath path,
                               final String range,
                               final String cell) {
        this.firstAndCheck(
            path,
            SpreadsheetSelection.parseCellRange(range),
            SpreadsheetSelection.parseCell(cell)
        );
    }

    private void firstAndCheck(final SpreadsheetCellRangeReferencePath path,
                               final SpreadsheetCellRangeReference range,
                               final SpreadsheetCellReference cell) {
        this.checkEquals(
            cell,
            path.first(range),
            () -> path + " first " + range
        );
    }

    // lastColumn..........................................................................................................

    @Test
    public void testLastColumnLRTD() {
        this.lastColumnAndCheck(
            SpreadsheetCellRangeReferencePath.LRTD,
            "A1",
            "A1:C3",
            "C1"
        );
    }

    @Test
    public void testLastColumnRLTD() {
        this.lastColumnAndCheck(
            SpreadsheetCellRangeReferencePath.RLTD,
            "C1",
            "A1:C3",
            "A1"
        );
    }

    @Test
    public void testLastColumnLRBU() {
        this.lastColumnAndCheck(
            SpreadsheetCellRangeReferencePath.LRBU,
            "A3",
            "A1:C3",
            "A1"
        );
    }

    @Test
    public void testLastColumnRLBU() {
        this.lastColumnAndCheck(
            SpreadsheetCellRangeReferencePath.RLBU,
            "C3",
            "A1:C3",
            "C1"
        );
    }

    @Test
    public void testLastColumnTDLR() {
        this.lastColumnAndCheck(
            SpreadsheetCellRangeReferencePath.TDLR,
            "A1",
            "A1:C3",
            "A3"
        );
    }

    @Test
    public void testLastColumnTDRL() {
        this.lastColumnAndCheck(
            SpreadsheetCellRangeReferencePath.TDRL,
            "C1",
            "A1:C3",
            "C3"
        );
    }

    @Test
    public void testLastColumnBULR() {
        this.lastColumnAndCheck(
            SpreadsheetCellRangeReferencePath.BULR,
            "A3",
            "A1:C3",
            "A1"
        );
    }

    @Test
    public void testLastColumnBURL() {
        this.lastColumnAndCheck(
            SpreadsheetCellRangeReferencePath.BURL,
            "C3",
            "A1:C3",
            "C1"
        );
    }

    private void lastColumnAndCheck(final SpreadsheetCellRangeReferencePath path,
                                    final String cell,
                                    final String range,
                                    final String lastColumn) {
        this.lastColumnAndCheck(
            path,
            SpreadsheetSelection.parseCell(cell),
            SpreadsheetSelection.parseCellRange(range),
            SpreadsheetSelection.parseCell(lastColumn)
        );
    }

    private void lastColumnAndCheck(final SpreadsheetCellRangeReferencePath path,
                                    final SpreadsheetCellReference cell,
                                    final SpreadsheetCellRangeReference range,
                                    final SpreadsheetCellReference lastColumn) {
        this.checkEquals(
            lastColumn,
            path.lastColumn(
                cell,
                range
            ),
            () -> path + " lastColumn " + cell + " " + range
        );
    }

    // nextRow..........................................................................................................

    @Test
    public void testNextRowLRTD() {
        this.nextRowAndCheck(
            SpreadsheetCellRangeReferencePath.LRTD,
            "A1",
            "A1:C3",
            "A2"
        );
    }

    @Test
    public void testNextRowRLTD() {
        this.nextRowAndCheck(
            SpreadsheetCellRangeReferencePath.RLTD,
            "C1",
            "A1:C3",
            "C2"
        );
    }

    @Test
    public void testNextRowLRBU() {
        this.nextRowAndCheck(
            SpreadsheetCellRangeReferencePath.LRBU,
            "A3",
            "A1:C3",
            "A2"
        );
    }

    @Test
    public void testNextRowRLBU() {
        this.nextRowAndCheck(
            SpreadsheetCellRangeReferencePath.RLBU,
            "C3",
            "A1:C3",
            "B3"
        );
    }

    @Test
    public void testNextRowTDLR() {
        this.nextRowAndCheck(
            SpreadsheetCellRangeReferencePath.TDLR,
            "A1",
            "A1:C3",
            "B1"
        );
    }

    @Test
    public void testNextRowTDRL() {
        this.nextRowAndCheck(
            SpreadsheetCellRangeReferencePath.TDRL,
            "C1",
            "A1:C3",
            "B1"
        );
    }

    @Test
    public void testNextRowBULR() {
        this.nextRowAndCheck(
            SpreadsheetCellRangeReferencePath.BULR,
            "A3",
            "A1:C3",
            "B3"
        );
    }

    @Test
    public void testNextRowBURL() {
        this.nextRowAndCheck(
            SpreadsheetCellRangeReferencePath.BURL,
            "C3",
            "A1:C3",
            "B3"
        );
    }


    private void nextRowAndCheck(final SpreadsheetCellRangeReferencePath path,
                                 final String cell,
                                 final String range,
                                 final String nextRow) {
        this.nextRowAndCheck(
            path,
            SpreadsheetSelection.parseCell(cell),
            SpreadsheetSelection.parseCellRange(range),
            SpreadsheetSelection.parseCell(nextRow)
        );
    }

    private void nextRowAndCheck(final SpreadsheetCellRangeReferencePath path,
                                 final SpreadsheetCellReference cell,
                                 final SpreadsheetCellRangeReference range,
                                 final SpreadsheetCellReference nextRow) {
        this.checkEquals(
            nextRow,
            path.nextRow(
                cell,
                range
            ),
            () -> path + " nextRow " + cell + " " + range
        );
    }

    // width...........................................................................................................

    @Test
    public void testWidthLRTD() {
        this.widthAndCheck(
            SpreadsheetCellRangeReferencePath.LRTD,
            "A1:D2",
            4
        );
    }

    @Test
    public void testWidthRLTD() {
        this.widthAndCheck(
            SpreadsheetCellRangeReferencePath.RLTD,
            "A1:D2",
            4
        );
    }

    @Test
    public void testWidthLRBU() {
        this.widthAndCheck(
            SpreadsheetCellRangeReferencePath.LRBU,
            "A1:D2",
            4
        );
    }

    @Test
    public void testWidthRLBU() {
        this.widthAndCheck(
            SpreadsheetCellRangeReferencePath.RLBU,
            "A1:D2",
            4
        );
    }

    @Test
    public void testWidthTDLR() {
        this.widthAndCheck(
            SpreadsheetCellRangeReferencePath.TDLR,
            "A1:D2",
            2
        );
    }

    @Test
    public void testWidthTDRL() {
        this.widthAndCheck(
            SpreadsheetCellRangeReferencePath.TDRL,
            "A1:D2",
            2
        );
    }

    @Test
    public void testWidthBULR() {
        this.widthAndCheck(
            SpreadsheetCellRangeReferencePath.BULR,
            "A1:D2",
            2
        );
    }

    @Test
    public void testWidthBURL() {
        this.widthAndCheck(
            SpreadsheetCellRangeReferencePath.BURL,
            "A1:D2",
            2
        );
    }

    private void widthAndCheck(final SpreadsheetCellRangeReferencePath path,
                               final String range,
                               final int width) {
        this.widthAndCheck(
            path,
            SpreadsheetSelection.parseCellRange(range),
            width
        );
    }

    private void widthAndCheck(final SpreadsheetCellRangeReferencePath path,
                               final SpreadsheetCellRangeReference range,
                               final int width) {
        this.checkEquals(
            width,
            path.width(range),
            () -> path + " width " + range
        );
    }

    // height...........................................................................................................

    @Test
    public void testHeightLRTD() {
        this.heightAndCheck(
            SpreadsheetCellRangeReferencePath.LRTD,
            "A1:D2",
            2
        );
    }

    @Test
    public void testHeightRLTD() {
        this.heightAndCheck(
            SpreadsheetCellRangeReferencePath.RLTD,
            "A1:D2",
            2
        );
    }

    @Test
    public void testHeightLRBU() {
        this.heightAndCheck(
            SpreadsheetCellRangeReferencePath.LRBU,
            "A1:D2",
            2
        );
    }

    @Test
    public void testHeightRLBU() {
        this.heightAndCheck(
            SpreadsheetCellRangeReferencePath.RLBU,
            "A1:D2",
            2
        );
    }

    @Test
    public void testHeightTDLR() {
        this.heightAndCheck(
            SpreadsheetCellRangeReferencePath.TDLR,
            "A1:D2",
            4
        );
    }

    @Test
    public void testHeightTDRL() {
        this.heightAndCheck(
            SpreadsheetCellRangeReferencePath.TDRL,
            "A1:D2",
            4
        );
    }

    @Test
    public void testHeightBULR() {
        this.heightAndCheck(
            SpreadsheetCellRangeReferencePath.BULR,
            "A1:D2",
            4
        );
    }

    @Test
    public void testHeightBURL() {
        this.heightAndCheck(
            SpreadsheetCellRangeReferencePath.BURL,
            "A1:D2",
            4
        );
    }

    private void heightAndCheck(final SpreadsheetCellRangeReferencePath path,
                                final String range,
                                final int height) {
        this.heightAndCheck(
            path,
            SpreadsheetSelection.parseCellRange(range),
            height
        );
    }

    private void heightAndCheck(final SpreadsheetCellRangeReferencePath path,
                                final SpreadsheetCellRangeReference range,
                                final int height) {
        this.checkEquals(
            height,
            path.height(range),
            () -> path + " height " + range
        );
    }

    // ClassTesting....................................................................................................

    @Override
    public Class<SpreadsheetCellRangeReferencePath> type() {
        return SpreadsheetCellRangeReferencePath.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}

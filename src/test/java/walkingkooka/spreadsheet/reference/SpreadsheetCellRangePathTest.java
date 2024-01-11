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
import walkingkooka.text.CharSequences;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellRangePathTest implements ClassTesting<SpreadsheetCellRangePath> {

    @Test
    public void testFromKebabCaseWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetCellRangePath.fromKebabCase(null)
        );
    }

    @Test
    public void testFromKebabCaseWithUnknownFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetCellRangePath.fromKebabCase("123?")
        );
        this.checkEquals(
                "Got \"123?\" expected one of lrtd, rltd, lrbu, rlbu, tdlr, tdrl, bulr, burl",
                thrown.getMessage()
        );
    }

    @Test
    public void testFromKebabCaseLRTD() {
        this.fromKebabCaseAndCheck(
                "lrtd",
                SpreadsheetCellRangePath.LRTD
        );
    }

    @Test
    public void testFromKebabCaseAllValues() {
        for (final SpreadsheetCellRangePath path : SpreadsheetCellRangePath.values()) {
            this.fromKebabCaseAndCheck(
                    path.name()
                            .toLowerCase(),
                    path
            );
        }
    }

    private void fromKebabCaseAndCheck(final String text,
                                       final SpreadsheetCellRangePath expected) {
        this.checkEquals(
                expected,
                SpreadsheetCellRangePath.fromKebabCase(text),
                () -> "from " + CharSequences.quoteAndEscape(text)
        );
    }

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
                SpreadsheetCellRangePath.LRTD,
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
                SpreadsheetCellRangePath.RLTD,
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
                SpreadsheetCellRangePath.LRBU,
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
                SpreadsheetCellRangePath.RLBU,
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
                SpreadsheetCellRangePath.TDLR,
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
                SpreadsheetCellRangePath.TDRL,
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
                SpreadsheetCellRangePath.BULR,
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
                SpreadsheetCellRangePath.BURL,
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

    private void sortAndCompare(final SpreadsheetCellRangePath direction,
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
        final List<SpreadsheetCellRangePath> directions = Lists.of(
                SpreadsheetCellRangePath.values()
        );
        for (final SpreadsheetCellRangePath direction1 : directions) {
            final List<SpreadsheetCellReference> sorted1 = Lists.array();
            sorted1.addAll(input);
            sorted1.sort(direction1.comparator());

            for (final SpreadsheetCellRangePath direction2 : directions) {
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
        for (final SpreadsheetCellRangePath path : SpreadsheetCellRangePath.values()) {
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
                SpreadsheetCellRangePath.LRTD,
                "left-right top-down"
        );
    }

    @Test
    public void testRLTDLabelText() {
        this.labelTextAndCheck(
                SpreadsheetCellRangePath.RLTD,
                "right-left top-down"
        );
    }

    @Test
    public void testLRBULabelText() {
        this.labelTextAndCheck(
                SpreadsheetCellRangePath.LRBU,
                "left-right bottom-up"
        );
    }

    private void labelTextAndCheck(final SpreadsheetCellRangePath path,
                                   final String expected) {
        this.checkEquals(
                expected,
                path.labelText(),
                () -> path.toString()
        );
    }

    // first...........................................................................................................

    @Test
    public void testFirstLRTD() {
        this.firstAndCheck(
                SpreadsheetCellRangePath.LRTD,
                "A1:C3",
                "A1"
        );
    }

    @Test
    public void testFirstRLTD() {
        this.firstAndCheck(
                SpreadsheetCellRangePath.RLTD,
                "A1:C3",
                "C1"
        );
    }

    @Test
    public void testFirstLRBU() {
        this.firstAndCheck(
                SpreadsheetCellRangePath.LRBU,
                "A1:C3",
                "A3"
        );
    }

    @Test
    public void testFirstRLBU() {
        this.firstAndCheck(
                SpreadsheetCellRangePath.RLBU,
                "A1:C3",
                "C3"
        );
    }

    @Test
    public void testFirstTDLR() {
        this.firstAndCheck(
                SpreadsheetCellRangePath.TDLR,
                "A1:C3",
                "A1"
        );
    }

    @Test
    public void testFirstTDRL() {
        this.firstAndCheck(
                SpreadsheetCellRangePath.TDRL,
                "A1:C3",
                "C1"
        );
    }

    @Test
    public void testFirstBULR() {
        this.firstAndCheck(
                SpreadsheetCellRangePath.BULR,
                "A1:C3",
                "A3"
        );
    }

    @Test
    public void testFirstBURL() {
        this.firstAndCheck(
                SpreadsheetCellRangePath.BURL,
                "A1:C3",
                "C3"
        );
    }


    private void firstAndCheck(final SpreadsheetCellRangePath path,
                               final String range,
                               final String cell) {
        this.firstAndCheck(
                path,
                SpreadsheetSelection.parseCellRange(range),
                SpreadsheetSelection.parseCell(cell)
        );
    }

    private void firstAndCheck(final SpreadsheetCellRangePath path,
                               final SpreadsheetCellRange range,
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
                SpreadsheetCellRangePath.LRTD,
                "A1",
                "A1:C3",
                "C1"
        );
    }

    @Test
    public void testLastColumnRLTD() {
        this.lastColumnAndCheck(
                SpreadsheetCellRangePath.RLTD,
                "C1",
                "A1:C3",
                "A1"
        );
    }

    @Test
    public void testLastColumnLRBU() {
        this.lastColumnAndCheck(
                SpreadsheetCellRangePath.LRBU,
                "A3",
                "A1:C3",
                "A1"
        );
    }

    @Test
    public void testLastColumnRLBU() {
        this.lastColumnAndCheck(
                SpreadsheetCellRangePath.RLBU,
                "C3",
                "A1:C3",
                "C1"
        );
    }

    @Test
    public void testLastColumnTDLR() {
        this.lastColumnAndCheck(
                SpreadsheetCellRangePath.TDLR,
                "A1",
                "A1:C3",
                "A3"
        );
    }

    @Test
    public void testLastColumnTDRL() {
        this.lastColumnAndCheck(
                SpreadsheetCellRangePath.TDRL,
                "C1",
                "A1:C3",
                "C3"
        );
    }

    @Test
    public void testLastColumnBULR() {
        this.lastColumnAndCheck(
                SpreadsheetCellRangePath.BULR,
                "A3",
                "A1:C3",
                "A1"
        );
    }

    @Test
    public void testLastColumnBURL() {
        this.lastColumnAndCheck(
                SpreadsheetCellRangePath.BURL,
                "C3",
                "A1:C3",
                "C1"
        );
    }

    private void lastColumnAndCheck(final SpreadsheetCellRangePath path,
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

    private void lastColumnAndCheck(final SpreadsheetCellRangePath path,
                                    final SpreadsheetCellReference cell,
                                    final SpreadsheetCellRange range,
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
                SpreadsheetCellRangePath.LRTD,
                "A1",
                "A1:C3",
                "A2"
        );
    }

    @Test
    public void testNextRowRLTD() {
        this.nextRowAndCheck(
                SpreadsheetCellRangePath.RLTD,
                "C1",
                "A1:C3",
                "C2"
        );
    }

    @Test
    public void testNextRowLRBU() {
        this.nextRowAndCheck(
                SpreadsheetCellRangePath.LRBU,
                "A3",
                "A1:C3",
                "A2"
        );
    }

    @Test
    public void testNextRowRLBU() {
        this.nextRowAndCheck(
                SpreadsheetCellRangePath.RLBU,
                "C3",
                "A1:C3",
                "B3"
        );
    }

    @Test
    public void testNextRowTDLR() {
        this.nextRowAndCheck(
                SpreadsheetCellRangePath.TDLR,
                "A1",
                "A1:C3",
                "B1"
        );
    }

    @Test
    public void testNextRowTDRL() {
        this.nextRowAndCheck(
                SpreadsheetCellRangePath.TDRL,
                "C1",
                "A1:C3",
                "B1"
        );
    }

    @Test
    public void testNextRowBULR() {
        this.nextRowAndCheck(
                SpreadsheetCellRangePath.BULR,
                "A3",
                "A1:C3",
                "B3"
        );
    }

    @Test
    public void testNextRowBURL() {
        this.nextRowAndCheck(
                SpreadsheetCellRangePath.BURL,
                "C3",
                "A1:C3",
                "B3"
        );
    }


    private void nextRowAndCheck(final SpreadsheetCellRangePath path,
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

    private void nextRowAndCheck(final SpreadsheetCellRangePath path,
                                 final SpreadsheetCellReference cell,
                                 final SpreadsheetCellRange range,
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
                SpreadsheetCellRangePath.LRTD,
                "A1:D2",
                4
        );
    }

    @Test
    public void testWidthRLTD() {
        this.widthAndCheck(
                SpreadsheetCellRangePath.RLTD,
                "A1:D2",
                4
        );
    }

    @Test
    public void testWidthLRBU() {
        this.widthAndCheck(
                SpreadsheetCellRangePath.LRBU,
                "A1:D2",
                4
        );
    }

    @Test
    public void testWidthRLBU() {
        this.widthAndCheck(
                SpreadsheetCellRangePath.RLBU,
                "A1:D2",
                4
        );
    }

    @Test
    public void testWidthTDLR() {
        this.widthAndCheck(
                SpreadsheetCellRangePath.TDLR,
                "A1:D2",
                2
        );
    }

    @Test
    public void testWidthTDRL() {
        this.widthAndCheck(
                SpreadsheetCellRangePath.TDRL,
                "A1:D2",
                2
        );
    }

    @Test
    public void testWidthBULR() {
        this.widthAndCheck(
                SpreadsheetCellRangePath.BULR,
                "A1:D2",
                2
        );
    }

    @Test
    public void testWidthBURL() {
        this.widthAndCheck(
                SpreadsheetCellRangePath.BURL,
                "A1:D2",
                2
        );
    }

    private void widthAndCheck(final SpreadsheetCellRangePath path,
                               final String range,
                               final int width) {
        this.widthAndCheck(
                path,
                SpreadsheetSelection.parseCellRange(range),
                width
        );
    }

    private void widthAndCheck(final SpreadsheetCellRangePath path,
                               final SpreadsheetCellRange range,
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
                SpreadsheetCellRangePath.LRTD,
                "A1:D2",
                2
        );
    }

    @Test
    public void testHeightRLTD() {
        this.heightAndCheck(
                SpreadsheetCellRangePath.RLTD,
                "A1:D2",
                2
        );
    }

    @Test
    public void testHeightLRBU() {
        this.heightAndCheck(
                SpreadsheetCellRangePath.LRBU,
                "A1:D2",
                2
        );
    }

    @Test
    public void testHeightRLBU() {
        this.heightAndCheck(
                SpreadsheetCellRangePath.RLBU,
                "A1:D2",
                2
        );
    }

    @Test
    public void testHeightTDLR() {
        this.heightAndCheck(
                SpreadsheetCellRangePath.TDLR,
                "A1:D2",
                4
        );
    }

    @Test
    public void testHeightTDRL() {
        this.heightAndCheck(
                SpreadsheetCellRangePath.TDRL,
                "A1:D2",
                4
        );
    }

    @Test
    public void testHeightBULR() {
        this.heightAndCheck(
                SpreadsheetCellRangePath.BULR,
                "A1:D2",
                4
        );
    }

    @Test
    public void testHeightBURL() {
        this.heightAndCheck(
                SpreadsheetCellRangePath.BURL,
                "A1:D2",
                4
        );
    }

    private void heightAndCheck(final SpreadsheetCellRangePath path,
                                final String range,
                                final int height) {
        this.heightAndCheck(
                path,
                SpreadsheetSelection.parseCellRange(range),
                height
        );
    }

    private void heightAndCheck(final SpreadsheetCellRangePath path,
                                final SpreadsheetCellRange range,
                                final int height) {
        this.checkEquals(
                height,
                path.height(range),
                () -> path + " height " + range
        );
    }

    // ClassTesting....................................................................................................

    @Override
    public Class<SpreadsheetCellRangePath> type() {
        return SpreadsheetCellRangePath.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}

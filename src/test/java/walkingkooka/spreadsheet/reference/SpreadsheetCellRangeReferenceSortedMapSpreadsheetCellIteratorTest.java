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
import walkingkooka.collect.iterator.IteratorTesting;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.value.SpreadsheetCell;

import java.util.Set;
import java.util.SortedMap;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellRangeReferenceSortedMapSpreadsheetCellIteratorTest implements IteratorTesting,
    ClassTesting<SpreadsheetCellRangeReferenceSortedMapSpreadsheetCellIterator> {

    @Test
    public void testWithNullMapFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetCellRangeReferenceSortedMapSpreadsheetCellIterator.with(
                SpreadsheetSelection.ALL_CELLS,
                null
            )
        );
    }

    @Test
    public void testEmptyMap() {
        this.iteratorCheck(
            "A1:B2"
        );
    }

    @Test
    public void testCellsBeforeTopLeft() {
        this.iteratorCheck(
            "B2:C3",
            "A1"
        );
    }

    @Test
    public void testCellsAfterBottomRight() {
        this.iteratorCheck(
            "B2:C3",
            "D4"
        );
    }

    @Test
    public void testCellsBefore() {
        this.iteratorCheck(
            "B2:C3",
            "A2",
            "A3"
        );
    }

    @Test
    public void testCellsAfter() {
        this.iteratorCheck(
            "B2:C3",
            "D2",
            "D3"
        );
    }

    @Test
    public void testCellsBeforeAndAfter() {
        this.iteratorCheck(
            "B2:C3",
            "A1",
            "D2",
            "A3",
            "D3"
        );
    }

    @Test
    public void testCellsSome() {
        this.iteratorCheck(
            "B2:C3",
            "A1",
            "B1",
            "B2",
            "B3",
            "B4",
            "C1",
            "C2",
            "C3",
            "C4"
        );
    }

    @Test
    public void testCellsSome2() {
        this.iteratorCheck(
            "B2:C3",
            "A1",
            "B1",
            "B2",
            "B3",
            "B4",
            "C1",
            "C2",
            "C3",
            "C4",
            "D4"
        );
    }

    @Test
    public void testCellsSome3() {
        this.iteratorCheck(
            "B2:C3",
            "A1",
            "B1",
            "B3",
            "B4",
            "C1",
            "C2",
            "C4",
            "D4"
        );
    }

    @Test
    public void testCellsOnly1() {
        this.iteratorCheck(
            "B2:C3",
            "B2"
        );
    }

    @Test
    public void testCellsOnly2() {
        this.iteratorCheck(
            "B2:C3",
            "C2",
            "B3"
        );
    }

    @Test
    public void testCellsOnly3() {
        this.iteratorCheck(
            "B2:C3",
            "B2",
            "C2",
            "B3",
            "C3"
        );
    }

    @Test
    public void testBigMostlyEmptyRange() {
        this.iteratorCheck(
            "A1:Z99",
            "A1"
        );
    }

    @Test
    public void testBigMostlyEmptyRange2() {
        this.iteratorCheck(
            "A1:Z99",
            "B2"
        );
    }

    @Test
    public void testBigMostlyEmptyRange3() {
        this.iteratorCheck(
            "A1:Z99",
            "Z99"
        );
    }

    @Test
    public void testAll() {
        this.iteratorCheck(
            SpreadsheetSelection.ALL_CELLS.toString(),
            "A1"
        );
    }

    @Test
    public void testAll2() {
        this.iteratorCheck(
            SpreadsheetSelection.ALL_CELLS.toString(),
            "A1",
            "B2"
        );
    }

    @Test
    public void testAll3() {
        this.iteratorCheck(
            SpreadsheetSelection.ALL_CELLS.toString(),
            "B2"
        );
    }

    @Test
    public void testAll4() {
        this.iteratorCheck(
            SpreadsheetSelection.ALL_CELLS.toString(),
            SpreadsheetSelection.ALL_CELLS.end().toString()
        );
    }

    private void iteratorCheck(final String range,
                               final String... cells) {
        this.iteratorCheck(
            range,
            Sets.of(cells)
        );
    }

    private void iteratorCheck(final String range,
                               final Set<String> cells) {
        this.iteratorCheck(
            SpreadsheetSelection.parseCellRange(range),
            cells.stream()
                .map(c -> SpreadsheetSelection.parseCell(c).setFormula(SpreadsheetFormula.EMPTY))
                .collect(Collectors.toCollection(() -> SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR)))
        );
    }

    private void iteratorCheck(final SpreadsheetCellRangeReference range,
                               final Set<SpreadsheetCell> cells) {
        final SortedMap<SpreadsheetCellReference, SpreadsheetCell> cellMap = SpreadsheetSelectionMaps.cell();
        final Set<SpreadsheetCell> iterated = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);

        cells.forEach(
            (c) -> {
                final SpreadsheetCellReference cellReference = c.reference();

                cellMap.put(
                    cellReference,
                    c
                );

                if (range.testCell(cellReference)) {
                    iterated.add(c);
                }
            }
        );

        final SpreadsheetCell[] expected = iterated.toArray(
            new SpreadsheetCell[iterated.size()]
        );

        this.iterateUsingHasNextAndCheck(
            SpreadsheetCellRangeReferenceSortedMapSpreadsheetCellIterator.with(
                range,
                cellMap
            ),
            expected
        );

        this.iterateAndCheck(
            SpreadsheetCellRangeReferenceSortedMapSpreadsheetCellIterator.with(
                range,
                cellMap
            ),
            expected
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetCellRangeReferenceSortedMapSpreadsheetCellIterator> type() {
        return SpreadsheetCellRangeReferenceSortedMapSpreadsheetCellIterator.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}

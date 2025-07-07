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
import walkingkooka.collect.iterator.IteratorTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;

public final class SpreadsheetCellRangeReferencePathCellsIteratorTest implements IteratorTesting,
    ClassTesting<SpreadsheetCellRangeReferencePathCellsIterator>,
    ToStringTesting<SpreadsheetCellRangeReferencePathCellsIterator> {

    private final static SpreadsheetCellReference A1 = SpreadsheetSelection.A1;

    private final static SpreadsheetCellReference B1 = SpreadsheetSelection.parseCell("B1");

    private final static SpreadsheetCellReference C1 = SpreadsheetSelection.parseCell("C1");

    private final static SpreadsheetCellReference A2 = SpreadsheetSelection.parseCell("A2");

    private final static SpreadsheetCellReference B2 = SpreadsheetSelection.parseCell("B2");

    private final static SpreadsheetCellReference C2 = SpreadsheetSelection.parseCell("C2");

    private final static SpreadsheetCellReference A3 = SpreadsheetSelection.parseCell("A3");

    private final static SpreadsheetCellReference B3 = SpreadsheetSelection.parseCell("B3");

    private final static SpreadsheetCellReference C3 = SpreadsheetSelection.parseCell("C3");

    @Test
    public void testLRTD() {
        this.iterateAndCheck2(
            SpreadsheetCellRangeReferencePath.LRTD,
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
        this.iterateAndCheck2(
            SpreadsheetCellRangeReferencePath.RLTD,
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
        this.iterateAndCheck2(
            SpreadsheetCellRangeReferencePath.LRBU,
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
        this.iterateAndCheck2(
            SpreadsheetCellRangeReferencePath.RLBU,
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
        this.iterateAndCheck2(
            SpreadsheetCellRangeReferencePath.TDLR,
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

    /**
     * <pre>
     * 7 4 1
     * 8 5 2
     * 9 6 3
     * </pre>
     */
    @Test
    public void testTDRL() {
        this.iterateAndCheck2(
            SpreadsheetCellRangeReferencePath.TDRL,
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

    /**
     * <pre>
     * 3 6 9
     * 2 5 8
     * 1 4 7
     * </pre>
     */
    @Test
    public void testBULR() {
        this.iterateAndCheck2(
            SpreadsheetCellRangeReferencePath.BULR,
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

    /**
     * <pre>
     * 9 6 3
     * 8 5 2
     * 7 4 1
     * </pre>
     */
    @Test
    public void testBURL() {
        this.iterateAndCheck2(
            SpreadsheetCellRangeReferencePath.BURL,
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

    private void iterateAndCheck2(final SpreadsheetCellRangeReferencePath path,
                                  final SpreadsheetCellReference... expected) {
        this.iterateAndCheck(
            SpreadsheetCellRangeReferencePathCellsIterator.with(
                SpreadsheetSelection.parseCellRange("A1:C3"),
                path
            ),
            expected
        );
    }

    // ToStringTesting..................................................................................................

    @Test
    public void testToString() {
        final SpreadsheetCellRangeReference cells = SpreadsheetSelection.parseCellRange("A1:C3");
        final SpreadsheetCellRangeReferencePath path = SpreadsheetCellRangeReferencePath.BULR;

        this.toStringAndCheck(
            SpreadsheetCellRangeReferencePathCellsIterator.with(cells, path),
            cells + " " + path
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetCellRangeReferencePathCellsIterator> type() {
        return SpreadsheetCellRangeReferencePathCellsIterator.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}

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

import java.util.Arrays;
import java.util.List;

public final class SpreadsheetCellReferenceDirectionTest implements ClassTesting<SpreadsheetCellReferenceDirection> {

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
                SpreadsheetCellReferenceDirection.LRTD,
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
                SpreadsheetCellReferenceDirection.RLTD,
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
                SpreadsheetCellReferenceDirection.LRBU,
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
                SpreadsheetCellReferenceDirection.RLBU,
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
                SpreadsheetCellReferenceDirection.TDLR,
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
                SpreadsheetCellReferenceDirection.TDRL,
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
                SpreadsheetCellReferenceDirection.BULR,
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
                SpreadsheetCellReferenceDirection.BURL,
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

    private void sortAndCompare(final SpreadsheetCellReferenceDirection direction,
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
        final List<SpreadsheetCellReferenceDirection> directions = Lists.of(
                SpreadsheetCellReferenceDirection.values()
        );
        for (final SpreadsheetCellReferenceDirection direction1 : directions) {
            final List<SpreadsheetCellReference> sorted1 = Lists.array();
            sorted1.addAll(input);
            sorted1.sort(direction1.comparator());

            for (final SpreadsheetCellReferenceDirection direction2 : directions) {
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

    // ClassTesting....................................................................................................

    @Override
    public Class<SpreadsheetCellReferenceDirection> type() {
        return SpreadsheetCellReferenceDirection.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}

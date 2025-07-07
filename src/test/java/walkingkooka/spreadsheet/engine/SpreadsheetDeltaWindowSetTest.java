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

package walkingkooka.spreadsheet.engine;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.ImmutableSetTesting;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetDeltaWindowSetTest implements ImmutableSetTesting<SpreadsheetDeltaWindowSet, SpreadsheetCellRangeReference> {

    @Test
    public void testWithOverlapFails() {
        this.withFails(
            "Window contains overlapping ranges A1:B2 and A1",
            a1b2(),
            c1e2(),
            SpreadsheetSelection.parseCellRange("a1")
        );
    }

    @Test
    public void testWithOverlapFails2() {
        this.withFails(
            "Window contains overlapping ranges A3:B5 and A4",
            a1b2(),
            c1e2(),
            a3b5(),
            SpreadsheetSelection.parseCellRange("a4")
        );
    }

    private void withFails(final String message,
                           final SpreadsheetCellRangeReference... ranges) {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetDeltaWindowSet.with(Sets.of(ranges))
        );
        this.checkEquals(message, thrown.getMessage());
    }

    @Test
    public void testWithOne() {
        final SpreadsheetCellRangeReference a1b2 = this.a1b2();

        SpreadsheetDeltaWindowSet.with(
            Sets.of(a1b2)
        );
    }

    @Test
    public void testWithTwo() {
        final SpreadsheetCellRangeReference a1b2 = this.a1b2();
        final SpreadsheetCellRangeReference c1e2 = this.c1e2();

        SpreadsheetDeltaWindowSet.with(
            Sets.of(a1b2, c1e2)
        );
    }

    // A1
    //   B2
    // A3
    //   B5
    @Test
    public void testWithTwo2() {
        final SpreadsheetCellRangeReference a1b2 = this.a1b2();
        final SpreadsheetCellRangeReference a3b5 = this.a3b5();

        SpreadsheetDeltaWindowSet.with(
            Sets.of(a1b2, a3b5)
        );
    }

    // A1   C1
    //   B2     E2
    // A3
    //   B5
    @Test
    public void testWithThree() {
        final SpreadsheetCellRangeReference a1b2 = this.a1b2();
        final SpreadsheetCellRangeReference c1e2 = this.c1e2();
        final SpreadsheetCellRangeReference a3b5 = this.a3b5();

        SpreadsheetDeltaWindowSet.with(
            Sets.of(a1b2, c1e2, a3b5)
        );
    }

    @Test
    public void testWithFour() {
        final SpreadsheetCellRangeReference a1b2 = this.a1b2();
        final SpreadsheetCellRangeReference c1e2 = this.c1e2();
        final SpreadsheetCellRangeReference a3b5 = this.a3b5();
        final SpreadsheetCellRangeReference c3e5 = this.c3e5();

        SpreadsheetDeltaWindowSet.with(
            Sets.of(a1b2, c1e2, a3b5, c3e5)
        );
    }

    @Test
    public void testContains() {
        final SpreadsheetCellRangeReference a1b2 = this.a1b2();
        final SpreadsheetCellRangeReference c1e2 = this.c1e2();
        final SpreadsheetCellRangeReference a3b5 = this.a3b5();
        final SpreadsheetCellRangeReference c3e5 = this.c3e5();

        this.containsAndCheck(
            SpreadsheetDeltaWindowSet.with(
                Sets.of(a1b2, c1e2, a3b5, c3e5)
            ),
            a1b2
        );
    }

    @Test
    public void testContains2() {
        final SpreadsheetCellRangeReference a1b2 = this.a1b2();
        final SpreadsheetCellRangeReference c1e2 = this.c1e2();
        final SpreadsheetCellRangeReference a3b5 = this.a3b5();
        final SpreadsheetCellRangeReference c3e5 = this.c3e5();

        this.containsAndCheck(
            SpreadsheetDeltaWindowSet.with(
                Sets.of(a1b2, c1e2, a3b5, c3e5)
            ),
            c1e2
        );
    }

    @Override
    public SpreadsheetDeltaWindowSet createSet() {
        return SpreadsheetDeltaWindowSet.with(
            Sets.of(this.a1b2())
        );
    }

    @Test
    public void testDelete() {
        final SpreadsheetCellRangeReference a1b2 = this.a1b2();
        final SpreadsheetCellRangeReference c1e2 = this.c1e2();
        final SpreadsheetCellRangeReference a3b5 = this.a3b5();
        final SpreadsheetCellRangeReference c3e5 = this.c3e5();

        final SpreadsheetDeltaWindowSet set = SpreadsheetDeltaWindowSet.with(
            Sets.of(a1b2, c1e2, a3b5, c3e5)
        );

        this.deleteAndCheck(
            set,
            a1b2,
            SpreadsheetDeltaWindowSet.with(
                Sets.of(c1e2, a3b5, c3e5)
            )
        );
    }

    @Test
    public void testSetElementsIncludesNullFails() {
        final NullPointerException thrown = assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDeltaWindowSet.with(
                Sets.of(
                    this.a1b2(),
                    null
                )
            )
        );

        this.checkEquals(
            "Window includes null cell-range",
            thrown.getMessage()
        );
    }

    @Test
    public void testSetElementsIncludesNullFails2() {
        final NullPointerException thrown = assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDeltaWindowSet.with(
                Sets.of(
                    this.a1b2(),
                    this.a3b5(),
                    null
                )
            )
        );

        this.checkEquals(
            "Window includes null cell-range",
            thrown.getMessage()
        );
    }

    @Test
    public void testSetElementsIncludesNullFails3() {
        final NullPointerException thrown = assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDeltaWindowSet.with(
                Sets.of(
                    this.a1b2(),
                    this.a3b5(),
                    this.c1e2(),
                    null
                )
            )
        );

        this.checkEquals(
            "Window includes null cell-range",
            thrown.getMessage()
        );
    }

    private SpreadsheetCellRangeReference a1b2() {
        return SpreadsheetSelection.parseCellRange("a1:b2");
    }

    private SpreadsheetCellRangeReference c1e2() {
        return SpreadsheetSelection.parseCellRange("c1:e2");
    }

    private SpreadsheetCellRangeReference a3b5() {
        return SpreadsheetSelection.parseCellRange("a3:b5");
    }

    private SpreadsheetCellRangeReference c3e5() {
        return SpreadsheetSelection.parseCellRange("c3:e5");
    }

    // Class............................................................................................................

    @Override
    public Class<SpreadsheetDeltaWindowSet> type() {
        return SpreadsheetDeltaWindowSet.class;
    }
}

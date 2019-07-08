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

package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.compare.Range;
import walkingkooka.type.JavaVisibility;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetDeltaTest extends SpreadsheetDeltaTestCase<SpreadsheetDelta<Optional<SpreadsheetId>>, Optional<SpreadsheetId>> {

    @Test
    public void testWithIdNullIdFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetDelta.withId(null, this.cells());
        });
    }

    @Test
    public void testWithIdNullCellsFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetDelta.withId(this.id(), null);
        });
    }

    @Test
    public void testWithId() {
        final Optional<SpreadsheetId> id = this.id();
        final Set<SpreadsheetCell> cells = this.cells();
        final SpreadsheetDelta delta = SpreadsheetDelta.withId(id, cells);
        this.checkId(delta, id);
        this.checkCells(delta, cells);
    }

    @Test
    public void testWithRangeNullRangeFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetDelta.withRange(null, this.cells());
        });
    }

    @Test
    public void testWithRangeNullCellsFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetDelta.withRange(this.range(1, 2), null);
        });
    }

    @Test
    public void testWithRange() {
        final Range<SpreadsheetId> range = this.range(1, 2);
        final Set<SpreadsheetCell> cells = this.cells();
        final SpreadsheetDelta delta = SpreadsheetDelta.withRange(range, cells);
        this.checkId(delta, range);
        this.checkCells(delta, cells);
    }

    private Optional<SpreadsheetId> id() {
        return Optional.of(SpreadsheetId.with(0x1234));
    }

    private Optional<SpreadsheetId> differentId() {
        return Optional.of(SpreadsheetId.with(0x99));
    }

    // ClassTesting..........................................................................................

    @Override
    public Class<SpreadsheetDelta<Optional<SpreadsheetId>>> type() {
        return Cast.to(SpreadsheetDelta.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}

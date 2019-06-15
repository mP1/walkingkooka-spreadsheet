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
import walkingkooka.type.JavaVisibility;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetDeltaTest extends SpreadsheetDeltaTestCase<SpreadsheetDelta> {

    @Test
    public void testWithNullIdFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetDelta.with(null, this.cells());
        });
    }

    @Test
    public void testWithNullCellsFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetDelta.with(this.id(), null);
        });
    }

    @Test
    public void testWith() {
        final SpreadsheetId id = this.id();
        final Set<SpreadsheetCell> cells = this.cells();
        final SpreadsheetDelta delta = SpreadsheetDelta.with(id, cells);
        this.checkId(delta, id);
        this.checkCells(delta, cells);
    }

    // ClassTesting..........................................................................................

    @Override
    public Class<SpreadsheetDelta> type() {
        return SpreadsheetDelta.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}

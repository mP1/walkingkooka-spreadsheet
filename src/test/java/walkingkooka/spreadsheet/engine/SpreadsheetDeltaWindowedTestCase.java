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
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public abstract class SpreadsheetDeltaWindowedTestCase<D extends SpreadsheetDelta> extends SpreadsheetDeltaTestCase2<D> {

    SpreadsheetDeltaWindowedTestCase() {
        super();
        assertNotEquals(this.window(), this.differentWindow(), "window v differentWindow must NOT be equal");
    }

    @Test
    public final void testEqualsDifferentWindow() {
        this.checkNotEquals(this.createSpreadsheetDelta(this.cells(), this.differentWindow()));
    }

    @Override
    final List<SpreadsheetRange> window() {
        return this.window0("A1:E5", "F6:Z99");
    }

    @Override
    final D createSpreadsheetDelta(final Set<SpreadsheetCell> cells) {
        return this.createSpreadsheetDelta(cells, this.window());
    }

    abstract D createSpreadsheetDelta(final Set<SpreadsheetCell> cells, final List<SpreadsheetRange> window);
}

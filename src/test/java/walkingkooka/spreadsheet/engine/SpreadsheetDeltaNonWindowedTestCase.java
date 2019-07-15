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
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;

import java.util.List;
import java.util.Set;

public abstract class SpreadsheetDeltaNonWindowedTestCase<D extends SpreadsheetDelta<I>, I> extends SpreadsheetDeltaTestCase2<D, I> {

    SpreadsheetDeltaNonWindowedTestCase() {
        super();
    }

    @Test
    public final void testSetDifferentCells() {
        final D delta = this.createSpreadsheetDelta();
        final Set<SpreadsheetCell> cells = this.cells0("B2", "C3");
        final SpreadsheetDelta different = delta.setCells(cells);
        this.checkId(different);
        this.checkCells(different, cells);

        this.checkId(delta);
        this.checkCells(delta);
    }

    @Override
    final List<SpreadsheetRange> window() {
        return Lists.empty();
    }
}

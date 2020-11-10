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
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRectangle;

public final class SpreadsheetDeltaTest extends SpreadsheetDeltaTestCase<SpreadsheetDelta> {

    @Test
    public void testWith() {
        final SpreadsheetDelta delta = SpreadsheetDelta.with(this.cells());
        this.checkCells(delta);
        this.checkMaxColumnWidths(delta, SpreadsheetDelta.NO_MAX_COLUMN_WIDTHS);
        this.checkMaxRowHeights(delta, SpreadsheetDelta.NO_MAX_ROW_HEIGHTS);
        this.checkWindow(delta, Lists.empty());
    }

    @Test
    public void testSetWindowIncludesSpreadsheetPixelRectangle() {
        final SpreadsheetDelta delta = SpreadsheetDelta.with(this.cells());
        final SpreadsheetDelta different = delta.setWindow(Lists.of(SpreadsheetExpressionReference.parseRange("Z99:Z100"), SpreadsheetRectangle.parseRectangle("1x2")));
        this.checkCells(different, this.cells());
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetDelta> type() {
        return SpreadsheetDelta.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}

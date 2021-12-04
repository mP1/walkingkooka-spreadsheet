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
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.spreadsheet.SpreadsheetViewport;

public abstract class SpreadsheetCellReferenceOrLabelNameTestCase<S extends SpreadsheetCellReferenceOrLabelName & Comparable<S>> extends SpreadsheetExpressionReferenceTestCase<S>
        implements ComparableTesting2<S> {

    SpreadsheetCellReferenceOrLabelNameTestCase() {
        super();
    }

    // SpreadsheetViewport.........................................................................................

    @Test
    public final void testViewport() {
        final double xOffset = 10.5;
        final double yOffset = 20.5;
        final double width = 30.5;
        final double height = 40.5;
        final S selection = this.createSelection();

        final SpreadsheetViewport viewport = selection.viewport(xOffset, yOffset, width, height);

        this.checkEquals(selection.toRelative(), viewport.cellOrLabel(), "cellOrLabel");
        this.checkEquals(xOffset, viewport.xOffset(), "xOffset");
        this.checkEquals(yOffset, viewport.yOffset(), "yOffset");
        this.checkEquals(width, viewport.width(), "width");
        this.checkEquals(height, viewport.height(), "height");
    }
}

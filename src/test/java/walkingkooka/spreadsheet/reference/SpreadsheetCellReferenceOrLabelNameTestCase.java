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

public abstract class SpreadsheetCellReferenceOrLabelNameTestCase<S extends SpreadsheetCellReferenceOrLabelName & Comparable<S>> extends SpreadsheetExpressionReferenceTestCase<S>
        implements ComparableTesting2<S> {

    SpreadsheetCellReferenceOrLabelNameTestCase() {
        super();
    }

    // simplify.........................................................................................................

    @Test
    public final void testSimplify() {
        this.simplifyAndCheck(
                this.createSelection()
        );
    }

    // SpreadsheetViewport.........................................................................................

    @Test
    public final void testViewport() {
        final SpreadsheetExpressionReference selection = this.createSelection();
        final double width = 30.5;
        final double height = 40.5;

        this.viewportAndCheck(
                selection,
                width,
                height,
                selection
        );
    }
}

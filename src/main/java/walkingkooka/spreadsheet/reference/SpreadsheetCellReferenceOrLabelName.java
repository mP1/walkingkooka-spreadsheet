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

import walkingkooka.spreadsheet.SpreadsheetViewport;

/**
 * Base class shared by {@link SpreadsheetCellReference} and {@link SpreadsheetLabelName}.
 * This type is necessary to avoid J2clTranspiler failures because of what appears to be a failure of the JDT not being
 * able to handle type parameters with multiple bounds.
 */
abstract public class SpreadsheetCellReferenceOrLabelName extends SpreadsheetExpressionReference {

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetCellReferenceOrLabelName() {
        super();
    }

    // SpreadsheetViewport........................................................................................

    /**
     * Creates a {@link SpreadsheetViewport} using this as the top/left.
     */
    public final SpreadsheetViewport viewport(final double xOffset,
                                              final double yOffset,
                                              final double width,
                                              final double height) {
        return SpreadsheetViewport.with(this, xOffset, yOffset, width, height);
    }

    abstract public SpreadsheetCellReferenceOrLabelName toRelative();
}

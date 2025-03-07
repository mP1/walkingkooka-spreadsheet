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

package walkingkooka.spreadsheet.store;

import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;

import java.util.Set;

/**
 * A {@link walkingkooka.store.Store} that holds external cell references for cells.
 * This is used to find cells that need to be recomputed when the external reference changes. In the example below
 * A1 would need to be recomputed when either A2 or A3 change values.
 * <pre>
 * A1=A2+A3+1 // A1=A2, A3
 * </pre>
 */
public interface SpreadsheetCellReferencesStore extends SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> {

    /**
     * Finds any {@link SpreadsheetCellReference} with the provided {@link SpreadsheetCellReferenceOrRange}.
     */
    Set<SpreadsheetCellReference> findCellsWithCellOrCellRange(final SpreadsheetCellReferenceOrRange cellOrCellRange,
                                                               final int offset,
                                                               final int count);
}

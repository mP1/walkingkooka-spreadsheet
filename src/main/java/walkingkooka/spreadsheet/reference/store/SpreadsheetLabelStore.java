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

package walkingkooka.spreadsheet.reference.store;

import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.store.Store;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Set;

/**
 * A store that holds all label to cell references for a spreadsheet. No additional actions are supported.
 */
public interface SpreadsheetLabelStore extends Store<SpreadsheetLabelName, SpreadsheetLabelMapping> {

    /**
     * Returns all {@link SpreadsheetCellReference} for the given {@link SpreadsheetLabelName}, including resolving
     * label to label references until they resolve to cells.
     */
    Set<? super ExpressionReference> loadCellReferencesOrRanges(final SpreadsheetLabelName label);

    /**
     * Returns all {@link SpreadsheetLabelName} that eventually map to the {@link SpreadsheetCellReference}, including resolving
     * label to label references until they resolve to cells.
     */
    Set<SpreadsheetLabelName> labels(final SpreadsheetCellReference cell);
}
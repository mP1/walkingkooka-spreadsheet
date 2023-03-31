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

import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * A store that holds all label to cell references for a spreadsheet. No additional actions are supported.
 */
public interface SpreadsheetLabelStore extends SpreadsheetStore<SpreadsheetLabelName, SpreadsheetLabelMapping> {

    /**
     * Finds all {@link SpreadsheetLabelName} for the given text.
     */
    Set<SpreadsheetLabelMapping> findSimilar(final String text, final int max);

    /**
     * Returns all {@link SpreadsheetCellReference} for the given {@link SpreadsheetLabelName}, including resolving
     * label to label references until they resolve to cells.
     */
    Set<? super ExpressionReference> loadCellReferencesOrRanges(final SpreadsheetLabelName label);

    /**
     * Returns all the {@link SpreadsheetLabelMapping} for the given {@link SpreadsheetExpressionReference}.
     * This includes resolving labels to other labels and eventually a {@link SpreadsheetExpressionReference}.
     */
    Set<SpreadsheetLabelMapping> labels(final SpreadsheetExpressionReference reference);

    /**
     * Attempts to resolve the given {@link SpreadsheetExpressionReference} which may be a label to a {@link SpreadsheetCellReferenceOrRange}.
     */
    default Optional<SpreadsheetCellReferenceOrRange> cellReferenceOrRange(final SpreadsheetExpressionReference reference) {
        return SpreadsheetLabelStoreCellReferenceOrRangeSpreadsheetSelectionVisitor.cellReferenceOrRange(
                reference,
                this
        );
    }

    /**
     * Attempts to resolve the given {@link SpreadsheetExpressionReference} which may be a label to a {@link SpreadsheetCellRange}.
     * This exists primarily so this can be passed as a method reference to {@link walkingkooka.spreadsheet.reference.SpreadsheetSelection#toCellRange(Function)}.
     */
    default Optional<SpreadsheetCellRange> cellRange(final SpreadsheetExpressionReference reference) {
        return this.cellReferenceOrRange(reference)
                .map(SpreadsheetCellReferenceOrRange::toCellRange);
    }

    /**
     * Attempts to resolve any labels to a {@link SpreadsheetCellReference} throwing an {@link IllegalArgumentException}
     * if any label resolution fails.
     */
    default SpreadsheetCellReferenceOrRange cellReferenceOrRangeOrFail(final SpreadsheetExpressionReference reference) {
        return this.cellReferenceOrRange(reference)
                .orElseThrow(() -> this.notFound(reference));
    }
}
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

import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.value.SpreadsheetError;
import walkingkooka.spreadsheet.value.SpreadsheetErrorException;

import java.util.Optional;
import java.util.Set;

/**
 * A store that holds all label to cell references for a spreadsheet. No additional actions are supported.
 * When an attempt is made to save a {@link SpreadsheetLabelMapping} that would cause a cycle an {@link SpreadsheetErrorException}
 * with {@link SpreadsheetError#cycle(SpreadsheetExpressionReference)} should be thrown and the
 * save aborted.
 */
public interface SpreadsheetLabelStore extends SpreadsheetStore<SpreadsheetLabelName, SpreadsheetLabelMapping> {

    /**
     * Finds all {@link SpreadsheetLabelName} for the given text.
     */
    Set<SpreadsheetLabelMapping> findLabelsByName(final String text,
                                                  final int offset,
                                                  final int count);

    /**
     * Resolves the given {@link SpreadsheetLabelName} to non label {@link SpreadsheetCellReferenceOrRange}.
     */
    Set<SpreadsheetCellReferenceOrRange> loadCellOrCellRanges(final SpreadsheetLabelName label);

    /**
     * Returns all the {@link SpreadsheetLabelMapping} for the given {@link SpreadsheetExpressionReference}.
     * This includes resolving labels to other labels and eventually a {@link SpreadsheetExpressionReference}.
     */
    Set<SpreadsheetLabelMapping> findLabelsWithReference(final SpreadsheetExpressionReference reference,
                                                         final int offset,
                                                         final int count);

    /**
     * Attempts to resolve the given {@link SpreadsheetExpressionReference} when it is a label to a {@link SpreadsheetCellReferenceOrRange}.
     */
    default Optional<SpreadsheetCellReferenceOrRange> resolveLabel(final SpreadsheetLabelName labelName) {
        return SpreadsheetLabelStoreResolveLabelSpreadsheetSelectionVisitor.resolveLabel(
            labelName,
            this
        );
    }

    /**
     * Attempts to resolve any labels to a {@link SpreadsheetCellReferenceOrRange} throwing an {@link #notFound(Object)}
     * if the label was not found.
     */
    default SpreadsheetCellReferenceOrRange resolveLabelOrFail(final SpreadsheetLabelName labelName) {
        return this.resolveLabel(labelName)
            .orElseThrow(() -> this.notFound(labelName));
    }
}
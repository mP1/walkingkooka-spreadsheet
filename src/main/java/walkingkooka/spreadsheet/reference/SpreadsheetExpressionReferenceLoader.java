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

import walkingkooka.Context;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;

import java.util.Optional;
import java.util.Set;

/**
 * Defines numerous methods to load each of the different {@link SpreadsheetExpressionReference}.
 */
public interface SpreadsheetExpressionReferenceLoader extends Context {

    /**
     * Loads the cell for the given {@link SpreadsheetCellReference}, note that the formula is not evaluated.
     */
    Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell,
                                       final SpreadsheetExpressionEvaluationContext context);

    /**
     * Attempts to load the given {@link SpreadsheetCellReference} throwing a {@link SpreadsheetError#selectionNotFound(SpreadsheetExpressionReference)}
     * if it is missing.
     */
    default SpreadsheetCell loadCellOrFail(final SpreadsheetCellReference cell,
                                           final SpreadsheetExpressionEvaluationContext context) {
        return this.loadCell(
                cell,
                context
        ).orElseThrow(
                () -> SpreadsheetError.selectionNotFound(cell)
                        .exception()
        );
    }

    /**
     * Loads all the cells present in the given {@link SpreadsheetCellRangeReference}.
     */
    Set<SpreadsheetCell> loadCellRange(final SpreadsheetCellRangeReference range,
                                       final SpreadsheetExpressionEvaluationContext context);

    /**
     * Loads the {@link SpreadsheetLabelMapping} for the given {@link SpreadsheetLabelName}.
     */
    Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName labelName);

    /**
     * Attempts to load the given {@link SpreadsheetLabelMapping} throwing a {@link SpreadsheetError#selectionNotFound(SpreadsheetExpressionReference)}
     * if it is missing.
     */
    default SpreadsheetLabelMapping loadLabelOrFail(final SpreadsheetLabelName labelName) {
        return this.loadLabel(
                labelName
        ).orElseThrow(
                () -> SpreadsheetError.selectionNotFound(labelName)
                        .exception()
        );
    }
}

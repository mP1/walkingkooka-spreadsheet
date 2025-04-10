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

package walkingkooka.spreadsheet.expression;

import walkingkooka.Cast;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetErrorException;
import walkingkooka.spreadsheet.SpreadsheetStrings;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.HasSpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.storage.expression.function.StorageExpressionEvaluationContext;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Enhances {@link ExpressionEvaluationContext} adding a few extra methods required by a spreadsheet during
 * expression execution.
 */
public interface SpreadsheetExpressionEvaluationContext extends StorageExpressionEvaluationContext,
        SpreadsheetConverterContext,
        HasSpreadsheetMetadata {

    /**
     * Helper that makes it easy to add a variable with a value. This is especially useful when executing a {@link Expression}
     * with a parameter such as a Validator.
     */
    default SpreadsheetExpressionEvaluationContext addLocalVariable(final ExpressionReference reference,
                                                                    final Optional<Object> value) {
        return this.enterScope(
                (final ExpressionReference expressionReference) -> Optional.ofNullable(
                        expressionReference.equals(reference) ?
                                value :
                                null
                )
        );
    }

    @Override
    default SpreadsheetExpressionEvaluationContext enterScope(final Function<ExpressionReference, Optional<Optional<Object>>> scoped) {
        return SpreadsheetExpressionEvaluationContexts.localLabels(
                Cast.to(scoped),
                this
        );
    }

    /**
     * Parses the {@link TextCursor formula} into an {@link SpreadsheetFormulaParserToken} which can then be transformed into an {@link Expression}.
     * Note a formula here is an expression without the leading equals sign. Value literals such as date like 1/2/2000 will actually probably
     * be parsed into a series of division operations and not an actual date. Apostrophe string literals will fail,
     * date/times and times will not actually return date/time or time values.
     */
    SpreadsheetFormulaParserToken parseFormula(final TextCursor formula);

    @Override
    default boolean isText(final Object value) {
        return SpreadsheetStrings.isText(value);
    }

    /**
     * If the {@link ExpressionReference} cannot be found an {@link SpreadsheetError} is created with {@link SpreadsheetError#referenceNotFound(ExpressionReference)}.
     */
    @Override
    default Object referenceOrFail(final ExpressionReference reference) {
        Object result;
        try {
            result = this.reference(reference)
                    .orElseGet(
                            () -> Optional.of(
                                    SpreadsheetError.referenceNotFound(reference)
                            )
                    ).orElse(null);
        } catch (final RuntimeException exception) {
            result = this.handleException(exception);
        }

        return result;
    }

    /**
     * Loads the cell for the given {@link SpreadsheetCellReference}, note that the formula is not evaluated.
     */
    Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell);

    /**
     * Attempts to load the given {@link SpreadsheetCellReference} throwing a {@link SpreadsheetError#selectionNotFound(SpreadsheetExpressionReference)}
     * if it is missing.
     */
    default SpreadsheetCell loadCellOrFail(final SpreadsheetCellReference cell) {
        return this.loadCell(cell)
                .orElseThrow(
                        () -> SpreadsheetError.selectionNotFound(cell)
                                .exception()
                );
    }

    /**
     * Loads all the cells present in the given {@link SpreadsheetCellRangeReference}.
     */
    Set<SpreadsheetCell> loadCellRange(final SpreadsheetCellRangeReference range);

    /**
     * Loads the {@link SpreadsheetLabelMapping} for the given {@link SpreadsheetLabelName}.
     */
    Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName labelName);

    /**
     * Attempts to load the given {@link SpreadsheetLabelMapping} throwing a {@link SpreadsheetError#selectionNotFound(SpreadsheetExpressionReference)}
     * if it is missing.
     */
    default SpreadsheetLabelMapping loadLabelOrFail(final SpreadsheetLabelName labelName) {
        return this.loadLabel(labelName)
                .orElseThrow(() -> new SpreadsheetErrorException(
                        SpreadsheetError.selectionNotFound(labelName))
                );
    }

    /**
     * Returns a {@link SpreadsheetExpressionEvaluationContext} with the given {@link SpreadsheetCell} as the current cell.
     */
    SpreadsheetExpressionEvaluationContext setCell(final Optional<SpreadsheetCell> cell);

    /**
     * Returns the current cell that owns the expression or formula being executed.
     */
    Optional<SpreadsheetCell> cell();

    default SpreadsheetCell cellOrFail() {
        return this.cell()
                .orElseThrow(() -> new IllegalStateException("Missing cell"));
    }

    /**
     * Returns the base server url, which can then be used to create links to cells and more.
     * This is necessary for functions such as hyperlink which creates a link to a cell.
     */
    AbsoluteUrl serverUrl();

    /**
     * Saves or replaces the current {@link SpreadsheetMetadata} with a new copy.
     * This is necessary to support a function that allows updating/replacing a {@link SpreadsheetMetadata}.
     */
    void setSpreadsheetMetadata(final SpreadsheetMetadata metadata);

    /**
     * Returns the next empty column for the requested {@link SpreadsheetRowReference}.
     */
    Optional<SpreadsheetColumnReference> nextEmptyColumn(final SpreadsheetRowReference row);

    /**
     * Returns the next empty {@link SpreadsheetRowReference} for the requested {@link SpreadsheetColumnReference}.
     */
    Optional<SpreadsheetRowReference> nextEmptyRow(final SpreadsheetColumnReference column);
}

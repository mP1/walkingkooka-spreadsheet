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
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.HasSpreadsheetMetadata;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Optional;
import java.util.function.Function;

/**
 * Enhances {@link ExpressionEvaluationContext} adding a few extra methods required by a spreadsheet during
 * expression execution.
 */
public interface SpreadsheetExpressionEvaluationContext extends ExpressionEvaluationContext,
        SpreadsheetConverterContext,
        HasSpreadsheetMetadata {

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
        return value instanceof Character || value instanceof CharSequence;
    }

    /**
     * If the {@link ExpressionReference} cannot be found returns a {@link SpreadsheetErrorKind#NAME} with a message.
     */
    @Override
    default Object referenceOrFail(final ExpressionReference reference) {
        Object result;
        try {
            result = this.reference(reference)
                    .orElseGet(
                            () -> Optional.of(
                                    SpreadsheetError.selectionNotFound(
                                            (SpreadsheetExpressionReference) reference
                                    )
                            )
                    ).orElse(null);
        } catch (final RuntimeException exception) {
            result = this.handleException(exception);
        }

        return result;
    }

    /**
     * Returns the current cell that owns the expression or formula being executed.
     */
    Optional<SpreadsheetCell> cell();

    default SpreadsheetCell cellOrFail() {
        return this.cell()
                .orElseThrow(() -> new IllegalStateException("Missing cell"));
    }

    /**
     * Loads the cell for the given {@link SpreadsheetCellReference}, note that the formula is not evaluated.
     */
    Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell);

    /**
     * Returns the base server url, which can then be used to create links to cells and more.
     * This is necessary for functions such as hyperlink which creates a link to a cell.
     */
    AbsoluteUrl serverUrl();
}

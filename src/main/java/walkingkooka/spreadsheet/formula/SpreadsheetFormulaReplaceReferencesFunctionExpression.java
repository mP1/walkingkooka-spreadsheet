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

package walkingkooka.spreadsheet.formula;

import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ReferenceExpression;

import java.util.Optional;
import java.util.function.Function;

final class SpreadsheetFormulaReplaceReferencesFunctionExpression extends SpreadsheetFormulaReplaceReferencesFunction<Expression> {

    static SpreadsheetFormulaReplaceReferencesFunctionExpression with(final Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>> mapper) {
        return new SpreadsheetFormulaReplaceReferencesFunctionExpression(mapper);
    }

    private SpreadsheetFormulaReplaceReferencesFunctionExpression(final Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>> mapper) {
        super(mapper);
    }

    @Override
    public Expression apply(final Expression expression) {
        final ReferenceExpression referenceExpression = (ReferenceExpression) expression;
        final SpreadsheetCellReferenceOrRange cellOrRange = (SpreadsheetCellReferenceOrRange) referenceExpression.value();

        Expression result = referenceExpression;

        final Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>> mapper = this.mapper;

        if (cellOrRange.isCell()) {
            result = mapper.apply(
                cellOrRange.toCell()
            ).map(
                c -> (Expression) referenceExpression.setValue(c)

            ).orElse(SELECTION_DELETED);
        } else {
            if (cellOrRange.isCellRange()) {
                final SpreadsheetCellRangeReference range = cellOrRange.toCellRange();
                final Optional<SpreadsheetCellReference> begin = mapper.apply(
                    range.begin()
                );
                if (begin.isPresent()) {
                    final Optional<SpreadsheetCellReference> end = mapper.apply(
                        range.end()
                    );
                    if (end.isPresent()) {
                        result = referenceExpression.setValue(
                            begin.get()
                                .cellRange(
                                    end.get()
                                )
                        );
                    } else {
                        result = SELECTION_DELETED;
                    }
                }
            }
        }

        return result;
    }

    private final static Expression SELECTION_DELETED = Expression.value(
        SpreadsheetError.selectionDeleted()
    );
}

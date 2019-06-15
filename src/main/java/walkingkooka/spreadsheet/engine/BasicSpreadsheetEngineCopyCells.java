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

package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCellReference;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetRange;

import java.util.Collection;
import java.util.stream.Collectors;

final class BasicSpreadsheetEngineCopyCells {

    static void execute(final Collection<SpreadsheetCell> from,
                        final SpreadsheetRange to,
                        final BasicSpreadsheetEngine engine,
                        final SpreadsheetEngineContext context) {
        new BasicSpreadsheetEngineCopyCells(engine, context)
                .execute(from, to);
    }

    private BasicSpreadsheetEngineCopyCells(final BasicSpreadsheetEngine engine,
                                            final SpreadsheetEngineContext context) {
        super();
        this.engine = engine;
        this.context = context;
    }

    private void execute(final Collection<SpreadsheetCell> from,
                         final SpreadsheetRange to) {
        final SpreadsheetRange fromRange = SpreadsheetRange.from(from.stream()
                .map(c -> c.reference())
                .collect(Collectors.toList()));

        final int fromWidth = fromRange.width();
        final int fromHeight = fromRange.height();

        final int toWidth = to.width();
        final int toHeight = to.height();

        final int widthMultiple = fromWidth >= toWidth ?
                1 :
                toWidth / fromWidth;
        final int heightMultiple = fromHeight >= toHeight ?
                1 :
                toHeight / fromHeight;

        final SpreadsheetCellReference fromBegin = fromRange.begin();
        final SpreadsheetCellReference toBegin = to.begin();

        final int xOffset = toBegin.column().value() - fromBegin.column().value();
        final int yOffset = toBegin.row().value() - fromBegin.row().value();

        for (int h = 0; h < heightMultiple; h++) {
            final int y = yOffset + h * fromHeight;

            for (int w = 0; w < widthMultiple; w++) {
                final int x = xOffset + w * fromWidth;

                for (SpreadsheetCell c : from) {
                    this.copyCell(c, x, y);
                }
            }
        }
    }

    /**
     * Fixes any relative references within the formula belonging to the cell's expression. Absolute references are
     * ignored and left unmodified.
     */
    private void copyCell(final SpreadsheetCell cell,
                          final int xOffset,
                          final int yOffset) {
        final SpreadsheetCell updatedReference = cell.setReference(cell.reference().add(xOffset, yOffset));
        final SpreadsheetFormula formula = updatedReference.formula();

        final BasicSpreadsheetEngine engine = this.engine;
        final SpreadsheetEngineContext context = this.context;

        final SpreadsheetCell save = updatedReference.setFormula(engine.parse(formula,
                token -> BasicSpreadsheetEngineCopyCellsSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor.expressionFixReferences(token,
                        xOffset,
                        yOffset),
                context));
        this.engine.maybeParseAndEvaluateAndFormat(save,
                SpreadsheetEngineEvaluation.CLEAR_VALUE_ERROR_SKIP_EVALUATE,
                context);
    }

    private final BasicSpreadsheetEngine engine;

    private final SpreadsheetEngineContext context;

    @Override
    public String toString() {
        return this.engine.toString();
    }
}

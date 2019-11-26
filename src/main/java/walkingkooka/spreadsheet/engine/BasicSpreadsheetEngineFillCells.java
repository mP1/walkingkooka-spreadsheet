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

import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

final class BasicSpreadsheetEngineFillCells {

    static void execute(final Collection<SpreadsheetCell> cells,
                        final SpreadsheetRange from,
                        final SpreadsheetRange to,
                        final BasicSpreadsheetEngine engine,
                        final SpreadsheetEngineContext context) {
        new BasicSpreadsheetEngineFillCells(engine, context)
                .execute(cells, from, to);
    }

    BasicSpreadsheetEngineFillCells(final BasicSpreadsheetEngine engine,
                                    final SpreadsheetEngineContext context) {
        super();
        this.engine = engine;
        this.context = context;
    }

    private void execute(final Collection<SpreadsheetCell> cells,
                         final SpreadsheetRange from,
                         final SpreadsheetRange to) {
        if (cells.isEmpty()) {
            this.clear(to);
        } else {
            final List<SpreadsheetCell> out = cells.stream()
                    .filter(c -> false == from.test(c.reference()))
                    .collect(Collectors.toList());
            if (!out.isEmpty()) {
                throw new IllegalArgumentException("Several cells " + out + " are outside the range " + from);
            }

            this.fill(cells, from, to);
        }
    }

    /**
     * Clears aka deletes all the cells in the given {@link SpreadsheetRange}
     */
    private void clear(final SpreadsheetRange to) {
        to.cellStream().forEach(this::deleteCell);
    }

    /**
     * Fills the given $to range repeating the from range as necessary. Cells missing from the $from will result in
     * deletion in the $to.
     */
    private void fill(final Collection<SpreadsheetCell> cells,
                      final SpreadsheetRange from,
                      final SpreadsheetRange to) {
        final List<Object> referencesAndCells = Lists.array();
        from.cells(cells,
                referencesAndCells::add,
                referencesAndCells::add);

        final int fromWidth = from.width();
        final int fromHeight = from.height();

        final int toWidth = to.width();
        final int toHeight = to.height();

        final int widthMultiple = fromWidth >= toWidth ?
                1 :
                toWidth / fromWidth;
        final int heightMultiple = fromHeight >= toHeight ?
                1 :
                toHeight / fromHeight;

        final SpreadsheetCellReference fromBegin = from.begin();
        final SpreadsheetCellReference toBegin = to.begin();

        final int xOffset = toBegin.column().value() - fromBegin.column().value();
        final int yOffset = toBegin.row().value() - fromBegin.row().value();

        for (int h = 0; h < heightMultiple; h++) {
            final int y = yOffset + h * fromHeight;

            for (int w = 0; w < widthMultiple; w++) {
                for (Object referenceOrCell : referencesAndCells) {
                    if (referenceOrCell instanceof SpreadsheetCellReference) {
                        this.deleteCell((SpreadsheetCellReference) referenceOrCell);
                    } else {
                        final int x = xOffset + w * fromWidth;
                        this.saveCell((SpreadsheetCell) referenceOrCell, x, y);
                    }
                }
            }
        }
    }

    private void deleteCell(final SpreadsheetCellReference reference) {
        this.engine.deleteCell(reference, this.context);
    }

    /**
     * Fixes any relative references within the formula belonging to the cell's expression. Absolute references are
     * ignored and left unmodified.
     */
    private void saveCell(final SpreadsheetCell cell,
                          final int xOffset,
                          final int yOffset) {
        final SpreadsheetCell updatedReference = cell.setReference(cell.reference().add(xOffset, yOffset));
        final SpreadsheetFormula formula = updatedReference.formula();

        final BasicSpreadsheetEngine engine = this.engine;
        final SpreadsheetEngineContext context = this.context;

        // possibly fix references, and then parse the formula and evaluate etc.
        final SpreadsheetCell save = updatedReference.setFormula(engine.parse(formula,
                token -> BasicSpreadsheetEngineFillCellsSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor.expressionFixReferences(token,
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

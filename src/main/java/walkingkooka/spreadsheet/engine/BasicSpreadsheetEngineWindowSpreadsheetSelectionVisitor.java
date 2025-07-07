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

import walkingkooka.spreadsheet.SpreadsheetViewportRectangle;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionVisitor;

import java.util.Optional;

/**
 * A visitor which accepts an original {@link SpreadsheetCellRangeReference} and then attempts to do a minimal pan to include
 * the given {@link SpreadsheetSelection}.
 */
final class BasicSpreadsheetEngineWindowSpreadsheetSelectionVisitor extends SpreadsheetSelectionVisitor {

    static SpreadsheetCellRangeReference pan(final SpreadsheetCellRangeReference range,
                                             final SpreadsheetViewportRectangle viewportRectangle,
                                             final SpreadsheetSelection selection,
                                             final BasicSpreadsheetEngine engine,
                                             final SpreadsheetEngineContext context) {
        final BasicSpreadsheetEngineWindowSpreadsheetSelectionVisitor visitor = new BasicSpreadsheetEngineWindowSpreadsheetSelectionVisitor(
            range,
            viewportRectangle,
            engine,
            context
        );
        visitor.accept(selection);
        return visitor.range;
    }

    BasicSpreadsheetEngineWindowSpreadsheetSelectionVisitor(final SpreadsheetCellRangeReference range,
                                                            final SpreadsheetViewportRectangle viewportRectangle,
                                                            final BasicSpreadsheetEngine engine,
                                                            final SpreadsheetEngineContext context) {
        super();

        this.range = range;
        this.viewportRectangle = viewportRectangle;
        this.engine = engine;
        this.context = context;
    }

    // SpreadsheetSelectionVisitor......................................................................................

    @Override
    protected void visit(final SpreadsheetCellRangeReference cellRange) {
        this.columnRange(cellRange.columnRange());
        this.rowRange(cellRange.rowRange());
    }

    @Override
    protected void visit(final SpreadsheetCellReference cell) {
        this.accept(cell.column());
        this.accept(cell.row());
    }

    @Override
    protected void visit(final SpreadsheetColumnReference column) {
        this.columnRange(column.toColumnRange());
    }

    @Override
    protected void visit(final SpreadsheetColumnRangeReference columnRange) {
        this.columnRange(columnRange);
    }

    private void columnRange(final SpreadsheetColumnRangeReference columnRange) {
        final SpreadsheetCellRangeReference cellRange = this.range;
        final SpreadsheetColumnReference beginColumn = cellRange.begin().column();

        final SpreadsheetColumnReference left = columnRange.begin();
        final SpreadsheetColumnReference right = columnRange.end();

        if (left.compareTo(beginColumn) < 0) {

            // set new left...
            this.range = cellRange.setColumnRange(
                this.engine.columnRange(
                    left,
                    0,
                    this.viewportRectangle.width(),
                    Optional.of(columnRange),
                    this.context
                )
            );
        } else {
            final SpreadsheetColumnReference viewportRight = cellRange.end().column();
            if (right.compareTo(viewportRight) > 0) {
                // set new right

                final BasicSpreadsheetEngine engine = this.engine;
                final SpreadsheetEngineContext context = this.context;

                final double rightOffset = engine.sumColumnWidths(
                    viewportRight.addSaturated(1),
                    right,
                    context
                );

                this.range = cellRange.setColumnRange(
                    engine.columnRange(
                        beginColumn,
                        rightOffset,
                        this.viewportRectangle.width(),
                        Optional.of(columnRange),
                        context
                    )
                );
            }
        }
    }

    @Override
    protected void visit(final SpreadsheetLabelName label) {
        this.accept(
            this.context.resolveLabelOrFail(label)
        );
    }

    @Override
    protected void visit(final SpreadsheetRowReference row) {
        this.rowRange(row.toRowRange());
    }

    @Override
    protected void visit(final SpreadsheetRowRangeReference rowRange) {
        this.rowRange(rowRange);
    }

    private void rowRange(SpreadsheetRowRangeReference rowRange) {
        final SpreadsheetCellRangeReference celRange = this.range;
        final SpreadsheetRowReference beginRow = celRange.begin().row();

        final SpreadsheetRowReference top = rowRange.begin();
        final SpreadsheetRowReference bottom = rowRange.end();

        if (top.compareTo(beginRow) < 0) {

            // set new top...
            this.range = celRange.setRowRange(
                this.engine.rowRange(
                    top,
                    0,
                    this.viewportRectangle.height(),
                    Optional.of(rowRange),
                    this.context
                )
            );
        } else {
            final SpreadsheetRowReference viewportBottom = celRange.end().row();
            if (bottom.compareTo(viewportBottom) > 0) {
                // set new bottom

                final BasicSpreadsheetEngine engine = this.engine;
                final SpreadsheetEngineContext context = this.context;

                final double bottomOffset = engine.sumRowHeights(
                    viewportBottom.addSaturated(1),
                    bottom,
                    context
                );

                this.range = celRange.setRowRange(
                    engine.rowRange(
                        beginRow,
                        bottomOffset,
                        this.viewportRectangle.height(),
                        Optional.of(rowRange),
                        context
                    )
                );
            }
        }
    }

    /**
     * Starts with the initial {@link SpreadsheetCellRangeReference} and then possibly due to the selection is adjusted
     * to enable minimum movement of the viewportRectangle but with the selection included.
     */
    private SpreadsheetCellRangeReference range;
    private final SpreadsheetViewportRectangle viewportRectangle;
    private final BasicSpreadsheetEngine engine;
    private final SpreadsheetEngineContext context;

    @Override
    public String toString() {
        return this.viewportRectangle.toString();
    }
}

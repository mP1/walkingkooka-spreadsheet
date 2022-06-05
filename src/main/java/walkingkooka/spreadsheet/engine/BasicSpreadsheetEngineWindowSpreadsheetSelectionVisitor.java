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

import walkingkooka.spreadsheet.SpreadsheetViewport;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReferenceRange;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReferenceRange;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionVisitor;

import java.util.Optional;

/**
 * A visitor which accepts an original {@link SpreadsheetCellRange} and then attempts to do a minimal pan to include
 * the given {@link SpreadsheetSelection}.
 */
final class BasicSpreadsheetEngineWindowSpreadsheetSelectionVisitor extends SpreadsheetSelectionVisitor {

    static SpreadsheetCellRange pan(final SpreadsheetCellRange range,
                                    final SpreadsheetViewport viewport,
                                    final SpreadsheetSelection selection,
                                    final BasicSpreadsheetEngine engine,
                                    final SpreadsheetEngineContext context) {
        final BasicSpreadsheetEngineWindowSpreadsheetSelectionVisitor visitor = new BasicSpreadsheetEngineWindowSpreadsheetSelectionVisitor(
                range,
                viewport,
                engine,
                context
        );
        visitor.accept(selection);
        return visitor.range;
    }

    BasicSpreadsheetEngineWindowSpreadsheetSelectionVisitor(final SpreadsheetCellRange range,
                                                            final SpreadsheetViewport viewport,
                                                            final BasicSpreadsheetEngine engine,
                                                            final SpreadsheetEngineContext context) {
        super();

        this.range = range;
        this.viewport = viewport;
        this.engine = engine;
        this.context = context;
    }

    // SpreadsheetSelectionVisitor......................................................................................

    @Override
    protected void visit(final SpreadsheetCellRange range) {
        this.columnRange(range.columnReferenceRange());
        this.rowRange(range.rowReferenceRange());
    }

    @Override
    protected void visit(final SpreadsheetCellReference reference) {
        this.accept(reference.column());
        this.accept(reference.row());
    }

    @Override
    protected void visit(final SpreadsheetColumnReference reference) {
        this.columnRange(reference.columnRange());
    }

    @Override
    protected void visit(final SpreadsheetColumnReferenceRange range) {
        this.columnRange(range);
    }

    private void columnRange(final SpreadsheetColumnReferenceRange columnRange) {
        final SpreadsheetCellRange cellRange = this.range;
        final SpreadsheetColumnReference beginColumn = cellRange.begin().column();

        final SpreadsheetColumnReference left = columnRange.begin();
        final SpreadsheetColumnReference right = columnRange.end();

        if (left.compareTo(beginColumn) < 0) {

            // set new left...
            this.range = cellRange.setColumnReferenceRange(
                    this.engine.columnRange(
                            left,
                            0,
                            this.viewport.width(),
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

                this.range = cellRange.setColumnReferenceRange(
                        engine.columnRange(
                                beginColumn,
                                rightOffset,
                                this.viewport.width(),
                                Optional.of(columnRange),
                                context
                        )
                );
            }
        }
    }

    @Override
    protected void visit(final SpreadsheetLabelName label) {
        this.accept(this.context.resolveCellReference(label));
    }

    @Override
    protected void visit(final SpreadsheetRowReference reference) {
        this.rowRange(reference.rowRange());
    }

    @Override
    protected void visit(final SpreadsheetRowReferenceRange range) {
        this.rowRange(range);
    }

    private void rowRange(SpreadsheetRowReferenceRange rowRange) {
        final SpreadsheetCellRange celRange = this.range;
        final SpreadsheetRowReference beginRow = celRange.begin().row();

        final SpreadsheetRowReference top = rowRange.begin();
        final SpreadsheetRowReference bottom = rowRange.end();

        if (top.compareTo(beginRow) < 0) {

            // set new top...
            this.range = celRange.setRowReferenceRange(
                    this.engine.rowRange(
                            top,
                            0,
                            this.viewport.height(),
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

                this.range = celRange.setRowReferenceRange(
                        engine.rowRange(
                                beginRow,
                                bottomOffset,
                                this.viewport.height(),
                                Optional.of(rowRange),
                                context
                        )
                );
            }
        }
    }

    /**
     * Starts with the initial {@link SpreadsheetCellRange} and then possibly due to the selection is adjusted
     * to enable minimum movement of the viewport but with the selection included.
     */
    private SpreadsheetCellRange range;
    private final SpreadsheetViewport viewport;
    private final BasicSpreadsheetEngine engine;
    private final SpreadsheetEngineContext context;

    @Override
    public String toString() {
        return this.viewport.toString();
    }
}

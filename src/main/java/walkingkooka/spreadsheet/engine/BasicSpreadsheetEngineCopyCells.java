package walkingkooka.spreadsheet.engine;

import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Collection;
import java.util.List;
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

        // load & force recompute after all cells copied.
        final List<SpreadsheetCellReference> copied = Lists.array();

        for (int h = 0; h < heightMultiple; h++) {
            final int y = yOffset + h * fromHeight;

            for (int w = 0; w < widthMultiple; w++) {
                final int x = xOffset + w * fromWidth;

                for (SpreadsheetCell c : from) {
                    copied.add(this.copyCell(c, x, y));
                }
            }
        }

        // because relative references may exist within the copy block, recompute once all cells are copied.
        this.recomputeCopiedCells(copied);
    }

    /**
     * Fixes any relative references within the formula belonging to the cell's expression. Absolute references are
     * ignored and left unmodified.
     */
    private SpreadsheetCellReference copyCell(final SpreadsheetCell cell,
                                              final int xOffset,
                                              final int yOffset) {
        final SpreadsheetCell updatedReference = cell.setReference(cell.reference().add(xOffset, yOffset));
        final SpreadsheetFormula formula = updatedReference.formula();

        final BasicSpreadsheetEngine engine = this.engine;
        final SpreadsheetEngineContext context = this.context;

        final SpreadsheetCell save = updatedReference.s etFormula(engine.parse(formula,
                token -> BasicSpreadsheetEngineCopyCellsSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor.expressionFixReferences(token,
                        xOffset,
                        yOffset),
                context));
        BasicSpreadsheetEngineSaveCell.execute(save,
                SpreadsheetEngineLoading.FORCE_RECOMPUTE,
                this.engine,
                context);
        return save.reference();
    }

    private void recomputeCopiedCells(final List<SpreadsheetCellReference> references) {
        references.forEach(r -> this.engine.loadCell(r, SpreadsheetEngineLoading.FORCE_RECOMPUTE, this.context));
    }

    private final BasicSpreadsheetEngine engine;

    private final SpreadsheetEngineContext context;

    @Override
    public String toString() {
        return this.engine.toString();
    }
}

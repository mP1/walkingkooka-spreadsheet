package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Objects;
import java.util.Optional;

/**
 * An action that coordinates the process of saving a cell. First all old references are removed then new ones saved.
 * After that re-evaluate all cells that reference the saved cell.
 */
final class BasicSpreadsheetEngineSaveCell {

    static void execute(final SpreadsheetCell cell,
                        final SpreadsheetEngineLoading loading,
                        final BasicSpreadsheetEngine engine,
                        final SpreadsheetEngineContext context) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(context, "context");

        new BasicSpreadsheetEngineSaveCell(cell,
                loading,
                engine,
                context).save();
    }

    // VisibleForTesting
    BasicSpreadsheetEngineSaveCell(final SpreadsheetCell unsaved,
                                   final SpreadsheetEngineLoading loading,
                                   final BasicSpreadsheetEngine engine,
                                   final SpreadsheetEngineContext context) {
        super();
        this.unsaved = unsaved;
        this.loading = loading;
        this.engine = engine;
        this.context = context;
    }

    /**
     * The cell about to be saved.
     */
    final SpreadsheetCell unsaved;

    /**
     * Allows control over loading evaluation.
     */
    final SpreadsheetEngineLoading loading;

    final BasicSpreadsheetEngine engine;

    /**
     * Used during cell re-evaluation.
     */
    private final SpreadsheetEngineContext context;

    /**
     * Executes the save.
     */
    void save() {
        final SpreadsheetCell unsaved = this.unsaved;
        final SpreadsheetCellReference reference = unsaved.reference();
        final Optional<SpreadsheetCell> before = this.engine.cellStore.load(reference);
        if (before.isPresent()) {
            this.removePreviousExpressionReferences(before.get().formula());
        }
        this.evaluateAndSaveCell(unsaved);
    }

    /**
     * Removes any existing references by this cell and replaces them with new references if any are present.
     */
    private void removePreviousExpressionReferences(final SpreadsheetFormula previous) {
        previous.expression()
                .ifPresent(e -> BasicSpreadsheetEngineSaveCellReferencesExpressionNodeVisitor.processReferences(e,
                        BasicSpreadsheetEngineSaveCellRemoveReferencesSpreadsheetExpressionReferenceVisitor.with(this)));
    }

    /**
     * Evaluates and saves the ell and then adds references to the cell.
     */
    private SpreadsheetCell evaluateAndSaveCell(final SpreadsheetCell cell) {
        final SpreadsheetCell evaluatedAndSaved = this.engine.maybeParseAndEvaluateAndFormat(cell,
                this.loading,
                this.context);
        this.addNewExpressionReferences(evaluatedAndSaved.formula());
        return evaluatedAndSaved;
    }

    /**
     * Removes any existing references by this cell and replaces them with new references if any are present.
     */
    private void addNewExpressionReferences(final SpreadsheetFormula current) {
        current.expression()
                .ifPresent(e -> BasicSpreadsheetEngineSaveCellReferencesExpressionNodeVisitor.processReferences(e,
                        BasicSpreadsheetEngineSaveCellAddReferencesSpreadsheetExpressionReferenceVisitor.with(this)));
    }

    @Override
    public String toString() {
        return this.unsaved.toString();
    }
}

package walkingkooka.spreadsheet.engine;

import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * An action that coordinates the process of saving a cell. First all old references are removed then new ones saved.
 * After that re-evaluate all cells that reference the saved cell.
 */
final class BasicSpreadsheetEngineSaveCell {

    static Set<SpreadsheetCell> execute(final SpreadsheetCell cell,
                                        final SpreadsheetEngineLoading loading,
                                        final BasicSpreadsheetEngine engine,
                                        final SpreadsheetEngineContext context) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(context, "context");

        return Sets.of(new BasicSpreadsheetEngineSaveCell(cell,
                loading,
                engine,
                context)
                .saved);
    }

    private BasicSpreadsheetEngineSaveCell(final SpreadsheetCell unsaved,
                                           final SpreadsheetEngineLoading loading,
                                           final BasicSpreadsheetEngine engine,
                                           final SpreadsheetEngineContext context) {
        super();
        this.reference = unsaved.reference();
        this.loading = loading;

        this.engine = engine;
        this.context = context;

        final SpreadsheetCellReference reference = unsaved.reference();
        final Optional<SpreadsheetCell> before = this.engine.cellStore.load(reference);
        if (before.isPresent()) {
            this.removePreviousExpressionReferences(before.get().formula());
        }
        this.saved = this.evaluateAndSaveCell(unsaved);
    }

    /**
     * The reference of the cell being saved.
     */
    final SpreadsheetCellReference reference;

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
     * The evaluated and saved cell.
     */
    private final SpreadsheetCell saved;

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
        return this.reference.toString();
    }
}

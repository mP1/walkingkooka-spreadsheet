package walkingkooka.spreadsheet.engine;

import walkingkooka.convert.Converter;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStore;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.tree.expression.ExpressionEvaluationException;
import walkingkooka.tree.expression.ExpressionNode;
import walkingkooka.tree.expression.ExpressionReference;

import java.math.MathContext;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A {@link Function} which may be passed to {@link walkingkooka.tree.expression.ExpressionEvaluationContexts#basic(BiFunction, Function, MathContext, Converter, walkingkooka.DecimalNumberContext)}
 * and acts as a bridge resolving references to a {@link SpreadsheetEngine}.
 */
final class SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction implements Function<ExpressionReference, Optional<ExpressionNode>> {

    /**
     * Factory that creates a new {@link SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction}
     */
    static SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction with(final SpreadsheetEngine engine,
                                                                                                      final SpreadsheetLabelStore labelStore,
                                                                                                      final SpreadsheetEngineContext context) {
        Objects.requireNonNull(engine, "engine");
        Objects.requireNonNull(labelStore, "labelStore");
        Objects.requireNonNull(context, "context");

        return new SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction(engine, labelStore, context);
    }

    /**
     * Private ctor.
     */
    private SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction(final SpreadsheetEngine engine,
                                                                                                  final SpreadsheetLabelStore labelStore,
                                                                                                  final SpreadsheetEngineContext context) {
        this.engine = engine;
        this.labelStore = labelStore;
        this.context = context;
    }

    @Override
    public Optional<ExpressionNode> apply(final ExpressionReference reference) {
        Objects.requireNonNull(reference, "reference");

        SpreadsheetCellReference cellReference = null;

        if(reference instanceof SpreadsheetLabelName) {
            final Optional<SpreadsheetLabelMapping> mapping = this.labelStore.load(SpreadsheetLabelName.class.cast(reference));
            if(!mapping.isPresent()) {
                throw new ExpressionEvaluationException("Unknown label reference=" + reference);
            }
            cellReference = mapping.get().cell();
        }
        if(reference instanceof SpreadsheetCellReference) {
            cellReference = SpreadsheetCellReference.class.cast(reference);
        }

        final Optional<SpreadsheetCell> maybeCell = this.engine.loadCell(cellReference, SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY, this.context);
        if(!maybeCell.isPresent()) {
            throw new ExpressionEvaluationException("Unknown cell reference " + reference);
        }
        final SpreadsheetFormula formula  = maybeCell.get().formula();
        final Optional<SpreadsheetError> error = formula.error();
        if(error.isPresent()) {
            throw new ExpressionEvaluationException(error.get().value());
        }

        return formula.expression();
    }

    private final SpreadsheetEngine engine;
    private final SpreadsheetLabelStore labelStore;
    private final SpreadsheetEngineContext context;

    @Override
    public String toString() {
        return this.engine.toString();
    }
}

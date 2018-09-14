package walkingkooka.spreadsheet.engine;

import walkingkooka.convert.Converter;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetError;
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
 * A {@link Function} which may be passed to {@link walkingkooka.tree.expression.ExpressionEvaluationContexts#basic(BiFunction, Function, MathContext, Converter)}
 * and acts as a bridge resolving references to a {@link SpreadsheetEngine}.
 */
final class SpreadsheetEngineExpressionEvaluationContextFactoryFunctionExpressionReferenceExpressionNodeFunction implements Function<ExpressionReference, ExpressionNode> {

    /**
     * Factory that creates a new {@link SpreadsheetEngineExpressionEvaluationContextFactoryFunctionExpressionReferenceExpressionNodeFunction}
     */
    static SpreadsheetEngineExpressionEvaluationContextFactoryFunctionExpressionReferenceExpressionNodeFunction with(final SpreadsheetEngine engine) {
        Objects.requireNonNull(engine, "engine");

        return new SpreadsheetEngineExpressionEvaluationContextFactoryFunctionExpressionReferenceExpressionNodeFunction(engine);
    }

    /**
     * Private ctor.
     */
    private SpreadsheetEngineExpressionEvaluationContextFactoryFunctionExpressionReferenceExpressionNodeFunction(final SpreadsheetEngine engine) {
        this.engine = engine;
    }

    @Override
    public ExpressionNode apply(final ExpressionReference reference) {
        Objects.requireNonNull(reference, "reference");

        SpreadsheetCellReference cellReference = null;

        if(reference instanceof SpreadsheetLabelName) {
            final Optional<SpreadsheetCellReference> maybeCellReference = engine.label(SpreadsheetLabelName.class.cast(reference));
            if(!maybeCellReference.isPresent()) {
                throw new ExpressionEvaluationException("Unknown label reference=" + reference);
            }
            cellReference = maybeCellReference.get();
        }
        if(reference instanceof SpreadsheetCellReference) {
            cellReference = SpreadsheetCellReference.class.cast(reference);
        }

        final Optional<SpreadsheetCell> maybeCell = engine.loadCell(cellReference, SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY);
        if(!maybeCell.isPresent()) {
            throw new ExpressionEvaluationException("Unknown cell reference " + reference);
        }
        final SpreadsheetCell cell = maybeCell.get();
        final Optional<SpreadsheetError> error = cell.error();
        if(error.isPresent()) {
            throw new ExpressionEvaluationException(error.get().value());
        }

        final Optional<ExpressionNode> expression = cell.expression();
        if(!expression.isPresent()) {
            throw new ExpressionEvaluationException("Unknown cell reference " + reference);
        }
        return expression.get();
    }

    private final SpreadsheetEngine engine;

    @Override
    public String toString() {
        return this.engine.toString();
    }
}

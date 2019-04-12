package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeException;

import java.util.Arrays;
import java.util.Objects;

public enum SpreadsheetEngineEvaluation implements HasJsonNode {

    /**
     * Performs no new evaluation of the formula, leaves the original value or error alone and does not change the style.
     */
    SKIP_EVALUATE {
        @Override
        SpreadsheetCell formulaEvaluateAndStyle(final SpreadsheetCell cell,
                                                final BasicSpreadsheetEngine engine,
                                                final SpreadsheetEngineContext context) {
            return cell;
        }
    },
    /**
     * Clears the value in the formula, evaluates the formula and value and applies styling.
     */
    FORCE_RECOMPUTE {
        @Override
        SpreadsheetCell formulaEvaluateAndStyle(final SpreadsheetCell cell,
                                                final BasicSpreadsheetEngine engine,
                                                final SpreadsheetEngineContext context) {
            // clear value and error to allow evaluation to continue.
            return engine.formulaEvaluateAndStyle(cell.setFormula(cell.formula().clear()),
                    context);
        }
    },

    /**
     * Evaluates the formula and value and applies styling.
     */
    COMPUTE_IF_NECESSARY {
        @Override
        SpreadsheetCell formulaEvaluateAndStyle(final SpreadsheetCell cell,
                                                final BasicSpreadsheetEngine engine,
                                                final SpreadsheetEngineContext context) {
            return engine.formulaEvaluateAndStyle(cell, context);
        }
    };

    abstract SpreadsheetCell formulaEvaluateAndStyle(final SpreadsheetCell cell,
                                                     final BasicSpreadsheetEngine engine,
                                                     final SpreadsheetEngineContext context);

    // HasJsonNode...........................................................................................

    /**
     * Factory that creates a {@link SpreadsheetEngineEvaluation} from a {@link JsonNode}
     */
    public static SpreadsheetEngineEvaluation fromJsonNode(final JsonNode node) {
        Objects.requireNonNull(node, "node");

        try {
            return valueOf(node.stringValueOrFail());
        } catch (final JsonNodeException cause) {
            throw new IllegalArgumentException(cause.getMessage(), cause);
        }
    }

    @Override
    public JsonNode toJsonNode() {
        return JsonNode.string(this.name());
    }

    static {
        HasJsonNode.register("spreadsheet-engine-evaluation",
                SpreadsheetEngineEvaluation::fromJsonNode,
                Arrays.stream(values()).map(e -> e.getClass()).toArray(Class[]::new));
    }
}

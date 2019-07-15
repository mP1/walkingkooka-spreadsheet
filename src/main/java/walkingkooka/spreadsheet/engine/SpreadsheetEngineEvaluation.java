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

import walkingkooka.net.header.LinkRelation;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.tree.json.FromJsonNodeException;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;

import java.util.Arrays;
import java.util.Objects;

public enum SpreadsheetEngineEvaluation implements HasJsonNode {

    /**
     * Clears any value and error and formatting.
     */
    CLEAR_VALUE_ERROR_SKIP_EVALUATE {
        @Override
        SpreadsheetCell formulaEvaluateAndStyle(final SpreadsheetCell cell,
                                                final BasicSpreadsheetEngine engine,
                                                final SpreadsheetEngineContext context) {
            return engine.cellStore.save(cell.setFormula(cell.formula().clear()));
        }
    },

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

    SpreadsheetEngineEvaluation() {
        this.linkRelation = LinkRelation.with(this.name().toLowerCase().replace('_', '-'));
    }

    abstract SpreadsheetCell formulaEvaluateAndStyle(final SpreadsheetCell cell,
                                                     final BasicSpreadsheetEngine engine,
                                                     final SpreadsheetEngineContext context);

    // LinkRelation.....................................................................................................

    public LinkRelation toLinkRelation() {
        return this.linkRelation;
    }

    private final LinkRelation linkRelation;


    // HasJsonNode......................................................................................................

    /**
     * Factory that creates a {@link SpreadsheetEngineEvaluation} from a {@link JsonNode}
     */
    public static SpreadsheetEngineEvaluation fromJsonNode(final JsonNode node) {
        Objects.requireNonNull(node, "node");

        try {
            return valueOf(node.stringValueOrFail());
        } catch (final RuntimeException cause) {
            throw new FromJsonNodeException(cause.getMessage(), node, cause);
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

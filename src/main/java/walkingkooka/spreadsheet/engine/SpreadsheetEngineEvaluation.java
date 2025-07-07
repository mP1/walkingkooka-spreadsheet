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

import walkingkooka.Cast;
import walkingkooka.net.header.LinkRelation;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.text.CaseKind;
import walkingkooka.tree.expression.ExpressionPurityContext;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Arrays;
import java.util.Optional;

public enum SpreadsheetEngineEvaluation {

    /**
     * Clears any value and error and formatting.
     */
    CLEAR_VALUE_ERROR_SKIP_EVALUATE {
        @Override
        SpreadsheetCell parseFormulaEvaluateAndStyle(final SpreadsheetCell cell,
                                                     final BasicSpreadsheetEngine engine,
                                                     final SpreadsheetExpressionReferenceLoader loader,
                                                     final SpreadsheetEngineContext context) {
            return context.storeRepository()
                .cells()
                .save(cell.setFormula(cell.formula().clear()));
        }

        @Override
        Optional<Object> evaluate(final BasicSpreadsheetEngine engine,
                                  final SpreadsheetCell cell,
                                  final SpreadsheetExpressionReferenceLoader loader,
                                  final SpreadsheetEngineContext context) {
            throw new UnsupportedOperationException();
        }
    },

    /**
     * Performs no new parsing or evaluation of the formula, leaves the original value or error alone and does not change the style.
     */
    SKIP_EVALUATE {
        @Override
        SpreadsheetCell parseFormulaEvaluateAndStyle(final SpreadsheetCell cell,
                                                     final BasicSpreadsheetEngine engine,
                                                     final SpreadsheetExpressionReferenceLoader loader,
                                                     final SpreadsheetEngineContext context) {
            return cell;
        }

        @Override
        Optional<Object> evaluate(final BasicSpreadsheetEngine engine,
                                  final SpreadsheetCell cell,
                                  final SpreadsheetExpressionReferenceLoader loader,
                                  final SpreadsheetEngineContext context) {
            throw new UnsupportedOperationException();
        }
    },

    /**
     * Clears the value in the formula, parses if necessary and evaluates the formula and value and applies styling.
     * The {@link walkingkooka.tree.expression.Expression#isPure(ExpressionPurityContext)} is ignored.
     */
    FORCE_RECOMPUTE {
        @Override
        SpreadsheetCell parseFormulaEvaluateAndStyle(final SpreadsheetCell cell,
                                                     final BasicSpreadsheetEngine engine,
                                                     final SpreadsheetExpressionReferenceLoader loader,
                                                     final SpreadsheetEngineContext context) {
            // clear value and error to allow evaluation to continue.
            return engine.parseFormulaEvaluateValidateFormatAndStyle(
                cell.setFormula(
                    cell.formula()
                        .clear()
                ),
                this,
                loader,
                context
            );
        }

        @Override
        Optional<Object> evaluate(final BasicSpreadsheetEngine engine,
                                  final SpreadsheetCell cell,
                                  final SpreadsheetExpressionReferenceLoader loader,
                                  final SpreadsheetEngineContext context) {
            return engine.evaluate(
                cell,
                loader,
                context
            );
        }
    },

    /**
     * Parses if necessary and evaluates the formula if the expression is NOT pure and a value/error is already present.
     */
    COMPUTE_IF_NECESSARY {
        @Override
        SpreadsheetCell parseFormulaEvaluateAndStyle(final SpreadsheetCell cell,
                                                     final BasicSpreadsheetEngine engine,
                                                     final SpreadsheetExpressionReferenceLoader loader,
                                                     final SpreadsheetEngineContext context) {
            return engine.parseFormulaEvaluateValidateFormatAndStyle(
                cell,
                this,
                loader,
                context
            );
        }

        @Override
        Optional<Object> evaluate(final BasicSpreadsheetEngine engine,
                                  final SpreadsheetCell cell,
                                  final SpreadsheetExpressionReferenceLoader loader,
                                  final SpreadsheetEngineContext context) {
            return engine.evaluateIfNecessary(
                cell,
                loader,
                context
            );
        }
    };

    SpreadsheetEngineEvaluation() {
        this.linkRelation = LinkRelation.with(
            CaseKind.kebabEnumName(this)
        );
    }

    abstract SpreadsheetCell parseFormulaEvaluateAndStyle(final SpreadsheetCell cell,
                                                          final BasicSpreadsheetEngine engine,
                                                          final SpreadsheetExpressionReferenceLoader loader,
                                                          final SpreadsheetEngineContext context);

    /**
     * This method is only really executed by {@link #COMPUTE_IF_NECESSARY} and {@link #FORCE_RECOMPUTE}.
     */
    abstract Optional<Object> evaluate(final BasicSpreadsheetEngine engine,
                                       final SpreadsheetCell cell,
                                       final SpreadsheetExpressionReferenceLoader loader,
                                       final SpreadsheetEngineContext context);

    // LinkRelation.....................................................................................................

    public LinkRelation<?> toLinkRelation() {
        return this.linkRelation;
    }

    private final LinkRelation<?> linkRelation;

    // JsonNodeContext..................................................................................................

    static SpreadsheetEngineEvaluation unmarshall(final JsonNode node,
                                                  final JsonNodeUnmarshallContext context) {
        return valueOf(node.stringOrFail());
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(this.name());
    }

    static {
        final Class<SpreadsheetEngineEvaluation>[] types = Cast.to(Arrays.stream(values())
            .map(SpreadsheetEngineEvaluation::getClass)
            .toArray(Class[]::new));

        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetEngineEvaluation.class),
            SpreadsheetEngineEvaluation::unmarshall,
            SpreadsheetEngineEvaluation::marshall,
            SpreadsheetEngineEvaluation.class,
            types
        );
    }
}

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

import walkingkooka.spreadsheet.SpreadsheetCellReference;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetLabelName;
import walkingkooka.spreadsheet.conditionalformat.SpreadsheetConditionalFormattingRule;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.range.SpreadsheetRangeStore;
import walkingkooka.spreadsheet.store.reference.SpreadsheetReferenceStore;
import walkingkooka.tree.expression.ExpressionNode;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.type.PublicStaticHelper;

import java.util.Optional;
import java.util.function.Function;

public final class SpreadsheetEngines implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetEngine}
     */
    public static SpreadsheetEngine basic(final SpreadsheetId id,
                                          final SpreadsheetCellStore cellStore,
                                          final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferencesStore,
                                          final SpreadsheetLabelStore labelStore,
                                          final SpreadsheetReferenceStore<SpreadsheetLabelName> labelReferencesStore,
                                          final SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCellStore,
                                          final SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRuleStore) {
        return BasicSpreadsheetEngine.with(id,
                cellStore,
                cellReferencesStore,
                labelStore,
                labelReferencesStore,
                rangeToCellStore,
                rangeToConditionalFormattingRuleStore);
    }

    /**
     * {@see FakeSpreadsheetEngine}
     */
    public static SpreadsheetEngine fake() {
        return new FakeSpreadsheetEngine();
    }

    /**
     * {@see SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction}
     */
    public static Function<ExpressionReference, Optional<ExpressionNode>> expressionEvaluationContextExpressionReferenceExpressionNodeFunction(final SpreadsheetEngine engine,
                                                                                                                                               final SpreadsheetLabelStore labelStore,
                                                                                                                                               final SpreadsheetEngineContext context) {
        return SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction.with(engine, labelStore, context);
    }

    /**
     * Stop creation
     */
    private SpreadsheetEngines() {
        throw new UnsupportedOperationException();
    }
}

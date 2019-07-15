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

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStores;
import walkingkooka.tree.expression.ExpressionNode;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.util.FunctionTesting;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunctionTest
        implements FunctionTesting<SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction, ExpressionReference, Optional<ExpressionNode>> {

    @Test
    public void testWithNullEngineFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction.with(null,
                    this.labelStore(),
                    this.spreadsheetEngineContext());
        });
    }

    @Test
    public void testWithNullLabelStoreFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction.with(this.engine(),
                    null,
                    this.spreadsheetEngineContext());
        });
    }

    @Test
    public void testWithNullLabelSpreadsheetEngineContextFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction.with(this.engine(),
                    this.labelStore(),
                    null);
        });
    }

    @Override
    public SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction createFunction() {
        return SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction.with(this.engine(),
                this.labelStore(),
                this.spreadsheetEngineContext());
    }

    private SpreadsheetEngine engine() {
        return SpreadsheetEngines.fake();
    }

    private SpreadsheetLabelStore labelStore() {
        return SpreadsheetLabelStores.fake();
    }

    private SpreadsheetEngineContext spreadsheetEngineContext() {
        return SpreadsheetEngineContexts.fake();
    }

    @Override
    public Class<SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction> type() {
        return SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction.class;
    }
}

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
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.util.FunctionTesting;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetEnginesExpressionReferenceToValueFunctionTest
        implements FunctionTesting<SpreadsheetEnginesExpressionReferenceToValueFunction, ExpressionReference, Optional<Optional<Object>>> {

    @Test
    public void testWithNullEngineFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetEnginesExpressionReferenceToValueFunction.with(null,
                        this.spreadsheetEngineContext())
        );
    }

    @Test
    public void testWithNullLabelSpreadsheetEngineContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetEnginesExpressionReferenceToValueFunction.with(
                        this.engine(),
                        null
                )
        );
    }

    @Override
    public SpreadsheetEnginesExpressionReferenceToValueFunction createFunction() {
        return SpreadsheetEnginesExpressionReferenceToValueFunction.with(
                this.engine(),
                this.spreadsheetEngineContext()
        );
    }

    private SpreadsheetEngine engine() {
        return SpreadsheetEngines.fake();
    }

    private SpreadsheetEngineContext spreadsheetEngineContext() {
        return SpreadsheetEngineContexts.fake();
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetEnginesExpressionReferenceToValueFunction> type() {
        return SpreadsheetEnginesExpressionReferenceToValueFunction.class;
    }
}

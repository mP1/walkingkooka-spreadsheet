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

package walkingkooka.spreadsheet.expression;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetEngineExpressionEvaluationContextTest implements ClassTesting<SpreadsheetEngineExpressionEvaluationContext>,
        ToStringTesting<SpreadsheetEngineExpressionEvaluationContext> {

    @Test
    public void testWithNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetEngineExpressionEvaluationContext.with(null)
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final SpreadsheetEngineContext context = SpreadsheetEngineContexts.fake();
        this.toStringAndCheck(
                SpreadsheetEngineExpressionEvaluationContext.with(context),
                context.toString()
        );
    }

    // ClassTesting......................................................................................................

    @Override
    public Class<SpreadsheetEngineExpressionEvaluationContext> type() {
        return SpreadsheetEngineExpressionEvaluationContext.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}

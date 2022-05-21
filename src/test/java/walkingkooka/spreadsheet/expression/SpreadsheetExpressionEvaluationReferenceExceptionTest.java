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

import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.StandardThrowableTesting;
import walkingkooka.tree.expression.ExpressionReference;

public final class SpreadsheetExpressionEvaluationReferenceExceptionTest implements StandardThrowableTesting<SpreadsheetExpressionEvaluationReferenceException> {

    private final static ExpressionReference EXPRESSION_REFERENCE = new ExpressionReference() {
    };

    @Override
    public void testIfClassIsFinalIfAllConstructorsArePrivate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetExpressionEvaluationReferenceException createThrowable(final String message) {
        return new SpreadsheetExpressionEvaluationReferenceException(message, EXPRESSION_REFERENCE);
    }

    @Override
    public SpreadsheetExpressionEvaluationReferenceException createThrowable(final String message, final Throwable cause) {
        return new SpreadsheetExpressionEvaluationReferenceException(message, EXPRESSION_REFERENCE, cause);
    }

    @Override
    public Class<SpreadsheetExpressionEvaluationReferenceException> type() {
        return SpreadsheetExpressionEvaluationReferenceException.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}

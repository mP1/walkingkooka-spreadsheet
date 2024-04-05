
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

package walkingkooka.spreadsheet.store;

import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.StandardThrowableTesting;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.FakeExpressionReference;

public final class SpreadsheetExpressionReferenceMissingStoreExceptionTest implements StandardThrowableTesting<SpreadsheetExpressionReferenceMissingStoreException> {

    private final static ExpressionReference EXPRESSION_REFERENCE = new FakeExpressionReference() {
    };

    @Override
    public SpreadsheetExpressionReferenceMissingStoreException createThrowable(final String message) {
        return new SpreadsheetExpressionReferenceMissingStoreException(message, EXPRESSION_REFERENCE);
    }

    @Override
    public SpreadsheetExpressionReferenceMissingStoreException createThrowable(final String message, final Throwable cause) {
        return new SpreadsheetExpressionReferenceMissingStoreException(message, EXPRESSION_REFERENCE, cause);
    }

    @Override
    public Class<SpreadsheetExpressionReferenceMissingStoreException> type() {
        return SpreadsheetExpressionReferenceMissingStoreException.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}

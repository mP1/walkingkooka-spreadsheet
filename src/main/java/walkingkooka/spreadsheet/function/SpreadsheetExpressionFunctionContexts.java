
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

package walkingkooka.spreadsheet.function;

import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.ExpressionEvaluationException;
import walkingkooka.tree.expression.ExpressionEvaluationReferenceException;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.function.Function;

public final class SpreadsheetExpressionFunctionContexts implements PublicStaticHelper {

    /**
     * {@see FakeSpreadsheetExpressionFunctionContext}
     */
    public static FakeSpreadsheetExpressionFunctionContext fake() {
        return new FakeSpreadsheetExpressionFunctionContext();
    }

    /**
     * A function that creates a {@link ExpressionEvaluationReferenceException}.
     */
    public static Function<ExpressionReference, ExpressionEvaluationException> referenceNotFound() {
        return (r) -> {
            final String text;
            if (r instanceof SpreadsheetSelection) {
                final SpreadsheetSelection selection = (SpreadsheetSelection) r;
                text = "Unknown " + selection.textLabel() + " " + selection;
            } else {
                text = "Unknown " + r.toString();
            }

            return new SpreadsheetExpressionEvaluationReferenceException(
                    text,
                    r
            );
        };
    }

    /**
     * Stop creation
     */
    private SpreadsheetExpressionFunctionContexts() {
        throw new UnsupportedOperationException();
    }
}

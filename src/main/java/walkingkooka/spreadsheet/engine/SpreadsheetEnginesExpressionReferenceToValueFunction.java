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

import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A {@link Function} which may be passed to {@link walkingkooka.tree.expression.ExpressionEvaluationContexts#basic}
 * and acts as a bridge resolving {@link ExpressionReference} to a {@link Expression}.
 * Note a {@link SpreadsheetCellRangeReference} will have missing cells given a {@link walkingkooka.spreadsheet.SpreadsheetErrorKind#NAME}.
 */
final class SpreadsheetEnginesExpressionReferenceToValueFunction implements Function<ExpressionReference, Optional<Optional<Object>>> {

    /**
     * Factory that creates a new {@link SpreadsheetEnginesExpressionReferenceToValueFunction}
     */
    static SpreadsheetEnginesExpressionReferenceToValueFunction with(final SpreadsheetEngine engine,
                                                                     final SpreadsheetEngineContext context) {
        Objects.requireNonNull(engine, "engine");
        Objects.requireNonNull(context, "context");

        return new SpreadsheetEnginesExpressionReferenceToValueFunction(
                engine,
                context
        );
    }

    /**
     * Private ctor.
     */
    private SpreadsheetEnginesExpressionReferenceToValueFunction(final SpreadsheetEngine engine,
                                                                 final SpreadsheetEngineContext context) {
        this.engine = engine;
        this.context = context;
    }

    @Override
    public Optional<Optional<Object>> apply(final ExpressionReference reference) {
        Objects.requireNonNull(reference, "values");

        return SpreadsheetEnginesExpressionReferenceToValueFunctionSpreadsheetSelectionVisitor.values(
                (SpreadsheetExpressionReference) reference,
                this.engine,
                this.context
        );
    }

    private final SpreadsheetEngine engine;
    private final SpreadsheetEngineContext context;

    @Override
    public String toString() {
        return this.engine.toString();
    }
}

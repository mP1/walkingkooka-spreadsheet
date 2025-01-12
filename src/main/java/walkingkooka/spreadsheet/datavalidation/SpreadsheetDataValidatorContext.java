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

package walkingkooka.spreadsheet.datavalidation;

import walkingkooka.Context;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * The {@link Context} that accompanies each validation request.
 */
public interface SpreadsheetDataValidatorContext extends ExpressionEvaluationContext {

    @Override
    default ExpressionEvaluationContext enterScope(final Function<ExpressionReference, Optional<Optional<Object>>> scoped) {
        Objects.requireNonNull(scoped, "scoped");

        throw new UnsupportedOperationException();
    }

    @Override
    default boolean isText(final Object value) {
        return value instanceof Character || value instanceof CharSequence;
    }

    /**
     * A {@link ExpressionReference} identifying the cell being validated.
     */
    ExpressionReference cellReference();
}

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

package walkingkooka.spreadsheet.reference;

import walkingkooka.spreadsheet.value.SpreadsheetError;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterName;
import walkingkooka.validation.ValidationError;
import walkingkooka.validation.ValidationReference;

/**
 * Base class for all Spreadsheet {@link ExpressionReference}.
 * Either a {@link SpreadsheetCellReference} or {@link SpreadsheetLabelName} with the later can be used for untargeted
 * form fields.
 */
abstract public class SpreadsheetExpressionReference extends SpreadsheetSelection implements ExpressionReference,
    ValidationReference {

    /**
     * Package private to limit subclassing.
     */
    SpreadsheetExpressionReference() {
        super();
    }

    @Override
    public final SpreadsheetSelection add(final int value) {
        throw new UnsupportedOperationException(this.toString());
    }

    @Override
    public final SpreadsheetSelection addSaturated(final int value) {
        throw new UnsupportedOperationException(this.toString());
    }

    @Override
    public final SpreadsheetSelection addIfRelative(final int delta) {
        throw new UnsupportedOperationException(this.toString());
    }

    /**
     * Adds the given deltas to the relative tokens of this {@link SpreadsheetExpressionReference} returning
     * the result.
     */
    public abstract SpreadsheetExpressionReference addIfRelative(final int column,
                                                                 final int row);

    @Override
    public final boolean testParameterName(final ExpressionFunctionParameterName parameterName) {
        return this.isLabelName() &&
            CASE_SENSITIVITY.equals(
                this.toString(),
                parameterName.value()
            );
    }

    /**
     * Returns this {@link SpreadsheetExpressionReference} in relative form. This is a no-op for {@link SpreadsheetLabelName}.
     */
    @Override
    abstract public SpreadsheetExpressionReference toRelative();

    // ValidationError..................................................................................................

    @Override
    public final ValidationError<SpreadsheetExpressionReference> setValidationErrorMessage(final String message) {
        return SpreadsheetError.parse(message).toValidationError(this);
    }
}

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

package walkingkooka.spreadsheet.validation;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.SpreadsheetContextTesting;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.validation.ValidatorContextTesting;

public interface SpreadsheetValidatorContextTesting<C extends SpreadsheetValidatorContext> extends ValidatorContextTesting<C, SpreadsheetExpressionReference>,
    SpreadsheetContextTesting<C> {

    @Test
    default void testValidationErrorWithCell() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final String message = "Hello";

        final SpreadsheetError error = SpreadsheetErrorKind.ERROR.setMessage(message);

        this.checkEquals(
            error.toValidationError(cell),
            this.createContext()
                .setValidationReference(cell)
                .validationError()
                .setMessage(message)
        );
    }

    @Test
    default void testValidationErrorWithLabel() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");
        final String message = "Hello";

        final SpreadsheetError error = SpreadsheetErrorKind.ERROR.setMessage(message);

        this.checkEquals(
            error.toValidationError(label),
            this.createContext()
                .setValidationReference(label)
                .validationError()
                .setMessage(message)
        );
    }

    @Override
    default String typeNameSuffix() {
        return SpreadsheetValidatorContext.class.getSimpleName();
    }
}

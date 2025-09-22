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

package walkingkooka.spreadsheet.validation.form;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.SpreadsheetProviderContextTesting;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.validation.form.FormHandlerContextTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetFormHandlerContextTesting<C extends SpreadsheetFormHandlerContext> extends FormHandlerContextTesting<C, SpreadsheetExpressionReference, SpreadsheetDelta>,
    SpreadsheetProviderContextTesting<C> {

    @Test
    default void testLoadFormFieldValueWithCellRangeFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .loadFormFieldValue(
                    SpreadsheetSelection.A1.toCellRange()
                )
        );
    }
}

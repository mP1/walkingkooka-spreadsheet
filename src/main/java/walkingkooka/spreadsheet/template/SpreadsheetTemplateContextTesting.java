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

package walkingkooka.spreadsheet.template;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContextTesting;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContextTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetTemplateContextTesting<C extends SpreadsheetTemplateContext> extends SpreadsheetParserContextTesting<C>,
        SpreadsheetExpressionEvaluationContextTesting<C> {

    @Test
    default void testCellFails() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> this.createContext()
                        .cell()
        );
    }

    @Test
    default void testLoadCellRangeFails() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> this.createContext()
                        .loadCellRange(
                                SpreadsheetSelection.A1.toCellRange()
                        )
        );
    }

    @Override
    default void testSetCellWithNullCellFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    default void testSetCellWithSame() {
        throw new UnsupportedOperationException();
    }

    @Test
    default void testSetCellFails() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> this.createContext()
                        .setCell(Optional.empty())
        );
    }

    @Override
    default void testSetSpreadsheetMetadataWithNullFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    default void testSetSpreadsheetMetadataWithDifferentIdFails() {
        throw new UnsupportedOperationException();
    }

    // FormHandlerContextTesting........................................................................................

    @Override
    default void testLoadFormFieldValueWithNullFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    default void testSaveFormFieldValuesWithNullFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    default void testUser() {
        throw new UnsupportedOperationException();
    }

    @Override
    default void testValidateFormWithNullFormFieldsFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    default void testValidatorContextWithNullReferenceFails() {
        throw new UnsupportedOperationException();
    }

    // class............................................................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetTemplateContext.class.getSimpleName();
    }
}

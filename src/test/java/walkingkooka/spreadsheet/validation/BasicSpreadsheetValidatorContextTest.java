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
import walkingkooka.Cast;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.validation.ValidatorContexts;
import walkingkooka.validation.provider.ValidatorSelector;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetValidatorContextTest implements SpreadsheetValidatorContextTesting<BasicSpreadsheetValidatorContext>,
    SpreadsheetMetadataTesting {

    @Test
    public void testWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetValidatorContext.with(null)
        );
    }

    @Override
    public void testSetLocaleWithDifferent() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testSetValidationReferenceSameDifferentKind() {
        final BasicSpreadsheetValidatorContext context = this.createContext();

        final SpreadsheetCellReference different = SpreadsheetSelection.parseCell("$A1");

        this.checkNotEquals(
            different,
            context.validationReference()
        );

        assertSame(
            context,
            context.setValidationReference(different)
        );
    }

    @Override
    public void testRemoveEnvironmentValueWithNullNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BasicSpreadsheetValidatorContext createContext() {
        return Cast.to(
            BasicSpreadsheetValidatorContext.with(
                ValidatorContexts.basic(
                    SpreadsheetSelection.A1,
                    (final ValidatorSelector validatorSelector) -> {
                        throw new UnsupportedOperationException();
                    },
                    (final Object value,
                     final SpreadsheetExpressionReference cellOrLabel) -> {
                        throw new UnsupportedOperationException();
                    },
                    SPREADSHEET_FORMATTER_CONTEXT,
                    PROVIDER_CONTEXT
                )
            )
        );
    }

    // class............................................................................................................

    @Override
    public Class<BasicSpreadsheetValidatorContext> type() {
        return BasicSpreadsheetValidatorContext.class;
    }
}

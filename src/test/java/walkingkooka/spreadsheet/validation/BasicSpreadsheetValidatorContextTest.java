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
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.validation.ValidatorContexts;

import java.math.MathContext;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetValidatorContextTest implements SpreadsheetValidatorContextTesting<BasicSpreadsheetValidatorContext>,
        SpreadsheetMetadataTesting{

    @Test
    public void testWithNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetValidatorContext.with(null)
        );
    }

    @Override
    public BasicSpreadsheetValidatorContext createContext() {
        return Cast.to(
                BasicSpreadsheetValidatorContext.with(
                        ValidatorContexts.basic(
                                SpreadsheetSelection.A1,
                                SPREADSHEET_FORMATTER_CONTEXT,
                                PROVIDER_CONTEXT
                        )
                )
        );
    }

    private final static DecimalNumberContext DECIMAL_NUMBER_CONTEXT = SPREADSHEET_FORMATTER_CONTEXT;

    @Override
    public String currencySymbol() {
        return DECIMAL_NUMBER_CONTEXT.currencySymbol();
    }

    @Override
    public char decimalSeparator() {
        return DECIMAL_NUMBER_CONTEXT.decimalSeparator();
    }

    @Override
    public String exponentSymbol() {
        return DECIMAL_NUMBER_CONTEXT.exponentSymbol();
    }

    @Override
    public char groupSeparator() {
        return DECIMAL_NUMBER_CONTEXT.groupSeparator();
    }

    @Override
    public MathContext mathContext() {
        return DECIMAL_NUMBER_CONTEXT.mathContext();
    }

    @Override
    public char negativeSign() {
        return DECIMAL_NUMBER_CONTEXT.negativeSign();
    }

    @Override
    public char percentageSymbol() {
        return DECIMAL_NUMBER_CONTEXT.percentageSymbol();
    }

    @Override
    public char positiveSign() {
        return DECIMAL_NUMBER_CONTEXT.positiveSign();
    }

    // class............................................................................................................

    @Override
    public Class<BasicSpreadsheetValidatorContext> type() {
        return BasicSpreadsheetValidatorContext.class;
    }
}

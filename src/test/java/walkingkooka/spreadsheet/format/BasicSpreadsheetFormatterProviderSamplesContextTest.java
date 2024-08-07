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

package walkingkooka.spreadsheet.format;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;

import java.math.MathContext;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetFormatterProviderSamplesContextTest implements SpreadsheetFormatterProviderSamplesContextTesting<BasicSpreadsheetFormatterProviderSamplesContext>,
        SpreadsheetMetadataTesting {

    @Test
    public void testWithNullSpreadsheetFormatterContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetFormatterProviderSamplesContext.with(null)
        );
    }

    @Override
    public BasicSpreadsheetFormatterProviderSamplesContext createContext() {
        return BasicSpreadsheetFormatterProviderSamplesContext.with(SPREADSHEET_FORMATTER_CONTEXT);
    }

    @Override
    public String currencySymbol() {
        return SPREADSHEET_FORMATTER_CONTEXT.currencySymbol();
    }

    @Override
    public char decimalSeparator() {
        return SPREADSHEET_FORMATTER_CONTEXT.decimalSeparator();
    }

    @Override
    public String exponentSymbol() {
        return SPREADSHEET_FORMATTER_CONTEXT.exponentSymbol();
    }

    @Override
    public char groupSeparator() {
        return SPREADSHEET_FORMATTER_CONTEXT.groupSeparator();
    }

    @Override
    public MathContext mathContext() {
        return SPREADSHEET_FORMATTER_CONTEXT.mathContext();
    }

    @Override
    public char negativeSign() {
        return SPREADSHEET_FORMATTER_CONTEXT.negativeSign();
    }

    @Override
    public char percentageSymbol() {
        return SPREADSHEET_FORMATTER_CONTEXT.percentageSymbol();
    }

    @Override
    public char positiveSign() {
        return SPREADSHEET_FORMATTER_CONTEXT.positiveSign();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<BasicSpreadsheetFormatterProviderSamplesContext> type() {
        return BasicSpreadsheetFormatterProviderSamplesContext.class;
    }
}

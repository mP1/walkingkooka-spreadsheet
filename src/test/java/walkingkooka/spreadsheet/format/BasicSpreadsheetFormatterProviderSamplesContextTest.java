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
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;

import java.math.MathContext;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetFormatterProviderSamplesContextTest implements SpreadsheetFormatterProviderSamplesContextTesting<BasicSpreadsheetFormatterProviderSamplesContext>,
    SpreadsheetMetadataTesting,
    DecimalNumberContextDelegator {

    @Test
    public void testWithNullSpreadsheetFormatterContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetFormatterProviderSamplesContext.with(
                null,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullProviderContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetFormatterProviderSamplesContext.with(
                SPREADSHEET_FORMATTER_CONTEXT,
                null
            )
        );
    }

    @Override
    public BasicSpreadsheetFormatterProviderSamplesContext createContext() {
        return BasicSpreadsheetFormatterProviderSamplesContext.with(
            SPREADSHEET_FORMATTER_CONTEXT,
            PROVIDER_CONTEXT
        );
    }

    // DecimalNumberContextDelegator....................................................................................

    @Override
    public MathContext mathContext() {
        return SPREADSHEET_FORMATTER_CONTEXT.mathContext();
    }

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return SPREADSHEET_FORMATTER_CONTEXT;
    }

    // class............................................................................................................

    @Override
    public Class<BasicSpreadsheetFormatterProviderSamplesContext> type() {
        return BasicSpreadsheetFormatterProviderSamplesContext.class;
    }
}

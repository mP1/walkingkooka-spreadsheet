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

import walkingkooka.convert.ConverterContext;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContextDelegator;

import java.time.LocalDateTime;

public interface SpreadsheetFormatterProviderSamplesContextDelegator extends SpreadsheetFormatterProviderSamplesContext,
    SpreadsheetFormatterContextDelegator,
    ProviderContextDelegator {

    // SpreadsheetFormatterContextDelegator.............................................................................

    @Override
    default SpreadsheetFormatterContext spreadsheetFormatterContext() {
        return this.spreadsheetFormatterProviderSamplesContext();
    }

    SpreadsheetFormatterProviderSamplesContext spreadsheetFormatterProviderSamplesContext();

    // ProviderContextDelegator.........................................................................................

    @Override
    default ProviderContext providerContext() {
        return this.spreadsheetFormatterProviderSamplesContext();
    }

    @Override
    default ConverterContext canConvert() {
        return this.spreadsheetFormatterProviderSamplesContext();
    }

    @Override
    default LocalDateTime now() {
        return this.spreadsheetFormatterProviderSamplesContext()
            .now();
    }
}

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

import walkingkooka.plugin.ProviderContext;

import java.util.List;
import java.util.Optional;

public interface SpreadsheetFormatterProviderDelegator extends SpreadsheetFormatterProvider {

    @Override
    default SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterSelector selector,
                                                      final ProviderContext context) {
        return this.spreadsheetFormatterProvider()
            .spreadsheetFormatter(
                selector,
                context
            );
    }

    @Override
    default SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterName name,
                                                      final List<?> values,
                                                      final ProviderContext context) {
        return this.spreadsheetFormatterProvider().spreadsheetFormatter(
            name,
            values,
            context
        );
    }

    @Override
    default Optional<SpreadsheetFormatterSelectorToken> spreadsheetFormatterNextToken(final SpreadsheetFormatterSelector selector) {
        return this.spreadsheetFormatterProvider().spreadsheetFormatterNextToken(selector);
    }

    @Override
    default List<SpreadsheetFormatterSample> spreadsheetFormatterSamples(final SpreadsheetFormatterName name,
                                                                         final SpreadsheetFormatterProviderSamplesContext context) {
        return this.spreadsheetFormatterProvider().spreadsheetFormatterSamples(
            name,
            context
        );
    }

    @Override
    default SpreadsheetFormatterInfoSet spreadsheetFormatterInfos() {
        return this.spreadsheetFormatterProvider().spreadsheetFormatterInfos();
    }

    SpreadsheetFormatterProvider spreadsheetFormatterProvider();
}

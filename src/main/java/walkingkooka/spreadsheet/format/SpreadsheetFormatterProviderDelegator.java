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

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SpreadsheetFormatterProviderDelegator extends SpreadsheetFormatterProvider {

    @Override
    default SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterSelector selector) {
        return this.spreadsheetFormatterProvider().spreadsheetFormatter(selector);
    }

    @Override
    default SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterName name,
                                                      final List<?> values) {
        return this.spreadsheetFormatterProvider().spreadsheetFormatter(
                name,
                values
        );
    }

    @Override
    default Optional<SpreadsheetFormatterSelectorTextComponent> spreadsheetFormatterNextTextComponent(final SpreadsheetFormatterSelector selector) {
        return this.spreadsheetFormatterProvider().spreadsheetFormatterNextTextComponent(selector);
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
    default Set<SpreadsheetFormatterInfo> spreadsheetFormatterInfos() {
        return this.spreadsheetFormatterProvider().spreadsheetFormatterInfos();
    }

    SpreadsheetFormatterProvider spreadsheetFormatterProvider();
}

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

package walkingkooka.spreadsheet.format.edit;

import walkingkooka.spreadsheet.format.FakeSpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterInfo;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSample;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelectorTextComponent;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class FakeSpreadsheetFormatterSelectorEditContext extends FakeSpreadsheetFormatterContext implements SpreadsheetFormatterSelectorEditContext {
    @Override
    public SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterSelector selector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterName name,
                                                     final List<?> values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetFormatterSelectorTextComponent> spreadsheetFormatterNextTextComponent(final SpreadsheetFormatterSelector selector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<SpreadsheetFormatterSample> spreadsheetFormatterSamples(final SpreadsheetFormatterName name,
                                                                        final SpreadsheetFormatterContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<SpreadsheetFormatterInfo> spreadsheetFormatterInfos() {
        throw new UnsupportedOperationException();
    }
}

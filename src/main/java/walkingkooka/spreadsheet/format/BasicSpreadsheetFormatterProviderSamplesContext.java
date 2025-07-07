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

import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

import java.util.Objects;

final class BasicSpreadsheetFormatterProviderSamplesContext implements SpreadsheetFormatterProviderSamplesContext,
    SpreadsheetFormatterContextDelegator {

    static BasicSpreadsheetFormatterProviderSamplesContext with(final SpreadsheetFormatterContext spreadsheetFormatterContext) {
        return new BasicSpreadsheetFormatterProviderSamplesContext(
            Objects.requireNonNull(
                spreadsheetFormatterContext,
                "spreadsheetFormatterContext"
            )
        );
    }

    private BasicSpreadsheetFormatterProviderSamplesContext(final SpreadsheetFormatterContext spreadsheetFormatterContext) {
        this.spreadsheetFormatterContext = spreadsheetFormatterContext;
    }

    @Override
    public SpreadsheetFormatterProviderSamplesContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
        final SpreadsheetFormatterContext before = this.spreadsheetFormatterContext;
        final SpreadsheetFormatterContext after = before.setPreProcessor(processor);

        return before.equals(after) ?
            this :
            new BasicSpreadsheetFormatterProviderSamplesContext(after);
    }

    // SpreadsheetFormatterContextDelegator.............................................................................

    @Override
    public SpreadsheetFormatterContext spreadsheetFormatterContext() {
        return this.spreadsheetFormatterContext;
    }

    private final SpreadsheetFormatterContext spreadsheetFormatterContext;

    @Override
    public String toString() {
        return this.spreadsheetFormatterContext.toString();
    }
}

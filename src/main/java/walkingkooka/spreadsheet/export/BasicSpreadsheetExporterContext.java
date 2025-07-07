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

package walkingkooka.spreadsheet.export;

import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextDelegator;

import java.util.Objects;

final class BasicSpreadsheetExporterContext implements SpreadsheetExporterContext,
    JsonNodeMarshallContextDelegator {

    static BasicSpreadsheetExporterContext with(final SpreadsheetMetadata spreadsheetMetadata,
                                                final JsonNodeMarshallContext context) {
        return new BasicSpreadsheetExporterContext(
            Objects.requireNonNull(spreadsheetMetadata, "spreadsheetMetadata"),
            Objects.requireNonNull(context, "context")
        );
    }

    private BasicSpreadsheetExporterContext(final SpreadsheetMetadata spreadsheetMetadata,
                                            final JsonNodeMarshallContext context) {
        this.spreadsheetMetadata = spreadsheetMetadata;
        this.context = context;
    }

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        return this.spreadsheetMetadata;
    }

    private final SpreadsheetMetadata spreadsheetMetadata;

    // JsonNodeMarshallContext..........................................................................................

    @Override
    public JsonNodeMarshallContext jsonNodeMarshallContext() {
        return this.context;
    }

    private final JsonNodeMarshallContext context;

    @Override
    public String toString() {
        return this.spreadsheetMetadata + " " + this.context;
    }
}

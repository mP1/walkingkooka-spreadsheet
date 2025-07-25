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


import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;

/**
 * A collection of {@link SpreadsheetExporterContext}.
 */
public final class SpreadsheetExporterContexts implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetExporterContext}
     */
    public static SpreadsheetExporterContext basic(final SpreadsheetMetadata spreadsheetMetadata,
                                                   final JsonNodeMarshallContext context) {
        return BasicSpreadsheetExporterContext.with(
            spreadsheetMetadata,
            context
        );
    }

    /**
     * {@see FakeSpreadsheetExporterContext}
     */
    public static SpreadsheetExporterContext fake() {
        return new FakeSpreadsheetExporterContext();
    }

    /**
     * Stop creation
     */
    private SpreadsheetExporterContexts() {
        throw new UnsupportedOperationException();
    }
}

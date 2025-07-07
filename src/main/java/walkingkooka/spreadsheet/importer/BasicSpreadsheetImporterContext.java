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

package walkingkooka.spreadsheet.importer;

import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextDelegator;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

import java.util.Objects;

final class BasicSpreadsheetImporterContext implements SpreadsheetImporterContext,
    JsonNodeUnmarshallContextDelegator {

    static BasicSpreadsheetImporterContext with(final JsonNodeUnmarshallContext context) {
        return new BasicSpreadsheetImporterContext(
            Objects.requireNonNull(context, "context")
        );
    }

    private BasicSpreadsheetImporterContext(final JsonNodeUnmarshallContext context) {
        this.context = context;
    }

    @Override
    public SpreadsheetImporterContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
        final JsonNodeUnmarshallContext before = this.context;
        final JsonNodeUnmarshallContext after = before.setPreProcessor(processor);

        return before.equals(after) ?
            this :
            new BasicSpreadsheetImporterContext(after);
    }

    @Override
    public JsonNodeUnmarshallContext jsonNodeUnmarshallContext() {
        return this.context;
    }

    private final JsonNodeUnmarshallContext context;

    @Override
    public String toString() {
        return this.context.toString();
    }
}

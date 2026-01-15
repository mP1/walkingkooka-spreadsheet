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

package walkingkooka.spreadsheet.compare;

import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContextDelegator;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

import java.util.Objects;

final class BasicSpreadsheetComparatorContext implements SpreadsheetComparatorContext,
    SpreadsheetConverterContextDelegator {

    static BasicSpreadsheetComparatorContext with(final SpreadsheetConverterContext converterContext) {
        return new BasicSpreadsheetComparatorContext(
            Objects.requireNonNull(converterContext, "converterContext")
        );
    }

    private BasicSpreadsheetComparatorContext(final SpreadsheetConverterContext converterContext) {
        this.converterContext = converterContext;
    }

    // LocaleContext....................................................................................................

    @Override
    public SpreadsheetComparatorContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor processor) {
        final SpreadsheetConverterContext before = this.converterContext;
        final SpreadsheetConverterContext after = before.setObjectPostProcessor(processor);
        return before.equals(after) ?
            this :
            new BasicSpreadsheetComparatorContext(after);
    }

    @Override
    public SpreadsheetComparatorContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
        final SpreadsheetConverterContext before = this.converterContext;
        final SpreadsheetConverterContext after = before.setPreProcessor(processor);
        return before.equals(after) ?
            this :
            new BasicSpreadsheetComparatorContext(after);
    }

    // SpreadsheetConverterContextDelegator.............................................................................

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        return SpreadsheetComparatorContext.super.spreadsheetMetadata();
    }

    @Override
    public SpreadsheetExpressionReference validationReference() {
        return SpreadsheetComparatorContext.super.validationReference();
    }

    @Override
    public SpreadsheetConverterContext spreadsheetConverterContext() {
        return this.converterContext;
    }

    private final SpreadsheetConverterContext converterContext;

    @Override
    public String toString() {
        return this.converterContext.toString();
    }
}

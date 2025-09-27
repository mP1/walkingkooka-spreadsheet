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

import walkingkooka.Context;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

/**
 * A {@link Context} that accompanies comparisons between two values. This might require converting values to a type
 * compatible with the actual {@link java.util.Comparator}.
 */
public interface SpreadsheetComparatorContext extends SpreadsheetConverterContext {

    /**
     * {@link SpreadsheetMetadata} is unavailable in a comparator context.
     */
    @Override
    default SpreadsheetMetadata spreadsheetMetadata() {
        throw new UnsupportedOperationException();
    }

    /**
     * A {@link SpreadsheetComparatorContext} is not executed within validation and will never need the validation reference.
     */
    @Override
    default SpreadsheetExpressionReference validationReference() {
        throw new UnsupportedOperationException();
    }

    // SpreadsheetConverterContext......................................................................................

    @Override
    SpreadsheetComparatorContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor processor);

    @Override
    SpreadsheetComparatorContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor);
}

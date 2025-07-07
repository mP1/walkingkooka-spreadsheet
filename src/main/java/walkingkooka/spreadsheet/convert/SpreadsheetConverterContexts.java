
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

package walkingkooka.spreadsheet.convert;

import walkingkooka.convert.Converter;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.tree.json.convert.JsonNodeConverterContext;

import java.util.Optional;

public final class SpreadsheetConverterContexts implements PublicStaticHelper {

    public final static Optional<SpreadsheetMetadata> NO_METADATA = Optional.empty();

    public final static Optional<SpreadsheetExpressionReference> NO_VALIDATION_REFERENCE = Optional.empty();

    /**
     * {@see BasicSpreadsheetConverterContext}
     */
    public static SpreadsheetConverterContext basic(final Optional<SpreadsheetMetadata> spreadsheetMetadata,
                                                    final Optional<SpreadsheetExpressionReference> validationReference,
                                                    final Converter<SpreadsheetConverterContext> converter,
                                                    final SpreadsheetLabelNameResolver spreadsheetLabelNameResolver,
                                                    final JsonNodeConverterContext context) {
        return BasicSpreadsheetConverterContext.with(
            spreadsheetMetadata,
            validationReference,
            converter,
            spreadsheetLabelNameResolver,
            context
        );
    }

    /**
     * {@see FakeSpreadsheetConverterContext}
     */
    public static SpreadsheetConverterContext fake() {
        return new FakeSpreadsheetConverterContext();
    }

    /**
     * Stop creation
     */
    private SpreadsheetConverterContexts() {
        throw new UnsupportedOperationException();
    }
}

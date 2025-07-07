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

import walkingkooka.convert.HasConverter;
import walkingkooka.spreadsheet.HasMissingCellNumberValue;
import walkingkooka.spreadsheet.meta.HasSpreadsheetMetadata;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContext;
import walkingkooka.tree.json.convert.JsonNodeConverterContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

/**
 * A {@link walkingkooka.convert.ConverterContext} that includes a few extra operations that are needed in a Spreadsheet context.
 */
public interface SpreadsheetConverterContext extends ExpressionNumberConverterContext,
    JsonNodeConverterContext,
    HasConverter<SpreadsheetConverterContext>,
    HasSpreadsheetMetadata,
    SpreadsheetLabelNameResolver,
    HasMissingCellNumberValue {

    @Override
    default ExpressionNumber missingCellNumberValue() {
        return this.expressionNumberKind()
            .zero();
    }

    /**
     * Returns the {@link SpreadsheetExpressionReference} being validated. This is useful for converters within a
     * validation.
     */
    SpreadsheetExpressionReference validationReference();

    // JsonNodeConverterContext.........................................................................................

    @Override
    SpreadsheetConverterContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor);
}

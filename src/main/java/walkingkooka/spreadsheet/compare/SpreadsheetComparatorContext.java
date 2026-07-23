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
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.validation.SpreadsheetValidationReference;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

/**
 * A {@link Context} that accompanies comparisons between two values. This might require converting values to a type
 * compatible with the actual {@link java.util.Comparator}.
 */
public interface SpreadsheetComparatorContext extends SpreadsheetConverterContext {

    /**
     * A {@link SpreadsheetLabelName} that may be used within {@link Expression} to get the LEFT value with a {@link SpreadsheetExpressionEvaluationContext}.
     */
    SpreadsheetLabelName LEFT = SpreadsheetSelection.labelName("left");

    /**
     * A {@link SpreadsheetLabelName} that may be used within {@link Expression} to get the RIGHT value with a {@link SpreadsheetExpressionEvaluationContext}.
     */
    SpreadsheetLabelName RIGHT = SpreadsheetSelection.labelName("right");

    /**
     * Returns a {@link SpreadsheetExpressionEvaluationContext} which may be used to evaluate an {@link Expression} that
     * compares two values.
     */
   SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext(final Object left,
                                                                                 final Object right);

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
    default SpreadsheetValidationReference validationReference() {
        throw new UnsupportedOperationException();
    }

    // SpreadsheetConverterContext......................................................................................

    @Override
    SpreadsheetComparatorContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor processor);

    @Override
    SpreadsheetComparatorContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor);
}

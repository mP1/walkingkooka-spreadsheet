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
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.ExpressionNumberConverterContextDelegator;

import java.util.Optional;

public interface SpreadsheetConverterContextDelegator extends SpreadsheetConverterContext,
        ExpressionNumberConverterContextDelegator {

    @Override
    default ExpressionNumberConverterContext expressionNumberConverterContext() {
        return this.spreadsheetConverterContext();
    }

    @Override
    default Converter<SpreadsheetConverterContext> converter() {
        return this.spreadsheetConverterContext()
                .converter();
    }

    @Override
    default Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName labelName) {
        return this.spreadsheetConverterContext()
                .resolveLabel(labelName);
    }

    @Override
    default SpreadsheetExpressionReference validationReference() {
        return this.validationReference();
    }

    SpreadsheetConverterContext spreadsheetConverterContext();
}

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

package walkingkooka.spreadsheet.datavalidation;

import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;

public abstract class SpreadsheetDataValidatorTemplateTestCase<V extends SpreadsheetDataValidatorTemplate, T> implements SpreadsheetDataValidatorTesting<V, T> {

    SpreadsheetDataValidatorTemplateTestCase() {
        super();
    }

    @Override
    public SpreadsheetDataValidatorContext createContext() {
        return BasicSpreadsheetDataValidatorContext.with(this.cellReference(), this.value(), this.expressionEvaluationContext());
    }

    final ExpressionReference cellReference() {
        return SpreadsheetReferenceKind.RELATIVE.column(1).setRow(SpreadsheetReferenceKind.RELATIVE.row(2));
    }

    final ExpressionEvaluationContext expressionEvaluationContext() {
        final Converter all = Converters.collection(
                Lists.of(Converters.simple(),
                        Converters.truthyNumberBoolean()));

        return new FakeExpressionEvaluationContext() {
            @Override
            public <TT> TT convert(final Object value, final Class<TT> target) {
                return all.convert(value, target, ConverterContexts.basic(this));
            }
        };
    }
}

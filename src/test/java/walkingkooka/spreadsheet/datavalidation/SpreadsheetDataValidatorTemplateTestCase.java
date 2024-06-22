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

import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberConverters;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;

public abstract class SpreadsheetDataValidatorTemplateTestCase<V extends SpreadsheetDataValidatorTemplate<T>, T> implements SpreadsheetDataValidatorTesting<V, T> {

    final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;

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
        final Converter<ExpressionNumberConverterContext> all = Converters.collection(
                Lists.of(
                        Converters.simple(),
                        ExpressionNumberConverters.toNumberOrExpressionNumber(
                                Converters.simple()
                        ),
                        ExpressionNumberConverters.numberOrExpressionNumberTo(
                                Converters.numberToBoolean()
                        )
                )
        );

        return new FakeExpressionEvaluationContext() {
            @Override
            public <TT> Either<TT, String> convert(final Object value,
                                                   final Class<TT> target) {
                return all.convert(value,
                        target,
                        ExpressionNumberConverterContexts.basic(Converters.fake(),
                                ConverterContexts.basic(Converters.fake(),
                                        DateTimeContexts.fake(),
                                        DecimalNumberContexts.fake()),
                                EXPRESSION_NUMBER_KIND));
            }
        };
    }
}

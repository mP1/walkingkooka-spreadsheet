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

import org.junit.jupiter.api.Test;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.ConverterTesting;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.PublicStaticHelperTesting;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.lang.reflect.Method;
import java.math.MathContext;

public final class SpreadsheetConvertersTest implements ClassTesting2<SpreadsheetConverters>,
        PublicStaticHelperTesting<SpreadsheetConverters>,
        ConverterTesting {

    // expressionNumber.................................................................................................

    @Test
    public void testExpressionNumberConvertFails() {
        this.convertFails(
                SpreadsheetConverters.expressionNumber(
                        SpreadsheetPattern.parseNumberParsePattern("0.00")
                                .parser()
                ),
                "1",
                ExpressionNumber.class,
                this.spreadsheetConverterContext(ExpressionNumberKind.BIG_DECIMAL)
        );
    }

    @Test
    public void testExpressionNumberBigDecimalConvert() {
        this.convertAndCheck2(
                ExpressionNumberKind.BIG_DECIMAL
        );
    }

    @Test
    public void testExpressionNumberDoubleConvert() {
        this.convertAndCheck2(
                ExpressionNumberKind.DOUBLE
        );
    }

    private void convertAndCheck2(final ExpressionNumberKind kind) {
        this.convertAndCheck(
                SpreadsheetConverters.expressionNumber(
                        SpreadsheetPattern.parseNumberParsePattern("0.00")
                                .parser()
                ),
                "1.25",
                ExpressionNumber.class,
                this.spreadsheetConverterContext(kind),
                kind.create(1.25)
        );
    }

    private SpreadsheetConverterContext spreadsheetConverterContext(final ExpressionNumberKind kind) {
        return SpreadsheetConverterContexts.basic(
                Converters.fake(), // not used
                SpreadsheetLabelNameResolvers.fake(), // not required
                ExpressionNumberConverterContexts.basic(
                        Converters.fake(), // not used
                        ConverterContexts.basic(
                                Converters.fake(),
                                DateTimeContexts.fake(), // unused only doing numbers
                                DecimalNumberContexts.american(MathContext.DECIMAL32)
                        ),
                        kind
                )
        );
    }

    // PublicStaticHelperTesting........................................................................................

    @Test
    public void testPublicStaticMethodsWithoutMathContextParameter() {
        this.publicStaticMethodParametersTypeCheck(MathContext.class);
    }

    @Override
    public Class<SpreadsheetConverters> type() {
        return SpreadsheetConverters.class;
    }

    @Override
    public boolean canHavePublicTypes(final Method method) {
        return false;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}

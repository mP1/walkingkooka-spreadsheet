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
import walkingkooka.convert.ConverterTesting2;
import walkingkooka.convert.Converters;
import walkingkooka.predicate.Predicates;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTextFormatPattern;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;

public final class GeneralSpreadsheetConverterBooleanStringTest extends GeneralSpreadsheetConverterTestCase<GeneralSpreadsheetConverterBooleanString> implements ConverterTesting2<GeneralSpreadsheetConverterBooleanString, ExpressionNumberConverterContext> {

    @Test
    public void testNonBooleanFails() {
        this.convertFails("true", String.class);
    }

    @Test
    public void testTrue() {
        this.convertAndCheck(true, String.class, "truetrue");
    }

    @Test
    public void testFalse() {
        this.convertAndCheck(false, String.class, "falsefalse");
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createConverter(), "isBoolean->class java.lang.String->@@");
    }

    @Override
    public GeneralSpreadsheetConverterBooleanString createConverter() {
        return GeneralSpreadsheetConverterBooleanString.with(
                Converters.booleanTrueFalse(
                        Predicates.customToString((v) -> v instanceof Boolean, "isBoolean"),
                        Predicates.is(String.class),
                        Predicates.is(Boolean.TRUE),
                        "true",
                        "false"
                ),
                SpreadsheetTextFormatPattern.parseTextFormatPattern("@@").formatter().converter()
        );
    }

    @Override
    public ExpressionNumberConverterContext createContext() {
        return ExpressionNumberConverterContexts.basic(
                Converters.fake(),
                ConverterContexts.fake(),
                ExpressionNumberKind.DEFAULT
        );
    }

    @Override
    public Class<GeneralSpreadsheetConverterBooleanString> type() {
        return GeneralSpreadsheetConverterBooleanString.class;
    }

    @Override
    public String typeNameSuffix() {
        return Boolean.class.getSimpleName() + String.class.getSimpleName();
    }
}

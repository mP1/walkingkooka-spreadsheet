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
import walkingkooka.convert.ConverterTesting2;
import walkingkooka.convert.Converters;
import walkingkooka.predicate.Predicates;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTextFormatPattern;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;

public final class SpreadsheetConverterGeneralBooleanStringTest implements ConverterTesting2<SpreadsheetConverterGeneralBooleanString, SpreadsheetConverterContext> {

    @Test
    public void testNonBooleanFails() {
        this.convertFails("true", String.class);
    }

    @Test
    public void testTrue() {
        this.convertAndCheck(
            true,
            "truetrue"
        );
    }

    @Test
    public void testFalse() {
        this.convertAndCheck(
            false,
            "falsefalse"
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createConverter(),
            "Boolean to class java.lang.String to @@"
        );
    }

    @Override
    public SpreadsheetConverterGeneralBooleanString createConverter() {
        return SpreadsheetConverterGeneralBooleanString.with(
            Converters.toBoolean(
                Predicates.customToString((v) -> v instanceof Boolean, "Boolean"),
                Predicates.is(String.class),
                Predicates.is(Boolean.TRUE),
                "true",
                "false"
            ),
            SpreadsheetTextFormatPattern.parseTextFormatPattern("@@")
                .formatter()
                .converter()
                .cast(SpreadsheetConverterContext.class)
        );
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return SpreadsheetConverterContexts.basic(
            SpreadsheetConverterContexts.NO_METADATA,
            SpreadsheetConverterContexts.NO_VALIDATION_REFERENCE,
            SpreadsheetConverters.basic(),
            SpreadsheetLabelNameResolvers.fake(),
            JsonNodeConverterContexts.fake()
        );
    }

    @Override
    public Class<SpreadsheetConverterGeneralBooleanString> type() {
        return SpreadsheetConverterGeneralBooleanString.class;
    }

    @Override
    public String typeNameSuffix() {
        return Boolean.class.getSimpleName() + String.class.getSimpleName();
    }
}

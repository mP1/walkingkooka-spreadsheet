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
import walkingkooka.Cast;
import walkingkooka.ToStringTesting;
import walkingkooka.convert.Converters;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.TypeNameTesting;

public final class GeneralSpreadsheetConverterMappingTest extends GeneralSpreadsheetConverterTestCase<GeneralSpreadsheetConverterMapping<?>>
        implements ClassTesting2<GeneralSpreadsheetConverterMapping<?>>,
        ToStringTesting<GeneralSpreadsheetConverterMapping<?>>,
        TypeNameTesting<GeneralSpreadsheetConverterMapping<?>> {

    @Test
    public void testToString2() {
        final GeneralSpreadsheetConverterMapping<?> mapping = GeneralSpreadsheetConverterMapping.with(
                Converters.fake().setToString("Boolean1"),
                Converters.fake().setToString("Date2"),
                Converters.fake().setToString("DateTime3"),
                Converters.fake().setToString("Number4"),
                Converters.fake().setToString("String5"),
                Converters.fake().setToString("Time6")
        );

        this.toStringAndCheck(
                mapping,
                "boolean=Boolean1, date=Date2, dateTime=DateTime3, number=Number4, string=String5, time=Time6"
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<GeneralSpreadsheetConverterMapping<?>> type() {
        return Cast.to(GeneralSpreadsheetConverterMapping.class);
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNameSuffix() {
        return "";
    }
}

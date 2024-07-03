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
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.Converters;
import walkingkooka.spreadsheet.SpreadsheetValueTypeVisitorTesting;

import java.time.LocalDate;

public final class GeneralSpreadsheetConverterSpreadsheetValueTypeVisitorTest extends GeneralSpreadsheetConverterTestCase<GeneralSpreadsheetConverterSpreadsheetValueTypeVisitor<ConverterContext>>
        implements SpreadsheetValueTypeVisitorTesting<GeneralSpreadsheetConverterSpreadsheetValueTypeVisitor<ConverterContext>> {

    @Override
    public void testAllConstructorsVisibility() {
    }

    @Test
    public void testToString() {
        final GeneralSpreadsheetConverterMapping<Converter<ConverterContext>> mapping = GeneralSpreadsheetConverterMapping.with(
                Converters.fake().setToString("Boolean1"),
                Converters.fake().setToString("Date2"),
                Converters.fake().setToString("DateTime3"),
                Converters.fake().setToString("Number4"),
                Converters.fake().setToString("String5"),
                Converters.fake().setToString("Time6")
        );
        this.toStringAndCheck(
                new GeneralSpreadsheetConverterSpreadsheetValueTypeVisitor<>(mapping),
                "mapping=boolean=Boolean1, date=Date2, dateTime=DateTime3, number=Number4, string=String5, time=Time6"
        );
    }

    @Test
    public void testToString2() {
        final GeneralSpreadsheetConverterMapping<Converter<ConverterContext>> mapping = GeneralSpreadsheetConverterMapping.with(
                Converters.fake().setToString("Boolean1"),
                Converters.fake().setToString("Date2"),
                Converters.fake().setToString("DateTime3"),
                Converters.fake().setToString("Number4"),
                Converters.fake().setToString("String5"),
                Converters.fake().setToString("Time6")
        );
        final GeneralSpreadsheetConverterSpreadsheetValueTypeVisitor<ConverterContext> visitor = new GeneralSpreadsheetConverterSpreadsheetValueTypeVisitor<>(mapping);
        visitor.accept(LocalDate.class);

        this.toStringAndCheck(visitor, "mapping=boolean=Boolean1, date=Date2, dateTime=DateTime3, number=Number4, string=String5, time=Time6, general=Date2");
    }

    @Override
    public GeneralSpreadsheetConverterSpreadsheetValueTypeVisitor<ConverterContext> createVisitor() {
        return new GeneralSpreadsheetConverterSpreadsheetValueTypeVisitor<>(null);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<GeneralSpreadsheetConverterSpreadsheetValueTypeVisitor<ConverterContext>> type() {
        return Cast.to(GeneralSpreadsheetConverterSpreadsheetValueTypeVisitor.class);
    }
}

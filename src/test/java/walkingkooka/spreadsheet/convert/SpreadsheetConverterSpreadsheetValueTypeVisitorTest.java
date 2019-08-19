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
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.spreadsheet.SpreadsheetValueTypeVisitorTesting;

import java.time.LocalDate;

public final class SpreadsheetConverterSpreadsheetValueTypeVisitorTest extends SpreadsheetConverterTestCase<SpreadsheetConverterSpreadsheetValueTypeVisitor>
        implements SpreadsheetValueTypeVisitorTesting<SpreadsheetConverterSpreadsheetValueTypeVisitor> {

    @Override
    public void testAllConstructorsVisibility() {
    }

    @Test
    public void testToString() {
        final SpreadsheetConverterMapping<Converter> mapping = SpreadsheetConverterMapping.with(Converters.fake().setToString("Boolean1"),
                Converters.fake().setToString("Date2"),
                Converters.fake().setToString("DateTime3"),
                Converters.fake().setToString("Number4"),
                Converters.fake().setToString("String5"),
                Converters.fake().setToString("Time6"));
        this.toStringAndCheck(new SpreadsheetConverterSpreadsheetValueTypeVisitor(mapping),
                "mapping=boolean=Boolean1, date=Date2, dateTime=DateTime3, number=Number4, string=String5, time=Time6");
    }

    @Test
    public void testToString2() {
        final SpreadsheetConverterMapping<Converter> mapping = SpreadsheetConverterMapping.with(Converters.fake().setToString("Boolean1"),
                Converters.fake().setToString("Date2"),
                Converters.fake().setToString("DateTime3"),
                Converters.fake().setToString("Number4"),
                Converters.fake().setToString("String5"),
                Converters.fake().setToString("Time6"));
        final SpreadsheetConverterSpreadsheetValueTypeVisitor visitor = new SpreadsheetConverterSpreadsheetValueTypeVisitor(mapping);
        visitor.accept(LocalDate.class);

        this.toStringAndCheck(visitor, "mapping=boolean=Boolean1, date=Date2, dateTime=DateTime3, number=Number4, string=String5, time=Time6, converter=Date2");
    }

    @Override
    public SpreadsheetConverterSpreadsheetValueTypeVisitor createVisitor() {
        return new SpreadsheetConverterSpreadsheetValueTypeVisitor(null);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetConverterSpreadsheetValueTypeVisitor> type() {
        return SpreadsheetConverterSpreadsheetValueTypeVisitor.class;
    }
}

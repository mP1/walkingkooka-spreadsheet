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
import walkingkooka.spreadsheet.SpreadsheetValueTypeVisitorTesting;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class SpreadsheetConverterSpreadsheetValueTypeVisitorTest extends SpreadsheetConverterTestCase<SpreadsheetConverterSpreadsheetValueTypeVisitor>
        implements SpreadsheetValueTypeVisitorTesting<SpreadsheetConverterSpreadsheetValueTypeVisitor> {

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createVisitor(), "all=\"BigDecimal\" dateOffset=1 format=\"#.#\"");
    }

    @Override
    public SpreadsheetConverterSpreadsheetValueTypeVisitor createVisitor() {
        return new SpreadsheetConverterSpreadsheetValueTypeVisitor(SpreadsheetConverterMapping.with(1,
                "#.#",
                "#.##",
                "#.###",
                DateTimeFormatter.ISO_LOCAL_DATE,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                DateTimeFormatter.ISO_TIME,
                "###").bigDecimal);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetConverterSpreadsheetValueTypeVisitor> type() {
        return SpreadsheetConverterSpreadsheetValueTypeVisitor.class;
    }
}

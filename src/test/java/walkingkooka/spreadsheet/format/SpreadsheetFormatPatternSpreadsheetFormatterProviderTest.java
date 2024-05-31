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

package walkingkooka.spreadsheet.format;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;

public final class SpreadsheetFormatPatternSpreadsheetFormatterProviderTest implements SpreadsheetFormatterProviderTesting<SpreadsheetFormatPatternSpreadsheetFormatterProvider>,
        ToStringTesting<SpreadsheetFormatPatternSpreadsheetFormatterProvider> {

    @Test
    public void testDateFormat() {
        this.spreadsheetFormatterAndCheck(
                "date-format dd/mm/yy",
                SpreadsheetPattern.parseDateFormatPattern("dd/mm/yy").formatter()
        );
    }

    @Test
    public void testDateTimeFormat() {
        this.spreadsheetFormatterAndCheck(
                "date-time-format dd/mm/yyyy hh:mm:ss",
                SpreadsheetPattern.parseDateTimeFormatPattern("dd/mm/yyyy hh:mm:ss").formatter()
        );
    }

    @Test
    public void testNumberFormat() {
        this.spreadsheetFormatterAndCheck(
                "number-format $0.00",
                SpreadsheetPattern.parseNumberFormatPattern("$0.00").formatter()
        );
    }

    @Test
    public void testTextFormat() {
        this.spreadsheetFormatterAndCheck(
                "text-format @@\"Hello\"",
                SpreadsheetPattern.parseTextFormatPattern("@@\"Hello\"").formatter()
        );
    }

    @Test
    public void testTimeFormat() {
        this.spreadsheetFormatterAndCheck(
                "time-format hh:mm:ss",
                SpreadsheetPattern.parseTimeFormatPattern("hh:mm:ss").formatter()
        );
    }

    @Override
    public SpreadsheetFormatPatternSpreadsheetFormatterProvider createSpreadsheetFormatterProvider() {
        return SpreadsheetFormatPatternSpreadsheetFormatterProvider.INSTANCE;
    }

    @Override
    public Class<SpreadsheetFormatPatternSpreadsheetFormatterProvider> type() {
        return SpreadsheetFormatPatternSpreadsheetFormatterProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetFormatPatternSpreadsheetFormatterProvider.INSTANCE,
                "SpreadsheetFormatPattern.spreadsheetFormatter"
        );
    }
}

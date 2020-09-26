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

package walkingkooka.spreadsheet.meta;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateParsePatterns;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateTimeParsePatterns;

import java.util.Locale;

public final class SpreadsheetMetadataPropertyNameSpreadsheetDateTimeParsePatternsTest extends SpreadsheetMetadataPropertyNameTestCase<SpreadsheetMetadataPropertyNameSpreadsheetDateTimeParsePatterns, SpreadsheetDateTimeParsePatterns> {

    @Test
    public void testExtractLocaleValue() {
        this.extractLocaleValueAndCheck(Locale.ENGLISH,
                SpreadsheetDateParsePatterns.parseDateTimeParsePatterns("dddd, mmmm d, yyyy \\a\\t H:mm:ss AM/PM;dddd, mmmm d, yyyy \\a\\t H:mm:ss AM/PM;dddd, mmmm d, yyyy, H:mm:ss AM/PM;dddd, mmmm d, yyyy, H:mm AM/PM;mmmm d, yyyy \\a\\t H:mm:ss AM/PM;mmmm d, yyyy \\a\\t H:mm:ss AM/PM;mmmm d, yyyy, H:mm:ss AM/PM;mmmm d, yyyy, H:mm AM/PM;mmm d, yyyy, H:mm:ss AM/PM;mmm d, yyyy, H:mm:ss AM/PM;mmm d, yyyy, H:mm:ss AM/PM;mmm d, yyyy, H:mm AM/PM;m/d/yy, H:mm:ss AM/PM;m/d/yy, H:mm:ss AM/PM;m/d/yy, H:mm:ss AM/PM;m/d/yy, H:mm AM/PM"));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetMetadataPropertyNameSpreadsheetDateTimeParsePatterns.instance(), "date-time-parse-patterns");
    }

    @Override
    SpreadsheetMetadataPropertyNameSpreadsheetDateTimeParsePatterns createName() {
        return SpreadsheetMetadataPropertyNameSpreadsheetDateTimeParsePatterns.instance();
    }

    @Override
    SpreadsheetDateTimeParsePatterns propertyValue() {
        return SpreadsheetDateTimeParsePatterns.parseDateTimeParsePatterns("ddmmyyyyhhmmss \"pattern-1\";yyyymmddhhmmss \"pattern-2\"");
    }

    @Override
    String propertyValueType() {
        return "DateTime parse patterns";
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameSpreadsheetDateTimeParsePatterns> type() {
        return SpreadsheetMetadataPropertyNameSpreadsheetDateTimeParsePatterns.class;
    }
}

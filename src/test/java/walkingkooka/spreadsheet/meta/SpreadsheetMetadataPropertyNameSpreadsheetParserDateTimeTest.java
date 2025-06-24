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
import walkingkooka.locale.LocaleContexts;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateTimeParsePattern;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;

import java.util.Locale;

public final class SpreadsheetMetadataPropertyNameSpreadsheetParserDateTimeTest extends SpreadsheetMetadataPropertyNameSpreadsheetParserTestCase<SpreadsheetMetadataPropertyNameSpreadsheetParserDateTime> {

    @Test
    public void testExtractLocaleAwareValue() {
        this.extractLocaleValueAwareAndCheck(
                LocaleContexts.jre(Locale.ENGLISH),
                SpreadsheetDateParsePattern.parseDateTimeParsePattern("dddd, mmmm d, yyyy \\a\\t h:mm:ss AM/PM;dddd, mmmm d, yy \\a\\t h:mm:ss AM/PM;dddd, mmmm d, yy \\a\\t h:mm:ss;dddd, mmmm d, yy \\a\\t h:mm AM/PM;dddd, mmmm d, yyyy \\a\\t h:mm:ss.0 AM/PM;dddd, mmmm d, yyyy \\a\\t h:mm:ss.0;dddd, mmmm d, yyyy \\a\\t h:mm:ss;dddd, mmmm d, yyyy \\a\\t h:mm AM/PM;dddd, mmmm d, yyyy \\a\\t h:mm;dddd, mmmm d, yyyy, h:mm:ss AM/PM;dddd, mmmm d, yy, h:mm:ss AM/PM;dddd, mmmm d, yy, h:mm:ss;dddd, mmmm d, yy, h:mm AM/PM;dddd, mmmm d, yyyy, h:mm:ss.0 AM/PM;dddd, mmmm d, yyyy, h:mm:ss.0;dddd, mmmm d, yyyy, h:mm:ss;dddd, mmmm d, yyyy, h:mm AM/PM;dddd, mmmm d, yyyy, h:mm;dddd, mmmm d, yy, h:mm;mmmm d, yyyy \\a\\t h:mm:ss AM/PM;mmmm d, yy \\a\\t h:mm:ss AM/PM;mmmm d, yy \\a\\t h:mm:ss;mmmm d, yy \\a\\t h:mm AM/PM;mmmm d, yyyy \\a\\t h:mm:ss.0 AM/PM;mmmm d, yyyy \\a\\t h:mm:ss.0;mmmm d, yyyy \\a\\t h:mm:ss;mmmm d, yyyy \\a\\t h:mm AM/PM;mmmm d, yyyy \\a\\t h:mm;mmmm d, yyyy, h:mm:ss AM/PM;mmmm d, yy, h:mm:ss AM/PM;mmmm d, yy, h:mm:ss;mmmm d, yy, h:mm AM/PM;mmmm d, yyyy, h:mm:ss.0 AM/PM;mmmm d, yyyy, h:mm:ss.0;mmmm d, yyyy, h:mm:ss;mmmm d, yyyy, h:mm AM/PM;mmmm d, yyyy, h:mm;mmmm d, yy, h:mm;mmm d, yyyy, h:mm:ss AM/PM;mmm d, yy, h:mm:ss AM/PM;mmm d, yy, h:mm:ss;mmm d, yy, h:mm AM/PM;mmm d, yyyy, h:mm:ss.0 AM/PM;mmm d, yyyy, h:mm:ss.0;mmm d, yyyy, h:mm:ss;mmm d, yyyy, h:mm AM/PM;mmm d, yyyy, h:mm;mmm d, yy, h:mm;m/d/yy, h:mm:ss AM/PM;m/d/yy, h:mm:ss;m/d/yy, h:mm AM/PM;m/d/yyyy, h:mm:ss AM/PM;m/d/yyyy, h:mm:ss.0 AM/PM;m/d/yyyy, h:mm:ss.0;m/d/yyyy, h:mm:ss;m/d/yyyy, h:mm AM/PM;m/d/yy, h:mm:ss.0;m/d/yy, h:mm;m/d/yyyy, h:mm")
                        .spreadsheetParserSelector()
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetMetadataPropertyNameSpreadsheetParserDateTime.instance(),
                "dateTimeParser"
        );
    }

    @Override
    SpreadsheetMetadataPropertyNameSpreadsheetParserDateTime createName() {
        return SpreadsheetMetadataPropertyNameSpreadsheetParserDateTime.instance();
    }

    @Override
    SpreadsheetParserSelector propertyValue() {
        return SpreadsheetDateTimeParsePattern.parseDateTimeParsePattern("ddmmyyyyhhmmss \"pattern-1\";yyyymmddhhmmss \"pattern-2\"")
                .spreadsheetParserSelector();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameSpreadsheetParserDateTime> type() {
        return SpreadsheetMetadataPropertyNameSpreadsheetParserDateTime.class;
    }
}

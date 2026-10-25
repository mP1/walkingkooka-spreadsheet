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
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateTimeParsePattern;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelector;

public final class SpreadsheetMetadataPropertyNameSpreadsheetParserSelectorDateTimeTest extends SpreadsheetMetadataPropertyNameSpreadsheetParserSelectorTestCase<SpreadsheetMetadataPropertyNameSpreadsheetParserSelectorDateTime> {

    @Test
    public void testExtractLocaleAwareValue() {
        this.extractLocaleValueAwareAndCheck(
            LOCALE_CONTEXT,
            SpreadsheetDateParsePattern.parseDateTimeParsePattern("dddd, d mmmm yyyy \\a\\t h:mm:ss AM/PM;dddd, d mmmm yy \\a\\t h:mm:ss AM/PM;dddd, d mmmm yy \\a\\t h:mm:ss;dddd, d mmmm yy \\a\\t h:mm AM/PM;dddd, d mmmm yyyy \\a\\t h:mm:ss.0 AM/PM;dddd, d mmmm yyyy \\a\\t h:mm:ss.0;dddd, d mmmm yyyy \\a\\t h:mm:ss;dddd, d mmmm yyyy \\a\\t h:mm AM/PM;dddd, d mmmm yyyy \\a\\t h:mm;dddd, d mmmm yyyy, h:mm:ss AM/PM;dddd, d mmmm yy, h:mm:ss AM/PM;dddd, d mmmm yy, h:mm:ss;dddd, d mmmm yy, h:mm AM/PM;dddd, d mmmm yyyy, h:mm:ss.0 AM/PM;dddd, d mmmm yyyy, h:mm:ss.0;dddd, d mmmm yyyy, h:mm:ss;dddd, d mmmm yyyy, h:mm AM/PM;dddd, d mmmm yyyy, h:mm;dddd, d mmmm yy, h:mm;d mmmm yyyy \\a\\t h:mm:ss AM/PM;d mmmm yy \\a\\t h:mm:ss AM/PM;d mmmm yy \\a\\t h:mm:ss;d mmmm yy \\a\\t h:mm AM/PM;d mmmm yyyy \\a\\t h:mm:ss.0 AM/PM;d mmmm yyyy \\a\\t h:mm:ss.0;d mmmm yyyy \\a\\t h:mm:ss;d mmmm yyyy \\a\\t h:mm AM/PM;d mmmm yyyy \\a\\t h:mm;d mmmm yyyy, h:mm:ss AM/PM;d mmmm yy, h:mm:ss AM/PM;d mmmm yy, h:mm:ss;d mmmm yy, h:mm AM/PM;d mmmm yyyy, h:mm:ss.0 AM/PM;d mmmm yyyy, h:mm:ss.0;d mmmm yyyy, h:mm:ss;d mmmm yyyy, h:mm AM/PM;d mmmm yyyy, h:mm;d mmmm yy, h:mm;d mmm yyyy, h:mm:ss AM/PM;d mmm yy, h:mm:ss AM/PM;d mmm yy, h:mm:ss;d mmm yy, h:mm AM/PM;d mmm yyyy, h:mm:ss.0 AM/PM;d mmm yyyy, h:mm:ss.0;d mmm yyyy, h:mm:ss;d mmm yyyy, h:mm AM/PM;d mmm yyyy, h:mm;d mmm yy, h:mm;d/m/yy, h:mm:ss AM/PM;d/m/yy, h:mm:ss;d/m/yy, h:mm AM/PM;d/m/yyyy, h:mm:ss AM/PM;d/m/yyyy, h:mm:ss.0 AM/PM;d/m/yyyy, h:mm:ss.0;d/m/yyyy, h:mm:ss;d/m/yyyy, h:mm AM/PM;d/m/yy, h:mm:ss.0;d/m/yy, h:mm;d/m/yyyy, h:mm")
                .spreadsheetParserSelector()
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetMetadataPropertyNameSpreadsheetParserSelectorDateTime.instance(),
            "dateTimeParser"
        );
    }

    @Override
    SpreadsheetMetadataPropertyNameSpreadsheetParserSelectorDateTime createName() {
        return SpreadsheetMetadataPropertyNameSpreadsheetParserSelectorDateTime.instance();
    }

    @Override
    SpreadsheetParserSelector propertyValue() {
        return SpreadsheetDateTimeParsePattern.parseDateTimeParsePattern("ddmmyyyyhhmmss \"pattern-1\";yyyymmddhhmmss \"pattern-2\"")
            .spreadsheetParserSelector();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameSpreadsheetParserSelectorDateTime> type() {
        return SpreadsheetMetadataPropertyNameSpreadsheetParserSelectorDateTime.class;
    }
}

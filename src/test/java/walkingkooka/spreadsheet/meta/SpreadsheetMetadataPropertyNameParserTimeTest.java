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
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTimeParsePattern;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;

import java.util.Locale;

public final class SpreadsheetMetadataPropertyNameParserTimeTest extends SpreadsheetMetadataPropertyNameParserTestCase<SpreadsheetMetadataPropertyNameParserTime> {

    @Test
    public void testExtractLocaleAwareValue() {
        this.extractLocaleValueAwareAndCheck(
                Locale.ENGLISH,
                SpreadsheetTimeParsePattern.parseTimeParsePattern("h:mm:ss AM/PM;h:mm:ss;h:mm:ss.0;h:mm AM/PM;h:mm")
                        .spreadsheetParserSelector()
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetMetadataPropertyNameParserTime.instance(),
                "time-parser"
        );
    }

    @Override
    SpreadsheetMetadataPropertyNameParserTime createName() {
        return SpreadsheetMetadataPropertyNameParserTime.instance();
    }

    @Override
    SpreadsheetParserSelector propertyValue() {
        return SpreadsheetTimeParsePattern.parseTimeParsePattern("hhmmss \"pattern-1\";hhmmss \"pattern-2\"")
                .spreadsheetParserSelector();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameParserTime> type() {
        return SpreadsheetMetadataPropertyNameParserTime.class;
    }
}

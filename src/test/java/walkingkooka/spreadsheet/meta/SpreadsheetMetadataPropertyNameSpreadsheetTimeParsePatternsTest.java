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
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTimeParsePatterns;

import java.util.Locale;

public final class SpreadsheetMetadataPropertyNameSpreadsheetTimeParsePatternsTest extends SpreadsheetMetadataPropertyNameTestCase<SpreadsheetMetadataPropertyNameSpreadsheetTimeParsePatterns, SpreadsheetTimeParsePatterns> {

    @Test
    public void testExtractLocaleValue() {
        this.extractLocaleValueAndCheck(Locale.ENGLISH,
                SpreadsheetTimeParsePatterns.parseTimeParsePatterns("H:mm:ss AM/PM;H:mm:ss AM/PM;H:mm:ss AM/PM;H:mm AM/PM"));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetMetadataPropertyNameSpreadsheetTimeParsePatterns.instance(), "time-parse-patterns");
    }

    @Override
    SpreadsheetMetadataPropertyNameSpreadsheetTimeParsePatterns createName() {
        return SpreadsheetMetadataPropertyNameSpreadsheetTimeParsePatterns.instance();
    }

    @Override
    SpreadsheetTimeParsePatterns propertyValue() {
        return SpreadsheetTimeParsePatterns.parseTimeParsePatterns("hhmmss \"pattern-1\";hhmmss \"pattern-2\"");
    }

    @Override
    String propertyValueType() {
        return "Time parse patterns";
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameSpreadsheetTimeParsePatterns> type() {
        return SpreadsheetMetadataPropertyNameSpreadsheetTimeParsePatterns.class;
    }
}

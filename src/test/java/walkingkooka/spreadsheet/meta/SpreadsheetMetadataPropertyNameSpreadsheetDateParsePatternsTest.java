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

import java.util.Locale;

public final class SpreadsheetMetadataPropertyNameSpreadsheetDateParsePatternsTest extends SpreadsheetMetadataPropertyNameTestCase<SpreadsheetMetadataPropertyNameSpreadsheetDateParsePatterns, SpreadsheetDateParsePatterns> {

    @Test
    public void testExtractLocaleValue() {
        this.extractLocaleValueAndCheck(Locale.ENGLISH, null);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetMetadataPropertyNameSpreadsheetDateParsePatterns.instance(), "date-parse-patterns");
    }

    @Override
    SpreadsheetMetadataPropertyNameSpreadsheetDateParsePatterns createName() {
        return SpreadsheetMetadataPropertyNameSpreadsheetDateParsePatterns.instance();
    }

    @Override
    SpreadsheetDateParsePatterns propertyValue() {
        return SpreadsheetDateParsePatterns.parseDateParsePatterns("dd mm yyyy \"custom\"");
    }

    @Override
    String propertyValueType() {
        return "Date parse patterns";
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameSpreadsheetDateParsePatterns> type() {
        return SpreadsheetMetadataPropertyNameSpreadsheetDateParsePatterns.class;
    }
}

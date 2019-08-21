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

import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateTimeParsePatterns;

public final class SpreadsheetMetadataPropertyValueHandlerSpreadsheetDateTimeParsePatternsTest extends SpreadsheetMetadataPropertyValueHandlerSpreadsheetPatternTestCase<
        SpreadsheetMetadataPropertyValueHandlerSpreadsheetDateTimeParsePatterns,
        SpreadsheetDateTimeParsePatterns> {

    @Override
    SpreadsheetMetadataPropertyValueHandlerSpreadsheetDateTimeParsePatterns handler() {
        return SpreadsheetMetadataPropertyValueHandlerSpreadsheetDateTimeParsePatterns.INSTANCE;
    }

    @Override
    SpreadsheetMetadataPropertyName<SpreadsheetDateTimeParsePatterns> propertyName() {
        return SpreadsheetMetadataPropertyName.DATETIME_PARSE_PATTERNS;
    }

    @Override
    SpreadsheetDateTimeParsePatterns propertyValue() {
        return SpreadsheetDateTimeParsePatterns.parseDateTimeParsePatterns("ddmmyyyyhhmmss \"pattern-1\";yyyymmddhhmmss \"pattern-2\"");
    }

    @Override
    public Class<SpreadsheetMetadataPropertyValueHandlerSpreadsheetDateTimeParsePatterns> type() {
        return SpreadsheetMetadataPropertyValueHandlerSpreadsheetDateTimeParsePatterns.class;
    }
}

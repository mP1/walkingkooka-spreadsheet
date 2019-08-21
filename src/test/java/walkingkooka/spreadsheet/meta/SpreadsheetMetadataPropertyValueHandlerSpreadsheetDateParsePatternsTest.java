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

import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateParsePatterns;

public final class SpreadsheetMetadataPropertyValueHandlerSpreadsheetDateParsePatternsTest extends SpreadsheetMetadataPropertyValueHandlerSpreadsheetPatternTestCase<
        SpreadsheetMetadataPropertyValueHandlerSpreadsheetDateParsePatterns,
        SpreadsheetDateParsePatterns> {

    @Override
    SpreadsheetMetadataPropertyValueHandlerSpreadsheetDateParsePatterns handler() {
        return SpreadsheetMetadataPropertyValueHandlerSpreadsheetDateParsePatterns.INSTANCE;
    }

    @Override
    SpreadsheetMetadataPropertyName<SpreadsheetDateParsePatterns> propertyName() {
        return SpreadsheetMetadataPropertyName.DATE_PARSE_PATTERNS;
    }

    @Override
    SpreadsheetDateParsePatterns propertyValue() {
        return SpreadsheetDateParsePatterns.parseDateParsePatterns("ddmmyyyy \"pattern-1\";yyyymmdd \"pattern-2\"");
    }

    @Override
    public Class<SpreadsheetMetadataPropertyValueHandlerSpreadsheetDateParsePatterns> type() {
        return SpreadsheetMetadataPropertyValueHandlerSpreadsheetDateParsePatterns.class;
    }
}

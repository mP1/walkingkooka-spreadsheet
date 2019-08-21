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

import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateFormatPattern;

public final class SpreadsheetMetadataPropertyValueHandlerSpreadsheetDateFormatPatternTest extends SpreadsheetMetadataPropertyValueHandlerSpreadsheetPatternTestCase<
        SpreadsheetMetadataPropertyValueHandlerSpreadsheetDateFormatPattern,
        SpreadsheetDateFormatPattern> {

    @Override
    SpreadsheetMetadataPropertyValueHandlerSpreadsheetDateFormatPattern handler() {
        return SpreadsheetMetadataPropertyValueHandlerSpreadsheetDateFormatPattern.INSTANCE;
    }

    @Override
    SpreadsheetMetadataPropertyName<SpreadsheetDateFormatPattern> propertyName() {
        return SpreadsheetMetadataPropertyName.DATE_FORMAT_PATTERN;
    }

    @Override
    SpreadsheetDateFormatPattern propertyValue() {
        return SpreadsheetDateFormatPattern.parseDateFormatPattern("dd mm yyyy \"custom\"");
    }

    @Override
    public Class<SpreadsheetMetadataPropertyValueHandlerSpreadsheetDateFormatPattern> type() {
        return SpreadsheetMetadataPropertyValueHandlerSpreadsheetDateFormatPattern.class;
    }
}

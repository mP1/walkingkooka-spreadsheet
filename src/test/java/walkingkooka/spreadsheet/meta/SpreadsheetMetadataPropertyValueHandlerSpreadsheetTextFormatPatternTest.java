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

import walkingkooka.spreadsheet.format.pattern.SpreadsheetTextFormatPattern;

public final class SpreadsheetMetadataPropertyValueHandlerSpreadsheetTextFormatPatternTest extends SpreadsheetMetadataPropertyValueHandlerSpreadsheetPatternTestCase<
        SpreadsheetMetadataPropertyValueHandlerSpreadsheetTextFormatPattern,
        SpreadsheetTextFormatPattern> {

    @Override
    SpreadsheetMetadataPropertyValueHandlerSpreadsheetTextFormatPattern handler() {
        return SpreadsheetMetadataPropertyValueHandlerSpreadsheetTextFormatPattern.INSTANCE;
    }

    @Override
    SpreadsheetMetadataPropertyName<SpreadsheetTextFormatPattern> propertyName() {
        return SpreadsheetMetadataPropertyName.TEXT_FORMAT_PATTERN;
    }

    @Override
    SpreadsheetTextFormatPattern propertyValue() {
        return SpreadsheetTextFormatPattern.parseTextFormatPattern("@ \"text-literal 123\"");
    }

    @Override
    public Class<SpreadsheetMetadataPropertyValueHandlerSpreadsheetTextFormatPattern> type() {
        return SpreadsheetMetadataPropertyValueHandlerSpreadsheetTextFormatPattern.class;
    }
}

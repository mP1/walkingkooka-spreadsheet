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

import walkingkooka.spreadsheet.format.pattern.SpreadsheetNumberParsePatterns;

public final class SpreadsheetMetadataPropertyValueHandlerSpreadsheetNumberParsePatternsTest extends SpreadsheetMetadataPropertyValueHandlerSpreadsheetPatternTestCase<
        SpreadsheetMetadataPropertyValueHandlerSpreadsheetNumberParsePatterns,
        SpreadsheetNumberParsePatterns> {

    @Override
    SpreadsheetMetadataPropertyValueHandlerSpreadsheetNumberParsePatterns handler() {
        return SpreadsheetMetadataPropertyValueHandlerSpreadsheetNumberParsePatterns.INSTANCE;
    }

    @Override
    SpreadsheetMetadataPropertyName<SpreadsheetNumberParsePatterns> propertyName() {
        return SpreadsheetMetadataPropertyName.NUMBER_PARSE_PATTERNS;
    }

    @Override
    SpreadsheetNumberParsePatterns propertyValue() {
        return SpreadsheetNumberParsePatterns.parseNumberParsePatterns("#.## \"pattern-1\";#.00 \"pattern-2\"");
    }

    @Override
    public Class<SpreadsheetMetadataPropertyValueHandlerSpreadsheetNumberParsePatterns> type() {
        return SpreadsheetMetadataPropertyValueHandlerSpreadsheetNumberParsePatterns.class;
    }
}

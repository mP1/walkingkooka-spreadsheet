
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


import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.parser.SpreadsheetParserInfo;
import walkingkooka.spreadsheet.parser.SpreadsheetParserInfoSet;
import walkingkooka.spreadsheet.parser.SpreadsheetParserName;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProviders;

public final class SpreadsheetMetadataPropertyNamePluginSpreadsheetParsersTest extends SpreadsheetMetadataPropertyNamePluginTestCase<SpreadsheetMetadataPropertyNamePluginSpreadsheetParsers, SpreadsheetParserInfoSet, SpreadsheetParserInfo, SpreadsheetParserName> {

    @Override
    SpreadsheetMetadataPropertyNamePluginSpreadsheetParsers createName() {
        return SpreadsheetMetadataPropertyNamePluginSpreadsheetParsers.instance();
    }

    @Override
    SpreadsheetParserInfoSet propertyValue() {
        return SpreadsheetParserProviders.spreadsheetParsePattern(
                SpreadsheetFormatterProviders.spreadsheetFormatPattern()
        ).spreadsheetParserInfos();
    }

    @Override
    String propertyValueType() {
        return SpreadsheetParserInfoSet.class.getSimpleName();
    }

    @Override
    public Class<SpreadsheetMetadataPropertyNamePluginSpreadsheetParsers> type() {
        return SpreadsheetMetadataPropertyNamePluginSpreadsheetParsers.class;
    }
}

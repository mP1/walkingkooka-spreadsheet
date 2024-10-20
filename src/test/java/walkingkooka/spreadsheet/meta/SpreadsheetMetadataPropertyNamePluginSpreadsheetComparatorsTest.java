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

import walkingkooka.spreadsheet.compare.SpreadsheetComparatorAlias;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorAliasSet;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorInfo;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorInfoSet;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorName;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorSelector;

public final class SpreadsheetMetadataPropertyNamePluginSpreadsheetComparatorsTest extends SpreadsheetMetadataPropertyNamePluginTestCase<
        SpreadsheetMetadataPropertyNamePluginSpreadsheetComparators,
        SpreadsheetComparatorName,
        SpreadsheetComparatorInfo,
        SpreadsheetComparatorInfoSet,
        SpreadsheetComparatorSelector,
        SpreadsheetComparatorAlias,
        SpreadsheetComparatorAliasSet> {

    @Override
    SpreadsheetMetadataPropertyNamePluginSpreadsheetComparators createName() {
        return SpreadsheetMetadataPropertyNamePluginSpreadsheetComparators.instance();
    }

    @Override
    SpreadsheetComparatorInfoSet propertyValue() {
        return SpreadsheetComparatorProviders.spreadsheetComparators()
                .spreadsheetComparatorInfos();
    }

    @Override
    String propertyValueType() {
        return SpreadsheetComparatorInfoSet.class.getSimpleName();
    }

    @Override
    public Class<SpreadsheetMetadataPropertyNamePluginSpreadsheetComparators> type() {
        return SpreadsheetMetadataPropertyNamePluginSpreadsheetComparators.class;
    }
}

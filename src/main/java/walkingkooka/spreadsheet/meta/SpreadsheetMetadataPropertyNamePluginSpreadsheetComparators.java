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

import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorInfo;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorInfoSet;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorName;

/**
 * This {@link SpreadsheetMetadataPropertyName} holds a {@link SpreadsheetComparatorInfoSet}.
 */
final class SpreadsheetMetadataPropertyNamePluginSpreadsheetComparators extends SpreadsheetMetadataPropertyNamePlugin<SpreadsheetComparatorInfoSet, SpreadsheetComparatorInfo, SpreadsheetComparatorName> {

    static {
        SpreadsheetComparatorInfoSet.with(Sets.empty()); // for registry of json marshaller
    }

    /**
     * Factory, the constant on {@link SpreadsheetMetadataPropertyName} will hold the singleton.
     */
    static SpreadsheetMetadataPropertyNamePluginSpreadsheetComparators instance() {
        return new SpreadsheetMetadataPropertyNamePluginSpreadsheetComparators();
    }

    /**
     * Private ctor use getter.
     */
    private SpreadsheetMetadataPropertyNamePluginSpreadsheetComparators() {
        super("spreadsheet-comparators");
    }

    @Override
    void accept(final SpreadsheetComparatorInfoSet value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitSpreadsheetComparators(value);
    }

    @Override
    SpreadsheetComparatorInfoSet parseUrlFragmentSaveValue0(final String value) {
        return SpreadsheetComparatorInfoSet.parse(value);
    }

    @Override
    Class<SpreadsheetComparatorInfoSet> type() {
        return SpreadsheetComparatorInfoSet.class;
    }
}

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
import walkingkooka.spreadsheet.format.SpreadsheetParserInfo;
import walkingkooka.spreadsheet.format.SpreadsheetParserInfoSet;
import walkingkooka.spreadsheet.format.SpreadsheetParserName;

/**
 * This {@link SpreadsheetMetadataPropertyName} holds a {@link SpreadsheetParserInfoSet}.
 */
final class SpreadsheetMetadataPropertyNamePluginSpreadsheetParsers extends SpreadsheetMetadataPropertyNamePlugin<SpreadsheetParserInfoSet, SpreadsheetParserInfo, SpreadsheetParserName> {

    static {
        SpreadsheetParserInfoSet.with(Sets.empty()); // for registry of json marshaller
    }

    /**
     * Factory, the constant on {@link SpreadsheetMetadataPropertyName} will hold the singleton.
     */
    static SpreadsheetMetadataPropertyNamePluginSpreadsheetParsers instance() {
        return new SpreadsheetMetadataPropertyNamePluginSpreadsheetParsers();
    }

    /**
     * Private ctor use getter.
     */
    private SpreadsheetMetadataPropertyNamePluginSpreadsheetParsers() {
        super("spreadsheet-parsers");
    }

    @Override
    void accept(final SpreadsheetParserInfoSet value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitSpreadsheetParsers(value);
    }

    @Override
    SpreadsheetParserInfoSet parseUrlFragmentSaveValue0(final String value) {
        return SpreadsheetParserInfoSet.parse(value);
    }

    @Override
    Class<SpreadsheetParserInfoSet> type() {
        return SpreadsheetParserInfoSet.class;
    }
}

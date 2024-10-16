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
import walkingkooka.spreadsheet.parser.SpreadsheetParserAlias;
import walkingkooka.spreadsheet.parser.SpreadsheetParserAliasSet;
import walkingkooka.spreadsheet.parser.SpreadsheetParserInfo;
import walkingkooka.spreadsheet.parser.SpreadsheetParserInfoSet;
import walkingkooka.spreadsheet.parser.SpreadsheetParserName;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;

/**
 * This {@link SpreadsheetMetadataPropertyName} holds a {@link SpreadsheetParserInfoSet}.
 */
final class SpreadsheetMetadataPropertyNamePluginSpreadsheetParsers extends SpreadsheetMetadataPropertyNamePlugin<SpreadsheetParserName, SpreadsheetParserInfo, SpreadsheetParserInfoSet, SpreadsheetParserSelector, SpreadsheetParserAlias, SpreadsheetParserAliasSet> {

    static {
        SpreadsheetParserInfoSet.with(Sets.empty()); // force registry of json marshaller
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
        super("parsers");
    }

    @Override
    void accept(final SpreadsheetParserInfoSet value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitParsers(value);
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

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
import walkingkooka.spreadsheet.format.SpreadsheetFormatterInfo;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterInfoSet;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterName;

/**
 * This {@link SpreadsheetMetadataPropertyName} holds a {@link SpreadsheetFormatterInfoSet}.
 */
final class SpreadsheetMetadataPropertyNamePluginSpreadsheetFormatters extends SpreadsheetMetadataPropertyNamePlugin<SpreadsheetFormatterName, SpreadsheetFormatterInfo, SpreadsheetFormatterInfoSet> {

    static {
        SpreadsheetFormatterInfoSet.with(Sets.empty()); // force registry of json marshaller
    }

    /**
     * Factory, the constant on {@link SpreadsheetMetadataPropertyName} will hold the singleton.
     */
    static SpreadsheetMetadataPropertyNamePluginSpreadsheetFormatters instance() {
        return new SpreadsheetMetadataPropertyNamePluginSpreadsheetFormatters();
    }

    /**
     * Private ctor use getter.
     */
    private SpreadsheetMetadataPropertyNamePluginSpreadsheetFormatters() {
        super("formatters");
    }

    @Override
    void accept(final SpreadsheetFormatterInfoSet value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitFormatters(value);
    }

    @Override
    SpreadsheetFormatterInfoSet parseUrlFragmentSaveValue0(final String value) {
        return SpreadsheetFormatterInfoSet.parse(value);
    }

    @Override
    Class<SpreadsheetFormatterInfoSet> type() {
        return SpreadsheetFormatterInfoSet.class;
    }
}

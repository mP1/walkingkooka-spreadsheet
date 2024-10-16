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
import walkingkooka.spreadsheet.export.SpreadsheetExporterAlias;
import walkingkooka.spreadsheet.export.SpreadsheetExporterAliasSet;
import walkingkooka.spreadsheet.export.SpreadsheetExporterInfo;
import walkingkooka.spreadsheet.export.SpreadsheetExporterInfoSet;
import walkingkooka.spreadsheet.export.SpreadsheetExporterName;
import walkingkooka.spreadsheet.export.SpreadsheetExporterSelector;

/**
 * This {@link SpreadsheetMetadataPropertyName} holds a {@link SpreadsheetExporterInfoSet}.
 */
final class SpreadsheetMetadataPropertyNamePluginSpreadsheetExporters extends SpreadsheetMetadataPropertyNamePlugin<SpreadsheetExporterName, SpreadsheetExporterInfo, SpreadsheetExporterInfoSet, SpreadsheetExporterSelector, SpreadsheetExporterAlias, SpreadsheetExporterAliasSet> {

    static {
        SpreadsheetExporterInfoSet.with(Sets.empty()); // force registry of json marshaller
    }

    /**
     * Factory, the constant on {@link SpreadsheetMetadataPropertyName} will hold the singleton.
     */
    static SpreadsheetMetadataPropertyNamePluginSpreadsheetExporters instance() {
        return new SpreadsheetMetadataPropertyNamePluginSpreadsheetExporters();
    }

    /**
     * Private ctor use getter.
     */
    private SpreadsheetMetadataPropertyNamePluginSpreadsheetExporters() {
        super("exporters");
    }

    @Override
    void accept(final SpreadsheetExporterInfoSet value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitExporters(value);
    }

    @Override
    SpreadsheetExporterInfoSet parseUrlFragmentSaveValue0(final String value) {
        return SpreadsheetExporterInfoSet.parse(value);
    }

    @Override
    Class<SpreadsheetExporterInfoSet> type() {
        return SpreadsheetExporterInfoSet.class;
    }
}

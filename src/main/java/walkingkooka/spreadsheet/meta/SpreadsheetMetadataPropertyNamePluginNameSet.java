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

import walkingkooka.locale.LocaleContext;
import walkingkooka.plugin.PluginNameSet;

import java.util.Optional;

/**
 * Base class for any property that holds a {@link PluginNameSet}.
 */
final class SpreadsheetMetadataPropertyNamePluginNameSet extends SpreadsheetMetadataPropertyName<PluginNameSet> {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNamePluginNameSet instance() {
        return new SpreadsheetMetadataPropertyNamePluginNameSet();
    }

    /**
     * Package private
     */
    private SpreadsheetMetadataPropertyNamePluginNameSet() {
        super("plugins");
    }

    @Override
    PluginNameSet checkValueNonNull(final Object value) {
        return this.checkValueType(
            value,
            v -> v instanceof PluginNameSet
        );
    }

    @Override
    String expected() {
        return PluginNameSet.class.getSimpleName();
    }

    @Override
    Optional<PluginNameSet> extractLocaleAwareValue(final LocaleContext context) {
        return Optional.empty();
    }

    @Override
    public Class<PluginNameSet> type() {
        return PluginNameSet.class;
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override
    PluginNameSet parseUrlFragmentSaveValueNonNull(final String text) {
        return PluginNameSet.parse(text);
    }

    @Override
    void accept(final PluginNameSet value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitPlugins(value);
    }
}

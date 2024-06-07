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

import walkingkooka.naming.Name;
import walkingkooka.plugin.PluginInfoLike;

/**
 * Base class for a few {@link SpreadsheetMetadataPropertyName} that have a {@link PluginInfoLike} as the value.
 */
abstract class SpreadsheetMetadataPropertyNamePluginInfo<I extends PluginInfoLike<I, N>, N extends Name & Comparable<N>> extends SpreadsheetMetadataPropertyName<I> {

    SpreadsheetMetadataPropertyNamePluginInfo() {
        super();
    }

    @Override
    final String compareToName() {
        return this.value();
    }

    /**
     * The parse value is the PLUGIN-NAME SPACE ABSOLUTE_URL.
     */
    @Override
    public final boolean isParseValueSupported() {
        return true;
    }
}

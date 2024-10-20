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
import walkingkooka.plugin.PluginAliasLike;
import walkingkooka.plugin.PluginAliasSetLike;
import walkingkooka.plugin.PluginInfoLike;
import walkingkooka.plugin.PluginInfoSetLike;
import walkingkooka.plugin.PluginSelectorLike;

import java.util.Locale;
import java.util.Optional;

/**
 * Base class for a few {@link SpreadsheetMetadataPropertyName} that have a {@link PluginInfoSetLike} as the value.
 */
abstract class SpreadsheetMetadataPropertyNamePlugin<N extends Name & Comparable<N>,
        I extends PluginInfoLike<I, N>,
        IS extends PluginInfoSetLike<N, I, IS, S, A, AS>,
        S extends PluginSelectorLike<N>,
        A extends PluginAliasLike<N, S, A>,
        AS extends PluginAliasSetLike<N, I, IS, S, A, AS>> extends SpreadsheetMetadataPropertyName<IS> {

    SpreadsheetMetadataPropertyNamePlugin(final String name) {
        super(name);
    }

    @Override
    final IS checkValue0(final Object value) {
        return this.checkValueType(
                value,
                i -> i.getClass().equals(this.type())
        );
    }

    @Override
    final String expected() {
        return this.type().getSimpleName();
    }

    @Override
    final Optional<IS> extractLocaleAwareValue(final Locale locale) {
        return Optional.empty();
    }
}

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

package walkingkooka.spreadsheet.convert;

import walkingkooka.Either;
import walkingkooka.plugin.PluginSelectorLike;

/**
 * A {@link walkingkooka.convert.Converter} that supports converting any {@link walkingkooka.plugin.PluginSelectorLike} to {@link String}.
 * <br>
 * This converter should only be used by Find/Highlighting queries
 */
final class SpreadsheetConverterPluginSelectorLikeToString extends SpreadsheetConverter {

    /**
     * Singleton
     */
    final static SpreadsheetConverterPluginSelectorLikeToString INSTANCE = new SpreadsheetConverterPluginSelectorLikeToString();

    private SpreadsheetConverterPluginSelectorLikeToString() {
        super();
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final SpreadsheetConverterContext context) {
        return value instanceof PluginSelectorLike &&
                String.class == type;
    }

    @Override
    <T> Either<T, String> convert0(final Object value,
                                   final Class<T> type,
                                   final SpreadsheetConverterContext context) {
        return this.successfulConversion(
                value.toString(),
                type
        );
    }

    @Override
    public String toString() {
        return "plugin-selector-like to String";
    }
}

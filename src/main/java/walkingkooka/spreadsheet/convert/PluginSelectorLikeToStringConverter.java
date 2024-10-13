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
import walkingkooka.convert.Converter;
import walkingkooka.plugin.PluginSelectorLike;

/**
 * A {@link walkingkooka.convert.Converter} that supports converting any {@link walkingkooka.plugin.PluginSelectorLike} to {@link String}.
 * <br>
 * This converter should only be used by Find/Highlighting queries
 */
final class PluginSelectorLikeToStringConverter implements Converter<SpreadsheetConverterContext> {

    /**
     * Singleton
     */
    final static PluginSelectorLikeToStringConverter INSTANCE = new PluginSelectorLikeToStringConverter();

    private PluginSelectorLikeToStringConverter() {
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
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type,
                                         final SpreadsheetConverterContext context) {
        return this.canConvert(
                value,
                type,
                context
        ) ?
                this.successfulConversion(
                        value.toString(),
                        type)
                :
                this.failConversion(
                        value,
                        type
                );
    }

    @Override
    public String toString() {
        return "plugin-selector-like to String";
    }
}

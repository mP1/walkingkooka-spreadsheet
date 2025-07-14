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

package walkingkooka.spreadsheet.format;

import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.HasConverter;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.tree.text.TextNode;

import java.util.List;
import java.util.Optional;

/**
 * A formatter is used to format a value into a {@link TextNode}.
 */
public interface SpreadsheetFormatter extends HasConverter<SpreadsheetConverterContext> {

    /**
     * Constant holding a failed format.
     */
    Optional<SpreadsheetText> EMPTY = Optional.empty();

    /**
     * Constant holding {@link SpreadsheetText} without color or text (aka empty {@link String}.
     */
    Optional<SpreadsheetText> NO_TEXT = Optional.of(
        SpreadsheetText.EMPTY
    );

    /**
     * Useful constant for {@link SpreadsheetFormatter} with no text components.
     */
    List<SpreadsheetFormatterSelectorToken> NO_TOKENS = Lists.empty();

    /**
     * Accepts a value and returns a {@link TextNode} if it could format the value.
     */
    Optional<TextNode> format(final Optional<Object> value,
                              final SpreadsheetFormatterContext context);

    /**
     * Formats the given {@link Object value} or returns {@link SpreadsheetText#EMPTY}.
     */
    default TextNode formatOrEmptyText(final Optional<Object> value,
                                       final SpreadsheetFormatterContext context) {
        return this.format(
            value,
            context
        ).orElse(TextNode.EMPTY_TEXT);
    }

    /**
     * {@see SpreadsheetFormatterConverter}
     */
    @Override
    default Converter<SpreadsheetConverterContext> converter() {
        return SpreadsheetFormatterConverter.with(this);
    }

    /**
     * Returns a list of {@link SpreadsheetFormatterSelectorToken} if this {@link SpreadsheetFormatter} supports
     * tokenizing its pattern. A {@link SpreadsheetFormatterContext} could be useful to provide the {@link java.util.Locale}
     * or {@link java.time.LocalDateTime now}  displaying day names in the label for an {@link SpreadsheetFormatterSelectorTokenAlternative}.
     */
    List<SpreadsheetFormatterSelectorToken> tokens(final SpreadsheetFormatterContext context);
}

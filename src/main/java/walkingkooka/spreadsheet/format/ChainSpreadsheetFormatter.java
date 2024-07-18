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
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.tree.text.TextNode;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A {@link SpreadsheetFormatter} that forms a chain trying one or many {@link SpreadsheetFormatter formatters}.
 */
final class ChainSpreadsheetFormatter implements SpreadsheetFormatter {

    /**
     * Creates a new {@link ChainSpreadsheetFormatter} as necessary.
     */
    static SpreadsheetFormatter with(final List<SpreadsheetFormatter> formatters) {
        Objects.requireNonNull(formatters, "formatters");

        final SpreadsheetFormatter result;

        final List<SpreadsheetFormatter> copy = Lists.immutable(formatters);
        switch (copy.size()) {
            case 0:
                throw new IllegalArgumentException("Formatters empty");
            case 1:
                result = copy.iterator().next();
                break;
            default:
                result = new ChainSpreadsheetFormatter(copy);
                break;
        }

        return result;
    }

    /**
     * Private ctor.
     */
    private ChainSpreadsheetFormatter(final List<SpreadsheetFormatter> formatters) {
        super();
        this.formatters = formatters;
    }

    // SpreadsheetFormatter.............................................................................................

    @Override
    public Optional<TextNode> format(final Object value,
                                     final SpreadsheetFormatterContext context) {
        return this.formatters.stream()
                .flatMap(f -> optionalStream(f.format(value, context)))
                .findFirst();
    }

    // TODO Missing GWT JRE Optional#stream
    private Stream<TextNode> optionalStream(final Optional<TextNode> optional) {
        return optional.map(v -> Stream.of(v))
                .orElse(Stream.of());
    }

    final List<SpreadsheetFormatter> formatters;

    @Override
    public List<SpreadsheetFormatterSelectorTextComponent> textComponents(final SpreadsheetFormatterContext context) {
        Objects.requireNonNull(context, "context");

        return SpreadsheetFormatter.NO_TEXT_COMPONENTS;
    }

    @Override
    public Optional<SpreadsheetFormatterSelectorTextComponent> nextTextComponent(final int index,
                                                                                 final SpreadsheetFormatterContext context) {
        if(0 != index) {
            throw new IndexOutOfBoundsException("Invalid index " + index);
        }
        Objects.requireNonNull(context, "context");

        return NO_NEXT_TEXT_COMPONENT;
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return SpreadsheetPattern.SEPARATOR.toSeparatedString(
                this.formatters,
                SpreadsheetFormatter::toString
        );
    }
}

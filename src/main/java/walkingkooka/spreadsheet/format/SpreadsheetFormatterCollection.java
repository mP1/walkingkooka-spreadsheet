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

import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelectorToken;
import walkingkooka.tree.text.TextNode;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A {@link SpreadsheetFormatter} that forms a collection trying each {@link SpreadsheetFormatter formatter} until success.
 */
final class SpreadsheetFormatterCollection implements SpreadsheetFormatter {

    /**
     * Creates a new {@link SpreadsheetFormatterCollection} as necessary.
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
                result = new SpreadsheetFormatterCollection(copy);
                break;
        }

        return result;
    }

    /**
     * Private ctor.
     */
    private SpreadsheetFormatterCollection(final List<SpreadsheetFormatter> formatters) {
        super();
        this.formatters = formatters;
    }

    // SpreadsheetFormatter.............................................................................................

    @Override
    public Optional<TextNode> format(final Optional<Object> value,
                                     final SpreadsheetFormatterContext context) {
        return this.formatters.stream()
            .flatMap(f -> optionalStream(
                    f.format(
                        value,
                        context
                    )
                )
            ).findFirst();
    }

    // TODO Missing GWT JRE Optional#stream
    private Stream<TextNode> optionalStream(final Optional<TextNode> optional) {
        return optional.map(Stream::of)
            .orElse(Stream.of());
    }

    final List<SpreadsheetFormatter> formatters;

    @Override
    public List<SpreadsheetFormatterSelectorToken> tokens(final SpreadsheetFormatterContext context) {
        Objects.requireNonNull(context, "context");

        return SpreadsheetFormatter.NO_TOKENS;
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.formatters.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetFormatterCollection &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetFormatterCollection other) {
        return this.formatters.equals(other.formatters);
    }

    @Override
    public String toString() {
        return SpreadsheetPattern.SEPARATOR.toSeparatedString(
            this.formatters,
            SpreadsheetFormatter::toString
        );
    }
}

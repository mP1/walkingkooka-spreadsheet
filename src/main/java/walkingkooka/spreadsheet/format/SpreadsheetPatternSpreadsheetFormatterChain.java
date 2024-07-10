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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A {@link SpreadsheetPatternSpreadsheetFormatter} that forms a chain of {@link SpreadsheetPatternSpreadsheetFormatter formatters}.
 * When a format request is made, each will be tried one by one until success.
 */
final class SpreadsheetPatternSpreadsheetFormatterChain implements SpreadsheetPatternSpreadsheetFormatter {

    /**
     * Creates a new {@link SpreadsheetPatternSpreadsheetFormatterChain} as necessary.
     */
    static SpreadsheetPatternSpreadsheetFormatter with(final List<SpreadsheetPatternSpreadsheetFormatter> formatters) {
        Objects.requireNonNull(formatters, "formatters");

        final SpreadsheetPatternSpreadsheetFormatter result;

        final List<SpreadsheetPatternSpreadsheetFormatter> copy = Lists.immutable(formatters);
        switch (copy.size()) {
            case 0:
                throw new IllegalArgumentException("Formatters empty");
            case 1:
                result = copy.iterator().next();
                break;
            default:
                result = new SpreadsheetPatternSpreadsheetFormatterChain(copy);
                break;
        }

        return result;
    }

    /**
     * Private ctor.
     */
    private SpreadsheetPatternSpreadsheetFormatterChain(final List<SpreadsheetPatternSpreadsheetFormatter> formatters) {
        super();
        this.formatters = formatters;
    }

    // SpreadsheetFormatter.............................................................................................

    @Override
    public Optional<SpreadsheetText> formatSpreadsheetText(final Object value,
                                                           final SpreadsheetFormatterContext context) {
        return this.formatters.stream()
                .flatMap(f -> optionalStream(
                                f.formatSpreadsheetText(value, context)
                        )
                ).findFirst();
    }

    // TODO Missing GWT JRE Optional#stream
    private Stream<SpreadsheetText> optionalStream(final Optional<SpreadsheetText> optional) {
        return optional.map(v -> Stream.of(v))
                .orElse(Stream.of());
    }

    @Override
    public Optional<List<SpreadsheetFormatterSelectorTextComponent>> textComponents(final SpreadsheetFormatterContext context) {
        throw new UnsupportedOperationException();
    }

    final List<SpreadsheetPatternSpreadsheetFormatter> formatters;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.formatters.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetPatternSpreadsheetFormatterChain && this.equals0((SpreadsheetPatternSpreadsheetFormatterChain) other);
    }

    private boolean equals0(final SpreadsheetPatternSpreadsheetFormatterChain other) {
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

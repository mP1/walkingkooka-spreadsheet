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
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelectorToken;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A {@link SpreadsheetPatternSpreadsheetFormatter} that forms a chain of {@link SpreadsheetPatternSpreadsheetFormatter formatters}.
 * When a format request is made, each will be tried one by one until success.
 */
final class SpreadsheetPatternSpreadsheetFormatterCollection implements SpreadsheetPatternSpreadsheetFormatter {

    /**
     * Creates a new {@link SpreadsheetPatternSpreadsheetFormatterCollection} as necessary.
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
                result = new SpreadsheetPatternSpreadsheetFormatterCollection(copy);
                break;
        }

        return result;
    }

    /**
     * Private ctor.
     */
    private SpreadsheetPatternSpreadsheetFormatterCollection(final List<SpreadsheetPatternSpreadsheetFormatter> formatters) {
        super();
        this.formatters = formatters;
    }

    // SpreadsheetFormatter.............................................................................................

    @Override
    public Optional<SpreadsheetText> formatSpreadsheetText(final Optional<Object> value,
                                                           final SpreadsheetFormatterContext context) {
        return this.formatters.stream()
            .flatMap(f -> optionalStream(
                    f.formatSpreadsheetText(
                        value,
                        context
                    )
                )
            ).findFirst();
    }

    /**
     * Necessary because GWT's JRE {@link Optional#stream()} is not implemented so an equivalent is provided here.
     */
    // TODO Missing GWT JRE Optional#stream
    private static Stream<SpreadsheetText> optionalStream(final Optional<SpreadsheetText> optional) {
        return optional.map(Stream::of)
            .orElse(Stream.of());
    }

    final List<SpreadsheetPatternSpreadsheetFormatter> formatters;

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
            other instanceof SpreadsheetPatternSpreadsheetFormatterCollection && this.equals0((SpreadsheetPatternSpreadsheetFormatterCollection) other);
    }

    private boolean equals0(final SpreadsheetPatternSpreadsheetFormatterCollection other) {
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

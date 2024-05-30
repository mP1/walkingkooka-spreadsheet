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
    public boolean canFormat(final Object value,
                             final SpreadsheetFormatterContext context) {
        return this.formatter(value, context).isPresent();
    }

    @Override
    public Optional<SpreadsheetText> formatSpreadsheetText(final Object value,
                                                           final SpreadsheetFormatterContext context) {
        return this.formatter(value, context)
                .flatMap(f -> f.formatSpreadsheetText(
                                value,
                                context
                        )
                );
    }

    private Optional<SpreadsheetPatternSpreadsheetFormatter> formatter(final Object value,
                                                                       final SpreadsheetFormatterContext context) {
        return this.formatters.stream()
                .filter(f -> f.canFormat(value, context))
                .findFirst();
    }

    final List<SpreadsheetPatternSpreadsheetFormatter> formatters;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return SpreadsheetPattern.SEPARATOR.toSeparatedString(
                this.formatters,
                SpreadsheetFormatter::toString
        );
    }
}

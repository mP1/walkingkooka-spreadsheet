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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
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
        switch(copy.size()) {
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
    public boolean canFormat(final Object value,
                             final SpreadsheetFormatterContext context) {
        return this.formatter(value, context).isPresent();
    }

    @Override
    public Optional<SpreadsheetText> format(final Object value,
                                            final SpreadsheetFormatterContext context) throws SpreadsheetFormatException {
        return this.formatter(value, context)
                .flatMap(f -> f.format(value, context));
    }

    Optional<SpreadsheetFormatter> formatter(final Object value, final SpreadsheetFormatterContext context) throws SpreadsheetFormatException {
        return this.formatters.stream()
                .filter(f -> f.canFormat(value, context))
                .findFirst();
    }

    @Override
    public SpreadsheetFormatter then(final SpreadsheetFormatter next) {
        Objects.requireNonNull(next);
        return this.equals(next) || this.formatters.contains(next) ?
                this :
                this.append(next);
    }

    /**
     * Unconditionally creates a new {@link ChainSpreadsheetFormatter}
     */
    private ChainSpreadsheetFormatter append(final SpreadsheetFormatter next) {
        return new ChainSpreadsheetFormatter(Stream.concat(this.formatters.stream(), Stream.of(next)).collect(Collectors.toList()));
    }

    final List<SpreadsheetFormatter> formatters;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.formatters.toString();
    }
}

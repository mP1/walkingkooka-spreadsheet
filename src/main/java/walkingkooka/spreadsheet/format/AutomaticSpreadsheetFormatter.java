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
import walkingkooka.tree.text.TextNode;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * This {@link SpreadsheetFormatter} selects one of its given {@link SpreadsheetFormatter} using the type of the value.
 */
final class AutomaticSpreadsheetFormatter implements SpreadsheetFormatter {

    static AutomaticSpreadsheetFormatter with(final SpreadsheetFormatter date,
                                              final SpreadsheetFormatter dateTime,
                                              final SpreadsheetFormatter error,
                                              final SpreadsheetFormatter number,
                                              final SpreadsheetFormatter text,
                                              final SpreadsheetFormatter time) {
        return new AutomaticSpreadsheetFormatter(
            Objects.requireNonNull(date, "date"),
            Objects.requireNonNull(dateTime, "dateTime"),
            Objects.requireNonNull(error, "error"),
            Objects.requireNonNull(number, "number"),
            Objects.requireNonNull(text, "text"),
            Objects.requireNonNull(time, "time")
        );
    }

    private AutomaticSpreadsheetFormatter(final SpreadsheetFormatter date,
                                          final SpreadsheetFormatter dateTime,
                                          final SpreadsheetFormatter error,
                                          final SpreadsheetFormatter number,
                                          final SpreadsheetFormatter text,
                                          final SpreadsheetFormatter time) {
        this.date = date;
        this.dateTime = dateTime;
        this.error = error;
        this.number = number;
        this.text = text;
        this.time = time;
    }

    @Override
    public Optional<TextNode> format(final Optional<Object> value,
                                     final SpreadsheetFormatterContext context) {
        final SpreadsheetFormatter formatter = SpreadsheetMetadataFormattersSpreadsheetFormatterSpreadsheetValueVisitor.select(
            this,
            value.orElse(null)
        );
        // if the formatter didnt work format with text
        return or(
            formatter.format(
                value,
                context
            ),
            () -> this.text.format(
                value,
                context
            )
        );
    }

    // Missing GWT JRE method Optional#or(Supplier)
    private Optional<TextNode> or(final Optional<TextNode> value,
                                  final Supplier<Optional<TextNode>> or) {
        return value.isPresent() ?
            value :
            or.get();
    }

    final SpreadsheetFormatter date;
    final SpreadsheetFormatter dateTime;
    final SpreadsheetFormatter error;
    final SpreadsheetFormatter number;
    final SpreadsheetFormatter text;
    final SpreadsheetFormatter time;

    @Override
    public List<SpreadsheetFormatterSelectorToken> tokens(final SpreadsheetFormatterContext context) {
        Objects.requireNonNull(context, "context");

        return NO_TOKENS;
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.date,
            this.dateTime,
            this.error,
            this.number,
            this.text,
            this.time
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof AutomaticSpreadsheetFormatter &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final AutomaticSpreadsheetFormatter other) {
        return this.date.equals(other.date) &&
            this.dateTime.equals(other.dateTime) &&
            this.error.equals(other.error) &&
            this.number.equals(other.number) &&
            this.text.equals(other.text) &&
            this.time.equals(other.time);
    }

    @Override
    public String toString() {
        return this.date + " | " + this.dateTime + " | " + this.error + " | " + this.number + " | " + this.text + " | " + this.time;
    }
}

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
import walkingkooka.datetime.DateFormatKind;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelectorToken;
import walkingkooka.tree.text.TextNode;
import walkingkooka.util.HasLocale;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetFormatter} that wraps a {@link DateFormat} using the provided {@link DateFormatKind#dateFormatStyle()}
 * and formats {@link java.time.LocalDate} values.
 */
final class SpreadsheetFormatterSharedDate extends SpreadsheetFormatterShared {

    static SpreadsheetFormatterSharedDate with(final DateFormatKind kind) {
        return new SpreadsheetFormatterSharedDate(
            Objects.requireNonNull(kind, "kind")
        );
    }

    private SpreadsheetFormatterSharedDate(final DateFormatKind kind) {
        super();
        this.kind = kind;
    }

    @Override
    public Optional<TextNode> format(final Optional<Object> value,
                                     final SpreadsheetFormatterContext context) {
        return this.date(
            context
        ).format(
            value,
            context
        );
    }

    @Override
    public List<SpreadsheetFormatterSelectorToken> tokens(final SpreadsheetFormatterContext context) {
        Objects.requireNonNull(context, "context");

        return Lists.empty(); // NONE!
    }

    private SpreadsheetFormatter date(final HasLocale context) {
        return SpreadsheetPattern.dateParsePattern(
            (SimpleDateFormat) DateFormat.getDateInstance(
                this.kind.dateFormatStyle(),
                context.locale()
            )
        ).formatter();
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.kind.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other || other instanceof SpreadsheetFormatterSharedDate &&
            this.equals0(
                (SpreadsheetFormatterSharedDate) other
            );
    }

    private boolean equals0(final SpreadsheetFormatterSharedDate other) {
        return this.kind.equals(other.kind);
    }

    @Override
    public String toString() {
        return "date-" + this.kind.name()
            .toLowerCase()
            .toLowerCase();
    }

    private final DateFormatKind kind;
}

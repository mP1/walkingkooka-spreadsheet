
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
 * A {@link SpreadsheetFormatter} that wraps a {@link DateFormat} using the provided {@link DateFormatKind#dateFormatStyle()}.
 */
final class SpreadsheetFormatterSharedDateTime extends SpreadsheetFormatterShared {

    static SpreadsheetFormatterSharedDateTime with(final DateFormatKind kind) {
        return new SpreadsheetFormatterSharedDateTime(
            Objects.requireNonNull(kind, "kind")
        );
    }

    private SpreadsheetFormatterSharedDateTime(final DateFormatKind kind) {
        super();
        this.kind = kind;
    }

    @Override
    public Optional<TextNode> format(final Optional<Object> value,
                                     final SpreadsheetFormatterContext context) {
        return this.formatter(
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

    private SpreadsheetFormatter formatter(final HasLocale context) {
        final int style = this.kind.dateFormatStyle();

        return SpreadsheetPattern.dateTimeParsePattern(
            (SimpleDateFormat) DateFormat.getDateTimeInstance(
                style,
                style,
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
        return this == other || other instanceof SpreadsheetFormatterSharedDateTime &&
            this.equals0(
                (SpreadsheetFormatterSharedDateTime) other
            );
    }

    private boolean equals0(final SpreadsheetFormatterSharedDateTime other) {
        return this.kind.equals(other.kind);
    }

    @Override
    public String toString() {
        return "dateTime-" + this.kind.name()
            .toLowerCase();
    }

    private final DateFormatKind kind;
}

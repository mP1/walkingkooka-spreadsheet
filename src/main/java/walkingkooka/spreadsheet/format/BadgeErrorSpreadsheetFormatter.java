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
import walkingkooka.Either;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelectorToken;
import walkingkooka.spreadsheet.value.SpreadsheetError;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.text.Badge;
import walkingkooka.tree.text.TextNode;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetFormatter} that outputs a {@link Badge} with the error text, and uses the
 * given {@link SpreadsheetFormatter} to format the {@link SpreadsheetError}.
 */
final class BadgeErrorSpreadsheetFormatter implements SpreadsheetFormatter {

    /**
     * Creates a new {@link BadgeErrorSpreadsheetFormatter}.
     */
    static BadgeErrorSpreadsheetFormatter with(final SpreadsheetFormatter formatter) {
        return new BadgeErrorSpreadsheetFormatter(
            Objects.requireNonNull(formatter, "formatter")
        );
    }

    private BadgeErrorSpreadsheetFormatter(final SpreadsheetFormatter formatter) {
        super();
        this.formatter = formatter;
    }

    @Override
    public Optional<TextNode> format(final Optional<Object> value,
                                     final SpreadsheetFormatterContext context) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(context, "context");

        final Object valueOrNull = value.orElse(null);

        final Either<SpreadsheetError, String> spreadsheetError = null != valueOrNull ?
            context.convert(
                valueOrNull,
                SpreadsheetError.class
            ) :
            null;

        return Optional.ofNullable(
            null != spreadsheetError && spreadsheetError.isLeft() ?
                this.formatSpreadsheetError(
                    spreadsheetError.leftValue(),
                    context
                ) :
                null
        );
    }

    private TextNode formatSpreadsheetError(final SpreadsheetError error,
                                            final SpreadsheetFormatterContext context) {
        String text = error.text();

        String badgeText = error.message();
        if (CharSequences.isNullOrEmpty(badgeText)) {
            badgeText = text;
        }

        return TextNode.badge(
            badgeText
        ).appendChild(
            this.formatter.format(
                Optional.of(text),
                context
            ).orElse(TextNode.EMPTY_TEXT)
        );
    }

    @Override
    public List<SpreadsheetFormatterSelectorToken> tokens(final SpreadsheetFormatterContext context) {
        Objects.requireNonNull(context, "context");

        return NO_TOKENS;
    }

    private final SpreadsheetFormatter formatter;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.formatter.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof BadgeErrorSpreadsheetFormatter &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final BadgeErrorSpreadsheetFormatter other) {
        return this.formatter.equals(other.formatter);
    }

    @Override
    public String toString() {
        return "badge-error " + this.formatter;
    }
}

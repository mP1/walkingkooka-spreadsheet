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

package walkingkooka.spreadsheet.format.pattern;

import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.parser.TextSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelectorToken;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetPatternSpreadsheetFormatter} that formats values after converting them to a {@link String}.
 * Note that null {@link String} values are supported but any placeholders in the pattern will be skipped.
 */
final class SpreadsheetPatternSpreadsheetFormatterText implements SpreadsheetPatternSpreadsheetFormatter {

    /**
     * Creates a {@link SpreadsheetPatternSpreadsheetFormatterText} parse a {@link TextSpreadsheetFormatParserToken}.
     */
    static SpreadsheetPatternSpreadsheetFormatterText with(final TextSpreadsheetFormatParserToken token) {
        Objects.requireNonNull(token, "token");

        return new SpreadsheetPatternSpreadsheetFormatterText(token);
    }

    /**
     * Private ctor use static parse.
     */
    private SpreadsheetPatternSpreadsheetFormatterText(final TextSpreadsheetFormatParserToken token) {
        super();

        this.token = token;
    }

    @Override
    public Optional<SpreadsheetText> formatSpreadsheetText(final Optional<Object> value,
                                                           final SpreadsheetFormatterContext context) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(context, "context");

        final Object valueOrNull = value.orElse(null);

        return Optional.ofNullable(
            context.canConvert(
                valueOrNull,
                String.class
            ) ?
                SpreadsheetPatternSpreadsheetFormatterTextSpreadsheetFormatParserTokenVisitor.format(
                    this.token,
                    context.convertOrFail(
                        valueOrNull,
                        String.class
                    ),
                    context
                ) :
                null
        );
    }

    @Override
    public List<SpreadsheetFormatterSelectorToken> tokens(final SpreadsheetFormatterContext context) {
        Objects.requireNonNull(context, "context");

        return SpreadsheetFormatterSelectorToken.tokens(this.token);
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.token.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetPatternSpreadsheetFormatterText && this.equals0((SpreadsheetPatternSpreadsheetFormatterText) other);
    }

    private boolean equals0(final SpreadsheetPatternSpreadsheetFormatterText other) {
        return this.token.equals(other.token);
    }

    @Override
    public String toString() {
        return this.token.text();
    }

    private final TextSpreadsheetFormatParserToken token;
}

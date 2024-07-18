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

import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextParserToken;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetPatternSpreadsheetFormatter} that formats values after converting them to a {@link String}.
 */
final class SpreadsheetPatternSpreadsheetFormatterText implements SpreadsheetPatternSpreadsheetFormatter {

    /**
     * Creates a {@link SpreadsheetPatternSpreadsheetFormatterText} parse a {@link SpreadsheetFormatTextParserToken}.
     */
    static SpreadsheetPatternSpreadsheetFormatterText with(final SpreadsheetFormatTextParserToken token) {
        Objects.requireNonNull(token, "token");

        return new SpreadsheetPatternSpreadsheetFormatterText(token);
    }

    /**
     * Private ctor use static parse.
     */
    private SpreadsheetPatternSpreadsheetFormatterText(final SpreadsheetFormatTextParserToken token) {
        super();

        this.token = token;
    }

    @Override
    public Optional<SpreadsheetText> formatSpreadsheetText(final Object value,
                                                           final SpreadsheetFormatterContext context) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(context, "context");

        return Optional.ofNullable(
                context.canConvert(value, String.class) ?
                        SpreadsheetPatternSpreadsheetFormatterTextSpreadsheetFormatParserTokenVisitor.format(
                                this.token,
                                context.convertOrFail(value, String.class),
                                context
                        ) :
                        null
        );
    }

    @Override
    public List<SpreadsheetFormatterSelectorTextComponent> textComponents(final SpreadsheetFormatterContext context) {
        return SpreadsheetFormatterSelectorTextComponent.textComponents(
                this.token,
                context
        );
    }

    @Override
    public Optional<SpreadsheetFormatterSelectorTextComponent> nextTextComponent(final SpreadsheetFormatterContext context) {
        Objects.requireNonNull(context, "context");
        throw new UnsupportedOperationException();
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

    private final SpreadsheetFormatTextParserToken token;
}

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
    public boolean canFormat(final Object value,
                             final SpreadsheetFormatterContext context) {
        return context.canConvert(value, String.class);
    }

    @Override
    public Optional<SpreadsheetText> formatSpreadsheetText(final Object value,
                                                           final SpreadsheetFormatterContext context) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(context, "context");
        
        return this.canFormat(value, context) ?
                Optional.of(
                        SpreadsheetPatternSpreadsheetFormatterTextSpreadsheetFormatParserTokenVisitor.format(
                                this.token,
                                context.convertOrFail(value, String.class),
                                context
                        )
                ) :
                Optional.empty();
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.token.text();
    }

    private final SpreadsheetFormatTextParserToken token;
}

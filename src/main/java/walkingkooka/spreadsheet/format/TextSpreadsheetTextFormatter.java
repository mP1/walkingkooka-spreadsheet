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

import java.util.Optional;

/**
 * A {@link SpreadsheetTextFormatter} that formats a {@link String}.
 */
final class TextSpreadsheetTextFormatter extends SpreadsheetTextFormatter3<String, SpreadsheetFormatTextParserToken> {

    /**
     * Creates a {@link TextSpreadsheetTextFormatter} from a {@link SpreadsheetFormatTextParserToken}.
     */
    static TextSpreadsheetTextFormatter with(final SpreadsheetFormatTextParserToken token) {
        check(token);
        return new TextSpreadsheetTextFormatter(token);
    }

    /**
     * Private ctor use static parse.
     */
    private TextSpreadsheetTextFormatter(final SpreadsheetFormatTextParserToken token) {
        super(token);
    }

    @Override
    public Class<String> type() {
        return String.class;
    }

    @Override
    Optional<SpreadsheetFormattedText> format0(final String value, final SpreadsheetTextFormatContext context) {
        return Optional.of(TextSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitor.format(this.token, value, context));
    }

    @Override
    String toStringSuffix() {
        return "";
    }
}

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

import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;

import java.util.Objects;

/**
 * Base class for all {@link SpreadsheetFormatter} implementations that use a {@link walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern}.
 * This will become important when there are formatters that originate based on a pattern and others that are custom formatters with their own formatting logic.
 */
abstract public class SpreadsheetPatternSpreadsheetFormatter<T extends SpreadsheetFormatParserToken> extends SpreadsheetFormatter2 {

    static SpreadsheetFormatter checkFormatter(final SpreadsheetFormatter formatter) {
        return Objects.requireNonNull(formatter, "formatter");
    }

    static SpreadsheetFormatParserToken checkParserToken(final SpreadsheetFormatParserToken token) {
        return Objects.requireNonNull(token, "token");
    }

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetPatternSpreadsheetFormatter(final T token) {
        super();

        this.token = token;
    }

    /**
     * Returns the original pattern.
     */
    @Override
    public final String toString() {
        return this.token.text() + this.toStringSuffix();
    }

    final T token;

    abstract String toStringSuffix();
}

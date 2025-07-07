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

package walkingkooka.spreadsheet.format.parser;

import walkingkooka.text.cursor.parser.ParserToken;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * A type safe {@link Predicate} that expects a {@link SpreadsheetFormatParserToken} which is only called if the given
 * {@link ParserToken} is a {@link SpreadsheetFormatParserToken}.
 */
final class SpreadsheetFormatParserTokenPredicate implements Predicate<ParserToken> {

    static SpreadsheetFormatParserTokenPredicate with(final Predicate<SpreadsheetFormatParserToken> predicate) {
        Objects.requireNonNull(predicate, "predicate");

        return new SpreadsheetFormatParserTokenPredicate(predicate);
    }

    private SpreadsheetFormatParserTokenPredicate(final Predicate<SpreadsheetFormatParserToken> predicate) {
        super();
        this.predicate = predicate;
    }

    @Override
    public boolean test(final ParserToken token) {
        return token instanceof SpreadsheetFormatParserToken &&
            this.predicate.test(token.cast(SpreadsheetFormatParserToken.class));
    }

    private final Predicate<SpreadsheetFormatParserToken> predicate;

    @Override
    public String toString() {
        return this.predicate.toString();
    }
}

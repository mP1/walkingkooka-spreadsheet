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

import walkingkooka.Value;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Base class for a leaf token. A leaf has no further breakdown into more detailed tokens.
 */
abstract class SpreadsheetFormatLeafParserToken<T> extends SpreadsheetFormatParserToken implements Value<T> {

    static void checkValueAndText(final Object value, final String text) {
        checkValue(value);
        checkTextNotEmptyOrWhitespace(text);
    }

    static <T> T checkValue(final T value) {
        return Objects.requireNonNull(value, "value");
    }

    SpreadsheetFormatLeafParserToken(final T value, final String text) {
        super(text);
        this.value = value;
    }

    // value............................................................................................................

    @Override
    public final T value() {
        return this.value;
    }

    final T value;

    // children.........................................................................................................

    @Override
    public final ParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.leafSetChildren(
                this,
                children
        );
    }

    // removeIf....................................................................................................

    @Override
    public final ParserToken removeIf(final Predicate<ParserToken> predicate) {
        Objects.requireNonNull(predicate, "predicate");
        return this;
    }
}

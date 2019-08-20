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

import walkingkooka.Cast;
import walkingkooka.Value;
import walkingkooka.convert.HasConverter;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.HasParser;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserException;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.json.HasJsonNode;

import java.util.Objects;
import java.util.function.Function;

/**
 * Holds a tokens that may be used to parse or format values along with helpers.
 */
abstract public class SpreadsheetPatterns<V> implements HashCodeEqualsDefined,
        HasConverter,
        HasJsonNode,
        HasParser<ParserContext>,
        Value<V> {

    /**
     * Parses text using the given parser and transformer.
     */
    static <P extends SpreadsheetPatterns> P parsePattern(final String text,
                                                          final Parser<SpreadsheetFormatParserContext> parser,
                                                          final Function<ParserToken, P> transformer) {
        Objects.requireNonNull(text, "text");

        try {
            return parser.parse(TextCursors.charSequence(text), SpreadsheetFormatParserContexts.basic())
                    .map(transformer)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid pattern " + CharSequences.quoteAndEscape(text)));
        } catch (final ParserException cause) {
            throw new IllegalArgumentException(cause.getMessage(), cause);
        }
    }

    // ctor.............................................................................................................

    /**
     * Package private ctor use factory
     */
    SpreadsheetPatterns(final V value) {
        super();
        this.value = value;
    }

    // Value............................................................................................................

    @Override
    public final V value() {
        return this.value;
    }

    final V value;

    // isXXX............................................................................................................

    /**
     * Returns true if holding date pattern(s)
     */
    public abstract boolean isDate();

    /**
     * Returns true if holding date/time pattern(s)
     */
    public abstract boolean isDateTime();

    /**
     * Returns true if holding number pattern(s)
     */
    public abstract boolean isNumber();

    /**
     * Returns true if holding time pattern(s)
     */
    public abstract boolean isTime();

    // HashCodeEqualsDefined............................................................................................

    @Override
    public final int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public final boolean equals(final Object other) {
        return this == other ||
                this.canBeEquals(other) &&
                        this.equals0(Cast.to(other));
    }

    abstract boolean canBeEquals(final Object other);

    private boolean equals0(final SpreadsheetPatterns other) {
        return this.value.equals(other.value);
    }
}

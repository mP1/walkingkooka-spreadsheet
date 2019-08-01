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

import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatBigDecimalParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;

import java.util.List;
import java.util.Objects;

/**
 * Holds a valid {@link SpreadsheetTextFormatterNumberPatterns}.
 */
public final class SpreadsheetTextFormatterNumberPatterns extends SpreadsheetTextFormatterDateTimeOrNumberPatterns<SpreadsheetFormatBigDecimalParserToken> {
    /**
     * Creates a new {@link SpreadsheetTextFormatterNumberPatterns} after checking the value is valid.
     */
    public static SpreadsheetTextFormatterNumberPatterns parse(final String text) {
        return parse0(text,
                PARSER,
                SpreadsheetTextFormatterNumberPatterns::transform);
    }

    private final static Parser<SpreadsheetFormatParserContext> PARSER = parser(SpreadsheetFormatParsers.bigDecimal().cast());

    /**
     * Transforms the tokens into a {@link SpreadsheetTextFormatterNumberPatterns}
     */
    private static SpreadsheetTextFormatterNumberPatterns transform(final ParserToken token) {
        return with(SpreadsheetTextFormatterNumberPatternsSpreadsheetFormatParserTokenVisitor.collect(token));
    }

    /**
     * Factory that creates a {@link SpreadsheetTextFormatterNumberPatterns} from the given tokens.
     */
    public static SpreadsheetTextFormatterNumberPatterns with(final List<SpreadsheetFormatBigDecimalParserToken> value) {
       return new SpreadsheetTextFormatterNumberPatterns(copyAndNotEmptyCheck(value));
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetTextFormatterNumberPatterns(final List<SpreadsheetFormatBigDecimalParserToken> value) {
        super(value);
    }

    // HashCodeEqualsDefined............................................................................................

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof SpreadsheetTextFormatterNumberPatterns;
    }

    // HasJsonNode......................................................................................................

    /**
     * Factory that creates a {@link SpreadsheetTextFormatterNumberPatterns} from a {@link JsonNode}.
     */
    static SpreadsheetTextFormatterNumberPatterns fromJsonNode(final JsonNode node) {
        Objects.requireNonNull(node, "node");

        return SpreadsheetTextFormatterNumberPatterns.parse(node.stringValueOrFail());
    }

    static {
        HasJsonNode.register("spreadsheet-text-formatter-number-pattern",
                SpreadsheetTextFormatterNumberPatterns::fromJsonNode,
                SpreadsheetTextFormatterNumberPatterns.class);
    }
}

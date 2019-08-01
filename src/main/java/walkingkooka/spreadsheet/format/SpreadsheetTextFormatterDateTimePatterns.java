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

import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;

import java.util.List;
import java.util.Objects;

/**
 * Holds a valid {@link SpreadsheetTextFormatterDateTimePatterns}.
 */
public final class SpreadsheetTextFormatterDateTimePatterns extends SpreadsheetTextFormatterDateTimeOrNumberPatterns<SpreadsheetFormatDateTimeParserToken> {
    /**
     * Creates a new {@link SpreadsheetTextFormatterDateTimePatterns} after checking the value is valid.
     */
    public static SpreadsheetTextFormatterDateTimePatterns parse(final String text) {
        return parse0(text,
                PARSER,
                SpreadsheetTextFormatterDateTimePatterns::transform);
    }

    private final static Parser<SpreadsheetFormatParserContext> PARSER = parser(SpreadsheetFormatParsers.dateTime().cast());

    /**
     * Transforms the tokens into a {@link SpreadsheetTextFormatterDateTimePatterns}
     */
    private static SpreadsheetTextFormatterDateTimePatterns transform(final ParserToken token) {
        return with(SpreadsheetTextFormatterDateTimePatternsSpreadsheetFormatParserTokenVisitor.collect(token));
    }

    /**
     * Factory that creates a {@link SpreadsheetTextFormatterDateTimePatterns} from the given tokens.
     */
    public static SpreadsheetTextFormatterDateTimePatterns with(final List<SpreadsheetFormatDateTimeParserToken> value) {
        return new SpreadsheetTextFormatterDateTimePatterns(copyAndNotEmptyCheck(value));
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetTextFormatterDateTimePatterns(final List<SpreadsheetFormatDateTimeParserToken> value) {
        super(value);
    }

    // HashCodeEqualsDefined............................................................................................

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof SpreadsheetTextFormatterDateTimePatterns;
    }

    // HasJsonNode......................................................................................................

    /**
     * Factory that creates a {@link SpreadsheetTextFormatterDateTimePatterns} from a {@link JsonNode}.
     */
    static SpreadsheetTextFormatterDateTimePatterns fromJsonNode(final JsonNode node) {
        Objects.requireNonNull(node, "node");

        return SpreadsheetTextFormatterDateTimePatterns.parse(node.stringValueOrFail());
    }

    static {
        HasJsonNode.register("spreadsheet-text-formatter-datetime-pattern",
                SpreadsheetTextFormatterDateTimePatterns::fromJsonNode,
                SpreadsheetTextFormatterDateTimePatterns.class);
    }
}

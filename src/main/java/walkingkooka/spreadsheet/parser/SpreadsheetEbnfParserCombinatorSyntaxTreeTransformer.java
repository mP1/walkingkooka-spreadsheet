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

package walkingkooka.spreadsheet.parser;

import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.RepeatedOrSequenceParserToken;
import walkingkooka.text.cursor.parser.SequenceParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfAlternativeParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfConcatenationParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfExceptionParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfGroupParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfIdentifierName;
import walkingkooka.text.cursor.parser.ebnf.EbnfIdentifierParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfOptionalParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfRangeParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfRepeatedParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfTerminalParserToken;
import walkingkooka.text.cursor.parser.ebnf.combinator.EbnfParserCombinatorSyntaxTreeTransformer;

import java.util.List;
import java.util.function.BiFunction;

/**
 * A {@link EbnfParserCombinatorSyntaxTreeTransformer} that only transforms terminal and ranges into their corresponding {@link SpreadsheetParserToken} equivalents.
 * Processing of other tokens will be done after this process completes.
 */
final class SpreadsheetEbnfParserCombinatorSyntaxTreeTransformer implements EbnfParserCombinatorSyntaxTreeTransformer {

    /**
     * Singleton
     */
    final static SpreadsheetEbnfParserCombinatorSyntaxTreeTransformer INSTANCE = new SpreadsheetEbnfParserCombinatorSyntaxTreeTransformer();

    /**
     * Private ctor use singleton.
     */
    private SpreadsheetEbnfParserCombinatorSyntaxTreeTransformer() {
        super();
    }

    @Override
    public Parser<ParserContext> alternatives(final EbnfAlternativeParserToken token,
                                              final Parser<ParserContext> parser) {
        return parser;
    }

    @Override
    public Parser<ParserContext> concatenation(final EbnfConcatenationParserToken token,
                                               final Parser<ParserContext> parser) {
        return parser.transform(this::concatenation);
    }

    /**
     * Special case for binary operators and operator priorities.
     *
     * <pre></pre>
     * (* addition, subtraction, multiplication, division, power, range *)
     * BINARY_OPERATOR         = "+" | "-" | "*" | "/" | "^" | ":";
     * BINARY_EXPRESSION       = EXPRESSION2, [ WHITESPACE ], BINARY_OPERATOR, [ WHITESPACE ], EXPRESSION2;
     * </pre>
     */
    private ParserToken concatenation(final ParserToken token,
                                      final ParserContext context) {
        return token.cast(SequenceParserToken.class)
                .transform(SpreadsheetEbnfParserCombinatorSyntaxTreeTransformerBinaryOperatorTransformer.INSTANCE);
    }

    @Override
    public Parser<ParserContext> exception(final EbnfExceptionParserToken token,
                                           final Parser<ParserContext> parser) {
        return parser;
    }

    @Override
    public Parser<ParserContext> group(final EbnfGroupParserToken token,
                                       final Parser<ParserContext> parser) {
        return parser;
    }

    /**
     * For identified rules, invokes the transformer or calls required check as a default.
     */
    @Override
    public Parser<ParserContext> identifier(final EbnfIdentifierParserToken token,
                                            final Parser<ParserContext> parser) {
        final EbnfIdentifierName name = token.value();
        final BiFunction<ParserToken, ParserContext, ParserToken> transformer;

        switch (name.value()) {

            case "EXPRESSION":
                transformer = SpreadsheetEbnfParserCombinatorSyntaxTreeTransformer::expression;
                break;
            case "FUNCTION":
                transformer = SpreadsheetEbnfParserCombinatorSyntaxTreeTransformer::function;
                break;
            case "GROUP":
                transformer = SpreadsheetEbnfParserCombinatorSyntaxTreeTransformer::group;
                break;
            case "NEGATIVE":
                transformer = SpreadsheetEbnfParserCombinatorSyntaxTreeTransformer::negative;
                break;
            case "PERCENTAGE":
                transformer = SpreadsheetEbnfParserCombinatorSyntaxTreeTransformer::percentage;
                break;
            default:
                transformer = null;
                break;
        }

        return null != transformer ?
                parser.transform(transformer) :
                name.value().endsWith("REQUIRED") ?
                        parser.orReport(ParserReporters.basic()) :
                        parser;
    }

    /**
     * If the expression had a leading or trailing whitespace it will appear as a {@link SequenceParserToken},
     * otherwise the token will be a {@link SpreadsheetParserToken}. If the former, wrap all tokens in the
     * {@link SequenceParserToken} in a {@link SpreadsheetGroupParserToken}.
     */
    private static ParserToken expression(final ParserToken token,
                                          final ParserContext context) {
        return token instanceof SpreadsheetParserToken ?
                token :
                SpreadsheetParserToken.group(
                        token.cast(SequenceParserToken.class).value(),
                        token.text()
                );
    }

    private static ParserToken function(final ParserToken token,
                                        final ParserContext context) {
        return SpreadsheetParserToken.function(clean(token), token.text());
    }

    private static ParserToken group(final ParserToken token,
                                     final ParserContext context) {
        return SpreadsheetParserToken.group(clean(token), token.text());
    }

    private static ParserToken negative(final ParserToken token,
                                        final ParserContext context) {
        return SpreadsheetParserToken.negative(clean(token), token.text());
    }

    private static ParserToken percentage(final ParserToken token,
                                          final ParserContext context) {
        return SpreadsheetParserToken.percentage(clean(token), token.text());
    }

    private static List<ParserToken> clean(final ParserToken token) {
        return token.cast(RepeatedOrSequenceParserToken.class)
                .flat()
                .value();
    }

    @Override
    public Parser<ParserContext> optional(final EbnfOptionalParserToken token,
                                          final Parser<ParserContext> parser) {
        return parser;
    }

    /**
     * Accepts the bounds tokens and creates a {@link SpreadsheetRangeParserToken}
     */
    @Override
    public Parser<ParserContext> range(final EbnfRangeParserToken token,
                                       final Parser<ParserContext> parser) {
        return parser.transform(SpreadsheetEbnfParserCombinatorSyntaxTreeTransformer::transformRange);
    }

    private static ParserToken transformRange(final ParserToken token,
                                              final ParserContext context) {
        return SpreadsheetParserToken.range(((SequenceParserToken) token).value(), token.text());
    }

    @Override
    public Parser<ParserContext> repeated(final EbnfRepeatedParserToken token,
                                          final Parser<ParserContext> parser) {
        return parser;
    }

    @Override
    public Parser<ParserContext> terminal(final EbnfTerminalParserToken token,
                                          final Parser<ParserContext> parser) {
        throw new UnsupportedOperationException(token.toString());
    }
}

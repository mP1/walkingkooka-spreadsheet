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
final class SpreadsheetParsersEbnfParserCombinatorSyntaxTreeTransformer implements EbnfParserCombinatorSyntaxTreeTransformer<SpreadsheetParserContext> {

    /**
     * Singleton
     */
    final static SpreadsheetParsersEbnfParserCombinatorSyntaxTreeTransformer INSTANCE = new SpreadsheetParsersEbnfParserCombinatorSyntaxTreeTransformer();

    /**
     * Private ctor use singleton.
     */
    private SpreadsheetParsersEbnfParserCombinatorSyntaxTreeTransformer() {
        super();
    }

    @Override
    public Parser<SpreadsheetParserContext> alternatives(final EbnfAlternativeParserToken token,
                                                         final Parser<SpreadsheetParserContext> parser) {
        return parser;
    }

    @Override
    public Parser<SpreadsheetParserContext> concatenation(final EbnfConcatenationParserToken token,
                                                          final Parser<SpreadsheetParserContext> parser) {
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
                                      final SpreadsheetParserContext context) {
        return token.cast(SequenceParserToken.class)
                .binaryOperator(SpreadsheetParsersEbnfParserCombinatorSyntaxTreeTransformerBinaryOperatorTransformer.INSTANCE);
    }

    @Override
    public Parser<SpreadsheetParserContext> exception(final EbnfExceptionParserToken token,
                                                      final Parser<SpreadsheetParserContext> parser) {
        return parser;
    }

    @Override
    public Parser<SpreadsheetParserContext> group(final EbnfGroupParserToken token,
                                                  final Parser<SpreadsheetParserContext> parser) {
        return parser;
    }

    /**
     * For identified rules, invokes the transformer or calls required check as a default.
     */
    @Override
    public Parser<SpreadsheetParserContext> identifier(final EbnfIdentifierParserToken token,
                                                       final Parser<SpreadsheetParserContext> parser) {
        final EbnfIdentifierName name = token.value();
        final BiFunction<ParserToken, SpreadsheetParserContext, ParserToken> transformer;

        switch (name.value()) {
            case "APOSTROPHE_STRING":
                transformer = SpreadsheetParsersEbnfParserCombinatorSyntaxTreeTransformer::apostropheString;
                break;
            case "EXPRESSION":
                transformer = SpreadsheetParsersEbnfParserCombinatorSyntaxTreeTransformer::expression;
                break;
            case "FUNCTION_PARAMETERS":
                transformer = SpreadsheetParsersEbnfParserCombinatorSyntaxTreeTransformer::functionParameters;
                break;
            case "GROUP":
                transformer = SpreadsheetParsersEbnfParserCombinatorSyntaxTreeTransformer::group;
                break;
            case "LAMBDA_FUNCTION":
                transformer = SpreadsheetParsersEbnfParserCombinatorSyntaxTreeTransformer::lambdaFunction;
                break;
            case "NAMED_FUNCTION":
                transformer = SpreadsheetParsersEbnfParserCombinatorSyntaxTreeTransformer::namedFunction;
                break;
            case "NEGATIVE":
                transformer = SpreadsheetParsersEbnfParserCombinatorSyntaxTreeTransformer::negative;
                break;
            case "PERCENTAGE":
                transformer = SpreadsheetParsersEbnfParserCombinatorSyntaxTreeTransformer::percentage;
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

    private static ParserToken apostropheString(final ParserToken token,
                                                final SpreadsheetParserContext context) {
        return flat(
                token,
                SpreadsheetParserToken::text
        );
    }

    /**
     * If the expression had a leading or trailing whitespace it will appear as a {@link SequenceParserToken},
     * otherwise the token will be a {@link SpreadsheetParserToken}. If the former, wrap all tokens in the
     * {@link SequenceParserToken} in a {@link SpreadsheetGroupParserToken}.
     */
    private static ParserToken expression(final ParserToken token,
                                          final SpreadsheetParserContext context) {
        return token instanceof SpreadsheetParserToken ?
                token :
                SpreadsheetParserToken.group(
                        token.cast(SequenceParserToken.class).value(),
                        token.text()
                );
    }

    private static ParserToken functionParameters(final ParserToken token,
                                                  final SpreadsheetParserContext context) {
        return flat(
                token,
                SpreadsheetParserToken::functionParameters
        );
    }

    private static ParserToken group(final ParserToken token,
                                     final SpreadsheetParserContext context) {
        return flat(
                token,
                SpreadsheetParserToken::group
        );
    }

    private static ParserToken lambdaFunction(final ParserToken token,
                                              final SpreadsheetParserContext context) {
        return flat(
                token,
                SpreadsheetParserToken::lambdaFunction
        );
    }

    private static ParserToken namedFunction(final ParserToken token,
                                             final SpreadsheetParserContext context) {
        return flat(
                token,
                SpreadsheetParserToken::namedFunction
        );
    }

    private static ParserToken negative(final ParserToken token,
                                        final SpreadsheetParserContext context) {
        return flat(
                token,
                SpreadsheetParserToken::negative
        );
    }

    private static ParserToken percentage(final ParserToken token,
                                          final SpreadsheetParserContext context) {
        return flat(
                token,
                SpreadsheetParserToken::number
        );
    }

    private static ParserToken flat(final ParserToken token,
                                    final BiFunction<List<ParserToken>, String, ParserToken> factory) {
        return factory.apply(
                flat0(token),
                token.text()
        );
    }

    private static List<ParserToken> flat0(final ParserToken token) {
        return token.cast(RepeatedOrSequenceParserToken.class)
                .flat()
                .value();
    }

    @Override
    public Parser<SpreadsheetParserContext> optional(final EbnfOptionalParserToken token,
                                                     final Parser<SpreadsheetParserContext> parser) {
        return parser;
    }

    /**
     * Accepts the bounds tokens and creates a {@link SpreadsheetCellRangeParserToken}
     */
    @Override
    public Parser<SpreadsheetParserContext> range(final EbnfRangeParserToken token,
                                                  final Parser<SpreadsheetParserContext> parser) {
        return parser.transform(SpreadsheetParsersEbnfParserCombinatorSyntaxTreeTransformer::transformRange);
    }

    private static ParserToken transformRange(final ParserToken token,
                                              final SpreadsheetParserContext context) {
        return SpreadsheetParserToken.cellRange(((SequenceParserToken) token).value(), token.text());
    }

    @Override
    public Parser<SpreadsheetParserContext> repeated(final EbnfRepeatedParserToken token,
                                                     final Parser<SpreadsheetParserContext> parser) {
        return parser;
    }

    @Override
    public Parser<SpreadsheetParserContext> terminal(final EbnfTerminalParserToken token,
                                                     final Parser<SpreadsheetParserContext> parser) {
        throw new UnsupportedOperationException(token.toString());
    }
}

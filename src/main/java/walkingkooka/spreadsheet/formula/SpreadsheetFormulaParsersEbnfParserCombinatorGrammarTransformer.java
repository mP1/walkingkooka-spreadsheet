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

package walkingkooka.spreadsheet.formula;

import walkingkooka.collect.map.Maps;
import walkingkooka.spreadsheet.formula.parser.GroupSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.RepeatedOrSequenceParserToken;
import walkingkooka.text.cursor.parser.SequenceParserToken;
import walkingkooka.text.cursor.parser.ebnf.AlternativeEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.ConcatenationEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfIdentifierName;
import walkingkooka.text.cursor.parser.ebnf.ExceptionEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.GroupEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.IdentifierEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.OptionalEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.RangeEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.RepeatedEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.RuleEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.TerminalEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.combinator.EbnfParserCombinatorGrammarTransformer;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * A {@link EbnfParserCombinatorGrammarTransformer} that only transforms terminal and ranges into their corresponding {@link SpreadsheetFormulaParserToken} equivalents.
 * Processing of other tokens will be done after this process completes.
 */
final class SpreadsheetFormulaParsersEbnfParserCombinatorGrammarTransformer implements EbnfParserCombinatorGrammarTransformer<SpreadsheetParserContext> {

    static SpreadsheetFormulaParsersEbnfParserCombinatorGrammarTransformer create() {
        return new SpreadsheetFormulaParsersEbnfParserCombinatorGrammarTransformer();
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetFormulaParsersEbnfParserCombinatorGrammarTransformer() {
        super();

        final Map<EbnfIdentifierName, BiFunction<ParserToken, SpreadsheetParserContext, ParserToken>> identifierToTransformer = Maps.sorted();

        identifierToTransformer.put(
            APOSTROPHE_STRING,
            SpreadsheetFormulaParsersEbnfParserCombinatorGrammarTransformer::transformApostropheString
        );
        identifierToTransformer.put(
            EXPRESSION,
            SpreadsheetFormulaParsersEbnfParserCombinatorGrammarTransformer::transformExpression
        );
        identifierToTransformer.put(
            FUNCTION_PARAMETERS,
            SpreadsheetFormulaParsersEbnfParserCombinatorGrammarTransformer::transformFunctionParameters
        );
        identifierToTransformer.put(
            GROUP,
            SpreadsheetFormulaParsersEbnfParserCombinatorGrammarTransformer::transformGroup
        );
        identifierToTransformer.put(
            LAMBDA_FUNCTION,
            SpreadsheetFormulaParsersEbnfParserCombinatorGrammarTransformer::transformLambdaFunction
        );
        identifierToTransformer.put(
            NAMED_FUNCTION,
            SpreadsheetFormulaParsersEbnfParserCombinatorGrammarTransformer::transformNamedFunction
        );
        identifierToTransformer.put(
            NEGATIVE,
            SpreadsheetFormulaParsersEbnfParserCombinatorGrammarTransformer::transformNegative
        );
        identifierToTransformer.put(
            PERCENTAGE,
            SpreadsheetFormulaParsersEbnfParserCombinatorGrammarTransformer::transformPercentage
        );

        this.identifierToTransformer = identifierToTransformer;
    }

    private Parser<SpreadsheetParserContext> transformIfNecessary(final EbnfIdentifierName name,
                                                                  final Parser<SpreadsheetParserContext> parser) {
        Parser<SpreadsheetParserContext> result = parser;

        final BiFunction<ParserToken, SpreadsheetParserContext, ParserToken> transformer = this.identifierToTransformer.remove(name);
        if (null != transformer) {
            result = parser.transform(transformer);
        }

        return name.value().endsWith("REQUIRED") ?
            result.orReport(ParserReporters.basic()) :
            result;
    }

    private final Map<EbnfIdentifierName, BiFunction<ParserToken, SpreadsheetParserContext, ParserToken>> identifierToTransformer;

    private static final EbnfIdentifierName APOSTROPHE_STRING = EbnfIdentifierName.with("APOSTROPHE_STRING");

    private static ParserToken transformApostropheString(final ParserToken token,
                                                         final SpreadsheetParserContext context) {
        return flat(
            token,
            SpreadsheetFormulaParserToken::text
        );
    }

    private static final EbnfIdentifierName EXPRESSION = EbnfIdentifierName.with("EXPRESSION");

    /**
     * If the expression had a leading or trailing whitespace it will appear as a {@link SequenceParserToken},
     * otherwise the token will be a {@link SpreadsheetFormulaParserToken}. If the former, wrap all tokens in the
     * {@link SequenceParserToken} in a {@link GroupSpreadsheetFormulaParserToken}.
     */
    private static ParserToken transformExpression(final ParserToken token,
                                                   final SpreadsheetParserContext context) {
        return token instanceof SpreadsheetFormulaParserToken ?
            token :
            SpreadsheetFormulaParserToken.expression(
                token.cast(SequenceParserToken.class).value(),
                token.text()
            );
    }

    private static final EbnfIdentifierName FUNCTION_PARAMETERS = EbnfIdentifierName.with("FUNCTION_PARAMETERS");

    private static ParserToken transformFunctionParameters(final ParserToken token,
                                                           final SpreadsheetParserContext context) {
        return flat(
            token,
            SpreadsheetFormulaParserToken::functionParameters
        );
    }

    private static final EbnfIdentifierName GROUP = EbnfIdentifierName.with("GROUP");

    private static ParserToken transformGroup(final ParserToken token,
                                              final SpreadsheetParserContext context) {
        return flat(
            token,
            SpreadsheetFormulaParserToken::group
        );
    }

    private static final EbnfIdentifierName LAMBDA_FUNCTION = EbnfIdentifierName.with("LAMBDA_FUNCTION");

    private static ParserToken transformLambdaFunction(final ParserToken token,
                                                       final SpreadsheetParserContext context) {
        return flat(
            token,
            SpreadsheetFormulaParserToken::lambdaFunction
        );
    }

    private static final EbnfIdentifierName NAMED_FUNCTION = EbnfIdentifierName.with("NAMED_FUNCTION");

    private static ParserToken transformNamedFunction(final ParserToken token,
                                                      final SpreadsheetParserContext context) {
        return flat(
            token,
            SpreadsheetFormulaParserToken::namedFunction
        );
    }

    private static final EbnfIdentifierName NEGATIVE = EbnfIdentifierName.with("NEGATIVE");

    private static ParserToken transformNegative(final ParserToken token,
                                                 final SpreadsheetParserContext context) {
        return flat(
            token,
            SpreadsheetFormulaParserToken::negative
        );
    }

    private static final EbnfIdentifierName PERCENTAGE = EbnfIdentifierName.with("PERCENTAGE");

    private static ParserToken transformPercentage(final ParserToken token,
                                                   final SpreadsheetParserContext context) {
        return flat(
            token,
            SpreadsheetFormulaParserToken::number
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

    // EbnfParserCombinator.............................................................................................

    @Override
    public Parser<SpreadsheetParserContext> alternatives(final AlternativeEbnfParserToken token,
                                                         final Parser<SpreadsheetParserContext> parser) {
        return parser;
    }

    @Override
    public Parser<SpreadsheetParserContext> concatenation(final ConcatenationEbnfParserToken token,
                                                          final Parser<SpreadsheetParserContext> parser) {
        return parser.transform(this::transformConcatenation);
    }

    /**
     * Special case for binary operators including handling of operator priorities.
     */
    private ParserToken transformConcatenation(final ParserToken token,
                                               final SpreadsheetParserContext context) {
        return token.cast(SequenceParserToken.class)
            .binaryOperator(SpreadsheetFormulaParsersEbnfParserCombinatorGrammarTransformerBinaryOperatorTransformer.INSTANCE);
    }

    @Override
    public Parser<SpreadsheetParserContext> exception(final ExceptionEbnfParserToken token,
                                                      final Parser<SpreadsheetParserContext> parser) {
        return parser;
    }

    @Override
    public Parser<SpreadsheetParserContext> group(final GroupEbnfParserToken token,
                                                  final Parser<SpreadsheetParserContext> parser) {
        return parser;
    }

    @Override
    public Parser<SpreadsheetParserContext> identifier(final IdentifierEbnfParserToken token,
                                                       final Parser<SpreadsheetParserContext> parser) {
        return this.transformIfNecessary(
            token.value(),
            parser
        );
    }

    @Override
    public Parser<SpreadsheetParserContext> optional(final OptionalEbnfParserToken token,
                                                     final Parser<SpreadsheetParserContext> parser) {
        return parser;
    }

    @Override
    public Parser<SpreadsheetParserContext> range(final RangeEbnfParserToken token,
                                                  final String beginText,
                                                  final String endText) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Parser<SpreadsheetParserContext> repeated(final RepeatedEbnfParserToken token,
                                                     final Parser<SpreadsheetParserContext> parser) {
        return parser;
    }

    @Override
    public Parser<SpreadsheetParserContext> rule(final RuleEbnfParserToken token,
                                                 final Parser<SpreadsheetParserContext> parser) {
        return this.transformIfNecessary(
            token.identifier()
                .value(),
            parser
        );
    }

    @Override
    public Parser<SpreadsheetParserContext> terminal(final TerminalEbnfParserToken token,
                                                     final Parser<SpreadsheetParserContext> parser) {
        throw new UnsupportedOperationException(token.toString());
    }
}

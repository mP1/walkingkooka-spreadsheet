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

import walkingkooka.collect.map.Maps;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.RepeatedOrSequenceParserToken;
import walkingkooka.text.cursor.parser.StringParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfAlternativeParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfConcatenationParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfExceptionParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfGroupParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfIdentifierName;
import walkingkooka.text.cursor.parser.ebnf.EbnfIdentifierParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfOptionalParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfRangeParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfRepeatedParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfRuleParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfTerminalParserToken;
import walkingkooka.text.cursor.parser.ebnf.combinator.EbnfParserCombinatorGrammarTransformer;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Helps transform the EBNF grammar into a {@link Parser} which will then return a {@link SpreadsheetFormatParserToken}
 */
final class SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer implements EbnfParserCombinatorGrammarTransformer<SpreadsheetFormatParserContext> {

    // constants must be init before singleton/ctor is run........................................................................

    private static ParserToken transformColorParserToken(final ParserToken token,
                                                         final SpreadsheetFormatParserContext context) {
        return SpreadsheetFormatParserToken.color(
                flat(token),
                token.text()
        );
    }

    private static ParserToken transformConditionEqual(final ParserToken token,
                                                       final SpreadsheetFormatParserContext context) {
        return flatAndCreate(
                token,
                SpreadsheetFormatParserToken::equalsParserToken
        );
    }

    private static final EbnfIdentifierName CONDITION_EQUAL_IDENTIFIER = EbnfIdentifierName.with("CONDITION_EQUAL");

    private static ParserToken transformConditionGreaterThan(final ParserToken token,
                                                             final SpreadsheetFormatParserContext context) {
        return flatAndCreate(
                token,
                SpreadsheetFormatParserToken::greaterThan
        );
    }

    private static final EbnfIdentifierName CONDITION_GREATER_THAN_IDENTIFIER = EbnfIdentifierName.with("CONDITION_GREATER_THAN");

    private static ParserToken transformConditionGreaterThanEqual(final ParserToken token,
                                                                  final SpreadsheetFormatParserContext context) {
        return flatAndCreate(
                token,
                SpreadsheetFormatParserToken::greaterThanEquals
        );
    }

    private static final EbnfIdentifierName CONDITION_GREATER_THAN_EQUAL_IDENTIFIER = EbnfIdentifierName.with("CONDITION_GREATER_THAN_EQUAL");

    private static ParserToken transformConditionLessThan(final ParserToken token,
                                                          final SpreadsheetFormatParserContext context) {
        return flatAndCreate(
                token,
                SpreadsheetFormatParserToken::lessThan
        );
    }

    private static final EbnfIdentifierName CONDITION_LESS_THAN_IDENTIFIER = EbnfIdentifierName.with("CONDITION_LESS_THAN");

    private static ParserToken transformConditionLessThanEqual(final ParserToken token,
                                                               final SpreadsheetFormatParserContext context) {
        return flatAndCreate(
                token,
                SpreadsheetFormatParserToken::lessThanEquals
        );
    }

    private static final EbnfIdentifierName CONDITION_LESS_THAN_EQUAL_IDENTIFIER = EbnfIdentifierName.with("CONDITION_LESS_THAN_EQUAL");

    private static ParserToken transformConditionNotEqual(final ParserToken token,
                                                          final SpreadsheetFormatParserContext context) {
        return flatAndCreate(
                token,
                SpreadsheetFormatParserToken::notEquals
        );
    }

    private static final EbnfIdentifierName CONDITION_NOT_EQUAL_IDENTIFIER = EbnfIdentifierName.with("CONDITION_NOT_EQUAL");

    private static ParserToken transformDate(final ParserToken token,
                                             final SpreadsheetFormatParserContext context) {
        return flatAndCreate(
                token,
                SpreadsheetFormatParserToken::date
        );
    }

    private static final EbnfIdentifierName DATE_IDENTIFIER = EbnfIdentifierName.with("DATE");
    private static final EbnfIdentifierName DATE_COLOR_IDENTIFIER = EbnfIdentifierName.with("DATE_COLOR");

    private static ParserToken transformDateTime(final ParserToken token,
                                                 final SpreadsheetFormatParserContext context) {
        return SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformerSpreadsheetFormatParserTokenVisitor.fixMinutes(token);
    }

    private static final EbnfIdentifierName DATETIME_IDENTIFIER = EbnfIdentifierName.with("DATETIME");
    private static final EbnfIdentifierName DATETIME_COLOR_IDENTIFIER = EbnfIdentifierName.with("DATETIME_COLOR");

    private static ParserToken transformExponentSymbol(final ParserToken token,
                                                       final SpreadsheetFormatParserContext context) {
        return SpreadsheetFormatParserToken.exponentSymbol(token.cast(StringParserToken.class).value(), token.text());
    }

    private static ParserToken transformFraction(final ParserToken token,
                                                 final SpreadsheetFormatParserContext context) {
        return flatAndCreate(
                token,
                SpreadsheetFormatParserToken::fraction
        );
    }

    private static final EbnfIdentifierName FRACTION_IDENTIFIER = EbnfIdentifierName.with("FRACTION");

    private static ParserToken transformGeneral(final ParserToken token,
                                                final SpreadsheetFormatParserContext context) {
        return flatAndCreate(
                token,
                SpreadsheetFormatParserToken::general
        );
    }

    private static ParserToken transformText(final ParserToken token,
                                             final SpreadsheetFormatParserContext context) {
        return flatAndCreate(
                token,
                SpreadsheetFormatParserToken::text
        );
    }

    private static final EbnfIdentifierName TEXT_IDENTIFIER = EbnfIdentifierName.with("TEXT");

    private static final EbnfIdentifierName TEXT_LITERAL_IDENTIFIER = EbnfIdentifierName.with("TEXT_LITERAL");

    private static ParserToken transformTextLiteral(final ParserToken token,
                                                    final SpreadsheetFormatParserContext context) {
        final String text = token.text();

        return SpreadsheetFormatParserToken.textLiteral(
                text,
                text
        );
    }

    private static ParserToken transformTime(final ParserToken token,
                                             final SpreadsheetFormatParserContext context) {
        return flatAndCreate(
                token,
                SpreadsheetFormatParserToken::time
        );
    }

    private static ParserToken transformNumber(final ParserToken token,
                                               final SpreadsheetFormatParserContext context) {
        return flatAndCreate(
                token,
                SpreadsheetFormatParserToken::number
        );
    }

    private static final EbnfIdentifierName NUMBER_IDENTIFIER = EbnfIdentifierName.with("NUMBER");
    private static final EbnfIdentifierName NUMBER_COLOR_IDENTIFIER = EbnfIdentifierName.with("NUMBER_COLOR");

    private static final EbnfIdentifierName NUMBER_EXPONENT_IDENTIFIER = EbnfIdentifierName.with("NUMBER_EXPONENT");
    private static final EbnfIdentifierName NUMBER_EXPONENT_COLOR_IDENTIFIER = EbnfIdentifierName.with("NUMBER_EXPONENT_COLOR");

    private static ParserToken transformBigDecimalExponent(final ParserToken token,
                                                           final SpreadsheetFormatParserContext context) {
        return flatAndCreate(
                token,
                SpreadsheetFormatParserToken::exponent
        );
    }

    private static final EbnfIdentifierName NUMBER_EXPONENT_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("NUMBER_EXPONENT_SYMBOL");

    private static final EbnfIdentifierName TIME_IDENTIFIER = EbnfIdentifierName.with("TIME");
    private static final EbnfIdentifierName TIME_COLOR_IDENTIFIER = EbnfIdentifierName.with("TIME_COLOR");

    private static ParserToken flatAndCreate(final ParserToken token,
                                             final BiFunction<List<ParserToken>, String, ParserToken> factory) {
        return factory.apply(
                flat(token),
                token.text()
        );
    }

    /**
     * Factory
     */
    static SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer create() {
        return new SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer();
    }

    /**
     * Private ctor use singleton
     */
    private SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer() {
        super();

        final Map<EbnfIdentifierName, BiFunction<ParserToken, SpreadsheetFormatParserContext, ParserToken>> identifierToTransform = Maps.sorted();

        identifierToTransform.put(
                SpreadsheetFormatParsers.COLOR_IDENTIFIER,
                SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::transformColorParserToken
        );

        identifierToTransform.put(CONDITION_EQUAL_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::transformConditionEqual);
        identifierToTransform.put(CONDITION_GREATER_THAN_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::transformConditionGreaterThan);
        identifierToTransform.put(CONDITION_GREATER_THAN_EQUAL_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::transformConditionGreaterThanEqual);
        identifierToTransform.put(CONDITION_LESS_THAN_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::transformConditionLessThan);
        identifierToTransform.put(CONDITION_LESS_THAN_EQUAL_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::transformConditionLessThanEqual);
        identifierToTransform.put(CONDITION_NOT_EQUAL_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::transformConditionNotEqual);

        identifierToTransform.put(SpreadsheetFormatParsers.NUMBER_FORMAT, SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::flat);
        identifierToTransform.put(SpreadsheetFormatParsers.NUMBER_PARSE, SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::flat);

        identifierToTransform.put(NUMBER_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::transformNumber);
        identifierToTransform.put(NUMBER_COLOR_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::transformNumber);

        identifierToTransform.put(NUMBER_EXPONENT_COLOR_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::transformBigDecimalExponent);
        identifierToTransform.put(NUMBER_EXPONENT_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::transformBigDecimalExponent);
        identifierToTransform.put(NUMBER_EXPONENT_SYMBOL_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::transformExponentSymbol);

        identifierToTransform.put(FRACTION_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::transformFraction);

//        identifierToTransform.put(SpreadsheetFormatParsers.DATE_FORMAT, SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::flat);
        identifierToTransform.put(SpreadsheetFormatParsers.DATE_PARSE, SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::flat);

        identifierToTransform.put(DATE_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::transformDate);
        identifierToTransform.put(DATE_COLOR_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::transformDate);

//        identifierToTransform.put(SpreadsheetFormatParsers.DATETIME_FORMAT, SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::flat);
        identifierToTransform.put(SpreadsheetFormatParsers.DATETIME_PARSE, SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::flat);

        identifierToTransform.put(DATETIME_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::transformDateTime);
        identifierToTransform.put(DATETIME_COLOR_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::transformDateTime);

  //      identifierToTransform.put(SpreadsheetFormatParsers.TIME_FORMAT, SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::flat);
        identifierToTransform.put(SpreadsheetFormatParsers.TIME_PARSE, SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::flat);

        identifierToTransform.put(TIME_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::transformTime);
        identifierToTransform.put(TIME_COLOR_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::transformTime);
        identifierToTransform.put(SpreadsheetFormatParsers.TIME_PARSE, SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::flat);

        identifierToTransform.put(SpreadsheetFormatParsers.GENERAL_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::transformGeneral);

        identifierToTransform.put(TEXT_LITERAL_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::transformTextLiteral);
        identifierToTransform.put(TEXT_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::transformText);
        //identifierToTransform.put(SpreadsheetFormatParsers.TEXT_FORMAT, SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformer::flat);

        this.identifierToTransform = identifierToTransform;
    }

    private static ParserToken flat(final ParserToken token,
                                    final SpreadsheetFormatParserContext context) {
        return token.cast(RepeatedOrSequenceParserToken.class)
                .flat();
    }

    private static List<ParserToken> flat(final ParserToken token) {
        return token.cast(RepeatedOrSequenceParserToken.class)
                .flat()
                .value();
    }

    @Override
    public Parser<SpreadsheetFormatParserContext> alternatives(final EbnfAlternativeParserToken token,
                                                               final Parser<SpreadsheetFormatParserContext> parser) {
        return parser;
    }

    @Override
    public Parser<SpreadsheetFormatParserContext> concatenation(final EbnfConcatenationParserToken token,
                                                                final Parser<SpreadsheetFormatParserContext> parser) {
        return parser;
    }

    @Override
    public Parser<SpreadsheetFormatParserContext> exception(final EbnfExceptionParserToken token,
                                                            final Parser<SpreadsheetFormatParserContext> parser) {
        throw new UnsupportedOperationException(token.text()); // there are no exception tokens.
    }

    @Override
    public Parser<SpreadsheetFormatParserContext> group(final EbnfGroupParserToken token,
                                                        final Parser<SpreadsheetFormatParserContext> parser) {
        return parser; //leave group definitions as they are.
    }

    @Override
    public Parser<SpreadsheetFormatParserContext> identifier(final EbnfIdentifierParserToken token,
                                                             final Parser<SpreadsheetFormatParserContext> parser) {
        return this.transformIfNecessary(
                token.value(),
                parser
        );
    }

    @Override
    public Parser<SpreadsheetFormatParserContext> optional(final EbnfOptionalParserToken token,
                                                           final Parser<SpreadsheetFormatParserContext> parser) {
        return parser; // leave optionals alone...
    }

    @Override
    public Parser<SpreadsheetFormatParserContext> range(final EbnfRangeParserToken token,
                                                        final String beginText,
                                                        final String endText) {
        throw new UnsupportedOperationException(token.text()); // there are no ranges...
    }

    @Override
    public Parser<SpreadsheetFormatParserContext> repeated(final EbnfRepeatedParserToken token,
                                                           final Parser<SpreadsheetFormatParserContext> parser) {
        return parser;
    }

    @Override
    public Parser<SpreadsheetFormatParserContext> rule(final EbnfRuleParserToken token,
                                                       final Parser<SpreadsheetFormatParserContext> parser) {
        return this.transformIfNecessary(
                token.identifier()
                        .value(),
                parser
        );
    }

    @Override
    public Parser<SpreadsheetFormatParserContext> terminal(final EbnfTerminalParserToken token,
                                                           final Parser<SpreadsheetFormatParserContext> parser) {
        return parser;
    }

    /**
     * Uses the {@link EbnfIdentifierName} to potentially wrap the given {@link Parser} in another will reset the
     * {@link walkingkooka.text.cursor.TextCursor} if only a color is matched, adds a transformer or a required token check.
     * <br>
     * The {@link #colorCheck(EbnfIdentifierName, Parser)} will fail patterns with only a color such as <code>[BLACK]</code>.
     */
    private Parser<SpreadsheetFormatParserContext> transformIfNecessary(final EbnfIdentifierName name,
                                                                        final Parser<SpreadsheetFormatParserContext> parser) {
        Parser<SpreadsheetFormatParserContext> result = parser;

        final BiFunction<ParserToken, SpreadsheetFormatParserContext, ParserToken> transformer = this.identifierToTransform.remove(name);
        if(null != transformer) {
            result = parser.transform(transformer);

            // replace grammar definition with COLOR
            if(SpreadsheetFormatParsers.COLOR_IDENTIFIER.equals(name)) {
                result = result.setToString(
                        name.toString()
                );
            }
        }

        result = colorCheck(
                name,
                result
        );

        return name.value().endsWith("REQUIRED") ?
                result.orReport(ParserReporters.basic()) :
                result;
    }

    private final Map<EbnfIdentifierName, BiFunction<ParserToken, SpreadsheetFormatParserContext, ParserToken>> identifierToTransform;

    private Parser<SpreadsheetFormatParserContext> colorCheck(final EbnfIdentifierName name,
                                                              final Parser<SpreadsheetFormatParserContext> parser) {
        return name.equals(DATE_COLOR_IDENTIFIER) ||
                name.equals(DATETIME_COLOR_IDENTIFIER) ||
                name.equals(SpreadsheetFormatParsers.GENERAL_IDENTIFIER) ||
                name.equals(NUMBER_COLOR_IDENTIFIER) ||
                name.equals(TIME_COLOR_IDENTIFIER) ?
                SpreadsheetFormatParsersFormatColorParser.with(parser) :
                parser;
    }
}

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
import walkingkooka.text.cursor.parser.ParserContext;
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
import walkingkooka.text.cursor.parser.ebnf.EbnfTerminalParserToken;
import walkingkooka.text.cursor.parser.ebnf.combinator.EbnfParserCombinatorSyntaxTreeTransformer;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Helps transform the EBNF grammar into a {@link Parser} which will then return a {@link SpreadsheetFormatParserToken}
 */
final class SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer implements EbnfParserCombinatorSyntaxTreeTransformer {

    // constants must be init before singleton/ctor is run........................................................................

    private static ParserToken transformColor(final ParserToken token,
                                              final ParserContext context) {
        return SpreadsheetFormatParserToken.color(clean0(token), token.text());
    }

    private static ParserToken transformConditionEqual(final ParserToken token,
                                                       final ParserContext context) {
        return clean(token, SpreadsheetFormatParserToken::equalsParserToken);
    }

    private static final EbnfIdentifierName CONDITION_EQUAL_IDENTIFIER = EbnfIdentifierName.with("CONDITION_EQUAL");

    private static ParserToken transformConditionGreaterThan(final ParserToken token,
                                                             final ParserContext context) {
        return clean(token, SpreadsheetFormatParserToken::greaterThan);
    }

    private static final EbnfIdentifierName CONDITION_GREATER_THAN_IDENTIFIER = EbnfIdentifierName.with("CONDITION_GREATER_THAN");

    private static ParserToken transformConditionGreaterThanEqual(final ParserToken token,
                                                                  final ParserContext context) {
        return clean(token, SpreadsheetFormatParserToken::greaterThanEquals);
    }

    private static final EbnfIdentifierName CONDITION_GREATER_THAN_EQUAL_IDENTIFIER = EbnfIdentifierName.with("CONDITION_GREATER_THAN_EQUAL");

    private static ParserToken transformConditionLessThan(final ParserToken token,
                                                          final ParserContext context) {
        return clean(token, SpreadsheetFormatParserToken::lessThan);
    }

    private static final EbnfIdentifierName CONDITION_LESS_THAN_IDENTIFIER = EbnfIdentifierName.with("CONDITION_LESS_THAN");

    private static ParserToken transformConditionLessThanEqual(final ParserToken token,
                                                               final ParserContext context) {
        return clean(token, SpreadsheetFormatParserToken::lessThanEquals);
    }

    private static final EbnfIdentifierName CONDITION_LESS_THAN_EQUAL_IDENTIFIER = EbnfIdentifierName.with("CONDITION_LESS_THAN_EQUAL");

    private static ParserToken transformConditionNotEqual(final ParserToken token,
                                                          final ParserContext context) {
        return clean(token, SpreadsheetFormatParserToken::notEquals);
    }

    private static final EbnfIdentifierName CONDITION_NOT_EQUAL_IDENTIFIER = EbnfIdentifierName.with("CONDITION_NOT_EQUAL");

    private static ParserToken transformDate(final ParserToken token,
                                             final ParserContext context) {
        return clean(token, SpreadsheetFormatParserToken::date);
    }

    private static final EbnfIdentifierName DATE_IDENTIFIER = EbnfIdentifierName.with("DATE");
    private static final EbnfIdentifierName DATE_LIST_IDENTIFIER = EbnfIdentifierName.with("DATE_LIST");

    private static ParserToken transformDateTime(final ParserToken token,
                                                 final ParserContext context) {
        return clean(token, SpreadsheetFormatParserToken::dateTime);
    }

    private static final EbnfIdentifierName DATETIME_IDENTIFIER = EbnfIdentifierName.with("DATETIME");
    private static final EbnfIdentifierName DATETIME_LIST_IDENTIFIER = EbnfIdentifierName.with("DATETIME_LIST");

    private static ParserToken transformExponentSymbol(final ParserToken token,
                                                       final ParserContext context) {
        return SpreadsheetFormatParserToken.exponentSymbol(token.cast(StringParserToken.class).value(), token.text());
    }

    private static ParserToken transformExpression(final ParserToken token,
                                                   final ParserContext context) {
        return clean(token, SpreadsheetFormatParserToken::expression);
    }

    private static ParserToken transformFraction(final ParserToken token,
                                                 final ParserContext context) {
        return clean(token, SpreadsheetFormatParserToken::fraction);
    }

    private static final EbnfIdentifierName FRACTION_IDENTIFIER = EbnfIdentifierName.with("FRACTION");

    private static ParserToken transformGeneral(final ParserToken token,
                                                final ParserContext context) {
        return SpreadsheetFormatParserToken.general(clean0(token), token.text());
    }

    private static ParserToken transformText(final ParserToken token,
                                             final ParserContext context) {
        return clean(token, SpreadsheetFormatParserToken::text);
    }

    private static ParserToken transformTime(final ParserToken token,
                                             final ParserContext context) {
        return clean(token, SpreadsheetFormatParserToken::time);
    }

    private static ParserToken transformNumber(final ParserToken token,
                                               final ParserContext context) {
        return clean(token, SpreadsheetFormatParserToken::number);
    }

    private static final EbnfIdentifierName NUMBER_IDENTIFIER = EbnfIdentifierName.with("NUMBER");

    private static final EbnfIdentifierName NUMBER_EXPONENT_IDENTIFIER = EbnfIdentifierName.with("NUMBER_EXPONENT");

    private static ParserToken transformBigDecimalExponent(final ParserToken token,
                                                           final ParserContext context) {
        return clean(token, SpreadsheetFormatParserToken::exponent);
    }

    private static final EbnfIdentifierName NUMBER_EXPONENT_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("NUMBER_EXPONENT_SYMBOL");

    private static final EbnfIdentifierName TIME_IDENTIFIER = EbnfIdentifierName.with("TIME");
    private static final EbnfIdentifierName TIME_LIST_IDENTIFIER = EbnfIdentifierName.with("TIME_LIST");

    private static ParserToken clean(final ParserToken token,
                                     final BiFunction<List<ParserToken>, String, ParserToken> factory) {
        return factory.apply(clean0(token), token.text());
    }

    private static List<ParserToken> clean0(final ParserToken token) {
        return token.cast(RepeatedOrSequenceParserToken.class)
                .flat()
                .value();
    }

    /**
     * Singleton
     */
    final static SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer INSTANCE = new SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer();

    /**
     * Private ctor use singleton
     */
    private SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer() {
        super();

        final Map<EbnfIdentifierName, BiFunction<ParserToken, ParserContext, ParserToken>> identiferToTransform = Maps.sorted();

        identiferToTransform.put(SpreadsheetFormatParsers.COLOR_IDENTIFIER, SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformColor);

        identiferToTransform.put(CONDITION_EQUAL_IDENTIFIER, SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformConditionEqual);
        identiferToTransform.put(CONDITION_GREATER_THAN_IDENTIFIER, SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformConditionGreaterThan);
        identiferToTransform.put(CONDITION_GREATER_THAN_EQUAL_IDENTIFIER, SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformConditionGreaterThanEqual);
        identiferToTransform.put(CONDITION_LESS_THAN_IDENTIFIER, SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformConditionLessThan);
        identiferToTransform.put(CONDITION_LESS_THAN_EQUAL_IDENTIFIER, SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformConditionLessThanEqual);
        identiferToTransform.put(CONDITION_NOT_EQUAL_IDENTIFIER, SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformConditionNotEqual);

        identiferToTransform.put(NUMBER_IDENTIFIER, SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformNumber);
        identiferToTransform.put(NUMBER_EXPONENT_IDENTIFIER, SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformBigDecimalExponent);
        identiferToTransform.put(NUMBER_EXPONENT_SYMBOL_IDENTIFIER, SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformExponentSymbol);

        identiferToTransform.put(FRACTION_IDENTIFIER, SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformFraction);

        identiferToTransform.put(DATE_IDENTIFIER, SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformDate);
        identiferToTransform.put(DATE_LIST_IDENTIFIER, SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformDate);

        identiferToTransform.put(DATETIME_IDENTIFIER, SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformDateTime);
        identiferToTransform.put(DATETIME_LIST_IDENTIFIER, SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformDateTime);

        identiferToTransform.put(TIME_IDENTIFIER, SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformTime);
        identiferToTransform.put(TIME_LIST_IDENTIFIER, SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformTime);

        identiferToTransform.put(SpreadsheetFormatParsers.EXPRESSION_IDENTIFIER, SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformExpression);

        identiferToTransform.put(SpreadsheetFormatParsers.GENERAL_IDENTIFIER, SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformGeneral);

        identiferToTransform.put(SpreadsheetFormatParsers.TEXT_IDENTIFIER, SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformText);

        this.identiferToTransform = identiferToTransform;
    }

    @Override
    public Parser<ParserContext> alternatives(final EbnfAlternativeParserToken token,
                                              final Parser<ParserContext> parser) {
        return parser;
    }

    @Override
    public Parser<ParserContext> concatenation(final EbnfConcatenationParserToken token,
                                               final Parser<ParserContext> parser) {
        return parser;
    }

    @Override
    public Parser<ParserContext> exception(final EbnfExceptionParserToken token,
                                           final Parser<ParserContext> parser) {
        throw new UnsupportedOperationException(token.text()); // there are no exception tokens.
    }

    @Override
    public Parser<ParserContext> group(final EbnfGroupParserToken token,
                                       final Parser<ParserContext> parser) {
        return parser; //leave group definitions as they are.
    }

    /**
     * For identified rules, transform or special checks for required rules.
     */
    @Override
    public Parser<ParserContext> identifier(final EbnfIdentifierParserToken token,
                                            final Parser<ParserContext> parser) {
        final EbnfIdentifierName name = token.value();
        final BiFunction<ParserToken, ParserContext, ParserToken> transform = this.identiferToTransform.get(name);
        return null != transform ?
                parser.transform(transform) :
                this.requiredCheck(name, parser);
    }

    private final Map<EbnfIdentifierName, BiFunction<ParserToken, ParserContext, ParserToken>> identiferToTransform;

    private Parser<ParserContext> requiredCheck(final EbnfIdentifierName name,
                                                final Parser<ParserContext> parser) {
        return name.value().endsWith("REQUIRED") ?
                parser.orReport(ParserReporters.basic()) :
                parser; // leave as is...
    }

    @Override
    public Parser<ParserContext> optional(final EbnfOptionalParserToken token,
                                          final Parser<ParserContext> parser) {
        return parser; // leave optionals alone...
    }

    @Override
    public Parser<ParserContext> range(final EbnfRangeParserToken token,
                                       final Parser<ParserContext> parserd) {
        throw new UnsupportedOperationException(token.text()); // there are no ranges...
    }

    @Override
    public Parser<ParserContext> repeated(final EbnfRepeatedParserToken token,
                                          final Parser<ParserContext> parser) {
        return parser;
    }

    @Override
    public Parser<ParserContext> terminal(final EbnfTerminalParserToken token,
                                          final Parser<ParserContext> parser) {
        return parser;
    }
}

/*
 * Copyright 2018 Miroslav Pokorny (github.com/mP1)
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
 *
 */

package walkingkooka.spreadsheet.parser;

import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.RepeatedOrSequenceParserToken;
import walkingkooka.text.cursor.parser.SequenceParserToken;
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
import java.util.function.BiFunction;

/**
 * Helps transform the EBNF grammar into a {@link Parser} which will then return a {@link SpreadsheetFormatParserToken}
 */
final class SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer implements EbnfParserCombinatorSyntaxTreeTransformer {

    final static SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer INSTANCE = new SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer();

    private SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer() {
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
        return parser; //leaver group definitions as they are.
    }

    /**
     * For identified rules, the {@link SequenceParserToken} are flattened, missings removed and the {@link SpreadsheetFormatParentParserToken}
     * created.
     */
    @Override
    public Parser<ParserContext> identifier(final EbnfIdentifierParserToken token,
                                            final Parser<ParserContext> parser) {
        final EbnfIdentifierName name = token.value();
        return name.equals(SpreadsheetFormatParsers.COLOR_IDENTIFIER) ?
                parser.transform(SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformColor) :
                name.equals(CONDITION_EQUAL_IDENTIFIER) ?
                        parser.transform(SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformConditionEqual) :
                        name.equals(CONDITION_GREATER_THAN_IDENTIFIER) ?
                                parser.transform(SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformConditionGreaterThan) :
                                name.equals(CONDITION_GREATER_THAN_EQUAL_IDENTIFIER) ?
                                        parser.transform(SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformConditionGreaterThanEqual) :
                                        name.equals(CONDITION_LESS_THAN_IDENTIFIER) ?
                                                parser.transform(SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformConditionLessThan) :
                                                name.equals(CONDITION_LESS_THAN_EQUAL_IDENTIFIER) ?
                                                        parser.transform(SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformConditionLessThanEqual) :
                                                        name.equals(CONDITION_NOT_EQUAL_IDENTIFIER) ?
                                                                parser.transform(SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformConditionNotEqual) :
                                                                name.equals(DATE_IDENTIFIER) || name.equals(DATE2_IDENTIFIER) ?
                                                                        parser.transform(SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformDate) :
                                                                        name.equals(DATETIME_IDENTIFIER) || name.equals(DATETIME2_IDENTIFIER) ?
                                                                                parser.transform(SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformDateTime) :
                                                                                name.equals(SpreadsheetFormatParsers.EXPRESSION_IDENTIFIER) ?
                                                                                        parser.transform(SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformExpression) :
                                                                                        name.equals(BIGDECIMAL_IDENTIFIER) ?
                                                                                                parser.transform(SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformBigDecimal) :
                                                                                                name.equals(BIGDECIMAL_EXPONENT_IDENTIFIER) ?
                                                                                                        parser.transform(SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformBigDecimalExponent) :
                                                                                                        name.equals(BIGDECIMAL_EXPONENT_SYMBOL_IDENTIFIER) ?
                                                                                                                parser.transform(SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformExponentSymbol) :
                                                                                                                name.equals(FRACTION_IDENTIFIER) ?
                                                                                                                        parser.transform(SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformFraction) :
                                                                                                                        name.equals(SpreadsheetFormatParsers.GENERAL_IDENTIFIER) ?
                                                                                                                                parser.transform(SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformGeneral) :
                                                                                                                                name.equals(SpreadsheetFormatParsers.TEXT_IDENTIFIER) ?
                                                                                                                                        parser.transform(SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformText) :
                                                                                                                                        name.equals(TIME_IDENTIFIER) || name.equals(TIME2_IDENTIFIER) ?
                                                                                                                                                parser.transform(SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer::transformTime) :
                                                                                                                                                this.requiredCheck(name, parser);
    }

    private static ParserToken transformColor(final ParserToken token,
                                              final ParserContext context) {
        return SpreadsheetFormatParserToken.color(clean0(token.cast()), token.text());
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
    private static final EbnfIdentifierName DATE2_IDENTIFIER = EbnfIdentifierName.with("DATE2");

    private static ParserToken transformDateTime(final ParserToken token,
                                                 final ParserContext context) {
        return clean(token, SpreadsheetFormatParserToken::dateTime);
    }

    private static final EbnfIdentifierName DATETIME_IDENTIFIER = EbnfIdentifierName.with("DATETIME");
    private static final EbnfIdentifierName DATETIME2_IDENTIFIER = EbnfIdentifierName.with("DATETIME2");

    private static ParserToken transformBigDecimal(final ParserToken token,
                                                   final ParserContext context) {
        return clean(token, SpreadsheetFormatParserToken::bigDecimal);
    }

    private static final EbnfIdentifierName BIGDECIMAL_IDENTIFIER = EbnfIdentifierName.with("BIGDECIMAL");

    private static final EbnfIdentifierName BIGDECIMAL_EXPONENT_IDENTIFIER = EbnfIdentifierName.with("BIGDECIMAL_EXPONENT");

    private static ParserToken transformBigDecimalExponent(final ParserToken token,
                                                           final ParserContext context) {
        return clean(token, SpreadsheetFormatParserToken::exponent);
    }

    private static final EbnfIdentifierName BIGDECIMAL_EXPONENT_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("BIGDECIMAL_EXPONENT_SYMBOL");

    private static ParserToken transformExponentSymbol(final ParserToken token,
                                                       final ParserContext context) {
        return SpreadsheetFormatParserToken.exponentSymbol(StringParserToken.class.cast(token).value(), token.text());
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
        return SpreadsheetFormatParserToken.general(clean0(token.cast()), token.text());
    }

    private static ParserToken transformText(final ParserToken token,
                                             final ParserContext context) {
        return clean(token, SpreadsheetFormatParserToken::text);
    }

    private static ParserToken transformTime(final ParserToken token,
                                             final ParserContext context) {
        return clean(token, SpreadsheetFormatParserToken::time);
    }

    private static final EbnfIdentifierName TIME_IDENTIFIER = EbnfIdentifierName.with("TIME");
    private static final EbnfIdentifierName TIME2_IDENTIFIER = EbnfIdentifierName.with("TIME2");

    private static ParserToken clean(final ParserToken token,
                                     final BiFunction<List<ParserToken>, String, ParserToken> factory) {
        return factory.apply(clean0(token.cast()), token.text());
    }

    private static List<ParserToken> clean0(final RepeatedOrSequenceParserToken<?> token) {
        return token.flat()
                .value();
    }

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

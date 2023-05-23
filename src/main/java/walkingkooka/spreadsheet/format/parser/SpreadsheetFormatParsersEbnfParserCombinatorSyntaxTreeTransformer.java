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
import walkingkooka.text.cursor.parser.ebnf.EbnfTerminalParserToken;
import walkingkooka.text.cursor.parser.ebnf.combinator.EbnfParserCombinatorSyntaxTreeTransformer;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Helps transform the EBNF grammar into a {@link Parser} which will then return a {@link SpreadsheetFormatParserToken}
 */
final class SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer implements EbnfParserCombinatorSyntaxTreeTransformer<SpreadsheetFormatParserContext> {

    // constants must be init before singleton/ctor is run........................................................................

    private static ParserToken transformColor(final ParserToken token,
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
        return SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformerSpreadsheetFormatParserTokenVisitor.fixMinutes(token);
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

    private static ParserToken transformTextCharacter(final ParserToken token,
                                                      final SpreadsheetFormatParserContext context) {
        final String text = token.text();
        return SpreadsheetFormatParserToken.textLiteral(
                text,
                text
        );
    }

    private static final EbnfIdentifierName TEXT_CHARACTER_IDENTIFIER = EbnfIdentifierName.with("TEXT_CHARACTER");

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

    private static List<ParserToken> flat(final ParserToken token) {
        return token.cast(RepeatedOrSequenceParserToken.class)
                .flat()
                .value();
    }

    /**
     * Singleton
     */
    final static SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer INSTANCE = new SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer();

    /**
     * Private ctor use singleton
     */
    private SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer() {
        super();

        final Map<EbnfIdentifierName, BiFunction<ParserToken, SpreadsheetFormatParserContext, ParserToken>> identifierToTransform = Maps.sorted();

        identifierToTransform.put(SpreadsheetFormatParsers.COLOR_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::transformColor);

        identifierToTransform.put(CONDITION_EQUAL_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::transformConditionEqual);
        identifierToTransform.put(CONDITION_GREATER_THAN_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::transformConditionGreaterThan);
        identifierToTransform.put(CONDITION_GREATER_THAN_EQUAL_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::transformConditionGreaterThanEqual);
        identifierToTransform.put(CONDITION_LESS_THAN_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::transformConditionLessThan);
        identifierToTransform.put(CONDITION_LESS_THAN_EQUAL_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::transformConditionLessThanEqual);
        identifierToTransform.put(CONDITION_NOT_EQUAL_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::transformConditionNotEqual);

        identifierToTransform.put(SpreadsheetFormatParsers.NUMBER_FORMAT, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::flat);
        identifierToTransform.put(SpreadsheetFormatParsers.NUMBER_PARSE, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::flat);

        identifierToTransform.put(NUMBER_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::transformNumber);
        identifierToTransform.put(NUMBER_COLOR_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::transformNumber);

        identifierToTransform.put(NUMBER_EXPONENT_COLOR_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::transformBigDecimalExponent);
        identifierToTransform.put(NUMBER_EXPONENT_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::transformBigDecimalExponent);
        identifierToTransform.put(NUMBER_EXPONENT_SYMBOL_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::transformExponentSymbol);

        identifierToTransform.put(FRACTION_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::transformFraction);

        identifierToTransform.put(SpreadsheetFormatParsers.DATE_FORMAT, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::flat);
        identifierToTransform.put(SpreadsheetFormatParsers.DATE_PARSE, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::flat);

        identifierToTransform.put(DATE_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::transformDate);
        identifierToTransform.put(DATE_COLOR_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::transformDate);

        identifierToTransform.put(SpreadsheetFormatParsers.DATETIME_FORMAT, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::flat);
        identifierToTransform.put(SpreadsheetFormatParsers.DATETIME_PARSE, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::flat);

        identifierToTransform.put(DATETIME_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::transformDateTime);
        identifierToTransform.put(DATETIME_COLOR_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::transformDateTime);

        identifierToTransform.put(SpreadsheetFormatParsers.TIME_FORMAT, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::flat);
        identifierToTransform.put(SpreadsheetFormatParsers.TIME_PARSE, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::flat);

        identifierToTransform.put(TIME_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::transformTime);
        identifierToTransform.put(TIME_COLOR_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::transformTime);

        identifierToTransform.put(SpreadsheetFormatParsers.GENERAL_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::transformGeneral);

        identifierToTransform.put(TEXT_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::transformText);
        identifierToTransform.put(SpreadsheetFormatParsers.TEXT_FORMAT, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::flat);
        identifierToTransform.put(SpreadsheetFormatParsers.TIME_PARSE, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::flat);

        identifierToTransform.put(TEXT_CHARACTER_IDENTIFIER, SpreadsheetFormatParsersEbnfParserCombinatorSyntaxTreeTransformer::transformTextCharacter);

        this.identifierToTransform = identifierToTransform;
    }

    private static ParserToken flat(final ParserToken token,
                                    final SpreadsheetFormatParserContext context) {
        return token.cast(RepeatedOrSequenceParserToken.class)
                .flat();
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

    /**
     * For identified rules, transform or special checks for required rules.
     */
    @Override
    public Parser<SpreadsheetFormatParserContext> identifier(final EbnfIdentifierParserToken token,
                                                             final Parser<SpreadsheetFormatParserContext> parser) {
        final EbnfIdentifierName name = token.value();
        final BiFunction<ParserToken, SpreadsheetFormatParserContext, ParserToken> transform = this.identifierToTransform.get(name);
        return null != transform ?
                parser.transform(transform) :
                this.requiredCheck(name, parser);
    }

    private final Map<EbnfIdentifierName, BiFunction<ParserToken, SpreadsheetFormatParserContext, ParserToken>> identifierToTransform;

    private Parser<SpreadsheetFormatParserContext> requiredCheck(final EbnfIdentifierName name,
                                                                 final Parser<SpreadsheetFormatParserContext> parser) {
        return name.value().endsWith("REQUIRED") ?
                parser.orReport(ParserReporters.basic()) :
                parser; // leave as is...
    }

    @Override
    public Parser<SpreadsheetFormatParserContext> optional(final EbnfOptionalParserToken token,
                                                           final Parser<SpreadsheetFormatParserContext> parser) {
        return parser; // leave optionals alone...
    }

    @Override
    public Parser<SpreadsheetFormatParserContext> range(final EbnfRangeParserToken token,
                                                        final Parser<SpreadsheetFormatParserContext> parser) {
        throw new UnsupportedOperationException(token.text()); // there are no ranges...
    }

    @Override
    public Parser<SpreadsheetFormatParserContext> repeated(final EbnfRepeatedParserToken token,
                                                           final Parser<SpreadsheetFormatParserContext> parser) {
        return parser;
    }

    @Override
    public Parser<SpreadsheetFormatParserContext> terminal(final EbnfTerminalParserToken token,
                                                           final Parser<SpreadsheetFormatParserContext> parser) {
        return parser;
    }
}

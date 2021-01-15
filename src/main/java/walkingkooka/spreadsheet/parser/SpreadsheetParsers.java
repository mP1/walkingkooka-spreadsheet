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

import walkingkooka.collect.map.Maps;
import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.function.SpreadsheetFunctionName;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.BigDecimalParserToken;
import walkingkooka.text.cursor.parser.CharacterParserToken;
import walkingkooka.text.cursor.parser.DoubleQuotedParserToken;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.cursor.parser.SequenceParserToken;
import walkingkooka.text.cursor.parser.StringParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfGrammarParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfIdentifierName;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserContexts;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserToken;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * Numerous {@link Parser parsers} that parse individual components of a formula or an entire formula.
 */
public final class SpreadsheetParsers implements PublicStaticHelper {

    /**
     * Range separator character used to separate the lower and upper bounds.
     */
    public static final CharacterConstant RANGE_SEPARATOR = CharacterConstant.with(':');

    /**
     * A {@link Parser} that returns a cell reference token of some sort.
     */
    public static Parser<SpreadsheetParserContext> cellReferences() {
        return CELL_REFERENCES_PARSER;
    }

    private final static Parser<SpreadsheetParserContext> CELL_REFERENCES_PARSER;

    private static void cellReferences(final Map<EbnfIdentifierName, Parser<ParserContext>> predefined) {
        predefined.put(COLUMN_ROW_IDENTIFIER, columnAndRow());
        predefined.put(LABEL_NAME_IDENTIFIER, labelName());
        predefined.put(BETWEEN_SYMBOL_IDENTIFIER, BETWEEN_SYMBOL);
    }

    private static final EbnfIdentifierName COLUMN_ROW_IDENTIFIER = EbnfIdentifierName.with("COLUMN_ROW");
    private static final EbnfIdentifierName LABEL_NAME_IDENTIFIER = EbnfIdentifierName.with("LABEL_NAME");
    private static final EbnfIdentifierName BETWEEN_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("BETWEEN_SYMBOL");

    private static final Parser<ParserContext> BETWEEN_SYMBOL = symbol(RANGE_SEPARATOR.character(),
            SpreadsheetParserToken::betweenSymbol,
            SpreadsheetBetweenSymbolParserToken.class);

    /**
     * {@see SpreadsheetColumnReferenceParser}
     */
    public static Parser<ParserContext> column() {
        return SpreadsheetColumnReferenceParser.INSTANCE.cast();
    }

    /**
     * {@see SpreadsheetColumnReferenceParser}
     */
    public static Parser<ParserContext> columnAndRow() {
        return COLUMN_AND_ROW;
    }

    private static final Parser<ParserContext> COLUMN_AND_ROW = column()
            .builder()
            .required(row())
            .build()
            .transform(SpreadsheetParsers::transformColumnAndRow);

    private static ParserToken transformColumnAndRow(final ParserToken token,
                                                     final ParserContext context) {
        return SpreadsheetParserToken.cellReference(token.cast(SequenceParserToken.class).value(), token.text());
    }

    /**
     * Returns a {@link Parser} that parsers expressions.
     */
    public static Parser<SpreadsheetParserContext> expression() {
        return EXPRESSION_PARSER;
    }

    private final static Parser<SpreadsheetParserContext> EXPRESSION_PARSER;
    private final static EbnfIdentifierName EXPRESSION_IDENTIFIER = EbnfIdentifierName.with("EXPRESSION");

    /**
     * Returns a {@link Parser} that parsers function invocations, starting with the name and parameters.
     */
    public static Parser<SpreadsheetParserContext> function() {
        return FUNCTION_PARSER;
    }

    private final static EbnfIdentifierName FUNCTION_IDENTIFIER = EbnfIdentifierName.with("FUNCTION");
    private final static Parser<SpreadsheetParserContext> FUNCTION_PARSER;

    /**
     * A parser that returns {@see SpreadsheetFunctionName}
     */
    public static Parser<ParserContext> functionName() {
        return FUNCTION_NAME;
    }

    static {
        final CharPredicate initial = CharPredicates.range('A', 'Z')
                .or(
                        CharPredicates.range('a', 'z')
                ); // SpreadsheetFunctionName.INITIAL

        FUNCTION_NAME = Parsers.stringInitialAndPartCharPredicate(
                initial,
                initial.or(CharPredicates.range('0', '9').or(CharPredicates.is('.'))), // SpreadsheetFunctionName.PART,
                1,
                SpreadsheetFunctionName.MAX_LENGTH)
                .transform(SpreadsheetParsers::transformFunctionName)
                .setToString(SpreadsheetFunctionName.class.getSimpleName());
    }

    private final static Parser<ParserContext> FUNCTION_NAME;

    private static ParserToken transformFunctionName(final ParserToken token, final ParserContext context) {
        return SpreadsheetParserToken.functionName(
                SpreadsheetFunctionName.with(token.cast(StringParserToken.class).value()),
                token.text()
        );
    }

    private static void functions(final Map<EbnfIdentifierName, Parser<ParserContext>> predefined) {
        predefined.put(FUNCTION_NAME_IDENTIFIER, functionName());
        predefined.put(VALUE_SEPARATOR_SYMBOL_IDENTIFIER, VALUE_SEPARATOR_SYMBOL);
    }

    private static final Parser<ParserContext> VALUE_SEPARATOR_SYMBOL = symbol(
            ',',
            SpreadsheetParserToken::valueSeparatorSymbol,
            SpreadsheetValueSeparatorSymbolParserToken.class
    );

    private static final EbnfIdentifierName FUNCTION_NAME_IDENTIFIER = EbnfIdentifierName.with("FUNCTION_NAME");
    private static final EbnfIdentifierName VALUE_SEPARATOR_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("VALUE_SEPARATOR_SYMBOL");

    /**
     * {@see SpreadsheetLabelNameParser}
     */
    public static Parser<ParserContext> labelName() {
        return SpreadsheetLabelNameParser.INSTANCE.cast();
    }

    /**
     * A {@link Parser} that returns a range which will include cell references or labels.
     */
    public static Parser<SpreadsheetParserContext> range() {
        return RANGE_PARSER;
    }

    private final static Parser<SpreadsheetParserContext> RANGE_PARSER;

    /**
     * {@see SpreadsheetRowReferenceParser}
     */
    public static Parser<ParserContext> row() {
        return SpreadsheetRowReferenceParser.INSTANCE.cast();
    }

    // conditions.............................................................................................................

    private static void conditions(final Map<EbnfIdentifierName, Parser<ParserContext>> predefined) {
        predefined.put(EQUALS_SYMBOL_IDENTIFIER, EQUALS_SYMBOL);
        predefined.put(NOT_EQUALS_SYMBOL_IDENTIFIER, NOT_EQUALS_SYMBOL);

        predefined.put(GREATER_THAN_SYMBOL_IDENTIFIER, GREATER_THAN_SYMBOL);
        predefined.put(GREATER_THAN_EQUALS_SYMBOL_IDENTIFIER, GREATER_THAN_EQUALS_SYMBOL);
        predefined.put(LESS_THAN_SYMBOL_IDENTIFIER, LESS_THAN_SYMBOL);
        predefined.put(LESS_THAN_EQUALS_SYMBOL_IDENTIFIER, LESS_THAN_EQUALS_SYMBOL);
    }

    private static final Parser<ParserContext> EQUALS_SYMBOL = symbol(
            "==",
            SpreadsheetParserToken::equalsSymbol,
            SpreadsheetEqualsSymbolParserToken.class
    );
    private static final Parser<ParserContext> NOT_EQUALS_SYMBOL = symbol(
            "!=",
            SpreadsheetParserToken::notEqualsSymbol,
            SpreadsheetNotEqualsSymbolParserToken.class
    );
    private static final Parser<ParserContext> GREATER_THAN_SYMBOL = symbol(
            '>',
            SpreadsheetParserToken::greaterThanSymbol,
            SpreadsheetGreaterThanSymbolParserToken.class
    );
    private static final Parser<ParserContext> GREATER_THAN_EQUALS_SYMBOL = symbol(
            ">=",
            SpreadsheetParserToken::greaterThanEqualsSymbol,
            SpreadsheetGreaterThanEqualsSymbolParserToken.class
    );
    private static final Parser<ParserContext> LESS_THAN_SYMBOL = symbol(
            '<',
            SpreadsheetParserToken::lessThanSymbol,
            SpreadsheetLessThanSymbolParserToken.class
    );
    private static final Parser<ParserContext> LESS_THAN_EQUALS_SYMBOL = symbol(
            "<=",
            SpreadsheetParserToken::lessThanEqualsSymbol,
            SpreadsheetLessThanEqualsSymbolParserToken.class
    );

    private static final EbnfIdentifierName EQUALS_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("EQUALS_SYMBOL");
    private static final EbnfIdentifierName NOT_EQUALS_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("NOT_EQUALS_SYMBOL");

    private static final EbnfIdentifierName GREATER_THAN_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("GREATER_THAN_SYMBOL");
    private static final EbnfIdentifierName GREATER_THAN_EQUALS_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("GREATER_THAN_EQUALS_SYMBOL");
    private static final EbnfIdentifierName LESS_THAN_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("LESS_THAN_SYMBOL");
    private static final EbnfIdentifierName LESS_THAN_EQUALS_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("LESS_THAN_EQUALS_SYMBOL");

    // math.............................................................................................................

    private static void math(final Map<EbnfIdentifierName, Parser<ParserContext>> predefined) {
        predefined.put(PLUS_SYMBOL_IDENTIFIER, PLUS_SYMBOL);
        predefined.put(NEGATIVE_SYMBOL_IDENTIFIER, NEGATIVE_SYMBOL);

        predefined.put(MULTIPLY_SYMBOL_IDENTIFIER, MULTIPLY_SYMBOL);
        predefined.put(DIVIDE_SYMBOL_IDENTIFIER, DIVIDE_SYMBOL);

        predefined.put(POWER_SYMBOL_IDENTIFIER, POWER_SYMBOL);
    }

    private static final EbnfIdentifierName PLUS_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("PLUS_SYMBOL");
    private static final EbnfIdentifierName NEGATIVE_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("NEGATIVE_SYMBOL");
    private static final EbnfIdentifierName MULTIPLY_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("MULTIPLY_SYMBOL");
    private static final EbnfIdentifierName DIVIDE_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("DIVIDE_SYMBOL");
    private static final EbnfIdentifierName POWER_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("POWER_SYMBOL");

    private static final Parser<ParserContext> PLUS_SYMBOL = symbol(
            '+',
            SpreadsheetParserToken::plusSymbol,
            SpreadsheetPlusSymbolParserToken.class
    );
    private static final Parser<ParserContext> NEGATIVE_SYMBOL = symbol(
            '-',
            SpreadsheetParserToken::minusSymbol,
            SpreadsheetMinusSymbolParserToken.class
    );
    private static final Parser<ParserContext> MULTIPLY_SYMBOL = symbol(
            '*',
            SpreadsheetParserToken::multiplySymbol,
            SpreadsheetMultiplySymbolParserToken.class
    );
    private static final Parser<ParserContext> DIVIDE_SYMBOL = symbol(
            '/',
            SpreadsheetParserToken::divideSymbol,
            SpreadsheetDivideSymbolParserToken.class
    );
    private static final Parser<ParserContext> POWER_SYMBOL = symbol(
            '^',
            SpreadsheetParserToken::powerSymbol,
            SpreadsheetPowerSymbolParserToken.class
    );

    // misc.............................................................................................................

    private static void misc(final Map<EbnfIdentifierName, Parser<ParserContext>> predefined) {
        predefined.put(FORMULA_EQUALS_SYMBOL_IDENTIFIER, FORMULA_EQUALS_SYMBOL);

        predefined.put(NUMBER_IDENTIFIER, NUMBER);
        predefined.put(PERCENT_SYMBOL_IDENTIFIER, PERCENT_SYMBOL);

        predefined.put(PARENTHESIS_OPEN_SYMBOL_IDENTIFIER, PARENTHESIS_OPEN_SYMBOL);
        predefined.put(PARENTHESIS_CLOSE_SYMBOL_IDENTIFIER, PARENTHESIS_CLOSE_SYMBOL);

        predefined.put(TEXT_IDENTIFIER, text());

        predefined.put(WHITESPACE_IDENTIFIER, whitespace());
    }
    private static final EbnfIdentifierName FORMULA_EQUALS_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("FORMULA_EQUALS_SYMBOL");
    private static final Parser<ParserContext> FORMULA_EQUALS_SYMBOL = symbol(
            "=",
            SpreadsheetParserToken::equalsSymbol,
            SpreadsheetEqualsSymbolParserToken.class
    );

    private static final EbnfIdentifierName NUMBER_IDENTIFIER = EbnfIdentifierName.with("NUMBER");
    private static final Parser<ParserContext> NUMBER = Parsers.bigDecimal()
            .transform(SpreadsheetParsers::transformNumber);

    private static ParserToken transformNumber(final ParserToken token,
                                               final ParserContext context) {
        return transformNumber0(
                token.cast(BigDecimalParserToken.class),
                (SpreadsheetParserContext) context
        );
    }

    private static ParserToken transformNumber0(final BigDecimalParserToken token,
                                                final SpreadsheetParserContext context) {
        return SpreadsheetParserToken.expressionNumber(
                context.expressionNumberKind()
                        .create(token.value()),
                token.text()
        );
    }

    private static final EbnfIdentifierName PERCENT_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("PERCENT_SYMBOL");
    private static final EbnfIdentifierName PARENTHESIS_OPEN_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("PARENTHESIS_OPEN_SYMBOL");
    private static final EbnfIdentifierName PARENTHESIS_CLOSE_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("PARENTHESIS_CLOSE_SYMBOL");
    private static final EbnfIdentifierName TEXT_IDENTIFIER = EbnfIdentifierName.with("TEXT");
    private static final EbnfIdentifierName WHITESPACE_IDENTIFIER = EbnfIdentifierName.with("WHITESPACE");

    private static final Parser<ParserContext> PERCENT_SYMBOL = symbol(
            '%',
            SpreadsheetParserToken::percentSymbol,
            SpreadsheetPercentSymbolParserToken.class
    );
    private static final Parser<ParserContext> PARENTHESIS_OPEN_SYMBOL = symbol(
            '(',
            SpreadsheetParserToken::parenthesisOpenSymbol,
            SpreadsheetParenthesisOpenSymbolParserToken.class
    );
    private static final Parser<ParserContext> PARENTHESIS_CLOSE_SYMBOL = symbol(
            ')',
            SpreadsheetParserToken::parenthesisCloseSymbol,
            SpreadsheetParenthesisCloseSymbolParserToken.class
    );

    /**
     * Text
     */
    public static Parser<ParserContext> text() {
        return TEXT;
    }

    private final static Parser<ParserContext> TEXT = Parsers.doubleQuoted()
            .transform(SpreadsheetParsers::transformText)
            .setToString(SpreadsheetTextParserToken.class.getSimpleName());

    private static ParserToken transformText(final ParserToken token, final ParserContext context) {
        return SpreadsheetParserToken.text(
                token.cast(DoubleQuotedParserToken.class).value(),
                token.text()
        );
    }

    /**
     * Value literals such as apostrophe string, number, date, date-time, time or equals-sign and expression.
     * TODO <a href="https://github.com/mP1/walkingkooka-spreadsheet/issues/1249">formula: Apostrophe text entry support</a>
     * TODO <a href="https://github.com/mP1/walkingkooka-spreadsheet/issues/1250">SpreadsheetDatePatternsParser used when parsing formula value</a>
     * TODO <a href="https://github.com/mP1/walkingkooka-spreadsheet/issues/1251">SpreadsheetDateTimePatternsParser used when parsing formula value</a>
     * TODO <a href="https://github.com/mP1/walkingkooka-spreadsheet/issues/1252">SpreadsheetTimePatternsParser used when parsing formula value</a>
     * TODO <a href="https://github.com/mP1/walkingkooka-spreadsheet/issues/1253">SpreadsheetNumberPatternsParser used when parsing formula value</a>
     * TODO <a href="https://github.com/mP1/walkingkooka-spreadsheet/issues/1254">default formula value parsing after apostrophe-string, date, datetime, number & time</a>
     */
    public static Parser<SpreadsheetParserContext> valueOrExpression() {
        return EQUALS_EXPRESSION_PARSER;
    }

    private static Parser<SpreadsheetParserContext> EQUALS_EXPRESSION_PARSER;

    private static final EbnfIdentifierName EQUALS_EXPRESSION_IDENTIFIER = EbnfIdentifierName.with("EQUALS_EXPRESSION");

    private static ParserToken transformValueOrExpression(final ParserToken token, final ParserContext context) {
        return SpreadsheetParserToken.expression(
                token.cast(SequenceParserToken.class).value(),
                token.text()
        );
    }

    /**
     * Whitespace
     */
    public static Parser<ParserContext> whitespace() {
        return WHITESPACE;
    }

    private final static Parser<ParserContext> WHITESPACE = Parsers.stringCharPredicate(
            CharPredicates.whitespace(),
            1,
            Integer.MAX_VALUE
    ).transform(SpreadsheetParsers::transformWhitespace)
            .setToString(SpreadsheetWhitespaceParserToken.class.getSimpleName());

    private static ParserToken transformWhitespace(final ParserToken token,
                                                   final ParserContext context) {
        return SpreadsheetParserToken.whitespace(
                token.cast(StringParserToken.class).value(),
                token.text()
        );
    }

    // helpers .........................................................................................................

    /*
     * Uses the grammar file to fetch one of the parsers.
     */
    static {
        final Map<EbnfIdentifierName, Parser<ParserContext>> predefined = Maps.sorted();

        cellReferences(predefined);
        conditions(predefined);
        functions(predefined);
        math(predefined);
        misc(predefined);

        final TextCursor grammarFile = TextCursors.charSequence(new SpreadsheetParsersGrammarProvider().text());

        final Map<EbnfIdentifierName, Parser<ParserContext>> parsers = EbnfParserToken.grammarParser()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(grammarFile, EbnfParserContexts.basic())
                .orElseThrow(() -> new IllegalStateException("Unable to parse parsers grammar file."))
                .cast(EbnfGrammarParserToken.class)
                .combinator(predefined, SpreadsheetParsersEbnfParserCombinatorSyntaxTreeTransformer.INSTANCE);

        CELL_REFERENCES_PARSER = parsers.get(EbnfIdentifierName.with("CELL")).cast();
        EXPRESSION_PARSER = parsers.get(EXPRESSION_IDENTIFIER).cast();
        EQUALS_EXPRESSION_PARSER = parsers.get(EQUALS_EXPRESSION_IDENTIFIER)
                .cast()
                .transform(SpreadsheetParsers::transformValueOrExpression)
                .setToString(SpreadsheetExpressionParserToken.class.getSimpleName())
                .cast();
        FUNCTION_PARSER = parsers.get(FUNCTION_IDENTIFIER).cast();
        RANGE_PARSER = parsers.get(EbnfIdentifierName.with("RANGE")).cast();
    }

    private static Parser<ParserContext> symbol(final char c,
                                                final BiFunction<String, String, ParserToken> factory,
                                                final Class<? extends SpreadsheetSymbolParserToken> tokenClass) {
        return Parsers.character(CharPredicates.is(c))
                .transform((charParserToken, context) -> factory.apply(charParserToken.cast(CharacterParserToken.class).value().toString(), charParserToken.text()))
                .setToString(tokenClass.getSimpleName());
    }

    private static Parser<ParserContext> symbol(final String text,
                                                final BiFunction<String, String, ParserToken> factory,
                                                final Class<? extends SpreadsheetSymbolParserToken> tokenClass) {
        return Parsers.string(text, CaseSensitivity.INSENSITIVE)
                .transform((stringParserToken, context) -> factory.apply(stringParserToken.cast(StringParserToken.class).value(), stringParserToken.text()))
                .setToString(tokenClass.getSimpleName());
    }

    public static int valueFromDigit(final char c) {
        final int digit = Character.toUpperCase(c) - 'A';
        return digit >= 0 && digit < SpreadsheetColumnReferenceParser.RADIX ? digit + 1 : -1;
    }

    /**
     * Stop construction
     */
    private SpreadsheetParsers() {
        throw new UnsupportedOperationException();
    }
}

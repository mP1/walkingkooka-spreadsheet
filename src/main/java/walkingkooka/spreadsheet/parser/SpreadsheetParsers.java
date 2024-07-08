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
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.expression.SpreadsheetFunctionName;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.CharacterParserToken;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.cursor.parser.SequenceParserToken;
import walkingkooka.text.cursor.parser.StringParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfGrammarParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfIdentifierName;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserContexts;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserToken;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Numerous {@link Parser parsers} that parse individual components of a formula or an entire formula.
 */
public final class SpreadsheetParsers implements PublicStaticHelper {

    /**
     * Range separator character used to separate the lower and upper bounds.
     */
    public static final CharacterConstant RANGE_SEPARATOR = CharacterConstant.with(':');

    /**
     * A parser that knows how to parse a cell reference, but not cell-range or label.
     */
    public static Parser<SpreadsheetParserContext> cell() {
        return CELL;
    }

    private static final Parser<SpreadsheetParserContext> CELL = column()
            .builder()
            .required(row())
            .build()
            .transform(SpreadsheetParsers::transformCell)
            .setToString("cell");

    private static ParserToken transformCell(final ParserToken token,
                                             final SpreadsheetParserContext context) {
        return SpreadsheetParserToken.cellReference(
                token.cast(SequenceParserToken.class)
                        .value(),
                token.text()
        );
    }

    /**
     * A {@link Parser} that returns a cell reference token of some sort.
     */
    public static Parser<SpreadsheetParserContext> cellOrCellRangeOrLabel() {
        return CELL_OR_CELL_RANGE_OR_LABEL_PARSER;
    }

    private final static Parser<SpreadsheetParserContext> CELL_OR_CELL_RANGE_OR_LABEL_PARSER;

    private static void cellOrCellRangeOrLabel(final Map<EbnfIdentifierName, Parser<SpreadsheetParserContext>> predefined) {
        predefined.put(CELL_IDENTIFIER, CELL);
        predefined.put(LABEL_NAME_IDENTIFIER, labelName());
        predefined.put(BETWEEN_SYMBOL_IDENTIFIER, BETWEEN_SYMBOL);
    }

    private static final EbnfIdentifierName CELL_IDENTIFIER = EbnfIdentifierName.with("CELL");
    private static final EbnfIdentifierName LABEL_NAME_IDENTIFIER = EbnfIdentifierName.with("LABEL_NAME");
    private static final EbnfIdentifierName BETWEEN_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("BETWEEN_SYMBOL");

    private static final Parser<SpreadsheetParserContext> BETWEEN_SYMBOL = symbol(RANGE_SEPARATOR.character(),
            SpreadsheetParserToken::betweenSymbol,
            SpreadsheetBetweenSymbolParserToken.class);


    /**
     * A {@link Parser} that returns a range but not cell reference or labels.
     */
    public static Parser<SpreadsheetParserContext> cellRange() {
        return CELL_RANGE_PARSER;
    }

    private final static Parser<SpreadsheetParserContext> CELL_RANGE_PARSER;

    /**
     * {@see SpreadsheetColumnReferenceParser}
     */
    public static SpreadsheetParser column() {
        return SpreadsheetColumnReferenceParser.INSTANCE;
    }

    /**
     * Returns a {@link Parser} that errors such as <code>#REF!</code>.
     */
    public static Parser<SpreadsheetParserContext> error() {
        return ERROR_PARSER;
    }

    private static final Parser<SpreadsheetParserContext> ERROR_PARSER = errorParser();

    private static Parser<SpreadsheetParserContext> errorParser() {
        return Parsers.alternatives(
                Arrays.stream(SpreadsheetErrorKind.values())
                        .map(SpreadsheetParsers::errorParser0)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Creates a {@link Parser} that matches the {@link SpreadsheetErrorKind#text()} and returns a {@link SpreadsheetParserToken#error(SpreadsheetError, String)}.
     */
    private static Parser<SpreadsheetParserContext> errorParser0(final SpreadsheetErrorKind kind) {
        final String text = kind.text();
        final SpreadsheetError error = kind.toError();
        final SpreadsheetParserToken token = SpreadsheetParserToken.error(error, text);

        return Parsers.string(
                        text,
                        CaseSensitivity.SENSITIVE
                ).transform((p, c) -> token)
                .cast();
    }

    /**
     * Returns a {@link Parser} that parsers expressions.
     */
    public static Parser<SpreadsheetParserContext> expression() {
        return EXPRESSION_PARSER;
    }

    private static final Parser<SpreadsheetParserContext> EXPRESSION_PARSER;

    private static void functions(final Map<EbnfIdentifierName, Parser<SpreadsheetParserContext>> predefined) {
        predefined.put(FUNCTION_NAME_IDENTIFIER, functionName());
        predefined.put(VALUE_SEPARATOR_SYMBOL_IDENTIFIER, SpreadsheetParsersValueSeparatorParser.INSTANCE);
    }

    private static final EbnfIdentifierName FUNCTION_NAME_IDENTIFIER = EbnfIdentifierName.with("FUNCTION_NAME");
    private static final EbnfIdentifierName VALUE_SEPARATOR_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("VALUE_SEPARATOR_SYMBOL");

    /**
     * {@see FakeSpreadsheetParser}
     */
    public static SpreadsheetParser fake() {
        return new FakeSpreadsheetParser();
    }

    /**
     * A parser that returns {@see SpreadsheetFunctionName}
     */
    public static Parser<SpreadsheetParserContext> functionName() {
        return FUNCTION_NAME;
    }

    static {
        final CharPredicate initial = CharPredicates.range('A', 'Z')
                .or(
                        CharPredicates.range('a', 'z')
                ); // SpreadsheetFunctionName.INITIAL

        FUNCTION_NAME = Parsers.<SpreadsheetParserContext>stringInitialAndPartCharPredicate(
                initial,
                initial.or(CharPredicates.range('0', '9').or(CharPredicates.is('.'))), // SpreadsheetFunctionName.PART,
                1,
                SpreadsheetFunctionName.MAX_LENGTH)
                .transform(SpreadsheetParsers::transformFunctionName)
                .setToString(SpreadsheetFunctionName.class.getSimpleName());
    }

    private final static Parser<SpreadsheetParserContext> FUNCTION_NAME;

    private static ParserToken transformFunctionName(final ParserToken token, final SpreadsheetParserContext context) {
        return SpreadsheetParserToken.functionName(
                SpreadsheetFunctionName.with(token.cast(StringParserToken.class).value()),
                token.text()
        );
    }

    /**
     * Returns a {@link Parser} that parsers expression invocations, starting with the name and parameters.
     */
    public static Parser<SpreadsheetParserContext> functionParameters() {
        return FUNCTION_PARAMETERS_PARSER;
    }

    private static final Parser<SpreadsheetParserContext> FUNCTION_PARAMETERS_PARSER;

    /**
     * {@see SpreadsheetLabelNameParser}
     */
    public static Parser<SpreadsheetParserContext> labelName() {
        return SpreadsheetLabelNameParser.INSTANCE;
    }

    /**
     * Returns a {@link Parser} that parsers expression invocations, starting with the name and parameters.
     */
    public static Parser<SpreadsheetParserContext> lambdaFunction() {
        return LAMBDA_FUNCTION;
    }

    private static final Parser<SpreadsheetParserContext> LAMBDA_FUNCTION;

    /**
     * Returns a {@link Parser} that parsers expression invocations, starting with the name and parameters.
     */
    public static Parser<SpreadsheetParserContext> namedFunction() {
        return NAMED_FUNCTION_PARSER;
    }

    private static final Parser<SpreadsheetParserContext> NAMED_FUNCTION_PARSER;

    /**
     * {@see ParserSpreadsheetParser}
     */
    public static SpreadsheetParser parser(final Parser<SpreadsheetParserContext> parser) {
        return ParserSpreadsheetParser.with(parser);
    }

    /**
     * {@see SpreadsheetRowReferenceParser}
     */
    public static SpreadsheetParser row() {
        return SpreadsheetRowReferenceParser.INSTANCE;
    }

    // conditions.............................................................................................................

    private static void conditions(final Map<EbnfIdentifierName, Parser<SpreadsheetParserContext>> predefined) {
        predefined.put(EQUALS_SYMBOL_IDENTIFIER, EQUALS_SYMBOL);
        predefined.put(NOT_EQUALS_SYMBOL_IDENTIFIER, NOT_EQUALS_SYMBOL);

        predefined.put(GREATER_THAN_SYMBOL_IDENTIFIER, GREATER_THAN_SYMBOL);
        predefined.put(GREATER_THAN_EQUALS_SYMBOL_IDENTIFIER, GREATER_THAN_EQUALS_SYMBOL);
        predefined.put(LESS_THAN_SYMBOL_IDENTIFIER, LESS_THAN_SYMBOL);
        predefined.put(LESS_THAN_EQUALS_SYMBOL_IDENTIFIER, LESS_THAN_EQUALS_SYMBOL);
    }

    private static final Parser<SpreadsheetParserContext> EQUALS_SYMBOL = symbol(
            '=',
            SpreadsheetParserToken::equalsSymbol,
            SpreadsheetEqualsSymbolParserToken.class
    );
    private static final Parser<SpreadsheetParserContext> NOT_EQUALS_SYMBOL = symbol(
            "<>",
            SpreadsheetParserToken::notEqualsSymbol,
            SpreadsheetNotEqualsSymbolParserToken.class
    );
    private static final Parser<SpreadsheetParserContext> GREATER_THAN_SYMBOL = symbol(
            '>',
            SpreadsheetParserToken::greaterThanSymbol,
            SpreadsheetGreaterThanSymbolParserToken.class
    );
    private static final Parser<SpreadsheetParserContext> GREATER_THAN_EQUALS_SYMBOL = symbol(
            ">=",
            SpreadsheetParserToken::greaterThanEqualsSymbol,
            SpreadsheetGreaterThanEqualsSymbolParserToken.class
    );
    private static final Parser<SpreadsheetParserContext> LESS_THAN_SYMBOL = symbol(
            '<',
            SpreadsheetParserToken::lessThanSymbol,
            SpreadsheetLessThanSymbolParserToken.class
    );
    private static final Parser<SpreadsheetParserContext> LESS_THAN_EQUALS_SYMBOL = symbol(
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

    private static void math(final Map<EbnfIdentifierName, Parser<SpreadsheetParserContext>> predefined) {
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

    private static final Parser<SpreadsheetParserContext> PLUS_SYMBOL = symbol(
            '+',
            SpreadsheetParserToken::plusSymbol,
            SpreadsheetPlusSymbolParserToken.class
    );
    private static final Parser<SpreadsheetParserContext> NEGATIVE_SYMBOL = symbol(
            '-',
            SpreadsheetParserToken::minusSymbol,
            SpreadsheetMinusSymbolParserToken.class
    );
    private static final Parser<SpreadsheetParserContext> MULTIPLY_SYMBOL = symbol(
            '*',
            SpreadsheetParserToken::multiplySymbol,
            SpreadsheetMultiplySymbolParserToken.class
    );
    private static final Parser<SpreadsheetParserContext> DIVIDE_SYMBOL = symbol(
            '/',
            SpreadsheetParserToken::divideSymbol,
            SpreadsheetDivideSymbolParserToken.class
    );
    private static final Parser<SpreadsheetParserContext> POWER_SYMBOL = symbol(
            '^',
            SpreadsheetParserToken::powerSymbol,
            SpreadsheetPowerSymbolParserToken.class
    );

    // misc.............................................................................................................

    private static void misc(final Map<EbnfIdentifierName, Parser<SpreadsheetParserContext>> predefined) {
        predefined.put(APOSTROPHE_SYMBOL_IDENTIFIER, APOSTROPHE_SYMBOL);
        predefined.put(STRING_IDENTIFIER, STRING);

        predefined.put(ERROR_IDENTIFIER, error());

        predefined.put(FORMULA_EQUALS_SYMBOL_IDENTIFIER, FORMULA_EQUALS_SYMBOL);

        predefined.put(LAMBDA_FUNCTION_NAME_IDENTIFIER, LAMBDA_FUNCTION_NAME);

        predefined.put(PERCENT_SYMBOL_IDENTIFIER, PERCENT_SYMBOL);

        predefined.put(PARENTHESIS_OPEN_SYMBOL_IDENTIFIER, PARENTHESIS_OPEN_SYMBOL);
        predefined.put(PARENTHESIS_CLOSE_SYMBOL_IDENTIFIER, PARENTHESIS_CLOSE_SYMBOL);

        predefined.put(TEXT_IDENTIFIER, text());

        predefined.put(WHITESPACE_IDENTIFIER, whitespace());
    }

    /**
     * The leading apostrophe before a string literal within a formula.
     * <pre>
     * 'Everything after the single quote is a string, escaping of any kind is not supported only literal characters.
     * </pre>
     */
    private static final EbnfIdentifierName APOSTROPHE_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("APOSTROPHE_SYMBOL");
    private static final Parser<SpreadsheetParserContext> APOSTROPHE_SYMBOL = symbol(
            "'",
            SpreadsheetParserToken::apostropheSymbol,
            SpreadsheetApostropheSymbolParserToken.class
    );

    private static final EbnfIdentifierName STRING_IDENTIFIER = EbnfIdentifierName.with("STRING");
    private static final Parser<SpreadsheetParserContext> STRING = Parsers.<SpreadsheetParserContext>stringCharPredicate(
            CharPredicates.always(),
            1,
            65536
    ).transform(SpreadsheetParsers::transformString)
            .setToString(SpreadsheetTextLiteralParserToken.class.getSimpleName());

    private static ParserToken transformString(final ParserToken token,
                                               final SpreadsheetParserContext context) {
        return SpreadsheetParserToken.textLiteral(
                token.cast(StringParserToken.class).value(),
                token.text()
        );
    }

    private static final EbnfIdentifierName ERROR_IDENTIFIER = EbnfIdentifierName.with("ERROR");

    private static final EbnfIdentifierName FORMULA_EQUALS_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("FORMULA_EQUALS_SYMBOL");
    private static final Parser<SpreadsheetParserContext> FORMULA_EQUALS_SYMBOL = symbol(
            "=",
            SpreadsheetParserToken::equalsSymbol,
            SpreadsheetEqualsSymbolParserToken.class
    );

    private static final EbnfIdentifierName LAMBDA_FUNCTION_NAME_IDENTIFIER = EbnfIdentifierName.with("LAMBDA_FUNCTION_NAME");
    private static final Parser<SpreadsheetParserContext> LAMBDA_FUNCTION_NAME = Parsers.<SpreadsheetParserContext>string("lambda", CaseSensitivity.INSENSITIVE)
            .transform(SpreadsheetParsers::transformFunctionName)
            .setToString(SpreadsheetFunctionName.class.getSimpleName())
            .cast();

    private static final EbnfIdentifierName PERCENT_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("PERCENT_SYMBOL");
    private static final EbnfIdentifierName PARENTHESIS_OPEN_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("PARENTHESIS_OPEN_SYMBOL");
    private static final EbnfIdentifierName PARENTHESIS_CLOSE_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("PARENTHESIS_CLOSE_SYMBOL");
    private static final EbnfIdentifierName TEXT_IDENTIFIER = EbnfIdentifierName.with("TEXT");
    private static final EbnfIdentifierName WHITESPACE_IDENTIFIER = EbnfIdentifierName.with("WHITESPACE");

    private static final Parser<SpreadsheetParserContext> PERCENT_SYMBOL = symbol(
            '%',
            SpreadsheetParserToken::percentSymbol,
            SpreadsheetPercentSymbolParserToken.class
    );
    private static final Parser<SpreadsheetParserContext> PARENTHESIS_OPEN_SYMBOL = symbol(
            '(',
            SpreadsheetParserToken::parenthesisOpenSymbol,
            SpreadsheetParenthesisOpenSymbolParserToken.class
    );
    private static final Parser<SpreadsheetParserContext> PARENTHESIS_CLOSE_SYMBOL = symbol(
            ')',
            SpreadsheetParserToken::parenthesisCloseSymbol,
            SpreadsheetParenthesisCloseSymbolParserToken.class
    );

    /**
     * Text, parses excel style double quoted text, including escaped (triple) double quotes.
     */
    public static Parser<SpreadsheetParserContext> text() {
        return SpreadsheetDoubleQuotesParser.INSTANCE;
    }

    /**
     * Value literals such as apostrophe string, number, date, date-time, time or equals-sign and expression.* TODO <a href="https://github.com/mP1/walkingkooka-spreadsheet/issues/1254">default formula value parsing after apostrophe-string, date, datetime, number & time</a>
     */
    public static Parser<SpreadsheetParserContext> valueOrExpression(final Parser<SpreadsheetParserContext> value) {
        Objects.requireNonNull(value, "value");

        return resolveParsers(value)
                .get(VALUE_OR_EXPRESSION_IDENTIFIER)
                .transform(SpreadsheetParsers::transformValueOrExpression)
                .cast();
    }

    private static final EbnfIdentifierName VALUE_OR_EXPRESSION_IDENTIFIER = EbnfIdentifierName.with("VALUE_OR_EXPRESSION");

    /**
     * If the token is a {@link SequenceParserToken} then it needs to be wrapped inside an {@link SpreadsheetExpressionParserToken}.
     */
    private static ParserToken transformValueOrExpression(final ParserToken token, final SpreadsheetParserContext context) {
        final String text = token.text();
        return text.startsWith("=") ?
                SpreadsheetParserToken.expression(
                        token.cast(SequenceParserToken.class).value(),
                        text) :
                token;
    }

    /**
     * Whitespace
     */
    public static Parser<SpreadsheetParserContext> whitespace() {
        return WHITESPACE;
    }

    private final static Parser<SpreadsheetParserContext> WHITESPACE = Parsers.<SpreadsheetParserContext>stringCharPredicate(
            CharPredicates.whitespace(),
            1,
            Integer.MAX_VALUE
    ).transform(SpreadsheetParsers::transformWhitespace)
            .setToString(SpreadsheetWhitespaceParserToken.class.getSimpleName());

    private static ParserToken transformWhitespace(final ParserToken token,
                                                   final SpreadsheetParserContext context) {
        return SpreadsheetParserToken.whitespace(
                token.cast(StringParserToken.class).value(),
                token.text()
        );
    }

    // helpers .........................................................................................................

    /**
     * Loads the grammar text file.
     */
    private static EbnfGrammarParserToken loadGrammar() {
        final TextCursor grammarFile = TextCursors.charSequence(new SpreadsheetParsersGrammarProvider().text());

        return EbnfParserToken.grammarParser()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(grammarFile, EbnfParserContexts.basic())
                .orElseThrow(() -> new IllegalStateException("Unable to parse parsers grammar file."))
                .cast(EbnfGrammarParserToken.class);
    }

    private final static EbnfGrammarParserToken GRAMMAR_PARSER_TOKEN = loadGrammar();

    /**
     * Returns a {@link Map} of all parsers.
     */
    private static Map<EbnfIdentifierName, Parser<SpreadsheetParserContext>> resolveParsers(final Parser<SpreadsheetParserContext> value) {
        final Map<EbnfIdentifierName, Parser<SpreadsheetParserContext>> predefined = Maps.sorted();

        cellOrCellRangeOrLabel(predefined);
        conditions(predefined);
        functions(predefined);
        math(predefined);
        misc(predefined);

        predefined.put(
                NUMBER_IDENTIFIER,
                SpreadsheetPattern.parseNumberParsePattern("#.#E+#;#.#%;#.#;#%;#").expressionParser() //
        );
        predefined.put(VALUE_IDENTIFIER, value.setToString(VALUE_IDENTIFIER.toString()));

        return GRAMMAR_PARSER_TOKEN
                .combinator(predefined, SpreadsheetParsersEbnfParserCombinatorSyntaxTreeTransformer.INSTANCE);
    }

    private static final EbnfIdentifierName NUMBER_IDENTIFIER = EbnfIdentifierName.with("NUMBER");
    private static final EbnfIdentifierName VALUE_IDENTIFIER = EbnfIdentifierName.with("VALUE");

    /*
     * Processes the grammar and sets all parsers that have static fields.
     */
    static {
        final Map<EbnfIdentifierName, Parser<SpreadsheetParserContext>> parsers = resolveParsers(Parsers.fake());

        CELL_OR_CELL_RANGE_OR_LABEL_PARSER = parsers.get(EbnfIdentifierName.with("CELL_OR_CELL_RANGE_OR_LABEL"));
        CELL_RANGE_PARSER = parsers.get(EbnfIdentifierName.with("CELL_RANGE"));
        EXPRESSION_PARSER = parsers.get(EbnfIdentifierName.with("EXPRESSION"));
        FUNCTION_PARAMETERS_PARSER = parsers.get(EbnfIdentifierName.with("FUNCTION_PARAMETERS"));
        LAMBDA_FUNCTION = parsers.get(EbnfIdentifierName.with("LAMBDA_FUNCTION"));
        NAMED_FUNCTION_PARSER = parsers.get(EbnfIdentifierName.with("NAMED_FUNCTION"));
    }

    private static Parser<SpreadsheetParserContext> symbol(final char c,
                                                           final BiFunction<String, String, ParserToken> factory,
                                                           final Class<? extends SpreadsheetSymbolParserToken> tokenClass) {
        return Parsers.character(CharPredicates.is(c))
                .transform((charParserToken, context) -> factory.apply(charParserToken.cast(CharacterParserToken.class).value().toString(), charParserToken.text()))
                .setToString(tokenClass.getSimpleName())
                .cast();
    }

    private static Parser<SpreadsheetParserContext> symbol(final String text,
                                                           final BiFunction<String, String, ParserToken> factory,
                                                           final Class<? extends SpreadsheetSymbolParserToken> tokenClass) {
        return Parsers.string(text, CaseSensitivity.INSENSITIVE)
                .transform((stringParserToken, context) -> factory.apply(stringParserToken.cast(StringParserToken.class).value(), stringParserToken.text()))
                .setToString(tokenClass.getSimpleName())
                .cast();
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

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
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.expression.SpreadsheetFunctionName;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.template.TemplateValueName;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.cursor.parser.CharacterParserToken;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.cursor.parser.SequenceParserToken;
import walkingkooka.text.cursor.parser.StringParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfIdentifierName;
import walkingkooka.text.cursor.parser.ebnf.GrammarEbnfParserToken;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Numerous {@link Parser parsers} that parse individual tokens of a formula or an entire formula.
 */
public final class SpreadsheetFormulaParsers implements PublicStaticHelper {

    static {
        TemplateValueName.with("Force-json-registry");
    }

    /**
     * Range separator character used to separate the lower and upper bounds.
     */
    public static final CharacterConstant RANGE_SEPARATOR = CharacterConstant.with(':');

    private static final EbnfIdentifierName CELL_IDENTIFIER = EbnfIdentifierName.with("CELL");
    private static final EbnfIdentifierName LABEL_NAME_IDENTIFIER = EbnfIdentifierName.with("LABEL_NAME");
    private static final EbnfIdentifierName BETWEEN_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("BETWEEN_SYMBOL");

    /**
     * A {@link SpreadsheetParser} that knows how to parse a cell reference, but not cell-range or label.
     */
    public static SpreadsheetParser cell() {
        return CELL;
    }

    private static final SpreadsheetParser CELL = SpreadsheetParsers.parser(
                    column()
                            .and(row())
                            .transform(SpreadsheetFormulaParsers::transformCell)
                            .setToString(CELL_IDENTIFIER.value())
            );

    private static ParserToken transformCell(final ParserToken token,
                                             final SpreadsheetParserContext context) {
        return SpreadsheetFormulaParserToken.cellReference(
                token.cast(SequenceParserToken.class)
                        .value(),
                token.text()
        );
    }

    /**
     * A {@link SpreadsheetParser} that returns a cell reference token of some sort.
     */
    public static SpreadsheetParser cellOrCellRangeOrLabel() {
        return CELL_OR_CELL_RANGE_OR_LABEL_PARSER;
    }

    private final static SpreadsheetParser CELL_OR_CELL_RANGE_OR_LABEL_PARSER;

    private static void cellOrCellRangeOrLabel(final Map<EbnfIdentifierName, Parser<SpreadsheetParserContext>> predefined) {
        predefined.put(CELL_IDENTIFIER, CELL);
        predefined.put(LABEL_NAME_IDENTIFIER, labelName());
        predefined.put(BETWEEN_SYMBOL_IDENTIFIER, BETWEEN_SYMBOL);
    }

    private static final Parser<SpreadsheetParserContext> BETWEEN_SYMBOL = symbol(
            RANGE_SEPARATOR.character(),
            SpreadsheetFormulaParserToken::betweenSymbol
    );

    /**
     * A {@link SpreadsheetParser} that returns a range but not cell reference or labels.
     */
    public static SpreadsheetParser cellRange() {
        return CELL_RANGE_PARSER;
    }

    private final static SpreadsheetParser CELL_RANGE_PARSER;

    /**
     * {@see SpreadsheetColumnReferenceSpreadsheetParser}
     */
    public static SpreadsheetParser column() {
        return SpreadsheetColumnReferenceSpreadsheetParser.INSTANCE;
    }

    /**
     * Returns a {@link SpreadsheetParser} that matches any of the condition operators including the RHS value or expression.
     * <pre>
     * &lt; 10
     * &gt; function( 1, 2, 3 )
     * </pre>
     * This is not intended to be used to parse formulas, but rather as component or placeholder within a larger expression template.
     */
    public static SpreadsheetParser conditionRight(final Parser<SpreadsheetParserContext> value) {
        Objects.requireNonNull(value, "value");

        return SpreadsheetParsers.parser(
                resolveParsers(value)
                        .apply(CONDITION_RIGHT_PARSER_IDENTIFIER)
                        .transform(SpreadsheetFormulaParsers::transformConditionRight)
        );
    }

    private static final EbnfIdentifierName CONDITION_RIGHT_PARSER_IDENTIFIER = EbnfIdentifierName.with("CONDITION_RIGHT");

    /**
     * Expects a {@link SequenceParserToken} then tests the symbol and creates the matching sub-classes of {@link ConditionRightSpreadsheetFormulaParserToken}.
     */
    private static ParserToken transformConditionRight(final ParserToken conditionRight, final SpreadsheetParserContext context) {
        final SequenceParserToken sequenceParserToken = conditionRight.cast(SequenceParserToken.class)
                .flat();
        final List<ParserToken> tokens = sequenceParserToken.children();

        ParserToken symbol = null;
        ParserToken parameter = null;

        for (final ParserToken token : tokens) {
            if (token.isWhitespace()) {
                continue;
            }

            if (null == symbol) {
                if (token.isSymbol()) {
                    symbol = token;
                    continue;
                }
            }

            if (null == parameter) {
                if (false == token.isSymbol()) {
                    parameter = token;
                }
                continue;
            }

            parameter = null;
            break;
        }

        final BiFunction<List<ParserToken>, String, ParserToken> factory;

        final String operatorText = symbol.text();
        switch (operatorText) {
            case EQUALS_SIGN:
                factory = SpreadsheetFormulaParserToken::conditionRightEquals;
                break;
            case LESS_THAN_SIGN:
                factory = SpreadsheetFormulaParserToken::conditionRightLessThan;
                break;
            case LESS_THAN_EQUALS_SIGN:
                factory = SpreadsheetFormulaParserToken::conditionRightLessThanEquals;
                break;
            case GREATER_THAN_SIGN:
                factory = SpreadsheetFormulaParserToken::conditionRightGreaterThan;
                break;
            case GREATER_THAN_EQUALS_SIGN:
                factory = SpreadsheetFormulaParserToken::conditionRightGreaterThanEquals;
                break;
            case NOT_EQUALS_SIGN:
                factory = SpreadsheetFormulaParserToken::conditionRightNotEquals;
                break;
            default:
                throw new IllegalArgumentException("Unknown operator " + CharSequences.quoteIfChars(operatorText));
        }

        return factory.apply(
                tokens,
                sequenceParserToken.text()
        );
    }

    private final static String EQUALS_SIGN = "=";

    private final static String NOT_EQUALS_SIGN = "<>";

    private final static String LESS_THAN_SIGN = "<";

    private final static String LESS_THAN_EQUALS_SIGN = "<=";

    private final static String GREATER_THAN_SIGN = ">";

    private final static String GREATER_THAN_EQUALS_SIGN = ">=";

    /**
     * Returns a {@link Parser} that parsers errors such as <code>#REF!</code>.
     */
    public static SpreadsheetParser error() {
        return ERROR_PARSER;
    }

    private static final SpreadsheetParser ERROR_PARSER = errorParser();

    private static SpreadsheetParser errorParser() {
        return SpreadsheetParsers.parser(
                Parsers.alternatives(
                        Arrays.stream(SpreadsheetErrorKind.values())
                                .map(SpreadsheetFormulaParsers::errorParser0)
                                .collect(Collectors.toList())
                )
        );
    }

    /**
     * Creates a {@link Parser} that matches the {@link SpreadsheetErrorKind#text()} and returns a {@link SpreadsheetFormulaParserToken#error(SpreadsheetError, String)}.
     */
    private static Parser<SpreadsheetParserContext> errorParser0(final SpreadsheetErrorKind kind) {
        final String text = kind.text();
        final SpreadsheetError error = kind.toError();
        final SpreadsheetFormulaParserToken token = SpreadsheetFormulaParserToken.error(error, text);

        return Parsers.string(
                        text,
                        CaseSensitivity.SENSITIVE
                ).transform((p, c) -> token)
                .cast();
    }

    /**
     * Returns a {@link SpreadsheetParser} that parses expressions.
     */
    public static SpreadsheetParser expression() {
        return EXPRESSION_PARSER;
    }

    private static final SpreadsheetParser EXPRESSION_PARSER;

    private static void functions(final Map<EbnfIdentifierName, Parser<SpreadsheetParserContext>> predefined) {
        predefined.put(FUNCTION_NAME_IDENTIFIER, functionName());
        predefined.put(VALUE_SEPARATOR_SYMBOL_IDENTIFIER, SpreadsheetFormulaParsersValueSeparatorParser.INSTANCE);
    }

    private static final EbnfIdentifierName FUNCTION_NAME_IDENTIFIER = EbnfIdentifierName.with("FUNCTION_NAME");
    private static final EbnfIdentifierName VALUE_SEPARATOR_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("VALUE_SEPARATOR_SYMBOL");

    /**
     * A {@link SpreadsheetParser} that matches {@see SpreadsheetFunctionName}
     */
    public static SpreadsheetParser functionName() {
        return FUNCTION_NAME;
    }

    private final static SpreadsheetParser FUNCTION_NAME = SpreadsheetParsers.parser(
            Parsers.<SpreadsheetParserContext>initialAndPartCharPredicateString(
                            SpreadsheetFunctionName.INITIAL,
                            SpreadsheetFunctionName.PART,
                            SpreadsheetFunctionName.MIN_LENGTH,
                            SpreadsheetFunctionName.MAX_LENGTH)
                    .transform(SpreadsheetFormulaParsers::transformFunctionName)
                    .setToString(FUNCTION_NAME_IDENTIFIER.value())
    );

    private static ParserToken transformFunctionName(final ParserToken token, final SpreadsheetParserContext context) {
        return SpreadsheetFormulaParserToken.functionName(
                SpreadsheetFunctionName.with(token.cast(StringParserToken.class).value()),
                token.text()
        );
    }

    /**
     * Returns a {@link SpreadsheetParser} that matches a function including its parameters
     */
    public static SpreadsheetParser functionParameters() {
        return FUNCTION_PARAMETERS_PARSER;
    }

    private static final SpreadsheetParser FUNCTION_PARAMETERS_PARSER;

    /**
     * {@see SpreadsheetLabelNameSpreadsheetParser}
     */
    public static SpreadsheetParser labelName() {
        return SpreadsheetLabelNameSpreadsheetParser.INSTANCE;
    }

    /**
     * Returns a {@link SpreadsheetParser} that matches a lambda function
     */
    public static SpreadsheetParser lambdaFunction() {
        return LAMBDA_FUNCTION;
    }

    private static final SpreadsheetParser LAMBDA_FUNCTION;

    /**
     * Returns a {@link SpreadsheetParser} that matches a named function and its parameters.
     */
    public static SpreadsheetParser namedFunction() {
        return NAMED_FUNCTION_PARSER;
    }

    private static final SpreadsheetParser NAMED_FUNCTION_PARSER;

    /**
     * {@see SpreadsheetRowReferenceSpreadsheetParser}
     */
    public static SpreadsheetParser row() {
        return SpreadsheetRowReferenceSpreadsheetParser.INSTANCE;
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
            EQUALS_SIGN,
            SpreadsheetFormulaParserToken::equalsSymbol
    );
    private static final Parser<SpreadsheetParserContext> NOT_EQUALS_SYMBOL = symbol(
            NOT_EQUALS_SIGN,
            SpreadsheetFormulaParserToken::notEqualsSymbol
    );
    private static final Parser<SpreadsheetParserContext> GREATER_THAN_SYMBOL = symbol(
            GREATER_THAN_SIGN,
            SpreadsheetFormulaParserToken::greaterThanSymbol
    );
    private static final Parser<SpreadsheetParserContext> GREATER_THAN_EQUALS_SYMBOL = symbol(
            GREATER_THAN_EQUALS_SIGN,
            SpreadsheetFormulaParserToken::greaterThanEqualsSymbol
    );
    private static final Parser<SpreadsheetParserContext> LESS_THAN_SYMBOL = symbol(
            LESS_THAN_SIGN,
            SpreadsheetFormulaParserToken::lessThanSymbol
    );
    private static final Parser<SpreadsheetParserContext> LESS_THAN_EQUALS_SYMBOL = symbol(
            LESS_THAN_EQUALS_SIGN,
            SpreadsheetFormulaParserToken::lessThanEqualsSymbol
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
        predefined.put(MINUS_SYMBOL_IDENTIFIER, MINUS_SYMBOL);

        predefined.put(MULTIPLY_SYMBOL_IDENTIFIER, MULTIPLY_SYMBOL);
        predefined.put(DIVIDE_SYMBOL_IDENTIFIER, DIVIDE_SYMBOL);

        predefined.put(POWER_SYMBOL_IDENTIFIER, POWER_SYMBOL);
    }

    private static final EbnfIdentifierName PLUS_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("PLUS_SYMBOL");
    private static final EbnfIdentifierName MINUS_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("MINUS_SYMBOL");
    private static final EbnfIdentifierName MULTIPLY_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("MULTIPLY_SYMBOL");
    private static final EbnfIdentifierName DIVIDE_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("DIVIDE_SYMBOL");
    private static final EbnfIdentifierName POWER_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("POWER_SYMBOL");

    private static final Parser<SpreadsheetParserContext> PLUS_SYMBOL = symbol(
            '+',
            SpreadsheetFormulaParserToken::plusSymbol
    );
    private static final Parser<SpreadsheetParserContext> MINUS_SYMBOL = symbol(
            '-',
            SpreadsheetFormulaParserToken::minusSymbol
    );
    private static final Parser<SpreadsheetParserContext> MULTIPLY_SYMBOL = symbol(
            '*',
            SpreadsheetFormulaParserToken::multiplySymbol
    );
    private static final Parser<SpreadsheetParserContext> DIVIDE_SYMBOL = symbol(
            '/',
            SpreadsheetFormulaParserToken::divideSymbol
    );
    private static final Parser<SpreadsheetParserContext> POWER_SYMBOL = symbol(
            '^',
            SpreadsheetFormulaParserToken::powerSymbol
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
            SpreadsheetFormulaParserToken::apostropheSymbol
    );

    private static final EbnfIdentifierName STRING_IDENTIFIER = EbnfIdentifierName.with("STRING");
    private static final Parser<SpreadsheetParserContext> STRING = Parsers.<SpreadsheetParserContext>charPredicateString(
                    CharPredicates.always(),
                    1,
                    65536
            ).transform(SpreadsheetFormulaParsers::transformString)
            .setToString(STRING_IDENTIFIER.value());

    private static ParserToken transformString(final ParserToken token,
                                               final SpreadsheetParserContext context) {
        return SpreadsheetFormulaParserToken.textLiteral(
                token.cast(StringParserToken.class).value(),
                token.text()
        );
    }

    private static final EbnfIdentifierName ERROR_IDENTIFIER = EbnfIdentifierName.with("ERROR");

    private static final EbnfIdentifierName FORMULA_EQUALS_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("FORMULA_EQUALS_SYMBOL");
    private static final Parser<SpreadsheetParserContext> FORMULA_EQUALS_SYMBOL = symbol(
            EQUALS_SIGN,
            SpreadsheetFormulaParserToken::equalsSymbol
    );

    private static final EbnfIdentifierName LAMBDA_FUNCTION_NAME_IDENTIFIER = EbnfIdentifierName.with("LAMBDA_FUNCTION_NAME");
    private static final Parser<SpreadsheetParserContext> LAMBDA_FUNCTION_NAME = Parsers.<SpreadsheetParserContext>string("lambda", CaseSensitivity.INSENSITIVE)
            .transform(SpreadsheetFormulaParsers::transformFunctionName)
            .setToString(LAMBDA_FUNCTION_NAME_IDENTIFIER.value())
            .cast();

    private static final EbnfIdentifierName PERCENT_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("PERCENT_SYMBOL");
    private static final EbnfIdentifierName PARENTHESIS_OPEN_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("PARENTHESIS_OPEN_SYMBOL");
    private static final EbnfIdentifierName PARENTHESIS_CLOSE_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("PARENTHESIS_CLOSE_SYMBOL");
    private static final EbnfIdentifierName TEXT_IDENTIFIER = EbnfIdentifierName.with("TEXT");
    private static final EbnfIdentifierName WHITESPACE_IDENTIFIER = EbnfIdentifierName.with("WHITESPACE");

    private static final Parser<SpreadsheetParserContext> PERCENT_SYMBOL = symbol(
            '%',
            SpreadsheetFormulaParserToken::percentSymbol
    );
    private static final Parser<SpreadsheetParserContext> PARENTHESIS_OPEN_SYMBOL = symbol(
            '(',
            SpreadsheetFormulaParserToken::parenthesisOpenSymbol
    );
    private static final Parser<SpreadsheetParserContext> PARENTHESIS_CLOSE_SYMBOL = symbol(
            ')',
            SpreadsheetFormulaParserToken::parenthesisCloseSymbol
    );

    /**
     * A {@link SpreadsheetParser} that parses excel style double quoted text, including escaped (triple) double quotes.
     */
    public static Parser<SpreadsheetParserContext> text() {
        return TEXT;
    }

    private final static Parser<SpreadsheetParserContext> TEXT = SpreadsheetDoubleQuotesParser.INSTANCE.setToString(TEXT_IDENTIFIER.value());

    /**
     * Value literals such as apostrophe string, number, date, date-time, time or equals-sign and expression.
     * <pre>
     * 'String or text
     * 1.234 // number matched by the number parser
     * 31/12/1999 // matched using the date parser
     * 31/12/1999 12:58:59 // matched using the date-time parser
     * 12:58:59 // matched using the time parser
     * </pre>
     */
    public static SpreadsheetParser valueOrExpression(final Parser<SpreadsheetParserContext> value) {
        Objects.requireNonNull(value, "value");

        return SpreadsheetParsers.parser(
                resolveParsers(value)
                        .apply(VALUE_OR_EXPRESSION_IDENTIFIER)
                        .transform(SpreadsheetFormulaParsers::transformValueOrExpression)
        );
    }

    private static final EbnfIdentifierName VALUE_OR_EXPRESSION_IDENTIFIER = EbnfIdentifierName.with("VALUE_OR_EXPRESSION");

    /**
     * If the token is a {@link SequenceParserToken} then it needs to be wrapped inside an {@link ExpressionSpreadsheetFormulaParserToken}.
     */
    private static ParserToken transformValueOrExpression(final ParserToken token, final SpreadsheetParserContext context) {
        final String text = token.text();
        return text.startsWith(EQUALS_SIGN) ?
                SpreadsheetFormulaParserToken.expression(
                        token.cast(SequenceParserToken.class).value(),
                        text) :
                token;
    }

    /**
     * Whitespace
     */
    public static SpreadsheetParser whitespace() {
        return WHITESPACE;
    }

    private final static SpreadsheetParser WHITESPACE = SpreadsheetParsers.parser(
            Parsers.<SpreadsheetParserContext>charPredicateString(
                            CharPredicates.whitespace(),
                            1,
                            Integer.MAX_VALUE
                    ).transform(SpreadsheetFormulaParsers::transformWhitespace)
                    .setToString(WHITESPACE_IDENTIFIER.value())
    );

    private static ParserToken transformWhitespace(final ParserToken token,
                                                   final SpreadsheetParserContext context) {
        return SpreadsheetFormulaParserToken.whitespace(
                token.cast(StringParserToken.class).value(),
                token.text()
        );
    }

    // helpers .........................................................................................................

    private final static String FILENAME = SpreadsheetFormulaParsers.class.getSimpleName() + "Grammar.txt";

    private final static GrammarEbnfParserToken GRAMMAR_PARSER_TOKEN = GrammarEbnfParserToken.parseFile(
            new SpreadsheetFormulaParsersGrammarProvider().text(),
            FILENAME
    );

    /**
     * Returns a {@link Map} of all parsers.
     */
    private static Function<EbnfIdentifierName, Parser<SpreadsheetParserContext>> resolveParsers(final Parser<SpreadsheetParserContext> value) {
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
                .combinatorForFile(
                        (n) -> Optional.ofNullable(
                                predefined.get(n)
                        ),
                        SpreadsheetFormulaParsersEbnfParserCombinatorGrammarTransformer.create(),
                        FILENAME
                );
    }

    private static final EbnfIdentifierName NUMBER_IDENTIFIER = EbnfIdentifierName.with("NUMBER");
    private static final EbnfIdentifierName VALUE_IDENTIFIER = EbnfIdentifierName.with("VALUE");

    /*
     * Processes the grammar and sets all parsers that have static fields.
     */
    static {
        final Function<EbnfIdentifierName, Parser<SpreadsheetParserContext>> parsers = resolveParsers(Parsers.fake());
        final Function<String, SpreadsheetParser> getSpreadsheetParser = (name) ->
                SpreadsheetParsers.parser(
                        parsers.apply(
                                EbnfIdentifierName.with(name)
                        ).setToString(name)
                );

        CELL_OR_CELL_RANGE_OR_LABEL_PARSER = getSpreadsheetParser.apply("CELL_OR_CELL_RANGE_OR_LABEL");
        CELL_RANGE_PARSER = getSpreadsheetParser.apply("CELL_RANGE");
        EXPRESSION_PARSER = getSpreadsheetParser.apply("EXPRESSION");
        FUNCTION_PARAMETERS_PARSER = getSpreadsheetParser.apply("FUNCTION_PARAMETERS");
        LAMBDA_FUNCTION = getSpreadsheetParser.apply("LAMBDA_FUNCTION");
        NAMED_FUNCTION_PARSER = getSpreadsheetParser.apply("NAMED_FUNCTION");
    }

    private static Parser<SpreadsheetParserContext> symbol(final char c,
                                                           final BiFunction<String, String, ParserToken> factory) {
        return Parsers.character(CharPredicates.is(c))
                .transform((charParserToken, context) -> factory.apply(charParserToken.cast(CharacterParserToken.class).value().toString(), charParserToken.text()))
                .setToString(
                        CharSequences.quoteAndEscape(
                                String.valueOf(c)
                        ).toString()
                )
                .cast();
    }

    private static Parser<SpreadsheetParserContext> symbol(final String text,
                                                           final BiFunction<String, String, ParserToken> factory) {
        return text.length() == 1 ?
                symbol(
                        text.charAt(0),
                        factory
                ) :
                Parsers.string(text, CaseSensitivity.INSENSITIVE)
                        .transform((stringParserToken, context) -> factory.apply(stringParserToken.cast(StringParserToken.class).value(), stringParserToken.text()))
                        .setToString(CharSequences.quoteAndEscape(text).toString())
                        .cast();
    }

    /**
     * Given a column letter such as A or B, returns the numeric column number, so A returns 0, B = 2 etc.
     */
    public static int columnLetterValue(final char c) {
        final int digit = Character.toUpperCase(c) - 'A';
        return digit >= 0 &&
                digit < SpreadsheetColumnReferenceSpreadsheetParser.RADIX ?
                digit + 1 :
                -1;
    }

    /**
     * Stop construction
     */
    private SpreadsheetFormulaParsers() {
        throw new UnsupportedOperationException();
    }
}

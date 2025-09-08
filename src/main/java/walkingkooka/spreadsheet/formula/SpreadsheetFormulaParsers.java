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

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.SpreadsheetStrings;
import walkingkooka.spreadsheet.SpreadsheetValueType;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.expression.SpreadsheetFunctionName;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.formula.parser.ConditionRightSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.ExpressionSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
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

    // cell, cell-range, label, cell-or-cell-range-or-label.............................................................

    private final static EbnfIdentifierName REFERENCE = EbnfIdentifierName.with("REFERENCE");

    static {
        final EbnfIdentifierName whitespaceIdentifier = EbnfIdentifierName.with("WHITESPACE");
        WHITESPACE_IDENTIFIER = whitespaceIdentifier;

        final SpreadsheetParser whitespace = SpreadsheetParsers.parser(
            Parsers.<SpreadsheetParserContext>charPredicateString(
                    CharPredicates.whitespace(),
                    1,
                    Integer.MAX_VALUE
                ).transform(SpreadsheetFormulaParsers::transformWhitespace)
                .setToString(whitespaceIdentifier.value()),
            SpreadsheetParser.NO_VALUE_TYPE
        );
        WHITESPACE = whitespace;

        final SpreadsheetParser cell = SpreadsheetParsers.parser(
            column()
                .and(row())
                .transform(SpreadsheetFormulaParsers::transformCell)
                .setToString("CELL"),
            Optional.of(SpreadsheetValueType.CELL)
        );
        CELL = cell;

        final Parser<SpreadsheetParserContext> betweenSymbol = symbol(
            RANGE_SEPARATOR.character(),
            SpreadsheetFormulaParserToken::betweenSymbol
        );

        // CELL_RANGE = CELL, [ WHITESPACE ], BETWEEN_SYMBOL, [ WHITESPACE ], CELL;
        final SpreadsheetParser cellRange = SpreadsheetParsers.parser(
            cell.and(whitespace.optional())
                .and(betweenSymbol)
                .and(whitespace.optional())
                .and(cell)
                .transform(SpreadsheetFormulaParsers::transformCellRange)
                .setToString("CELL_RANGE"),
            Optional.of(SpreadsheetValueType.CELL)
        );

        CELL_RANGE = cellRange;

        // CELL_OR_CELL_RANGE_OR_LABEL = LABEL_NAME | CELL_RANGE | CELL;
        CELL_OR_CELL_RANGE_OR_LABEL = SpreadsheetParsers.parser(
            labelName()
                .or(cellRange)
                .or(cell),
            //.setToString("CELL_OR_CELL_RANGE_OR_LABEL")
            Optional.of(SpreadsheetValueType.CELL)
        );
    }

    // cell.............................................................................................................

    /**
     * A {@link SpreadsheetParser} that knows how to parse a cell reference, but not cell-range or label.
     */
    public static SpreadsheetParser cell() {
        return CELL;
    }

    private static final SpreadsheetParser CELL;

    private static ParserToken transformCell(final ParserToken token,
                                             final SpreadsheetParserContext context) {
        return SpreadsheetFormulaParserToken.cell(
            token.cast(SequenceParserToken.class)
                .value(),
            token.text()
        );
    }

    // cellOrCellRangeOrLabel...........................................................................................

    /**
     * A {@link SpreadsheetParser} that returns a cell reference token of some sort, such as a label/cell-range or cell.
     */
    public static SpreadsheetParser cellOrCellRangeOrLabel() {
        return CELL_OR_CELL_RANGE_OR_LABEL;
    }

    private final static SpreadsheetParser CELL_OR_CELL_RANGE_OR_LABEL;

    // cellRange........................................................................................................

    /**
     * A {@link SpreadsheetParser} that returns a range but not cell reference or labels.
     */
    public static SpreadsheetParser cellRange() {
        return CELL_RANGE;
    }

    private final static SpreadsheetParser CELL_RANGE;

    private static ParserToken transformCellRange(final ParserToken token,
                                                  final SpreadsheetParserContext context) {
        return SpreadsheetFormulaParserToken.cellRange(
            token.cast(SequenceParserToken.class)
                .value(),
            token.text()
        );
    }

    // column...........................................................................................................

    /**
     * {@see SpreadsheetColumnReferenceSpreadsheetParser}
     */
    public static SpreadsheetParser column() {
        return SpreadsheetColumnReferenceSpreadsheetParser.INSTANCE;
    }

    // conditionRight...................................................................................................

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
                .transform(SpreadsheetFormulaParsers::transformConditionRight),
            Optional.of(SpreadsheetValueType.CONDITION)
        );
    }

    private static final EbnfIdentifierName CONDITION_RIGHT_PARSER_IDENTIFIER = EbnfIdentifierName.with("CONDITION_RIGHT");

    /**
     * Expects a {@link SequenceParserToken} then tests the symbol and creates the matching subclasses of {@link ConditionRightSpreadsheetFormulaParserToken}.
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
            ),
            Optional.of(SpreadsheetValueType.ERROR)
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
            .setToString(FUNCTION_NAME_IDENTIFIER.value()),
        SpreadsheetParser.NO_VALUE_TYPE
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

    /**
     * Returns a {@link SpreadsheetParser} that parses expressions.
     */
    public static SpreadsheetParser templateExpression() {
        return TEMPLATE_EXPRESSION_PARSER;
    }

    private static final SpreadsheetParser TEMPLATE_EXPRESSION_PARSER;

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
    private static final Parser<SpreadsheetParserContext> LAMBDA_FUNCTION_NAME = Parsers.<SpreadsheetParserContext>string(
            "lambda",
            SpreadsheetExpressionFunctions.NAME_CASE_SENSITIVITY
        ).transform(SpreadsheetFormulaParsers::transformFunctionName)
        .setToString(LAMBDA_FUNCTION_NAME_IDENTIFIER.value())
        .cast();

    private static final EbnfIdentifierName PERCENT_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("PERCENT_SYMBOL");
    private static final EbnfIdentifierName PARENTHESIS_OPEN_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("PARENTHESIS_OPEN_SYMBOL");
    private static final EbnfIdentifierName PARENTHESIS_CLOSE_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("PARENTHESIS_CLOSE_SYMBOL");
    private static final EbnfIdentifierName TEXT_IDENTIFIER = EbnfIdentifierName.with("TEXT");
    private static final EbnfIdentifierName WHITESPACE_IDENTIFIER; // must be set very early

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
     * A {@link SpreadsheetParser} that parses excel style double-quoted text, including escaped (triple) double quotes.
     */
    public static Parser<SpreadsheetParserContext> text() {
        return TEXT;
    }

    private final static Parser<SpreadsheetParserContext> TEXT = SpreadsheetDoubleQuotesParser.INSTANCE.setToString(TEXT_IDENTIFIER.value());

    /**
     * Value literals such as apostrophe string, boolean literals "true" or "false", number, date, date-time, time or equals-sign and expression.
     * <pre>
     * 'String or text
     * true // boolean true
     * false // boolean false
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
                .transform(SpreadsheetFormulaParsers::transformValueOrExpression),
            Optional.of(SpreadsheetValueType.VALUE_OR_EXPRESSION)
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

    private final static SpreadsheetParser WHITESPACE;

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
        return resolveParsers(
            CELL_OR_CELL_RANGE_OR_LABEL,
            value
        );
    }

    /**
     * Returns a {@link Map} of all parsers.
     */
    private static Function<EbnfIdentifierName, Parser<SpreadsheetParserContext>> resolveParsers(final Parser<SpreadsheetParserContext> references,
                                                                                                 final Parser<SpreadsheetParserContext> value) {
        final Map<EbnfIdentifierName, Parser<SpreadsheetParserContext>> predefined = Maps.sorted();

        conditions(predefined);
        functions(predefined);
        math(predefined);
        misc(predefined);

        predefined.put(
            REFERENCE,
            references
        );

        predefined.put(
            BOOLEAN_IDENTIFIER,
            BOOLEAN
        );
        predefined.put(
            NUMBER_IDENTIFIER,
            SpreadsheetPattern.parseNumberParsePattern("#.#E+#;#.#%;#.#;#%;#").expressionParser() //
        );
        predefined.put(
            VALUE_IDENTIFIER,
            value.setToString(
                VALUE_IDENTIFIER.toString()
            )
        );

        return GRAMMAR_PARSER_TOKEN
            .combinatorForFile(
                (n) -> Optional.ofNullable(
                    predefined.get(n)
                ),
                SpreadsheetFormulaParsersEbnfParserCombinatorGrammarTransformer.create(),
                FILENAME
            );
    }

    private static final EbnfIdentifierName BOOLEAN_IDENTIFIER = EbnfIdentifierName.with("BOOLEAN");
    private static final EbnfIdentifierName NUMBER_IDENTIFIER = EbnfIdentifierName.with("NUMBER");
    private static final EbnfIdentifierName VALUE_IDENTIFIER = EbnfIdentifierName.with("VALUE");

    /**
     * Handles matching boolean literals "true" or "false".
     */
    private final static Parser<SpreadsheetParserContext> BOOLEAN = symbol(
        SpreadsheetStrings.BOOLEAN_TRUE,
        SpreadsheetFormulaParsers::transformBoolean
    ).or(
        symbol(
            SpreadsheetStrings.BOOLEAN_FALSE,
            SpreadsheetFormulaParsers::transformBoolean
        )
    ).setToString(BOOLEAN_IDENTIFIER.toString());

    private static SpreadsheetFormulaParserToken transformBoolean(final String text,
                                                                  final String value) {
        return SpreadsheetFormulaParserToken.booleanValue(
            Lists.of(
                SpreadsheetFormulaParserToken.booleanLiteral(
                    Boolean.parseBoolean(text),
                    value
                )
            ),
            text
        );
    }

    /*
     * Processes the grammar and sets all parsers that have static fields.
     */
    static {
        final Function<EbnfIdentifierName, Parser<SpreadsheetParserContext>> parsers = resolveParsers(Parsers.fake());
        final Function<String, SpreadsheetParser> getSpreadsheetParser = (name) ->
            SpreadsheetParsers.parser(
                parsers.apply(
                    EbnfIdentifierName.with(name)
                ).setToString(name),
                SpreadsheetParser.NO_VALUE_TYPE
            );

        EXPRESSION_PARSER = getSpreadsheetParser.apply("EXPRESSION");
        FUNCTION_PARAMETERS_PARSER = getSpreadsheetParser.apply("FUNCTION_PARAMETERS");
        LAMBDA_FUNCTION = getSpreadsheetParser.apply("LAMBDA_FUNCTION");
        NAMED_FUNCTION_PARSER = getSpreadsheetParser.apply("NAMED_FUNCTION");
    }

    static {
        final Function<EbnfIdentifierName, Parser<SpreadsheetParserContext>> parsers = resolveParsers(
            SpreadsheetParsers.parser(
                TemplateValueName.PARSER.setToString("TEMPLATE_VALUE_NAME")
                    .cast(),
                Optional.of(SpreadsheetValueType.TEMPLATE_VALUE_NAME)
            ).transform(
                (t, x) -> SpreadsheetFormulaParserToken.templateValueName(
                    TemplateValueName.with(t.text()),
                    t.text()
                )
            ),
            Parsers.fake() // value ignored
        );
        final Function<String, SpreadsheetParser> getSpreadsheetParser = (name) ->
            SpreadsheetParsers.parser(
                parsers.apply(
                    EbnfIdentifierName.with(name)
                ).setToString(name),
                SpreadsheetParser.NO_VALUE_TYPE
            );

        TEMPLATE_EXPRESSION_PARSER = getSpreadsheetParser.apply("EXPRESSION");
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
            Parsers.string(text, SpreadsheetStrings.CASE_SENSITIVITY)
                .transform((stringParserToken, context) -> factory.apply(stringParserToken.cast(StringParserToken.class).value(), stringParserToken.text()))
                .setToString(CharSequences.quoteAndEscape(text).toString())
                .cast();
    }

    /**
     * Given a column letter such as A or B, returns the numeric column number, so A returns 0, B = 2 etc.
     * A value of -1 is returned if the column letter code is invalid.
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

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

import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ParserTokens;
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

/**
 * A {@link EbnfParserCombinatorSyntaxTreeTransformer} that only transforms terminal and ranges into their corresponding {@link SpreadsheetParserToken} equivalents.
 * Processing of other tokens will be done after this process completes.
 */
final class SpreadsheetEbnfParserCombinatorSyntaxTreeTransformer implements EbnfParserCombinatorSyntaxTreeTransformer {

    final static SpreadsheetEbnfParserCombinatorSyntaxTreeTransformer INSTANCE = new SpreadsheetEbnfParserCombinatorSyntaxTreeTransformer();

    private SpreadsheetEbnfParserCombinatorSyntaxTreeTransformer() {
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
    private ParserToken concatenation(final ParserToken sequence,
                                      final ParserContext context) {
        return transformConcatenation(sequence.cast(), context);
    }

    private static ParserToken transformConcatenation(final SequenceParserToken sequence,
                                                      final ParserContext context) {
        ParserToken result;

        for (; ; ) {
            final SequenceParserToken cleaned = sequence.flat();

            final SpreadsheetParserToken first = cleaned.removeWhitespace()
                    .value()
                    .get(0)
                    .cast();

            if (first.isSymbol()) {
                result = sequence;
                break;
            }

            result = binaryOperandPrioritize(cleaned.value(), sequence);
            break;
        }

        return result;
    }

    private static ParserToken binaryOperandPrioritize(final List<ParserToken> tokens,
                                                       final SequenceParserToken parent) {
        List<ParserToken> prioritized = maybeExpandNegatives(tokens);

        for (int priority = SpreadsheetParserToken.HIGHEST_PRIORITY; priority > SpreadsheetParserToken.LOWEST_PRIORITY; priority--) {
            boolean changed;

            do {
                changed = false;
                int i = 0;
                for (ParserToken t : prioritized) {
                    final SpreadsheetParserToken s = t.cast();
                    if (s.operatorPriority() == priority) {
                        changed = true;

                        final int firstIndex = findNonWhitespaceSiblingToken(prioritized, i - 1, -1);
                        final int lastIndex = findNonWhitespaceSiblingToken(prioritized, i + 1, +1);

                        final List<ParserToken> binaryOperandTokens = Lists.array();
                        binaryOperandTokens.addAll(prioritized.subList(firstIndex, lastIndex + 1));

                        final List<ParserToken> replaced = Lists.array();
                        replaced.addAll(prioritized.subList(0, firstIndex));
                        replaced.add(s.binaryOperand(binaryOperandTokens, ParserToken.text(binaryOperandTokens)));
                        replaced.addAll(prioritized.subList(lastIndex + 1, prioritized.size()));

                        prioritized = replaced;
                        break;
                    }
                    i++;
                }
            } while (changed && prioritized.size() > 1);
        }

        return prioritized.size() == 1 ?
                prioritized.get(0) :
                ParserTokens.sequence(prioritized, parent.text());
    }

    /**
     * Expands any {@link SpreadsheetNegativeParserToken} into its core components, only if it doesnt follow another symbol.
     * This fixes the parsing "mistake" that converts any minus followed by a token into a {@link SpreadsheetNegativeParserToken}.
     */
    private static List<ParserToken> maybeExpandNegatives(final List<ParserToken> tokens) {
        final List<ParserToken> expanded = Lists.array();
        boolean expand = false;

        for (ParserToken t : tokens) {
            final SpreadsheetParserToken s = t.cast();
            if (s.isWhitespace()) {
                expanded.add(t);
                continue;
            }

            if (s.isNegative() && expand) {
                final SpreadsheetNegativeParserToken negativeParserToken = s.cast();
                expanded.addAll(negativeParserToken.value());
                expand = true;
                continue;
            }
            expand = !s.isSymbol();
            expanded.add(s);
        }

        return expanded;
    }

    private static int findNonWhitespaceSiblingToken(final List<ParserToken> tokens,
                                                     final int startIndex,
                                                     final int step) {
        int i = startIndex;
        for (; ; ) {
            final SpreadsheetParserToken token = tokens.get(i).cast();
            if (!token.isWhitespace()) {
                break;
            }
            i = i + step;
        }
        return i;
    }

    @Override
    public Parser<ParserContext> exception(final EbnfExceptionParserToken token,
                                           final Parser<ParserContext> parser) {
        return parser;
    }

    @Override
    public Parser<ParserContext> group(final EbnfGroupParserToken token,
                                       final Parser<ParserContext> parser) {
        return parser;
    }

    /**
     * For identified rules, the {@link SequenceParserToken} are flatted, missings removed and the {@link SpreadsheetPowerParserToken}
     * created.
     */
    @Override
    public Parser<ParserContext> identifier(final EbnfIdentifierParserToken token,
                                            final Parser<ParserContext> parser) {
        final EbnfIdentifierName name = token.value();
        return name.equals(SpreadsheetParsers.FUNCTION_IDENTIFIER) ?
                parser.transform(SpreadsheetEbnfParserCombinatorSyntaxTreeTransformer::function) :
                name.equals(GROUP_IDENTIFIER) ?
                        parser.transform(SpreadsheetEbnfParserCombinatorSyntaxTreeTransformer::group) :
                        name.equals(NEGATIVE_IDENTIFIER) ?
                                parser.transform(SpreadsheetEbnfParserCombinatorSyntaxTreeTransformer::negative) :
                                name.equals(PERCENTAGE_IDENTIFIER) ?
                                        parser.transform(SpreadsheetEbnfParserCombinatorSyntaxTreeTransformer::percentage) :
                                        this.requiredCheck(name, parser);
    }

    private static ParserToken function(final ParserToken token,
                                        final ParserContext context) {
        return SpreadsheetParserToken.function(clean(token.cast()), token.text());
    }

    private static ParserToken group(final ParserToken token,
                                     final ParserContext context) {
        return SpreadsheetParserToken.group(clean(token.cast()), token.text());
    }

    private static final EbnfIdentifierName GROUP_IDENTIFIER = EbnfIdentifierName.with("GROUP");

    private static ParserToken negative(final ParserToken token,
                                        final ParserContext context) {
        return SpreadsheetParserToken.negative(clean(token.cast()), token.text());
    }

    private static final EbnfIdentifierName NEGATIVE_IDENTIFIER = EbnfIdentifierName.with("NEGATIVE");

    private static ParserToken percentage(final ParserToken token,
                                          final ParserContext context) {
        return SpreadsheetParserToken.percentage(clean(token.cast()), token.text());
    }

    private static final EbnfIdentifierName PERCENTAGE_IDENTIFIER = EbnfIdentifierName.with("PERCENTAGE");

    private static List<ParserToken> clean(final SequenceParserToken token) {
        return token.flat()
                .value();
    }

    private static Parser<ParserContext> requiredCheck(final EbnfIdentifierName name,
                                                       final Parser<ParserContext> parser) {
        return name.value().endsWith("REQUIRED") ?
                parser.orReport(ParserReporters.basic()) :
                parser; // leave as is...
    }

    @Override
    public Parser<ParserContext> optional(final EbnfOptionalParserToken token,
                                          final Parser<ParserContext> parser) {
        return parser;
    }

    /**
     * Accepts the bounds tokens and creates a {@link SpreadsheetRangeParserToken}
     */
    @Override
    public Parser<ParserContext> range(final EbnfRangeParserToken token,
                                       final Parser<ParserContext> parser) {
        return parser.transform(SpreadsheetEbnfParserCombinatorSyntaxTreeTransformer::transformRange);
    }

    private static ParserToken transformRange(final ParserToken token,
                                              final ParserContext context) {
        return SpreadsheetParserToken.range(SequenceParserToken.class.cast(token).value(), token.text());
    }

    @Override
    public Parser<ParserContext> repeated(final EbnfRepeatedParserToken token,
                                          final Parser<ParserContext> parser) {
        return parser;
    }

    @Override
    public Parser<ParserContext> terminal(final EbnfTerminalParserToken token,
                                          final Parser<ParserContext> parser) {
        throw new UnsupportedOperationException(token.toString());
    }
}

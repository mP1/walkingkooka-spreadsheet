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

package walkingkooka.spreadsheet.reference;

import walkingkooka.CanBeEmpty;
import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserContexts;
import walkingkooka.text.cursor.parser.ParserException;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;

import java.util.Optional;
import java.util.SortedSet;
import java.util.function.Function;

/**
 * Provides parsing of CSV text into a {@link SortedSet} of {@link SpreadsheetSelection}.
 */
final class SpreadsheetSelectionCsvParser<S extends SpreadsheetSelection> implements CanBeEmpty {

    final static CharacterConstant SEPARATOR = CharacterConstant.COMMA;

    static <S extends SpreadsheetSelection> SortedSet<S> parse(final String text,
                                                               final Parser<SpreadsheetParserContext> selectionTokenParser,
                                                               final Function<SpreadsheetFormulaParserToken, S> parserTokenToSelection) {
        final SpreadsheetSelectionCsvParser<S> parser = new SpreadsheetSelectionCsvParser<>(
            text,
            selectionTokenParser,
            parserTokenToSelection
        );

        final SortedSet<S> selections = SortedSets.tree(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);

        parser.spaces();

        if (parser.isNotEmpty()) {
            for (; ; ) {
                parser.spaces();

                final int offset = parser.cursor.lineInfo().textOffset();
                try {
                    final Optional<S> selection = parser.selection();
                    if (selection.isPresent()) {
                        selections.add(
                            selection.get()
                        );
                    }
                } catch (final InvalidCharacterException invalid) {
                    throw invalid.setTextAndPosition(
                        text,
                        offset + invalid.position()
                    );
                }

                parser.spaces();

                if (SEPARATOR.string().equals(parser.comma())) {
                    continue;
                }

                if (parser.isEmpty()) {
                    break;
                }

                parser.invalidCharacterException();
            }
        }

        return selections;
    }

    private SpreadsheetSelectionCsvParser(final String text,
                                          final Parser<SpreadsheetParserContext> selectionTokenParser,
                                          final Function<SpreadsheetFormulaParserToken, S> parserTokenToSelection) {
        this.cursor = TextCursors.maxPosition(
            TextCursors.charSequence(text)
        );
        this.selectionTokenParser = selectionTokenParser;
        this.parserTokenToSelection = parserTokenToSelection;
    }

    // spaces...........................................................................................................

    String spaces() {
        return SPACES.parse(
                this.cursor,
                CONTEXT
            ).map(ParserToken::text)
            .orElse("");
    }

    private final static Parser<ParserContext> SPACES = Parsers.charPredicateString(
        CharPredicates.is(' '),
        1,
        Character.MAX_VALUE
    );

    // SpreadsheetSelection.............................................................................................

    /**
     * Attempts to parse and consume the text at the cursor advancing if a selection was matched.
     */
    Optional<S> selection() {
        try {
            return this.selectionTokenParser.parse(
                this.cursor,
                SpreadsheetParserContexts.fake()
            ).map(t -> this.parserTokenToSelection.apply(t.cast(SpreadsheetFormulaParserToken.class)));
        } catch (final ParserException cause) {
            final Throwable wrapped = cause.getCause();

            if (wrapped instanceof NullPointerException) {
                throw (NullPointerException) wrapped;
            }
            if (wrapped instanceof IllegalColumnOrRowArgumentException) {
                throw (IllegalColumnOrRowArgumentException) wrapped;
            }
            if (wrapped instanceof IllegalArgumentException) {
                throw (IllegalArgumentException) wrapped;
            }

            throw new IllegalArgumentException(cause.getMessage(), cause);
        }
    }

    private final Parser<SpreadsheetParserContext> selectionTokenParser;

    /**
     * Extracts the {@link SpreadsheetSelection} from the {@link SpreadsheetFormulaParserToken}.
     */
    private final Function<SpreadsheetFormulaParserToken, S> parserTokenToSelection;

    // comma............................................................................................................

    String comma() {
        return COMMA.parse(
                this.cursor,
                CONTEXT
            ).map(ParserToken::text)
            .orElse("");
    }

    private final static Parser<ParserContext> COMMA = Parsers.character(
        CharPredicates.is(
            SEPARATOR.character()
        )
    );

    // CanBeEmpty.......................................................................................................

    @Override
    public boolean isEmpty() {
        return this.cursor.isEmpty();
    }

    private final static ParserContext CONTEXT = ParserContexts.basic(
        InvalidCharacterExceptionFactory.POSITION,
        DateTimeContexts.fake(),
        DecimalNumberContexts.fake()
    );

    final TextCursor cursor;

    void invalidCharacterException() {
        throw CONTEXT.invalidCharacterException(
            PARSER,
            this.cursor
        );
    }

    private final static Parser<SpreadsheetParserContext> PARSER = SpreadsheetParsers.fake()
        .setToString("CELLS");
}

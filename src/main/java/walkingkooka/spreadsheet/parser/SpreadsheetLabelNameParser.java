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

import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;

import java.util.Optional;

/**
 * A {@link Parser} that consumes a {@link SpreadsheetLabelNameParserToken}
 */
final class SpreadsheetLabelNameParser implements Parser<SpreadsheetParserContext> {

    /**
     * Singleton
     */
    final static SpreadsheetLabelNameParser INSTANCE = new SpreadsheetLabelNameParser();

    /**
     * Private ctor use singleton
     */
    private SpreadsheetLabelNameParser() {
        super();
    }

    @Override
    public Optional<ParserToken> parse(final TextCursor cursor, final SpreadsheetParserContext context) {
        final TextCursorSavePoint save = cursor.save();
        final Optional<ParserToken> stringParserToken = LABEL.parse(cursor, context);
        return stringParserToken.isPresent() ?
                this.token(stringParserToken.get(), save) :
                Optional.empty();
    }

    private Optional<ParserToken> token(final ParserToken stringParserToken, final TextCursorSavePoint save) {
        final String text = stringParserToken.text();
        return text.length() < SpreadsheetLabelName.MAX_LENGTH && !SpreadsheetLabelName.isTextCellReference(text) ?
                Optional.of(SpreadsheetParserToken.labelName(SpreadsheetExpressionReference.labelName(text), text)) :
                restoreAndNothing(save);
    }

    private Optional<ParserToken> restoreAndNothing(final TextCursorSavePoint save) {
        save.restore();
        return Optional.empty();
    }

    // @see SpreadsheetLabelName
    static {
        final CharPredicate LETTER = CharPredicates.range('A', 'Z').or(CharPredicates.range('a', 'z'));
        @SuppressWarnings("UnnecessaryLocalVariable")
        final CharPredicate INITIAL = LETTER;

        final CharPredicate DIGIT = CharPredicates.range('0', '9');

        final CharPredicate PART = INITIAL.or(DIGIT.or(CharPredicates.is('_')));

        LABEL = Parsers.stringInitialAndPartCharPredicate(
                INITIAL,
                PART,
                1,
                SpreadsheetLabelName.MAX_LENGTH
        );
    }

    private final static Parser<SpreadsheetParserContext> LABEL;

    @Override
    public String toString() {
        return SpreadsheetLabelName.class.getSimpleName();
    }
}

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

import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.spreadsheet.SpreadsheetValueType;
import walkingkooka.spreadsheet.formula.parser.LabelSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelectorToken;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.cursor.parser.RequiredParser;
import walkingkooka.validation.ValidationValueTypeName;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetParser} that consumes a {@link LabelSpreadsheetFormulaParserToken} matching a label.
 * Note {@link #tokens(SpreadsheetParserContext)} has no pattern representation and always returns {@link #NO_TOKENS}.
 */
final class SpreadsheetLabelNameSpreadsheetParser implements SpreadsheetParser,
        RequiredParser<SpreadsheetParserContext> {

    /**
     * Singleton
     */
    final static SpreadsheetLabelNameSpreadsheetParser INSTANCE = new SpreadsheetLabelNameSpreadsheetParser();

    /**
     * Private ctor use singleton
     */
    private SpreadsheetLabelNameSpreadsheetParser() {
        super();
    }

    @Override
    public Optional<ParserToken> parse(final TextCursor cursor,
                                       final SpreadsheetParserContext context) {
        final TextCursorSavePoint save = cursor.save();
        final Optional<ParserToken> stringParserToken = LABEL.parse(cursor, context);
        return Optional.ofNullable(
                stringParserToken.isPresent() ?
                        token(
                                cursor,
                                stringParserToken.get(),
                                save
                        ) :
                        null
        );
    }

    private static ParserToken token(final TextCursor cursor,
                                     final ParserToken stringParserToken,
                                     final TextCursorSavePoint save) {
        ParserToken token = null;

        // if the label is followed by a dollar-sign, abort, its probably a cell eg: A$1
        if (cursor.isEmpty() || SpreadsheetReferenceKind.ABSOLUTE_PREFIX != cursor.at()) {
            final String text = stringParserToken.text();
            if (SpreadsheetSelection.isLabelText(text)) {
                token = SpreadsheetFormulaParserToken.label(
                        SpreadsheetSelection.labelName(text),
                        text
                );
            }
        }

        if (null == token) {
            save.restore();
        }

        return token;
    }

    // @see SpreadsheetLabelName
    static {
        @SuppressWarnings("UnnecessaryLocalVariable") final CharPredicate LETTER = CharPredicates.range(
                'A',
                'Z'
        ).or(
                CharPredicates.range('a', 'z')
        );

        @SuppressWarnings("UnnecessaryLocalVariable") final CharPredicate INITIAL = LETTER;

        final CharPredicate DIGIT = CharPredicates.range('0', '9');
        final CharPredicate PART = INITIAL.or(
                DIGIT.or(
                        CharPredicates.is('_')
                )
        );

        LABEL = Parsers.initialAndPartCharPredicateString(
                INITIAL,
                PART,
                1,
                SpreadsheetLabelName.MAX_LENGTH
        );
    }

    private final static Parser<SpreadsheetParserContext> LABEL;

    @Override
    public List<SpreadsheetParserSelectorToken> tokens(final SpreadsheetParserContext context) {
        Objects.requireNonNull(context, "context");

        return NO_TOKENS;
    }

    @Override
    public Optional<ValidationValueTypeName> valueType() {
        return LABEL_VALUE_TYPE;
    }

    private final static Optional<ValidationValueTypeName> LABEL_VALUE_TYPE = Optional.of(
            SpreadsheetValueType.LABEL
    );

    @Override
    public String toString() {
        return "LABEL";
    }
}

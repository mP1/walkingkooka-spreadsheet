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

package walkingkooka.spreadsheet.format.pattern;

import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetTimeParserToken;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.ExpressionEvaluationContext;

import java.time.LocalTime;

/**
 * Holds a valid {@link SpreadsheetTimeParsePattern}.
 */
public final class SpreadsheetTimeParsePattern extends SpreadsheetNonNumberParsePattern<LocalTime> {

    /**
     * Factory that creates a {@link ParserToken} from the given tokens.
     */
    static SpreadsheetTimeParsePattern with(final ParserToken token) {
        final SpreadsheetTimeParsePatternSpreadsheetFormatParserTokenVisitor visitor = SpreadsheetTimeParsePatternSpreadsheetFormatParserTokenVisitor.with();
        visitor.startAccept(token);
        return new SpreadsheetTimeParsePattern(token);
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetTimeParsePattern(final ParserToken token) {
        super(token);
    }

    @Override
    public SpreadsheetTimeFormatPattern toFormat() {
        return SpreadsheetPattern.timeFormatPattern(this.value());
    }

    @Override
    Class<LocalTime> targetType() {
        return LocalTime.class;
    }

    @Override
    LocalTime converterTransformer0(final ParserToken token,
                                    final ExpressionEvaluationContext context) {
        return token.cast(
                SpreadsheetTimeParserToken.class
        ).toLocalTime();
    }

    // parse......................................................................................................

    /**
     * Tries to parse the given {@link String text} into a {@link LocalTime} or throw.
     */
    @Override
    public LocalTime parse(final String text,
                           final SpreadsheetParserContext context) {
        return this.parser()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(text), context)
                .get()
                .cast(SpreadsheetTimeParserToken.class)
                .toLocalTime();
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof SpreadsheetTimeParsePattern;
    }
}

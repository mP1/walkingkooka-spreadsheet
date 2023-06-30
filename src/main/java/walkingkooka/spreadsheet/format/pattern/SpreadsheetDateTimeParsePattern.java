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

import walkingkooka.spreadsheet.parser.SpreadsheetDateTimeParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.ExpressionEvaluationContext;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Holds a valid {@link SpreadsheetDateTimeParsePattern}.
 */
public final class SpreadsheetDateTimeParsePattern extends SpreadsheetNonNumberParsePattern<LocalDateTime> {

    /**
     * Factory that creates a {@link ParserToken} from the given tokens.
     */
    static SpreadsheetDateTimeParsePattern with(final ParserToken token) {
        final SpreadsheetDateTimeParsePatternSpreadsheetFormatParserTokenVisitor visitor = SpreadsheetDateTimeParsePatternSpreadsheetFormatParserTokenVisitor.with();
        visitor.startAccept(token);
        return new SpreadsheetDateTimeParsePattern(token);
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetDateTimeParsePattern(final ParserToken token) {
        super(token);
    }

    @Override
    public SpreadsheetDateTimeFormatPattern toFormat() {
        return SpreadsheetPattern.dateTimeFormatPattern(this.value());
    }

    @Override
    Class<LocalDateTime> targetType() {
        return LocalDateTime.class;
    }

    @Override
    LocalDateTime converterTransformer0(final ParserToken token,
                                        final ExpressionEvaluationContext context) {
        return token.cast(
                SpreadsheetDateTimeParserToken.class
        ).toLocalDateTime(context);
    }

    // parse......................................................................................................

    /**
     * Tries to parse the given {@link String text} into a {@link LocalDateTime} or throw.
     */
    @Override
    public LocalDateTime parse(final String text,
                               final SpreadsheetParserContext context) {
        return this.parser()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(text), context)
                .get()
                .cast(SpreadsheetDateTimeParserToken.class)
                .toLocalDateTime(context);
    }

    // patterns.........................................................................................................

    @Override
    public List<SpreadsheetDateTimeParsePattern> patterns() {
        return SpreadsheetPatternPatternsSpreadsheetFormatParserTokenVisitor.patterns(
                this,
                SpreadsheetDateTimeParsePattern::new
        );
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof SpreadsheetDateTimeParsePattern;
    }
}

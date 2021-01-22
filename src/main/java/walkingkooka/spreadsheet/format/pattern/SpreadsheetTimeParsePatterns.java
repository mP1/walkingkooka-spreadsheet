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

import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTimeParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetTimeParserToken;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.ExpressionEvaluationContext;

import java.time.LocalTime;
import java.util.List;

/**
 * Holds a valid {@link SpreadsheetTimeParsePatterns}.
 */
public final class SpreadsheetTimeParsePatterns extends SpreadsheetParsePatterns2<SpreadsheetFormatTimeParserToken,
        SpreadsheetTimeParserToken,
        LocalTime> {

    /**
     * Factory that creates a {@link SpreadsheetTimeParsePatterns} from the given tokens.
     */
    static SpreadsheetTimeParsePatterns withToken(final ParserToken token) {
        final SpreadsheetTimeParsePatternsSpreadsheetFormatParserTokenVisitor visitor = SpreadsheetTimeParsePatternsSpreadsheetFormatParserTokenVisitor.with();
        visitor.startAccept(token);
        return new SpreadsheetTimeParsePatterns(visitor.tokens());
    }

    /**
     * Factory that creates a {@link SpreadsheetTimeParsePatterns} from the given tokens.
     */
    static SpreadsheetTimeParsePatterns withTokens(final List<SpreadsheetFormatTimeParserToken> tokens) {
        check(tokens);

        final SpreadsheetTimeParsePatternsSpreadsheetFormatParserTokenVisitor visitor = SpreadsheetTimeParsePatternsSpreadsheetFormatParserTokenVisitor.with();
        tokens.forEach(visitor::startAccept);
        return new SpreadsheetTimeParsePatterns(visitor.tokens());
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetTimeParsePatterns(final List<SpreadsheetFormatTimeParserToken> tokens) {
        super(tokens);
    }

    @Override
    Class<LocalTime> targetType() {
        return LocalTime.class;
    }

    @Override
    LocalTime converterTransformer0(final ParserToken token,
                                    final ExpressionEvaluationContext context) {
        return token.cast(SpreadsheetTimeParserToken.class).toLocalTime();
    }

    @Override
    SpreadsheetTimeParserToken parserTransform0(final List<ParserToken> token,
                                                final String text) {
        return SpreadsheetParserToken.time(token, text);
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof SpreadsheetTimeParsePatterns;
    }
}

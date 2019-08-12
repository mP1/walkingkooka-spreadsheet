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

package walkingkooka.spreadsheet.format;

import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTimeParserToken;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;

import java.util.List;

/**
 * Holds a valid {@link SpreadsheetTimePatterns}.
 */
public final class SpreadsheetTimePatterns extends SpreadsheetPatterns2<SpreadsheetFormatTimeParserToken> {

    /**
     * Factory that creates a {@link SpreadsheetTimePatterns} from the given tokens.
     */
    static SpreadsheetTimePatterns withToken(final ParserToken token) {
        final SpreadsheetTimePatternsSpreadsheetFormatParserTokenVisitor visitor = SpreadsheetTimePatternsSpreadsheetFormatParserTokenVisitor.with();
        visitor.startAccept(token);
        return new SpreadsheetTimePatterns(visitor.tokens(), visitor.ampms);
    }

    /**
     * Factory that creates a {@link SpreadsheetTimePatterns} from the given tokens.
     */
    static SpreadsheetTimePatterns withTokens(final List<SpreadsheetFormatTimeParserToken> tokens) {
        check(tokens);

        final SpreadsheetTimePatternsSpreadsheetFormatParserTokenVisitor visitor = SpreadsheetTimePatternsSpreadsheetFormatParserTokenVisitor.with();
        tokens.forEach(visitor::startAccept);
        return new SpreadsheetTimePatterns(visitor.tokens(), visitor.ampms);
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetTimePatterns(final List<SpreadsheetFormatTimeParserToken> tokens,
                                    final List<Boolean> ampms) {
        super(tokens);
        this.ampms = ampms;
    }

    @Override
    public boolean isDate() {
        return false;
    }

    @Override
    public boolean isDateTime() {
        return false;
    }

    @Override
    public boolean isTime() {
        return true;
    }

    // HashCodeEqualsDefined............................................................................................

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof SpreadsheetTimePatterns;
    }

    // HasParser........................................................................................................

    @Override
    Parser<ParserContext> createDateTimeFormatterParser(final int i) {
        return Parsers.localTime(SpreadsheetPatterns2DateTimeContextDateTimeFormatterFunction.with(this.value().get(i),
                this.ampms.get(i)));
    }

    private final List<Boolean> ampms;
}

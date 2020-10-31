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

import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTimeParserToken;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;

import java.util.List;

/**
 * Holds a valid {@link SpreadsheetTimeParsePatterns}.
 */
public final class SpreadsheetTimeParsePatterns extends SpreadsheetParsePatterns2<SpreadsheetFormatTimeParserToken> {

    /**
     * Factory that creates a {@link SpreadsheetTimeParsePatterns} from the given tokens.
     */
    static SpreadsheetTimeParsePatterns withToken(final ParserToken token) {
        final SpreadsheetTimeParsePatternsSpreadsheetFormatParserTokenVisitor visitor = SpreadsheetTimeParsePatternsSpreadsheetFormatParserTokenVisitor.with();
        visitor.startAccept(token);
        return new SpreadsheetTimeParsePatterns(visitor.tokens(), visitor.ampms);
    }

    /**
     * Factory that creates a {@link SpreadsheetTimeParsePatterns} from the given tokens.
     */
    static SpreadsheetTimeParsePatterns withTokens(final List<SpreadsheetFormatTimeParserToken> tokens) {
        check(tokens);

        final SpreadsheetTimeParsePatternsSpreadsheetFormatParserTokenVisitor visitor = SpreadsheetTimeParsePatternsSpreadsheetFormatParserTokenVisitor.with();
        tokens.forEach(visitor::startAccept);
        return new SpreadsheetTimeParsePatterns(visitor.tokens(), visitor.ampms);
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetTimeParsePatterns(final List<SpreadsheetFormatTimeParserToken> tokens,
                                         final List<Boolean> ampms) {
        super(tokens);
        this.ampms = ampms;
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof SpreadsheetTimeParsePatterns;
    }

    // HasConverter.....................................................................................................

    @Override
    Converter<ExpressionNumberConverterContext> createDateTimeFormatterConverter(final int i) {
        return Converters.stringLocalTime(this.dateTimeContextDateTimeFormatterFunction(i));
    }

    // HasParser........................................................................................................

    @Override
    Parser<ParserContext> createDateTimeFormatterParser(final int i) {
        return Parsers.localTime(this.dateTimeContextDateTimeFormatterFunction(i));
    }

    private SpreadsheetParsePatterns2DateTimeContextDateTimeFormatterFunction dateTimeContextDateTimeFormatterFunction(final int i) {
        return SpreadsheetParsePatterns2DateTimeContextDateTimeFormatterFunction.with(this.value().get(i), this.ampms.get(i));
    }

    private final List<Boolean> ampms;
}

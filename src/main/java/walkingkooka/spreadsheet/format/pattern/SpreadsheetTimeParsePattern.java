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
import walkingkooka.spreadsheet.SpreadsheetValueType;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverters;
import walkingkooka.spreadsheet.formula.parser.TimeSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.validation.ValidationValueTypeName;

import java.time.LocalTime;
import java.util.List;

/**
 * Holds a valid {@link SpreadsheetTimeParsePattern}.
 */
public final class SpreadsheetTimeParsePattern extends SpreadsheetNonNumberParsePattern {

    /**
     * Factory that creates a {@link ParserToken} parse the given tokens.
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
    public ValidationValueTypeName valueType() {
        return SpreadsheetValueType.TIME;
    }

    @Override
    public SpreadsheetTimeFormatPattern toFormat() {
        return SpreadsheetPattern.timeFormatPattern(this.value());
    }

    // HasConverter.....................................................................................................

    @Override
    Converter<SpreadsheetConverterContext> createConverter() {
        return SpreadsheetConverters.textToTime(this.parser());
    }

    // parse............................................................................................................

    /**
     * Tries to parse the given {@link String text} into a {@link LocalTime} or throw.
     */
    @Override
    public LocalTime parse(final String text,
                           final SpreadsheetParserContext context) {
        return this.parser()
            .parseText(
                text,
                context
            ).cast(TimeSpreadsheetFormulaParserToken.class)
            .toLocalTime();
    }

    // patterns.........................................................................................................

    @Override
    public List<SpreadsheetTimeParsePattern> patterns() {
        if (null == this.patterns) {
            this.patterns = SpreadsheetPatternPatternsSpreadsheetFormatParserTokenVisitor.patterns(
                this,
                SpreadsheetTimeParsePattern::new
            );
        }

        return this.patterns;
    }

    // remove...........................................................................................................

    @Override
    public SpreadsheetTimeParsePattern removeColor() {
        return this;
    }

    // removeCondition..................................................................................................

    @Override
    public SpreadsheetTimeParsePattern removeCondition() {
        return this;
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof SpreadsheetTimeParsePattern;
    }
}

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
import walkingkooka.spreadsheet.formula.parser.DateSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.validation.ValidationValueTypeName;

import java.time.LocalDate;
import java.util.List;

/**
 * Holds a valid {@link SpreadsheetDateParsePattern}.
 */
public final class SpreadsheetDateParsePattern extends SpreadsheetNonNumberParsePattern {

    /**
     * Factory that creates a {@link ParserToken} parse the given tokens.
     */
    static SpreadsheetDateParsePattern with(final ParserToken token) {
        final SpreadsheetDateParsePatternSpreadsheetFormatParserTokenVisitor visitor = SpreadsheetDateParsePatternSpreadsheetFormatParserTokenVisitor.with();
        visitor.startAccept(token);
        return new SpreadsheetDateParsePattern(token);
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetDateParsePattern(final ParserToken token) {
        super(token);
    }

    @Override
    public ValidationValueTypeName valueType() {
        return SpreadsheetValueType.DATE;
    }

    @Override
    public SpreadsheetDateFormatPattern toFormat() {
        return SpreadsheetPattern.dateFormatPattern(this.value());
    }

    // HasConverter.....................................................................................................

    @Override
    Converter<SpreadsheetConverterContext> createConverter() {
        return SpreadsheetConverters.textToDate(this.parser());
    }

    // parse............................................................................................................

    /**
     * Tries to parse the given {@link String text} into a {@link LocalDate} or throw.
     */
    @Override
    public LocalDate parse(final String text,
                           final SpreadsheetParserContext context) {
        return this.parser()
                .parseText(
                        text,
                        context
                ).cast(DateSpreadsheetFormulaParserToken.class)
                .toLocalDate(context);
    }

    // patterns.........................................................................................................

    @Override
    public List<SpreadsheetDateParsePattern> patterns() {
        if (null == this.patterns) {
            this.patterns = SpreadsheetPatternPatternsSpreadsheetFormatParserTokenVisitor.patterns(
                    this,
                    SpreadsheetDateParsePattern::new
            );
        }
        return this.patterns;
    }

    // removeColor.......................................................................................................

    @Override
    public SpreadsheetDateParsePattern removeColor() {
        return this;
    }

    // removeCondition..................................................................................................

    @Override
    public SpreadsheetDateParsePattern removeCondition() {
        return this;
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof SpreadsheetDateParsePattern;
    }
}

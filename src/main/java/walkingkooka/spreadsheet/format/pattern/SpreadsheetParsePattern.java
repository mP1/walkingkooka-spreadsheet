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
import walkingkooka.convert.HasConverter;
import walkingkooka.spreadsheet.color.SpreadsheetColors;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserName;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelector;
import walkingkooka.text.CaseKind;
import walkingkooka.text.cursor.parser.HasParser;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.Objects;

/**
 * Holds a {@link ParserToken} typically a date/dateime/time and possibly color or conditions.
 */
public abstract class SpreadsheetParsePattern extends SpreadsheetPattern
    implements HasConverter<SpreadsheetConverterContext>,
    HasParser<SpreadsheetParserContext> {

    // ctor.............................................................................................................

    /**
     * Package private ctor use factory
     */
    SpreadsheetParsePattern(final ParserToken token) {
        super(token);
    }

    // spreadsheetParserSelector.....................................................................................

    /**
     * Returns the {@link SpreadsheetParserSelector} equivalent to this pattern.
     * <pre>
     * date pattern-here
     * date-time pattern-here
     * </pre>
     */
    public final SpreadsheetParserSelector spreadsheetParserSelector() {
        return SpreadsheetParserSelector.with(
            SpreadsheetParserName.with(
                CaseKind.CAMEL.change(
                    this.getClass().getSimpleName()
                        .replace("Spreadsheet", "")
                        .replace("ParsePattern", ""),
                    CaseKind.KEBAB
                )
            ),
            this.text()
        );
    }

    // HasFormatter.....................................................................................................

    @Override final SpreadsheetPatternSpreadsheetFormatter createFormatter() {
        return this.toFormat()
            .formatter();
    }

    // HasConverter.....................................................................................................

    /**
     * Returns a {@link Converter} which will try all the patterns.
     */
    @Override
    public final Converter<SpreadsheetConverterContext> converter() {
        if (null == this.converter) {
            this.converter = this.createConverter();
        }
        return this.converter;
    }

    private Converter<SpreadsheetConverterContext> converter;

    /**
     * Factory that lazily creates a {@link Converter}
     */
    abstract Converter<SpreadsheetConverterContext> createConverter();

    // HasParser........................................................................................................

    /**
     * Returns a {@link SpreadsheetParser} which will try all the patterns.<br>
     * {@link java.time.LocalDate}, {@link java.time.LocalDateTime}, {@link java.time.LocalTime} will all fail to parse
     * the if the value has extra trailing text. If this parse is for {@link walkingkooka.tree.expression.ExpressionNumber}
     * and will be used to parse number literals the {@link Parser#andEmptyTextCursor()} must be called afterwards.
     */
    @Override
    public final SpreadsheetParser parser() {
        if (null == this.parser) {
            this.parser = this.createParser();
        }
        return this.parser;
    }

    private SpreadsheetParser parser;

    /**
     * Factory that lazily creates a {@link SpreadsheetParser}
     */
    abstract SpreadsheetParser createParser();

    // parse........................................................................................................

    /**
     * Parses the text which hopefully is acceptable for this pattern returning the value.
     * <br>
     * Different subclasses return a different value type, eg {@link SpreadsheetNumberParsePattern} returns a {@link walkingkooka.tree.expression.ExpressionNumber}
     * if the text is compatible with the pattern.
     */
    public abstract Object parse(final String text,
                                 final SpreadsheetParserContext context);

    // color............................................................................................................

    @Override
    public final SpreadsheetPattern setColorName(final SpreadsheetColorName name) {
        Objects.requireNonNull(name, "name");
        throw new IllegalStateException("Cannot set color name " + name + " for pattern=" + this);
    }

    @Override
    public final SpreadsheetPattern setColorNumber(final int number) {
        SpreadsheetColors.checkNumber(number);
        throw new IllegalStateException("Cannot set color number " + number + " for pattern=" + this);
    }
}

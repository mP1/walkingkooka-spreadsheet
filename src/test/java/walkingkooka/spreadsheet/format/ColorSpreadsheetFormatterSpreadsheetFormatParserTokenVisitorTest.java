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

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatColorParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class ColorSpreadsheetFormatterSpreadsheetFormatParserTokenVisitorTest extends SpreadsheetFormatParserTokenVisitorTestCase<ColorSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor>
        implements ToStringTesting<ColorSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor> {

    @Test
    public void testColorName() {
        final List<ParserToken> tokens = Lists.of(
                SpreadsheetFormatParserToken.bracketOpenSymbol("[", "["),
                SpreadsheetFormatParserToken.colorName("RED", "RED"),
                SpreadsheetFormatParserToken.bracketCloseSymbol("]", "]")
        );
        colorNameOrNumberOrFailAndCheck(SpreadsheetFormatParserToken.color(tokens, ParserToken.text(tokens)),
                ColorSpreadsheetFormatterColorSource.NAME,
                SpreadsheetColorName.with("RED"));
    }

    @Test
    public void testColorNumber() {
        final List<ParserToken> tokens = Lists.of(
                SpreadsheetFormatParserToken.bracketOpenSymbol("[", "["),
                SpreadsheetFormatParserToken.colorLiteralSymbol("COLOR", "COLOR"),
                SpreadsheetFormatParserToken.whitespace(" ", " "),
                SpreadsheetFormatParserToken.colorNumber(13, "13"),
                SpreadsheetFormatParserToken.bracketCloseSymbol("]", "]")
        );

        colorNameOrNumberOrFailAndCheck(SpreadsheetFormatParserToken.color(tokens, ParserToken.text(tokens)),
                ColorSpreadsheetFormatterColorSource.NUMBER,
                13);
    }

    private void colorNameOrNumberOrFailAndCheck(final SpreadsheetFormatColorParserToken color,
                                                 final ColorSpreadsheetFormatterColorSource source,
                                                 final Object nameOrNumber)  {
        final ColorSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor visitor = ColorSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor.colorNameOrNumberOrFail(color);
        assertEquals(source, visitor.source, "source");
        assertEquals(nameOrNumber, visitor.nameOrNumber, "nameOrNumber");
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createVisitor(), "");
    }

    @Test
    public void testToString2() {
        final ColorSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor visitor = new ColorSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor();

        final List<ParserToken> tokens = Lists.of(
                SpreadsheetFormatParserToken.bracketOpenSymbol("[", "["),
                SpreadsheetFormatParserToken.colorName("RED", "RED"),
                SpreadsheetFormatParserToken.bracketCloseSymbol("]", "]")
        );

        final SpreadsheetFormatColorParserToken color = SpreadsheetFormatParserToken.color(tokens, ParserToken.text(tokens));
        visitor.accept(color);

        this.toStringAndCheck(visitor, "NAME RED");
    }

    @Override
    public ColorSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor createVisitor() {
        return new ColorSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor();
    }

    @Override
    public String typeNamePrefix() {
        return ColorSpreadsheetFormatter.class.getSimpleName();
    }

    @Override
    public Class<ColorSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor> type() {
        return ColorSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor.class;
    }
}

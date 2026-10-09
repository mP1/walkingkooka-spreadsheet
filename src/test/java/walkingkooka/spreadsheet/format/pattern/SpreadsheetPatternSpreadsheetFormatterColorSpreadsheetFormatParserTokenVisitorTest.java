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

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.color.Color;
import walkingkooka.spreadsheet.format.parser.ColorSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.List;

public final class SpreadsheetPatternSpreadsheetFormatterColorSpreadsheetFormatParserTokenVisitorTest extends SpreadsheetPatternSpreadsheetFormatParserTokenVisitorTestCase<SpreadsheetPatternSpreadsheetFormatterColorSpreadsheetFormatParserTokenVisitor>
    implements ToStringTesting<SpreadsheetPatternSpreadsheetFormatterColorSpreadsheetFormatParserTokenVisitor> {

    @Test
    public void testColorName() {
        final List<ParserToken> tokens = Lists.of(
            SpreadsheetFormatParserToken.bracketOpenSymbol("[", "["),
            SpreadsheetFormatParserToken.colorName("RED", "RED"),
            SpreadsheetFormatParserToken.bracketCloseSymbol("]", "]")
        );
        colorNameOrNumberOrFailAndCheck(
            SpreadsheetFormatParserToken.color(
                tokens,
                ParserToken.text(tokens)
            ),
            Color.named("RED")
        );
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

        colorNameOrNumberOrFailAndCheck(
            SpreadsheetFormatParserToken.color(
                tokens,
                ParserToken.text(tokens)
            ),
            Color.indexed(13)
        );
    }

    private void colorNameOrNumberOrFailAndCheck(final ColorSpreadsheetFormatParserToken color,
                                                 final Color expected) {
        final SpreadsheetPatternSpreadsheetFormatterColorSpreadsheetFormatParserTokenVisitor visitor = SpreadsheetPatternSpreadsheetFormatterColorSpreadsheetFormatParserTokenVisitor.colorNameOrNumberOrFail(color);
        this.checkEquals(
            expected,
            visitor.color,
            "color"
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createVisitor(),
            "null"
        );
    }

    @Test
    public void testToString2() {
        final SpreadsheetPatternSpreadsheetFormatterColorSpreadsheetFormatParserTokenVisitor visitor = new SpreadsheetPatternSpreadsheetFormatterColorSpreadsheetFormatParserTokenVisitor();

        final List<ParserToken> tokens = Lists.of(
            SpreadsheetFormatParserToken.bracketOpenSymbol("[", "["),
            SpreadsheetFormatParserToken.colorName("RED", "RED"),
            SpreadsheetFormatParserToken.bracketCloseSymbol("]", "]")
        );

        final ColorSpreadsheetFormatParserToken color = SpreadsheetFormatParserToken.color(tokens, ParserToken.text(tokens));
        visitor.accept(color);

        this.toStringAndCheck(
            visitor,
            "\"RED\""
        );
    }

    @Override
    public SpreadsheetPatternSpreadsheetFormatterColorSpreadsheetFormatParserTokenVisitor createVisitor() {
        return new SpreadsheetPatternSpreadsheetFormatterColorSpreadsheetFormatParserTokenVisitor();
    }

    @Override
    public String typeNamePrefix() {
        return SpreadsheetPatternSpreadsheetFormatterColor.class.getSimpleName();
    }

    @Override
    public Class<SpreadsheetPatternSpreadsheetFormatterColorSpreadsheetFormatParserTokenVisitor> type() {
        return SpreadsheetPatternSpreadsheetFormatterColorSpreadsheetFormatParserTokenVisitor.class;
    }
}

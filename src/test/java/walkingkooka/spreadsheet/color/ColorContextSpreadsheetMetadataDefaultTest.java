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

package walkingkooka.spreadsheet.color;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.color.Color;
import walkingkooka.color.ColorContextTesting2;
import walkingkooka.color.WebColorName;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ColorContextSpreadsheetMetadataDefaultTest implements ColorContextTesting2<ColorContextSpreadsheetMetadataDefault>,
    ToStringTesting<ColorContextSpreadsheetMetadataDefault> {

    // indexedColor.....................................................................................................

    @Test
    public void testIndexedColorWithZeroFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .indexedColor(0)
        );
    }

    @Test
    public void testIndexedColorWithMaxPlus1Fails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .indexedColor(
                    SpreadsheetColors.MAX + 1
                )
        );
    }

    @Test
    public void testIndexedColorWithMin() {
        this.indexedColorAndCheck(
            this.createContext(),
            SpreadsheetColors.MIN,
            Color.indexed(
                SpreadsheetColors.MIN
            )
        );
    }

    @Test
    public void testIndexedColorWithMin2() {
        this.indexedColorAndCheck(
            this.createContext(),
            SpreadsheetColors.MIN + 1,
            Color.indexed(
                SpreadsheetColors.MIN + 1
            )
        );
    }

    @Test
    public void testIndexedColorWithMax() {
        this.indexedColorAndCheck(
            this.createContext(),
            SpreadsheetColors.MAX,
            Color.indexed(
                SpreadsheetColors.MAX
            )
        );
    }

    // lookupColor......................................................................................................

    @Test
    public void testLookupColorWithIndexedColorZeroFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .lookupColor(
                    Color.indexed(0)
                )
        );
    }

    @Test
    public void testLookupColorWithIndexedColorMaxPlus1Fails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .lookupColor(
                    Color.indexed(
                        SpreadsheetColors.MAX + 1
                    )
                )
        );
    }

    @Test
    public void testLookupColorWithIndexedColorMin() {
        this.lookupColorAndCheck(
            this.createContext(),
            Color.indexed(SpreadsheetColors.MIN),
            Color.BLACK
        );
    }

    @Test
    public void testLookupColorWithIndexedColor2() {
        this.lookupColorAndCheck(
            this.createContext(),
            Color.indexed(2),
            Color.WHITE
        );
    }

    @Test
    public void testLookupColorWithNamedColorWithMax() {
        this.lookupColorAndCheck(
            this.createContext(),
            Color.indexed(
                SpreadsheetColors.MAX
            ),
            Color.parse("#333")
        );
    }

    @Test
    public void testLookupColorWithNamedColorBlack() {
        this.lookupColorAndCheck(
            this.createContext(),
            SpreadsheetColors.BLACK,
            Color.BLACK
        );
    }

    @Test
    public void testLookupColorWithNamedColorWhite() {
        this.lookupColorAndCheck(
            this.createContext(),
            SpreadsheetColors.WHITE,
            Color.WHITE
        );
    }

    @Test
    public void testLookupColorWithNamedColorRed() {
        this.lookupColorAndCheck(
            this.createContext(),
            SpreadsheetColors.RED,
            WebColorName.RED.color()
        );
    }

    @Test
    public void testLookupColorWithNamedColorGreen() {
        this.lookupColorAndCheck(
            this.createContext(),
            SpreadsheetColors.GREEN,
            WebColorName.LIME.color()
        );
    }

    @Test
    public void testLookupColorWithNamedColorBlue() {
        this.lookupColorAndCheck(
            this.createContext(),
            SpreadsheetColors.BLUE,
            WebColorName.BLUE.color()
        );
    }

    @Test
    public void testLookupColorWithNamedColorYellow() {
        this.lookupColorAndCheck(
            this.createContext(),
            SpreadsheetColors.YELLOW,
            WebColorName.YELLOW.color()
        );
    }

    @Test
    public void testLookupColorWithNamedColorMagenta() {
        this.lookupColorAndCheck(
            this.createContext(),
            SpreadsheetColors.MAGENTA,
            WebColorName.MAGENTA.color()
        );
    }

    @Test
    public void testLookupColorWithNamedColorCyan() {
        this.lookupColorAndCheck(
            this.createContext(),
            SpreadsheetColors.CYAN,
            WebColorName.CYAN.color()
        );
    }

    // namedColor.......................................................................................................

    @Test
    public void testNamedColorWithBlack() {
        this.namedColorAndCheck(
            this.createContext(),
            "BLACK",
            SpreadsheetColors.BLACK
        );
    }

    @Test
    public void testNamedColorWithWhite() {
        this.namedColorAndCheck(
            this.createContext(),
            "WHITE",
            SpreadsheetColors.WHITE
        );
    }

    // parseColor.......................................................................................................

    @Test
    public void testParseIndexedColorWithMinLessOneFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .parseColor(
                    String.valueOf(
                        SpreadsheetColors.MIN - 1
                    )
                )
        );
    }

    @Test
    public void testParseIndexedColorWithMaxMoreOneFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .parseColor(
                    String.valueOf(
                        SpreadsheetColors.MAX + 1
                    )
                )
        );
    }

    @Test
    public void testParseIndexedColor() {
        this.parseColorAndCheck(
            this.createContext(),
            "1",
            Color.indexed(1)
        );
    }

    @Test
    public void testParseNamedColor() {
        this.parseColorAndCheck(
            this.createContext(),
            "RED",
            Color.parse("RED")
        );
    }

    @Test
    public void testParseRgbColor() {
        this.parseColorAndCheck(
            this.createContext(),
            "#123",
            Color.parse("#123")
        );
    }

    @Override
    public ColorContextSpreadsheetMetadataDefault createContext() {
        return ColorContextSpreadsheetMetadataDefault.INSTANCE;
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createContext(),
            "color1: black\n" +
                "color2: white\n" +
                "color3: red\n" +
                "color4: lime\n" +
                "color5: blue\n" +
                "color6: yellow\n" +
                "color7: magenta\n" +
                "color8: cyan\n" +
                "color9: maroon\n" +
                "color10: green\n" +
                "color11: navy\n" +
                "color12: olive\n" +
                "color13: purple\n" +
                "color14: teal\n" +
                "color15: silver\n" +
                "color16: grey\n" +
                "color17: #99f\n" +
                "color18: #936\n" +
                "color19: #ffc\n" +
                "color20: #cff\n" +
                "color21: #606\n" +
                "color22: #ff8080\n" +
                "color23: #06c\n" +
                "color24: #ccf\n" +
                "color25: navy\n" +
                "color26: magenta\n" +
                "color27: yellow\n" +
                "color28: cyan\n" +
                "color29: purple\n" +
                "color30: maroon\n" +
                "color31: teal\n" +
                "color32: blue\n" +
                "color33: #0cf\n" +
                "color34: #cff\n" +
                "color35: #cfc\n" +
                "color36: #ff9\n" +
                "color37: #9cf\n" +
                "color38: #f9c\n" +
                "color39: #c9f\n" +
                "color40: #fc9\n" +
                "color41: #36f\n" +
                "color42: #3cc\n" +
                "color43: #9c0\n" +
                "color44: #fc0\n" +
                "color45: #f90\n" +
                "color46: #f60\n" +
                "color47: #669\n" +
                "color48: #969696\n" +
                "color49: #036\n" +
                "color50: #396\n" +
                "color51: #030\n" +
                "color52: #330\n" +
                "color53: #930\n" +
                "color54: #936\n" +
                "color55: #339\n" +
                "color56: #333\n" +
                "colorBlack: 1\n" +
                "colorBlue: 5\n" +
                "colorCyan: 8\n" +
                "colorGreen: 4\n" +
                "colorMagenta: 7\n" +
                "colorRed: 3\n" +
                "colorWhite: 2\n" +
                "colorYellow: 6\n"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testPrintTree() {
        this.treePrintAndCheck(
            this.createContext(),
            "ColorContextSpreadsheetMetadataDefault\n" +
                "  color1: black\n" +
                "  color2: white\n" +
                "  color3: red\n" +
                "  color4: lime\n" +
                "  color5: blue\n" +
                "  color6: yellow\n" +
                "  color7: magenta\n" +
                "  color8: cyan\n" +
                "  color9: maroon\n" +
                "  color10: green\n" +
                "  color11: navy\n" +
                "  color12: olive\n" +
                "  color13: purple\n" +
                "  color14: teal\n" +
                "  color15: silver\n" +
                "  color16: grey\n" +
                "  color17: #99f\n" +
                "  color18: #936\n" +
                "  color19: #ffc\n" +
                "  color20: #cff\n" +
                "  color21: #606\n" +
                "  color22: #ff8080\n" +
                "  color23: #06c\n" +
                "  color24: #ccf\n" +
                "  color25: navy\n" +
                "  color26: magenta\n" +
                "  color27: yellow\n" +
                "  color28: cyan\n" +
                "  color29: purple\n" +
                "  color30: maroon\n" +
                "  color31: teal\n" +
                "  color32: blue\n" +
                "  color33: #0cf\n" +
                "  color34: #cff\n" +
                "  color35: #cfc\n" +
                "  color36: #ff9\n" +
                "  color37: #9cf\n" +
                "  color38: #f9c\n" +
                "  color39: #c9f\n" +
                "  color40: #fc9\n" +
                "  color41: #36f\n" +
                "  color42: #3cc\n" +
                "  color43: #9c0\n" +
                "  color44: #fc0\n" +
                "  color45: #f90\n" +
                "  color46: #f60\n" +
                "  color47: #669\n" +
                "  color48: #969696\n" +
                "  color49: #036\n" +
                "  color50: #396\n" +
                "  color51: #030\n" +
                "  color52: #330\n" +
                "  color53: #930\n" +
                "  color54: #936\n" +
                "  color55: #339\n" +
                "  color56: #333\n" +
                "  colorBlack: 1\n" +
                "  colorBlue: 5\n" +
                "  colorCyan: 8\n" +
                "  colorGreen: 4\n" +
                "  colorMagenta: 7\n" +
                "  colorRed: 3\n" +
                "  colorWhite: 2\n" +
                "  colorYellow: 6\n"
        );
    }

    // class............................................................................................................

    @Override
    public Class<ColorContextSpreadsheetMetadataDefault> type() {
        return ColorContextSpreadsheetMetadataDefault.class;
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
}

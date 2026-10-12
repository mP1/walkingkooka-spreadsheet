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
            "color1: black\r\n" +
                "color2: white\r\n" +
                "color3: red\r\n" +
                "color4: lime\r\n" +
                "color5: blue\r\n" +
                "color6: yellow\r\n" +
                "color7: magenta\r\n" +
                "color8: cyan\r\n" +
                "color9: maroon\r\n" +
                "color10: green\r\n" +
                "color11: navy\r\n" +
                "color12: olive\r\n" +
                "color13: purple\r\n" +
                "color14: teal\r\n" +
                "color15: silver\r\n" +
                "color16: grey\r\n" +
                "color17: #99f\r\n" +
                "color18: #936\r\n" +
                "color19: #ffc\r\n" +
                "color20: #cff\r\n" +
                "color21: #606\r\n" +
                "color22: #ff8080\r\n" +
                "color23: #06c\r\n" +
                "color24: #ccf\r\n" +
                "color25: navy\r\n" +
                "color26: magenta\r\n" +
                "color27: yellow\r\n" +
                "color28: cyan\r\n" +
                "color29: purple\r\n" +
                "color30: maroon\r\n" +
                "color31: teal\r\n" +
                "color32: blue\r\n" +
                "color33: #0cf\r\n" +
                "color34: #cff\r\n" +
                "color35: #cfc\r\n" +
                "color36: #ff9\r\n" +
                "color37: #9cf\r\n" +
                "color38: #f9c\r\n" +
                "color39: #c9f\r\n" +
                "color40: #fc9\r\n" +
                "color41: #36f\r\n" +
                "color42: #3cc\r\n" +
                "color43: #9c0\r\n" +
                "color44: #fc0\r\n" +
                "color45: #f90\r\n" +
                "color46: #f60\r\n" +
                "color47: #669\r\n" +
                "color48: #969696\r\n" +
                "color49: #036\r\n" +
                "color50: #396\r\n" +
                "color51: #030\r\n" +
                "color52: #330\r\n" +
                "color53: #930\r\n" +
                "color54: #936\r\n" +
                "color55: #339\r\n" +
                "color56: #333\r\n" +
                "colorBlack: 1\r\n" +
                "colorBlue: 5\r\n" +
                "colorCyan: 8\r\n" +
                "colorGreen: 4\r\n" +
                "colorMagenta: 7\r\n" +
                "colorRed: 3\r\n" +
                "colorWhite: 2\r\n" +
                "colorYellow: 6\r\n"
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

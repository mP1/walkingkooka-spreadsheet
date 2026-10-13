
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
import walkingkooka.color.HslColor;
import walkingkooka.color.HsvColor;
import walkingkooka.reflect.ThrowableTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetColorContextEmptyTest implements SpreadsheetColorContextTesting2<SpreadsheetColorContextEmpty>,
    ThrowableTesting,
    ToStringTesting<SpreadsheetColorContextEmpty> {

    // indexedColor.....................................................................................................

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
    public void testIndexedColorWithMinPlus1() {
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
            Color.indexed(SpreadsheetColors.MIN)
        );
    }

    @Test
    public void testLookupColorWithIndexedColor2() {
        this.lookupColorAndCheck(
            this.createContext(),
            Color.indexed(2)
        );
    }

    @Test
    public void testLookupColorWithNamedColorWithMax() {
        this.lookupColorAndCheck(
            this.createContext(),
            Color.indexed(
                SpreadsheetColors.MAX
            )
        );
    }

    @Test
    public void testLookupColorWithNamedColorBlack() {
        this.lookupColorAndCheck(
            this.createContext(),
            SpreadsheetColors.BLACK
        );
    }

    @Test
    public void testLookupColorWithNamedColorWhite() {
        this.lookupColorAndCheck(
            this.createContext(),
            SpreadsheetColors.WHITE
        );
    }

    @Test
    public void testLookupColorWithHslColor() {
        final HslColor hsl = Color.BLACK.toHsl();
        
        this.lookupColorAndCheck(
            this.createContext(),
            hsl,
            hsl
        );
    }

    @Test
    public void testLookupColorWithHsvColor() {
        final HsvColor hsv = Color.BLACK.toHsv();

        this.lookupColorAndCheck(
            this.createContext(),
            hsv,
            hsv
        );
    }
    
    @Test
    public void testLookupColorWithRgbColor() {
        this.lookupColorAndCheck(
            this.createContext(),
            Color.BLACK,
            Color.BLACK
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
    public void testParseColorWithIndexedColor() {
        this.parseColorAndCheck(
            this.createContext(),
            "1",
            Color.indexed(1)
        );
    }

    @Test
    public void testParseColorWithNamedColor() {
        this.parseColorAndCheck(
            this.createContext(),
            "RED",
            Color.parse("RED")
        );
    }

    @Test
    public void testParseColorWithRgbColor() {
        this.parseColorAndCheck(
            this.createContext(),
            "#123",
            Color.parse("#123")
        );
    }

    @Override
    public SpreadsheetColorContextEmpty createContext() {
        return SpreadsheetColorContextEmpty.INSTANCE;
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createContext(),
            ""
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testPrintTree() {
        this.treePrintAndCheck(
            this.createContext(),
            "SpreadsheetColorContextEmpty\n"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetColorContextEmpty> type() {
        return SpreadsheetColorContextEmpty.class;
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
}

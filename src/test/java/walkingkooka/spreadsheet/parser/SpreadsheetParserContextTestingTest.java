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

package walkingkooka.spreadsheet.parser;

import org.junit.jupiter.api.Test;
import walkingkooka.reflect.PublicClassTesting;

public final class SpreadsheetParserContextTestingTest implements SpreadsheetParserContextTesting,
    PublicClassTesting<SpreadsheetParserContextTesting> {

    @Test
    public void testSpreadsheetParserContextConstantToString() {
        this.checkEquals(
            SPREADSHEET_PARSER_CONTEXT.toString(),
            "canIndexedNamedColor=color1: black\n" +
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
                "color invalidCharacterExceptionFactory=POSITION_EXPECTED dateTimeContext=symbols=ampms=\"am\", \"pm\" monthNa"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetParserContextTesting> type() {
        return SpreadsheetParserContextTesting.class;
    }
}

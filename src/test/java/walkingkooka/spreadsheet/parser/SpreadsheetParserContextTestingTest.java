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
            "invalidCharacterExceptionFactory=POSITION_EXPECTED dateTimeContext=symbols=ampms=\"am\", \"pm\" monthNames=\"January\", \"February\", \"March\", \"April\", \"May\", \"June\", \"July\", \"August\", \"September\", \"October\", \"November\", \"December\" monthNameAbbreviations=\"Jan.\", \"Feb.\", \"Mar.\", \"Apr.\", \"May\", \"Jun.\", \"Jul.\", \"Aug.\", \"Sep.\", \"Oct.\", \"Nov.\", \"Dec.\" weekDayNames=\"Sunday\", \"Monday\", \"Tuesday\", \"Wednesday\", \"Thursday\", \"Friday\", \"Saturday\" weekDayNameAbbreviations=\"Sun.\", \"Mon.\", \"Tue.\", \"Wed.\", \"Thu.\", \"Fri.\", \"Sat.\" locale=\"en-AU\" twoDigitYear=50 expressionNumberContext=expressionNumberKind=BIG_DECIMAL decimalNumberContext=decimalNumberDigitNumberCount=9 symbols=negativeSign='-' positiveSign='+' zeroDigit='0' currencySymbol=\"$\" decimalSeparator='.' exponentSymbol=\"e\" groupSeparator=',' infinitySymbol=\"∞\" monetaryDecimalSeparator='.' nanSymbol=\"NaN\" percentSymbol='%' permillSymbol='‰' locale=en_AU mathContext=precision=7 roundingMode=HALF_EVEN valueSeparator=','"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetParserContextTesting> type() {
        return SpreadsheetParserContextTesting.class;
    }
}

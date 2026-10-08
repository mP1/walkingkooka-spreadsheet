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

package walkingkooka.spreadsheet.format.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;

import java.math.MathContext;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetFormatterProviderSamplesContextBasicTest implements SpreadsheetFormatterProviderSamplesContextTesting<SpreadsheetFormatterProviderSamplesContextBasic>,
    SpreadsheetMetadataTesting,
    DecimalNumberContextDelegator,
    ToStringTesting<SpreadsheetFormatterProviderSamplesContextBasic> {

    @Test
    public void testWithNullSpreadsheetFormatterContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormatterProviderSamplesContextBasic.with(
                null,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullProviderContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormatterProviderSamplesContextBasic.with(
                SPREADSHEET_FORMATTER_CONTEXT,
                null
            )
        );
    }

    @Override
    public SpreadsheetFormatterProviderSamplesContextBasic createContext() {
        return SpreadsheetFormatterProviderSamplesContextBasic.with(
            SPREADSHEET_FORMATTER_CONTEXT,
            PROVIDER_CONTEXT
        );
    }

    // DecimalNumberContextDelegator....................................................................................

    @Override
    public int decimalNumberDigitCount() {
        return SPREADSHEET_FORMATTER_CONTEXT.decimalNumberDigitCount();
    }

    @Override
    public MathContext mathContext() {
        return SPREADSHEET_FORMATTER_CONTEXT.mathContext();
    }

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return SPREADSHEET_FORMATTER_CONTEXT;
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createContext(),
            "spreadsheetFormatterContext=cell=A1 valueType=text/Text currency=\"AUD\" currencyExchangeRater=\"test-currency-exchange-rater-111\" dateTimeSymbols=\"ampms=\"am\", \"pm\" monthNames=\"January\", \"February\", \"March\", \"April\", \"May\", \"June\", \"July\", \"August\", \"September\", \"October\", \"November\", \"December\" monthNameAbbreviations=\"Jan.\", \"Feb.\", \"Mar.\", \"Apr.\", \"May\", \"Jun.\", \"Jul.\", \"Aug.\", \"Sep.\", \"Oct.\", \"Nov.\", \"Dec.\" weekDayNames=\"Sunday\", \"Monday\", \"Tuesday\", \"Wednesday\", \"Thursday\", \"Friday\", \"Saturday\" weekDayNameAbbreviations=\"Sun.\", \"Mon.\", \"Tue.\", \"Wed.\", \"Thu.\", \"Fri.\", \"Sat.\"\" decimalNumberSymbols=\"negativeSign='-' positiveSign='+' zeroDigit='0' currencySymbol=\"$\" decimalSeparator='.' exponentSymbol=\"e\" groupSeparator=',' infinitySymbol=\"∞\" monetaryDecimalSeparator='.' nanSymbol=\"NaN\" percentSymbol='%' permillSymbol='‰'\" formatter=\"formatter-selector-111\" formattedValue=\"formatted-value\" l providerContext=ReadOnly mediaTypeDetector=application/octet-stream multiplier=walkingkooka.tree.ex"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetFormatterProviderSamplesContextBasic> type() {
        return SpreadsheetFormatterProviderSamplesContextBasic.class;
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
}

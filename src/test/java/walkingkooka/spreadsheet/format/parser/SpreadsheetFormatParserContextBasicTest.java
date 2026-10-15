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

package walkingkooka.spreadsheet.format.parser;

import org.junit.jupiter.api.Test;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;

import java.math.MathContext;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetFormatParserContextBasicTest implements ClassTesting2<SpreadsheetFormatParserContextBasic>,
    SpreadsheetFormatParserContextTesting2<SpreadsheetFormatParserContextBasic>,
    DecimalNumberContextDelegator {

    @Test
    public void testWithNullInvalidCharacterExceptionFactoryFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormatParserContextBasic.with(null)
        );
    }

    @Override
    public void testInvalidCharacterExceptionWithNullParserFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testInvalidCharacterExceptionWithNullTextCursorFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testLocale() {
        this.localeAndCheck(
            this.createContext(),
            this.decimalNumberContext()
                .locale()
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createContext(),
            "POSITION locale=en_US \"mathContext\" precision=0 roundingMode=HALF_UP \"decimalNumberSymbols\" negativeSign='-' positiveSign='+' zeroDigit='0' currencySymbol=\"$\" decimalSeparator='.' exponentSymbol=\"E\" groupSeparator=',' infinitySymbol=\"∞\" monetaryDecimalSeparator='.' nanSymbol=\"NaN\" percentSymbol='%' permillSymbol='‰'"
        );
    }

    @Override
    public SpreadsheetFormatParserContextBasic createContext() {
        return SpreadsheetFormatParserContextBasic.with(InvalidCharacterExceptionFactory.POSITION);
    }

    // DecimalNumberContext.............................................................................................

    @Override
    public int decimalNumberDigitCount() {
        return this.decimalNumberContext()
            .decimalNumberDigitCount();
    }

    @Override
    public MathContext mathContext() {
        return MathContext.UNLIMITED;
    }

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return DecimalNumberContexts.american(this.mathContext());
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetFormatParserContextBasic> type() {
        return SpreadsheetFormatParserContextBasic.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
}

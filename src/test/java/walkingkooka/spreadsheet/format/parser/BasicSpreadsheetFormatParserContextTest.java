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
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import java.math.MathContext;

public final class BasicSpreadsheetFormatParserContextTest implements ClassTesting2<BasicSpreadsheetFormatParserContext>,
        SpreadsheetFormatParserContextTesting<BasicSpreadsheetFormatParserContext> {

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
        this.hasLocaleAndCheck(BasicSpreadsheetFormatParserContext.INSTANCE, this.decimalNumberContext().locale());
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(BasicSpreadsheetFormatParserContext.INSTANCE, DecimalNumberContexts.american(MathContext.UNLIMITED).toString());
    }

    @Override
    public BasicSpreadsheetFormatParserContext createContext() {
        return BasicSpreadsheetFormatParserContext.INSTANCE;
    }

    @Override
    public String currencySymbol() {
        return this.decimalNumberContext().currencySymbol();
    }

    @Override
    public char decimalSeparator() {
        return this.decimalNumberContext().decimalSeparator();
    }

    @Override
    public String exponentSymbol() {
        return this.decimalNumberContext().exponentSymbol();
    }

    @Override
    public char groupSeparator() {
        return this.decimalNumberContext().groupSeparator();
    }

    @Override
    public MathContext mathContext() {
        return MathContext.UNLIMITED;
    }

    @Override
    public char negativeSign() {
        return this.decimalNumberContext().negativeSign();
    }

    @Override
    public char percentageSymbol() {
        return this.decimalNumberContext().percentageSymbol();
    }

    @Override
    public char positiveSign() {
        return this.decimalNumberContext().positiveSign();
    }

    private DecimalNumberContext decimalNumberContext() {
        return DecimalNumberContexts.american(this.mathContext());
    }

    @Override
    public Class<BasicSpreadsheetFormatParserContext> type() {
        return BasicSpreadsheetFormatParserContext.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}

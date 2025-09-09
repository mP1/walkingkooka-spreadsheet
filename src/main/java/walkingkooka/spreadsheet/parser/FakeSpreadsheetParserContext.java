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

import walkingkooka.InvalidCharacterException;
import walkingkooka.datetime.FakeDateTimeContext;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.MathContext;
import java.util.Locale;

public class FakeSpreadsheetParserContext extends FakeDateTimeContext implements SpreadsheetParserContext {

    @Override
    public String currencySymbol() {
        throw new UnsupportedOperationException();
    }

    @Override
    public char decimalSeparator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String exponentSymbol() {
        throw new UnsupportedOperationException();
    }

    @Override
    public char groupSeparator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String infinitySymbol() {
        throw new UnsupportedOperationException();
    }

    @Override
    public char monetaryDecimalSeparator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String nanSymbol() {
        throw new UnsupportedOperationException();
    }

    @Override
    public char negativeSign() {
        throw new UnsupportedOperationException();
    }

    @Override
    public char percentSymbol() {
        throw new UnsupportedOperationException();
    }

    @Override
    public char permillSymbol() {
        throw new UnsupportedOperationException();
    }

    @Override
    public char positiveSign() {
        throw new UnsupportedOperationException();
    }

    @Override
    public char zeroDigit() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DecimalNumberSymbols decimalNumberSymbols() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MathContext mathContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canNumbersHaveGroupSeparator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public InvalidCharacterException invalidCharacterException(final Parser<?> parser,
                                                               final TextCursor cursor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Locale locale() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ExpressionNumberKind expressionNumberKind() {
        throw new UnsupportedOperationException();
    }

    @Override
    public char valueSeparator() {
        throw new UnsupportedOperationException();
    }
}

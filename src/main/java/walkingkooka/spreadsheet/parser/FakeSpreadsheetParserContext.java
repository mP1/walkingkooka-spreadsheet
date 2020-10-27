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

import walkingkooka.datetime.FakeDateTimeContext;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.MathContext;
import java.util.List;
import java.util.Locale;

public class FakeSpreadsheetParserContext extends FakeDateTimeContext implements SpreadsheetParserContext{

    @Override
    public String currencySymbol() {
        throw new UnsupportedOperationException();
    }

    @Override
    public char decimalSeparator() {
        return 0;
    }

    @Override
    public String exponentSymbol() {
        throw new UnsupportedOperationException();
    }

    @Override
    public char groupingSeparator() {
        return 0;
    }

    @Override
    public char percentageSymbol() {
        return 0;
    }

    @Override
    public MathContext mathContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public char negativeSign() {
        throw new UnsupportedOperationException();
    }

    @Override
    public char positiveSign() {
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
}

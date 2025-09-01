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

package walkingkooka.spreadsheet.format;

import walkingkooka.color.Color;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContextDelegator;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContextTestingTest.TestSpreadsheetFormatterContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;
import walkingkooka.tree.text.TextNode;

import java.math.MathContext;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public final class SpreadsheetFormatterContextTestingTest implements SpreadsheetFormatterContextTesting2<TestSpreadsheetFormatterContext>,
    SpreadsheetMetadataTesting {

    @Override
    public TestSpreadsheetFormatterContext createContext() {
        return new TestSpreadsheetFormatterContext();
    }

    @Override
    public MathContext mathContext() {
        return SPREADSHEET_FORMATTER_CONTEXT.mathContext();
    }

    @Override
    public String currencySymbol() {
        return DECIMAL_NUMBER_SYMBOLS.currencySymbol();
    }

    @Override
    public char decimalSeparator() {
        return DECIMAL_NUMBER_SYMBOLS.decimalSeparator();
    }

    @Override
    public String exponentSymbol() {
        return DECIMAL_NUMBER_SYMBOLS.exponentSymbol();
    }

    @Override
    public char groupSeparator() {
        return DECIMAL_NUMBER_SYMBOLS.groupSeparator();
    }

    @Override
    public String infinitySymbol() {
        return DECIMAL_NUMBER_SYMBOLS.infinitySymbol();
    }

    @Override
    public char monetaryDecimalSeparator() {
        return DECIMAL_NUMBER_SYMBOLS.monetaryDecimalSeparator();
    }

    @Override
    public String nanSymbol() {
        return DECIMAL_NUMBER_SYMBOLS.nanSymbol();
    }

    @Override
    public char percentSymbol() {
        return DECIMAL_NUMBER_SYMBOLS.percentSymbol();
    }

    @Override
    public char permillSymbol() {
        return DECIMAL_NUMBER_SYMBOLS.permillSymbol();
    }

    @Override
    public char negativeSign() {
        return DECIMAL_NUMBER_SYMBOLS.negativeSign();
    }

    @Override
    public char positiveSign() {
        return DECIMAL_NUMBER_SYMBOLS.positiveSign();
    }

    @Override
    public char zeroDigit() {
        return DECIMAL_NUMBER_SYMBOLS.zeroDigit();
    }

    // class............................................................................................................

    @Override
    public Class<TestSpreadsheetFormatterContext> type() {
        return TestSpreadsheetFormatterContext.class;
    }

    static class TestSpreadsheetFormatterContext implements SpreadsheetFormatterContext,
        SpreadsheetConverterContextDelegator {

        // SpreadsheetConverterContextDelegator.........................................................................

        @Override
        public SpreadsheetConverterContext spreadsheetConverterContext() {
            return SPREADSHEET_FORMATTER_CONTEXT;
        }

        @Override
        public int cellCharacterWidth() {
            return 0;
        }

        @Override
        public Optional<Color> colorNumber(final int number) {
            return Optional.empty();
        }

        @Override
        public Optional<Color> colorName(final SpreadsheetColorName name) {
            return Optional.empty();
        }

        @Override
        public Optional<TextNode> formatValue(final Optional<Object> value) {
            return Optional.empty();
        }

        @Override
        public int generalFormatNumberDigitCount() {
            return 0;
        }

        @Override
        public SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext(final Optional<Object> value) {
            Objects.requireNonNull(value, "value");
            throw new UnsupportedOperationException();
        }

        @Override
        public SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterSelector selector) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SpreadsheetFormatterContext setLocale(final Locale locale) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SpreadsheetFormatterContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<SpreadsheetCell> cell() {
            return Optional.empty();
        }

        @Override
        public SpreadsheetExpressionReference validationReference() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }
}

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

package walkingkooka.spreadsheet.formula;

import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContextDelegator;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContextTesting;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.tree.expression.ExpressionNumberContext;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.MathContext;
import java.text.DateFormatSymbols;
import java.time.LocalDateTime;
import java.util.Locale;

public final class SpreadsheetParserContextDelegatorTest implements SpreadsheetParserContextTesting<SpreadsheetParserContextDelegatorTest.TestSpreadsheetParserContextDelegator>,
    DecimalNumberContextDelegator {

    @Override
    public TestSpreadsheetParserContextDelegator createContext() {
        return new TestSpreadsheetParserContextDelegator();
    }

    private final static ExpressionNumberContext EXPRESSION_NUMBER_CONTEXT = ExpressionNumberContexts.basic(
        ExpressionNumberKind.BIG_DECIMAL,
        DecimalNumberContexts.american(MathContext.DECIMAL32)
    );

    // DecimalNumberContextDelegator....................................................................................

    @Override
    public int decimalNumberDigitCount() {
        return EXPRESSION_NUMBER_CONTEXT.decimalNumberDigitCount();
    }

    @Override
    public MathContext mathContext() {
        return EXPRESSION_NUMBER_CONTEXT.mathContext();
    }

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return EXPRESSION_NUMBER_CONTEXT;
    }

    static final class TestSpreadsheetParserContextDelegator implements SpreadsheetParserContextDelegator {

        @Override
        public SpreadsheetParserContext spreadsheetParserContext() {
            final Locale locale = Locale.ENGLISH;

            return SpreadsheetParserContexts.basic(
                InvalidCharacterExceptionFactory.COLUMN_AND_LINE,
                DateTimeContexts.basic(
                    DateTimeSymbols.fromDateFormatSymbols(
                        new DateFormatSymbols(locale)
                    ),
                    locale,
                    1950, // defaultYear
                    50, // twoDigitYear
                    LocalDateTime::now
                ),
                EXPRESSION_NUMBER_CONTEXT,
                ',' // valueSeparator
            );
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }

    // class............................................................................................................

    @Override
    public Class<TestSpreadsheetParserContextDelegator> type() {
        return TestSpreadsheetParserContextDelegator.class;
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
}

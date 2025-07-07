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

package walkingkooka.spreadsheet.format.pattern;

import walkingkooka.ToStringBuilder;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.FakeDecimalNumberContext;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.TypeNameTesting;
import walkingkooka.text.cursor.parser.ParserTesting;

import java.math.MathContext;

public abstract class SpreadsheetNumberParsePatternTestCase<T> implements ClassTesting2<T>,
    ParserTesting,
    TypeNameTesting<T> {

    SpreadsheetNumberParsePatternTestCase() {
        super();
    }

    final static boolean NEXT_CALLED = false;
    final static boolean NEXT_SKIPPED = true;

    final static String CURRENCY = "NZ$";

    final static char GROUP = 'g';
    final static char PERCENT = 'q';
    final static String EXPONENT = "XYZ";

    final static char PLUS = 'p';
    final static char MINUS = 'm';
    final static char DECIMAL = 'd';
    final static char ZERO = '0';

    @Override
    public final DecimalNumberContext decimalNumberContext() {
        return new FakeDecimalNumberContext() {
            @Override
            public String currencySymbol() {
                return CURRENCY;
            }

            @Override
            public char decimalSeparator() {
                return DECIMAL;
            }

            @Override
            public String exponentSymbol() {
                return EXPONENT;
            }

            @Override
            public char groupSeparator() {
                return GROUP;
            }

            @Override
            public char negativeSign() {
                return MINUS;
            }

            @Override
            public char percentSymbol() {
                return PERCENT;
            }

            @Override
            public char positiveSign() {
                return PLUS;
            }

            @Override
            public char zeroDigit() {
                return ZERO;
            }

            @Override
            public MathContext mathContext() {
                return MathContext.UNLIMITED;
            }

            @Override
            public String toString() {
                return ToStringBuilder.empty()
                    .label("currencySymbol").value(this.currencySymbol())
                    .label("decimalSeparator").value(this.decimalSeparator())
                    .label("exponentSymbol").value(this.exponentSymbol())
                    .label("groupSeparator").value(this.groupSeparator())
                    .label("negativeSign").value(this.negativeSign())
                    .label("percentSymbol").value(this.percentSymbol())
                    .label("positiveSign").value(this.positiveSign())
                    .label("zeroDigit").value(this.zeroDigit())
                    .label("mathContext").value(this.mathContext())
                    .build();
            }
        };
    }

    // ClassTesting.....................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}

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

import walkingkooka.ToStringBuilder;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.FakeDecimalNumberContext;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.TypeNameTesting;
import walkingkooka.type.JavaVisibility;

import java.math.MathContext;

public abstract class SpreadsheetNumberParsePatternsTestCase<T> implements ClassTesting2<T>,
        TypeNameTesting<T> {

    SpreadsheetNumberParsePatternsTestCase() {
        super();
    }

    final static String CURRENCY = "aud";

    public final DecimalNumberContext decimalNumberContext() {
        return new FakeDecimalNumberContext() {
            @Override
            public String currencySymbol() {
                return CURRENCY;
            }

            @Override
            public char decimalSeparator() {
                return 'D';
            }

            @Override
            public char exponentSymbol() {
                return 'X';
            }

            @Override
            public char groupingSeparator() {
                return 'G';
            }

            @Override
            public char negativeSign() {
                return 'N';
            }

            @Override
            public char percentageSymbol() {
                return 'P';
            }

            @Override
            public char positiveSign() {
                return 'Q';
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
                        .label("groupingSeparator").value(this.groupingSeparator())
                        .label("negativeSign").value(this.negativeSign())
                        .label("percentageSymbol").value(this.percentageSymbol())
                        .label("positiveSign").value(this.positiveSign())
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

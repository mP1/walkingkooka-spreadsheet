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

import walkingkooka.math.DecimalNumberContext;

import java.math.MathContext;
import java.util.Objects;

/**
 * A {@link SpreadsheetFormatParserContext} without any functionality.
 */
final class BasicSpreadsheetFormatParserContext implements SpreadsheetFormatParserContext {

    static BasicSpreadsheetFormatParserContext with(final DecimalNumberContext context) {
        Objects.requireNonNull(context, "context");

        return new BasicSpreadsheetFormatParserContext(context);
    }

    private BasicSpreadsheetFormatParserContext(final DecimalNumberContext context) {
        super();
        this.context = context;
    }

    @Override
    public String currencySymbol() {
        return this.context.currencySymbol();
    }

    @Override
    public char decimalPoint() {
        return this.context.decimalPoint();
    }

    @Override
    public char exponentSymbol() {
        return this.context.exponentSymbol();
    }

    @Override
    public char groupingSeparator() {
        return this.context.groupingSeparator();
    }

    @Override
    public char minusSign() {
        return this.context.minusSign();
    }

    @Override
    public char percentageSymbol() {
        return this.context.percentageSymbol();
    }

    @Override
    public char plusSign() {
        return this.context.plusSign();
    }

    @Override
    public MathContext mathContext() {
        return this.context.mathContext();
    }

    private final DecimalNumberContext context;

    @Override
    public String toString() {
        return this.context.toString();
    }
}

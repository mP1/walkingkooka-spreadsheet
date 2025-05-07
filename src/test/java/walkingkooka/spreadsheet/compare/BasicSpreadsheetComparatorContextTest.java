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

package walkingkooka.spreadsheet.compare;

import org.junit.jupiter.api.Test;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.MathContext;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetComparatorContextTest implements SpreadsheetComparatorContextTesting<BasicSpreadsheetComparatorContext> {

    @Test
    public void testWithNullConverterContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetComparatorContext.with(
                        null
                )
        );
    }

    @Test
    public void testConvert() {
        this.convertAndCheck(
                this.createContext(),
                LocalDate.of(1999, 12, 31),
                String.class,
                "1999-12-31"
        );
    }

    @Test
    public void testToString() {
        final SpreadsheetConverterContext SpreadsheetConverterContext = SpreadsheetConverterContexts.fake();
        this.toStringAndCheck(
                BasicSpreadsheetComparatorContext.with(
                        SpreadsheetConverterContext
                ),
                SpreadsheetConverterContext.toString()
        );
    }

    @Override
    public BasicSpreadsheetComparatorContext createContext() {
        return BasicSpreadsheetComparatorContext.with(
                CONVERTER_CONTEXT
        );
    }

    private final SpreadsheetConverterContext CONVERTER_CONTEXT = SpreadsheetConverterContexts.basic(
            Converters.objectToString(),
            (label) -> {
                Objects.requireNonNull(label, "label");
                throw new UnsupportedOperationException();
            },
            ExpressionNumberConverterContexts.basic(
                    Converters.fake(),
                    ConverterContexts.basic(
                            Converters.JAVA_EPOCH_OFFSET, // dateOffset
                            Converters.objectToString(),
                            DateTimeContexts.basic(
                                    DateTimeSymbols.fromDateFormatSymbols(
                                            new DateFormatSymbols(
                                                    Locale.forLanguageTag("EN-AU")
                                            )
                                    ),
                                    Locale.forLanguageTag("EN-AU"),
                                    1950, // default year
                                    50, // two-digit-year
                                    LocalDateTime::now
                            ),
                            DecimalNumberContexts.american(
                                    MathContext.DECIMAL32
                            )
                    ),
                    ExpressionNumberKind.BIG_DECIMAL
            )
    );

    @Override
    public String currencySymbol() {
        return CONVERTER_CONTEXT.currencySymbol();
    }

    @Override
    public char decimalSeparator() {
        return CONVERTER_CONTEXT.decimalSeparator();
    }

    @Override
    public String exponentSymbol() {
        return CONVERTER_CONTEXT.exponentSymbol();
    }

    @Override
    public char groupSeparator() {
        return CONVERTER_CONTEXT.groupSeparator();
    }

    @Override
    public MathContext mathContext() {
        return CONVERTER_CONTEXT.mathContext();
    }

    @Override
    public char negativeSign() {
        return CONVERTER_CONTEXT.negativeSign();
    }

    @Override
    public char percentageSymbol() {
        return CONVERTER_CONTEXT.percentageSymbol();
    }

    @Override
    public char positiveSign() {
        return CONVERTER_CONTEXT.positiveSign();
    }

    @Override
    public Class<BasicSpreadsheetComparatorContext> type() {
        return BasicSpreadsheetComparatorContext.class;
    }
}

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

import org.junit.jupiter.api.Test;
import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetTextFormatContextTest implements SpreadsheetTextFormatContextTesting<BasicSpreadsheetTextFormatContext> {

    @Test
    public void testWithNullNumberToColorFails() {
        this.withFails(null,
                this.nameToColor(),
                WIDTH,
                CONVERTER,
                this.defaultSpreadsheetTextFormatter(),
                this.dateTimeContext(),
                this.decimalNumberContext());
    }

    @Test
    public void testWithNullNameToColorFails() {
        this.withFails(this.numberToColor(),
                null,
                WIDTH,
                CONVERTER,
                this.defaultSpreadsheetTextFormatter(),
                this.dateTimeContext(),
                this.decimalNumberContext());
    }

    @Test
    public void testWithInvalidWidthFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            BasicSpreadsheetTextFormatContext.with(this.numberToColor(),
                    this.nameToColor(),
                    -1,
                    CONVERTER,
                    this.defaultSpreadsheetTextFormatter(),
                    this.dateTimeContext(),
                    this.decimalNumberContext());
        });
    }

    @Test
    public void testWithInvalidWidthFails2() {
        assertThrows(IllegalArgumentException.class, () -> {
            BasicSpreadsheetTextFormatContext.with(this.numberToColor(),
                    this.nameToColor(),
                    0,
                    CONVERTER,
                    this.defaultSpreadsheetTextFormatter(),
                    this.dateTimeContext(),
                    this.decimalNumberContext());
        });
    }

    @Test
    public void testWithNullConverterFails() {
        this.withFails(this.numberToColor(),
                this.nameToColor(),
                WIDTH,
                null,
                this.defaultSpreadsheetTextFormatter(),
                this.dateTimeContext(),
                this.decimalNumberContext());
    }

    @Test
    public void testWithNullDefaultSpreadsheetTextFormatterFails() {
        this.withFails(this.numberToColor(),
                this.nameToColor(),
                WIDTH,
                CONVERTER,
                null,
                this.dateTimeContext(),
                this.decimalNumberContext());
    }

    @Test
    public void testWIthNullDateTimeContextFails() {
        this.withFails(this.numberToColor(),
                this.nameToColor(),
                WIDTH,
                CONVERTER,
                this.defaultSpreadsheetTextFormatter(),
                null,
                this.decimalNumberContext());
    }

    @Test
    public void testWithNullDecimalNumberContextFails() {
        this.withFails(this.numberToColor(),
                this.nameToColor(),
                WIDTH,
                CONVERTER,
                this.defaultSpreadsheetTextFormatter(),
                this.dateTimeContext(),
                null);
    }

    private void withFails(final Function<Integer, Optional<Color>> numberToColor,
                           final Function<String, Optional<Color>> nameToColor,
                           final int width,
                           final Converter converter,
                           final SpreadsheetTextFormatter defaultSpreadsheetTextFormatter,
                           final DateTimeContext dateTimeContext,
                           final DecimalNumberContext decimalNumberContext) {
        assertThrows(NullPointerException.class, () -> {
            BasicSpreadsheetTextFormatContext.with(numberToColor,
                    nameToColor,
                    width,
                    converter,
                    defaultSpreadsheetTextFormatter,
                    dateTimeContext,
                    decimalNumberContext);
        });
    }

    @Test
    public void testColorNumber() {
        this.colorNumberAndCheck(this.createContext(), 1, Optional.of(this.color()));
    }

    @Test
    public void testColorName() {
        this.colorNameAndCheck(this.createContext(), "bingo", Optional.of(this.color()));
    }

    @Test
    public void testConvert() {
        this.convertAndCheck(1, Boolean.class, Boolean.TRUE);
    }

    @Test
    public void testConvert2() {
        this.convertAndCheck(0, Boolean.class, Boolean.FALSE);
    }

    @Test
    public void testDefaultFormatText() {
        this.defaultFormatTextAndCheck(BigDecimal.valueOf(12.5),
                Optional.of(SpreadsheetFormattedText.with(SpreadsheetFormattedText.WITHOUT_COLOR, "012.500")));
    }

    @Test
    public void testToString() {
        final DateFormatSymbols symbols = DateFormatSymbols.getInstance(Locale.ENGLISH);

        this.toStringAndCheck(this.createContext(symbols),
                "numberToColor=1=#123456 nameToColor=bingo=#123456 width=1 converter=Truthy BigDecimal|BigInteger|Byte|Short|Integer|Long|Float|Double->Boolean dateTimeContext=" + symbols + " decimalNumberContext=\"$$\" '!' 'E' 'G' 'M' 'P' 'L' precision=7 roundingMode=HALF_EVEN");
    }

    @Override
    public BasicSpreadsheetTextFormatContext createContext() {
        return this.createContext(this.dateFormatSymbols());
    }

    private BasicSpreadsheetTextFormatContext createContext(final DateFormatSymbols dateFormatSymbols) {
        return BasicSpreadsheetTextFormatContext.with(this.numberToColor(),
                this.nameToColor(),
                WIDTH,
                CONVERTER,
                this.defaultSpreadsheetTextFormatter(),
                DateTimeContexts.dateFormatSymbols(dateFormatSymbols),
                decimalNumberContext());
    }

    private Function<Integer, Optional<Color>> numberToColor() {
        return new Function<>() {

            @Override
            public Optional<Color> apply(final Integer number) {
                assertEquals(number, 1, "color number");
                return Optional.of(BasicSpreadsheetTextFormatContextTest.this.color());
            }

            @Override
            public String toString() {
                return 1 + "=" + BasicSpreadsheetTextFormatContextTest.this.color();
            }
        };
    }

    private Function<String, Optional<Color>> nameToColor() {
        return new Function<>() {

            @Override
            public Optional<Color> apply(final String name) {
                assertEquals(name, "bingo", "color name");
                return Optional.of(BasicSpreadsheetTextFormatContextTest.this.color());
            }

            @Override
            public String toString() {
                return "bingo=" + BasicSpreadsheetTextFormatContextTest.this.color();
            }
        };
    }

    private Color color() {
        return Color.fromRgb(0x123456);
    }

    private final static int WIDTH = 1;
    private final Converter CONVERTER = Converters.truthyNumberBoolean();

    private SpreadsheetTextFormatter defaultSpreadsheetTextFormatter() {
        return new SpreadsheetTextFormatter() {
            @Override
            public boolean canFormat(final Object value) {
                return value instanceof BigDecimal;
            }

            @Override
            public Optional<SpreadsheetFormattedText> format(final Object value, final SpreadsheetTextFormatContext context) {
                return Optional.of(SpreadsheetFormattedText.with(SpreadsheetFormattedText.WITHOUT_COLOR, new DecimalFormat("000.000").format(value)));
            }
        };
    }

    private DateTimeContext dateTimeContext() {
        return DateTimeContexts.dateFormatSymbols(this.dateFormatSymbols());
    }

    private DateFormatSymbols dateFormatSymbols() {
        return DateFormatSymbols.getInstance(Locale.ENGLISH);
    }

    private DecimalNumberContext decimalNumberContext() {
        return DecimalNumberContexts.basic(this.currencySymbol(),
                this.decimalPoint(),
                this.exponentSymbol(),
                this.groupingSeparator(),
                this.minusSign(),
                this.percentageSymbol(),
                this.plusSign(),
                this.mathContext());
    }

    @Override
    public String currencySymbol() {
        return "$$";
    }

    @Override
    public char decimalPoint() {
        return '!';
    }

    @Override
    public char exponentSymbol() {
        return 'E';
    }

    @Override
    public char groupingSeparator() {
        return 'G';
    }

    @Override
    public MathContext mathContext() {
        return MathContext.DECIMAL32;
    }

    @Override
    public char minusSign() {
        return 'M';
    }

    @Override
    public char percentageSymbol() {
        return 'P';
    }

    @Override
    public char plusSign() {
        return 'L';
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<BasicSpreadsheetTextFormatContext> type() {
        return BasicSpreadsheetTextFormatContext.class;
    }
}

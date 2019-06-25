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
import walkingkooka.collect.map.Maps;
import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.FakeDateTimeContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;

import java.math.MathContext;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetTextFormatContextTest implements SpreadsheetTextFormatContextTesting<BasicSpreadsheetTextFormatContext> {

    @Test
    public void testWithNullNumberToColorFails() {
        this.withFails(null,
                this.nameToColor(),
                GENERAL_DECIMAL_FORMAT_PATTERN,
                WIDTH,
                CONVERTER,
                this.dateTimeContext(),
                this.decimalNumberContext());
    }

    @Test
    public void testWithNullNameToColorFails() {
        this.withFails(this.numberToColor(),
                null,
                GENERAL_DECIMAL_FORMAT_PATTERN,
                WIDTH,
                CONVERTER,
                this.dateTimeContext(),
                this.decimalNumberContext());
    }

    @Test
    public void testWithNullGeneralDecimalFormatPatternFails() {
        this.withFails(this.numberToColor(),
                this.nameToColor(),
                null,
                WIDTH,
                CONVERTER,
                this.dateTimeContext(),
                this.decimalNumberContext());
    }

    @Test
    public void testWithInvalidWidthFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            BasicSpreadsheetTextFormatContext.with(this.numberToColor(),
                    this.nameToColor(),
                    GENERAL_DECIMAL_FORMAT_PATTERN,
                    -1,
                    CONVERTER,
                    this.dateTimeContext(),
                    this.decimalNumberContext());
        });
    }

    @Test
    public void testWithInvalidWidthFails2() {
        assertThrows(IllegalArgumentException.class, () -> {
            BasicSpreadsheetTextFormatContext.with(this.numberToColor(),
                    this.nameToColor(),
                    GENERAL_DECIMAL_FORMAT_PATTERN,
                    0,
                    CONVERTER,
                    this.dateTimeContext(),
                    this.decimalNumberContext());
        });
    }

    @Test
    public void testWithNullConverterFails() {
        this.withFails(this.numberToColor(),
                this.nameToColor(),
                GENERAL_DECIMAL_FORMAT_PATTERN,
                WIDTH,
                null,
                this.dateTimeContext(),
                this.decimalNumberContext());
    }

    @Test
    public void testWIthNullDateTimeContextFails() {
        this.withFails(this.numberToColor(),
                this.nameToColor(),
                GENERAL_DECIMAL_FORMAT_PATTERN,
                WIDTH,
                CONVERTER,
                null,
                this.decimalNumberContext());
    }

    @Test
    public void testWithNullDecimalNumberContextFails() {
        this.withFails(this.numberToColor(),
                this.nameToColor(),
                GENERAL_DECIMAL_FORMAT_PATTERN,
                WIDTH,
                CONVERTER,
                this.dateTimeContext(),
                null);
    }

    private void withFails(final Map<Integer, Color> numberToColor,
                           final Map<String, Color> nameToColor,
                           final String generalDecimalFormatPattern,
                           final int width,
                           final Converter converter,
                           final DateTimeContext dateTimeContext,
                           final DecimalNumberContext decimalNumberContext) {
        assertThrows(NullPointerException.class, () -> {
            BasicSpreadsheetTextFormatContext.with(numberToColor,
                    nameToColor,
                    generalDecimalFormatPattern,
                    width,
                    converter,
                    dateTimeContext,
                    decimalNumberContext);
        });
    }

    @Test
    public void testColorNumber() {
        this.colorNumberAndCheck(this.createContext(), 1, this.color());
    }

    @Test
    public void testColorName() {
        this.colorNameAndCheck(this.createContext(), "bingo", this.color());
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
    public void testToString() {
        this.toStringAndCheck(this.createContext(),
                "numberToColor=1=#123456 nameToColor=bingo=#123456 generalDecimalFormatPattern=\"##.#\" width=1 converter=Truthy BigDecimal|BigInteger|Byte|Short|Integer|Long|Float|Double->Boolean dateTimeContext=DateTimeContext123 decimalNumberContext=\"$$\" '!' 'E' 'G' 'M' 'P' 'L' precision=7 roundingMode=HALF_EVEN");
    }

    @Override
    public BasicSpreadsheetTextFormatContext createContext() {
        return BasicSpreadsheetTextFormatContext.with(this.numberToColor(),
                this.nameToColor(),
                GENERAL_DECIMAL_FORMAT_PATTERN,
                WIDTH,
                CONVERTER,
                dateTimeContext(),
                decimalNumberContext());
    }

    private Map<Integer, Color> numberToColor() {
        return Maps.of(1, this.color());
    }

    private Map<String, Color> nameToColor() {
        return Maps.of("bingo", color());
    }

    private Color color() {
        return Color.fromRgb(0x123456);
    }

    private final static String GENERAL_DECIMAL_FORMAT_PATTERN = "##.#";
    private final static int WIDTH = 1;
    private final Converter CONVERTER = Converters.truthyNumberBoolean();

    private DateTimeContext dateTimeContext() {
        return new FakeDateTimeContext() {
            @Override
            public String toString() {
                return "DateTimeContext123";
            }
        };
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

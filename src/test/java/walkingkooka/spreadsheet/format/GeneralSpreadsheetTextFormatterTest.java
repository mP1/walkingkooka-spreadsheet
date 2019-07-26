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
import walkingkooka.convert.ConversionException;
import walkingkooka.text.CharSequences;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public final class GeneralSpreadsheetTextFormatterTest extends SpreadsheetTextFormatterTestCase<GeneralSpreadsheetTextFormatter> {

    private final static BigDecimal BIG_DECIMAL = BigDecimal.valueOf(123);
    private final static String BIGDECIMAL_STRING = "123D00Text";
    private final static LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(2000, 12, 31, 12, 58, 59);
    private final static BigDecimal LOCAL_DATE_TIME_BIGDECIMAL = BigDecimal.valueOf(999);
    private final static String LOCAL_DATE_TIME_STRING = "999D00Text";

    @Test
    public void testFormatText() {
        final String text = "abc123";
        this.formatAndCheck(text, text);
    }

    @Test
    public void testFormatBigDecimal() {
        this.formatAndCheck(BIG_DECIMAL, BIGDECIMAL_STRING);
    }

    @Test
    public void testFormatLocalDateTime() {
        this.formatAndCheck(LOCAL_DATE_TIME, LOCAL_DATE_TIME_STRING);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createFormatter(), "General");
    }

    @Override
    public GeneralSpreadsheetTextFormatter createFormatter() {
        return GeneralSpreadsheetTextFormatter.INSTANCE;
    }

    @Override
    public Object value() {
        return BigDecimal.valueOf(1.5);
    }

    @Override
    public SpreadsheetTextFormatContext createContext() {
        return new FakeSpreadsheetTextFormatContext() {

            @Override
            public String currencySymbol() {
                return "C";
            }

            @Override
            public char decimalPoint() {
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
            public char minusSign() {
                return 'M';
            }

            @Override
            public char percentageSymbol() {
                return 'R';
            }

            @Override
            public char plusSign() {
                return 'P';
            }

            @Override
            public <T> T convert(final Object value, final Class<T> target) {
                if (BigDecimal.class == target) {
                    if (value instanceof BigDecimal) {
                        return target.cast(value);
                    }
                    if (value instanceof LocalDateTime) {
                        return target.cast(LOCAL_DATE_TIME_BIGDECIMAL);
                    }
                }
                throw new ConversionException("Failed to convert " + CharSequences.quoteIfChars(value) + " to " + target.getName());
            }

            @Override
            public Optional<SpreadsheetFormattedText> defaultFormatText(final Object value) {
                if (value instanceof String) {
                    return this.formattedText(value.toString());
                }
                if (BIG_DECIMAL.equals(value)) {
                    return this.formattedText(BIGDECIMAL_STRING);
                }
                if (LOCAL_DATE_TIME.equals(value)) {
                    return this.formattedText(LOCAL_DATE_TIME_STRING);
                }
                return this.formattedText(value.toString());
            }

            private Optional<SpreadsheetFormattedText> formattedText(final String text) {
                return Optional.of(SpreadsheetFormattedText.with(SpreadsheetFormattedText.WITHOUT_COLOR, text));
            }
        };
    }

    @Override
    public Class<GeneralSpreadsheetTextFormatter> type() {
        return GeneralSpreadsheetTextFormatter.class;
    }
}

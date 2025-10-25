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
import walkingkooka.Either;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.text.TextNode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public final class ContextFormatTextSpreadsheetFormatterTest implements SpreadsheetFormatterTesting2<ContextFormatValueTextSpreadsheetFormatter> {

    private final static BigDecimal BIG_DECIMAL = BigDecimal.valueOf(123);
    private final static String BIGDECIMAL_STRING = "123D00Text";
    private final static LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(2000, 12, 31, 12, 58, 59);
    private final static BigDecimal LOCAL_DATE_TIME_BIGDECIMAL = BigDecimal.valueOf(999);
    private final static String LOCAL_DATE_TIME_STRING = "999D00Text";

    @Test
    public void testFormatNull() {
        this.formatAndCheck(
            Optional.empty(),
            ""
        );
    }

    @Test
    public void testFormatText() {
        final String text = "abc123";
        this.formatAndCheck(
            text,
            text
        );
    }

    @Test
    public void testFormatBigDecimal() {
        this.formatAndCheck(
            BIG_DECIMAL,
            BIGDECIMAL_STRING
        );
    }

    @Test
    public void testFormatLocalDateTime() {
        this.formatAndCheck(
            LOCAL_DATE_TIME,
            LOCAL_DATE_TIME_STRING
        );
    }

    @Test
    public void testTokens() {
        this.tokensAndCheck();
    }

    @Override
    public ContextFormatValueTextSpreadsheetFormatter createFormatter() {
        return ContextFormatValueTextSpreadsheetFormatter.INSTANCE;
    }

    @Override
    public Object value() {
        return BigDecimal.valueOf(1.5);
    }

    @Override
    public SpreadsheetFormatterContext createContext() {
        return new FakeSpreadsheetFormatterContext() {

            @Override
            public String currencySymbol() {
                return "C";
            }

            @Override
            public char decimalSeparator() {
                return 'D';
            }

            @Override
            public String exponentSymbol() {
                return "X";
            }

            @Override
            public char groupSeparator() {
                return 'G';
            }

            @Override
            public char negativeSign() {
                return 'N';
            }

            @Override
            public char percentSymbol() {
                return 'R';
            }

            @Override
            public char positiveSign() {
                return 'P';
            }

            @Override
            public boolean canConvert(final Object value, final Class<?> target) {
                return (value instanceof BigDecimal && BigDecimal.class == target) ||
                    (value instanceof LocalDateTime && LocalDateTime.class == target);
            }

            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                if (BigDecimal.class == target) {
                    if (value instanceof BigDecimal) {
                        return this.successfulConversion(
                            target.cast(value),
                            target
                        );
                    }
                    if (value instanceof LocalDateTime) {
                        return this.successfulConversion(
                            target.cast(LOCAL_DATE_TIME_BIGDECIMAL),
                            target
                        );
                    }
                }
                return Either.right("Failed to convert " + CharSequences.quoteIfChars(value) + " to " + target.getName());
            }

            @Override
            public Optional<TextNode> formatValue(final Optional<Object> value) {
                final Object valueOrNull = value.orElse(null);

                if (valueOrNull instanceof String) {
                    return this.formattedText(valueOrNull.toString());
                }
                if (BIG_DECIMAL.equals(valueOrNull)) {
                    return this.formattedText(BIGDECIMAL_STRING);
                }
                if (LOCAL_DATE_TIME.equals(valueOrNull)) {
                    return this.formattedText(LOCAL_DATE_TIME_STRING);
                }
                return this.formattedText(
                    (String) valueOrNull
                );
            }

            private Optional<TextNode> formattedText(final String text) {
                return Optional.of(
                    SpreadsheetText.with(
                        null == text ?
                            "" :
                            text
                    ).textNode()
                );
            }
        };
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createFormatter(),
            "formatValue"
        );
    }

    // class............................................................................................................

    @Override
    public Class<ContextFormatValueTextSpreadsheetFormatter> type() {
        return ContextFormatValueTextSpreadsheetFormatter.class;
    }
}

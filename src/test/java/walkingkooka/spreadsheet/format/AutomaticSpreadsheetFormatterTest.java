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
import walkingkooka.ToStringTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.text.TextNode;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class AutomaticSpreadsheetFormatterTest implements SpreadsheetFormatterTesting2<AutomaticSpreadsheetFormatter>,
    ToStringTesting<AutomaticSpreadsheetFormatter> {

    private final static LocalDate DATE = LocalDate.of(1999, 12, 31);

    private final static Optional<TextNode> DATE_FORMATTED = Optional.of(
        TextNode.text("date-formatted " + DATE)
    );

    private final SpreadsheetFormatter DATE_FORMATTER = new FakeSpreadsheetFormatter() {
        @Override
        public Optional<TextNode> format(final Optional<Object> value,
                                         final SpreadsheetFormatterContext context) {
            checkEquals(
                DATE,
                value.orElse(null),
                "value"
            );
            return DATE_FORMATTED;
        }
    };

    private final static LocalDateTime DATE_TIME = LocalDateTime.of(1999, 12, 31, 12, 58, 59);

    private final static Optional<TextNode> DATE_TIME_FORMATTED = Optional.of(
        TextNode.text("date-time-formatted " + DATE_TIME)
    );

    private final SpreadsheetFormatter DATE_TIME_FORMATTER = new FakeSpreadsheetFormatter() {
        @Override
        public Optional<TextNode> format(final Optional<Object> value,
                                         final SpreadsheetFormatterContext context) {
            checkEquals(
                DATE_TIME,
                value.orElse(null),
                "value"
            );
            return DATE_TIME_FORMATTED;
        }
    };

    private final SpreadsheetFormatter NUMBER_FORMATTER = new FakeSpreadsheetFormatter() {
        @Override
        public Optional<TextNode> format(final Optional<Object> value,
                                         final SpreadsheetFormatterContext context) {
            return Optional.of(
                TextNode.text(
                    "number-formatted " +
                        value.orElse("")
                )
            );
        }
    };

    private final static String TEXT = "*text*";

    private final static Optional<TextNode> TEXT_FORMATTED = Optional.of(
        TextNode.text("string-formatted " + TEXT)
    );

    private final SpreadsheetFormatter TEXT_FORMATTER = new FakeSpreadsheetFormatter() {
        @Override
        public Optional<TextNode> format(final Optional<Object> value,
                                         final SpreadsheetFormatterContext context) {
            return TEXT_FORMATTED;
        }
    };

    private final static LocalTime TIME = LocalTime.of(12, 58, 59);

    private final static Optional<TextNode> TIME_FORMATTED = Optional.of(
        TextNode.text("time-formatted " + TIME)
    );

    private final SpreadsheetFormatter TIME_FORMATTER = new FakeSpreadsheetFormatter() {
        @Override
        public Optional<TextNode> format(final Optional<Object> value,
                                         final SpreadsheetFormatterContext context) {
            checkEquals(
                TIME,
                value.orElse(null),
                "value"
            );
            return TIME_FORMATTED;
        }
    };

    @Override
    public AutomaticSpreadsheetFormatter createFormatter() {
        return AutomaticSpreadsheetFormatter.with(
            DATE_FORMATTER,
            DATE_TIME_FORMATTER,
            NUMBER_FORMATTER,
            TEXT_FORMATTER,
            TIME_FORMATTER
        );
    }

    @Test
    public void testWithNullDateFormatterFails() {
        assertThrows(
            NullPointerException.class,
            () -> AutomaticSpreadsheetFormatter.with(
                null,
                DATE_TIME_FORMATTER,
                NUMBER_FORMATTER,
                TEXT_FORMATTER,
                TIME_FORMATTER
            )
        );
    }

    @Override
    public void testFormatWithNullContextFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testWithNullDateTimeFormatterFails() {
        assertThrows(
            NullPointerException.class,
            () -> AutomaticSpreadsheetFormatter.with(
                DATE_FORMATTER,
                null,
                NUMBER_FORMATTER,
                TEXT_FORMATTER,
                TIME_FORMATTER
            )
        );
    }

    @Test
    public void testWithNullNumberFormatterFails() {
        assertThrows(
            NullPointerException.class,
            () -> AutomaticSpreadsheetFormatter.with(
                DATE_FORMATTER,
                DATE_TIME_FORMATTER,
                null,
                TEXT_FORMATTER,
                TIME_FORMATTER
            )
        );
    }

    @Test
    public void testWithNullTextFormatterFails() {
        assertThrows(
            NullPointerException.class,
            () -> AutomaticSpreadsheetFormatter.with(
                DATE_FORMATTER,
                DATE_TIME_FORMATTER,
                NUMBER_FORMATTER,
                null,
                TIME_FORMATTER
            )
        );
    }

    @Test
    public void testWithNullTimeFormatterFails() {
        assertThrows(
            NullPointerException.class,
            () -> AutomaticSpreadsheetFormatter.with(
                DATE_FORMATTER,
                DATE_TIME_FORMATTER,
                NUMBER_FORMATTER,
                TEXT_FORMATTER,
                null
            )
        );
    }

    // format...........................................................................................................

    @Test
    public void testFormatByte() {
        this.formatNumberAndCheck(
            (byte) 1
        );
    }

    @Test
    public void testFormatShort() {
        this.formatNumberAndCheck(
            (short) 1
        );
    }

    @Test
    public void testFormatInteger() {
        this.formatNumberAndCheck(
            1
        );
    }

    @Test
    public void testFormatLong() {
        this.formatNumberAndCheck(
            1L
        );
    }

    @Test
    public void testFormatFloat() {
        this.formatNumberAndCheck(
            1.5f
        );
    }

    @Test
    public void testFormatDouble() {
        this.formatNumberAndCheck(
            1.5
        );
    }

    @Test
    public void testFormatBigInteger() {
        this.formatNumberAndCheck(
            BigInteger.valueOf(123)
        );
    }

    @Test
    public void testFormatBigDecimal() {
        this.formatNumberAndCheck(
            BigDecimal.valueOf(123.5)
        );
    }

    @Test
    public void testFormatExpressionNumber() {
        this.formatNumberAndCheck(
            ExpressionNumberKind.DEFAULT.create(123)
        );
    }

    private void formatNumberAndCheck(final Number number) {
        this.formatAndCheck(
            number,
            TextNode.text("number-formatted " + number)
        );
    }

    @Test
    public void testFormatNull() {
        this.formatAndCheck(
            Optional.empty(),
            TEXT_FORMATTED
        );
    }

    @Test
    public void testFormatBoolean() {
        this.formatTextAndCheck(
            true
        );
    }

    @Test
    public void testFormatCharacter() {
        this.formatTextAndCheck('C');
    }

    @Test
    public void testFormatString() {
        this.formatTextAndCheck("Text 123");
    }

    @Test
    public void testFormatCell() {
        this.formatTextAndCheck(SpreadsheetSelection.A1);
    }

    @Test
    public void testFormatCellRange() {
        this.formatTextAndCheck(
            SpreadsheetSelection.parseCellRange("A1:B2")
        );
    }

    @Test
    public void testFormatColumn() {
        this.formatTextAndCheck(
            SpreadsheetSelection.parseColumn("B")
        );
    }

    @Test
    public void testFormatColumnRange() {
        this.formatTextAndCheck(
            SpreadsheetSelection.parseColumnRange("B:C")
        );
    }

    @Test
    public void testFormatRow() {
        this.formatTextAndCheck(
            SpreadsheetSelection.parseRow("23")
        );
    }

    @Test
    public void testFormatRowRange() {
        this.formatTextAndCheck(
            SpreadsheetSelection.parseRowRange("23:45")
        );
    }

    private void formatTextAndCheck(final Object value) {
        this.formatAndCheck(
            value,
            TEXT_FORMATTED
        );
    }

    @Test
    public void testFormatDate() {
        this.formatAndCheck(
            DATE,
            DATE_FORMATTED
        );
    }

    @Test
    public void testFormatDateTime() {
        this.formatAndCheck(
            DATE_TIME,
            DATE_TIME_FORMATTED
        );
    }

    @Test
    public void testFormatTime() {
        this.formatAndCheck(
            TIME,
            TIME_FORMATTED
        );
    }

    @Override
    public Object value() {
        return "Hello";
    }

    @Override
    public SpreadsheetFormatterContext createContext() {
        return SpreadsheetFormatterContexts.fake();
    }

    // tokens...................................................................................................

    @Test
    public void testTokens() {
        this.tokensAndCheck();
    }

    // class............................................................................................................

    @Override
    public Class<AutomaticSpreadsheetFormatter> type() {
        return AutomaticSpreadsheetFormatter.class;
    }
}

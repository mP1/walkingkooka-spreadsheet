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

package walkingkooka.spreadsheet.meta;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.map.Maps;
import walkingkooka.color.Color;
import walkingkooka.convert.Converters;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.format.SpreadsheetTextFormatterPattern;
import walkingkooka.type.JavaVisibility;
import walkingkooka.visit.Visiting;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public final class SpreadsheetMetadataVisitorTest implements SpreadsheetMetadataVisitorTesting<SpreadsheetMetadataVisitor> {

    @Override
    public void testCheckToStringOverridden() {
    }

    @Override
    public void testClassVisibility() {
    }

    @Override
    public void testStartVisitMethodsSingleParameter() {
    }

    @Override
    public void testEndVisitMethodsSingleParameter() {
    }

    @Test
    public void testVisitSpreadsheetMetadataSkip() {
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY;

        new FakeSpreadsheetMetadataVisitor() {
            @Override
            protected Visiting startVisit(final SpreadsheetMetadata t) {
                assertSame(metadata, t);
                return Visiting.SKIP;
            }

            @Override
            protected void endVisit(final SpreadsheetMetadata t) {
                assertSame(metadata, t);
            }
        }.accept(metadata);
    }

    @Test
    public void testVisitSpreadsheetMetadataPropertyNameSkip() {
        final SpreadsheetMetadataPropertyName<EmailAddress> propertyName = SpreadsheetMetadataPropertyName.CREATOR;
        final EmailAddress value = this.emailAddress();
        final SpreadsheetMetadata metadata = metadata(propertyName, value);

        new FakeSpreadsheetMetadataVisitor() {
            @Override
            protected Visiting startVisit(final SpreadsheetMetadata t) {
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final SpreadsheetMetadata t) {
            }

            @Override
            protected Visiting startVisit(final SpreadsheetMetadataPropertyName<?> p, final Object v) {
                assertSame(propertyName, p, "propertyName");
                assertSame(value, v, "value");
                return Visiting.SKIP;
            }

            @Override
            protected void endVisit(final SpreadsheetMetadataPropertyName<?> p, final Object v) {
                assertSame(propertyName, p, "propertyName");
                assertSame(value, v, "value");
            }
        }.accept(metadata);
    }

    @Test
    public void testVisitCreator() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitCreator(final EmailAddress e) {
                this.visited = e;
            }
        }.accept(SpreadsheetMetadataPropertyName.CREATOR, this.emailAddress());
    }

    @Test
    public void testVisitCreateDateTime() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitCreateDateTime(final LocalDateTime d) {
                this.visited = d;
            }
        }.accept(SpreadsheetMetadataPropertyName.CREATE_DATE_TIME, this.dateTime());
    }

    @Test
    public void testVisitCurrencySymbol() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitCurrencySymbol(final String c) {
                this.visited = c;
            }
        }.accept(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "$$");
    }

    @Test
    public void testVisitDateFormatPattern() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitDateFormatPattern(final SpreadsheetTextFormatterPattern p) {
                this.visited = p;
            }
        }.accept(SpreadsheetMetadataPropertyName.DATE_FORMAT_PATTERN, SpreadsheetTextFormatterPattern.parse("DD/MM/YYYY"));
    }

    @Test
    public void testVisitDateTimeFormatPattern() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitDateTimeFormatPattern(final SpreadsheetTextFormatterPattern p) {
                this.visited = p;
            }
        }.accept(SpreadsheetMetadataPropertyName.DATETIME_FORMAT_PATTERN, SpreadsheetTextFormatterPattern.parse("DD/MM/YYYY hh:mm"));
    }

    @Test
    public void testVisitDateTimeOffset() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitDateTimeOffset(final Long offset) {
                this.visited = offset;
            }
        }.accept(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.EXCEL_OFFSET);
    }

    @Test
    public void testVisitDecimalPoint() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitDecimalPoint(final Character c) {
                this.visited = c;
            }
        }.accept(SpreadsheetMetadataPropertyName.DECIMAL_POINT, '.');
    }

    @Test
    public void testVisitExponentSymbol() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitExponentSymbol(final Character c) {
                this.visited = c;
            }
        }.accept(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, '.');
    }

    @Test
    public void testVisitGroupingSeparator() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitGroupingSeparator(final Character c) {
                this.visited = c;
            }
        }.accept(SpreadsheetMetadataPropertyName.GROUPING_SEPARATOR, ',');
    }

    @Test
    public void testVisitLocale() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitLocale(final Locale l) {
                this.visited = l;
            }
        }.accept(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH);
    }

    @Test
    public void testVisitMinusSign() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitMinusSign(final Character c) {
                this.visited = c;
            }
        }.accept(SpreadsheetMetadataPropertyName.MINUS_SIGN, '-');
    }

    @Test
    public void testVisitModifiedBy() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitModifiedBy(final EmailAddress e) {
                this.visited = e;
            }
        }.accept(SpreadsheetMetadataPropertyName.MODIFIED_BY, this.emailAddress());
    }

    @Test
    public void testVisitModifiedDateTime() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitModifiedDateTime(final LocalDateTime d) {
                this.visited = d;
            }
        }.accept(SpreadsheetMetadataPropertyName.MODIFIED_DATE_TIME, this.dateTime());
    }

    @Test
    public void testVisitNumberedColor() {
        final int number = 7;

        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitNumberedColor(final int n, final Color c) {
                assertEquals(number, n, "number");
                this.visited = c;
            }
        }.accept(SpreadsheetMetadataPropertyName.color(number), this.color());
    }

    @Test
    public void testVisitNumberFormatPattern() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitNumberFormatPattern(final SpreadsheetTextFormatterPattern p) {
                this.visited = p;
            }
        }.accept(SpreadsheetMetadataPropertyName.NUMBER_FORMAT_PATTERN, SpreadsheetTextFormatterPattern.parse("#0.0"));
    }

    @Test
    public void testVisitPercentageSymbol() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitPercentageSymbol(final Character c) {
                this.visited = c;
            }
        }.accept(SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL, '.');
    }

    @Test
    public void testVisitPlusSign() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitPlusSign(final Character c) {
                this.visited = c;
            }
        }.accept(SpreadsheetMetadataPropertyName.PLUS_SIGN, '+');
    }

    @Test
    public void testVisitPrecision() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitPrecision(final Integer i) {
                this.visited = i;
            }
        }.accept(SpreadsheetMetadataPropertyName.PRECISION, 123);
    }

    @Test
    public void testVisitRoundingMode() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitRoundingMode(final RoundingMode r) {
                this.visited = r;
            }
        }.accept(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.HALF_UP);
    }

    @Test
    public void testVisitSpreadsheetId() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitSpreadsheetId(final SpreadsheetId i) {
                this.visited = i;
            }
        }.accept(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(123));
    }

    @Test
    public void testVisitTimeFormatPattern() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitTimeFormatPattern(final SpreadsheetTextFormatterPattern p) {
                this.visited = p;
            }
        }.accept(SpreadsheetMetadataPropertyName.TIME_FORMAT_PATTERN, SpreadsheetTextFormatterPattern.parse("hh:mm"));
    }

    @Test
    public void testVisitTwoDigitYearInterpretation() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitTwoDigitYearInterpretation(final Integer i) {
                this.visited = i;
            }
        }.accept(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR_INTERPRETATION, 32);
    }

    private static <T> SpreadsheetMetadata metadata(final SpreadsheetMetadataPropertyName<T> propertyName, final T value) {
        return SpreadsheetMetadata.with(Maps.of(propertyName, value));
    }

    @Override
    public SpreadsheetMetadataVisitor createVisitor() {
        return new TestSpreadsheetMetadataVisitor();
    }

    static class TestSpreadsheetMetadataVisitor extends FakeSpreadsheetMetadataVisitor {

        <T> void accept(final SpreadsheetMetadataPropertyName<T> propertyName, final T value) {
            this.expected = value;

            final SpreadsheetMetadata metadata = metadata(propertyName, value);
            this.accept(metadata);
            assertEquals(this.expected, this.visited);

            new SpreadsheetMetadataVisitor() {
            }.accept(metadata);
        }

        private Object expected;
        Object visited;

        @Override
        protected Visiting startVisit(final SpreadsheetMetadata metadata) {
            return Visiting.CONTINUE;
        }

        @Override
        protected void endVisit(final SpreadsheetMetadata metadata) {
        }

        @Override
        protected Visiting startVisit(final SpreadsheetMetadataPropertyName<?> property, final Object value) {
            return Visiting.CONTINUE;
        }

        @Override
        protected void endVisit(final SpreadsheetMetadataPropertyName<?> property, final Object value) {
        }
    }

    private Color color() {
        return Color.parse("#123abc");
    }

    private LocalDateTime dateTime() {
        return LocalDateTime.of(2000, 1, 31, 12, 58, 59);
    }

    private EmailAddress emailAddress() {
        return EmailAddress.parse("user@example.com");
    }

    @Override
    public Class<SpreadsheetMetadataVisitor> type() {
        return SpreadsheetMetadataVisitor.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public String typeNamePrefix() {
        return "";
    }
}

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
import walkingkooka.collect.set.Sets;
import walkingkooka.color.Color;
import walkingkooka.convert.Converters;
import walkingkooka.convert.provider.ConverterInfoSet;
import walkingkooka.convert.provider.ConverterProviders;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateTimeParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.importer.SpreadsheetImporterInfoSet;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetViewport;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.visit.Visiting;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Locale;

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
    public void testVisitCellCharacterWidth() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitCellCharacterWidth(final int i) {
                this.visited = i;
            }
        }.accept(SpreadsheetMetadataPropertyName.CELL_CHARACTER_WIDTH, 0);
    }

    @Test
    public void testVisitConverters() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitConverters(final ConverterInfoSet c) {
                this.visited = c;
            }
        }.accept(
                SpreadsheetMetadataPropertyName.CONVERTERS,
                ConverterInfoSet.with(
                        ConverterProviders.converters()
                                .converterInfos()
                )
        );
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
    public void testVisitDateFormatter() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitDateFormatter(final SpreadsheetFormatterSelector s) {
                this.visited = s;
            }
        }.accept(
                SpreadsheetMetadataPropertyName.DATE_FORMATTER,
                SpreadsheetPattern.parseDateFormatPattern("DD/MM/YYYY")
                        .spreadsheetFormatterSelector()
        );
    }

    @Test
    public void testVisitDateParser() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitDateParser(final SpreadsheetParserSelector p) {
                this.visited = p;
            }
        }.accept(
                SpreadsheetMetadataPropertyName.DATE_PARSER,
                SpreadsheetDateTimeParsePattern.parseDateParsePattern("DD/MM/YYYY")
                        .spreadsheetParserSelector()
        );
    }

    @Test
    public void testVisitDateTimeFormatter() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitDateTimeFormatter(final SpreadsheetFormatterSelector s) {
                this.visited = s;
            }
        }.accept(
                SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER,
                SpreadsheetPattern.parseDateTimeFormatPattern("DD/MM/YYYY hh:mm").spreadsheetFormatterSelector()
        );
    }

    @Test
    public void testVisitDateTimeOffset() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitDateTimeOffset(final long offset) {
                this.visited = offset;
            }
        }.accept(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, Converters.EXCEL_1900_DATE_SYSTEM_OFFSET);
    }

    @Test
    public void testVisitDateTimeParser() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitDateTimeParser(final SpreadsheetParserSelector s) {
                this.visited = s;
            }
        }.accept(
                SpreadsheetMetadataPropertyName.DATE_TIME_PARSER,
                SpreadsheetDateTimeParsePattern.parseDateTimeParsePattern("DD/MM/YYYY HH:MM:SS;DDMMYYYY HHMMSS")
                        .spreadsheetParserSelector()
        );
    }

    @Test
    public void testVisitDecimalSeparator() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitDecimalSeparator(final char c) {
                this.visited = c;
            }
        }.accept(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, '.');
    }

    @Test
    public void testVisitDefaultYear() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitDefaultYear(final int i) {
                this.visited = i;
            }
        }.accept(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, 1901);
    }

    @Test
    public void testVisitExponentSymbol() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitExponentSymbol(final String s) {
                this.visited = s;
            }
        }.accept(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, ".");
    }

    @Test
    public void testVisitExpressionConverter() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitExpressionConverter(final ConverterSelector s) {
                this.visited = s;
            }
        }.accept(
                SpreadsheetMetadataPropertyName.EXPRESSION_CONVERTER,
                ConverterSelector.parse("general")
        );
    }

    @Test
    public void testVisitFormatConverter() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitFormatConverter(final ConverterSelector s) {
                this.visited = s;
            }
        }.accept(
                SpreadsheetMetadataPropertyName.FORMAT_CONVERTER,
                ConverterSelector.parse("general")
        );
    }

    @Test
    public void testVisitFrozenColumns() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitFrozenColumns(final SpreadsheetColumnRangeReference r) {
                this.visited = r;
            }
        }.accept(
                SpreadsheetMetadataPropertyName.FROZEN_COLUMNS,
                SpreadsheetSelection.parseColumnRange("A:B")
        );
    }

    @Test
    public void testVisitFrozenRows() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitFrozenRows(final SpreadsheetRowRangeReference r) {
                this.visited = r;
            }
        }.accept(
                SpreadsheetMetadataPropertyName.FROZEN_ROWS,
                SpreadsheetSelection.parseRowRange("1:2")
        );
    }

    @Test
    public void testVisitGeneralNumberFormatDigitCount() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitGeneralNumberFormatDigitCount(final int i) {
                this.visited = i;
            }
        }.accept(SpreadsheetMetadataPropertyName.GENERAL_NUMBER_FORMAT_DIGIT_COUNT, 123);
    }

    @Test
    public void testVisitGroupSeparator() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitGroupSeparator(final char c) {
                this.visited = c;
            }
        }.accept(SpreadsheetMetadataPropertyName.GROUP_SEPARATOR, ',');
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
    public void testVisitNegativeSign() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitNegativeSign(final char c) {
                this.visited = c;
            }
        }.accept(SpreadsheetMetadataPropertyName.NEGATIVE_SIGN, '-');
    }

    @Test
    public void testVisitNamedColor() {
        final SpreadsheetColorName name = SpreadsheetColorName.with("shiny");
        final int colorNumber = 23;

        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitNamedColor(final SpreadsheetColorName n,
                                           final int c) {
                checkEquals(name, n, "name");
                this.visited = c;
            }
        }.accept(
                SpreadsheetMetadataPropertyName.namedColor(name),
                colorNumber
        );
    }

    @Test
    public void testVisitNumberedColor() {
        final int number = 7;

        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitNumberedColor(final int n, final Color c) {
                checkEquals(number, n, "number");
                this.visited = c;
            }
        }.accept(SpreadsheetMetadataPropertyName.numberedColor(number), this.color());
    }

    @Test
    public void testVisitNumberFormatter() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitNumberFormatter(final SpreadsheetFormatterSelector s) {
                this.visited = s;
            }
        }.accept(
                SpreadsheetMetadataPropertyName.NUMBER_FORMATTER,
                SpreadsheetPattern.parseNumberFormatPattern("#0.0").spreadsheetFormatterSelector()
        );
    }

    @Test
    public void testVisitNumberParser() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitNumberParser(final SpreadsheetParserSelector s) {
                this.visited = s;
            }
        }.accept(
                SpreadsheetMetadataPropertyName.NUMBER_PARSER,
                SpreadsheetPattern.parseNumberParsePattern("#0.0;#0.00")
                        .spreadsheetParserSelector()
        );
    }

    @Test
    public void testVisitPercentageSymbol() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitPercentageSymbol(final char c) {
                this.visited = c;
            }
        }.accept(SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL, '.');
    }

    @Test
    public void testVisitPositiveSign() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitPositiveSign(final char c) {
                this.visited = c;
            }
        }.accept(SpreadsheetMetadataPropertyName.POSITIVE_SIGN, '+');
    }

    @Test
    public void testVisitPrecision() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitPrecision(final int i) {
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
    public void testVisitSpreadsheetImporters() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitSpreadsheetImporters(final SpreadsheetImporterInfoSet s) {
                this.visited = s;
            }
        }.accept(
                SpreadsheetMetadataPropertyName.SPREADSHEET_IMPORTERS,
                SpreadsheetImporterInfoSet.with(
                        Sets.empty()
                )
        );
    }

    @Test
    public void testVisitStyle() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitStyle(final TextStyle style) {
                this.visited = style;
            }
        }.accept(SpreadsheetMetadataPropertyName.STYLE, SpreadsheetMetadata.NON_LOCALE_DEFAULTS.getOrFail(SpreadsheetMetadataPropertyName.STYLE));
    }

    @Test
    public void testVisitTextFormatter() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitTextFormatter(final SpreadsheetFormatterSelector s) {
                this.visited = s;
            }
        }.accept(
                SpreadsheetMetadataPropertyName.TEXT_FORMATTER,
                SpreadsheetPattern.parseTextFormatPattern("@").spreadsheetFormatterSelector()
        );
    }

    @Test
    public void testVisitTimeFormatter() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitTimeFormatter(final SpreadsheetFormatterSelector s) {
                this.visited = s;
            }
        }.accept(
                SpreadsheetMetadataPropertyName.TIME_FORMATTER,
                SpreadsheetPattern.parseTimeFormatPattern("hh:mm").spreadsheetFormatterSelector()
        );
    }

    @Test
    public void testVisitTimeParser() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitTimeParser(final SpreadsheetParserSelector s) {
                this.visited = s;
            }
        }.accept(SpreadsheetMetadataPropertyName.TIME_PARSER, SpreadsheetPattern.parseTimeParsePattern("hh:mm;hh:mm:ss;hh:mm:ss.000").spreadsheetParserSelector());
    }

    @Test
    public void testVisitTwoDigitYear() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitTwoDigitYear(final int i) {
                this.visited = i;
            }
        }.accept(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR, 32);
    }

    @Test
    public void testVisitValueSeparator() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitValueSeparator(final char c) {
                this.visited = c;
            }
        }.accept(SpreadsheetMetadataPropertyName.VALUE_SEPARATOR, '.');
    }

    @Test
    public void testVisitViewport() {
        new TestSpreadsheetMetadataVisitor() {
            @Override
            protected void visitViewport(final SpreadsheetViewport selection) {
                this.visited = selection;
            }
        }.accept(
                SpreadsheetMetadataPropertyName.VIEWPORT,
                SpreadsheetSelection.parseCell("A2")
                        .viewportRectangle(100, 50)
                        .viewport()
        );
    }

    private static <T> SpreadsheetMetadata metadata(final SpreadsheetMetadataPropertyName<T> propertyName, final T value) {
        return SpreadsheetMetadata.EMPTY.set(propertyName, value);
    }

    @Override
    public SpreadsheetMetadataVisitor createVisitor() {
        return new TestSpreadsheetMetadataVisitor();
    }

    class TestSpreadsheetMetadataVisitor extends FakeSpreadsheetMetadataVisitor {

        <T> void accept(final SpreadsheetMetadataPropertyName<T> propertyName, final T value) {

            final SpreadsheetMetadata metadata = metadata(propertyName, value);
            this.accept(metadata);
            checkEquals(value, this.visited);

            new SpreadsheetMetadataVisitor() {
            }.accept(metadata);
        }

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

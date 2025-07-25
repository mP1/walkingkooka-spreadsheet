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

package walkingkooka.spreadsheet.convert;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.color.Color;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.net.Url;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.parser.DateTimeSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.NumberSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.format.parser.TextSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.SequenceParserToken;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContexts;
import walkingkooka.tree.text.TextAlign;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetConverterGeneralTest extends SpreadsheetConverterTestCase<SpreadsheetConverterGeneral> {

    private final static String TEXT_SUFFIX = "text-literal-123";

    private final static SpreadsheetFormatter DATE_FORMATTER = dateTimeFormatter(
        "\\D yyyy-mm-dd",
        LocalDate.class
    );

    private final static Parser<SpreadsheetParserContext> DATE_PARSER = SpreadsheetPattern.parseDateParsePattern("\\D yyyy-mm-dd")
        .parser();

    private final static SpreadsheetFormatter DATE_TIME_FORMATTER = dateTimeFormatter(
        "\"DT\" yyyy-mm-dd hh-mm",
        LocalDateTime.class
    );

    private final static Parser<SpreadsheetParserContext> DATE_TIME_PARSER = SpreadsheetPattern.parseDateTimeParsePattern("\"DT\" dd mm yyyy hh mm ss")
        .parser();

    private final static SpreadsheetFormatter NUMBER_FORMATTER = formatter(
        "\\N #.#",
        SpreadsheetFormatParsers.numberParse(),
        NumberSpreadsheetFormatParserToken.class,
        SpreadsheetFormatters::number
    );

    private final static Parser<SpreadsheetParserContext> NUMBER_PARSER = SpreadsheetPattern.parseNumberParsePattern("\"N\" #;\"N\" #.#")
        .parser();

    private final static SpreadsheetFormatter TEXT_FORMATTER = formatter(
        "@\"" + TEXT_SUFFIX + "\"",
        SpreadsheetFormatParsers.textFormat(),
        TextSpreadsheetFormatParserToken.class,
        SpreadsheetFormatters::text
    );

    private final static SpreadsheetFormatter TIME_FORMATTER = dateTimeFormatter(
        "\\T hh-mm",
        LocalTime.class
    );

    private final static Parser<SpreadsheetParserContext> TIME_PARSER = SpreadsheetPattern.parseTimeParsePattern("\\T hh mm ss")
        .parser();

    private static SpreadsheetFormatter dateTimeFormatter(final String pattern,
                                                          final Class<? extends Temporal> type) {
        return formatter(
            pattern,
            SpreadsheetFormatParsers.dateTimeFormat(),
            DateTimeSpreadsheetFormatParserToken.class,
            (t) -> SpreadsheetFormatters.dateTime(
                t,
                type
            )
        );
    }

    private static <T extends SpreadsheetFormatParserToken> SpreadsheetFormatter formatter(final String pattern,
                                                                                           final Parser<SpreadsheetFormatParserContext> parser,
                                                                                           final Class<T> token,
                                                                                           final Function<T, SpreadsheetFormatter> formatterFactory) {
        return parser.orFailIfCursorNotEmpty(ParserReporters.basic())
            .parse(
                TextCursors.charSequence(pattern),
                SpreadsheetFormatParserContexts.basic(InvalidCharacterExceptionFactory.POSITION)
            ).map(t -> t instanceof SequenceParserToken ?
                t.cast(SequenceParserToken.class).value().get(0) :
                t
            ).map(t -> t.cast(token))
            .map(formatterFactory)
            .orElse(SpreadsheetFormatters.fake()); // orElse wont happen.
    }

    // with.............................................................................................................

    @Test
    public void testWithNullDateFormatterFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetConverterGeneral.with(
                null,
                DATE_PARSER,
                DATE_TIME_FORMATTER,
                DATE_TIME_PARSER,
                NUMBER_FORMATTER,
                NUMBER_PARSER,
                TEXT_FORMATTER,
                TIME_FORMATTER,
                TIME_PARSER
            )
        );
    }

    @Test
    public void testWithNullDateParserFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetConverterGeneral.with(
                DATE_FORMATTER,
                null,
                DATE_TIME_FORMATTER,
                DATE_TIME_PARSER,
                NUMBER_FORMATTER,
                NUMBER_PARSER,
                TEXT_FORMATTER,
                TIME_FORMATTER,
                TIME_PARSER
            )
        );
    }

    @Test
    public void testWithNullDateTimeFormatterFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetConverterGeneral.with(
                DATE_FORMATTER,
                DATE_PARSER,
                null,
                DATE_TIME_PARSER,
                NUMBER_FORMATTER,
                NUMBER_PARSER,
                TEXT_FORMATTER,
                TIME_FORMATTER,
                TIME_PARSER
            )
        );
    }

    @Test
    public void testWithNullDateTimeParserFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetConverterGeneral.with(
                DATE_FORMATTER,
                DATE_PARSER,
                DATE_TIME_FORMATTER,
                null,
                NUMBER_FORMATTER,
                NUMBER_PARSER,
                TEXT_FORMATTER,
                TIME_FORMATTER,
                TIME_PARSER
            )
        );
    }

    @Test
    public void testWithNullNumberFormatterFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetConverterGeneral.with(
                DATE_FORMATTER,
                DATE_PARSER,
                DATE_TIME_FORMATTER,
                DATE_TIME_PARSER,
                null,
                NUMBER_PARSER,
                TEXT_FORMATTER,
                TIME_FORMATTER,
                TIME_PARSER
            )
        );
    }

    @Test
    public void testWithNullNumberParserFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetConverterGeneral.with(
                DATE_FORMATTER,
                DATE_PARSER,
                DATE_TIME_FORMATTER,
                DATE_TIME_PARSER,
                NUMBER_FORMATTER,
                null,
                TEXT_FORMATTER,
                TIME_FORMATTER,
                TIME_PARSER
            )
        );
    }

    @Test
    public void testWithNullTextFormatterFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetConverterGeneral.with(
                DATE_FORMATTER,
                DATE_PARSER,
                DATE_TIME_FORMATTER,
                DATE_TIME_PARSER,
                NUMBER_FORMATTER,
                NUMBER_PARSER,
                null,
                TIME_FORMATTER,
                TIME_PARSER
            )
        );
    }

    @Test
    public void testWithNullTimeFormatterFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetConverterGeneral.with(
                DATE_FORMATTER,
                DATE_PARSER,
                DATE_TIME_FORMATTER,
                DATE_TIME_PARSER,
                NUMBER_FORMATTER,
                NUMBER_PARSER,
                TEXT_FORMATTER,
                null,
                TIME_PARSER
            )
        );
    }

    @Test
    public void testWithNullTimeParserFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetConverterGeneral.with(
                DATE_FORMATTER,
                DATE_PARSER,
                DATE_TIME_FORMATTER,
                DATE_TIME_PARSER,
                NUMBER_FORMATTER,
                NUMBER_PARSER,
                TEXT_FORMATTER,
                TIME_FORMATTER,
                null
            )
        );
    }

    // convert..........................................................................................................

    private final static long DATE_OFFSET = Converters.JAVA_EPOCH_OFFSET;

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;

    private final static Integer NUMBER_TRUE = 1;
    private final static Integer NUMBER_FALSE = 0;

    private final static LocalDate DATE = LocalDate.of(2000, 12, 31);
    private final static LocalDate DATE_TRUE = LocalDate.ofEpochDay(NUMBER_TRUE + DATE_OFFSET);
    private final static LocalDate DATE_FALSE = LocalDate.ofEpochDay(NUMBER_FALSE + DATE_OFFSET);

    private final static LocalTime TIME = LocalTime.of(12, 58, 59);
    private final static LocalTime TIME_TRUE = LocalTime.ofSecondOfDay(NUMBER_TRUE);
    private final static LocalTime TIME_FALSE = LocalTime.ofSecondOfDay(NUMBER_FALSE);

    private final static LocalDateTime DATE_TIME_TRUE = LocalDateTime.of(DATE_TRUE, LocalTime.MIDNIGHT);
    private final static LocalDateTime DATE_TIME_FALSE = LocalDateTime.of(DATE_FALSE, LocalTime.MIDNIGHT);
    private final static LocalDateTime DATE_TIME = LocalDateTime.of(DATE, TIME);

    private final static String STRING_FALSE = "false";
    private final static String STRING_TRUE = "true";

    // Object..........................................................................................................

    @Test
    public void testConvertWithNullToObject() {
        this.convertAndCheck(
            null,
            Object.class
        );
    }

    @Test
    public void testConvertWithBooleanTrueToObject() {
        this.convertAndCheck(
            true,
            Object.class
        );
    }

    @Test
    public void testConvertWithExpressionNumberToObject() {
        this.convertAndCheck(
            EXPRESSION_NUMBER_KIND.create(123),
            Object.class
        );
    }

    @Test
    public void testConvertWithIntegerTOObject() {
        this.convertAndCheck(
            1,
            Object.class
        );
    }

    @Test
    public void testConvertWithStringToObject() {
        this.convertAndCheck(
            "abc123",
            Object.class
        );
    }

    // SpreadsheetCellReference.........................................................................................

    @Test
    public void testConvertWithSpreadsheetCellReferenceToStringFails() {
        this.convertFails(
            SpreadsheetSelection.A1,
            String.class
        );
    }

    // SpreadsheetCellRangeReference.............................................................................................

    @Test
    public void testConvertWithSpreadsheetCellRangeToStringFails() {
        this.convertFails(
            SpreadsheetSelection.parseCellRange("A1:B2"),
            String.class
        );
    }

    // SpreadsheetColumnReference.......................................................................................

    @Test
    public void testConvertWithSpreadsheetColumnReferenceToStringFails() {
        this.convertFails(
            SpreadsheetSelection.parseColumn("C"),
            String.class
        );
    }

    // SpreadsheetColumnRangeReference..................................................................................

    @Test
    public void testConvertWithSpreadsheetColumnRangeReferenceToStringFails() {
        this.convertFails(
            SpreadsheetSelection.parseColumnRange("D:E"),
            String.class
        );
    }

    // SpreadsheetLabelName............................................................................................

    @Test
    public void testConvertWithSpreadsheetLabelNameToStringFails() {
        this.convertFails(
            SpreadsheetSelection.labelName("Label123"),
            String.class
        );
    }

    // SpreadsheetRowReference.......................................................................................

    @Test
    public void testConvertWithSpreadsheetRowReferenceToStringFails() {
        this.convertFails(
            SpreadsheetSelection.parseRow("5"),
            String.class
        );
    }

    // SpreadsheetRowRangeReference..................................................................................

    @Test
    public void testConvertWithSpreadsheetRowRangeReferenceToStringFails() {
        this.convertFails(
            SpreadsheetSelection.parseRowRange("6:7"),
            String.class
        );
    }

    // error ...........................................................................................................

    @Test
    public void testConvertWithSpreadsheetErrorToExpressionNumberFails() {
        this.convertFails(
            SpreadsheetErrorKind.ERROR.setMessage("Ignored"),
            ExpressionNumber.class
        );
    }

    @Test
    public void testConvertWithSpreadsheetErrorToStringFails() {
        this.convertFails(
            SpreadsheetErrorKind.DIV0.setMessage("Ignored"),
            String.class
        );
    }

    // Expression.......................................................................................................

    @Test
    public void testConvertWithExpressionValueToExpression() {
        this.convertAndCheck(
            Expression.value(123),
            Expression.class
        );
    }

    // boolean..........................................................................................................

    @Test
    public void testConvertWithBooleanTrueToBoolean() {
        this.convertAndCheck(true);
    }

    @Test
    public void testConvertWithBooleanFalseToBoolean() {
        this.convertAndCheck(false);
    }

    @Test
    public void testConvertWithBooleanTrueToDate() {
        this.convertAndBackCheck(
            true,
            DATE_TRUE
        );
    }

    @Test
    public void testConvertWithBooleanFalseToDate() {
        this.convertAndBackCheck(
            false,
            DATE_FALSE
        );
    }

    @Test
    public void testConvertWithBooleanTrueToDateTime() {
        this.convertAndBackCheck(
            true,
            DATE_TIME_TRUE
        );
    }

    @Test
    public void testConvertWithBooleanFalseToDateTime() {
        this.convertAndBackCheck(
            false,
            DATE_TIME_FALSE
        );
    }

    @Test
    public void testConvertWithBooleanTrueToByte() {
        this.convertAndCheck(
            true,
            (byte) 1
        );
    }

    @Test
    public void testConvertWithBooleanFalseToByte() {
        this.convertAndCheck(
            false,
            (byte) 0
        );
    }

    @Test
    public void testConvertWithBooleanTrueToShort() {
        this.convertAndCheck(
            true,
            (short) 1
        );
    }

    @Test
    public void testConvertWithBooleanFalseToShort() {
        this.convertAndCheck(
            false,
            (short) 0
        );
    }

    @Test
    public void testConvertWithBooleanTrueToInteger() {
        this.convertAndCheck(
            true,
            1
        );
    }

    @Test
    public void testConvertWithBooleanFalseToInteger() {
        this.convertAndCheck(
            false,
            0
        );
    }

    @Test
    public void testConvertWithBooleanTrueToLong() {
        this.convertAndCheck(
            true,
            1L
        );
    }

    @Test
    public void testConvertWithBooleanFalseToLong() {
        this.convertAndCheck(
            false,
            0L
        );
    }

    @Test
    public void testConvertWithBooleanTrueToFloat() {
        this.convertAndCheck(
            true,
            1f
        );
    }

    @Test
    public void testConvertWithBooleanFalseToFloat() {
        this.convertAndCheck(
            false,
            0f
        );
    }

    @Test
    public void testConvertWithBooleanTrueToDouble() {
        this.convertAndCheck(
            true,
            1.0
        );
    }

    @Test
    public void testConvertWithBooleanFalseToDouble() {
        this.convertAndCheck(
            false,
            0.0
        );
    }

    @Test
    public void testConvertWithBooleanTrueToExpressionNumber() {
        this.convertAndCheck(
            true,
            EXPRESSION_NUMBER_KIND.one()
        );
    }

    @Test
    public void testConvertWithBooleanFalseToExpressionNumber() {
        this.convertAndCheck(
            false,
            EXPRESSION_NUMBER_KIND.zero()
        );
    }

    @Test
    public void testConvertWithBooleanTrueToBigInteger() {
        this.convertAndCheck(
            true,
            BigInteger.ONE
        );
    }

    @Test
    public void testConvertWithBooleanFalseToBigInteger() {
        this.convertAndCheck(
            false,
            BigInteger.ZERO
        );
    }

    @Test
    public void testConvertWithBooleanTrueToBigDecimal() {
        this.convertAndCheck(
            true,
            BigDecimal.ONE
        );
    }

    @Test
    public void testConvertWithBooleanFalseToBigDecimal() {
        this.convertAndCheck(
            false,
            BigDecimal.ZERO
        );
    }

    @Test
    public void testConvertWithBooleanTrueToString() {
        this.convertAndCheck(
            true,
            STRING_TRUE + TEXT_SUFFIX
        );
    }

    @Test
    public void testConvertWithBooleanFalseToString() {
        this.convertAndCheck(
            false,
            false + TEXT_SUFFIX
        );
    }

    @Test
    public void testConvertWithBooleanTrueToTime() {
        this.convertAndBackCheck(
            true,
            TIME_TRUE
        );
    }

    @Test
    public void testConvertWithBooleanFalseToTime() {
        this.convertAndBackCheck(
            false,
            TIME_FALSE
        );
    }

    // Character........................................................................................................

    @Test
    public void testConvertWithCharacterToString() {
        this.convertAndCheck(
            'A',
            String.class,
            "A"
        );
    }

    // Date.............................................................................................................

    @Test
    public void testConvertWithNullToBoolean() {
        this.convertAndCheck(
            null,
            Boolean.class
        );
    }

    @Test
    public void testConvertWithNullToDate() {
        this.convertAndCheck(
            null,
            LocalDate.class
        );
    }

    @Test
    public void testConvertWithNullToDateTime() {
        this.convertAndCheck(
            null,
            LocalDateTime.class
        );
    }

    @Test
    public void testConvertWithNullToTime() {
        this.convertAndCheck(
            null,
            LocalTime.class
        );
    }

    @Test
    public void testConvertWithNullToExpressionNumber() {
        this.convertAndCheck(
            null,
            ExpressionNumber.class,
            EXPRESSION_NUMBER_KIND.zero()
        );
    }

    @Test
    public void testConvertWithNullToNumber() {
        this.convertAndCheck(
            null,
            Number.class,
            EXPRESSION_NUMBER_KIND.zero()
        );
    }

    @Test
    public void testConvertWithNullToString() {
        this.convertAndCheck(
            null,
            String.class
        );
    }

    // date............................................................................................................

    @Test
    public void testConvertWithDateTrueToBoolean() {
        this.convertAndCheck(
            DATE_TRUE,
            true
        );
    }

    @Test
    public void testConvertWithDateFalseToBoolean() {
        this.convertAndCheck(
            DATE_FALSE,
            false
        );
    }

    @Test
    public void testConvertWithDateToDate() {
        this.convertAndCheck(
            LocalDate.of(2000, 12, 31)
        );
    }

    @Test
    public void testConvertWithDateTrueToDateTime() {
        this.convertAndBackCheck(
            DATE_TRUE,
            LocalDateTime.of(DATE_TRUE, LocalTime.MIDNIGHT)
        );
    }

    @Test
    public void testConvertWithDateFalseToDateTime() {
        this.convertAndBackCheck(
            DATE_FALSE,
            LocalDateTime.of(DATE_FALSE, LocalTime.MIDNIGHT)
        );
    }

    @Test
    public void testConvertWithDateTrueToByte() {
        this.convertAndCheck(
            DATE_TRUE,
            NUMBER_TRUE.byteValue()
        );
    }

    @Test
    public void testConvertWithDateFalseToByte() {
        this.convertAndCheck(
            DATE_FALSE,
            NUMBER_FALSE.byteValue()
        );
    }

    @Test
    public void testConvertWithDateTrueToShort() {
        this.convertAndCheck(
            DATE_TRUE,
            NUMBER_TRUE.shortValue()
        );
    }

    @Test
    public void testConvertWithDateFalseToShort() {
        this.convertAndCheck(
            DATE_FALSE,
            NUMBER_FALSE.shortValue()
        );
    }

    @Test
    public void testConvertWithDateTrueToInteger() {
        this.convertAndCheck(
            DATE_TRUE,
            NUMBER_TRUE.intValue()
        );
    }

    @Test
    public void testConvertWithDateFalseToInteger() {
        this.convertAndCheck(
            DATE_FALSE,
            NUMBER_FALSE.intValue()
        );
    }

    @Test
    public void testConvertWithDateTrueToLong() {
        this.convertAndCheck(
            DATE_TRUE,
            NUMBER_TRUE.longValue()
        );
    }

    @Test
    public void testConvertWithDateFalseToLong() {
        this.convertAndCheck(
            DATE_FALSE,
            NUMBER_FALSE.longValue()
        );
    }

    @Test
    public void testConvertWithDateTrueToFloat() {
        this.convertAndCheck(
            DATE_TRUE,
            NUMBER_TRUE.floatValue()
        );
    }

    @Test
    public void testConvertWithDateFalseToFloat() {
        this.convertAndCheck(
            DATE_FALSE,
            NUMBER_FALSE.floatValue()
        );
    }

    @Test
    public void testConvertWithDateTrueToDouble() {
        this.convertAndCheck(
            DATE_TRUE,
            NUMBER_TRUE.doubleValue()
        );
    }

    @Test
    public void testConvertWithDateFalseToDouble() {
        this.convertAndCheck(
            DATE_FALSE,
            NUMBER_FALSE.doubleValue()
        );
    }

    @Test
    public void testConvertWithDateTrueToExpressionNumber() {
        this.convertAndCheck(
            DATE_TRUE,
            EXPRESSION_NUMBER_KIND.one()
        );
    }

    @Test
    public void testConvertWithDateFalseToExpressionNumber() {
        this.convertAndCheck(
            DATE_FALSE,
            EXPRESSION_NUMBER_KIND.zero()
        );
    }

    @Test
    public void testConvertWithDateTrueToString() {
        this.convertAndCheck(
            DATE,
            "D 2000-12-31"
        );
    }

    @Test
    public void testConvertWithDateToTimeFails() {
        this.convertFails(
            DATE,
            LocalTime.class
        );
    }

    // DateTime.........................................................................................................

    @Test
    public void testConvertWithDateTimeTrueToBoolean() {
        this.convertAndCheck(
            DATE_TIME_TRUE,
            true
        );
    }

    @Test
    public void testConvertWithDateTimeFalseToBoolean() {
        this.convertAndCheck(
            DATE_TIME_FALSE,
            false
        );
    }

    @Test
    public void testConvertWithDateTimeToDate() {
        this.convertAndCheck(
            DATE_TIME,
            DATE
        );
    }

    @Test
    public void testConvertWithDateTimeToDateTime() {
        this.convertAndCheck(DATE_TIME);
    }

    @Test
    public void testConvertWithDateTimeTrueToByte() {
        this.convertAndCheck(
            DATE_TIME_TRUE,
            NUMBER_TRUE.byteValue()
        );
    }

    @Test
    public void testConvertWithDateTimeFalseToByte() {
        this.convertAndCheck(
            DATE_TIME_FALSE,
            NUMBER_FALSE.byteValue()
        );
    }

    @Test
    public void testConvertWithDateTimeTrueToShort() {
        this.convertAndCheck(
            DATE_TIME_TRUE,
            NUMBER_TRUE.shortValue()
        );
    }

    @Test
    public void testConvertWithDateTimeFalseToShort() {
        this.convertAndCheck(
            DATE_TIME_FALSE,
            NUMBER_FALSE.shortValue()
        );
    }

    @Test
    public void testConvertWithDateTimeTrueToInteger() {
        this.convertAndCheck(
            DATE_TIME_TRUE,
            NUMBER_TRUE.intValue()
        );
    }

    @Test
    public void testConvertWithDateTimeFalseToInteger() {
        this.convertAndCheck(
            DATE_TIME_FALSE,
            NUMBER_FALSE.intValue()
        );
    }

    @Test
    public void testConvertWithDateTimeTrueToLong() {
        this.convertAndCheck(
            DATE_TIME_TRUE,
            NUMBER_TRUE.longValue()
        );
    }

    @Test
    public void testConvertWithDateTimeFalseToLong() {
        this.convertAndCheck(
            DATE_TIME_FALSE,
            NUMBER_FALSE.longValue()
        );
    }

    @Test
    public void testConvertWithDateTimeTrueToFloat() {
        this.convertAndCheck(
            DATE_TIME_TRUE,
            NUMBER_TRUE.floatValue()
        );
    }

    @Test
    public void testConvertWithDateTimeFalseToFloat() {
        this.convertAndCheck(
            DATE_TIME_FALSE,
            NUMBER_FALSE.floatValue()
        );
    }

    @Test
    public void testConvertWithDateTimeTrueToDouble() {
        this.convertAndCheck(
            DATE_TIME_TRUE,
            NUMBER_TRUE.doubleValue()
        );
    }

    @Test
    public void testConvertWithDateTimeFalseToDouble() {
        this.convertAndCheck(
            DATE_TIME_FALSE,
            NUMBER_FALSE.doubleValue()
        );
    }

    @Test
    public void testConvertWithDateTimeTrueToExpressionNumber() {
        this.convertAndCheck(
            DATE_TIME_TRUE,
            EXPRESSION_NUMBER_KIND.one()
        );
    }

    @Test
    public void testConvertWithDateTimeFalseToExpressionNumber() {
        this.convertAndCheck(
            DATE_TIME_FALSE,
            EXPRESSION_NUMBER_KIND.zero()
        );
    }

    @Test
    public void testConvertWithDateTimeTrueToString() {
        this.convertAndCheck(
            DATE_TIME,
            "DT 2000-12-31 12-58"
        );
    }

    @Test
    public void testConvertWithDateTimeToTime() {
        this.convertAndCheck(
            DATE_TIME,
            TIME
        );
    }

    // HasText..........................................................................................................

    @Test
    public void testConvertWithHasTextToBooleanFalseFails() {
        this.convertFails(
            hasText("false"),
            Boolean.class
        );
    }

    @Test
    public void testConvertWithHasTextToBooleanTrueFails() {
        this.convertFails(
            hasText("true"),
            Boolean.class
        );
    }

    @Test
    public void testConvertWithHasTextToDateFails() {
        this.convertFails(
            hasText("D 2000-12-31"),
            LocalDate.class
        );
    }

    @Test
    public void testConvertWithHasTextToDateTimeFails() {
        this.convertFails(
            hasText("DT 31 12 2000 12 58 59"),
            LocalDateTime.class
        );
    }

    @Test
    public void testConvertWithHasTextToExpressionNumberFails() {
        this.convertFails(
            hasText("N 123"),
            ExpressionNumber.class
        );
    }

    @Test
    public void testConvertWithHasTextToTimeFails() {
        this.convertFails(
            hasText("T 12 58 59"),
            LocalTime.class
        );
    }

    @Test
    public void testConvertWithHasTextToStringFails() {
        this.convertFails(
            hasText("A"),
            String.class
        );
    }

    private static TextNode hasText(final String text) {
        return SpreadsheetText.with(text)
            .setColor(Optional.of(Color.BLACK))
            .toTextNode();
    }

    @Test
    public void testConvertWithAbsoluteUrlToStringFailsFails() {
        final String url = "https://example.com";

        this.convertFails(
            Url.parseAbsolute(url),
            String.class//,
            //url
        );
    }

    @Test
    public void testConvertWithColorToStringFails() {
        this.convertFails(
            Color.BLACK,
            String.class
        );
    }

    @Test
    public void testConvertWithTextStyleToStringFails() {
        this.convertFails(
            TextStyle.EMPTY.set(
                TextStylePropertyName.COLOR,
                Color.BLACK
            ).set(
                TextStylePropertyName.TEXT_ALIGN,
                TextAlign.LEFT
            ),
            String.class
        );
    }

    // Locale...........................................................................................................

    @Test
    public void testConvertWithLocaleToStringFails() {
        this.convertFails(
            Locale.forLanguageTag("en-AU"),
            String.class
        );
    }

    // Number...........................................................................................................

    @Test
    public void testConvertWithByteTrueToBoolean() {
        this.convertAndCheck(
            NUMBER_TRUE.byteValue(),
            true
        );
    }

    @Test
    public void testConvertWithByteFalseToBoolean() {
        this.convertAndCheck(
            NUMBER_FALSE.byteValue(),
            false
        );
    }

    @Test
    public void testConvertWithShortTrueToBoolean() {
        this.convertAndCheck(
            NUMBER_TRUE.shortValue(),
            true
        );
    }

    @Test
    public void testConvertWithShortFalseToBoolean() {
        this.convertAndCheck(
            NUMBER_FALSE.shortValue(),
            false
        );
    }

    @Test
    public void testConvertWithIntegerTrueToBoolean() {
        this.convertAndCheck(
            NUMBER_TRUE.intValue(),
            true
        );
    }

    @Test
    public void testConvertWithIntegerFalseToBoolean() {
        this.convertAndCheck(
            NUMBER_FALSE.intValue(),
            false
        );
    }

    @Test
    public void testConvertWithLongTrueToBoolean() {
        this.convertAndCheck(
            NUMBER_TRUE.longValue(),
            true
        );
    }

    @Test
    public void testConvertWithLongFalseToBoolean() {
        this.convertAndCheck(
            NUMBER_FALSE.longValue(),
            false
        );
    }

    @Test
    public void testConvertWithFloatTrueToBoolean() {
        this.convertAndCheck(
            NUMBER_TRUE.floatValue(),
            true
        );
    }

    @Test
    public void testConvertWithFloatFalseToBoolean() {
        this.convertAndCheck(
            NUMBER_FALSE.floatValue(),
            false
        );
    }

    @Test
    public void testConvertWithDoubleTrueToBoolean() {
        this.convertAndCheck(
            NUMBER_TRUE.doubleValue(),
            true
        );
    }

    @Test
    public void testConvertWithDoubleFalseToBoolean() {
        this.convertAndCheck(
            NUMBER_FALSE.doubleValue(),
            false
        );
    }

    @Test
    public void testConvertWithExpressionNumberTrueToBoolean() {
        this.convertAndCheck(
            EXPRESSION_NUMBER_KIND.one(),
            true
        );
    }

    @Test
    public void testConvertWithExpressionNumberFalseToBoolean() {
        this.convertAndCheck(
            EXPRESSION_NUMBER_KIND.zero(),
            false
        );
    }

    @Test
    public void testConvertWithBigDecimalTrueToBoolean() {
        this.convertAndCheck(
            BigDecimal.ONE,
            true
        );
    }

    @Test
    public void testConvertWithBigDecimalFalseToBoolean() {
        this.convertAndCheck(
            BigDecimal.ZERO,
            false
        );
    }

    @Test
    public void testConvertWithBigIntegerTrueToBoolean() {
        this.convertAndCheck(
            BigInteger.ONE,
            true
        );
    }

    @Test
    public void testConvertWithBigIntegerFalseToBoolean() {
        this.convertAndCheck(
            BigInteger.ZERO,
            false
        );
    }

    // String...........................................................................................................

    @Test
    public void testConvertWithCharacterToString2() {
        this.convertAndCheck(
            'A',
            "A"
        );
    }

    @Test
    public void testConvertWithStringToBooleanFalse() {
        this.convertAndCheck(
            STRING_FALSE,
            false
        );
    }

    @Test
    public void testConvertWithStringToBooleanTrue() {
        this.convertAndCheck(
            STRING_TRUE,
            true
        );
    }

    @Test
    public void testConvertWithStringToCharacter() {
        this.convertAndCheck(
            "A",
            'A'
        );
    }

    @Test
    public void testConvertWithStringToDate() {
        this.convertAndCheck(
            "D 2000-12-31",
            DATE
        );
    }

    // "\"DT\" dd mm yyyy hh mm ss"

    @Test
    public void testConvertWithStringToDateTime() {
        this.convertAndCheck(
            "DT 31 12 2000 12 58 59",
            DATE_TIME
        );
    }

    @Test
    public void testConvertWithStringToByte() {
        this.convertAndCheck(
            "N 123",
            (byte) 123
        );
    }

    @Test
    public void testConvertWithStringToShort() {
        this.convertAndCheck(
            "N 123",
            (short) 123
        );
    }

    @Test
    public void testConvertWithStringToInteger() {
        this.convertAndCheck(
            "N 123",
            123
        );
    }

    @Test
    public void testConvertWithStringToLong() {
        this.convertAndCheck(
            "N 123",
            123L
        );
    }

    @Test
    public void testConvertWithStringToFloat() {
        this.convertAndCheck(
            "N 123",
            123f
        );
    }

    @Test
    public void testConvertWithStringToDouble() {
        this.convertAndCheck(
            "N 123",
            123f
        );
    }

    @Test
    public void testConvertWithStringToBigInteger() {
        this.convertAndCheck(
            "N 123",
            BigInteger.valueOf(123)
        );
    }

    @Test
    public void testConvertWithStringToBigDecimal() {
        this.convertAndCheck(
            "N 123",
            BigDecimal.valueOf(123)
        );
    }

    @Test
    public void testConvertWithStringToExpressionNumber() {
        this.convertAndCheck(
            "N 123",
            EXPRESSION_NUMBER_KIND.create(123)
        );
    }

    @Test
    public void testConvertWithStringToSpreadsheetCellReferenceFails() {
        this.convertFails(
            SpreadsheetSelection.parseCell("Z99"),
            String.class
        );
    }

    @Test
    public void testConvertWithStringToSpreadsheetCellRangeFails() {
        this.convertFails(
            SpreadsheetSelection.parseCellRange("Z99:Z100"),
            String.class
        );
    }

    @Test
    public void testConvertWithStringToSpreadsheetColumnReferenceFails() {
        this.convertFails(
            SpreadsheetSelection.parseColumn("Z"),
            String.class
        );
    }

    @Test
    public void testConvertWithStringToSpreadsheetColumnRangeReferenceFails() {
        this.convertFails(
            SpreadsheetSelection.parseColumnRange("X:Y"),
            String.class
        );
    }

    @Test
    public void testConvertWithStringToSpreadsheetLabelNameFails() {
        this.convertFails(
            SpreadsheetSelection.labelName("Label123"),
            String.class
        );
    }

    @Test
    public void testConvertWithStringToSpreadsheetRowReferenceFails() {
        this.convertFails(
            SpreadsheetSelection.parseRow("123"),
            String.class
        );
    }

    @Test
    public void testConvertWithStringToSpreadsheetRowRangeReferenceFails() {
        this.convertFails(
            SpreadsheetSelection.parseRowRange("123:456"),
            String.class
        );
    }

    @Test
    public void testConvertWithStringToString() {
        final String text = "abc123";
        this.convertAndCheck(text, text);
    }

    @Test
    public void testConvertWithStringToTime() {
        this.convertAndCheck(
            "T 12 58 59",
            TIME
        );
    }

    @Test
    public void testConvertWithStringTrueToByte() {
        this.convertAndCheck(
            STRING_TRUE,
            NUMBER_TRUE.byteValue()
        );
    }

    @Test
    public void testConvertWithStringFalseToByte() {
        this.convertAndCheck(
            STRING_FALSE,
            NUMBER_FALSE.byteValue()
        );
    }

    @Test
    public void testConvertWithStringTrueToShort() {
        this.convertAndCheck(
            STRING_TRUE,
            NUMBER_TRUE.shortValue()
        );
    }

    @Test
    public void testConvertWithStringFalseToShort() {
        this.convertAndCheck(
            STRING_FALSE,
            NUMBER_FALSE.byteValue()
        );
    }

    @Test
    public void testConvertWithStringTrueToInteger() {
        this.convertAndCheck(
            STRING_TRUE,
            NUMBER_TRUE.intValue()
        );
    }

    @Test
    public void testConvertWithStringFalseToInteger() {
        this.convertAndCheck(
            STRING_FALSE,
            NUMBER_FALSE.intValue()
        );
    }

    @Test
    public void testConvertWithStringTrueToLong() {
        this.convertAndCheck(
            STRING_TRUE,
            NUMBER_TRUE.longValue()
        );
    }

    @Test
    public void testConvertWithStringFalseToLong() {
        this.convertAndCheck(
            STRING_FALSE,
            NUMBER_FALSE.longValue()
        );
    }

    @Test
    public void testConvertWithStringTrueToFloat() {
        this.convertAndCheck(
            STRING_TRUE,
            NUMBER_TRUE.floatValue()
        );
    }

    @Test
    public void testConvertWithStringFalseToFloat() {
        this.convertAndCheck(
            STRING_FALSE,
            NUMBER_FALSE.floatValue()
        );
    }

    @Test
    public void testConvertWithStringTrueToDouble() {
        this.convertAndCheck(
            STRING_TRUE,
            NUMBER_TRUE.doubleValue()
        );
    }

    @Test
    public void testConvertWithStringFalseToDouble() {
        this.convertAndCheck(
            STRING_FALSE,
            NUMBER_FALSE.doubleValue()
        );
    }

    @Test
    public void testConvertWithStringTrueToBigInteger() {
        this.convertAndCheck(
            STRING_TRUE,
            new BigInteger(NUMBER_TRUE.toString())
        );
    }

    @Test
    public void testConvertWithStringFalseToBigInteger() {
        this.convertAndCheck(
            STRING_FALSE,
            new BigInteger(NUMBER_FALSE.toString())
        );
    }

    @Test
    public void testConvertWithStringTrueToBigDecimal() {
        this.convertAndCheck(
            STRING_TRUE,
            new BigDecimal(NUMBER_TRUE.toString())
        );
    }

    @Test
    public void testConvertWithStringFalseToBigDecimal() {
        this.convertAndCheck(
            STRING_FALSE,
            new BigDecimal(NUMBER_FALSE.toString())
        );
    }

    @Test
    public void testConvertWithStringTrueToNumberFails() {
        this.convertFails(
            STRING_TRUE,
            Number.class
        );
    }

    @Test
    public void testConvertWithStringFalseToNumberFails() {
        this.convertFails(
            STRING_FALSE,
            Number.class
        );
    }

    @Test
    public void testConvertWithStringTrueToExpressionNumber() {
        this.convertAndCheck(
            STRING_TRUE,
            EXPRESSION_NUMBER_KIND.one()
        );
    }

    @Test
    public void testConvertWithStringFalseToExpressionNumber() {
        this.convertAndCheck(
            STRING_FALSE,
            EXPRESSION_NUMBER_KIND.zero()
        );
    }

    @Test
    public void testConvertWithStringTrueToDate() {
        this.convertAndCheck(
            STRING_TRUE,
            DATE_TRUE
        );
    }

    @Test
    public void testConvertWithStringFalseToDate() {
        this.convertAndCheck(
            STRING_FALSE,
            DATE_FALSE
        );
    }

    @Test
    public void testConvertWithStringTrueToDateTime() {
        this.convertAndCheck(
            STRING_TRUE,
            DATE_TIME_TRUE
        );
    }

    @Test
    public void testConvertWithStringFalseToDateTime() {
        this.convertAndCheck(
            STRING_FALSE,
            DATE_TIME_FALSE
        );
    }

    @Test
    public void testConvertWithStringTrueToTime() {
        this.convertAndCheck(
            STRING_TRUE,
            TIME_TRUE
        );
    }

    @Test
    public void testConvertWithStringFalseToTime() {
        this.convertAndCheck(
            STRING_FALSE,
            TIME_FALSE
        );
    }

    // Time.............................................................................................................

    @Test
    public void testConvertWithTimeTrueToBoolean() {
        this.convertAndCheck(
            TIME_TRUE,
            true
        );
    }

    @Test
    public void testConvertWithTimeFalseToBoolean() {
        this.convertAndCheck(
            TIME_FALSE,
            false
        );
    }

    @Test
    public void testConvertWithTimeToDate() {
        this.convertFails(
            TIME,
            LocalDate.class
        );
    }

    @Test
    public void testConvertWithTimeToDateTime() {
        this.convertAndCheck(
            TIME,
            LocalDateTime.of(DATE_FALSE, TIME)
        );
    }

    @Test
    public void testConvertWithTimeTrueToByte() {
        this.convertAndCheck(
            TIME_TRUE,
            NUMBER_TRUE.byteValue()
        );
    }

    @Test
    public void testConvertWithTimeFalseToByte() {
        this.convertAndCheck(
            TIME_FALSE,
            NUMBER_FALSE.byteValue()
        );
    }

    @Test
    public void testConvertWithTimeTrueToShort() {
        this.convertAndCheck(
            TIME_TRUE,
            NUMBER_TRUE.shortValue()
        );
    }

    @Test
    public void testConvertWithTimeFalseToShort() {
        this.convertAndCheck(
            TIME_FALSE,
            NUMBER_FALSE.shortValue()
        );
    }

    @Test
    public void testConvertWithTimeTrueToInteger() {
        this.convertAndCheck(
            TIME_TRUE,
            NUMBER_TRUE.intValue()
        );
    }

    @Test
    public void testConvertWithTimeFalseToInteger() {
        this.convertAndCheck(
            TIME_FALSE,
            NUMBER_FALSE.intValue()
        );
    }

    @Test
    public void testConvertWithTimeTrueToLong() {
        this.convertAndCheck(
            TIME_TRUE,
            NUMBER_TRUE.longValue()
        );
    }

    @Test
    public void testConvertWithTimeFalseToLong() {
        this.convertAndCheck(
            TIME_FALSE,
            NUMBER_FALSE.longValue()
        );
    }

    @Test
    public void testConvertWithTimeTrueToFloat() {
        this.convertAndCheck(
            TIME_TRUE,
            NUMBER_TRUE.floatValue()
        );
    }

    @Test
    public void testConvertWithTimeFalseToFloat() {
        this.convertAndCheck(
            TIME_FALSE,
            NUMBER_FALSE.floatValue()
        );
    }

    @Test
    public void testConvertWithTimeTrueToDouble() {
        this.convertAndCheck(
            TIME_TRUE,
            NUMBER_TRUE.doubleValue()
        );
    }

    @Test
    public void testConvertWithTimeFalseToDouble() {
        this.convertAndCheck(
            TIME_FALSE,
            NUMBER_FALSE.doubleValue()
        );
    }

    @Test
    public void testConvertWithTimeTrueToExpressionNumber() {
        this.convertAndCheck(
            TIME_TRUE,
            EXPRESSION_NUMBER_KIND.one()
        );
    }

    @Test
    public void testConvertWithTimeFalseToExpressionNumber() {
        this.convertAndCheck(
            TIME_FALSE,
            EXPRESSION_NUMBER_KIND.zero()
        );
    }

    @Test
    public void testConvertWithDateTrueToString2() {
        this.convertAndCheck(
            DATE,
            "D 2000-12-31"
        );
    }

    @Test
    public void testConvertWithTimeToTime() {
        this.convertAndCheck(TIME);
    }

    @Override
    public SpreadsheetConverterGeneral createConverter() {
        return SpreadsheetConverterGeneral.with(
            DATE_FORMATTER,
            DATE_PARSER,
            DATE_TIME_FORMATTER,
            DATE_TIME_PARSER,
            NUMBER_FORMATTER,
            NUMBER_PARSER,
            TEXT_FORMATTER,
            TIME_FORMATTER,
            TIME_PARSER
        );
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return this.createContext(
            SpreadsheetLabelNameResolvers.fake()
        );
    }

    private SpreadsheetConverterContext createContext(final SpreadsheetLabelNameResolver labelNameResolver) {
        final Locale locale = Locale.ENGLISH;

        return SpreadsheetConverterContexts.basic(
            SpreadsheetConverterContexts.NO_METADATA,
            SpreadsheetConverterContexts.NO_VALIDATION_REFERENCE,
            SpreadsheetConverters.collection(
                Lists.of(
                    Converters.simple(),
                    SpreadsheetConverters.textToText(),
                    SpreadsheetConverters.numberToNumber(),
                    Converters.localDateToLocalDateTime(),
                    Converters.localTimeToLocalDateTime()
                )
            ),
            labelNameResolver,
            JsonNodeConverterContexts.basic(
                ExpressionNumberConverterContexts.basic(
                    Converters.fake(),
                    ConverterContexts.basic(
                        DATE_OFFSET, // dateOffset
                        Converters.fake(),
                        DateTimeContexts.basic(
                            DateTimeSymbols.fromDateFormatSymbols(
                                new DateFormatSymbols(locale)
                            ),
                            locale,
                            1900,
                            20,
                            LocalDateTime::now
                        ),
                        DecimalNumberContexts.basic(
                            DecimalNumberSymbols.with(
                                ':', // negativeSign
                                ';', // positiveSign
                                '0',
                                "CC",
                                '*', // decimalSeparator
                                "EE",
                                '/', // groupSeparator
                                "Infinity!",
                                '*',
                                "Nan!",
                                '^', // percentSymbol,
                                '&'
                            ),
                            locale,
                            MathContext.DECIMAL32
                        )
                    ),
                    EXPRESSION_NUMBER_KIND
                ),
                JsonNodeMarshallUnmarshallContexts.fake()
            )
        );
    }

    private void convertAndBackCheck(final Object value,
                                     final Object expected) {
        this.convertAndCheck(value, expected);
        this.convertAndCheck(expected, value);
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createConverter(),
            SpreadsheetConverterGeneral.class.getSimpleName()
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetConverterGeneral> type() {
        return SpreadsheetConverterGeneral.class;
    }
}

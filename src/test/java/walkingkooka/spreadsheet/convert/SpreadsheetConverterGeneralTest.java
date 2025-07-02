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
import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
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

    private final static int NUMBER_TRUE = 1;
    private final static int NUMBER_FALSE = 0;

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
    public void testConvertNullToObject() {
        this.convertAndCheck(
                null,
                Object.class
        );
    }

    @Test
    public void testConvertBooleanTrueToObject() {
        this.convertAndCheck(
                true,
                Object.class
        );
    }

    @Test
    public void testConvertExpressionNumberToObject() {
        this.convertAndCheck(
                EXPRESSION_NUMBER_KIND.create(123),
                Object.class
        );
    }

    @Test
    public void testConvertIntegerTOObject() {
        this.convertAndCheck(
                1,
                Object.class
        );
    }

    @Test
    public void testConvertStringToObject() {
        this.convertAndCheck(
                "abc123",
                Object.class
        );
    }

    // SpreadsheetCellReference.........................................................................................

    @Test
    public void testConvertSpreadsheetCellReferenceToStringFails() {
        this.convertFails(
                SpreadsheetSelection.A1,
                String.class
        );
    }

    // SpreadsheetCellRangeReference.............................................................................................

    @Test
    public void testConvertSpreadsheetCellRangeToStringFails() {
        this.convertFails(
                SpreadsheetSelection.parseCellRange("A1:B2"),
                String.class
        );
    }

    // SpreadsheetColumnReference.......................................................................................

    @Test
    public void testConvertSpreadsheetColumnReferenceToStringFails() {
        this.convertFails(
                SpreadsheetSelection.parseColumn("C"),
                String.class
        );
    }

    // SpreadsheetColumnRangeReference..................................................................................

    @Test
    public void testConvertSpreadsheetColumnRangeReferenceToStringFails() {
        this.convertFails(
                SpreadsheetSelection.parseColumnRange("D:E"),
                String.class
        );
    }

    // SpreadsheetLabelName............................................................................................

    @Test
    public void testConvertSpreadsheetLabelNameToStringFails() {
        this.convertFails(
                SpreadsheetSelection.labelName("Label123"),
                String.class
        );
    }

    // SpreadsheetRowReference.......................................................................................

    @Test
    public void testConvertSpreadsheetRowReferenceToStringFails() {
        this.convertFails(
                SpreadsheetSelection.parseRow("5"),
                String.class
        );
    }

    // SpreadsheetRowRangeReference..................................................................................

    @Test
    public void testConvertSpreadsheetRowRangeReferenceToStringFails() {
        this.convertFails(
                SpreadsheetSelection.parseRowRange("6:7"),
                String.class
        );
    }

    // error ...........................................................................................................

    @Test
    public void testConvertSpreadsheetErrorToExpressionNumberFails() {
        this.convertFails(
                SpreadsheetErrorKind.ERROR.setMessage("Ignored"),
                ExpressionNumber.class
        );
    }

    @Test
    public void testConvertSpreadsheetErrorToString() {
        this.convertAndCheck(
                SpreadsheetErrorKind.DIV0.setMessage("Ignored"),
                String.class,
                SpreadsheetErrorKind.DIV0.toString()
        );
    }

    // Expression.......................................................................................................

    @Test
    public void testConvertExpressionValueToExpression() {
        this.convertAndCheck(
                Expression.value(123),
                Expression.class
        );
    }

    // boolean..........................................................................................................

    @Test
    public void testConvertBooleanTrueToBoolean() {
        this.convertAndCheck(true);
    }

    @Test
    public void testConvertBooleanFalseToBoolean() {
        this.convertAndCheck(false);
    }

    @Test
    public void testConvertBooleanTrueToDate() {
        this.convertAndBackCheck(
                true,
                DATE_TRUE
        );
    }

    @Test
    public void testConvertBooleanFalseToDate() {
        this.convertAndBackCheck(
                false,
                DATE_FALSE
        );
    }

    @Test
    public void testConvertBooleanTrueToDateTime() {
        this.convertAndBackCheck(
                true,
                DATE_TIME_TRUE
        );
    }

    @Test
    public void testConvertBooleanFalseToDateTime() {
        this.convertAndBackCheck(
                false,
                DATE_TIME_FALSE
        );
    }

    @Test
    public void testConvertBooleanTrueToAllNumberTypes() {
        this.convertToAllNumberTypesAndCheck(
                true,
                NUMBER_TRUE
        );
    }

    @Test
    public void testConvertBooleanFalseToAllNumberTypes() {
        this.convertToAllNumberTypesAndCheck(
                false,
                NUMBER_FALSE
        );
    }

    @Test
    public void testConvertBooleanTrueToString() {
        this.convertAndCheck(
                true,
                STRING_TRUE + TEXT_SUFFIX
        );
    }

    @Test
    public void testConvertBooleanFalseToString() {
        this.convertAndCheck(
                false,
                false + TEXT_SUFFIX
        );
    }

    @Test
    public void testConvertBooleanTrueToTime() {
        this.convertAndBackCheck(
                true,
                TIME_TRUE
        );
    }

    @Test
    public void testConvertBooleanFalseToTime() {
        this.convertAndBackCheck(
                false,
                TIME_FALSE
        );
    }

    // Character........................................................................................................

    @Test
    public void testConvertCharacterToString() {
        this.convertAndCheck(
                'A',
                String.class,
                "A"
        );
    }

    // Date.............................................................................................................

    @Test
    public void testConvertNullToBoolean() {
        this.convertAndCheck(
                null,
                Boolean.class
        );
    }

    @Test
    public void testConvertNullToDate() {
        this.convertAndCheck(
                null,
                LocalDate.class
        );
    }

    @Test
    public void testConvertNullToDateTime() {
        this.convertAndCheck(
                null,
                LocalDateTime.class
        );
    }

    @Test
    public void testConvertNullToTime() {
        this.convertAndCheck(
                null,
                LocalTime.class
        );
    }

    @Test
    public void testConvertNullToExpressionNumber() {
        this.convertAndCheck(
                null,
                ExpressionNumber.class,
                EXPRESSION_NUMBER_KIND.zero()
        );
    }

    @Test
    public void testConvertNullToNumber() {
        this.convertAndCheck(
                null,
                Number.class,
                EXPRESSION_NUMBER_KIND.zero()
        );
    }

    @Test
    public void testConvertNullToString() {
        this.convertAndCheck(
                null,
                String.class
        );
    }

    // date............................................................................................................

    @Test
    public void testConvertDateTrueToBoolean() {
        this.convertAndCheck(
                DATE_TRUE,
                true
        );
    }

    @Test
    public void testConvertDateFalseToBoolean() {
        this.convertAndCheck(
                DATE_FALSE,
                false
        );
    }

    @Test
    public void testConvertDateToDate() {
        this.convertAndCheck(
                LocalDate.of(2000, 12, 31)
        );
    }

    @Test
    public void testConvertDateTrueToDateTime() {
        this.convertAndBackCheck(
                DATE_TRUE,
                LocalDateTime.of(DATE_TRUE, LocalTime.MIDNIGHT)
        );
    }

    @Test
    public void testConvertDateFalseToDateTime() {
        this.convertAndBackCheck(
                DATE_FALSE,
                LocalDateTime.of(DATE_FALSE, LocalTime.MIDNIGHT)
        );
    }

    @Test
    public void testConvertDateTrueToAllNumberTypes() {
        this.convertToAllNumberTypesAndCheck(
                DATE_TRUE,
                NUMBER_TRUE
        );
    }

    @Test
    public void testConvertDateFalseToAllNumberTypes() {
        this.convertToAllNumberTypesAndCheck(
                DATE_FALSE,
                NUMBER_FALSE
        );
    }

    @Test
    public void testConvertDateToAllNumberTypes() {
        final int value = 100;
        this.convertToAllNumberTypesAndCheck(
                LocalDate.ofEpochDay(value),
                value
        );
    }

    @Test
    public void testConvertDateTrueToString() {
        this.convertAndCheck(
                DATE,
                "D 2000-12-31"
        );
    }

    @Test
    public void testConvertDateToTimeFails() {
        this.convertFails(
                DATE,
                LocalTime.class
        );
    }

    // DateTime.........................................................................................................

    @Test
    public void testConvertDateTimeTrueToBoolean() {
        this.convertAndCheck(
                DATE_TIME_TRUE,
                true
        );
    }

    @Test
    public void testConvertDateTimeFalseToBoolean() {
        this.convertAndCheck(
                DATE_TIME_FALSE,
                false
        );
    }

    @Test
    public void testConvertDateTimeToDate() {
        this.convertAndCheck(
                DATE_TIME,
                DATE
        );
    }

    @Test
    public void testConvertDateTimeToDateTime() {
        this.convertAndCheck(DATE_TIME);
    }

    @Test
    public void testConvertDateTimeTrueToAllNumberTypes() {
        this.convertToAllNumberTypesAndCheck(
                DATE_TIME_TRUE,
                NUMBER_TRUE
        );
    }

    @Test
    public void testConvertDateTimeFalseToAllNumberTypes() {
        this.convertToAllNumberTypesAndCheck(
                DATE_TIME_FALSE,
                NUMBER_FALSE
        );
    }

    @Test
    public void testConvertDateTimeToAllNumberTypes() {
        final int value = 100;
        this.convertToAllNumberTypesAndCheck(
                LocalDateTime.of(
                        LocalDate.ofEpochDay(value),
                        LocalTime.MIDNIGHT
                ),
                value
        );
    }

    @Test
    public void testConvertDateTimeTrueToString() {
        this.convertAndCheck(
                DATE_TIME,
                "DT 2000-12-31 12-58"
        );
    }

    @Test
    public void testConvertDateTimeToTime() {
        this.convertAndCheck(
                DATE_TIME,
                TIME
        );
    }

    // HasText..........................................................................................................

    @Test
    public void testConvertHasTextToBooleanFalse() {
        this.convertAndCheck(
                text("false"),
                Boolean.class,
                false
        );
    }

    @Test
    public void testConvertHasTextToBooleanTrue() {
        this.convertAndCheck(
                text("true"),
                Boolean.class,
                true
        );
    }

    @Test
    public void testConvertHasTextToDate() {
        this.convertAndCheck(
                text("D 2000-12-31"),
                LocalDate.class,
                DATE
        );
    }

    @Test
    public void testConvertHasTextToDateTime() {
        this.convertAndCheck(
                text("DT 31 12 2000 12 58 59"),
                LocalDateTime.class,
                DATE_TIME
        );
    }

    @Test
    public void testConvertHasTextToExpressionNumber() {
        this.convertAndCheck(
                text("N 123"),
                ExpressionNumber.class,
                EXPRESSION_NUMBER_KIND.create(123)
        );
    }

    @Test
    public void testConvertHasTextToTime() {
        this.convertAndCheck(
                text("T 12 58 59"),
                LocalTime.class,
                TIME
        );
    }

    @Test
    public void testConvertHasTextToString() {
        this.convertAndCheck(
                text("A"),
                String.class,
                "A"
        );
    }

    private static TextNode text(final String text) {
        return SpreadsheetText.with(text)
                .setColor(Optional.of(Color.BLACK))
                .toTextNode();
    }

    @Test
    public void testConvertAbsoluteUrlToString() {
        final String url = "https://example.com";

        this.convertAndCheck(
                Url.parseAbsolute(url),
                String.class,
                url
        );
    }

    @Test
    public void testConvertColorToString() {
        final Color color = Color.BLACK;

        this.convertAndCheck(
                color,
                String.class,
                color.text()
        );
    }

    @Test
    public void testConvertTextStyleToString() {
        final TextStyle style = TextStyle.EMPTY.set(
                TextStylePropertyName.COLOR,
                Color.BLACK
        ).set(
                TextStylePropertyName.TEXT_ALIGN,
                TextAlign.LEFT
        );

        this.convertAndCheck(
                style,
                String.class,
                style.text()
        );
    }

    // Locale...........................................................................................................

    @Test
    public void testConvertLocaleToString() {
        this.convertAndCheck(
                Locale.forLanguageTag("en-AU"),
                "en-AU"
        );
    }

    // Number...........................................................................................................

    @Test
    public void testConvertAllNumberTypesTrueToBoolean() {
        this.convertAllNumberTypesAndCheck(
                1,
                true
        );
    }

    @Test
    public void testConvertAllNumberTypesFalseToBoolean() {
        this.convertAllNumberTypesAndCheck(
                0,
                false
        );
    }

    @Test
    public void testConvertAllNumberTypesTrueToDate() {
        this.convertAndBackCheck(
                1,
                DATE_TRUE
        );
    }

    @Test
    public void testConvertAllNumberTypesFalseToDate() {
        this.convertAndBackCheck(
                0,
                DATE_FALSE
        );
    }

    @Test
    public void testConvertAllNumberTypesTrueToDateTime() {
        this.convertAndBackCheck(
                1,
                DATE_TIME_TRUE
        );
    }

    @Test
    public void testConvertAllNumberTypesFalseToDateTime() {
        this.convertAndBackCheck(
                0,
                DATE_TIME_FALSE
        );
    }

    @Test
    public void testConvertAllNumberTypesToByte() {
        this.convertAllNumberTypesAndCheck((byte) 123);
    }

    @Test
    public void testConvertAllNumberTypesToShort() {
        this.convertAllNumberTypesAndCheck((short) 123);
    }

    @Test
    public void testConvertAllNumberTypesToInteger() {
        this.convertAllNumberTypesAndCheck(123);
    }

    @Test
    public void testConvertAllNumberTypesToLong() {
        this.convertAllNumberTypesAndCheck(123L);
    }

    @Test
    public void testConvertAllNumberTypesToFloat() {
        this.convertAllNumberTypesAndCheck(123f);
    }

    @Test
    public void testConvertAllNumberTypesToDouble() {
        this.convertAllNumberTypesAndCheck(123.0);
    }

    @Test
    public void testConvertAllNumberTypesToBigDecimal() {
        this.convertAllNumberTypesAndCheck(BigDecimal.valueOf(123));
    }

    @Test
    public void testConvertAllNumberTypesToBigInteger() {
        this.convertAllNumberTypesAndCheck(BigInteger.valueOf(123));
    }

    @Test
    public void testConvertAllNumberTypesToCharacter() {
        this.convertAndCheck(
                SpreadsheetConverterGeneral.with(
                        DATE_FORMATTER,
                        DATE_PARSER,
                        DATE_TIME_FORMATTER,
                        DATE_TIME_PARSER,
                        formatter(
                                "#",
                                SpreadsheetFormatParsers.numberParse(),
                                NumberSpreadsheetFormatParserToken.class,
                                SpreadsheetFormatters::number
                        ),
                        NUMBER_PARSER,
                        TEXT_FORMATTER,
                        TIME_FORMATTER,
                        TIME_PARSER
                ),
                ExpressionNumberKind.DEFAULT.create(1),
                Character.class,
                '1'
        );
    }

    @Test
    public void testConvertAllNumberTypesTrueToString() {
        this.convertAndCheck(
                -12.5,
                "N :12*5"
        );
    }

    @Test
    public void testConvertAllNumberTypesFalseTOString() {
        this.convertAndCheck(
                false,
                TIME_FALSE
        );
    }

    @Test
    public void testConvertAllNumberTypesToTime() {
        this.convertAndBackCheck(
                Converters.localTimeToNumber()
                        .convertOrFail(
                                TIME,
                                BigDecimal.class,
                                this.createContext()
                        ),
                TIME
        );
    }

    private void convertAllNumberTypesAndCheck(final Number value) {
        this.convertAllNumberTypesAndCheck(value, value);
    }

    private void convertAllNumberTypesAndCheck(final Number value,
                                               final Object expected) {
        final Converter<ConverterContext> numberNumber = Converters.numberToNumber();
        final ConverterContext context = this.createContext();

        this.convertAndBackCheck(numberNumber.convertOrFail(value, BigDecimal.class, context), expected);
        this.convertAndBackCheck(numberNumber.convertOrFail(value, BigInteger.class, context), expected);
        this.convertAndBackCheck(numberNumber.convertOrFail(value, Byte.class, context), expected);
        this.convertAndBackCheck(numberNumber.convertOrFail(value, Double.class, context), expected);
        this.convertAndBackCheck(numberNumber.convertOrFail(value, Float.class, context), expected);
        this.convertAndBackCheck(numberNumber.convertOrFail(value, Integer.class, context), expected);
        this.convertAndBackCheck(numberNumber.convertOrFail(value, Long.class, context), expected);
        this.convertAndBackCheck(numberNumber.convertOrFail(value, Short.class, context), expected);
    }

    // String.............................................................................................................

    @Test
    public void testConvertCharacterToString2() {
        this.convertAndCheck(
                'A',
                "A"
        );
    }

    @Test
    public void testConvertStringToBooleanFalse() {
        this.convertAndCheck(
                STRING_FALSE,
                false
        );
    }

    @Test
    public void testConvertStringToBooleanTrue() {
        this.convertAndCheck(
                STRING_TRUE,
                true
        );
    }

    @Test
    public void testConvertStringToCharacter() {
        this.convertAndCheck(
                "A",
                'A'
        );
    }

    @Test
    public void testConvertStringToDate() {
        this.convertAndCheck(
                "D 2000-12-31",
                DATE
        );
    }

    // "\"DT\" dd mm yyyy hh mm ss"

    @Test
    public void testConvertStringToDateTime() {
        this.convertAndCheck(
                "DT 31 12 2000 12 58 59",
                DATE_TIME
        );
    }

    @Test
    public void testConvertStringToByte() {
        this.convertAndCheck(
                "N 123",
                (byte) 123
        );
    }

    @Test
    public void testConvertStringToShort() {
        this.convertAndCheck(
                "N 123",
                (short) 123
        );
    }

    @Test
    public void testConvertStringToInteger() {
        this.convertAndCheck(
                "N 123",
                123
        );
    }

    @Test
    public void testConvertStringToLong() {
        this.convertAndCheck(
                "N 123",
                123L
        );
    }

    @Test
    public void testConvertStringToFloat() {
        this.convertAndCheck(
                "N 123",
                123f
        );
    }

    @Test
    public void testConvertStringToDouble() {
        this.convertAndCheck(
                "N 123",
                123f
        );
    }

    @Test
    public void testConvertStringToBigInteger() {
        this.convertAndCheck(
                "N 123",
                BigInteger.valueOf(123)
        );
    }

    @Test
    public void testConvertStringToBigDecimal() {
        this.convertAndCheck(
                "N 123",
                BigDecimal.valueOf(123)
        );
    }

    @Test
    public void testConvertStringToExpressionNumber() {
        this.convertAndCheck(
                "N 123",
                EXPRESSION_NUMBER_KIND.create(123)
        );
    }

    @Test
    public void testConvertStringToSpreadsheetCellReferenceFails() {
        this.convertFails(
                SpreadsheetSelection.parseCell("Z99"),
                String.class
        );
    }

    @Test
    public void testConvertStringToSpreadsheetCellRangeFails() {
        this.convertFails(
                SpreadsheetSelection.parseCellRange("Z99:Z100"),
                String.class
        );
    }

    @Test
    public void testConvertStringToSpreadsheetColumnReferenceFails() {
        this.convertFails(
                SpreadsheetSelection.parseColumn("Z"),
                String.class
        );
    }

    @Test
    public void testConvertStringToSpreadsheetColumnRangeReferenceFails() {
        this.convertFails(
                SpreadsheetSelection.parseColumnRange("X:Y"),
                String.class
        );
    }

    @Test
    public void testConvertStringToSpreadsheetLabelNameFails() {
        this.convertFails(
                SpreadsheetSelection.labelName("Label123"),
                String.class
        );
    }

    @Test
    public void testConvertStringToSpreadsheetRowReferenceFails() {
        this.convertFails(
                SpreadsheetSelection.parseRow("123"),
                String.class
        );
    }

    @Test
    public void testConvertStringToSpreadsheetRowRangeReferenceFails() {
        this.convertFails(
                SpreadsheetSelection.parseRowRange("123:456"),
                String.class
        );
    }

    @Test
    public void testConvertStringToString() {
        final String text = "abc123";
        this.convertAndCheck(text, text);
    }

    @Test
    public void testConvertStringToTime() {
        this.convertAndCheck(
                "T 12 58 59",
                TIME
        );
    }

    // Time.............................................................................................................

    @Test
    public void testConvertTimeTrueToBoolean() {
        this.convertAndCheck(
                TIME_TRUE,
                true
        );
    }

    @Test
    public void testConvertTimeFalseToBoolean() {
        this.convertAndCheck(
                TIME_FALSE,
                false
        );
    }

    @Test
    public void testConvertTimeToDate() {
        this.convertFails(
                TIME,
                LocalDate.class
        );
    }

    @Test
    public void testConvertTimeToDateTime() {
        this.convertAndCheck(
                TIME,
                LocalDateTime.of(DATE_FALSE, TIME)
        );
    }

    @Test
    public void testConvertTimeTrueToAllNumberTypes() {
        this.convertToAllNumberTypesAndCheck(
                TIME_TRUE,
                NUMBER_TRUE
        );
    }

    @Test
    public void testConvertTimeFalseToAllNumberTypes() {
        this.convertToAllNumberTypesAndCheck(
                TIME_FALSE,
                NUMBER_FALSE
        );
    }

    @Test
    public void testConvertTimeToAllNumberTypes() {
        final int value = 123;
        this.convertToAllNumberTypesAndCheck(
                LocalTime.ofSecondOfDay(value),
                value
        );
    }

    @Test
    public void testConvertDateTrueToString2() {
        this.convertAndCheck(
                DATE,
                "D 2000-12-31"
        );
    }

    @Test
    public void testConvertTimeToTime() {
        this.convertAndCheck(TIME);
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createConverter(), SpreadsheetConverterGeneral.class.getSimpleName());
    }

    // ConverterTesting.................................................................................................

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
                SpreadsheetConverters.basic(),
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

    private void convertToAllNumberTypesAndCheck(final Object value,
                                                 final double expected) {
        final Converter<SpreadsheetConverterContext> converter = this.createConverter();
        final SpreadsheetConverterContext context = this.createContext();

        this.convertAndBackCheck(value, converter.convertOrFail(expected, BigDecimal.class, context));
        this.convertAndBackCheck(value, converter.convertOrFail(expected, BigInteger.class, context));
        this.convertAndBackCheck(value, converter.convertOrFail(expected, Byte.class, context));
        this.convertAndBackCheck(value, converter.convertOrFail(expected, Double.class, context));
        this.convertAndBackCheck(value, converter.convertOrFail(expected, Float.class, context));
        this.convertAndBackCheck(value, converter.convertOrFail(expected, Integer.class, context));
        this.convertAndBackCheck(value, converter.convertOrFail(expected, Long.class, context));
        this.convertAndBackCheck(value, converter.convertOrFail(expected, Short.class, context));
        this.convertAndBackCheck(value, converter.convertOrFail(expected, ExpressionNumber.class, context));
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetConverterGeneral> type() {
        return SpreadsheetConverterGeneral.class;
    }
}

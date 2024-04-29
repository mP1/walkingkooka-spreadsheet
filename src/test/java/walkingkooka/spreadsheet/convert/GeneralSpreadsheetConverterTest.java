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
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.ConverterTesting2;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.spreadsheet.SpreadsheetErrorException;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextParserToken;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateTimeParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetNumberParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTimeParsePattern;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.SequenceParserToken;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class GeneralSpreadsheetConverterTest extends GeneralSpreadsheetConverterTestCase<GeneralSpreadsheetConverter>
        implements ConverterTesting2<GeneralSpreadsheetConverter, SpreadsheetConverterContext> {

    private final static long DATE_OFFSET = Converters.JAVA_EPOCH_OFFSET;
    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;

    // with.............................................................................................................

    @Test
    public void testWithNullDateFormatterFails() {
        withFails(null,
                dateParser(),
                dateTimeFormatter(),
                dateTimeParser(),
                numberFormatter(),
                numberParser(),
                textFormatter(),
                timeFormatter(),
                timeParser());
    }

    @Test
    public void testWithNullDateParserFails() {
        withFails(dateFormatter(),
                null,
                dateTimeFormatter(),
                dateTimeParser(),
                numberFormatter(),
                numberParser(),
                textFormatter(),
                timeFormatter(),
                timeParser());
    }

    @Test
    public void testWithNullDateTimeFormatterFails() {
        withFails(dateFormatter(),
                dateParser(),
                null,
                dateTimeParser(),
                numberFormatter(),
                numberParser(),
                textFormatter(),
                timeFormatter(),
                timeParser());
    }

    @Test
    public void testWithNullDateTimeParserFails() {
        withFails(dateFormatter(),
                dateParser(),
                dateTimeFormatter(),
                null,
                numberFormatter(),
                numberParser(),
                textFormatter(),
                timeFormatter(),
                timeParser());
    }

    @Test
    public void testWithNullNumberFormatterFails() {
        withFails(dateFormatter(),
                dateParser(),
                dateTimeFormatter(),
                dateTimeParser(),
                null,
                numberParser(),
                textFormatter(),
                timeFormatter(),
                timeParser());
    }

    @Test
    public void testWithNullNumberParserFails() {
        withFails(dateFormatter(),
                dateParser(),
                dateTimeFormatter(),
                dateTimeParser(),
                numberFormatter(),
                null,
                textFormatter(),
                timeFormatter(),
                timeParser());
    }

    @Test
    public void testWithNullTextFormatterFails() {
        withFails(dateFormatter(),
                dateParser(),
                dateTimeFormatter(),
                dateTimeParser(),
                numberFormatter(),
                numberParser(),
                null,
                timeFormatter(),
                timeParser());
    }

    @Test
    public void testWithNullTimeFormatterFails() {
        withFails(dateFormatter(),
                dateParser(),
                dateTimeFormatter(),
                dateTimeParser(),
                numberFormatter(),
                numberParser(),
                textFormatter(),
                null,
                timeParser());
    }

    @Test
    public void testWithNullTimeParserFails() {
        withFails(dateFormatter(),
                dateParser(),
                dateTimeFormatter(),
                dateTimeParser(),
                numberFormatter(),
                numberParser(),
                textFormatter(),
                timeFormatter(),
                null);
    }

    private void withFails(final SpreadsheetFormatter dateFormatter,
                           final SpreadsheetDateParsePattern dateParser,
                           final SpreadsheetFormatter dateTimeFormatter,
                           final SpreadsheetDateTimeParsePattern dateTimeParser,
                           final SpreadsheetFormatter numberFormatter,
                           final SpreadsheetNumberParsePattern numberParser,
                           final SpreadsheetFormatter textFormatter,
                           final SpreadsheetFormatter timeFormatter,
                           final SpreadsheetTimeParsePattern timeParser) {
        assertThrows(NullPointerException.class, () -> GeneralSpreadsheetConverter.with(dateFormatter,
                dateParser,
                dateTimeFormatter,
                dateTimeParser,
                numberFormatter,
                numberParser,
                textFormatter,
                timeFormatter,
                timeParser,
                DATE_OFFSET));
    }

    // convert..........................................................................................................

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
    public void testNullObject() {
        this.convertAndCheck(
                null,
                Object.class
        );
    }

    @Test
    public void testBooleanTrueObject() {
        this.convertAndCheck(
                true,
                Object.class
        );
    }

    @Test
    public void testExpressionNumberObject() {
        this.convertAndCheck(
                EXPRESSION_NUMBER_KIND.create(123),
                Object.class
        );
    }

    @Test
    public void testIntegerObject() {
        this.convertAndCheck(
                1,
                Object.class
        );
    }

    @Test
    public void testStringObject() {
        this.convertAndCheck(
                "abc123",
                Object.class
        );
    }

    // SpreadsheetCellReference.........................................................................................

    @Test
    public void testSpreadsheetCellReferenceToCellReference() {
        this.convertAndCheck(
                SpreadsheetSelection.A1,
                SpreadsheetCellReference.class
        );
    }

    @Test
    public void testSpreadsheetCellReferenceToCellReferenceOrRange() {
        this.convertAndCheck(
                SpreadsheetSelection.A1,
                SpreadsheetCellReferenceOrRange.class
        );
    }

    @Test
    public void testSpreadsheetCellReferenceToExpressionReference() {
        this.convertAndCheck(
                SpreadsheetSelection.parseCell("Z99"),
                SpreadsheetExpressionReference.class
        );
    }

    @Test
    public void testSpreadsheetCellReferenceToRange() {
        this.convertAndCheck(
                SpreadsheetSelection.parseCell("Z99"),
                SpreadsheetCellRangeReference.class,
                SpreadsheetSelection.parseCellRange("Z99")
        );
    }

    @Test
    public void testSpreadsheetCellReferenceToSpreadsheetSelection() {
        this.convertAndCheckSpreadsheetSelection(
                SpreadsheetSelection.A1
        );
    }

    @Test
    public void testSpreadsheetCellReferenceToString() {
        this.convertAndCheckString(
                SpreadsheetSelection.A1
        );
    }

    // SpreadsheetCellRangeReference.............................................................................................

    @Test
    public void testSpreadsheetCellRangeToCellOrCellRange() {
        this.convertAndCheck(
                SpreadsheetSelection.parseCellRange("A1:B2"),
                SpreadsheetCellReferenceOrRange.class
        );
    }

    @Test
    public void testSpreadsheetCellRangeToCellRange() {
        this.convertAndCheck(
                SpreadsheetSelection.parseCellRange("A1:B2"),
                SpreadsheetCellRangeReference.class
        );
    }

    @Test
    public void testSpreadsheetCellRangeToCell() {
        this.convertAndCheck(
                SpreadsheetSelection.parseCellRange("C3:D4"),
                SpreadsheetCellReference.class,
                SpreadsheetSelection.parseCell("C3")
        );
    }

    @Test
    public void testSpreadsheetCellRangeToExpressionReference() {
        this.convertAndCheck(
                SpreadsheetSelection.parseCellRange("C3:D4"),
                SpreadsheetExpressionReference.class
        );
    }

    @Test
    public void testSpreadsheetCellRangeToSpreadsheetSelection() {
        this.convertAndCheckSpreadsheetSelection(
                SpreadsheetSelection.parseCellRange("C3:D4")
        );
    }

    @Test
    public void testSpreadsheetCellRangeToString() {
        this.convertAndCheckString(
                SpreadsheetSelection.parseCellRange("A1:B2")
        );
    }

    // SpreadsheetColumnReference.......................................................................................

    @Test
    public void testSpreadsheetColumnReference() {
        this.convertAndCheck(
                SpreadsheetSelection.parseColumn("C"),
                SpreadsheetColumnReference.class
        );
    }

    @Test
    public void testSpreadsheetColumnReferenceToSpreadsheetSelection() {
        this.convertAndCheckSpreadsheetSelection(
                SpreadsheetSelection.parseColumn("C")
        );
    }

    @Test
    public void testSpreadsheetColumnReferenceToString() {
        this.convertAndCheckString(
                SpreadsheetSelection.parseColumn("C")
        );
    }

    // SpreadsheetColumnRangeReference..................................................................................

    @Test
    public void testSpreadsheetColumnRangeReference() {
        this.convertAndCheck(
                SpreadsheetSelection.parseColumnRange("D:E"),
                SpreadsheetColumnRangeReference.class
        );
    }

    @Test
    public void testSpreadsheetColumnRangeReferenceToSpreadsheetSelection() {
        this.convertAndCheckSpreadsheetSelection(
                SpreadsheetSelection.parseColumnRange("D:E")
        );
    }

    @Test
    public void testSpreadsheetColumnRangeReferenceToString() {
        this.convertAndCheckString(
                SpreadsheetSelection.parseColumnRange("D:E")
        );
    }

    // SpreadsheetLabelName............................................................................................

    @Test
    public void testSpreadsheetLabelNameToLabel() {
        this.convertAndCheck(
                SpreadsheetSelection.labelName("Label123"),
                SpreadsheetLabelName.class
        );
    }

    @Test
    public void testSpreadsheetLabelNameToCell() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123Cell");
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("Z999");

        this.convertAndCheck(
                label,
                SpreadsheetCellReference.class,
                this.createContext(label, cell),
                cell
        );
    }

    @Test
    public void testSpreadsheetLabelNameToCellRange() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123Range");
        final SpreadsheetCellRangeReference range = SpreadsheetSelection.parseCellRange("Z9:Z99");

        this.convertAndCheck(
                label,
                SpreadsheetCellRangeReference.class,
                this.createContext(label, range),
                range
        );
    }

    @Test
    public void testSpreadsheetLabelNameToCellOrRangeCell() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123Cell");
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("Z999");

        this.convertAndCheck(
                label,
                SpreadsheetCellReferenceOrRange.class,
                this.createContext(label, cell),
                cell
        );
    }

    @Test
    public void testSpreadsheetLabelNameToCellOrRangeCellRange() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123CellRange");
        final SpreadsheetCellRangeReference range = SpreadsheetSelection.parseCellRange("B2:c3");

        this.convertAndCheck(
                label,
                SpreadsheetCellReferenceOrRange.class,
                this.createContext(label, range),
                range
        );
    }

    // SpreadsheetRowReference.......................................................................................

    @Test
    public void testSpreadsheetRowReference() {
        this.convertAndCheck(
                SpreadsheetSelection.parseRow("5"),
                SpreadsheetRowReference.class
        );
    }

    @Test
    public void testSpreadsheetRowReferenceToSpreadsheetSelection() {
        this.convertAndCheckSpreadsheetSelection(
                SpreadsheetSelection.parseRow("5")
        );
    }

    @Test
    public void testSpreadsheetRowReferenceToString() {
        this.convertAndCheckString(
                SpreadsheetSelection.parseRow("5")
        );
    }

    // SpreadsheetRowRangeReference..................................................................................

    @Test
    public void testSpreadsheetRowRangeReference() {
        this.convertAndCheck(
                SpreadsheetSelection.parseRowRange("6:7"),
                SpreadsheetRowRangeReference.class
        );
    }

    @Test
    public void testSpreadsheetRowRangeReferenceToSpreadsheetSelection() {
        this.convertAndCheckSpreadsheetSelection(
                SpreadsheetSelection.parseRowRange("6:7")
        );
    }

    @Test
    public void testSpreadsheetRowRangeReferenceToString() {
        this.convertAndCheckString(
                SpreadsheetSelection.parseRowRange("6:7")
        );
    }

    private void convertAndCheckSpreadsheetSelection(final SpreadsheetSelection selection) {
        this.convertAndCheck(
                selection,
                SpreadsheetSelection.class,
                selection
        );
    }

    private void convertAndCheckString(final SpreadsheetSelection selection) {
        this.convertAndCheck(
                selection,
                String.class,
                selection.toString()
        );
    }

    // error ...........................................................................................................

    @Test
    public void testSpreadsheetErrorToNumber() {
        assertThrows(
                SpreadsheetErrorException.class,
                () -> this.createConverter()
                        .convert(
                                SpreadsheetErrorKind.ERROR.setMessage("Ignored"),
                                ExpressionNumber.class,
                                this.createContext()
                        )
        );
    }

    @Test
    public void testSpreadsheetErrorToString() {
        assertThrows(
                SpreadsheetErrorException.class,
                () -> this.convert(
                        SpreadsheetErrorKind.DIV0.setMessage("Message is ignored"),
                        String.class
                )
        );
    }

    // boolean..........................................................................................................

    @Test
    public void testBooleanTrueBoolean() {
        this.convertAndCheck(true);
    }

    @Test
    public void testBooleanFalseBoolean() {
        this.convertAndCheck(false);
    }

    @Test
    public void testBooleanTrueDate() {
        this.convertAndBackCheck(true, DATE_TRUE);
    }

    @Test
    public void testBooleanFalseDate() {
        this.convertAndBackCheck(false, DATE_FALSE);
    }

    @Test
    public void testBooleanTrueDateTime() {
        this.convertAndBackCheck(true, DATE_TIME_TRUE);
    }

    @Test
    public void testBooleanFalseDateTime() {
        this.convertAndBackCheck(false, DATE_TIME_FALSE);
    }

    @Test
    public void testBooleanTrueNumber() {
        this.convertNumberAndCheck(true, NUMBER_TRUE);
    }

    @Test
    public void testBooleanFalseNumber() {
        this.convertNumberAndCheck(false, NUMBER_FALSE);
    }

    @Test
    public void testBooleanTrueString() {
        this.convertAndCheck(true, STRING_TRUE + TEXT_SUFFIX);
    }

    @Test
    public void testBooleanFalseString() {
        this.convertAndCheck(false, false + TEXT_SUFFIX);
    }

    @Test
    public void testBooleanTrueTime() {
        this.convertAndBackCheck(true, TIME_TRUE);
    }

    @Test
    public void testBooleanFalseTime() {
        this.convertAndBackCheck(false, TIME_FALSE);
    }

    // Character........................................................................................................

    @Test
    public void testCharacterToString() {
        this.convertAndCheck('A', String.class, "A");
    }

    // Date.............................................................................................................

    @Test
    public void testNullBoolean() {
        this.convertAndCheck(null, Boolean.class, null);
    }

    @Test
    public void testNullDate() {
        this.convertAndCheck(null, LocalDate.class, DATE_FALSE);
    }

    @Test
    public void testNullDateTime() {
        this.convertAndCheck(null, LocalDateTime.class, LocalDateTime.of(DATE_FALSE, TIME_FALSE));
    }

    @Test
    public void testNullTime() {
        this.convertAndCheck(null, LocalTime.class, TIME_FALSE);
    }

    @Test
    public void testNullExpressionNumber() {
        this.convertAndCheck(null, ExpressionNumber.class, null);
    }

    @Test
    public void testNullNumber() {
        this.convertAndCheck(null, Number.class, null);
    }

    @Test
    public void testNullString() {
        this.convertAndCheck(null, String.class, "falsetext-literal-123");
    }

    // character.......................................................................................................

    @Test
    public void testCharacterToSpreadsheetColumnReference() {
        final SpreadsheetColumnReference column = SpreadsheetSelection.parseColumn("X");

        this.convertAndCheck(
                'X',
                column
        );
    }

    @Test
    public void testCharacterToSpreadsheetColumnRangeReference() {
        final SpreadsheetColumnRangeReference range = SpreadsheetSelection.parseColumnRange("X");

        this.convertAndCheck(
                'X',
                range
        );
    }

    @Test
    public void testCharacterToSpreadsheetRowReference() {
        final SpreadsheetRowReference row = SpreadsheetSelection.parseRow("1");

        this.convertAndCheck(
                '1',
                row
        );
    }

    @Test
    public void testCharacterToSpreadsheetRowRangeReference() {
        final SpreadsheetRowRangeReference range = SpreadsheetSelection.parseRowRange("1");

        this.convertAndCheck(
                '1',
                range
        );
    }

    // date............................................................................................................

    @Test
    public void testDateTrueBoolean() {
        this.convertAndCheck(DATE_TRUE, true);
    }

    @Test
    public void testDateFalseBoolean() {
        this.convertAndCheck(DATE_FALSE, false);
    }

    @Test
    public void testDateDate() {
        this.convertAndCheck(LocalDate.of(2000, 12, 31));
    }

    @Test
    public void testDateTrueDateTime() {
        this.convertAndBackCheck(DATE_TRUE, LocalDateTime.of(DATE_TRUE, LocalTime.MIDNIGHT));
    }

    @Test
    public void testDateFalseDateTime() {
        this.convertAndBackCheck(DATE_FALSE, LocalDateTime.of(DATE_FALSE, LocalTime.MIDNIGHT));
    }

    @Test
    public void testDateTrueNumber() {
        this.convertNumberAndCheck(DATE_TRUE, NUMBER_TRUE);
    }

    @Test
    public void testDateFalseNumber() {
        this.convertNumberAndCheck(DATE_FALSE, NUMBER_FALSE);
    }

    @Test
    public void testDateNumber() {
        final int value = 100;
        this.convertNumberAndCheck(LocalDate.ofEpochDay(value), value);
    }

    @Test
    public void testDateTrueString() {
        this.convertAndCheck(DATE, "D 2000-12-31");
    }

    @Test
    public void testDateTime() {
        this.convertFails(DATE, LocalTime.class);
    }

    // DateTime.........................................................................................................

    @Test
    public void testDateTimeTrueBoolean() {
        this.convertAndCheck(DATE_TIME_TRUE, true);
    }

    @Test
    public void testDateTimeFalseBoolean() {
        this.convertAndCheck(DATE_TIME_FALSE, false);
    }

    @Test
    public void testDateTimeDate() {
        this.convertAndCheck(DATE_TIME, DATE);
    }

    @Test
    public void testDateTimeDateTime() {
        this.convertAndCheck(DATE_TIME);
    }

    @Test
    public void testDateTimeTrueNumber() {
        this.convertNumberAndCheck(DATE_TIME_TRUE, NUMBER_TRUE);
    }

    @Test
    public void testDateTimeFalseNumber() {
        this.convertNumberAndCheck(DATE_TIME_FALSE, NUMBER_FALSE);
    }

    @Test
    public void testDateTimeNumber() {
        final int value = 100;
        this.convertNumberAndCheck(LocalDateTime.of(LocalDate.ofEpochDay(value), LocalTime.MIDNIGHT), value);
    }

    @Test
    public void testDateTimeTrueString() {
        this.convertAndCheck(DATE_TIME, "DT 2000-12-31 12-58");
    }

    @Test
    public void testDateTimeTime() {
        this.convertAndCheck(DATE_TIME, TIME);
    }

    // Number...........................................................................................................

    @Test
    public void testNumberTrueBoolean() {
        this.convertNumberAndCheck(1, true);
    }

    @Test
    public void testNumberFalseBoolean() {
        this.convertNumberAndCheck(0, false);
    }

    @Test
    public void testNumberTrueDate() {
        this.convertAndBackCheck(1, DATE_TRUE);
    }

    @Test
    public void testNumberFalseDate() {
        this.convertAndBackCheck(0, DATE_FALSE);
    }

    @Test
    public void testNumberTrueDateTime() {
        this.convertAndBackCheck(1, DATE_TIME_TRUE);
    }

    @Test
    public void testNumberFalseDateTime() {
        this.convertAndBackCheck(0, DATE_TIME_FALSE);
    }

    @Test
    public void testNumberByte() {
        this.convertNumberAndCheck((byte) 123);
    }

    @Test
    public void testNumberShort() {
        this.convertNumberAndCheck((short) 123);
    }

    @Test
    public void testNumberInteger() {
        this.convertNumberAndCheck(123);
    }

    @Test
    public void testNumberLong() {
        this.convertNumberAndCheck(123L);
    }

    @Test
    public void testNumberFloat() {
        this.convertNumberAndCheck(123f);
    }

    @Test
    public void testNumberDouble() {
        this.convertNumberAndCheck(123.0);
    }

    @Test
    public void testNumberBigDecimal() {
        this.convertNumberAndCheck(BigDecimal.valueOf(123));
    }

    @Test
    public void testNumberBigInteger() {
        this.convertNumberAndCheck(BigInteger.valueOf(123));
    }

    @Test
    public void testNumberCharacter() {
        this.convertAndCheck(
                GeneralSpreadsheetConverter.with(
                        dateFormatter(),
                        dateParser(),
                        dateTimeFormatter(),
                        dateTimeParser(),
                        formatter(
                                "#",
                                SpreadsheetFormatParsers.numberParse(),
                                SpreadsheetFormatNumberParserToken.class,
                                SpreadsheetFormatters::number
                        ),
                        numberParser(),
                        textFormatter(),
                        timeFormatter(),
                        timeParser(),
                        DATE_OFFSET
                ),
                ExpressionNumberKind.DEFAULT.create(1),
                Character.class,
                '1'
        );
    }

    @Test
    public void testNumberTrueString() {
        this.convertAndCheck(12.5, "N 12D5");
    }

    @Test
    public void testNumberFalseString() {
        this.convertAndCheck(false, TIME_FALSE);
    }

    @Test
    public void testNumberTime() {
        this.convertAndBackCheck(Converters.localTimeNumber()
                        .convertOrFail(TIME, BigDecimal.class, this.createContext()),
                TIME);
    }

    private void convertNumberAndCheck(final Number value) {
        this.convertNumberAndCheck(value, value);
    }

    private void convertNumberAndCheck(final Number value,
                                       final Object expected) {
        final Converter<ConverterContext> numberNumber = Converters.numberNumber();
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
    public void testCharacterString() {
        this.convertAndCheck(
                'A',
                "A"
        );
    }

    @Test
    public void testStringBooleanFalse() {
        this.convertAndCheck(
                STRING_FALSE,
                false
        );
    }

    @Test
    public void testStringBooleanTrue() {
        this.convertAndCheck(
                STRING_TRUE,
                true
        );
    }

    @Test
    public void testStringCharacter() {
        this.convertAndCheck(
                "A",
                'A'
        );
    }

    @Test
    public void testStringDate() {
        this.convertAndCheck(
                "D 2000-12-31",
                DATE
        );
    }

    // "\"DT\" dd mm yyyy hh mm ss"

    @Test
    public void testStringDateTime() {
        this.convertAndCheck(
                "DT 31 12 2000 12 58 59",
                DATE_TIME
        );
    }

    @Test
    public void testStringNumber() {
        this.convertAndCheck(
                "N 123",
                EXPRESSION_NUMBER_KIND.create(123)
        );
    }

    @Test
    public void testStringToSpreadsheetCellReference() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("Z99");

        this.convertAndCheck(
                cell.toString(),
                cell
        );
    }

    @Test
    public void testStringLabelToSpreadsheetCellReference() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("Z99");

        this.convertAndCheck(
                cell.toString(),
                SpreadsheetCellReference.class,
                this.createContext(label, cell),
                cell
        );
    }

    @Test
    public void testStringToSpreadsheetCellRange() {
        final SpreadsheetCellRangeReference range = SpreadsheetSelection.parseCellRange("Z99:Z100");

        this.convertAndCheck(
                range.toString(),
                range
        );
    }

    @Test
    public void testStringLabelToSpreadsheetCellRange() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");
        final SpreadsheetCellRangeReference range = SpreadsheetSelection.parseCellRange("Z9:Z99");

        this.convertAndCheck(
                range.toString(),
                SpreadsheetCellRangeReference.class,
                this.createContext(label, range),
                range
        );
    }

    @Test
    public void testStringToSpreadsheetColumnReference() {
        final SpreadsheetColumnReference column = SpreadsheetSelection.parseColumn("Z");

        this.convertAndCheck(
                column.toString(),
                column
        );
    }

    @Test
    public void testStringToSpreadsheetColumnRangeReference() {
        final SpreadsheetColumnRangeReference range = SpreadsheetSelection.parseColumnRange("X:Y");

        this.convertAndCheck(
                range.toString(),
                range
        );
    }

    @Test
    public void testStringToSpreadsheetLabelName() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");

        this.convertAndCheck(
                label.toString(),
                label
        );
    }

    @Test
    public void testStringToSpreadsheetRowReference() {
        final SpreadsheetRowReference row = SpreadsheetSelection.parseRow("123");

        this.convertAndCheck(
                row.toString(),
                row
        );
    }

    @Test
    public void testStringToSpreadsheetRowRangeReference() {
        final SpreadsheetRowRangeReference range = SpreadsheetSelection.parseRowRange("123:456");

        this.convertAndCheck(
                range.toString(),
                range
        );
    }

    @Test
    public void testStringString() {
        final String text = "abc123";
        this.convertAndCheck(text, text);
    }

    @Test
    public void testStringTime() {
        this.convertAndCheck(
                "T 12 58 59",
                TIME
        );
    }

    // Time.............................................................................................................

    @Test
    public void testTimeTrueBoolean() {
        this.convertAndCheck(TIME_TRUE, true);
    }

    @Test
    public void testTimeFalseBoolean() {
        this.convertAndCheck(TIME_FALSE, false);
    }

    @Test
    public void testTimeDate() {
        this.convertFails(TIME, LocalDate.class);
    }

    @Test
    public void testTimeDateTime() {
        this.convertAndCheck(TIME, LocalDateTime.of(DATE_FALSE, TIME));
    }

    @Test
    public void testTimeTrueNumber() {
        this.convertNumberAndCheck(TIME_TRUE, NUMBER_TRUE);
    }

    @Test
    public void testTimeFalseNumber() {
        this.convertNumberAndCheck(TIME_FALSE, NUMBER_FALSE);
    }

    @Test
    public void testTimeNumber() {
        final int value = 123;
        this.convertNumberAndCheck(LocalTime.ofSecondOfDay(value), value);
    }

    @Test
    public void testTimeTrueString() {
        this.convertAndCheck(DATE, "D 2000-12-31");
    }

    @Test
    public void testTimeTime() {
        this.convertAndCheck(TIME);
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createConverter(), GeneralSpreadsheetConverter.class.getSimpleName());
    }

    // ConverterTesting.................................................................................................

    @Override
    public GeneralSpreadsheetConverter createConverter() {
        return GeneralSpreadsheetConverter.with(
                dateFormatter(),
                dateParser(),
                dateTimeFormatter(),
                dateTimeParser(),
                numberFormatter(),
                numberParser(),
                textFormatter(),
                timeFormatter(),
                timeParser(),
                DATE_OFFSET
        );
    }

    private SpreadsheetFormatter dateFormatter() {
        return dateTimeFormatter(
                "\\D yyyy-mm-dd",
                LocalDate.class
        );
    }

    private SpreadsheetDateParsePattern dateParser() {
        return SpreadsheetParsePattern.parseDateParsePattern("\\D yyyy-mm-dd");
    }

    private SpreadsheetFormatter dateTimeFormatter() {
        return dateTimeFormatter(
                "\"DT\" yyyy-mm-dd hh-mm",
                LocalDateTime.class
        ); // dateTimeFormatter
    }

    private SpreadsheetDateTimeParsePattern dateTimeParser() {
        return SpreadsheetParsePattern.parseDateTimeParsePattern("\"DT\" dd mm yyyy hh mm ss");
    }

    private SpreadsheetFormatter numberFormatter() {
        return formatter("\\N #.#",
                SpreadsheetFormatParsers.numberParse(),
                SpreadsheetFormatNumberParserToken.class,
                SpreadsheetFormatters::number
        );
    }

    private SpreadsheetNumberParsePattern numberParser() {
        return SpreadsheetParsePattern.parseNumberParsePattern("\"N\" #;\"N\" #.#");
    }

    private SpreadsheetFormatter textFormatter() {
        return formatter(
                "@\"" + TEXT_SUFFIX + "\"",
                SpreadsheetFormatParsers.textFormat(),
                SpreadsheetFormatTextParserToken.class,
                SpreadsheetFormatters::text
        );
    }

    private final static String TEXT_SUFFIX = "text-literal-123";

    private SpreadsheetFormatter timeFormatter() {
        return dateTimeFormatter(
                "\\T hh-mm",
                LocalTime.class
        );
    }

    private SpreadsheetTimeParsePattern timeParser() {
        return SpreadsheetParsePattern.parseTimeParsePattern("\\T hh mm ss");
    }

    private SpreadsheetFormatter dateTimeFormatter(final String pattern,
                                                   final Class<?> type) {
        return formatter(
                pattern,
                SpreadsheetFormatParsers.dateTimeFormat(),
                SpreadsheetFormatDateTimeParserToken.class,
                (t) -> SpreadsheetFormatters.dateTime(t, type::isInstance)
        );
    }

    private <T extends SpreadsheetFormatParserToken> SpreadsheetFormatter formatter(final String pattern,
                                                                                    final Parser<SpreadsheetFormatParserContext> parser,
                                                                                    final Class<T> token,
                                                                                    final Function<T, SpreadsheetFormatter> formatterFactory) {
        return parser.orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(pattern), SpreadsheetFormatParserContexts.basic())
                .map(t -> t.cast(SequenceParserToken.class).value().get(0).cast(token))
                .map(formatterFactory)
                .orElse(SpreadsheetFormatters.fake()); // orElse wont happen.
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return this.createContext(LABEL_NAME_RESOLVER);
    }

    private SpreadsheetConverterContext createContext(final SpreadsheetLabelName label,
                                                      final SpreadsheetSelection selection) {
        return this.createContext(
                (s) -> {
                    this.checkEquals(label, s, "label");
                    return selection;
                }
        );
    }

    private SpreadsheetConverterContext createContext(final SpreadsheetLabelNameResolver labelNameResolver) {
        return SpreadsheetConverterContexts.basic(
                SpreadsheetConverters.basic(),
                labelNameResolver,
                ExpressionNumberConverterContexts.basic(
                        Converters.fake(),
                        ConverterContexts.basic(
                                Converters.fake(),
                                DateTimeContexts.locale(
                                        Locale.ENGLISH,
                                        1900,
                                        20,
                                        LocalDateTime::now
                                ),
                                DecimalNumberContexts.basic("C",
                                        'D',
                                        "E",
                                        'G',
                                        'M',
                                        'P',
                                        'L',
                                        Locale.ENGLISH,
                                        MathContext.DECIMAL32)),
                        EXPRESSION_NUMBER_KIND
                )
        );
    }

    private void convertAndBackCheck(final Object value,
                                     final Object expected) {
        this.convertAndCheck(value, expected);
        this.convertAndCheck(expected, value);
    }

    private void convertNumberAndCheck(final Object value,
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
    public Class<GeneralSpreadsheetConverter> type() {
        return GeneralSpreadsheetConverter.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNameSuffix() {
        return "";
    }
}

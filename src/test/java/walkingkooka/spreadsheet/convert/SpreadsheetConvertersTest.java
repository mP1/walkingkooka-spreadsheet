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
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.ConverterTesting;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.PublicStaticHelperTesting;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContexts;

import java.lang.reflect.Method;
import java.math.MathContext;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

public final class SpreadsheetConvertersTest implements ClassTesting2<SpreadsheetConverters>,
    PublicStaticHelperTesting<SpreadsheetConverters>,
    ConverterTesting {

    // date.............................................................................................................

    @Test
    public void testTextToDateConvertInvalidStringToDateFails() {
        this.convertFails(
            SpreadsheetConverters.textToDate(
                SpreadsheetPattern.parseDateParsePattern("yyyy/mm/dd")
                    .parser()
            ),
            "1999/12", // missing day
            LocalDate.class,
            this.dateTimeSpreadsheetConverterContext()
        );
    }

    @Test
    public void testTextToDateConvertStringToDate() {
        this.convertAndCheck(
            SpreadsheetConverters.textToDate(
                SpreadsheetPattern.parseDateParsePattern("yyyy/mm/dd")
                    .parser()
            ),
            "1999/12/31",
            LocalDate.class,
            this.dateTimeSpreadsheetConverterContext(),
            LocalDate.of(1999, 12, 31)
        );
    }

    // dateTime.............................................................................................................

    @Test
    public void testTextToDateTimeConvertFails() {
        this.convertFails(
            SpreadsheetConverters.textToDateTime(
                SpreadsheetPattern.parseDateTimeParsePattern("yyyy/mm/dd hh:mm")
                    .parser()
            ),
            "1999/12", // missing day
            LocalDateTime.class,
            this.dateTimeSpreadsheetConverterContext()
        );
    }

    @Test
    public void testTextToDateTimeConvertStringToDateTime() {
        this.convertAndCheck(
            SpreadsheetConverters.textToDateTime(
                SpreadsheetPattern.parseDateTimeParsePattern("yyyy/mm/dd hh:mm")
                    .parser()
            ),
            "1999/12/31 12:59",
            LocalDateTime.class,
            this.dateTimeSpreadsheetConverterContext(),
            LocalDateTime.of(1999, 12, 31, 12, 59)
        );
    }

    // time.............................................................................................................

    @Test
    public void testTextToTimeConvertInvalidStringToTimeFails() {
        this.convertFails(
            SpreadsheetConverters.textToTime(
                SpreadsheetPattern.parseTimeParsePattern("hh:mm")
                    .parser()
            ),
            "12:", // missing minutes
            LocalTime.class,
            this.dateTimeSpreadsheetConverterContext()
        );
    }

    @Test
    public void testTextToTimeConvertStringToTime() {
        this.convertAndCheck(
            SpreadsheetConverters.textToTime(
                SpreadsheetPattern.parseTimeParsePattern("hh:mm")
                    .parser()
            ),
            "12:59",
            LocalTime.class,
            this.dateTimeSpreadsheetConverterContext(),
            LocalTime.of(12, 59)
        );
    }

    private SpreadsheetConverterContext dateTimeSpreadsheetConverterContext() {
        final Locale locale = Locale.forLanguageTag("EN-AU");

        return SpreadsheetConverterContexts.basic(
            SpreadsheetConverterContexts.NO_METADATA,
            SpreadsheetConverterContexts.NO_VALIDATION_REFERENCE,
            SpreadsheetConverters.textToText(), // not used
            SpreadsheetLabelNameResolvers.fake(), // not required
            JsonNodeConverterContexts.basic(
                ExpressionNumberConverterContexts.basic(
                    Converters.fake(), // not used
                    ConverterContexts.basic(
                        Converters.JAVA_EPOCH_OFFSET, // dateOffset
                        Converters.fake(),
                        DateTimeContexts.basic(
                            DateTimeSymbols.fromDateFormatSymbols(
                                new DateFormatSymbols(locale)
                            ),
                            locale,
                            1950,
                            50,
                            () -> {
                                throw new UnsupportedOperationException("now() not supported");
                            }
                        ),
                        DecimalNumberContexts.fake()
                    ),
                    ExpressionNumberKind.BIG_DECIMAL
                ),
                JsonNodeMarshallUnmarshallContexts.fake()
            )
        );
    }

    // textToNumber.....................................................................................................

    @Test
    public void testTextToNumberConvertInvalidStringPatternToExpressionNumberFails() {
        this.convertFails(
            SpreadsheetConverters.textToNumber(
                SpreadsheetPattern.parseNumberParsePattern("0.00")
                    .parser()
            ),
            "1",
            ExpressionNumber.class,
            this.spreadsheetConverterContext(ExpressionNumberKind.BIG_DECIMAL)
        );
    }

    @Test
    public void testTextToNumberConvertCharSequenceToExpressionNumberBigDecimalConvert() {
        this.textToNumberConvertCharSequenceToExpressionNumberAndCheck(
            ExpressionNumberKind.BIG_DECIMAL
        );
    }

    @Test
    public void testTextToNumberConvertCharSequenceToExpressionNumberDoubleConvert() {
        this.textToNumberConvertCharSequenceToExpressionNumberAndCheck(
            ExpressionNumberKind.DOUBLE
        );
    }

    private void textToNumberConvertCharSequenceToExpressionNumberAndCheck(final ExpressionNumberKind kind) {
        this.convertAndCheck(
            SpreadsheetConverters.textToNumber(
                SpreadsheetPattern.parseNumberParsePattern("0.00")
                    .parser()
            ),
            new StringBuilder("1.25"),
            ExpressionNumber.class,
            this.spreadsheetConverterContext(kind),
            kind.create(1.25)
        );
    }

    @Test
    public void testTextToNumberConvertTextToExpressionNumberBigDecimalConvert() {
        this.textToNumberConvertTextToExpressionNumberAndCheck(
            ExpressionNumberKind.BIG_DECIMAL
        );
    }

    @Test
    public void testTextToNumberConvertTextToExpressionNumberDoubleConvert() {
        this.textToNumberConvertTextToExpressionNumberAndCheck(
            ExpressionNumberKind.DOUBLE
        );
    }

    private void textToNumberConvertTextToExpressionNumberAndCheck(final ExpressionNumberKind kind) {
        this.convertAndCheck(
            SpreadsheetConverters.textToNumber(
                SpreadsheetPattern.parseNumberParsePattern("0.00")
                    .parser()
            ),
            "1.25",
            ExpressionNumber.class,
            this.spreadsheetConverterContext(kind),
            kind.create(1.25)
        );
    }

    @Test
    public void testTextToNumberConvertStringToInteger() {
        this.convertAndCheck(
            SpreadsheetConverters.textToNumber(
                SpreadsheetPattern.parseNumberParsePattern("000")
                    .parser()
            ),
            "123",
            Integer.class,
            this.spreadsheetConverterContext(ExpressionNumberKind.BIG_DECIMAL),
            123
        );
    }

    private SpreadsheetConverterContext spreadsheetConverterContext(final ExpressionNumberKind kind) {
        return SpreadsheetConverterContexts.basic(
            SpreadsheetConverterContexts.NO_METADATA,
            SpreadsheetConverterContexts.NO_VALIDATION_REFERENCE,
            SpreadsheetConverters.textToText(), // not used
            SpreadsheetLabelNameResolvers.fake(), // not required
            JsonNodeConverterContexts.basic(
                ExpressionNumberConverterContexts.basic(
                    Converters.fake(), // not used
                    ConverterContexts.basic(
                        Converters.JAVA_EPOCH_OFFSET, // dateOffset
                        Converters.fake(),
                        DateTimeContexts.fake(), // unused only doing numbers
                        DecimalNumberContexts.american(MathContext.DECIMAL32)
                    ),
                    kind
                ),
                JsonNodeMarshallUnmarshallContexts.fake()
            )
        );
    }

    // PublicStaticHelperTesting........................................................................................

    @Test
    public void testPublicStaticMethodsWithoutMathContextParameter() {
        this.publicStaticMethodParametersTypeCheck(MathContext.class);
    }

    @Override
    public Class<SpreadsheetConverters> type() {
        return SpreadsheetConverters.class;
    }

    @Override
    public boolean canHavePublicTypes(final Method method) {
        return false;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}

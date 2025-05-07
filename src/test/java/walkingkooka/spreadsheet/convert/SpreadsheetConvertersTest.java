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
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;

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
    public void testStringToDateConvertFails() {
        this.convertFails(
                SpreadsheetConverters.stringToDate(
                        SpreadsheetPattern.parseDateParsePattern("yyyy/mm/dd")
                                .parser()
                ),
                "1999/12", // missing day
                LocalDate.class,
                this.dateTimeSpreadsheetConverterContext()
        );
    }

    @Test
    public void testStringToDateConvert() {
        this.convertAndCheck(
                SpreadsheetConverters.stringToDate(
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
    public void testStringToDateTimeConvertFails() {
        this.convertFails(
                SpreadsheetConverters.stringToDateTime(
                        SpreadsheetPattern.parseDateTimeParsePattern("yyyy/mm/dd hh:mm")
                                .parser()
                ),
                "1999/12", // missing day
                LocalDateTime.class,
                this.dateTimeSpreadsheetConverterContext()
        );
    }

    @Test
    public void testStringToDateTimeConvert() {
        this.convertAndCheck(
                SpreadsheetConverters.stringToDateTime(
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
    public void testStringToTimeConvertFails() {
        this.convertFails(
                SpreadsheetConverters.stringToTime(
                        SpreadsheetPattern.parseTimeParsePattern("hh:mm")
                                .parser()
                ),
                "12:", // missing minutes
                LocalTime.class,
                this.dateTimeSpreadsheetConverterContext()
        );
    }

    @Test
    public void testStringToTimeConvert() {
        this.convertAndCheck(
                SpreadsheetConverters.stringToTime(
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
        return SpreadsheetConverterContexts.basic(
                Converters.fake(), // not used
                SpreadsheetLabelNameResolvers.fake(), // not required
                ExpressionNumberConverterContexts.basic(
                        Converters.fake(), // not used
                        ConverterContexts.basic(
                                Converters.JAVA_EPOCH_OFFSET, // dateOffset
                                Converters.fake(),
                                DateTimeContexts.basic(
                                        DateTimeSymbols.fromDateFormatSymbols(
                                                new DateFormatSymbols(Locale.forLanguageTag("EN-AU"))
                                        ),
                                        Locale.forLanguageTag("EN-AU"),
                                        1950,
                                        50,
                                        () -> {
                                            throw new UnsupportedOperationException("now() not supported");
                                        }
                                ),
                                DecimalNumberContexts.fake()
                        ),
                        ExpressionNumberKind.BIG_DECIMAL
                )
        );
    }

    // expressionNumber.................................................................................................

    @Test
    public void testStringToExpressionNumberConvertFails() {
        this.convertFails(
                SpreadsheetConverters.stringToExpressionNumber(
                        SpreadsheetPattern.parseNumberParsePattern("0.00")
                                .parser()
                ),
                "1",
                ExpressionNumber.class,
                this.expressionNumberSpreadsheetConverterContext(ExpressionNumberKind.BIG_DECIMAL)
        );
    }

    @Test
    public void testStringToExpressionNumberBigDecimalConvert() {
        this.convertStringToExpressionNumberAndCheck(
                ExpressionNumberKind.BIG_DECIMAL
        );
    }

    @Test
    public void testStringToExpressionNumberDoubleConvert() {
        this.convertStringToExpressionNumberAndCheck(
                ExpressionNumberKind.DOUBLE
        );
    }

    private void convertStringToExpressionNumberAndCheck(final ExpressionNumberKind kind) {
        this.convertAndCheck(
                SpreadsheetConverters.stringToExpressionNumber(
                        SpreadsheetPattern.parseNumberParsePattern("0.00")
                                .parser()
                ),
                "1.25",
                ExpressionNumber.class,
                this.expressionNumberSpreadsheetConverterContext(kind),
                kind.create(1.25)
        );
    }

    @Test
    public void testExpressionNumberConvertIntegerFails() {
        this.convertFails(
                SpreadsheetConverters.stringToExpressionNumber(
                        SpreadsheetPattern.parseNumberParsePattern("000")
                                .parser()
                ),
                "123",
                Integer.class,
                this.expressionNumberSpreadsheetConverterContext(ExpressionNumberKind.BIG_DECIMAL)
        );
    }

    private SpreadsheetConverterContext expressionNumberSpreadsheetConverterContext(final ExpressionNumberKind kind) {
        return SpreadsheetConverterContexts.basic(
                Converters.fake(), // not used
                SpreadsheetLabelNameResolvers.fake(), // not required
                ExpressionNumberConverterContexts.basic(
                        Converters.fake(), // not used
                        ConverterContexts.basic(
                                Converters.JAVA_EPOCH_OFFSET, // dateOffset
                                Converters.fake(),
                                DateTimeContexts.fake(), // unused only doing numbers
                                DecimalNumberContexts.american(MathContext.DECIMAL32)
                        ),
                        kind
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

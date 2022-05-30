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
import walkingkooka.Cast;
import walkingkooka.ToStringTesting;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.ConverterTesting2;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

public final class UnformattedNumberSpreadsheetConverterTest implements ConverterTesting2<UnformattedNumberSpreadsheetConverter<ExpressionNumberConverterContext>, ExpressionNumberConverterContext>,
        ToStringTesting<UnformattedNumberSpreadsheetConverter<ExpressionNumberConverterContext>> {

    @Test
    public void testConvertToNumberFails() {
        this.convertFails(
                "123",
                Number.class
        );
    }

    @Test
    public void testBooleanTrue() {
        this.convertToStringAndCheck(
                true,
                "true"
        );
    }

    @Test
    public void testBooleanFalse() {
        this.convertToStringAndCheck(
                false,
                "false"
        );
    }

    @Test
    public void testBigDecimal() {
        this.convertToStringAndCheck(
                new BigDecimal(1.25),
                "1.25"
        );
    }

    @Test
    public void testBigInteger() {
        this.convertToStringAndCheck(
                BigInteger.valueOf(125),
                "125"
        );
    }

    @Test
    public void testDouble() {
        this.convertToStringAndCheck(
                1.25,
                "1.25"
        );
    }

    @Test
    public void testExpressionNumberBigDecimal() {
        this.convertToStringAndCheck(
                ExpressionNumberKind.BIG_DECIMAL.create(12.5),
                "12.5"
        );
    }

    @Test
    public void testExpressionNumberDouble() {
        this.convertToStringAndCheck(
                ExpressionNumberKind.DOUBLE.create(12.5),
                "12.5"
        );
    }

    @Test
    public void testFloat() {
        this.convertToStringAndCheck(
                123.5f,
                "123.5"
        );
    }

    @Test
    public void testInteger() {
        this.convertToStringAndCheck(
                123,
                "123"
        );
    }

    @Test
    public void testLocalDate() {
        this.convertToStringAndCheck(
                LocalDate.of(1999, 12, 31),
                "date: 31/12/1999"
        );
    }

    @Test
    public void testLocalDateTime() {
        this.convertToStringAndCheck(
                LocalDateTime.of(
                        LocalDate.of(1999, 12, 31),
                        LocalTime.of(12, 58, 59)
                ),
                "datetime: 31/12/1999 12:58:59"
        );
    }

    @Test
    public void testLocalTime() {
        this.convertToStringAndCheck(
                LocalTime.of(12, 58, 59),
                "time: 12:58:59"
        );
    }

    @Test
    public void testLong() {
        this.convertToStringAndCheck(
                123L,
                "123"
        );
    }

    @Test
    public void testString() {
        this.convertAndCheck("Hello");
    }

    private void convertToStringAndCheck(final Object value,
                                         final String expected) {
        this.convertAndCheck(
                value,
                String.class,
                expected
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createConverter(),
                UnformattedNumberSpreadsheetConverter.class.getSimpleName()
        );
    }

    @Override
    public UnformattedNumberSpreadsheetConverter<ExpressionNumberConverterContext> createConverter() {
        return UnformattedNumberSpreadsheetConverter.instance();
    }

    @Override
    public ExpressionNumberConverterContext createContext() {
        final JsonNodeUnmarshallContext context = JsonNodeUnmarshallContexts.basic(
                ExpressionNumberKind.DOUBLE,
                MathContext.UNLIMITED
        );

        SpreadsheetMetadata.EMPTY.toString(); // force JsonNodeUnmarshallContexts register

        final SpreadsheetMetadata metadata = context.unmarshall(
                JsonNode.parse(
                        "{\n" +
                                "  \"date-time-offset\": " + Converters.EXCEL_1900_DATE_SYSTEM_OFFSET + ",\n" +
                                "  \"date-format-pattern\": \"\\\"date:\\\" dd/mm/yyyy\",\n" +
                                "  \"date-parse-patterns\": \"ddmmyyyy\",\n" +
                                "  \"date-time-format-pattern\": \"\\\"datetime:\\\" dd/mm/yyyy hh:mm:ss\",\n" +
                                "  \"date-time-parse-patterns\": \"ddmmyyyyhhmmss\",\n" +
                                "  \"number-format-pattern\": \"\\\"number: \\\" #.##\",\n" +
                                "  \"number-parse-patterns\": \"\\\"number: \\\" #.##\",\n" +
                                "  \"text-format-pattern\": \"\\\"text: \\\" @\",\n" +
                                "  \"time-format-pattern\": \"\\\"time:\\\" hh:mm:ss\",\n" +
                                "  \"time-parse-patterns\": \"hhmmss\"\n" +
                                "}"
                ),
                SpreadsheetMetadata.class
        );

        final Converter<ExpressionNumberConverterContext> converter = metadata.converter();

        return ExpressionNumberConverterContexts.basic(
                converter,
                ConverterContexts.basic(
                        Cast.to(converter),
                        DateTimeContexts.locale(
                                Locale.FRANCE,
                                1900,
                                50,
                                LocalDateTime::now
                        ),
                        DecimalNumberContexts.fake()
                ),
                ExpressionNumberKind.DOUBLE
        );
    }

    @Override
    public Class<UnformattedNumberSpreadsheetConverter<ExpressionNumberConverterContext>> type() {
        return Cast.to(UnformattedNumberSpreadsheetConverter.class);
    }
}

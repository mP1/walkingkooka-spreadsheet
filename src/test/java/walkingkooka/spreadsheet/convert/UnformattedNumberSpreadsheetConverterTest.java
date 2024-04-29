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
import walkingkooka.ToStringTesting;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterTesting2;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class UnformattedNumberSpreadsheetConverterTest implements ConverterTesting2<UnformattedNumberSpreadsheetConverter, SpreadsheetConverterContext>,
        SpreadsheetMetadataTesting,
        ToStringTesting<UnformattedNumberSpreadsheetConverter> {

    private final static SpreadsheetLabelNameResolver LABEL_NAME_RESOLVER = SpreadsheetLabelNameResolvers.fake();

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
                BigDecimal.valueOf(1.25),
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
    public UnformattedNumberSpreadsheetConverter createConverter() {
        return UnformattedNumberSpreadsheetConverter.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        final SpreadsheetMetadata metadata = METADATA_EN_AU.set(
                SpreadsheetMetadataPropertyName.DATE_FORMAT_PATTERN,
                SpreadsheetPattern.parseDateFormatPattern("\"date:\" dd/mm/yyyy")
        ).set(
                SpreadsheetMetadataPropertyName.DATETIME_FORMAT_PATTERN,
                SpreadsheetPattern.parseDateTimeFormatPattern("\"datetime:\" dd/mm/yyyy hh:mm:ss")
        ).set(
                SpreadsheetMetadataPropertyName.TIME_FORMAT_PATTERN,
                SpreadsheetPattern.parseTimeFormatPattern("\"time:\" hh:mm:ss")
        );
        final Converter<SpreadsheetConverterContext> converter = metadata.converter();

        return SpreadsheetConverterContexts.basic(
                converter,
                LABEL_NAME_RESOLVER,
                metadata.converterContext(
                        LocalDateTime::now,
                        LABEL_NAME_RESOLVER
                )
        );
    }

    @Override
    public Class<UnformattedNumberSpreadsheetConverter> type() {
        return UnformattedNumberSpreadsheetConverter.class;
    }
}

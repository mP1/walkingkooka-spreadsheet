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
import walkingkooka.plugin.ProviderContext;
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

public final class SpreadsheetConverterUnformattedNumberTest extends SpreadsheetConverterTestCase<SpreadsheetConverterUnformattedNumber>
    implements SpreadsheetMetadataTesting {

    @Test
    public void testConvertStringToNumberFails() {
        this.convertFails(
            "123",
            Number.class
        );
    }

    @Test
    public void testConvertBooleanTrueToString() {
        this.convertToStringAndCheck(
            true,
            "true"
        );
    }

    @Test
    public void testConvertBooleanFalseToString() {
        this.convertToStringAndCheck(
            false,
            "false"
        );
    }

    @Test
    public void testConvertBigDecimalToString() {
        this.convertToStringAndCheck(
            BigDecimal.valueOf(1.25),
            "1.25"
        );
    }

    @Test
    public void testConvertBigIntegerToString() {
        this.convertToStringAndCheck(
            BigInteger.valueOf(125),
            "125"
        );
    }

    @Test
    public void testConvertDoubleToString() {
        this.convertToStringAndCheck(
            1.25,
            "1.25"
        );
    }

    @Test
    public void testConvertExpressionNumberBigDecimalToString() {
        this.convertToStringAndCheck(
            ExpressionNumberKind.BIG_DECIMAL.create(12.5),
            "12.5"
        );
    }

    @Test
    public void testConvertExpressionNumberDoubleToString() {
        this.convertToStringAndCheck(
            ExpressionNumberKind.DOUBLE.create(12.5),
            "12.5"
        );
    }

    @Test
    public void testConvertFloatToString() {
        this.convertToStringAndCheck(
            123.5f,
            "123.5"
        );
    }

    @Test
    public void testConvertIntegerToString() {
        this.convertToStringAndCheck(
            123,
            "123"
        );
    }

    @Test
    public void testConvertLocalDateToString() {
        this.convertToStringAndCheck(
            LocalDate.of(1999, 12, 31),
            "date: 31/12/1999"
        );
    }

    @Test
    public void testConvertLocalDateTimeToString() {
        this.convertToStringAndCheck(
            LocalDateTime.of(
                LocalDate.of(1999, 12, 31),
                LocalTime.of(12, 58, 59)
            ),
            "datetime: 31/12/1999 12:58:59"
        );
    }

    @Test
    public void testConvertLocalTimeToString() {
        this.convertToStringAndCheck(
            LocalTime.of(12, 58, 59),
            "time: 12:58:59"
        );
    }

    @Test
    public void testConvertLongToString() {
        this.convertToStringAndCheck(
            123L,
            "123"
        );
    }

    @Test
    public void testConvertStringToString() {
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

    @Override
    public SpreadsheetConverterUnformattedNumber createConverter() {
        return SpreadsheetConverterUnformattedNumber.INSTANCE;
    }

    private final static SpreadsheetLabelNameResolver LABEL_NAME_RESOLVER = SpreadsheetLabelNameResolvers.fake();

    @Override
    public SpreadsheetConverterContext createContext() {
        final SpreadsheetMetadata metadata = METADATA_EN_AU.set(
            SpreadsheetMetadataPropertyName.DATE_FORMATTER,
            SpreadsheetPattern.parseDateFormatPattern("\"date:\" dd/mm/yyyy").spreadsheetFormatterSelector()
        ).set(
            SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER,
            SpreadsheetPattern.parseDateTimeFormatPattern("\"datetime:\" dd/mm/yyyy hh:mm:ss").spreadsheetFormatterSelector()
        ).set(
            SpreadsheetMetadataPropertyName.TIME_FORMATTER,
            SpreadsheetPattern.parseTimeFormatPattern("\"time:\" hh:mm:ss").spreadsheetFormatterSelector()
        );

        return metadata.spreadsheetConverterContext(
            SpreadsheetMetadata.NO_CELL,
            SpreadsheetMetadata.NO_VALIDATION_REFERENCE,
            SpreadsheetMetadataPropertyName.FORMULA_CONVERTER,
            LABEL_NAME_RESOLVER,
            SpreadsheetConvertersConverterProviders.spreadsheetConverters(
                (ProviderContext p) -> metadata.dateTimeConverter(
                    SPREADSHEET_FORMATTER_PROVIDER,
                    SPREADSHEET_PARSER_PROVIDER,
                    p
                ),
                (ProviderContext p) -> metadata.generalConverter(
                    SPREADSHEET_FORMATTER_PROVIDER,
                    SPREADSHEET_PARSER_PROVIDER,
                    p
                )
            ),
            LOCALE_CONTEXT,
            PROVIDER_CONTEXT
        );
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createConverter(),
            SpreadsheetConverterUnformattedNumber.class.getSimpleName()
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterUnformattedNumber> type() {
        return SpreadsheetConverterUnformattedNumber.class;
    }
}

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

package walkingkooka.spreadsheet.format;

import walkingkooka.Cast;
import walkingkooka.convert.ConversionException;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * A {@link SpreadsheetTextFormatter} that formats any basic spreadsheet value. Any {@link java.time.LocalDate},
 * {@link java.time.LocalDateTime} and {@link java.time.LocalTime} values are first converted to {@link BigDecimal}.
 * {@link String Text} is returned verbatim, and {@link Number numbers} are formatted using a {@link java.text.DecimalFormat formatter}.
 */
final class GeneralSpreadsheetTextFormatter extends SpreadsheetTextFormatter2 {

    /**
     * The {@link GeneralSpreadsheetTextFormatter} singleton instance.
     */
    static final GeneralSpreadsheetTextFormatter INSTANCE = new GeneralSpreadsheetTextFormatter();

    /**
     * Private ctor use factory
     */
    private GeneralSpreadsheetTextFormatter() {
        super();
    }

    @Override
    public boolean canFormat(final Object value) {
        return this.isSpreadsheetValue(value);
    }

    @Override
    Optional<SpreadsheetFormattedText> format0(final Object value, final SpreadsheetTextFormatContext context) {
        return value instanceof String ?
                formatText(Cast.to(value)) :
                formatNonText(value, context);
    }

    private Optional<SpreadsheetFormattedText> formatText(final String value) {
        return Optional.of(SpreadsheetFormattedText.with(SpreadsheetFormattedText.WITHOUT_COLOR, value));
    }

    private Optional<SpreadsheetFormattedText> formatNonText(final Object value, final SpreadsheetTextFormatContext context) {
        final BigDecimal bigDecimal = this.toBigDecimal(value, context);
        final String text = this.decimalFormatConverter(context)
                .convert(bigDecimal, String.class, ConverterContexts.basic(context));
        return Optional.of(SpreadsheetFormattedText.with(SpreadsheetFormattedText.WITHOUT_COLOR, text));
    }

    private BigDecimal toBigDecimal(final Object value, final SpreadsheetTextFormatContext context) throws ConversionException {
        return context.convert(value, BigDecimal.class);
    }

    private Converter decimalFormatConverter(final SpreadsheetTextFormatContext context) {
        return Converters.decimalFormatString(context.generalDecimalFormatPattern());
    }

    @Override
    public String toString() {
        return "General";
    }
}

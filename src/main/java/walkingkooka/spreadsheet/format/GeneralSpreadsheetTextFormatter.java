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
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetTextFormatter} that formats any number using a {@link DecimalFormat}.
 */
final class GeneralSpreadsheetTextFormatter implements SpreadsheetTextFormatter<Object> {

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
    public Class<Object> type() {
        return Object.class;
    }

    @Override
    public Optional<SpreadsheetFormattedText> format(final Object value, final SpreadsheetTextFormatContext context) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(context, "context");

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

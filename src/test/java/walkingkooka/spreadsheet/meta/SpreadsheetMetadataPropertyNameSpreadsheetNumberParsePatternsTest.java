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

package walkingkooka.spreadsheet.meta;

import org.junit.jupiter.api.Test;
import walkingkooka.Either;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.FakeConverter;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetNumberFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetNumberParsePatterns;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetMetadataPropertyNameSpreadsheetNumberParsePatternsTest extends SpreadsheetMetadataPropertyNameTestCase<SpreadsheetMetadataPropertyNameSpreadsheetNumberParsePatterns, SpreadsheetNumberParsePatterns> {

    @Test
    public void testExtractLocaleValue() throws ParseException{
        this.extractLocaleValueAndCheck("1.25");
    }

    @Test
    public void testExtractLocaleValueInteger() throws ParseException{
        this.extractLocaleValueAndCheck("789");
    }

    private void extractLocaleValueAndCheck(final String text) throws ParseException {
        final Locale locale = Locale.ENGLISH;
        final SpreadsheetNumberParsePatterns pattern = SpreadsheetMetadataPropertyNameSpreadsheetNumberParsePatterns.instance()
                .extractLocaleValue(locale)
                .get();

        final BigDecimal value = pattern.converter()
                .convertOrFail(text, BigDecimal.class, ConverterContexts.basic(DateTimeContexts.locale(Locale.ENGLISH, 20), DecimalNumberContexts.american(MathContext.DECIMAL32)));

        final DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance(locale);
        decimalFormat.setParseBigDecimal(true);
        final Number expected = decimalFormat.parse(text);

        assertEquals(expected, value, () -> pattern + "\nDecimalFormat: " + decimalFormat.toPattern());
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetMetadataPropertyNameSpreadsheetNumberParsePatterns.instance(), "number-parse-patterns");
    }

    @Override
    SpreadsheetMetadataPropertyNameSpreadsheetNumberParsePatterns createName() {
        return SpreadsheetMetadataPropertyNameSpreadsheetNumberParsePatterns.instance();
    }

    @Override
    SpreadsheetNumberParsePatterns propertyValue() {
        return SpreadsheetNumberParsePatterns.parseNumberParsePatterns("#.## \"pattern-1\";#.00 \"pattern-2\"");
    }

    @Override
    String propertyValueType() {
        return "Number parse patterns";
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameSpreadsheetNumberParsePatterns> type() {
        return SpreadsheetMetadataPropertyNameSpreadsheetNumberParsePatterns.class;
    }
}

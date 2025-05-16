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
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContexts;
import walkingkooka.spreadsheet.convert.SpreadsheetConverters;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetNumberParsePattern;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;

import java.math.MathContext;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Locale;

public final class SpreadsheetMetadataPropertyNameSpreadsheetParserNumberTest extends SpreadsheetMetadataPropertyNameSpreadsheetParserTestCase<SpreadsheetMetadataPropertyNameSpreadsheetParserNumber>
        implements SpreadsheetMetadataTesting {

    @Test
    public void testExtractLocaleAwareValue() throws ParseException {
        this.extractLocaleValueAndCheck("1.25");
    }

    @Test
    public void testExtractLocaleAwareValueInteger() throws ParseException {
        this.extractLocaleValueAndCheck("789");
    }

    private void extractLocaleValueAndCheck(final String text) throws ParseException {
        this.extractLocaleValueAndCheck2(
                text,
                ExpressionNumberKind.BIG_DECIMAL
        );
        this.extractLocaleValueAndCheck2(
                text,
                ExpressionNumberKind.DOUBLE
        );
    }

    private void extractLocaleValueAndCheck2(final String text,
                                             final ExpressionNumberKind kind) throws ParseException {
        final Locale locale = Locale.ENGLISH;
        final SpreadsheetParserSelector parserSelector = SpreadsheetMetadataPropertyNameSpreadsheetParserNumber.instance()
                .extractLocaleAwareValue(locale)
                .get();

        final ExpressionNumber value = SpreadsheetConverters.textToNumber(
                SPREADSHEET_PARSER_PROVIDER.spreadsheetParser(
                        parserSelector,
                        PROVIDER_CONTEXT
                )
        ).convertOrFail(
                text,
                ExpressionNumber.class,
                SpreadsheetConverterContexts.basic(
                        SpreadsheetConverterContexts.NO_METADATA,
                        SpreadsheetConverterContexts.NO_VALIDATION_REFERENCE,
                        SpreadsheetConverters.basic(),
                        SpreadsheetLabelNameResolvers.fake(),
                        ExpressionNumberConverterContexts.basic(
                                Converters.fake(),
                                ConverterContexts.basic(
                                        Converters.JAVA_EPOCH_OFFSET, // dateOffset
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
                                        DecimalNumberContexts.american(MathContext.DECIMAL32)
                                ),
                                kind
                        )
                )
        );

        final DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance(locale);
        decimalFormat.setParseBigDecimal(true);
        final Number expected = decimalFormat.parse(text);

        this.checkEquals(
                kind.create(expected),
                value,
                () -> parserSelector + "\n" + kind + "\nDecimalFormat: " + decimalFormat.toPattern()
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetMetadataPropertyNameSpreadsheetParserNumber.instance(),
                "numberParser"
        );
    }

    @Override
    SpreadsheetMetadataPropertyNameSpreadsheetParserNumber createName() {
        return SpreadsheetMetadataPropertyNameSpreadsheetParserNumber.instance();
    }

    @Override
    SpreadsheetParserSelector propertyValue() {
        return SpreadsheetNumberParsePattern.parseNumberParsePattern("#.## \"pattern-1\";#.00 \"pattern-2\"")
                .spreadsheetParserSelector();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameSpreadsheetParserNumber> type() {
        return SpreadsheetMetadataPropertyNameSpreadsheetParserNumber.class;
    }
}

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
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.convert.FakeConverter;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetNumberFormatPattern;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.value.HasSpreadsheetCell;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContexts;

import java.math.MathContext;
import java.text.DateFormatSymbols;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

public final class SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorNumberTest extends SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorTestCase<SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorNumber> {

    private final static ExpressionNumberKind KIND = ExpressionNumberKind.DOUBLE;

    @Test
    public void testExtractLocaleAwareValue() {
        this.extractLocaleValueAndCheck(
            KIND.create(1.25),
            "1.25"
        );
    }

    @Test
    public void testExtractLocaleAwareValueInteger() {
        this.extractLocaleValueAndCheck(
            KIND.create(789),
            "789."
        );
    }

    private void extractLocaleValueAndCheck(final ExpressionNumber number,
                                            final String expected) {
        final Locale locale = Locale.ENGLISH;
        final SpreadsheetFormatPattern pattern = SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorNumber.instance()
            .extractLocaleAwareValue(
                LocaleContexts.jre(locale)
            ).get()
            .spreadsheetFormatPattern()
            .get();

        final String formatted = pattern.formatter()
            .format(
                Optional.of(number),
                spreadsheetFormatterContext()
            ).get()
            .text();

        this.checkEquals(
            expected,
            formatted,
            pattern::toString
        );
    }

    private SpreadsheetFormatterContext spreadsheetFormatterContext() {
        final LineEnding lineEnding = LineEnding.NL;
        final Locale locale = Locale.ENGLISH;

        return SpreadsheetFormatterContexts.basic(
            HasSpreadsheetCell.NO_CELL,
            (n -> {
                throw new UnsupportedOperationException();
            }),
            (n -> {
                throw new UnsupportedOperationException();
            }),
            1, // cellCharacterWidth
            SpreadsheetFormatters.fake(),
            (final Optional<Object> value) -> {
                throw new UnsupportedOperationException();
            },
            SpreadsheetConverterContexts.basic(
                SpreadsheetConverterContexts.NO_METADATA,
                SpreadsheetConverterContexts.NO_VALIDATION_REFERENCE,
                new FakeConverter<>() {

                    @Override
                    public boolean canConvert(final Object value,
                                              final Class<?> type,
                                              final SpreadsheetConverterContext context) {
                        return type.isInstance(value);
                    }

                    @Override
                    public <T> Either<T, String> convert(final Object value,
                                                         final Class<T> type,
                                                         final SpreadsheetConverterContext context) {
                        return this.successfulConversion(
                            type.cast(value),
                            type
                        );
                    }
                },
                LABEL_NAME_RESOLVER,
                JsonNodeConverterContexts.basic(
                    ExpressionNumberConverterContexts.basic(
                        Converters.fake(),
                        ConverterContexts.basic(
                            false, // canNumbersHaveGroupSeparator
                            Converters.JAVA_EPOCH_OFFSET, // dateOffset
                            lineEnding,
                            ',', // valueSeparator
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
                        ExpressionNumberKind.DEFAULT
                    ),
                    JsonNodeMarshallUnmarshallContexts.fake()
                ),
                LocaleContexts.fake()
            ),
            SpreadsheetFormatterProviders.fake(),
            ProviderContexts.fake()
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorNumber.instance(),
            "numberFormatter"
        );
    }

    @Override
    SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorNumber createName() {
        return SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorNumber.instance();
    }

    @Override
    SpreadsheetFormatterSelector propertyValue() {
        return SpreadsheetNumberFormatPattern.parseNumberFormatPattern("#.## \"custom\"")
            .spreadsheetFormatterSelector();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorNumber> type() {
        return SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorNumber.class;
    }
}

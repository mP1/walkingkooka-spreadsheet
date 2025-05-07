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

import walkingkooka.collect.list.Lists;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.math.DecimalNumberSymbols;

import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

/**
 * Handles building a {@link DecimalNumberContext} for {@link SpreadsheetMetadata#decimalNumberContext()}.
 */
final class SpreadsheetMetadataDecimalNumberContextComponents {

    static SpreadsheetMetadataDecimalNumberContextComponents with(final SpreadsheetMetadata metadata) {
        return new SpreadsheetMetadataDecimalNumberContextComponents(metadata);
    }

    private SpreadsheetMetadataDecimalNumberContextComponents(final SpreadsheetMetadata metadata) {
        super();
        this.components = SpreadsheetMetadataComponents.with(metadata);
    }

    DecimalNumberContext decimalNumberContext() {
        final SpreadsheetMetadataComponents components = this.components;

        final String currencySymbol = components.getOrNull(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL);
        final Character decimalSeparator = components.getOrNull(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR);
        final String exponentSymbol = components.getOrNull(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL);
        final Character groupSeparator = components.getOrNull(SpreadsheetMetadataPropertyName.GROUP_SEPARATOR);
        final Character negativeSign = components.getOrNull(SpreadsheetMetadataPropertyName.NEGATIVE_SIGN);
        final Character percentSymbol = components.getOrNull(SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL);
        final Character positiveSign = components.getOrNull(SpreadsheetMetadataPropertyName.POSITIVE_SIGN);

        final Locale locale = components.getOrNull(SpreadsheetMetadataPropertyName.LOCALE);

        final Integer precision = components.getOrNull(SpreadsheetMetadataPropertyName.PRECISION);
        final RoundingMode roundingMode = components.getOrNull(SpreadsheetMetadataPropertyName.ROUNDING_MODE);

        components.reportIfMissing();

        return DecimalNumberContexts.basic(
                DecimalNumberSymbols.with(
                        negativeSign,
                        positiveSign,
                        currencySymbol,
                        decimalSeparator,
                        exponentSymbol,
                        groupSeparator,
                        percentSymbol
                ),
                locale,
                new MathContext(
                        precision,
                        roundingMode
                )
        );
    }

    // this list should match the properties used in the method above.
    static final List<SpreadsheetMetadataPropertyName<?>> REQUIRED = Lists.of(
            SpreadsheetMetadataPropertyName.LOCALE,

            SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL,
            SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR,
            SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL,
            SpreadsheetMetadataPropertyName.GROUP_SEPARATOR,
            SpreadsheetMetadataPropertyName.NEGATIVE_SIGN,
            SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL,
            SpreadsheetMetadataPropertyName.POSITIVE_SIGN,
            SpreadsheetMetadataPropertyName.PRECISION,
            SpreadsheetMetadataPropertyName.ROUNDING_MODE
    );

    final SpreadsheetMetadataComponents components;

    @Override
    public String toString() {
        return this.components.toString();
    }
}

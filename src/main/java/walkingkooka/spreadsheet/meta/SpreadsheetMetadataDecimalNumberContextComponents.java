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

import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.math.DecimalNumberSymbols;

import java.math.MathContext;
import java.math.RoundingMode;
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
        this.missing = SpreadsheetMetadataMissingComponents.with(metadata);
    }

    DecimalNumberContext decimalNumberContext() {
        final SpreadsheetMetadataMissingComponents missing = this.missing;

        final DecimalNumberSymbols decimalNumberSymbols = missing.getOrNull(SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_SYMBOLS);
        final Locale locale = missing.getOrNull(SpreadsheetMetadataPropertyName.LOCALE);

        final Integer precision = missing.getOrNull(SpreadsheetMetadataPropertyName.PRECISION);
        final RoundingMode roundingMode = missing.getOrNull(SpreadsheetMetadataPropertyName.ROUNDING_MODE);

        missing.reportIfMissing();

        return DecimalNumberContexts.basic(
                decimalNumberSymbols,
                locale,
                new MathContext(
                        precision,
                        roundingMode
                )
        );
    }

    final SpreadsheetMetadataMissingComponents missing;

    @Override
    public String toString() {
        return this.missing.toString();
    }
}

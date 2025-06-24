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
import walkingkooka.locale.LocaleContexts;

import java.math.RoundingMode;
import java.util.Locale;


public final class SpreadsheetMetadataPropertyNameRoundingModeTest extends SpreadsheetMetadataPropertyNameTestCase<SpreadsheetMetadataPropertyNameRoundingMode, RoundingMode> {

    @Test
    public void testCheckValueWithInvalidRoundingModeFails() {
        this.checkValueFails(
                "invalid",
                "Metadata roundingMode=\"invalid\", Expected RoundingMode"
        );
    }

    @Test
    public void testExtractLocaleAwareValue() {
        this.extractLocaleValueAwareAndCheck(
                LocaleContexts.jre(Locale.ENGLISH),
                null
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetMetadataPropertyNameRoundingMode.instance(),
                "roundingMode"
        );
    }

    @Override
    SpreadsheetMetadataPropertyNameRoundingMode createName() {
        return SpreadsheetMetadataPropertyNameRoundingMode.instance();
    }

    @Override
    RoundingMode propertyValue() {
        return RoundingMode.DOWN;
    }

    @Override
    String propertyValueType() {
        return RoundingMode.class.getSimpleName();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameRoundingMode> type() {
        return SpreadsheetMetadataPropertyNameRoundingMode.class;
    }
}

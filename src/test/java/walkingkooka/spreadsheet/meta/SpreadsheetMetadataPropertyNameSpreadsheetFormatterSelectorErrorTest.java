
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
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;

import java.util.Optional;

public final class SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorErrorTest extends SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorTestCase<SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorError>
    implements SpreadsheetMetadataTesting {

    @Test
    public void testExtractLocaleAwareValue() {
        this.checkEquals(
            Optional.empty(),
            SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorError.instance()
                .extractLocaleAwareValue(LocaleContexts.fake())
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorError.instance(),
            "errorFormatter"
        );
    }

    @Override
    SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorError createName() {
        return SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorError.instance();
    }

    @Override
    SpreadsheetFormatterSelector propertyValue() {
        return SpreadsheetFormatterSelector.parse("hello-error-formatter");
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorError> type() {
        return SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorError.class;
    }
}

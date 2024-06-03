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

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetFormatterProviderCollectionTest implements SpreadsheetFormatterProviderTesting<SpreadsheetFormatterProviderCollection> {

    @Test
    public void testWithNullProvidersFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetFormatterProviderCollection.with(null)
        );
    }

    @Test
    public void testGet() {
        final SpreadsheetFormatterProvider provider = SpreadsheetFormatterProviders.spreadsheetFormatPattern();

        this.spreadsheetFormatterAndCheck(
                SpreadsheetFormatterProviderCollection.with(
                        Sets.of(provider)
                ),
                SpreadsheetFormatterSelector.parse("text-format @@"),
                SpreadsheetPattern.parseTextFormatPattern("@@")
                        .formatter()
        );
    }

    @Test
    public void testInfos() {
        final SpreadsheetFormatterProvider provider = SpreadsheetFormatterProviders.spreadsheetFormatPattern();

        this.spreadsheetFormatterInfosAndCheck(
                SpreadsheetFormatterProviderCollection.with(Sets.of(provider)),
                provider.spreadsheetFormatterInfos()
        );
    }

    @Override
    public Class<SpreadsheetFormatterProviderCollection> type() {
        return SpreadsheetFormatterProviderCollection.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public SpreadsheetFormatterProviderCollection createSpreadsheetFormatterProvider() {
        return SpreadsheetFormatterProviderCollection.with(
                Sets.of(
                        SpreadsheetFormatterProviders.spreadsheetFormatPattern()
                )
        );
    }
}

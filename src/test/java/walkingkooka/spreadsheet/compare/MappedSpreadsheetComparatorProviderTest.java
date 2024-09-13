/*
 * Copyright 2024 Miroslav Pokorny (github.com/mP1)
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

package walkingkooka.spreadsheet.compare;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class MappedSpreadsheetComparatorProviderTest implements SpreadsheetComparatorProviderTesting<MappedSpreadsheetComparatorProvider>,
        ToStringTesting<MappedSpreadsheetComparatorProvider> {

    private final static AbsoluteUrl URL = Url.parseAbsolute("https://example.com/comparator123");

    private final static SpreadsheetComparatorName NAME = SpreadsheetComparatorName.with("different-comparator-name-123");

    private final static SpreadsheetComparatorName ORIGINAL_NAME = SpreadsheetComparatorName.with("original-comparator-123");

    private final static SpreadsheetComparator<?> COMPARATOR = SpreadsheetComparators.fake();

    private final static ProviderContext PROVIDER_CONTEXT = ProviderContexts.fake();

    @Test
    public void testWithNullViewFails() {
        assertThrows(
                NullPointerException.class,
                () -> MappedSpreadsheetComparatorProvider.with(
                        null,
                        SpreadsheetComparatorProviders.fake()
                )
        );
    }

    @Test
    public void testWithNullProviderFails() {
        assertThrows(
                NullPointerException.class,
                () -> MappedSpreadsheetComparatorProvider.with(
                        SpreadsheetComparatorInfoSet.EMPTY,
                        null
                )
        );
    }

    @Test
    public void testSpreadsheetComparator() {
        this.spreadsheetComparatorAndCheck(
                NAME,
                PROVIDER_CONTEXT,
                COMPARATOR
        );
    }

    @Test
    public void testSpreadsheetComparatorUnknownFails() {
        this.spreadsheetComparatorFails(
                SpreadsheetComparatorName.with("unknown"),
                PROVIDER_CONTEXT
        );
    }

    @Test
    public void testInfos() {
        this.spreadsheetComparatorInfosAndCheck(
                SpreadsheetComparatorInfo.with(
                        URL,
                        NAME
                )
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createSpreadsheetComparatorProvider(),
                "https://example.com/comparator123 different-comparator-name-123"
        );
    }

    @Override
    public MappedSpreadsheetComparatorProvider createSpreadsheetComparatorProvider() {
        return MappedSpreadsheetComparatorProvider.with(
                SpreadsheetComparatorInfoSet.EMPTY.concat(
                        SpreadsheetComparatorInfo.with(
                                URL,
                                NAME
                        )
                ),
                new FakeSpreadsheetComparatorProvider() {

                    @Override
                    public SpreadsheetComparator<?> spreadsheetComparator(final SpreadsheetComparatorName name,
                                                                          final ProviderContext context) {
                        return name.equals(ORIGINAL_NAME) ?
                                COMPARATOR :
                                null;
                    }

                    @Override
                    public SpreadsheetComparatorInfoSet spreadsheetComparatorInfos() {
                        return SpreadsheetComparatorInfoSet.EMPTY.concat(
                                SpreadsheetComparatorInfo.with(
                                        URL,
                                        ORIGINAL_NAME
                                )
                        );
                    }
                }
        );
    }

    @Override
    public Class<MappedSpreadsheetComparatorProvider> type() {
        return MappedSpreadsheetComparatorProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}

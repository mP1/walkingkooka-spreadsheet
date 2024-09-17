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
import walkingkooka.collect.set.Sets;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class MergedMappedSpreadsheetComparatorProviderTest implements SpreadsheetComparatorProviderTesting<MergedMappedSpreadsheetComparatorProvider>,
        ToStringTesting<MergedMappedSpreadsheetComparatorProvider> {

    private final static AbsoluteUrl RENAMED_URL = Url.parseAbsolute("https://example.com/rename-comparator111");

    private final static SpreadsheetComparatorName RENAMED_RENAME_NAME = SpreadsheetComparatorName.with("renamed-rename-comparator-111");

    private final static SpreadsheetComparatorName RENAME_PROVIDER_NAME = SpreadsheetComparatorName.with("renamed-provider-comparator-111");

    private final static SpreadsheetComparator<?> RENAME_COMPARATOR = SpreadsheetComparators.fake();

    private final static AbsoluteUrl PROVIDER_ONLY_URL = Url.parseAbsolute("https://example.com/provider-only-comparator-222");

    private final static SpreadsheetComparatorName PROVIDER_ONLY_NAME = SpreadsheetComparatorName.with("provider-only-comparator-222");

    private final static SpreadsheetComparator<?> PROVIDER_ONLY_COMPARATOR = SpreadsheetComparators.fake();

    private final static ProviderContext PROVIDER_ONLY_CONTEXT = ProviderContexts.fake();

    @Test
    public void testWithNullInfosFails() {
        assertThrows(
                NullPointerException.class,
                () -> MergedMappedSpreadsheetComparatorProvider.with(
                        null,
                        SpreadsheetComparatorProviders.fake()
                )
        );
    }

    @Test
    public void testWithNullProviderFails() {
        assertThrows(
                NullPointerException.class,
                () -> MergedMappedSpreadsheetComparatorProvider.with(
                        SpreadsheetComparatorInfoSet.EMPTY,
                        null
                )
        );
    }

    @Test
    public void testSpreadsheetComparatorWithRenamedName() {
        this.spreadsheetComparatorAndCheck(
                RENAMED_RENAME_NAME,
                PROVIDER_ONLY_CONTEXT,
                RENAME_COMPARATOR
        );
    }

    @Test
    public void testSpreadsheetComparatorWithProviderOnlyName() {
        this.spreadsheetComparatorAndCheck(
                PROVIDER_ONLY_NAME,
                PROVIDER_ONLY_CONTEXT,
                PROVIDER_ONLY_COMPARATOR
        );
    }

    @Test
    public void testSpreadsheetComparatorUnknownFails() {
        this.spreadsheetComparatorFails(
                SpreadsheetComparatorName.with("unknown"),
                PROVIDER_ONLY_CONTEXT
        );
    }

    @Test
    public void testInfos() {
        this.spreadsheetComparatorInfosAndCheck(
                SpreadsheetComparatorInfo.with(
                        RENAMED_URL,
                        RENAMED_RENAME_NAME
                ),
                SpreadsheetComparatorInfo.with(
                        PROVIDER_ONLY_URL,
                        PROVIDER_ONLY_NAME
                )
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createSpreadsheetComparatorProvider(),
                "https://example.com/provider-only-comparator-222 provider-only-comparator-222,https://example.com/rename-comparator111 renamed-rename-comparator-111"
        );
    }

    @Override
    public MergedMappedSpreadsheetComparatorProvider createSpreadsheetComparatorProvider() {
        return MergedMappedSpreadsheetComparatorProvider.with(
                SpreadsheetComparatorInfoSet.EMPTY.concat(
                        SpreadsheetComparatorInfo.with(
                                RENAMED_URL,
                                RENAMED_RENAME_NAME
                        )
                ),
                new FakeSpreadsheetComparatorProvider() {

                    @Override
                    public SpreadsheetComparator<?> spreadsheetComparator(final SpreadsheetComparatorName name,
                                                                          final ProviderContext context) {
                        if(name.equals(RENAME_PROVIDER_NAME)) {
                            return RENAME_COMPARATOR;
                        }
                        if(name.equals(PROVIDER_ONLY_NAME)) {
                            return PROVIDER_ONLY_COMPARATOR;
                        }
                        throw new IllegalArgumentException("Unknown comparator " + name);
                    }

                    @Override
                    public SpreadsheetComparatorInfoSet spreadsheetComparatorInfos() {
                        return SpreadsheetComparatorInfoSet.with(
                                Sets.of(
                                        SpreadsheetComparatorInfo.with(
                                                RENAMED_URL,
                                                RENAME_PROVIDER_NAME
                                        ),
                                        SpreadsheetComparatorInfo.with(
                                                PROVIDER_ONLY_URL,
                                                PROVIDER_ONLY_NAME
                                        )
                                )
                        );
                    }
                }
        );
    }

    // class............................................................................................................

    @Override
    public Class<MergedMappedSpreadsheetComparatorProvider> type() {
        return MergedMappedSpreadsheetComparatorProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}

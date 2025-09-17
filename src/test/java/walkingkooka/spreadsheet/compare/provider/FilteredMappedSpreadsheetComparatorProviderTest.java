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

package walkingkooka.spreadsheet.compare.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.compare.SpreadsheetComparator;
import walkingkooka.spreadsheet.compare.SpreadsheetComparators;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class FilteredMappedSpreadsheetComparatorProviderTest implements SpreadsheetComparatorProviderTesting<FilteredMappedSpreadsheetComparatorProvider>,
    ToStringTesting<FilteredMappedSpreadsheetComparatorProvider> {

    private final static AbsoluteUrl URL = Url.parseAbsolute("https://example.com/comparator123");

    private final static SpreadsheetComparatorName NAME = SpreadsheetComparatorName.with("different-comparator-name-123");

    private final static SpreadsheetComparatorName ORIGINAL_NAME = SpreadsheetComparatorName.with("original-comparator-123");

    private final static List<?> VALUES = Lists.of("abc");

    private final static SpreadsheetComparator<?> COMPARATOR = SpreadsheetComparators.fake();

    private final static ProviderContext PROVIDER_CONTEXT = ProviderContexts.fake();

    @Test
    public void testWithNullViewFails() {
        assertThrows(
            NullPointerException.class,
            () -> FilteredMappedSpreadsheetComparatorProvider.with(
                null,
                SpreadsheetComparatorProviders.fake()
            )
        );
    }

    @Test
    public void testWithNullProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> FilteredMappedSpreadsheetComparatorProvider.with(
                SpreadsheetComparatorInfoSet.EMPTY,
                null
            )
        );
    }

    @Test
    public void testSpreadsheetComparatorSelector() {
        this.spreadsheetComparatorAndCheck(
            SpreadsheetComparatorSelector.parse(NAME + "(\"abc\")"),
            PROVIDER_CONTEXT,
            COMPARATOR
        );
    }

    @Test
    public void testSpreadsheetComparatorSelectorWithUnknownFails() {
        this.spreadsheetComparatorFails(
            SpreadsheetComparatorSelector.parse("unknown"),
            PROVIDER_CONTEXT
        );
    }

    @Test
    public void testSpreadsheetComparatorName() {
        this.spreadsheetComparatorAndCheck(
            NAME,
            VALUES,
            PROVIDER_CONTEXT,
            COMPARATOR
        );
    }

    @Test
    public void testSpreadsheetComparatorNameWithUnknownFails() {
        this.spreadsheetComparatorFails(
            SpreadsheetComparatorName.with("unknown"),
            VALUES,
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
    public FilteredMappedSpreadsheetComparatorProvider createSpreadsheetComparatorProvider() {
        return FilteredMappedSpreadsheetComparatorProvider.with(
            SpreadsheetComparatorInfoSet.EMPTY.concat(
                SpreadsheetComparatorInfo.with(
                    URL,
                    NAME
                )
            ),
            new FakeSpreadsheetComparatorProvider() {

                @Override
                public SpreadsheetComparator<?> spreadsheetComparator(final SpreadsheetComparatorSelector selector,
                                                                      final ProviderContext context) {
                    return selector.evaluateValueText(
                        this,
                        context
                    );
                }

                @Override
                public SpreadsheetComparator<?> spreadsheetComparator(final SpreadsheetComparatorName name,
                                                                      final List<?> values,
                                                                      final ProviderContext context) {
                    if (name.equals(ORIGINAL_NAME)) {
                        checkEquals(VALUES, values);
                        return COMPARATOR;
                    }

                    throw new IllegalArgumentException("Unknown comparator " + name);
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
    public Class<FilteredMappedSpreadsheetComparatorProvider> type() {
        return FilteredMappedSpreadsheetComparatorProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}

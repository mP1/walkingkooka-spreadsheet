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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.compare.FakeSpreadsheetComparator;
import walkingkooka.spreadsheet.compare.SpreadsheetComparator;

import java.util.List;

public final class AliasesSpreadsheetComparatorProviderTest implements SpreadsheetComparatorProviderTesting<AliasesSpreadsheetComparatorProvider> {

    private final static String NAME1_STRING = "comparator1";

    private final static SpreadsheetComparatorName NAME1 = SpreadsheetComparatorName.with(NAME1_STRING);

    private final static SpreadsheetComparatorInfo INFO1 = SpreadsheetComparatorInfo.parse("https://example.com/comparator1 " + NAME1);

    private final static SpreadsheetComparatorName ALIAS2 = SpreadsheetComparatorName.with("alias2");

    private final static SpreadsheetComparator<?> COMPARATOR1 = spreadsheetComparator(NAME1);

    private final static String NAME2_STRING = "comparator2";

    private final static SpreadsheetComparatorName NAME2 = SpreadsheetComparatorName.with(NAME2_STRING);

    private final static SpreadsheetComparator<?> COMPARATOR2 = spreadsheetComparator(NAME2);

    private final static SpreadsheetComparatorInfo INFO2 = SpreadsheetComparatorInfo.parse("https://example.com/comparator2 " + NAME2);

    private final static String NAME3_STRING = "comparator3";

    private final static SpreadsheetComparatorName NAME3 = SpreadsheetComparatorName.with(NAME3_STRING);

    private final static SpreadsheetComparator<?> COMPARATOR3 = spreadsheetComparator(NAME3);

    private final static SpreadsheetComparatorInfo INFO3 = SpreadsheetComparatorInfo.parse("https://example.com/comparator3 " + NAME3);

    private final static String VALUE3 = "Value3";

    private final static String NAME4_STRING = "custom4";

    private final static SpreadsheetComparatorName NAME4 = SpreadsheetComparatorName.with(NAME4_STRING);

    private final static SpreadsheetComparatorInfo INFO4 = SpreadsheetComparatorInfo.parse("https://example.com/custom4 " + NAME4);

    private final static ProviderContext CONTEXT = ProviderContexts.fake();

    private static SpreadsheetComparator<?> spreadsheetComparator(final SpreadsheetComparatorName name) {
        return new FakeSpreadsheetComparator<>() {
            @Override
            public SpreadsheetComparatorName name() {
                return name;
            }

            @Override
            public String toString() {
                return name.toString();
            }
        };
    }

    @Test
    public void testWithUnknownComparatorName() {
        AliasesSpreadsheetComparatorProvider.with(
            SpreadsheetComparatorAliasSet.parse("unknown-comparator404"),
            new FakeSpreadsheetComparatorProvider() {
                @Override
                public SpreadsheetComparatorInfoSet spreadsheetComparatorInfos() {
                    return SpreadsheetComparatorInfoSet.parse("https://example.com/comparator111 comparator111");
                }
            }
        );
    }

    @Test
    public void testSpreadsheetComparatorNameWithName() {
        this.spreadsheetComparatorAndCheck(
            NAME1,
            Lists.empty(),
            CONTEXT,
            COMPARATOR1
        );
    }

    @Test
    public void testSpreadsheetComparatorSelectorWithName() {
        this.spreadsheetComparatorAndCheck(
            SpreadsheetComparatorSelector.parse(NAME1 + ""),
            CONTEXT,
            COMPARATOR1
        );
    }

    @Test
    public void testSpreadsheetComparatorNameWithAlias() {
        this.spreadsheetComparatorAndCheck(
            ALIAS2,
            Lists.empty(),
            CONTEXT,
            COMPARATOR2
        );
    }

    @Test
    public void testSpreadsheetComparatorNameWithAliasReversed() {
        this.spreadsheetComparatorAndCheck(
            ALIAS2.reversed(),
            Lists.empty(),
            CONTEXT,
            COMPARATOR2.reversed()
        );
    }

    @Test
    public void testSpreadsheetComparatorSelectorWithAlias() {
        this.spreadsheetComparatorAndCheck(
            SpreadsheetComparatorSelector.parse(ALIAS2 + ""),
            CONTEXT,
            COMPARATOR2
        );
    }

    @Test
    public void testSpreadsheetComparatorSelectorWithAliasReversed() {
        this.spreadsheetComparatorAndCheck(
            SpreadsheetComparatorSelector.parse(ALIAS2.reversed() + ""),
            CONTEXT,
            COMPARATOR2.reversed()
        );
    }

    @Test
    public void testSpreadsheetComparatorNameWithSelector() {
        this.spreadsheetComparatorAndCheck(
            NAME4,
            Lists.empty(),
            CONTEXT,
            COMPARATOR3
        );
    }

    @Test
    public void testSpreadsheetComparatorNameWithSelectorReversed() {
        this.spreadsheetComparatorAndCheck(
            NAME4.reversed(),
            Lists.empty(),
            CONTEXT,
            COMPARATOR3.reversed()
        );
    }

    @Test
    public void testSpreadsheetComparatorSelectorWithSelector() {
        this.spreadsheetComparatorAndCheck(
            SpreadsheetComparatorSelector.parse(NAME4 + ""),
            CONTEXT,
            COMPARATOR3
        );
    }

    @Test
    public void testSpreadsheetComparatorSelectorWithSelectorReversed() {
        this.spreadsheetComparatorAndCheck(
            SpreadsheetComparatorSelector.parse(NAME4.reversed() + ""),
            CONTEXT,
            COMPARATOR3.reversed()
        );
    }

    @Test
    public void testInfos() {
        this.spreadsheetComparatorInfosAndCheck(
            INFO1,
            INFO2.setName(ALIAS2),
            INFO4.setName(NAME4) // from SpreadsheetComparatorAliasSet
        );
    }

    @Override
    public AliasesSpreadsheetComparatorProvider createSpreadsheetComparatorProvider() {
        final String aliases = "comparator1, alias2 comparator2, custom4 comparator3(\"Value3\") https://example.com/custom4";

        this.checkEquals(
            NAME1 + ", " + ALIAS2 + " " + NAME2 + ", " + NAME4 + " " + NAME3 + "(\"" + VALUE3 + "\") " + INFO4.url(),
            aliases
        );

        return AliasesSpreadsheetComparatorProvider.with(
            SpreadsheetComparatorAliasSet.parse(aliases),
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
                    SpreadsheetComparator<?> comparator;

                    switch (name.toString()) {
                        case NAME1_STRING:
                            checkEquals(Lists.empty(), values, "values");
                            comparator = COMPARATOR1;
                            break;
                        case NAME2_STRING:
                            checkEquals(Lists.empty(), values, "values");
                            comparator = COMPARATOR2;
                            break;
                        case NAME3_STRING:
                            checkEquals(Lists.of(VALUE3), values, "values");
                            comparator = COMPARATOR3;
                            break;
                        case NAME1_STRING + SpreadsheetComparatorName.REVERSED:
                            checkEquals(Lists.empty(), values, "values");
                            comparator = COMPARATOR1.reversed();
                            break;
                        case NAME2_STRING + SpreadsheetComparatorName.REVERSED:
                            checkEquals(Lists.empty(), values, "values");
                            comparator = COMPARATOR2.reversed();
                            break;
                        case NAME3_STRING + SpreadsheetComparatorName.REVERSED:
                            checkEquals(Lists.of(VALUE3), values, "values");
                            comparator = COMPARATOR3.reversed();
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown comparator " + name);
                    }

                    return comparator;
                }

                @Override
                public SpreadsheetComparatorInfoSet spreadsheetComparatorInfos() {
                    return SpreadsheetComparatorInfoSet.with(
                        Sets.of(
                            INFO1,
                            INFO2,
                            INFO3
                        )
                    );
                }
            }
        );
    }

    // class............................................................................................................

    @Override
    public Class<AliasesSpreadsheetComparatorProvider> type() {
        return AliasesSpreadsheetComparatorProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}

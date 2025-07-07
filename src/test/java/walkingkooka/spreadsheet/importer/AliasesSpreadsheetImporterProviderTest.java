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

package walkingkooka.spreadsheet.importer;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;

import java.util.List;

public final class AliasesSpreadsheetImporterProviderTest implements SpreadsheetImporterProviderTesting<AliasesSpreadsheetImporterProvider> {

    private final static String NAME1_STRING = "importer1";

    private final static SpreadsheetImporterName NAME1 = SpreadsheetImporterName.with(NAME1_STRING);

    private final static SpreadsheetImporterInfo INFO1 = SpreadsheetImporterInfo.parse("https://example.com/importer1 " + NAME1);

    private final static SpreadsheetImporterName ALIAS2 = SpreadsheetImporterName.with("alias2");

    private final static SpreadsheetImporter IMPORTER1 = SpreadsheetImporters.fake();

    private final static String NAME2_STRING = "importer2";

    private final static SpreadsheetImporterName NAME2 = SpreadsheetImporterName.with(NAME2_STRING);

    private final static SpreadsheetImporter IMPORTER2 = SpreadsheetImporters.fake();

    private final static SpreadsheetImporterInfo INFO2 = SpreadsheetImporterInfo.parse("https://example.com/importer2 " + NAME2);

    private final static String NAME3_STRING = "importer3";

    private final static SpreadsheetImporterName NAME3 = SpreadsheetImporterName.with(NAME3_STRING);

    private final static SpreadsheetImporter IMPORTER3 = SpreadsheetImporters.fake();

    private final static SpreadsheetImporterInfo INFO3 = SpreadsheetImporterInfo.parse("https://example.com/importer3 " + NAME3);

    private final static String VALUE3 = "Value3";

    private final static String NAME4_STRING = "custom4";

    private final static SpreadsheetImporterName NAME4 = SpreadsheetImporterName.with(NAME4_STRING);

    private final static SpreadsheetImporterInfo INFO4 = SpreadsheetImporterInfo.parse("https://example.com/custom4 " + NAME4);

    private final static ProviderContext CONTEXT = ProviderContexts.fake();

    @Test
    public void testWithUnknownImporterName() {
        AliasesSpreadsheetImporterProvider.with(
            SpreadsheetImporterAliasSet.parse("unknown-importer404"),
            new FakeSpreadsheetImporterProvider() {
                @Override
                public SpreadsheetImporterInfoSet spreadsheetImporterInfos() {
                    return SpreadsheetImporterInfoSet.parse("https://example.com/importer111 importer111");
                }
            }
        );
    }

    @Test
    public void testSpreadsheetImporterNameWithName() {
        this.spreadsheetImporterAndCheck(
            NAME1,
            Lists.empty(),
            CONTEXT,
            IMPORTER1
        );
    }

    @Test
    public void testSpreadsheetImporterSelectorWithName() {
        this.spreadsheetImporterAndCheck(
            SpreadsheetImporterSelector.parse(NAME1 + ""),
            CONTEXT,
            IMPORTER1
        );
    }

    @Test
    public void testSpreadsheetImporterNameWithAlias() {
        this.spreadsheetImporterAndCheck(
            ALIAS2,
            Lists.empty(),
            CONTEXT,
            IMPORTER2
        );
    }

    @Test
    public void testSpreadsheetImporterSelectorWithAlias() {
        this.spreadsheetImporterAndCheck(
            SpreadsheetImporterSelector.parse(ALIAS2 + ""),
            CONTEXT,
            IMPORTER2
        );
    }

    @Test
    public void testSpreadsheetImporterNameWithSelector() {
        this.spreadsheetImporterAndCheck(
            NAME4,
            Lists.empty(),
            CONTEXT,
            IMPORTER3
        );
    }

    @Test
    public void testSpreadsheetImporterSelectorWithSelector() {
        this.spreadsheetImporterAndCheck(
            SpreadsheetImporterSelector.parse(NAME4 + ""),
            CONTEXT,
            IMPORTER3
        );
    }

    @Test
    public void testInfos() {
        this.spreadsheetImporterInfosAndCheck(
            INFO1,
            INFO2.setName(ALIAS2),
            INFO4.setName(NAME4) // from SpreadsheetImporterAliasSet
        );
    }

    @Override
    public AliasesSpreadsheetImporterProvider createSpreadsheetImporterProvider() {
        final String aliases = "importer1, alias2 importer2, custom4 importer3(\"Value3\") https://example.com/custom4";

        this.checkEquals(
            NAME1 + ", " + ALIAS2 + " " + NAME2 + ", " + NAME4 + " " + NAME3 + "(\"" + VALUE3 + "\") " + INFO4.url(),
            aliases
        );

        return AliasesSpreadsheetImporterProvider.with(
            SpreadsheetImporterAliasSet.parse(aliases),
            new FakeSpreadsheetImporterProvider() {
                @Override
                public SpreadsheetImporter spreadsheetImporter(final SpreadsheetImporterSelector selector,
                                                               final ProviderContext context) {
                    return selector.evaluateValueText(
                        this,
                        context
                    );
                }

                @Override
                public SpreadsheetImporter spreadsheetImporter(final SpreadsheetImporterName name,
                                                               final List<?> values,
                                                               final ProviderContext context) {
                    SpreadsheetImporter importer;

                    switch (name.toString()) {
                        case NAME1_STRING:
                            checkEquals(Lists.empty(), values, "values");
                            importer = IMPORTER1;
                            break;
                        case NAME2_STRING:
                            checkEquals(Lists.empty(), values, "values");
                            importer = IMPORTER2;
                            break;
                        case NAME3_STRING:
                            checkEquals(Lists.of(VALUE3), values, "values");
                            importer = IMPORTER3;
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown importer " + name);
                    }

                    return importer;
                }

                @Override
                public SpreadsheetImporterInfoSet spreadsheetImporterInfos() {
                    return SpreadsheetImporterInfoSet.with(
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
    public Class<AliasesSpreadsheetImporterProvider> type() {
        return AliasesSpreadsheetImporterProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}

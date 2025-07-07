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

package walkingkooka.spreadsheet.export;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;

import java.util.List;

public final class AliasesSpreadsheetExporterProviderTest implements SpreadsheetExporterProviderTesting<AliasesSpreadsheetExporterProvider> {

    private final static String NAME1_STRING = "exporter1";

    private final static SpreadsheetExporterName NAME1 = SpreadsheetExporterName.with(NAME1_STRING);

    private final static SpreadsheetExporterInfo INFO1 = SpreadsheetExporterInfo.parse("https://example.com/exporter1 " + NAME1);

    private final static SpreadsheetExporterName ALIAS2 = SpreadsheetExporterName.with("alias2");

    private final static SpreadsheetExporter EXPORTER1 = SpreadsheetExporters.fake();

    private final static String NAME2_STRING = "exporter2";

    private final static SpreadsheetExporterName NAME2 = SpreadsheetExporterName.with(NAME2_STRING);

    private final static SpreadsheetExporter EXPORTER2 = SpreadsheetExporters.fake();

    private final static SpreadsheetExporterInfo INFO2 = SpreadsheetExporterInfo.parse("https://example.com/exporter2 " + NAME2);

    private final static String NAME3_STRING = "exporter3";

    private final static SpreadsheetExporterName NAME3 = SpreadsheetExporterName.with(NAME3_STRING);

    private final static SpreadsheetExporter EXPORTER3 = SpreadsheetExporters.fake();

    private final static SpreadsheetExporterInfo INFO3 = SpreadsheetExporterInfo.parse("https://example.com/exporter3 " + NAME3);

    private final static String VALUE3 = "Value3";

    private final static String NAME4_STRING = "custom4";

    private final static SpreadsheetExporterName NAME4 = SpreadsheetExporterName.with(NAME4_STRING);

    private final static SpreadsheetExporterInfo INFO4 = SpreadsheetExporterInfo.parse("https://example.com/custom4 " + NAME4);

    private final static ProviderContext CONTEXT = ProviderContexts.fake();

    @Test
    public void testWithUnknownExporterName() {
        AliasesSpreadsheetExporterProvider.with(
            SpreadsheetExporterAliasSet.parse("unknown-exporter404"),
            new FakeSpreadsheetExporterProvider() {
                @Override
                public SpreadsheetExporterInfoSet spreadsheetExporterInfos() {
                    return SpreadsheetExporterInfoSet.parse("https://example.com/exporter111 exporter111");
                }
            }
        );
    }

    @Test
    public void testSpreadsheetExporterNameWithName() {
        this.spreadsheetExporterAndCheck(
            NAME1,
            Lists.empty(),
            CONTEXT,
            EXPORTER1
        );
    }

    @Test
    public void testSpreadsheetExporterSelectorWithName() {
        this.spreadsheetExporterAndCheck(
            SpreadsheetExporterSelector.parse(NAME1 + ""),
            CONTEXT,
            EXPORTER1
        );
    }

    @Test
    public void testSpreadsheetExporterNameWithAlias() {
        this.spreadsheetExporterAndCheck(
            ALIAS2,
            Lists.empty(),
            CONTEXT,
            EXPORTER2
        );
    }

    @Test
    public void testSpreadsheetExporterSelectorWithAlias() {
        this.spreadsheetExporterAndCheck(
            SpreadsheetExporterSelector.parse(ALIAS2 + ""),
            CONTEXT,
            EXPORTER2
        );
    }

    @Test
    public void testSpreadsheetExporterNameWithSelector() {
        this.spreadsheetExporterAndCheck(
            NAME4,
            Lists.empty(),
            CONTEXT,
            EXPORTER3
        );
    }

    @Test
    public void testSpreadsheetExporterSelectorWithSelector() {
        this.spreadsheetExporterAndCheck(
            SpreadsheetExporterSelector.parse(NAME4 + ""),
            CONTEXT,
            EXPORTER3
        );
    }

    @Test
    public void testInfos() {
        this.spreadsheetExporterInfosAndCheck(
            INFO1,
            INFO2.setName(ALIAS2),
            INFO4.setName(NAME4) // from SpreadsheetExporterAliasSet
        );
    }

    @Override
    public AliasesSpreadsheetExporterProvider createSpreadsheetExporterProvider() {
        final String aliases = "exporter1, alias2 exporter2, custom4 exporter3(\"Value3\") https://example.com/custom4";

        this.checkEquals(
            NAME1 + ", " + ALIAS2 + " " + NAME2 + ", " + NAME4 + " " + NAME3 + "(\"" + VALUE3 + "\") " + INFO4.url(),
            aliases
        );

        return AliasesSpreadsheetExporterProvider.with(
            SpreadsheetExporterAliasSet.parse(aliases),
            new FakeSpreadsheetExporterProvider() {
                @Override
                public SpreadsheetExporter spreadsheetExporter(final SpreadsheetExporterSelector selector,
                                                               final ProviderContext context) {
                    return selector.evaluateValueText(
                        this,
                        context
                    );
                }

                @Override
                public SpreadsheetExporter spreadsheetExporter(final SpreadsheetExporterName name,
                                                               final List<?> values,
                                                               final ProviderContext context) {
                    SpreadsheetExporter exporter;

                    switch (name.toString()) {
                        case NAME1_STRING:
                            checkEquals(Lists.empty(), values, "values");
                            exporter = EXPORTER1;
                            break;
                        case NAME2_STRING:
                            checkEquals(Lists.empty(), values, "values");
                            exporter = EXPORTER2;
                            break;
                        case NAME3_STRING:
                            checkEquals(Lists.of(VALUE3), values, "values");
                            exporter = EXPORTER3;
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown exporter " + name);
                    }

                    return exporter;
                }

                @Override
                public SpreadsheetExporterInfoSet spreadsheetExporterInfos() {
                    return SpreadsheetExporterInfoSet.with(
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
    public Class<AliasesSpreadsheetExporterProvider> type() {
        return AliasesSpreadsheetExporterProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}

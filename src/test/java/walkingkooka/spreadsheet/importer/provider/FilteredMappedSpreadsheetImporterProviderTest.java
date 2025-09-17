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

package walkingkooka.spreadsheet.importer.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.UrlPath;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.importer.FakeSpreadsheetImporter;
import walkingkooka.spreadsheet.importer.SpreadsheetImporter;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class FilteredMappedSpreadsheetImporterProviderTest implements SpreadsheetImporterProviderTesting<FilteredMappedSpreadsheetImporterProvider>,
    SpreadsheetMetadataTesting {

    private final static ProviderContext CONTEXT = ProviderContexts.fake();

    @Test
    public void testWithNullInfosFails() {
        assertThrows(
            NullPointerException.class,
            () -> FilteredMappedSpreadsheetImporterProvider.with(
                null,
                SpreadsheetImporterProviders.fake()
            )
        );
    }

    @Test
    public void testWithNullProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> FilteredMappedSpreadsheetImporterProvider.with(
                SpreadsheetImporterInfoSet.EMPTY.concat(
                    SpreadsheetImporterInfo.with(
                        SpreadsheetImporterProviders.BASE_URL.appendPath(
                            UrlPath.parse("test-123")
                        ),
                        SpreadsheetImporterName.with("test-123")
                    )
                ),
                null
            )
        );
    }

    private final static String NAME = "test-123";

    private final static SpreadsheetImporter IMPORTER = new FakeSpreadsheetImporter() {
        @Override
        public String toString() {
            return NAME;
        }
    };

    @Test
    public void testSpreadsheetImporterSelector() {
        this.spreadsheetImporterAndCheck(
            SpreadsheetImporterSelector.parse(NAME + " (11.0)"),
            CONTEXT,
            IMPORTER
        );
    }

    private final static List<?> VALUES = Lists.of(11.0);

    @Test
    public void testSpreadsheetImporterName() {
        this.spreadsheetImporterAndCheck(
            SpreadsheetImporterName.with(NAME),
            VALUES,
            CONTEXT,
            IMPORTER
        );
    }

    @Test
    public void testSpreadsheetInfos() {
        this.spreadsheetImporterInfosAndCheck(
            SpreadsheetImporterInfo.with(
                url(NAME),
                SpreadsheetImporterName.with(NAME)
            )
        );
    }

    @Override
    public FilteredMappedSpreadsheetImporterProvider createSpreadsheetImporterProvider() {
        final SpreadsheetImporterProvider provider = new SpreadsheetImporterProvider() {
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
                checkEquals(NAME, name.value(), "name");
                checkEquals(VALUES, values, "values");

                return IMPORTER;
            }

            @Override
            public SpreadsheetImporterInfoSet spreadsheetImporterInfos() {
                return SpreadsheetImporterInfoSet.EMPTY.concat(
                    SpreadsheetImporterInfo.with(
                        url(NAME),
                        SpreadsheetImporterName.with(NAME)
                    )
                );
            }
        };

        return FilteredMappedSpreadsheetImporterProvider.with(
            SpreadsheetImporterInfoSet.EMPTY.concat(
                SpreadsheetImporterInfo.with(
                    url(NAME),
                    SpreadsheetImporterName.with(NAME)
                )
            ),
            provider
        );
    }

    private static AbsoluteUrl url(final String importerName) {
        return SpreadsheetImporterProviders.BASE_URL.appendPath(
            UrlPath.parse(importerName)
        );
    }

    // class............................................................................................................

    @Override
    public Class<FilteredMappedSpreadsheetImporterProvider> type() {
        return FilteredMappedSpreadsheetImporterProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}

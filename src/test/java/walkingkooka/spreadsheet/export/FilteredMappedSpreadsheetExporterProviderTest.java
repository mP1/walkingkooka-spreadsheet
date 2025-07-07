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
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.UrlPath;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class FilteredMappedSpreadsheetExporterProviderTest implements SpreadsheetExporterProviderTesting<FilteredMappedSpreadsheetExporterProvider>,
    SpreadsheetMetadataTesting {

    private final static ProviderContext CONTEXT = ProviderContexts.fake();

    @Test
    public void testWithNullInfosFails() {
        assertThrows(
            NullPointerException.class,
            () -> FilteredMappedSpreadsheetExporterProvider.with(
                null,
                SpreadsheetExporterProviders.fake()
            )
        );
    }

    @Test
    public void testWithNullProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> FilteredMappedSpreadsheetExporterProvider.with(
                SpreadsheetExporterInfoSet.EMPTY.concat(
                    SpreadsheetExporterInfo.with(
                        SpreadsheetExporterProviders.BASE_URL.appendPath(
                            UrlPath.parse("test-123")
                        ),
                        SpreadsheetExporterName.with("test-123")
                    )
                ),
                null
            )
        );
    }

    private final static String NAME = "test-123";

    private final static SpreadsheetExporter EXPORTER = new FakeSpreadsheetExporter() {
        @Override
        public String toString() {
            return NAME;
        }
    };

    @Test
    public void testSpreadsheetExporterSelector() {
        this.spreadsheetExporterAndCheck(
            SpreadsheetExporterSelector.parse(NAME + " (11.0)"),
            CONTEXT,
            EXPORTER
        );
    }

    private final static List<?> VALUES = Lists.of(11.0);

    @Test
    public void testSpreadsheetExporterName() {
        this.spreadsheetExporterAndCheck(
            SpreadsheetExporterName.with(NAME),
            VALUES,
            CONTEXT,
            EXPORTER
        );
    }

    @Test
    public void testSpreadsheetInfos() {
        this.spreadsheetExporterInfosAndCheck(
            SpreadsheetExporterInfo.with(
                url(NAME),
                SpreadsheetExporterName.with(NAME)
            )
        );
    }

    @Override
    public FilteredMappedSpreadsheetExporterProvider createSpreadsheetExporterProvider() {
        final SpreadsheetExporterProvider provider = new SpreadsheetExporterProvider() {
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
                checkEquals(NAME, name.value(), "name");
                checkEquals(VALUES, values, "values");

                return EXPORTER;
            }

            @Override
            public SpreadsheetExporterInfoSet spreadsheetExporterInfos() {
                return SpreadsheetExporterInfoSet.EMPTY.concat(
                    SpreadsheetExporterInfo.with(
                        url(NAME),
                        SpreadsheetExporterName.with(NAME)
                    )
                );
            }
        };

        return FilteredMappedSpreadsheetExporterProvider.with(
            SpreadsheetExporterInfoSet.EMPTY.concat(
                SpreadsheetExporterInfo.with(
                    url(NAME),
                    SpreadsheetExporterName.with(NAME)
                )
            ),
            provider
        );
    }

    private static AbsoluteUrl url(final String exporterName) {
        return SpreadsheetExporterProviders.BASE_URL.appendPath(
            UrlPath.parse(exporterName)
        );
    }

    // class............................................................................................................

    @Override
    public Class<FilteredMappedSpreadsheetExporterProvider> type() {
        return FilteredMappedSpreadsheetExporterProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}

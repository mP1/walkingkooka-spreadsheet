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

package walkingkooka.spreadsheet.export.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.net.UrlPath;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.export.FakeSpreadsheetExporter;
import walkingkooka.spreadsheet.export.SpreadsheetExporter;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class MergedMappedSpreadsheetExporterProviderTest implements SpreadsheetExporterProviderTesting<MergedMappedSpreadsheetExporterProvider>,
    SpreadsheetMetadataTesting {

    private final static ProviderContext CONTEXT = ProviderContexts.fake();

    @Test
    public void testWithNullInfosFails() {
        assertThrows(
            NullPointerException.class,
            () -> MergedMappedSpreadsheetExporterProvider.with(
                null,
                SpreadsheetExporterProviders.fake()
            )
        );
    }

    @Test
    public void testWithNullProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> MergedMappedSpreadsheetExporterProvider.with(
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

    private final static String RENAMED_RENAME_NAME = "renamed-rename-111";

    private final static String RENAMED_PROVIDER_NAME = "renamed-provider-111";

    private final static AbsoluteUrl RENAMED_URL = Url.parseAbsolute("https://example.com/" + RENAMED_PROVIDER_NAME);

    private final static SpreadsheetExporter RENAMED_EXPORTER = new FakeSpreadsheetExporter() {
        @Override
        public String toString() {
            return RENAMED_PROVIDER_NAME;
        }
    };

    private final static String PROVIDER_ONLY_NAME = "renamed-provider-222";

    private final static AbsoluteUrl PROVIDER_ONLY_URL = Url.parseAbsolute("https://example.com/" + PROVIDER_ONLY_NAME);

    private final static SpreadsheetExporter PROVIDER_ONLY_EXPORTER = new FakeSpreadsheetExporter() {
        @Override
        public String toString() {
            return PROVIDER_ONLY_NAME;
        }
    };

    @Test
    public void testSpreadsheetExporterSelector() {
        this.spreadsheetExporterAndCheck(
            SpreadsheetExporterSelector.parse(RENAMED_RENAME_NAME + " (11.0)"),
            CONTEXT,
            RENAMED_EXPORTER
        );
    }

    private final static List<?> VALUES = Lists.of(11.0);

    @Test
    public void testSpreadsheetExporterNameWithRenamed() {
        this.spreadsheetExporterAndCheck(
            SpreadsheetExporterName.with(RENAMED_RENAME_NAME),
            VALUES,
            CONTEXT,
            RENAMED_EXPORTER
        );
    }

    @Test
    public void testSpreadsheetExporterNameWithProviderOnly() {
        this.spreadsheetExporterAndCheck(
            SpreadsheetExporterName.with(PROVIDER_ONLY_NAME),
            VALUES,
            CONTEXT,
            PROVIDER_ONLY_EXPORTER
        );
    }

    @Test
    public void testSpreadsheetInfos() {
        this.spreadsheetExporterInfosAndCheck(
            SpreadsheetExporterInfo.with(
                RENAMED_URL,
                SpreadsheetExporterName.with(RENAMED_RENAME_NAME)
            ),
            SpreadsheetExporterInfo.with(
                PROVIDER_ONLY_URL,
                SpreadsheetExporterName.with(PROVIDER_ONLY_NAME)
            )
        );
    }

    @Override
    public MergedMappedSpreadsheetExporterProvider createSpreadsheetExporterProvider() {
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
                switch (name.value()) {
                    case RENAMED_PROVIDER_NAME:
                        return RENAMED_EXPORTER;
                    case PROVIDER_ONLY_NAME:
                        return PROVIDER_ONLY_EXPORTER;
                    default:
                        throw new IllegalArgumentException("Unknown exporter " + name);
                }
            }

            @Override
            public SpreadsheetExporterInfoSet spreadsheetExporterInfos() {
                return SpreadsheetExporterInfoSet.with(
                    Sets.of(
                        SpreadsheetExporterInfo.with(
                            RENAMED_URL,
                            SpreadsheetExporterName.with(RENAMED_PROVIDER_NAME)
                        ),
                        SpreadsheetExporterInfo.with(
                            PROVIDER_ONLY_URL,
                            SpreadsheetExporterName.with(PROVIDER_ONLY_NAME)
                        )
                    )
                );
            }
        };

        return MergedMappedSpreadsheetExporterProvider.with(
            SpreadsheetExporterInfoSet.EMPTY.concat(
                SpreadsheetExporterInfo.with(
                    RENAMED_URL,
                    SpreadsheetExporterName.with(RENAMED_RENAME_NAME)
                )
            ),
            provider
        );
    }

    // class............................................................................................................

    @Override
    public Class<MergedMappedSpreadsheetExporterProvider> type() {
        return MergedMappedSpreadsheetExporterProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}

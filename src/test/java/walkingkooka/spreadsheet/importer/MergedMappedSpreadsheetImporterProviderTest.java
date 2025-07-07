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
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.net.UrlPath;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class MergedMappedSpreadsheetImporterProviderTest implements SpreadsheetImporterProviderTesting<MergedMappedSpreadsheetImporterProvider>,
    SpreadsheetMetadataTesting {

    private final static ProviderContext CONTEXT = ProviderContexts.fake();

    @Test
    public void testWithNullInfosFails() {
        assertThrows(
            NullPointerException.class,
            () -> MergedMappedSpreadsheetImporterProvider.with(
                null,
                SpreadsheetImporterProviders.fake()
            )
        );
    }

    @Test
    public void testWithNullProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> MergedMappedSpreadsheetImporterProvider.with(
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

    private final static String RENAMED_RENAME_NAME = "renamed-rename-111";

    private final static String RENAMED_PROVIDER_NAME = "renamed-provider-111";

    private final static SpreadsheetImporter RENAMED_IMPORTER = new FakeSpreadsheetImporter() {
        @Override
        public String toString() {
            return RENAMED_RENAME_NAME;
        }
    };

    private final static AbsoluteUrl RENAMED_URL = Url.parseAbsolute("https://example.com/" + RENAMED_PROVIDER_NAME);

    @Test
    public void testSpreadsheetImporterSelectorWithRenamed() {
        this.spreadsheetImporterAndCheck(
            SpreadsheetImporterSelector.parse(RENAMED_RENAME_NAME + " (11.0)"),
            CONTEXT,
            RENAMED_IMPORTER
        );
    }

    private final static List<?> VALUES = Lists.of(11.0);

    @Test
    public void testSpreadsheetImporterNameWithRenamed() {
        this.spreadsheetImporterAndCheck(
            SpreadsheetImporterName.with(RENAMED_RENAME_NAME),
            VALUES,
            CONTEXT,
            RENAMED_IMPORTER
        );
    }

    private final static String PROVIDER_ONLY_NAME = "provider-only-222";

    private final static SpreadsheetImporter PROVIDER_ONLY_IMPORTER = new FakeSpreadsheetImporter() {
        @Override
        public String toString() {
            return PROVIDER_ONLY_NAME;
        }
    };

    private final static AbsoluteUrl PROVIDER_ONLY_URL = Url.parseAbsolute("https://example.com/" + PROVIDER_ONLY_NAME);

    @Test
    public void testSpreadsheetImporterSelectorWithProviderName() {
        this.spreadsheetImporterAndCheck(
            SpreadsheetImporterSelector.parse(PROVIDER_ONLY_NAME + " (11.0)"),
            CONTEXT,
            PROVIDER_ONLY_IMPORTER
        );
    }

    @Test
    public void testSpreadsheetImporterNameWithProviderName() {
        this.spreadsheetImporterAndCheck(
            SpreadsheetImporterName.with(PROVIDER_ONLY_NAME),
            VALUES,
            CONTEXT,
            PROVIDER_ONLY_IMPORTER
        );
    }

    @Test
    public void testSpreadsheetInfos() {
        this.spreadsheetImporterInfosAndCheck(
            SpreadsheetImporterInfo.with(
                RENAMED_URL,
                SpreadsheetImporterName.with(RENAMED_RENAME_NAME)
            ),
            SpreadsheetImporterInfo.with(
                PROVIDER_ONLY_URL,
                SpreadsheetImporterName.with(PROVIDER_ONLY_NAME)
            )
        );
    }

    @Override
    public MergedMappedSpreadsheetImporterProvider createSpreadsheetImporterProvider() {
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
                switch (name.value()) {
                    case RENAMED_PROVIDER_NAME:
                        return RENAMED_IMPORTER;
                    case PROVIDER_ONLY_NAME:
                        return PROVIDER_ONLY_IMPORTER;
                    default:
                        throw new IllegalArgumentException("Unknown importer " + name);
                }
            }

            @Override
            public SpreadsheetImporterInfoSet spreadsheetImporterInfos() {
                return SpreadsheetImporterInfoSet.with(
                    Sets.of(
                        SpreadsheetImporterInfo.with(
                            RENAMED_URL,
                            SpreadsheetImporterName.with(RENAMED_PROVIDER_NAME)
                        ),
                        SpreadsheetImporterInfo.with(
                            PROVIDER_ONLY_URL,
                            SpreadsheetImporterName.with(PROVIDER_ONLY_NAME)
                        )
                    )
                );
            }
        };

        return MergedMappedSpreadsheetImporterProvider.with(
            SpreadsheetImporterInfoSet.EMPTY.concat(
                SpreadsheetImporterInfo.with(
                    RENAMED_URL,
                    SpreadsheetImporterName.with(RENAMED_RENAME_NAME)
                )
            ),
            provider
        );
    }

    // class............................................................................................................

    @Override
    public Class<MergedMappedSpreadsheetImporterProvider> type() {
        return MergedMappedSpreadsheetImporterProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}

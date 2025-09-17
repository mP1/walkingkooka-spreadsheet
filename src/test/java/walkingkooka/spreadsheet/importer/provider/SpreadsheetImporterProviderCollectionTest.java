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
import walkingkooka.collect.set.Sets;
import walkingkooka.net.Url;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.importer.SpreadsheetImporter;
import walkingkooka.spreadsheet.importer.SpreadsheetImporters;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetImporterProviderCollectionTest implements SpreadsheetImporterProviderTesting<SpreadsheetImporterProviderCollection> {

    private final static SpreadsheetImporterName NAME = SpreadsheetImporterName.with("Test123");

    private final static List<?> VALUES = Lists.of("@@");

    private final static SpreadsheetImporterInfo INFO = SpreadsheetImporterInfo.with(
        Url.parseAbsolute("https://example.com/Test123"),
        NAME
    );

    private final static SpreadsheetImporter IMPORTER = SpreadsheetImporters.fake();

    private final static SpreadsheetImporterProvider PROVIDER = new SpreadsheetImporterProvider() {
        @Override
        public SpreadsheetImporter spreadsheetImporter(final SpreadsheetImporterSelector selector,
                                                       final ProviderContext context) {
            return this.spreadsheetImporter(
                selector.name(),
                VALUES,
                context
            );
        }

        @Override
        public SpreadsheetImporter spreadsheetImporter(final SpreadsheetImporterName name,
                                                       final List<?> values,
                                                       final ProviderContext context) {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(values, "values");
            Objects.requireNonNull(context, "context");

            if (false == NAME.equals(name)) {
                throw new IllegalArgumentException("Unknown importer " + name);
            }
            if (false == VALUES.equals(values)) {
                throw new IllegalArgumentException("Invalid values " + values);
            }

            return IMPORTER;
        }

        @Override
        public SpreadsheetImporterInfoSet spreadsheetImporterInfos() {
            return SpreadsheetImporterInfoSet.EMPTY.concat(INFO);
        }
    };

    private final static ProviderContext CONTEXT = ProviderContexts.fake();

    @Test
    public void testWithNullProvidersFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetImporterProviderCollection.with(null)
        );
    }

    @Test
    public void testSpreadsheetImporterSelectorMissingValuesFails() {
        this.spreadsheetImporterFails(
            SpreadsheetImporterProviderCollection.with(
                Sets.of(PROVIDER)
            ),
            SpreadsheetImporterSelector.parse("unknown123"),
            CONTEXT
        );
    }

    @Test
    public void testSpreadsheetImporterSelector() {
        this.spreadsheetImporterAndCheck(
            SpreadsheetImporterProviderCollection.with(
                Sets.of(PROVIDER)
            ),
            SpreadsheetImporterSelector.parse(NAME + " @@"),
            CONTEXT,
            IMPORTER
        );
    }

    @Test
    public void testSpreadsheetImporterNameMissingValuesFails() {
        this.spreadsheetImporterFails(
            SpreadsheetImporterProviderCollection.with(
                Sets.of(PROVIDER)
            ),
            NAME,
            Lists.of(),
            CONTEXT
        );
    }

    @Test
    public void testSpreadsheetImporterName() {
        this.spreadsheetImporterAndCheck(
            SpreadsheetImporterProviderCollection.with(
                Sets.of(PROVIDER)
            ),
            NAME,
            VALUES,
            CONTEXT,
            IMPORTER
        );
    }

    @Test
    public void testInfos() {
        this.spreadsheetImporterInfosAndCheck(
            SpreadsheetImporterProviderCollection.with(
                Sets.of(PROVIDER)
            ),
            INFO
        );
    }

    @Override
    public SpreadsheetImporterProviderCollection createSpreadsheetImporterProvider() {
        return SpreadsheetImporterProviderCollection.with(
            Sets.of(
                PROVIDER
            )
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetImporterProviderCollection> type() {
        return SpreadsheetImporterProviderCollection.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}

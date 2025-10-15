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
import walkingkooka.net.Url;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.export.SpreadsheetExporter;
import walkingkooka.spreadsheet.export.SpreadsheetExporters;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetExporterProviderCollectionTest implements SpreadsheetExporterProviderTesting<SpreadsheetExporterProviderCollection> {

    private final static SpreadsheetExporterName NAME = SpreadsheetExporterName.with("test-123");

    private final static List<?> VALUES = Lists.of("@@");

    private final static SpreadsheetExporterInfo INFO = SpreadsheetExporterInfo.with(
        Url.parseAbsolute("https://example.com/test-123"),
        NAME
    );

    private final static SpreadsheetExporter EXPORTER = SpreadsheetExporters.fake();

    private final static SpreadsheetExporterProvider PROVIDER = new SpreadsheetExporterProvider() {
        @Override
        public SpreadsheetExporter spreadsheetExporter(final SpreadsheetExporterSelector selector,
                                                       final ProviderContext context) {
            return this.spreadsheetExporter(
                selector.name(),
                VALUES,
                context
            );
        }

        @Override
        public SpreadsheetExporter spreadsheetExporter(final SpreadsheetExporterName name,
                                                       final List<?> values,
                                                       final ProviderContext context) {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(values, "values");
            Objects.requireNonNull(context, "context");

            if (false == NAME.equals(name)) {
                throw new IllegalArgumentException("Unknown exporter " + name);
            }
            if (false == VALUES.equals(values)) {
                throw new IllegalArgumentException("Invalid values " + values);
            }

            return EXPORTER;
        }

        @Override
        public SpreadsheetExporterInfoSet spreadsheetExporterInfos() {
            return SpreadsheetExporterInfoSet.EMPTY.concat(INFO);
        }
    };

    private final static ProviderContext CONTEXT = ProviderContexts.fake();

    @Test
    public void testWithNullProvidersFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExporterProviderCollection.with(null)
        );
    }

    @Test
    public void testSpreadsheetExporterSelectorMissingValuesFails() {
        this.spreadsheetExporterFails(
            SpreadsheetExporterProviderCollection.with(
                Sets.of(PROVIDER)
            ),
            SpreadsheetExporterSelector.parse("unknown123"),
            CONTEXT
        );
    }

    @Test
    public void testSpreadsheetExporterSelector() {
        this.spreadsheetExporterAndCheck(
            SpreadsheetExporterProviderCollection.with(
                Sets.of(PROVIDER)
            ),
            SpreadsheetExporterSelector.parse(NAME + " @@"),
            CONTEXT,
            EXPORTER
        );
    }

    @Test
    public void testSpreadsheetExporterNameMissingValuesFails() {
        this.spreadsheetExporterFails(
            SpreadsheetExporterProviderCollection.with(
                Sets.of(PROVIDER)
            ),
            NAME,
            Lists.of(),
            CONTEXT
        );
    }

    @Test
    public void testSpreadsheetExporterName() {
        this.spreadsheetExporterAndCheck(
            SpreadsheetExporterProviderCollection.with(
                Sets.of(PROVIDER)
            ),
            NAME,
            VALUES,
            CONTEXT,
            EXPORTER
        );
    }

    @Test
    public void testInfos() {
        this.spreadsheetExporterInfosAndCheck(
            SpreadsheetExporterProviderCollection.with(
                Sets.of(PROVIDER)
            ),
            INFO
        );
    }

    @Override
    public SpreadsheetExporterProviderCollection createSpreadsheetExporterProvider() {
        return SpreadsheetExporterProviderCollection.with(
            Sets.of(
                PROVIDER
            )
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetExporterProviderCollection> type() {
        return SpreadsheetExporterProviderCollection.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}

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
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.plugin.PluginAliasLikeTesting;
import walkingkooka.reflect.JavaVisibility;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetExporterAliasTest implements PluginAliasLikeTesting<SpreadsheetExporterName, SpreadsheetExporterSelector, SpreadsheetExporterAlias> {

    private final static SpreadsheetExporterName NAME = SpreadsheetExporterName.with("Hello");

    private final static Optional<SpreadsheetExporterSelector> SELECTOR = Optional.of(
        SpreadsheetExporterSelector.parse("exporter123")
    );

    private final static Optional<AbsoluteUrl> URL = Optional.of(
        Url.parseAbsolute("https://example.com/exporter123")
    );

    // with.............................................................................................................

    @Test
    public void testWithNullNameFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExporterAlias.with(
                null,
                SELECTOR,
                URL
            )
        );
    }

    @Test
    public void testWithNullSelectorFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExporterAlias.with(
                NAME,
                null,
                URL
            )
        );
    }

    @Test
    public void testWithNullUrlFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExporterAlias.with(
                NAME,
                SELECTOR,
                null
            )
        );
    }

    // Comparable.......................................................................................................

    @Override
    public SpreadsheetExporterAlias createComparable() {
        return SpreadsheetExporterAlias.with(
            NAME,
            SELECTOR,
            URL
        );
    }

    // parse............................................................................................................

    @Test
    public void testParse() {
        this.parseStringAndCheck(
            "alias1 name1 https://example.com",
            SpreadsheetExporterAlias.with(
                SpreadsheetExporterName.with("alias1"),
                Optional.of(
                    SpreadsheetExporterSelector.parse("name1")
                ),
                Optional.of(
                    Url.parseAbsolute("https://example.com")
                )
            )
        );
    }

    @Override
    public SpreadsheetExporterAlias parseString(final String text) {
        return SpreadsheetExporterAlias.parse(text);
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetExporterAlias> type() {
        return SpreadsheetExporterAlias.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}

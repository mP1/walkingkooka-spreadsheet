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
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.plugin.PluginAliasLikeTesting;
import walkingkooka.reflect.JavaVisibility;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetImporterAliasTest implements PluginAliasLikeTesting<SpreadsheetImporterName, SpreadsheetImporterSelector, SpreadsheetImporterAlias> {

    private final static SpreadsheetImporterName NAME = SpreadsheetImporterName.with("Hello");

    private final static Optional<SpreadsheetImporterSelector> SELECTOR = Optional.of(
        SpreadsheetImporterSelector.parse("importer123")
    );

    private final static Optional<AbsoluteUrl> URL = Optional.of(
        Url.parseAbsolute("https://example.com/importer123")
    );

    // with.............................................................................................................

    @Test
    public void testWithNullNameFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetImporterAlias.with(
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
            () -> SpreadsheetImporterAlias.with(
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
            () -> SpreadsheetImporterAlias.with(
                NAME,
                SELECTOR,
                null
            )
        );
    }

    // Comparable.......................................................................................................

    @Override
    public SpreadsheetImporterAlias createComparable() {
        return SpreadsheetImporterAlias.with(
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
            SpreadsheetImporterAlias.with(
                SpreadsheetImporterName.with("alias1"),
                Optional.of(
                    SpreadsheetImporterSelector.parse("name1")
                ),
                Optional.of(
                    Url.parseAbsolute("https://example.com")
                )
            )
        );
    }

    @Override
    public SpreadsheetImporterAlias parseString(final String text) {
        return SpreadsheetImporterAlias.parse(text);
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetImporterAlias> type() {
        return SpreadsheetImporterAlias.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}

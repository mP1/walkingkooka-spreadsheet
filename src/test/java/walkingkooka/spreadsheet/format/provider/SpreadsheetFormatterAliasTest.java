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

package walkingkooka.spreadsheet.format.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.plugin.PluginAliasLikeTesting;
import walkingkooka.reflect.JavaVisibility;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetFormatterAliasTest implements PluginAliasLikeTesting<SpreadsheetFormatterName, SpreadsheetFormatterSelector, SpreadsheetFormatterAlias> {

    private final static SpreadsheetFormatterName NAME = SpreadsheetFormatterName.with("Hello");

    private final static Optional<SpreadsheetFormatterSelector> SELECTOR = Optional.of(
        SpreadsheetFormatterSelector.parse("formatter123")
    );

    private final static Optional<AbsoluteUrl> URL = Optional.of(
        Url.parseAbsolute("https://example.com/formatter123")
    );

    // with.............................................................................................................

    @Test
    public void testWithNullNameFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormatterAlias.with(
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
            () -> SpreadsheetFormatterAlias.with(
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
            () -> SpreadsheetFormatterAlias.with(
                NAME,
                SELECTOR,
                null
            )
        );
    }

    // Comparable.......................................................................................................

    @Override
    public SpreadsheetFormatterAlias createComparable() {
        return SpreadsheetFormatterAlias.with(
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
            SpreadsheetFormatterAlias.with(
                SpreadsheetFormatterName.with("alias1"),
                Optional.of(
                    SpreadsheetFormatterSelector.parse("name1")
                ),
                Optional.of(
                    Url.parseAbsolute("https://example.com")
                )
            )
        );
    }

    @Override
    public SpreadsheetFormatterAlias parseString(final String text) {
        return SpreadsheetFormatterAlias.parse(text);
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetFormatterAlias> type() {
        return SpreadsheetFormatterAlias.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}

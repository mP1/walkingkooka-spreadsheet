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

package walkingkooka.spreadsheet.parser;

import walkingkooka.Cast;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.plugin.PluginAlias;
import walkingkooka.plugin.PluginAliasLike;
import walkingkooka.text.printer.IndentingPrinter;

import java.util.Objects;
import java.util.Optional;

public final class SpreadsheetParserAlias implements PluginAliasLike<SpreadsheetParserName, SpreadsheetParserSelector, SpreadsheetParserAlias> {

    public static SpreadsheetParserAlias parse(final String text) {
        return with(
            PluginAlias.parse(
                text,
                SpreadsheetParserPluginHelper.INSTANCE
            )
        );
    }

    public static SpreadsheetParserAlias with(final SpreadsheetParserName name,
                                              final Optional<SpreadsheetParserSelector> selector,
                                              final Optional<AbsoluteUrl> url) {
        return with(
            PluginAlias.with(
                name,
                selector,
                url
            )
        );
    }

    static SpreadsheetParserAlias with(final PluginAlias<SpreadsheetParserName, SpreadsheetParserSelector> pluginAlias) {
        return new SpreadsheetParserAlias(
            Objects.requireNonNull(pluginAlias, "pluginAlias")
        );
    }

    private SpreadsheetParserAlias(final PluginAlias<SpreadsheetParserName, SpreadsheetParserSelector> pluginAlias) {
        this.pluginAlias = pluginAlias;
    }

    // PluginAliasLike..................................................................................................

    @Override
    public SpreadsheetParserName name() {
        return this.pluginAlias.name();
    }

    @Override
    public Optional<SpreadsheetParserSelector> selector() {
        return this.pluginAlias.selector();
    }

    @Override
    public Optional<AbsoluteUrl> url() {
        return this.pluginAlias.url();
    }

    @Override
    public String text() {
        return this.pluginAlias.text();
    }

    // Comparable.......................................................................................................

    @Override
    public int compareTo(final SpreadsheetParserAlias other) {
        return this.pluginAlias.compareTo(other.pluginAlias);
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        this.pluginAlias.printTree(printer);
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.pluginAlias.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof SpreadsheetParserAlias && this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetParserAlias other) {
        return this.pluginAlias.equals(other.pluginAlias);
    }

    @Override
    public String toString() {
        return this.pluginAlias.text();
    }

    private final PluginAlias<SpreadsheetParserName, SpreadsheetParserSelector> pluginAlias;
}

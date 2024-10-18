/*
 * Copyright 2024 Miroslav Pokorny (github.com/mP1)
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

import walkingkooka.Cast;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.plugin.PluginAlias;
import walkingkooka.plugin.PluginAliasLike;
import walkingkooka.text.printer.IndentingPrinter;

import java.util.Objects;
import java.util.Optional;

public final class SpreadsheetImporterAlias implements PluginAliasLike<SpreadsheetImporterName, SpreadsheetImporterSelector, SpreadsheetImporterAlias> {

    public static SpreadsheetImporterAlias parse(final String text) {
        return with(
                PluginAlias.parse(
                        text,
                        SpreadsheetImporterPluginHelper.INSTANCE
                )
        );
    }

    public static SpreadsheetImporterAlias with(final SpreadsheetImporterName name,
                                                final Optional<SpreadsheetImporterSelector> selector,
                                                final Optional<AbsoluteUrl> url) {
        return new SpreadsheetImporterAlias(
                PluginAlias.with(
                        name,
                        selector,
                        url
                )
        );
    }

    static SpreadsheetImporterAlias with(final PluginAlias<SpreadsheetImporterName, SpreadsheetImporterSelector> pluginAlias) {
        return new SpreadsheetImporterAlias(
                Objects.requireNonNull(pluginAlias, "pluginAlias")
        );
    }

    private SpreadsheetImporterAlias(final PluginAlias<SpreadsheetImporterName, SpreadsheetImporterSelector> pluginAlias) {
        this.pluginAlias = pluginAlias;
    }

    // PluginAliasLike..................................................................................................

    @Override
    public SpreadsheetImporterName name() {
        return this.pluginAlias.name();
    }

    @Override
    public Optional<SpreadsheetImporterSelector> selector() {
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
    public int compareTo(final SpreadsheetImporterAlias other) {
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
        return this == other || other instanceof SpreadsheetImporterAlias && this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetImporterAlias other) {
        return this.pluginAlias.equals(other.pluginAlias);
    }

    @Override
    public String toString() {
        return this.pluginAlias.text();
    }

    private final PluginAlias<SpreadsheetImporterName, SpreadsheetImporterSelector> pluginAlias;
}

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

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.UrlPath;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.export.SpreadsheetExporter;
import walkingkooka.spreadsheet.export.SpreadsheetExporters;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

final class SpreadsheetExportSpreadsheetExporterProvider implements SpreadsheetExporterProvider {

    final static SpreadsheetExportSpreadsheetExporterProvider INSTANCE = new SpreadsheetExportSpreadsheetExporterProvider();

    private SpreadsheetExportSpreadsheetExporterProvider() {
        super();
    }

    @Override
    public SpreadsheetExporter spreadsheetExporter(final SpreadsheetExporterSelector selector,
                                                   final ProviderContext context) {
        Objects.requireNonNull(selector, "selector");
        Objects.requireNonNull(context, "context");

        return selector.evaluateValueText(
            this,
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

        return this.spreadsheetExporter0(
            name,
            Lists.immutable(values),
            context
        );
    }

    private SpreadsheetExporter spreadsheetExporter0(final SpreadsheetExporterName name,
                                                     final List<?> values,
                                                     final ProviderContext context) {
        final int count = values.size();

        final SpreadsheetExporter exporter;

        switch (name.value()) {
            case SpreadsheetExporterName.COLLECTION_STRING:
                exporter = SpreadsheetExporters.collection(
                    values.stream()
                        .map(e -> (SpreadsheetExporter) e)
                        .collect(Collectors.toList())
                );
                break;
            case SpreadsheetExporterName.EMPTY_STRING:
                if (0 != count) {
                    throw new IllegalArgumentException("Got " + count + " expected 0 values");
                }
                exporter = SpreadsheetExporters.empty();
                break;
            case SpreadsheetExporterName.JSON_STRING:
                if (0 != count) {
                    throw new IllegalArgumentException("Got " + count + " expected 0 values");
                }
                exporter = SpreadsheetExporters.json();
                break;
            default:
                throw new IllegalArgumentException("Unknown exporter " + name);
        }

        return exporter;
    }

    // spreadsheetExporterInfos........................................................................................

    @Override
    public SpreadsheetExporterInfoSet spreadsheetExporterInfos() {
        return INFOS;
    }

    private final static SpreadsheetExporterInfoSet INFOS = SpreadsheetExporterInfoSet.with(
        Sets.of(
            spreadsheetExporterInfo(SpreadsheetExporterName.COLLECTION),
            spreadsheetExporterInfo(SpreadsheetExporterName.EMPTY),
            spreadsheetExporterInfo(SpreadsheetExporterName.JSON)
        )
    );

    private static SpreadsheetExporterInfo spreadsheetExporterInfo(final SpreadsheetExporterName name) {
        return SpreadsheetExporterInfo.with(
            SpreadsheetExporterProviders.BASE_URL.appendPath(UrlPath.parse(name.value())),
            name
        );
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}

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

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.UrlPath;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.importer.SpreadsheetImporter;
import walkingkooka.spreadsheet.importer.SpreadsheetImporters;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

final class SpreadsheetImportSpreadsheetImporterProvider implements SpreadsheetImporterProvider {

    final static SpreadsheetImportSpreadsheetImporterProvider INSTANCE = new SpreadsheetImportSpreadsheetImporterProvider();

    private SpreadsheetImportSpreadsheetImporterProvider() {
        super();
    }

    @Override
    public SpreadsheetImporter spreadsheetImporter(final SpreadsheetImporterSelector selector,
                                                   final ProviderContext context) {
        Objects.requireNonNull(selector, "selector");
        Objects.requireNonNull(context, "context");

        return selector.evaluateValueText(
            this,
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

        return this.spreadsheetImporter0(
            name,
            Lists.immutable(values),
            context
        );
    }

    private SpreadsheetImporter spreadsheetImporter0(final SpreadsheetImporterName name,
                                                     final List<?> values,
                                                     final ProviderContext context) {
        final int count = values.size();

        final SpreadsheetImporter importer;

        switch (name.value()) {
            case SpreadsheetImporterName.COLLECTION_STRING:
                importer = SpreadsheetImporters.collection(
                    values.stream()
                        .map(e -> (SpreadsheetImporter) e)
                        .collect(Collectors.toList())
                );
                break;
            case SpreadsheetImporterName.EMPTY_STRING:
                if (0 != count) {
                    throw new IllegalArgumentException("Got " + count + " expected 0 values");
                }
                importer = SpreadsheetImporters.empty();
                break;
            case SpreadsheetImporterName.JSON_STRING:
                if (0 != count) {
                    throw new IllegalArgumentException("Got " + count + " expected 0 values");
                }
                importer = SpreadsheetImporters.json();
                break;
            default:
                throw new IllegalArgumentException("Unknown importer " + name);
        }

        return importer;
    }

    // spreadsheetImporterInfos........................................................................................

    @Override
    public SpreadsheetImporterInfoSet spreadsheetImporterInfos() {
        return INFOS;
    }

    private final static SpreadsheetImporterInfoSet INFOS = SpreadsheetImporterInfoSet.with(
        Sets.of(
            spreadsheetImporterInfo(SpreadsheetImporterName.COLLECTION),
            spreadsheetImporterInfo(SpreadsheetImporterName.EMPTY),
            spreadsheetImporterInfo(SpreadsheetImporterName.JSON)
        )
    );

    private static SpreadsheetImporterInfo spreadsheetImporterInfo(final SpreadsheetImporterName name) {
        return SpreadsheetImporterInfo.with(
            SpreadsheetImporterProviders.BASE_URL.appendPath(UrlPath.parse(name.value())),
            name
        );
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}

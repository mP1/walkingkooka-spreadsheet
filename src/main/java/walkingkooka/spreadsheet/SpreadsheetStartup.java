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

package walkingkooka.spreadsheet;

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.convert.provider.ConvertProviderStartup;
import walkingkooka.convert.provider.ConverterName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.plugin.PluginStartup;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorAliasSet;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorInfo;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorInfoSet;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorName;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorNameList;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorSelector;
import walkingkooka.spreadsheet.convert.provider.MissingConverter;
import walkingkooka.spreadsheet.convert.provider.MissingConverterSet;
import walkingkooka.spreadsheet.convert.provider.MissingConverterValue;
import walkingkooka.spreadsheet.engine.SpreadsheetCellFindQuery;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.collection.SpreadsheetCellSet;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterAliasSet;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterInfo;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterInfoSet;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterName;
import walkingkooka.spreadsheet.expression.SpreadsheetFunctionName;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterAliasSet;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterInfo;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterInfoSet;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterName;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterAliasSet;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterInfo;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterInfoSet;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserAliasSet;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserInfo;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserInfoSet;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserName;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceSet;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReferenceSet;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceSet;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameSet;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReferenceSet;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewport;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportRectangle;
import walkingkooka.storage.StorageStartup;
import walkingkooka.template.TemplateStartup;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.text.TreeTextStartup;
import walkingkooka.validation.ValidationStartup;

/**
 * Used to force all values types to register their {@link JsonNodeContext#register}
 */
public final class SpreadsheetStartup implements PublicStaticHelper {

    static {
        PluginStartup.init();
        StorageStartup.init();
        TemplateStartup.init();
        TreeTextStartup.init();
        ValidationStartup.init();

        // register json marshallers/unmarshallers.
        SpreadsheetCell.with(
            SpreadsheetSelection.A1,
            SpreadsheetFormula.EMPTY
        );
        
        SpreadsheetColumn.with(
            SpreadsheetSelection.A1.column()
        );

        SpreadsheetRow.with(
            SpreadsheetSelection.A1.row()
        );

        SpreadsheetLabelMapping.with(
            SpreadsheetSelection.labelName("Hello"),
            SpreadsheetSelection.A1
        );

        SpreadsheetDelta.EMPTY.window();

        SpreadsheetErrorKind.ERROR.setMessage("hello");

        SpreadsheetFunctionName.with("hello");

        SpreadsheetViewport.with(
            SpreadsheetViewportRectangle.with(
                SpreadsheetSelection.A1,
                1,
                2
            )
        );

        SpreadsheetCellFindQuery.empty();

        final AbsoluteUrl url = Url.parseAbsolute("https://example.com");

        {
            SpreadsheetComparatorNameList.EMPTY.size();

            SpreadsheetComparatorAliasSet.EMPTY.size();
            SpreadsheetComparatorInfoSet.with(
                Lists.of(
                    SpreadsheetComparatorInfo.with(
                        url,
                        SpreadsheetComparatorName.DATE
                    )
                )
            );
            SpreadsheetComparatorSelector.parse("hello");
        }

        ConvertProviderStartup.init();

        MissingConverterSet.EMPTY.concat(
            MissingConverter.with(
                ConverterName.HAS_TEXT,
                Sets.of(
                    MissingConverterValue.with(
                        "Hello",
                        "text"
                    )
                )
            )
        );

        {
            SpreadsheetExporterAliasSet.EMPTY.size();
            SpreadsheetExporterInfoSet.with(
                Lists.of(
                    SpreadsheetExporterInfo.with(
                        url,
                        SpreadsheetExporterName.JSON
                    )
                )
            );
        }

        {
            SpreadsheetFormatterAliasSet.EMPTY.size();
            SpreadsheetFormatterInfoSet.with(
                Lists.of(
                    SpreadsheetFormatterInfo.with(
                        url,
                        SpreadsheetFormatterName.GENERAL
                    )
                )
            );
        }

        {
            SpreadsheetImporterAliasSet.EMPTY.size();
            SpreadsheetImporterInfoSet.with(
                Lists.of(
                    SpreadsheetImporterInfo.with(
                        url,
                        SpreadsheetImporterName.JSON
                    )
                )
            );
        }

        {
            SpreadsheetParserAliasSet.EMPTY.size();
            SpreadsheetParserInfoSet.with(
                Lists.of(
                    SpreadsheetParserInfo.with(
                        url,
                        SpreadsheetParserName.TIME
                    )
                )
            );
        }

        // Sets
        MissingConverterSet.EMPTY.size();

        SpreadsheetCellSet.EMPTY.size();

        SpreadsheetColumnReferenceSet.EMPTY.size();
        SpreadsheetCellReferenceSet.EMPTY.size();
        SpreadsheetExpressionReferenceSet.EMPTY.size();
        SpreadsheetLabelNameSet.EMPTY.size();
        SpreadsheetRowReferenceSet.EMPTY.size();

        SpreadsheetId.with(1);

        SpreadsheetMetadata.EMPTY.id();
    }

    public static void init() {
        // NOP
    }

    private SpreadsheetStartup() {
        throw new UnsupportedOperationException();
    }
}

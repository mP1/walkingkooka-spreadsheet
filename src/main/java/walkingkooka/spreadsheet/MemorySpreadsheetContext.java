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

import walkingkooka.ToStringBuilder;
import walkingkooka.collect.map.Maps;
import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.Fraction;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.UrlPathName;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.routing.Router;
import walkingkooka.spreadsheet.conditionalformat.SpreadsheetConditionalFormattingRule;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.spreadsheet.engine.SpreadsheetEngines;
import walkingkooka.spreadsheet.format.SpreadsheetTextFormatter;
import walkingkooka.spreadsheet.hateos.SpreadsheetHateosHandlers;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.store.meta.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.store.range.SpreadsheetRangeStore;
import walkingkooka.spreadsheet.store.range.SpreadsheetRangeStores;
import walkingkooka.spreadsheet.store.reference.SpreadsheetReferenceStore;
import walkingkooka.spreadsheet.store.reference.SpreadsheetReferenceStores;
import walkingkooka.spreadsheet.store.repo.StoreRepositories;
import walkingkooka.spreadsheet.store.repo.StoreRepository;
import walkingkooka.spreadsheet.store.security.SpreadsheetGroupStores;
import walkingkooka.spreadsheet.store.security.SpreadsheetUserStores;
import walkingkooka.tree.Node;
import walkingkooka.tree.expression.ExpressionNodeName;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A {@link SpreadsheetContext} that creates a new {@link StoreRepository} for unknown {@link SpreadsheetId}.
 * There is no way to delete existing spreadsheets.
 */
final class MemorySpreadsheetContext<N extends Node<N, ?, ?, ?>> implements SpreadsheetContext {

    /**
     * Creates a new empty {@link MemorySpreadsheetContext}
     */
    static <N extends Node<N, ?, ?, ?>> MemorySpreadsheetContext with(final AbsoluteUrl base,
                                                                      final HateosContentType<N> contentType,
                                                                      final Function<BigDecimal, Fraction> fractioner,
                                                                      final Supplier<SpreadsheetMetadata> metadata,
                                                                      final Function<SpreadsheetId, Converter> spreadsheetIdConverter,
                                                                      final Function<SpreadsheetId, DateTimeContext> spreadsheetIdDateTimeContext,
                                                                      final Function<SpreadsheetId, DecimalNumberContext> spreadsheetIdDecimalFormatContext,
                                                                      final Function<SpreadsheetId, SpreadsheetTextFormatter<?>> spreadsheetIdDefaultSpreadsheetTextFormatter,
                                                                      final Function<SpreadsheetId, BiFunction<ExpressionNodeName, List<Object>, Object>> spreadsheetIdFunctions,
                                                                      final Function<SpreadsheetId, String> spreadsheetIdGeneralDecimalFormatPattern,
                                                                      final Function<SpreadsheetId, Function<String, Color>> spreadsheetIdNameToColor,
                                                                      final Function<SpreadsheetId, Function<Integer, Color>> spreadsheetIdNumberToColor,
                                                                      final Function<SpreadsheetId, Integer> spreadsheetIdWidth) {
        Objects.requireNonNull(base, "base");
        Objects.requireNonNull(contentType, "contentType");
        Objects.requireNonNull(fractioner, "fractioner");
        Objects.requireNonNull(metadata, "metadata");
        Objects.requireNonNull(spreadsheetIdConverter, "spreadsheetIdConverter");
        Objects.requireNonNull(spreadsheetIdDateTimeContext, "spreadsheetIdDateTimeContext");
        Objects.requireNonNull(spreadsheetIdDecimalFormatContext, "spreadsheetIdDecimalFormatContext");
        Objects.requireNonNull(spreadsheetIdDefaultSpreadsheetTextFormatter, "spreadsheetIdDefaultSpreadsheetTextFormatter");
        Objects.requireNonNull(spreadsheetIdFunctions, "spreadsheetIdFunctions");
        Objects.requireNonNull(spreadsheetIdGeneralDecimalFormatPattern, "spreadsheetIdGeneralDecimalFormatPattern");
        Objects.requireNonNull(spreadsheetIdNameToColor, "spreadsheetIdNameToColor");
        Objects.requireNonNull(spreadsheetIdNumberToColor, "spreadsheetIdNumberToColor");
        Objects.requireNonNull(spreadsheetIdWidth, "spreadsheetIdWidth");

        return new MemorySpreadsheetContext<>(base,
                contentType,
                fractioner,
                metadata,
                spreadsheetIdConverter,
                spreadsheetIdDateTimeContext,
                spreadsheetIdDecimalFormatContext,
                spreadsheetIdDefaultSpreadsheetTextFormatter,
                spreadsheetIdFunctions,
                spreadsheetIdGeneralDecimalFormatPattern,
                spreadsheetIdNameToColor,
                spreadsheetIdNumberToColor,
                spreadsheetIdWidth);
    }

    private MemorySpreadsheetContext(final AbsoluteUrl base,
                                     final HateosContentType<N> contentType,
                                     final Function<BigDecimal, Fraction> fractioner,
                                     final Supplier<SpreadsheetMetadata> metadata,
                                     final Function<SpreadsheetId, Converter> spreadsheetIdConverter,
                                     final Function<SpreadsheetId, DateTimeContext> spreadsheetIdDateTimeContext,
                                     final Function<SpreadsheetId, DecimalNumberContext> spreadsheetIdDecimalFormatContext,
                                     final Function<SpreadsheetId, SpreadsheetTextFormatter<?>> spreadsheetIdDefaultSpreadsheetTextFormatter,
                                     final Function<SpreadsheetId, BiFunction<ExpressionNodeName, List<Object>, Object>> spreadsheetIdFunctions,
                                     final Function<SpreadsheetId, String> spreadsheetIdGeneralDecimalFormatPattern,
                                     final Function<SpreadsheetId, Function<String, Color>> spreadsheetIdNameToColor,
                                     final Function<SpreadsheetId, Function<Integer, Color>> spreadsheetIdNumberToColor,
                                     final Function<SpreadsheetId, Integer> spreadsheetIdWidth) {
        super();

        this.base = base;
        this.contentType = contentType;
        this.fractioner = fractioner;
        this.metadata = metadata;

        this.spreadsheetIdConverter = spreadsheetIdConverter;
        this.spreadsheetIdDateTimeContext = spreadsheetIdDateTimeContext;
        this.spreadsheetIdDecimalFormatContext = spreadsheetIdDecimalFormatContext;
        this.spreadsheetIdDefaultSpreadsheetTextFormatter = spreadsheetIdDefaultSpreadsheetTextFormatter;
        this.spreadsheetIdFunctions = spreadsheetIdFunctions;
        this.spreadsheetIdGeneralDecimalFormatPattern = spreadsheetIdGeneralDecimalFormatPattern;
        this.spreadsheetIdNameToColor = spreadsheetIdNameToColor;
        this.spreadsheetIdNumberToColor = spreadsheetIdNumberToColor;
        this.spreadsheetIdWidth = spreadsheetIdWidth;
    }

    @Override
    public Converter converter(final SpreadsheetId id) {
        return this.spreadsheetIdConverter.apply(id);
    }

    private final Function<SpreadsheetId, Converter> spreadsheetIdConverter;

    @Override
    public DateTimeContext dateTimeContext(final SpreadsheetId id) {
        return this.spreadsheetIdDateTimeContext.apply(id);
    }

    private final Function<SpreadsheetId, DateTimeContext> spreadsheetIdDateTimeContext;

    @Override
    public DecimalNumberContext decimalNumberContext(final SpreadsheetId id) {
        return this.spreadsheetIdDecimalFormatContext.apply(id);
    }

    private final Function<SpreadsheetId, DecimalNumberContext> spreadsheetIdDecimalFormatContext;

    @Override
    public SpreadsheetTextFormatter<?> defaultSpreadsheetTextFormatter(final SpreadsheetId id) {
        return this.spreadsheetIdDefaultSpreadsheetTextFormatter.apply(id);
    }

    private final Function<SpreadsheetId, SpreadsheetTextFormatter<?>> spreadsheetIdDefaultSpreadsheetTextFormatter;

    @Override
    public BiFunction<ExpressionNodeName, List<Object>, Object> functions(final SpreadsheetId id) {
        return this.spreadsheetIdFunctions.apply(id);
    }

    private final Function<SpreadsheetId, BiFunction<ExpressionNodeName, List<Object>, Object>> spreadsheetIdFunctions;

    @Override
    public String generalDecimalFormatPattern(final SpreadsheetId id) {
        return this.spreadsheetIdGeneralDecimalFormatPattern.apply(id);
    }

    private final Function<SpreadsheetId, String> spreadsheetIdGeneralDecimalFormatPattern;

    // hateosRouter.....................................................................................................

    @Override
    public Router<HttpRequestAttribute<?>, BiConsumer<HttpRequest, HttpResponse>> hateosRouter(final SpreadsheetId id) {
        SpreadsheetContext.checkId(id);

        Router<HttpRequestAttribute<?>, BiConsumer<HttpRequest, HttpResponse>> hateosRouter = this.idToHateosRouter.get(id);
        if (null == hateosRouter) {
            hateosRouter = this.createHateosHandler(id);

            this.idToHateosRouter.put(id, hateosRouter);
        }
        return hateosRouter;
    }

    private final Map<SpreadsheetId, Router<HttpRequestAttribute<?>, BiConsumer<HttpRequest, HttpResponse>>> idToHateosRouter = Maps.sorted();

    /**
     * Factory that creates a {@link Router} for the given {@link SpreadsheetId spreadsheet}.
     */
    private Router<HttpRequestAttribute<?>, BiConsumer<HttpRequest, HttpResponse>> createHateosHandler(final SpreadsheetId id) {
        final StoreRepository storeRepository = this.storeRepository(id);

        final SpreadsheetCellStore cellStore = storeRepository.cells();
        final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferencesStore = storeRepository.cellReferences();
        final SpreadsheetLabelStore labelStore = storeRepository.labels();
        final SpreadsheetReferenceStore<SpreadsheetLabelName> labelReferencesStore = storeRepository.labelReferences();
        final SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCellStore = storeRepository.rangeToCells();
        final SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRules = storeRepository.rangeToConditionalFormattingRules();

        final SpreadsheetEngine engine = SpreadsheetEngines.basic(id,
                cellStore,
                cellReferencesStore,
                labelStore,
                labelReferencesStore,
                rangeToCellStore,
                rangeToConditionalFormattingRules);

        final Converter converter = this.spreadsheetIdConverter.apply(id);
        final BiFunction<ExpressionNodeName, List<Object>, Object> functions = this.spreadsheetIdFunctions.apply(id);
        final Function<Integer, Color> numberToColor = this.spreadsheetIdNumberToColor.apply(id);
        final Function<String, Color> nameToColor = this.spreadsheetIdNameToColor.apply(id);
        final String generalDecimalFormatPattern = this.spreadsheetIdGeneralDecimalFormatPattern.apply(id);
        final int width = this.spreadsheetIdWidth.apply(id);
        final Function<BigDecimal, Fraction> fractioner = this.fractioner;
        final SpreadsheetTextFormatter<?> defaultSpreadsheetTextFormatter = this.defaultSpreadsheetTextFormatter(id);

        final SpreadsheetEngineContext engineContext = SpreadsheetEngineContexts.basic(functions,
                engine,
                labelStore,
                converter,
                this.decimalNumberContext(id),
                this.dateTimeContext(id),
                numberToColor,
                nameToColor,
                generalDecimalFormatPattern,
                width,
                fractioner,
                defaultSpreadsheetTextFormatter);

        return SpreadsheetHateosHandlers.router(this.baseWithSpreadsheetId(id),
                this.contentType,
                SpreadsheetHateosHandlers.copyCells(engine, engineContext),
                SpreadsheetHateosHandlers.deleteColumns(engine, engineContext),
                SpreadsheetHateosHandlers.deleteRows(engine, engineContext),
                SpreadsheetHateosHandlers.insertColumns(engine, engineContext),
                SpreadsheetHateosHandlers.insertRows(engine, engineContext),
                SpreadsheetHateosHandlers.loadCell(engine, engineContext),
                SpreadsheetHateosHandlers.saveCell(engine, engineContext));
    }

    /**
     * Appends the spreadsheet id to the {@link #base}.
     */
    private AbsoluteUrl baseWithSpreadsheetId(final SpreadsheetId id) {
        final AbsoluteUrl base = this.base;
        return base.setPath(base.path()
                .append(UrlPathName.with(String.valueOf(id.value()))));
    }

    private final AbsoluteUrl base;
    private final HateosContentType<N> contentType;
    private final Function<BigDecimal, Fraction> fractioner;

    // metadata.........................................................................................................

    @Override
    public SpreadsheetMetadata metadataWithDefaults() {
        return this.metadata.get();
    }

    private final Supplier<SpreadsheetMetadata> metadata;

    @Override
    public Function<String, Color> nameToColor(final SpreadsheetId id) {
        return this.spreadsheetIdNameToColor.apply(id);
    }

    private final Function<SpreadsheetId, Function<String, Color>> spreadsheetIdNameToColor;

    @Override
    public Function<Integer, Color> numberToColor(final SpreadsheetId id) {
        return this.spreadsheetIdNumberToColor.apply(id);
    }

    private final Function<SpreadsheetId, Function<Integer, Color>> spreadsheetIdNumberToColor;

    @Override
    public StoreRepository storeRepository(final SpreadsheetId id) {
        Objects.requireNonNull(id, "id");

        StoreRepository storeRepository = this.idToStoreRepository.get(id);
        if (null == storeRepository) {
            storeRepository = this.createStoreRepository();
            this.idToStoreRepository.put(id, storeRepository);
        }
        return storeRepository;
    }

    private StoreRepository createStoreRepository() {
        return StoreRepositories.basic(SpreadsheetCellStores.treeMap(),
                SpreadsheetReferenceStores.treeMap(),
                SpreadsheetGroupStores.treeMap(),
                SpreadsheetLabelStores.treeMap(),
                SpreadsheetReferenceStores.treeMap(),
                SpreadsheetMetadataStores.treeMap(),
                SpreadsheetRangeStores.treeMap(),
                SpreadsheetRangeStores.treeMap(),
                SpreadsheetUserStores.treeMap());
    }

    private final Map<SpreadsheetId, StoreRepository> idToStoreRepository = Maps.sorted();

    @Override
    public int width(final SpreadsheetId id) {
        return this.spreadsheetIdWidth.apply(id);
    }

    private final Function<SpreadsheetId, Integer> spreadsheetIdWidth;

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .label("base").value(this.base)
                .label("contentType").value(this.contentType)
                .build();
    }
}
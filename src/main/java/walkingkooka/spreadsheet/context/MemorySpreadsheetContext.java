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

package walkingkooka.spreadsheet.context;

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
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.conditionalformat.SpreadsheetConditionalFormattingRule;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineEvaluation;
import walkingkooka.spreadsheet.engine.SpreadsheetEngines;
import walkingkooka.spreadsheet.engine.hateos.SpreadsheetEngineHateosHandlers;
import walkingkooka.spreadsheet.format.SpreadsheetTextFormatter;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.reference.store.SpreadsheetRangeStore;
import walkingkooka.spreadsheet.reference.store.SpreadsheetRangeStores;
import walkingkooka.spreadsheet.reference.store.SpreadsheetReferenceStore;
import walkingkooka.spreadsheet.reference.store.SpreadsheetReferenceStores;
import walkingkooka.spreadsheet.security.store.SpreadsheetGroupStores;
import walkingkooka.spreadsheet.security.store.SpreadsheetUserStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.tree.Node;
import walkingkooka.tree.expression.ExpressionNodeName;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A {@link SpreadsheetContext} that creates a new {@link SpreadsheetStoreRepository} for unknown {@link SpreadsheetId}.
 * There is no way to delete existing spreadsheets.
 */
final class MemorySpreadsheetContext<N extends Node<N, ?, ?, ?>> implements SpreadsheetContext {

    /**
     * Creates a new empty {@link MemorySpreadsheetContext}
     */
    static <N extends Node<N, ?, ?, ?>> MemorySpreadsheetContext with(final AbsoluteUrl base,
                                                                      final HateosContentType<N> contentType,
                                                                      final Function<BigDecimal, Fraction> fractioner,
                                                                      final Function<Optional<Locale>, SpreadsheetMetadata> metadata,
                                                                      final Function<SpreadsheetId, Converter> spreadsheetIdConverter,
                                                                      final Function<SpreadsheetId, DateTimeContext> spreadsheetIdDateTimeContext,
                                                                      final Function<SpreadsheetId, DecimalNumberContext> spreadsheetIdDecimalNumberContext,
                                                                      final Function<SpreadsheetId, SpreadsheetTextFormatter<?>> spreadsheetIdDefaultSpreadsheetTextFormatter,
                                                                      final Function<SpreadsheetId, BiFunction<ExpressionNodeName, List<Object>, Object>> spreadsheetIdFunctions,
                                                                      final Function<SpreadsheetId, String> spreadsheetIdGeneralDecimalFormatPattern,
                                                                      final Function<SpreadsheetId, Function<String, Optional<Color>>> spreadsheetIdNameToColor,
                                                                      final Function<SpreadsheetId, Function<Integer, Optional<Color>>> spreadsheetIdNumberToColor,
                                                                      final Function<SpreadsheetId, Integer> spreadsheetIdWidth) {
        Objects.requireNonNull(base, "base");
        Objects.requireNonNull(contentType, "contentType");
        Objects.requireNonNull(fractioner, "fractioner");
        Objects.requireNonNull(metadata, "metadata");
        Objects.requireNonNull(spreadsheetIdConverter, "spreadsheetIdConverter");
        Objects.requireNonNull(spreadsheetIdDateTimeContext, "spreadsheetIdDateTimeContext");
        Objects.requireNonNull(spreadsheetIdDecimalNumberContext, "spreadsheetIdDecimalNumberContext");
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
                spreadsheetIdDecimalNumberContext,
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
                                     final Function<Optional<Locale>, SpreadsheetMetadata> metadata,
                                     final Function<SpreadsheetId, Converter> spreadsheetIdConverter,
                                     final Function<SpreadsheetId, DateTimeContext> spreadsheetIdDateTimeContext,
                                     final Function<SpreadsheetId, DecimalNumberContext> spreadsheetIdDecimalNumberContext,
                                     final Function<SpreadsheetId, SpreadsheetTextFormatter<?>> spreadsheetIdDefaultSpreadsheetTextFormatter,
                                     final Function<SpreadsheetId, BiFunction<ExpressionNodeName, List<Object>, Object>> spreadsheetIdFunctions,
                                     final Function<SpreadsheetId, String> spreadsheetIdGeneralDecimalFormatPattern,
                                     final Function<SpreadsheetId, Function<String, Optional<Color>>> spreadsheetIdNameToColor,
                                     final Function<SpreadsheetId, Function<Integer, Optional<Color>>> spreadsheetIdNumberToColor,
                                     final Function<SpreadsheetId, Integer> spreadsheetIdWidth) {
        super();

        this.base = base;
        this.contentType = contentType;
        this.fractioner = fractioner;
        this.metadata = metadata;

        this.spreadsheetIdConverter = spreadsheetIdConverter;
        this.spreadsheetIdDateTimeContext = spreadsheetIdDateTimeContext;
        this.spreadsheetIdDecimalNumberContext = spreadsheetIdDecimalNumberContext;
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
        return this.spreadsheetIdDecimalNumberContext.apply(id);
    }

    private final Function<SpreadsheetId, DecimalNumberContext> spreadsheetIdDecimalNumberContext;

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

    /**
     * Lazily creates a {@link Router} using the {@link SpreadsheetId} to a cache.
     */
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
        final SpreadsheetStoreRepository storeRepository = this.storeRepository(id);

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
        final Function<Integer, Optional<Color>> numberToColor = this.spreadsheetIdNumberToColor.apply(id);
        final Function<String, Optional<Color>> nameToColor = this.spreadsheetIdNameToColor.apply(id);
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

        return SpreadsheetEngineHateosHandlers.engineRouter(this.baseWithSpreadsheetId(id),
                this.contentType,
                SpreadsheetEngineHateosHandlers.deleteColumns(engine, engineContext),
                SpreadsheetEngineHateosHandlers.deleteRows(engine, engineContext),
                SpreadsheetEngineHateosHandlers.fillCells(engine, engineContext),
                SpreadsheetEngineHateosHandlers.insertColumns(engine, engineContext),
                SpreadsheetEngineHateosHandlers.insertRows(engine, engineContext),
                SpreadsheetEngineHateosHandlers.loadCell(SpreadsheetEngineEvaluation.CLEAR_VALUE_ERROR_SKIP_EVALUATE, engine, engineContext),
                SpreadsheetEngineHateosHandlers.loadCell(SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY, engine, engineContext),
                SpreadsheetEngineHateosHandlers.loadCell(SpreadsheetEngineEvaluation.FORCE_RECOMPUTE, engine, engineContext),
                SpreadsheetEngineHateosHandlers.loadCell(SpreadsheetEngineEvaluation.SKIP_EVALUATE, engine, engineContext),
                SpreadsheetEngineHateosHandlers.saveCell(engine, engineContext));
    }

    /**
     * Appends the spreadsheet id to the {@link #base}.
     */
    private AbsoluteUrl baseWithSpreadsheetId(final SpreadsheetId id) {
        final AbsoluteUrl base = this.base;
        return base.setPath(base.path()
                .append(UrlPathName.with(id.hateosLinkId())));
    }

    private final AbsoluteUrl base;
    private final HateosContentType<N> contentType;
    private final Function<BigDecimal, Fraction> fractioner;

    // metadata.........................................................................................................

    @Override
    public SpreadsheetMetadata metadataWithDefaults(final Optional<Locale> locale) {
        return this.metadata.apply(locale);
    }

    private final Function<Optional<Locale>, SpreadsheetMetadata> metadata;

    @Override
    public Function<String, Optional<Color>> nameToColor(final SpreadsheetId id) {
        return this.spreadsheetIdNameToColor.apply(id);
    }

    private final Function<SpreadsheetId, Function<String, Optional<Color>>> spreadsheetIdNameToColor;

    @Override
    public Function<Integer, Optional<Color>> numberToColor(final SpreadsheetId id) {
        return this.spreadsheetIdNumberToColor.apply(id);
    }

    private final Function<SpreadsheetId, Function<Integer, Optional<Color>>> spreadsheetIdNumberToColor;

    @Override
    public SpreadsheetStoreRepository storeRepository(final SpreadsheetId id) {
        Objects.requireNonNull(id, "id");

        SpreadsheetStoreRepository storeRepository = this.idToStoreRepository.get(id);
        if (null == storeRepository) {
            storeRepository = this.createStoreRepository();
            this.idToStoreRepository.put(id, storeRepository);
        }
        return storeRepository;
    }

    private SpreadsheetStoreRepository createStoreRepository() {
        return SpreadsheetStoreRepositories.basic(SpreadsheetCellStores.treeMap(),
                SpreadsheetReferenceStores.treeMap(),
                SpreadsheetGroupStores.treeMap(),
                SpreadsheetLabelStores.treeMap(),
                SpreadsheetReferenceStores.treeMap(),
                SpreadsheetMetadataStores.treeMap(),
                SpreadsheetRangeStores.treeMap(),
                SpreadsheetRangeStores.treeMap(),
                SpreadsheetUserStores.treeMap());
    }

    private final Map<SpreadsheetId, SpreadsheetStoreRepository> idToStoreRepository = Maps.sorted();

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
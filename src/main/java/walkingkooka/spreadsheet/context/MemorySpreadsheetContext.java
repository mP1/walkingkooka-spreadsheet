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
import walkingkooka.convert.ConverterContexts;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.Fraction;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.UrlPathName;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.route.Router;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.conditionalformat.SpreadsheetConditionalFormattingRule;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineEvaluation;
import walkingkooka.spreadsheet.engine.SpreadsheetEngines;
import walkingkooka.spreadsheet.engine.hateos.SpreadsheetEngineHateosHandlers;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.reference.store.SpreadsheetRangeStore;
import walkingkooka.spreadsheet.reference.store.SpreadsheetReferenceStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
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
final class MemorySpreadsheetContext implements SpreadsheetContext {

    /**
     * Creates a new empty {@link MemorySpreadsheetContext}
     */
    static MemorySpreadsheetContext with(final AbsoluteUrl base,
                                         final HateosContentType contentType,
                                         final Function<BigDecimal, Fraction> fractioner,
                                         final Function<SpreadsheetId, BiFunction<ExpressionNodeName, List<Object>, Object>> spreadsheetIdFunctions,
                                         final Function<Optional<Locale>, SpreadsheetMetadata> metadata,
                                         final Function<SpreadsheetId, SpreadsheetStoreRepository> spreadsheetIdToRepository) {
        Objects.requireNonNull(base, "base");
        Objects.requireNonNull(contentType, "contentType");
        Objects.requireNonNull(fractioner, "fractioner");
        Objects.requireNonNull(spreadsheetIdFunctions, "spreadsheetIdFunctions");
        Objects.requireNonNull(metadata, "metadata");
        Objects.requireNonNull(spreadsheetIdToRepository, "spreadsheetIdToRepository");

        return new MemorySpreadsheetContext(base,
                contentType,
                fractioner,
                spreadsheetIdFunctions,
                metadata,
                spreadsheetIdToRepository);
    }

    private MemorySpreadsheetContext(final AbsoluteUrl base,
                                     final HateosContentType contentType,
                                     final Function<BigDecimal, Fraction> fractioner,
                                     final Function<SpreadsheetId, BiFunction<ExpressionNodeName, List<Object>, Object>> spreadsheetIdFunctions,
                                     final Function<Optional<Locale>, SpreadsheetMetadata> metadata,
                                     final Function<SpreadsheetId, SpreadsheetStoreRepository> spreadsheetIdToRepository) {
        super();

        this.base = base;
        this.contentType = contentType;
        this.fractioner = fractioner;
        this.metadata = metadata;
        this.spreadsheetIdFunctions = spreadsheetIdFunctions;
        this.spreadsheetIdToRepository = spreadsheetIdToRepository;
    }

    @Override
    public Converter converter(final SpreadsheetId id) {
        return this.loadAndGet(id, SpreadsheetMetadata::converter);
    }

    @Override
    public DateTimeContext dateTimeContext(final SpreadsheetId id) {
        return this.loadAndGet(id, SpreadsheetMetadata::dateTimeContext);
    }

    @Override
    public DecimalNumberContext decimalNumberContext(final SpreadsheetId id) {
        return this.loadAndGet(id, SpreadsheetMetadata::decimalNumberContext);
    }

    @Override
    public SpreadsheetFormatter defaultSpreadsheetFormatter(final SpreadsheetId id) {
        return this.loadAndGet(id, SpreadsheetMetadata::formatter);
    }

    @Override
    public BiFunction<ExpressionNodeName, List<Object>, Object> functions(final SpreadsheetId id) {
        return this.spreadsheetIdFunctions.apply(id);
    }

    private final Function<SpreadsheetId, BiFunction<ExpressionNodeName, List<Object>, Object>> spreadsheetIdFunctions;

    /**
     * Loads the {@link SpreadsheetMetadata} and then executes the given getter to return a particular property.
     */
    private <T> T loadAndGet(final SpreadsheetId id,
                             final Function<SpreadsheetMetadata, T> getter) {
        Objects.requireNonNull(id, "id");

        return getter.apply(this.spreadsheetIdToRepository.apply(id).metadatas().loadOrFail(id));
    }

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

        final Converter converter = this.converter(id);
        final BiFunction<ExpressionNodeName, List<Object>, Object> functions = this.spreadsheetIdFunctions.apply(id);
        final Function<Integer, Optional<Color>> numberToColor = this.numberToColor(id);
        final Function<SpreadsheetColorName, Optional<Color>> nameToColor = this.nameToColor(id);
        final int width = this.width(id);
        final Function<BigDecimal, Fraction> fractioner = this.fractioner;
        final SpreadsheetFormatter defaultSpreadsheetFormatter = this.defaultSpreadsheetFormatter(id);

        final SpreadsheetEngineContext engineContext = SpreadsheetEngineContexts.basic(functions,
                engine,
                labelStore,
                converter,
                ConverterContexts.basic(this.dateTimeContext(id), this.decimalNumberContext(id)),
                numberToColor,
                nameToColor,
                width,
                fractioner,
                defaultSpreadsheetFormatter);

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
    private final HateosContentType contentType;
    private final Function<BigDecimal, Fraction> fractioner;

    // metadata.........................................................................................................

    @Override
    public SpreadsheetMetadata metadataWithDefaults(final Optional<Locale> locale) {
        return this.metadata.apply(locale);
    }

    private final Function<Optional<Locale>, SpreadsheetMetadata> metadata;

    @Override
    public Function<SpreadsheetColorName, Optional<Color>> nameToColor(final SpreadsheetId id) {
        return this.loadAndGet(id, SpreadsheetMetadata::nameToColor);
    }

    @Override
    public Function<Integer, Optional<Color>> numberToColor(final SpreadsheetId id) {
        return this.loadAndGet(id, SpreadsheetMetadata::numberToColor);
    }

    @Override
    public SpreadsheetStoreRepository storeRepository(final SpreadsheetId id) {
        return this.spreadsheetIdToRepository.apply(id);
    }

    private final Function<SpreadsheetId, SpreadsheetStoreRepository> spreadsheetIdToRepository;

    @Override
    public int width(final SpreadsheetId id) {
        return this.loadAndGet(id, this::width0);
    }

    private int width0(final SpreadsheetMetadata metadata) {
        return metadata.getOrFail(SpreadsheetMetadataPropertyName.WIDTH);
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .label("base").value(this.base)
                .label("contentType").value(this.contentType)
                .build();
    }
}
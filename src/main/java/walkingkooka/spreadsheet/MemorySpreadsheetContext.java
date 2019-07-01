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

import walkingkooka.collect.map.Maps;
import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.spreadsheet.format.SpreadsheetTextFormatter;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.store.range.SpreadsheetRangeStores;
import walkingkooka.spreadsheet.store.reference.SpreadsheetReferenceStores;
import walkingkooka.spreadsheet.store.repo.StoreRepositories;
import walkingkooka.spreadsheet.store.repo.StoreRepository;
import walkingkooka.spreadsheet.store.security.SpreadsheetGroupStores;
import walkingkooka.spreadsheet.store.security.SpreadsheetUserStores;
import walkingkooka.tree.expression.ExpressionNodeName;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A {@link SpreadsheetContext} that creates a new {@link StoreRepository} for unknown {@link SpreadsheetId}.
 * There is no way to delete existing spreadsheets.
 */
final class MemorySpreadsheetContext implements SpreadsheetContext {

    /**
     * Creates a new empty {@link MemorySpreadsheetContext}
     */
    static MemorySpreadsheetContext with(final Function<SpreadsheetId, Converter> spreadsheetIdConverter,
                                         final Function<SpreadsheetId, DateTimeContext> spreadsheetIdDateTimeContext,
                                         final Function<SpreadsheetId, DecimalNumberContext> spreadsheetIdDecimalFormatContext,
                                         final Function<SpreadsheetId, SpreadsheetTextFormatter<?>> spreadsheetIdDefaultSpreadsheetTextFormatter,
                                         final Function<SpreadsheetId, BiFunction<ExpressionNodeName, List<Object>, Object>> spreadsheetIdFunctions,
                                         final Function<SpreadsheetId, String> spreadsheetIdGeneralDecimalFormatPattern,
                                         final Function<SpreadsheetId, Function<String, Color>> spreadsheetIdNameToColor,
                                         final Function<SpreadsheetId, Function<Integer, Color>> spreadsheetIdNumberToColor,
                                         final Function<SpreadsheetId, Integer> spreadsheetIdWidth) {
        Objects.requireNonNull(spreadsheetIdConverter, "spreadsheetIdConverter");
        Objects.requireNonNull(spreadsheetIdDateTimeContext, "spreadsheetIdDateTimeContext");
        Objects.requireNonNull(spreadsheetIdDecimalFormatContext, "spreadsheetIdDecimalFormatContext");
        Objects.requireNonNull(spreadsheetIdDefaultSpreadsheetTextFormatter, "spreadsheetIdDefaultSpreadsheetTextFormatter");
        Objects.requireNonNull(spreadsheetIdFunctions, "spreadsheetIdFunctions");
        Objects.requireNonNull(spreadsheetIdGeneralDecimalFormatPattern, "spreadsheetIdGeneralDecimalFormatPattern");
        Objects.requireNonNull(spreadsheetIdNameToColor, "spreadsheetIdNameToColor");
        Objects.requireNonNull(spreadsheetIdNumberToColor, "spreadsheetIdNumberToColor");
        Objects.requireNonNull(spreadsheetIdWidth, "spreadsheetIdWidth");

        return new MemorySpreadsheetContext(spreadsheetIdConverter,
                spreadsheetIdDateTimeContext,
                spreadsheetIdDecimalFormatContext,
                spreadsheetIdDefaultSpreadsheetTextFormatter,
                spreadsheetIdFunctions,
                spreadsheetIdGeneralDecimalFormatPattern,
                spreadsheetIdNameToColor,
                spreadsheetIdNumberToColor,
                spreadsheetIdWidth);
    }

    private MemorySpreadsheetContext(final Function<SpreadsheetId, Converter> spreadsheetIdConverter,
                                     final Function<SpreadsheetId, DateTimeContext> spreadsheetIdDateTimeContext,
                                     final Function<SpreadsheetId, DecimalNumberContext> spreadsheetIdDecimalFormatContext,
                                     final Function<SpreadsheetId, SpreadsheetTextFormatter<?>> spreadsheetIdDefaultSpreadsheetTextFormatter,
                                     final Function<SpreadsheetId, BiFunction<ExpressionNodeName, List<Object>, Object>> spreadsheetIdFunctions,
                                     final Function<SpreadsheetId, String> spreadsheetIdGeneralDecimalFormatPattern,
                                     final Function<SpreadsheetId, Function<String, Color>> spreadsheetIdNameToColor,
                                     final Function<SpreadsheetId, Function<Integer, Color>> spreadsheetIdNumberToColor,
                                     final Function<SpreadsheetId, Integer> spreadsheetIdWidth) {
        super();

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
        return this.idToStoreRepository.toString();
    }
}
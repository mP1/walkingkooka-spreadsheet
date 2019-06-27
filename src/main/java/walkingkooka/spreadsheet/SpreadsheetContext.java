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

import walkingkooka.Context;
import walkingkooka.color.Color;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.spreadsheet.store.repo.StoreRepository;
import walkingkooka.tree.expression.ExpressionNodeName;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A {@link Context} for spreadsheets.
 */
public interface SpreadsheetContext extends Context {

    /**
     * The {@link DateTimeContext} for the given {@link SpreadsheetId}
     */
    DateTimeContext dateTimeContext(final SpreadsheetId id);

    /**
     * The {@link DecimalNumberContext} for the given {@link SpreadsheetId}
     */
    DecimalNumberContext decimalNumberContext(final SpreadsheetId id);

    /**
     * Returns a {@link BiFunction} which knows available functions for the given {@link SpreadsheetId}.
     */
    BiFunction<ExpressionNodeName, List<Object>, Object> functions(final SpreadsheetId id);

    /**
     * Returns a {@link String general decimal format pattern} for the given {@link SpreadsheetId}.
     */
    String generalDecimalFormatPattern(final SpreadsheetId id);

    /**
     * Returns a {@link Function} which maps {@link String color name} to {@link Color} for the given {@link SpreadsheetId}.
     */
    Function<String, Color> nameToColor(final SpreadsheetId id);

    /**
     * Returns a {@link Function} which maps color number to {@link Color} for the given {@link SpreadsheetId}.
     */
    Function<Integer, Color> numberToColor(final SpreadsheetId id);

    /**
     * Factory that returns a {@link StoreRepository} for a given {@link SpreadsheetId}
     */
    StoreRepository storeRepository(final SpreadsheetId id);
}

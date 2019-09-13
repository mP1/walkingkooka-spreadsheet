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

import walkingkooka.Context;
import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.route.Router;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.tree.expression.ExpressionNodeName;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A {@link Context} for spreadsheets.
 */
public interface SpreadsheetContext extends Context {

    static void checkId(final SpreadsheetId id) {
        Objects.requireNonNull(id, "id");
    }

    /**
     * The {@link Converter} for the given {@link SpreadsheetId}
     */
    Converter converter(final SpreadsheetId id);

    /**
     * Returns a {@link SpreadsheetMetadata} with necessary defaults allocating a new {@link SpreadsheetId}.
     */
    SpreadsheetMetadata createMetadata(final Optional<Locale> locale);

    /**
     * The {@link DateTimeContext} for the given {@link SpreadsheetId}
     */
    DateTimeContext dateTimeContext(final SpreadsheetId id);

    /**
     * The {@link DecimalNumberContext} for the given {@link SpreadsheetId}
     */
    DecimalNumberContext decimalNumberContext(final SpreadsheetId id);

    /**
     * Returns the default {@link SpreadsheetFormatter }for a given {@link SpreadsheetId}
     */
    SpreadsheetFormatter defaultSpreadsheetFormatter(final SpreadsheetId id);

    /**
     * Returns a {@link BiFunction} which knows available functions for the given {@link SpreadsheetId}.
     */
    BiFunction<ExpressionNodeName, List<Object>, Object> functions(final SpreadsheetId id);

    /**
     * A {@link Router} that can handle hateos requests for the given identified spreadsheet.
     */
    Router<HttpRequestAttribute<?>, BiConsumer<HttpRequest, HttpResponse>> hateosRouter(final SpreadsheetId id);

    /**
     * Returns a {@link Function} which maps {@link String color name} to {@link Color} for the given {@link SpreadsheetId}.
     */
    Function<SpreadsheetColorName, Optional<Color>> nameToColor(final SpreadsheetId id);

    /**
     * Returns a {@link Function} which maps color number to {@link Color} for the given {@link SpreadsheetId}.
     */
    Function<Integer, Optional<Color>> numberToColor(final SpreadsheetId id);

    /**
     * Factory that returns a {@link SpreadsheetStoreRepository} for a given {@link SpreadsheetId}
     */
    SpreadsheetStoreRepository storeRepository(final SpreadsheetId id);

    /**
     * Returns the width for a given {@link SpreadsheetId}
     */
    int width(final SpreadsheetId id);
}

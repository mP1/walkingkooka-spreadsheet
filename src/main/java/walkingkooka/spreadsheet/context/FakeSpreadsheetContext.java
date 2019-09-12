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
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class FakeSpreadsheetContext implements SpreadsheetContext {

    @Override
    public Converter converter(final SpreadsheetId id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DateTimeContext dateTimeContext(final SpreadsheetId id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DecimalNumberContext decimalNumberContext(final SpreadsheetId id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetFormatter defaultSpreadsheetFormatter(final SpreadsheetId id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BiFunction<ExpressionNodeName, List<Object>, Object> functions(final SpreadsheetId id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Router<HttpRequestAttribute<?>, BiConsumer<HttpRequest, HttpResponse>> hateosRouter(final SpreadsheetId id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetMetadata metadataWithDefaults(final Optional<Locale> locale) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Function<SpreadsheetColorName, Optional<Color>> nameToColor(final SpreadsheetId id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Function<Integer, Optional<Color>> numberToColor(final SpreadsheetId id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetStoreRepository storeRepository(final SpreadsheetId id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int width(final SpreadsheetId id) {
        throw new UnsupportedOperationException();
    }
}

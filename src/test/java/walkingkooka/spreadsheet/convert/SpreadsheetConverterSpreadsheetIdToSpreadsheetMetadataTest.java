
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

package walkingkooka.spreadsheet.convert;

import org.junit.jupiter.api.Test;
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;

import java.util.Optional;

public final class SpreadsheetConverterSpreadsheetIdToSpreadsheetMetadataTest extends SpreadsheetConverterTestCase<SpreadsheetConverterSpreadsheetIdToSpreadsheetMetadata> {

    private final static SpreadsheetId SPREADSHEET_ID = SpreadsheetId.with(1);

    private final static SpreadsheetMetadata SPREADSHEET_METADATA = SpreadsheetMetadata.EMPTY.set(
        SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
        SPREADSHEET_ID
    );

    @Test
    public void testConvertNullToSpreadsheetMetadataFails() {
        this.convertFails(
            null,
            SpreadsheetMetadata.class
        );
    }

    @Test
    public void testConvertSpreadsheetIdToSpreadsheetMetadata() {
        this.convertAndCheck(
            SPREADSHEET_ID,
            SpreadsheetMetadata.class,
            SPREADSHEET_METADATA
        );
    }

    @Test
    public void testConvertSpreadsheetIdToSpreadsheetMetadata2() {
        this.convertAndCheck(
            SPREADSHEET_ID,
            SPREADSHEET_METADATA
        );
    }

    @Test
    public void testConvertStringWithSpreadsheetIdToSpreadsheetMetadata() {
        this.convertAndCheck(
            SPREADSHEET_ID.toString(),
            SPREADSHEET_METADATA
        );
    }

    @Override
    public SpreadsheetConverterSpreadsheetIdToSpreadsheetMetadata createConverter() {
        return SpreadsheetConverterSpreadsheetIdToSpreadsheetMetadata.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return new FakeSpreadsheetConverterContext() {

            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> type) {
                return converter.canConvert(
                    value,
                    type,
                    this
                );
            }

            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                return this.converter.convert(
                    value,
                    target,
                    this
                );
            }

            private final Converter<SpreadsheetConverterContext> converter = SpreadsheetConverters.collection(
                Lists.of(
                    Converters.simple(),
                    SpreadsheetConverters.textToText(),
                    SpreadsheetConverters.textToSpreadsheetId()
                )
            );

            @Override
            public Optional<SpreadsheetMetadata> loadMetadata(final SpreadsheetId id) {
                return Optional.ofNullable(
                    SPREADSHEET_ID.equals(id) ?
                        SPREADSHEET_METADATA :
                        null
                );
            }
        };
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetConverterSpreadsheetIdToSpreadsheetMetadata.INSTANCE,
            "SpreadsheetId to SpreadsheetMetadata"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterSpreadsheetIdToSpreadsheetMetadata> type() {
        return SpreadsheetConverterSpreadsheetIdToSpreadsheetMetadata.class;
    }
}

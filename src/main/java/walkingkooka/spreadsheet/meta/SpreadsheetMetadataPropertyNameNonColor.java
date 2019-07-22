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

package walkingkooka.spreadsheet.meta;

import walkingkooka.Cast;
import walkingkooka.color.Color;
import walkingkooka.naming.Name;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * The {@link Name} of metadata property, with a custom handler and visitor to handle dispatching to a {@link SpreadsheetMetadataVisitor} method.
 */
final class SpreadsheetMetadataPropertyNameNonColor<T> extends SpreadsheetMetadataPropertyName<T> {

    /**
     * Factory used to create a new {@link SpreadsheetMetadataPropertyNameNonColor} constant.
     */
    static <T> SpreadsheetMetadataPropertyNameNonColor<T> with(final String name,
                                                               final SpreadsheetMetadataPropertyValueHandler<T> handler,
                                                               final BiConsumer<T, SpreadsheetMetadataVisitor> visitor) {
        return new SpreadsheetMetadataPropertyNameNonColor<>(name, handler, visitor);
    }

    /**
     * Private constructor use factory.
     */
    private SpreadsheetMetadataPropertyNameNonColor(final String name,
                                                    final SpreadsheetMetadataPropertyValueHandler<T> handler,
                                                    final BiConsumer<T, SpreadsheetMetadataVisitor> visitor) {
        super(name);

        this.handler = handler;
        this.visitor = visitor;
    }

    @Override
    SpreadsheetMetadataPropertyValueHandler<T> handler() {
        return this.handler;
    }

    final SpreadsheetMetadataPropertyValueHandler<T> handler;

    // NonEmptySpreadsheetMetadata......................................................................................

    @Override
    void addNumberedColor(final Object value, final Map<Integer, Color> numberToColor) {
        // nop
    }

    // StyleMetadataVisitor.............................................................................................

    /**
     * Dispatches to the appropriate {@link SpreadsheetMetadataVisitor} visit method.
     */
    @Override
    void accept(final Object value, final SpreadsheetMetadataVisitor visitor) {
        this.visitor.accept(Cast.to(value), visitor);
    }

    /**
     * Calls the appropriate {@link SpreadsheetMetadataVisitor} visit method
     */
    private final BiConsumer<T, SpreadsheetMetadataVisitor> visitor;

    // Object...........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetMetadataPropertyNameNonColor;
    }
}

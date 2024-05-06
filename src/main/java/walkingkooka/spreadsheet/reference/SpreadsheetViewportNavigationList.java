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

package walkingkooka.spreadsheet.reference;

import walkingkooka.collect.list.ImmutableListDefaults;

import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

/**
 * An {@link walkingkooka.collect.list.ImmutableList} holding zero or more {@link SpreadsheetViewportNavigation}.
 */
public final class SpreadsheetViewportNavigationList extends AbstractList<SpreadsheetViewportNavigation>
        implements ImmutableListDefaults<SpreadsheetViewportNavigationList, SpreadsheetViewportNavigation> {

    /**
     * Factory that creates a new {@link SpreadsheetViewportNavigationList} after taking a defensive copy.
     */
    public static SpreadsheetViewportNavigationList with(final List<SpreadsheetViewportNavigation> list) {
        return list instanceof SpreadsheetViewportNavigationList ?
                (SpreadsheetViewportNavigationList) list :
                copy(list);
    }

    static SpreadsheetViewportNavigationList copy(final List<SpreadsheetViewportNavigation> list) {
        Objects.requireNonNull(list, "list");

        final SpreadsheetViewportNavigation[] copy = new SpreadsheetViewportNavigation[list.size()];
        list.toArray(copy);
        return new SpreadsheetViewportNavigationList(copy);
    }

    private SpreadsheetViewportNavigationList(final SpreadsheetViewportNavigation[] list) {
        this.list = list;
    }

    @Override
    public SpreadsheetViewportNavigation get(final int index) {
        return this.list[index];
    }

    @Override
    public int size() {
        return this.list.length;
    }

    @Override
    public SpreadsheetViewportNavigationList setElements(final List<SpreadsheetViewportNavigation> list) {
        final SpreadsheetViewportNavigationList copy = with(list);
        return this.equals(copy) ?
                this :
                copy;
    }

    private final SpreadsheetViewportNavigation[] list;
}

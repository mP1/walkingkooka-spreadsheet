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

import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
import walkingkooka.text.CharSequences;
import walkingkooka.text.HasText;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Captures a users input movement relative to a selection, such as a cursor-left from a selection in the viewport.
 */
public abstract class SpreadsheetViewportSelectionNavigation implements HasText {

    /**
     * {@see SpreadsheetViewportSelectionNavigationDown}
     */
    public static SpreadsheetViewportSelectionNavigation extendDown() {
        return SpreadsheetViewportSelectionNavigationExtendDown.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportSelectionNavigationExtendLeft}
     */
    public static SpreadsheetViewportSelectionNavigation extendLeft() {
        return SpreadsheetViewportSelectionNavigationExtendLeft.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportSelectionNavigationExtendRight}
     */
    public static SpreadsheetViewportSelectionNavigation extendRight() {
        return SpreadsheetViewportSelectionNavigationExtendRight.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportSelectionNavigationExtendUp}
     */
    public static SpreadsheetViewportSelectionNavigation extendUp() {
        return SpreadsheetViewportSelectionNavigationExtendUp.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportSelectionNavigationDown}
     */
    public static SpreadsheetViewportSelectionNavigation down() {
        return SpreadsheetViewportSelectionNavigationDown.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportSelectionNavigationLeft}
     */
    public static SpreadsheetViewportSelectionNavigation left() {
        return SpreadsheetViewportSelectionNavigationLeft.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportSelectionNavigationRight}
     */
    public static SpreadsheetViewportSelectionNavigation right() {
        return SpreadsheetViewportSelectionNavigationRight.INSTANCE;
    }

    /**
     * {@see SpreadsheetViewportSelectionNavigationUp}
     */
    public static SpreadsheetViewportSelectionNavigation up() {
        return SpreadsheetViewportSelectionNavigationUp.INSTANCE;
    }

    SpreadsheetViewportSelectionNavigation() {
    }

    /**
     * Executes this navigation on the given selection and anchor returning the updated result.
     */
    public abstract Optional<SpreadsheetViewportSelection> update(final SpreadsheetSelection selection,
                                                                  final SpreadsheetViewportSelectionAnchor anchor,
                                                                  final SpreadsheetColumnStore columnStore,
                                                                  final SpreadsheetRowStore rowStore);

    abstract boolean isOpposite(final SpreadsheetViewportSelectionNavigation other);

    @Override
    public final String toString() {
        return this.text();
    }

    /**
     * Accepts text that has a more pretty form of any {@link SpreadsheetViewportSelectionNavigation enum value}.
     * The text is identical to the enum name but in lower case and underscore replaced with dash.
     * <br>
     * {@link #extendLeft()} = <pre>extend-left</pre>.
     */
    public static List<SpreadsheetViewportSelectionNavigation> parse(final String text) {
        return SpreadsheetViewportSelection.SEPARATOR.parse(
                text,
                SpreadsheetViewportSelectionNavigation::parse0
        );
    }

    private static SpreadsheetViewportSelectionNavigation parse0(final String text) {
        for (final SpreadsheetViewportSelectionNavigation navigation : VALUES) {
            if (navigation.text().equals(text)) {
                return navigation;
            }
        }

        throw new IllegalArgumentException("Invalid text=" + CharSequences.quoteAndEscape(text));
    }

    private final static SpreadsheetViewportSelectionNavigation[] VALUES = new SpreadsheetViewportSelectionNavigation[]{
            down(),
            left(),
            right(),
            up(),
            extendDown(),
            extendLeft(),
            extendRight(),
            extendUp()
    };

    /**
     * Accepts some navigations and removes opposites returning the result.
     */
    public static List<SpreadsheetViewportSelectionNavigation> compact(final List<SpreadsheetViewportSelectionNavigation> navigations) {
        Objects.requireNonNull(navigations, "navigations");

        final List<SpreadsheetViewportSelectionNavigation> copy = Lists.immutable(navigations);
        final int size = copy.size();

        List<SpreadsheetViewportSelectionNavigation> result = null;

        switch (size) {
            case 0:
            case 1:
                result = copy;
                break;
            default:
                final SpreadsheetViewportSelectionNavigation[] temp = new SpreadsheetViewportSelectionNavigation[size];
                copy.toArray(temp);

                int compactSize = size;

                Exit:
                for (int i = 0; i < size; i++) {
                    final SpreadsheetViewportSelectionNavigation left = temp[i];
                    if (null == left) {
                        continue;
                    }

                    // try and find an opposite
                    for (int j = i + 1; j < size; j++) {
                        if (left.isOpposite(temp[j])) {
                            temp[i] = null;
                            temp[j] = null;

                            compactSize = compactSize - 2;

                            // all navigations cancelled themselves out
                            if (0 == compactSize) {
                                result = Lists.empty();
                                i = Integer.MAX_VALUE - 1; // because i++ will increment before i < size test.
                            }
                            break;
                        }
                    }
                }

                // fill an array with non-null navigations.
                if (null == result) {
                    final SpreadsheetViewportSelectionNavigation[] compact = new SpreadsheetViewportSelectionNavigation[compactSize];

                    int i = 0;
                    for (SpreadsheetViewportSelectionNavigation item : temp) {
                        if (null != item) {
                            compact[i] = item;
                            i++;
                        }
                    }

                    result = Lists.of(compact);
                }

                break;
        }

        return result;
    }
}

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

import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
import walkingkooka.text.CharSequences;

/**
 * Captures a users input movement relative to a selection, such as a cursor-left from a selection in the viewport.
 */
public enum SpreadsheetViewportSelectionNavigation {
    LEFT {
        @Override
        public SpreadsheetViewportSelection perform(final SpreadsheetSelection selection,
                                                    final SpreadsheetViewportSelectionAnchor anchor,
                                                    final SpreadsheetColumnStore columnStore,
                                                    final SpreadsheetRowStore rowStore) {
            return selection.left(anchor, columnStore, rowStore)
                    .setAnchorOrDefault(anchor);
        }
    },
    UP {
        @Override
        public SpreadsheetViewportSelection perform(final SpreadsheetSelection selection,
                                                    final SpreadsheetViewportSelectionAnchor anchor,
                                                    final SpreadsheetColumnStore columnStore,
                                                    final SpreadsheetRowStore rowStore) {
            return selection.up(anchor, columnStore, rowStore)
                    .setAnchorOrDefault(anchor);
        }
    },
    RIGHT {
        @Override
        public SpreadsheetViewportSelection perform(final SpreadsheetSelection selection,
                                                    final SpreadsheetViewportSelectionAnchor anchor,
                                                    final SpreadsheetColumnStore columnStore,
                                                    final SpreadsheetRowStore rowStore) {
            return selection.right(anchor, columnStore, rowStore)
                    .setAnchorOrDefault(anchor);
        }
    },
    DOWN {
        @Override
        public SpreadsheetViewportSelection perform(final SpreadsheetSelection selection,
                                                    final SpreadsheetViewportSelectionAnchor anchor,
                                                    final SpreadsheetColumnStore columnStore,
                                                    final SpreadsheetRowStore rowStore) {
            return selection.down(anchor, columnStore, rowStore)
                    .setAnchorOrDefault(anchor);
        }
    },
    EXTEND_LEFT {
        @Override
        public SpreadsheetViewportSelection perform(final SpreadsheetSelection selection,
                                                    final SpreadsheetViewportSelectionAnchor anchor,
                                                    final SpreadsheetColumnStore columnStore,
                                                    final SpreadsheetRowStore rowStore) {
            return selection.extendLeft(anchor, columnStore, rowStore);
        }
    },
    EXTEND_UP {
        @Override
        public SpreadsheetViewportSelection perform(final SpreadsheetSelection selection,
                                                    final SpreadsheetViewportSelectionAnchor anchor,
                                                    final SpreadsheetColumnStore columnStore,
                                                    final SpreadsheetRowStore rowStore) {
            return selection.extendUp(anchor, columnStore, rowStore);
        }
    },
    EXTEND_RIGHT {
        @Override
        public SpreadsheetViewportSelection perform(final SpreadsheetSelection selection,
                                                    final SpreadsheetViewportSelectionAnchor anchor,
                                                    final SpreadsheetColumnStore columnStore,
                                                    final SpreadsheetRowStore rowStore) {
            return selection.extendRight(anchor, columnStore, rowStore);
        }
    },
    EXTEND_DOWN {
        @Override
        public SpreadsheetViewportSelection perform(final SpreadsheetSelection selection,
                                                    final SpreadsheetViewportSelectionAnchor anchor,
                                                    final SpreadsheetColumnStore columnStore,
                                                    final SpreadsheetRowStore rowStore) {
            return selection.extendDown(anchor, columnStore, rowStore);
        }
    };

    /**
     * Executes this navigation on the given selection and anchor returning the updated result.
     */
    public abstract SpreadsheetViewportSelection perform(final SpreadsheetSelection selection,
                                                         final SpreadsheetViewportSelectionAnchor anchor,
                                                         final SpreadsheetColumnStore columnStore,
                                                         final SpreadsheetRowStore rowStore);

    /**
     * Accepts text that has a more pretty form of any {@link SpreadsheetViewportSelectionNavigation enum value}.
     * The text is identical to the enum name but in lower case and underscore replaced with dash.
     * <br>
     * {@link #EXTEND_LEFT} = <pre>extend-left</pre>.
     */
    public static SpreadsheetViewportSelectionNavigation from(final String text) {
        CharSequences.failIfNullOrEmpty(text, "navigation");

        for (final SpreadsheetViewportSelectionNavigation navigation : values()) {
            if (navigation.name().toLowerCase().replace('_', '-').equals(text)) {
                return navigation;
            }
        }

        throw new IllegalArgumentException("Invalid text=" + CharSequences.quoteAndEscape(text));
    }
}

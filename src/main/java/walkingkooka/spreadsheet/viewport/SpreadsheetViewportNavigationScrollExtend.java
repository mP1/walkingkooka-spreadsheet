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

package walkingkooka.spreadsheet.viewport;

import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Optional;

abstract class SpreadsheetViewportNavigationScrollExtend extends SpreadsheetViewportNavigationScroll {

    SpreadsheetViewportNavigationScrollExtend(final int value) {
        super(value);
    }

    @Override
    final Optional<AnchoredSpreadsheetSelection> updateViewportSelection(final AnchoredSpreadsheetSelection anchoredSelection,
                                                                         final SpreadsheetViewportRectangle rectangle,
                                                                         final SpreadsheetViewportNavigationContext context) {
        final SpreadsheetSelection selectionOrNull = context.resolveIfLabel(
            anchoredSelection.selection()
        ).orElse(null);

        // if selection is an unknown label, clear the selection and ignore the extend.
        return null == selectionOrNull ?
            Optional.empty() :
            this.updateSelection(
                selectionOrNull,
                anchoredSelection.anchor(),
                context
            );
    }

    // text.............................................................................................................

    // scroll&extend down 123px
    @Override
    public final String text() {
        return "scroll&extend " + this.textToken() + " " + this.value + "px";
    }

    abstract String textToken();

}

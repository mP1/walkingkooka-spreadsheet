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

package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.viewport.SpreadsheetViewport;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportWindows;

import java.util.Optional;

/**
 * An either for {@link BasicSpreadsheetEngine#window0(SpreadsheetViewport, boolean, SpreadsheetEngineContext)}.
 */
final class BasicSpreadsheetEngineWindow {

    static BasicSpreadsheetEngineWindow with(final Optional<SpreadsheetViewport> viewport,
                                             final Optional<SpreadsheetViewportWindows> windows) {
        return new BasicSpreadsheetEngineWindow(viewport, windows);
    }

    private BasicSpreadsheetEngineWindow(final Optional<SpreadsheetViewport> viewport,
                                         final Optional<SpreadsheetViewportWindows> windows) {
        this.viewport = viewport;
        this.windows = windows;
    }

    final Optional<SpreadsheetViewport> viewport;

    final Optional<SpreadsheetViewportWindows> windows;
}

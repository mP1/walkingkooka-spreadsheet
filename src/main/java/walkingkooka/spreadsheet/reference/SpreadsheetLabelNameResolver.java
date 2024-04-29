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

/**
 * A SAM interface that defines a single method to resolve any {@link SpreadsheetLabelName} selections to a NON
 * {@link SpreadsheetSelection}. In the cases of an unknown label, a {@link RuntimeException} will be thrown.
 */
public interface SpreadsheetLabelNameResolver {

    /**
     * Resolves a {@link SpreadsheetSelection} if it is a {@link SpreadsheetLabelName} otherwise returning the target.
     * This must never return a {@link SpreadsheetLabelName}.
     */
    SpreadsheetSelection resolveIfLabel(final SpreadsheetSelection selection);
}

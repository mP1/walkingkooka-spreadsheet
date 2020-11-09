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

import walkingkooka.text.CharSequences;

/**
 * A rectangular region that selects one or more cells
 */
public abstract class SpreadsheetRectangle extends SpreadsheetExpressionReference {

    public static SpreadsheetRectangle parseRectangle(final String text) {
        CharSequences.failIfNullOrEmpty(text, "text");

        final SpreadsheetRectangle rectangle;

        switch (text.charAt(0)) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                rectangle = SpreadsheetExpressionReference.parsePixelRectangle(text);
                break;
            default:
                rectangle = SpreadsheetExpressionReference.parseRange(text);
                break;
        }

        return rectangle;
    }

    SpreadsheetRectangle() {
        super();
    }
}

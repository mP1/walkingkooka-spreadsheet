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

package walkingkooka.spreadsheet;

/**
 * TODO create a sub class that extracts the source and destination types from a {@link ClassCastException#getMessage()}.
 * <br>
 * https://github.com/mP1/walkingkooka-spreadsheet/issues/2302
 */
final class SpreadsheetErrorKindClassCastExceptionMessage {

    // "walkingkooka.spreadsheet.SpreadsheetErrorKindTest cannot be cast to java.base/java.lang.Void"
    // ->
    // Failed to convert $FROM to $TO
    static String extractClassCastExceptionMessage(final String message) {
        final String[] tokens = message.split(" ");

        return tokens.length < 2 ?
                message :
                extractClassCastExceptionMessage0(tokens);
    }

    private static String extractClassCastExceptionMessage0(final String[] tokens) {
        final String first = tokens[0];

        String last = tokens[tokens.length - 1];
        final int slash = last.indexOf('/');
        if (-1 != slash) {
            last = last.substring(slash + 1);
        }

        return "Failed to convert " + extractSimpleClassName(first) + " to " + extractSimpleClassName(last);
    }

    private static String extractSimpleClassName(final String typeName) {
        final int lastDot = typeName.lastIndexOf('.');

        return -1 == lastDot ?
                typeName :
                typeName.substring(lastDot + 1);
    }
}

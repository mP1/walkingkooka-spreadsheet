
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

package walkingkooka.spreadsheet.compare;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.value.SpreadsheetError;
import walkingkooka.spreadsheet.value.SpreadsheetErrorKind;

public final class SpreadsheetComparatorSharedErrorTest extends SpreadsheetComparatorTestCase<SpreadsheetComparatorSharedError, SpreadsheetError> {

    @Test
    public void testCompareLess() {
        this.compareAndCheckLess(
            SpreadsheetErrorKind.DIV0.toError(),
            SpreadsheetErrorKind.ERROR.toError()
        );
    }

    @Test
    public void testCompareMoreWithNullLeft() {
        this.compareAndCheckMore(
            null,
            SpreadsheetErrorKind.DIV0.toError()
        );
    }

    @Test
    public void testCompareLessWithNullRight() {
        this.compareAndCheckLess(
            SpreadsheetErrorKind.DIV0.toError(),
            null
        );
    }

    @Test
    public void testCompareLessWithDifferentMessage() {
        this.compareAndCheckLess(
            SpreadsheetErrorKind.DIV0.setMessage("Hello111"),
            SpreadsheetErrorKind.DIV0.setMessage("Hello222")
        );
    }

    @Test
    public void testCompareLessWithDifferentMessageDifferentCase() {
        this.compareAndCheckLess(
            SpreadsheetErrorKind.DIV0.setMessage("aaa"),
            SpreadsheetErrorKind.DIV0.setMessage("zzz")
        );
    }

    @Test
    public void testCompareEqualDifferentValue() {
        final String message = "Hello";

        this.compareAndCheckEquals(
            SpreadsheetErrorKind.DIV0.setMessageAndValue(
                message,
                111
            ),
            SpreadsheetErrorKind.DIV0.setMessageAndValue(
                message,
                222
            )
        );
    }

    @Override
    public SpreadsheetComparatorSharedError createComparator() {
        return SpreadsheetComparatorSharedError.INSTANCE;
    }

    @Override
    public SpreadsheetComparatorContext createContext() {
        return new FakeSpreadsheetComparatorContext();
    }

    // toString.........................................................................................................

    @Test
    public void tesToString() {
        this.toStringAndCheck(
            this.createComparator(),
            "SpreadsheetError"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetComparatorSharedError> type() {
        return SpreadsheetComparatorSharedError.class;
    }
}

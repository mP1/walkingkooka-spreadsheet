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

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting;

public final class SpreadsheetLabelNameResolverEmptyTest implements SpreadsheetLabelNameResolverTesting2<SpreadsheetLabelNameResolverEmpty>,
    ToStringTesting<SpreadsheetLabelNameResolverEmpty>,
    ClassTesting<SpreadsheetLabelNameResolverEmpty> {

    @Test
    public void testL() {
        this.resolveIfLabelAndCheck(
            SpreadsheetLabelNameResolverEmpty.INSTANCE,
            SpreadsheetSelection.labelName("Label123")
        );
    }

    @Override
    public SpreadsheetLabelNameResolverEmpty createSpreadsheetLabelNameResolver() {
        return SpreadsheetLabelNameResolverEmpty.INSTANCE;
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetLabelNameResolverEmpty.INSTANCE,
            SpreadsheetLabelNameResolverEmpty.class.getSimpleName()
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetLabelNameResolverEmpty> type() {
        return SpreadsheetLabelNameResolverEmpty.class;
    }
}

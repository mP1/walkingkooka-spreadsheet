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

package walkingkooka.spreadsheet.security.store;

import walkingkooka.net.email.EmailAddress;
import walkingkooka.reflect.TypeNameTesting;
import walkingkooka.spreadsheet.security.User;
import walkingkooka.spreadsheet.security.UserId;
import walkingkooka.store.StoreTesting;

import java.util.Optional;

public interface SpreadsheetUserStoreTesting<S extends SpreadsheetUserStore> extends StoreTesting<S, UserId, User>,
    TypeNameTesting<S> {

    // StoreTesting...........................................................

    @Override
    default UserId id() {
        return UserId.with(1);
    }

    @Override
    default User value() {
        return User.with(Optional.of(this.id()), EmailAddress.parse("user@example.com"));
    }

    // TypeNameTesting..................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetUserStore.class.getSimpleName();
    }
}

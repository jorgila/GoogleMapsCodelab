// Copyright 2024 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.example.mountainmarkers.data.local

import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.example.mountainmarkers.data.utils.m
import com.example.mountainmarkers.subjects.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MountainsRepositoryTest {
    @Test
    fun canLoadMountainData() = runBlocking {
        val repo = MountainsRepository(ApplicationProvider.getApplicationContext())
        val mountains = repo.loadMountains().value

        assertThat(mountains).hasSize(143)

        with(mountains.first { it.name == "Mount Sneffels" }) {
            assertThat(name).isEqualTo("Mount Sneffels")
            assertThat(elevation.value).isWithin(1.0e-6).of(4315.4)
            assertThat(location).isWithin(3.m).of(38.0038, -107.7923)
            assertThat(is14er()).isTrue()
        }

        with(mountains.first { it.name.contains("\uD83D\uDC3B") }) {
            assertThat(name).isEqualTo("Grizzly Peak \uD83D\uDC3B")
            assertThat(elevation.value).isWithin(1.0e-6).of(4265.6)
            assertThat(location).isWithin(3.m).of(39.0425, -106.5976)
            assertThat(is14er()).isFalse()
        }
    }
}

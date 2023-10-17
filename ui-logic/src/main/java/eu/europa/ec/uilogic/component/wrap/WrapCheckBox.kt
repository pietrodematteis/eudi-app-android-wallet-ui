/*
 *
 *  * Copyright (c) 2023 European Commission
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package eu.europa.ec.uilogic.component.wrap

import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

data class CheckboxData(
    val isChecked: Boolean,
    val onCheckedChange: ((Boolean) -> Unit)?,
    val enabled: Boolean = true
)

@Composable
fun WrapCheckbox(
    checkboxData: CheckboxData,
    modifier: Modifier = Modifier,
) {
    Checkbox(
        checked = checkboxData.isChecked,
        onCheckedChange = checkboxData.onCheckedChange,
        modifier = modifier,
        enabled = checkboxData.enabled
    )
}

@Preview
@Composable
private fun WrapCheckBoxPreview() {
    var isChecked by remember {
        mutableStateOf(true)
    }

    val checkBoxData = CheckboxData(
        isChecked = isChecked,
        onCheckedChange = {
            isChecked = it
        },
        enabled = true
    )

    WrapCheckbox(checkboxData = checkBoxData)
}
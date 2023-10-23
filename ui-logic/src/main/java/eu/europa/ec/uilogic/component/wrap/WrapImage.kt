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

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import eu.europa.ec.uilogic.component.IconData

@Composable
fun WrapImage(
    iconData: IconData,
    modifier: Modifier = Modifier
) {
    val iconContentDescription = stringResource(id = iconData.contentDescriptionId)

    iconData.resourceId?.let { resId ->
        Image(
            modifier = modifier,
            painter = painterResource(id = resId),
            contentDescription = iconContentDescription
        )
    } ?: run {
        iconData.imageVector?.let { imageVector ->
            Image(
                modifier = modifier,
                imageVector = imageVector,
                contentDescription = iconContentDescription
            )
        }
    }
}
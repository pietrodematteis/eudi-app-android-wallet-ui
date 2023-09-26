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

package eu.europa.ec.assemblylogic.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import eu.europa.ec.resourceslogic.theme.ThemeManager
import eu.europa.ec.resourceslogic.theme.templates.ThemeDimensTemplate
import eu.europa.ec.resourceslogic.theme.values.ThemeColors
import eu.europa.ec.resourceslogic.theme.values.ThemeShapes
import eu.europa.ec.resourceslogic.theme.values.ThemeTypography
import eu.europa.ec.startupfeature.router.featureStartupGraph
import eu.europa.ec.uilogic.container.EudiComponentActivity

class MainActivity : EudiComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize theme manager.
        ThemeManager.Builder()
            .withLightColors(ThemeColors.lightColors)
            .withDarkColors(ThemeColors.darkColors)
            .withTypography(ThemeTypography.typo)
            .withShapes(ThemeShapes.shapes)
            .withDimensions(
                ThemeDimensTemplate(
                    screenPadding = 10.0
                )
            )
            .build()

        setContent {
            Content {
                featureStartupGraph(it)
            }
        }
    }
}
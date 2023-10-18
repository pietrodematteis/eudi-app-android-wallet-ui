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

package eu.europa.ec.loginfeature.di

import eu.europa.ec.loginfeature.interactor.FaqInteractorImpl
import eu.europa.ec.loginfeature.interactor.LoginInteractor
import eu.europa.ec.loginfeature.repository.LoginRepository
import eu.europa.ec.loginfeature.repository.LoginRepositoryImpl
import eu.europa.ec.networklogic.api.ApiClient
import eu.europa.ec.resourceslogic.provider.ResourceProvider
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module


@Module
@ComponentScan("eu.europa.ec.loginfeature")
class LoginModule


@Factory
fun provideLoginRepository(apiClient: ApiClient): LoginRepository {
    return LoginRepositoryImpl(apiClient)
}

@Factory
fun provideLoginInteractor(
    loginRepository: LoginRepository,
    resourceProvider: ResourceProvider
): LoginInteractor {
    return FaqInteractorImpl(loginRepository, resourceProvider)
}
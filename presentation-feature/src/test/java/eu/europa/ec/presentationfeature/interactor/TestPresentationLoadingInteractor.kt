/*
 * Copyright (c) 2023 European Commission
 *
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by the European
 * Commission - subsequent versions of the EUPL (the "Licence"); You may not use this work
 * except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the Licence is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the Licence for the specific language
 * governing permissions and limitations under the Licence.
 */

package eu.europa.ec.presentationfeature.interactor

import android.content.Context
import eu.europa.ec.authenticationlogic.controller.authentication.BiometricsAvailability
import eu.europa.ec.authenticationlogic.controller.authentication.DeviceAuthenticationResult
import eu.europa.ec.authenticationlogic.model.BiometricCrypto
import eu.europa.ec.commonfeature.interactor.DeviceAuthenticationInteractor
import eu.europa.ec.corelogic.controller.WalletCorePartialState
import eu.europa.ec.corelogic.controller.WalletCorePresentationController
import eu.europa.ec.resourceslogic.provider.ResourceProvider
import eu.europa.ec.testfeature.mockedGenericErrorMessage
import eu.europa.ec.testfeature.mockedPlainFailureMessage
import eu.europa.ec.testlogic.base.TestApplication
import eu.europa.ec.testlogic.base.getMockedContext
import eu.europa.ec.testlogic.extension.runFlowTest
import eu.europa.ec.testlogic.extension.runTest
import eu.europa.ec.testlogic.extension.toFlow
import eu.europa.ec.testlogic.rule.CoroutineTestRule
import junit.framework.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.net.URI

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class TestPresentationLoadingInteractor {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @Mock
    private lateinit var walletCorePresentationController: WalletCorePresentationController

    @Mock
    private lateinit var deviceAuthenticationInteractor: DeviceAuthenticationInteractor

    private lateinit var context: Context
    private lateinit var crypto: BiometricCrypto

    @Mock
    private lateinit var resultHandler: DeviceAuthenticationResult

    private lateinit var interactor: PresentationLoadingInteractor

    private lateinit var closeable: AutoCloseable

    @Before
    fun before() {
        closeable = MockitoAnnotations.openMocks(this)

        interactor = PresentationLoadingInteractorImpl(
            walletCorePresentationController = walletCorePresentationController,
            deviceAuthenticationInteractor = deviceAuthenticationInteractor
        )

        context = getMockedContext()
        crypto = BiometricCrypto(cryptoObject = null)
    }

    @After
    fun after() {
        closeable.close()
    }

    //region observeResponse

    // Case 1:
    // 1. walletCorePresentationController.events emits:
    // TransferEventPartialState.Error, with an error message.

    // Case 1 Expected Result:
    // PresentationLoadingObserveResponsePartialState.Failure state, with the same error message.

    @Test
    fun `Given Case 1, When observeResponse is called, Then Case 1 Expected Result is returned`() {
        coroutineRule.runTest {
            // Given
            mockWalletCorePresentationControllerEventEmission(
                event = WalletCorePartialState.Failure(
                    error = mockedPlainFailureMessage
                )
            )

            // When
            interactor.observeResponse()
                .runFlowTest {
                    // Then
                    TestCase.assertEquals(
                        PresentationLoadingObserveResponsePartialState.Failure(
                            error = mockedPlainFailureMessage
                        ),
                        awaitItem()
                    )
                }
        }
    }

    // Case 2:
    // 1. walletCorePresentationController.events emits:
    // TransferEventPartialState.Success.

    // Case 2 Expected Result:
    // PresentationLoadingObserveResponsePartialState.Success state.

    @Test
    fun `Given Case 2, When observeResponse is called, Then Case 2 Expected Result is returned`() {
        coroutineRule.runTest {
            // Given
            mockWalletCorePresentationControllerEventEmission(
                event = WalletCorePartialState.Success
            )

            // When
            interactor.observeResponse()
                .runFlowTest {
                    // Then
                    TestCase.assertEquals(
                        PresentationLoadingObserveResponsePartialState.Success,
                        awaitItem()
                    )
                }
        }
    }

    // Case 3:
    // 1. walletCorePresentationController.events emits:
    // TransferEventPartialState.Success.

    // Case 3 Expected Result:
    // PresentationLoadingObserveResponsePartialState.Redirect with the same URI.

    @Test
    fun `Given Case 3, When observeResponse is called, Then Case 3 Expected Result is returned`() {
        coroutineRule.runTest {
            // Given
            mockWalletCorePresentationControllerEventEmission(
                event = WalletCorePartialState.Redirect(uri = URI("uri"))
            )

            // When
            interactor.observeResponse()
                .runFlowTest {
                    // Then
                    TestCase.assertEquals(
                        PresentationLoadingObserveResponsePartialState.Redirect(URI("uri")),
                        awaitItem()
                    )
                }
        }
    }

    // Case 4:
    // 1. walletCorePresentationController.events emits:
    // TransferEventPartialState.UserAuthenticationRequired.

    // Case 4 Expected Result:
    // PresentationLoadingObserveResponsePartialState.UserAuthenticationRequired.

    @Test
    fun `Given Case 4, When observeResponse is called, Then Case 4 Expected Result is returned`() {
        coroutineRule.runTest {
            // Given
            mockWalletCorePresentationControllerEventEmission(
                event = WalletCorePartialState.UserAuthenticationRequired(
                    crypto = null,
                    resultHandler = DeviceAuthenticationResult()
                )
            )

            // When
            interactor.observeResponse()
                .runFlowTest {
                    // Then
                    TestCase.assertEquals(
                        PresentationLoadingObserveResponsePartialState.UserAuthenticationRequired(
                            crypto = null,
                            resultHandler = DeviceAuthenticationResult()
                        ),
                        awaitItem()
                    )
                }
        }
    }

    //endregion

    //region handleUserAuthentication

    // Case 5:
    // 1. deviceAuthenticationInteractor.getBiometricsAvailability returns:
    // BiometricsAvailability.CanAuthenticate

    // Case 5 Expected Result:
    // deviceAuthenticationInteractor.authenticateWithBiometrics called once.
    @Test
    fun `Given case 5, When handleUserAuthentication is called, Then Case 5 expected result is returned`() {
        // Given
        mockBiometricsAvailabilityResponse(
            response = BiometricsAvailability.CanAuthenticate
        )

        // When
        interactor.handleUserAuthentication(
            context = context,
            crypto = crypto,
            resultHandler = resultHandler
        )

        // Then
        verify(deviceAuthenticationInteractor, times(1))
            .authenticateWithBiometrics(context, crypto, resultHandler)
    }

    // Case 6:
    // 1. deviceAuthenticationInteractor.getBiometricsAvailability returns:
    // BiometricsAvailability.NonEnrolled

    // Case 6 Expected Result:
    // deviceAuthenticationInteractor.authenticateWithBiometrics called once.
    @Test
    fun `Given case 6, When handleUserAuthentication is called, Then Case 6 expected result is returned`() {
        // Given
        mockBiometricsAvailabilityResponse(
            response = BiometricsAvailability.NonEnrolled
        )

        // When
        interactor.handleUserAuthentication(
            context = context,
            crypto = crypto,
            resultHandler = resultHandler
        )

        // Then
        verify(deviceAuthenticationInteractor, times(1))
            .authenticateWithBiometrics(context, crypto, resultHandler)
    }

    // Case 7:
    // 1. deviceAuthenticationInteractor.getBiometricsAvailability returns:
    // BiometricsAvailability.Failure

    // Case 7 Expected Result:
    // resultHandler.onAuthenticationFailure called once.
    @Test
    fun `Given case 7, When handleUserAuthentication is called, Then Case 7 expected result is returned`() {
        // Given
        val mockedOnAuthenticationFailure: () -> Unit = {}
        whenever(resultHandler.onAuthenticationFailure)
            .thenReturn(mockedOnAuthenticationFailure)

        mockBiometricsAvailabilityResponse(
            response = BiometricsAvailability.Failure(
                errorMessage = mockedPlainFailureMessage
            )
        )

        // When
        interactor.handleUserAuthentication(
            context = context,
            crypto = crypto,
            resultHandler = resultHandler
        )

        // Then
        verify(resultHandler, times(1))
            .onAuthenticationFailure
    }
    //endregion

    //region stopPresentation

    @Test
    fun `when interactor stopPresentation is called then it delegates to walletCoreInteractor stopPresentation`() {
        interactor.stopPresentation()
        verify(walletCorePresentationController, times(1)).stopPresentation()
    }

    //endregion

    //region helper functions
    private fun mockWalletCorePresentationControllerEventEmission(event: WalletCorePartialState) {
        whenever(walletCorePresentationController.observeSentDocumentsRequest())
            .thenReturn(event.toFlow())
    }

    private fun mockBiometricsAvailabilityResponse(response: BiometricsAvailability) {
        whenever(deviceAuthenticationInteractor.getBiometricsAvailability(listener = any()))
            .thenAnswer {
                val bioAvailability = it.getArgument<(BiometricsAvailability) -> Unit>(0)
                bioAvailability(response)
            }
    }
    //endregion
}
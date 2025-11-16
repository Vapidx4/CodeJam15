//package com.example.drivesafe
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.update
//import android.content.Context
//import androidx.camera.core.ImageProxy
//import com.google.mediapipe.tasks.vision.core.RunningMode
//
//@Suppress("UNUSED_PARAMETER")
//class FaceDetectionViewModel : ViewModel() {
//    private var faceLandmarkerHelper: FaceLandmarkerHelper? = null
//
//    private val _faceDetectionState = MutableStateFlow(FaceDetectionState())
//    val faceDetectionState = _faceDetectionState.asStateFlow()
//
//    private val _isProcessing = MutableStateFlow(false)
//    val isProcessing = _isProcessing.asStateFlow()
//
//    fun initializeFaceLandmarker(context: Context) {
//        faceLandmarkerHelper = FaceLandmarkerHelper(
//            minFaceDetectionConfidence = 0.5f,
//            minFaceTrackingConfidence = 0.5f,
//            minFacePresenceConfidence = 0.5f,
//            maxNumFaces = 1,
//            currentDelegate = FaceLandmarkerHelper.DELEGATE_CPU,
//            runningMode = RunningMode.LIVE_STREAM,
//            context = context,
//            faceLandmarkerHelperListener = object : FaceLandmarkerHelper.LandmarkerListener {
//                override fun onError(error: String, errorCode: Int) {
//                    _faceDetectionState.update {
//                        it.copy(error = error)
//                    }
//                    _isProcessing.update { false }
//                }
//
//                override fun onResults(resultBundle: FaceLandmarkerHelper.ResultBundle) {
//                    val landmarks = resultBundle.result.faceLandmarks().flatten()
//                    _faceDetectionState.update {
//                        FaceDetectionState(
//                            landmarks = landmarks,
//                            isFaceDetected = landmarks.isNotEmpty(),
//                            inferenceTime = resultBundle.inferenceTime,
//                            error = null
//                        )
//                    }
//                    _isProcessing.update { false }
//                }
//
//                override fun onEmpty() {
//                    _faceDetectionState.update {
//                        FaceDetectionState(
//                            isFaceDetected = false,
//                            inferenceTime = 0L,
//                            error = null
//                        )
//                    }
//                    _isProcessing.update { false }
//                }
//            }
//        )
//    }
//
//    fun processImage(imageProxy: ImageProxy, isFrontCamera: Boolean) {
//        _isProcessing.update { true }
//        faceLandmarkerHelper?.detectLiveStream(imageProxy, isFrontCamera)
//    }
//
//    fun clearFaceLandmarker() {
//        faceLandmarkerHelper?.clearFaceLandmarker()
//        faceLandmarkerHelper = null
//    }
//
//    override fun onCleared() {
//        clearFaceLandmarker()
//        super.onCleared()
//    }
//}
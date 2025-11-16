//package com.example.drivesafe
//
//import com.google.mediapipe.formats.proto.LandmarkProto
//
//// FaceLandmarkerListener.kt
//interface FaceLandmarkerListener {
//    fun onError(error: String, errorCode: Int = FaceLandmarkerHelper.OTHER_ERROR)
//    fun onResults(resultBundle: FaceLandmarkerHelper.ResultBundle)
//    fun onEmpty()
//}
//
//// Face Detection State
//data class FaceDetectionState(
//    val landmarks: List<LandmarkProto.NormalizedLandmark> = emptyList(),
//    val isFaceDetected: Boolean = false,
//    val inferenceTime: Long = 0L,
//    val error: String? = null
//)
package com.github.eucesinha.model

import kotlinx.serialization.Serializable

class VisionModels {

@Serializable
data class VisionRequest(val requests: List<ImageRequest>)

@Serializable
data class ImageRequest(
    val image: ImageContent,
    val features: List<Feature>
)

@Serializable
data class ImageContent(val content: String)

@Serializable
data class Feature(val type: String)

//@Serializable
//data class VisionResponse(val responses: List<LabelResponse>? = null)

@Serializable
data class VisionResponse(
    val responses: List<LabelResponse> = emptyList()
)

@Serializable
data class LabelResponse(
    val labelAnnotations: List<LabelAnnotation> = emptyList()
)

@Serializable
data class LabelAnnotation(
    val description: String,
    val score: Float,
    val topicality: Float
)

}
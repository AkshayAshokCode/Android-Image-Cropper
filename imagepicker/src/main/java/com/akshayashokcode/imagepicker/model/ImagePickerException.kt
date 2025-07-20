package com.akshayashokcode.imagepicker.model

/**
 * Represents various error states that can occur during image picking.
 */
sealed class ImagePickerException(message: String) : Exception(message) {

    /** Required permission was denied by the user */
    object PermissionDenied : ImagePickerException("Required permission was denied.")

    /** No app available to handle the requested action (e.g., no gallery/camera app) */
    object AppNotFound : ImagePickerException("Required app (camera or gallery) is not available.")

    /** Could not create temporary file for captured image */
    object FileCreationFailed : ImagePickerException("Could not create temporary file.")

    /** The image URI returned was null or invalid */
    object InvalidUri : ImagePickerException("Invalid or null URI received.")

    /** Failed to decode or rotate the image properly */
    object RotationFailed : ImagePickerException("Failed to decode or rotate the image.")

    /** Error deleting temporary file after cancel or failure */
    object FileDeletionFailed : ImagePickerException("Failed to delete temporary image file.")

    /** Raised when decoding the image or applying orientation corrections fails. */
    object DecodingFailed : ImagePickerException("Failed to decode or rotate image.")

    object IntentFailed : ImagePickerException("Failed to launch intent for image capture or selection.")


    /** Generic catch-all error */
    class Unknown(message: String, cause: Throwable? = null) : ImagePickerException(message) {
        init {
            cause?.let { initCause(it) }
        }
    }
}
package com.todoarielthibault.todo.user

// UserActivity.kt
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.todoarielthibault.todo.data.Api
import com.todoarielthibault.todo.data.Api.userWebService
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.*


class UserActivity : ComponentActivity() {


    private fun Bitmap.toRequestBody(): MultipartBody.Part {
        val tmpFile = File.createTempFile("avatar", "jpg")
        tmpFile.outputStream().use { // *use*: open et close automatiquement
            this.compress(Bitmap.CompressFormat.JPEG, 100, it) // *this* est le bitmap ici
        }
        return MultipartBody.Part.createFormData(
            name = "avatar",
            filename = "avatar.jpg",
            body = tmpFile.readBytes().toRequestBody()
        )
    }

    private val photoUri by lazy {
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues())
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        val userWebService = Api.userWebService
        super.onCreate(savedInstanceState)

        setContent {
            var bitmap: Bitmap? by remember { mutableStateOf(null) }
            var uri: Uri? by remember { mutableStateOf(null) }


            val composeScope = rememberCoroutineScope()

            // launcher
            val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) uri = photoUri
                if (uri != null) {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    val requestBody = bitmap.toRequestBody()
                    composeScope.launch {
                        userWebService.updateAvatar(requestBody)
                    }
                }
            }
// ...



            Column {
                AsyncImage(
                    modifier = Modifier.fillMaxHeight(.2f),
                    model = bitmap ?: uri,
                    contentDescription = null
                )
                Button(
                    onClick = {
                        takePicture.launch(photoUri)

                    },
                    content = { Text("Take picture") }
                )
                Button(
                    onClick = {},
                    content = { Text("Pick photo") }
                )
            }
        }
    }
}

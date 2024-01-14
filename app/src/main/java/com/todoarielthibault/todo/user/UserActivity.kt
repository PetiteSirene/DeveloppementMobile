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
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.todoarielthibault.todo.data.Api
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class UserActivity : ComponentActivity() {


    private var username by mutableStateOf("")

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

    private fun Uri.toRequestBody(): MultipartBody.Part {
        val fileInputStream = contentResolver.openInputStream(this)!!
        val fileBody = fileInputStream.readBytes().toRequestBody()
        return MultipartBody.Part.createFormData(
            name = "avatar",
            filename = "avatar.jpg",
            body = fileBody
        )
    }

    private val captureUri by lazy {
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues())
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        val userWebService = Api.userWebService
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            username = userWebService.fetchUser().body()!!.name
        }

        setContent {
            var bitmap: Bitmap? by remember { mutableStateOf(null) }
            var uri: Uri? by remember { mutableStateOf(null) }


            val composeScope = rememberCoroutineScope()


            // launcher
            val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) uri = captureUri
                if (uri != null) {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    val requestBody = bitmap.toRequestBody()
                    composeScope.launch {
                        userWebService.updateAvatar(requestBody)
                    }
                }
            }



            val pickPicture = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri->
                if (uri != null) {
                    val requestBody = uri.toRequestBody()
                    composeScope.launch {
                        userWebService.updateAvatar(requestBody)
                    }
                } else {

                }
            }

            val askPermission = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
                if (it){
                    pickPicture.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
                /*
                else {
                    pickPicture.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
                 */
            }



            Column {
                AsyncImage(
                    modifier = Modifier.fillMaxHeight(.2f),
                    model = bitmap ?: uri,
                    contentDescription = null
                )
                Button(
                    onClick = {
                        takePicture.launch(captureUri)

                    },
                    content = { Text("Take picture") }
                )
                Button(
                    onClick = {
                        askPermission.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    },
                    content = { Text("Pick photo") }
                )
                Text("Username: $username", modifier = Modifier.padding(16.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = { newName -> username = newName },
                    label = { Text("Edit Username") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                Button(
                    onClick = {
                        lifecycleScope.launch {
                            userWebService.update(UserUpdate(username))
                        }
                    },
                    content = { Text("Valider") }
                )
            }
        }
    }
}

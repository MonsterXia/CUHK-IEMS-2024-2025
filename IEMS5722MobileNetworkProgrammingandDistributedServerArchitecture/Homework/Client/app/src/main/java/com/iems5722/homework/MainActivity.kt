package com.iems5722.homework

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.iems5722.homework.ui.theme.HomeworkTheme
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpGet
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.HttpClientBuilder

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.BufferedReader

import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get the registration token
            val token = task.result
            Log.d(TAG, "FCM registration token: $token")
            // Send the token to your server or save it locally
        }

        FirebaseMessaging.getInstance().subscribeToTopic("all")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.e("TAG", "onCreate: subscribeToTopic", )
                }else{
                    Log.e("TAG", "onCreate: subscribeToTopic failed", )
                }
            }

        requestNotificationPermission()

        enableEdgeToEdge()
        setContent {
            HomeworkTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val self_fig = "https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410221715894.jpg"
                    // TODO: Your Student id
                    val self_uid = 1155000000
                    Greeting(
                        self_fig = self_fig,
                        self_uid = self_uid,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    0
                )
            }
            // The code below is related to create notification channel for later use
            val channel = NotificationChannel("MyNotification","MyNotification",
                NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java) as
                    NotificationManager;
            manager.createNotificationChannel(channel)

        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(DelicateCoroutinesApi::class)
@Composable
fun Greeting(self_fig: String, self_uid: Int, modifier: Modifier = Modifier) {
    var isLoaded by remember { mutableStateOf(false) }

    var context = LocalContext.current

    var bgHeight = ContentScale.FillHeight
    var headerHeight by remember { mutableIntStateOf(0) }
    var bottomHeight by remember { mutableIntStateOf(0) }

    var backgroundImgUrl = "https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410040609825.jpg"

    var contractsList by remember { mutableStateOf(listOf<ContractsItem>()) }

    LaunchedEffect(true) {
        // TODO: Put your IP and Port here
        val url = "http://IP:Port/get_chatrooms/"
        val result = GET(url)
        val obj = Json.decodeFromString<GetChatroomList>(result)

        withContext(Dispatchers.Main){
            obj.data.forEach { chatroom ->
                // TODO: Store the avatar/Latest message/unread/isGroup in server and fix user Url
                var userUrl = "https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410040238667.jpg"
                val userTemp = User(userUrl, chatroom.id, chatroom.name)

                val chatroomTemp = ContractsItem(userTemp, "", "", 0, true )
                contractsList += chatroomTemp
            }
            isLoaded = true
        }
    }

    if ( !isLoaded){
        CircularProgressIndicator()
    }else {
        Box(
            // Background layer
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp)
        ){
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(backgroundImgUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "bg_img",
                contentScale = bgHeight,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                // Header
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .background(Color(android.graphics.Color.parseColor("#F8FFFFFF")))
                    .onGloballyPositioned { coordinates ->
                        headerHeight = coordinates.size.height / 2
                    }
                    .zIndex(1f)
            ){
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(self_fig)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(R.drawable.placeholder),
                        contentDescription = "user_img",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(48.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.myname))
                }
            }

            Box (
                // Bottom
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color(android.graphics.Color.parseColor("#F8FFFFFF")))
                    .onGloballyPositioned { coordinates ->
                        bottomHeight = coordinates.size.height / 2
                    }
                    .zIndex(1f)
            ){
                Column {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        IconButton(
                            onClick = { },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.notice),
                                contentDescription = "Notice"
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.contracts),
                                contentDescription = "Contracts"
                            )
                        }
                    }
                }
            }

            Box(
                // Lazy Column
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .align(Alignment.TopCenter)
                    .background(Color.Transparent)
            ){
                Column {
                    Spacer(modifier = Modifier.height(headerHeight.dp))
                    LazyColumn {
                        itemsIndexed(contractsList) { index, contractsItem ->
                            Row (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .clickable(
                                        onClick = {
                                            var intent = Intent(context, ChatActivity::class.java)

                                            intent.putExtra("avatar", contractsItem.user.avatar)
                                            intent.putExtra("uid", contractsItem.user.uid.toString())
                                            intent.putExtra("nickname", contractsItem.user.nickname)
                                            intent.putExtra("msgShow", contractsItem.msgShow)
                                            intent.putExtra("isGroup", contractsItem.isGroup)
                                            intent.putExtra("self_fig", self_fig)
                                            intent.putExtra("self_uid", self_uid.toString())

                                            context.startActivity(intent)
                                        }
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(contractsItem.user.avatar)
                                        .crossfade(true)
                                        .build(),
                                    placeholder = painterResource(R.drawable.placeholder),
                                    contentDescription = "user_img",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .size(48.dp),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column (
                                    modifier = Modifier.weight(1f)
                                ){
                                    Text(
                                        text = contractsItem.user.nickname,
                                        style = TextStyle(fontSize = 20.sp)
                                    )
                                    Text(
                                        text = contractsItem.msgShow,
                                        style = TextStyle(fontSize = 10.sp),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                                Column (
                                    horizontalAlignment = Alignment.End
                                ){
                                    Text(
                                        text = contractsItem.latestTime,
                                        style = TextStyle(fontSize = 10.sp)
                                    )
                                    Text(
                                        text = contractsItem.unread.toString()
                                    )
                                }
                            }

                            HorizontalDivider()
                            if (index == contractsList.size - 1) {
                                Spacer(modifier = Modifier.height(bottomHeight.dp))
                            }
                        }
                    }

                }
            }
        }

    }
}

data class User(
    var avatar: String,
    var uid: Int,
    var nickname: String
)

data class ContractsItem(
    var user: User,
    var msgShow: String,
    var latestTime: String,
    var unread: Int,
    var isGroup: Boolean
)

@Serializable
data class Chatroom(val id: Int, val name: String)
@Serializable
data class GetChatroomList(val data: List<Chatroom>, val status: String)

fun GET(url: String?): String {
    var inputStream: InputStream? = null
    var result = ""
    try {
        // 1. create HttpClient
        val httpclient: HttpClient = HttpClientBuilder.create().build()
        // 2. make GET request to the given URL
        val httpResponse: HttpResponse = httpclient.execute(HttpGet(url))
        // 3. receive response as inputStream
        inputStream = httpResponse.getEntity().getContent()
        // 4. convert inputstream to string
        if (inputStream != null) result =
            convertInputStreamToString(inputStream).toString()
        else result = "Did not work!"
    } catch (e: Exception) {
        Log.d("InputStream", e.localizedMessage)
    }
    return result
}
@Throws(IOException::class)
private fun convertInputStreamToString(inputStream: InputStream): String? {
    val bufferedReader = BufferedReader(InputStreamReader(inputStream))
    var line: String? = ""
    var result: String? = ""
    while ((bufferedReader.readLine().also { line = it }) != null)
        result += line
    inputStream.close()
    return result
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HomeworkTheme {
//        var userUrl = "https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410040238667.jpg"
//        var user = User(userUrl, 12345, "Skadi")
//        var msgList by remember { mutableStateOf(listOf<ListItem>()) }
//        var contractsList by remember { mutableStateOf(listOf<ContractsItem>()) }
//
//        msgList = listOf(
//            ListItem(true, "Hello", "2024-10-04")
//        )
//        contractsList = listOf(
//            ContractsItem(user, "", "2024-10-04", 0),
//        )
        val self_fig = "https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410221715894.jpg"
        // TODO: YOUR STUDENT ID
        val self_uid = 1155000000
        Greeting(self_fig, self_uid)
    }
}
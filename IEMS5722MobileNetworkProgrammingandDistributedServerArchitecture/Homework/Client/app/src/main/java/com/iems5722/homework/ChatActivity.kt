package com.iems5722.homework

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.updateBounds
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.iems5722.homework.ui.theme.HomeworkTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

import java.util.Calendar
import java.util.Locale

class ChatActivity  : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HomeworkTheme {
                val context = LocalContext.current

                var avatar = intent.getStringExtra("avatar").toString()
                var uid = intent.getStringExtra("uid")?.toIntOrNull() ?: 0
                var nickname = intent.getStringExtra("nickname").toString()
                var msgShow = intent.getStringExtra("msgShow").toString()
                var isGroup = intent.getStringExtra("isGroup").toBoolean()

                var user = User(avatar, uid, nickname)

                var self_fig = intent.getStringExtra("self_fig").toString()
                var self_uid = intent.getStringExtra("self_uid")?.toIntOrNull() ?: 0

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ChatShow(
                        self_fig = self_fig,
                        self_uid = self_uid,
                        isGroup = isGroup,
                        user = user,
                        msgShow=msgShow,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


fun POST(url: String, toPost:PostRequestModel): String {
    // 1. create HttpURLConnection
    val conn = URL(url).openConnection() as HttpURLConnection
    conn.requestMethod = "POST"
    conn.doOutput = true
    conn.doInput = true
    conn.readTimeout = 15000
    conn.connectTimeout = 15000
    conn.setRequestProperty("Content-Type", "application/json")
    conn.setRequestProperty("Accept", "application/json")
    // 2. build JSON object
    val message = Json.encodeToString(toPost)
    // 3. add JSON content to POST request body
    val os: OutputStream = conn.outputStream
    val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
    writer.write(message)
    writer.flush()
    writer.close()
    os.close()
    // 4. return response message
    val responseCode = conn.responseCode
    if (responseCode == HttpURLConnection.HTTP_OK) {
        val `in` = BufferedReader(InputStreamReader(conn.inputStream))
        val sb = StringBuffer("")
        var line: String? = ""
        while (`in`.readLine().also { line = it } != null) {
            sb.append(line)
            break
        }
        `in`.close()
        return sb.toString()
//        return conn.responseMessage + "" // previously
    }
    return "ERROR"
}

data class ListItem(
    var user_id: Int,
    var content: String,
    var time: String
)

@Serializable
data class Getmsg(val message: String, val name: String, val message_time: String, val user_id: Int)
@Serializable
data class GetmsgArray(val messages: Array<Getmsg>)
@Serializable
data class GetmsgList(val data: GetmsgArray, val status: String)
@Serializable
data class PostRequestModel(val chatroom_id: Int, val user_id: Int, val name: String, val message: String)
@Serializable
data class PostResponseModel(val status: String)


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(DelicateCoroutinesApi::class)
@Composable
fun ChatShow(self_fig: String, self_uid: Int, isGroup: Boolean, user: User, msgShow: String, modifier: Modifier = Modifier){
    var isLoaded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val bgHeight = ContentScale.FillHeight
    var headerHeight by remember { mutableStateOf(0) }
    var bottomHeight by remember { mutableStateOf(0) }

    val backgroundImgUrl =
        "https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410040609825.jpg"
    val chatBg = ContextCompat.getDrawable(LocalContext.current, R.drawable.chat)

    var msgList by remember { mutableStateOf(listOf<ListItem>()) }
    var str by remember { mutableStateOf("") }

    var postMessage by remember { mutableStateOf("Test") }

    LaunchedEffect(true) {
        // TODO: Put your IP and Port here
        val url = "http://IP:Port/get_messages/?chatroom_id=${user.uid}"
        val result = GET(url)
        val obj = Json.decodeFromString<GetmsgList>(result)
        withContext(Dispatchers.Main){
            obj.data.messages.reversed().forEach { Getmsg ->
                // TODO: Build user_id, user_avatar pair in DB
                val listItemTemp = ListItem(Getmsg.user_id, Getmsg.message, Getmsg.message_time)
                msgList += listItemTemp
            }

            if (msgList.isNotEmpty() && msgList.last().content != msgShow) {
                str = msgShow
            } else if (msgList.isEmpty() && msgShow.isNotBlank()) {
                str = msgShow
            } else {
                str = ""
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
        ) {
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
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.normal_left),
                            contentDescription = "Back"
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = user.nickname)
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = {
                            GlobalScope.launch {
                                // TODO: Put your IP and Port here
                                val url = "http://IP:Port/get_messages/?chatroom_id=${user.uid}"
                                val result = GET(url)
                                val obj = Json.decodeFromString<GetmsgList>(result)
                                withContext(Dispatchers.Main){
                                    msgList = listOf<ListItem>()
                                    obj.data.messages.reversed().forEach { Getmsg ->
                                        // TODO: Build user_id, user_avatar pair in DB
                                        val listItemTemp = ListItem(Getmsg.user_id, Getmsg.message, Getmsg.message_time)
                                        msgList += listItemTemp
                                    }

                                    if (msgList.isNotEmpty() && msgList.last().content != msgShow) {
                                        str = msgShow
                                    } else if (msgList.isEmpty() && msgShow.isNotBlank()) {
                                        str = msgShow
                                    } else {
                                        str = ""
                                    }
                                    isLoaded = true
                                }
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.refresh),
                            contentDescription = "Refresh"
                        )
                    }
                }
            }

            Box(
                // Bottom
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color(android.graphics.Color.parseColor("#F8FFFFFF")))
                    .onGloballyPositioned { coordinates ->
                        bottomHeight = coordinates.size.height / 2
                    }
                    .zIndex(1f)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = str,
                            onValueChange = { text ->
                                str = text
                            },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (str.isNotBlank()) {
                                    val calendar = Calendar.getInstance()
                                    val year = calendar.get(Calendar.YEAR)
                                    val month = calendar.get(Calendar.MONTH)
                                    val day = calendar.get(Calendar.DAY_OF_MONTH)
                                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                                    val minute = calendar.get(Calendar.MINUTE)
                                    var listItem = ListItem(
                                        self_uid,
                                        str,
                                        String.format(
                                            Locale.getDefault(),
                                            "%04d-%02d-%02d\t%02d:%02d",
                                            year,
                                            month,
                                            day,
                                            hour,
                                            minute
                                        )
                                    )
                                    msgList += listItem

                                    CoroutineScope(Dispatchers.Default).launch {
//                                    GlobalScope.launch {
                                        // TODO: Put your IP and Port here
                                        val url = "http://IP:Port/send_message/"
                                        // TODO: Your Name Here
                                        val toPostName = "Your Name"
                                        val toPost = PostRequestModel(
                                            chatroom_id = user.uid,
                                            user_id = self_uid,
                                            name = toPostName,
                                            message = str
                                        )
//
                                        val data = POST(url, toPost)
                                        // val obj = Json.decodeFromString<PostResponseModel>(data)
                                        withContext(Dispatchers.Main){
                                            str = ""
                                        }
                                    }


                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.normal_right),
                                contentDescription = "Send"
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
            ) {
                Column {
                    Spacer(modifier = Modifier.height(headerHeight.dp))
                    Text(text = postMessage)
                    LazyColumn {
                        itemsIndexed(msgList) { index, listItem ->
                            val receiver: Boolean
                            if (listItem.user_id == self_uid){
                                receiver = false
                            }else{
                                receiver = true
                            }

                            if (receiver) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    val dannyURL = "https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410221647187.png"
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(dannyURL)
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
                                    Box(
                                        modifier = Modifier.weight(1f),
                                        contentAlignment = Alignment.TopStart
                                    ) {
                                        // TODO:Usually it should get Name from server by sending id
                                        var sender_name = "Danny3"
                                        if (listItem.user_id == 1){
                                            sender_name = "Danny1"
                                        }else if (listItem.user_id == 2){
                                            sender_name = "Danny2"
                                        }
                                        // listItem.time = "%04d-%02d-%02d\t%02d:%02d"
                                        val timeBeautify = listItem.time.substring(11 until 16)

                                        val nameAndTime = sender_name + "\t" + timeBeautify
                                        Text(
                                            text = nameAndTime,
                                            style = TextStyle(fontSize = 12.sp),
                                        )
                                        Box() {
                                            Text(
                                                text = listItem.content,
                                                style = TextStyle(fontSize = 16.sp),
                                                modifier = Modifier
                                                    .drawBehind {
                                                        chatBg?.updateBounds(
                                                            0,
                                                            0,
                                                            size.width.toInt(),
                                                            size.height.toInt()
                                                        )
                                                        chatBg?.draw(drawContext.canvas.nativeCanvas)
                                                    }
                                                    .padding(
                                                        start = 40.dp,
                                                        end = 15.dp,
                                                        top = 30.dp,
                                                        bottom = 30.dp
                                                    )
                                            )
                                        }

                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                            } else {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier.weight(1f),
                                        contentAlignment = Alignment.TopEnd
                                    ) {
                                        val timeBeautify = listItem.time.substring(11 until 16)
                                        Text(
                                            text = timeBeautify,
                                            style = TextStyle(fontSize = 12.sp),
                                        )
                                        Box() {
                                            Text(
                                                text = listItem.content,
                                                style = TextStyle(fontSize = 16.sp),
                                                modifier = Modifier
                                                    .drawBehind {
                                                        chatBg?.updateBounds(
                                                            0,
                                                            0,
                                                            size.width.toInt(),
                                                            size.height.toInt()
                                                        )
                                                        chatBg?.draw(drawContext.canvas.nativeCanvas)
                                                    }
                                                    .padding(
                                                        start = 40.dp,
                                                        end = 15.dp,
                                                        top = 30.dp,
                                                        bottom = 30.dp
                                                    )
                                            )
                                        }

                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
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



//                                    Image(
//                                        painter = painterResource(R.drawable.placeholder),
//                                        contentDescription = "user_img",
//                                        contentScale = ContentScale.Crop,
//                                        modifier = Modifier
//                                            .clip(CircleShape)
//                                            .size(48.dp),
//                                    )

                                }
                            }
                            if (index == msgList.size - 1) {
                                Spacer(modifier = Modifier.height(bottomHeight.dp))
                            }
                        }
                    }

                }
            }
        }
    }
}
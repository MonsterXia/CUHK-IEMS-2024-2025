# Assignment2

<div align=center style="font-size: 18px;">
    Name<u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u><u>Your Name</u><u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u>SID<u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u><u>1155xxxxxx</u><u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u>
</div>


### Basic information

My phone model is <strong>XiaoMi 12S Pro</strong>(Xiaomi 2206122SC/Android 12L)
I use an Android emulator.

[TOC]

<div style="page-break-after: always;"></div>
## Result

### 1. The app contains two activities, which are MainActivity and ChatActivity. (10%)

Screenshot(s) for the project tree:

![](https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202501111831703.png)

Screenshot(s) for the updated part in the AndroidManifest.xml :

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">
    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme._1155xxxxxx_Assignment2"
        tools:targetApi="31">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme._1155xxxxxx_Assignment2">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        <activity
            android:name=".ChatActivity"
            android:exported="true"
            android:theme="@style/Theme._1155xxxxxx_Assignment2">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

### 2. There is a button on MainActivity which will allow the user to enter the ChatActivity. (10%)

Screenshot(s) for emulator on the MainActivity:

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410070407674.png"/>

Screenshot(s) for code segment on UI:

```kotlin
package com.monsterxia.a1155xxxxxx_assignment2

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Parcel
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.monsterxia.a1155xxxxxx_assignment2.ui.theme._1155xxxxxx_Assignment2Theme
import java.io.File
import java.io.FileWriter


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _1155xxxxxx_Assignment2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var userUrl = "https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410040238667.jpg"
                    var user = User(userUrl, 12345, "Skadi")
                    var msgList by remember { mutableStateOf(listOf<ListItem>()) }
                    var contractsList by remember { mutableStateOf(listOf<ContractsItem>()) }

                    msgList = listOf(
                        ListItem(true, "Hello", "2024-10-04")
                    )
                    contractsList = listOf(
                        ContractsItem(user, "", "2024-10-04", 0, msgList),
                    )

                    Greeting(
                        contractsList = contractsList,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(contractsList: List<ContractsItem>, modifier: Modifier = Modifier) {
    var context = LocalContext.current

    var bgHeight = ContentScale.FillHeight
    var headerHeight by remember { mutableStateOf(0) }
    var bottomHeight by remember { mutableStateOf(0) }

    var backgroundImgUrl = "https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410040609825.jpg"
    var chatBg = ContextCompat.getDrawable(LocalContext.current, R.drawable.chat)

    var str by remember { mutableStateOf("") }
    var msgList by remember { mutableStateOf(listOf<ListItem>()) }

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
                Image(
                    painter = painterResource(R.drawable.placeholder),
                    contentDescription = "user_img",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(48.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.my_name))
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

//                                        val fileName = "${contractsItem.user.uid}_data.csv"
//                                        val dir = File(context.filesDir, "Data/CSVData")
//                                        if (!dir.exists()) {
//                                            dir.mkdirs()
//                                        }
//                                        val file = File(dir, fileName)
//
//                                        FileWriter(file).use { writer ->
//                                            for (item in contractsItem.msgList) {
//                                                writer.append(
//                                                    "${item.receiver},${item.content},${item.time}\n"
//                                                )
//                                            }
//                                        }
//
//                                        val builder = AlertDialog.Builder(context)
//                                        builder.setTitle("title")
//                                        builder.setMessage("Write Finish")
//                                        builder.setPositiveButton("OK") { dialog, which ->
//                                            // 点击“OK”按钮后的操作
//                                            dialog.dismiss()
//                                        }
//                                        builder.setNegativeButton("Cancel") { dialog, which ->
//                                            // 点击“Cancel”按钮后的操作
//                                            dialog.dismiss()
//                                        }
//                                        val dialog = builder.create()
//                                        dialog.show()

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
                        if (index == msgList.size - 1) {
                            Spacer(modifier = Modifier.height(bottomHeight.dp))
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

data class ListItem(
    var receiver: Boolean,
    var content: String,
    var time: String
)

data class ContractsItem(
    var user: User,
    var msgShow: String,
    var latestTime: String,
    var unread: Int,
    var msgList:List<ListItem>
)


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    _1155xxxxxx_Assignment2Theme {
        var userUrl = "https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410040238667.jpg"
        var user = User(userUrl, 12345, "Skadi")
        var msgList by remember { mutableStateOf(listOf<ListItem>()) }
        var contractsList by remember { mutableStateOf(listOf<ContractsItem>()) }

        msgList = listOf(
            ListItem(true, "Hello", "2024-10-04")
        )
        contractsList = listOf(
            ContractsItem(user, "", "2024-10-04", 0, msgList),
        )
        Greeting(contractsList)
    }
}
```

Screenshot(s) for code segment on intent:

```kotlin
onClick = {
                                        var intent = Intent(context, ChatActivity::class.java)

                                        intent.putExtra("avatar", contractsItem.user.avatar)
                                        intent.putExtra("uid", contractsItem.user.uid.toString())
                                        intent.putExtra("nickname", contractsItem.user.nickname)
                                        intent.putExtra("msgShow", contractsItem.msgShow)
}
```



### 3. The ChatActivity has a back button on the top, allowing the user to go back to the MainActivity. (10%)

 Screenshot(s) for emulator on the ChatActivity:

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410070409869.png"/>

 Screenshot(s) for code segment on intent:

```kotlin
onClick = {
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }
```

### 4. The ChatActivity consists of a LazyColumn for listing past messages, an EditText for user input, and an ImageButton or ImageView for the user to send the message. (15%)

Screenshot(s) for emulator on the ChatActivity: (same as task 3)

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410070436550.png"/>

Screenshot(s) for code segment on UI:

```kotlin
package com.monsterxia.a1155xxxxxx_assignment2

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
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
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.updateBounds
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.monsterxia.a1155xxxxxx_assignment2.ui.theme._1155xxxxxx_Assignment2Theme
import java.util.Calendar
import java.util.Locale

class ChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _1155xxxxxx_Assignment2Theme {
                val context = LocalContext.current

                var avatar = intent.getStringExtra("avatar")
                var uid = intent.getStringExtra("uid")?.toIntOrNull() ?: 0
                var nickname = intent.getStringExtra("nickname")
                var msgShow = intent.getStringExtra("msgShow").toString()

                var user = User(avatar.toString(), uid, nickname.toString())

//
//                var msgList by remember { mutableStateOf(listOf<ListItem>()) }
//
//                val fileName = "${user.uid}_data.csv"
//                val dir = File(context.filesDir, "Data/CSVData")
//                val file = File(dir, fileName)
//
//                if (file.exists()) {
//                    file.bufferedReader().forEachLine { line ->
//                        val parts = line.split(",")
//                        if (parts.size == 3) {
//                            val receiver = parts[0].toBoolean()
//                            val content = parts[1]
//                            val time = parts[2]
//                            msgList += ListItem(receiver, content, time)
//                        }
//                    }
//                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ChatShow(
                        user = user,
                        msgShow=msgShow,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


fun msgListIni(): List<ListItem> {
    var msgList = listOf(
        ListItem(true,"Hello","00:00"),
        ListItem(false,"Hello","00:00"),
        ListItem(true,"Nice to meet you","00:00")
    )
    return msgList
}

@Composable
fun ChatShow(user: User, msgShow: String, modifier: Modifier = Modifier){
    val context = LocalContext.current

    val bgHeight = ContentScale.FillHeight
    var headerHeight by remember { mutableStateOf(0) }
    var bottomHeight by remember { mutableStateOf(0) }

    val backgroundImgUrl = "https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410040609825.jpg"
    val chatBg = ContextCompat.getDrawable(LocalContext.current, R.drawable.chat)

    var msgList by remember { mutableStateOf(msgListIni()) }
    var str by remember { mutableStateOf("") }

    LaunchedEffect(true) {
        if (msgList.isNotEmpty() && msgList.last().content != msgShow) {
            str = msgShow
        } else if (msgList.isEmpty() && msgShow.isNotBlank() ){
            str = msgShow
        }else{
            str = ""
        }
    }

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
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                IconButton(
                    onClick = {
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.normal_left),
                        contentDescription = "Send"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = user.nickname)
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
                    verticalAlignment = Alignment.CenterVertically
                ){
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
                            if (str.isNotBlank()){

                                val calendar = Calendar.getInstance()
                                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                                val minute = calendar.get(Calendar.MINUTE)
                                var listItem = ListItem(false,
                                    str,
                                    String.format(
                                        Locale.getDefault(),
                                        "%02d:%02d",
                                        hour,
                                        minute
                                    )
                                )
                                msgList += listItem

                                str = ""
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
        ){
            Column {
                Spacer(modifier = Modifier.height(headerHeight.dp))
                LazyColumn {
                    itemsIndexed(msgList) { index,listItem ->
                        if (listItem.receiver){
                            Row (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.Start
                            ){
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(user.avatar)
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
                                ){
                                    Text(
                                        text = listItem.time,
                                        style = TextStyle(fontSize = 12.sp),
                                    )
                                    Box(){
                                        Text(
                                            text = listItem.content,
                                            style = TextStyle(fontSize = 16.sp),
                                            modifier = Modifier
                                                .drawBehind {
                                                    chatBg?.updateBounds(0, 0, size.width.toInt(), size.height.toInt())
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
                        }else{
                            Row (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.TopEnd
                                ){
                                    Text(
                                        text = listItem.time,
                                        style = TextStyle(fontSize = 12.sp),
                                    )
                                    Box(){
                                        Text(
                                            text = listItem.content,
                                            style = TextStyle(fontSize = 16.sp),
                                            modifier = Modifier
                                                .drawBehind {
                                                    chatBg?.updateBounds(0, 0, size.width.toInt(), size.height.toInt())
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
                                Image(
                                    painter = painterResource(R.drawable.placeholder),
                                    contentDescription = "user_img",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .size(48.dp),
                                )

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

```

### 5. The ChatActivity displayed some past messages using a hard code function. (10%)

Screenshot(s) for the hard code function:

<ul>
    <li>This function generates a predefined chat history.</li>
    <li>The content in the hard code function should be displayed in the emulator screenshot in task 3 and task 4.</li>
</ul>

```kotlin
fun msgListIni(): List<ListItem> {
    var msgList = listOf(
        ListItem(true,"Hello","00:00"),
        ListItem(false,"Hello","00:00"),
        ListItem(true,"Nice to meet you","00:00")
    )
    return msgList
}
```



### 6. In the ChatActivity, only your messages are aligned to the right, while the remaining messages are aligned to the left. (10%)

Screenshot(s) for emulator on the ChatActivity: (same as task 3)

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410070436550.png"/>

Screenshot(s) for code segment on text alignment:

```kotlin
if (listItem.receiver){
                            Row (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.Start
                            ){
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(user.avatar)
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
                                ){
                                    Text(
                                        text = listItem.time,
                                        style = TextStyle(fontSize = 12.sp),
                                    )
                                    Box(){
                                        Text(
                                            text = listItem.content,
                                            style = TextStyle(fontSize = 16.sp),
                                            modifier = Modifier
                                                .drawBehind {
                                                    chatBg?.updateBounds(0, 0, size.width.toInt(), size.height.toInt())
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
                        }else{
                            Row (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.TopEnd
                                ){
                                    Text(
                                        text = listItem.time,
                                        style = TextStyle(fontSize = 12.sp),
                                    )
                                    Box(){
                                        Text(
                                            text = listItem.content,
                                            style = TextStyle(fontSize = 16.sp),
                                            modifier = Modifier
                                                .drawBehind {
                                                    chatBg?.updateBounds(0, 0, size.width.toInt(), size.height.toInt())
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
                                Image(
                                    painter = painterResource(R.drawable.placeholder),
                                    contentDescription = "user_img",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .size(48.dp),
                                )

                            }
                        }
```

### 7.  Each message item in the LazyColumn should contain three elements, which are the message itself, the time (HH:MM) when the message is sent and a chat image background behind the text. (15%)

Screenshot(s) for the data class (code segment) of the message:

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410070439068.png"/>

```kotlin
LazyColumn {
                    itemsIndexed(msgList) { index,listItem ->
                        if (listItem.receiver){
                            Row (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.Start
                            ){
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(user.avatar)
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
                                ){
                                    Text(
                                        text = listItem.time,
                                        style = TextStyle(fontSize = 12.sp),
                                    )
                                    Box(){
                                        Text(
                                            text = listItem.content,
                                            style = TextStyle(fontSize = 16.sp),
                                            modifier = Modifier
                                                .drawBehind {
                                                    chatBg?.updateBounds(0, 0, size.width.toInt(), size.height.toInt())
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
                        }else{
                            Row (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.TopEnd
                                ){
                                    Text(
                                        text = listItem.time,
                                        style = TextStyle(fontSize = 12.sp),
                                    )
                                    Box(){
                                        Text(
                                            text = listItem.content,
                                            style = TextStyle(fontSize = 16.sp),
                                            modifier = Modifier
                                                .drawBehind {
                                                    chatBg?.updateBounds(0, 0, size.width.toInt(), size.height.toInt())
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
                                Image(
                                    painter = painterResource(R.drawable.placeholder),
                                    contentDescription = "user_img",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .size(48.dp),
                                )

                            }
                        }
                        if (index == msgList.size - 1) {
                            Spacer(modifier = Modifier.height(bottomHeight.dp))
                        }
                    }
                }
```

### 8. When the user clicks on the send button, the app should perform the following actions: (20%)

<ul>
    <li>Check if the user has input any text (i.e. check whether the input area is empty)</li>
    <li>If the user has input some text, add a new item containing the message to the end of the LazyColumn; and then clear the input area.</li>
</ul>

Screenshot(s) for emulator on the ChatActivity, with a message typed in the message box:

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410070442773.png"/>

Screenshot(s) for emulator on the ChatActivity, with the message just sent to the chatroom:

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410070443438.png"/>

Screenshot(s) for emulator on the ChatActivity, with another message typed in the message box:

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410070443251.png"/>

Screenshot(s) for emulator on the ChatActivity, with the message just sent to the chatroom:

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410070444727.png"/>

 Screenshot(s) for scrolling the LazyColumn, such that:

<ul>
    <li>The message on the top: only the lower half of the message is shown.</li>
    <li>The message at the bottom: only the upper half of the message is shown.</li>
</ul>

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410070446992.png"/>

Screenshot(s) for code segment on checking the text message is non-empty:

```kotlin
onClick = {
                            if (str.isNotBlank()){
                                val calendar = Calendar.getInstance()
                                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                                val minute = calendar.get(Calendar.MINUTE)
                                var listItem = ListItem(false,
                                    str,
                                    String.format(
                                        Locale.getDefault(),
                                        "%02d:%02d",
                                        hour,
                                        minute
                                    )
                                )
                                msgList += listItem

                                str = ""
                            }
                        }
```

Screenshot(s) for code segment on clearing the text message:

```kotlin
onClick = {
                            if (str.isNotBlank()){
                                val calendar = Calendar.getInstance()
                                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                                val minute = calendar.get(Calendar.MINUTE)
                                var listItem = ListItem(false,
                                    str,
                                    String.format(
                                        Locale.getDefault(),
                                        "%02d:%02d",
                                        hour,
                                        minute
                                    )
                                )
                                msgList += listItem

                                str = ""
                            }
                        }
```

## Reference

<ul>
    <li>https://www.iconfont.cn/collections/detail?spm=a313x.collections_index.i1.d9df05512.14e13a81WtZIhj&cid=50108</li>
    <li>https://github.com/coil-kt/coil/blob/main/README-zh.md#jetpack-compose</li>
    <li>https://www.iconfont.cn/collections/detail?spm=a313x.collections_index.i1.d9df05512.2cfe3a81iKXHIn&cid=50028</li>
</ul>



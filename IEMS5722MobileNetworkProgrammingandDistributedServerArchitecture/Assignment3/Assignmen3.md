# Assignment3

<div align=center style="font-size: 18px;">
    Name<u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u><u>Your Name</u><u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u>SID<u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u><u>1155xxxxxx</u><u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u>
</div>

### Basic information

My phone model is <strong>XiaoMi 12S Pro</strong>(Xiaomi 2206122SC/Android 12L)
I use an Android emulator.

Due to Problem of Global Protect, I deploy the main.py in my own server IP:Port

[TOC]

<div style="page-break-after: always;"></div>
## Result

### 1. Able to retrieve and list Chatrooms automatically. (20%)

Screenshot(s) for emulator on the MainActivity:

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410230629085.png"/>

Screenshot(s) for code segment on the GET methods related to listing Chatrooms:

```kotlin
LaunchedEffect(true) {
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
```

### 2. Clicking on a chatroom in MainActivity goes to the correct Chatroom. (20%)

Screenshot(s) for emulator on different ChatActivity:

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410230634840.png"/>

Screenshot(s) for code segment on the intent from MainActivity to ChatAcitivity:

```kotlin	
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
```

### 3. Able to retrieve messages and refresh messages in the Chatroom. (20%)

Screenshot(s) for code segment on GET method related to retrieving messages:

```kotlin
LaunchedEffect(true) {
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
```

Screenshot(s) for code segment on the refresh button:

```kotlin	
IconButton(
    onClick = {
        GlobalScope.launch {
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
```

### 4. Able to use asynchronous tasks (e.g. coroutine) to handle both GET and POST requests. (20%)

Screenshot(s) for code segment on the coroutine on getting chatroom:

```kotlin
LaunchedEffect(true) {
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
```

Screenshot(s) for code segment on the coroutine on getting messages:

```kotlin
LaunchedEffect(true) {
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
```

Screenshot(s) for code segment on the coroutine on sending messages:

```kotlin
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
                val url = "http://IP:Port/send_message/"
                val toPostName = "Your Name"
                val toPost = PostRequestModel(
                    chatroom_id = user.uid,
                    user_id = self_uid.toString(),
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
```

### 5. Able to send messages to a specific Chatroom and update UI correctly. (20%)

Screenshot(s) for emulator on the ChatActivity: (same as task 2)

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410230634840.png"/>

Screenshot(s) for emulator on the ChatActivity after sending a message:

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410230648965.png"/>

Screenshot(s) for emulator on the ChatActivity after refreshing the chatroom: (same as task 2)

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410230649734.png"/>

Screenshot(s) for code segment for updating the ChatActivity after sending a message:

```kotlin
// done by Lazy Column
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
                                               val sender_name = "Danny"
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
```

## Reference

<ul>
    <li>https://www.iconfont.cn/collections/detail?spm=a313x.collections_index.i1.d9df05512.2cfe3a81iKXHIn&cid=50028</li>
</ul>




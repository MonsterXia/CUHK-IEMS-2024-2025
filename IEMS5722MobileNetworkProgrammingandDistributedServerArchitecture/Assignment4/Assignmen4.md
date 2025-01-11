# Assignment4

<div align=center style="font-size: 18px;">
    Name<u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u><u>Your Name</u><u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u>SID<u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u><u>1155xxxxxx</u><u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u>
</div>

### Basic information

My phone model is <strong>XiaoMi 12S Pro</strong>(Xiaomi 2206122SC/Android 12L)
I use an Android emulator.

As write in assignment3, my server program in my own server runs in IP:Port

[TOC]

<div style="page-break-after: always;"></div>
## Result

### 1.Setup the NoSQL database, either MongoDB Atlas or Mongoose. Design the collections for the NoSQL database. (20%)

I use the database system: MongDB Atlas <del>/ Mongoose / Others: _____________</del>

Screenshot(s) for design of the collections in the database system:

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202411020235647.png"/>

Screenshot(s) for each of the collection, with some initial documents:

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202411020236910.png"/>

### 2. Design the APIs for the three methods in FastAPI. Output the HTTP request to the console for easier grading. (30%)

Screenshot(s) for the Python code segment for handling the following API:

​	GET http://localhost:PORT_NUM/get_chatrooms

```python
@app.get("/get_chatrooms/")
async def get_chatroom(status_code=200):
    chatrooms = []
    for chatroom in chatroom_collection.find():
        chatrooms.append({
            "id": chatroom["id"],
            "name": chatroom["name"]
        })

    data = {
        "data": chatrooms,
        "status": "OK"
    }
    return JSONResponse(content=jsonable_encoder(data))
```

Screenshot(s) for the Python code segment for handling the following API:

​	GET http://localhost:PORT_NUM/get_messages

```python
@app.get("/get_messages/")
async def get_chatroom(chatroom_id: int = -1, status_code=200):
    has_chatroom_id = False
    for message in chatroom_messages.find():
        if chatroom_id == message["chatroom_id"]:
            has_chatroom_id = True
            data = {
                "data": {
                    "messages": message["messages"]
                },
                "status": "OK"
            }
            return JSONResponse(content=jsonable_encoder(data)) 
        
    if has_chatroom_id == False:
        data = {
            "data" : { "messages": ["There's no such chatroom_id"] },
            "status": "ERROR"
        }
        return JSONResponse(content=jsonable_encoder(data))
    
    data = {
        "data" : { "messages": ["Missing parameter chatroom_id"] },
        "status": "ERROR"
    }
    return JSONResponse(content=jsonable_encoder(data))
```

Screenshot(s) for the Python code segment for handling the following API:

​	POST http://localhost:PORT_NUM/send_message

```python
@app.post("/send_message/")
async def send_message(request: Request):  
    item = await request.json()
    print(request, "\n", item)
    
    list_of_keys = list(item.keys())
    
    if len(list_of_keys) != 4:
        data = {
            "data" : { "messages": ["Input parameters' number error"] },
            "status": "ERROR"
        }
        return JSONResponse(content=jsonable_encoder(data))
    
    if "chatroom_id" not in item.keys():
        data = {
            "data" : { "messages": ["Need chatroom_id"] },
            "status": "ERROR"
        }
        return JSONResponse(content=jsonable_encoder(data))
    
    if "user_id" not in item.keys() or item["user_id"] not in student_list:
        data = {
            "data" : { "messages": ["You're not the sender"] },
            "status": "ERROR"
        }
        return JSONResponse(content=jsonable_encoder(data))
        
    if "name" not in item.keys() or len(item["name"]) > 20:
        data = {
            "data" : { "messages": ["Missing your name or your name is too long"] },
            "status": "ERROR"
        }
        return JSONResponse(content=jsonable_encoder(data))  

    if "message" not in item.keys() or len(item["message"]) > 20:
        data = {
            "data" : { "messages": ["There's no message or the message is too long"] },
            "status": "ERROR"
        }
        return JSONResponse(content=jsonable_encoder(data)) 
    
    has_chatroom_id = False
    result = []
    for message in chatroom_messages.find():
        if item["chatroom_id"] == message["chatroom_id"]:
            has_chatroom_id = True

            result.append({
                "message": item["message"],
                "name": item["name"],
                "message_time": datetime.now().strftime("%Y-%m-%d %H:%M"),
                "user_id": item["user_id"]
            })
            result += message["messages"]

            query = {"chatroom_id": item["chatroom_id"]}
            new_values = {"$set": {"messages": result}}
            chatroom_messages.update_one(query, new_values)
    
    if has_chatroom_id == False:
        data = {
            "data" : { "messages": ["There is no chatroom with the given chatroom_id"] },
            "status": "ERROR"
        }
        return JSONResponse(content=jsonable_encoder(data))
    
    data = {"status": "OK"}
    # data = {
    #     "status": "OK",
    #     "data": {
    #         "message": result,
    #     }
    # }
    return JSONResponse(content=jsonable_encoder(data))
```

### 3. Start the Uvicorn service in the <del>localhost </del>Server side. (10%)

The command for starting the Uvicorn service <del>locally</del>in server:

```shell
uvicorn 1155xxxxxx:app --host IP --port Port
```

Screenshot(s) for console for the Uvicorn:

![](https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202501111845917.png)

### 4. Test your APIs. (40%)

Screenshot(s) for testing the following API:

​	GET http://localhost:PORT_NUM/get_chatrooms

- At least 3 chatrooms are shown in the emulator.
- The HTTP request sent from the emulator to the Uvicorn service is displayed on the console log.

- The chatroom information is consistent with the collection in the database.

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202411020240110.png"/>

![](https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202501111848304.png)

Screenshot(s) for testing the following API:

​	GET http://localhost:PORT_NUM/get_messages

- At least 6 messages sent from at least 3 different users are shown in the emulator.
- The HTTP request sent from the emulator to the Uvicorn service is displayed on the console log.
- The message information is consistent with the collection in the database.

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202411020254407.png"/>

![](https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202501111846672.png)

Screenshot(s) for testing the following API:

​	POST http://localhost:PORT_NUM/send_message

- A new message is sent from the emulator.
- The HTTP request sent from the emulator to the Uvicorn service is displayed on the console log.
- The new message is inserted to the collection in the database.

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202411020257363.png"/>

![](https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202501111849998.png)

For the following, I use POSTMAN for easy request.

Screenshot(s) for testing errors in using the following API:

​	GET http://localhost:PORT_NUM/get_chatrooms

​	GET http://localhost:PORT_NUM/get_messages

- Insufficient or excess parameters are supplied to this method using a web browser.

    For get_chatrooms, nothing need to pass thus I think no action is required to restrict parameters, as they will not have any impact on the server regardless of their content and can correctly read the list.

![](https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202501111849899.png)
If parameters is insufficient, let ERROR

![](https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202501111852962.png)

Screenshot(s) for testing errors in using the following API:

​	POST http://localhost:PORT_NUM/send_message

- Invalid displayed name (length > 20 characters) is supplied to this method via the Kotlin script or emulator.

![](https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202501111853396.png)

Screenshot(s) for testing errors in using the following API:

​	POST http://localhost:PORT_NUM/send_message

- Invalid message (length > 200 characters) is supplied to this method via the Kotlin script or emulator.

![](https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202501111854674.png)

Screenshot(s) for testing errors in using the following API:

​	POST http://localhost:PORT_NUM/send_message

- Non-existing chatroom id is supplied to this method via the Kotlin script or emulator.

![](https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202501111854416.png)

## Reference

<ul>
    <li>https://www.postman.com/</li>
</ul>

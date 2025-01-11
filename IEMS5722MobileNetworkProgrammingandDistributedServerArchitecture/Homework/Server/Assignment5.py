# to run the script with uvicorn with fastapi at port 57220: uvicorn Assignment5:app --host 0.0.0.0 --port 57220

from fastapi import FastAPI
from fastapi.responses import JSONResponse
from fastapi.encoders import jsonable_encoder
from pydantic import BaseModel
from fastapi import Request
from datetime import date,datetime
import json

from pyfcm import FCMNotification
from pymongo.mongo_client import MongoClient
from pymongo.server_api import ServerApi

# mogoDB connection
url = ""
client = MongoClient(url, server_api = ServerApi("1"))
db = client["Homework"]
chatroom_collection = db["chatrooms"]
chatroom_messages = db["messages"]
user_tokens = db["FCMTokens"]

# Fastapi
# define a Fast API app
app = FastAPI()

# define a route, binding a function to a URL (e.g. GET method) of the server
@app.get("/demo/")
async def get_demo(a: int = 0, b: int = 0, status_code=200):
    sum = a+b
    data = {"sum": sum, "date": date.today()}
    return JSONResponse(content=jsonable_encoder(data))

@app.get("/")
async def root():
  return {"message": "Hello World"}  # the API returns a JSON response

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

@app.get("/get_messages/")
async def get_messages(chatroom_id: int = -1, status_code=200):
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


@app.get("/get_user_info/")
async def get_messages(uid: int = -1, status_code=200):
    for message in user_tokens.find():
        if uid == message["uid"]:
            data = {
                "nickname": message["nickname"],
                "avatar": message["avatar"],
                "status": "OK"
            }
            return JSONResponse(content=jsonable_encoder(data)) 
        

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
    
    if "user_id" not in item.keys():
        data = {
            "data" : { "messages": ["You're not the sender"] },
            "status": "ERROR"
        }
        return JSONResponse(content=jsonable_encoder(data))
        
    if "name" not in item.keys():
        data = {
            "data" : { "messages": ["Missing your name"] },
            "status": "ERROR"
        }
        return JSONResponse(content=jsonable_encoder(data))  

    if "message" not in item.keys():
        data = {
            "data" : { "messages": ["There's no message"] },
            "status": "ERROR"
        }
        return JSONResponse(content=jsonable_encoder(data)) 
    
    has_chatroom_id = False
    result = []
    user_in_group = set()
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

            for temp in result:
                user_id = temp.get("user_id")
                user_in_group.add(user_id)

    if has_chatroom_id == False:
        data = {
            "data" : { "messages": ["There is no chatroom with the given chatroom_id"] },
            "status": "ERROR"
        }
        return JSONResponse(content=jsonable_encoder(data))
    
    fcm = FCMNotification(service_account_file="iems5722-mx-firebase-adminsdk-xxxxxxxxxxxxx.json", project_id="iems5722-mx")
    
    for user_notify_id in user_in_group:
        for token in user_tokens.find():
            if user_notify_id == token["uid"]:
                fcm_token = token["token"]

                notification_title = ""
                for find_chatroom_name in chatroom_collection.find():
                    if find_chatroom_name["id"] == item["chatroom_id"]:
                        notification_title = item["name"] + " has sent a message in " + find_chatroom_name["name"]
                notification_body = item["name"] + ": " + item["message"]
        
        if len(fcm_token) > 0:
            result = fcm.notify(fcm_token=fcm_token, notification_title=notification_title, notification_body=notification_body)

                # result = fcm.notify(fcm_token=fcm_token, notification_title=notification_title, notification_body=notification_body)

    data = {"status": "OK"}
    return JSONResponse(content=jsonable_encoder(data))

@app.post("/submit_push_token/")
async def submit_push_token(request: Request):
    item = await request.json()
    print(request, "\n", item)
    
    list_of_keys = list(item.keys())
    
    if len(list_of_keys) != 2:
        data = {
            "data" : { "messages": ["Input parameters' number error"] },
            "status": "ERROR"
        }
        return JSONResponse(content=jsonable_encoder(data))
    
    if "uid" not in item.keys():
        data = {
            "data" : { "messages": ["Need uid"] },
            "status": "ERROR"
        }
        return JSONResponse(content=jsonable_encoder(data))
    
    if "token" not in item.keys():
        data = {
            "data" : { "messages": ["No token are in request"] },
            "status": "ERROR"
        }
        return JSONResponse(content=jsonable_encoder(data))
    
    has_token = False
    result = []

    for message in user_tokens.find():
        if item["uid"] == message["uid"]:
            query = {"uid": item["uid"]}
            new_values = {"$set": {"token": item["token"]}}
            user_tokens.update_one(query, new_values)
    
    data = {
        "message": "Token Updated",
        "status": "OK"
    }
    return JSONResponse(content=jsonable_encoder(data))   
    
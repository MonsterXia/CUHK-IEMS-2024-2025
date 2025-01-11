# to run the script with FastAPI: fastapi dev main.py
# to run the script with uvicorn with fastapi at port 55722: uvicorn 1155xxxxxx:app --host 0.0.0.0 --port 55722

# import the Fast API package
from fastapi import FastAPI
from datetime import datetime
from fastapi.responses import JSONResponse
from fastapi.encoders import jsonable_encoder
from pydantic import BaseModel
from fastapi import Request
from pymongo.mongo_client import MongoClient
from pymongo.server_api import ServerApi
import json

# mogoDB connection
url = ""
client = MongoClient(url, server_api = ServerApi("1"))
db = client["Homework"]
chatroom_collection = db["chatrooms"]
chatroom_messages = db["messages"]

# for testing, you can update this one to your student ID
student_list = [1155000000] 

# define a Fast API app
app = FastAPI()

# define a route, binding a function to a URL (e.g. GET method) of the server
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
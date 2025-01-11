#### UReply 2024-2025

[TOC]

##### Sep 19 Android Programming

<ul>
    <strong>Suggest two main reasons why Kotlin is so popular in mobile application development.</strong>
    <li>Support from various framework in Kotlin.</li>
    <li>Kotlin includes not just the frontend, but also a full stack dev, the backend server.</li>
    <li>Simplified code style.</li>
</ul>

<ul>
    <strong>What is the major relation between Kotlin and Java?</strong>
    <li>Kotin is developed on top of Java. Kotlin code can be decoupled to Java code directly. Both of them are run on Java Virtual Machine (JVM).</li>
</ul>

<ul>
    <strong>Suggest three viewgroups in Java/Kotlin.</strong>
    <li>Linear Layout.</li>
    <li>Relative Layout.</li>
    <li>Constraint Layout.</li>
    <li>Frame Layout.</li>
</ul>

<ul>
    <strong>What is an ativity in mobile application development?</strong>
    <li>Each page in Android is an Activity.</li>
</ul>

<ul>
    <strong>Suggest two applications of companion objects in Kotlin.</strong>
    <li>Property needed at class level and not a specific instance (static).</li>
    <li>Factory pattern: perform extra work before an object can be used.</li>
</ul>

##### Sep 26 Android Programming II

<ul>
    <strong>How many types of intent? What are they?</strong>
    <li>Two types. Which are implicit intent and explicit intent.</li>
</ul>

<ul>
    <strong>What is an intent filter?</strong>
    <li>An inten fiter is an epression in an apps manifest file that specifies the type of intents that the component would like to receive. For instance, by declaring an intent filter for an activity, you make it possible for other apps to directly start your activity with a certain kind ol intent.
</li>
</ul>

<ul>
    <strong>Suggest 3 reasons why Jetpack Compose is getting more popular than ViewGroup in Android UI design.</strong>
    <li>Declarative UI is cleaner, readable, and performant than imperative UI.</li>
    <li>Compose allows you to do more with less code compared to XML.</li>
    <li>Compose is intuitive. This means that you just need to tell Compose what you want to show the user.</li>
    <li>Compose is compatible with all your existing code: you can call Compose code from Views and Views from Compose. Also integrated with many Jetpack Libraries.</li>
    <li>Compose improves your build time and APK size.</li>
</ul>

<ul>
    <strong>In Jetpack Compose, do we need to create an event listener for the UI components (e.g. onClick)? Explain your answer.</strong>
    <li>Yes: we can handle UI events using an event listener.</li>
    <li>No: It is more common to create a sublass of ViewModel(), or to use a state variable to handle UI events.</li>
</ul>

<ul>
    <strong>Suggest two different ways in debugging an Android Studio project.</strong>
    <li>Use debug mode.</li>
    <li>Use Log methods in the logcat tool.</li>
</ul>

##### Oct 3 Data Communications, Client-Server Architecture and HTTP Basics

<ul>
    <strong>State 2 main differences between TCP and UDP.</strong>
    <li>TCP is a connection-based protocol and UDP is connectionless.</li>
    <li>While TCP is more reliable, it transfers data more slowly. UDP is less reliable but works more quickly.</li>
    <li>TCP can send sequence data, while UDP cannot.</li>
    <li>TCP can retransmit data, while UDP cannot.</li>
    <li>UDP supports broadcasting, while TCP cannot.</li>
</ul>

<ul>
    <strong>What is a port in the computer network?</strong>
    <li>Ports are “endpoints of communications” in a computer’s OS.</li>
    <li>Ports allow different applications running on the same computer to share a single physical link to the network.</li>
    <li>Each application must bind to a unique port (identified by a number) in order to communicate with the network.</li>
    <li>Port number is a 16-bit unsigned integer (i.e. 0 to 65535).</li>
    <li>Port numbers are regulated and are divided into 3 different ranges (Regulated by the Internet Assigned Numbers Authority (IANA)).</li>
</ul>

<ol>
    <strong>What are the steps in writing a server program for a UDP echo server?
</strong>
    <li>Create a socket that binds to a port.</li>
    <li>Create an empty packet for receiving data.</li>
    <li>Receive the packet from the socket.</li>
</ol>

<ul>
    <strong>Can an android app use multi-process? Can an android app use multi-threading? Explain your answer.</strong>
    <li>No.</li>
    <li>Yes.</li>
    <li>An android app is an single process application. Multi-process is not allowed in android application.</li>
    <li>We use multi-threading to handle some heavy computations, I/O extensive tasks.</li>
</ul>

<ul>
    <strong>What are the major differences between coroutine and thread? Suggest two advantages of coroutine over thread.</strong>
    <li>Coroutines do not map directly to native threads. Instead, they are managed by the Kotlin runtime and can be suspended and resumed without blocking the underlying thread.</li>
    <li>Structured Concurrency: Coroutines provide a structured way to manage concurrency, making it easier to handle complex asynchronous operations. In short, coroutines does not require the callbacks structure.
.</li>
    <li>An android app is an single process application. Multi-process is not allowed in android application.</li>
    <li>Lightweight: Coroutines are lightweight compared to threads, as they don’t require creating and managing additional system resources. This makes coroutines more  efficient in terms of memory usage..</li>
    <li>Suspend and Resume: Coroutines allow for suspending and resuming execution at specific points, making it easier to handle long-running tasks without blocking the main thread. This helps in keeping the UI responsive and improves the overall user experience.</li>
    <li>Jetpack Integration: Coroutines provide built-in exception handling mechanisms, making it easier to handle and propagate exceptions within the coroutine context.</li>
    <li>Unlike threads for coroutines, the application by default does not wait for it to finish the execution.</li>
</ul>
##### Oct 10 Using HTTP in Android, and Web and Application Servers

<ul>
    <strong>In Kotlin, if we send a post request to a HTTP(port80) server using HttpURLConnection, what settings do we need to add to the default Jetpack Compose project?
</strong>
    <li>Add uses permission to AndroidManifest.xml file.<br>
  	android.permission.INTERNET<br>
  	android.permission.ACCESS_NETWORK_STATE
    </li>
    <li>Create a network_security_config.xml under res\xml to add the site to the trusted site.</li>
    <li>Add a path of the configuration file to the AndroidManifest.xml.</li>
</ul>

<ul>
    <strong>Suggest two major benefits that ASGI is better than WSGI.</strong>
    <li>ASGl efficiently handles concurrency and is recommended for long-lived connections or many clients.</li>
    <li>ASGl supports both HTTP and WebSocket, ideal for real-time bidirectional communication,while WSGl only supports HTTP only.</li>
    <li>ASGl supports both HTTP and WebSocket,ideal for real-time bidirectional communication,but WSGl only supports HTTP/1.1.</li>
  	<li>ASGI middleware is asynchronous, ensuring compatibility with asynchronous applications.</li>
</ul>

<ul>
    <strong>What is a server worker in Uvicorn?</strong>
    <li>In Uvicorn,a server worker is created by spawn,i.e.a thread.</li>
    <li>The main server worker monitors the progress of other server workers.</li>
    <li>The rest of the server workers runcopies of the application.</li>
</ul>

<ul>
    <strong>Name four different load balancer strategies.</strong>
    <li>Round Robin</li>
  	<li>Least Connections</li>
  	<li>Weighted Round Robin or Weighted Least Connections</li>
  	<li>IP Hashing</li>
  	<li>Random Allocation</li>
  	<li>Geo-Location Based Load Balancing</li>
  	<li>Resource-Based Load Balancing</li>
  	<li>Application Layer Content Switching</li>
  	<li>Rate Limiting</li>
</ul>

##### Oct 17 Databases and Caches

<ul>
    <strong>What is SQL? What is RDBMS?</strong>
    <li>SQL stands for Structured Query Language.SQL is a standard language for accessing and manipulating databases.</li>
    <li>The software used to store,manage, query,and retrieve data stored in a relational database is called a relational database management system (RDBMS).</li>
</ul>

<ul>
    <strong>What is an ER diagram?What is an entity in SQL? What is an attribute in SQL?</strong>
    <li>An Entity Relationship (ER)Diagram is a type of flowchart that illustrates how 'entities"such as people,objects or concepts relate to each other within a system.</li>
    <li>An entity is an object about which data is to be captured.E.g.person,item.</li>
  	<li>The attributes of an entity further define the information being stored. E.g.name of a person,age of a person.</li>
</ul>

<ul>
    <strong>What is noSQL? What is a document in noSQL? What is a collection in noSQL?</strong>
    <li>NoSQL databases are non-tabular databases and store data differently than relational tables.</li>
    <li>Documents store data in field-value pairs.</li>
  	<li>Collection stores a list of documents(i.e.data,JSON).</li>
</ul>

<ul>
    <strong>What is a cluster in MongoDB? What is the actual data format stored inside MongoDB?</strong>
    <li>A MongoDB cluster allows a MongoDB database to either horizontally scale across many servers with sharding,or to replicate data ensuring high availability with MongoDB replica sets, therefore enhancing the overall performance and reliability of the MongoDB cluster.</li>
    <li>DBSON,binary format of JSON.</li>
</ul>

<ul>
    <strong>What is a cluster in MongoDB? What is the actual data format stored inside MongoDB?</strong>
    <li>A MongoDB cluster allows a MongoDB database to either horizontally scale across many servers with sharding,or to replicate data ensuring high availability with MongoDB replica sets, therefore enhancing the overall performance and reliability of the MongoDB cluster.</li>
    <li>DBSON,binary format of JSON.</li>
</ul>

<ul>
    <strong>What are CRUD operations? Do we need to create the collection in MongoDB?Explain your choice.</strong>
    <li>Create,Retrieve/Read,Update,Delete.</li>
    <li> No,the collection will be created automatically upon insertion.</li>
</ul>

<ul>
    <strong>What is cache?</strong>
  	<li>Cache is a temporary data storage that stores data for quick retrieval in the future.</li>
     <li>Mostly implemented as a key-value store,where the unique key can be used to retrieve the value at O(1)time.</li>
  	<li>Cache is usually small (RAM is expensive!).</li>
  	<li>Hit (found)vs.Miss (not found).</li>
  	<li>Cache can be persistent,if it also stores the current state into some persistent storage (e.g.the hard disk).</li>
</ul>

##### Oct 24 Instant Messaging and Google Cloud Messaging

[Week 7: Instant Messaging and Google Cloud Messaging](https://blackboard.cuhk.edu.hk/webapps/blackboard/content/listContent.jsp?course_id=_205751_1&content_id=_5028850_1&mode=reset#)

<ul>
    <strong>Suggest two ways to send out FCM messages to Android apps.</strong>
    <li>Use the Firebase Console.</li>
		<li>Generate a send request and submit it to the FCM connection server.</li>
</ul>

<ul>
    <strong>What is a registration token?Will it be expire?</strong>
    <li>On initial startup of your app,the FCM SDK generates a registration token for the client app instance.This token is needed to be able to send FCM messages and notifications to the users device. </li>
		<li>It expires after 270 days of inactivity.</li>
</ul>

<ul>
    <strong>What is the Firebase service account file?Given an example for the usage of this file.</strong>
    <li>Firebase uses service accounts to operate and manage services without sharing user credentials.When you create a Firebase project,you might notice that a number of service accounts are already available in your project.</li>
		<li>When we send a notification from the FCMNotification module using Python, we need to provide the Firebase service account file to establish the connection to the FCM push server.</li>
</ul>

<ul>
    <strong>When the application server sends a message to the user,suggest three different ways in solving this task.</strong>
    <li>Polling(periodic pull)</li>
		<li>Comet Model</li>
		<li>BOSH</li>
		<li>WebSockets</li>
    <li>XMPP</li>
</ul>

<ul>
    <strong>Explain how push notification works on mobile.</strong>
    <li>A persistent TCP connection is established between mobile device and push server.</li>
		<li>Applications request the push server to push messages to clients when necessary.</li>
</ul>

<ul>
    <strong>In order to use the FCM services in an emulator,what do you need to do in mobile settings?What do you need to include in AndroidManifest.xml?</strong>
    <li>In the mobile settings of the emulator, sign in to Google. </li>
		<li>In the AndroidManifest.xml,include the uses-permission for POST NOTIFICATIONS.</li>
</ul>

##### Oct 31 Peer-to-Peer Networking in Android

<ul>
    <strong>What is the full name of Wi- Fi? What is the most updated android version? What is the most updated android API level?</strong>
    <li>Wireless Fidelity. </li>
		<li>Android 15.0.</li>
		<li>API level 35.</li>
</ul>
<ul>
  <strong>In file transfer,which one is faster,Wi-Fi Direct,Wi-Fi via internet or Bluetooth? Does the iPhone support Wi-Fi Direct?</strong>
		<li>Wi-Fi Direct.</li>
		<li>iPhone does not support Wi-Fi Direct.</li>
</ul>

<ul>
    <strong>How can Wi-Fi Direct save power?</strong>
		<li>Opportunistic power-saving protocol When all clients are sleeping,the AP will go to sleep Notice-of-absence protocol.</li>
		<li>The AP will announce certain period of time when it will go to sleep to save power,clients are not allowed to access the channel during these times.</li>
</ul>

<ul>
    <strong>What is the range of bluetooth? What is the typical distance for the bluetooth connection?</strong>
		<li>Range from 0.5 m to 100 m.</li>
		<li>Typically less than 10 m.</li>
</ul>

<ul>
    <strong>Suggest three common applications for bluetooth on Android.</strong>
		<li>Wireless mouse and keyboard.</li>
		<li>Wireless earphones,headphones and microphones.</li>
		<li>File transmission between mobile devices Wearable devices (e.g.smart watches).</li>
		<li>Sensing (e.g.iBeacon,Eddystone)</li>
</ul>

<ul>
    <strong>What is the typical distance for NPC? Suggest three common android applications for NFC.</strong>
		<li>Typically less than 4 cm Contactless payment (e.g.,credit cards, electronic tickets).</li>
		<li>Bootstrapping other connection technologies such Bluetooth and Wi-Fi. Sharing contacts,photos,videos.</li>
		<li>Acting as identity documents and keycards.</li>
		<li>Smartphone automation and NFC tags.</li>
		<li>Gaming.</li>
</ul>

<ul>
    <strong>What is NDEF? What is the NDEF message?</strong>
		<li>NDEF stands for NFC Forum Data Exchange Format.</li>
		<li>NDEF is a lightweight binary message format designed to encapsulate one or more application-defined payloads into a single message construct. </li>
		<li>A NDEF message contains one or more NDEF records,each carrying a payload of arbitrary type and up to 2^32-1 bytes in size.</li>
		<li>Records can be chained together to support larger payloads.</li>
		<li>An NDEF record carries three parameters for describing its payload: the payload length,the payload type, and an optional payload identifier.</li>
</ul>
##### Nov 7 Microsoft Azure, GitHub, Async Tasks and Message Queues

<ul>
    <strong>What is a repository in GitHub?What are the basic operations for Git?</strong>
  	<li>Repository is a place where you can store your code,your files,and each files revision history.</li>
		<li>The basic operations include initializing,add,commit,pull and push.</li>
</ul>

<ul>
    <strong>What are asynchronous tasks?Give two examples using asynchronous tasks in Android applications.</strong>
		<li>Asynchronous tasks run in the background and evaluate functions asynchronously when there is an event.</li>
		<li>Asynchronous tasks may run only until some work is completed, or they may be designed to run indefinitely.</li>
		<li>This tutorial describes how to interact with asynchronous tasks.</li>
		<li> Example:database connection,wait for user input.</li>
</ul>

<ul>
    <strong>What is a message broker?</strong>
  	<li>A message broker is software that enables applications,systems and services to communicate with each other and exchange information.</li>
</ul>

<ul>
    <strong>In RabbitMQ,what are exchanges?What are the types of exchanges?</strong>
  	<li>Producer usually sends a message to an exchange,which will dispatch the message to zero,one or more queues according to some logic defined there. Further decouple the producer(s)and the consumer(s). </li>
		<li>The four types of exchanges are direct, fanout,topic and headers.</li>
</ul>

<ul>
    <strong>We already establish the message broker in the server,why do we include Celery (a distributed task queue)in the server?</strong>
  	<li>Allows you to send messages/invoke asynchronous tasks by simply making a function call.</li>
		<li>A Helps you manage your workers in case of failure or exception encountered.</li>
</ul>

<ul>
    <strong>What is a message queue? What is the role of the message queue in the server?</strong>
  	<li>A message queue is a form of asynchronous service-to-service communication used in serverless and microservices architectures.Messages are stored on the queue until they are processed and deleted.Each message is processed only once,by a single consumer.</li>
		<li>Message queues allow different parts of a system to communicate and process operations asynchronously.A message queue provides a lightweight buffer which temporarily stores messages,and endpoints that allow software components to connect to the queue in order to send and receive messages.</li>
</ul>

##### Nov 14 Docker and WebSocket

<ul>
    <strong>What is Docker? Suggest two main reasohs why docker is used in app or project development.</strong>
    <li>Docker is a software platform that allows you to build,test,and deploy applications quickly.Docker packages software into standardized units called containers that have everything the software needs to run including libraries,system tools,code,and runtime.</li>
		<li>Consistent environments</li>
		<li>Efficiency in using resources</li>
		<li>Scalability,flexibility,and portability</li>
    <li>Isolation</li>
</ul>
<ul>
    <strong>Why do we stick to the Linux environment in most of the server applications?</strong>
		<li>Open-source nature and customization flexibility</li>
		<li>Range of applications and tools</li>
		<li>Enhanced security</li>
    <li>High stability and reliability</li>
  	<li>Community support and resources</li>
		<li>Cost-effectiveness compared to proprietary software</li>
		<li>Scalability for handling large amounts of data and traffic</li>
    <li>Compatibility with modern DevOps practices and configuration management</li>
  	<li>Support for visualization</li>
</ul>

<ul>
    <strong>What are the design principles of the WebSocket?</strong>
		<li>An additional layer on top of TCP.</li>
		<li>Enable bi-directional communication between client and servers.</li>
		<li>Support low-latency apps without HTTP overhead.</li>
    <li>Web origin-based security model for browsers.</li>
  	<li>Support multiple server-side endpoints.</li>
</ul> 

<ul>
    <strong>What are the types of events in SocketlO?Explain briefly?</strong>
		<li>Special events refer to the connection (i.e connect,disconnect, join and leave).</li>
		<li>Unnamed events refer to messages, i.e.message and json.</li>
		<li>Custom events are user-define events,e.g.my_event.</li>
</ul> 


<ul>
    <strong>Suggest a good practice in handling user passwords.</strong>
    <li>Instead of the actual user passwords, the database should store the hash codes of the user passwords. The hash codes of the user passwords should be transferred under HTTPS.</li>
</ul> 

<ul>
    <strong>What is OAuth 2.0? How do the OAuth 2.0 work?</strong>
    <li>OAuth 2.0 is Open Authorization Version 2.0.OAuth 2.0 is a standard designed to allow a website or application to access resources hosted by other web apps on behalf of a user.</li>
		<li>The client requests authorization from the authorization server.</li>
		<li>The authorization server authenticates the client and verifies the requested scopes.</li>
		<li>The resource owner interacts with the authorization server to grant access.</li>
  	<li>The authorization server returns an authorization code or access token back to the client.</li>
  	<li>The client uses the token to access the resource.</li>
</ul>

##### Nov 21 Advanced Android Programming

<ul>
    <strong>What are the two main approaches in using Google Services? How can you access GoogleApi?</strong>
    <li>Google Services without APl authorization.</li>
		<li>Google Services with APl authorization.</li>
		<li>Create an instance of a subclass of GoogleApi.</li>
</ul>

<ul>
    <strong>What are the steps in setting up the app to use the location APIs?</strong>
    <li>Create location service client.</li>
		<li>Get the last known location.</li>
		<li>Set up a location request.</li>
		<li>Make a location request.</li>
  	<li>Receive location update via callback method.</li>
</ul>

<ul>
    <strong>What are the steps in adding the Google map APl key to the app?</strong>
    <li>Add the right dependencies and plugins under the build.grade.</li>
		<li>Set targetSdk and compileSdk to 34.</li>
		<li>Sync the project with gradle.</li>
		<li>Save the APl key in the secrets.properties.</li>
  	<li>Connect the APl key to the local default property.</li>
  	<li>Include the metadata of the APl key in the AndroidManifest.</li>
  	<li>Set the secret property to the build.grade.</li>
</ul>

<ul>
    <strong>What is geocoding? Why do we use geocqding in apps?</strong>
    <li>Geocoding is the process of converting an address(like 1600 Pennsylvania Avenue NW)to-and-from geographic coordinates(e.g., 38.8811111,-77.036871).</li>
		<li>Addresses are pieces of text and can change over time,but coordinates will always be the same.</li>
</ul>

<ul>
    <strong>Suggest 5 different cloud service platforms.</strong>
    <li>Amazon</li>
  	<li>Microsoft Azure</li>
  	<li>Google Cloud</li>
  	<li>Facebook</li>
  	<li>Instagram</li>
  	<li>WeChat</li>
  	<li>Baidu</li>
  	<li>Youtube</li>
  	<li>IBM Cloud</li>
</ul>

<ul>
    <strong>What are the steps in creating an auto-complete text view?</strong>
    <li>Input the text in the text view.</li>
  	<li>Submit the text to the suggestion server.</li>
  	<li>Extract the suggestions from the server response.</li>
  	<li>Update the data and notify the adaptor.</li>
</ul>

<ul>
    <strong>What is the build gradle(app module)? What is the build gradle(project)?</strong>
    <li>If there were another module,then that module would have its own build.gradle file,too.As an example,I made a library project with three modules:a library module,a demo app module,and another app module that I plan to use for testing.Each of them have their own build.gradle files that I can tweak.</li>
  	<li>The build.gradle(Project: MyApplication)file is in the root folder of the project and its configuration settings apply to every module in the project.A module is an isolated piece of the bigger project.In a multi- module project,these modules have their own jobs but work together to form the whole project.Most Android projects only have one module,the app module.</li>
</ul>

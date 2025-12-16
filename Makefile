server: SmallChatServer.java SocketServer.java ByteBufferUtils.java
	javac SmallChatServer.java SocketServer.java ByteBufferUtils.java
	java SmallChatServer

client: SmallChatClient.java SocketClient.java ByteBufferUtils.java
	javac SmallChatClient.java SocketClient.java ByteBufferUtils.java
	java SmallChatClient

clean:
	rm -f *.class

var express = require("express");
var app = express();
var server = require("http").createServer(app);
var io = require("socket.io").listen(server);
var fs = require("fs");
server.listen(process.env.PORT || 3000);

var arrUserName = [];
var arrChat = [];


app.get('/getArrUserName', function (request, response) {
  response.status(200).send(arrUserName);
});

io.sockets.on('connection', function (socket) {

  console.log("STATUS: Have connect" + socket.id);

  socket.on('cilent-send-username', function (username) {
    var result = false;
    if (arrUserName.indexOf(username) > -1) {
      console.log("Exist username: " + username);
      result = "Exist username: " + username;
    } else {
      console.log("Add username: [" + arrUserName.length + "," + username + "]");
      arrUserName.push(username);
      socket.un = username;
      result = true;
      io.sockets.emit('sever-send-arrUserName', { send: arrUserName });
    }

    socket.emit('sever-register-result', { result: result });

    //   	// emit toi tat ca moi nguoi
    // io.sockets.emit('serverguitinnhan', { noidung: data });

    // // emit tới máy nguoi vừa gửi
    // socket.emit('serverguitinnhan', { noidung: data });

  });
  socket.on('cilent-send-messenger', function (messenger) {
    console.log("Add chat: [" + socket.un + ", " + messenger + "]");
    io.sockets.emit('sever-send-arrChat', { messenger: messenger, username: socket.un });
  });
});
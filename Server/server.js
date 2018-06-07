var express = require("express")
var shortid = require('shortid')
var database = require('./sql-connection')
var bodyParser = require('body-parser')
var hasher = require('./hasher')
var app = express()

var jsonParser = bodyParser.json()

app.use(function (req, res, next) {
    res.header('Access-Control-Allow-Origin', 'example.com');
    res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE');
    res.header('Access-Control-Allow-Headers', 'Content-Type');
    next();
})


app.post('/hangman/register', jsonParser, function (req, res) {
    var databaseModel = hasher.createHash(req.body.password)
    databaseModel.Username = req.body.username
    console.log(databaseModel)
    database.registerUser(databaseModel, function (result) {
        res.send(result)
    })
})

app.post('/hangman/authorize', jsonParser, function (req, res) {
    
    database.findUser(req.body, function (error, result) {
        if(error){
            console.log(error)
            res.send(false)
            return
        }
        var userModel = result[0]
        if(hasher.checkPassword(req.body.password, userModel.PasswordSalt, userModel.PasswordHash)){
            res.send(true)
        }
        else{
            res.send(false)
        }
    })
})

app.get('/hangman/getWord', function (req, res) {
 
    database.pickWord(function (error, words) {
        if (error) {
            res.send(false)
            return
        }
        res.send(words[0].word)
    })
})

app.get('/hangman/users', function (req, res) {
    database.getUsers(function (result) {
        res.send(result)
    })
})


app.listen(8080, function () {
    console.log('Server started on port 8080')
})
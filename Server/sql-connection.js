var edge = require("edge")

const connectionString = "Server=DESKTOP-H521TNQ; Database=Hangman; Integrated Security=True"

const registerUserQuery = "insert into Users values (@Username, @PasswordSalt, @PasswordHash)"
const getUsersQuery = "select username from Users"
const findUserQuery = "select * from Users where username = @username"
const pickWordQuery = "select TOP 1 word from words order by NEWID()"
const createGameQuery = "insert into Games values (@gameid, @word, '', @giver, 0)"

var getUsers = edge.func('sql', {
    connectionString: connectionString,
    source: getUsersQuery
});

var createGame = edge.func('sql', {
    connectionString: connectionString,
    source: getUsersQuery
});

var registerUser = edge.func('sql', {
    connectionString: connectionString,
    source: registerUserQuery
});

var findUser = edge.func('sql', {
    connectionString: connectionString,
    source: findUserQuery
});

var pickWord = edge.func('sql', {
    connectionString: connectionString,
    source: pickWordQuery
});

function createGameWrapper(data, callback){
    createGame(data, function(error, result){
        if (error) {
            console.log(error)
            return callback(false)
        }
        return callback(true)
    })
}

function findUserWrapper(data, callback) {
    findUser(data, function (error, result) {
        if (error) {
            console.log(error)
            return callback(error)
        }
        if(result.length == 1){
            return callback(null, result)
        }
        else{
            return callback(true)
        }
    });
}

function pickWordWrapper(callback) {
    pickWord(null, function (error, result) {
        if (error) {
            return callback(error)
        }
        return callback(null, result)
    });
}

function registerUserWrapper(data, callback) {
    registerUser(data, function (error, result) {
        if (error) {
            console.log(error)
            return callback(false)
        }
        return callback(true)
    });
}

function getUsersWrapper(callback) {
    getUsers(null, function (error, result) {
        if (error) {
            console.log(error)
        }
        return callback(result)
    });
}

exports.getUsers = getUsersWrapper
exports.registerUser = registerUserWrapper
exports.findUser = findUserWrapper
exports.pickWord = pickWordWrapper
exports.createGame = createGameWrapper
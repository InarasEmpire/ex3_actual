var gameIsStarted = false;
var refreshRate = 1000;
var GAME_URL = buildUrlWithContextPath("singlegameroom");
var USER_MOVE_URL = buildUrlWithContextPath("usermove");

function createTable(gameJson) {
    var tableElem, rowElem, colElem;
    var boardArray = gameJson.board;

    $("#board").empty();
    tableElem = document.createElement('table');

    rowElem = document.createElement('tr');
    var btn;
    for (var j = 0; j < gameJson.cols; j++) {
        colElem = document.createElement('td');
        btn = document.createElement('button');
        btn.innerHTML = 'v';
        btn.addEventListener("click", function () {  selectedPushInCol($(this)); }, false);
        colElem.appendChild(btn);
        rowElem.appendChild(colElem);
    }
    tableElem.appendChild(rowElem);


    // for (var i in boardArray) {
    var k = 0;
    for(var x=0; x<gameJson.rows; x++){
        rowElem = document.createElement('tr');
        for (var y = 0; y < gameJson.cols; y++) {
            colElem = document.createElement('td');
            colElem.setAttribute("row", boardArray[k].rowId);
            colElem.setAttribute("col", boardArray[k].colId);
           // colElem.setAttribute("class", "cell");
            colElem.setAttribute("bgcolor", boardArray[k].color);
            k++;
            rowElem.appendChild(colElem);
        }
        //i=i+j-1;
        tableElem.appendChild(rowElem);
    }

    if(gameJson.variant == "popout")
    {
        rowElem = document.createElement('tr');
        for (var j = 0; j < gameJson.cols; j++) {
            colElem = document.createElement('button');
            btn.innerHTML = 'v';
            colElem.addEventListener("click", function() {  selectedPopOutCol($(this)); }, false);
            rowElem.appendChild(colElem);
        }
        tableElem.appendChild(rowElem);
    }

    $("#board").append(tableElem);
}

function getQueryVariable(variable)
{
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i=0;i<vars.length;i++) {
        var pair = vars[i].split("=");
        if(pair[0] == variable){return pair[1];}
    }
    return(false);
}

function ajaxBoardContent() {
    var gameindex = getQueryVariable("index");

    console.log(GAME_URL);
    $.ajax({
        url: GAME_URL,
        data: {"gameindex" : gameindex},
        dataType: 'json',
        success: function(gameJson) {
            console.log(GAME_URL);
            createTable(gameJson);
            refreshUsersList(gameJson.players);
            if(!gameIsStarted) {
                checkIfGameStarted(gameJson);
            }
            updateStatus(gameJson);
        },
        error: function(jqxhr, status, exception) {
            alert('Exception:', exception);
        }
            /*
        error: function(error) {
            console.log(GAME_URL);
            console.error(error.responseText);
        }*/
    });
}

function selectedPushInCol(elem){
    if(gameIsStarted) {
        var gameindex = getQueryVariable("index");
        var col = elem.closest("td").index();
        //.matches
        var dataString = {"gameindex": gameindex, "col": col, "type": "pushin"};

        $.ajax({
            url: USER_MOVE_URL,
            type: 'POST',
            timeout: 2000,
            data: dataString,
            dataType: "json",
           /* error: function (error) {
                console.error("Failed to submit player's move");
                errorMessage(error.responseText);
            },*/
            success: function () {
                console.log("success submit player's move");
                clearErrorMessage();
            }
        });
    }
}

function selectedPopOutCol(elem)
{
    if(gameIsStarted) {
        var gameindex = getQueryVariable("index");
        var col = elem.closest("td").index();
        //.matches
        var dataString = {"gameindex": gameindex, "col": col, "type": "pushin"};

        if (gameIsStarted) {
            var gameindex = getQueryVariable("index");
            var col = elem.parent().children().index(elem);
            $.ajax({
                data: dataString,
                url: USER_MOVE_URL,
                type: 'POST',
                dataType: 'json',
                timeout: 2000,
                error: function (error) {
                    console.error("Failed to submit player's move");
                    errorMessage(error.responseText);
                },
                success: function () {
                    console.log("success submit player's move");
                    clearErrorMessage();
                }
            });
        }
    }
}

function checkIfGameStarted(gameJson)
{
    if(!gameIsStarted &&  gameJson.status == "active")
    {
        alert("Game is about to start!");
        gameIsStarted = true;
    }
}

function updateStatus(data){
    var gameJson = jQuery.parseJSON(JSON.stringify(data));

    var message = "";
    $("#status").empty();
    if(gameJson.ended == "true"){
        message = "game is ended."
    }
    if(gameJson.status == "active") {
        message = "next turn: " + gameJson.nextTurn;
    }
    else{
        var m1 = "waiting to start, current players amount: ";
        message = m1.concat(gameJson.currentPlayers);
        message = message.concat( "/");
        message = message.concat(gameJson.totalPlayers);
    }

    var node = document.createElement("h3");
    var textnode = document.createTextNode(message);
    node.appendChild(textnode);
    document.getElementById("status").appendChild(node);
}
//----------------
//   players list
//----------------

function refreshUsersList(usersJson) {
    $("#onlineusers").empty();

    var userList = document.getElementsByClassName('onlineusers');

    for(var user in usersJson)
    {
        var currUserLi = document.createElement('li');
        var currUserDiv = document.createElement('div');
        //var nameElem = document.createTextNode(user.name);
        //nameElem. = "white";
        currUserDiv.appendChild(nameElem);
        currUserDiv.appendChild(document.createTextNode(user.name));
        currUserDiv.appendChild(document.createTextNode(user.type));
        currUserDiv.appendChild(document.createTextNode(user.turnAmount));
        currUserDiv.setAttribute("bgcolor", user.color);
        //nameElem.style.backgroundColor = user.color;

        userList.append(currUserLi);
    }
}

function errorMessage(error)
{
    if(error != null) {
        clearErrorMessage();

        $("#errorarea").append("\t<div id=\"popup1\" class=\"overlay\">\n" +
            "\t\t<div class=\"popup\">\n" +
            "\t\t\t<h2>ERROR!!</h2>\n" +
            "\t\t\t<a class=\"close\" href=\"javascript:clearErrorMessage()\">&times;</a>\n" +
            "\t\t\t<div class=\"content\" id=content\">\n" +
            error +
            "\t\t\t</div>\n" +
            "\t\t</div>\n" +
            "\t</div>");
    }
}

function clearErrorMessage(){
    $("#errorarea").empty();
}


//----------------------------------------------------
function triggerAjaxPageContent() {
    console.log("ajax board timeout");
    setTimeout(ajaxBoardContent, refreshRate);
}

//activate the timer calls after the page is loaded
$(function() {
    setInterval(ajaxBoardContent, refreshRate);
    triggerAjaxPageContent();
});

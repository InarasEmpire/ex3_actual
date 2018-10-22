var gameIsStarted = false;
var refreshRate = 1000;
var GAME_URL = buildUrlWithContextPath("singlegameroom");
var USER_MOVE_URL = buildUrlWithContextPath("usermove");
var REMOVE_USER_URL = buildUrlWithContextPath("adduser");


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
            refreshUsersList(gameJson.players, gameJson.nextTurn);
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
        gameEnded(gameJson);
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

function gameEnded(gameJson){
    var message;
    if(gameJson.winners.length == 1)
    {
        if(gameJson.players.length == 1)
        {
            message ="all the other players left the game.";
        }
        else{
            message = "The winner is: " + winners[0].name;

        }
    }
    else if(gameJson.winners.length == 1){
        message = "The winner are: ";
        for(var i = 0; i<gameJson.winners.length; i++){
            message += gameJson.winners[i].name +  "  ";
        }
    }
    else{
        message = "It's a Tie! Game Over.";
    }

    alert(message);
}

function goBack(){
    $.ajax({
        url: REMOVE_USER_URL,
        data: {"gameindex": gameIndex, "operation": "operation"},
        dataType: "POST"
    });

    var url = buildUrlWithContextPath("pages/gamesroom/gamesroom.html" +  gameIndex);
    document.location.href = url;
    gameIndex = -1;

}

//----------------
//   players list
//----------------

function refreshUsersList(usersJson, nextTurnUserName) {
    $("#onlineusers").empty();
    var userListDiv = document.getElementsByClassName('onlineusers');
    var uList = document.createElement('ul');

    for(var i in usersJson)
    {
        var node = document.createElement('li');

        var spanName = document.createElement('span');
        if(nextTurnUserName.localeCompare(usersJson[i].name) == 0){
            spanName.setAttribute("class", "name_turn");
        }
        else{
            spanName.setAttribute("class", "name");
        }
        spanName.innerHTML = usersJson[i].name;

        var spanName = document.createElement('span');
        spanName.setAttribute("class", "name");
        spanName.innerHTML = usersJson[i].name;

        var spanType = document.createElement('span');
        spanType.setAttribute("class", "type");
        spanType.innerHTML =" (" +usersJson[i].type+")";


        var spanTurn = document.createElement('span');
        spanTurn.setAttribute("class", "turns color");
        spanTurn.innerHTML =usersJson[i].turnAmount;
        spanTurn.style.backgroundColor =usersJson[i].color


        node.appendChild(spanName);
        node.appendChild(spanType);
        node.appendChild(spanTurn);

        uList.appendChild(node);
    }
    $("#onlineusers").append(uList);
    //userList.appendChild(uList);
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

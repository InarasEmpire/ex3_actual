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
        btn.addEventListener("click", function () {  selectedPushInCol($(this)); }, false);
        colElem.appendChild(btn);
        rowElem.appendChild(colElem);
    }
    tableElem.appendChild(rowElem);


    // for (var i in boardArray) {
    for(var i=0; i<boardArray.length; i++){
        rowElem = document.createElement('tr');

        for (var j = 0; j < gameJson.cols; j++) {
            colElem = document.createElement('td');
            colElem.setAttribute("row",boardArray[i].row);
            colElem.setAttribute("col", boardArray[i].col);
            colElem.setAttribute("bgcolor", boardArray[i].color);

            rowElem.appendChild(colElem);
        }
        i=i+j;
        tableElem.appendChild(rowElem);
    }

    if(gameJson.variant == "popout")
    {
        rowElem = document.createElement('tr');
        for (var j = 0; j < gameJson.cols; j++) {
            colElem = document.createElement('button');
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
            updateStatus(gameJson);
            checkIfGameStarted(gameJson);
            updateStatus(gameJson);
        },
        error: function(error) {
            console.log(GAME_URL);
            console.error(error.responseText);
        }
    });
}

function selectedPushInCol(elem){
    var gameindex = getQueryVariable("index");
    var col =  elem.closest("td").index();
    //.matches
    var dataString = {"gameindex":gameindex, "col":col, "type":"pushin"};

    $.ajax({
        url: USER_MOVE_URL,
        type: 'POST',
        timeout: 2000,
        data: dataString,
        dataType:"json",
        contentType:false,
        processData:false,
        error: function(error) {
            console.error("Failed to submit player's move");
            errorMessage(error.responseText);
        },
        success: function() {
            console.log("success submit player's move");
            clearErrorMessage();
        }
    });
}

function selectedPopOutCol(elem)
{
    var gameindex = getQueryVariable("index");
    var col = elem.parent().children().index(elem);
    $.ajax({
        data: {"col":col, "type":"popout", "gameindex": gameindex},
        url: USER_MOVE_URL,
        type: 'POST',
        dataType: 'json',
        timeout: 2000,
        contentType:false,
        processData:false,
        error: function(error) {
            console.error("Failed to submit player's move");
            errorMessage(error.responseText);
        },
        success: function() {
            console.log("success submit player's move");
            clearErrorMessage();
        }
    });
}

function checkIfGameStarted(gameJson)
{
    if(!gameIsStarted && gameJson.state == "active")
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

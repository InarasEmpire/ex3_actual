var tableVersion = 0;
var refreshRate = 2000; //mili seconds
var USER_LIST_URL = buildUrlWithContextPath("userslist");
var TABLE_LIST_URL = buildUrlWithContextPath("table");


/*
User List Handler
 */
//users = a list of usernames, essentially an array of javascript strings:
// ["moshe","nachum","nachche"...]
function refreshUsersList(users) {
    //clear all current users
    $("#userslist").empty();
    
    // rebuild the list of users: scan all users and add them to the list of users
    $.each(users || [], function(index, username) {
        console.log("Adding user #" + index + ": " + username);
        //create a new <option> tag with a value in it and
        //appeand it to the #userslist (div with id=userslist) element
        $('<li>' + username + '</li>').appendTo($("#userslist"));
    });
}

/*

Table Game Handle
 */

function ajaxUsersList() {
    $.ajax({
        url: USER_LIST_URL,
        success: function(users) {
            console.log(USER_LIST_URL);
            refreshUsersList(users);
        }
    });
}

//entries = the added chat strings represented as a single string
function appendToTableArea(entries) {
    // add the relevant entries
    $("#tablearea").empty();
    $("#tablearea").append("\t\t\t<tr>\n" +
        "\t\t\t\t<th class=\"text-left\">Game Title</th>\n" +
        "\t\t\t\t<th class=\"text-left\">Creator</th>\n" +
        "\t\t\t\t<th class=\"text-left\">Board Size</th>\n" +
        "\t\t\t\t<th class=\"text-left\">Target</th>\n" +
        "\t\t\t\t<th class=\"text-left\">Variant</th>\n" +
        "\t\t\t\t<th class=\"text-left\">Status</th>\n" +
        "\t\t\t\t<th class=\"text-left\">Players</th>\n" +
        "\t\t\t</tr>");

    $.each(entries || [], appendTableEntry);

    // handle the scroller to auto scroll to the end of the chat area
   // var scroller = $("#tablearea");
    //var height = scroller[0].scrollHeight - $(scroller).height();
    //$(scroller).stop().animate({ scrollTop: height }, "slow");
}

function appendTableEntry(index, entry){
    var entryElement = createTableEntry(entry);
    $("#tablearea").append(entryElement);
}

function createTableEntry (currGame){
    var newEntry = "<tr><td class=\"text-left\">"+currGame.GameTitle+"</td><td class=\"text-left\">"+currGame.Creator+"</td>" +
        "<td class=\"text-left\">"+currGame.BoardSize+"</td><td class=\"text-left\">"+currGame.Target+"</td>" +
        "<td class=\"text-left\">"+currGame.Variant+"</td><td class=\"text-left\">"+currGame.Status+"</td>" +
        "<td class=\"text-left\">"+currGame.Players+"</td></tr>";
    return newEntry;
}

//call the server and get the chat version
//we also send it the current chat version so in case there was a change
//in the chat content, we will get the new string as well
function ajaxTableContent() {
    console.log("inside ajaxTableContent");
    $.ajax({
        url: TABLE_LIST_URL,
        data: "tableversion=" + tableVersion,
        dataType: 'json',
        success: function(data) {
            console.log("data.version: " + data.version + " || tableVersion: " +tableVersion);
            //clearErrorMessage("");
            if (data.version !== tableVersion) {
                tableVersion = data.version;
                appendToTableArea(data.games);
            }
            triggerAjaxTableContent();
        },
        error: function(error) {
            triggerAjaxTableContent();
            errorMessage(error.responseText);
        }
    });
}

//add a method to the button in order to make that form use AJAX
//and not actually submit the form
$(function() { // onload...do
    //add a function to the submit event
   // $("#tableform").submit(function() {

    $("#tableform").submit(function(e){
        e.preventDefault();
        var form = $("#tableform")[0];
        var formData = new FormData(form);

        $.ajax({
            data: formData,
            url: this.action,
            type: 'POST',
            timeout: 2000,
            contentType:false,
            processData:false,
            error: function(error) {
                console.error("Failed to submit");
                errorMessage(error.responseText);
            },
            success: function() {
                console.log("success onload");
                clearErrorMessage();
                //do not add the user string to the chat area
                //since it's going to be retrieved from the server
                //$("#result h1").text(r);
            }
        });

        $("#filename").val("");
        // by default - we'll always return false so it doesn't redirect the user.
        return false;
    });
});

function triggerAjaxTableContent() {
    console.log("table timeout");
    setTimeout(ajaxTableContent, refreshRate);
}

/*
OTHERS
 */

function errorMessage(error)
{
    var cmp = error.localeCompare("");
    if(cmp !== 0 ) {
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

//activate the timer calls after the page is loaded
$(function() {

    //The users list is refreshed automatically every second
    setInterval(ajaxUsersList, refreshRate);

    //The chat content is refreshed only once (using a timeout) but
    //on each call it triggers another execution of itself later (1 second later)
    triggerAjaxTableContent();
});


/*
click on table row
 */

//TODO: add buttons
$(document).on("click", "tr", function(e) {
    $(this).append("<!-- The Modal -->\n" +
        "<div id=\"myModal\" class=\"modal\">\n" +
        "\n" +
        "  <div class=\"modal-content\">\n" +
        "    <span class=\"close\">&times;</span>\n" +
        "    <p>You are about to start the game</p>\n" +
        "\t<button type=\"submit\"  onclick=\"gotoGame( $(this).index());\"\">Enter Game</button>\n" +
        "<button type=\"submit\" value=\"Submit\" onclick=\"closeTab()\">Dismiss</button>" +
        "  </div>\n" +
        "\n\n" +
        "</div>\n"
    );
    var modal = document.getElementById('myModal');


// Get the <span> element that closes the modal
    var span = document.getElementsByClassName("close")[0];


        modal.style.display = "block";

// When the user clicks on <span> (x), close the modal
    span.onclick = function() {
        modal.style.display = "none";
    }

// When the user clicks anywhere outside of the modal, close it
    window.onclick = function(event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    }
});

function gotoGame(index){
    var id = (index - 1);
    var url = buildUrlWithContextPath("../../games?index=" +  index);

    document.location.href = url;
}

function closeTab(){
 document.getElementById("myModal").innerHTML = "";

}

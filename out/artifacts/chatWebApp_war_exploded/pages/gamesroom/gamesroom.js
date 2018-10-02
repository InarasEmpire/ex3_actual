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
//    $("#chatarea").children(".success").removeClass("success");

    //("#tablearea").append("<tr><th>user name</th><th>file name</th></tr>");

    // add the relevant entries
    $.each(entries || [], appendTableEntry);

    // handle the scroller to auto scroll to the end of the chat area
    var scroller = $("#tablearea");
    var height = scroller[0].scrollHeight - $(scroller).height();
    $(scroller).stop().animate({ scrollTop: height }, "slow");
}

function appendTableEntry(index, entry){
    var entryElement = createTableEntry(entry);
    $("#tablearea").append(entryElement);
}

function createTableEntry (entry){
    // entry.filename = entry.chatString.replace(":)", "<span class='smiley'></span>");
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
            /*
             data is of the next form:
             {
                "entries": [
                    {
                        "fileName":"Hi",
                        "username":"bbb",
                        "time":1485548397514
                    },
                    {
                        "fileName":"Hello",
                        "username":"bbb",
                        "time":1485548397514
                    }
                ],
                "version":1
             }
             */
            //console.log("Server chat version: " + data.version + ", Current tableVersion version: " + tableVersion);
           // console.log(data.toString());
            console.log("data.version: " + data.version + " || tableVersion: " +tableVersion);

            if (data.version !== tableVersion) {
                console.log("APPENDINGGGGGGGGGGGGGGGGG");
                tableVersion = data.version;
                appendToTableArea(data.entries);
            }
            triggerAjaxTableContent();
        },
        error: function(error) {
            triggerAjaxTableContent();
        }
    });
}

//add a method to the button in order to make that form use AJAX
//and not actually submit the form
$(function() { // onload...do


    //add a function to the submit event
   // $("#tableform").submit(function() {

    $("form#tableform").submit(function(){
        var formData = new FormData($(this)[0]);

        $.ajax({
            data: formData,
            url: this.action,
            type: 'POST',
            timeout: 2000,
            error: function() {
                console.error("Failed to submit");
            },
            success: function() {
                console.log("success onload");
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

function errorMessge(error)
{
    document.write("\n" +
        "<div class=\"cd-popup\" role=\"alert\">\n" +
        "\t<div class=\"cd-popup-container\">\n" +
        "\t\t<p>"+error.toString()+"</p>\n" +
        "\t\t<ul class=\"cd-buttons\">\n" +
        "\t\t\t<li><a href=\"#0\">OK</a></li>\n" +
        "\t\t</ul>\n" +
        "\t\t<a class=\"cd-popup-close img-replace\">Close</a>\n" +
        "\t</div>\n" +
        "</div> ");
}

//activate the timer calls after the page is loaded
$(function() {

    //The users list is refreshed automatically every second
    setInterval(ajaxUsersList, refreshRate);

    //The chat content is refreshed only once (using a timeout) but
    //on each call it triggers another execution of itself later (1 second later)
    triggerAjaxTableContent();
});
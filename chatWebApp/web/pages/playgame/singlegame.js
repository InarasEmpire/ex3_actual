
//TODO: continue this
const constructBoard = (cols,rows) => {

    for (var i = 1; i <= cols; i++) {
        $('.arrow bounce up').append('<button class="cell-button">');
    };

    for (var i = 1; i <= cols; i++) {
        $('.arrow bounce down').append('<button class="cell-button">');
    };

    for (var i = 1; i <= rows; i++) {
        $('.board').append('<div class="row">');
        $('.row:even').addClass('row-even');
        $('.row:odd').addClass('row-odd');


    };
    for (var i = 1; i <= cols; i++) {
        $('.row').append('<div class="cell">');
    };
    $('.container').css('padding-bottom',(rows/cols)*100 + '%');
    $('.cell-button').text('fuck');
    $('.cell-button').css('width',((100/cols) + '%'));
}

$(document).ready(()=>constructBoard(6,6));


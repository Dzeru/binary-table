var maxSquareNum = 13 // Максимальный ближайший квадрат числа для поиска

var data = {
  // GET from server
  numString: '',              
  id: 0,
  userId: 0,  
  title: 'Loading...',
  note: '',
  // web-only variables
  saved: true,
  lastState: '',
  findClosestSquare: function(steps) {
    for (var i = 1; i < maxSquareNum; i++) { // Поставить ограничение на количество шагов!
      if (i * i >= steps || i === maxSquareNum) {
        return i;
      }
    }
  },
  cells: [],
  squareSide: 0
};

// Функция замены символа в строке
function setCharAt(str, index, chr) {
    if(index > str.length - 1) return str;
    return str.substr(0, index) + chr + str.substr(index + 1);
}

// Если заменить numstring.length на squareSide, то таблица будет полной 
function createCells() {
  for (var i = 0 ; i < data.numString.length; i++){
    var newBox = document.createElement('td');
    newBox.cellRealIndex = i;
    data.cells.push(newBox);
  }
}

// функция срабатывает при клике. Изменяет у ячейки на которую нажали цифру
// Затем вызывает обновление прогресса
var titleAndProgress = document.getElementsByClassName('mytitle')[0];

function changeNumber() {
  if (this.innerHTML === "1") {
    this.innerHTML = "0";
    $(this).css("color", 'white');
    data.numString = setCharAt(data.numString, this.cellRealIndex, '0');
  }
  else {
    this.innerHTML = "1";
    if (this.savedValue === '0') {
      $(this).css("color", 'rgb(60, 221, 86)');
    }
    data.numString = setCharAt(data.numString, this.cellRealIndex, '1');
  }

  // Появление кнопки сохранения
  if (data.saved === true) {
    $("button.saveState").prop('disabled', false);
    $("button.saveState").hide().css("visibility", "visible").fadeTo("slow", 1);
    data.saved = false;
  }
  calculateProgressString(data.title);
}

// создание таблицы из ячеек 
function createTable(cellTable) {
  data.squareSide = data.findClosestSquare(data.numString.length);
  for (var j = 0 ; j < data.squareSide; j++){
    var tableRow = document.createElement('tr');
    cellTable.appendChild(tableRow);
    for (var i = 0; i < data.squareSide; i++) {
      var realI = j * data.squareSide + i;
      var showBox = data.cells[realI];
      var numberInCell = data.numString[realI];                 // Начальное содержимое ячейки
      if (numberInCell){
        data.cells[realI].savedValue = numberInCell;            // Запоминаем сохраненное значение
        data.cells[realI].cellRealIndex = realI;
        showBox.appendChild(document.createTextNode(numberInCell)); // Добавляем его в ячейку
        tableRow.appendChild(showBox);                              // И добавляем ячейку в таблицу
      }
    }
  }
}

// создание строки из 0 если бэк не настроен
function stringGoalProgressInit(n) {
  for (var i = 0; i < n; i++) {
    data.numString = data.numString + '0';
  }
}

function makeButtons() {
  var elementList = data.cells;
  for (var i = 0; i < elementList.length; i++) {
    elementList[i].addEventListener('click', changeNumber);
  }
}

function convertStringToJson (stringToConvert) {
  var JsonObject = JSON.stringify(stringToConvert);
}

// изменяется title так, чтобы он учитывал прогресс
function calculateProgress() {
  var onesCount = 0;
  for (var i = 0; i < data.numString.length; i++) {
    if (data.numString[i] === '1') {
      onesCount++;
    }
  }
  return onesCount;
}
function calculateProgressString(original) {
  $("#openNoteBtn").appendTo("body");
  var ones = calculateProgress();
  titleAndProgress.innerHTML = original + ' (' + ones + '/' + data.numString.length + ') ';
  $("#openNoteBtn").appendTo("td.mytitle");
}

/////////////////////////////////////////////////////////////////////// 
// Начальная загрузка страницы + GET информации

// Считывание URL страницы
var getUrlParameter = function getUrlParameter(sParam) {
  var sPageURL = decodeURIComponent(window.location.search.substring(1)),
      sURLVariables = sPageURL.split('&'),
      sParameterName,
      i;

  for (i = 0; i < sURLVariables.length; i++) {
      sParameterName = sURLVariables[i].split('=');

      if (sParameterName[0] === sParam) {
          return sParameterName[0] === undefined ? false : sParameterName[1];
      }
  }
};

var id = getUrlParameter('id');
//var url = "http://www.mocky.io/v2/5b3fc7af3400002b00001c8a";
var url = "/getgoal?id=" + id;

function getGoalInfo() {
  $.ajax({
    url: url,
    type: 'GET',
    // dataType: 'jsonp',
    // url: /goal
    success: function(res) {
      data.numString = res.currentState;
      data.title = res.goalName;
      data.id = res.id;
      data.userId = res.userId;
      data.note = res.note;

      calculateProgressString(data.title);
      var cellTable = document.querySelector('table');
      createCells();
      createTable(cellTable);
      makeButtons();
      $("button.saveState").on('click', function(event) {
        updateGoal();
      });
      $("#openNoteBtn").on('click', function(event) { // Кнопка просмотра заметки
        typeWriterCall();
      });
    },
    error: function(err) {
      console.log("request failed");
      console.log(err);
    }
  })
}

/////////////////////////-------------------//////////////////////////
/////////////////////////////////////////////////////////////////////////
//                          Запуск скриптов                            //
/////////////////////////////////////////////////////////////////////////
/////////////////////////-------------------//////////////////////////

$(document).ready(function() {
  var isJavaEnabled = 1; // Изменять вручную, 0 для debug'а без сервера
  if (!isJavaEnabled) {
    var goalNumber = 5;
    data.numString = '00100';
    data.title = 'client-only testing';
    data.id = 9000;
    data.userId = 0;
    data.note = 'this feature is not implemented probably';

    calculateProgressString(data.title);
    var cellTable = document.querySelector('table');
    createCells();
    createTable(cellTable, data);
    makeButtons();
    $("button.saveState").fadeTo("fast", 0.25);
    $("#vkshare").fadeTo("fast", 0.25);
    $("button.saveState").prop('disabled', true);
    $("button.saveState").on('click', function(event) {
      updateGoal();
    });
    if (data.note) {
      $("#openNoteBtn").on('click', function(event) { // Кнопка просмотра заметки
        typeWriterCall();
      });
    }
    else {
      $("#openNoteBtn").css('visibility', 'hidden');
    }
    $("button.screenShotter").on("click", function() {
      makeScreenshot();
    });
  }
  else {
    getGoalInfo();
  }
});


///////////////////////////////////////////////////////////////////////
// Магия с кнопкой сохранения состояния
function updateGoal() {
  var formData = {
    "id": data.id,
    "goalName": data.title,
    "allSteps": data.numString.length,
    "doneSteps": calculateProgress(),
    "currentState": data.numString,
    "note": data.note,
    "userId": data.userId
  };

  $.ajax({
    //url:'http://www.mocky.io/v2/5afffe89310000730076ded3'
    url:'/updategoal',
    method:'POST',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    data: JSON.stringify(formData),
    headers:
    {
        'X-CSRF-TOKEN' : $('meta[name="_csrf"]').attr('content')
    }})
    .done(function(res) {
      data.saved = true;
      $("button.saveState").fadeTo("slow", 0.5);
      $("button.saveState").prop('disabled', true);
      for (var i = data.cells.length - 1; i >= 0; i--) {
        data.cells[i].savedValue = data.cells[i].innerHTML; // Клетки снова
        $(data.cells[i]).css("color", 'white');             // становятся белыми
      }
      console.log("State saved!");
    })
    .fail(function(res) {
      data.saved = true;
      console.log("Saving state went wrong!");
      console.log(JSON.stringify(res));
    });
}

///////////////////////////////////////////////////////////////////////
// Все что связано с удалением цели
function askDeleteConfirmation() {
  console.log("Showing dialog");
  $('#deleteConfirmation').dialog({
    classes: {
      "ui-dialog-content": "confNumberText",
      "ui-dialog-titlebar": "ui-borders"
    }
  });
  $('#deleteConfirmation').dialog('option', 'classes.ui-dialog', 'deleteWidget');

  //$('deleteConfirmation').dialog("open");
}

var confirmationsCount = 0;

$("button.deleteGoal").on('click', function(event) {
  askDeleteConfirmation();
  $(".confNumber").on('click', function(event) {
    if (this.innerHTML == 0) {
      this.innerHTML = 1;
      confirmationsCount = confirmationsCount + 1;
    }
    else {
      this.innerHTML = 0;
      confirmationsCount = confirmationsCount - 1;
    }
    if (confirmationsCount == 4) {
      $('#deleteConfirmation').dialog("close");
      deleteGoal();
    }
  });
});

function deleteGoal() {
  var formData = {
    "id": data.id
  };

  $.ajax({
    //url:'http://www.mocky.io/v2/5afffe89310000730076ded3'
    url:'/deletegoal',
    method:'POST',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    data: JSON.stringify(formData),
    headers:
    {
        'X-CSRF-TOKEN' : $('meta[name="_csrf"]').attr('content')
    }})
    .done(function(res) {
      location.replace('/goals'); //location.href = '/goals' (?)
      console.log("Delete request sended!");
    })
    .fail(function(res) {
      location.replace('/goals');
      console.log("Goal deletion went wrong!");
      console.log(JSON.stringify(res));
    });
}

///////////////////////////////////////////////////////////////////////
// Заметка к цели и связанные с ней функции
var opened = false;
function typeWriterCall() {
  var i = 0;
  var speed = 35; /* The speed/duration of the effect in milliseconds */

  function typeWriter() {
    if (i < data.note.length) {
      document.getElementById("note").innerHTML += data.note.charAt(i);
      i++;
      setTimeout(typeWriter, speed);
    }
  }

  if (!opened) {
    $("#note").fadeToggle("slow");
    typeWriter();
  }
  else {
    $("#note").fadeToggle(400, "swing", function() {
      $("#note").text("");
    });

  }
  opened = !opened;
}

///////////////////////////////////////////////////////////////////////
// Потенциальная нереализованная магия

function makeScreenshot() {
  var tbl_width = document.getElementsByClassName('cross')[0].offsetWidth;
  var tbl_height = document.getElementsByClassName('cross')[0].offsetHeight;
  var options = { 
    backgroundColor: '#ad33ff',
    height: tbl_height,
    width: tbl_width,
  };

  html2canvas(document.getElementsByClassName("cross")[0], options).then(canvas => {
    var postData = {
      file: canvas.toDataURL(),
      upload_preset: "k25wiy42",
      public_id: data.userId + '_' + data.id + '.jpg'
    };

  // Запрос на облачное хранилище изображений
  $.ajax({
    //url:'http://www.mocky.io/v2/5afffe89310000730076ded3'
    url:'https://api.cloudinary.com/v1_1/pie2table/raw/upload',
    method:'POST',
    //dataType: 'jsonp',                                // Здесь это не нужно!
    //contentType: 'application/json; charset=utf-8',   // Потому что гладиолус
    data: postData
    })
    .done(function(res) {
      console.log("Screenshot saved!")
      console.log(JSON.stringify(res));

      imgSource = res.url;

      $("meta[property='og:image']").attr('content', imgSource);
      $("#vkshare").fadeTo("fast", 1);
      document.getElementById('vkshare').innerHTML = 
        VK.Share.button(
          {
            url: 'https://binarytable.neocities.org/',
            image: imgSource
          },
          {
            type: 'custom',
            text: '<img src="http://vk.com/images/vk32.png" />',
          }); 
    })
    .fail(function(res) {
      console.log("Too bad! Image was not saved on server!");
      console.log(JSON.stringify(res));
    });

  });


}

// var angryButton = document.querySelector('.yelling'); 
// angryButton.addEventListener('click', spawnBox); 
// var angryButtonPos = angryButton.getBoundingClientRect(); 

// var boxWithButton = document.querySelector('.box1'); 


// function spawnBox() { 
// var floatingPopBox = document.createElement('div'); 
// floatingPopBox.position = 'absolute'; 
// console.log(angryButtonPos.left); 
// console.log(floatingPopBox.style.left) 
// floatingPopBox.style.marginLeft = angryButtonPos.left + 'px'; 
// floatingPopBox.style.marginTop = angryButtonPos.top - boxWithButton.top + 'px'; 

// console.log(floatingPopBox.style.left); 

// var floatingBoxText = document.createTextNode('You are awesome!'); 
// floatingPopBox.appendChild(floatingBoxText); 
// boxWithButton.appendChild(floatingPopBox); 
// }
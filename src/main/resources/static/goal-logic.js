var maxSquareNum = 13 // Максимальный ближайший квадрат числа для поиска

var data = {
  numString: '',
  id: 0,
  saved: true,
  lastState: '',
  userId: 0,
  title: 'Loading...',
  note: '',
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
function createCells(thisData) {
  for (var i = 0 ; i < thisData.numString.length; i++){
    var newBox = document.createElement('td');
    var myCellIndex = i;
    newBox.myCellIndex = myCellIndex;
    thisData.cells.push(newBox);
  }
}

// функция срабатывает при клике. Изменяет у ячейки на которую нажали цифру
// Затем вызывает обновление прогресса
var titleAndProgress = document.getElementsByClassName('mytitle')[0];

function changeNumber() {
  if (this.innerHTML === "1") {
    this.innerHTML = "0";
    data.numString = setCharAt(data.numString, this.myCellIndex, '0');
  }
  else {
    this.innerHTML = "1";
    data.numString = setCharAt(data.numString, this.myCellIndex, '1');
  }

  // Появление кнопки сохранения
  if (data.saved === true) {
    $("button.saveState").prop('disabled', false);
    $("button.saveState").hide().css("visibility", "visible").fadeIn();
    data.saved = false;
  }
	calculateProgressString(data.title);
}

// создание таблицы из ячеек 
function createTable(cellTable, thisData) {
  thisData.squareSide = thisData.findClosestSquare(thisData.numString.length);
  for (var j = 0 ; j < thisData.squareSide; j++){
    var tableRow = document.createElement('tr');
    cellTable.appendChild(tableRow);
    for (var i = 0; i < thisData.squareSide; i++) {
      var realI = j * thisData.squareSide + i;
      var showBox = thisData.cells[realI];
      var textToShow = thisData.numString[realI];
      if (textToShow){
        showBox.appendChild(document.createTextNode(textToShow));
        tableRow.appendChild(showBox);
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
  var ones = calculateProgress();
  titleAndProgress.innerHTML = original + ' (' + ones + '/' + data.numString.length + ')';
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
      //==============================================//
      //==============Запуск скриптов=================//
      //==============================================//
      calculateProgressString(data.title);
      var cellTable = document.querySelector('table');
      createCells(data);
      createTable(cellTable, data);
      makeButtons();
      $("button.saveState").on('click', function(event) {
        updateGoal();
      });
    },
    error: function(err) {
      console.log("request failed");
      console.log(err);
    }
  })
}
var isJavaEnabled = 0; // Изменять вручную, 0 для debug'а без сервера
if (!isJavaEnabled) {
  var goalNumber = 9;
  data.numString = '010101000';
  data.title = 'client-only testing';
  data.id = 9000;
  data.userId = 0;
  data.note = 'not implemented probably';

  calculateProgressString(data.title);
  var cellTable = document.querySelector('table');
  createCells(data);
  createTable(cellTable, data);
  makeButtons();
  $("button.saveState").on('click', function(event) {
    updateGoal();
  });
}
else {
  getGoalInfo();
}

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
      $("button.saveState").fadeTo(500, 0.001);
      $("button.saveState").prop('disabled', true);
      console.log("State saved!");
    })
    .fail(function(res) {
      data.saved = true;
      $("button.saveState").fadeTo(500, 0.001);
      $("button.saveState").prop('disabled', true);
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
      "ui-dialog-content": "confNumberText"
    }
  });

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
      llocation.replace('/goals');
      console.log("Goal deletion went wrong!");
      console.log(JSON.stringify(res));
    });
}



///////////////////////////////////////////////////////////////////////
// Потенциальная нереализованная магия

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
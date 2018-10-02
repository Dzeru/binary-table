var maxSquareNum = 13 // Максимальный ближайший квадрат числа для поиска не включая

var data = {
  // GET from server
  numString: '',              
  id: 0,
  userId: 0,  
  title: 'Loading...',
  note: '',
  finished: false, // Option used in update request
  groupGoal: true,
  goalTimestamp: '0-0-0',
  collaborators: [],
  // web-only variables
  saved: true,
  lastState: '',
  findClosestSquare: function(steps) {
    for (var i = 1; i <= maxSquareNum; i++) { // Поставить ограничение на количество шагов!
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



// функция срабатывает при клике. Изменяет у ячейки на которую нажали цифру
// Затем вызывает обновление прогресса
var tableTitleString = document.getElementById('titleString');
var stepsCount = document.getElementById('stepsCounter');
var stepsCurrent = document.getElementById('stepsCurrent');
var stepsOutOf = document.getElementById('stepsOutOf');

function changeNumber() {
    if (this.innerHTML === "1") {
        this.innerHTML = "0";
        $(this).animate({ backgroundColor:'#B172C5' }, 200);
        data.numString = setCharAt(data.numString, this.cellRealIndex, '0');
    }
    else {
        this.innerHTML = "1";
        if (this.savedValue === '0') {
          $(this).animate({ backgroundColor: '#A656CD'}, 200);
        }
        data.numString = setCharAt(data.numString, this.cellRealIndex, '1');
    }

    // Появление кнопки сохранения
    if (data.saved === true) {
        $("button.saveState").prop('disabled', false);
        $("button.saveState").fadeTo("slow", 1);
        data.saved = false;
    }
    calculateProgressString(data.title);
}

// Если заменить numstring.length на squareSide, то таблица будет полной 
// Создаем ячейки без цифр, присваиваем каждой индекс. Записываются в массив ячеек
function createCells() {
    for (var i = 0 ; i < data.numString.length; i++){
        var newBox = document.createElement('td');
        newBox.cellRealIndex = i;
        data.cells.push(newBox);
    }
}

// Добавляем на каждую ячейку обработчик клика для смены цифры внутри
function makeButtons() {
    var elementList = data.cells;
    for (var i = 0; i < elementList.length; i++) {
        elementList[i].addEventListener('click', changeNumber);
    }
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
    if (onesCount == data.numString.length) {
        // Activate goal finisher
        $("#endGoal").css('visibility', 'visible');
    }
    else {
        $("#endGoal").css('visibility', 'hidden');
    }
    return onesCount;
}

function calculateProgressString() {
    var ones = calculateProgress();
    stepsCurrent.innerHTML = ones;
    stepsOutOf.innerHTML = data.numString.length;
}

function initializeTitle() {
    $("#openNoteBtn").insertAfter("#titleString");
    $("#stepsCounter").insertAfter("#titleString");
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



function getGoalInfo() {
  var id = getUrlParameter('id');
  //var url = "http://www.mocky.io/v2/5b3fc7af3400002b00001c8a";
  var url = "/getgoal?id=" + id;

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
      data.finished = res.finished;
      data.groupGoal = res.groupGoal;
      data.goalTimestamp = res.goalTimestamp;

      // Логика странички
      initializeTable();
    },
    error: function(err) {
      console.log("request failed");
      console.log(err);
    }
  })
}

/////////////////////////-------------------//////////////////////////
/////////////////////////////////////////////////////////////////////////
//            Запуск скриптов, инициализация элементов                 //
/////////////////////////////////////////////////////////////////////////
/////////////////////////-------------------//////////////////////////

var isJavaEnabled = 1; // Изменять вручную, 0 для debug'а без сервера
$(document).ready(function() {
    if (!isJavaEnabled) {
      var goalNumber = 5;
      data.numString = '00100';
      data.title = 'client-only testing';
      data.id = 9000;
      data.userId = 0;
      data.note = 'this feature is not implemented probably';

      // Логика странички
      initializeTable();
    }
    else {
      getGoalInfo();
    }
});

function initializeTable() {
  window.vkAsyncInit = function() {
    VK.init({
      apiId: 6700902
    });
  };

  setTimeout(function() {
    var el = document.createElement("script");
    el.type = "text/javascript";
    el.src = "https://vk.com/js/api/openapi.js?159";
    el.async = true;
    document.getElementById("vk_api_transport").appendChild(el);
  }, 0);

  tableTitleString.innerHTML = data.title
  calculateProgressString();
  initializeTitle();
  var cellTable = document.getElementById('tableBodyId');
  createCells();
  createTable(cellTable, data);
  makeButtons();
  $("button.saveState").fadeTo("fast", 0.25);
  $("#vkshare").fadeTo("fast", 0.25);
  $("button.saveState").prop('disabled', true);
  $("button.saveState").on('click', function(event) {
      updateGoal();
  });
  document.getElementById('titleString').addEventListener('focus', function() {
      $("button.saveState").prop('disabled', false);
      $("button.saveState").fadeTo("slow", 1);
  });
  document.getElementById('note').addEventListener('focus', function() {
      $("button.saveState").prop('disabled', false);
      $("button.saveState").fadeTo("slow", 1);
  });
  $("#endGoal").on('click', function() {
      finishGoal();
  }); // TODO Добавить подтверждение
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
  document.title = "BIT | " + data.title;

  if (data.groupGoal) {
    collapseCollabsButton();
    getCollabsInfo();
  }

  saveStepsCount();
  $('#stepsOutOf').on('blur', function() {
    checkInputStepsChange();
  });
}


///////////////////////////////////////////////////////////////////////
// Магия с кнопкой сохранения состояния
function updateGoal() {
    data.title = document.getElementById('titleString').innerHTML;
    data.note = document.getElementById('note').innerHTML;
    var formData = {
        "id": data.id,
        "goalName": data.title,
        "allSteps": data.numString.length,
        "doneSteps": calculateProgress(),
        "currentState": data.numString,
        "note": data.note,
        "userId": data.userId,
        "finished": data.finished,
        "goalTimestamp": data.goalTimestamp
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
            $("button.saveState").fadeTo("slow", 0.25);
            $("button.saveState").prop('disabled', true);
            for (var i = data.cells.length - 1; i >= 0; i--) {
                data.cells[i].savedValue = data.cells[i].innerHTML;           // Клетки снова
                $(data.cells[i]).animate({ backgroundColor:'#B172C5' }, 200); // становятся белыми
            }
            console.log("State saved!");
            if (data.finished) {
                location.replace('/goals');
            }
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
        if (this.innerHTML === 0) {
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
    "id": data.id,
    "groupGoal": data.groupGoal
  };

  $.ajax({
    //url:'http://www.mocky.io/v2/5afffe89310000730076ded3'
    url:'/deletegoal',
    method:'POST',
    dataType: 'json',
    contentType: 'application/json;charset=utf-8',
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
// Sharing

function makeScreenshot() {
    var tbl_width = document.getElementsByClassName('cross')[0].offsetWidth;
    var tbl_height = document.getElementsByClassName('cross')[0].offsetHeight;
    var options = {
        backgroundColor: '#B172C5',
        height: tbl_height,
        width: tbl_width,
    };

    html2canvas(document.getElementsByClassName("cross")[0], options).then(function(canvas) {
      var d = new Date();
      var postData = {
          file: canvas.toDataURL(),
          upload_preset: "k25wiy42",
          public_id: data.userId + '_' + Math.floor(d.getTime()/15000) + '_' + data.id + '.jpg'
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

        imgSource = res.url;

        $("meta[property='og:image']").attr('content', imgSource);
        $("#vkshare").fadeTo("fast", 1);
        $("#vkshare").on('click', function() {
          vkPost(canvas);
        });
        
        })
        .fail(function(res) {
          console.log("Too bad! Image was not saved on server!");
          console.log(JSON.stringify(res));
        });

    });
}


function vkPost(canvas) {
  var settings = 8196;
  VK.Auth.login(function(response) {
    if (response.session) {
      var urlToLoad = 
      {
        upload_url : "",
        album_id : 0,
        user_id : 0
      };
      
      VK.Api.call('photos.getWallUploadServer', {v: 5.85}, function(r) {       
        
        urlToLoad.upload_url = r.response.upload_url;
        urlToLoad.album_id = r.response.album_id;
        urlToLoad.user_id = r.response.user_id;

        var uploadImageData = {
          imageUrl: $("meta[property='og:image']").attr('content'),
          uploadUrl: r.response.upload_url
        }
        
        $.ajax({
          //url:'http://www.mocky.io/v2/5afffe89310000730076ded3',
          url: '/vkpostimage',
          method: 'POST',
          dataType: 'json',                                // Здесь это не нужно!
          contentType: 'application/json;charset=utf-8',   // Потому что гладиолус
          data: JSON.stringify(uploadImageData),
          headers:
          {
              'X-CSRF-TOKEN' : $('meta[name="_csrf"]').attr('content')
          }})
          .done(function(res) {
            console.log("Yeah! Screenshot saved!");
            VK.Api.call('photos.saveWallPhoto', 
              {
                v: 5.85,
                user_id: r.user_id,
                photo: res.photo,
                server: res.server,
                hash: res.hash
              }, 
              function(saveRes) {
                VK.Api.call('wall.post', 
                {
                  v: 5.85,
                  attachments: "photo" + saveRes.owner_id + "_" + saveRes.id
                },
                function(wallPostRes) {
                  console.log("IT IS FINALLY WORKING OH MY GOD OH MY GOD");
                });
              });
          })
          .fail(function(res) {
            console.log("Too bad! Image was not send to bit server!");
            console.log(JSON.stringify(res));
          });
      });
    

        /* Пользователь успешно авторизовался */
      if (response.settings) {
        /* Выбранные настройки доступа пользователя, если они были запрошены */
      }
    
    } else {
      /* Пользователь нажал кнопку Отмена в окне авторизации */
    }
  }, settings);
  
  
    



  
  //VK.Api.call('wall.post', {attachments: , v: 5.85});

}



///////////////////////////////////////////////////////////////////////
// Завершение цели

function finishGoal() {
    data.finished = true;
    updateGoal();
}

function restoreGoal() {
    data.finished = false;
    updateGoal();
}

///////////////////////////////////////////////////////////////////////
// Collaborators - связанное

// Make button like bootstrap-collapsible
function collapseCollabsButton() {
  var coll = document.getElementsByClassName("collapsible");
  var i;

  for (i = 0; i < coll.length; i++) {
    coll[i].addEventListener("click", function() {
      this.classList.toggle("active");
      var content = this.nextElementSibling;
      if (content.style.maxHeight){
        content.style.maxHeight = null;
      } else {
        content.style.maxHeight = content.scrollHeight + "px";
      } 
    });
  }
}

function fillCollabsInfo(collabs) {
  $('#collabs').empty();
  var ulCollabs = document.getElementById('collabs');
  collabs.forEach(function(collab) {
    var item = document.createElement('ul');
    item.innerHTML = collab;
    ulCollabs.appendChild(item);
  })
}

function getCollabsInfo() {
  if (isJavaEnabled) {
    var id = getUrlParameter('id');
    var url = "/getcollaborators?id=" + id;
    //var url = "http://www.mocky.io/v2/5ba1eaec3500004f005bbeb2?id=" + id;
    $.ajax({
      url: url,
      type: 'GET',
      // dataType: 'jsonp',
      // url: /goal
      success: function(res) {
        data.collaborators = res.collaborators;
        fillCollabsInfo(data.collaborators);
      },
      error: function(err) {
        console.log("request failed");
        console.log(err);
      }
    });
  }
  else {
    data.collaborators = ["mew@cat.com", "binary@table.bit", "synthwave.love@gmail.com"];
    fillCollabsInfo(data.collaborators);
  }
}

///////////////////////////////////////////////////////////////////////
// Потенциальная нереализованная магия
var previousSteps;

// For actions to be done after steps are changed
function saveStepsCount() {
  previousSteps = Number($('#stepsOutOf').text());
}

function checkInputStepsChange() {
  var currentSteps = $('#stepsOutOf').text();
  currentSteps = currentSteps.trim()          
  var currentStepsNum = Number(currentSteps);
  if (!currentStepsNum) {                       // Check if string contains letters & strange symbols
    $('#stepsOutOf').html(previousSteps);       // Revert changes
  }
  else if (currentSteps.indexOf(' ') !== -1 || currentSteps.indexOf('\n') !== -1) {
    $('#stepsOutOf').html(previousSteps);       
  }
  else {                                        // Accept changes
    currentStepsNum = Math.min(currentStepsNum, 169);

    // Clear previous data
    $('#tableBodyId').empty();  // Clear table body
    data.cells = [];

    // Set data (new string of numbers)
    var numStringCopy = data.numString;

    if (currentStepsNum > numStringCopy.length) { // If just new cells need to be added
      for (var i = numStringCopy.length; i < currentStepsNum; i++) {
        data.numString += '0';
      }
    }
    else { // if max goal steps is less than before
      var onesBefore = calculateProgress(); 
      data.numString = data.numString.substr(0, currentStepsNum);
      var onesAfter = calculateProgress();
      var onesToAdd = onesBefore - onesAfter;   // Count '1' in cells to be deleted
      for (var i = 0; i < currentStepsNum; i++) { // Share them between existing cells
        if (onesToAdd > 0 && data.numString[i] === '0') {
          data.numString = setCharAt(data.numString, i, '1');
          onesToAdd = onesToAdd - 1;
        }
      }
      calculateProgressString();      // recalculate progress in case max steps is less than ones
    }

    //////////////
    // Re-create table
    var cellTable = document.getElementById('tableBodyId');
    createCells();
    createTable(cellTable, data);
    makeButtons();
    //////////////

    $('#stepsOutOf').html(currentStepsNum);
  }
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
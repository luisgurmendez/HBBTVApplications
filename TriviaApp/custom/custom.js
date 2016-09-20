//All ready!. Page &  Cordova loaded => $(document).ready && cordova "deviceready"
//Todo listo!. Página & Cordova cargados => $(document).ready && cordova "deviceready"
function deviceReady() {
	try {



		//Typically fired when the device changes orientation.
		//Típicamente disparado cuando el dispositivo cambia de orientación.
		$(window).resize(function() {
			//somthing
			mui.viewport.iScrollRefresh();
		});
	
		//Sample when Internet connection is needed but not mandatory
		//Ejemplo de cuando se necesita conexióna a Internet pero no es obligatoria.
		if (!mui.connectionAvailable()){
			if ("plugins" in window && "toast" in window.plugins)
				window.plugins.toast.showLongCenter("We recommend you connect your device to the Internet");
			else
				mui.alert("We recommend you connect your device to the Internet");
		}
		
		installEvents();

		navigator.splashscreen.hide();



	} catch (e) {
		//your decision
		//tu decisión
	}
}

function installEvents() {

	$('#scroll_up_btn > a').click(function(){
		$('#historical-page .mui-scroll-wrapper').css({"transform":"translate(0px,0px) scale(1) translateZ(0px)"})
		//$('#scroll_up_btn').hide()
	})




	$('#tabbar-historical').click(function(){
		mui.viewport.showPage('historical-page','SLIDE_RIGHT')
		$('#tabbar-historical').addClass('active-tabbar')
		$('#tabbar-activeQuestion').removeClass('active-tabbar')

	});

	$('#tabbar-activeQuestion').click(function(){
		mui.viewport.showPage('home-page','SLIDE_LEFT')
		$('#tabbar-activeQuestion').addClass('active-tabbar')
		$('#tabbar-historical').removeClass('active-tabbar')


	})

	mui.viewport.on("swiperight", function(currentPageId, originalTarget, event, startX, startY, endX, endY) {
		if(currentPageId != 'historical-page') {
			mui.viewport.showPage('historical-page','SLIDE_RIGHT')
			$('#tabbar-historical').addClass('active-tabbar')
			$('#tabbar-activeQuestion').removeClass('active-tabbar')
		}
	});

	mui.viewport.on("swipeleft", function(currentPageId, originalTarget, event, startX, startY, endX, endY) {
		if(currentPageId != 'home-page') {
			mui.viewport.showPage('home-page','SLIDE_LEFT')
			$('#tabbar-activeQuestion').addClass('active-tabbar')
			$('#tabbar-historical').removeClass('active-tabbar')

		}
	});
	/**
	mui.viewport.on("swipeup",function(){
		if( parseInt($('#home-page .mui-scroll-wrapper').css("transform").replace("matrix(","").replace(")","").split(",")[5])<-100){
			$('#scroll_up_btn').show()
		}else{
			$('#scroll_up_btn').hide()
		}
	})

	mui.viewport.on("swipedown",function(){
		if( parseInt($('#home-page .mui-scroll-wrapper').css("transform").replace("matrix(","").replace(")","").split(",")[5])<-100){
			$('#scroll_up_btn').show()
		}else{
			$('#scroll_up_btn').hide()
		}
	})


	 **/
}

/**
 * Courtesy: Open an url using InAppBrowser plugin.
 * Cortesía: Abre una url usando InAppBrowser plugin.
 * @param url
 */
function openInAppBrowser(url) {
	window.open(encodeURI(url), "_blank", "location=yes,closebuttoncaption=Volver,presentationstyle=pagesheet,transitionstyle='fliphorizontal',EnableViewPortScale=yes");
}

function secondsToHms(d) {
	d = Number(d);
	var h = Math.floor(d / 3600);
	var m = Math.floor(d % 3600 / 60);
	var s = Math.floor(d % 3600 % 60);
	return ((h > 0 ? h + ":" + (m < 10 ? "0" : "") : "") + m + ":" + (s < 10 ? "0" : "") + s); }


function createSummaryMsgHTML(data){
	date= new Date();
	hours = date.getHours()
	minutes = date.getMinutes()
	if(minutes < 10){
		minutes = "0" + minutes
	}
	if(hours <10){
		hours = "0" + hours
	}

	time= hours + ":" + minutes
	//time = secondsToHms(data.time)
	html="<div class='summaryMsgContainer'><span class='summaryMsg summaryLastElement'>" + data.summary + "</span><span class='summaryMsgDate'>" + time + "</span></div>"
	return html

}




function createQuestionHTML(data,isActive){
	html = isActive? "<div data-question-number=" +data.questionNumber + " class='question_container activeQuestion'>" : "<div data-question-number=" + data.questionNumber + "  class='question_container'>"
	html+="<div class='question '>" + data.question + "</div><hr>";
	answers=data.options;
	for(var i in answers){
		if(isActive){
			html+="<div class='answer_wrapper activeQuestion'><div class='custom_radiobtn' name='answerbtn' data-answer='" + answerIndex[(parseInt(i)+1)] + "' ><a href='#'></a></div><div class='answer'>" + answers[i] + "</div></div>";
		}else{
			html+="<div class='answer_wrapper'><div class='custom_radiobtn' name='answerbtn' data-answer='" + answerIndex[(parseInt(i)+1)] + "' ><a href='#'></a></div><div class='answer'>" + answers[i] + "</div></div>";

		}
	}

	html+= isActive? "<div class='send_answerbtn ' ><a href='#'>Send</a></div></div>" : "<div class='display_correctness' ><a href='#'></a></div></div>";
	return html

}

function installDynamicEvents(){

	$('.answer_wrapper.activeQuestion').click(function(){
		if(canAnswer){
			$('.custom_radiobtn.selected').removeClass('selected');
			$(this).children('.custom_radiobtn').addClass('selected');
			question.answer=$(this).children('.custom_radiobtn.selected').data('answer');
			question.isAnswered=true;

		}

	})
	$('.send_answerbtn').click(function(){
		if(question.isAnswered && !question.isSent && canAnswer){
			websocket.send(JSON.stringify({'app':'trivia','typeOfMessage':'answerMessage','questionNumber':question.questionNumber,'playersAnswer': question.answer}))
			$(this).addClass('deactivated_btn');
			question.isSent=true;
			canAnswer=false;
			toast.toast("Answer sent")
		}

	})

	$('.reconnect_btn').click(function(){
		connectWebSocket();
		toast.toast("Reconnecting...")
		$(this).remove();
		$('.info_msg').remove();
	})
}

function showErrorMessage(msg){
	toast.toast(msg)

}




var websocket=null;
var answerIndex={'1':'a','2':'b','3':'c','4':'d','5':'e','6':'f','7':'g','8':'h'}
var indexAnswer={'a':'1','b':'2','c':'3','d':'4','e':'5','f':'6','g':'7','h':'8'}
var question={};
var summary={};
var canAnswer=false;
var toastHTML=null;
var app="none"

var toast={
	toast: function(text){
		if(toastHTML != null){
			toastHTML.fadeOut(300)
		}
		toastHTML=$("<div class='toast_msg' style='display: none'>" + text + "</div>")
		$('#mui-viewport').append(toastHTML)
		toastHTML.fadeIn(400,function(){setTimeout(function(){toastHTML.fadeOut(300)},3000)}); //fade out after 3 seconds
		toastWidth = (-toastHTML.outerWidth())/2;
		toastHTML.css({"margin-left":toastWidth + "px"})

	}
}


function startRequests(){

	requestInterval = setInterval(function(){
		if(websocket.readyState == 1){
			switch(app){
				case "trivia":
					websocket.send(JSON.stringify({'app':'trivia','typeOfMessage':'requestMessage'}))
					break;
				case "summary":
					websocket.send(JSON.stringify({'app':'summary','typeOfMessage':'requestMessage'}))
					break;
				case "none":
					websocket.send(JSON.stringify({'app':'none'}))
			}
		}else{
			clearInterval(requestInterval)
		}
	},500)

}


function connectWebSocket(){

	wsUri="ws://192.168.4.43:8080/RealTimeSummary/addinfo";
	websocket = new WebSocket(wsUri)
	websocket.onopen=function(evt){
		//$('#home-body > div > div').prepend("<span class='info_msg con_success'> - - Connection Successfull! - - </span>")
		toast.toast("Connection Successful");
		startRequests();
		
	}
	websocket.onmessage=function(evt){
		typeOfMessage= jQuery.parseJSON(evt.data).typeOfMessage;
		data = jQuery.parseJSON(evt.data)
		console.log(data)
		app=data.app
		toggleFooter()
		switch(data.app){
			case "trivia":
				triviaOnMessage(data)
				break;
			case "summary":
				summaryOnMessage(data);
				break;
			case "app":
				app=data.app;
			default:

				break;
		}
	}
	websocket.onerror=function(evt){
		$('.info_msg').remove();
		toast.toast("Can't connect to server.")
		$('#home-body > div > div').prepend("<span class='info_msg con_error'> - - Connection Error - - </span>")
		mui.viewport.refreshScroll("home-page");
		clearInterval(requestInterval)

	}
	websocket.onclose=function(evt){
		$('.info_msg').remove()
		$('.question_container.activeQuestion').remove()
		$('#home-body > div > div').prepend("<div class='reconnect_btn'><a href='#'>Reconnect</a></div>")
		screenHeight=$('#mui-screen').height()/3
		$('.reconnect_btn').css({'margin-top': screenHeight});
		$('#home-body > div > div').prepend("<span class='info_msg con_close'> - - Connection Closed - - </span>")
		installDynamicEvents();
		mui.viewport.refreshScroll("home-page");
		clearInterval(requestInterval)


	}
}
connectWebSocket();


function summaryOnMessage(data){

	switch(typeOfMessage){

		case "summaryMessage":

			if(data.time != summary.time){
				//$('.summaryContainer .lastElement').remove();
				summaryMsgHTML=createSummaryMsgHTML(data)
				$(summaryMsgHTML).prependTo('#home-body .mui-scroll-wrapper > div').hide().fadeIn("slow");
				//$(summaryMsgHTML).prependTo('#historical-body .mui-scroll-wrapper > div').hide().fadeIn("slow");
				summary.time = data.time
				summary.summary = data.summary
			}
			
			break;
		case "errorMessage":
			toast.toast(data.message)
			break;
	}



}


function toggleFooter(){

	switch(data.app){
		case "trivia":

			$('.mui-viewport').css({'bottom':48})
			$('#footer').show()

			break;
		case "summary":
			$('.mui-viewport').css({'bottom':0})
			$('#footer').hide()

			break;
		default:

			break;
	}



}




function triviaOnMessage(data){
	switch(typeOfMessage){
		case "questionMessage":
			if(question.questionNumber != data.questionNumber){
				$('.question_container.activeQuestion').remove();
				question.data=data;
				$(createQuestionHTML(data,true)).prependTo('#home-body .mui-scroll-wrapper > div').hide().fadeIn("slow");
				screenHeight=$('#mui-screen').height()/3
				$('.question_container.activeQuestion').css({'margin-top': screenHeight});
				mui.viewport.refreshScroll("home-page");
				mui.viewport.refreshScroll("historical-page");
				question.questionNumber=data.questionNumber;
				question.question=data.question;
				question.isAnswered=false;
				question.isSent=false;
				question.answerAlreadyArrived=false
				canAnswer=true;
				installDynamicEvents();
			}
			break;
		case "resultMessage":
			canAnswer=false;
			if(!question.answerAlreadyArrived){
				question.answerAlreadyArrived=true;
				if(data.questionNumber == question.questionNumber || indexAnswer[data.rightAnswer] != null){
					if(question.isSent){
						isCorrect=false;
						if(data.rightAnswer==question.answer){
							isCorrect=true;
							//Change Green color of accerted option.
							$($('.question_container.activeQuestion .answer_wrapper')[indexAnswer[data.rightAnswer]-1]).children('.custom_radiobtn').addClass('correct_answer')
							$($('.question_container.activeQuestion .answer_wrapper .custom_radiobtn.selected')[0]).removeClass('selected')
							// Add question to historical page, and add green color to accerted option.
							historicalLastQuestion=$(createQuestionHTML(question.data,false)).prependTo('#historical-body .mui-scroll-wrapper > div').hide().fadeIn("slow");
							$(historicalLastQuestion.children('.answer_wrapper')[indexAnswer[question.answer] - 1]).children('.custom_radiobtn').addClass('correct_answer')
							$(historicalLastQuestion).children('.display_correctness').addClass('correct_icon')



						}else{
							//Change to green and red colors of wrong and right answer.
							$($('.question_container.activeQuestion .answer_wrapper')[indexAnswer[data.rightAnswer]-1]).children('.custom_radiobtn').addClass('correct_answer')
							$($('.question_container.activeQuestion .answer_wrapper .custom_radiobtn.selected')[0]).removeClass('selected').addClass('incorrect_answer')
							//Add question to historical page.
							historicalLastQuestion=$(createQuestionHTML(question.data,false)).prependTo('#historical-body .mui-scroll-wrapper > div').hide().fadeIn("slow");
							//Add green color of the right answer.
							$(historicalLastQuestion.children('.answer_wrapper')[indexAnswer[data.rightAnswer] - 1]).children('.custom_radiobtn').addClass('correct_answer')
							$(historicalLastQuestion.children('.answer_wrapper')[indexAnswer[question.answer] - 1]).children('.custom_radiobtn').addClass('incorrect_answer')
							// Add result icon at bottom right.
							$(historicalLastQuestion).children('.display_correctness').addClass('incorrect_icon')
						}
						if(isCorrect){
							toast.toast('Correct Answer!')
						}else{
							toast.toast('Wrong Answer!')
						}
					}else{
						toast.toast("Time is out")
						$($('.question_container.activeQuestion .answer_wrapper')[indexAnswer[data.rightAnswer]-1]).children('.custom_radiobtn').addClass('correct_answer')
						$('.custom_radiobtn.selected').removeClass('selected')
						$('.send_answerbtn').css({"background-color":"#c3c0c8","box-shadow":"2px 2px 1px #96949a"})

					}

					setTimeout(function(){$('.question_container.activeQuestion').fadeOut(300)},3000);


				}else{
					//ERROR
					showErrorMessage("ERROR");
				}
			}
			break;
		case "timeoutMessage":
			canAnswer=false;
			toast.toast("Time is out")
			break;

		case "errorMessage":
			toast.toast(data.message)
			break;

		default:

	}

}

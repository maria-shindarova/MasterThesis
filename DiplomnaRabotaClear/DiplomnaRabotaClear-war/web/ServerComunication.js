// 
// @author Maria Shindarova
// 
// 
//http://localhost:8080/DiplomnaRabotaClear-war/index
function sendMSG(){
	var xmlhttp=new XMLHttpRequest(); // IE7+, Firefox, Chrome, Opera, Safari
	
	if (!window.XMLHttpRequest){// IE6, IE5
	    xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	
	xmlhttp.onreadystatechange = function(){
	    
            if((xmlhttp.readyState == 4)||(xmlhttp.status == 200)){
	         
                var response=JSON.parse(xmlhttp.responseText);
                //http://localhost:8080/generated/
	        var urlResponse="http://"+location.hostname+":"+location.port+"/generated/";
	        //alert(urlResponse+response.urlResult);
                
	        addAudio(urlResponse+response.urlResult);
	    }
	};
			      
	var url= "http://"+location.hostname+":"+location.port+"/DiplomnaRabotaClear-war/index";
	
	  //document.getElementById("server").value+"/++++++++/++++++++";
	   
	
	var paramsRaw = getText();
	//var params="?processText="+encodeURIComponent(paramsRaw);
        var params="?processText="+paramsRaw;
        xmlhttp.open("POST",url+params,false);
	xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded;charset=UTF-8");
	xmlhttp.setRequestHeader("Content-length", params.length);
        //alert(params+" "+url);
	xmlhttp.send(paramsRaw);        
};
function getText(){
	var text = document.getElementById("textPotrebitel").value;
	var from = document.getElementById("textPotrebitelLen").value;
	
	forProcesing =text.substring(from,text.length); 
        document.getElementById("textPotrebitel").value+="<br> ------------------------------------------------/n"
	    
        document.getElementById("textPotrebitelLen").value=(document.getElementById("textPotrebitel").value).length*1;
	return forProcesing; 
};

function addAudio(response){
	
	var checkIfPlayed = document.getElementById("audio").getAttribute("src");
//	var audio = new Audio(response);
//        audio.play();
	if(checkIfPlayed!=""){
	
		var service= document.getElementById("audio");
			
		if (service.currentAudio
        	&& service.currentAudio.currentTime > 0
        	&& !service.currentAudio.paused
        	&& !service.currentAudio.ended
        	&& service.currentAudio.readyState > 2){
			
			var node=document.createElement("audio");
			node.setAttribute("src",response);
			node.setAttribute("type","hidden");
			//node.setAttribute("id","audio1");
			node.setAttribute("autoplay","true");
			node.setAttribute("volume",1.0);
			
			service.addChild(node,service);
                        node.play();
			return;
		}
	}
	
	var temp = document.getElementById("audio");
	
	temp.setAttribute("src",response);
	temp.setAttribute("autoplay","true");
	temp.play();
}
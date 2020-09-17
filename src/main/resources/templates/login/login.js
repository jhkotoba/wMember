function login(){
	
	let userId = document.getElementById("userId").value;
	let pass = document.getElementById("pass").value;
	
	fetch("/api/member/loginProcess", {
        method: "POST",
        mode: "same-origin",
        credentials: "include",
        headers: {
             "Accept": "application/json",
             "Content-Type": "application/json"
        },
        body: JSON.stringify({
            "userId": userId,
            "password": pass
        })
    })
    .then(response => response.json())            
    .then(data => {
		if(data.isLogin){		
			document.location.href = 
				document.location.href.substring(document.location.href.lastIndexOf("rtnUrl") + 7, document.location.href.length);
		
		}	
	});
}
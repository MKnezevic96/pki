window.onload = function () {

	$('#submitLogin').click(function(e){
		e.preventDefault()


		let username = $('#inputUsername').val()
		let password = $('#inputPassword').val()

		let json = JSON.stringify({"password":password, "username":username})

		$.ajax({
			type: 'POST',
			url:'/api/auth/login',
			data: json,
			dataType : "json",
			contentType : "application/json; charset=utf-8",
			complete: function(data)
			{

				if(data.status == "200")
				{

                     window.location.href = "certificates.html"

				}
				else
				{
					alert("nece");
				}
			}
		})

	})



}


console.log("This message is from script file")


const toggleSidebar=()=>{
    if($('.sidebar').is(":visible")){
        $(".sidebar").css("display","none");
        $(".content").css("margin-left","0%");
        //$(".menu-open").css("display","block");
    }
    else{
        $(".sidebar").css("display","block");
        $(".content").css("margin-left","20%");
        //$(".menu-open").css("display","none");
    }
    
    /*if($('.sidebar').is(":visible")){
		$(".menu-open").css("display","none");
	}*/
};

const search_contact=()=>{
    let query = $("#input_search_contact").val();
    if(query==''){
        $(".search-result").hide();
    }
    else{
        //console.log(query);
        let url = `http://localhost:8080/user/search/${query}`;
        fetch(url).then((response)=>{
            return response.json();
        }).then((data)=>{          
            let text = `<div class = 'list-group'>`;
            data.forEach((contact) => {
                text+= `<a href='/user/view_contact/0/${contact.id}' class='list-group-item list-group-item-action'> ${contact.name} </a>`;
            });
            text+= `</div>`;
            $(".search-result").html(text); 
            $(".search-result").show();
        });
    }
};
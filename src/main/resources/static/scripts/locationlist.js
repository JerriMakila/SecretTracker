async function getLocations(){
    let locations;

    try{
        const data = await fetch(`${baseUrl}locations`)
        locations = await data.json();
    }catch{
        locations = null;
    }
    
    return locations;
}

async function showLocations(){
    const locations = await getLocations();
    let errorMsgContainer = document.getElementById("error-msg");

    if(locations !== null){
         let locationsTable = "";
        const role = getUserRole();

        locations.forEach(location => {
            const locationName = location.location;
            const id = location.locationId;
            const deleteLocationBtn = role === 'ADMIN' ? `<td sec:authorize="hasAuthority('ADMIN')"><button class="btn btn-danger" onClick="deleteLocation(${id})">Delete</button></td></tr>` : '';

            locationsTable += `<tr><td>${locationName}</td><td><a class="btn btn-primary" href=${baseUrl}secretsbylocation/${id}>secrets</a></td>`
                + `${deleteLocationBtn}`;
        });

        document.getElementById("location-table").innerHTML = locationsTable;
        errorMsgContainer.innerHTML = "";
    }else{
        errorMsgContainer.innerHTML = "Something went wrong, please refresh the page";
    }
   

}

function getUserRole(){
    const role = document.getElementById("role").innerHTML.replace("[", "").replace("]","");
    return role;
}

async function deleteLocation(locationId){
    let errorMsgContainer = document.getElementById("error-msg");

    try{
        const response = await fetch(`${baseUrl}locations/${locationId}`, {
            method: 'DELETE'
        });

        if(response.status !== 204){
            errorMsgContainer.innerHTML = "Something went wrong, please try again";
        } else{
            errorMsgContainer.innerHTML = "";
            showLocations();
        }
    }catch{
        errorMsgContainer.innerHTML = "Something went wrong, please try again";
    }
}

showLocations();
function setProjectPropertySync(projectIdOrKey, connection, active) {
    fetch('https://exalatedevserhiy.atlassian.net/rest/api/2/project/'+projectIdOrKey+'/properties/sync', {
        method: 'PUT',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            "connection" : connection,
            "active" : active === "true" ? "true" : "false"
        })
    })
        .then(response => {
            console.log(
                `Response: ${response.status} ${response.statusText}`
            );
            return response.text();
        })
        .then(text => console.log(text))
        .catch(err => console.error(err));
}
//setProjectPropertySync("AA", "A_to_B", "true")
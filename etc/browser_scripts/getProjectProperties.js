function getProjectProperties(projectIdOrKey) {
    fetch('https://exalatedevserhiy.atlassian.net/rest/api/2/project/'+projectIdOrKey+'/properties', {
        method: 'GET',
        headers: {
            'Accept': 'application/json'
        }
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
//getProjectProperties("AA")
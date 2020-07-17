function sendVote(type,id){
    const params = new URLSearchParams();
    const pageName = document.getElementById("page-name").value;
    params.append('key', id);
    params.append('page-name', pageName);
    params.append('vote-type',type);

    fetch('/vote-comment', {method: 'POST', body: params})
        .then(() => {
            getComments();
        }).catch((err) => console.log(err));
}

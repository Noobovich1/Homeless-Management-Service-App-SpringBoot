document.getElementById('fetchDataBtn').addEventListener('click', function() {
    
    // Using Fetch API (Modern AJAX) to call the Spring Boot Backend
    fetch('/api/data')
        .then(response => response.json())
        .then(data => {
            if(data.status === 'success') {
                const resultBox = document.getElementById('resultBox');
                const messageText = document.getElementById('messageText');
                
                messageText.innerText = data.message;
                resultBox.classList.remove('hidden');
            }
        })
        .catch(error => console.error('Error with AJAX request:', error));
});
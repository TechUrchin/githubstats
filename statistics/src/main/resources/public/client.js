makeRequest = () => {
    const repoOwner = $("#owner-input").val();
    const repoName = $("#repo-input").val();
    callGitHub(repoOwner, repoName);
}

let fetchedCommits = [];

callGitHub = (owner, repo) => {
    let startTime = new Date();
    resetResult();
    const url = 'https://api.github.com/repos/' + owner + '/' + repo + '/commits';
    $.ajax({
        type: "GET",
        url: url,
        success: function (data) {
            const formattedCommits = formatCommit(data,repo,owner);
            callStatistics(formattedCommits);
        },
        error: function (e) {
            writeErrorMessage("An error has occurred while calling GitHub API. Details: "+ e.toString());
        }
    });
    let endTime = new Date();
    var timeDiff = endTime - startTime; //in ms
    console.log("GitHub time taken:", timeDiff);
}

callStatistics = (commits) => {
    let startTime = new Date();
    const url = '/api/stats/generate'
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: url,
        data: JSON.stringify(commits),
        dataType: 'json',
        success: function (data) {
            callReports(data);
        },
        error: function (e) {
            writeErrorMessage("An error has occurred while calling Statistics API. Details: "+ e.toString());
        }
    });
    let endTime = new Date();
    var timeDiff = endTime - startTime; //in ms
    console.log("Stats time taken:", timeDiff);
}

callReports = (stats) => {
    let startTime = new Date();
    const url = 'http://localhost:8070/api/reports/pdf'
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: url,
        data: JSON.stringify(stats),
        cache: false,
        xhrFields: {
            responseType: 'blob'
        },
        success: function (data, status, xhr) {
            const url = window.URL.createObjectURL(new Blob([data]));
            const filename = xhr.getResponseHeader("Content-Disposition");
            writeSuccessfulResult(url, filename);
        },
        error: function (xhr, status, error) {
            writeErrorMessage("An error has occurred while calling Reports API. Details: "+ xhr.responseText);
        }
    });
    let endTime = new Date();
    let timeDiff = endTime - startTime; //in ms
    console.log("Reports time taken:", timeDiff);
}

formatCommit = (commits, repoName, repoOwner) => {
    //Initialises empty JSON Object
    let repository = {
        "repositoryName": "",
        "repositoryOwner": "",
        "commits": []
    }

    let commitObject = {
        "commitDate": "",
        "author":
            {
                "gitName": "",
                "gitEmail": "",
                "githubUsername": "",
                "githubUserIcon": ""
            }
    }

    repository.repositoryName = repoName;
    repository.repositoryOwner = repoOwner;
    commits.forEach(i =>
    {
        commitObject.commitDate = i.commit.author.date;
        if(i.author == null)
        {
            commitObject.author.gitName = i.commit.author.name;
            commitObject.author.gitEmail = i.commit.author.email;
            commitObject.author.githubUsername = null;
            commitObject.author.githubUserIcon = null;
        }
        else{
            commitObject.author.gitName = i.commit.author.name;
            commitObject.author.gitEmail = i.commit.author.email;
            commitObject.author.githubUsername = i.author.login;
            commitObject.author.githubUserIcon = i.author.avatar_url;
        }
        const updatedCommit = JSON.parse(JSON.stringify(commitObject));
        repository.commits.push(updatedCommit);
    })
    return repository;
}


resetResult = () => {
    $("#result").attr("hidden")
    $("#report-download-link").removeAttr("href");
    $("#report-download-link").removeAttr("download");
    $("#report-download-link").removeAttr("hidden");
    $("#success-message").text("");
    fetchedCommits = [];
}

writeErrorMessage = (message) => {
    $("#result").removeAttr("hidden");
    $("#success-message").text(message);
    $("#report-download-link").attr("hidden");
}

writeSuccessfulResult = (url, filename) => {
    $("#result").removeAttr("hidden");
    $("#report-download-link").attr("href", url);
    $("#report-download-link").attr("download", filename);
    $("#success-message").text("Report has successfully generated!");
    $("#report-download-link").text("Click to download report ", filename);

}

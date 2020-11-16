import logo from './github-logo.png';
import './App.css';
import axios from 'axios';
import React, {Component} from 'react';
//import { render } from 'react-dom';

class App extends Component{
  render() 
  { 
    return (
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <h1>
            GitHub Stats
          </h1>
          <h5>Coursework 2 - COMP3211 Distributed Systems</h5>
          <li>This will give you some detailed statistics of a repository and all the users who have contributed to that repository. </li>
          <li>This will generate a report that will be in the form of a PDF that you will be able to view in a browser or locally if you choose to download it</li>
          <label>Owner of Github Repository</label>
          <input placeholder="e.g octocat"></input>
          <label>Name of Repository</label>
          <input placeholder="e.g hello-world"></input>
          <button className="Button" onClick={this.getGitHub}>Click Here for Stats</button>
        </header>
      </div>
    );
  }
state = {
  repository: {}
}
  getGitHub = () => {
    //config = {headers: {Authorization: `Bearer 4dc896d320882759be824f64df3e1afa4675857b`}
    let repository
    axios.get(
      `https://api.github.com/repos/octocat/hello-world/commits`
      ).then(res => {
      const commits = res.data;
      console.log("hello from GH:", commits);
      repository = this.formatCommit(commits);
      this.setState({repository}); 
      this.postToStats();
      });

    //call stats
    

    //call reports
    /*axios.get(
        '/api/reports/hello'
    )
        .then(res => {
          console.log("Hello from reports api:", res.data)
        });
    */

  }

  postToStats = () => {
    let stats
    console.log("State", this.state)
    axios.post(
        '/api/stats/generate', this.state.repository, {
          "Content-Type":"application/json"
        }
    )
        .then(res => {
          stats = res.data;
          console.log(stats)
        });
    this.postToReport();
  }

  postToReport = () => {
    axios.post(
      '/api/stats/generate', this.state.stats, {
        "Content-Type":"application/json"
      }
  )
      .then(res => {
        console.log(res);
        console.log(res.data);
      });
  }


  formatCommit = (commits) => {
    console.log("hello from formatCommit ", commits)
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

    repository.repositoryName = "Hello-World"; //variable needs to be defined
    repository.repositoryOwner = "Octocat"; //Same here
    commits.forEach(i =>
    {
      console.log(i)
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
    console.log("All the Commits", repository)
    return repository;
  }

}

export default App;

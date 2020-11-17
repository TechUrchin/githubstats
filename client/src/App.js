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
          <a href={this.state.reportLink} download={this.state.reportFilename}>Download report {this.state.reportFilename}</a>
        </header>
      </div>
    );
  }
state = {
  repository: {},
  statistics: {},
  reportLink: '#',
  reportFilename: 'sadfasdfsda'
}
  getGitHub = () => {
    const config = {headers: {Authorization: `Bearer 4dc896d320882759be824f64df3e1afa4675857b`}}
    axios.get(
      `https://api.github.com/repos/shimmamconyx/githubstats/commits`, config
      ).then(res => {
      const commits = res.data;
      let repositoryData = this.formatCommit(commits);
      this.setState({repository: repositoryData});
      this.postToStats();
      });

  }

  postToStats = () => {
    axios.post(
        '/api/stats/generate', this.state.repository, {
          "Content-Type":"application/json"
        }
    )
        .then(res => {
          const stats = res.data;
          this.setState({
            statistics: stats
          })
          this.postToReport();
        });
  }

  postToReport = () => {
    console.log(this.state)
    axios.post(
      '/api/reports/pdf', this.state.statistics, {
        "Content-Type":"application/json",
        "responseType": "blob"
      })
      .then(res => {
        console.log(res)
        const url = window.URL.createObjectURL(new Blob([res.data]))
        const filename = res.headers["content-disposition"]
        this.setState({reportLink: url})
        this.setState({reportFilename: filename})
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

    repository.repositoryName = "githubstats"; //variable needs to be defined
    repository.repositoryOwner = "shimmamconyx"; //Same here
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

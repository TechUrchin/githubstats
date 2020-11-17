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
        <header>
          <img src={logo} className="App-logo" alt="logo" />
          <h1>
            GitHub Stats
          </h1>
          <h2>Coursework 2 - COMP3211 Distributed Systems</h2>
        </header>

        <div className="content">
          <div className="container">
            <div className="column">
              <h3>What is this app?</h3>
              <p>This app generates a PDF report containing statistics about a repository and a contributor leaderboard.</p>
              <h3>How does it work?</h3>
              <p>
                This app makes a call to the GitHub API, requesting a list of commits in a repository. The data is passed to our API that generates statistics.
                The generated statistics are passed into our final API, that is responsible for generating reports.</p>
              <h3>How to use it?</h3>
              <p>
                Enter GitHub repository owner and name below to proceed. The report will then get generated, and be available for download below.
              </p>
            </div>
            <div className="column notes">
              <p>
                <b>note:</b> This only works on public GitHub repositories, because
                GitHub API only allows to access public repositories without authentication.
                Some examples of public repositories:
                <ul>
                  {/*TODO add more repos*/}
                  <li>octocat.hello-world</li>
                  {/*<li></li>*/}
                </ul>
              </p>
            </div>
          </div>


          <div className="form-wrapper">
            <h2>Repository details</h2>
              <label>Repository owner</label>
              <input placeholder="e.g octocat" name="repoOwner" value={this.state.repoOwner} onChange={this.handleChange.bind(this)}/>

              <label>Repository name</label>
              <input placeholder="e.g hello-world" name="repoName" value={this.state.repoName} onChange={this.handleChange.bind(this)}/>

              <button onClick={this.handleRequest}>Submit</button>

              <div className="result" hidden={this.state.success==null}>
                <p>{this.state.success}</p>
                <a href={this.state.reportLink} download={this.state.reportFilename} hidden={this.state.reportFilename==null}>Click to download report {this.state.reportFilename}</a>
              </div>
          </div>
        </div>
      </div>
    );
  }

  handleChange(event) {
    this.setState({[event.target.name]: event.target.value})
  }

state = {
  repoName: "hello-world",
  repoOwner: "octocat",
  repository: {},
  statistics: {},
  reportLink: '#',
  reportFilename: null,
  success: null
}

//reset state for new request and call the APIs
  handleRequest = () => {
    console.log(this.state)
      this.setState({repository: null});
      this.setState({statistics: null});
      this.setState({reportLink: '#'});
      this.setState({reportFilename: null});
      this.setState({success: null});
      this.getGitHub();
  }

  getGitHub = () => {
    const url = 'https://api.github.com/repos/'+this.state.repoOwner+'/'+this.state.repoName+'/commits';
    axios.get(url)
        .then(res => {
      const commits = res.data;
      let repositoryData = this.formatCommit(commits);
      this.setState({repository: repositoryData});
      this.postToStats();
      })
        .catch(err => {
          this.setState({success: "An error has occurred while calling GitHub API. Error details: "+ err})
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
        })
        .catch(err => {
          this.setState({success: "An error has occurred while calling statistics API. Error details: "+ err})
        })
  }

  postToReport = () => {
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
        this.setState({success: "Report successfully generated!"})
      })
        .catch(err => {
          this.setState({success: "An error has occurred while calling reports API. Error details: "+ err})
        });
  }

  formatCommit = (commits) => {
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

    repository.repositoryName = this.state.repoName;
    repository.repositoryOwner = this.state.repoOwner;
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

}

export default App;

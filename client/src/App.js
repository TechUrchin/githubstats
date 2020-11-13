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

  getGitHub = () => {
    //config = {headers: {Authorization: `Bearer 4dc896d320882759be824f64df3e1afa4675857b`}
    axios.get(
      `https://api.github.com/repos/octocat/hello-world`
      ).then(res => {
      const repo = res.data;
      console.log("hello from GH:", repo)
      });
    //call stats
    axios.get(
        '/api/stats/hello'
    )
        .then(res => {
          console.log("Hello from stats api:", res.data)
        });
    //call reports
    axios.get(
        '/api/reports/hello'
    )
        .then(res => {
          console.log("Hello from reports api:", res.data)
        });

  }

}

export default App;

import logo from './logo.svg';
import './App.css';
import axios from 'axios';
import React, {Component} from 'react';
import { render } from 'react-dom';

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
      console.log(repo)
      })
      
  }
}

export default App;

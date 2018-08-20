import React, {Component} from 'react';
import {Route, Switch, Redirect} from 'react-router-dom';
import Cookies from 'js-cookie';
import './App.css';
import Login from './Login';
import Dashboard from './Dashboard';

class App extends Component {
  render() {
    return (
      <div className="App">
        <header className="App-header">
        </header>
        <Switch>
          <Route path="/login" component={Login}/>
          <Route path="/" render={props => (
            Cookies.get('access_token') ? <Dashboard history={props.history}/> : <Redirect to="/login"/>
          )}/>
        </Switch>
        <div>footer</div>
      </div>
    );
  }
}

export default App;

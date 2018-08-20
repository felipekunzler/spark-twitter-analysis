import React, {Component} from 'react';
import {Route, Switch, Redirect} from 'react-router-dom';
import Cookies from 'js-cookie';
import './App.css';
import Login from './Login';
import Dashboard from './Dashboard';
import NavigationBar from "./NavigationBar";

class App extends Component {

  constructor() {
    super();
    this.state = {
      currentUser: Cookies.get('username')
    };
    this.setCurrentUser = this.setCurrentUser.bind(this);
  }

  setCurrentUser(user) {
    Cookies.set('username', user);
    this.setState({currentUser: user});
  }

  render() {
    return (
      <div>
        <NavigationBar currentUser={this.state.currentUser} setCurrentUser={this.setCurrentUser}/>
        <Switch>
          <Route path="/login" render={props => <Login setCurrentUser={this.setCurrentUser} history={props.history}/>}/>
          <Route path="/" render={props => (
            Cookies.get('access_token') ? <Dashboard /> : <Redirect to="/login"/>
          )}/>
        </Switch>
      </div>
    );
  }
}

export default App;

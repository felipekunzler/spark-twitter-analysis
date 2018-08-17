import React, {Component} from 'react';
import {Route, Switch, Redirect} from 'react-router-dom';
import Cookies from 'js-cookie';
import './App.css';

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

class Login extends Component {

  handleLogin() {
    fetch('http://localhost:8080/oauth/token', {
      method: 'post',
      headers: new Headers({
        'Authorization': 'Basic d2ViYXBwOnNlY3JldA==',
        'Content-Type': 'application/x-www-form-urlencoded'
      }),
      body: 'grant_type=password&username=admin&password=123'
    })
      .then(res => res.json())
      .then(res => {
        let expiryDate = new Date(new Date().getTime() + res['expires_in'] * 1000);
        Cookies.set('access_token', res['access_token'], {expires: expiryDate});
        this.props.history.push('/');
      });
  }

  render() {
    return (
      <div>
        <h1>login</h1>
        <input type="text"/><br/>
        <input type="password"/><br/>
        <button onClick={this.handleLogin.bind(this)}>login</button>
        <br/>
      </div>
    );
  }
}

class Dashboard extends Component {

  constructor() {
    super();
    this.state = {
      userComponents: []
    }
  }

  componentDidMount() {
    let token = Cookies.get('access_token');
    fetch('http://localhost:8080/components', {
      headers: new Headers({
        'Authorization': 'Bearer ' + token,
      }),
    })
      .then(resp => resp.json())
      .then(json => {
        this.setState({userComponents: json['_embedded']['components']});
      });
  }

  handleSignOut() {
    Cookies.remove('access_token');
    this.props.history.push('/');
  }

  render() {
    return (
      <div>
        <div>Dashboard page here</div>
        <button onClick={this.handleSignOut.bind(this)}>Sign out</button>
        <div>
          {this.state.userComponents.map((d, idx) => {
            return (<li key={idx}>{d.name}</li>)
          })}
        </div>
      </div>
    );
  }

}
